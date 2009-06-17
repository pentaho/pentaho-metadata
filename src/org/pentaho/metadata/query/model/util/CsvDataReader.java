package org.pentaho.metadata.query.model.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CsvDataReader {
  private String fileLocation;
  private boolean headerPresent;
  private String enclosure;
  private String delimiter;
  private int rowLimit;
  public static int DEFAULT_ROW_LIMIT = 5;
  public static String DEFAULT_DELIMETER = ",";
  public static String DEFAULT_ENCLOSURE = "/";
  
  public CsvDataReader() {
    
  }
  public CsvDataReader(String fileLocation, boolean headerPresent, String enclosure, String delimiter, int rowLimit) {
    this.fileLocation = fileLocation;
    this.delimiter = delimiter;
    this.headerPresent = headerPresent;
    this.enclosure = enclosure;
    this.rowLimit = rowLimit;
  }
  public CsvDataReader(String fileLocation, boolean headerPresent) {
    this.fileLocation = fileLocation;
    this.delimiter = DEFAULT_DELIMETER;
    this.headerPresent = headerPresent;
    this.enclosure = DEFAULT_ENCLOSURE;
    this.rowLimit = DEFAULT_ROW_LIMIT;
  }  
  public List<List<String>> getData() {
    String line = null;
    int row = 0;
    List<List<String>> dataSample = new ArrayList<List<String>>(rowLimit);
    File file = new File(fileLocation);
    BufferedReader bufRdr = null;
    try {
      bufRdr = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //read each line of text file
    try {
      while((line = bufRdr.readLine()) != null && row < rowLimit)
      {
        StringTokenizer st = new StringTokenizer(line,delimiter);
        List<String> rowData = new ArrayList<String>();
        while (st.hasMoreTokens())
        {
          //get next token and store it in the list
          rowData.add(st.nextToken());
        }
        if(headerPresent && row != 0 || !headerPresent) {
          dataSample.add(rowData);  
        }
        row++;
      }
      //close the file
      bufRdr.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return dataSample;
  }
  
  public List<String> getColumnData(String columnName) {
    String line = null;
    int row = 0;
    int column = 0;
    int columnNumber = -1;
    List<String> dataSample = new ArrayList<String>(rowLimit);
    File file = new File(fileLocation);
    BufferedReader bufRdr = null;
    try {
      bufRdr = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //read each line of text file
    try {
      while((line = bufRdr.readLine()) != null && row < rowLimit)
      {
        column = 0;
        StringTokenizer st = new StringTokenizer(line,delimiter);
        List<String> headerRowData = new ArrayList<String>();
        if(row == 0) {
          while (st.hasMoreTokens())
          {
            //get next token and store it in the list
            headerRowData.add(st.nextToken());
          }
          columnNumber = headerRowData.indexOf(columnName);
        } else {
          while (st.hasMoreTokens())
          {
            if(column == columnNumber) {
              //get next token and store it in the list
              dataSample.add(st.nextToken());
              break;
            }
            st.nextToken();
            column++;
          }
        }
        row++;
      }
      //close the file
      bufRdr.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return dataSample;    
  }
  
  public List<String> getColumnData(int columnNumber) {
    String line = null;
    int row = 0;
    int column = 0;
    List<String> dataSample = new ArrayList<String>(rowLimit);
    File file = new File(fileLocation);
    BufferedReader bufRdr = null;
    try {
      bufRdr = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //read each line of text file
    try {
      while((line = bufRdr.readLine()) != null && row < rowLimit)
      { 
        column = 0;
        StringTokenizer st = new StringTokenizer(line,delimiter);
        List<String> headerRowData = new ArrayList<String>();
        if(row == 0 && headerPresent) {
          while (st.hasMoreTokens())
          {
            //get next token and store it in the list
            headerRowData.add(st.nextToken());
          }
        } else {
          while (st.hasMoreTokens())
          {
            //get next token and store it in the list
            if(column == columnNumber) {
              dataSample.add(st.nextToken());
              break;
            }
            st.nextToken();
            column++;
          }
        }
        row++;
      }
      //close the file
      bufRdr.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return dataSample; 
  }
  public String getFileLocation() {
    return fileLocation;
  }
  public void setFileLocation(String fileLocation) {
    this.fileLocation = fileLocation;
  }
  public boolean isHeaderPresent() {
    return headerPresent;
  }
  public void setHeaderPresent(boolean headerPresent) {
    this.headerPresent = headerPresent;
  }
  public String getEnclosure() {
    return enclosure;
  }
  public void setEnclosure(String enclosure) {
    this.enclosure = enclosure;
  }
  public String getDelimiter() {
    return delimiter;
  }
  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }
  public int getRowLimit() {
    return rowLimit;
  }
  public void setRowLimit(int rowLimit) {
    this.rowLimit = rowLimit;
  }
}
