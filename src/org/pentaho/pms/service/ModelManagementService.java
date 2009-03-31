package org.pentaho.pms.service;

import java.sql.Connection;
import java.sql.Driver;
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

public class ModelManagementService implements IModelManagementService {

  public Category createCategory(IDataSource dataSource, String businessViewName, List<Column> businessColumns,
      Map columnCrossRef) {
    // TODO Auto-generated method stub
    return null;
  }

  public List<Column> getColumns(IDataSource dataSource) {
    //TODO: we will not always assume the datasource is sql, but for now...
    return getColumnsFromSqlSource((SQLDataSource)dataSource);
  }

  public List<List<String>> getDataSample(IDataSource dataSource, int rows) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private List<Column> getColumnsFromSqlSource(SQLDataSource sqlDataSource) {
    List<Column> columns = new ArrayList<Column>();

    String driver = sqlDataSource.getDbMeta().getDriverClass();
    String url;
    try {
      url = sqlDataSource.getDbMeta().getURL();
      System.err.println("url is "+url);
    } catch (KettleDatabaseException e1) {
      e1.printStackTrace();
      return null;
    }
    String username = sqlDataSource.getDbMeta().getUsername();
    String password = sqlDataSource.getDbMeta().getPassword();

    Connection conn = null;
    Statement stmt = null;
    ResultSet results = null;

    try {
      try {
        DriverManager.registerDriver((Driver)Class.forName(driver).newInstance());
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
      conn = DriverManager.getConnection(url, username, password);
      stmt = conn.createStatement();
      results = stmt.executeQuery(sqlDataSource.getColumnFilterExpression());
      ResultSetMetaData resultMeta = results.getMetaData();
      for (int i = 1; i <= resultMeta.getColumnCount(); i++) {
        Column col = new Column();
        col.setName(resultMeta.getColumnName(i));
        col.setDataType(resultMeta.getColumnTypeName(i));
        columns.add(col);
      }

    } catch (SQLException e) {
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
}