package org.pentaho.metadata.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Props;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.pms.util.Settings;

public class SQLModelGenerator {
  String modelName;
  Connection connection;
  String query;
  
  public SQLModelGenerator() {
    super();
    if(!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
  }

  public SQLModelGenerator(String modelName, Connection connection, String query) {
    if(!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
    this.query = query;
    this.connection = connection;
    this.modelName = modelName;
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
    return generate(this.modelName, this.connection, this.query);
  }
  
  public Domain generate(String modelName, Connection connection, String query) throws SQLModelGeneratorException{
    if(validate()) {
    SqlPhysicalModel model = new SqlPhysicalModel();
    String modelID = Settings.getBusinessModelIDPrefix()+ modelName;
    model.setId(modelID);
    model.setName(new LocalizedString(modelName));
    model.setDescription(new LocalizedString("A Description of the Model"));
    model.setDatasource(modelName);
    SqlPhysicalTable table = new SqlPhysicalTable(model);
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable(query);
    
    String[] columnHeader = null;
    String[] columnType = null;
      Statement stmt = null;
      ResultSet rs = null;
      try {

        if (!StringUtils.isEmpty(query)) {
          stmt = connection.createStatement();
          stmt.setMaxRows(5);
          rs = stmt.executeQuery(query);
          ResultSetMetaData metadata = rs.getMetaData();
          columnHeader = new String[metadata.getColumnCount()];
          columnType = new String[metadata.getColumnCount()];
          columnHeader = getColumnNames(metadata);
          columnType = getColumnTypesNames(metadata);
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
      Category mainCategory = new Category();
      LogicalModel logicalModel = new LogicalModel();
      logicalModel.setId("MODEL_1");
      logicalModel.setName(new LocalizedString(modelName));
      LogicalTable logicalTable = new LogicalTable();
      
      for(int i=0;i<columnHeader.length;i++) {
        SqlPhysicalColumn column = new SqlPhysicalColumn(table);
        
        // should get unique id here
        
        column.setId(columnHeader[i]);
        column.setTargetColumn(columnHeader[i]);
        // Get the localized string
        column.setName(new LocalizedString(columnHeader[i]));
        // Map the SQL Column Type to Metadata Column Type
        column.setDataType(converSQLToMetadataColumnType(columnType[i]));
        String physicalColumnID = Settings.getPhysicalColumnIDPrefix() + "_" + columnHeader[i];
        column.setId(physicalColumnID);
        table.getPhysicalColumns().add(column);
                
        logicalTable.setPhysicalTable(table);
        logicalTable.setId("LOGICAL_TABLE_1");
        logicalModel.getLogicalTables().add(logicalTable);
        
        LogicalColumn logicalColumn = new LogicalColumn();
        String columnID = Settings.getBusinessColumnIDPrefix();
        logicalColumn.setId(columnID + "_" +columnHeader[i]);
        
        // the default name of the logical column.
        logicalColumn.setName(new LocalizedString(columnHeader[i]));
        
        logicalColumn.setPhysicalColumn(column);
        logicalColumn.setLogicalTable(logicalTable);
        
        logicalTable.addLogicalColumn(logicalColumn);
        
        mainCategory.addLogicalColumn(logicalColumn);
      }
      String categoryID= Settings.getBusinessCategoryIDPrefix()+ modelName;
      mainCategory.setId(categoryID);
      mainCategory.setName(new LocalizedString(modelName));
      
      logicalModel.getCategories().add(mainCategory);
      
      Domain domain = new Domain();
      domain.addPhysicalModel(model);
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
 
  private DataType converSQLToMetadataColumnType(String sqlColumnType) {
    if(sqlColumnType.equals("VARCHAR")) {
      return DataType.STRING;
    } else if(sqlColumnType.equals("BOOLEAN")) {
      return DataType.BOOLEAN;
    } else if(sqlColumnType.equals("INTEGER")) {
      return DataType.NUMERIC;
    } else if(sqlColumnType.equals("DATE")) {
      return DataType.DATE;
    } else if(sqlColumnType.equals("BINARY")) {
      return DataType.BINARY;
    } else {
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
}
