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

import org.pentaho.metadata.model.LogicalRelationship;

/**
 * Represents a bi-directional relationship between two <code>Node</code> objects that will be reviewed by the graph to
 * determine if it is required to connect all necessary <code>Node</code> objects.
 * 
 * @author Nick Coleman
 */
public class Arc implements GraphElement {
  private Node left;
  private Node right;
  private GraphElementDomain domain;
  private GraphElementChangeListener listener;
  private LogicalRelationship relationship;
  private boolean queued;

  public Arc( Node left, Node right, LogicalRelationship relationship, GraphElementChangeListener listener ) {
    this.left = left;
    this.right = right;
    this.relationship = relationship;
    this.listener = listener;
    this.domain = new GraphElementDomain( this );
  }

  /**
   * Returns true if this <code>Arc</code> is required by the graph
   * 
   * @return True if <code>Arc</code> is required
   */
  public boolean isRequired() {
    return domain.getRequirement() == GraphElementRequirement.REQUIRED;
  }

  /**
   * Returns true if this <code>Arc</code> is not required by the graph
   * 
   * @return True if <code>Arc</code> is not required
   */
  public boolean isNotRequired() {
    return domain.getRequirement() == GraphElementRequirement.NOT_REQUIRED;
  }

  /**
   * Returns true if this <code>Arc</code> is known to be required or not required by the graph
   * 
   * @return True if <code>Arc</code> requirement is unknown
   */
  public boolean isRequirementKnown() {
    return domain.getRequirement() != GraphElementRequirement.UNKNOWN;
  }

  /**
   * Returns left <code>Node</code> of <code>Arc</code>
   * 
   * @return left <code>Node</code> of <code>Arc</code>
   */
  public Node getLeft() {
    return left;
  }

  /**
   * Returns right <code>Node</code> of <code>Arc</code>
   * 
   * @return right <code>Node</code> of <code>Arc</code>
   */
  public Node getRight() {
    return right;
  }

  /**
   * Returns <code>RelationshipMeta</code> associated with this <code>Arc</code>
   * 
   * @return <code>RelationshipMeta</code> associated with this <code>Arc</code>
   */
  public LogicalRelationship getRelationship() {
    return this.relationship;
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

      // if arc is required, both nodes are also required
      if ( required ) {
        left.setRequirement( true );
        right.setRequirement( true );
      } else {
        // if arc is not required, we can prune nodes that this was the last
        // possible path to
        left.prune();
        right.prune();
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
   * Enforces the constraints of this <code>Arc</code> on related <code>Nodes</code>. This method is called during
   * propagation of changes in a graph when a <code>Node</code> changes.
   * 
   * @param source
   *          A node that is bound from which arc constraints should be enforced
   * @throws ConsistencyException
   *           When constraints cannot be enforced
   */
  public void propagate( Node source ) throws ConsistencyException {
    Node target = ( source == left ) ? right : left;

    // if source isn't required, this arc isn't either
    if ( source.isNotRequired() ) {
      setRequirement( false );
    }

    switch ( domain.getRequirement() ) {
    // if this arc has been marked as required, the target will be
    // required as well
      case REQUIRED:
        target.setRequirement( true );
        return;

        // if this arc has been marked as required, try to prune target
      case NOT_REQUIRED:
        target.prune();
        return;
    }

    // if the source is required and this arc is unknown, we know that
    // the source has multiple possible paths. We need to see if
    // the node this arc leads to can be reached by one of the
    // other possible arcs. If it can't this arc will be required.
    if ( source.isRequired() ) {
      if ( !source.canReachNode( target, null ) ) {
        setRequirement( true );
      }
    }
  }

  public String toString() {
    return "Arc: Left[" + left.getId() + "] -> Right[" + right.getId() + "]";
  }
}
