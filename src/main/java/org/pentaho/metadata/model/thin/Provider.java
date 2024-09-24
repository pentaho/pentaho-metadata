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
package org.pentaho.metadata.model.thin;

/**
 * Concrete, lightweight, serializable object that holds information about a model provider
 * 
 * @author jamesdixon
 * 
 */
public class Provider {

  private String id;

  private String name;

  public Provider() {

  }

  public Provider( String id, String name ) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

}
