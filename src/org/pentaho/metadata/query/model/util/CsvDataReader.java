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
package org.pentaho.metadata.query.model.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.steps.textfileinput.TextFileInput;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputMeta;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;

public class CsvDataReader {
  
  private static final Log logger = LogFactory.getLog(QueryXmlHelper.class);
  
  private static int DEFAULT_ROW_LIMIT = 5;
  private static String DEFAULT_DELIMETER = ","; //$NON-NLS-1$
  private static String DEFAULT_ENCLOSURE = "/"; //$NON-NLS-1$
  
  private String fileLocation;
  private boolean headerPresent;
  private String enclosure;
  private String delimiter;
  private int rowLimit;
  
  // generated values
  private List<String> header;
  private List<List<String>> data;
  private int columnCount;
  
  public CsvDataReader(String fileLocation, boolean headerPresent, String delimiter, String enclosure, int rowLimit) {
    this.fileLocation = fileLocation;
    this.headerPresent = headerPresent;
    this.delimiter = delimiter;
    this.enclosure = enclosure;
    this.rowLimit = rowLimit;
  }
  
  public List<List<String>> loadData() {
    String line = null;
    int row = 0;
    List<List<String>> dataSample = new ArrayList<List<String>>(rowLimit);
    InputStreamReader reader = null;
    try {
      InputStream inputStream = KettleVFS.getInputStream(fileLocation);
      reader = new InputStreamReader(inputStream);

      //read each line of text file
      StringBuilder stringBuilder = new StringBuilder(1000);  
      line = TextFileInput.getLine(null, reader, TextFileInputMeta.FILE_FORMAT_MIXED, stringBuilder);
      
      while(line != null && row < rowLimit) {
        CSVTokenizer csvt = new CSVTokenizer(line, delimiter, enclosure, false);
        List<String> rowData = new ArrayList<String>();
        int count = 0;
        
        while (csvt.hasMoreTokens()) {
          //get next token and store it in the list
          rowData.add(csvt.nextToken());
          count++;
        }
        
        if (columnCount < count) {
          columnCount = count;
        }
        
        if (headerPresent && row == 0) {
          header = rowData;
        } else {
          dataSample.add(rowData);  
        }
        line = TextFileInput.getLine(null, reader, TextFileInputMeta.FILE_FORMAT_MIXED, stringBuilder);
        row++;
      }

    } catch (Exception e) {
      logger.error(Messages.getString("CsvDataReader.ERROR_0001_Failed"), e); //$NON-NLS-1$
    } finally {
      
      //close the file
      try {
        if (reader != null) reader.close();
      } catch (Exception e) {
        // ignore 
      }
    }
    data = dataSample;
    return dataSample;
  }
  
  public List<String> getHeader() {
    return header;
  }
  
  public List<String> getColumnData(int columnNumber) {
    List<String> dataSample = new ArrayList<String>(rowLimit);
    for (List<String> row : data) {
      if(row.size() > columnNumber) {
        dataSample.add(row.get(columnNumber));
      }
    }
    return dataSample;
  }
  
  public int getColumnCount() {
    return columnCount;
  }

}
