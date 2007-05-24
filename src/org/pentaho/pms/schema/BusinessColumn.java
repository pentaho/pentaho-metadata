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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.ChangedFlagInterface;
import be.ibridge.kettle.core.database.DatabaseMeta;

public class BusinessColumn extends ConceptUtilityBase implements ChangedFlagInterface, ConceptUtilityInterface, Cloneable
{
  
    private static final Log logger = LogFactory.getLog(BusinessColumn.class);
  
    private PhysicalColumn physicalColumn;
    private boolean enabled;
    
    /** The parent business table to figure out the relationships later on! */
    private BusinessTable businessTable;

    public BusinessColumn()
    {
        super();
        enabled=true; // enabled by default.
    }
    
    public BusinessColumn(String id, PhysicalColumn physicalColumn, BusinessTable businessTable)
    {
        super(id);
        enabled=true; // enabled by default.
        setBusinessTable(businessTable);
        setPhysicalColumn(physicalColumn);
    }

    public BusinessColumn(String id)
    {
        super(id);
        enabled=true; // enabled by default.
    }

    /**
     * @return the description of the model element 
     */
    public String getModelElementDescription()
    {
        return Messages.getString("BusinessColumn.USER_DESCRIPTION"); //$NON-NLS-1$
    }

    
    public Object clone()
    {
        try
        {
            BusinessColumn businessColumn = (BusinessColumn) super.clone();

            businessColumn.setConcept((ConceptInterface) getConcept().clone()); // deep copy
            businessColumn.setPhysicalColumn(physicalColumn); // shallow copy
            
            return businessColumn;
        }
        catch(CloneNotSupportedException e)
        {
            return null;
        }
    }
    
    public static final String proposeId(String locale, BusinessTable businessTable, PhysicalColumn physicalColumn)
    {
        String baseID = Const.toID( businessTable.getDisplayName(locale) );
        String namePart = Const.toID( Const.NVL(physicalColumn.getName(locale), physicalColumn.getFormula() ) );
        String id = Settings.getBusinessColumnIDPrefix() + baseID+"_" + namePart; //$NON-NLS-1$
        if (Settings.isAnIdUppercase()) id = id.toUpperCase();
        return id;
    }

    public String toString()
    {
        return businessTable.getId()+"."+getId(); //$NON-NLS-1$
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * @return the phyiscalColumn
     */
    public PhysicalColumn getPhysicalColumn()
    {
        return physicalColumn;
    }

    /**
     * @param physicalColumn the phyiscalColumn to set
     */
    public void setPhysicalColumn(PhysicalColumn physicalColumn)
    {
        this.physicalColumn = physicalColumn;
        if (physicalColumn!=null)
        {
            getConcept().setInheritedInterface(physicalColumn.getConcept());
        }
        else
        {
            getConcept().setInheritedInterface(null);
        }
    }

    /**
     * @return the businessTable
     */
    public BusinessTable getBusinessTable()
    {
        return businessTable;
    }

    /**
     * @param businessTable the businessTable to set
     */
    public void setBusinessTable(BusinessTable businessTable)
    {
        this.businessTable = businessTable;
    }
    
    public String getFunctionTableAndColumnForSQL(BusinessModel model, String locale)
    {
        DatabaseMeta databaseMeta = getBusinessTable().getPhysicalTable().getDatabaseMeta();
        
        if (isExact())
        { 
          // convert to sql using libformula subsystem
          try {
            PMSFormula formula = new PMSFormula(model, getBusinessTable(), getFormula());
            formula.parseAndValidate();
            return formula.generateSQL(locale);
          } catch (PentahoMetadataException e) {
            // this is for backwards compatibility.
            // eventually throw any errors
            logger.error(Messages.getErrorString("BusinessColumn.ERROR_0001_FAILED_TO_PARSE_FORMULA", getFormula()), e); //$NON-NLS-1$
          }
          return getFormula();
        }
        else
        {
            String tableColumn = ""; //$NON-NLS-1$
            
            // TODO: WPG: is this correct?  shouldn't we be getting an alias for the table vs. it's display name?
            tableColumn += databaseMeta.quoteField( getBusinessTable().getDisplayName(locale) );
            tableColumn += "."; //$NON-NLS-1$
            
            // TODO: WPG: instead of using formula, shouldn't we use the physical column's name?
            tableColumn += databaseMeta.quoteField( getFormula() );
            
            if (hasAggregate()) // For the having clause, for example: HAVING sum(turnover) > 100
            {
                return getFunction(databaseMeta)+"("+tableColumn+")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                return tableColumn;
            }
        }
    }
    
    public String getFunction(DatabaseMeta databaseMeta)
    {
        String fn=""; //$NON-NLS-1$
        
        switch(getAggregationType().getType())
        {
            case AggregationSettings.TYPE_AGGREGATION_AVERAGE: fn=databaseMeta.getFunctionAverage(); break;
            case AggregationSettings.TYPE_AGGREGATION_COUNT  : fn=databaseMeta.getFunctionCount(); break;
            case AggregationSettings.TYPE_AGGREGATION_MAXIMUM: fn=databaseMeta.getFunctionMaximum(); break;
            case AggregationSettings.TYPE_AGGREGATION_MINIMUM: fn=databaseMeta.getFunctionMinimum(); break;
            case AggregationSettings.TYPE_AGGREGATION_SUM    : fn=databaseMeta.getFunctionSum(); break;
            default: break;
        }
        
        return fn;
    }
}
