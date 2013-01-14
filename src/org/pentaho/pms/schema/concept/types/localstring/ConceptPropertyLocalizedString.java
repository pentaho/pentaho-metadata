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
package org.pentaho.pms.schema.concept.types.localstring;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
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
