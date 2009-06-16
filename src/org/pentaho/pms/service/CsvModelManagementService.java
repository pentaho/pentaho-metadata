package org.pentaho.pms.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.util.InlineEtlModelGenerator;

public class CsvModelManagementService {

  public Domain generateModel(String modelName, String fileLocation, boolean headerPresent, String enclosure, String delimiter, Boolean securityEnabled, List<String> permittedRoleList, List<String> permittedUserList, String createdBy)
      throws ModelManagementServiceException {
    try {
    InlineEtlModelGenerator generator = new InlineEtlModelGenerator(modelName, fileLocation, headerPresent, enclosure, delimiter);
    generator.setSecurityEnabled(securityEnabled);
    if(securityEnabled) {
      generator.setRoles(permittedRoleList);
      generator.setUsers(permittedUserList);
      generator.setCreatedBy(createdBy);
    }
    return generator.generate();
    } catch (Exception e) {
      throw new ModelManagementServiceException(e);
    }
  }


  public List<List<String>> getDataSample(String fileLocation, boolean headerPresent, String enclosure, String delimiter, int rowLimit) {
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

}
