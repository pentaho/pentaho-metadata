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

import java.util.List;

import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.olap.OlapCube;
import org.pentaho.pms.schema.olap.OlapDimension;
import org.pentaho.pms.schema.olap.OlapDimensionUsage;
import org.pentaho.pms.schema.olap.OlapHierarchy;
import org.pentaho.pms.schema.olap.OlapHierarchyLevel;
import org.pentaho.pms.schema.olap.OlapMeasure;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.XMLHandler;

public class MondrianModelExporter
{
    private BusinessModel businessModel;

    private String       locale;

    /**
     * @param businessModel
     */
    public MondrianModelExporter(BusinessModel businessModel, String locale)
    {
        super();
        this.businessModel = businessModel;
        this.locale = locale;
    }

    public String createMondrianModelXML() throws Exception
    {
        StringBuffer xml = new StringBuffer(10000);

        xml.append("<Schema "); //$NON-NLS-1$
        xml.append("name=\""); //$NON-NLS-1$
        XMLHandler.appendReplacedChars(xml, businessModel.getDisplayName(locale));
        xml.append("\">"); //$NON-NLS-1$
        xml.append(Const.CR);
        
        List olapDimensions = businessModel.getOlapDimensions();
        for (int d=0;d<olapDimensions.size();d++)
        {
            OlapDimension olapDimension = (OlapDimension) olapDimensions.get(d);
            
            xml.append("  <Dimension"); //$NON-NLS-1$

            xml.append(" name=\""); //$NON-NLS-1$
            XMLHandler.appendReplacedChars(xml, olapDimension.getName());
            xml.append("\""); //$NON-NLS-1$
            
            if (olapDimension.isTimeDimension())
            {
                xml.append(" type=\""); //$NON-NLS-1$
                XMLHandler.appendReplacedChars(xml, "TimeDimension"); //$NON-NLS-1$
                xml.append("\""); //$NON-NLS-1$
            }
            xml.append(">"); //$NON-NLS-1$
            xml.append(Const.CR);
            
            List olapHierarchies = olapDimension.getHierarchies();
            for (int h=0;h<olapHierarchies.size();h++)
            {
                OlapHierarchy olapHierarchy = (OlapHierarchy) olapHierarchies.get(h);
                xml.append("    <Hierarchy"); //$NON-NLS-1$

                xml.append(" hasAll=\""); //$NON-NLS-1$
                xml.append(olapHierarchy.isHavingAll()?"true":"false"); //$NON-NLS-1$ //$NON-NLS-2$
                xml.append("\""); //$NON-NLS-1$
                
                xml.append(" primaryKey=\""); //$NON-NLS-1$
                xml.append(olapHierarchy.getPrimaryKey().getFormula());
                xml.append("\""); //$NON-NLS-1$
                xml.append(">"); //$NON-NLS-1$
                xml.append(Const.CR);
                
                xml.append("      <Table"); //$NON-NLS-1$
                xml.append(" name=\""); //$NON-NLS-1$
                XMLHandler.appendReplacedChars(xml, olapHierarchy.getBusinessTable().getTargetTable());
                xml.append("\""); //$NON-NLS-1$
                xml.append("/>"); //$NON-NLS-1$
                xml.append(Const.CR);

                List hierarchyLevels = olapHierarchy.getHierarchyLevels();
                for (int hl=0;hl<hierarchyLevels.size();hl++)
                {
                    OlapHierarchyLevel olapHierarchyLevel = (OlapHierarchyLevel) hierarchyLevels.get(hl);

                    xml.append("      <Level"); //$NON-NLS-1$
                    xml.append(" name=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, olapHierarchyLevel.getName());
                    xml.append("\""); //$NON-NLS-1$
                    
                    xml.append(" column=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, olapHierarchyLevel.getReferenceColumn().getFormula());
                    xml.append("\""); //$NON-NLS-1$

                    xml.append(" uniqueMembers=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, olapHierarchyLevel.isHavingUniqueMembers()?"true":"false"); //$NON-NLS-1$ //$NON-NLS-2$
                    xml.append("\""); //$NON-NLS-1$
                    xml.append(">"); //$NON-NLS-1$
                    xml.append(Const.CR);
                    
                    List businessColumns = olapHierarchyLevel.getBusinessColumns();
                    for (int i=0;i<businessColumns.size();i++)
                    {
                        BusinessColumn businessColumn = (BusinessColumn) businessColumns.get(i);
                        xml.append("        <Property"); //$NON-NLS-1$

                        xml.append(" name=\""); //$NON-NLS-1$
                        XMLHandler.appendReplacedChars(xml, businessColumn.getDisplayName(locale));
                        xml.append("\""); //$NON-NLS-1$
                        
                        xml.append(" column=\""); //$NON-NLS-1$
                        XMLHandler.appendReplacedChars(xml, businessColumn.getFormula());
                        xml.append("\""); //$NON-NLS-1$

                        DataTypeSettings dataType = businessColumn.getDataType();
                        String typeDesc = null;
                        switch(dataType.getType())
                        {
                        case DataTypeSettings.DATA_TYPE_STRING:  typeDesc = "String"; break; //$NON-NLS-1$
                        case DataTypeSettings.DATA_TYPE_NUMERIC: typeDesc = "Numeric"; break; //$NON-NLS-1$
                        case DataTypeSettings.DATA_TYPE_BOOLEAN: typeDesc = "Boolean"; break; //$NON-NLS-1$
                        case DataTypeSettings.DATA_TYPE_DATE:    typeDesc = "Date"; break; //$NON-NLS-1$
                        }
                        
                        if (typeDesc!=null)
                        {
                            xml.append(" type=\""); //$NON-NLS-1$
                            XMLHandler.appendReplacedChars(xml, typeDesc);
                            xml.append("\""); //$NON-NLS-1$
                        }

                        xml.append("/>"); //$NON-NLS-1$
                        xml.append(Const.CR);
                    }

                    xml.append("      </Level>").append(Const.CR); //$NON-NLS-1$

                }

                xml.append("    </Hierarchy>").append(Const.CR); //$NON-NLS-1$
            }
            
            xml.append("  </Dimension>").append(Const.CR); //$NON-NLS-1$
            
        }
        
        // Now do the cubes too...
        
        List olapCubes = businessModel.getOlapCubes();
        for (int c=0;c<olapCubes.size();c++)
        {
            OlapCube olapCube = (OlapCube) olapCubes.get(c);
            
            xml.append("  <Cube"); //$NON-NLS-1$
            
            xml.append(" name=\""); //$NON-NLS-1$
            XMLHandler.appendReplacedChars(xml, olapCube.getName());
            xml.append("\""); //$NON-NLS-1$
            xml.append(">").append(Const.CR); //$NON-NLS-1$

            xml.append("    <Table"); //$NON-NLS-1$
            
            xml.append(" name=\""); //$NON-NLS-1$
            XMLHandler.appendReplacedChars(xml, olapCube.getBusinessTable().getTargetTable());
            xml.append("\""); //$NON-NLS-1$
            xml.append("/>").append(Const.CR); //$NON-NLS-1$

            //  DIMENSION USAGE
            //
            List usages = olapCube.getOlapDimensionUsages();
            for (int u=0;u<usages.size();u++)
            {
                OlapDimensionUsage usage = (OlapDimensionUsage) usages.get(u);
                
                xml.append("    <DimensionUsage"); //$NON-NLS-1$
                
                xml.append(" name=\""); //$NON-NLS-1$
                XMLHandler.appendReplacedChars(xml, usage.getName());
                xml.append("\""); //$NON-NLS-1$

                xml.append(" source=\""); //$NON-NLS-1$
                XMLHandler.appendReplacedChars(xml, usage.getOlapDimension().getName());
                xml.append("\""); //$NON-NLS-1$

                // To know the foreign key, look up the relationship between the cube table and the dimension table...
                BusinessTable dimTable = usage.getOlapDimension().findBusinessTable();
                BusinessTable cubeTable = olapCube.getBusinessTable();
                RelationshipMeta relationshipMeta = businessModel.findRelationshipUsing(dimTable, cubeTable);

                if (relationshipMeta!=null)
                {
                    BusinessColumn keyColumn;
                    if (relationshipMeta.getTableFrom().equals(dimTable))
                    {
                        keyColumn = relationshipMeta.getFieldTo();
                    }
                    else
                    {
                        keyColumn = relationshipMeta.getFieldFrom();
                    }
                    
                    xml.append(" foreignKey=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, keyColumn.getFormula());
                    xml.append("\""); //$NON-NLS-1$
                }
                else
                {
                    throw new Exception(Messages.getString("MondrianModelExporter.ERROR_0001_ERROR_NO_RELATIONSHIP", dimTable.getDisplayName(locale),cubeTable.toString())); //$NON-NLS-1$  
                }
                xml.append("/>").append(Const.CR); //$NON-NLS-1$
            }
            
            // MEASURES
            //
            List measures = olapCube.getOlapMeasures();
            for (int m=0;m<measures.size();m++)
            {
                OlapMeasure measure = (OlapMeasure) measures.get(m);
                BusinessColumn businessColumn = measure.getBusinessColumn();
                
                xml.append("    <Measure"); //$NON-NLS-1$

                xml.append(" name=\""); //$NON-NLS-1$
                XMLHandler.appendReplacedChars(xml, businessColumn.getDisplayName(locale));
                xml.append("\"");    //$NON-NLS-1$

                xml.append(" column=\""); //$NON-NLS-1$
                XMLHandler.appendReplacedChars(xml, businessColumn.getFormula());
                xml.append("\"");    //$NON-NLS-1$

                AggregationSettings aggregationType = businessColumn.getAggregationType();
                String typeDesc=null;
                switch(aggregationType.getType())
                {
                case AggregationSettings.TYPE_AGGREGATION_NONE            : typeDesc="none"; break; //$NON-NLS-1$
                case AggregationSettings.TYPE_AGGREGATION_SUM             : typeDesc="sum"; break; //$NON-NLS-1$
                case AggregationSettings.TYPE_AGGREGATION_AVERAGE         : typeDesc="avg"; break; //$NON-NLS-1$
                case AggregationSettings.TYPE_AGGREGATION_COUNT           : typeDesc="count"; break; //$NON-NLS-1$
                case AggregationSettings.TYPE_AGGREGATION_COUNT_DISTINCT  : typeDesc="distinct count"; break; //$NON-NLS-1$
                case AggregationSettings.TYPE_AGGREGATION_MINIMUM         : typeDesc="min"; break; //$NON-NLS-1$
                case AggregationSettings.TYPE_AGGREGATION_MAXIMUM         : typeDesc="max"; break; //$NON-NLS-1$
                }
                
                if (typeDesc!=null)
                {
                    xml.append(" aggregator=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, typeDesc);
                    xml.append("\""); //$NON-NLS-1$
                }
                
                String formatString = businessColumn.getMask();
                if (Const.isEmpty(formatString)) formatString = "Standard"; //$NON-NLS-1$

                xml.append(" formatString=\""); //$NON-NLS-1$
                XMLHandler.appendReplacedChars(xml, formatString);
                xml.append("\""); //$NON-NLS-1$
                
                xml.append("/>").append(Const.CR); //$NON-NLS-1$
            }
            
            xml.append("  </Cube>").append(Const.CR); //$NON-NLS-1$
        }


        xml.append("</Schema>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * @return the businessModel
     */
    public BusinessModel getBusinessModel()
    {
        return businessModel;
    }

    /**
     * @param businessModel the businessModel to set
     */
    public void setBusinessModel(BusinessModel businessModel)
    {
        this.businessModel = businessModel;
    }
}
