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
package org.pentaho.pms.locale;

import be.ibridge.kettle.core.ChangedFlag;

public class LocaleMeta extends ChangedFlag implements LocaleInterface, Comparable
{
    private String  code;
    private String  description;
    private int     order;
    private boolean active;
    
    /**
     * @param code
     * @param description
     * @param order
     * @param active
     */
    public LocaleMeta(String code, String description, int order, boolean active)
    {
        super();
        this.code = code;
        this.description = description;
        this.order = order;
        this.active = active;
    }

    public LocaleMeta()
    {
        super();
    }

    /**
     * @return the active
     */
    public boolean isActive()
    {
        return active;
    }
    
    /**
     * @param active the active to set
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    /**
     * @return the code
     */
    public String getCode()
    {
        return code;
    }
    
    /**
     * @param code the code to set
     */
    public void setCode(String code)
    {
        this.code = code;
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
     * @return the order
     */
    public int getOrder()
    {
        return order;
    }
    
    /**
     * @param order the order to set
     */
    public void setOrder(int order)
    {
        this.order = order;
    }

    /**
     * Clear the changed flag
     */
    public void clearChanged()
    {
        setChanged(false);   
    }

    public int compareTo(Object obj)
    {
        LocaleMeta locale = (LocaleMeta) obj;
        if (order==locale.order) return code.compareTo(locale.code);
        return new Integer(order).compareTo(new Integer(locale.order));
    }
    
}
