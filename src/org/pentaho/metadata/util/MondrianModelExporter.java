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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.model.olap.*;

public class MondrianModelExporter
{
    private LogicalModel businessModel;

    private String       locale;

    /**
     * @param businessModel
     */
    public MondrianModelExporter(LogicalModel businessModel, String locale)
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
      
        String name = businessModel.getName(locale);
        if (businessModel.getProperty("AGILE_BI_GENERATED_SCHEMA") != null) {
          // clean up the _OLAP suffix on the name
          name = name.replace("_OLAP", "");
        }
        XMLHandler.appendReplacedChars(xml, name);
        xml.append("\">"); //$NON-NLS-1$
        xml.append(Util.CR);
        
        @SuppressWarnings("unchecked")
        List<OlapDimension> olapDimensions = (List<OlapDimension>)businessModel.getProperty("olap_dimensions"); //$NON-NLS-1$
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
            xml.append(Util.CR);
            
            List olapHierarchies = olapDimension.getHierarchies();
            for (int h=0;h<olapHierarchies.size();h++)
            {
                OlapHierarchy olapHierarchy = (OlapHierarchy) olapHierarchies.get(h);
                xml.append("    <Hierarchy"); //$NON-NLS-1$

                // don't specify the hierarchy name if it's the same as the dimension name
                if(StringUtils.isNotEmpty(olapHierarchy.getName()) &&
                    !StringUtils.equals(olapHierarchy.getName(), olapDimension.getName())
                ){
                  xml.append(" name=\""); //$NON-NLS-1$
                  xml.append(olapHierarchy.getName());
                  xml.append("\""); //$NON-NLS-1$
                }
                
                xml.append(" hasAll=\""); //$NON-NLS-1$
                xml.append(olapHierarchy.isHavingAll()?"true":"false"); //$NON-NLS-1$ //$NON-NLS-2$
                xml.append("\""); //$NON-NLS-1$
                
                if( olapHierarchy.getPrimaryKey() != null ) {
                    xml.append(" primaryKey=\""); //$NON-NLS-1$
                    xml.append(olapHierarchy.getPrimaryKey().getProperty(SqlPhysicalColumn.TARGET_COLUMN));
                    xml.append("\""); //$NON-NLS-1$
                }
                xml.append(">"); //$NON-NLS-1$
                xml.append(Util.CR);

                if (olapHierarchy.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE_TYPE) == TargetTableType.INLINE_SQL) {
                  xml.append("    <View alias=\"FACT\">").append(Util.CR);
                  xml.append("        <SQL dialect=\"generic\">").append(Util.CR);
                  xml.append("         <![CDATA["+ olapHierarchy.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE) + "]]>").append(Util.CR);
                  xml.append("        </SQL>").append(Util.CR);
                  xml.append("    </View>").append(Util.CR);
                } else {
                  xml.append("      <Table"); //$NON-NLS-1$
                  xml.append(" name=\""); //$NON-NLS-1$
                  XMLHandler.appendReplacedChars(xml, (String)olapHierarchy.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE));
                  xml.append("\""); //$NON-NLS-1$
                  if (!StringUtils.isBlank((String)olapHierarchy.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_SCHEMA))) {
                    xml.append(" schema=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, (String)olapHierarchy.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_SCHEMA));
                    xml.append("\""); //$NON-NLS-1$
                  }
                  xml.append("/>"); //$NON-NLS-1$
                  xml.append(Util.CR);
                }

                List hierarchyLevels = olapHierarchy.getHierarchyLevels();
                for (int hl=0;hl<hierarchyLevels.size();hl++)
                {
                    OlapHierarchyLevel olapHierarchyLevel = (OlapHierarchyLevel) hierarchyLevels.get(hl);

                    xml.append("      <Level"); //$NON-NLS-1$
                    xml.append(" name=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, olapHierarchyLevel.getName());
                    xml.append("\""); //$NON-NLS-1$
                    
                    xml.append(" column=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, (String)olapHierarchyLevel.getReferenceColumn().getProperty(SqlPhysicalColumn.TARGET_COLUMN));
                    xml.append("\""); //$NON-NLS-1$

                    DataType dataTypeLevel = olapHierarchyLevel.getReferenceColumn().getDataType();
                    String typeDescLevel = null;
                    switch(dataTypeLevel) {
                      case STRING:  typeDescLevel = "String"; break; //$NON-NLS-1$
                      case NUMERIC: typeDescLevel = "Numeric"; break; //$NON-NLS-1$
                      case BOOLEAN: typeDescLevel = "Boolean"; break; //$NON-NLS-1$
                      // Date Type caused BISERVER-6670, removing it for now until we can investigate further 
                      // case DATE:    typeDescLevel = "Date"; break; //$NON-NLS-1$
                    }
                        
                    if (typeDescLevel!=null)
                    {
                        xml.append(" type=\""); //$NON-NLS-1$
                        XMLHandler.appendReplacedChars(xml, typeDescLevel);
                        xml.append("\""); //$NON-NLS-1$
                    }

                    xml.append(" uniqueMembers=\""); //$NON-NLS-1$
                    XMLHandler.appendReplacedChars(xml, olapHierarchyLevel.isHavingUniqueMembers()?"true":"false"); //$NON-NLS-1$ //$NON-NLS-2$
                    xml.append("\""); //$NON-NLS-1$
                    xml.append(">"); //$NON-NLS-1$
                    xml.append(Util.CR);

                    if(olapHierarchyLevel.getAnnotations().size() > 0) {
                      xml.append("        <Annotations>");

                      for(OlapAnnotation annotation : olapHierarchyLevel.getAnnotations()) {
                        xml.append(Util.CR);
                        xml.append(annotation.asXml());
                      }
                      xml.append(Util.CR);
                      xml.append("        </Annotations>");
                      xml.append(Util.CR);
                    }

                    List businessColumns = olapHierarchyLevel.getLogicalColumns();
                    for (int i=0;i<businessColumns.size();i++)
                    {
                        LogicalColumn businessColumn = (LogicalColumn) businessColumns.get(i);
                        xml.append("        <Property"); //$NON-NLS-1$

                        xml.append(" name=\""); //$NON-NLS-1$

                        XMLHandler.appendReplacedChars(xml, businessColumn.getName(locale));

                        xml.append("\""); //$NON-NLS-1$
                        
                        xml.append(" column=\""); //$NON-NLS-1$
                        XMLHandler.appendReplacedChars(xml, (String)businessColumn.getProperty(SqlPhysicalColumn.TARGET_COLUMN));
                        xml.append("\""); //$NON-NLS-1$

                        DataType dataType = businessColumn.getDataType();
                        String typeDesc = null;
                        switch(dataType) {
                        case STRING:  typeDesc = "String"; break; //$NON-NLS-1$
                        case NUMERIC: typeDesc = "Numeric"; break; //$NON-NLS-1$
                        case BOOLEAN: typeDesc = "Boolean"; break; //$NON-NLS-1$
                        case DATE:    typeDesc = "Date"; break; //$NON-NLS-1$
                        }
                        
                        if (typeDesc!=null)
                        {
                            xml.append(" type=\""); //$NON-NLS-1$
                            XMLHandler.appendReplacedChars(xml, typeDesc);
                            xml.append("\""); //$NON-NLS-1$
                        }

                        if(businessColumn.getDescription() != null) {
                          xml.append(" description=\""); //$NON-NLS-1$
                          XMLHandler.appendReplacedChars(xml, businessColumn.getDescription(locale));
                          xml.append("\""); //$NON-NLS-1$
                        }
                      
                        xml.append("/>"); //$NON-NLS-1$
                        xml.append(Util.CR);
                    }

                    xml.append("      </Level>").append(Util.CR); //$NON-NLS-1$

                }

                xml.append("    </Hierarchy>").append(Util.CR); //$NON-NLS-1$
            }
            
            xml.append("  </Dimension>").append(Util.CR); //$NON-NLS-1$
            
        }
        
        // Now do the cubes too...
        
        @SuppressWarnings("unchecked")
        List<OlapCube> olapCubes = (List<OlapCube>)businessModel.getProperty("olap_cubes"); //$NON-NLS-1$
        if(olapCubes != null){
          for (int c=0;c<olapCubes.size();c++)
          {
              OlapCube olapCube = (OlapCube) olapCubes.get(c);

              xml.append("  <Cube"); //$NON-NLS-1$

              xml.append(" name=\""); //$NON-NLS-1$
              XMLHandler.appendReplacedChars(xml, olapCube.getName());
              xml.append("\""); //$NON-NLS-1$
              xml.append(">").append(Util.CR); //$NON-NLS-1$

              if (olapCube.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE_TYPE) == TargetTableType.INLINE_SQL) {
                xml.append("    <View alias=\"FACT\">").append(Util.CR);
                xml.append("        <SQL dialect=\"generic\">").append(Util.CR);
                xml.append("         <![CDATA["+ olapCube.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE) + "]]>").append(Util.CR);
                xml.append("        </SQL>").append(Util.CR);
                xml.append("    </View>").append(Util.CR);
              } else {
                xml.append("    <Table"); //$NON-NLS-1$
                xml.append(" name=\""); //$NON-NLS-1$
                XMLHandler.appendReplacedChars(xml, (String)olapCube.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE));
                xml.append("\""); //$NON-NLS-1$
                if (!StringUtils.isBlank((String)olapCube.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_SCHEMA))) {
                  xml.append(" schema=\""); //$NON-NLS-1$
                  XMLHandler.appendReplacedChars(xml, (String)olapCube.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_SCHEMA));
                  xml.append("\""); //$NON-NLS-1$
                }
                xml.append("/>").append(Util.CR); //$NON-NLS-1$
              }

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
                  LogicalTable dimTable = usage.getOlapDimension().findLogicalTable();
                  LogicalTable cubeTable = olapCube.getLogicalTable();
                  LogicalRelationship relationshipMeta = businessModel.findRelationshipUsing(dimTable, cubeTable);

                  if( dimTable.equals( cubeTable ) && relationshipMeta == null ) {
                    // this is ok
                  }
                  else if (relationshipMeta!=null)
                  {
                      LogicalColumn keyColumn;
                      if (relationshipMeta.getFromTable().equals(dimTable))
                      {
                          keyColumn = relationshipMeta.getToColumn();
                      }
                      else
                      {
                          keyColumn = relationshipMeta.getFromColumn();
                      }

                      xml.append(" foreignKey=\""); //$NON-NLS-1$
                      XMLHandler.appendReplacedChars(xml, (String)keyColumn.getProperty(SqlPhysicalColumn.TARGET_COLUMN));
                      xml.append("\""); //$NON-NLS-1$
                  }
                  else
                  {
                      throw new Exception(Messages.getString("MondrianModelExporter.ERROR_0001_ERROR_NO_RELATIONSHIP", dimTable.getName(locale),cubeTable.toString())); //$NON-NLS-1$
                  }
                  xml.append("/>").append(Util.CR); //$NON-NLS-1$
              }

              // MEASURES
              //
              List measures = olapCube.getOlapMeasures();
              for (int m=0;m<measures.size();m++)
              {
                  OlapMeasure measure = (OlapMeasure) measures.get(m);
                  LogicalColumn businessColumn = measure.getLogicalColumn();

                  xml.append("    <Measure"); //$NON-NLS-1$

                  xml.append(" name=\""); //$NON-NLS-1$
                  XMLHandler.appendReplacedChars(xml, businessColumn.getName(locale));
                  xml.append("\"");    //$NON-NLS-1$

                  xml.append(" column=\""); //$NON-NLS-1$
                  XMLHandler.appendReplacedChars(xml, (String)businessColumn.getProperty(SqlPhysicalColumn.TARGET_COLUMN));
                  xml.append("\"");    //$NON-NLS-1$

                  AggregationType aggregationType = businessColumn.getAggregationType();
                  String typeDesc=null;
                  switch(aggregationType) {
                    case NONE            : typeDesc="none"; break; //$NON-NLS-1$
                    case SUM             : typeDesc="sum"; break; //$NON-NLS-1$
                    case AVERAGE         : typeDesc="avg"; break; //$NON-NLS-1$
                    case COUNT           : typeDesc="count"; break; //$NON-NLS-1$
                    case COUNT_DISTINCT  : typeDesc="distinct count"; break; //$NON-NLS-1$
                    case MINIMUM         : typeDesc="min"; break; //$NON-NLS-1$
                    case MAXIMUM         : typeDesc="max"; break; //$NON-NLS-1$
                  }

                  if (typeDesc!=null)
                  {
                      xml.append(" aggregator=\""); //$NON-NLS-1$
                      XMLHandler.appendReplacedChars(xml, typeDesc);
                      xml.append("\""); //$NON-NLS-1$
                  }

                  String formatString = (String)businessColumn.getProperty("mask"); //$NON-NLS-1$
                  if (StringUtils.isEmpty(formatString)) formatString = "Standard"; //$NON-NLS-1$

                  xml.append(" formatString=\""); //$NON-NLS-1$
                  XMLHandler.appendReplacedChars(xml, formatString);
                  xml.append("\""); //$NON-NLS-1$

                  xml.append("/>").append(Util.CR); //$NON-NLS-1$
              }

              xml.append("  </Cube>").append(Util.CR); //$NON-NLS-1$
          }
        }


        xml.append("</Schema>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * @return the businessModel
     */
    public LogicalModel getLogicalModel()
    {
        return businessModel;
    }

    /**
     * @param businessModel the businessModel to set
     */
    public void setLogicalModel(LogicalModel businessModel)
    {
        this.businessModel = businessModel;
    }
}
