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
package org.pentaho.pms.schema.concept.types.aggregation;

public class AggregationSettings
{
    public static final int TYPE_AGGREGATION_NONE           = 0;
    public static final int TYPE_AGGREGATION_SUM            = 1;
    public static final int TYPE_AGGREGATION_AVERAGE        = 2;
    public static final int TYPE_AGGREGATION_COUNT          = 3;
    public static final int TYPE_AGGREGATION_COUNT_DISTINCT = 4;
    public static final int TYPE_AGGREGATION_MINIMUM        = 5;
    public static final int TYPE_AGGREGATION_MAXIMUM        = 6;
    
    public static final AggregationSettings NONE           = new AggregationSettings(TYPE_AGGREGATION_NONE);
    public static final AggregationSettings SUM            = new AggregationSettings(TYPE_AGGREGATION_SUM);
    public static final AggregationSettings AVERAGE        = new AggregationSettings(TYPE_AGGREGATION_AVERAGE);
    public static final AggregationSettings COUNT          = new AggregationSettings(TYPE_AGGREGATION_COUNT);
    public static final AggregationSettings COUNT_DISTINCT = new AggregationSettings(TYPE_AGGREGATION_COUNT_DISTINCT);
    public static final AggregationSettings MINIMUM        = new AggregationSettings(TYPE_AGGREGATION_MINIMUM);
    public static final AggregationSettings MAXIMUM        = new AggregationSettings(TYPE_AGGREGATION_MAXIMUM);
    
    public static final String typeCodes[] = 
        { 
            "none", "sum", "average", "count", "count_distinct", "minimum", "maximum",   
        };
    
    public static final String typeDescriptions[] = 
        { 
            "None", "Sum", "Average", "Count", "Distinct count", "Minimum", "Maximum",   
        };
    
    public static final AggregationSettings[] types = new AggregationSettings[]
        {
            NONE, SUM, AVERAGE, COUNT, MINIMUM, MAXIMUM,  
        };

    private int type;

    /**
     * @param name
     * @param type
     */
    public AggregationSettings(int type)
    {
        this.type = type;
    }

    /**
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type)
    {
        this.type = type;
    }
    
    public boolean equals(Object obj)
    {
        return type == ((AggregationSettings)obj).getType();
    }
    
    public int hashCode()
    {
        return new Integer(type).hashCode();
    }
    
    public String toString()
    {
        return typeDescriptions[type];
    }
    
    public static AggregationSettings getType(String description)
    {
        for (int i=0;i<typeDescriptions.length;i++)
        {
            if (typeDescriptions[i].equalsIgnoreCase(description))
            {
                return types[i];
            }
        }
        for (int i=0;i<typeCodes.length;i++)
        {
            if (typeCodes[i].equalsIgnoreCase(description))
            {
                return types[i];
            }
        }
        return NONE;
    }
    
    public String getCode()
    {
        return typeCodes[type];
    }
    
    public String getDescription()
    {
        return typeDescriptions[type];
    }

}
