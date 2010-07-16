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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model.olap;

import org.pentaho.metadata.model.LogicalColumn;

import java.io.Serializable;

public class OlapMeasure implements Cloneable, Serializable {
  private String name;
  private LogicalColumn logicalColumn;

  public OlapMeasure() {
  }

  /**
   * @param name
   * @param logicalColumn
   */
  public OlapMeasure(String name, LogicalColumn logicalColumn) {
    this();
    this.name = name;
    this.logicalColumn = logicalColumn;
  }

  public Object clone() {
    // shallow copy of logical column is desired
    return new OlapMeasure(name, logicalColumn);
  }

  /**
   * @return the logicalColumn
   */
  public LogicalColumn getLogicalColumn() {
    return logicalColumn;
  }

  /**
   * @param logicalColumn
   *          the logicalColumn to set
   */
  public void setLogicalColumn(LogicalColumn logicalColumn) {
    this.logicalColumn = logicalColumn;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

}
