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
package org.pentaho.pms.schema.olap;

import org.pentaho.pms.schema.BusinessColumn;

public class OlapMeasure implements Cloneable
{
    private String name;
    private BusinessColumn businessColumn;
    
    public OlapMeasure()
    {
    }
    
    /**
     * @param name
     * @param businessColumn
     */
    public OlapMeasure(String name, BusinessColumn businessColumn)
    {
        this();
        this.name = name;
        this.businessColumn = businessColumn;
    }

    public Object clone()
    {
        return new OlapMeasure(name, businessColumn); // shallow copy of business column is desired
    }
    
    /**
     * @return the businessColumn
     */
    public BusinessColumn getBusinessColumn()
    {
        return businessColumn;
    }

    /**
     * @param businessColumn the businessColumn to set
     */
    public void setBusinessColumn(BusinessColumn businessColumn)
    {
        this.businessColumn = businessColumn;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

}
