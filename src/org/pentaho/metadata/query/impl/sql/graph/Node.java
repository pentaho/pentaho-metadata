/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 20011 Pentaho Corporation..  All rights reserved.
 * 
 * Contributed by Nick Coleman
 */
package org.pentaho.metadata.query.impl.sql.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.model.LogicalTable;

public class Node implements GraphElement, Comparable {
  private static final Log logger = LogFactory.getLog( Node.class );

  private int id;
  private LogicalTable table;
  private GraphElementChangeListener listener;
  private GraphElementDomain domain;
  private List<Arc> arcs;
  private List<Arc> requiredArcs;
  private boolean queued;

  public Node( int id, LogicalTable table, GraphElementChangeListener listener ) {
    this.id = id;
    this.table = table;
    this.listener = listener;
    this.domain = new GraphElementDomain( this );
    this.arcs = new ArrayList<Arc>();
  }

  /**
   * Returns true if this <code>Node</code> is required by the graph
   * 
   * @return True if <code>Node</code> is required
   */
  public boolean isRequired() {
    return domain.getRequirement() == GraphElementRequirement.REQUIRED;
  }

  /**
   * Returns true if this <code>Node</code> is not required by the graph
   * 
   * @return True if <code>Node</code> is not required
   */
  public boolean isNotRequired() {
    return domain.getRequirement() == GraphElementRequirement.NOT_REQUIRED;
  }

  /**
   * Returns true if this <code>Node</code> is known to be required or not required by the graph
   * 
   * @return True if <code>Node</code> requirement is unknown
   */
  public boolean isRequirementKnown() {
    return domain.getRequirement() != GraphElementRequirement.UNKNOWN;
  }

  /**
   * Returns the <code>BusinessTable</code> associated with this <code>Node</code>
   * 
   * @return <code>BusinessTable</code> for <code>Node</code>
   */
  public LogicalTable getTable() {
    return table;
  }

  /**
   * Returns the status of the <code>queued</code> flag which is used by the graphing functions to determine if this
   * <code>Node</code> is currently in the queue waiting to be processed.
   * 
   * @return value of <code>queued</code> flag
   */
  public boolean isQueued() {
    return queued;
  }

  /**
   * Sets value of <code>queued</code> flag
   * 
   * @param queued
   *          New value of <code>queued</code> flag
   * @see #isQueued()
   */
  public void setQueued( boolean queued ) {
    this.queued = queued;
  }

  /**
   * Attaches an <code>Arc</code> to this <code>Node</code>
   * 
   * @param arc
   *          <code>Arc</code> to associate with <code>Node</code>
   */
  public void addArc( Arc arc ) {
    arcs.add( arc );
  }

  /**
   * Adds an <code>Arc</code> that should be required if this <code>Node</code> is required
   * 
   * @param arc
   *          Arc this node depends upon
   */
  public void addRequiredArc( Arc arc ) {
    if ( requiredArcs == null ) {
      requiredArcs = new LinkedList<Arc>();
    }
    requiredArcs.add( arc );
  }

  /**
   * @see Comparable.compareTo(Object)
   */
  public int compareTo( Object o ) {
    Node n = (Node) o;

    boolean thisKnown = isRequirementKnown();
    boolean nKnown = n.isRequirementKnown();
    if ( thisKnown && !nKnown ) {
      return -1;
    }
    if ( !thisKnown && nKnown ) {
      return 1;
    }

    return ( arcs.size() - n.arcs.size() ) * ( thisKnown ? -1 : 1 );
  }

  /**
   * Returns list of <code>Arcs</code> associated to this <code>Node</code>
   * 
   * @return
   */
  public List<Arc> getArcs() {
    return arcs;
  }

  /**
   * Assigns a requirement value to this element
   * 
   * @param required
   *          True if element is required / false if not required
   * @throws ConsistencyException
   *           When assignment is inconsistent with graph constraints or an attempt to set a different value when one is
   *           already set
   */
  public void setRequirement( boolean required ) throws ConsistencyException {
    // check if value will alter the domain
    if ( domain.setRequirement( required ) ) {
      if ( required ) {
        // update all required arcs' status
        if ( requiredArcs != null ) {
          for ( Arc arc : requiredArcs ) {
            arc.setRequirement( true );
          }
        }

        // if node is required and there is only one possible arc, the arc
        // will be required as well
        Arc requiredArc = null;
        for ( Arc arc : arcs ) {
          if ( !arc.isNotRequired() ) {
            if ( requiredArc == null ) {
              requiredArc = arc;
            } else {
              requiredArc = null;
              break;
            }
          }
        }

        if ( requiredArc != null ) {
          requiredArc.setRequirement( true );
        }
      } else {
        // if node is not required, no arcs to it will be either
        for ( Arc arc : arcs ) {
          arc.setRequirement( false );
        }
      }

      listener.graphElementChanged( this );
    }
  }

  /**
   * Changes requirement setting to unknown
   */
  public void clearRequirement() {
    domain.clearRequirement();
  }

  /**
   * Returns true if this node was successfully marked as not required
   * 
   * @throws ConsistencyException
   *           If a prune attempt is made, but graph constraints are violated
   */
  public boolean prune() throws ConsistencyException {
    boolean prune = false;

    // when no arcs reach this node it should be pruned
    if ( arcs.size() == 0 ) {
      prune = true;
    }

    // if only 1 arc to this node and it is not yet marked as required we can prune it
    else if ( domain.getRequirement() == GraphElementRequirement.UNKNOWN ) {
      // if list has only 1 arc, no need to check if it is required
      if ( arcs.size() == 1 ) {
        prune = true;
      }

      // when multiple paths exist, make sure not more than one is possible
      else {
        int possiblePaths = 0;
        for ( Arc arc : arcs ) {
          // if any arc is required, this node must be required
          if ( arc.isRequired() ) {
            setRequirement( true );
            return false;
          }

          if ( !arc.isNotRequired() ) {
            possiblePaths++;
          }

          // no need to check if there are more than 2 paths
          if ( possiblePaths > 1 ) {
            break;
          }
        }

        if ( possiblePaths < 2 ) {
          prune = true;
        }
      }
    }

    // attempt to prune if necessary
    if ( prune ) {
      setRequirement( false );
    }

    return prune;
  }

  /**
   * Returns true if the target node can be reached from this node to a target.
   * 
   * @param target
   *          Node to be found during search
   * @param avoidArc
   *          An optional arc to avoid when searching
   * @return True if target node can be reached
   */
  public boolean canReachNode( Node target, Arc avoidArc ) {
    List<Node> visitedList = new LinkedList<Node>();

    if ( doTargetSearch( this, null, target, visitedList, avoidArc ) ) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns true if the all the nodes in target list can be reached from this node
   * 
   * @param targetList
   *          List of Nodes to find
   * @return True if all target nodes can be reached
   */
  public boolean canReachAllNodes( List<Node> targetList ) {
    List<Node> visitedList = new LinkedList<Node>();

    if ( doTargetSearch( this, targetList, null, visitedList, null ) ) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns true if the target node can be reached from the current node through some other <code>Arc</code> than this
   * one.
   * 
   * @param current
   *          Node to search for an arc that will lead to target
   * @param targetList
   *          List of Nodes to find
   * @param target
   *          Node to be found during search
   * @param visitedList
   *          List of nodes previously visited to avoid visiting again
   * @param avoidArc
   *          An optional arc to avoid when searching
   * @return True if target node can be reached
   */
  private boolean
    doTargetSearch( Node current, List<Node> targetList, Node target, List<Node> visitedList, Arc avoidArc ) {
    // append the current node to the list of visited nodes
    // the list upon the first call
    visitedList.add( current );

    for ( Arc arc : current.getArcs() ) {
      // locate a possible path to another node, lets see where it goes
      if ( arc != avoidArc && !arc.isNotRequired() ) {
        Node left = arc.getLeft();
        Node right = arc.getRight();

        // determine to which node arc leads from current node
        Node next = ( left == current ) ? right : left;

        if ( target != null ) {
          // if the arc points at our target, we can stop
          if ( next == target ) {
            return true;
          }
        }
        // If the arc points at our target, remove it from the list.
        // If all targets are removed, we can stop searching
        if ( targetList.remove( next ) && targetList.size() == 0 ) {
          return true;
        }

        // if we haven't already visited this node, continue
        // search from it for target
        if ( !visitedList.contains( next ) ) {
          if ( doTargetSearch( next, targetList, target, visitedList, avoidArc ) ) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public int getId() {
    return id;
  }

  public String toString() {
    StringBuffer buff = new StringBuffer();
    buff.append( "Node[" + id + "]: Table " );
    if ( ( table != null ) && ( table.getPhysicalTable() != null ) ) {
      buff.append( table.getPhysicalTable().getName( "" ) );
    } else if ( table != null ) {
      buff.append( table.getName( "" ) ); // Business Table
    } else {
      buff.append( "*null*" ); // Business
    }
    return buff.toString();
  }
}
