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
package org.pentaho.pms.schema.security;

import org.pentaho.pms.util.Const;
import org.w3c.dom.Node;

import be.ibridge.kettle.core.XMLHandler;

public class SecurityACL implements Cloneable, Comparable
{
    private String name;
    private int    mask;
    
    public SecurityACL()
    {
    }
    
    /**
     * @param name
     * @param mask
     */
    public SecurityACL(String name, int mask)
    {
        super();
        this.name = name;
        this.mask = mask;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            return null;
        }
    }

    public int compareTo(Object o)
    {
        SecurityACL other = (SecurityACL) o;
        return mask-other.mask;
    }
    
    public String toXML()
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<acl>");
        xml.append(XMLHandler.addTagValue("name", name, false));
        xml.append(XMLHandler.addTagValue("mask", mask, false));
        xml.append("</acl>");
        
        return xml.toString();
    }
    
    public SecurityACL(Node aclNode)
    {
        name = XMLHandler.getTagValue(aclNode, "name");
        mask = Const.toInt(XMLHandler.getTagValue(aclNode, "mask"), 0);
    }
    
    public String toString()
    {
        return toXML();
    }

    /**
     * @return the mask
     */
    public int getMask()
    {
        return mask;
    }

    /**
     * @param mask the mask to set
     */
    public void setMask(int mask)
    {
        this.mask = mask;
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
