package org.pentaho.pms.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.pms.schema.v3.model.Category;
import org.pentaho.pms.schema.v3.model.Column;
import org.pentaho.pms.schema.v3.physical.IDataSource;
import org.pentaho.pms.schema.v3.physical.SQLDataSource;

public class JDBCModelManagementService implements IModelManagementService {

  public Category createCategory(IDataSource dataSource, String businessViewName, List<Column> businessColumns,
      Map columnCrossRef) {
    // TODO Auto-generated method stub
    return null;
  }

  public List<Column> getColumns(IDataSource dataSource) {
    SQLDataSource sqlDataSource = (SQLDataSource)dataSource;
    List<Column> columns = new ArrayList<Column>();

    Connection conn = null;
    
    Statement stmt = null;
    ResultSet results = null;

    try {
      conn = getJdbcConnection(sqlDataSource);
      stmt = conn.createStatement();
      results = stmt.executeQuery(sqlDataSource.getColumnFilterExpression());
      ResultSetMetaData resultMeta = results.getMetaData();
      for (int i = 1; i <= resultMeta.getColumnCount(); i++) {
        Column col = new Column();
        col.setName(resultMeta.getColumnName(i));
        col.setDataType(resultMeta.getColumnTypeName(i));
        columns.add(col);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (results != null)
          results.close();
        if (stmt != null)
          stmt.close();
        if (conn != null)
          conn.close();
      } catch (SQLException e) {
      }
    }
    return columns;
  }

  public List<List<String>> getDataSample(IDataSource dataSource, int rows) {
    //TODO: need to implement a dialect-specific row limit instead of hacking
    //a limit into the returned data sample
    List<List<String>> dataSample = new ArrayList<List<String>>(rows);
    
    SQLDataSource sqlDataSource = (SQLDataSource)dataSource;
    Connection conn = null;
    Statement stmt = null;
    ResultSet results = null;

    try {
      conn = getJdbcConnection(sqlDataSource);
      stmt = conn.createStatement();
      results = stmt.executeQuery(sqlDataSource.getColumnFilterExpression());
      
      int colCount = results.getMetaData().getColumnCount();
      //loop through rows
      int rowIdx = 0;
      while (results.next()) {
        if(rowIdx >= rows) {
          break;
        }
        dataSample.add(new ArrayList<String>(colCount));
        //loop through columns
        for (int colIdx = 1; colIdx <= colCount; colIdx++) {
          dataSample.get(rowIdx).add(results.getString(colIdx));
        }
        rowIdx++;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (results != null)
          results.close();
        if (stmt != null)
          stmt.close();
        if (conn != null)
          conn.close();
      } catch (SQLException e) {
      }
    }
    return dataSample;

  }

  private void loadDriver(SQLDataSource sqlDataSource) {
    String driver = sqlDataSource.getDbMeta().getDriverClass();
    try {
      Class.forName(driver);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //TODO: we should probably be using a connection factory with pooling of some sort
  //instead of direct jdbc
  private Connection getJdbcConnection(SQLDataSource sqlDataSource) throws Exception {
    loadDriver(sqlDataSource);
    String url;
    try {
      url = sqlDataSource.getDbMeta().getURL();
      System.err.println("url is " + url);
    } catch (KettleDatabaseException e1) {
      e1.printStackTrace();
      return null;
    }
    String username = sqlDataSource.getDbMeta().getUsername();
    String password = sqlDataSource.getDbMeta().getPassword();

    Connection conn = null;
    Statement stmt = null;
    ResultSet results = null;

    return DriverManager.getConnection(url, username, password);
  }
}