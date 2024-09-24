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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
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
