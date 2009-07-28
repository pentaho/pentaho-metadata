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
package org.pentaho.pms.mql;

/**
 * Contains a selection and the sort direction, used to specify the sorting of that column.
 * 
 * @author Matt
 * 
 * @deprecated as of metadata 3.0.  Please use org.pentaho.metadata.query.model.Order
 */
public class OrderBy
{
    private Selection selection;
    private boolean ascending;

    /**
     * @param selection the selection to sort on (ascending)
     */
    public OrderBy(Selection selection) {
        super();
        this.selection = selection;
        this.ascending = true;
    }
    
    /**
     * @param selection the selection to sort on 
     * @param ascending true if you want to sort ascending, false if you want to sort descending
     */
    public OrderBy(Selection selection, boolean ascending)
    {
        super();
        this.selection = selection;
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
     * @return the selection to sort on
     */
    public Selection getSelection()
    {
        return selection;
    }

    /**
     * @param selection the selection to sort on
     */
    public void setSelection(Selection selection)
    {
        this.selection = selection;
    }
    
    
}
