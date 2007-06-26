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
package org.pentaho.pms.schema.concept.types;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;

/**
 * Base class for all concept properties.
 *
 * @author Matt
 *
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