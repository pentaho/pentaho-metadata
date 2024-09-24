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
package org.pentaho.metadata.model.olap;

import java.io.Serializable;

public class OlapRole implements Cloneable, Serializable {

  private String name;
  private String definition;

  public OlapRole() {
  }

  public OlapRole( String name, String roleXml ) {
    super();
    this.name = name;
    this.definition = roleXml;
  }

  @Override
  protected Object clone() {
    return new OlapRole( name, definition );
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition( String roleXml ) {
    this.definition = roleXml;
  }


}
