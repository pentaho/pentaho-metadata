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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.TargetColumnType;

/**
 * this is the SQL implementation of physical column.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class SqlPhysicalColumn extends AbstractPhysicalColumn {

  private static final long serialVersionUID = -9131564777458111496L;

  public static final String TARGET_COLUMN = "target_column"; //$NON-NLS-1$
  public static final String TARGET_COLUMN_TYPE = "target_column_type"; //$NON-NLS-1$

  private SqlPhysicalTable table;

  public SqlPhysicalColumn() {
    super();
    setTargetColumnType( TargetColumnType.COLUMN_NAME );
  }

  public SqlPhysicalColumn( SqlPhysicalTable table ) {
    this();
    this.table = table;
  }

  @Override
  public IConcept getParent() {
    return table;
  }

  public String getTargetColumn() {
    return (String) getProperty( TARGET_COLUMN );
  }

  public void setTargetColumn( String targetTable ) {
    setProperty( TARGET_COLUMN, targetTable );
  }

  public TargetColumnType getTargetColumnType() {
    return (TargetColumnType) getProperty( TARGET_COLUMN_TYPE );
  }

  public void setTargetColumnType( TargetColumnType targetTableType ) {
    setProperty( TARGET_COLUMN_TYPE, targetTableType );
  }

  public IPhysicalTable getPhysicalTable() {
    return table;
  }

}
