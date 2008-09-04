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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregation;
import org.pentaho.pms.schema.concept.types.alignment.AlignmentSettings;
import org.pentaho.pms.schema.concept.types.alignment.ConceptPropertyAlignment;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.color.ColorSettings;
import org.pentaho.pms.schema.concept.types.color.ConceptPropertyColor;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.date.ConceptPropertyDate;
import org.pentaho.pms.schema.concept.types.fieldtype.ConceptPropertyFieldType;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumber;
import org.pentaho.pms.schema.concept.types.rowlevelsecurity.ConceptPropertyRowLevelSecurity;
import org.pentaho.pms.schema.concept.types.security.ConceptPropertySecurity;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.concept.types.tabletype.ConceptPropertyTableType;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.concept.types.url.ConceptPropertyURL;
import org.pentaho.pms.schema.security.RowLevelSecurity;
import org.pentaho.pms.schema.security.Security;

public class DefaultPropertyID
{
    private static final Log logger = LogFactory.getLog(DefaultPropertyID.class);
    private ConceptPropertyType type;
    private String id;
    private String description;
    private ConceptPropertyInterface defaultValue;

    public static final DefaultPropertyID defaults[] = new DefaultPropertyID[]
         {
            new DefaultPropertyID("name",              Messages.getString("DefaultPropertyID.USER_NAME_DESC"), ConceptPropertyType.LOCALIZED_STRING, new ConceptPropertyLocalizedString("name", new LocalizedStringSettings())), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("description",       Messages.getString("DefaultPropertyID.USER_DESCRIPTION_DESC"), ConceptPropertyType.LOCALIZED_STRING, new ConceptPropertyLocalizedString("description", new LocalizedStringSettings())), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("comments",          Messages.getString("DefaultPropertyID.USER_COMMENTS_DESC"), ConceptPropertyType.LOCALIZED_STRING, new ConceptPropertyLocalizedString("comments", new LocalizedStringSettings())), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("aggregation",       Messages.getString("DefaultPropertyID.USER_AGGREGATION_RULE_DESC"), ConceptPropertyType.AGGREGATION, new ConceptPropertyAggregation("aggregation", AggregationSettings.SUM)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("formula",           Messages.getString("DefaultPropertyID.USER_FORMULA_DESC"), ConceptPropertyType.STRING, new ConceptPropertyString("formula", null)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("hidden",            Messages.getString("DefaultPropertyID.USER_HIDDEN_DESC"), ConceptPropertyType.BOOLEAN, new ConceptPropertyBoolean("hidden", Boolean.FALSE)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("exact",             Messages.getString("DefaultPropertyID.USER_FORMULA_EXACT"), ConceptPropertyType.BOOLEAN, new ConceptPropertyBoolean("exact", Boolean.FALSE)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("datatype",          Messages.getString("DefaultPropertyID.USER_DATA_TYPE_DESC"), ConceptPropertyType.DATATYPE, new ConceptPropertyDataType("datatype", DataTypeSettings.STRING)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("mask",              Messages.getString("DefaultPropertyID.USER_MASK_DESC"), ConceptPropertyType.STRING, new ConceptPropertyString("mask", null)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("foreground_color",  Messages.getString("DefaultPropertyID.USER_FOREGROUND_COLOR_DESC"), ConceptPropertyType.COLOR, new ConceptPropertyColor("foreground_color", ColorSettings.BLACK)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("background_color",  Messages.getString("DefaultPropertyID.USER_BACKGROUND_COLOR_DESC"), ConceptPropertyType.COLOR, new ConceptPropertyColor("background_color", ColorSettings.WHITE)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("font",              Messages.getString("DefaultPropertyID.USER_FONT_DESC"), ConceptPropertyType.FONT, new ConceptPropertyFont("font", null)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("fieldtype",         Messages.getString("DefaultPropertyID.USER_FIELD_TYPE_DESC"), ConceptPropertyType.FIELDTYPE, new ConceptPropertyFieldType("fieldtype", FieldTypeSettings.DIMENSION)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("tabletype",         Messages.getString("DefaultPropertyID.USER_TABLE_TYPE_DESC"), ConceptPropertyType.TABLETYPE, new ConceptPropertyTableType("tabletype", TableTypeSettings.OTHER)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("relative_size",     Messages.getString("DefaultPropertyID.USER_RELATIVE_SIZE_DESC"), ConceptPropertyType.NUMBER, new ConceptPropertyNumber("relative_size", null)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("target_table",      Messages.getString("DefaultPropertyID.USER_TARGET_TABLE_DESC"), ConceptPropertyType.STRING, new ConceptPropertyString("target_table", null)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("target_schema",      Messages.getString("DefaultPropertyID.USER_TARGET_SCHEMA_DESC"), ConceptPropertyType.STRING, new ConceptPropertyString("target_schema", null)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("alignment",         Messages.getString("DefaultPropertyID.USER_TEXT_ALIGNMENT_DESC"), ConceptPropertyType.ALIGNMENT, new ConceptPropertyAlignment("alignment", AlignmentSettings.LEFT)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("column_width",      Messages.getString("DefaultPropertyID.USER_COLUMN_WIDTH_DESC"), ConceptPropertyType.COLUMN_WIDTH, new ConceptPropertyColumnWidth("column_width", ColumnWidth.PIXELS)), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("security",          Messages.getString("DefaultPropertyID.USER_SECURITY_INFORMATION_DESC"), ConceptPropertyType.SECURITY, new ConceptPropertySecurity("security", new Security())), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new DefaultPropertyID("row_level_security",Messages.getString("DefaultPropertyID.USER_ROW_LEVEL_SECURITY_DESC"), ConceptPropertyType.ROW_LEVEL_SECURITY, new ConceptPropertyRowLevelSecurity("row_level_security", new RowLevelSecurity())), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         };

    public static final DefaultPropertyID NAME            = defaults[ 0];
    public static final DefaultPropertyID DESCRIPTION     = defaults[ 1];
    public static final DefaultPropertyID COMMENTS        = defaults[ 2];
    public static final DefaultPropertyID AGGREGATION     = defaults[ 3];
    public static final DefaultPropertyID FORMULA         = defaults[ 4];
    public static final DefaultPropertyID HIDDEN          = defaults[ 5];
    public static final DefaultPropertyID EXACT           = defaults[ 6];
    public static final DefaultPropertyID DATA_TYPE       = defaults[ 7];
    public static final DefaultPropertyID MASK            = defaults[ 8];
    public static final DefaultPropertyID COLOR_FG        = defaults[ 9];
    public static final DefaultPropertyID COLOR_BG        = defaults[10];
    public static final DefaultPropertyID FONT            = defaults[11];
    public static final DefaultPropertyID FIELD_TYPE      = defaults[12];
    public static final DefaultPropertyID TABLE_TYPE      = defaults[13];
    public static final DefaultPropertyID RELATIVE_SIZE   = defaults[14];
    public static final DefaultPropertyID TARGET_TABLE    = defaults[15];
    public static final DefaultPropertyID TARGET_SCHEMA   = defaults[16];
    public static final DefaultPropertyID ALIGNMENT       = defaults[17];
    public static final DefaultPropertyID COLUMN_WIDTH    = defaults[18];
    public static final DefaultPropertyID SECURITY        = defaults[19];
    public static final DefaultPropertyID ROW_LEVEL_SECURITY        = defaults[20];


    /**
     * @param type The concept property type
     * @param id The possible id to use
     */
    public DefaultPropertyID(String id, String description, ConceptPropertyType type, ConceptPropertyInterface defaultValue)
    {
        this.id = id;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
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
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the type
     */
    public ConceptPropertyType getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ConceptPropertyType type)
    {
        this.type = type;
    }

    /**
     * @return the defaultValue
     */
    public ConceptPropertyInterface getDefaultValue()
    {
        try
        {
          return (ConceptPropertyInterface) defaultValue.clone();
        }
        catch (CloneNotSupportedException e)
        {
          logger.error("an exception occurred", e);
          return null;
        }
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(ConceptPropertyInterface defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return an array containing the default proposed (recognized) property id's
     */
    public static final String[] getDefaultPropertyIDs()
    {
        String[] ids = new String[defaults.length];
        for (int i=0;i<ids.length;i++)
        {
            ids[i] = defaults[i].getId();
        }
        return ids;
    }

    /**
     * @return an array containing the default proposed (recognized) property types corresponding with the ID's array
     */
    public static final ConceptPropertyType[] getDefaultPropertyTypes()
    {
        ConceptPropertyType[] types = new ConceptPropertyType[defaults.length];
        for (int i=0;i<types.length;i++)
        {
            types[i] = defaults[i].getType();
        }
        return types;
    }

    /**
     * Create a new
     * @param conceptPropertyType the property type to generate a default for.
     * @return
     */
    public static final ConceptPropertyInterface getDefaultEmptyProperty(ConceptPropertyType conceptPropertyType, String name)
    {
        ConceptPropertyInterface property = null;
        switch(conceptPropertyType.getType())
        {
        case ConceptPropertyType.PROPERTY_TYPE_STRING           : property = new ConceptPropertyString(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_DATE             : property = new ConceptPropertyDate(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_NUMBER           : property = new ConceptPropertyNumber(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_COLOR            : property = new ConceptPropertyColor(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_FONT             : property = new ConceptPropertyFont(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_FIELDTYPE        : property = new ConceptPropertyFieldType(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_AGGREGATION      : property = new ConceptPropertyAggregation(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_BOOLEAN          : property = new ConceptPropertyBoolean(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_DATATYPE         : property = new ConceptPropertyDataType(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_LOCALIZED_STRING : property = new ConceptPropertyLocalizedString(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_TABLETYPE        : property = new ConceptPropertyTableType(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_URL              : property = new ConceptPropertyURL(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_SECURITY         : property = new ConceptPropertySecurity(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_ALIGNMENT        : property = new ConceptPropertyAlignment(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_COLUMN_WIDTH     : property = new ConceptPropertyColumnWidth(name, null); break;
        case ConceptPropertyType.PROPERTY_TYPE_ROW_LEVEL_SECURITY     : property = new ConceptPropertyRowLevelSecurity(name, null); break;
        }
        return property;
    }

    /**
     * @return the defined default IDs, types, default values, etc.
     */
    public static DefaultPropertyID[] getDefaults()
    {
        return defaults;
    }

    public static DefaultPropertyID findDefaultPropertyID(String id)
    {
        for (int i=0;i<defaults.length;i++)
        {
            if (defaults[i].getId().equals(id)) return defaults[i];
        }
        return null;
    }

    public static int findDefaultPropertyOrder(String id)
    {
        for (int i=0;i<defaults.length;i++)
        {
            if (defaults[i].getId().equals(id)) return i;
        }
        return defaults.length; // put non-default properties always at the back
    }
}
