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

public class OlapDimensionUsage implements Cloneable
{
    private String name;
    private OlapDimension olapDimension;
    
    public OlapDimensionUsage()
    {
    }
    
    /**
     * @param name
     * @param olapDimension
     */
    public OlapDimensionUsage(String name, OlapDimension olapDimension)
    {
        super();
        this.name = name;
        this.olapDimension = olapDimension;
    }
    
    public Object clone()
    {
        try
        {
            OlapDimensionUsage usage = (OlapDimensionUsage) super.clone(); // shallow copy of the dimension is fine.
            return usage;
        }
        catch(CloneNotSupportedException e)
        {
            return null;
        }
    }
    
    public boolean equals(Object obj)
    {
        return name.equals( ((OlapDimensionUsage)obj).name);
    }
    
    public int hashCode()
    {
        return name.hashCode();
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

    /**
     * @return the olapDimension
     */
    public OlapDimension getOlapDimension()
    {
        return olapDimension;
    }

    /**
     * @param olapDimension the olapDimension to set
     */
    public void setOlapDimension(OlapDimension olapDimension)
    {
        this.olapDimension = olapDimension;
    }

    
    
}
