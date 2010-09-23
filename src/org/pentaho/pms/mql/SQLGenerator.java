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
package org.pentaho.pms.mql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.dialect.JoinType;
import org.pentaho.pms.mql.dialect.SQLDialectFactory;
import org.pentaho.pms.mql.dialect.SQLDialectInterface;
import org.pentaho.pms.mql.dialect.SQLQueryModel;
import org.pentaho.pms.mql.dialect.SQLQueryModel.OrderType;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

/**
 * This class contains the SQL generation algorithm.
 * The primary entrance method into this class is 
 * getSQL()
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
 * @deprecated as of metadata 3.0.  please use org.pentaho.metadata.query.impl.sql.SqlGenerator
 */
public class SQLGenerator {
  
  private static final Log logger = LogFactory.getLog(SQLGenerator.class);
  
  /**
   * This private class is used to sort the business tables in terms of the number of neighbours they have. We use
   * this information to find the table best suited to provide the missing link between selected tables while doing
   * SQL generation.
   */
  protected class BusinessTableNeighbours implements Comparable<BusinessTableNeighbours> {
    public BusinessTable businessTable;

    public int nrNeighbours;

    public int compareTo(BusinessTableNeighbours obj) {
      if (nrNeighbours == obj.nrNeighbours) {
        return businessTable.compareTo(obj.businessTable);
      } else {
        return new Integer(nrNeighbours).compareTo(new Integer(obj.nrNeighbours));
      }
    }
  }
  
  /**
   * This method traverses the set of selections and renders those selections
   * to the SQL string buffer.  This method determines the SQL column aliases.
   * It also calls getBusinessColumnSQL() which renders each individual 
   * business column in three different ways.  Either as an MQL Formula, an
   * aggregate function, or as a standard SQL column.
   *
   * @param sql sql string buffer
   * @param model business model
   * @param databaseMeta database metadata
   * @param selections sql selections
   * @param disableDistinct if true, disable distinct rendering
   * @param group if true, disable distinct rendering
   * @param locale locale string
   * @param columnsMap map of column aliases to populate
   */
  public void generateSelect(SQLQueryModel query, BusinessModel model, DatabaseMeta databaseMeta, List<Selection> selections, boolean disableDistinct, boolean group, String locale, Map<BusinessTable, String> tableAliases, Map<String, String> columnsMap) {
    query.setDistinct(!disableDistinct && !group);
    for (int i = 0; i < selections.size(); i++) {
      // in some database implementations, the "as" name has a finite length;
      // for instance, oracle cannot handle a name longer than 30 characters. 
      // So, we map a short name here to the longer id, and replace the id
      // later in the resultset metadata. 
      String alias = null;
      if(columnsMap != null){
        alias = databaseMeta.generateColumnAlias(i, selections.get(i).getBusinessColumn().getId());
        columnsMap.put(alias, selections.get(i).getBusinessColumn().getId());
        alias = databaseMeta.quoteField(alias);
      }else{
        alias = databaseMeta.quoteField(selections.get(i).getBusinessColumn().getId());
      }
      SQLAndTables sqlAndTables = getBusinessColumnSQL(model, selections.get(i), tableAliases, databaseMeta, locale);
      query.addSelection(sqlAndTables.getSql(), alias);
    }
  }
  
  /**
   * This method first traverses the set of included business tables 
   * and renders those tables to the SQL string buffer. Second, it traverses
   * the list of joins and renders those in the WHERE clause. Finally, it 
   * traverses the constraints and adds them to the where or having clauses.
   * 
   * @param query sql query model
   * @param usedBusinessTables used business tables in query
   * @param model the current business model
   * @param path the join path
   * @param conditions the where conditions 
   * @param databaseMeta database metadata
   * @param locale locale string
   */
  public void generateFromAndWhere(SQLQueryModel query, List<BusinessTable> usedBusinessTables, BusinessModel model, Path path, List<WhereCondition> conditions, Map<BusinessTable, String> tableAliases, DatabaseMeta databaseMeta, String locale) throws PentahoMetadataException {

    // ConceptInterface concept = null;
    // ConceptPropertyInterface delayOuterJoin = null;
    // Boolean delayTableConditionOnOuterJoin = null;
    
    // FROM TABLES
    for (int i = 0; i < usedBusinessTables.size(); i++) {
      BusinessTable businessTable = usedBusinessTables.get(i);

      // ToDo: Allow table-level override for outer-joins on specific tables
      // concept = businessTable.getConcept();
      //      delayOuterJoin = concept.getProperty("delay_table_outer_join_conditions");
      //      if ((delayOuterJoin != null) && (delayOuterJoin.getType().equals(ConceptPropertyType.BOOLEAN) ) ) {
      //        delayTableConditionOnOuterJoin = (Boolean)delayOuterJoin.getValue();
      //      } else {
      //        delayTableConditionOnOuterJoin = null;
      //      }
      
      String schemaName = null;
      if (businessTable.getTargetSchema() != null) {
        schemaName = databaseMeta.quoteField(businessTable.getTargetSchema());
      }
      
      // this code allows subselects to drive the physical model.
      // TODO: make this key off a metadata flag vs. the 
      // beginning of the table name.
      
      String tableName = businessTable.getTargetTable();
      if (tableName.toLowerCase().startsWith("select ")) {
        tableName = "(" + tableName + ")"; 
      } else {
        tableName = databaseMeta.quoteField(businessTable.getTargetTable());
      }
      
      // if (delayTableConditionOnOuterJoin == null) {
      query.addTable(databaseMeta.getSchemaTableCombination(schemaName, tableName),
          databaseMeta.quoteField(tableAliases.get(businessTable)));
      // } else {
      //  query.addTable(databaseMeta.getSchemaTableCombination(schemaName, tableName),
      //      databaseMeta.quoteField(tableAliases.get(businessTable)), delayTableConditionOnOuterJoin.booleanValue());
      // }
    }
    
    // JOIN CONDITIONS
    if (path != null) {
      for (int i = 0; i < path.size(); i++) {
        RelationshipMeta relation = path.getRelationship(i);
        String joinFormula = getJoin(model, relation, tableAliases, databaseMeta, locale);
        String joinOrderKey = relation.getJoinOrderKey();
        JoinType joinType;
        switch(relation.getJoinType()) {
        case RelationshipMeta.TYPE_JOIN_LEFT_OUTER : joinType = JoinType.LEFT_OUTER_JOIN; break;
        case RelationshipMeta.TYPE_JOIN_RIGHT_OUTER : joinType = JoinType.RIGHT_OUTER_JOIN; break;
        case RelationshipMeta.TYPE_JOIN_FULL_OUTER : joinType = JoinType.FULL_OUTER_JOIN; break;
        default: joinType = JoinType.INNER_JOIN; break;
        }
        
        String leftTableName = databaseMeta.getQuotedSchemaTableCombination(relation.getTableFrom().getTargetSchema(), relation.getTableFrom().getTargetTable());
        String leftTableAlias = relation.getTableFrom().getId();
        String rightTableName = databaseMeta.getQuotedSchemaTableCombination(relation.getTableTo().getTargetSchema(), relation.getTableTo().getTargetTable());
        String rightTableAlias = relation.getTableTo().getId();
        
        query.addJoin(leftTableName, leftTableAlias, rightTableName, rightTableAlias, joinType, joinFormula, joinOrderKey);
        // query.addWhereFormula(joinFormula, "AND"); //$NON-NLS-1$
      }
    }
    
    // WHERE CONDITIONS
    if (conditions != null) {
      boolean first = true;
      for (WhereCondition condition : conditions) {
        
        // configure formula to use table aliases
        condition.getPMSFormula().setTableAliases(tableAliases);
        
        // The ones with aggregates in it are for the HAVING clause
        if (!condition.hasAggregate()) {
          
          String sqlFormula = condition.getPMSFormula().generateSQL(locale);
          String[] usedTables = condition.getPMSFormula().getBusinessTableIDs();
          query.addWhereFormula(sqlFormula, first ? "AND" : condition.getOperator(), usedTables); //$NON-NLS-1$
          first = false;
        } else {
          query.addHavingFormula(condition.getPMSFormula().generateSQL(locale), condition.getOperator());
        }
      }
    }
  }
  
  /**
   * this method adds the group by statements to the query model
   * 
   * @param query sql query model
   * @param model business model
   * @param selections list of selections
   * @param databaseMeta database info
   * @param locale locale string
   */
  public void generateGroupBy(SQLQueryModel query, BusinessModel model, List<Selection> selections, Map<BusinessTable, String> tableAliases, DatabaseMeta databaseMeta, String locale) {
    // can be moved to selection loop
    for (Selection selection : selections) {
      // Check if the column has any nested aggregation in there like a calculated column : SUM(a)/SUM(b) with no aggregation set.
      //
      if (!hasFactsInIt(model, selection, databaseMeta, locale)) {
    	SQLAndTables sqlAndTables = getBusinessColumnSQL(model, selection, tableAliases, databaseMeta, locale);
        query.addGroupBy(sqlAndTables.getSql(), null);
      }
    }
  }
  
  /**
   * this method adds the order by statements to the query model
   * 
   * @param query sql query model
   * @param model business model
   * @param orderBy list of order bys
   * @param databaseMeta database info
   * @param locale locale string
   */
  public void generateOrderBy(SQLQueryModel query, BusinessModel model, List<OrderBy> orderBy, DatabaseMeta databaseMeta, String locale, Map<BusinessTable, String> tableAliases, Map<String,String> columnsMap) {
    if (orderBy != null) {
      for (OrderBy orderItem : orderBy) {
        BusinessColumn businessColumn = orderItem.getSelection().getBusinessColumn();
        String alias=null;
        if (columnsMap!=null) {
	        // The column map is a unique mapping of Column alias to the column ID
	        // Here we have the column ID and we need the alias.
	        // We need to do the order by on the alias, not the column name itself.
        	// For most databases, it can be both, but the alias is more standard.
        	//
        	// Using the column name and not the alias caused an issue on Apache Derby.
	        //
	        for (String key : columnsMap.keySet()) {
	        	String value = columnsMap.get(key);
	        	if (value.equals(businessColumn.getId())) {
	        		// Found it: the alias is the key
	        		alias = key;
	        		break;
	        	}
	        }
        }
        SQLAndTables sqlAndTables = getBusinessColumnSQL(model, orderItem.getSelection(), tableAliases, databaseMeta, locale);
        query.addOrderBy(sqlAndTables.getSql(), alias, !orderItem.isAscending() ? OrderType.DESCENDING : null); //$NON-NLS-1$
      }
    }
  }
  
  private static String genString(String base, int val) {
    if (val < 10) {
      return base + "0" + val;
    }
    return base + val;
  }
  
  /**
   * this method generates a unique alias name, limited to a specific length
   * 
   * @param alias The name of the original alias to use
   * @param maxLength the maximum length the alias can be
   * @param existingAliases existing aliases
   * 
   * @return
   */
  public static String generateUniqueAlias(String alias, int maxLength, Collection<String> existingAliases) {
    if (alias.length() <= maxLength) {
      if (!existingAliases.contains(alias)) {
        return alias;
      } else {
        if (alias.length() > maxLength - 2) {
          alias = alias.substring(0, maxLength - 2);
        }
      }
    } else {
      alias = alias.substring(0, maxLength - 2);
    }

    int id = 1;
    String aliasWithId = genString(alias, id);
    while (existingAliases.contains(aliasWithId)) {
      aliasWithId = genString(alias, ++id);
    }
    return aliasWithId;
  }
  
  /**
   * returns the generated SQL and additional metadata
   * 
   * @param selections The selected business columns
   * @param conditions the conditions to apply (null = no conditions)
   * @param orderBy the ordering (null = no order by clause)
   * @param databaseMeta the meta info which determines the SQL generated.
   * @param locale the locale
   * @param disableDistinct if true, disables default behavior of using DISTINCT when there
   * are no groupings.
   * @param securityConstraint if provided, applies a global security constraint to the query
   * 
   * @return a SQL query based on a column selection, conditions and a locale
   */
  public MappedQuery getSQL(
      BusinessModel model, 
      List<Selection> selections, 
      List<WhereCondition> conditions, 
      List<OrderBy> orderBy, 
      DatabaseMeta databaseMeta, 
      String locale, 
      boolean disableDistinct, 
      WhereCondition securityConstraint) throws PentahoMetadataException {
    SQLQueryModel query = new SQLQueryModel();
    // Get settings for the query model
    ConceptInterface concept = model.getConcept();
    ConceptPropertyInterface delayOuterJoin = concept.getProperty("delay_outer_join_conditions");
    if ((delayOuterJoin != null) && (delayOuterJoin.getType().equals(ConceptPropertyType.BOOLEAN) ) ) {
      Boolean value = (Boolean)delayOuterJoin.getValue();
      query.setDelayOuterJoinConditions(value.booleanValue());
    }

    //StringBuffer sql = new StringBuffer();
    Map<String,String> columnsMap = new HashMap<String,String>();
    
    // These are the tables involved in the field selection
    //
    List<BusinessTable> tabs = getTablesInvolved(model, selections, conditions, orderBy, databaseMeta, locale, securityConstraint);

    // Now get the shortest path between these tables.
    Path path = getShortestPathBetween(model, tabs);
    if (path == null) {
      throw new PentahoMetadataException(Messages.getErrorString("BusinessModel.ERROR_0001_FAILED_TO_FIND_PATH")); //$NON-NLS-1$
    }

    List<BusinessTable> usedBusinessTables = path.getUsedTables();
    if (path.size() == 0) {
      // just a selection from 1 table: pick any column...
      if (selections.size() > 0) // Otherwise, why bother, right?
      {
        usedBusinessTables.add(selections.get(0).getBusinessColumn().getBusinessTable());
      }
    }

    if (usedBusinessTables.size() > 0) {

      // generate tableAliases mapping
      
      int maxAliasNameWidth = SQLDialectFactory.getSQLDialect(databaseMeta).getMaxTableNameLength();
      Map<BusinessTable, String> tableAliases = new HashMap<BusinessTable, String>();
      for (BusinessTable table : usedBusinessTables) {
        String uniqueAlias = generateUniqueAlias(table.getId(), maxAliasNameWidth, tableAliases.values());
        tableAliases.put(table, uniqueAlias);
      }
      
      boolean group = hasFactsInIt(model, selections, conditions, databaseMeta, locale);

      generateSelect(query, model, databaseMeta, selections, disableDistinct, group, locale, tableAliases, columnsMap);
      generateFromAndWhere(query, usedBusinessTables, model, path, conditions, tableAliases, databaseMeta, locale);
      if (group) {
        generateGroupBy(query, model, selections, tableAliases, databaseMeta, locale);
      }
      generateOrderBy(query, model, orderBy, databaseMeta, locale, tableAliases, columnsMap);
      
      if (securityConstraint != null) {
        // apply current table aliases
        securityConstraint.getPMSFormula().setTableAliases(tableAliases);
        
        // generate sql
        String sqlFormula = securityConstraint.getPMSFormula().generateSQL(locale);
        query.setSecurityConstraint(sqlFormula, securityConstraint.hasAggregate());
      }
    }

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(databaseMeta);
   
    String sqlStr = dialect.generateSelectStatement(query);
    if (logger.isTraceEnabled()) {
      logger.trace(sqlStr);
    }

    return new MappedQuery(sqlStr, columnsMap, selections);
  }

  protected List<BusinessTable> getTablesInvolved(
      BusinessModel model, 
      List<Selection> selections, 
      List<WhereCondition> conditions, 
      List<OrderBy> orderBy, 
      DatabaseMeta databaseMeta, 
      String locale,
      WhereCondition securityConstraint) {
    Set<BusinessTable> treeSet = new TreeSet<BusinessTable>();

    // Figure out which tables are involved in the SELECT
    //
    for (Selection selection : selections) {
      // We need to figure out which tables are involved in the formula.
      // This could simply be the parent table, but it could also be another one too.
      // 
      // If we want to know all the tables involved in the query, we need to parse all the formula first
      // TODO: We re-use the static method below, maybe there is a better way to clean this up a bit.
      //
      
      
      SQLAndTables sqlAndTables = getBusinessColumnSQL(model, selection, null, databaseMeta, locale);
      
  	  // Add the involved tables to the list...
  	  //
      for (BusinessTable businessTable : sqlAndTables.getUsedTables()) {
    	  treeSet.add(businessTable);
      }
    }
    
    // Figure out which tables are involved in the WHERE
    //
    for(WhereCondition condition : conditions) {
      List<Selection> cols = condition.getBusinessColumns();
      for (Selection selection : cols) {
        BusinessTable businessTable = selection.getBusinessColumn().getBusinessTable();
        treeSet.add(businessTable); //$NON-NLS-1$
      }
    }
    
    // Figure out which tables are involved in the ORDER BY
    //
    for(OrderBy order : orderBy) {
    	SQLAndTables sqlAndTables = getBusinessColumnSQL(model, order.getSelection(), null, databaseMeta, locale);
    	
    	// Add the involved tables to the list...
    	//
        for (BusinessTable businessTable : sqlAndTables.getUsedTables()) {
      	  treeSet.add(businessTable);
        }
      }
    
    // find any tables listed in the security constraint

    if (securityConstraint != null) {
      List cols = securityConstraint.getBusinessColumns();
      Iterator iter = cols.iterator();
      while (iter.hasNext()) {
        Selection col = (Selection)iter.next();
        BusinessTable businessTable = col.getBusinessColumn().getBusinessTable();
        treeSet.add(businessTable); //$NON-NLS-1$
      }
    }
    
    return new ArrayList<BusinessTable>(treeSet);
  }
  
  public boolean hasFactsInIt(BusinessModel model, List<Selection> selections, List<WhereCondition> conditions, DatabaseMeta databaseMeta, String locale) {
	// We don't have to simply check the columns in the selection
	// If the column is made up of a calculation, we need to verify that there is no aggregation in the calculation too.
	//
	// For example, this is the case for the calculation of a ration: SUM(A) / SUM(B).
	// The resulting ratio will not have an aggregate set (none) but the used business columns (A and B) will have one set.
	// As such, we need to do this recursively.
	//
    for (Selection selection : selections) {
    
      if (hasFactsInIt(model, selection, databaseMeta, locale)) {
    	  return true;
      }
    }
    
    // Verify the conditions in the same way too
    //
    if (conditions != null) {
      for (WhereCondition condition : conditions) {
    	  for (Selection conditionColumn : condition.getBusinessColumns()) {
    	      if (hasFactsInIt(model, conditionColumn, databaseMeta, locale)) {
    	    	  return true;
    	      }
    	  }
      }
    }
    return false;
  }
  
  /**
   * See if the business column specified has a fact in it.<br>
   * We verify the formula specified in the column to see if it contains calculations with any aggregated column.<br>
   * We even do this nested down through the used business columns in the formula.<br>
   * 
   * @param model the business model to reference
   * @param businessColumn the column to verify for facts
   * @param databaseMeta the database to reference
   * @param locale the locale to use
   * @return true if the business column uses any aggregation in the formula or is aggregated itself.
   */
  public boolean hasFactsInIt(BusinessModel model, Selection businessColumn, DatabaseMeta databaseMeta, String locale) {
	  if (businessColumn.hasAggregate()) return true;

	  // Parse the formula in the business column to see which tables and columns are involved...
      //
      SQLAndTables sqlAndTables = getBusinessColumnSQL(model, businessColumn, null, databaseMeta, locale);
      for (Selection column : sqlAndTables.getUsedColumns()) {
	      if (column.hasAggregate()) {
	        return true;
	      }
      }
      
      // Nothing found
      //
      return false;

  }
    
  public <T> List<List<T>> getSubsetsOfSize(int size, List<T> list) {
    if (size <= 0) return new ArrayList<List<T>>();
    return getSubsets(0, size, new ArrayList<T>(), list);
  }
  
  // recursive function to generate all subsets
  public static <T> List<List<T>> getSubsets(int indexToStart, int subSize, List<T> toClone, List<T> origList) {
    List<List<T>> allSubsets = new ArrayList<List<T>>();
    for (int i = indexToStart; i <= origList.size() - subSize; i++) {
      List<T> subset = new ArrayList<T>(toClone);
      subset.add(origList.get(i));
      if (subSize == 1) {
        allSubsets.add(subset);
      } else {
        allSubsets.addAll(getSubsets(i + 1, subSize - 1, subset, origList));
      }
    }
    return allSubsets;
  }
  
  /**
   * This method determines the shortest path between the list of included
   * tables within the MQL Query. The algorithm first determines if there is an
   * existing path between all selected tables.  If not, the algorithm 
   * continues to add new tables to the list until a path is discovered.  If 
   * more than one path is available with a certain number of tables, the 
   * algorithm uses the relative size values if specified to determine which 
   * path to traverse in the SQL Join.
   * 
   * @param model the business model
   * @param tables include tables
   * @return shortest path
   */
  public Path getShortestPathBetween(BusinessModel model, List<BusinessTable> tables) {
    // We have the business tables.
    // Let's try to see if they are somehow connected first.
    // If they are not, we add a table that's not being used so far and add it to the equation.
    // We can continue like that until we connect all tables with joins.

    // This is a list of all the paths that we could find between all the tables...
    List<Path> paths = new ArrayList<Path>();

    // Here are the tables we need to link it all together.
    List<BusinessTable> origSelectedTables = new ArrayList<BusinessTable>(tables);
    boolean allUsed = (tables.size() == 0);
    // These are the tables that are not yet used
    List<BusinessTable> notSelectedTables = getNonSelectedTables(model, origSelectedTables);

    for (int ns = 0; ns <= notSelectedTables.size() && !allUsed; ns++) {
    
      // find unique combinations of notSelectedTables of size NS
      List<List<BusinessTable>> uniqueCombos = getSubsetsOfSize(ns, notSelectedTables);
      if (ns == 0) {
        uniqueCombos.add(new ArrayList<BusinessTable>());
      }
        
      // add all the selected tables to this list
      for (int i = 0; i < uniqueCombos.size(); i++) {
        List<BusinessTable> uc = uniqueCombos.get(i);
        uc.addAll(origSelectedTables);
      }
    
      for (int p = 0; p < uniqueCombos.size(); p++) {
      
        List selectedTables = (List)uniqueCombos.get(p);
        Path path = new Path();
        
        // Generate all combinations of the selected tables...
        for (int i = 0; i < selectedTables.size(); i++) {
          for (int j = i + 1; j < selectedTables.size(); j++) {
            BusinessTable one = (BusinessTable) selectedTables.get(i);
            BusinessTable two = (BusinessTable) selectedTables.get(j);

            // See if we have a relationship that goes from one to two...
            RelationshipMeta relationship = model.findRelationshipUsing(one, two);
            if (relationship != null && !path.contains(relationship)) {
              path.addRelationship(relationship);
            }
          }
  
          // We need to have (n-1) relationships for n tables, otherwise we will not connect everything.
          if (path.size() == selectedTables.size() - 1) {
            // This is a valid path, the first we find here is probably the shortest
            paths.add(path);
            // We can stop now.
            allUsed = true;
          }
        }
      }
    }

    // Now, off all the paths, look for the shortest number of relationships
    // If we have the same number of relationships, get the one with the lowest total relative size.

    int minSize = Integer.MAX_VALUE;
    int minScore = Integer.MAX_VALUE;
    Path minPath = null;
    for (int i = 0; i < paths.size(); i++) {
      Path path = (Path) paths.get(i);
      if (path.size() < minScore || (path.size() == minSize && path.score() < minScore)) {
        minPath = path;
        minScore = path.score();
        minSize = path.size();
      }
    }
    return minPath; 
  }

  protected List<BusinessTable> getNonSelectedTables(BusinessModel model, List<BusinessTable> selectedTables) {
    List<BusinessTableNeighbours> extra = new ArrayList<BusinessTableNeighbours>(model.nrBusinessTables());
    List<BusinessTable> unused = new ArrayList<BusinessTable>();
    List<BusinessTable> used = new ArrayList<BusinessTable>(selectedTables);
    
    // the first part of this algorithm looks for all the tables that are connected to the selected 
    // tables in any way.  We loop through all the tables until there are no more connections
    
    for (int i = 0; i < model.nrBusinessTables(); i++) {
      unused.add(model.getBusinessTable(i));
    }
    
    boolean anyFound = true;
    
    // iterate over the list until there are no more neighbors
    while (anyFound) {
      anyFound = false;
      Iterator<BusinessTable> iter = unused.iterator();
      while (iter.hasNext()) {
        boolean found = false;        
        BusinessTable check = iter.next(); // unused.get(i);
        for (int j = 0; j < used.size(); j++) {
          BusinessTable businessTable = used.get(j);
          if (check.equals(businessTable)) {
            found = true;
          }
        }
        if (!found) {
          BusinessTableNeighbours btn = new BusinessTableNeighbours();
          btn.businessTable = check;
          btn.nrNeighbours = model.getNrNeighbours(check, used);
          if (btn.nrNeighbours > 0) {
            extra.add(btn);
            used.add(check);
            // remove check from the unused list
            iter.remove();
            anyFound = true;
          }
        }
      }
    }

    // OK, we now have a number of tables, but we want to sort this list
    // The tables with the highest numbers of neighbours should be placed first. (descending)
    //
    Collections.sort(extra);

    List<BusinessTable> retval = new ArrayList<BusinessTable>(extra.size());
    for (int i = 0; i < extra.size(); i++) {
      BusinessTableNeighbours btn = extra.get(i);
      // If the number of neighbours is 0, there is no point in returning the table for the SQL generation
      // There is no way the table can connect to the selected tables anyway as there are no neighbours.
      //
      if (btn.nrNeighbours > 0) {
        retval.add(0, btn.businessTable);
      }
    }

    return retval;
  }

  public static SQLAndTables getBusinessColumnSQL(BusinessModel businessModel, Selection column, Map<BusinessTable, String> tableAliases, DatabaseMeta databaseMeta, String locale)
  {
      if (column.getBusinessColumn().isExact())
      { 
        // convert to sql using libformula subsystem
        try {
          // we'll need to pass in some context to PMSFormula so it can resolve aliases if necessary
          PMSFormula formula = new PMSFormula(businessModel, column.getBusinessColumn().getBusinessTable(), databaseMeta, column.getBusinessColumn().getFormula(), tableAliases);
          formula.parseAndValidate();
          
          String formulaSql = formula.generateSQL(locale);
          
          // check for old style, where function is hardcoded in the model.
          if (column.hasAggregate() && !hasAggregateDefinedAlready(formulaSql, databaseMeta)) {
            formulaSql = getFunctionExpression(column, formulaSql, databaseMeta);
          }
          
          return new SQLAndTables(formulaSql, formula.getBusinessTables(), formula.getBusinessColumns());
        } catch (PentahoMetadataException e) {
          // this is for backwards compatibility.
          // eventually throw any errors
          logger.error(Messages.getErrorString("BusinessColumn.ERROR_0001_FAILED_TO_PARSE_FORMULA", column.getBusinessColumn().getFormula()), e); //$NON-NLS-1$

          // Report just this table and column as being used along with the formula.
          //
          return new SQLAndTables(column.getBusinessColumn().getFormula(), column.getBusinessColumn().getBusinessTable(), column);
        }
      }
      else
      {
          String tableColumn = ""; //$NON-NLS-1$
          
          // this step is required because this method is called in two contexts.  The first
          // call determines all the tables involved, making it impossible to guarantee
          // unique aliases.
          
          String tableAlias = null;
          if (tableAliases != null) {
            tableAlias = tableAliases.get(column.getBusinessColumn().getBusinessTable());
          } else {
            tableAlias = column.getBusinessColumn().getBusinessTable().getId(); 
          }
          tableColumn += databaseMeta.quoteField( tableAlias );
          tableColumn += "."; //$NON-NLS-1$
          
          // TODO: WPG: instead of using formula, shouldn't we use the physical column's name?
          tableColumn += databaseMeta.quoteField( column.getBusinessColumn().getFormula() );
          
          if (column.hasAggregate()) // For the having clause, for example: HAVING sum(turnover) > 100
          {
              return new SQLAndTables(getFunctionExpression(column, tableColumn, databaseMeta), column.getBusinessColumn().getBusinessTable(), column);
          }
          else
          {
              return new SQLAndTables(tableColumn, column.getBusinessColumn().getBusinessTable(), column);
          }
      }
  }

  // This method is for backwards compatibility of already defined
  // isExact formulas that may contain at the root an aggregate function.
  private static boolean hasAggregateDefinedAlready(String sql, DatabaseMeta databaseMeta) {
    String trimmed = sql.trim();
    return 
      trimmed.startsWith(databaseMeta.getFunctionAverage() + "(") ||
      trimmed.startsWith(databaseMeta.getFunctionCount() + "(") ||
      trimmed.startsWith(databaseMeta.getFunctionMaximum() + "(") ||
      trimmed.startsWith(databaseMeta.getFunctionMinimum()  + "(") ||
      trimmed.startsWith(databaseMeta.getFunctionSum() + "(");
  }
  
  public static String getFunctionExpression(Selection column, String tableColumn, DatabaseMeta databaseMeta) {
      String expression=getFunction(column, databaseMeta); //$NON-NLS-1$
      
      switch(column.getActiveAggregationType().getType()) {
          case AggregationSettings.TYPE_AGGREGATION_COUNT_DISTINCT : expression+="(DISTINCT "+tableColumn+")"; break;   //$NON-NLS-1$ //$NON-NLS-2$
          default: expression+="("+tableColumn+")"; break;  //$NON-NLS-1$ //$NON-NLS-2$
      }
      
      return expression;
  }
  
  public static String getFunction(Selection column, DatabaseMeta databaseMeta) {
      String fn=""; //$NON-NLS-1$
      
      switch(column.getActiveAggregationType().getType()) {
          case AggregationSettings.TYPE_AGGREGATION_AVERAGE: fn=databaseMeta.getFunctionAverage(); break;
          case AggregationSettings.TYPE_AGGREGATION_COUNT_DISTINCT :
          case AggregationSettings.TYPE_AGGREGATION_COUNT  : fn=databaseMeta.getFunctionCount(); break;
          case AggregationSettings.TYPE_AGGREGATION_MAXIMUM: fn=databaseMeta.getFunctionMaximum(); break;
          case AggregationSettings.TYPE_AGGREGATION_MINIMUM: fn=databaseMeta.getFunctionMinimum(); break;
          case AggregationSettings.TYPE_AGGREGATION_SUM    : fn=databaseMeta.getFunctionSum(); break;
          default: break;
      }
      
      return fn;
  }

  public String getJoin(BusinessModel businessModel, RelationshipMeta relation, Map<BusinessTable, String> tableAliases, DatabaseMeta databaseMeta, String locale) {
    String join=""; //$NON-NLS-1$
    if (relation.isComplex()) {
      try {
        // parse join as MQL
        PMSFormula formula = new PMSFormula(businessModel, databaseMeta, relation.getComplexJoin(), tableAliases);
        formula.parseAndValidate();
        join = formula.generateSQL(locale);
      } catch(PentahoMetadataException e) {
        // backward compatibility, deprecate
        logger.error(Messages.getErrorString("MQLQueryImpl.ERROR_0017_FAILED_TO_PARSE_COMPLEX_JOIN", relation.getComplexJoin()), e); //$NON-NLS-1$
        join = relation.getComplexJoin();
      }
    } else if (relation.getTableFrom() != null && relation.getTableTo() != null && 
               relation.getFieldFrom() !=null && relation.getFieldTo() != null)
    {
            
        // Left side
        String leftTableAlias = null;
        if (tableAliases != null) {
          leftTableAlias = tableAliases.get(relation.getFieldFrom().getBusinessTable());
        } else {
          leftTableAlias = relation.getFieldFrom().getBusinessTable().getId();
        }
      
        join  = databaseMeta.quoteField( leftTableAlias );
        join += "."; //$NON-NLS-1$
        join += databaseMeta.quoteField( relation.getFieldFrom().getFormula() );
        
        // Equals
        join += " = "; //$NON-NLS-1$
        
        // Right side
        String rightTableAlias = null;
        if (tableAliases != null) {
          rightTableAlias = tableAliases.get(relation.getFieldTo().getBusinessTable());  
        } else {
          rightTableAlias = relation.getFieldTo().getBusinessTable().getId();
        }
        
        join += databaseMeta.quoteField( rightTableAlias );
        join += "."; //$NON-NLS-1$
        join += databaseMeta.quoteField( relation.getFieldTo().getFormula() );
    } else {
      logger.error(Messages.getErrorString("SQLGenerator.ERROR_0001_INVALID_RELATION", relation.toString())); //$NON-NLS-1$
    }
    
    return join;
  }
}
