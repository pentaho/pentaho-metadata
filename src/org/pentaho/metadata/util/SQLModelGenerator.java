package org.pentaho.metadata.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Props;
import org.pentaho.metadata.messages.LocaleHelper;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.SqlDataSource.DataSourceType;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.security.SecurityOwner.OwnerType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.pms.util.Settings;

public class SQLModelGenerator {
  String modelName;
  Connection connection;
  String query;
  String connectionName;
  Boolean securityEnabled;
  List<String> users;
  List<String> roles;
  int defaultAcls;
  String createdBy;
  
  public SQLModelGenerator() {
    super();
    if(!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
  }

  public SQLModelGenerator(String modelName, String connectionName, Connection connection, String query,
      Boolean securityEnabled, List<String> users, List<String> roles, int defaultAcls, String createdBy) {
    if(!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
    this.query = query;
    this.connectionName = connectionName;
    this.connection = connection;
    this.modelName = modelName;
    this.securityEnabled = securityEnabled;
    this.users = users;
    this.roles = roles;
    this.defaultAcls = defaultAcls;
    this.createdBy = createdBy;
  }
 
  public Connection getConnection() {
    return connection;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
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
    return !StringUtils.isEmpty(this.modelName) && !StringUtils.isEmpty(this.query) && this.connection != null;  
  }
  public Domain generate() throws SQLModelGeneratorException {
    return generate(this.modelName, this.connectionName, this.connection, this.query, this.securityEnabled, this.users, this.roles, this.defaultAcls, this.createdBy);
  }
  
  public Domain generate(String modelName, String connectionName, Connection connection, String query,
      Boolean securityEnabled, List<String> users, List<String> roles, int defaultAcls, String createdBy) throws SQLModelGeneratorException {
    
    LocaleType locale = new LocaleType(LocaleHelper.getLocale().toString(), LocaleHelper.getLocale().getDisplayName());
    
    if(validate()) {
      SqlPhysicalModel model = new SqlPhysicalModel();
      String modelID = Settings.getBusinessModelIDPrefix()+ modelName;
      model.setId(modelID);
      model.setName(new LocalizedString(locale.getCode(), modelName));
      SqlDataSource dataSource = new SqlDataSource();
      dataSource.setType(DataSourceType.JNDI);
      dataSource.setDatabaseName(connectionName);
      model.setDatasource(dataSource);
      SqlPhysicalTable table = new SqlPhysicalTable(model);
      table.setId("INLINE_SQL_1");
      model.getPhysicalTables().add(table);
      table.setTargetTableType(TargetTableType.INLINE_SQL);
      table.setTargetTable(query);
      
      String[] columnHeader = null;
      //String[] columnType = null;
      int[] columnType = null;
      Statement stmt = null;
      ResultSet rs = null;
      try {

        if (!StringUtils.isEmpty(query)) {
          stmt = connection.createStatement();
          stmt.setMaxRows(5);
          rs = stmt.executeQuery(query);
          ResultSetMetaData metadata = rs.getMetaData();
          columnHeader = new String[metadata.getColumnCount()];
          //columnType = new String[metadata.getColumnCount()];
          columnType = new int[metadata.getColumnCount()];
          columnHeader = getColumnNames(metadata);
          columnType = getColumnTypes(metadata);
        } else {
          throw new SQLModelGeneratorException("Query not valid"); //$NON-NLS-1$
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new SQLModelGeneratorException("Query validation failed", e); //$NON-NLS-1$
      } finally {
        try {
          if (rs != null) {
            rs.close();
          }
          if (stmt != null) {
            stmt.close();
          }
          if (connection != null) {
            connection.close();
          }
        } catch (SQLException e) {
          throw new SQLModelGeneratorException(e);
        }
      }
      
      try {
        LogicalModel logicalModel = new LogicalModel();
        logicalModel.setId("MODEL_1");
        logicalModel.setName(new LocalizedString(locale.getCode(), modelName));

        Category mainCategory = new Category(logicalModel);
        String categoryID= Settings.getBusinessCategoryIDPrefix()+ modelName;
        mainCategory.setId(categoryID);
        mainCategory.setName(new LocalizedString(locale.getCode(), modelName));
  
        LogicalTable logicalTable = new LogicalTable(logicalModel, table);
        logicalTable.setId("LOGICAL_TABLE_1");
        
        logicalModel.getLogicalTables().add(logicalTable);
        
        for(int i=0;i<columnHeader.length;i++) {
          SqlPhysicalColumn column = new SqlPhysicalColumn(table);
          
          // should get unique id here
          
          column.setId(columnHeader[i]);
          column.setTargetColumn(columnHeader[i]);
          // Get the localized string
          column.setName(new LocalizedString(locale.getCode(), columnHeader[i]));
          // Map the SQL Column Type to Metadata Column Type
          column.setDataType(converDataType(columnType[i]));
          String physicalColumnID = Settings.getPhysicalColumnIDPrefix() + "_" + columnHeader[i];
          column.setId(physicalColumnID);
          table.getPhysicalColumns().add(column);
                  
          LogicalColumn logicalColumn = new LogicalColumn();
          String columnID = Settings.getBusinessColumnIDPrefix();
          logicalColumn.setId(columnID + columnHeader[i]);
          
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
          domain.setProperty("created_by", createdBy);
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

      } catch(Exception e) {
        throw new SQLModelGeneratorException(e);
      }
    } else {
      throw new SQLModelGeneratorException("Input Validation Failed");
    }
  }
 
  private static DataType converDataType(int type)
  {
    switch (type)
    {
    case Types.BIGINT:
    case Types.INTEGER:
    case Types.NUMERIC:
      return DataType.NUMERIC;
    
    case Types.BINARY:
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
  
  /**
   * The following method returns an array of String(java.sql.Types) containing the column types for
   * a given ResultSetMetaData object.
   */
  private String[] getColumnTypesNames(ResultSetMetaData resultSetMetaData) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    String[] columnTypes = new String[columnCount];

    for(int colIndex=1; colIndex<=columnCount; colIndex++){
      columnTypes[colIndex-1] = resultSetMetaData.getColumnTypeName(colIndex);
    }

    return columnTypes;
  }
  
  /**
   * The following method returns an array of strings containing the column names for
   * a given ResultSetMetaData object.
   */
  public String[] getColumnNames(ResultSetMetaData resultSetMetaData) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    String columnNames[] = new String[columnCount];

    for(int colIndex=1; colIndex<=columnCount; colIndex++){
      columnNames[colIndex-1] = resultSetMetaData.getColumnName(colIndex);
    }

    return columnNames;
  }
  
  /**
   * The following method returns an array of int(java.sql.Types) containing the column types for
   * a given ResultSetMetaData object.
   */
  public int[] getColumnTypes(ResultSetMetaData resultSetMetaData) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    int[] columnTypes = new int[columnCount];

    for(int colIndex=1; colIndex<=columnCount; colIndex++){
      columnTypes[colIndex-1] = resultSetMetaData.getColumnType(colIndex);
    }

    return columnTypes;
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

}
