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
package org.pentaho.pms.schema.concept.types.localstring;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class LocalizedStringSettings implements Cloneable
{
    public static final LocalizedStringSettings EMPTY = new LocalizedStringSettings();

    private Map<String,String> localeStringMap;

    /**
     * @param localeStringMap
     */
    public LocalizedStringSettings()
    {
        localeStringMap=new Hashtable<String,String>(5);
    }

    public Object clone() throws CloneNotSupportedException
    {
        LocalizedStringSettings settings = new LocalizedStringSettings();
        settings.localeStringMap.putAll(localeStringMap);
        /* String locales[] = getLocales();
        for (int i=0;i<locales.length;i++)
        {
            settings.setLocaleString(locales[i], getString(locales[i]));
        }
        */
        return settings;
    }

    /**
     * @param localeStringMap
     */
    public LocalizedStringSettings(Map<String,String> localeStringMap)
    {
        this.localeStringMap = localeStringMap;
    }

    public boolean equals(Object obj) {
      if (obj instanceof LocalizedStringSettings == false) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      LocalizedStringSettings rhs = (LocalizedStringSettings) obj;
      return new EqualsBuilder().append(localeStringMap, rhs.localeStringMap).isEquals();
    }

    public int hashCode() {
      return new HashCodeBuilder(19, 163).append(localeStringMap).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
          append(localeStringMap).
          toString();
    }

    /**
     * @return the localeStringMap
     */
    public Map getLocaleStringMap()
    {
        return localeStringMap;
    }

    /**
     * @param localeStringMap the localeStringMap to set
     */
    public void setLocaleStringMap(Map<String,String> localeStringMap)
    {
        this.localeStringMap = localeStringMap;
    }

    public String getString(String locale)
    {
        return (String) localeStringMap.get(locale);
    }

    public void setLocaleString(String locale, String string)
    {
        localeStringMap.put(locale, string);
    }

    public String[] getLocales()
    {
        return (String[]) localeStringMap.keySet().toArray(new String[localeStringMap.keySet().size()]);
    }

    public void clear()
    {
        localeStringMap.clear();
    }

}
