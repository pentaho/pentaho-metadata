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
package org.pentaho.pms.schema;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.changed.ChangedFlagInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.gui.Point;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.olap.OlapCube;
import org.pentaho.pms.schema.olap.OlapDimension;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.UniqueArrayList;
import org.pentaho.pms.util.UniqueList;

/**
 * @deprecated as of metadata 3.0.  Please use org.pentaho.metadata.model.LogicalModel
 */
public class BusinessModel extends ConceptUtilityBase implements ChangedFlagInterface, Cloneable,
    ConceptUtilityInterface {

  private UniqueList<BusinessTable> businessTables;

  private List<RelationshipMeta> relationships;

  private List<NotePadMeta> notes;

  private UniqueList<OlapDimension> olapDimensions;

  private UniqueList<OlapCube> olapCubes;

  private BusinessCategory rootCategory;

  // TODO: Until support for multiple connections is implemented, identify the one connection
  // this model holds reference to.
  public DatabaseMeta connection = null;

  public BusinessModel() {
    super();
    this.businessTables = new UniqueArrayList<BusinessTable>();
    this.relationships = new ArrayList<RelationshipMeta>();
    this.notes = new ArrayList<NotePadMeta>();
    this.olapDimensions = new UniqueArrayList<OlapDimension>();
    this.olapCubes = new UniqueArrayList<OlapCube>();

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
  public void setNotes(List<NotePadMeta> notes) {
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
      if (rect.contains(p.x,p.y))
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
    ArrayList<BusinessTable> modelTables = new ArrayList<BusinessTable>();

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
  public List<BusinessTable> getSelectedDrawnBusinessTableList() {
    List<BusinessTable> list = new ArrayList<BusinessTable>();

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
  public UniqueList<BusinessColumn> getAllBusinessColumns() {
    UniqueList<BusinessColumn> columns = new UniqueArrayList<BusinessColumn>();

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
    List<BusinessColumnString> strings = new ArrayList<BusinessColumnString>();
    Stack<BusinessCategory> categoriesPath = new Stack<BusinessCategory>();

    getFlatCategoriesView(strings, categoriesPath, rootCategory, locale);

    return strings;
  }

  private void getFlatCategoriesView(List<BusinessColumnString> strings, Stack<BusinessCategory> categoriesPath, BusinessCategory parentCategory, String locale) {
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
  public List<OlapDimension> getOlapDimensions() {
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
  public List<OlapCube> getOlapCubes() {
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










































