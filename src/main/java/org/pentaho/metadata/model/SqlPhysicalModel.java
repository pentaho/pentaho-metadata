/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;

/**
 * This is the SQL implementation of the physical model. For now it contains a string reference to it's data source
 * (JNDI or Hitachi Vantara).
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class SqlPhysicalModel extends Concept implements IPhysicalModel {

  private static final long serialVersionUID = 8834210720816769790L;

  private static final String CLASS_ID = "IPhysicalModel";

  // this property should be replaced with a thin
  // representation of database meta, which is required
  // for full backward compatibility.

  /**
   * returns a pentaho or JNDI datasource
   **/
  private SqlDataSource datasource;

  // this contains a list of the physical tables
  private List<SqlPhysicalTable> physicalTables = new ArrayList<SqlPhysicalTable>();

  public SqlPhysicalModel() {
    super();
  }

  public void setDomain( Domain domain ) {
    setParent( domain );
  }

  public Domain getDomain() {
    return (Domain) getParent();
  }

  @Override
  public List<String> getUniqueId() {
    List<String> uid = new ArrayList<String>();
    uid.add( CLASS_ID.concat( UID_TYPE_SEPARATOR ) + getId() );
    return uid;
  }

  @Override
  public List<IConcept> getChildren() {
    List<IConcept> children = new ArrayList<IConcept>();
    children.addAll( physicalTables );
    return children;
  }

  public String getQueryExecName() {
    return "metadataqueryexec-SQL";
  }

  public String getDefaultQueryClassname() {
    return "org.pentaho.platform.plugin.services.connections.metadata.sql.SqlMetadataQueryExec";
  }

  public void setDatasource( SqlDataSource datasource ) {
    this.datasource = datasource;
  }

  public SqlDataSource getDatasource() {
    return datasource;
  }

  public List<SqlPhysicalTable> getPhysicalTables() {
    return physicalTables;
  }

  public void addPhysicalTable( SqlPhysicalTable table ) {
    physicalTables.add( table );
  }

}
