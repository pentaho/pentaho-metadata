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

import org.pentaho.di.core.changed.ChangedFlagInterface;

public interface LocaleInterface extends ChangedFlagInterface,Comparable<LocaleInterface>
{
    public void setCode(String code);
    public String getCode();

    public void setDescription(String description);
    public String getDescription();
    
    public void setOrder(int order);
    public int getOrder();
    
    public void setActive(boolean active);
    public boolean isActive();
    
    public void clearChanged();
}
