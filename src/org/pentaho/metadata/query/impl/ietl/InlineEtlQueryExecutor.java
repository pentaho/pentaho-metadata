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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.commons.connection.memory.MemoryResultSet;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowListener;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.filterrows.FilterRowsMeta;
import org.pentaho.di.trans.steps.formula.FormulaMeta;
import org.pentaho.di.trans.steps.formula.FormulaMetaFunction;
import org.pentaho.di.trans.steps.groupby.GroupByMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;
import org.pentaho.di.trans.steps.sort.SortRowsMeta;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.InlineEtlPhysicalColumn;
import org.pentaho.metadata.model.InlineEtlPhysicalModel;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
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
  
  private static final Log logger = LogFactory.getLog(InlineEtlQueryExecutor.class);
  
  String transformLocation = "res:org/pentaho/metadata/query/impl/ietl/";
  
  protected String getTransformLocation() {
    return transformLocation;
  }
  
  public void setTransformLocation(String transformLocation) {
    this.transformLocation = transformLocation;
  }
  
  private List<Selection> getAllSelections(Query query, List<QueryConstraint> queryConstraints) {
    
    List<Selection> allSelections = new ArrayList<Selection>();
    
    // selections
    
    allSelections.addAll(query.getSelections());
    
    // orders
    
    for (Order order : query.getOrders()) {
      if (!allSelections.contains(order.getSelection())) {
        allSelections.add(order.getSelection());
      }
    }
    
    // constraints
    for (QueryConstraint constraint : queryConstraints) {
      for (Selection selection : constraint.selections) {
        if (!allSelections.contains(selection)) {
          allSelections.add(selection);
        }        
      }
    }
    return allSelections;
  }
  
  private static class QueryConstraint {
    boolean groupby = false;
    List<Selection> selections = new ArrayList<Selection>();
    String formula;
    Constraint orig;
    // will probably have to handle group by somehow here
  }
  
  public List<QueryConstraint> parseConstraints(Query query) {
    List<QueryConstraint> constraints = new ArrayList<QueryConstraint>();
    for (Constraint constraint : query.getConstraints()) {
      QueryConstraint qc = new QueryConstraint();
      qc.orig = constraint;
      // parse out all the [] fields
      Pattern p = Pattern.compile("\\[([^\\]]*)\\]");
      Matcher m = p.matcher(constraint.getFormula());
      StringBuffer sb = new StringBuffer();
      while (m.find()) {
        String match = m.group(1);
        String seg[] = match.split("\\.");
        Category cat = query.getLogicalModel().findCategory(seg[0]);
        LogicalColumn col = cat.findLogicalColumn(seg[1]);
        if (col == null) {
          logger.error("FAILED TO LOCATE: " + seg[0] + "." + seg[1]);
        }
        String fieldName = (String)col.getProperty(InlineEtlPhysicalColumn.FIELD_NAME);
        AggregationType agg = null;
        if (seg.length > 2) {
           agg = AggregationType.valueOf(seg[2].toUpperCase());
        }
        Selection sel = new Selection(cat, col, agg);
        if (!qc.selections.contains(sel)) {
          qc.selections.add(sel);
          if (sel.getActiveAggregationType() != null && sel.getActiveAggregationType() != AggregationType.NONE) {
            qc.groupby = true;
          }
          
        }
        
        // this may be different in the group by context.
        
        m.appendReplacement(sb, "[" + fieldName + "]");
      }
      m.appendTail(sb);
      qc.formula = sb.toString();
      logger.debug("PARSED FORMULA: " + qc.formula);
      constraints.add(qc);
    }
    return constraints;
  }
  
  public IPentahoResultSet executeQuery(Query query) throws Exception {
    // step one, execute a transformation into a result set

    // group by?
    int groupBys = 0;
    List<QueryConstraint> queryConstraints = parseConstraints(query);
    
    List<Selection> allSelections = getAllSelections(query, queryConstraints);
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
    // CSV FILE LOCATION AND FIELDS
    //

    InlineEtlPhysicalModel physicalModel = (InlineEtlPhysicalModel)query.getSelections().get(0).getLogicalColumn().getPhysicalColumn().getPhysicalTable().getPhysicalModel();
    
    CsvInputMeta csvinput = (CsvInputMeta)getStepMeta(transMeta, "CSV file input").getStepMetaInterface();

    
    // the file name might need to be translated to the correct location here
    
    csvinput.setFilename(physicalModel.getFileLocation());
    
    csvinput.setDelimiter(physicalModel.getDelimiter());
    csvinput.setEnclosure(physicalModel.getEnclosure());
    csvinput.setHeaderPresent(physicalModel.getHeaderPresent());
    
    // update fields
    
    LogicalTable table = query.getLogicalModel().getLogicalTables().get(0);
    
    csvinput.allocate(table.getLogicalColumns().size());
    
    for (int i = 0; i < csvinput.getInputFields().length; i++) {
      // Update csv input
      
      LogicalColumn col = table.getLogicalColumns().get(i);
      csvinput.getInputFields()[i] = new TextFileInputField();
      String fieldName = (String)col.getProperty(InlineEtlPhysicalColumn.FIELD_NAME);
      logger.debug("FROM CSV: " + fieldName);
      csvinput.getInputFields()[i].setName(fieldName);
      csvinput.getInputFields()[i].setType(convertType(col.getDataType()));
    }
    
    //
    // SELECT
    //
    
    StepMeta selections = getStepMeta(transMeta, "Select values");

    
    SelectValuesMeta selectVals = (SelectValuesMeta)selections.getStepMetaInterface();
    selectVals.allocate(allSelections.size(), 0, 0);
    for (int i = 0; i < allSelections.size(); i++) {
      // 
      Selection selection = allSelections.get(i);
      String fieldName = ((InlineEtlPhysicalColumn)selection.getLogicalColumn().getPhysicalColumn()).getFieldName();
      selectVals.getSelectName()[i] = fieldName;
      
      logger.debug("SELECT " + fieldName);
    }

    StepMeta finalSelections = getStepMeta(transMeta, "Select values 2");
    Map<String, String> fieldNameMap = new HashMap<String, String>();
    
    SelectValuesMeta finalSelectVals = (SelectValuesMeta)finalSelections.getStepMetaInterface();
    finalSelectVals.allocate(query.getSelections().size(), 0, 0);
    for (int i = 0; i < query.getSelections().size(); i++) {
      // 
      Selection selection = query.getSelections().get(i);
      String fieldName = ((InlineEtlPhysicalColumn)selection.getLogicalColumn().getPhysicalColumn()).getFieldName();
      fieldNameMap.put(fieldName.toUpperCase(), selection.getLogicalColumn().getId());
      finalSelectVals.getSelectName()[i] = fieldName;
    }
    
    //
    // CONSTRAINTS
    //
    
    if (query.getConstraints().size() > 0) {
      
      StepMeta formula = getStepMeta(transMeta, "Formula");
      FormulaMeta formulaMeta = (FormulaMeta)formula.getStepMetaInterface();

      int alloc = 0;
      for (QueryConstraint constraint : queryConstraints) {
        if (constraint.groupby) {
          continue;
        }
        alloc++;
      }
      if (alloc > 0) {
        formulaMeta.allocate(alloc);
      }
      
      StepMeta filter = getStepMeta(transMeta, "Filter rows");
      FilterRowsMeta filterRows = (FilterRowsMeta)filter.getStepMetaInterface();
      Condition rootCondition = new Condition();      
      int c = 0;
      for (QueryConstraint constraint : queryConstraints) {
        if (constraint.groupby) {
          continue;
        }
        String formulaVal = constraint.formula;
        formulaMeta.getFormula()[c] = new FormulaMetaFunction("__FORMULA_" + c, formulaVal, ValueMetaInterface.TYPE_BOOLEAN, -1, -1, null);

        Condition condition = new Condition();
        condition.setLeftValuename("__FORMULA_" + c);
        condition.setOperator(convertOperator(constraint.orig.getCombinationType()));
        condition.setFunction(Condition.FUNC_EQUAL);
        condition.setRightExact(new ValueMetaAndData("dummy", true));
        rootCondition.addCondition(condition);
        c++;
      }
      
      if (c > 0) {
        filterRows.setCondition(rootCondition);
        filterRows.setSendTrueStep(null);
        filterRows.setSendTrueStepname(null);
        filterRows.setSendFalseStep(null);
        filterRows.setSendFalseStepname(null);
      }
      
      if (groupBys > 0) {
        
        StepMeta formula2 = getStepMeta(transMeta, "Formula 2");
        FormulaMeta formulaMeta2 = (FormulaMeta)formula2.getStepMetaInterface();
        
        alloc = 0;
        for (QueryConstraint constraint : queryConstraints) {
          if (!constraint.groupby) {
            continue;
          }
          alloc++;
        }
        if (alloc > 0) {
          formulaMeta2.allocate(alloc);
        }

        
        StepMeta filter2 = getStepMeta(transMeta, "Filter rows 2");
        FilterRowsMeta filterRows2 = (FilterRowsMeta)filter2.getStepMetaInterface();
        Condition rootCondition2 = new Condition();
        
        c = 0;
        for (QueryConstraint constraint : queryConstraints) {
          if (!constraint.groupby) {
            continue;
          }
          String formulaVal = constraint.formula;
          formulaMeta2.getFormula()[c] = new FormulaMetaFunction("__FORMULA2_" + c, formulaVal, ValueMetaInterface.TYPE_BOOLEAN, -1, -1, null);

          Condition condition = new Condition();
          condition.setLeftValuename("__FORMULA2_" + c);
          condition.setOperator(convertOperator(constraint.orig.getCombinationType()));
          condition.setFunction(Condition.FUNC_EQUAL);
          condition.setRightExact(new ValueMetaAndData("dummy", true));
          rootCondition2.addCondition(condition);
          c++;
        }
        if (c > 0) {
          filterRows2.setCondition(rootCondition2);
          filterRows2.setSendTrueStep(null);
          filterRows2.setSendTrueStepname(null);
          filterRows2.setSendFalseStep(null);
          filterRows2.setSendFalseStepname(null);

        }
        
      }
      
    } else {
      // we could remove the formula and filter steps for performance.
    }
    
    // 
    // SORT
    //
    
    // TODO: does group by impact sorting?
    
    StepMeta sort = getStepMeta(transMeta, "Sort rows");
    
    SortRowsMeta sortRows = (SortRowsMeta)sort.getStepMetaInterface();
    
    sortRows.allocate(query.getOrders().size()); //allSelections.size());

    int c = 0;
    for (Order order : query.getOrders()) {
      String fieldName = ((InlineEtlPhysicalColumn)order.getSelection().getLogicalColumn().getPhysicalColumn()).getFieldName();
      sortRows.getFieldName()[c] = fieldName;
      logger.debug("ORDER: " + fieldName);
      sortRows.getAscending()[c] = (order.getType() == Order.Type.ASC);
      sortRows.getCaseSensitive()[c] = false;
      c++;
    }
    
    //
    // GROUP BY
    //
    
    if (groupBys > 0) {
      
      // GROUP SORT
      
      StepMeta groupsort = getStepMeta(transMeta, "Group Sort rows");
      
      SortRowsMeta groupSortRows = (SortRowsMeta)groupsort.getStepMetaInterface();
      

      int groups = 0;
      for (Selection selection : query.getSelections()) {
        // sort the group by fields
      
        if (selection.getActiveAggregationType() == null ||
            selection.getActiveAggregationType() == AggregationType.NONE) {
          groups++;
        }
      }

      groupSortRows.allocate(groups);
      
      c = 0;
      for (Selection selection : query.getSelections()) {
        // sort the group by fields
      
        if (selection.getActiveAggregationType() == null ||
            selection.getActiveAggregationType() == AggregationType.NONE) {
          
          String fieldName = ((InlineEtlPhysicalColumn)selection.getLogicalColumn().getPhysicalColumn()).getFieldName();
          groupSortRows.getFieldName()[c] = fieldName;
          logger.debug("GROUP ORDER: " + fieldName);
          groupSortRows.getAscending()[c] = true;
          groupSortRows.getCaseSensitive()[c] = false;
          c++;
        }
      }
      
      // GROUP BY
      
      StepMeta group = getStepMeta(transMeta, "Group by");
      GroupByMeta groupStep = (GroupByMeta)group.getStepMetaInterface();
      // c is the number 
      groupStep.allocate(groups, groupBys);
      
      // group only on selections
      
      c = 0;
      for (Selection selection : query.getSelections()) {
        if (selection.getActiveAggregationType() == null || selection.getActiveAggregationType() == AggregationType.NONE) {
          String fieldName = ((InlineEtlPhysicalColumn)selection.getLogicalColumn().getPhysicalColumn()).getFieldName();
          groupStep.getGroupField()[c] = fieldName;
          logger.debug("GROUP BY: " + fieldName);
          c++;
        }
      }
      
      // add group by fields for all the grouped selections
      
      c = 0;
      for (Selection selection : allSelections) {
        if (selection.getActiveAggregationType() != null && selection.getActiveAggregationType() != AggregationType.NONE) {
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
  
  private int convertOperator(CombinationType combo) {
    switch(combo) {
      case OR:
        return Condition.OPERATOR_OR;
      case AND_NOT:
        return Condition.OPERATOR_AND_NOT;
      case OR_NOT:
        return Condition.OPERATOR_OR_NOT;
      case AND:
      default:
        return Condition.OPERATOR_AND;  
    }
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
//            if (!"Select values".equals(step.stepname)) {
              continue;
            }
            logger.debug("STEP NAME: " + step.stepname);
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
        
        if (logger.isDebugEnabled()) {
          StringBuffer sb = new StringBuffer();
          for (int i = 0; i < pentahoRow.length; i++) {
            sb.append(pentahoRow[i] + "; ");
          }
          logger.debug(sb.toString());
        }

        memResults.addRow(pentahoRow);
      } catch (KettleValueException e) {
        throw new KettleStepException(e);
      }
    }

    
  }
  
  private int convertType(DataType type) {
    switch(type) {
      case DATE:
        return ValueMetaInterface.TYPE_DATE;
      case BOOLEAN:
        return ValueMetaInterface.TYPE_BOOLEAN;
      case NUMERIC:
        return ValueMetaInterface.TYPE_NUMBER;
      case BINARY:
      case IMAGE:
        return ValueMetaInterface.TYPE_BINARY;
      case UNKNOWN:
      case URL:
      case STRING:
      default:
        return ValueMetaInterface.TYPE_STRING;
    }
  }
}
