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

/**
 * Created on 6-feb-04
 * 
 * @deprecated as of metadata 3.0.
 */
public class SelectionField
{
	private String name; 
	private PhysicalColumn field;
	private SelectionGroup group;
	
	public SelectionField(String name, PhysicalColumn field, SelectionGroup group)
	{
		this.name = name;
		this.field = field;
		this.group = group;
	}

	public SelectionField(String name, PhysicalColumn field)
	{
		this(name, field, null);
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setField(PhysicalColumn field)
	{
		this.field = field;
	}
	
	public PhysicalColumn getField()
	{
		return field;
	}
	
	public void setGroup(SelectionGroup group)
	{
		this.group = group;
	}
	
	public SelectionGroup getGroup()
	{
		return group;
	}
}
