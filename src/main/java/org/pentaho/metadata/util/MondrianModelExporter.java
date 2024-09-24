/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
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
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.model.olap.OlapAnnotation;
import org.pentaho.metadata.model.olap.OlapCalculatedMember;
import org.pentaho.metadata.model.olap.OlapCube;
import org.pentaho.metadata.model.olap.OlapDimension;
import org.pentaho.metadata.model.olap.OlapDimensionUsage;
import org.pentaho.metadata.model.olap.OlapHierarchy;
import org.pentaho.metadata.model.olap.OlapHierarchyLevel;
import org.pentaho.metadata.model.olap.OlapMeasure;
import org.pentaho.metadata.model.olap.OlapRole;

public class MondrianModelExporter {
  private LogicalModel businessModel;

  private String locale;

  /**
   * @param businessModel
   */
  public MondrianModelExporter( LogicalModel businessModel, String locale ) {
    super();
    this.businessModel = businessModel;
    this.locale = locale;
  }

  public void updateModelToNewDomainName( String catalogName ) {
    if ( getSchemaName().equals( catalogName ) ) {
      return;
    }

    setSchemaName( catalogName );
  }

  private void setSchemaName( String catalogName ) {
    String newName = catalogName;

    if ( businessModel.getProperty( "AGILE_BI_GENERATED_SCHEMA" ) != null ) {
      newName = catalogName.concat( "_OLAP" );
    }

    if ( businessModel.getName() == null ) {
      businessModel.setId( newName );
    } else {
      businessModel.getName().setString( locale, newName );
    }
  }

  public String createMondrianModelXML() throws Exception {
    StringBuilder xml = new StringBuilder( 10000 );

    xml.append( "<Schema " ); //$NON-NLS-1$
    xml.append( "name=\"" ); //$NON-NLS-1$

    String name = getSchemaName();
    XMLHandler.appendReplacedChars( xml, name );
    xml.append( "\">" ); //$NON-NLS-1$
    xml.append( Util.CR );

    @SuppressWarnings( "unchecked" )
    List<OlapDimension> olapDimensions =
        (List<OlapDimension>) businessModel.getProperty( LogicalModel.PROPERTY_OLAP_DIMS ); //$NON-NLS-1$
    if ( olapDimensions != null ) {
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
        xml.append( Util.CR );

        List olapHierarchies = olapDimension.getHierarchies();
        for ( int h = 0; h < olapHierarchies.size(); h++ ) {
          OlapHierarchy olapHierarchy = (OlapHierarchy) olapHierarchies.get( h );
          xml.append( "    <Hierarchy" ); //$NON-NLS-1$

          // don't specify the hierarchy name if it's the same as the dimension name
          if ( StringUtils.isNotEmpty( olapHierarchy.getName() )
              && !StringUtils.equals( olapHierarchy.getName(), olapDimension.getName() ) ) {
            xml.append( " name=\"" ); //$NON-NLS-1$
            xml.append( olapHierarchy.getName() );
            xml.append( "\"" ); //$NON-NLS-1$
          }

          xml.append( " hasAll=\"" ); //$NON-NLS-1$
          xml.append( olapHierarchy.isHavingAll() ? "true" : "false" ); //$NON-NLS-1$ //$NON-NLS-2$
          xml.append( "\"" ); //$NON-NLS-1$

          if ( olapHierarchy.getPrimaryKey() != null ) {
            xml.append( " primaryKey=\"" ); //$NON-NLS-1$
            xml.append( olapHierarchy.getPrimaryKey().getProperty( SqlPhysicalColumn.TARGET_COLUMN ) );
            xml.append( "\"" ); //$NON-NLS-1$
          }
          xml.append( ">" ); //$NON-NLS-1$
          xml.append( Util.CR );

          if ( olapHierarchy.getLogicalTable().getProperty( SqlPhysicalTable.TARGET_TABLE_TYPE ) == TargetTableType.INLINE_SQL ) {
            xml.append( "    <View alias=\"FACT\">" ).append( Util.CR );
            xml.append( "        <SQL dialect=\"generic\">" ).append( Util.CR );
            xml.append(
                "         <![CDATA[" + olapHierarchy.getLogicalTable().getProperty( SqlPhysicalTable.TARGET_TABLE )
                    + "]]>" ).append( Util.CR );
            xml.append( "        </SQL>" ).append( Util.CR );
            xml.append( "    </View>" ).append( Util.CR );
          } else {
            xml.append( "      <Table" ); //$NON-NLS-1$
            xml.append( " name=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, cleanseDbName( (String) olapHierarchy.getLogicalTable().getProperty(
                SqlPhysicalTable.TARGET_TABLE ) ) );
            xml.append( "\"" ); //$NON-NLS-1$
            if ( !StringUtils.isBlank( (String) olapHierarchy.getLogicalTable().getProperty(
                SqlPhysicalTable.TARGET_SCHEMA ) ) ) {
              xml.append( " schema=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, cleanseDbName( (String) olapHierarchy.getLogicalTable()
                  .getProperty( SqlPhysicalTable.TARGET_SCHEMA ) ) );
              xml.append( "\"" ); //$NON-NLS-1$
            }
            xml.append( "/>" ); //$NON-NLS-1$
            xml.append( Util.CR );
          }

          List hierarchyLevels = olapHierarchy.getHierarchyLevels();
          for ( int hl = 0; hl < hierarchyLevels.size(); hl++ ) {
            OlapHierarchyLevel olapHierarchyLevel = (OlapHierarchyLevel) hierarchyLevels.get( hl );

            xml.append( "      <Level" ); //$NON-NLS-1$

            xml.append( " name=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, olapHierarchyLevel.getName() );
            xml.append( "\"" ); //$NON-NLS-1$

            xml.append( " uniqueMembers=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, olapHierarchyLevel.isHavingUniqueMembers() ? "true" : "false" ); //$NON-NLS-1$ //$NON-NLS-2$
            xml.append( "\"" ); //$NON-NLS-1$

            LogicalColumn column;

            column = olapHierarchyLevel.getReferenceColumn();
            xml.append( " column=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, (String) column.getProperty( SqlPhysicalColumn.TARGET_COLUMN ) );
            xml.append( "\"" ); //$NON-NLS-1$

            column = olapHierarchyLevel.getReferenceOrdinalColumn();
            if ( column != null ) {
              xml.append( " ordinalColumn=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, (String) column.getProperty( SqlPhysicalColumn.TARGET_COLUMN ) );
              xml.append( "\"" ); //$NON-NLS-1$
            }

            column = olapHierarchyLevel.getReferenceCaptionColumn();
            if ( column != null ) {
              xml.append( " captionColumn=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, (String) column.getProperty( SqlPhysicalColumn.TARGET_COLUMN ) );
              xml.append( "\"" ); //$NON-NLS-1$
            }

            String levelType = olapHierarchyLevel.getLevelType();
            if ( levelType != null && !levelType.equals( "" ) ) {
              xml.append( " levelType=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, levelType );
              xml.append( "\"" ); //$NON-NLS-1$
            }

            DataType dataTypeLevel = olapHierarchyLevel.getReferenceColumn().getDataType();
            String typeDescLevel = null;
            switch ( dataTypeLevel ) {
              case STRING:
                typeDescLevel = "String"; //$NON-NLS-1$
                break;
              case NUMERIC:
                typeDescLevel = "Numeric"; //$NON-NLS-1$
                break;
              case BOOLEAN:
                typeDescLevel = "Boolean"; //$NON-NLS-1$
                break;
              case DATE:
                typeDescLevel = "Date"; //$NON-NLS-1$
                break;
            }

            if ( typeDescLevel != null ) {
              xml.append( " type=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, typeDescLevel );
              xml.append( "\"" ); //$NON-NLS-1$
            }

            // include only when hidden
            if ( olapHierarchyLevel.isHidden() ) {
              xml.append( " visible=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, !olapHierarchyLevel.isHidden() + "" );
              xml.append( "\"" ); //$NON-NLS-1$
            }

            if ( !StringUtils.isBlank( olapHierarchyLevel.getFormatter() ) ) {
              xml.append( " formatter=\"" );
              XMLHandler.appendReplacedChars( xml, olapHierarchyLevel.getFormatter() );
              xml.append( "\"" );
            }

            xml.append( ">" ); //$NON-NLS-1$
            xml.append( Util.CR );

            if ( olapHierarchyLevel.getAnnotations().size() > 0 ) {
              xml.append( "        <Annotations>" );

              for ( OlapAnnotation annotation : olapHierarchyLevel.getAnnotations() ) {
                xml.append( Util.CR );
                OlapAnnotation escapedAnnotation = escapeAnnotationValue( annotation );
                xml.append( escapedAnnotation.asXml() );
              }
              xml.append( Util.CR );
              xml.append( "        </Annotations>" );
              xml.append( Util.CR );
            }

            List businessColumns = olapHierarchyLevel.getLogicalColumns();
            for ( int i = 0; i < businessColumns.size(); i++ ) {
              LogicalColumn businessColumn = (LogicalColumn) businessColumns.get( i );
              xml.append( "        <Property" ); //$NON-NLS-1$

              xml.append( " name=\"" ); //$NON-NLS-1$

              XMLHandler.appendReplacedChars( xml, businessColumn.getName( locale ) );

              xml.append( "\"" ); //$NON-NLS-1$

              xml.append( " column=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, (String) businessColumn
                  .getProperty( SqlPhysicalColumn.TARGET_COLUMN ) );
              xml.append( "\"" ); //$NON-NLS-1$

              DataType dataType = businessColumn.getDataType();
              String typeDesc = null;
              switch ( dataType ) {
                case STRING:
                  typeDesc = "String"; //$NON-NLS-1$
                  break;
                case NUMERIC:
                  typeDesc = "Numeric"; //$NON-NLS-1$
                  break;
                case BOOLEAN:
                  typeDesc = "Boolean"; //$NON-NLS-1$
                  break;
                case DATE:
                  typeDesc = "Date"; //$NON-NLS-1$
                  break;
              }

              if ( typeDesc != null ) {
                xml.append( " type=\"" ); //$NON-NLS-1$
                XMLHandler.appendReplacedChars( xml, typeDesc );
                xml.append( "\"" ); //$NON-NLS-1$
              }

              if ( businessColumn.getDescription() != null ) {
                xml.append( " description=\"" ); //$NON-NLS-1$
                XMLHandler.appendReplacedChars( xml, businessColumn.getDescription( locale ) );
                xml.append( "\"" ); //$NON-NLS-1$
              }

              xml.append( "/>" ); //$NON-NLS-1$
              xml.append( Util.CR );
            }

            xml.append( "      </Level>" ).append( Util.CR ); //$NON-NLS-1$

          }

          xml.append( "    </Hierarchy>" ).append( Util.CR ); //$NON-NLS-1$
        }

        xml.append( "  </Dimension>" ).append( Util.CR ); //$NON-NLS-1$

      }
    }

    // Now do the cubes too...

    @SuppressWarnings( "unchecked" )
    List<OlapCube> olapCubes = (List<OlapCube>) businessModel.getProperty( LogicalModel.PROPERTY_OLAP_CUBES ); //$NON-NLS-1$
    if ( olapCubes != null ) {
      for ( int c = 0; c < olapCubes.size(); c++ ) {
        OlapCube olapCube = (OlapCube) olapCubes.get( c );

        xml.append( "  <Cube" ); //$NON-NLS-1$

        xml.append( " name=\"" ); //$NON-NLS-1$
        XMLHandler.appendReplacedChars( xml, olapCube.getName() );
        xml.append( "\"" ); //$NON-NLS-1$
        xml.append( ">" ).append( Util.CR ); //$NON-NLS-1$

        if ( olapCube.getLogicalTable().getProperty( SqlPhysicalTable.TARGET_TABLE_TYPE ) == TargetTableType.INLINE_SQL ) {
          xml.append( "    <View alias=\"FACT\">" ).append( Util.CR );
          xml.append( "        <SQL dialect=\"generic\">" ).append( Util.CR );
          xml.append(
              "         <![CDATA[" + olapCube.getLogicalTable().getProperty( SqlPhysicalTable.TARGET_TABLE ) + "]]>" )
              .append( Util.CR );
          xml.append( "        </SQL>" ).append( Util.CR );
          xml.append( "    </View>" ).append( Util.CR );
        } else {
          xml.append( "    <Table" ); //$NON-NLS-1$
          xml.append( " name=\"" ); //$NON-NLS-1$
          XMLHandler.appendReplacedChars( xml, cleanseDbName( (String) olapCube.getLogicalTable().getProperty(
              SqlPhysicalTable.TARGET_TABLE ) ) );
          xml.append( "\"" ); //$NON-NLS-1$
          if ( !StringUtils
              .isBlank( (String) olapCube.getLogicalTable().getProperty( SqlPhysicalTable.TARGET_SCHEMA ) ) ) {
            xml.append( " schema=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, cleanseDbName( (String) olapCube.getLogicalTable().getProperty(
                SqlPhysicalTable.TARGET_SCHEMA ) ) );
            xml.append( "\"" ); //$NON-NLS-1$
          }
          xml.append( "/>" ).append( Util.CR ); //$NON-NLS-1$
        }

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
          LogicalTable dimTable = usage.getOlapDimension().findLogicalTable();
          LogicalTable cubeTable = olapCube.getLogicalTable();
          LogicalRelationship relationshipMeta = businessModel.findRelationshipUsing( dimTable, cubeTable );

          if ( !dimTable.equals( cubeTable ) || relationshipMeta != null ) {
            if ( relationshipMeta != null ) {
              LogicalColumn keyColumn;
              if ( relationshipMeta.getFromTable().equals( dimTable ) ) {
                keyColumn = relationshipMeta.getToColumn();
              } else {
                keyColumn = relationshipMeta.getFromColumn();
              }

              xml.append( " foreignKey=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, (String) keyColumn.getProperty( SqlPhysicalColumn.TARGET_COLUMN ) );
              xml.append( "\"" ); //$NON-NLS-1$
            } else {
              throw new Exception(
                  Messages
                      .getString(
                          "MondrianModelExporter.ERROR_0001_ERROR_NO_RELATIONSHIP", dimTable.getName( locale ), cubeTable.toString() ) ); //$NON-NLS-1$
            }
          }
          xml.append( "/>" ).append( Util.CR ); //$NON-NLS-1$
        }

        // MEASURES
        //
        List measures = olapCube.getOlapMeasures();
        for ( int m = 0; m < measures.size(); m++ ) {
          OlapMeasure measure = (OlapMeasure) measures.get( m );
          LogicalColumn businessColumn = measure.getLogicalColumn();

          xml.append( "    <Measure" ); //$NON-NLS-1$

          xml.append( " name=\"" ); //$NON-NLS-1$
          XMLHandler.appendReplacedChars( xml, businessColumn.getName( locale ) );
          xml.append( "\"" ); //$NON-NLS-1$

          xml.append( " column=\"" ); //$NON-NLS-1$
          XMLHandler.appendReplacedChars( xml, (String) businessColumn.getProperty( SqlPhysicalColumn.TARGET_COLUMN ) );
          xml.append( "\"" ); //$NON-NLS-1$

          String typeDesc = convertToMondrian( businessColumn.getAggregationType() );
          if ( typeDesc != null ) {
            xml.append( " aggregator=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, typeDesc );
            xml.append( "\"" ); //$NON-NLS-1$
          }

          String formatString = (String) businessColumn.getProperty( "mask" ); //$NON-NLS-1$
          if ( !StringUtils.isEmpty( formatString ) ) {
            xml.append( " formatString=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, formatString );
            xml.append( "\"" ); //$NON-NLS-1$
          }

          LocalizedString description = businessColumn.getDescription();
          if ( description != null ) {
            String desc = description.getLocalizedString( locale );
            if ( !StringUtils.isEmpty( desc ) ) {
              xml.append( " description=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, desc );
              xml.append( "\"" ); //$NON-NLS-1$
            }
          }

          // include only when hidden
          if ( measure.isHidden() ) {
            xml.append( " visible=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, !measure.isHidden() + "" );
            xml.append( "\"" ); //$NON-NLS-1$
          }

          xml.append( "/>" ).append( Util.CR ); //$NON-NLS-1$
        }

        // Calculated Members
        //
        if ( olapCube.getOlapCalculatedMembers() != null ) {
          for ( OlapCalculatedMember member : olapCube.getOlapCalculatedMembers() ) {
            xml.append( "    <CalculatedMember" ); //$NON-NLS-1$

            // Calculated member name
            xml.append( " name=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, member.getName() );
            xml.append( "\"" ); //$NON-NLS-1$

            // Dimension
            xml.append( " dimension=\"" ); //$NON-NLS-1$
            XMLHandler.appendReplacedChars( xml, member.getDimension() );
            xml.append( "\"" ); //$NON-NLS-1$

            // Format string
            String formatString = member.getFormatString(); //$NON-NLS-1$
            if ( !StringUtils.isEmpty( formatString ) ) {
              xml.append( " formatString=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, formatString );
              xml.append( "\"" ); //$NON-NLS-1$
            }

            // include only when hidden
            if ( member.isHidden() ) {
              xml.append( " visible=\"" ); //$NON-NLS-1$
              XMLHandler.appendReplacedChars( xml, !member.isHidden() + "" );
              xml.append( "\"" ); //$NON-NLS-1$
            }

            xml.append( ">" ).append( Util.CR ); //$NON-NLS-1$

            xml.append( "<Formula><![CDATA[" ).append( member.getFormula() );
            xml.append( "]]>" ).append( "</Formula>" ).append( Util.CR );

            String solveOrder = member.isCalculateSubtotals() ? "200" : "0";
            xml.append( "<CalculatedMemberProperty name=\"SOLVE_ORDER\" value=\"" )
                .append( solveOrder ).append( "\"/>" ).append( Util.CR );

            xml.append( "</CalculatedMember>" ).append( Util.CR ); //$NON-NLS-1$
          }
        }

        xml.append( "  </Cube>" ).append( Util.CR ); //$NON-NLS-1$
      }
    }

    // Export roles
    @SuppressWarnings( "unchecked" )
    List<OlapRole> roles = (List<OlapRole>) businessModel.getProperty( LogicalModel.PROPERTY_OLAP_ROLES );
    if ( roles != null && roles.size() > 0 ) {
      for ( OlapRole role : roles ) {
        xml.append( "  <Role name=\">" );
        XMLHandler.appendReplacedChars( xml, role.getName() );
        xml.append( "\">" ).append( Util.CR ); //$NON-NLS-1$
        xml.append( role.getDefinition() );
        xml.append( "  </Role>" ).append( Util.CR );
      }
    }

    xml.append( "</Schema>" ); //$NON-NLS-1$

    return xml.toString();
  }

  private String getSchemaName() {
    String name = businessModel.getName( locale );
    if ( businessModel.getProperty( "AGILE_BI_GENERATED_SCHEMA" ) != null ) {
      // clean up the _OLAP suffix on the name
      name = name.replace( "_OLAP", "" );
    }
    return name;
  }

  /**
   * Strip leading and trailing quote characters from the db name.
   */
  private String cleanseDbName( String name ) {
    return name.replaceAll( "^[`'\"]|[`'\"]$", "" );
  }

  private OlapAnnotation escapeAnnotationValue( OlapAnnotation annotation ) {
    StringBuilder escapedValue = new StringBuilder();
    XMLHandler.appendReplacedChars( escapedValue, annotation.getValue() );
    return new OlapAnnotation( annotation.getName(), escapedValue.toString() );
  }

  /**
   * @return the businessModel
   */
  public LogicalModel getLogicalModel() {
    return businessModel;
  }

  /**
   * @param businessModel
   *          the businessModel to set
   */
  public void setLogicalModel( LogicalModel businessModel ) {
    this.businessModel = businessModel;
  }

  public static String convertToMondrian( AggregationType aggregationType ) {
    String typeDesc = null;
    switch ( aggregationType ) {
      case NONE:
        typeDesc = "none"; //$NON-NLS-1$
        break;
      case SUM:
        typeDesc = "sum"; //$NON-NLS-1$
        break;
      case AVERAGE:
        typeDesc = "avg"; //$NON-NLS-1$
        break;
      case COUNT:
        typeDesc = "count"; //$NON-NLS-1$
        break;
      case COUNT_DISTINCT:
        typeDesc = "distinct count"; //$NON-NLS-1$
        break;
      case MINIMUM:
        typeDesc = "min"; //$NON-NLS-1$
        break;
      case MAXIMUM:
        typeDesc = "max"; //$NON-NLS-1$
        break;
    }
    return typeDesc;
  }
}
