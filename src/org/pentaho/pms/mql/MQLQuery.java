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
package org.pentaho.pms.mql;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;

/**
 * @deprecated as of metadata 3.0.  Please use org.pentaho.metadata.query.model.Query
 */
public interface MQLQuery {

  public MappedQuery getQuery() throws PentahoMetadataException;
  
  public DatabaseMeta getDatabaseMeta();

  public SchemaMeta getSchemaMeta();

  public void setSchemaMeta(SchemaMeta schemaMeta);
  
  public BusinessModel getModel();

  public void setModel(BusinessModel model);
  
  public boolean getDisableDistinct();
 
  public void setDisableDistinct(boolean value);
  
  public int getLimit();
  
  public void setLimit(int limit);
  
  public List<Selection> getSelections();
  
  public void setSelections(List<Selection> list);
 
  public List<WhereCondition> getConstraints();
  
  public void setConstraints(List<WhereCondition> list);
  
  public List<OrderBy> getOrder();
  
  public void setOrder(List<OrderBy> list);

  public String getXML();

}