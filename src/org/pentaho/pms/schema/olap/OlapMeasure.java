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
package org.pentaho.pms.schema.olap;

import org.pentaho.pms.schema.BusinessColumn;

@SuppressWarnings("deprecation")
public class OlapMeasure implements Cloneable
{
    private String name;
    private BusinessColumn businessColumn;
    
    public OlapMeasure()
    {
    }
    
    /**
     * @param name
     * @param businessColumn
     */
    public OlapMeasure(String name, BusinessColumn businessColumn)
    {
        this();
        this.name = name;
        this.businessColumn = businessColumn;
    }

    public Object clone()
    {
        return new OlapMeasure(name, businessColumn); // shallow copy of business column is desired
    }
    
    /**
     * @return the businessColumn
     */
    public BusinessColumn getBusinessColumn()
    {
        return businessColumn;
    }

    /**
     * @param businessColumn the businessColumn to set
     */
    public void setBusinessColumn(BusinessColumn businessColumn)
    {
        this.businessColumn = businessColumn;
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

}
