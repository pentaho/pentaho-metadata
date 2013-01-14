/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.schema;

import java.util.List;

/**
 * Proxy class to tie together a business column in a categories tree and a string representation.
 * 
 * @author Matt
 * 
 * @deprecated as of metadata 3.0.
 */
public class BusinessColumnString
{
    private String         flatRepresentation;
    private int            index;
    private BusinessColumn businessColumn;
    
    /**
     * @param flatRepresentation
     * @param index
     * @param businessColumn
     */
    public BusinessColumnString(String flatRepresentation, int index, BusinessColumn businessColumn)
    {
        super();
        this.flatRepresentation = flatRepresentation;
        this.index = index;
        this.businessColumn = businessColumn;
    }

    public BusinessColumn getBusinessColumn()
    {
        return businessColumn;
    }

    public void setBusinessColumn(BusinessColumn businessColumn)
    {
        this.businessColumn = businessColumn;
    }

    public String getFlatRepresentation()
    {
        return flatRepresentation;
    }

    public void setFlatRepresentation(String flatRepresentation)
    {
        this.flatRepresentation = flatRepresentation;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }
    
    /**
     * @param businessColumnStrings A List of {@link BusinessColumnString}
     * @return just an array of flat representation strings
     */
    public static final String[] getFlatRepresentations(List businessColumnStrings)
    {
        String[] strings = new String[businessColumnStrings.size()];
        
        for (int i=0;i<businessColumnStrings.size();i++)
        {
            BusinessColumnString businessColumnString = (BusinessColumnString) businessColumnStrings.get(i);
            strings[i] = businessColumnString.getFlatRepresentation();
        }
        
        return strings;
    }
    
    public static final int getBusinessColumnIndex(List businessColumnStrings, BusinessColumn businessColumn)
    {
        for (int i=0;i<businessColumnStrings.size();i++)
        {
            BusinessColumnString businessColumnString = (BusinessColumnString) businessColumnStrings.get(i);
            BusinessColumn column = businessColumnString.getBusinessColumn();
            if (column!=null && column.equals(businessColumn)) return i;
        }
        return -1;
    }
}

