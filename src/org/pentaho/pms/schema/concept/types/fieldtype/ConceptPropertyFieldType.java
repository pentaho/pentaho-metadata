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
package org.pentaho.pms.schema.concept.types.fieldtype;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class ConceptPropertyFieldType extends ConceptPropertyBase implements Cloneable
{
    public static final ConceptPropertyFieldType DEFAULT_OTHER     = new ConceptPropertyFieldType("fieldtype", FieldTypeSettings.OTHER);    //$NON-NLS-1$
    public static final ConceptPropertyFieldType DEFAULT_DIMENSION = new ConceptPropertyFieldType("fieldtype", FieldTypeSettings.DIMENSION);    //$NON-NLS-1$
    public static final ConceptPropertyFieldType DEFAULT_FACT      = new ConceptPropertyFieldType("fieldtype", FieldTypeSettings.FACT);    //$NON-NLS-1$
    public static final ConceptPropertyFieldType DEFAULT_KEY       = new ConceptPropertyFieldType("fieldtype", FieldTypeSettings.KEY);    //$NON-NLS-1$

    private FieldTypeSettings value;

    public ConceptPropertyFieldType(String name, FieldTypeSettings value)
    {
        this(name, value, false);
    }

    public ConceptPropertyFieldType(String name, FieldTypeSettings value, boolean required)
    {
        super(name, required);
        this.value = value;
    }

    public String toString()
    {
        if (value==null) return null;
        return value.toString();
    }

    public Object clone() throws CloneNotSupportedException
    {
      ConceptPropertyFieldType rtn = (ConceptPropertyFieldType) super.clone();
      if (value != null) {
        rtn.value = new FieldTypeSettings(value.getType());
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.FIELDTYPE;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (FieldTypeSettings) value;
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
