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
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.database.DatabaseMeta;

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
  public void generateSelect(StringBuffer sql, BusinessModel model, DatabaseMeta databaseMeta, List<? extends Selection> selections, boolean disableDistinct, boolean group, String locale, Map<String, String> columnsMap) {
    
    sql.append("SELECT "); //$NON-NLS-1$
    
    if (!disableDistinct && !group) {
      sql.append("DISTINCT "); //$NON-NLS-1$
    }
    sql.append(Const.CR);

    for (int i = 0; i < selections.size(); i++) {
      if (i > 0) {
        sql.append("         ,"); //$NON-NLS-1$
      } else {
        sql.append("          "); //$NON-NLS-1$
      }
      sql.append(getBusinessColumnSQL(model, selections.get(i).getBusinessColumn(), databaseMeta, locale));
      sql.append(" AS "); //$NON-NLS-1$

      // in some database implementations, the "as" name has a finite length;
      // for instance, oracle cannot handle a name longer than 30 characters. 
      // So, we map a short name here to the longer id, and replace the id
      // later in the resultset metadata. 

      if(columnsMap != null){
        columnsMap.put("COL" + Integer.toString(i), selections.get(i).getBusinessColumn().getId()); //$NON-NLS-1$
        sql.append(databaseMeta.quoteField("COL" + Integer.toString(i))); //$NON-NLS-1$
      }else{
        sql.append(databaseMeta.quoteField(selections.get(i).getBusinessColumn().getId()));
      }
      sql.append(Const.CR);
    }
  }
  
  /**
   * This method traverses the set of included business tables 
   * and renders those tables to the SQL string buffer.
   * 
   * @param sql sql string buffer
   * @param usedBusinessTables used business tables in query
   * @param databaseMeta database metadata
   * @param locale locale string
   */
  public void generateFrom(StringBuffer sql, List<BusinessTable> usedBusinessTables, DatabaseMeta databaseMeta, String locale) {
    
    sql.append("FROM ").append(Const.CR); //$NON-NLS-1$
    
    for (int i = 0; i < usedBusinessTables.size(); i++) {
      BusinessTable businessTable = usedBusinessTables.get(i);
      if (i > 0) {
        sql.append("         ,"); //$NON-NLS-1$
      } else {
        sql.append("          "); //$NON-NLS-1$
      }
      String schemaName = null;
      if (businessTable.getTargetSchema() != null) {
        schemaName = databaseMeta.quoteField(businessTable.getTargetSchema());
      }
      String tableName = databaseMeta.quoteField(businessTable.getTargetTable());
      sql.append(databaseMeta.getSchemaTableCombination(schemaName, tableName) + " " //$NON-NLS-1$
          + databaseMeta.quoteField(businessTable.getDisplayName(locale)));
      sql.append(Const.CR);
    }
  }
  
  public boolean generateJoins(StringBuffer sql, BusinessModel model, Path path, DatabaseMeta databaseMeta, String locale) {
    boolean whereAdded = false;
    if (path != null) {
      for (int i = 0; i < path.size(); i++) {
        if (!whereAdded) {
          sql.append("WHERE ").append(Const.CR); //$NON-NLS-1$
          whereAdded = true;
        }
        RelationshipMeta relation = path.getRelationship(i);

        if (i > 0) {
          sql.append("      AND "); //$NON-NLS-1$
        } else {
          sql.append("          "); //$NON-NLS-1$
        }
        sql.append(getJoin(model, relation, databaseMeta, locale));
        sql.append(Const.CR);
      }
    }
    return whereAdded;
  }
  
  /**
   * This method renders all WhereCondition's that are not part of an aggregate column.

   * @param sql sql string buffer
   * @param whereAdded
   * @param conditions
   * @param locale
   * @throws PentahoMetadataException
   */
  public void generateWhere(StringBuffer sql, boolean whereAdded, List<WhereCondition> conditions, String locale) throws PentahoMetadataException {
    // WHERE from conditions
    //
    if (conditions != null) {
      boolean bracketOpen = false;
      boolean justOpened = false;
      for (WhereCondition condition : conditions) {
        // The ones with aggregates in it are for the HAVING clause
        //
        if (!condition.hasAggregate()) {
          if (!whereAdded) {
            sql.append("WHERE ").append(Const.CR); //$NON-NLS-1$
            whereAdded = true;
            justOpened = true;
          } else if (!bracketOpen) {
            sql.append("      AND ( ").append(Const.CR); //$NON-NLS-1$
            bracketOpen = true;
            justOpened = true;
          }
          sql.append("             ").append(condition.getWhereClause(locale, !justOpened)); //$NON-NLS-1$
          sql.append(Const.CR);
          justOpened = false;
        }
      }
      if (bracketOpen) {
        sql.append("          )").append(Const.CR); //$NON-NLS-1$
      }
    }
  }
  
  public void generateGroupBy(StringBuffer sql, BusinessModel model, List<? extends Selection> selections, DatabaseMeta databaseMeta, String locale) {
    boolean groupByAdded = false;
    boolean first = true;
    for (Selection selection : selections) {
      BusinessColumn businessColumn = selection.getBusinessColumn();

      if (!businessColumn.hasAggregate()) {
        if (!groupByAdded) {
          sql.append("GROUP BY ").append(Const.CR); //$NON-NLS-1$
          groupByAdded = true;
        }

        if (!first) {
          sql.append("         ,"); //$NON-NLS-1$
        } else {
          sql.append("          "); //$NON-NLS-1$
        }
        first = false;
        sql.append(getBusinessColumnSQL(model, businessColumn, databaseMeta, locale));
        sql.append(Const.CR);
      }
    }
  }
  
  public void generateHaving(StringBuffer sql, List<WhereCondition> conditions, String locale) throws PentahoMetadataException {
    if (conditions != null) {
      boolean havingAdded = false;
      boolean justOpened = false;
      // boolean first=true;
      for (WhereCondition condition : conditions) {
        if (condition.hasAggregate()) {
          if (!havingAdded) {
            sql.append("HAVING ").append(Const.CR); //$NON-NLS-1$
            havingAdded = true;
            justOpened = true;
          }
          // if (!first) sql+=" AND "; else sql+=" ";
          // first=false;
          sql.append(condition.getWhereClause(locale, !justOpened));
          sql.append(Const.CR);
          justOpened = false;
        }
      }
    }

  }
  
  public void generateOrderBy(StringBuffer sql, BusinessModel model, List<OrderBy> orderBy, DatabaseMeta databaseMeta, String locale) {
    if (orderBy != null) {
      boolean orderByAdded = false;
      boolean first = true;
      for (OrderBy orderItem : orderBy) {
        BusinessColumn businessColumn = orderItem.getBusinessColumn();

        if (!orderByAdded) {
          sql.append("ORDER BY ").append(Const.CR); //$NON-NLS-1$
          orderByAdded = true;
        }

        if (!first) {
          sql.append("         ,"); //$NON-NLS-1$
        } else {
          sql.append("          "); //$NON-NLS-1$
        }
        first = false;
        sql.append(getBusinessColumnSQL(model, businessColumn, databaseMeta, locale));
        if (!orderItem.isAscending()) {
          sql.append(" DESC"); //$NON-NLS-1$
        }
        sql.append(Const.CR);
      }
    }

  }
  
  /**
   * @param selections The selected business columns
   * @param conditions the conditions to apply (null = no conditions)
   * @param orderBy the ordering (null = no order by clause)
   * @param databaseMeta the meta info which determines the SQL generated.
   * @param locale the locale
   * @param disableDistinct if true, disables default behavior of using DISTINCT when there
   * are no groupings.
   * @return a SQL query based on a column selection, conditions and a locale
   */
  public MappedQuery getSQL(BusinessModel model, List<? extends Selection> selections, List<WhereCondition> conditions, List<OrderBy> orderBy, DatabaseMeta databaseMeta, String locale, boolean disableDistinct) throws PentahoMetadataException {

    StringBuffer sql = new StringBuffer();
    Map<String,String> columnsMap = new HashMap<String,String>();
    
    // These are the tables involved in the field selection:
    List<BusinessTable> tabs = getTablesInvolved(selections, conditions);

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

      // generate the sql
      
      // SELECT
      generateSelect(sql, model, databaseMeta, selections, disableDistinct, group, locale, columnsMap);
      
      // FROM
      generateFrom(sql, usedBusinessTables, databaseMeta, locale);
      
      // WHERE
      boolean whereAdded = generateJoins(sql, model, path, databaseMeta, locale);
      
      generateWhere(sql, whereAdded, conditions, locale);

      if (group) {

        // GROUP BY
        generateGroupBy(sql, model, selections, databaseMeta, locale);
        // HAVING
        generateHaving(sql, conditions, locale);
        
      }
      
      // ORDER BY
      generateOrderBy(sql, model, orderBy, databaseMeta, locale);
    }

    return new MappedQuery(sql.toString(), columnsMap, selections);
  }

  protected List<BusinessTable> getTablesInvolved(List<? extends Selection> selections, List<WhereCondition> conditions) {
    Set<BusinessTable> treeSet = new TreeSet<BusinessTable>();

    for (Selection selection : selections) {
      BusinessTable businessTable = selection.getBusinessColumn().getBusinessTable();
      treeSet.add(businessTable); //$NON-NLS-1$
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
  
  public boolean hasFactsInIt(List<? extends Selection> selections, List<? extends WhereCondition> conditions) {
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

  public static String getBusinessColumnSQL(BusinessModel businessModel, BusinessColumn column, DatabaseMeta databaseMeta, String locale)
  {
      if (column.isExact())
      { 
        // convert to sql using libformula subsystem
        try {
          // we'll need to pass in some context to PMSFormula so it can resolve aliases if necessary
          PMSFormula formula = new PMSFormula(businessModel, column.getBusinessTable(), databaseMeta, column.getFormula());
          formula.parseAndValidate();
          return formula.generateSQL(locale);
        } catch (PentahoMetadataException e) {
          // this is for backwards compatibility.
          // eventually throw any errors
          logger.error(Messages.getErrorString("BusinessColumn.ERROR_0001_FAILED_TO_PARSE_FORMULA", column.getFormula()), e); //$NON-NLS-1$
        }
        return column.getFormula();
      }
      else
      {
          String tableColumn = ""; //$NON-NLS-1$
          
          // TODO: WPG: is this correct?  shouldn't we be getting an alias for the table vs. it's display name?
          tableColumn += databaseMeta.quoteField( column.getBusinessTable().getDisplayName(locale) );
          tableColumn += "."; //$NON-NLS-1$
          
          // TODO: WPG: instead of using formula, shouldn't we use the physical column's name?
          tableColumn += databaseMeta.quoteField( column.getFormula() );
          
          if (column.hasAggregate()) // For the having clause, for example: HAVING sum(turnover) > 100
          {
              return getFunction(column, databaseMeta)+"("+tableColumn+")"; //$NON-NLS-1$ //$NON-NLS-2$
          }
          else
          {
              return tableColumn;
          }
      }
  }

  public static String getFunction(BusinessColumn column, DatabaseMeta databaseMeta) {
      String fn=""; //$NON-NLS-1$
      
      switch(column.getAggregationType().getType()) {
          case AggregationSettings.TYPE_AGGREGATION_AVERAGE: fn=databaseMeta.getFunctionAverage(); break;
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
        join  = databaseMeta.quoteField( relation.getFieldFrom().getBusinessTable().getDisplayName(locale) );
        join += "."; //$NON-NLS-1$
        join += databaseMeta.quoteField( relation.getFieldFrom().getFormula() );
        
        // Equals
        join += " = "; //$NON-NLS-1$
        
        // Right side
        join += databaseMeta.quoteField( relation.getFieldTo().getBusinessTable().getDisplayName(locale) );
        join += "."; //$NON-NLS-1$
        join += databaseMeta.quoteField( relation.getFieldTo().getFormula() );
    }
    
    return join;
  }
}
