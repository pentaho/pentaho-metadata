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
package org.pentaho.pms.schema.concept.types.alignment;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class ConceptPropertyAlignment extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable
{
    public static final ConceptPropertyAlignment LEFT      = new ConceptPropertyAlignment("aggregation", AlignmentSettings.LEFT);
    public static final ConceptPropertyAlignment RIGHT     = new ConceptPropertyAlignment("aggregation", AlignmentSettings.RIGHT);
    public static final ConceptPropertyAlignment CENTERED  = new ConceptPropertyAlignment("aggregation", AlignmentSettings.CENTERED);
    public static final ConceptPropertyAlignment JUSTIFIED = new ConceptPropertyAlignment("aggregation", AlignmentSettings.JUSTIFIED);
    
    private AlignmentSettings value;
    
    public ConceptPropertyAlignment(String name, AlignmentSettings value)
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
      ConceptPropertyAlignment rtn = (ConceptPropertyAlignment)super.clone();
      if (value != null) {
        rtn.value = new AlignmentSettings(value.getType());
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.ALIGNMENT;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (AlignmentSettings) value;
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
