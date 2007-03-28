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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.pms.messages.Messages;

import be.ibridge.kettle.core.ChangedFlag;

public class Locales extends ChangedFlag
{
    public static final String EN_US = "en_US"; //$NON-NLS-1$
    
    private   List           localeList;
    
    public Locales()
    {
        localeList = new ArrayList();
        
        setDefault();
    }
    
    
    public void setDefault()
    {
        LocaleInterface locale = new LocaleMeta(EN_US, Messages.getString("Locales.USER_LOCALE_DESCRIPTION"), 1, true); //$NON-NLS-1$
        addLocale(locale);
    }


    /**
     * @return the locales
     */
    public List getLocaleList()
    {
        return localeList;
    }

    /**
     * @param locales the locales to set
     */
    public void setLocaleList(List locales)
    {
        this.localeList = locales;
    }

    public int nrLocales()
    {
        return localeList.size();
    }
    
    public LocaleInterface getLocale(int i)
    {
        return (LocaleInterface) localeList.get(i);
    }
    
    public void removeLocale(int i)
    {
        localeList.remove(i);
        setChanged(true);
    }
    
    public void addLocale(LocaleInterface locale)
    {
        localeList.add(locale);
        setChanged(true);
    }

    public void addLocale(int index, LocaleInterface locale)
    {
        localeList.add(index, locale);
        setChanged(true);
    }
    
    public int indexOfLocale(LocaleInterface locale)
    {
        return localeList.indexOf(locale);
    }
    
    public void setLocale(int idx, LocaleInterface locale)
    {
        localeList.set(idx, locale);
        setChanged(true);
    }

    /**
     * @return the activeLocale
     */
    public String getActiveLocale()
    {
        for (int i=0;i<nrLocales();i++)
        {
            if (getLocale(i).isActive()) return getLocale(i).getCode();
        }
        return EN_US; // Just to get something back :-)
    }

    /**
     * @param activeLocale the locale code to set as active
     */
    public void setActiveLocale(String activeLocale)
    {
        for (int i=0;i<nrLocales();i++)
        {
            LocaleInterface locale = getLocale(i);
            locale.setActive(locale.getCode().equalsIgnoreCase(activeLocale));
        }
    }

    public void clearChanged()
    {
        setChanged(false);
    }

    /**
     *  
     * @return
     */
    public String[] getLocaleCodes()
    {
        String codes[] = new String[localeList.size()];
        for (int i = 0; i < codes.length; i++)
        {
            codes[i] = getLocale(i).getCode();
        }
        return codes;
    }
    
    public void sortLocales()
    {
        Collections.sort(localeList);
    }
}
