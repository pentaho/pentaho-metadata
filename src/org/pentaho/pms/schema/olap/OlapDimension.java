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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.pms.schema.BusinessTable;

@SuppressWarnings("deprecation")
public class OlapDimension extends ChangedFlag implements Cloneable
{
    private String name;
    private boolean timeDimension;
    
    private List<OlapHierarchy> hierarchies;
    
    public OlapDimension()
    {
        hierarchies = new ArrayList<OlapHierarchy>();
    }
    
    public Object clone()
    {
        OlapDimension olapDimension = new OlapDimension();
        
        olapDimension.name = name;
        olapDimension.timeDimension = timeDimension;
        for (int i=0;i<hierarchies.size();i++)
        {
            OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get(i);
            olapDimension.hierarchies.add((OlapHierarchy)hierarchy.clone());
        }
        
        return olapDimension;
    }
    
    public boolean equals(Object obj)
    {
        return name.equals(((OlapDimension)obj).getName());
    }

    /**
     * @return the hierarchies
     */
    public List<OlapHierarchy> getHierarchies()
    {
        return hierarchies;
    }

    /**
     * @param hierarchies the hierarchies to set
     */
    public void setHierarchies(List<OlapHierarchy> hierarchies)
    {
        this.hierarchies = hierarchies;
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
     * @return the timeDimension
     */
    public boolean isTimeDimension()
    {
        return timeDimension;
    }

    /**
     * @param timeDimension the timeDimension to set
     */
    public void setTimeDimension(boolean timeDimension)
    {
        this.timeDimension = timeDimension;
    }

    public OlapHierarchy findOlapHierarchy(String thisName)
    {
        for (int i=0;i<hierarchies.size();i++)
        {
            OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get(i);
            if (hierarchy.getName().equalsIgnoreCase(thisName)) return hierarchy;
        }
        return null;
    }

    /**
     * @return the businessTable
     */
    public BusinessTable findBusinessTable()
    {
        for (int i=0;i<hierarchies.size();i++)
        {
            OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get(i);
            if (hierarchy.getBusinessTable()!=null) return hierarchy.getBusinessTable();
        }
        return null;
    }
    
    public boolean hasChanged()
    {
        for (int i=0;i<hierarchies.size();i++)
        {
            if (((OlapHierarchy)hierarchies.get(i)).hasChanged()) return true;
        }
        return super.hasChanged();
    }
    
    public void clearChanged()
    {
        for (int i=0;i<hierarchies.size();i++)
        {
            ((OlapHierarchy)hierarchies.get(i)).clearChanged();
        }
        setChanged(false);
    }
}
