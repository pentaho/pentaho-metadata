/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.pms.mql.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.mql.Path;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;

/**
 * Class that build a Node-Arc graph based on <code>BusinesTable</code> and <code>RelationshipMeta</code> objects
 * specified in a <code>BusinessModel</code>. It attempts to use Arc Consistency Optimization to find a
 * <code>Path</code> that utilizes the smallest number of relationships to include a list of required tables.
 */
public class MqlGraph implements GraphElementChangeListener {
  private static final Log logger = LogFactory.getLog( MqlGraph.class );

  private List<BusinessTable> requiredTables;

  private List<Node> nodes;

  private List<Arc> arcs;

  private Map<BusinessTable, Node> tableNodeMap;

  private GraphElementQueue basicNodeQueue;

  private GraphElementQueue extendedNodeQueue;

  private LinkedList<List<GraphElement>> searchStack;

  private boolean needsReset = false;

  /**
   * Creates a new graph for a business model
   *
   * @param model Business model to base graph upon
   */
  @SuppressWarnings( "unchecked" )
  public MqlGraph( BusinessModel model ) {
    this.nodes = new ArrayList<Node>();
    this.arcs = new ArrayList<Arc>();
    this.tableNodeMap = new HashMap<BusinessTable, Node>();
    this.basicNodeQueue = new GraphElementQueue();
    this.extendedNodeQueue = new GraphElementQueue();

    // build the graph for this model
    build( model.getRelationships() );
  }

  /**
   * Calculates and returns a path that satisfies the required tables list or null if one cannot be found
   *
   * @param requiredTables Tables that are required to be in path
   * @return Path with smallest number of relationships to ensure all required tables are included
   */
  public Path getPath( PathType searchTechnique, List<BusinessTable> requiredTables ) {
    // if reset works and validity check passes, build path
    if ( reset( requiredTables ) && isValid( searchTechnique ) ) {
      logger.debug( "Path determined sucessfully" );

      Path path = new Path();
      for ( Arc arc : arcs ) {
        if ( arc.isRequired() ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "Arc selected for path: " + arc );
          }
          path.addRelationship( arc.getRelationship() );
        } else if ( logger.isDebugEnabled() ) {
          logger.debug( "Arc not used for path: Requirement Known[" + arc.isRequirementKnown() + "], Required["
            + arc.isRequired() + "]" );
        }
      }

      if ( logger.isDebugEnabled() ) {
        for ( Node n : nodes ) {
          logger.debug( "Node selection state: Requirement Known[" + n.isRequirementKnown() + "], Required["
            + n.isRequired() + "]" );
        }
      }
      if ( path.size() > 0 ) {
        return path;
      }
    }

    return null;
  }

  /**
   * Resets this graph before locating a path
   */
  private boolean reset( List<BusinessTable> requiredTables ) {
    try {
      // reset required tables and nodes
      this.requiredTables = requiredTables;
      if ( needsReset ) { // Don't need to reset first-time through
        if ( this.searchStack != null ) {
          this.searchStack.clear();
        }

        // Clear required-status from nodes
        for ( Node n : nodes ) {
          n.clearRequirement();
        }

        // Clear required-status from arcs
        for ( Arc a : arcs ) {
          a.clearRequirement();
        }
      } else {
        this.needsReset = true;
      }

      // initialize nodes
      for ( Node n : nodes ) {
        if ( requiredTables.contains( n.getTable() ) ) {
          n.setRequirement( true );
        }
      }

      return true;
    } catch ( ConsistencyException cx ) {
      logger.debug( "failed to reset", cx );
      return false;
    }
  }

  /**
   * Calculates graph validity
   */
  private boolean isValid( PathType searchTechnique ) {
    // remove all arcs from queue
    extendedNodeQueue.clear();

    // initialize node queue with all nodes in graph
    basicNodeQueue.clear();
    basicNodeQueue.addAll( nodes );

    try {
      // check for all search technique
      if ( searchTechnique == PathType.ALL ) {
        for ( Node n : nodes ) {
          n.setRequirement( true );
        }
        for ( Arc a : arcs ) {
          a.setRequirement( true );
        }
      }

      // start by making initial graph consistent
      propagate();

      // search to test assigning a requirement setting
      // to tables not yet determined
      search( searchTechnique );

      return true;
    } catch ( ConsistencyException cx ) {
      logger.debug( "failed to validate", cx );
      return false;
    }
  }

  /**
   * Performs work necessary to bind all nodes that have not been assigned a requirement value yet
   *
   * @param searchTechnique Indicates type of search that should be performed
   * @throws ConsistencyException When determine that graph is impossible to satisfy
   */
  private void search( PathType searchTechnique ) throws ConsistencyException {
    // locate first solution
    Solution bestKnown = searchForNextSolution( searchTechnique, null );
    if ( logger.isDebugEnabled() ) {
      logger.debug( "initial solution found - Rating[" + bestKnown.getRating() + "]" );
    }

    // for paths looking for the best rating, continue until we can't find a better solution
    if ( searchTechnique == PathType.SHORTEST || searchTechnique == PathType.LOWEST_SCORE ) {

      try {
        Solution lastSolution = bestKnown;
        while ( lastSolution != null ) {
          logger.debug( "continuing search for more solutions from last one located" );
          lastSolution = searchForNextSolution( searchTechnique, lastSolution );

          if ( lastSolution != null && logger.isDebugEnabled() ) {
            logger.debug( "Next solution result: " + toBitPath( lastSolution.searchPath ) + " - partial["
              + lastSolution.isPartial() + "]" );
          }

          // check if a new complete solution was located
          if ( lastSolution != null && !lastSolution.isPartial() ) {
            if ( logger.isDebugEnabled() ) {
              logger.debug( "new solution located - Rating[" + lastSolution.getRating() + "]" );
            }

            // check if new solution is better than last
            if ( lastSolution.getRating() < bestKnown.getRating() ) {
              if ( logger.isDebugEnabled() ) {
                logger.debug( "New solution is better than previously best known, continuing from it" );
              }
              bestKnown = lastSolution;
            }
          }
        }
      } catch ( ConsistencyException cx ) {
        logger.debug( "failed while looking for more solutions", cx );
        // no need to do anything if no more solutions exist since we already have a best solution
      }

      // reset the search function before we can return to best solution
      logger.debug( "returning to best known solution" );
      // Restore state to best-found solution
      reset( requiredTables );
      propagate();

      // recreate path for best known solution
      Iterator<Arc> arcIter = bestKnown.getSearchArcs().iterator();
      for ( SearchDirection direction : bestKnown.getSearchPath() ) {

        // all of these operations should work without issue
        Arc arc = arcIter.next();
        if ( !attemptArcAssignment( arc, direction ) ) {
          throw new ConsistencyException( arc );
        }
      }
    }
  }

  /**
   * Attempts to find next valid solution to the graph depending on what type of <code>PathType</code> is desired.
   *
   * @param searchTechnique Indicates type of search that should be performed
   * @param prevSolution    Previous solution to allow this search to continue from that point
   * @return The resulting solution
   * @throws ConsistencyException When determine that graph is impossible to satisfy
   */
  private Solution searchForNextSolution( PathType searchTechnique, Solution prevSolution )
    throws ConsistencyException {
    // A left move equates to setting a requirement to false and a right move is equivalent to true.
    // Try setting to "false" first to reduce the number of tables for most searches.
    // For the "any relevant" search use "true" first which is quicker
    SearchDirection firstDirection;
    SearchDirection secondDirection;
    if ( searchTechnique == PathType.ANY_RELEVANT ) {
      firstDirection = SearchDirection.RIGHT;
      secondDirection = SearchDirection.LEFT;
    } else {
      firstDirection = SearchDirection.LEFT;
      secondDirection = SearchDirection.RIGHT;
    }

    // if this is a subsequent search after a solution was already found, we need
    // to return to the location where the last move in the first direction was made
    List<SearchDirection> searchPath = new LinkedList<SearchDirection>();
    List<Arc> searchArcs = new LinkedList<Arc>();
    if ( prevSolution != null ) {
      // check for situation where we have already traversed all possible paths
      boolean prevContainsFirstDirection = false;
      for ( SearchDirection direction : prevSolution.searchPath ) {
        if ( direction == firstDirection ) {
          prevContainsFirstDirection = true;
          break;
        }
      }
      if ( !prevContainsFirstDirection ) {
        return null;
      }

      ListIterator<SearchDirection> pathIter = prevSolution.searchPath.listIterator( prevSolution.searchPath.size() );

      // continue to move back in search path until we find an arc that can
      // be assigned the second direction
      boolean foundSecondDir = false;
      while ( pathIter.hasPrevious() && !foundSecondDir ) {

        // reset the search function for next search operation
        reset( requiredTables );
        propagate();
        searchPath.clear();
        searchArcs.clear();

        // locate the last move that has an alternative
        while ( pathIter.hasPrevious() ) {
          SearchDirection direction = pathIter.previous();

          if ( direction == firstDirection ) {
            break;
          }
        }

        // recreate path up to point where we can try a different direction
        Iterator<Arc> arcIter = prevSolution.getSearchArcs().iterator();
        if ( pathIter.hasPrevious() ) {
          Iterator<SearchDirection> redoIter = prevSolution.getSearchPath().iterator();
          int lastIdx = pathIter.previousIndex();
          for ( int idx = 0; idx <= lastIdx; idx++ ) {

            // all of these operations should work without issue
            SearchDirection direction = redoIter.next();
            Arc arc = arcIter.next();
            if ( !attemptArcAssignment( arc, direction ) ) {
              throw new ConsistencyException( arc );
            }

            // add movement to newly constructed search path
            searchPath.add( direction );
            searchArcs.add( arc );
          }
        }

        // before any searching will begin, make sure the path we are going down shouldn't
        // just be skipped
        int rating = getRatingForCurrentState( searchTechnique );

        // current state isn't any better, return it as next solution
        if ( rating >= prevSolution.getRating() ) {
          return new Solution( arcs, rating, searchPath, searchArcs, true );
        }

        // retrieve arc which we are going to move second direction
        Arc arc = arcIter.next();

        // if we can't move the second direction here, continue
        // to move back in search path until we find an arc that can
        // be assigned the second direction
        if ( attemptArcAssignment( arc, secondDirection ) ) {
          // update new search path
          searchPath.add( secondDirection );
          searchArcs.add( arc );

          // before any searching will begin, make sure the path we are going down shouldn't
          // just be skipped
          rating = getRatingForCurrentState( searchTechnique );

          // current state isn't any better, return it as next solution
          if ( rating >= prevSolution.getRating() ) {
            return new Solution( arcs, rating, searchPath, searchArcs, true );
          }

          // set second direction flag so search will continue
          foundSecondDir = true;
        }
      }

      // if we weren't able to make another movement, there are not more solutions
      if ( searchPath.size() == 0 ) {
        return null;
      }
    }

    // dump current state of graph
    if ( logger.isDebugEnabled() ) {
      logger.debug( "-- Graph State Before Search --" );
      dumpStateToLog();
    }

    // look for arcs that are not bound
    int rating = -1;
    for ( Arc a : arcs ) {
      if ( !a.isRequirementKnown() ) {
        // try the first direction
        if ( attemptArcAssignment( a, firstDirection ) ) {
          searchPath.add( firstDirection );
        } else if ( attemptArcAssignment( a, secondDirection ) ) { // if first direction fails, try the second
          searchPath.add( secondDirection );
        } else { // If arc cannot be assigned a requirement value, throw an exception
          throw new ConsistencyException( a );
        }

        // record arc that was altered in search path
        searchArcs.add( a );

        // make sure solution is getting better
        if ( prevSolution != null ) {
          rating = getRatingForCurrentState( searchTechnique );

          // current state isn't any better, return it as next solution
          if ( rating >= prevSolution.getRating() ) {
            return new Solution( arcs, rating, searchPath, searchArcs, true );
          }
        }
      }
    }

    // compute rating if never computed
    if ( rating < 0 ) {
      rating = getRatingForCurrentState( searchTechnique );
    }

    // return solution to graph problem
    return new Solution( arcs, rating, searchPath, searchArcs, false );
  }

  // This method is used for debugging
  private List<Integer> toBitPath( List<SearchDirection> searchPath ) {
    List<Integer> result = new ArrayList<Integer>();
    for ( SearchDirection direction : searchPath ) {
      if ( direction == SearchDirection.LEFT ) {
        result.add( 0 );
      } else {
        result.add( 1 );
      }
    }
    return result;
  }

  private void dumpStateToLog() {
    if ( logger.isDebugEnabled() ) {
      logger.debug( "-------------------------------------------------" );

      for ( Arc arc : arcs ) {
        if ( arc.isRequired() ) {
          logger.debug( arc + "-> Yes" );
        } else if ( arc.isNotRequired() ) {
          logger.debug( arc + "-> No" );
        } else {
          logger.debug( arc + "-> ?" );
        }
      }

      for ( Node n : nodes ) {
        if ( n.isRequired() ) {
          logger.debug( n + "-> Yes" );
        } else if ( n.isNotRequired() ) {
          logger.debug( n + "-> No" );
        } else {
          logger.debug( n + "-> ?" );
        }
      }

      logger.debug( "=================================================" );
    }
  }

  /**
   * Calculates rating of current solution that is in progress
   *
   * @param searchTechnique Technique being used in searching to determine rating method
   */
  private int getRatingForCurrentState( PathType searchTechnique ) {
    int rating = 0;

    switch ( searchTechnique ) {
      case SHORTEST:
        for ( Node n : nodes ) {
          if ( n.isRequired() ) {
            rating++;
          }
        }
        break;

      case LOWEST_SCORE:
        for ( Node n : nodes ) {
          if ( n.isRequired() ) {
            rating += n.getTable().getRelativeSize() + 1;
          }
        }
        break;

      default:
        return 0;
    }

    return rating;
  }

  /**
   * Attempts to assign an arc to a given requirement status and returns true if consistency may still be possible
   */
  private boolean attemptArcAssignment( Arc arc, SearchDirection direction ) {
    // initialize search stack for roll back
    pushSearchStack();

    // try to assign value to element and propagate changes
    // to other elements. If propagation fails, rollback changes
    try {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Attempting move - Direction[" + direction + "], Arc[" + arc + "]" );
      }
      arc.setRequirement( ( direction == SearchDirection.LEFT ) ? false : true );
      propagate();

      if ( logger.isDebugEnabled() ) {
        logger.debug( "Move succeeded - State after move" );
        dumpStateToLog();
      }

      return true;
    } catch ( ConsistencyException cx ) {
      logger.debug( "Move failed" );
      popSearchStack();
      return false;
    }
  }

  /**
   * Called to start a new transaction in the search routing
   */
  private void pushSearchStack() {
    if ( searchStack == null ) {
      searchStack = new LinkedList<List<GraphElement>>();
    }
    searchStack.add( new ArrayList<GraphElement>() );
  }

  /**
   * Called to undo changes to elements during searching
   */
  private void popSearchStack() {
    List<GraphElement> alteredElements = searchStack.removeLast();
    for ( GraphElement element : alteredElements ) {
      element.clearRequirement();
    }
  }

  /**
   * Called to reset the search stack to original state before searching began
   */
  private void resetSearchStack() {
    while ( searchStack.size() > 0 ) {
      popSearchStack();
    }
  }

  /**
   * Performs work of propagating changes from source nodes to target nodes until consistency is reached
   *
   * @throws ConsistencyException When current graph cannot be made consistent
   */
  private void propagate() throws ConsistencyException {
    logger.debug( "Beginning propagation" );

    // first prune all non-required nodes
    for ( Node n : nodes ) {
      n.prune();
    }

    // process until queues are empty
    while ( basicNodeQueue.size() > 0 || extendedNodeQueue.size() > 0 ) {

      // process basic node consistency enforcement because it's
      // faster than the extended arc propagation checks
      if ( basicNodeQueue.size() > 0 ) {
        Node source = (Node) basicNodeQueue.remove();

        // check if source node is bound to a requirement setting
        // before processing arcs
        if ( source.isRequirementKnown() ) {

          // get list of arcs originating at node
          List<Arc> sourceArcs = source.getArcs();
          for ( Arc arc : sourceArcs ) {
            Node target = ( arc.getLeft() == source ) ? arc.getRight() : arc.getLeft();

            // if source is not required, arc is not required and
            // we can try and prune the target
            if ( source.isNotRequired() ) {
              arc.setRequirement( false );
              target.prune();
            }
          }
        }
      } else {
        // process extended enforcement of arc constraints on altered nodes
        // since we need to make sure that any node that is connected already
        Node source = (Node) extendedNodeQueue.remove();

        // enforce arc constraints on nodes
        List<Arc> sourceArcs = source.getArcs();
        for ( Arc arc : sourceArcs ) {
          arc.propagate( source );
        }
      }
    }

    // build required nodes list
    List<Node> requiredNodes = new LinkedList<Node>();
    for ( Node n : nodes ) {
      if ( n.isRequired() ) {
        requiredNodes.add( n );
      }
    }

    // make sure all required nodes can reach one another before returning
    // to ensure consistency
    if ( requiredNodes.size() > 1 ) {
      List<Node> targetList = new LinkedList<Node>( requiredNodes );
      Node start = requiredNodes.remove( 0 );
      if ( !start.canReachAllNodes( targetList ) ) {
        logger.debug( "Arc propagation completed, but not all targets could be reached from first node" );
        throw new ConsistencyException( start );
      }
    }

    logger.debug( "Propagation completed successfully" );
  }

  /**
   * Called whenever a target node is altered
   *
   * @param n Node that was altered
   */
  public void graphElementChanged( GraphElement element ) {
    List<GraphElement> searchDelta = ( searchStack != null && searchStack.size() > 0 ) ? searchStack.getLast() : null;
    if ( searchDelta != null ) {
      searchDelta.add( element );
    }

    if ( element instanceof Node ) {
      Node n = (Node) element;
      basicNodeQueue.add( n );

      // for more complex arcs we need to do extended propagation checks
      if ( n.getArcs().size() > 1 ) {
        extendedNodeQueue.add( n );
      }
    }
  }

  /**
   * Builds this graph based on data stored in list of relationships
   *
   * @param relationships List of relationships that describe the graph
   */
  private void build( List<RelationshipMeta> relationships ) {
    // loop through relationships and add necessary arcs
    // to the graph
    for ( RelationshipMeta relationship : relationships ) {
      // obtains nodes corresponding to tables
      Node left = getNodeForTable( relationship.getTableFrom() );
      Node right = getNodeForTable( relationship.getTableTo() );

      // record arcs that correspond to change
      Arc arc = createArc( left, right, relationship );

      // TODO: if any table requires a relationship when the table is
      // used to ensure proper filtering, add dependency here
      //
      // if (relationship.getTableFrom() requires relationship)
      // left.addRequiredArc(arc);
      //
      // if (relationship.getTableTo() requires relationship)
      // right.addRequiredArc(arc);
    }
  }

  /**
   * Returns a node corresponding to a business table
   *
   * @param table Table to locate node
   * @return Node corresponding to table
   */
  private Node getNodeForTable( BusinessTable table ) {
    Node n = tableNodeMap.get( table );
    if ( n == null ) {
      n = new Node( nodes.size(), table, this );

      nodes.add( n );
      tableNodeMap.put( table, n );
    }
    return n;
  }

  /**
   * Creates a new arc and records appropriate dependencies in internal collections and maps
   *
   * @param left  Left node for arc
   * @param right RIght node for arc
   */
  private Arc createArc( Node left, Node right, RelationshipMeta relationship ) {
    Arc arc = new Arc( left, right, relationship, this );
    arcs.add( arc );
    logger.trace( "Created " + arc );

    // add new arc to list of arcs originating from nodes
    left.addArc( arc );
    right.addArc( arc );

    return arc;
  }

  /**
   * Contains values indicating whether a search path took a left or right route
   */
  private static enum SearchDirection {
    LEFT, RIGHT
  }

  /**
   * Class that holds a possible solution to the graph problem for use during searching
   */
  private static class Solution {
    private int rating;

    private List<SearchDirection> searchPath;

    private List<Arc> searchArcs;

    private List<Boolean> solutionValues;

    private boolean partial;

    Solution( List<Arc> arcs, int rating, List<SearchDirection> searchPath, List<Arc> searchArcs, boolean partial ) {
      this.rating = rating;
      this.searchPath = searchPath;
      this.searchArcs = searchArcs;
      this.partial = partial;

      this.solutionValues = new LinkedList<Boolean>();
      for ( Arc a : arcs ) {
        solutionValues.add( a.isRequired() );
      }
    }

    public int getRating() {
      return rating;
    }

    public List<Boolean> getSolutionValues() {
      return solutionValues;
    }

    public List<SearchDirection> getSearchPath() {
      return searchPath;
    }

    public List<Arc> getSearchArcs() {
      return searchArcs;
    }

    public boolean isPartial() {
      return partial;
    }
  }
}
