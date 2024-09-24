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
package org.pentaho.pms.mql;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;

/**
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.query.model.Query
 */
public interface MQLQuery {

  public MappedQuery getQuery() throws PentahoMetadataException;

  public DatabaseMeta getDatabaseMeta();

  public SchemaMeta getSchemaMeta();

  public void setSchemaMeta( SchemaMeta schemaMeta );

  public BusinessModel getModel();

  public void setModel( BusinessModel model );

  public boolean getDisableDistinct();

  public void setDisableDistinct( boolean value );

  public int getLimit();

  public void setLimit( int limit );

  public List<Selection> getSelections();

  public void setSelections( List<Selection> list );

  public List<WhereCondition> getConstraints();

  public void setConstraints( List<WhereCondition> list );

  public List<OrderBy> getOrder();

  public void setOrder( List<OrderBy> list );

  public String getXML();

}
