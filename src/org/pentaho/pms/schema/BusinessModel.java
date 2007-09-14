/*
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
package org.pentaho.pms.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.olap.OlapCube;
import org.pentaho.pms.schema.olap.OlapDimension;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ChangedFlagInterface;
import be.ibridge.kettle.core.NotePadMeta;
import be.ibridge.kettle.core.Point;
import be.ibridge.kettle.core.Rectangle;
import be.ibridge.kettle.core.TransAction;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.list.UniqueArrayList;
import be.ibridge.kettle.core.list.UniqueList;
import be.ibridge.kettle.spoon.UndoInterface;
import be.ibridge.kettle.trans.TransMeta;
import be.ibridge.kettle.trans.TransPreviewFactory;
import be.ibridge.kettle.trans.step.tableinput.TableInputMeta;

public class BusinessModel extends ConceptUtilityBase implements ChangedFlagInterface, Cloneable,
    ConceptUtilityInterface, UndoInterface {
  /**
   * This private class is used to sort the business tables in terms of the number of neighbours they have. We use
   * this information to find the table best suited to provide the missing link between selected tables while doing
   * SQL generation.
   */
  private class BusinessTableNeighbours implements Comparable {
    public BusinessTable businessTable;

    public int nrNeighbours;

    public int compareTo(Object obj) {
      BusinessTableNeighbours someClass = (BusinessTableNeighbours) obj;
      if (nrNeighbours == someClass.nrNeighbours) {
        return businessTable.compareTo(someClass.businessTable);
      } else {
        return new Integer(nrNeighbours).compareTo(new Integer(someClass.nrNeighbours));
      }
    }
  }

  private UniqueList businessTables;

  private List relationships;

  private List notes;

  private UniqueList olapDimensions;

  private UniqueList olapCubes;

  private BusinessCategory rootCategory;

  // TODO: Until support for multiple connections is implemented, identify the one connection
  // this model holds reference to.
  public DatabaseMeta connection = null;

  public BusinessModel() {
    super();
    this.businessTables = new UniqueArrayList();
    this.relationships = new ArrayList();
    this.notes = new ArrayList();
    this.olapDimensions = new UniqueArrayList();
    this.olapCubes = new UniqueArrayList();

    BusinessCategory businessCategory = new BusinessCategory();
    businessCategory.setRootCategory(true);
    setRootCategory(businessCategory); // also takes care of the security hierarchy


  }

  public BusinessModel(String id) {
    this();
    try {
      setId(id);
    } catch (ObjectAlreadyExistsException e) {
      // Ignore this one, the class doesn't have any listeners yet so it's impossible for an exception to be thrown.
    }
  }

  /**
   * @return the description of the model element
   */
  public String getModelElementDescription() {
    return Messages.getString("BusinessModel.USER_DESCRIPTION"); //$NON-NLS-1$
  }

  /**
   * @return the relationships
   */
  public List getRelationships() {
    return relationships;
  }

  public Object clone() {
    try {
      BusinessModel businessModel = (BusinessModel) super.clone();
      businessModel.setConcept((ConceptInterface) getConcept().clone());
      return businessModel;
    } catch (CloneNotSupportedException e) // Why would this ever happen anyway???
    {
      return null;
    }
  }

  public RelationshipMeta getRelationship(int i) {
    return (RelationshipMeta) relationships.get(i);
  }

  public void removeRelationship(RelationshipMeta relationship) {
    int i = relationships.indexOf(relationship);
    removeRelationship(i);
  }

  public void removeRelationship(int i) {
    relationships.remove(i);
    setChanged();
  }

  public void addRelationship(RelationshipMeta relationshipMeta) {
    relationships.add(relationshipMeta);
    setChanged();
  }

  public void addRelationship(int p, RelationshipMeta relationshipMeta) throws ObjectAlreadyExistsException {
    relationships.add(p, relationshipMeta);
    setChanged();
  }

  public int indexOfRelationship(RelationshipMeta relationshipMeta) {
    return relationships.indexOf(relationshipMeta);
  }

  public int nrRelationships() {
    return relationships.size();
  }

  /**
   * Finds the relationship with the specified origin and destination table.
   *
   * @param from The id of the physical origin table
   * @param to The id of the physical destination table
   * @return The relationship or null if nothing was found
   */
  public RelationshipMeta findRelationship(String from, String to) {
    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta relationshipMeta = getRelationship(i);
      if (relationshipMeta.getTableFrom().getId().equals(from) && relationshipMeta.getTableTo().getId().equals(to))
        return relationshipMeta;
    }
    return null;
  }

  /**
   * Finds the relationship with the specified id
   *
   * @param id The id of the relationship (compares with toString())
   * @return The relationship or null if nothing was found
   */
  public RelationshipMeta findRelationship(String id) {
    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta relationshipMeta = getRelationship(i);
      if (relationshipMeta.toString().equals(id))
        return relationshipMeta;
    }
    return null;
  }

  /**
   * @return the businessTables
   */
  public UniqueList getBusinessTables() {
    return businessTables;
  }

  public int nrBusinessTables() {
    return businessTables.size();
  }

  public BusinessTable getBusinessTable(int i) {
    return (BusinessTable) businessTables.get(i);
  }

  public void addBusinessTable(BusinessTable businessTable) throws ObjectAlreadyExistsException {
    businessTables.add(businessTable);
    // businessTable.getConcept().setSecurityParentInterface(getConcept()); // set the security parent to the model,
    // not the physical table
    setChanged();
  }

  public void addBusinessTable(int p, BusinessTable businessTable) throws ObjectAlreadyExistsException {
    businessTables.add(p, businessTable);
    // businessTable.getConcept().setSecurityParentInterface(getConcept()); // set the security parent to the model,
    // not the physical table
    setChanged();
  }

  public int indexOfBusinessTable(BusinessTable businessTable) {
    return businessTables.indexOf(businessTable);
  }

  public void removeBusinessTable(int i) {
    getBusinessTable(i).getConcept().setSecurityParentInterface(null); // clear the security parent.

    businessTables.remove(i);
    setChanged();
  }

  /**
   * @return the notes
   */
  public List getNotes() {
    return notes;
  }

  /**
   * @param notes the notes to set
   */
  public void setNotes(List notes) {
    this.notes = notes;
  }

  public int nrSelected() {
    int i, count;
    count = 0;
    for (i = 0; i < nrBusinessTables(); i++) {
      if (getBusinessTable(i).isSelected())
        count++;
    }
    return count;
  }

  public BusinessTable[] getSelected() {
    BusinessTable[] bTables = new BusinessTable[nrSelected()];
    for (int i = 0, j = 0; i < nrBusinessTables(); i++) {
      BusinessTable aTable = getBusinessTable(i);
      if (aTable.isSelected()) {
        bTables[j++] = aTable;
      }
    }
    return bTables;
  }

  public BusinessTable getSelected(int nr) {
    int i, count;
    count = 0;
    for (i = 0; i < nrBusinessTables(); i++) {
      if (getBusinessTable(i).isSelected()) {
        if (nr == count)
          return getBusinessTable(i);
        count++;
      }
    }
    return null;
  }

  public String getSelectedName(String locale, int nr) {
    BusinessTable table = getSelected(nr);
    if (table != null) {
      return table.getConcept().getName(locale);
    }
    return null;
  }

  public void selectInRect(Rectangle rect) {
    int i;
    for (i = 0; i < nrBusinessTables(); i++) {
      BusinessTable ti = getBusinessTable(i);
      Point p = ti.getLocation();
      if (rect.contains(p))
        ti.setSelected(true);
    }
  }

  public Point[] getSelectedLocations() {
    int sels = nrSelected();
    Point retval[] = new Point[sels];
    for (int i = 0; i < sels; i++) {
      BusinessTable ti = getSelected(i);
      Point p = ti.getLocation();
      retval[i] = new Point(p.x, p.y); // explicit copy of location
    }
    return retval;
  }

  public BusinessTable[] getSelectedTables() {
    int sels = nrSelected();
    if (sels == 0)
      return null;

    BusinessTable retval[] = new BusinessTable[sels];
    for (int i = 0; i < sels; i++) {
      BusinessTable table = getSelected(i);
      retval[i] = table;
    }
    return retval;
  }

  public void unselectAll() {
    for (int i = 0; i < nrBusinessTables(); i++) {
      getBusinessTable(i).setSelected(false);
    }
  }

  public void selectAll() {
    for (int i = 0; i < nrBusinessTables(); i++) {
      getBusinessTable(i).setSelected(true);
    }
  }

  public Point getMaximum() {
    int maxx = 0, maxy = 0;

    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      Point location = businessTable.getLocation();
      if (location.x > maxx) {
        maxx = location.x;
      }
      if (location.y > maxy) {
        maxy = location.y;
      }
    }
    for (int i = 0; i < nrNotes(); i++) {
      NotePadMeta notePadMeta = getNote(i);
      Point location = notePadMeta.getLocation();
      if (location.x + notePadMeta.width > maxx) {
        maxx = location.x + notePadMeta.width;
      }
      if (location.y + notePadMeta.height > maxy) {
        maxy = location.y + notePadMeta.height;
      }
    }

    return new Point(maxx + 100, maxy + 100);
  }

  public void addNote(NotePadMeta ni) {
    notes.add(ni);
    setChanged();
  }

  public void addNote(int p, NotePadMeta ni) {
    notes.add(p, ni);
    setChanged();
  }

  public NotePadMeta getNote(int i) {
    return (NotePadMeta) notes.get(i);
  }

  public void removeNote(int i) {
    if (i < 0 || i >= notes.size())
      return;
    notes.remove(i);
    setChanged();
  }

  public int nrNotes() {
    return notes.size();
  }

  public int indexOfNote(Object ni) {
    return notes.indexOf(ni);
  }

  public NotePadMeta getNote(int x, int y) {
    int i, s;
    s = notes.size();
    for (i = s - 1; i >= 0; i--) // Back to front because drawing goes from start to end
    {
      NotePadMeta ni = (NotePadMeta) notes.get(i);
      Point loc = ni.getLocation();
      Point p = new Point(loc.x, loc.y);
      if (x >= p.x && x <= p.x + ni.width + 2 * Const.NOTE_MARGIN && y >= p.y
          && y <= p.y + ni.height + 2 * Const.NOTE_MARGIN) {
        return ni;
      }
    }
    return null;
  }

  public BusinessTable getTable(int x, int y, int iconsize) {
    for (int i = nrBusinessTables() - 1; i >= 0; i--) // Back to front because drawing goes from start to end
    {
      BusinessTable table = getBusinessTable(i);
      if (table.isDrawn()) // Only consider tables that are on the canvas
      {
        Point p = table.getLocation();
        if (p != null) {
          if (x >= p.x && x <= p.x + iconsize && y >= p.y && y <= p.y + iconsize) {
            return table;
          }
        }
      }
    }
    return null;
  }

  public int[] getTableIndexes(BusinessTable tables[]) {
    int retval[] = new int[tables.length];

    for (int i = 0; i < tables.length; i++)
      retval[i] = indexOfBusinessTable(tables[i]);

    return retval;
  }

  /**
   * Search for a relationship starting with a given starting point
   *
   * @param id The physical table id (ID) to start from
   * @return The relationship if any could be found or null if nothing could be found.
   */
  public RelationshipMeta findRelationshipFrom(String id) {
    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta ri = getRelationship(i);
      if (ri.getTableFrom().getId().equalsIgnoreCase(id)) // return the first
      {
        return ri;
      }
    }
    return null;
  }

  /**
   * Search for a relationship starting with a given termination point
   *
   * @param id The physical table id (ID) to look for as an ending point
   * @return The relationship if any could be found or null if nothing could be found.
   */
  public RelationshipMeta findRelationshipTo(String id) {
    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta ri = getRelationship(i);
      if (ri.getTableTo().getId().equalsIgnoreCase(id)) // return the first
      {
        return ri;
      }
    }
    return null;
  }

  /**
   * Is the physical table with the specified id used in a relationship in this business model?
   *
   * @param id The physical table id to look out for
   * @return true if the physical table with the specified id is used in this business model in one or more
   * relationships. False if this is not the case.
   */
  public boolean isTableUsedInRelationships(String id) {
    RelationshipMeta fr = findRelationshipFrom(id);
    RelationshipMeta to = findRelationshipTo(id);
    if (fr != null || to != null)
      return true;
    return false;
  }

  public ArrayList getModelTables() {
    ArrayList modelTables = new ArrayList();

    for (int x = 0; x < nrRelationships(); x++) {
      RelationshipMeta hi = getRelationship(x);
      BusinessTable si = hi.getTableFrom(); // FROM
      int idx = modelTables.indexOf(si);
      if (idx < 0)
        modelTables.add(si);

      si = hi.getTableTo(); // TO
      idx = modelTables.indexOf(si);
      if (idx < 0)
        modelTables.add(si);
    }

    // Also, add the tables that need to be painted, but are not part of a relationship
    for (int x = 0; x < nrBusinessTables(); x++) {
      BusinessTable table = getBusinessTable(x);
      if (table.isDrawn() && !isTableUsedInRelationships(table.getPhysicalTable().getId())) {
        modelTables.add(table);
      }
    }

    return modelTables;
  }

  public int countRelationshipsUsing(BusinessTable one) {
    int nr = 0;
    for (int i = 0; i < nrRelationships(); i++) {
      if (getRelationship(i).isUsingTable(one))
        nr++;
    }
    return nr;
  }

  public RelationshipMeta[] findRelationshipsUsing(BusinessTable one) {
    RelationshipMeta rels[] = new RelationshipMeta[countRelationshipsUsing(one)];

    int nr = 0;

    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta relationship = getRelationship(i);
      if (relationship.isUsingTable(one)) {
        rels[nr] = relationship;
        nr++;
      }
    }

    return rels;
  }

  public RelationshipMeta findRelationshipUsing(BusinessTable one, BusinessTable two) {
    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta relationship = getRelationship(i);
      if (relationship.isUsingTable(one) && relationship.isUsingTable(two)) {
        return getRelationship(i);
      }
    }

    return null;
  }

  public Vector getShortestPathsBetween(BusinessTable tabs[]) {
    if (tabs == null || tabs.length < 2)
      return new Vector();

    List pathList = new ArrayList(); // list of Vector
    Path path = new Path(); // empty path;
    Vector paths = null;

    for (int i = 0; i < tabs.length - 1; i++) {
      BusinessTable one = tabs[i];
      BusinessTable two = tabs[i + 1];

      Vector vector = new Vector();
      if (i > 0) {
        Vector prev = (Vector) pathList.get(i - 1);
        for (int p = 0; p < prev.size(); p++) // use the previous list of paths...
        {
          getShortestPathsBetween(one, two, vector, (Path) prev.get(p));
        }
      } else {
        getShortestPathsBetween(one, two, vector, path);
      }
      int min = getMinimumSize(vector);
      if (min > 0)
        onlyKeepSize(vector, min);
    }

    paths = (Vector) pathList.get(tabs.length - 2);
    quickSort(paths);
    for (int p = 0; p < paths.size(); p++) {
      Path pth = (Path) paths.get(p);
    }
    return paths;
  }

  public Path getShortestPathBetween(BusinessTable tabs[]) {
    // Let's try a different approach.
    // We have the business tables.
    // Let's try to see if they are somehow connected first.
    // If they are not, we add a table that's not being used so far and add it to the equation.
    // We can continue like that until we connect all tables with joins.

    // This is a list of all the paths that we could find between all the tables...
    List paths = new ArrayList();

    // Here are the tables we need to link it all together.
    List selectedTables = new ArrayList();
    for (int i = 0; i < tabs.length; i++)
      selectedTables.add(tabs[i]);

    boolean allUsed = tabs.length == 0;
    while (!allUsed) {
      // These are the tables that are not yet used
      List notSelectedTables = getNonSelectedTables(selectedTables);

      Path path = new Path();

      // Generate all combinations of the selected tables...
      for (int i = 0; i < selectedTables.size(); i++) {
        for (int j = i + 1; j < selectedTables.size(); j++) {
          if (i != j) {
            BusinessTable one = (BusinessTable) selectedTables.get(i);
            BusinessTable two = (BusinessTable) selectedTables.get(j);

            // See if we have a relationship that goes from one to two...
            RelationshipMeta relationship = findRelationshipUsing(one, two);
            if (relationship != null && !path.contains(relationship)) {
              path.addRelationship(relationship);
            }
          }
        }

        // We need to have (n-1) relationships for n tables, otherwise we will not connect everything.
        //
        if (path.size() == selectedTables.size() - 1) {
          // This is a valid path, the first we find here is probably the shortest
          paths.add(path);
          // We can stop now.
          allUsed = true;
        }
      }

      if (!allUsed) {
        // Add one of the tables to the equation
        // Try one that has a relationship to one of the other tables.
        // Otherwise it doesn't make sense to add it.
        if (notSelectedTables.size() > 0) {
          BusinessTable businessTable = (BusinessTable) notSelectedTables.get(0);
          notSelectedTables.remove(0);
          selectedTables.add(businessTable);
        } else {
          allUsed = true; // we're done
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

  private List getNonSelectedTables(List selectedTables) {
    List extra = new ArrayList();
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable check = getBusinessTable(i);
      boolean found = false;
      for (int j = 0; j < selectedTables.size(); j++) {
        BusinessTable businessTable = (BusinessTable) selectedTables.get(j);
        if (check.equals(businessTable))
          found = true;
      }

      if (!found) {
        BusinessTableNeighbours btn = new BusinessTableNeighbours();
        btn.businessTable = check;
        btn.nrNeighbours = getNrNeighbours(check, selectedTables);
        extra.add(btn);
      }
    }

    // OK, we now have a number of tables, but we want to sort this list
    // The tables with the highest numbers of neighbours should be placed first. (descending)
    //
    Collections.sort(extra);

    List retval = new ArrayList();
    for (int i = 0; i < extra.size(); i++) {
      BusinessTableNeighbours btn = (BusinessTableNeighbours) extra.get(i);
      // If the number of neighbours is 0, there is no point in returning the table for the SQL generation
      // There is no way the table can connect to the selected tables anyway as there are no neighbours.
      //
      if (btn.nrNeighbours > 0) {
        retval.add(0, btn.businessTable);
      }
    }

    return retval;
  }

  public void getShortestPathsBetween(BusinessTable one, BusinessTable two, Vector paths, Path path) {
    RelationshipMeta rels[] = findRelationshipsUsing(one);
    for (int i = 0; i < rels.length; i++) {
      RelationshipMeta rel = (RelationshipMeta) rels[i].clone();
      if (!rel.getTableFrom().equals(one)) {
        rel.flip();
      }

      if (!path.contains(rel)) // Let's not go endlessly round and round!
      {
        path.addRelationship(rel); // go and explore this possibility...

        BusinessTable next = rel.getTableTo();
        if (!next.equals(two)) {
          getShortestPathsBetween(rel.getTableTo(), two, paths, path);
        } else {
          paths.add(path.clone());
        }

        path.removeRelationship(); // Undo this possibility
      }
    }
  }

  /**
   * @param selectedColumns The selected business columns
   * @param locale the locale
   * @param useDisplayNames if true, use localized display name, else use column id
   * @param columnsMap map of truncated column "as" references to true column ids 
   * @return a SQL query based on a column selection and locale
   */
  public MappedQuery getSQL(BusinessColumn selectedColumns[], String locale) throws PentahoMetadataException {
    return getSQL(selectedColumns, (WhereCondition[]) null, (OrderBy[]) null, locale);
  }

  /**
   * @param selectedColumns The selected business columns
   * @param conditions the conditions to apply
   * @param locale the locale
   * @param useDisplayNames if true, use localized display name, else use column id
   * @param columnsMap map of truncated column "as" references to true column ids 
   * @return a SQL query based on a column selection, conditions and a locale
   */
  public MappedQuery getSQL(BusinessColumn selectedColumns[], WhereCondition conditions[], String locale) throws PentahoMetadataException {
    return getSQL(selectedColumns, conditions, (OrderBy[]) null, locale);
  }

  /**
   * @param selectedColumns The selected business columns
   * @param conditions the conditions to apply (null = no conditions)
   * @param orderBy the ordering (null = no order by clause)
   * @param locale the locale
   * @param useDisplayNames if true, use localized display name, else use column id
   * @param columnsMap map of truncated column "as" references to true column ids 
   * @return a SQL query based on a column selection, conditions and a locale
   */
  public MappedQuery getSQL(BusinessColumn selectedColumns[], WhereCondition conditions[], OrderBy[] orderBy, String locale) throws PentahoMetadataException {
    MappedQuery sql = null;

    // These are the tables involved in the field selection:
    BusinessTable tabs[] = getTablesInvolved(selectedColumns, conditions);

    // Now get the shortest path between these tables.
    Path path = getShortestPathBetween(tabs);
    if (path == null) {
      throw new PentahoMetadataException(Messages.getErrorString("BusinessModel.ERROR_0001_FAILED_TO_FIND_PATH")); //$NON-NLS-1$
    }

    sql = getSQL(selectedColumns, path, conditions, orderBy, locale);

    return sql;
  }

  // Make a list of all facts with aggr. type <> none
  // if the list is not empty, make a group by list: all dimensions & aggr.type==none
  // place sum()/etc functions over field
  // add group by line...
  // Klair!


  public MappedQuery getSQL(BusinessColumn selectedColumns[], Path path, WhereCondition conditions[], OrderBy[] orderBy,
      String locale) throws PentahoMetadataException {
    String sql = null;
    Map columnsMap = new HashMap();

    BusinessTable usedBusinessTables[] = path.getUsedTables();
    if (path.size() == 0) {
      // just a selection from 1 table: pick any column...
      if (selectedColumns.length > 0) // Otherwise, why bother, right?
      {
        usedBusinessTables = new BusinessTable[] { selectedColumns[0].getBusinessTable() };
      }
    }

    if (usedBusinessTables.length > 0) {
      DatabaseMeta databaseMeta = usedBusinessTables[0].getPhysicalTable().getDatabaseMeta(); // just the first table
      // for now.

      // SELECT
      //
      sql = "SELECT "; //$NON-NLS-1$

      //
      // Add the fields...
      //
      boolean group = hasFactsInIt(selectedColumns, conditions);

      if (!group)
        sql += "DISTINCT "; //$NON-NLS-1$
      sql += Const.CR;

      for (int i = 0; i < selectedColumns.length; i++) {
        BusinessColumn businessColumn = selectedColumns[i];

        if (i > 0)
          sql += "         ,"; //$NON-NLS-1$
        else
          sql += "          "; //$NON-NLS-1$
        sql += businessColumn.getFunctionTableAndColumnForSQL(this, locale);
        sql += " AS "; //$NON-NLS-1$
/*
        if (useDisplayNames) {
          sql += databaseMeta.quoteField(businessColumn.getDisplayName(locale));
        } else {
*/
          // in some database implementations, the "as" name has a finite length;
          // for instance, oracle cannot handle a name longer than 30 characters. 
          // So, we map a short name here to the longer id, and replace the id
          // later in the resultset metadata. 

          if(columnsMap != null){
            columnsMap.put("COL" + Integer.toString(i), businessColumn.getId());
            sql += databaseMeta.quoteField("COL" + Integer.toString(i));
          }else{
            sql += databaseMeta.quoteField(businessColumn.getId());
          }

          //}

        sql += Const.CR;
      }

      // FROM
      //
      sql += "FROM " + Const.CR; //$NON-NLS-1$
      for (int i = 0; i < usedBusinessTables.length; i++) {
        BusinessTable businessTable = usedBusinessTables[i];

        if (i > 0)
          sql += "         ,"; //$NON-NLS-1$
        else
          sql += "          "; //$NON-NLS-1$
        String schemaName = null;
        if (businessTable.getTargetSchema() != null)
          schemaName = databaseMeta.quoteField(businessTable.getTargetSchema());
        String tableName = databaseMeta.quoteField(businessTable.getTargetTable());
        sql += databaseMeta.getSchemaTableCombination(schemaName, tableName) + " " //$NON-NLS-1$
            + databaseMeta.quoteField(businessTable.getDisplayName(locale));
        sql += Const.CR;
      }

      // WHERE from joins first
      //
      boolean whereAdded = false;
      int nr = 0;
      if (path != null) {
        for (int i = 0; i < path.size(); i++, nr++) {
          if (!whereAdded) {
            sql += "WHERE " + Const.CR; //$NON-NLS-1$
            whereAdded = true;
          }
          RelationshipMeta relation = path.getRelationship(i);

          if (nr > 0)
            sql += "      AND "; //$NON-NLS-1$
          else
            sql += "          "; //$NON-NLS-1$
          sql += relation.getJoin(locale);
          sql += Const.CR;
        }
      }

      // WHERE from conditions
      //
      if (conditions != null) {
        boolean bracketOpen = false;
        boolean justOpened = false;
        for (int i = 0; i < conditions.length; i++, nr++) {
          WhereCondition condition = conditions[i];

          // The ones with aggregates in it are for the HAVING clause
          //
          if (!condition.hasAggregate()) {
            if (!whereAdded) {
              sql += "WHERE " + Const.CR; //$NON-NLS-1$
              whereAdded = true;
              justOpened = true;
            } else if (!bracketOpen) {
              sql += "      AND ( " + Const.CR; //$NON-NLS-1$
              bracketOpen = true;
              justOpened = true;
            }
            sql += "             " + conditions[i].getWhereClause(locale, !justOpened); //$NON-NLS-1$
            sql += Const.CR;
            justOpened = false;
          }
        }
        if (bracketOpen) {
          sql += "          )" + Const.CR; //$NON-NLS-1$
        }
      }

      // GROUP BY
      //
      if (group) {
        boolean groupByAdded = false;
        boolean first = true;
        for (int i = 0; i < selectedColumns.length; i++) {
          BusinessColumn businessColumn = selectedColumns[i];

          if (!businessColumn.hasAggregate()) {
            if (!groupByAdded) {
              sql += "GROUP BY " + Const.CR; //$NON-NLS-1$
              groupByAdded = true;
            }

            if (!first)
              sql += "         ,"; //$NON-NLS-1$
            else
              sql += "          "; //$NON-NLS-1$
            first = false;
            sql += businessColumn.getFunctionTableAndColumnForSQL(this, locale);
            sql += Const.CR;
          }
        }
      }

      // HAVING
      //
      if (group && conditions != null) {
        boolean havingAdded = false;
        boolean justOpened = false;
        // boolean first=true;
        for (int i = 0; i < conditions.length; i++, nr++) {
          WhereCondition condition = conditions[i];

          if (condition.hasAggregate()) {
            if (!havingAdded) {
              sql += "HAVING " + Const.CR; //$NON-NLS-1$
              havingAdded = true;
              justOpened = true;
            }
            // if (!first) sql+=" AND "; else sql+=" ";
            // first=false;
            sql += conditions[i].getWhereClause(locale, !justOpened);
            sql += Const.CR;
            justOpened = false;
          }
        }
      }
      
      // ORDER BY
      //
      if (orderBy != null) {
        boolean orderByAdded = false;
        boolean first = true;
        for (int i = 0; i < orderBy.length; i++) {
          BusinessColumn businessColumn = orderBy[i].getBusinessColumn();

          if (!orderByAdded) {
            sql += "ORDER BY " + Const.CR; //$NON-NLS-1$
            orderByAdded = true;
          }

          if (!first)
            sql += "         ,"; //$NON-NLS-1$
          else
            sql += "          "; //$NON-NLS-1$
          first = false;
          sql += businessColumn.getFunctionTableAndColumnForSQL(this, locale);
          if (!orderBy[i].isAscending())
            sql += " DESC"; //$NON-NLS-1$
          sql += Const.CR;
        }
      }
    }

    return new MappedQuery(sql, columnsMap);
  }

  public TransMeta getTransformationMeta(BusinessColumn selectedColumns[], WhereCondition conditions[],
      OrderBy[] orderBy, String locale) throws PentahoMetadataException  {
    if (selectedColumns == null || selectedColumns.length == 0)
      return null;

    DatabaseMeta databaseMeta = selectedColumns[0].getBusinessTable().getPhysicalTable().getDatabaseMeta();
    MappedQuery query = getSQL(selectedColumns, conditions, orderBy, locale);

    TableInputMeta tableInputMeta = new TableInputMeta();
    tableInputMeta.setDatabaseMeta(databaseMeta);
    tableInputMeta.setSQL(query.getQuery());

    TransMeta transMeta = TransPreviewFactory.generatePreviewTransformation(tableInputMeta, Messages
        .getString("BusinessModel.USER_TITLE_QUERY")); //$NON-NLS-1$
    transMeta.addDatabase(databaseMeta);

    transMeta.setName(Messages.getString("BusinessModel.USER_QUERY_GENERATED_FROM_MODEL", getName(locale))); //$NON-NLS-1$

    return transMeta;
  }

  public BusinessTable[] getTablesInvolved(BusinessColumn fields[], WhereCondition conditions[]) {
    Hashtable lookup = new Hashtable();

    for (int i = 0; i < fields.length; i++) {
      BusinessTable businessTable = fields[i].getBusinessTable();
      lookup.put(businessTable, "OK"); //$NON-NLS-1$
    }
    for (int i = 0; i < conditions.length; i++) {
      List cols = conditions[i].getBusinessColumns();
      Iterator iter = cols.iterator();
      while (iter.hasNext()) {
        BusinessColumn col = (BusinessColumn)iter.next();
        BusinessTable businessTable = col.getBusinessTable();
        lookup.put(businessTable, "OK"); //$NON-NLS-1$
      }
    }

    Set keySet = lookup.keySet();
    return (BusinessTable[]) keySet.toArray(new BusinessTable[keySet.size()]);
  }

  public boolean hasFactsInIt(BusinessColumn fields[], WhereCondition conditions[]) {
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].hasAggregate())
        return true;
    }
    if (conditions != null) {
      for (int i = 0; i < conditions.length; i++) {
        if (conditions[i].hasAggregate())
          return true;
      }
    }
    return false;
  }

  public RelationshipMeta[] getRelationsInvolved(BusinessTable tabs[]) {
    Hashtable lookup = new Hashtable();

    // Store the relationships that are used by the tables...
    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta rel = getRelationship(i);
      for (int j = 0; j < tabs.length; j++) {
        if (rel.isUsingTable(tabs[j])) {
          lookup.put(rel, "OK"); //$NON-NLS-1$
        }
      }
    }

    Set keySet = lookup.keySet();
    return (RelationshipMeta[]) keySet.toArray(new RelationshipMeta[keySet.size()]);
  }

  public int nrNextRelationships(BusinessTable table) {
    int nr = 0;

    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta rel = getRelationship(i);

      if (rel.isUsingTable(table)) {
        nr++;
      }
    }
    return nr;
  }

  public RelationshipMeta getNextRelationship(BusinessTable table, int getnr) {
    int nr = 0;

    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta rel = getRelationship(i);

      if (rel.isUsingTable(table)) {
        if (getnr == nr) {
          RelationshipMeta retval = (RelationshipMeta) rel.clone();
          if (!retval.getTableFrom().getId().equalsIgnoreCase(table.getId())) {
            retval.flip();
          }
          return retval;
        }
        nr++;
      }
    }

    return null;
  }

  public int getMinimumSize(Vector paths) {
    // What's the shortest number of steps between the two tables?
    int min = Integer.MAX_VALUE;
    for (int i = 0; i < paths.size(); i++) {
      Path p = (Path) paths.get(i);
      if (p.size() > 0 && p.size() < min)
        min = p.size();
    }
    if (min == Integer.MAX_VALUE)
      return -1;
    return min;
  }

  public void onlyKeepSize(Vector paths, int min) {
    // OK, now only keep the smallest paths between these 2 tables...
    // No need to wander around and keep all junk!
    for (int i = paths.size() - 1; i >= 0; i--) {
      Path p = (Path) paths.get(i);
      if (p.size() > min)
        paths.remove(i);
    }
  }

  /**
   * Sort the entire vector, if it is not empty
   */
  public synchronized void quickSort(Vector elements) {
    if (!elements.isEmpty()) {
      quickSort(elements, 0, elements.size() - 1);
    }
  }

  /**
   * QuickSort.java by Henk Jan Nootenboom, 9 Sep 2002 Copyright 2002-2003 SUMit. All Rights Reserved.
   *
   * Algorithm designed by prof C. A. R. Hoare, 1962 See http://www.sum-it.nl/en200236.html for algorithm improvement
   * by Henk Jan Nootenboom, 2002.
   *
   * Recursive Quicksort, sorts (part of) a Vector by 1. Choose a pivot, an element used for comparison 2. dividing
   * into two parts: - less than-equal pivot - and greater than-equal to pivot. A element that is equal to the pivot
   * may end up in any part. See www.sum-it.nl/en200236.html for the theory behind this. 3. Sort the parts recursively
   * until there is only one element left.
   *
   * www.sum-it.nl/QuickSort.java this source code www.sum-it.nl/quicksort.php3 demo of this quicksort in a java
   * applet
   *
   * Permission to use, copy, modify, and distribute this java source code and its documentation for NON-COMMERCIAL or
   * COMMERCIAL purposes and without fee is hereby granted. See http://www.sum-it.nl/security/index.html for copyright
   * laws.
   */
  private synchronized void quickSort(Vector elements, int lowIndex, int highIndex) {
    int lowToHighIndex;
    int highToLowIndex;
    int pivotIndex;
    Path pivotValue; // values are Strings in this demo, change to suit your application
    Path lowToHighValue;
    Path highToLowValue;
    Path parking;
    int newLowIndex;
    int newHighIndex;
    int compareResult;

    lowToHighIndex = lowIndex;
    highToLowIndex = highIndex;
    /**
     * Choose a pivot, remember it's value No special action for the pivot element itself. It will be treated just
     * like any other element.
     */
    pivotIndex = (lowToHighIndex + highToLowIndex) / 2;
    pivotValue = (Path) elements.elementAt(pivotIndex);

    /**
     * Split the Vector in two parts.
     *
     * The lower part will be lowIndex - newHighIndex, containing elements <= pivot Value
     *
     * The higher part will be newLowIndex - highIndex, containting elements >= pivot Value
     *
     */
    newLowIndex = highIndex + 1;
    newHighIndex = lowIndex - 1;
    // loop until low meets high
    while ((newHighIndex + 1) < newLowIndex) // loop until partition complete
    { // loop from low to high to find a candidate for swapping
      lowToHighValue = (Path) elements.elementAt(lowToHighIndex);
      while (lowToHighIndex < newLowIndex & lowToHighValue.compare(pivotValue) < 0) {
        newHighIndex = lowToHighIndex; // add element to lower part
        lowToHighIndex++;
        lowToHighValue = (Path) elements.elementAt(lowToHighIndex);
      }

      // loop from high to low find other candidate for swapping
      highToLowValue = (Path) elements.elementAt(highToLowIndex);
      while (newHighIndex <= highToLowIndex & (highToLowValue.compare(pivotValue) > 0)) {
        newLowIndex = highToLowIndex; // add element to higher part
        highToLowIndex--;
        highToLowValue = (Path) elements.elementAt(highToLowIndex);
      }

      // swap if needed
      if (lowToHighIndex == highToLowIndex) // one last element, may go in either part
      {
        newHighIndex = lowToHighIndex; // move element arbitrary to lower part
      } else if (lowToHighIndex < highToLowIndex) // not last element yet
      {
        compareResult = lowToHighValue.compare(highToLowValue);
        if (compareResult >= 0) // low >= high, swap, even if equal
        {
          parking = lowToHighValue;
          elements.setElementAt(highToLowValue, lowToHighIndex);
          elements.setElementAt(parking, highToLowIndex);

          newLowIndex = highToLowIndex;
          newHighIndex = lowToHighIndex;

          lowToHighIndex++;
          highToLowIndex--;
        }
      }
    }

    // Continue recursion for parts that have more than one element
    if (lowIndex < newHighIndex) {
      this.quickSort(elements, lowIndex, newHighIndex); // sort lower subpart
    }
    if (newLowIndex < highIndex) {
      this.quickSort(elements, newLowIndex, highIndex); // sort higher subpart
    }
  }

  /**
   * Finds a business table using the display name and locale or the ID
   *
   * @param locale The locale to use
   * @param tablename The name of the table or the ID if the table name
   * @return
   */
  public BusinessTable findBusinessTable(String locale, String tablename) {
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      String displayName = businessTable.getDisplayName(locale);
      if (displayName != null && displayName.equals(tablename))
        return businessTable;
    }
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      String id = businessTable.getId();
      if (id != null && id.equals(tablename))
        return businessTable;
    }
    return null;
  }

  /**
   * Finds a business table using the table's ID
   *
   * @param tableID The ID of the table to look for
   * @return The business table of null if nothing could be found.
   */
  public BusinessTable findBusinessTable(String tableID) {
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      String id = businessTable.getId();
      if (id != null && id.equalsIgnoreCase(tableID))
        return businessTable;
    }
    return null;
  }

  /**
   * Get an array of all the selected job entries
   *
   * @return A list containing all the selected & drawn job entries.
   */
  public List getSelectedDrawnBusinessTableList() {
    List list = new ArrayList();

    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      if (businessTable.isDrawn() && businessTable.isSelected()) {
        list.add(businessTable);
      }

    }
    return list;
  }

  /**
   * Get an array of the locations of an array of business tables
   *
   * @param steps An array of business tables
   * @return an array of the indices of an array of business tables
   */
  public int[] getBusinessTableIndexes(BusinessTable[] tables) {
    int retval[] = new int[tables.length];

    for (int i = 0; i < tables.length; i++) {
      retval[i] = indexOfBusinessTable(tables[i]);
    }

    return retval;
  }

  /**
   * This method deletes all objects in the business model that reference the given physical table
   *
   * @param physicalTable The physical table to which all references have to be deleted.
   */
  public void deletePhysicalTableReferences(PhysicalTable physicalTable) {
    for (int t = nrBusinessTables() - 1; t >= 0; t--) {
      BusinessTable businessTable = getBusinessTable(t);
      if (businessTable.getPhysicalTable().equals(physicalTable)) // This reference has to go
      {
        // See if there are any relationships using this businesstable
        RelationshipMeta[] tableRelationships = findRelationshipsUsing(businessTable);
        for (int r = 0; r < tableRelationships.length; r++) {
          int idx = indexOfRelationship(tableRelationships[r]);
          removeRelationship(idx);
        }
        // Then remove the business table
        removeBusinessTable(t);
      }
    }
  }

  /**
   * Find the business category using a path (array) with the names
   *
   * @param path the path to the business category
   * @param locale The locale to look for. If the locale is not found, we try the ID
   * @return The business category when one is found or the best match possible using the path. If nothing matches, we
   * return the root category.
   */
  public BusinessCategory findBusinessCategory(String[] path, String locale) {
    return findBusinessCategory(path, locale, false);
  }

  /**
   * Find the business category using a path (array) with the names
   *
   * @param path the path to the business category
   * @param locale The locale to look for. If the locale is not found, we try the ID
   * @param exact if true, we do an exact match and don't return the closest match nor root category. In that case we
   * return null.
   * @return The business category when one is found or the best match possible using the path. If nothing matches, we
   * return the root category.
   */
  public BusinessCategory findBusinessCategory(String[] path, String locale, boolean exact) {
    if (path.length == 0) {
      if (exact)
        return null;
      else
        return rootCategory;
    }
    for (int i = 0; i < rootCategory.nrBusinessCategories(); i++) {
      BusinessCategory businessCategory = rootCategory.getBusinessCategory(i);
      BusinessCategory found = findBusinessCategory(path, 0, businessCategory, locale, exact);
      if (found != null)
        return found;
    }
    if (exact)
      return null;
    else
      return rootCategory;
  }

  private BusinessCategory findBusinessCategory(String[] path, int level, BusinessCategory businessCategory,
      String locale, boolean exact) {
    // if (level>path.length-1) return null; // how is this possible?

    if (path[level].equals(businessCategory.getDisplayName(locale)) || path[level].equals(businessCategory.getId())) {
      // We're on the right path...
      //
      if (level >= path.length - 1) {
        // We have reached the lowest possible node: return this one
        return businessCategory;
      } else {
        // Not yet at the bottom...
        for (int i = 0; i < businessCategory.nrBusinessCategories(); i++) {
          BusinessCategory category = businessCategory.getBusinessCategory(i);
          BusinessCategory found = findBusinessCategory(path, level + 1, category, locale, exact);
          if (found != null)
            return found;
        }

        if (exact)
          return null;
        else
          return businessCategory; // return the best match we have.
      }
    } else {
      // Not on the right path, don't persue anymore...
      return null;
    }
  }

  /**
   * Returns all business columns
   *
   * @return a UniqueList of all business columns in this model
   */
  public UniqueList getAllBusinessColumns() {
    UniqueList columns = new UniqueArrayList();

    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      for (int j = 0; j < businessTable.nrBusinessColumns(); j++) {
        try {
          columns.add(businessTable.getBusinessColumn(j));
        } catch (ObjectAlreadyExistsException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return columns;
  }

  /**
   * Finds a column using the id
   *
   * @param columnId the id of the column to look for
   * @return a business column in this model with the specified id or null if nothing was found.
   */
  public BusinessColumn findBusinessColumn(String columnId) {
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      BusinessColumn businessColumn = businessTable.findBusinessColumn(columnId);
      if (businessColumn != null)
        return businessColumn;
    }
    return null;
  }

  /**
   * Finds a column name using a localized name
   *
   * @param locale the locale to search for
   * @param name the displayed name of the column to look for or the ID of the column if no localized name was found
   * @return a business column in this model with the specified id or null if nothing was found.
   */
  public BusinessColumn findBusinessColumn(String locale, String name) {
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      BusinessColumn businessColumn = businessTable.findBusinessColumn(locale, name);
      if (businessColumn != null)
        return businessColumn;
    }
    return null;
  }

  /**
   * @param locale the locale to display the categories and columns in.
   * @return a "flat" representation of the categories. Note: it is a List<BusinessColumn>
   */
  public List getFlatCategoriesView(String locale) {
    List strings = new ArrayList();
    Stack categoriesPath = new Stack();

    getFlatCategoriesView(strings, categoriesPath, rootCategory, locale);

    return strings;
  }

  private void getFlatCategoriesView(List strings, Stack categoriesPath, BusinessCategory parentCategory, String locale) {
    // Add the category id itself...
    StringBuffer pathString = new StringBuffer();
    for (int i = 0; i < categoriesPath.size(); i++) {
      BusinessCategory businessCategory = (BusinessCategory) categoriesPath.get(i);
      if (i > 0)
        pathString.append(" - "); //$NON-NLS-1$
      String categoryName = businessCategory.getDisplayName(locale);
      if (i + 1 == categoriesPath.size()) {
        pathString.append(categoryName); // The last item in the path.
      } else {
        pathString.append(Const.rightPad(" ", categoryName.length())); //$NON-NLS-1$
      }
    }

    BusinessColumnString categoryString = new BusinessColumnString(pathString.toString(), strings.size(), null); // no column here!
    strings.add(categoryString);

    // Now add the sub-categories...
    for (int i = 0; i < parentCategory.nrBusinessCategories(); i++) {
      BusinessCategory businessCategory = parentCategory.getBusinessCategory(i);
      categoriesPath.push(businessCategory);
      getFlatCategoriesView(strings, categoriesPath, businessCategory, locale);
      categoriesPath.pop();
    }

    // And finally add the business columns in the parent category
    for (int i = 0; i < parentCategory.nrBusinessColumns(); i++) {
      BusinessColumn businessColumn = parentCategory.getBusinessColumn(i);

      // Only show columns that are not hidden in the physical table
      //
      if (!businessColumn.getPhysicalColumn().isHidden()) {
        String desc = Const.rightPad(" ", pathString.length()) + "  " + businessColumn.getDisplayName(locale); //$NON-NLS-1$ //$NON-NLS-2$

        BusinessColumnString columnString = new BusinessColumnString(desc, strings.size(), businessColumn); // no column here!
        strings.add(columnString);
      }
    }
  }

  public BusinessCategory getRootCategory() {
    return rootCategory;
  }

  public void setRootCategory(BusinessCategory rootCategory) {
    this.rootCategory = rootCategory;
    rootCategory.getConcept().setSecurityParentInterface(getConcept());
  }

  /**
   * Find the business table containing the business column
   *
   * @param businessColumn
   * @return
   */
  public BusinessTable findBusinessTable(BusinessColumn businessColumn) {
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      if (businessTable.findBusinessColumn(businessColumn.getId()) != null)
        return businessTable;
    }
    return null;
  }

  public boolean hasChanged() {
    if (haveTablesChanged())
      return true;
    if (haveRelationshipsChanged())
      return true;
    if (rootCategory.hasChanged())
      return true;
    if (haveOlapDimensionsChanged())
      return true;
    if (haveOlapCubesChanged())
      return true;
    return super.hasChanged();
  }

  private boolean haveRelationshipsChanged() {
    for (int i = 0; i < nrRelationships(); i++) {
      if (getRelationship(i).hasChanged())
        return true;
    }
    return false;
  }

  public boolean haveTablesChanged() {
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable bt = getBusinessTable(i);
      if (bt.hasChanged())
        return true;
    }
    return false;
  }

  public boolean haveOlapDimensionsChanged() {
    for (int i = 0; i < olapDimensions.size(); i++) {
      OlapDimension olapDimension = (OlapDimension) olapDimensions.get(i);
      if (olapDimension.hasChanged())
        return true;
    }
    return false;
  }

  public boolean haveOlapCubesChanged() {
    for (int i = 0; i < olapCubes.size(); i++) {
      OlapCube olapCube = (OlapCube) olapCubes.get(i);
      if (olapCube.hasChanged())
        return true;
    }
    return false;
  }

  public void clearChanged() {
    super.clearChanged();

    // Clear the flag on the business table
    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable businessTable = getBusinessTable(i);
      businessTable.clearChanged();
    }

    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta relationshipMeta = getRelationship(i);
      relationshipMeta.clearChanged();
    }

    for (int i = 0; i < nrNotes(); i++) {
      NotePadMeta notePadMeta = getNote(i);
      notePadMeta.setChanged(false);
    }

    for (int i = 0; i < olapDimensions.size(); i++) {
      OlapDimension olapDimension = (OlapDimension) olapDimensions.get(i);
      olapDimension.clearChanged();
    }

    for (int i = 0; i < olapCubes.size(); i++) {
      OlapCube olapCube = (OlapCube) olapCubes.get(i);
      olapCube.setChanged(false);
    }

    rootCategory.clearChanged();
  }

  /**
   * @param businessTable
   *          the table to calculate the number of neighbours for
   * @param selectedTables
   *          the list of selected business tables
   * @return The number of neighbours in a list of selected tables using the relationships defined in this business model
   */
  public int getNrNeighbours(BusinessTable businessTable, List selectedTables) {
    int nr = 0;

    for (int i = 0; i < nrRelationships(); i++) {
      RelationshipMeta relationship = getRelationship(i);
      if (relationship.isUsingTable(businessTable)) {
        // See if one of the selected tables is also using this relationship.
        // If so, we have a neighbour in the selected tables.
        //
        boolean found = false;
        for (int s = 0; s < selectedTables.size() && !found; s++) {
          BusinessTable selectedTable = (BusinessTable) selectedTables.get(s);
          if (relationship.isUsingTable(selectedTable) && !businessTable.equals(selectedTable)) {
            nr++;
          }
        }
      }
    }

    return nr;
  }

  /**
   * @return the olapDimensions
   */
  public List getOlapDimensions() {
    return olapDimensions.getList();
  }

  public OlapDimension findOlapDimension(String name) {
    for (int i = 0; i < olapDimensions.size(); i++) {
      OlapDimension dimension = (OlapDimension) olapDimensions.get(i);
      if (dimension.getName().equalsIgnoreCase(name))
        return dimension;
    }
    return null;
  }

  public String[] getBusinessTableNames(String locale) {
    String names[] = new String[nrBusinessTables()];
    for (int i = 0; i < nrBusinessTables(); i++)
      names[i] = getBusinessTable(i).getDisplayName(locale);
    return names;
  }

  public String getMondrianModel(String locale) throws Exception {
    MondrianModelExporter exporter = new MondrianModelExporter(this, locale);
    String xml = exporter.createMondrianModelXML();
    return xml;
  }

  /**
   * @return the olapCubes
   */
  public List getOlapCubes() {
    return olapCubes.getList();
  }

  public OlapCube findOlapCube(String name) {
    for (int i = 0; i < olapCubes.size(); i++) {
      OlapCube cube = (OlapCube) olapCubes.get(i);
      if (cube.getName().equalsIgnoreCase(name))
        return cube;
    }
    return null;
  }

  public void addUndo(Object[] from, Object[] to, int[] pos, Point[] prev, Point[] curr, int type_of_change,
      boolean nextAlso) {
  }

  public int getMaxUndo() {
    return 0;
  }

  public TransAction nextUndo() {
    return null;
  }

  public TransAction previousUndo() {
    return null;
  }

  public void setMaxUndo(int mu) {
  }

  public TransAction viewNextUndo() {
    return null;
  }

  public TransAction viewPreviousUndo() {
    return null;
  }

  public TransAction viewThisUndo() {
    return null;
  }

  public boolean hasConnection(){
    return getConnection() != null;
  }

  public void setConnection(DatabaseMeta connection) {
    this.connection = connection;

    for (int i = 0; i < nrBusinessTables(); i++) {
      BusinessTable bt = getBusinessTable(i);

      if (null == bt) {
        continue;
      }

      PhysicalTable pt = bt.getPhysicalTable();

      if (null == pt) {
        continue;
      }

      pt.setDatabaseMeta(connection);
    }
  }

  public DatabaseMeta getConnection(){

    // A bit cheap and fragile - use the connection reference from the first
    // business table to enforce this single connection on the rest of the tables added.

    if ((connection == null) && (nrBusinessTables()>0)){
      connection = getBusinessTable(0).getPhysicalTable().getDatabaseMeta();
    }

//    if (nrBusinessTables()<=0){
//      connection = null;
//    }

    return connection;
  }

  public void clearConnection(){
    connection = null;
  }

  public boolean verify(ConceptUtilityBase base){
    boolean verify  = false;

    //Connection hasn't been set yet - allow any table or column in
    if ((getConnection()==null) && (base!=null)){
      return true;
    }

    if (base == null){
      return false;
    }

    if (base instanceof PhysicalTable){

      PhysicalTable physicalTable = (PhysicalTable)base;
      verify =  physicalTable.getDatabaseMeta().equals(getConnection());

    } else if (base instanceof PhysicalColumn){

      PhysicalColumn physicalColumn = (PhysicalColumn)base;
      verify =  physicalColumn.getTable().getDatabaseMeta().equals(getConnection());
    }
    return verify;
  }

}










































