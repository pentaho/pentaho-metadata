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
package org.pentaho.pms.schema.concept.types.bool;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class ConceptPropertyBoolean extends ConceptPropertyBase implements Cloneable
{
    private Boolean value;

    public ConceptPropertyBoolean(String name, Boolean value)
    {
        this(name, null != value ? value.booleanValue() : false);
    }

    public ConceptPropertyBoolean(String name, boolean value)
    {
        this(name, value, false);
    }

    public ConceptPropertyBoolean(String name, boolean value, boolean required)
    {
        super(name, required);
        this.value = new Boolean(value);
    }


    public String toString()
    {
        if (value==null) return null;
        return value.booleanValue()?"Y":"N"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public Object clone() throws CloneNotSupportedException
    {
      ConceptPropertyBoolean rtn = (ConceptPropertyBoolean)super.clone();
      if (value != null) {
        rtn.value = new Boolean(value.booleanValue());
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.BOOLEAN;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (Boolean) value;
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
