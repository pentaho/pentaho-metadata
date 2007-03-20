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
*/
package org.pentaho.pms.schema.concept;

import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.security.Security;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public interface ConceptUtilityInterface
{
    /**
     * @return the id
     */
    public abstract String getId();

    /**
     * @param id the id to set
     */
    public abstract void setId(String id) throws ObjectAlreadyExistsException;
    
    /**
     * @return the description of the model element 
     */
    public String getModelElementDescription();

    /**
     * @return The concept
     */
    public abstract ConceptInterface getConcept();

    /**
     * @param concept The concept to set
     */
    public abstract void setConcept(ConceptInterface concept);

    /** 
     *  In case the changed flag needs to be set or cleared. 
     *  @param changed true if the concept needs to be set to changed 
     *  */
    public void setChanged(boolean changed);

    /** 
     *  In case the changed flag needs to be set. 
     *  */
    public void setChanged();

    /**
     * @return true if the concept was changed;
     */
    public boolean hasChanged();
 
    /**
     * Clear the changed flag of this concept and the underlying objects
     */
    public void clearChanged();
    
    
    
    
    /**
     * @param locale The prefered locale to go for 
     * @return The localized name or the id if nothing was found for that locale
     */
    public String getDisplayName(String locale);

    /**
     * Set the localized name of the object
     * 
     * @param locale The prefered locale to go for
     * @param name the name to set
     */
    public void setName(String locale, String name);
    
    
    /**
     * @param locale The prefered locale to go for 
     * @return The localized name or null if nothing was found for that locale
     */
    public String getName(String locale);
    
    /**
     * Set the localized description of the object
     * @param locale The prefered locale to go for
     * @param description the description to set
     * 
     */
    public void setDescription(String locale, String description);
    
    /**
     * @param locale The prefered locale to go for 
     * @return The localized description or null if nothing was found for that locale
     */
    public String getDescription(String locale);

    public String getTargetSchema();
    public void setTargetSchema(String targetSchema);

    public String getTargetTable();
    public void setTargetTable(String targetTable);
    
    public String getMask();
    
    public void setMask(String mask);
    
    public TableTypeSettings getTableType();
    
    public void setTableType(TableTypeSettings type);
    
    public boolean isDimensionTable();
    
    public boolean isFactTable();
    
    public int getRelativeSize();
    
    public void setRelativeSize(int size);
    
    public String getFormula();
    
    public void setFormula(String formula);
    
    public FieldTypeSettings getFieldType();
    
    /**
     * @param columnType the column type to set
     */
    public void setFieldType(FieldTypeSettings fieldType);
    public String getFieldTypeDesc();
    
    public void setAggregationType(AggregationSettings aggregationType);
    public AggregationSettings getAggregationType();
    public String getAggregationTypeDesc();
    
    public void setHidden(boolean hidden);    
    public void flipHidden();
    public boolean isHidden();
    
    public boolean isExact();
    public void setExact(boolean exact);    
    public void flipExact();
    
    public boolean isFactField();
    public boolean isDimensionField();
    public boolean isAttributeField();
    
    public boolean hasAggregate();    
    
    public DataTypeSettings getDataType();
    public void setDataType(DataTypeSettings dataType);
    
    public Security getSecurity();
    public void setSecurity(Security security);
}

