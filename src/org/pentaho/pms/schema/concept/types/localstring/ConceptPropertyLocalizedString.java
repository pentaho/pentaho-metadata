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
package org.pentaho.pms.schema.concept.types.localstring;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;


public class ConceptPropertyLocalizedString extends ConceptPropertyBase implements Cloneable
{
    private LocalizedStringSettings value;

    public ConceptPropertyLocalizedString(String name, LocalizedStringSettings value)
    {
        this(name, value, false);
    }

    public ConceptPropertyLocalizedString(String name, LocalizedStringSettings value, boolean required)
    {
        super(name, required);
        if (null != value) {
          this.value = value;
        } else {
          this.value = new LocalizedStringSettings();
        }
    }

    public String toString()
    {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
      append(getId()).append(isRequired()).append(value).
      toString();
    }

    public Object clone() throws CloneNotSupportedException
    {
        ConceptPropertyLocalizedString locString = (ConceptPropertyLocalizedString) super.clone();
        if (value!=null) locString.value = (LocalizedStringSettings) value.clone();
        return locString;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.LOCALIZED_STRING;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (LocalizedStringSettings) value;
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
