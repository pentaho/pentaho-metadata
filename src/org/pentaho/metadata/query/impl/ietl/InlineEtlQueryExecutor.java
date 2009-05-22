/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
package org.pentaho.metadata.query.impl.ietl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.commons.connection.memory.MemoryResultSet;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowListener;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.groupby.GroupByMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;
import org.pentaho.di.trans.steps.sort.SortRowsMeta;
import org.pentaho.metadata.model.InlineEtlPhysicalColumn;
import org.pentaho.metadata.model.InlineEtlPhysicalModel;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryModelMetaData;

/**
 * This query executor generates an inline ETL result set, based on the inline etl 
 * physical model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class InlineEtlQueryExecutor {
  
  String transformLocation = "res:org/pentaho/metadata/query/impl/ietl/";
  
  protected String getTransformLocation() {
    return transformLocation;
  }
  
  public void setTransformLocation(String transformLocation) {
    this.transformLocation = transformLocation;
  }
  
  private List<Selection> getAllSelections(Query query) {
    return query.getSelections();
  }
  
  public IPentahoResultSet executeQuery(Query query) throws Exception {
    // step one, execute a transformation into a result set

    // group by?
    int groupBys = 0;
    List<Selection> allSelections = getAllSelections(query);
    for (Selection selection : allSelections) {
      if (selection.getActiveAggregationType() != null && selection.getActiveAggregationType() != AggregationType.NONE) {
        groupBys++;
      }
    }

    String fileAddress = getTransformLocation() + "inlinecsv.ktr";
    if (groupBys > 0) {
      fileAddress = getTransformLocation() + "inlinecsv_groupby.ktr";  
    }
    TransMeta transMeta = new TransMeta(fileAddress, null, true);
    transMeta.setFilename(fileAddress);
    
    //
    // CSV FILE LOCATION
    //

    InlineEtlPhysicalModel physicalModel = (InlineEtlPhysicalModel)query.getSelections().get(0).getLogicalColumn().getPhysicalColumn().getPhysicalTable().getPhysicalModel();
    
    CsvInputMeta csvinput = (CsvInputMeta)getStepMeta(transMeta, "CSV file input").getStepMetaInterface();

    
    // the file name might need to be translated to the correct location here
    
    csvinput.setFilename(physicalModel.getFileLocation());
    
    csvinput.setDelimiter(physicalModel.getDelimiter());
    csvinput.setEnclosure(physicalModel.getEnclosure());
    csvinput.setHeaderPresent(physicalModel.getHeaderPresent());
    
    //
    // SELECT
    //
    
    StepMeta selections = getStepMeta(transMeta, "Select values");
    Map<String, String> fieldNameMap = new HashMap<String, String>();
    
    SelectValuesMeta selectVals = (SelectValuesMeta)selections.getStepMetaInterface();
    selectVals.allocate(query.getSelections().size(), 0, 0);
    for (int i = 0; i < query.getSelections().size(); i++) {
      // 
      Selection selection = query.getSelections().get(i);
      String fieldName = ((InlineEtlPhysicalColumn)selection.getLogicalColumn().getPhysicalColumn()).getFieldName();
      fieldNameMap.put(fieldName.toUpperCase(), selection.getLogicalColumn().getId());
      selectVals.getSelectName()[i] = fieldName;
    }

    // 
    // SORT
    //
    
    // TODO: group by impacts sorting
    
    StepMeta sort = getStepMeta(transMeta, "Sort rows");
    
    SortRowsMeta sortRows = (SortRowsMeta)sort.getStepMetaInterface();
    
    sortRows.allocate(query.getSelections().size());
    
    List<Selection> unordered = new ArrayList<Selection>();
    for (Selection selection : query.getSelections()) {
      boolean found = false;
      for (Order order : query.getOrders()) {
        if (selection.equals(order.getSelection())) {
          found = true;
        }
      }
      if (!found) {
        unordered.add(selection);
      }
    }
    
    int c = 0;
    for (Order order : query.getOrders()) {
      String fieldName = ((InlineEtlPhysicalColumn)order.getSelection().getLogicalColumn().getPhysicalColumn()).getFieldName();
      sortRows.getFieldName()[c] = fieldName;
      sortRows.getAscending()[c] = (order.getType() == Order.Type.ASC);
      sortRows.getCaseSensitive()[c] = false;
      c++;
    }
    for (Selection selection : unordered) {
      String fieldName = ((InlineEtlPhysicalColumn)selection.getLogicalColumn().getPhysicalColumn()).getFieldName();
      sortRows.getFieldName()[c] = fieldName;
      sortRows.getAscending()[c] = true;
      sortRows.getCaseSensitive()[c] = false;
      c++;
    }
    
    //
    // GROUP BY
    //
    
    if (groupBys > 0) {
      StepMeta group = getStepMeta(transMeta, "Group by");
      GroupByMeta groupStep = (GroupByMeta)group.getStepMetaInterface();
      groupStep.allocate(allSelections.size() - groupBys, groupBys);
      
      c = 0;
      for (Selection selection : allSelections) {
        if (selection.getActiveAggregationType() == AggregationType.NONE) {
          String fieldName = ((InlineEtlPhysicalColumn)selection.getLogicalColumn().getPhysicalColumn()).getFieldName();
          groupStep.getGroupField()[c] = fieldName;
          c++;
        }
      }
      c = 0;
      for (Selection selection : allSelections) {
        if (selection.getActiveAggregationType() != AggregationType.NONE) {
          String fieldName = ((InlineEtlPhysicalColumn)selection.getLogicalColumn().getPhysicalColumn()).getFieldName();
          groupStep.getAggregateField()[c] = fieldName;    
          groupStep.getSubjectField()[c] = fieldName;    
          groupStep.getAggregateType()[c] = selection.getActiveAggregationType().ordinal();
          groupStep.getValueField()[c] = null;
          c++;
        }
      }
    }
    
    InlineEtlRowListener listener = new InlineEtlRowListener();
    Trans trans = new Trans(transMeta);
    trans.prepareExecution(transMeta.getArguments());
    listener.registerAsStepListener(trans, query, fieldNameMap);
    
    trans.startThreads();
    trans.waitUntilFinished();
    trans.endProcessing("end"); //$NON-NLS-1$
    
    return listener.results;
  }
  
  private StepMeta getStepMeta(TransMeta meta, String name) {
    for (StepMeta step : meta.getSteps()) {
      if (name.equals(step.getName())) {
        return step;
      }
    }
    return null;
  }
  
  private static class InlineEtlRowListener implements RowListener {
    
    private MemoryResultSet results;
    
    private MemoryResultSet errorResults;

    private boolean registerAsStepListener(Trans trans, Query query, Map fieldMap) throws Exception {
      boolean success = false;
//      try{
        if(trans != null){
          List<StepMetaDataCombi> stepList = trans.getSteps();
          // assume the last step
          for (StepMetaDataCombi step : stepList) {
            if (!"Unique rows".equals(step.stepname)) {
              continue;
            }
            System.out.println("STEP NAME: " + step.stepname);
            RowMetaInterface row = trans.getTransMeta().getStepFields(step.stepMeta); // step.stepname?
            // create the metadata that the Pentaho result sets need
            String fieldNames[] = row.getFieldNames();
            
            String columns[][] = new String[1][fieldNames.length];
            for (int column = 0; column < fieldNames.length; column++) {
              columns[0][column] = fieldNames[column];
            }
            
            //TODO: build valid metadata 
            
            QueryModelMetaData metadata = new QueryModelMetaData(fieldMap, columns, null, query.getSelections()); 
            
            // MemoryMetaData metaData = new MemoryMetaData(columns, null);
            results = new MemoryResultSet(metadata);
            errorResults = new MemoryResultSet(metadata);
            // add ourself as a row listener
            step.step.addRowListener(this);
            success = true;
          }
        }
//      } catch (Exception e){
//        throw new KettleComponentException(Messages.getString("Kettle.ERROR_0027_ERROR_INIT_STEP",stepName), e); //$NON-NLS-1$
//      }
      
      return success;
    }
    
    
    public void rowReadEvent(final RowMetaInterface row, final Object[] values) {
    }

    public void rowWrittenEvent(final RowMetaInterface rowMeta, final Object[] row) throws KettleStepException {
      processRow(results, rowMeta, row);
    }

    public void errorRowWrittenEvent(final RowMetaInterface rowMeta, final Object[] row) throws KettleStepException {
      processRow(errorResults, rowMeta, row);
    }
    
    public void processRow(MemoryResultSet memResults, final RowMetaInterface rowMeta, final Object[] row) throws KettleStepException {
      if (memResults == null) {
        return;
      }
      try {
        Object pentahoRow[] = new Object[memResults.getColumnCount()];
        for (int columnNo = 0; columnNo < memResults.getColumnCount(); columnNo++) {
          ValueMetaInterface valueMeta = rowMeta.getValueMeta(columnNo);

          switch (valueMeta.getType()) {
            case ValueMetaInterface.TYPE_BIGNUMBER:
              pentahoRow[columnNo] = rowMeta.getBigNumber(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_BOOLEAN:
              pentahoRow[columnNo] = rowMeta.getBoolean(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_DATE:
              pentahoRow[columnNo] = rowMeta.getDate(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_INTEGER:
              pentahoRow[columnNo] = rowMeta.getInteger(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_NONE:
              pentahoRow[columnNo] = rowMeta.getString(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_NUMBER:
              pentahoRow[columnNo] = rowMeta.getNumber(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_STRING:
              pentahoRow[columnNo] = rowMeta.getString(row, columnNo);
              break;
            default:
              pentahoRow[columnNo] = rowMeta.getString(row, columnNo);
          }
        }
        memResults.addRow(pentahoRow);
      } catch (KettleValueException e) {
        throw new KettleStepException(e);
      }
    }

    
  }
}
