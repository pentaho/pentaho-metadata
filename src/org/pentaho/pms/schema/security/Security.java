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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.pentaho.pms.util.Const;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import be.ibridge.kettle.core.XMLHandler;

/**
 * Contains a mapping between a SecurityOwner (named user or role) and the rights (integer : masks with ACLs)
 * @author Matt
 *
 */
public class Security implements Cloneable
{
    private Map ownerAclMap;
    
    public Security()
    {
        ownerAclMap = new Hashtable();
    }

    /**
     * @param aclSets
     * @param securityReference
     */
    public Security(Map aclSets)
    {
        super();
        this.ownerAclMap = aclSets;
    }
    
    /**
     * Shows the security information as a String (XML)
     */
    public String toString()
    {
        StringBuffer string = new StringBuffer();

        string.append("{");
        List owners = getOwners();
        for (int i=0;i<owners.size();i++)
        {
            SecurityOwner owner = (SecurityOwner) owners.get(i);
            int rights = getOwnerRights(owner);
            if (i>0) string.append(", ");
            string.append(owner.toString()+"("+rights+")");
        }
        string.append("}");
        
        return string.toString();
    }
    
    public Object clone()
    {
        Security security = new Security();
        List owners = getOwners();
        for (int i=0;i<owners.size();i++)
        {
            SecurityOwner owner = (SecurityOwner) owners.get(i);
            int rights = getOwnerRights(owner);
            
            security.putOwnerRights((SecurityOwner) owner.clone(), rights);
        }
        return security;
    }
    
    public String toXML()
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<security>").append(Const.CR);
        
        List owners = getOwners();
        for (int i=0;i<owners.size();i++)
        {
            xml.append("  <owner-rights>").append(Const.CR);
            SecurityOwner owner = (SecurityOwner) owners.get(i);
            int rights = getOwnerRights(owner); 
            xml.append("  "+owner.toXML()+" <rights>"+rights+"</rights>").append(Const.CR);
            xml.append("  </owner-rights>").append(Const.CR);
        }
        
        xml.append("</security>").append(Const.CR);
        
        return xml.toString();
    }
    
    /**
     * Create a new Security object from string (xml)
     * @param value the String to convert
     * @return the new Security object
     * @throws Exception in case the XML is not valid for this object.
     */
    public Security(Node securityNode) throws Exception
    {
        this();
        
        try
        {
            int nrOwnerRights = XMLHandler.countNodes(securityNode, "owner-rights");
            for (int i=0;i<nrOwnerRights;i++)
            {
                Node ownerRightsNode = XMLHandler.getSubNodeByNr(securityNode, "owner-rights", i);
                Node ownerNode = XMLHandler.getSubNode(ownerRightsNode, "owner");
                SecurityOwner owner = new SecurityOwner(ownerNode);
                int rights = Integer.parseInt( XMLHandler.getTagValue(ownerRightsNode, "rights") );
                
                putOwnerRights(owner, rights);
            }
        }
        catch(Exception e)
        {
            throw new Exception("Unable to create security object from XML", e);
        }
    }
    

    public static Security fromXML(String value) throws Exception
    {
        try
        {
            Document doc = XMLHandler.loadXMLString(value);
            return new Security(XMLHandler.getSubNode(doc, "security"));
        }
        catch(Exception e)
        {
            throw new Exception("Unable to create security object from XML / String", e);
        }
    }

    /**
     * @param owner the owner to store the rights for
     * @param rights the ACLs to store
     */
    public void putOwnerRights(SecurityOwner owner, int rights)
    {
        ownerAclMap.put(owner, new Integer(rights));
    }
    
    /**
     * @param owner the owner to get the rights for
     * @return the ACLs
     */
    public int getOwnerRights(SecurityOwner owner)
    {
        return ((Integer)ownerAclMap.get(owner)).intValue();
    }

    /**
     * Remove the rights out of the map for the given user
     * @param owner the owner to remove the rights for.
     */
    public void removeOwnerRights(SecurityOwner owner)
    {
        ownerAclMap.remove(owner);
    }


    /**
     * @return a list of all the owners in the map
     */
    public List getOwners()
    {
        return new ArrayList(ownerAclMap.keySet());
    }

    /**
     * @return the aclSets
     */
    public Map getOwnerAclMap()
    {
        return ownerAclMap;
    }

    /**
     * @param aclSets the aclSets to set
     */
    public void setOwnerAclMap(Map aclSets)
    {
        this.ownerAclMap = aclSets;
    }


}
