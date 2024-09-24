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
package org.pentaho.pms.schema.concept.types;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;

/**
 * Base class for all concept properties.
 * 
 * @author Matt
 * @deprecated as of metadata 3.0.
 */
public abstract class ConceptPropertyBase implements ConceptPropertyInterface, Cloneable {
  private String id;
  private boolean required;

  /**
   * @param id
   */
  public ConceptPropertyBase( String id ) {
    this( id, false );
  }

  public ConceptPropertyBase( String id, boolean required ) {
    super();
    this.id = id;
    this.required = required;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId( String id ) {
    this.id = id;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired( final boolean required ) {
    this.required = required;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
