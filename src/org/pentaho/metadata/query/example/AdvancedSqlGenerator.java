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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.query.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.RelationshipType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.Path;
import org.pentaho.metadata.query.impl.sql.SqlAndTables;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.Order.Type;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.pms.mql.dialect.JoinType;
import org.pentaho.pms.mql.dialect.SQLDialectFactory;
import org.pentaho.pms.mql.dialect.SQLDialectInterface;
import org.pentaho.pms.mql.dialect.SQLQueryModel;
import org.pentaho.pms.mql.dialect.SQLQueryModel.OrderType;

/**
 * This is an example of extending the metadata query model.
 * 
 * @author Will Gorman (wgorman@penthao.com)
 *
 */
public class AdvancedSqlGenerator extends SqlGenerator {

  public static final String DEFAULT_ALIAS = "__DEFAULT__"; //$NON-NLS-1$
  
  static class AliasedPathLogicalTable {
    private String alias;
    private LogicalTable table;
    AliasedPathLogicalTable(String alias, LogicalTable table) {
      this.alias = alias;
      this.table = table;
    }
    
    public boolean equals(Object obj) {
      AliasedPathLogicalTable apbt = (AliasedPathLogicalTable)obj;
      return apbt.alias.equals(alias) && apbt.table.equals(table);
    }
    
    public String getAlias() {
      return alias;
    }
    
    public LogicalTable getLogicalTable() {
      return table;
    }
  }
  
  @Override
  protected MappedQuery getSQL(
      LogicalModel model, 
      List<Selection> selections, 
      List<Constraint> constraints, 
      List<Order> orderbys, 
      DatabaseMeta databaseMeta, 
      String locale, 
      Map<String, Object> parameters,
      boolean genAsPreparedStatement,
      boolean disableDistinct, 
      int limit,
      Constraint securityConstraint) throws PentahoMetadataException {

    // generate the formula objects for constraints 
    Map<Constraint, AliasAwareSqlOpenFormula> constraintFormulaMap = new HashMap<Constraint, AliasAwareSqlOpenFormula>();
    Map<AliasedSelection, AliasAwareSqlOpenFormula> selectionFormulaMap = new HashMap<AliasedSelection, AliasAwareSqlOpenFormula>();
    for (Constraint constraint : constraints) {
      AliasAwareSqlOpenFormula formula = new AliasAwareSqlOpenFormula(model, databaseMeta, constraint.getFormula(), selections, DEFAULT_ALIAS);
      formula.parseAndValidate();
      constraintFormulaMap.put(constraint, formula);
    }
    
    
    Map<String,String> columnsMap = new HashMap<String,String>();
    if (model == null || selections.size() == 0) {
      return null;
    }
    
    // implement SQL generation here
    List<Selection> defaultList = null;
    List<List<Selection>> lists = new ArrayList<List<Selection>>();
    List<String> aliasNames = new ArrayList<String>();
    Map<String, List<Selection>> listlookup = new HashMap<String, List<Selection>>();
    
    List<Selection> selectionsAndOrderBys = new ArrayList<Selection>();
    selectionsAndOrderBys.addAll(selections);
    for (Order orderBy : orderbys) {
      selectionsAndOrderBys.add(orderBy.getSelection());
    }
    
    // default + alias lists
    for (Selection selection : selectionsAndOrderBys) {
      AliasedSelection sel = (AliasedSelection)selection;
      
      if (sel.hasFormula()) {
        AliasAwareSqlOpenFormula formula = new AliasAwareSqlOpenFormula(model, databaseMeta, sel.getFormula(), selections, DEFAULT_ALIAS); // formula;
        formula.setAllowAggregateFunctions(true);
        formula.parseAndValidate();
        selectionFormulaMap.put(sel, formula);
      }
      
      if (sel.alias == null) {
        sel.alias = DEFAULT_ALIAS;
      } 
      List<Selection> list = listlookup.get(sel.alias);
      if (list == null) {
        list = new ArrayList<Selection>();
        if (sel.alias.equals(DEFAULT_ALIAS)) {
          defaultList = list;
          lists.add(0, list);
          aliasNames.add(0, DEFAULT_ALIAS);
        } else {
          lists.add(list);
          aliasNames.add(sel.alias);
        }
        listlookup.put(sel.alias, list);
      }
      if (!list.contains(sel)) {
        list.add(sel);
      }
    }

    if (!listlookup.containsKey(DEFAULT_ALIAS)) {
      throw new PentahoMetadataException("No non-aliased columns selected"); //$NON-NLS-1$
    }
    
    // generate paths for all the lists
    List<AliasedLogicalRelationship> allRelationships = new ArrayList<AliasedLogicalRelationship>();
    
    List<LogicalTable> defaultTables = getTablesInvolved(
        model, defaultList, constraints, orderbys, databaseMeta, 
        locale, selectionFormulaMap, constraintFormulaMap);
    Path defaultPath = getShortestPathBetween(model, defaultTables);
    List<LogicalTable> tbls = defaultPath.getUsedTables();
    List<AliasedPathLogicalTable> allTables = new ArrayList<AliasedPathLogicalTable>();
    for (LogicalTable tbl : tbls) {
      allTables.add(new AliasedPathLogicalTable(DEFAULT_ALIAS, tbl));
    }
    if (tbls.size() == 0) {
      allTables.add(new AliasedPathLogicalTable(DEFAULT_ALIAS, defaultTables.get(0)));
    }
    
    
    if (defaultPath == null) {
      throw new PentahoMetadataException(Messages.getErrorString("SqlGenerator.ERROR_0002_FAILED_TO_FIND_PATH")); //$NON-NLS-1$
    }
    
    for (int i = 0; i < defaultPath.size(); i++) {
      allRelationships.add(new AliasedLogicalRelationship(DEFAULT_ALIAS, DEFAULT_ALIAS, defaultPath.getRelationship(i)));
    }

    for (int i = 1; i < lists.size(); i++) {
      List<Selection> aliasedColumns = lists.get(i);
      List<Selection> aliasedAndDefaultColumns = new ArrayList<Selection>();
      aliasedAndDefaultColumns.addAll(aliasedColumns);
      aliasedAndDefaultColumns.addAll(defaultList);
      List<LogicalTable> aliasedTables = getTablesInvolved(
          model, aliasedColumns, null, null, databaseMeta, locale, 
          selectionFormulaMap, constraintFormulaMap);
      List<LogicalTable> aliasedAndDefaultTables = getTablesInvolved(
          model, aliasedAndDefaultColumns, null, null, databaseMeta, locale, 
          selectionFormulaMap, constraintFormulaMap);
      Path aliasedAndDefaultPath = getShortestPathBetween(model, aliasedAndDefaultTables);
      
      // Prune and connect aliased path with default path
      for (LogicalTable aliasedTable : aliasedTables) {
        // follow the path, move relationships into allRelationships and allTables
        traversePath((String)aliasNames.get(i), aliasedTable, aliasedAndDefaultPath, aliasedTables, defaultTables, allTables, allRelationships);
      }
      
    }
    SQLQueryModel sqlquery = new SQLQueryModel();
    boolean group = hasFactsInIt(selections, constraints, selectionFormulaMap, constraintFormulaMap);

    // SELECT
    
    sqlquery.setDistinct(!disableDistinct && !group);
    sqlquery.setLimit(limit);
    for (int i = 0; i < selections.size(); i++) {
      AliasedSelection selection = (AliasedSelection)selections.get(i);
      String formula;
      if (selection.hasFormula()) {
        try {
          
          formula = selectionFormulaMap.get(selection).generateSQL(locale);
        } catch (PentahoMetadataException e) {
          throw new RuntimeException(e);
        }
      } else {
        SqlAndTables sqlAndTables = getSelectionSQL(model, selection, databaseMeta, locale);
        formula = sqlAndTables.getSql();        
//        formula = getFunctionTableAndColumnForSQL(model, selection, databaseMeta, locale);
      }

      // in some database implementations, the "as" name has a finite length;
      // for instance, oracle cannot handle a name longer than 30 characters. 
      // So, we map a short name here to the longer id, and replace the id
      // later in the resultset metadata. 
      String alias = null;
      if(columnsMap != null){
        if (selection.getLogicalColumn() != null && selection.getAlias().equals(DEFAULT_ALIAS)) {
          
          // BIG TODO: map bizcol correctly
          
          columnsMap.put("COL" + i, selection.getLogicalColumn().getId()); //$NON-NLS-1$
        } else {
          columnsMap.put("COL" + i, "CUSTOM_" +  i);
        }
        alias = databaseMeta.quoteField("COL" + Integer.toString(i)); //$NON-NLS-1$
      }else{
        alias = databaseMeta.quoteField(selection.getLogicalColumn().getId());
      }
      sqlquery.addSelection(formula, alias);
    }

    // FROM
    
    for (int i = 0; i < allTables.size(); i++) {
      AliasedPathLogicalTable tbl = (AliasedPathLogicalTable)allTables.get(i);
      // if __DEFAULT__, no alias
      // otherwise TABLE_ALIAS
      String alias = tbl.getLogicalTable().getId();
      if (!tbl.getAlias().equals(DEFAULT_ALIAS)) {
        alias = alias + "_" + tbl.getAlias(); //$NON-NLS-1$
      }
      String schemaName = null;
      if (tbl.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_SCHEMA) != null) {
        schemaName = databaseMeta.quoteField((String)tbl.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_SCHEMA));
      }
      String tableName = databaseMeta.quoteField((String)tbl.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE));
      sqlquery.addTable(databaseMeta.getSchemaTableCombination(schemaName, tableName), databaseMeta.quoteField(alias));
      
    }
    
    // JOINS
    
//    for (int i = 0; i < allRelationships.size(); i++) {
//      AliasedLogicalRelationship relation = allRelationships.get(i);
//      String join = getJoin(relation, databaseMeta, locale);
//      sqlquery.addWhereFormula(join, "AND");
//    }
    for (int i = 0; i < allRelationships.size(); i++) {
      AliasedLogicalRelationship aliasedRelation = allRelationships.get(i);
      String joinFormula = getJoin(model, aliasedRelation, databaseMeta, locale, selections);
      String joinOrderKey = aliasedRelation.relation.getJoinOrderKey();
      JoinType joinType;
      switch(RelationshipType.getJoinType(aliasedRelation.relation.getRelationshipType())) {
      case LEFT_OUTER: joinType = JoinType.LEFT_OUTER_JOIN; break;
      case RIGHT_OUTER: joinType = JoinType.RIGHT_OUTER_JOIN; break;
      case FULL_OUTER : joinType = JoinType.FULL_OUTER_JOIN; break;
      default: joinType = JoinType.INNER_JOIN; break;
      }
      
      String leftSchema = (String)aliasedRelation.relation.getFromTable().getProperty(SqlPhysicalTable.TARGET_SCHEMA);
      String leftTable = (String)aliasedRelation.relation.getFromTable().getProperty(SqlPhysicalTable.TARGET_TABLE);
        
      String rightSchema = (String)aliasedRelation.relation.getToTable().getProperty(SqlPhysicalTable.TARGET_SCHEMA);
      String rightTable = (String)aliasedRelation.relation.getToTable().getProperty(SqlPhysicalTable.TARGET_TABLE);
      
      String leftTableName = databaseMeta.getQuotedSchemaTableCombination(leftSchema, leftTable);
      String rightTableName = databaseMeta.getQuotedSchemaTableCombination(rightSchema, rightTable);
      
      String leftTableAlias = aliasedRelation.relation.getFromTable().getId();
      if (!aliasedRelation.leftAlias.equals(DEFAULT_ALIAS)) {
        leftTableAlias = leftTableAlias + "_" + aliasedRelation.leftAlias; //$NON-NLS-1$
      } 

      String rightTableAlias = aliasedRelation.relation.getToTable().getId();
      if (!aliasedRelation.rightAlias.equals(DEFAULT_ALIAS)) {
        rightTableAlias = rightTableAlias + "_" + aliasedRelation.rightAlias; //$NON-NLS-1$
      }  
      
      sqlquery.addJoin(leftTableName, leftTableAlias, rightTableName, rightTableAlias, joinType, joinFormula, joinOrderKey);
    }


    // WHERE CONDITIONS

    if (constraints != null) {
      boolean first = true;
      for (Constraint constraint : constraints) {
        // The ones with aggregates in it are for the HAVING clause
        AliasAwareSqlOpenFormula formula = constraintFormulaMap.get(constraint);
        
        if (!formula.hasAggregate() && !formula.hasAggregateFunction()) {
          String sql = formula.generateSQL(locale);
          
          // usedTables should be getBusinessAliases()
          String[] usedTables = formula.getTableAliasNames();
          sqlquery.addWhereFormula(sql, first ? "AND" : constraint.getCombinationType().toString(), usedTables); //$NON-NLS-1$
          first = false;
        } else {
          sqlquery.addHavingFormula(formula.generateSQL(locale), constraint.getCombinationType().toString());
        }
      }
    }

    // GROUP BY
    if (group) {
      // can be moved to selection loop
      for (Selection selection : selections) {
        // LogicalColumn LogicalColumn = selection.getLogicalColumn();
        AliasedSelection aliasedSelection = (AliasedSelection)selection;
        if (aliasedSelection.hasFormula()) {
          AliasAwareSqlOpenFormula formula = selectionFormulaMap.get(aliasedSelection);
          if (!formula.hasAggregate() && !formula.hasAggregateFunction()) {
            SqlAndTables sqlAndTables = getSelectionSQL(model, aliasedSelection, databaseMeta, locale);
            sqlquery.addGroupBy(sqlAndTables.getSql(), null);
          }
        } else {
          if (!aliasedSelection.hasAggregate()) {
            SqlAndTables sqlAndTables = getSelectionSQL(model, aliasedSelection, databaseMeta, locale);
            sqlquery.addGroupBy(sqlAndTables.getSql(), null);
          }
        }
      }
    }
    
    // ORDER BY
    if (orderbys != null) {
      for (Order orderItem : orderbys) {
        AliasedSelection selection = (AliasedSelection)orderItem.getSelection();
        String sqlSelection = null;
        if (!selection.hasFormula()) {
          SqlAndTables sqlAndTables = getSelectionSQL(model, selection, databaseMeta, locale);
          sqlSelection = sqlAndTables.getSql();
        } else {
          AliasAwareSqlOpenFormula formula = selectionFormulaMap.get(selection);
          sqlSelection = formula.generateSQL(locale);
        }
        sqlquery.addOrderBy(sqlSelection, null, (orderItem.getType() != Type.ASC) ? OrderType.DESCENDING : null); //$NON-NLS-1$
      }
    }
    
    
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(databaseMeta);
    String sql = dialect.generateSelectStatement(sqlquery);

    MappedQuery query = new MappedQuery(sql, columnsMap, selections, null);
    
    // defaultPath.getUsedTables();
    
    
    // selections, constraints, order, disableDistinct, locale, etc
    
    // first, generate join paths
    
    // second generate select statement
    
    return query;
  }
  
  protected boolean hasFactsInIt(
      List<Selection> selections, List<Constraint> conditions,
      Map<AliasedSelection, AliasAwareSqlOpenFormula> selectionFormulaMap,
      Map<Constraint, AliasAwareSqlOpenFormula> constraintFormulaMap
      ) {
    for (Selection selection : selections) {
      AliasedSelection aliasedSelection = (AliasedSelection)selection;
      if (aliasedSelection.hasFormula()) {
        AliasAwareSqlOpenFormula formula = selectionFormulaMap.get(aliasedSelection);
        if (formula.hasAggregate()) {
          return true;
        }
        // the formula may also define an aggregate function... 
        if (formula.hasAggregateFunction()) {
          return true;
        }
      } else {
        if (aliasedSelection.hasAggregate()) {
          return true;
        }
      }
    }
    if (conditions != null) {
      for (Constraint condition : conditions) {
        AliasAwareSqlOpenFormula formula = constraintFormulaMap.get(condition);
        if (formula.hasAggregate()) {
          return true;
        }
        if (formula.hasAggregateFunction()) {
          return true;
        }
      }
    }
    return false;
  }
  
  protected List<LogicalTable> getTablesInvolved(
      LogicalModel model, List<Selection> selections, List<Constraint> conditions, List<Order> orderBys, DatabaseMeta databaseMeta, String locale,
      Map<AliasedSelection, AliasAwareSqlOpenFormula> selectionFormulaMap,
      Map<Constraint, AliasAwareSqlOpenFormula> constraintFormulaMap
      ) {
    Set<LogicalTable> treeSet = new TreeSet<LogicalTable>();

    for (Selection selection : selections) {
      AliasedSelection aliasedSelection = (AliasedSelection)selection;
      if (aliasedSelection.hasFormula()) {
        AliasAwareSqlOpenFormula formula = selectionFormulaMap.get(aliasedSelection);
        List<Selection> cols = formula.getSelections();
        for(Selection sel : cols) {
          LogicalTable LogicalTable = sel.getLogicalColumn().getLogicalTable();
          treeSet.add(LogicalTable); //$NON-NLS-1$
        }
      } else {
//        LogicalTable LogicalTable = selection.getLogicalColumn().getLogicalTable();
//        treeSet.add(LogicalTable); //$NON-NLS-1$
        SQLAndAliasedTables sqlAndTables = getSelectionSQL(model, aliasedSelection, databaseMeta, locale);
        
        // Add the involved tables to the list...
        //
        for (AliasedPathLogicalTable LogicalTable : sqlAndTables.getAliasedLogicalTables()) {
          treeSet.add(LogicalTable.getLogicalTable());
        }
      }
    }
    if (conditions != null) {
      for(Constraint condition : conditions) {
        AliasAwareSqlOpenFormula formula = constraintFormulaMap.get(condition);
        List<Selection> cols = formula.getSelections();
        for (Selection sel : cols) {
          LogicalTable LogicalTable = sel.getLogicalColumn().getLogicalTable();
          treeSet.add(LogicalTable); //$NON-NLS-1$
        }
      }
    }
    
    // Figure out which tables are involved in the ORDER BY
    //
    if (orderBys != null) {
      for(Order order : orderBys) {
        AliasedSelection aliasedSelection = (AliasedSelection)order.getSelection();
        if (aliasedSelection.hasFormula()) {
          AliasAwareSqlOpenFormula formula = selectionFormulaMap.get(aliasedSelection);
          List<Selection> cols = formula.getSelections();
          for (Selection sel : cols) {
            LogicalTable LogicalTable = sel.getLogicalColumn().getLogicalTable();
            treeSet.add(LogicalTable); //$NON-NLS-1$
          }
        } else {
          SQLAndAliasedTables sqlAndTables = getSelectionSQL(model, (AliasedSelection)order.getSelection(), databaseMeta, locale);
          
          // Add the involved tables to the list...
          //
          for (AliasedPathLogicalTable LogicalTable : sqlAndTables.getAliasedLogicalTables()) {
            treeSet.add(LogicalTable.getLogicalTable());
          }
        }
      }
    }
    return new ArrayList<LogicalTable>(treeSet);
  }
  
  public static class SQLAndAliasedTables extends SqlAndTables {
    final List<AliasedPathLogicalTable> aliasedTables;
    
    public SQLAndAliasedTables(String sql, AliasedPathLogicalTable aliasedTable) {
      super(sql, (LogicalTable)null, (Selection)null);
      aliasedTables = new ArrayList<AliasedPathLogicalTable>();
      aliasedTables.add(aliasedTable);
    }

    public SQLAndAliasedTables(String sql, List<AliasedPathLogicalTable> aliasedTables) {
      super(sql, (LogicalTable)null, (Selection)null);
      this.aliasedTables = aliasedTables;
    }

    public List<AliasedPathLogicalTable> getAliasedLogicalTables() {
      return aliasedTables;
    }
    
    public List<LogicalTable> getUsedTables() {
      throw new UnsupportedOperationException();
    }

    public void setUsedTables(List<LogicalTable> tables) {
      throw new UnsupportedOperationException();
    }
    
  }
  
  // we should do something with this other than a static method that is alias aware.  
  // The folks that call this should be alias aware or not, and call a different method possibly?
  // this is primarily due to the context that would need to get passed into PMSFormula
  // we don't want the pentaho MQL solution to ever come across aliases, etc. 
  public static SQLAndAliasedTables getSelectionSQL(LogicalModel LogicalModel, AliasedSelection selection, DatabaseMeta databaseMeta, String locale) {
    String columnStr = (String)selection.getLogicalColumn().getProperty(SqlPhysicalColumn.TARGET_COLUMN);
    if (selection.getLogicalColumn().getProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE) == TargetColumnType.OPEN_FORMULA) { 
        // convert to sql using libformula subsystem
        try {
          // we'll need to pass in some context to PMSFormula so it can resolve aliases if necessary
          AliasAwareSqlOpenFormula formula = new AliasAwareSqlOpenFormula(LogicalModel, selection.getLogicalColumn().getLogicalTable(), databaseMeta, columnStr, selection.getAlias());
          formula.parseAndValidate();
          // return formula.generateSQL(locale);
          return new SQLAndAliasedTables(formula.generateSQL(locale), formula.getUsedAliasedTables());
        } catch (PentahoMetadataException e) {
          // this is for backwards compatibility.
          // eventually throw any errors
          throw new RuntimeException(Messages.getErrorString("SqlGenerator.ERROR_0001_FAILED_TO_PARSE_FORMULA", columnStr)); //$NON-NLS-1$  
        }
      } else {
          String tableColumn = ""; //$NON-NLS-1$
          String tblName = selection.getLogicalColumn().getLogicalTable().getId();
          if (!selection.getAlias().equals(DEFAULT_ALIAS)) {
            tblName += "_" + selection.getAlias(); //$NON-NLS-1$
          }
          tableColumn += databaseMeta.quoteField( tblName  );
          tableColumn += "."; //$NON-NLS-1$
          
          // TODO: WPG: instead of using formula, shouldn't we use the physical column's name?
          tableColumn += databaseMeta.quoteField( columnStr );
          
          if (selection.hasAggregate()) // For the having clause, for example: HAVING sum(turnover) > 100
          {
              // return getFunctionExpression(selection.getLogicalColumn(), tableColumn, databaseMeta);
              return new SQLAndAliasedTables(
                            getFunctionExpression(selection, tableColumn, databaseMeta), 
                            new AliasedPathLogicalTable(tblName, selection.getLogicalColumn().getLogicalTable())
                          );
          }
          else
          {
              return new SQLAndAliasedTables(tableColumn, new AliasedPathLogicalTable(tblName, selection.getLogicalColumn().getLogicalTable()));
          }
      }
  }
    
  public String getJoin(LogicalModel LogicalModel, AliasedLogicalRelationship relation, DatabaseMeta databaseMeta, String locale, List<Selection> selections) throws PentahoMetadataException
  {
    String join=""; //$NON-NLS-1$
    
    if (relation.relation.isComplex()) {
      // parse join as MQL
      String formulaString = relation.relation.getComplexJoin();
      AliasAwareSqlOpenFormula formula = new AliasAwareSqlOpenFormula(LogicalModel, databaseMeta, formulaString, selections, DEFAULT_ALIAS);

      // if we're dealing with an aliased join, inform the formula
      if (!relation.rightAlias.equals(DEFAULT_ALIAS) || !relation.leftAlias.equals(DEFAULT_ALIAS)) {
        Map<String, String> LogicalTableToAliasMap = new HashMap<String, String>();
        if (!relation.rightAlias.equals(DEFAULT_ALIAS)) {
          LogicalTableToAliasMap.put(relation.relation.getToTable().getId(), relation.rightAlias);
        } 
        if (!relation.leftAlias.equals(DEFAULT_ALIAS)) {
          LogicalTableToAliasMap.put(relation.relation.getFromTable().getId(), relation.leftAlias);
        } 
        formula.setLogicalTableToAliasMap(LogicalTableToAliasMap);
      }
      
      formula.parseAndValidate();
      join = formula.generateSQL(locale);
      
    } else if (relation.relation.getFromTable() != null && relation.relation.getToTable() != null && relation.relation.getFromColumn() !=null && relation.relation.getToColumn() != null) {
        String rightAlias = relation.relation.getToTable().getId();
        if (!relation.rightAlias.equals(DEFAULT_ALIAS)) {
          rightAlias = rightAlias + "_" + relation.rightAlias; //$NON-NLS-1$
        } 

        String leftAlias = relation.relation.getFromTable().getId();
        if (!relation.leftAlias.equals(DEFAULT_ALIAS)) {
          leftAlias = leftAlias + "_" + relation.leftAlias; //$NON-NLS-1$
        }             
        
        // Left side
        join  = databaseMeta.quoteField(leftAlias );
        join += "."; //$NON-NLS-1$
        join += databaseMeta.quoteField((String) relation.relation.getFromColumn().getProperty(SqlPhysicalColumn.TARGET_COLUMN) );
        
        // Equals
        join += " = "; //$NON-NLS-1$
        
        // Right side
        join += databaseMeta.quoteField(rightAlias );
        join += "."; //$NON-NLS-1$
        join += databaseMeta.quoteField((String) relation.relation.getToColumn().getProperty(SqlPhysicalColumn.TARGET_COLUMN) );
    }
    
    return join;
  }

  class AliasedLogicalRelationship {
    String leftAlias;
    String rightAlias;
    LogicalRelationship relation;
    AliasedLogicalRelationship(String left, String right, LogicalRelationship rel) {
      this.leftAlias = left;
      this.rightAlias = right;
      this.relation = rel;
    }
  }
  
  protected void traversePath(String alias, LogicalTable aliasedTable, Path aliasedPath, List<LogicalTable> aliasedTables, List<LogicalTable> defaultTables, List<AliasedPathLogicalTable> allTables, List<AliasedLogicalRelationship> allRelationships) {
    AliasedPathLogicalTable aliasedPathTable = new AliasedPathLogicalTable(alias, aliasedTable);
    if (allTables.contains(aliasedPathTable)) {
      allTables.add(aliasedPathTable);
    }
    allTables.add(aliasedPathTable);
    List<LogicalRelationship> cachedAliasedPath = new ArrayList<LogicalRelationship>();
    for (int i = 0; i < aliasedPath.size(); i++) {
      cachedAliasedPath.add(aliasedPath.getRelationship(i));
    }
    for (int i = 0; i < cachedAliasedPath.size(); i++) {
      LogicalRelationship rel = cachedAliasedPath.get(i);
      int index = -1;
      for (int j = 0; j < aliasedPath.size(); j++) {
        if (aliasedPath.getRelationship(j) == rel) {
          index = j;
          break;
        }
      }
      if (index == -1) {
        continue;
      }
      if (rel.isUsingTable(aliasedTable)) {
        // this needs to either be an aliased relation or a alias to default relation
        boolean joinsToADefaultTable = false;
        for (LogicalTable defaultTable : defaultTables) {
          if (rel.isUsingTable(defaultTable)) {
            boolean inAliasedTables = false;
            for (LogicalTable aliased : aliasedTables) { 
              if (defaultTable.equals(aliased)) {
                inAliasedTables = true;
              }
            }
          
            if (!inAliasedTables) {
              joinsToADefaultTable = true;
              // this relation will join to the default path 
              aliasedPath.removeRelationship(index);
              String leftAlias = null;
              String rightAlias = null;
              if (aliasedTable.equals(rel.getFromTable())) {
                leftAlias = alias;
                rightAlias = DEFAULT_ALIAS;
              } else {
                leftAlias = DEFAULT_ALIAS;
                rightAlias = alias;
              }
              allRelationships.add(new AliasedLogicalRelationship(leftAlias,rightAlias,rel));
            }
          }
        }
        if (!joinsToADefaultTable) {
            // this relation is joined to either an aliased or a soon to be aliased table
            aliasedPath.removeRelationship(index);
            // we need to add this to the aliased path list, along with the table we're joining to
            // check for uniqueness?
            allRelationships.add(new AliasedLogicalRelationship(alias,alias,rel));
            LogicalTable tbl = rel.getFromTable() == aliasedTable ? rel.getToTable() : rel.getFromTable();
            traversePath(alias, tbl, aliasedPath, aliasedTables, defaultTables, allTables, allRelationships);
        }
      }
    }
  }
}
