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
package org.pentaho.pms.schema.concept.types.font;

import org.pentaho.pms.util.Const;



public class FontSettings
{
    private String  name;
    private int     height;
    private boolean bold;
    private boolean italic;

    private static final String SEPARATOR = "-";
    private static final String BOLD = "bold";
    private static final String ITALIC = "italic";

    public FontSettings()
    {
    }
    
    /**
     * @param name
     * @param size
     * @param bold
     * @param italic
     */
    public FontSettings(String name, int size, boolean bold, boolean italic)
    {
        this.name = name;
        this.height = size;
        this.bold = bold;
        this.italic = italic;
    }
    
    public String toString()
    {
        return name+SEPARATOR+height+(bold?SEPARATOR+BOLD:"")+(italic?SEPARATOR+ITALIC:"");
    }
    
    public static FontSettings fromString(String value)
    {
        String pieces[] = value.split(SEPARATOR);
        switch(pieces.length)
        {
        case 0: return null; 
        case 1: return new FontSettings(pieces[0], 10, false, false);
        case 2: return new FontSettings(pieces[0], Const.toInt(pieces[1], 10), false, false);
        case 3: return new FontSettings(pieces[0], Const.toInt(pieces[1], 10), BOLD.equalsIgnoreCase(pieces[2]), ITALIC.equalsIgnoreCase(pieces[2]));
        case 4: return new FontSettings(pieces[0], Const.toInt(pieces[1], 10), true, true);
        default: return null; 
        }
    }

    /**
     * @return the bold
     */
    public boolean isBold()
    {
        return bold;
    }
    
    /**
     * @param bold the bold to set
     */
    public void setBold(boolean bold)
    {
        this.bold = bold;
    }
    
    /**
     * @return the italic
     */
    public boolean isItalic()
    {
        return italic;
    }
    
    /**
     * @param italic the italic to set
     */
    public void setItalic(boolean italic)
    {
        this.italic = italic;
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
    
    /**
     * @return the size
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @param size the size to set
     */
    public void setHeight(int size)
    {
        this.height = size;
    }

    public boolean equals(Object obj)
    {
        FontSettings cmp = (FontSettings) obj;
        return (cmp.getName().equals(name)) && (cmp.getHeight()==height) && (cmp.isBold()==bold) && (cmp.isItalic()==italic);
    }
    
    public int hashCode()
    {
        return (name.hashCode()) ^ (new Integer(height).hashCode()) ^ (new Boolean(bold).hashCode()) ^ (new Boolean(italic).hashCode());
    }
    
}
