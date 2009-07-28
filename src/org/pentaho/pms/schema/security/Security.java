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

import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Contains a mapping between a SecurityOwner (named user or role) and the rights (integer : masks with ACLs)
 * @author Matt
 * 
 * @deprecated as of metadata 3.0. please use org.pentaho.metadata.model.concept.security.Security
 */
public class Security implements Cloneable
{
    private Map<SecurityOwner,Integer> ownerAclMap;
    
    public Security()
    {
        ownerAclMap = new Hashtable<SecurityOwner,Integer>();
    }

    /**
     * @param aclSets
     * @param securityReference
     */
    public Security(Map<SecurityOwner,Integer> aclSets)
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

        string.append("{"); //$NON-NLS-1$
        List owners = getOwners();
        for (int i=0;i<owners.size();i++)
        {
            SecurityOwner owner = (SecurityOwner) owners.get(i);
            int rights = getOwnerRights(owner);
            if (i>0) string.append(", "); //$NON-NLS-1$
            string.append(owner.toString()+"("+rights+")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        string.append("}"); //$NON-NLS-1$
        
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
        
        xml.append("<security>").append(Const.CR); //$NON-NLS-1$
        
        List owners = getOwners();
        for (int i=0;i<owners.size();i++)
        {
            xml.append("  <owner-rights>").append(Const.CR); //$NON-NLS-1$
            SecurityOwner owner = (SecurityOwner) owners.get(i);
            int rights = getOwnerRights(owner); 
            xml.append("  "+owner.toXML()+" <rights>"+rights+"</rights>").append(Const.CR); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            xml.append("  </owner-rights>").append(Const.CR); //$NON-NLS-1$
        }
        
        xml.append("</security>").append(Const.CR); //$NON-NLS-1$
        
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
            int nrOwnerRights = XMLHandler.countNodes(securityNode, "owner-rights"); //$NON-NLS-1$
            for (int i=0;i<nrOwnerRights;i++)
            {
                Node ownerRightsNode = XMLHandler.getSubNodeByNr(securityNode, "owner-rights", i); //$NON-NLS-1$
                Node ownerNode = XMLHandler.getSubNode(ownerRightsNode, "owner"); //$NON-NLS-1$
                SecurityOwner owner = new SecurityOwner(ownerNode);
                int rights = Integer.parseInt( XMLHandler.getTagValue(ownerRightsNode, "rights") ); //$NON-NLS-1$
                
                putOwnerRights(owner, rights);
            }
        }
        catch(Exception e)
        {
            throw new Exception(Messages.getString("Security.ERROR_0001_CANT_CREATE_SECURITY_OBJECT"), e); //$NON-NLS-1$
        }
    }
    

    public static Security fromXML(String value) throws Exception
    {
        try
        {
            Document doc = XMLHandler.loadXMLString(value);
            return new Security(XMLHandler.getSubNode(doc, "security")); //$NON-NLS-1$
        }
        catch(Exception e)
        {
            throw new Exception(Messages.getString("Security.ERROR_0001_CANT_CREATE_SECURITY_OBJECT"), e); //$NON-NLS-1$
        }
    }

    /**
     * @param owner the owner to store the rights for
     * @param rights the ACLs to store
     */
    public void putOwnerRights(SecurityOwner owner, int rights)
    {
        ownerAclMap.put(owner, rights);
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
    public List<SecurityOwner> getOwners()
    {
        return new ArrayList<SecurityOwner>(ownerAclMap.keySet());
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
    public void setOwnerAclMap(Map<SecurityOwner,Integer> aclSets)
    {
        this.ownerAclMap = aclSets;
    }


}
