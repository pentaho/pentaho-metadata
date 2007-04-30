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
package org.pentaho.pms.schema.concept.types.string;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class ConceptPropertyString extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable
{
    private String value;
    private static final String EMPTY_STRING = "";

    public ConceptPropertyString(String name, String value) {
      super(name);
      setValue(value);
    }

    public String toString()
    {
        return value;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.STRING;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value) {
      if (null != value) {
        this.value = (String) value;
      } else {
        this.value = EMPTY_STRING;
      }
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
