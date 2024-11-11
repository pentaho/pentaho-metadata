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
 * Concrete, lightweight, serializable implementation of an {@see IColumn} object
 * 
 * @author jamesdixon
 * 
 */
public class Column {

  private static final long serialVersionUID = 3751750093446278893L;
  private String id, name, description;
  private String type;
  private String[] aggTypes;
  private String defaultAggType;
  private String selectedAggType;
  private String fieldType;
  private String category;
  private String getHorizontalAlignment;
  private String formatMask;
  private boolean hiddenForUser;

  public String getHorizontalAlignment() {
    return getHorizontalAlignment;
  }

  public void setHorizontalAlignment( String getHorizontalAlignment ) {
    this.getHorizontalAlignment = getHorizontalAlignment;
  }

  public String getFormatMask() {
    return formatMask;
  }

  public void setFormatMask( String formatMask ) {
    this.formatMask = formatMask;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory( String category ) {
    this.category = category;
  }

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType( String fieldType ) {
    this.fieldType = fieldType;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public String getDefaultAggType() {
    return defaultAggType;
  }

  public String[] getAggTypes() {
    return aggTypes;
  }

  public void setAggTypes( String[] aggTypes ) {
    this.aggTypes = aggTypes;
  }

  public void setDefaultAggType( String defaultAggType ) {
    this.defaultAggType = defaultAggType;
  }

  public void setSelectedAggType( String aggType ) {
    this.selectedAggType = aggType;
  }

  public String getSelectedAggType() {
    return this.selectedAggType;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setHiddenForUser( boolean hiddenForUser ) {
    this.hiddenForUser = hiddenForUser;
  }

  public boolean isHiddenForUser() {
    return hiddenForUser;
  }

  public String getGetHorizontalAlignment() {
    return getHorizontalAlignment;
  }

  public void setGetHorizontalAlignment( String getHorizontalAlignment ) {
    this.getHorizontalAlignment = getHorizontalAlignment;
  }
}
