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
package org.pentaho.pms.schema.concept.types.columnwidth;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.types.alignment.AlignmentSettings;

public class ColumnWidth
{
    public static final int TYPE_WIDTH_PIXELS  = 0;
    public static final int TYPE_WIDTH_PERCENT = 1;
    public static final int TYPE_WIDTH_INCHES  = 2;
    public static final int TYPE_WIDTH_CM      = 3;
    public static final int TYPE_WIDTH_POINTS  = 4;

    public static final ColumnWidth PIXELS     = new ColumnWidth( TYPE_WIDTH_PIXELS,   100 );
    public static final ColumnWidth PERCENT    = new ColumnWidth( TYPE_WIDTH_PERCENT,   10 );
    public static final ColumnWidth INCHES     = new ColumnWidth( TYPE_WIDTH_INCHES,     3 );
    public static final ColumnWidth CM         = new ColumnWidth( TYPE_WIDTH_CM,        10 );
    public static final ColumnWidth POINTS     = new ColumnWidth( TYPE_WIDTH_POINTS,     1 );

    public static final String typeCodes[] =
        {
            "pixels", "percent", "inches", "cm", "points",   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        };

    public static final String typeDescriptions[] =
        {
            Messages.getString("ColumnWidth.USER_PIXELS_DESC"), Messages.getString("ColumnWidth.USER_PERCENT_DESC"), Messages.getString("ColumnWidth.USER_INCHES_DESC"), Messages.getString("ColumnWidth.USER_CENTIMETERS_DESC"), Messages.getString("ColumnWidth.USER_POINTS_DESC"),     //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        };

    public static final ColumnWidth[] types = new ColumnWidth[]
        {
            PIXELS, PERCENT, INCHES, CM, POINTS,
        };

    private int        type;
    private BigDecimal width;
    private static final String SEPARATOR = ",";

    /**
     * @param type the column width type
     * @param width the prefered width of the column
     */
    public ColumnWidth(int type, BigDecimal width)
    {
        this.type = type;
        this.width = width;
    }

    /**
     * @param type the column width type
     * @param width the prefered width of the column
     */
    public ColumnWidth(int type, int width)
    {
        this.type = type;
        this.width = new BigDecimal(width);
    }

    /**
     * @param type the column width type
     * @param width the prefered width of the column
     */
    public ColumnWidth(int type, double width)
    {
        this.type = type;
        this.width = new BigDecimal(width);
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


  public boolean equals(Object obj) {
    if (obj instanceof ColumnWidth == false) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    ColumnWidth rhs = (ColumnWidth) obj;
    return new EqualsBuilder().append(type, rhs.type).append(width, rhs.width).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(19, 163).append(type).append(width).toHashCode();
  }

  public String toString() {
      return type + SEPARATOR + width;
  }


  public static ColumnWidth fromString( String value )
  {
    String[] parts = value.split( SEPARATOR );
    if ( parts.length != 2 )
    {
      return null;
    }
    return new ColumnWidth( Integer.parseInt( parts[0] ), new BigDecimal( parts[1]) );
  }

    public static ColumnWidth getType(String description)
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
        return PIXELS;
    }

    public String getCode()
    {
        return typeCodes[type];
    }

    public String getDescription()
    {
        return typeDescriptions[type];
    }

    /**
     * @return the width
     */
    public BigDecimal getWidth()
    {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(BigDecimal width)
    {
        this.width = width;
    }

}
