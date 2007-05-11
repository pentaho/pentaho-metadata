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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregation;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.ConceptPropertyFieldType;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumber;
import org.pentaho.pms.schema.concept.types.security.ConceptPropertySecurity;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.concept.types.tabletype.ConceptPropertyTableType;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.changes.AllowsIDChangeListenersInterface;
import be.ibridge.kettle.core.changes.IDChangedEvent;
import be.ibridge.kettle.core.changes.IDChangedListener;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.list.UniqueList;

public class ConceptUtilityBase extends ChangedFlag implements AllowsIDChangeListenersInterface
{
    private String id;
    private ConceptInterface concept;

    protected transient List idChangedListeners;

    public ConceptUtilityBase()
    {
        concept = new Concept();
        idChangedListeners = new ArrayList();
    }

    public ConceptUtilityBase(String id)
    {
        this();
        this.id = id;
    }

    public boolean equals(Object obj) {
      if (obj instanceof ConceptUtilityBase == false) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      ConceptUtilityBase rhs = (ConceptUtilityBase) obj;

      String lhsId = null != id ? id.toUpperCase() : null;
      String rhsId = null != rhs.id ? rhs.id.toUpperCase() : null;

      return new EqualsBuilder().append(lhsId, rhsId).isEquals();
    }

    public int hashCode() {
      String idToHash = null != id ? id.toUpperCase() : null;
      return new HashCodeBuilder(11, 113).append(idToHash).toHashCode();
    }

    public String toString()
    {
        return id;
    }

    public boolean hasChanged()
    {
        if (concept.hasChanged()) return true;
        return super.hasChanged();
    }

    public void clearChanged()
    {
        setChanged(false);
        concept.clearChanged();
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     * @throws ObjectAlreadyExistsException in case the ID already exists in a parent list (UniqueList)
     */
    public void setId(String id) throws ObjectAlreadyExistsException
    {
        // Verify uniqueness BEFORE we change the ID!
        // We do this by calling the changed listener.
        // The verifyer needs to be somewhere else.
        // That is because we don't want this class to "know" about the parent list in which it is contained.
        //
        for (int i=0;i<idChangedListeners.size();i++)
        {
            IDChangedListener listener = (IDChangedListener) idChangedListeners.get(i);
            try
            {
                listener.IDChanged(new IDChangedEvent(this.id, id, this));
            }
            catch(ObjectAlreadyExistsException e)
            {
                throw new ObjectAlreadyExistsException(Messages.getString("ConceptUtilityBase.ERROR_0001_OBJECT_ID_EXISTS", id), e); //$NON-NLS-1$
            }
        }
        this.id = id;
        setChanged();
    }

    /**
     * @return The concept
     */
    public ConceptInterface getConcept()
    {
        return concept;
    }

    /**
     * @param concept The concept to set
     */
    public void setConcept(ConceptInterface concept)
    {
        this.concept = concept;
        setChanged();
    }

    /**
     * @param locale The prefered locale to go for
     * @return The localized name or the id if nothing was found for that locale
     */
    public String getDisplayName(String locale)
    {
        String name = concept.getName(locale);
        if (Const.isEmpty(name)) return id; else return name;
    }

    /**
     * Set the localized name of the object
     *
     * @param locale The prefered locale to go for
     * @param name the name to set
     */
    public void setName(String locale, String name)
    {
        concept.setName(locale, name);
    }

    /**
     * @param locale The prefered locale to go for
     * @return The localized name or null if nothing was found for that locale
     */
    public String getName(String locale)
    {
        String name = concept.getName(locale);
        if (Const.isEmpty(name)) return id;
        return name;
    }


    /**
     * Set the localized description of the object
     * @param locale The prefered locale to go for
     * @param description the description to set
     *
     */
    public void setDescription(String locale, String description)
    {
        concept.setDescription(locale, description);
    }

    /**
     * @param locale The prefered locale to go for
     * @return The localized description or null if nothing was found for that locale
     */
    public String getDescription(String locale)
    {
        String description = concept.getDescription(locale);
        if (Const.isEmpty(description)) return id;
        return description;
    }

    public String getTargetSchema()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.TARGET_SCHEMA.getId());
        if (property!=null) return property.toString(); // It's a String, nothing fancy here...
        return null;
    }

    public void setTargetSchema(String targetSchema)
    {
        concept.addProperty(new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(), targetSchema));
    }

    public String getTargetTable()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.TARGET_TABLE.getId());
        if (property!=null) return property.toString(); // It's a String, nothing fancy here...
        return null;
    }

    public void setTargetTable(String targetTable)
    {
        concept.addProperty(new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(), targetTable));
    }

    public String getMask()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.MASK.getId());
        if (property!=null) return property.toString(); // It's a String, nothing fancy here...
        return null;
    }

    public void setMask(String mask)
    {
        concept.addProperty(new ConceptPropertyString(DefaultPropertyID.MASK.getId(), mask));
    }


    public TableTypeSettings getTableType()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.TABLE_TYPE.getId());
        if (property==null)
        {
            return TableTypeSettings.OTHER;
        }
        else
        {
            return (TableTypeSettings) property.getValue();
        }
    }

    public void setTableType(TableTypeSettings type)
    {
        ConceptPropertyInterface property = new ConceptPropertyTableType(DefaultPropertyID.TABLE_TYPE.getId(), type);
        concept.addProperty(property);
    }

    public boolean isDimensionTable()
    {
        return getTableType().isDimension();
    }

    public boolean isFactTable()
    {
        return getTableType().isFact();
    }

    public int getRelativeSize()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.RELATIVE_SIZE.getId());
        if (property!=null)
        {
            BigDecimal value = (BigDecimal)property.getValue();
            if (value!=null) return value.intValue();
        }
        return -1;
    }

    public void setRelativeSize(int size)
    {
        concept.addProperty(new ConceptPropertyNumber(DefaultPropertyID.RELATIVE_SIZE.getId(), size));
    }


    public String getFormula()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.FORMULA.getId());
        if (property==null) return null;
        return (String) property.getValue();
    }

    public void setFormula(String formula)
    {
        concept.addProperty(new ConceptPropertyString(DefaultPropertyID.FORMULA.getId(), formula));
    }

    public FieldTypeSettings getFieldType()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.FIELD_TYPE.getId());
        if (property==null || property.getValue()==null) return FieldTypeSettings.OTHER;
        return (FieldTypeSettings) property.getValue();
    }

    /**
     * @param columnType the column type to set
     */
    public void setFieldType(FieldTypeSettings fieldType)
    {
        concept.addProperty(new ConceptPropertyFieldType(DefaultPropertyID.FIELD_TYPE.getId(), fieldType));
    }

    public String getFieldTypeDesc()
    {
        return getFieldType().getDescription();
    }

    public void setAggregationType(AggregationSettings aggregationType)
    {
        concept.addProperty(new ConceptPropertyAggregation(DefaultPropertyID.AGGREGATION.getId(), aggregationType));
    }

    public AggregationSettings getAggregationType()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.AGGREGATION.getId());
        if (property==null || property.getValue()==null) return AggregationSettings.NONE;
        return (AggregationSettings) property.getValue();
    }

    public String getAggregationTypeDesc()
    {
        return getAggregationType().getDescription();
    }

    public void setHidden(boolean hidden)
    {
        concept.addProperty(new ConceptPropertyBoolean(DefaultPropertyID.HIDDEN.getId(), hidden));
    }

    public void flipHidden()
    {
        setHidden(!isHidden());
    }

    public boolean isHidden()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.HIDDEN.getId());
        if (property==null || property.getValue()==null) return false;
        return ((Boolean)property.getValue()).booleanValue();
    }

    public boolean isExact()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.EXACT.getId());
        if (property==null || property.getValue()==null) return false;
        return ((Boolean)property.getValue()).booleanValue();
    }

    public void setExact(boolean exact)
    {
        concept.addProperty(new ConceptPropertyBoolean(DefaultPropertyID.EXACT.getId(), exact));
    }

    public void flipExact()
    {
        setExact(!isExact());
    }

    public boolean isFactField()
    {
        return getFieldType().isFact();
    }

    public boolean isDimensionField()
    {
        return getFieldType().isDimension();
    }

    public boolean isAttributeField()
    {
        return getFieldType().isDimension();
    }

    public boolean hasAggregate()
    {
        return !getAggregationType().equals(AggregationSettings.NONE);
    }


    public DataTypeSettings getDataType()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.DATA_TYPE.getId());
        if (property==null || property.getValue()==null) return DataTypeSettings.UNKNOWN;
        return (DataTypeSettings) property.getValue();
    }

    public void setDataType(DataTypeSettings dataType)
    {
        concept.addProperty(new ConceptPropertyDataType(DefaultPropertyID.DATA_TYPE.getId(), dataType));
    }

    public Security getSecurity()
    {
        ConceptPropertyInterface property = concept.getProperty(DefaultPropertyID.SECURITY.getId());
        if (property==null || property.getValue()==null) return new Security();
        return (Security) property.getValue();
    }

    public void setSecurity(Security security)
    {
        concept.addProperty(new ConceptPropertySecurity(DefaultPropertyID.SECURITY.getId(), security));
    }

    public void addIDChangedListener(IDChangedListener listener)
    {
        idChangedListeners.add(listener);
    }

    public static IDChangedListener createIDChangedListener(final UniqueList uniqueList)
    {
        return new IDChangedListener()
        {
            public void IDChanged(IDChangedEvent event) throws ObjectAlreadyExistsException
            {
                if (event.newID==null && event.oldID==null) return;
                if (event.newID==null) return;

                if (event.newID.equals(event.oldID)) return; // no problem

                // The ID has changed
                // See if the new ID conflicts with one in the list...
                //
                if (event.object instanceof ConceptUtilityBase)
                {
                    // Look for the new ID
                    for (int i=0;i<uniqueList.size();i++)
                    {
                        ConceptUtilityBase base = (ConceptUtilityBase) uniqueList.get(i);
                        if (base.getId().equals(event.newID))
                        {
                            // This is a problem...
                            throw new ObjectAlreadyExistsException(Messages.getString("ConceptUtilityBase.ERROR_0001_OBJECT_ID_EXISTS", event.newID)); //$NON-NLS-1$
                        }
                    }
                }
            }
        };
    }
}
