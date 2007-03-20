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
package org.pentaho.pms.schema.concept.types.datatype;

public class DataTypeSettings
{
    public static final int DATA_TYPE_UNKNOWN   = 0;
    public static final int DATA_TYPE_STRING    = 1;
    public static final int DATA_TYPE_DATE      = 2;
    public static final int DATA_TYPE_BOOLEAN   = 3;
    public static final int DATA_TYPE_NUMERIC   = 4;
    public static final int DATA_TYPE_BINARY    = 5;
    public static final int DATA_TYPE_IMAGE     = 6;
    public static final int DATA_TYPE_URL       = 7;
    
    public static final DataTypeSettings UNKNOWN = new DataTypeSettings(DATA_TYPE_UNKNOWN);
    public static final DataTypeSettings STRING  = new DataTypeSettings(DATA_TYPE_STRING);
    public static final DataTypeSettings DATE    = new DataTypeSettings(DATA_TYPE_DATE);
    public static final DataTypeSettings BOOLEAN = new DataTypeSettings(DATA_TYPE_BOOLEAN);
    public static final DataTypeSettings NUMERIC = new DataTypeSettings(DATA_TYPE_NUMERIC);
    public static final DataTypeSettings BINARY  = new DataTypeSettings(DATA_TYPE_BINARY);
    public static final DataTypeSettings IMAGE   = new DataTypeSettings(DATA_TYPE_IMAGE);
    public static final DataTypeSettings URL     = new DataTypeSettings(DATA_TYPE_URL);
    
    public static final String typeCodes[] = { "Unknown", "String", "Date", "Boolean", "Numeric", "Binary", "Image", "URL", };
    public static final String typeDescriptions[] = { "Unknown", "String", "Date", "Boolean", "Numeric", "Binary", "Image", "URL", };
    public static final DataTypeSettings[] types = new DataTypeSettings[] { UNKNOWN, STRING, DATE, BOOLEAN, NUMERIC, BINARY, IMAGE, URL, };
    
    private static final String SEPARATOR = ",";
    
    private int type;
    private int length;
    private int precision;
    
    /**
     * @param type
     * @param length
     * @param precision
     */
    public DataTypeSettings(int type, int length, int precision)
    {
        super();
        this.type = type;
        this.length = length;
        this.precision = precision;
    }
    
    /**
     * @param type
     * @param length
     * @param precision
     */
    public DataTypeSettings(int type)
    {
        super();
        this.type = type;
        this.length = -1;
        this.precision = -1;
    }
    
    public String toString()
    {
        return getCode()+SEPARATOR+length+SEPARATOR+precision;
    }
    
    public static DataTypeSettings fromString(String value)
    {
        String pieces[] = value.split(SEPARATOR);
        if (pieces.length>0)
        {
            DataTypeSettings settings = getType(pieces[0]);
            if (pieces.length>1)
            {
                settings.setLength(Integer.parseInt(pieces[1]));
                if (pieces.length>2)
                {
                    settings.setPrecision(Integer.parseInt(pieces[2]));
                }
            }
            
            return settings;
        }
        return null;
    }


    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public int getPrecision()
    {
        return precision;
    }

    public void setPrecision(int precision)
    {
        this.precision = precision;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
    
    public String getCode()
    {
        return typeCodes[type];
    }
    
    public String getDescription()
    {
        return typeDescriptions[type];
    }
    
    public static DataTypeSettings getType(String description)
    {
        for (int i=0;i<typeDescriptions.length;i++)
        {
            if (typeDescriptions[i].equalsIgnoreCase(description))
            {
                return types[i];
            }
        }
        for (int i=0;i<typeCodes.length;i++)
        {
            if (typeCodes[i].equalsIgnoreCase(description))
            {
                return types[i];
            }
        }
        return UNKNOWN;
    }

}
