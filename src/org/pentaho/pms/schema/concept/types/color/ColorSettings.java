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
package org.pentaho.pms.schema.concept.types.color;



public class ColorSettings
{
    private static final String SEPARATOR = ","; //$NON-NLS-1$ 
    
    public static final ColorSettings BLACK    = new ColorSettings(  0,   0,   0);
    public static final ColorSettings WHITE    = new ColorSettings(255, 255, 255);
    public static final ColorSettings RED      = new ColorSettings(255,   0,   0);
    public static final ColorSettings GREEN    = new ColorSettings(  0, 255,   0);
    public static final ColorSettings BLUE     = new ColorSettings(  0,   0, 255);

    int red, green, blue;

    /**
     * @param red
     * @param green
     * @param blue
     */
    public ColorSettings(int red, int green, int blue)
    {
        super();
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * @return the blue
     */
    public int getBlue()
    {
        return blue;
    }

    /**
     * @param blue the blue to set
     */
    public void setBlue(int blue)
    {
        this.blue = blue;
    }

    /**
     * @return the green
     */
    public int getGreen()
    {
        return green;
    }

    /**
     * @param green the green to set
     */
    public void setGreen(int green)
    {
        this.green = green;
    }

    /**
     * @return the red
     */
    public int getRed()
    {
        return red;
    }

    /**
     * @param red the red to set
     */
    public void setRed(int red)
    {
        this.red = red;
    }
    
    public String toString()
    {
        return red+SEPARATOR+green+SEPARATOR+blue;
    }
    
    public static final ColorSettings fromString(String value)
    {
        String colors[] = value.split(SEPARATOR);
        if (colors.length==3)
        {
            int red   = Integer.parseInt(colors[0]);
            int green = Integer.parseInt(colors[1]);
            int blue  = Integer.parseInt(colors[2]);
            
            return new ColorSettings(red, green, blue);
        }
        return null;
    }
    
    public boolean equals(Object obj)
    {
        ColorSettings color = (ColorSettings)obj;
        return red ==color.getRed() && green == color.getGreen() && blue == color.getBlue();
    }
    
    public int hashCode()
    {
        return new Integer(red).hashCode() ^ new Integer(green).hashCode() ^ new Integer(blue).hashCode();
    }
}
