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

package org.pentaho.metadata.query.impl.sql.graph;

/**
 * Interface implements by all elements of the graph
 */
public interface GraphElement {

  /**
   * Returns true if this <code>GraphElement</code> is required by the graph
   * 
   * @return True if <code>GraphElement</code> is required
   */
  public boolean isRequired();

  /**
   * Returns true if this <code>GraphElement</code> is not required by the graph
   * 
   * @return True if <code>GraphElement</code> is not required
   */
  public boolean isNotRequired();

  /**
   * Returns true if this <code>GraphElement</code> is known to be required or not required by the graph
   * 
   * @return True if <code>GraphElement</code> requirement is known
   */
  public boolean isRequirementKnown();

  /**
   * Assigns a requirement value to this element
   * 
   * @param required
   *          True if element is required / false if not required
   * @throws ConsistencyException
   *           When assignment is inconsistent with graph constrains
   */
  public void setRequirement( boolean required ) throws ConsistencyException;

  /**
   * Changes requirement setting to unknown
   */
  public void clearRequirement();

  /**
   * Returns the status of the <code>queued</code> flag which is used by the graphing functions to determine if this
   * <code>GraphElement</code> is currently in the queue waiting to be processed.
   * 
   * @return value of <code>queued</code> flag
   */
  public boolean isQueued();

  /**
   * Sets value of <code>queued</code> flag
   * 
   * @param queued
   *          New value of <code>queued</code> flag
   * @see #isQueued()
   */
  public void setQueued( boolean queued );
}
