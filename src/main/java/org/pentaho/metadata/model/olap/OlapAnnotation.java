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

/**
 * Created by IntelliJ IDEA. User: rfellows Date: 9/23/11 Time: 3:03 PM To change this template use File | Settings |
 * File Templates.
 */
public class OlapAnnotation implements Serializable {
  private String name;
  private String value;

  public OlapAnnotation() {
  }

  public OlapAnnotation( String name, String value ) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue( String value ) {
    this.value = value;
  }

  public String asXml() {
    if ( name != null & value != null ) {
      return "          <Annotation name=\"" + name + "\">" + value + "</Annotation>";
    }
    return "";
  }

}
