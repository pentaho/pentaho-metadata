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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.metadata.util;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Props;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.*;
import org.pentaho.metadata.model.SqlDataSource.DataSourceType;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.security.SecurityOwner.OwnerType;
import org.pentaho.metadata.model.concept.types.*;
import org.pentaho.pms.util.Settings;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SQLModelGenerator {
  String modelName;

  int[] columnTypes;

  String[] columnNames;

  String query;

  String connectionName;

  Boolean securityEnabled;

  List<String> users;

  List<String> roles;

  int defaultAcls;

  String createdBy;

  String dbType;

  public SQLModelGenerator() {
    super();
    if (!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
  }

  public SQLModelGenerator(String modelName, String connectionName, int[] columnTypes, String[] columnNames, String query,
      Boolean securityEnabled, List<String> users, List<String> roles, int defaultAcls, String createdBy) {
    if (!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
    this.query = query;
    this.connectionName = connectionName;
    this.columnTypes = columnTypes;
    this.columnNames = columnNames;
    this.modelName = modelName;
    this.securityEnabled = securityEnabled;
    this.users = users;
    this.roles = roles;
    this.defaultAcls = defaultAcls;
    this.createdBy = createdBy;
  }

  public SQLModelGenerator(String modelName, String connectionName, String dbType, int[] columnTypes, String[] columnNames, String query,
      Boolean securityEnabled, List<String> users, List<String> roles, int defaultAcls, String createdBy) {
    this(modelName, connectionName, columnTypes, columnNames, query, securityEnabled, users, roles, defaultAcls, createdBy);
    this.dbType = dbType;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  private boolean validate() {
    return !StringUtils.isEmpty(this.modelName) && !StringUtils.isEmpty(this.query)
    && this.columnTypes != null && columnTypes.length > 0 && this.columnNames != null && columnNames.length > 0;
  }

  public Domain generate() throws SQLModelGeneratorException {
    return generate(this.modelName, this.connectionName, this.columnTypes, this.columnNames, this.query, this.securityEnabled, this.users,
        this.roles, this.defaultAcls, this.createdBy);
  }

  public Domain generate(String modelName, String connectionName, int[] columnType, String[] columnHeader, String query,
      Boolean securityEnabled, List<String> users, List<String> roles, int defaultAcls, String createdBy)
      throws SQLModelGeneratorException {
      LocaleType locale = new LocaleType(LocalizedString.DEFAULT_LOCALE, Locale.US.getDisplayName());
      if (validate()) {
        SqlPhysicalModel model = new SqlPhysicalModel();
        String modelID = Settings.getBusinessModelIDPrefix() + modelName;
        model.setId(modelID);
        model.setName(new LocalizedString(locale.getCode(), modelName));
        SqlDataSource dataSource = new SqlDataSource();
        dataSource.getAttributes().put("QUOTE_ALL_FIELDS", "Y"); //$NON-NLS-1$ //$NON-NLS-2$
        dataSource.setType(DataSourceType.JNDI);
        if(dbType != null){
          dataSource.setDialectType(dbType);
        }

        dataSource.setDatabaseName(connectionName);
        model.setDatasource(dataSource);

        SqlPhysicalTable table = new SqlPhysicalTable(model);
        table.setId("INLINE_SQL_1"); //$NON-NLS-1$
        model.getPhysicalTables().add(table);
        table.setTargetTableType(TargetTableType.INLINE_SQL);
        table.setTargetTable(query);

        try {
          LogicalModel logicalModel = new LogicalModel();
          logicalModel.setPhysicalModel(model);
          logicalModel.setId("MODEL_1"); //$NON-NLS-1$
          logicalModel.setName(new LocalizedString(locale.getCode(), modelName));
          
          logicalModel.setDescription(new LocalizedString(locale.getCode(), "This is the data model for "
  		        + modelName));          

          Category mainCategory = new Category(logicalModel);
          String categoryID = Settings.getBusinessCategoryIDPrefix() + modelName;
          mainCategory.setId(categoryID);
          mainCategory.setName(new LocalizedString(locale.getCode(), modelName));

          LogicalTable logicalTable = new LogicalTable(logicalModel, table);
          logicalTable.setId("LOGICAL_TABLE_1"); //$NON-NLS-1$

          logicalModel.getLogicalTables().add(logicalTable);
          for (int i = 0; i < columnHeader.length; i++) {
            SqlPhysicalColumn column = new SqlPhysicalColumn(table);

            // should get unique id here

            column.setId(columnHeader[i]);
            column.setTargetColumn(columnHeader[i]);
            // Get the localized string
            column.setName(new LocalizedString(locale.getCode(), columnHeader[i]));
            // Map the SQL Column Type to Metadata Column Type
            column.setDataType(converDataType(columnType[i]));
            String physicalColumnID = Settings.getPhysicalColumnIDPrefix() + "_" + columnHeader[i]; //$NON-NLS-1$
            column.setId(physicalColumnID);
            table.getPhysicalColumns().add(column);

            LogicalColumn logicalColumn = new LogicalColumn();
            String columnID = Settings.getBusinessColumnIDPrefix();
            logicalColumn.setId(columnID + columnHeader[i]);
            logicalColumn.setAggregationType(AggregationType.NONE);

            // the default name of the logical column.
            // this inherits from the physical column.
            // logicalColumn.setName(new LocalizedString(columnHeader[i]));

            logicalColumn.setPhysicalColumn(column);
            logicalColumn.setLogicalTable(logicalTable);

            logicalTable.addLogicalColumn(logicalColumn);

            mainCategory.addLogicalColumn(logicalColumn);
          }

          logicalModel.getCategories().add(mainCategory);

          Domain domain = new Domain();
          domain.addPhysicalModel(model);

          if (getCreatedBy() != null) {
            domain.setProperty("created_by", createdBy); //$NON-NLS-1$
          }

          if (isSecurityEnabled()) {
            Security security = new Security();
            for (String user : users) {
              SecurityOwner owner = new SecurityOwner(OwnerType.USER, user);
              security.putOwnerRights(owner, defaultAcls);
            }
            for (String role : roles) {
              SecurityOwner owner = new SecurityOwner(OwnerType.ROLE, role);
              security.putOwnerRights(owner, defaultAcls);
            }
            logicalModel.setProperty(Concept.SECURITY_PROPERTY, security);
          }

          List<LocaleType> locales = new ArrayList<LocaleType>();
          locales.add(locale);
          domain.setLocales(locales);
          domain.addLogicalModel(logicalModel);
          domain.setId(modelName);
          return domain;

        } catch (Exception e) {
          throw new SQLModelGeneratorException(e);
        }
      } else {
        throw new SQLModelGeneratorException(Messages
            .getErrorString("SQLModelGenerator.ERROR_0001_INPUT_VALIDATION_FAILED")); //$NON-NLS-1$
      }
  }

  private static DataType converDataType(int type)
  {
    switch (type)
    {
	    case Types.FLOAT:
	    case Types.BIT:
	    case Types.DOUBLE:
	    case Types.SMALLINT:
	    case Types.REAL:
	    case Types.DECIMAL:
	    case Types.BIGINT:
	    case Types.INTEGER:
	    case Types.NUMERIC:
	      return DataType.NUMERIC;
	      
	    case Types.BINARY:
	    case Types.CLOB:
	    case Types.BLOB:
	      return DataType.BINARY;
	
	    case Types.BOOLEAN:
	      return DataType.BOOLEAN;
	
	    case Types.DATE:
	      return DataType.DATE;
	
	    case Types.TIMESTAMP:
	      return DataType.DATE;
	
	    case Types.LONGVARCHAR:
	    case Types.VARCHAR:
	      return DataType.STRING;
	
	    default:
	      return DataType.UNKNOWN;
    }
  }

  public void setSecurityEnabled(Boolean securityEnabled) {
    this.securityEnabled = securityEnabled;
  }

  public Boolean isSecurityEnabled() {
    return securityEnabled;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }

  public List<String> getUsers() {
    return users;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setDefaultAcls(int defaultAcls) {
    this.defaultAcls = defaultAcls;
  }

  public int getDefaultAcls() {
    return defaultAcls;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getConnectionName() {
    return connectionName;
  }

  public void setConnectionName(String connectionName) {
    this.connectionName = connectionName;
  }

  public Boolean getSecurityEnabled() {
    return securityEnabled;
  }

  public int[] getColumnTypes() {
    return columnTypes;
  }

  public void setColumnTypes(int[] columnTypes) {
    this.columnTypes = columnTypes;
  }

  public String[] getColumnNames() {
    return columnNames;
  }

  public void setColumnNames(String[] columnNames) {
    this.columnNames = columnNames;
  }
}
