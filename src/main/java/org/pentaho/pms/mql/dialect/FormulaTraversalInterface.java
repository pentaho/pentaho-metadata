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

package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;

/**
 * This interface is implemented by PMSFormula in order to traverse the libformula object model and call the appropriate
 * dialect related classes in this package.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public interface FormulaTraversalInterface {

  /**
   * this method traverses the libformula object model recursively
   * 
   * @param parent
   *          current object's parent in libformula object model
   * @param val
   *          current object in libformula object model
   * @param sb
   *          string buffer to append sql to
   * @param locale
   *          specific locale of the sql
   * @throws PentahoMetadataException
   *           if a problem occurs during the generation of sql
   */
  public void generateSQL( Object parent, Object val, StringBuffer sb, String locale ) throws PentahoMetadataException;

  /**
   * this method allows access to parameter values, so if a function needs to perform runtime evaluation before
   * generating SQL, it can.
   * 
   * @param lookup
   *          parameter value
   * @return value of the parameter
   * @throws PentahoMetadataException
   */
  public Object getParameterValue( ContextLookup lookup ) throws PentahoMetadataException;
}
