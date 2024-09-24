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
package org.pentaho.metadata.query.model;

import java.io.Serializable;

/**
 * This class defines the order of the results from a logical query model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class Order implements Serializable {

  private static final long serialVersionUID = 7828692078614137281L;

  public enum Type {
    ASC, DESC
  };

  private Selection selection;
  private Type type;

  public Order( Selection selection, Type type ) {
    this.selection = selection;
    this.type = type;
  }

  public Selection getSelection() {
    return selection;
  }

  public Type getType() {
    return type;
  }

}
