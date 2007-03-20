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

/**
 * Contains a business column an the sort direction, used to specify the sorting of that column.
 * 
 * @author Matt
 *
 */
public class OrderBy
{
    private BusinessColumn businessColumn;
    private boolean ascending;

    /**
     * @param businessColumn the business column to sort on (ascending)
     */
    public OrderBy(BusinessColumn businessColumn)
    {
        super();
        this.businessColumn = businessColumn;
        this.ascending = true;
    }
    
    /**
     * @param businessColumn the business column to sort on 
     * @param ascending true if you want to sort ascending, false if you want to sort descending
     */
    public OrderBy(BusinessColumn businessColumn, boolean ascending)
    {
        super();
        this.businessColumn = businessColumn;
        this.ascending = ascending;
    }

    /**
     * @return the ascending flag, true = ascending, false = descending
     */
    public boolean isAscending()
    {
        return ascending;
    }

    /**
     * @param ascending the ascending flag to set, true = ascending, false = descending
     */
    public void setAscending(boolean ascending)
    {
        this.ascending = ascending;
    }

    /**
     * @return the businessColumn to sort on
     */
    public BusinessColumn getBusinessColumn()
    {
        return businessColumn;
    }

    /**
     * @param businessColumn the businessColumn to sort on
     */
    public void setBusinessColumn(BusinessColumn businessColumn)
    {
        this.businessColumn = businessColumn;
    }
    
    
}
