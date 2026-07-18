/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package org.pentaho.pms.mql.dialect;

/**
 * This method manages the generation of the SQL for a specific function.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public interface SQLOperatorGeneratorInterface {

  /**
   * return the sql for this operator
   * 
   * @return sql
   */
  public String getOperatorSQL();
}
