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
 * Copyright (c) 2009 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.model.concept.types;

/**
 * The data type of a physical or logical column.
 * 
 * <li> {@link #UNKNOWN}
 * <li> {@link #STRING}
 * <li> {@link #DATE}
 * <li> {@link #BOOLEAN}
 * <li> {@link #NUMERIC}
 * <li> {@link #BINARY}
 * <li> {@link #IMAGE}
 * <li> {@link #URL}
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public enum DataType {
  UNKNOWN( 0, "Unknown", "DataType.USER_UNKNOWN_DESC" ), //$NON-NLS-1$  //$NON-NLS-2$
  STRING( 1, "String", "DataType.USER_STRING_DESC" ), //$NON-NLS-1$  //$NON-NLS-2$
  DATE( 2, "Date", "DataType.USER_DATE_DESC" ), //$NON-NLS-1$  //$NON-NLS-2$
  BOOLEAN( 3, "Boolean", "DataType.USER_BOOLEAN_DESC" ), //$NON-NLS-1$  //$NON-NLS-2$
  NUMERIC( 4, "Numeric", "DataType.USER_NUMERIC_DESC" ), //$NON-NLS-1$  //$NON-NLS-2$
  BINARY( 5, "Binary", "DataType.USER_BINARY_DESC" ), //$NON-NLS-1$  //$NON-NLS-2$
  IMAGE( 6, "Image", "DataType.USER_IMAGE_DESC" ), //$NON-NLS-1$  //$NON-NLS-2$
  URL( 7, "URL", "DataType.USER_URL_DESC" ); //$NON-NLS-1$  //$NON-NLS-2$

  private int type;
  private String name, description;

  DataType( int type, String name, String description ) {
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public int getType() {
    return type;
  }

  public void setType( int type ) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

}
