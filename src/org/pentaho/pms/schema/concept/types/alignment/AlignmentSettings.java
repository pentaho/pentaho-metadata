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
package org.pentaho.pms.schema.concept.types.alignment;

public class AlignmentSettings
{
    public static final int TYPE_ALIGNMENT_LEFT          = 0;
    public static final int TYPE_ALIGNMENT_RIGHT         = 1;
    public static final int TYPE_ALIGNMENT_CENTERED      = 2;
    public static final int TYPE_ALIGNMENT_JUSTIFIED     = 3;
    
    public static final AlignmentSettings LEFT           = new AlignmentSettings(TYPE_ALIGNMENT_LEFT);
    public static final AlignmentSettings RIGHT          = new AlignmentSettings(TYPE_ALIGNMENT_RIGHT);
    public static final AlignmentSettings CENTERED       = new AlignmentSettings(TYPE_ALIGNMENT_CENTERED);
    public static final AlignmentSettings JUSTIFIED      = new AlignmentSettings(TYPE_ALIGNMENT_JUSTIFIED);
    
    public static final String typeCodes[] = 
        { 
            "left", "right", "centered", "justified",   
        };
    
    public static final String typeDescriptions[] = 
        { 
            "Left", "Right", "Centered", "Justified",    
        };
    
    public static final AlignmentSettings[] types = new AlignmentSettings[]
        {
            LEFT, RIGHT, CENTERED, JUSTIFIED,  
        };

    private int type;

    /**
     * @param name
     * @param type
     */
    public AlignmentSettings(int type)
    {
        this.type = type;
    }

    /**
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type)
    {
        this.type = type;
    }
    
    public boolean equals(Object obj)
    {
        return type == ((AlignmentSettings)obj).getType();
    }
    
    public int hashCode()
    {
        return new Integer(type).hashCode();
    }
    
    public String toString()
    {
        return typeDescriptions[type];
    }
    
    public static AlignmentSettings getType(String description)
    {
        for (int i=0;i<typeDescriptions.length;i++)
        {
            if (typeDescriptions[i].equalsIgnoreCase(description))
            {
                return types[i];
            }
        }
        for (int i=0;i<typeCodes.length;i++)
        {
            if (typeCodes[i].equalsIgnoreCase(description))
            {
                return types[i];
            }
        }
        return LEFT;
    }
    
    public String getCode()
    {
        return typeCodes[type];
    }
    
    public String getDescription()
    {
        return typeDescriptions[type];
    }

}
