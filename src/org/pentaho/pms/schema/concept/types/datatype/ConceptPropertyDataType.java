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

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class ConceptPropertyDataType extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable
{
    public static final ConceptPropertyDataType UNKNOWN = new ConceptPropertyDataType("datatype", DataTypeSettings.UNKNOWN);
    public static final ConceptPropertyDataType STRING  = new ConceptPropertyDataType("datatype", DataTypeSettings.STRING);
    public static final ConceptPropertyDataType DATE    = new ConceptPropertyDataType("datatype", DataTypeSettings.DATE);
    public static final ConceptPropertyDataType BOOLEAN = new ConceptPropertyDataType("datatype", DataTypeSettings.BOOLEAN);
    public static final ConceptPropertyDataType NUMERIC = new ConceptPropertyDataType("datatype", DataTypeSettings.NUMERIC);
    public static final ConceptPropertyDataType BINARY  = new ConceptPropertyDataType("datatype", DataTypeSettings.BINARY);
    public static final ConceptPropertyDataType IMAGE   = new ConceptPropertyDataType("datatype", DataTypeSettings.IMAGE);
    public static final ConceptPropertyDataType URL     = new ConceptPropertyDataType("datatype", DataTypeSettings.URL);
    
    private DataTypeSettings value;
    
    public ConceptPropertyDataType(String name, DataTypeSettings value)
    {
        super(name);
        this.value = value;
    }

    public String toString()
    {
        if (value==null) return null;
        return value.toString();
    }
    
    public Object clone() throws CloneNotSupportedException 
    {
      ConceptPropertyDataType rtn = (ConceptPropertyDataType) super.clone();
      if (value != null) {
        rtn.value = new DataTypeSettings(value.getType());
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.DATATYPE;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (DataTypeSettings) value;
    }
    
    public boolean equals(Object obj)
    {
        return value.equals(obj);
    }

    public int hashCode()
    {
        return value.hashCode();
    }
}
