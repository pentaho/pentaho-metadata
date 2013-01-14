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
package org.pentaho.pms.schema.concept.types;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;

/**
 * Base class for all concept properties.
 *
 * @author Matt
 @deprecated as of metadata 3.0.
 */
public abstract class ConceptPropertyBase implements ConceptPropertyInterface, Cloneable
{
    private String id;
    private boolean required;

    /**
     * @param id
     */
    public ConceptPropertyBase(String id)
    {
      this(id, false);
    }

    public ConceptPropertyBase(String id, boolean required) {
      super();
      this.id = id;
      this.required = required;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isRequired() {
      return required;
    }

    public void setRequired(final boolean required) {
      this.required = required;
    }

    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
}