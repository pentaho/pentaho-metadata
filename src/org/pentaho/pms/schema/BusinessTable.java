/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
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

import java.util.Iterator;

import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;
import org.w3c.dom.Node;

import be.ibridge.kettle.core.ChangedFlagInterface;
import be.ibridge.kettle.core.GUIPositionInterface;
import be.ibridge.kettle.core.Point;
import be.ibridge.kettle.core.XMLHandler;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.list.UniqueArrayList;
import be.ibridge.kettle.core.list.UniqueList;


/**
 * A business table maps to a Physical table with a certain id (this is used as ID)
 * 
 * @author Matt
 * @since  18-aug-2006
 */
public class BusinessTable extends ConceptUtilityBase 
    implements Cloneable, GUIPositionInterface, ChangedFlagInterface, ConceptUtilityInterface, Comparable
{
    private Point location;
    private boolean changed;
    private boolean drawn;
    
    private boolean selected;
    
    private PhysicalTable physicalTable;
    
    private UniqueList businessColumns;
    
    public BusinessTable()
    {
        super();
        this.physicalTable = null;
        this.businessColumns = new UniqueArrayList();
        
        this.location = new Point(150, 150);
        this.drawn = true;
        this.changed = true;
    }
    
    public BusinessTable(String id)
    {
        this();
        try
        {
            setId(id);
        }
        catch (ObjectAlreadyExistsException e)
        {
            // Ignore, there are no listeners defined yet.
        }
    }
    
    public BusinessTable(String id, PhysicalTable physicalTable)
    {
        this(id);
        setPhysicalTable(physicalTable);
    }
    
    /**
     * @return the description of the model element 
     */
    public String getModelElementDescription()
    {
        return Messages.getString("BusinessTable.USER_DESCRIPTION"); //$NON-NLS-1$
    }

    public Object clone()
    {
        BusinessTable businessTable = new BusinessTable(getId(), physicalTable);
        
        // TODO (GEM) this could be a problem - not sure about dependencies; see case 
        // PMD-85 and PMD-86 for why this is here. 
        businessTable.idChangedListeners = this.idChangedListeners;
        
        businessTable.setConcept( (ConceptInterface) getConcept().clone() ); // deep copy
        businessTable.getBusinessColumns().clear(); // clear the list of column
        for (int i=0;i<nrBusinessColumns();i++)
        {
            try
            {
                businessTable.addBusinessColumn((BusinessColumn) getBusinessColumn(i).clone());
            }
            catch (ObjectAlreadyExistsException e)
            {
                throw new RuntimeException(e); // This should not happen, but I don't like to swallow the error.
            }
        }
        
        // GUI stuff too
        if (location!=null)
        {
            businessTable.setLocation( new Point(location.x, location.y) );
        }
        else
        {
            businessTable.setLocation( null );
        }
        return businessTable;
    }
    
    /**
     * 
     * @param tables List of tables to compare new table id against
     * @return a new BusinessTable, duplicate of this, with only the id changed to be unique in it's list
     */
    public BusinessTable cloneUnique (String locale, UniqueList tables){
      
      BusinessTable businessTable  = (BusinessTable)clone(); 

      businessTable.getBusinessColumns().clear(); // clear the list of column
      for (int i=0;i<nrBusinessColumns();i++)
      {
          try
          {
              businessTable.addBusinessColumn(getBusinessColumn(i).cloneUnique(locale, businessColumns));
          }
          catch (ObjectAlreadyExistsException e)
          {
              throw new RuntimeException(e); // This should not happen, but I don't like to swallow the error.
          }
      }
      
      String newId = proposeId(locale, this, physicalTable, tables);
      try {
        businessTable.setId(newId);
      } catch (ObjectAlreadyExistsException e) {
        return null;
      }
      
      return businessTable;
      
    }
 
    public static final String proposeId(String locale, BusinessTable businessTable, PhysicalTable physicalTable)
    {
        String baseID = Const.toID( businessTable.getDisplayName(locale) );
        String namePart = Const.toID( Const.NVL(physicalTable.getName(locale), physicalTable.getFormula() ) );
        String id = Settings.getBusinessTableIDPrefix() + baseID+"_" + namePart; //$NON-NLS-1$
        if (Settings.isAnIdUppercase()) id = id.toUpperCase();
        return id;
    }
    
    public static final String proposeId(String locale, BusinessTable businessTable, PhysicalTable physicalTable, UniqueList tables){
      boolean gotNew = false;
      boolean found = false;
      String id = proposeId(locale, businessTable, physicalTable);
      int catNr = 1;
      String newId = id;
      
      
      while (!gotNew) {
        
        for (Iterator iter = tables.iterator(); iter.hasNext();) {
          ConceptUtilityBase element = (ConceptUtilityBase) iter.next();
          if (element.getId().equalsIgnoreCase(newId)){
            found = true;
            break;
          }
        }
        if (found){
          catNr++;
          newId = id + "_" + catNr; //$NON-NLS-1$
          found = false;
        }else{
          gotNew = true;
        }
      }
      
      if (Settings.isAnIdUppercase())
        newId = newId.toUpperCase();
      
      return newId;
    }
    
    public BusinessCategory generateCategory(String locale, BusinessCategory rootCat){

      BusinessCategory businessCategory = new BusinessCategory();
      try {
        businessCategory.setId(BusinessCategory.proposeId(locale, this, businessCategory, rootCat.getBusinessCategories()));
      } catch (ObjectAlreadyExistsException ex) {
        throw new RuntimeException(ex); // This should not happen ...
      }

      // The name is the same as the table...
      String categoryName = getDisplayName(locale);

      boolean gotNew = false;
      boolean found = false;
      int catNr = 1;
      String newName = categoryName;
      
      
      while (!gotNew) {
        
        for (Iterator iter = rootCat.getBusinessCategories().iterator(); iter.hasNext();) {
          ConceptUtilityBase element = (ConceptUtilityBase) iter.next();
          if (element.getName(locale).equalsIgnoreCase(newName)){
            found = true;
            break;
          }
        }
        if (found){
          catNr++;
          newName = categoryName + "_" + catNr; //$NON-NLS-1$
          found = false;
        }else{
          gotNew = true;
        }
      }
      businessCategory.getConcept().setName(locale, newName);

      // add the business columns to the category
      //
      for (int i = nrBusinessColumns() - 1; i >= 0; i--) {
        try {
          businessCategory.addBusinessColumn(getBusinessColumn(i).cloneUnique(locale, rootCat.getAllBusinessColumns()));
        } catch (ObjectAlreadyExistsException e) {
          throw new RuntimeException(e); // This should not happen ...
        }
      }
      
      return businessCategory;
    }
    
    /**
     * @return the changed
     */
    public boolean hasChanged()
    {
        return changed;
    }
    
    /**
     * @param changed the changed to set
     */
    public void setChanged(boolean changed)
    {
        this.changed = changed;
    }

    public void setChanged()
    {
       setChanged(true);
    }

    /**
     * @return the physicalTable
     */
    public PhysicalTable getPhysicalTable()
    {
        return physicalTable;
    }

    /**
     * @param physicalTable the physicalTable to set
     */
    public void setPhysicalTable(PhysicalTable physicalTable)
    {
        this.physicalTable = physicalTable;
        
        if (physicalTable!=null)
        {
            // Make the business table inherit from the physical table...
            getConcept().setInheritedInterface(physicalTable.getConcept());
        }
        else
        {
            getConcept().setInheritedInterface(null);
        }
    }

    /**
     * @return the businessColumns
     */
    public UniqueList getBusinessColumns()
    {
        return businessColumns;
    }
    
    public void readData(Node tablenode)
    {
        String sxloc   = XMLHandler.getTagValue(tablenode, "xloc"); //$NON-NLS-1$
        String syloc   = XMLHandler.getTagValue(tablenode, "yloc"); //$NON-NLS-1$

        int x   = Const.toInt(sxloc, 0);
        int y   = Const.toInt(syloc, 0);
        location = new Point(x,y);

        
        String sdrawn  = XMLHandler.getTagValue(tablenode, "draw_table"); //$NON-NLS-1$
        drawn = "Y".equalsIgnoreCase(sdrawn); //$NON-NLS-1$
    }
    
    public String getXML()
    {
        String retval="<business-table>"; //$NON-NLS-1$
        
        retval+="      "+XMLHandler.addTagValue("xloc",   location.x); //$NON-NLS-1$ //$NON-NLS-2$
        retval+="      "+XMLHandler.addTagValue("yloc",   location.y); //$NON-NLS-1$ //$NON-NLS-2$

        retval+="      "+XMLHandler.addTagValue("draw_table", drawn); //$NON-NLS-1$ //$NON-NLS-2$

        retval+="</business-table>"; //$NON-NLS-1$
        
        return retval;
    }

    /**
     * @return the drawn
     */
    public boolean isDrawn()
    {
        return drawn;
    }

    /**
     * @param drawn the drawn to set
     */
    public void setDrawn(boolean drawn)
    {
        if (this.drawn!=drawn) setChanged();
        this.drawn = drawn;
    }
    
    public void hide()
    {
        setDrawn(false);
    }
    
    public void setLocation(int x, int y)
    {
        int nx = (x>=0?x:0);
        int ny = (y>=0?y:0);
        
        Point loc = new Point(nx,ny);
        if (!loc.equals(location)) setChanged();
        location=loc;
    }
    
    public void setLocation(Point loc)
    {
        if (loc!=null && !loc.equals(location)) setChanged();
        location = loc;
    }
    
    public Point getLocation()
    {
        return location;
    }
    
    public void setSelected(boolean sel)
    {
        selected=sel;
    }

    public void flipSelected()
    {
        selected=!selected;
    }

    public boolean isSelected()
    {
        return selected;
    }

    
    public int nrBusinessColumns()
    {
        return businessColumns.size();
    }

    /**
     * Finds a business column in the table using the id of the column
     * @param columnId The id
     * @return the business column or null if nothing could be found.
     */
    public BusinessColumn findBusinessColumn(String columnId)
    {
        for (int i=0;i<nrBusinessColumns();i++)
        {
            BusinessColumn businessColumn = getBusinessColumn(i);
            if (businessColumn.getId().equalsIgnoreCase(columnId)) return businessColumn;
        }
        return null;
    }

    /**
     * Finds a business column using the displayed name and the locale.  If nothing is found, the IDs are searched as well.
     * @param name The localized name or the ID in case nothing could be found
     * @param locale the locale
     * @return The business column or null if nothing could be found
     */
    public BusinessColumn findBusinessColumn(String locale, String name)
    {
        for (int i=0;i<nrBusinessColumns();i++)
        {
            BusinessColumn businessColumn = getBusinessColumn(i);
            String displayName = businessColumn.getDisplayName(locale);
            if (displayName!=null && displayName.equals(name)) return businessColumn;
        }
        for (int i=0;i<nrBusinessColumns();i++)
        {
            BusinessColumn businessColumn = getBusinessColumn(i);
            String id = businessColumn.getId();
            if (id!=null && id.equals(name)) return businessColumn;
        }
        return null;
    }
    
    public BusinessColumn getBusinessColumn(int i)
    {
    		if( i <  businessColumns.size() ) {
    			return (BusinessColumn) businessColumns.get(i);
    		} else {
    			return null;
    		}
    }

    public void addBusinessColumn(BusinessColumn businessColumn) throws ObjectAlreadyExistsException
    {
        businessColumns.add(businessColumn);
        // businessColumn.getConcept().setSecurityParentInterface(getConcept()); // set the security parent to the table, not the physical column
        setChanged(true);
    }

    public void addBusinessColumn(int index, BusinessColumn businessColumn) throws ObjectAlreadyExistsException
    {
        businessColumns.add(index, businessColumn);
        // businessColumn.getConcept().setSecurityParentInterface(getConcept()); // set the security parent to the table, not the physical column
        setChanged(true);
    }

    public int indexOfBusinessColumn(BusinessColumn businessColumn)
    {
        return businessColumns.indexOf(businessColumn);
    }
    
    public void removeBusinessColumn(int index)
    {
        getBusinessColumn(index).getConcept().setSecurityParentInterface(null); // clear the security parent

        businessColumns.remove(index);
        setChanged(true);
    }

    public void clearChanged()
    {
        setChanged(false);
        for (int i=0;i<nrBusinessColumns();i++)
        {
            BusinessColumn businessColumn = getBusinessColumn(i);
            businessColumn.clearChanged();
        }
    }

    public int compareTo(Object obj)
    {
        BusinessTable businessTable = (BusinessTable) obj;
        return getId().compareTo(businessTable.getId());
    }

    /**
     * @return the IDs of all the business columns
     */
    public String[] getColumnIDs()
    {
        String[] ids = new String[nrBusinessColumns()];
        for (int i=0;i<nrBusinessColumns();i++)
        {
            ids[i] = getBusinessColumn(i).getId();
        }
        
        return ids;
    }
    
    /**
     * @return the display names of all the business columns
     */
    public String[] getColumnNames(String locale)
    {
        String[] bColumns = new String[nrBusinessColumns()];
        for (int i=0;i<nrBusinessColumns();i++)
        {
            bColumns[i] = getBusinessColumn(i).getDisplayName(locale);
        }
        
        return bColumns;
    }
}
