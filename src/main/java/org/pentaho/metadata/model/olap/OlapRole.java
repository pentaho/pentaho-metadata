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
