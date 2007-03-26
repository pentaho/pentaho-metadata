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

        xml.append("<Schema ");
        xml.append("name=\"");
        XMLHandler.appendReplacedChars(xml, businessModel.getDisplayName(locale));
        xml.append("\">");
        xml.append(Const.CR);
        
        List olapDimensions = businessModel.getOlapDimensions();
        for (int d=0;d<olapDimensions.size();d++)
        {
            OlapDimension olapDimension = (OlapDimension) olapDimensions.get(d);
            
            xml.append("  <Dimension");

            xml.append(" name=\"");
            XMLHandler.appendReplacedChars(xml, olapDimension.getName());
            xml.append("\"");
            
            if (olapDimension.isTimeDimension())
            {
                xml.append(" type=\"");
                XMLHandler.appendReplacedChars(xml, "TimeDimension");
                xml.append("\"");
            }
            xml.append(">");
            xml.append(Const.CR);
            
            List olapHierarchies = olapDimension.getHierarchies();
            for (int h=0;h<olapHierarchies.size();h++)
            {
                OlapHierarchy olapHierarchy = (OlapHierarchy) olapHierarchies.get(h);
                xml.append("    <Hierarchy");

                xml.append(" hasAll=\"");
                xml.append(olapHierarchy.isHavingAll()?"true":"false");
                xml.append("\"");
                
                xml.append(" primaryKey=\"");
                xml.append(olapHierarchy.getPrimaryKey().getFormula());
                xml.append("\"");
                xml.append(">");
                xml.append(Const.CR);
                
                xml.append("      <Table");
                xml.append(" name=\"");
                XMLHandler.appendReplacedChars(xml, olapHierarchy.getBusinessTable().getTargetTable());
                xml.append("\"");
                xml.append("/>");
                xml.append(Const.CR);

                List hierarchyLevels = olapHierarchy.getHierarchyLevels();
                for (int hl=0;hl<hierarchyLevels.size();hl++)
                {
                    OlapHierarchyLevel olapHierarchyLevel = (OlapHierarchyLevel) hierarchyLevels.get(hl);

                    xml.append("      <Level");
                    xml.append(" name=\"");
                    XMLHandler.appendReplacedChars(xml, olapHierarchyLevel.getName());
                    xml.append("\"");
                    
                    xml.append(" column=\"");
                    XMLHandler.appendReplacedChars(xml, olapHierarchyLevel.getReferenceColumn().getFormula());
                    xml.append("\"");

                    xml.append(" uniqueMembers=\"");
                    XMLHandler.appendReplacedChars(xml, olapHierarchyLevel.isHavingUniqueMembers()?"true":"false");
                    xml.append("\"");
                    xml.append(">");
                    xml.append(Const.CR);
                    
                    List businessColumns = olapHierarchyLevel.getBusinessColumns();
                    for (int i=0;i<businessColumns.size();i++)
                    {
                        BusinessColumn businessColumn = (BusinessColumn) businessColumns.get(i);
                        xml.append("        <Property");

                        xml.append(" name=\"");
                        XMLHandler.appendReplacedChars(xml, businessColumn.getDisplayName(locale));
                        xml.append("\"");
                        
                        xml.append(" column=\"");
                        XMLHandler.appendReplacedChars(xml, businessColumn.getFormula());
                        xml.append("\"");

                        DataTypeSettings dataType = businessColumn.getDataType();
                        String typeDesc = null;
                        switch(dataType.getType())
                        {
                        case DataTypeSettings.DATA_TYPE_STRING:  typeDesc = "String"; break;
                        case DataTypeSettings.DATA_TYPE_NUMERIC: typeDesc = "Numeric"; break;
                        case DataTypeSettings.DATA_TYPE_BOOLEAN: typeDesc = "Boolean"; break;
                        case DataTypeSettings.DATA_TYPE_DATE:    typeDesc = "Date"; break;
                        }
                        
                        if (typeDesc!=null)
                        {
                            xml.append(" type=\"");
                            XMLHandler.appendReplacedChars(xml, typeDesc);
                            xml.append("\"");
                        }

                        xml.append("/>");
                        xml.append(Const.CR);
                    }

                    xml.append("      </Level>").append(Const.CR);

                }

                xml.append("    </Hierarchy>").append(Const.CR);
            }
            
            xml.append("  </Dimension>").append(Const.CR);
            
        }
        
        // Now do the cubes too...
        
        List olapCubes = businessModel.getOlapCubes();
        for (int c=0;c<olapCubes.size();c++)
        {
            OlapCube olapCube = (OlapCube) olapCubes.get(c);
            
            xml.append("  <Cube");
            
            xml.append(" name=\"");
            XMLHandler.appendReplacedChars(xml, olapCube.getName());
            xml.append("\"");
            xml.append(">").append(Const.CR);

            xml.append("    <Table");
            
            xml.append(" name=\"");
            XMLHandler.appendReplacedChars(xml, olapCube.getBusinessTable().getTargetTable());
            xml.append("\"");
            xml.append("/>").append(Const.CR);

            //  DIMENSION USAGE
            //
            List usages = olapCube.getOlapDimensionUsages();
            for (int u=0;u<usages.size();u++)
            {
                OlapDimensionUsage usage = (OlapDimensionUsage) usages.get(u);
                
                xml.append("    <DimensionUsage");
                
                xml.append(" name=\"");
                XMLHandler.appendReplacedChars(xml, usage.getName());
                xml.append("\"");

                xml.append(" source=\"");
                XMLHandler.appendReplacedChars(xml, usage.getOlapDimension().getName());
                xml.append("\"");

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
                    
                    xml.append(" foreignKey=\"");
                    XMLHandler.appendReplacedChars(xml, keyColumn.getFormula());
                    xml.append("\"");
                }
                else
                {
                    throw new Exception("Can't create model!\nThere is no relationship between tables ["+dimTable.getDisplayName(locale)+"] and ["+cubeTable+"]");
                }
                xml.append("/>").append(Const.CR);
            }
            
            // MEASURES
            //
            List measures = olapCube.getOlapMeasures();
            for (int m=0;m<measures.size();m++)
            {
                OlapMeasure measure = (OlapMeasure) measures.get(m);
                BusinessColumn businessColumn = measure.getBusinessColumn();
                
                xml.append("    <Measure");

                xml.append(" name=\"");
                XMLHandler.appendReplacedChars(xml, businessColumn.getDisplayName(locale));
                xml.append("\"");   

                xml.append(" column=\"");
                XMLHandler.appendReplacedChars(xml, businessColumn.getFormula());
                xml.append("\"");   

                AggregationSettings aggregationType = businessColumn.getAggregationType();
                String typeDesc=null;
                switch(aggregationType.getType())
                {
                case AggregationSettings.TYPE_AGGREGATION_NONE            : typeDesc="none"; break;
                case AggregationSettings.TYPE_AGGREGATION_SUM             : typeDesc="sum"; break;
                case AggregationSettings.TYPE_AGGREGATION_AVERAGE         : typeDesc="avg"; break;
                case AggregationSettings.TYPE_AGGREGATION_COUNT           : typeDesc="count"; break;
                case AggregationSettings.TYPE_AGGREGATION_COUNT_DISTINCT  : typeDesc="distinct count"; break;
                case AggregationSettings.TYPE_AGGREGATION_MINIMUM         : typeDesc="min"; break;
                case AggregationSettings.TYPE_AGGREGATION_MAXIMUM         : typeDesc="max"; break;
                }
                
                if (typeDesc!=null)
                {
                    xml.append(" aggregator=\"");
                    XMLHandler.appendReplacedChars(xml, typeDesc);
                    xml.append("\"");
                }
                
                String formatString = businessColumn.getMask();
                if (Const.isEmpty(formatString)) formatString = "Standard";

                xml.append(" formatString=\"");
                XMLHandler.appendReplacedChars(xml, formatString);
                xml.append("\"");
                
                xml.append("/>").append(Const.CR);
            }
            
            xml.append("  </Cube>").append(Const.CR);
        }


        xml.append("</Schema>");

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
