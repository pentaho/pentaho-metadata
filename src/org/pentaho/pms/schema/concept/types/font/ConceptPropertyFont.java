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
package org.pentaho.pms.schema.concept.types.font;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class ConceptPropertyFont extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable
{
    private FontSettings value;

    public ConceptPropertyFont(String name, FontSettings value)
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
      ConceptPropertyFont rtn = (ConceptPropertyFont) super.clone();
      if (value != null) {
        rtn.value = new FontSettings(value.getName(), value.getHeight(), value.isBold(), value.isItalic());
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.FONT;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (FontSettings) value;
    }

    public boolean equals(Object obj) {
      if (obj instanceof ConceptPropertyFont == false) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      ConceptPropertyFont rhs = (ConceptPropertyFont) obj;
      return new EqualsBuilder().append(value, rhs.value).isEquals();
    }

    public int hashCode() {
      return new HashCodeBuilder(47, 71).append(value).toHashCode();
    }
}
