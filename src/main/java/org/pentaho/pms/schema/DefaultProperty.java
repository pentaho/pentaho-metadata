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
package org.pentaho.pms.schema;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

@SuppressWarnings( "deprecation" )
public class DefaultProperty {
  private Class subject;
  private String name;
  private String description;
  private ConceptPropertyType conceptPropertyType;
  private ConceptPropertyInterface defaultValue;

  /**
   * @param subject
   * @param name
   * @param description
   * @param conceptPropertyType
   */
  public DefaultProperty( Class subject, String name, String description, ConceptPropertyType conceptPropertyType,
      ConceptPropertyInterface defaultValue ) {
    super();
    this.subject = subject;
    this.name = name;
    this.description = description;
    this.conceptPropertyType = conceptPropertyType;
    this.defaultValue = defaultValue;
  }

  /**
   * @param subject
   * @param name
   * @param conceptPropertyType
   */
  public DefaultProperty( Class subject, DefaultPropertyID defaultPropertyID ) {
    super();
    this.subject = subject;
    this.name = defaultPropertyID.getId();
    this.description = defaultPropertyID.getDescription();
    this.conceptPropertyType = defaultPropertyID.getType();
    this.defaultValue = defaultPropertyID.getDefaultValue();
  }

  public ConceptPropertyType getConceptPropertyType() {
    return conceptPropertyType;
  }

  public void setConceptPropertyType( ConceptPropertyType conceptPropertyType ) {
    this.conceptPropertyType = conceptPropertyType;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public Class getSubject() {
    return subject;
  }

  public void setSubject( Class subject ) {
    this.subject = subject;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }

  /**
   * @return the defaultValue
   */
  public ConceptPropertyInterface getDefaultValue() {
    return defaultValue;
  }

  /**
   * @param defaultValue
   *          the defaultValue to set
   */
  public void setDefaultValue( ConceptPropertyInterface defaultValue ) {
    this.defaultValue = defaultValue;
  }

  public String toString() {
    return name + ":" + description + ":" + conceptPropertyType.getDescription() + ":" + defaultValue.toString(); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
  }
}
