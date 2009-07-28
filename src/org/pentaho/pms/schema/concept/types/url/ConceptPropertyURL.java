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
package org.pentaho.pms.schema.concept.types.url;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyURL extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable
{
    private URL value;

    public ConceptPropertyURL(String name, URL value)
    {
        this(name, value, false);
    }

    public ConceptPropertyURL(String name, URL value, boolean required)
    {
        super(name, required);
        this.value = value;
    }

    public Object clone() throws CloneNotSupportedException
    {
      ConceptPropertyURL rtn = (ConceptPropertyURL) super.clone();
      if (value != null) {
        try {
          rtn.value = new URL(value.toString());
        } catch (MalformedURLException ignored) {}
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.URL;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (URL) value;
    }


    public boolean equals(Object obj)
    {
      if (obj instanceof ConceptPropertyURL == false) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      ConceptPropertyURL rhs = (ConceptPropertyURL) obj;
      return new EqualsBuilder().append(value, rhs.value).isEquals();
    }

    public int hashCode()
    {
      return new HashCodeBuilder(79, 223).append(value).toHashCode();
    }

    public String toString()
    {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
        append(value).
        toString();
  }

}
