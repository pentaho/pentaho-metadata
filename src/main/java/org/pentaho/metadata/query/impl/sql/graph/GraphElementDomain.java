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
 * Copyright (c) 2006 - 20011 Hitachi Vantara..  All rights reserved.
 * 
 * Contributed by Nick Coleman
 */
package org.pentaho.metadata.query.impl.sql.graph;

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
