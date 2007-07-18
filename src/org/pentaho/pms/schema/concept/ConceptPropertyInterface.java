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
package org.pentaho.pms.schema.concept;

import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * The interface that describes a concept property
 *
 * @author Matt
 */
public interface ConceptPropertyInterface extends Cloneable
{
    /** @return the concept property type */
    public ConceptPropertyType getType();

    /** @return a string representation for this concept property */
    public String toString();

    /** @return get the id of the property */
    public String getId();

    /** @param id the property id to set */
    public void setId(String id);

    /** @return the value of this property */
    public Object getValue();

    /** @param value the value of this property to set */
    public void setValue(Object value);

    public boolean equals(Object obj);
    public int hashCode();
    public Object clone() throws CloneNotSupportedException;

//    /**
//     * @return a copy of the concept property
//     * @throws CloneNotSupportedException
//     */
//    public Object clone() throws CloneNotSupportedException;

    /**
     * Returns <code>true</code> if this property is a default property for this concept, otherwise <code>false</code>.
     * A property's default status varies from subject to subject.
     */
    public boolean isRequired();
    public void setRequired(boolean required);
}
