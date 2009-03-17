/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 *
 * Copyright 2009 Pentaho Corporation.  All rights reserved. 
 *
 * @created Mar, 2009
 * @author James Dixon
 * 
*/
package org.pentaho.pms.schema.v3.envelope;

/**
 * This class is a generic super-class that holds the id, and the localized name and
 * description of an element. This class can be sub-classed by anything needing to
 * hold these attributes.
 * @author jamesdixon
 *
 */
public class Envelope {

  private String id;
  
  private String name;
  
  private String description;

  /**
   * Returns the id of this object
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id of this object
   * @param id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the localized name of this object
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the localized name of this object
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the localized description of this object
   * @return
   */
  public String getDescription() {
    return description;
  }
  
  /**
   * Sets the localized description of this object
   * @param name
   */
  public void setDescription(String description) {
    this.description = description;
  }

  
}
