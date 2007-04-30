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
package org.pentaho.pms.schema.concept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.security.ConceptPropertySecurity;

import be.ibridge.kettle.core.ChangedFlag;

public class Concept extends ChangedFlag implements ConceptInterface, Cloneable
{
    private String name;

    private Map childPropertyInterfaces;
    private ConceptInterface parentInterface;
    private ConceptInterface inheritedInterface;
    private ConceptInterface securityParentInterface;

    public Concept()
    {
        this.childPropertyInterfaces = new Hashtable();
    }

    public Concept(String name)
    {
        this();
        this.name = name;
    }

    public Concept(String name, ConceptInterface parentInterface)
    {
        this(name);
        this.parentInterface = parentInterface;
    }

    public boolean equals(Object obj)
    {
        Concept concept = (Concept)obj;
        if (name!=null)
        {
            return name.equals(concept.getName());
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        if (name!=null)
        {
            return name.hashCode();
        }
        return 0;
    }

    public Object clone()
    {
        try
        {
            Concept concept = new Concept();
            concept.setName(name);
            concept.setParentInterface(parentInterface);
            concept.setInheritedInterface(inheritedInterface);
            if (securityParentInterface!=null) concept.setSecurityParentInterface((ConceptInterface) securityParentInterface.clone()); // deep copy of the security information
            String[] ids = getChildPropertyIDs();
            for (int i=0;i<ids.length;i++)
            {
                ConceptPropertyInterface property = getChildProperty(ids[i]);
                if (property!=null)
                {
                    concept.addProperty((ConceptPropertyInterface) property.clone());
                }
                else
                {
                    System.out.println(Messages.getString("Concept.ERROR_0001_NO_PROPERTY_FOUND", ids[i])); //$NON-NLS-1$
                }
            }
            concept.setChanged(hasChanged());

            return concept;
        }
        catch(CloneNotSupportedException e)
        {
            return null;
        }
    }

    public String toString()
    {
        StringBuffer string = new StringBuffer();

        // Print the properties...
        String[] propertyIDs = getPropertyIDs();
        for (int i = 0; i < propertyIDs.length; i++)
        {
            String id = propertyIDs[i];
            String value = getProperty(id).toString();
            if (i>0) string.append(", "); //$NON-NLS-1$
            string.append(value);
        }

        return string.toString();
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

    public Map getChildPropertyInterfaces()
    {
        return childPropertyInterfaces;
    }

    public Map getParentPropertyInterfaces()
    {
        if (parentInterface==null) return null;
        return parentInterface.getPropertyInterfaces();
    }

    public Map getInheritedPropertyInterfaces()
    {
        if (inheritedInterface==null) return null;
        return inheritedInterface.getPropertyInterfaces();
    }

    public Map getSecurityPropertyInterfaces()
    {
        if (securityParentInterface==null) return null;
        return securityParentInterface.getPropertyInterfaces();
    }

    /**
     * @return a read-only list of properties: please modify using the child properties
     */
    public Map getPropertyInterfaces()
    {
        Map all = new Hashtable();

        // Properties inherited from the "logical relationship": BusinessColumn inherits from Physical Column, B.Table from Ph.Table
        //
        if (inheritedInterface!=null)
        {
            all.putAll(inheritedInterface.getPropertyInterfaces());
        }

        // Properties inherited from the pre-defined concepts like "Base", "ID", "Name", "Description", etc.
        //
        if (parentInterface!=null)
        {
            all.putAll(parentInterface.getPropertyInterfaces());
        }

        // The security settings from the security parent: Business table inherits from Business model, business column from business table
        //
        if (securityParentInterface!=null) // Only take over the security information, nothing else
        {
            String id = DefaultPropertyID.SECURITY.getId();
            ConceptPropertySecurity parentSecurityProperty = (ConceptPropertySecurity) securityParentInterface.getPropertyInterfaces().get(id);
            if (parentSecurityProperty!=null)
            {
               all.put(id, parentSecurityProperty);
            }
        }

        // The child properties overwrite everything else.
        //
        all.putAll(childPropertyInterfaces);

        return all;
    }

    public void setParentInterface(ConceptInterface parentInterface)
    {
        if ( (this.parentInterface==null && parentInterface!=null) ||
             (this.parentInterface!=null && parentInterface==null) ||
             (this.parentInterface!=null && parentInterface!=null && !this.parentInterface.equals(parentInterface))
            )
        {
            setChanged(true);
        }
        this.parentInterface = parentInterface;
    }

    public ConceptInterface getParentInterface()
    {
        return parentInterface;
    }

    public void addProperty(ConceptPropertyInterface property)
    {
        childPropertyInterfaces.put(property.getId(), property);
        setChanged(true);
    }

    public ConceptInterface getInheritedInterface()
    {
        return inheritedInterface;
    }

    public void setInheritedInterface(ConceptInterface inheritedInterface)
    {
        this.inheritedInterface = inheritedInterface;
    }

    public ConceptPropertyInterface getProperty(String id)
    {
        return (ConceptPropertyInterface) getPropertyInterfaces().get(id);
    }

    public ConceptPropertyInterface getChildProperty(String id)
    {
        return (ConceptPropertyInterface) getChildPropertyInterfaces().get(id);
    }

    public ConceptPropertyInterface getParentProperty(String id)
    {
        if (parentInterface==null) return null;
        return (ConceptPropertyInterface) getParentPropertyInterfaces().get(id);
    }

    public ConceptPropertyInterface getInheritedProperty(String id)
    {
        if (inheritedInterface==null) return null;
        return (ConceptPropertyInterface) getInheritedPropertyInterfaces().get(id);
    }

    public ConceptPropertyInterface getSecurityProperty(String id)
    {
        if (securityParentInterface==null) return null;
        return (ConceptPropertyInterface) getSecurityPropertyInterfaces().get(id);
    }

    public void clearChanged()
    {
        setChanged(false);
    }

    public String[] getChildPropertyIDs()
    {
        return getSortedPropertyIDs(childPropertyInterfaces);
    }

    public String[] getPropertyIDs()
    {
        return getSortedPropertyIDs(getPropertyInterfaces());
    }

    private static final String[] getSortedPropertyIDs(Map map)
    {
        Set keySet = map.keySet();
        List list = new ArrayList(keySet);
        Comparator comparator = new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                String one = (String) o1;
                String two = (String) o2;

                int oneOrder = DefaultPropertyID.findDefaultPropertyOrder(one);
                int twoOrder = DefaultPropertyID.findDefaultPropertyOrder(two);

                if (oneOrder!=twoOrder) return oneOrder-twoOrder;

                // Non-default properties: order by ID
                return one.compareTo(two);
            }
        };
        Collections.sort(list, comparator);
        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * @return the depth from the root concept<br>
     * <br>
     * Depth 1: child<br>
     * Depth 2: child --> parent<br>
     * Depth 3: child --> parent --> parent<br>
     * etc.<br>
     */
    public int getDepth()
    {
        int depth = 0;
        ConceptInterface follow = this;
        while (follow!=null)
        {
            follow = follow.getParentInterface();
            depth++;
        }

        return depth;
    }

    /**
     * @return the path from root --> .. --> gp --> parent --> concept
     */
    public String[] getPath()
    {
        int depth = getDepth();
        String[] retval = new String[depth];

        int d=0;
        ConceptInterface follow = this;
        while (follow!=null)
        {
            retval[depth-d-1] = follow.getName();
            follow = follow.getParentInterface();
            d++;
        }

        return retval;
    }

    /**
     * @param path the path to match with
     * @return true if the path matches the specified path from root --> .. --> gp --> parent --> concept
     */
    public boolean matches(String[] path)
    {
        if (path.length==0) return false;

        int depth=0;
        ConceptInterface follow = this;
        while (follow!=null && depth<path.length)
        {
            if (!follow.getName().equals(path[path.length-depth-1])) return false;
            follow = follow.getParentInterface();
            depth++;
        }

        if (depth!=path.length) return false;

        return true;
    }


    /**
     * Find a localized property (type ConceptPropertyType.PROPERTY_TYPE_LOC_STRING)
     * @param propertyName the name
     * @param locale the locale to search for
     * @return the localized value (string) or null if nothing could be found
     */
    public String getLocalizedProperty(String propertyName, String locale)
    {
        ConceptPropertyInterface property = getProperty(propertyName);
        if (property!=null && property.getType().equals(ConceptPropertyType.LOCALIZED_STRING))
        {
            LocalizedStringSettings locString = (LocalizedStringSettings) property.getValue();
            return locString.getString(locale);
        }
        return null;
    }

    /**
     * Add a localized string property (type ConceptPropertyType.PROPERTY_TYPE_LOC_STRING) to this concept
     * @param propertyName the name
     * @param locale the locale
     * @param value the value to store
     */
    public void addLocalizedProperty(String propertyName, String locale, String value)
    {
        // Get the property
        //
        ConceptPropertyInterface property = getChildProperty(propertyName);
        LocalizedStringSettings locStringSettings;

        // Found anything?
        if (property==null || (property!=null && !property.getType().equals(ConceptPropertyType.LOCALIZED_STRING)) || property.getValue()==null)
        {
            locStringSettings = new LocalizedStringSettings();

            // If there is no (or an empty) property or it somehow got the wrong type in there, we create a new one on that for that name...
            //
            property = new ConceptPropertyLocalizedString(propertyName, locStringSettings);
            addProperty(property);
        }
        else
        {
            locStringSettings = (LocalizedStringSettings) property.getValue();
        }

        // Now, that we have the localized String settings property, add the localized value to it..
        locStringSettings.setLocaleString(locale, value);
    }

    /**
     * @return The locale used in the properties of type Localized String
     */
    public String[] getUsedLocale()
    {
        Map locales = new Hashtable();
        String[] propertyNames = getChildPropertyIDs();
        for (int i = 0; i < propertyNames.length; i++)
        {
            ConceptPropertyInterface property = getProperty(propertyNames[i]);
            if (property.getType().equals(ConceptPropertyType.LOCALIZED_STRING))
            {
                // Yep, this is localized.
                LocalizedStringSettings locString = (LocalizedStringSettings) property.getValue();
                String[] locs = locString.getLocales();
                for (int j=0;j<locs.length;j++) locales.put(locs[j], ""); //$NON-NLS-1$
            }
        }

        Set keySet = locales.keySet();
        return (String[])keySet.toArray(new String[keySet.size()]);
    }

    /**
     * Get an array of the localized concept properties (ONLY type Localized String!!)
     * @param locale the locale to look for
     * @return an array of the localized concept properties
     */
    public ConceptPropertyInterface[] getLocalizedProperties(String locale)
    {
        List propertiesList = new ArrayList();

        String[] propertyNames = getChildPropertyIDs();
        for (int i = 0; i < propertyNames.length; i++)
        {
            ConceptPropertyInterface property = getProperty(propertyNames[i]);
            if (property.getType().equals(ConceptPropertyType.LOCALIZED_STRING))
            {
                // Yep, this is localized.
                LocalizedStringSettings locString = (LocalizedStringSettings) property.getValue();
                if (locString.getString(locale)!=null)
                {
                    // We have a property for this locale
                    // add it to the list or properties
                    propertiesList.add(property);
                }
            }
        }

        return (ConceptPropertyInterface[])propertiesList.toArray(new ConceptPropertyInterface[propertiesList.size()]);
    }

    /**
     * Special utility method: save a special localized property indicating the name
     * @param locale the locale the name is in
     * @param name the name of the object that contains this concept
     */
    public void setName(String locale, String name)
    {
        addLocalizedProperty(DefaultPropertyID.NAME.getId(), locale, name);
    }

    /**
     * special utility method: get the value of a special localized property indicating the name
     * @param locale the locale the name is in
     * @return the localized name or null if nothing was found in the specified locale
     */
    public String getName(String locale)
    {
        ConceptPropertyInterface property = getProperty(DefaultPropertyID.NAME.getId());
        if (property!=null && property.getType().equals(ConceptPropertyType.LOCALIZED_STRING))
        {
            LocalizedStringSettings locString = (LocalizedStringSettings)property.getValue();
            if (locString!=null)
            {
                return locString.getString(locale);
            }
        }
        return null;
    }

    /**
     * Special utility: save a special localized property indicating the description
     * @param locale the locale the description is in
     * @param name the description of the object that contains this concept
     */
    public void setDescription(String locale, String description)
    {
        addLocalizedProperty(DefaultPropertyID.DESCRIPTION.getId(), locale, description);
    }

    /**
     * special utility method: get the value of a special localized property indicating the description
     * @param locale the locale the description is in
     * @return the localized description or null if nothing was found in the specified locale
     */
    public String getDescription(String locale)
    {
        ConceptPropertyInterface property = getProperty(DefaultPropertyID.DESCRIPTION.getId());
        if (property!=null && property.getType().equals(ConceptPropertyType.LOCALIZED_STRING))
        {
            LocalizedStringSettings locString = (LocalizedStringSettings)property.getValue();
            if (locString!=null)
            {
                return locString.getString(locale);
            }
        }
        return null;
    }

    /**
     * Clears the properties of this concept, not the parents or anything like that.
     */
    public void clearChildProperties()
    {
        getChildPropertyInterfaces().clear();
        setChanged(true);
    }

    /**
     * Remove a property from the children.
     * @param property The property to remove
     */
    public void removeChildProperty(ConceptPropertyInterface property)
    {
        childPropertyInterfaces.remove(property.getId());
        setChanged(true);
    }

    public boolean hasInheritedConcept()
    {
        return inheritedInterface!=null;
    }

    public boolean hasParentConcept()
    {
        return parentInterface!=null;
    }

    public boolean hasSecurityParentConcept()
    {
        return securityParentInterface!=null;
    }

    /**
     * @return the securityParentInterface
     */
    public ConceptInterface getSecurityParentInterface()
    {
        return securityParentInterface;
    }

    /**
     * @param securityParentInterface the securityParentInterface to set
     */
    public void setSecurityParentInterface(ConceptInterface securityParentInterface)
    {
        this.securityParentInterface = securityParentInterface;
    }

    /**
     * Go up to the parent, the grandparent, etc, until you find a parent concept.
     * @return the first parent concept by going up until the root
     */
    public ConceptInterface findFirstParentConcept()
    {
        ConceptInterface concept = this;
        ConceptInterface parent = parentInterface;
        int levels = 0; // just to make sure we're not going in an endless loop somewhere.
        while (concept!=null && parent==null && concept.getInheritedInterface()!=null && levels<20)
        {
            concept = concept.getInheritedInterface();
            parent = concept.getParentInterface();
            levels++;
        }
        return parent;
    }
}
