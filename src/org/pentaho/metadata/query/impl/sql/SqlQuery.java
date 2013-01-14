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
package org.pentaho.metadata.query.impl.sql;

import org.pentaho.commons.connection.IPentahoMetaData;

/**
 * TODO: I'd prefer if the Sql Execution was done in this package, not having the SQL Execution happen outside of metadata
 * To make this happen, we'd need to move SQLConnection and SQLResultSet into a commons project.
 * 
 * This interface defines the result of executing an MQLQuery
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public interface SqlQuery {
  
  public String getQuery();

  public IPentahoMetaData generateMetadata(IPentahoMetaData nativeMetadata);
  
}
