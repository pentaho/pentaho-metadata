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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;

/**
 * This is the SQL implementation of the physical model.  For now 
 * it contains a string reference to it's data source (JNDI or Pentaho).
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class SqlPhysicalModel extends Concept implements IPhysicalModel{
  
  private static final long serialVersionUID = 8834210720816769790L;
  
  // this property should be replaced with a thin 
  // representation of database meta, which is required
  // for full backward compatibility.
  
  /** returns a pentaho or JNDI datasource **/
  private SqlDataSource datasource;

  // this contains a list of the physical tables
  private List<SqlPhysicalTable> physicalTables = new ArrayList<SqlPhysicalTable>();

  public SqlPhysicalModel() {
    super();
  }
  
  public String getQueryExecName() {
    return "metadataqueryexec-SQL";
  }

  public String getDefaultQueryClassname() {
    return "org.pentaho.platform.plugin.services.connections.metadata.sql.SqlMetadataQueryExec";
  }
  
  public void setDatasource(SqlDataSource datasource) {
    this.datasource = datasource;
  }

  public SqlDataSource getDatasource() {
    return datasource;
  }
  
  public List<SqlPhysicalTable> getPhysicalTables() {
    return physicalTables;
  }
  
  public void addPhysicalTable(SqlPhysicalTable table) {
    physicalTables.add(table);
  }
  

}
