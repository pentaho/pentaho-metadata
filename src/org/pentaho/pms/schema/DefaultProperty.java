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
package org.pentaho.pms.schema;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

@SuppressWarnings("deprecation")
public class DefaultProperty
{
    private Class subject;
    private String name;
    private String description;
    private ConceptPropertyType conceptPropertyType;
    private ConceptPropertyInterface defaultValue;
    
    /**
     * @param subject
     * @param name
     * @param description
     * @param conceptPropertyType
     */
    public DefaultProperty(Class subject, String name, String description, ConceptPropertyType conceptPropertyType, ConceptPropertyInterface defaultValue)
    {
        super();
        this.subject = subject;
        this.name = name;
        this.description = description;
        this.conceptPropertyType = conceptPropertyType;
        this.defaultValue = defaultValue;
    }
    
    /**
     * @param subject
     * @param name
     * @param conceptPropertyType
     */
    public DefaultProperty(Class subject, DefaultPropertyID defaultPropertyID)
    {
        super();
        this.subject = subject;
        this.name = defaultPropertyID.getId();
        this.description = defaultPropertyID.getDescription();
        this.conceptPropertyType = defaultPropertyID.getType();
        this.defaultValue = defaultPropertyID.getDefaultValue();
    }

    public ConceptPropertyType getConceptPropertyType()
    {
        return conceptPropertyType;
    }
    
    public void setConceptPropertyType(ConceptPropertyType conceptPropertyType)
    {
        this.conceptPropertyType = conceptPropertyType;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Class getSubject()
    {
        return subject;
    }
    
    public void setSubject(Class subject)
    {
        this.subject = subject;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the defaultValue
     */
    public ConceptPropertyInterface getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(ConceptPropertyInterface defaultValue)
    {
        this.defaultValue = defaultValue;
    }
    
    public String toString()
    {
        return name+":"+description+":"+conceptPropertyType.getDescription()+":"+defaultValue.toString();  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
    }
}
