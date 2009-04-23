/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
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
package org.pentaho.metadata.model.concept.types;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.pms.messages.Messages;

public class DataType
{
    public static final int DATA_TYPE_UNKNOWN   = 0;
    public static final int DATA_TYPE_STRING    = 1;
    public static final int DATA_TYPE_DATE      = 2;
    public static final int DATA_TYPE_BOOLEAN   = 3;
    public static final int DATA_TYPE_NUMERIC   = 4;
    public static final int DATA_TYPE_BINARY    = 5;
    public static final int DATA_TYPE_IMAGE     = 6;
    public static final int DATA_TYPE_URL       = 7;

    public static final DataType UNKNOWN = new DataType(DATA_TYPE_UNKNOWN);
    public static final DataType STRING  = new DataType(DATA_TYPE_STRING);
    public static final DataType DATE    = new DataType(DATA_TYPE_DATE);
    public static final DataType BOOLEAN = new DataType(DATA_TYPE_BOOLEAN);
    public static final DataType NUMERIC = new DataType(DATA_TYPE_NUMERIC);
    public static final DataType BINARY  = new DataType(DATA_TYPE_BINARY);
    public static final DataType IMAGE   = new DataType(DATA_TYPE_IMAGE);
    public static final DataType URL     = new DataType(DATA_TYPE_URL);

    private static final String typeCodes[] = { "Unknown", "String", "Date", "Boolean", "Numeric", "Binary", "Image", "URL", }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    private static final String typeDescriptions[] = {
      Messages.getString("DataType.USER_UNKNOWN_DESC"),   //$NON-NLS-1$
      Messages.getString("DataType.USER_STRING_DESC"),   //$NON-NLS-1$
      Messages.getString("DataType.USER_DATE_DESC"),   //$NON-NLS-1$
      Messages.getString("DataType.USER_BOOLEAN_DESC"),  //$NON-NLS-1$
      Messages.getString("DataType.USER_NUMERIC_DESC"),   //$NON-NLS-1$
      Messages.getString("DataType.USER_BINARY_DESC"),   //$NON-NLS-1$
      Messages.getString("DataType.USER_IMAGE_DESC"),   //$NON-NLS-1$
      Messages.getString("DataType.USER_URL_DESC"), }; //$NON-NLS-1$
    public static final DataType[] types = new DataType[] { UNKNOWN, STRING, DATE, BOOLEAN, NUMERIC, BINARY, IMAGE, URL, };

    private static final String SEPARATOR = ","; //$NON-NLS-1$

    private int type;
    private int length;
    private int precision;

    /**
     * @param type
     * @param length
     * @param precision
     */
    public DataType(int type, int length, int precision)
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
    public DataType(int type)
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

    public static DataType fromString(String value)
    {
        String pieces[] = value.split(SEPARATOR);
        if (pieces.length>0)
        {
            DataType settings = getType(pieces[0]);
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

    public static DataType getType(String description)
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

    public boolean equals(Object obj) {
      if (obj instanceof DataType == false) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      DataType rhs = (DataType) obj;
      return new EqualsBuilder().append(type, rhs.type).append(length, rhs.length).append(precision, rhs.precision)
      .isEquals();
    }

    public int hashCode() {
      return new HashCodeBuilder(23, 227).append(type).append(length).append(precision).toHashCode();
    }

    public static String[] getTypeDescriptions() {
      return typeDescriptions.clone();
    }

}
