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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.pms.schema;

import java.util.List;

import org.pentaho.di.core.xml.XMLHandler;
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

@SuppressWarnings( "deprecation" )
public class MondrianModelExporter {
  private BusinessModel businessModel;

  private String locale;

  /**
   * @param businessModel
   */
  public MondrianModelExporter( BusinessModel businessModel, String locale ) {
    super();
    this.businessModel = businessModel;
    this.locale = locale;
  }

  public String createMondrianModelXML() throws Exception {
    StringBuilder xml = new StringBuilder( 10000 );

    xml.append( "<Schema " ); //$NON-NLS-1$
    xml.append( "name=\"" ); //$NON-NLS-1$
    XMLHandler.appendReplacedChars( xml, businessModel.getDisplayName( locale ) );
    xml.append( "\">" ); //$NON-NLS-1$
    xml.append( Const.CR );

    List olapDimensions = businessModel.getOlapDimensions();
    for ( int d = 0; d < olapDimensions.size(); d++ ) {
      OlapDimension olapDimension = (OlapDimension) olapDimensions.get( d );

      xml.append( "  <Dimension" ); //$NON-NLS-1$

      xml.append( " name=\"" ); //$NON-NLS-1$
      XMLHandler.appendReplacedChars( xml, olapDimension.getName() );
      xml.append( "\"" ); //$NON-NLS-1$

      if ( olapDimension.isTimeDimension() ) {
        xml.append( " type=\"" ); //$NON-NLS-1$
        XMLHandler.appendReplacedChars( xml, "TimeDimension" ); //$NON-NLS-1$
        xml.append( "\"" ); //$NON-NLS-1$
      }
      xml.append( ">" ); //$NON-NLS-1$
      xml.append( Const.CR );

      List olapHierarchies = olapDimension.getHierarchies();
      for ( int h = 0; h < olapHierarchies.size(); h++ ) {
        OlapHierarchy olapHierarchy = (OlapHierarchy) olapHierarchies.get( h );
        xml.append( "    <Hierarchy" ); //$NON-NLS-1$

        xml.append( " hasAll=\"" ); //$NON-NLS-1$
        xml.append( olapHierarchy.isHavingAll() ? "true" : "false" ); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append( "\"" ); //$NON-NLS-1$

        if ( olapHierarchy.getPrimaryKey() != null ) {
          xml.append( " primaryKey=\"" ); //$NON-NLS-1$
          xml.append( olapHierarchy.getPrimaryKey().getFormula() );
          xml.append( "\"" ); //$NON-NLS-1$
        }
        xml.append( ">" ); //$NON-NLS-1$
        xml.append( Const.CR );

        xml.append( "      <Table" ); //$NON-NLS-1$
        xml.append( " name=\"" ); //$NON-NLS-1$
        XMLHandler.appendReplacedChars( xml, olapHierarchy.getBusinessTable().getTargetTable() );
        xml.append( "\"" ); //$NON-NLS-1$
        xml.append( "/>" ); //$NON-NLS-1$
        xml.append( Const.CR );

        List hierarchyLevels = olapHierarchy.getHierarchyLevels();
        for ( int hl = 0; hl < hierarchyLevels.size(); hl++ ) {
          OlapHierarchyLevel olapHierarchyLevel = (OlapHierarchyLevel) hierarchyLevels.get( hl );

          xml.append( "      <Level" ); //$NON-NLS-1$
          xml.append( " name=\"" ); //$NON-NLS-1$
          XMLHandler.appendReplacedChars( xml, olapHierarchyLevel.getName() );
          xml.append( "\"" ); //$NON-NLS-1$

          xml.append( " column=\"" ); //$NON-NLS-1$
          XMLHandler.appendReplacedChars( xml, olapHierarchyLevel.getReferenceColumn().getFormula() );
          xml.append( "\"" ); //$NON-NLS-1$

          xml.append( " uniqueMembers=\"" ); //$NON-NLS-1$
          XMLHandler.appendReplacedChars( xml, olapHierarchyLevel.isHavingUniqueMembers() ? "true" : "false" ); //$NON-NLS-1$ //$NON-NLS-2$
          xml.append( "\"" ); //$NON-NLS-1$
          xml.append( ">" ); //$NON-NLS-1$
          xml.append( Const.CR );

          List businessColumns = olapHierarchyLevel.getBusinessColumns();
          for ( int i = 0; i < businessColumns.size(); i++ ) {
            BusinessColumn businessColumn = (BusinessColumn) businessColumns.get( i );
            xml.append( "        <Property" ); //$NON-NLS-1$

            xml.append( " name=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, businessColumn.getDisplayName( locale ) );
            xml.append( "\"" ); //$NON-NLS-1$

            xml.append( " column=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, businessColumn.getFormula() );
            xml.append( "\"" ); //$NON-NLS-1$

            DataTypeSettings dataType = businessColumn.getDataType();
            String typeDesc = null;
            switch ( dataType.getType() ) {
              case DataTypeSettings.DATA_TYPE_STRING:
                typeDesc = "String"; //$NON-NLS-1$
                break;
              case DataTypeSettings.DATA_TYPE_NUMERIC:
                typeDesc = "Numeric"; //$NON-NLS-1$
                break;
              case DataTypeSettings.DATA_TYPE_BOOLEAN:
                typeDesc = "Boolean"; //$NON-NLS-1$
                break;
              case DataTypeSettings.DATA_TYPE_DATE:
                typeDesc = "Date"; //$NON-NLS-1$
                break;
              case DataTypeSettings.DATA_TYPE_TIMESTAMP:
                typeDesc = "Timestamp"; //$NON-NLS-1$
                break;
            }

            if ( typeDesc != null ) {
              xml.append( " type=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, typeDesc );
              xml.append( "\"" ); //$NON-NLS-1$
            }

            xml.append( "/>" ); //$NON-NLS-1$
            xml.append( Const.CR );
          }

          xml.append( "      </Level>" ).append( Const.CR ); //$NON-NLS-1$

        }

        xml.append( "    </Hierarchy>" ).append( Const.CR ); //$NON-NLS-1$
      }

      xml.append( "  </Dimension>" ).append( Const.CR ); //$NON-NLS-1$

    }

    // Now do the cubes too...

    List olapCubes = businessModel.getOlapCubes();
    for ( int c = 0; c < olapCubes.size(); c++ ) {
      OlapCube olapCube = (OlapCube) olapCubes.get( c );

      xml.append( "  <Cube" ); //$NON-NLS-1$

      xml.append( " name=\"" ); //$NON-NLS-1$
      XMLHandler.appendReplacedChars( xml, olapCube.getName() );
      xml.append( "\"" ); //$NON-NLS-1$
      xml.append( ">" ).append( Const.CR ); //$NON-NLS-1$

      xml.append( "    <Table" ); //$NON-NLS-1$

      xml.append( " name=\"" ); //$NON-NLS-1$
      XMLHandler.appendReplacedChars( xml, olapCube.getBusinessTable().getTargetTable() );
      xml.append( "\"" ); //$NON-NLS-1$
      xml.append( "/>" ).append( Const.CR ); //$NON-NLS-1$

      // DIMENSION USAGE
      //
      List usages = olapCube.getOlapDimensionUsages();
      for ( int u = 0; u < usages.size(); u++ ) {
        OlapDimensionUsage usage = (OlapDimensionUsage) usages.get( u );

        xml.append( "    <DimensionUsage" ); //$NON-NLS-1$

        xml.append( " name=\"" ); //$NON-NLS-1$
        XMLHandler.appendReplacedChars( xml, usage.getName() );
        xml.append( "\"" ); //$NON-NLS-1$

        xml.append( " source=\"" ); //$NON-NLS-1$
        XMLHandler.appendReplacedChars( xml, usage.getOlapDimension().getName() );
        xml.append( "\"" ); //$NON-NLS-1$

        // To know the foreign key, look up the relationship between the cube table and the dimension table...
        BusinessTable dimTable = usage.getOlapDimension().findBusinessTable();
        BusinessTable cubeTable = olapCube.getBusinessTable();
        RelationshipMeta relationshipMeta = businessModel.findRelationshipUsing( dimTable, cubeTable );

        if ( !dimTable.equals( cubeTable ) || relationshipMeta != null ) {
          if ( relationshipMeta != null ) {
            BusinessColumn keyColumn;
            if ( relationshipMeta.getTableFrom().equals( dimTable ) ) {
              keyColumn = relationshipMeta.getFieldTo();
            } else {
              keyColumn = relationshipMeta.getFieldFrom();
            }

            xml.append( " foreignKey=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, keyColumn.getFormula() );
            xml.append( "\"" ); //$NON-NLS-1$
          } else {
            throw new Exception(
                Messages
                    .getString(
                        "MondrianModelExporter.ERROR_0001_ERROR_NO_RELATIONSHIP", dimTable.getDisplayName( locale ), cubeTable.toString() ) ); //$NON-NLS-1$
          }
        }
        xml.append( "/>" ).append( Const.CR ); //$NON-NLS-1$
      }

      // MEASURES
      //
      List measures = olapCube.getOlapMeasures();
      for ( int m = 0; m < measures.size(); m++ ) {
        OlapMeasure measure = (OlapMeasure) measures.get( m );
        BusinessColumn businessColumn = measure.getBusinessColumn();

        xml.append( "    <Measure" ); //$NON-NLS-1$

        xml.append( " name=\"" ); //$NON-NLS-1$
        XMLHandler.appendReplacedChars( xml, businessColumn.getDisplayName( locale ) );
        xml.append( "\"" ); //$NON-NLS-1$

        xml.append( " column=\"" ); //$NON-NLS-1$
        XMLHandler.appendReplacedChars( xml, businessColumn.getFormula() );
        xml.append( "\"" ); //$NON-NLS-1$

        AggregationSettings aggregationType = businessColumn.getAggregationType();
        String typeDesc = null;
        switch ( aggregationType.getType() ) {
          case AggregationSettings.TYPE_AGGREGATION_NONE:
            typeDesc = "none"; //$NON-NLS-1$
            break;
          case AggregationSettings.TYPE_AGGREGATION_SUM:
            typeDesc = "sum"; //$NON-NLS-1$
            break;
          case AggregationSettings.TYPE_AGGREGATION_AVERAGE:
            typeDesc = "avg"; //$NON-NLS-1$
            break;
          case AggregationSettings.TYPE_AGGREGATION_COUNT:
            typeDesc = "count"; //$NON-NLS-1$
            break;
          case AggregationSettings.TYPE_AGGREGATION_COUNT_DISTINCT:
            typeDesc = "distinct count"; //$NON-NLS-1$
            break;
          case AggregationSettings.TYPE_AGGREGATION_MINIMUM:
            typeDesc = "min"; //$NON-NLS-1$
            break;
          case AggregationSettings.TYPE_AGGREGATION_MAXIMUM:
            typeDesc = "max"; //$NON-NLS-1$
            break;
        }

        if ( typeDesc != null ) {
          xml.append( " aggregator=\"" ); //$NON-NLS-1$
          XMLHandler.appendReplacedChars( xml, typeDesc );
          xml.append( "\"" ); //$NON-NLS-1$
        }

        String formatString = businessColumn.getMask();
        if ( Const.isEmpty( formatString ) ) {
          formatString = "Standard"; //$NON-NLS-1$
        }

        xml.append( " formatString=\"" ); //$NON-NLS-1$
        XMLHandler.appendReplacedChars( xml, formatString );
        xml.append( "\"" ); //$NON-NLS-1$

        xml.append( "/>" ).append( Const.CR ); //$NON-NLS-1$
      }

      xml.append( "  </Cube>" ).append( Const.CR ); //$NON-NLS-1$
    }

    xml.append( "</Schema>" ); //$NON-NLS-1$

    return xml.toString();
  }

  /**
   * @return the businessModel
   */
  public BusinessModel getBusinessModel() {
    return businessModel;
  }

  /**
   * @param businessModel
   *          the businessModel to set
   */
  public void setBusinessModel( BusinessModel businessModel ) {
    this.businessModel = businessModel;
  }
}
