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

import java.util.HashMap;

/**
 * Concrete, lightweight, serializable implementation of a model element
 * 
 * @author jamesdixon
 * 
 */
public class Element {

  private static final long serialVersionUID = 3751750093446278893L;

  public static final String CAPABILITY_CAN_FILTER = "element_filter"; // default is true
  public static final String CAPABILITY_CAN_SEARCH = "element_search"; // default is true
  public static final String CAPABILITY_CAN_SORT = "element_sortable"; // default is true

  public static final String AGG_TYPES_SUM = "SUM";
  public static final String AGG_TYPES_AVERAGE = "AVERAGE";
  public static final String AGG_TYPES_MIN = "MINIMUM";
  public static final String AGG_TYPES_MAX = "MAXIMUM";
  public static final String AGG_TYPES_COUNT = "COUNT";
  public static final String AGG_TYPES_COUNT_DISTINCT = "COUNT_DISTINCT";
  public static final String AGG_TYPES_NONE = "NONE";
  public static final String AGG_TYPES_VAR = "VAR";
  public static final String AGG_TYPES_STDDEV = "STDDEV";
  public static final String AGG_TYPES_CALC = "CALC";
  public static final String AGG_TYPES_UNKNOWN = "UNKNOWN";

  private String id, name, description;
  private String dataType;
  private String[] availableAggregations;
  private String defaultAggregation = AGG_TYPES_NONE;
  private String selectedAggregation = AGG_TYPES_NONE;
  private String elementType;
  private String horizontalAlignment;
  private String formatMask;
  private boolean hiddenForUser;
  private String parentId;
  private HashMap capabilities = new HashMap();
  private boolean isQueryElement = true;

  public boolean getIsQueryElement() {
    return isQueryElement;
  }

  public void setIsQueryElement( boolean isQueryElement ) {
    this.isQueryElement = isQueryElement;
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

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType( String dataType ) {
    this.dataType = dataType;
  }

  public String[] getAvailableAggregations() {
    return availableAggregations;
  }

  public void setAvailableAggregations( String[] availableAggregations ) {
    this.availableAggregations = availableAggregations;
  }

  public String getElementType() {
    return elementType;
  }

  public String getDefaultAggregation() {
    return defaultAggregation;
  }

  public void setDefaultAggregation( String defaultAggregation ) {
    this.defaultAggregation = defaultAggregation;
  }

  public String getSelectedAggregation() {
    return selectedAggregation;
  }

  public void setSelectedAggregation( String selectedAggregation ) {
    this.selectedAggregation = selectedAggregation;
  }

  public void setElementType( String elementType ) {
    this.elementType = elementType;
  }

  public String getHorizontalAlignment() {
    return horizontalAlignment;
  }

  public void setHorizontalAlignment( String horizontalAlignment ) {
    this.horizontalAlignment = horizontalAlignment;
  }

  public String getFormatMask() {
    return formatMask;
  }

  public void setFormatMask( String formatMask ) {
    this.formatMask = formatMask;
  }

  public boolean isHiddenForUser() {
    return hiddenForUser;
  }

  public void setHiddenForUser( boolean hiddenForUser ) {
    this.hiddenForUser = hiddenForUser;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId( String parentId ) {
    this.parentId = parentId;
  }

  public HashMap getCapabilities() {
    return capabilities;
  }

  public void setCapabilities( HashMap capabilities ) {
    this.capabilities = capabilities;
  }

}
