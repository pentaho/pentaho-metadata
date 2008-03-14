package org.pentaho.pms.mql;

import java.util.ArrayList;
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
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

/**
 * This class contains the SQL generation algorithm.
 * The primary entrance method into this class is 
 * getSQL()
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
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
  public void generateSelect(SQLQueryModel query, BusinessModel model, DatabaseMeta databaseMeta, List<Selection> selections, boolean disableDistinct, boolean group, String locale, Map<String, String> columnsMap) {
    query.setDistinct(!disableDistinct && !group);
    for (int i = 0; i < selections.size(); i++) {
      // in some database implementations, the "as" name has a finite length;
      // for instance, oracle cannot handle a name longer than 30 characters. 
      // So, we map a short name here to the longer id, and replace the id
      // later in the resultset metadata. 
      String alias = null;
      if(columnsMap != null){
        columnsMap.put("COL" + Integer.toString(i), selections.get(i).getBusinessColumn().getId()); //$NON-NLS-1$
        alias = databaseMeta.quoteField("COL" + Integer.toString(i)); //$NON-NLS-1$
      }else{
        alias = databaseMeta.quoteField(selections.get(i).getBusinessColumn().getId());
      }
      SQLAndTables sqlAndTables = getBusinessColumnSQL(model, selections.get(i).getBusinessColumn(), databaseMeta, locale);
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
  public void generateFromAndWhere(SQLQueryModel query, List<BusinessTable> usedBusinessTables, BusinessModel model, Path path, List<WhereCondition> conditions, DatabaseMeta databaseMeta, String locale) throws PentahoMetadataException {

    // FROM TABLES
    for (int i = 0; i < usedBusinessTables.size(); i++) {
      BusinessTable businessTable = usedBusinessTables.get(i);
      String schemaName = null;
      if (businessTable.getTargetSchema() != null) {
        schemaName = databaseMeta.quoteField(businessTable.getTargetSchema());
      }
      String tableName = databaseMeta.quoteField(businessTable.getTargetTable());
      query.addTable(databaseMeta.getSchemaTableCombination(schemaName, tableName),
          databaseMeta.quoteField(businessTable.getId()));
    }
    
    // JOIN CONDITIONS
    if (path != null) {
      for (int i = 0; i < path.size(); i++) {
        RelationshipMeta relation = path.getRelationship(i);
        String joinFormula = getJoin(model, relation, databaseMeta, locale);
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
        // The ones with aggregates in it are for the HAVING clause
        if (!condition.hasAggregate()) {
          String[] usedTables = condition.getPMSFormula().getBusinessTableIDs();
          query.addWhereFormula(condition.getPMSFormula().generateSQL(locale), first ? "AND" : condition.getOperator(), usedTables); //$NON-NLS-1$
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
  public void generateGroupBy(SQLQueryModel query, BusinessModel model, List<Selection> selections, DatabaseMeta databaseMeta, String locale) {
    // can be moved to selection loop
    for (Selection selection : selections) {
      BusinessColumn businessColumn = selection.getBusinessColumn();
      if (!businessColumn.hasAggregate()) {
    	SQLAndTables sqlAndTables = getBusinessColumnSQL(model, businessColumn, databaseMeta, locale);
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
  public void generateOrderBy(SQLQueryModel query, BusinessModel model, List<OrderBy> orderBy, DatabaseMeta databaseMeta, String locale, Map<String,String> columnsMap) {
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
        SQLAndTables sqlAndTables = getBusinessColumnSQL(model, businessColumn, databaseMeta, locale);
        query.addOrderBy(sqlAndTables.getSql(), alias, !orderItem.isAscending() ? OrderType.DESCENDING : null); //$NON-NLS-1$
      }
    }
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
   * @return a SQL query based on a column selection, conditions and a locale
   */
  public MappedQuery getSQL(BusinessModel model, List<Selection> selections, List<WhereCondition> conditions, List<OrderBy> orderBy, DatabaseMeta databaseMeta, String locale, boolean disableDistinct) throws PentahoMetadataException {
    SQLQueryModel query = new SQLQueryModel();
    //StringBuffer sql = new StringBuffer();
    Map<String,String> columnsMap = new HashMap<String,String>();
    
    // These are the tables involved in the field selection:
    List<BusinessTable> tabs = getTablesInvolved(model, selections, conditions, databaseMeta, locale);

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

      boolean group = hasFactsInIt(selections, conditions);

      generateSelect(query, model, databaseMeta, selections, disableDistinct, group, locale, columnsMap);
      generateFromAndWhere(query, usedBusinessTables, model, path, conditions, databaseMeta, locale);
      if (group) {
        generateGroupBy(query, model, selections, databaseMeta, locale);
      }
      generateOrderBy(query, model, orderBy, databaseMeta, locale, columnsMap);
    }

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(databaseMeta);
   
    return new MappedQuery(dialect.generateSelectStatement(query), columnsMap, selections);
  }

  protected List<BusinessTable> getTablesInvolved(BusinessModel model, List<Selection> selections, List<WhereCondition> conditions, DatabaseMeta databaseMeta, String locale) {
    Set<BusinessTable> treeSet = new TreeSet<BusinessTable>();

    for (Selection selection : selections) {
      // We need to figure out which tables are involved in the formula.
      // This could simply be the parent table, but it could also be another one too.
      // 
      // If we want to know all the tables involved in the query, we need to parse all the formula first
      // TODO: We re-use the static method below, maybe there is a better way to clean this up a bit.
      //
      SQLAndTables sqlAndTables = getBusinessColumnSQL(model, selection.getBusinessColumn(), databaseMeta, locale);
      for (BusinessTable businessTable : sqlAndTables.getUsedTables()) {
    	  treeSet.add(businessTable); //$NON-NLS-1$
      }
    }
    for(WhereCondition condition : conditions) {
      List cols = condition.getBusinessColumns();
      Iterator iter = cols.iterator();
      while (iter.hasNext()) {
        BusinessColumn col = (BusinessColumn)iter.next();
        BusinessTable businessTable = col.getBusinessTable();
        treeSet.add(businessTable); //$NON-NLS-1$
      }
    }
    return new ArrayList<BusinessTable>(treeSet);
  }
  
  public boolean hasFactsInIt(List<Selection> selections, List<WhereCondition> conditions) {
    for (Selection selection : selections) {
      if (selection.getBusinessColumn().hasAggregate())
        return true;
    }
    if (conditions != null) {
      for (WhereCondition condition : conditions) {
        if (condition.hasAggregate())
          return true;
      }
    }
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
      if (path.size() < minScore || (path.size() == minScore && path.score() < minSize))
        minPath = path;
    }

    return minPath;
  }

  protected List<BusinessTable> getNonSelectedTables(BusinessModel model, List<BusinessTable> selectedTables) {
    List<BusinessTableNeighbours> extra = new ArrayList<BusinessTableNeighbours>(model.nrBusinessTables());
    for (int i = 0; i < model.nrBusinessTables(); i++) {
      BusinessTable check = model.getBusinessTable(i);
      boolean found = false;
      for (int j = 0; j < selectedTables.size(); j++) {
        BusinessTable businessTable = selectedTables.get(j);
        if (check.equals(businessTable)) {
          found = true;
        }
      }

      if (!found) {
        BusinessTableNeighbours btn = new BusinessTableNeighbours();
        btn.businessTable = check;
        btn.nrNeighbours = model.getNrNeighbours(check, selectedTables);
        extra.add(btn);
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

  public static SQLAndTables getBusinessColumnSQL(BusinessModel businessModel, BusinessColumn column, DatabaseMeta databaseMeta, String locale)
  {
      if (column.isExact())
      { 
        // convert to sql using libformula subsystem
        try {
          // we'll need to pass in some context to PMSFormula so it can resolve aliases if necessary
          PMSFormula formula = new PMSFormula(businessModel, column.getBusinessTable(), databaseMeta, column.getFormula());
          formula.parseAndValidate();
          return new SQLAndTables(formula.generateSQL(locale), formula.getBusinessTables());
        } catch (PentahoMetadataException e) {
          // this is for backwards compatibility.
          // eventually throw any errors
          logger.error(Messages.getErrorString("BusinessColumn.ERROR_0001_FAILED_TO_PARSE_FORMULA", column.getFormula()), e); //$NON-NLS-1$
        }
        return new SQLAndTables(column.getFormula(), column.getBusinessTable());
      }
      else
      {
          String tableColumn = ""; //$NON-NLS-1$
          
          tableColumn += databaseMeta.quoteField( column.getBusinessTable().getId() );
          tableColumn += "."; //$NON-NLS-1$
          
          // TODO: WPG: instead of using formula, shouldn't we use the physical column's name?
          tableColumn += databaseMeta.quoteField( column.getFormula() );
          
          if (column.hasAggregate()) // For the having clause, for example: HAVING sum(turnover) > 100
          {
              return new SQLAndTables(getFunctionExpression(column, tableColumn, databaseMeta), column.getBusinessTable());
          }
          else
          {
              return new SQLAndTables(tableColumn,column.getBusinessTable());
          }
      }
  }

  public static String getFunctionExpression(BusinessColumn column, String tableColumn, DatabaseMeta databaseMeta) {
      String expression=getFunction(column, databaseMeta); //$NON-NLS-1$
      
      switch(column.getAggregationType().getType()) {
          case AggregationSettings.TYPE_AGGREGATION_COUNT_DISTINCT : expression+="(DISTINCT "+tableColumn+")"; break;   //$NON-NLS-1$ //$NON-NLS-2$
          default: expression+="("+tableColumn+")"; break;  //$NON-NLS-1$ //$NON-NLS-2$
      }
      
      return expression;
  }
  
  public static String getFunction(BusinessColumn column, DatabaseMeta databaseMeta) {
      String fn=""; //$NON-NLS-1$
      
      switch(column.getAggregationType().getType()) {
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

  public String getJoin(BusinessModel businessModel, RelationshipMeta relation, DatabaseMeta databaseMeta, String locale) {
    String join=""; //$NON-NLS-1$
    if (relation.isComplex()) {
      try {
        // parse join as MQL
        PMSFormula formula = new PMSFormula(businessModel, databaseMeta, relation.getComplexJoin());
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
        join  = databaseMeta.quoteField( relation.getFieldFrom().getBusinessTable().getId() );
        join += "."; //$NON-NLS-1$
        join += databaseMeta.quoteField( relation.getFieldFrom().getFormula() );
        
        // Equals
        join += " = "; //$NON-NLS-1$
        
        // Right side
        join += databaseMeta.quoteField( relation.getFieldTo().getBusinessTable().getId() );
        join += "."; //$NON-NLS-1$
        join += databaseMeta.quoteField( relation.getFieldTo().getFormula() );
    }
    
    return join;
  }
}
