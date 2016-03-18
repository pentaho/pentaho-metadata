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
 * Copyright (c) 2009-2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.metadata.query.impl.ietl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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
import org.pentaho.di.trans.TransHopMeta;
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
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.InlineEtlPhysicalColumn;
import org.pentaho.metadata.model.InlineEtlPhysicalModel;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.BaseMetadataQueryExec;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryModelMetaData;

/**
 * This query executor generates an inline ETL result set, based on the inline etl physical model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class InlineEtlQueryExecutor extends BaseMetadataQueryExec {

  private static final String __FORMULA_ = "__FORMULA_"; //$NON-NLS-1$

  private static final Log logger = LogFactory.getLog( InlineEtlQueryExecutor.class );

  String transformLocation = "res:org/pentaho/metadata/query/impl/ietl/"; //$NON-NLS-1$

  private String csvFileLoc = null;

  @Override
  public void setParameter( Parameter param, Object value ) {

    super.setParameter( param, convertParameterValue( param, value ) );
  }

  public IPentahoResultSet executeQuery( Query queryObject ) {

    try {
      return executeQuery( queryObject, parameters );
    } catch ( Exception e ) {
      logger.error( "error", e ); //$NON-NLS-1$
      return null;
    }
  }

  protected void init() {

  }

  public boolean isLive() {
    return false;
  }

  protected String getTransformLocation() {
    return transformLocation;
  }

  public void setTransformLocation( String transformLocation ) {
    this.transformLocation = transformLocation;
  }

  private List<Selection> getAllSelections( Query query, List<QueryConstraint> queryConstraints ) {
    List<Selection> allSelections = new ArrayList<Selection>();

    // selections
    allSelections.addAll( query.getSelections() );

    // orders
    for ( Order order : query.getOrders() ) {
      if ( !allSelections.contains( order.getSelection() ) ) {
        allSelections.add( order.getSelection() );
      }
    }

    // constraints
    for ( QueryConstraint constraint : queryConstraints ) {
      for ( Selection selection : constraint.selections ) {
        if ( !allSelections.contains( selection ) ) {
          allSelections.add( selection );
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
  }

  public List<QueryConstraint> parseConstraints( Query query, Map<String, Object> parameters ) {
    List<QueryConstraint> constraints = new ArrayList<QueryConstraint>();
    for ( Constraint constraint : query.getConstraints() ) {
      QueryConstraint qc = new QueryConstraint();
      qc.orig = constraint;

      // parse out all the [] fields
      Pattern p = Pattern.compile( "\\[([^\\]]*)\\]" ); //$NON-NLS-1$
      Matcher m = p.matcher( constraint.getFormula() );
      StringBuffer sb = new StringBuffer();
      while ( m.find() ) {
        String match = m.group( 1 );
        if ( match.startsWith( "param:" ) ) { //$NON-NLS-1$
          String paramName = match.substring( 6 );
          Object paramValue = parameters.get( paramName );
          String openFormulaValue = ""; //$NON-NLS-1$
          if ( paramValue instanceof Boolean ) {
            // need to get and then render either true or false function.
            if ( ( (Boolean) paramValue ).booleanValue() ) {
              openFormulaValue = "TRUE()"; //$NON-NLS-1$
            } else {
              openFormulaValue = "FALSE()"; //$NON-NLS-1$
            }
          } else if ( paramValue instanceof Double ) {
            openFormulaValue = paramValue.toString();
          } else {
            // assume a string, string literal quote
            openFormulaValue = "\"" + paramValue + "\""; //$NON-NLS-1$ //$NON-NLS-2$
          }
          m.appendReplacement( sb, openFormulaValue );
        } else {
          String[] seg = match.split( "\\." ); //$NON-NLS-1$
          if ( seg != null && seg.length > 1 ) {
            Category cat = query.getLogicalModel().findCategory( seg[0] );
            LogicalColumn col = cat.findLogicalColumn( seg[1] );
            if ( col == null ) {
              logger.error( Messages.getErrorString(
                  "InlineEtlQueryExecutor.ERROR_0001_FAILED_TO_LOCATE_COLUMN", seg[0], seg[1] ) ); //$NON-NLS-1$
            }
            String fieldName = (String) col.getProperty( InlineEtlPhysicalColumn.FIELD_NAME );
            AggregationType agg = null;
            if ( seg.length > 2 ) {
              agg = AggregationType.valueOf( seg[2].toUpperCase() );
            }
            Selection sel = new Selection( cat, col, agg );
            if ( !qc.selections.contains( sel ) ) {
              qc.selections.add( sel );
              if ( sel.getActiveAggregationType() != null && sel.getActiveAggregationType() != AggregationType.NONE ) {
                qc.groupby = true;
              }
            }
            // this may be different in the group by context.

            m.appendReplacement( sb, "[" + fieldName + "]" ); //$NON-NLS-1$ //$NON-NLS-2$
          } else {
            logger
                .error( Messages.getErrorString( "InlineEtlQueryExecutor.ERROR_0002_FAILED_TO_PARSE_FORMULA", match ) ); //$NON-NLS-1$
          }
        }
      }
      m.appendTail( sb );
      qc.formula = sb.toString();
      if ( logger.isDebugEnabled() ) {
        logger.debug( "PARSED FORMULA: " + qc.formula ); //$NON-NLS-1$
      }
      constraints.add( qc );
    }
    return constraints;
  }

  protected void doInputWiring( Query query, TransMeta transMeta ) {
    //
    // CSV FILE LOCATION AND FIELDS
    //

    InlineEtlPhysicalModel physicalModel = (InlineEtlPhysicalModel) query.getLogicalModel().getPhysicalModel();

    CsvInputMeta csvinput = (CsvInputMeta) getStepMeta( transMeta, "CSV file input" ).getStepMetaInterface(); //$NON-NLS-1$

    // the file name might need to be translated to the correct location here
    if ( csvFileLoc != null ) {
      csvinput.setFilename( csvFileLoc + physicalModel.getFileLocation() );
    } else {
      csvinput.setFilename( physicalModel.getFileLocation() );
    }

    csvinput.setDelimiter( physicalModel.getDelimiter() );
    csvinput.setEnclosure( physicalModel.getEnclosure() );
    csvinput.setHeaderPresent( physicalModel.getHeaderPresent() );

    // update fields

    LogicalTable table = query.getLogicalModel().getLogicalTables().get( 0 );

    csvinput.allocate( table.getLogicalColumns().size() );

    for ( int i = 0; i < csvinput.getInputFields().length; i++ ) {
      // Update csv input

      LogicalColumn col = table.getLogicalColumns().get( i );
      csvinput.getInputFields()[i] = new TextFileInputField();
      String fieldName = (String) col.getProperty( InlineEtlPhysicalColumn.FIELD_NAME );
      if ( logger.isDebugEnabled() ) {
        logger.debug( "FROM CSV: " + fieldName ); //$NON-NLS-1$
      }
      csvinput.getInputFields()[i].setName( fieldName );
      csvinput.getInputFields()[i].setType( convertType( col.getDataType() ) );
    }

  }

  protected void doInjector( Query query, Trans trans ) throws Exception {
    // nothing to do in CSV mode, folks can override this behavior.
  }

  public IPentahoResultSet executeQuery( Query query, String fileLoc, Map<String, Object> parameters ) throws Exception {
    setCsvFileLoc( fileLoc );
    return executeQuery( query, parameters );
  }

  public IPentahoResultSet executeQuery( Query query, Map<String, Object> parameters ) throws Exception {
    if ( query.getLimit() >= 0 ) {
      throw new UnsupportedOperationException( Messages
          .getErrorString( "InlineEtlQueryExecutor.ERROR_0003_LIMIT_NOT_SUPPORTED" ) );
    }

    // resolve any missing parameters with default values
    if ( parameters == null && query.getParameters().size() > 0 ) {
      parameters = new HashMap<String, Object>();
    }
    for ( Parameter param : query.getParameters() ) {
      if ( !parameters.containsKey( param.getName() ) ) {
        parameters.put( param.getName(), param.getDefaultValue() );
      }
    }

    // group by?
    int groupBys = 0;
    List<QueryConstraint> queryConstraints = parseConstraints( query, parameters );

    TreeSet<String> repeatedSelections = new TreeSet<String>();
    List<Selection> allSelections = getAllSelections( query, queryConstraints );
    Map<Selection, String> selectionFieldNames = new HashMap<>();

    // calculate number of group bys, also build up a list
    // of unique field names.
    for ( Selection selection : allSelections ) {
      String fieldName = ( (InlineEtlPhysicalColumn) selection.getLogicalColumn().getPhysicalColumn() ).getFieldName();
      String useFieldName = fieldName;
      int count = 1;
      while ( repeatedSelections.contains( useFieldName ) ) {
        useFieldName = fieldName + "_" + count++; //$NON-NLS-1$
      }
      repeatedSelections.add( useFieldName );
      selectionFieldNames.put( selection, useFieldName );
      if ( selection.getActiveAggregationType() != null && selection.getActiveAggregationType() != AggregationType.NONE ) {
        groupBys++;
      }
    }

    String fileAddress = getTransformLocation() + "inlinecsv.ktr"; //$NON-NLS-1$
    if ( groupBys > 0 && query.getConstraints().size() == 0 ) {
      fileAddress = getTransformLocation() + "inlinecsv_groupby.ktr"; //$NON-NLS-1$
    }
    if ( groupBys > 0 && query.getConstraints().size() > 0 ) {
      fileAddress = getTransformLocation() + "inlinecsv_groupby_and_constraints.ktr"; //$NON-NLS-1$
    }
    TransMeta transMeta = new TransMeta( fileAddress, null, true );
    transMeta.setFilename( fileAddress );

    doInputWiring( query, transMeta );

    //
    // SELECT
    //

    StepMeta selections = getStepMeta( transMeta, "Select values" ); //$NON-NLS-1$
    SelectValuesMeta selectVals = (SelectValuesMeta) selections.getStepMetaInterface();
    selectVals.allocate( allSelections.size(), 0, 0 );
    final String[] selectNames = new String[ allSelections.size() ];
    final String[] selectRenames = new String[ allSelections.size() ];
    for ( int i = 0; i < allSelections.size(); i++ ) {
      Selection selection = allSelections.get( i );
      String fieldName = ( (InlineEtlPhysicalColumn) selection.getLogicalColumn().getPhysicalColumn() ).getFieldName();
      String renameFieldName = selectionFieldNames.get( selection );

      selectNames[ i ] = fieldName;
      // add a rename property if this field is used for multiple
      // selections
      if ( !fieldName.equals( renameFieldName ) ) {
        selectRenames[ i ] = renameFieldName;
      }

      if ( logger.isDebugEnabled() ) {
        logger.debug( "SELECT " + fieldName + " RENAME TO " + renameFieldName ); //$NON-NLS-1$//$NON-NLS-2$
      }
    }
    selectVals.setSelectName( selectNames );
    selectVals.setSelectRename( selectRenames );

    StepMeta finalSelections = getStepMeta( transMeta, "Select values 2" ); //$NON-NLS-1$
    Map<String, String> fieldNameMap = new HashMap<>();

    SelectValuesMeta finalSelectVals = (SelectValuesMeta) finalSelections.getStepMetaInterface();
    finalSelectVals.allocate( query.getSelections().size(), 0, 0 );
    final String[] finalSelectValsNames = new String[ query.getSelections().size() ];
    for ( int i = 0; i < query.getSelections().size(); i++ ) {
      Selection selection = query.getSelections().get( i );
      String fieldName = selectionFieldNames.get( selection );
      fieldNameMap.put( fieldName.toUpperCase(), selection.getLogicalColumn().getId() );
      finalSelectValsNames[ i ] = fieldName;
    }
    finalSelectVals.setSelectName( finalSelectValsNames );

    //
    // CONSTRAINTS
    //

    if ( query.getConstraints().size() > 0 ) {

      StepMeta formula = getStepMeta( transMeta, "Formula" ); //$NON-NLS-1$
      FormulaMeta formulaMeta = (FormulaMeta) formula.getStepMetaInterface();

      int alloc = 0;
      for ( QueryConstraint constraint : queryConstraints ) {
        if ( constraint.groupby ) {
          continue;
        }
        alloc++;
      }
      if ( alloc > 0 ) {
        formulaMeta.allocate( alloc );
      }

      StepMeta filter = getStepMeta( transMeta, "Filter rows" ); //$NON-NLS-1$
      FilterRowsMeta filterRows = (FilterRowsMeta) filter.getStepMetaInterface();
      Condition rootCondition = new Condition();
      int c = 0;
      for ( QueryConstraint constraint : queryConstraints ) {
        if ( constraint.groupby ) {
          continue;
        }
        String formulaVal = constraint.formula;
        formulaMeta.getFormula()[c] =
            new FormulaMetaFunction( __FORMULA_ + c, formulaVal, ValueMetaInterface.TYPE_BOOLEAN, -1, -1, null );

        Condition condition = new Condition();
        condition.setLeftValuename( __FORMULA_ + c );
        condition.setOperator( convertOperator( constraint.orig.getCombinationType() ) );
        condition.setFunction( Condition.FUNC_EQUAL );
        condition.setRightExact( new ValueMetaAndData( "dummy", true ) ); //$NON-NLS-1$
        rootCondition.addCondition( condition );
        c++;
      }

      if ( c > 0 ) {
        filterRows.setCondition( rootCondition );

        // link the dummy step to FALSE hop
        StepMeta dummy = getStepMeta( transMeta, "Dummy 1" ); //$NON-NLS-1$
        filterRows.getStepIOMeta().getTargetStreams().get( 1 ).setStepMeta( dummy );
        transMeta.addTransHop( new TransHopMeta( filter, dummy ) );
      }

      if ( groupBys > 0 ) {

        StepMeta formula2 = getStepMeta( transMeta, "Formula 2" ); //$NON-NLS-1$
        FormulaMeta formulaMeta2 = (FormulaMeta) formula2.getStepMetaInterface();

        alloc = 0;
        for ( QueryConstraint constraint : queryConstraints ) {
          if ( !constraint.groupby ) {
            continue;
          }
          alloc++;
        }
        if ( alloc > 0 ) {
          formulaMeta2.allocate( alloc );
        }

        StepMeta filter2 = getStepMeta( transMeta, "Filter rows 2" ); //$NON-NLS-1$
        FilterRowsMeta filterRows2 = (FilterRowsMeta) filter2.getStepMetaInterface();
        Condition rootCondition2 = new Condition();

        c = 0;
        for ( QueryConstraint constraint : queryConstraints ) {
          if ( !constraint.groupby ) {
            continue;
          }
          String formulaVal = constraint.formula;
          formulaMeta2.getFormula()[c] =
              new FormulaMetaFunction( "__FORMULA2_" + c, formulaVal, ValueMetaInterface.TYPE_BOOLEAN, -1, -1, null ); //$NON-NLS-1$

          Condition condition = new Condition();
          condition.setLeftValuename( "__FORMULA2_" + c ); //$NON-NLS-1$
          condition.setOperator( convertOperator( constraint.orig.getCombinationType() ) );
          condition.setFunction( Condition.FUNC_EQUAL );
          condition.setRightExact( new ValueMetaAndData( "dummy", true ) ); //$NON-NLS-1$
          rootCondition2.addCondition( condition );
          c++;
        }
        if ( c > 0 ) {
          filterRows2.setCondition( rootCondition2 );
          // link the dummy step to FALSE hop
          StepMeta dummy2 = getStepMeta( transMeta, "Dummy 2" ); //$NON-NLS-1$
          filterRows2.getStepIOMeta().getTargetStreams().get( 1 ).setStepMeta( dummy2 );
          transMeta.addTransHop( new TransHopMeta( filter2, dummy2 ) );
        }
      }

    }
    // we could remove the formula and filter steps for performance.

    //
    // SORT
    //

    StepMeta sort = getStepMeta( transMeta, "Sort rows" ); //$NON-NLS-1$

    SortRowsMeta sortRows = (SortRowsMeta) sort.getStepMetaInterface();

    sortRows.allocate( query.getOrders().size() ); // allSelections.size());

    int c = 0;
    for ( Order order : query.getOrders() ) {
      String fieldName = selectionFieldNames.get( order.getSelection() );
      sortRows.getFieldName()[c] = fieldName;
      if ( logger.isDebugEnabled() ) {
        logger.debug( "ORDER: " + fieldName ); //$NON-NLS-1$
      }
      sortRows.getAscending()[c] = ( order.getType() == Order.Type.ASC );
      sortRows.getCaseSensitive()[c] = false;
      c++;
    }

    //
    // GROUP BY
    //

    if ( groupBys > 0 ) {

      // GROUP SORT

      StepMeta groupsort = getStepMeta( transMeta, "Group Sort rows" ); //$NON-NLS-1$

      SortRowsMeta groupSortRows = (SortRowsMeta) groupsort.getStepMetaInterface();

      int groups = 0;
      for ( Selection selection : query.getSelections() ) {
        // sort the group by fields

        if ( selection.getActiveAggregationType() == null
            || selection.getActiveAggregationType() == AggregationType.NONE ) {
          groups++;
        }
      }

      groupSortRows.allocate( groups );

      c = 0;
      for ( Selection selection : query.getSelections() ) {
        // sort the group by fields

        if ( selection.getActiveAggregationType() == null
            || selection.getActiveAggregationType() == AggregationType.NONE ) {

          String fieldName = selectionFieldNames.get( selection );
          groupSortRows.getFieldName()[c] = fieldName;
          if ( logger.isDebugEnabled() ) {
            logger.debug( "GROUP ORDER: " + fieldName ); //$NON-NLS-1$
          }
          groupSortRows.getAscending()[c] = true;
          groupSortRows.getCaseSensitive()[c] = false;
          c++;
        }
      }

      //
      // GROUP BY
      //

      StepMeta group = getStepMeta( transMeta, "Group by" ); //$NON-NLS-1$
      GroupByMeta groupStep = (GroupByMeta) group.getStepMetaInterface();
      // c is the number
      groupStep.allocate( groups, groupBys );

      // group only on selections

      c = 0;
      for ( Selection selection : query.getSelections() ) {
        if ( selection.getActiveAggregationType() == null
            || selection.getActiveAggregationType() == AggregationType.NONE ) {
          String fieldName = selectionFieldNames.get( selection );
          groupStep.getGroupField()[c] = fieldName;
          if ( logger.isDebugEnabled() ) {
            logger.debug( "GROUP BY: " + fieldName ); //$NON-NLS-1$
          }
          c++;
        }
      }

      // add group by fields for all the grouped selections

      c = 0;
      for ( Selection selection : allSelections ) {
        if ( selection.getActiveAggregationType() != null
            && selection.getActiveAggregationType() != AggregationType.NONE ) {
          String fieldName = selectionFieldNames.get( selection );
          groupStep.getAggregateField()[c] = fieldName;
          groupStep.getSubjectField()[c] = fieldName;
          groupStep.getAggregateType()[c] = convertAggType( selection.getActiveAggregationType() );
          groupStep.getValueField()[c] = null;
          c++;
        }
      }
    }

    InlineEtlRowListener listener = new InlineEtlRowListener();
    Trans trans = new Trans( transMeta );
    trans.prepareExecution( transMeta.getArguments() );

    // allows for subclasses to swap the csv step with an injector step
    doInjector( query, trans );

    listener.registerAsStepListener( trans, query, fieldNameMap );

    trans.startThreads();
    trans.waitUntilFinished();
    trans.cleanup();

    return listener.results;
  }

  private int convertAggType( AggregationType type ) {
    switch ( type ) {
      case NONE:
        return GroupByMeta.TYPE_GROUP_NONE;
      case AVERAGE:
        return GroupByMeta.TYPE_GROUP_AVERAGE;
      case SUM:
        return GroupByMeta.TYPE_GROUP_SUM;
      case COUNT:
        return GroupByMeta.TYPE_GROUP_COUNT_ALL;
      case COUNT_DISTINCT:
        return GroupByMeta.TYPE_GROUP_COUNT_DISTINCT;
      case MINIMUM:
        return GroupByMeta.TYPE_GROUP_MIN;
      case MAXIMUM:
        return GroupByMeta.TYPE_GROUP_MAX;
      default:
        return GroupByMeta.TYPE_GROUP_NONE;
    }
  }

  private int convertOperator( CombinationType combo ) {
    switch ( combo ) {
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

  protected StepMeta getStepMeta( TransMeta meta, String name ) {
    for ( StepMeta step : meta.getSteps() ) {
      if ( name.equals( step.getName() ) ) {
        return step;
      }
    }
    return null;
  }

  private static class InlineEtlRowListener implements RowListener {

    private MemoryResultSet results;
    private MemoryResultSet errorResults;

    private boolean registerAsStepListener( Trans trans, Query query, Map fieldMap ) throws Exception {
      boolean success = false;
      if ( trans != null ) {
        List<StepMetaDataCombi> stepList = trans.getSteps();
        // assume the last step
        for ( StepMetaDataCombi step : stepList ) {
          if ( !"Unique rows".equals( step.stepname ) ) { //$NON-NLS-1$
            continue;
          }
          if ( logger.isDebugEnabled() ) {
            logger.debug( "STEP NAME: " + step.stepname ); //$NON-NLS-1$
          }
          RowMetaInterface row = trans.getTransMeta().getStepFields( step.stepMeta ); // step.stepname?
          // create the metadata that the Pentaho result sets need
          String[] fieldNames = row.getFieldNames();

          String[][] columns = new String[1][fieldNames.length];
          for ( int column = 0; column < fieldNames.length; column++ ) {
            columns[0][column] = fieldNames[column];
          }

          // build valid metadata
          QueryModelMetaData metadata = new QueryModelMetaData( fieldMap, columns, null, query.getSelections() );

          results = new MemoryResultSet( metadata );
          errorResults = new MemoryResultSet( metadata );

          // add ourself as a row listener
          step.step.addRowListener( this );
          success = true;
        }
      }
      return success;
    }

    public void rowReadEvent( final RowMetaInterface row, final Object[] values ) {
    }

    public void rowWrittenEvent( final RowMetaInterface rowMeta, final Object[] row ) throws KettleStepException {
      processRow( results, rowMeta, row );
    }

    public void errorRowWrittenEvent( final RowMetaInterface rowMeta, final Object[] row ) throws KettleStepException {
      processRow( errorResults, rowMeta, row );
    }

    public void processRow( MemoryResultSet memResults, final RowMetaInterface rowMeta, final Object[] row )
      throws KettleStepException {
      if ( memResults == null ) {
        return;
      }
      try {
        Object[] pentahoRow = new Object[memResults.getColumnCount()];
        for ( int columnNo = 0; columnNo < memResults.getColumnCount(); columnNo++ ) {
          ValueMetaInterface valueMeta = rowMeta.getValueMeta( columnNo );

          switch ( valueMeta.getType() ) {
            case ValueMetaInterface.TYPE_BIGNUMBER:
              pentahoRow[columnNo] = rowMeta.getBigNumber( row, columnNo );
              break;
            case ValueMetaInterface.TYPE_BOOLEAN:
              pentahoRow[columnNo] = rowMeta.getBoolean( row, columnNo );
              break;
            case ValueMetaInterface.TYPE_DATE:
              pentahoRow[columnNo] = rowMeta.getDate( row, columnNo );
              break;
            case ValueMetaInterface.TYPE_INTEGER:
              pentahoRow[columnNo] = rowMeta.getInteger( row, columnNo );
              break;
            case ValueMetaInterface.TYPE_NONE:
              pentahoRow[columnNo] = rowMeta.getString( row, columnNo );
              break;
            case ValueMetaInterface.TYPE_NUMBER:
              pentahoRow[columnNo] = rowMeta.getNumber( row, columnNo );
              break;
            case ValueMetaInterface.TYPE_STRING:
              pentahoRow[columnNo] = rowMeta.getString( row, columnNo );
              break;
            default:
              pentahoRow[columnNo] = rowMeta.getString( row, columnNo );
          }
        }

        if ( logger.isDebugEnabled() ) {
          StringBuffer sb = new StringBuffer();
          for ( int i = 0; i < pentahoRow.length; i++ ) {
            sb.append( pentahoRow[i] ).append( "; " ); //$NON-NLS-1$
          }
          logger.debug( sb.toString() );
        }

        memResults.addRow( pentahoRow );
      } catch ( KettleValueException e ) {
        throw new KettleStepException( e );
      }
    }
  }

  private int convertType( DataType type ) {
    switch ( type ) {
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

  public String getCsvFileLoc() {
    return csvFileLoc;
  }

  public void setCsvFileLoc( String csvFileLoc ) {
    this.csvFileLoc = csvFileLoc;
  }

}
