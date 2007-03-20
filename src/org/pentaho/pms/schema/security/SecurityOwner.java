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


import org.w3c.dom.Node;

import be.ibridge.kettle.core.XMLHandler;


/**
 * A security owner is a combination of a user or role type and the name of that user or role.
 * 
 * @author Matt
 * @since 01-NOV-2006
 * 
 */
public class SecurityOwner implements Cloneable
{
    public static final String STRING_USER_DESC = "User";
    public static final String STRING_ROLE_DESC = "Role";
    
    public static final int OWNER_TYPE_USER = 0;
    public static final int OWNER_TYPE_ROLE = 1;
    
    public static final String[] ownerTypeCodes        = new String[] { "user", "role" };
    public static final String[] ownerTypeDescriptions = new String[] { STRING_USER_DESC, STRING_ROLE_DESC };
    
    private int ownerType;
    private String ownerName;
    
    /**
     * @param ownerType the type of ACL owner : user or role
     * @param ownerName the name or the user or role
     * @param rights
     */
    public SecurityOwner(int ownerType, String ownerName, int rights)
    {
        this.ownerType = ownerType;
        this.ownerName = ownerName;
    }

    /**
     * Create a new ACL Set without any rights.
     * @param ownerType the type of ACL : user or role
     * @param ownerName the name or the user or role
     */
    public SecurityOwner(int ownerType, String ownerName)
    {
        this(ownerType, ownerName, 0);
    }

    public String toString()
    {
        return ownerName+":"+getOwnerTypeDescription();
    }

    public String toXML()
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<owner>");
        xml.append(XMLHandler.addTagValue("type", getOwnerTypeCode(), false));
        xml.append(XMLHandler.addTagValue("name", ownerName, false));
        xml.append("</owner>");
        
        return xml.toString();
    }
    
    public SecurityOwner(Node ownerNode)
    {
        ownerType = getOwnerType( XMLHandler.getTagValue(ownerNode, "type") );
        ownerName = XMLHandler.getTagValue(ownerNode, "name");
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

    public boolean equals(Object obj)
    {
        SecurityOwner set = (SecurityOwner) obj;
        return ownerType == set.ownerType && ownerName.equals(set.ownerName); // I WANT this to NPE if ownerName is null :-)
    }
    
    public int hashCode()
    {
        return new Integer(ownerType).hashCode() ^ ownerName.hashCode();
    }
    
    /**
     * @return the type of ACL : user or role
     */
    public int getOwnerType()
    {
        return ownerType;
    }

    /**
     * @param aclType the type of ACL to set: user or role
     */
    public void setOwnerType(int aclType)
    {
        this.ownerType = aclType;
    }

    /**
     * @return the name or the user or role
     */
    public String getOwnerName()
    {
        return ownerName;
    }

    /**
     * @param ownerName the name or the user or role
     */
    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    /**
     * @return the code for the type of ACL : user or role
     */
    public String getOwnerTypeCode()
    {
        return ownerTypeCodes[ownerType];
    }
    
    /**
     * @return the description for the type of ACL : user or role
     */
    public String getOwnerTypeDescription()
    {
        return ownerTypeDescriptions[ownerType];
    }

    /**
     * @param description the description or code of the ACL owner type (user or role)
     * @return the ACL type
     */
    public static final int getOwnerType(String description)
    {
        for (int i=0;i<ownerTypeDescriptions.length;i++)
        {
            if (ownerTypeDescriptions[i].equalsIgnoreCase(description)) return i;
        }
        for (int i=0;i<ownerTypeCodes.length;i++)
        {
            if (ownerTypeCodes[i].equalsIgnoreCase(description)) return i;
        }
        return OWNER_TYPE_USER;
    }


}
