/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
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
