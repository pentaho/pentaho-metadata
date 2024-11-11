/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.mql.graph;

/**
 * Contains the Domain of possible values for an element of the graph
 */
public class GraphElementDomain {
  private GraphElement element;
  private GraphElementRequirement requirement;

  public GraphElementDomain( GraphElement element ) {
    this.element = element;
    this.requirement = GraphElementRequirement.UNKNOWN;
  }

  /**
   * Assigns a requirement value to this domain
   * 
   * @param required
   *          True if element is required / false if not required
   * @return True if domain is altered
   * @throws ConsistencyException
   *           When assignment is inconsistent with graph constraints or an attempt to set a different value when one is
   *           already set
   */
  public boolean setRequirement( boolean required ) throws ConsistencyException {
    // check that changes are actually occuring
    if ( ( !required && requirement == GraphElementRequirement.NOT_REQUIRED )
        || ( required && requirement == GraphElementRequirement.REQUIRED ) ) {
      return false;
    }

    // ensure that domain has not already been bound to another value
    if ( requirement != GraphElementRequirement.UNKNOWN ) {
      throw new ConsistencyException( element );
    }

    if ( required ) {
      requirement = GraphElementRequirement.REQUIRED;
    } else {
      requirement = GraphElementRequirement.NOT_REQUIRED;
    }

    return true;
  }

  /**
   * Changes requirement setting to unknown
   */
  public void clearRequirement() {
    requirement = GraphElementRequirement.UNKNOWN;
  }

  /**
   * Returns the current value assigned for requirement of the graph element
   */
  public GraphElementRequirement getRequirement() {
    return requirement;
  }
}
