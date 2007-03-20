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
import java.util.Arrays;
import java.util.List;

import org.pentaho.pms.schema.BusinessTable;

import be.ibridge.kettle.core.ChangedFlag;

public class OlapCube extends ChangedFlag implements Cloneable
{
    private String name;
    private BusinessTable businessTable;
    
    private List olapDimensionUsages;
    private List olapMeasures;
    
    // TODO: private dimensions
    
    public OlapCube()
    {
        olapDimensionUsages = new ArrayList();
        olapMeasures = new ArrayList();
    }
    
    public Object clone()
    {
        OlapCube olapCube = new OlapCube();
        
        olapCube.name = name;
        for (int i=0;i<olapDimensionUsages.size();i++)
        {
            OlapDimensionUsage usage = (OlapDimensionUsage) olapDimensionUsages.get(i);
            olapCube.olapDimensionUsages.add(usage.clone());
        }
        
        for (int i=0;i<olapMeasures.size();i++)
        {
            OlapMeasure measure = (OlapMeasure)olapMeasures.get(i);
            olapCube.olapMeasures.add( measure.clone() );
        }
        
        if (businessTable!=null) olapCube.businessTable = businessTable; // no cloning here please!
        
        return olapCube;
    }
    
    public boolean equals(Object obj)
    {
        return name.equals(((OlapCube)obj).getName());
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
     * @return the businessTable
     */
    public BusinessTable getBusinessTable()
    {
        return businessTable;
    }

    /**
     * @param businessTable the businessTable to set
     */
    public void setBusinessTable(BusinessTable businessTable)
    {
        this.businessTable = businessTable;
    }

    /**
     * @return the olapDimensionUsages
     */
    public List getOlapDimensionUsages()
    {
        return olapDimensionUsages;
    }

    /**
     * @param olapDimensionUsages the olapDimensionUsages to set
     */
    public void setOlapDimensionUsages(List olapDimensionUsages)
    {
        this.olapDimensionUsages = olapDimensionUsages;
    }

    /**
     * @return the olapMeasures
     */
    public List getOlapMeasures()
    {
        return olapMeasures;
    }

    /**
     * @param olapMeasures the olapMeasures to set
     */
    public void setOlapMeasures(List olapMeasures)
    {
        this.olapMeasures = olapMeasures;
    }

    public OlapMeasure findOlapMeasure(String measureName)
    {
        for (int i=0;i<olapMeasures.size();i++)
        {
            OlapMeasure olapMeasure = (OlapMeasure) olapMeasures.get(i);
            if (olapMeasure.getName().equals(measureName)) return olapMeasure;
        }
        return null;
    }
    
    public String[] getUnusedColumnNames(String locale)
    {
        String[] allColumnNames = businessTable.getColumnNames(locale);
        List names = new ArrayList();
        names.addAll(Arrays.asList(allColumnNames));
        
        for (int i=names.size()-1;i>=0;i--)
        {
            String name = (String) names.get(i);
            for (int m=0;m<olapMeasures.size();m++)
            {
                OlapMeasure measure = (OlapMeasure)olapMeasures.get(m);
                if (measure.getBusinessColumn().getDisplayName(locale).equals(name)) names.remove(i);
            }
        }
        
        return (String[]) names.toArray(new String[names.size()]);
    }
}
