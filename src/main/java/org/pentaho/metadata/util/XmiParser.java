// CHECKSTYLE:FileLength:OFF
/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.metadata.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.core.xml.XMLParserFactoryProducer;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.IPhysicalModel;
import org.pentaho.metadata.model.IPhysicalTable;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlDataSource.DataSourceType;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.security.RowLevelSecurity;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.metadata.model.concept.types.Color;
import org.pentaho.metadata.model.concept.types.ColumnWidth.WidthType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.RelationshipType;
import org.pentaho.metadata.model.concept.types.TableType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
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
import org.pentaho.metadata.model.olap.util.OlapUtil;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregationList;
import org.pentaho.pms.schema.concept.types.alignment.AlignmentSettings;
import org.pentaho.pms.schema.concept.types.color.ColorSettings;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.security.RowLevelSecurity.Type;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * This code parses an XMI xml file.
 * <p/>
 * Note: olap support (CWMOLAP:Schema) is not supported at this time.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
@SuppressWarnings( "deprecation" )
public class XmiParser {

  private static final Log logger = LogFactory.getLog( XmiParser.class );

  public String generateXmi( Domain domain ) {
    if ( domain == null ) {
      logger.error( Messages.getErrorString( "XmiParser.ERROR_0001_DOMAIN_NULL" ) ); //$NON-NLS-1$
      return null;
    }

    try {
      StringWriter stringWriter = new StringWriter();
      StreamResult result = new StreamResult();
      result.setWriter( stringWriter );
      TransformerFactory factory = TransformerFactory.newInstance();
      factory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
      factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_DTD, "" );
      factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "" );
      Document doc = toXmiDocument( domain );
      if ( doc != null ) {
        factory.newTransformer().transform( new DOMSource( doc ), result );
        return stringWriter.getBuffer().toString();
      }
    } catch ( Exception e ) {
      logger.error( Messages.getErrorString( "XmiParser.ERROR_0002_TO_XML_FAILED" ), e ); //$NON-NLS-1$
    }
    return null;
  }

  protected static class IdGen {
    int val = 1;

    public String getNextId() {
      return "a" + val++; //$NON-NLS-1$
    }
  }

  public Document toXmiDocument( Domain domain ) {
    if ( domain == null ) {
      logger.error( Messages.getErrorString( "XmiParser.ERROR_0001_DOMAIN_NULL" ) ); //$NON-NLS-1$
      return null;
    }

    Document doc;
    try {
      // create an XML document
      DocumentBuilderFactory dbf = XMLParserFactoryProducer.createSecureDocBuilderFactory();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.newDocument();
      Element xmiElement = doc.createElement( "XMI" ); //$NON-NLS-1$
      xmiElement.setAttribute( "xmlns:CWM", "org.omg.xmi.namespace.CWM" ); //$NON-NLS-1$ //$NON-NLS-2$
      xmiElement.setAttribute( "xmlns:CWMMDB", "org.omg.xmi.namespace.CWMMDB" ); //$NON-NLS-1$ //$NON-NLS-2$
      xmiElement.setAttribute( "xmlns:CWMOLAP", "org.omg.xmi.namespace.CWMOLAP" ); //$NON-NLS-1$ //$NON-NLS-2$
      xmiElement.setAttribute( "xmlns:CWMRDB", "org.omg.xmi.namespace.CWMRDB" ); //$NON-NLS-1$ //$NON-NLS-2$
      xmiElement.setAttribute( "xmlns:CWMTFM", "org.omg.xmi.namespace.CWMTFM" ); //$NON-NLS-1$ //$NON-NLS-2$
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat( "EEE MMM dd HH:mm:ss z yyyy" ); //$NON-NLS-1$
      xmiElement.setAttribute( "timestamp", sdf.format( date ) ); //$NON-NLS-1$
      xmiElement.setAttribute( "xmi.version", "1.2" ); //$NON-NLS-1$ //$NON-NLS-2$
      doc.appendChild( xmiElement );
      Element xmiHeader = doc.createElement( "XMI.header" ); //$NON-NLS-1$
      xmiElement.appendChild( xmiHeader );
      Element xmiDocumentation = doc.createElement( "XMI.documentation" ); //$NON-NLS-1$
      xmiHeader.appendChild( xmiDocumentation );
      addTextElement( doc, xmiDocumentation, "XMI.exporter", "Pentaho XMI Generator" ); //$NON-NLS-1$ //$NON-NLS-2$
      addTextElement( doc, xmiDocumentation, "XMI.exporterVersion", "1.0" ); //$NON-NLS-1$ //$NON-NLS-2$

      Element xmiContent = doc.createElement( "XMI.content" ); //$NON-NLS-1$
      xmiElement.appendChild( xmiContent );

      // first add concepts
      List<Element> allDescriptions = new ArrayList<Element>();

      IdGen idGen = new IdGen();
      for ( Concept concept : domain.getConcepts() ) {
        /*
         * <CWM:Class isAbstract="false" name="Date" xmi.id="a1"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue
         * tag="CONCEPT_PARENT_NAME" value="Base" xmi.id="a2"/> </CWM:ModelElement.taggedValue> </CWM:Class>
         */
        Element cwmClass = doc.createElement( "CWM:Class" ); //$NON-NLS-1$
        cwmClass.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
        cwmClass.setAttribute( "name", concept.getId() ); //$NON-NLS-1$
        String idStr = idGen.getNextId();

        createDescriptions( doc, concept, "CWM:Class", idStr, allDescriptions, idGen ); //$NON-NLS-1$

        cwmClass.setAttribute( "xmi.id", idStr ); //$NON-NLS-1$

        if ( concept.getParentConcept() != null ) {
          Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
          modelElement.appendChild( createTaggedValue( doc,
              "CONCEPT_PARENT_NAME", concept.getParentConcept().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
          cwmClass.appendChild( modelElement );
        }

        xmiContent.appendChild( cwmClass );
      }

      // Description

      Element beforeDesc = null;

      // Event Support

      Element eventModelElement = null;
      for ( String key : domain.getChildProperties().keySet() ) {
        if ( key.startsWith( "LEGACY_EVENT_" ) ) { //$NON-NLS-1$
          // if any keys event, create a model element
          if ( eventModelElement == null ) {
            eventModelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
          }
          String shortkey = key.substring( "LEGACY_EVENT_".length() ); //$NON-NLS-1$
          eventModelElement.appendChild( createTaggedValue( doc, shortkey, (String) domain.getChildProperties().get(
              key ), idGen.getNextId() ) );
        }
      }
      // only add cwm:event if one or more keys exist
      if ( eventModelElement != null ) {
        Element event = doc.createElement( "CWM:Event" ); //$NON-NLS-1$
        event.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
        event.setAttribute( "name", "SECURITY_SERVICE" ); //$NON-NLS-1$ //$NON-NLS-2$
        event.appendChild( eventModelElement );
        xmiContent.appendChild( event );
      }

      // Parameter / Locale info
      int val = 1;
      for ( LocaleType localeType : domain.getLocales() ) {
        Element cwmParameter = doc.createElement( "CWM:Parameter" ); //$NON-NLS-1$
        if ( beforeDesc == null ) {
          beforeDesc = cwmParameter;
        }
        cwmParameter.setAttribute( "name", localeType.getCode() ); //$NON-NLS-1$
        cwmParameter.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
        Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
        modelElement.appendChild( createTaggedValue( doc,
            "LOCALE_IS_DEFAULT", "" + ( ( val == 1 ) ? "Y" : "N" ),
            idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        modelElement.appendChild(
            createTaggedValue( doc, "LOCALE_ORDER", "" + val++, idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$
        modelElement.appendChild( createTaggedValue( doc,
            "LOCALE_DESCRIPTION", localeType.getDescription(), idGen.getNextId() ) ); //$NON-NLS-1$
        cwmParameter.appendChild( modelElement );
        xmiContent.appendChild( cwmParameter );
      }

      // CWMOLAP:Schema elements get converted here
      generateOlapXmi( domain, doc, idGen, xmiContent );

      // CWMRDB:Catalog: Data Source objects
      for ( IPhysicalModel model : domain.getPhysicalModels() ) {
        if ( model.getId().equals( "__MISSING_PARENT_PHYSICAL_MODEL__" ) ) { //$NON-NLS-1$
          continue;
        }

        if ( model instanceof SqlPhysicalModel ) {
          SqlPhysicalModel sqlModel = (SqlPhysicalModel) model;
          SqlDataSource datasource = sqlModel.getDatasource();
          Element catalog = doc.createElement( "CWMRDB:Catalog" ); //$NON-NLS-1$
          catalog.setAttribute( "name", model.getId() ); //$NON-NLS-1$
          catalog.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
          Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$

          modelElement.appendChild( createTaggedValue( doc,
              "DATABASE_TYPE", datasource.getDialectType(), idGen.getNextId() ) ); //$NON-NLS-1$
          modelElement.appendChild( createTaggedValue( doc,
              "DATABASE_ACCESS", datasource.getType().toString(), idGen.getNextId() ) ); //$NON-NLS-1$
          modelElement.appendChild( createTaggedValue( doc,
              "DATABASE_DATABASE", datasource.getDatabaseName(), idGen.getNextId() ) ); //$NON-NLS-1$
          modelElement.appendChild( createTaggedValue( doc,
              "DATABASE_SERVER", datasource.getHostname(), idGen.getNextId() ) ); //$NON-NLS-1$
          modelElement.appendChild(
              createTaggedValue( doc, "DATABASE_PORT", datasource.getPort(), idGen.getNextId() ) ); //$NON-NLS-1$
          modelElement.appendChild( createTaggedValue( doc,
              "DATABASE_USERNAME", datasource.getUsername(), idGen.getNextId() ) ); //$NON-NLS-1$
          modelElement.appendChild( createTaggedValue( doc,
              "DATABASE_PASSWORD", datasource.getPassword(), idGen.getNextId() ) ); //$NON-NLS-1$
          if ( !StringUtils.isEmpty( datasource.getServername() ) ) {
            modelElement.appendChild( createTaggedValue( doc,
                "DATABASE_SERVER_INSTANCE", datasource.getServername(), idGen.getNextId() ) ); //$NON-NLS-1$
          }

          for ( String attribute : datasource.getAttributes().keySet() ) {
            modelElement.appendChild( createTaggedValue( doc, CWM.TAG_DATABASE_ATTRIBUTE_PREFIX + attribute, datasource
                .getAttributes().get( attribute ), idGen.getNextId() ) );
          }

          catalog.appendChild( modelElement );
          xmiContent.appendChild( catalog );
        } else {
          // we do not support CSV to XMI yet
          logger.warn( Messages.getErrorString(
              "XmiParser.ERROR_0003_PHYSICAL_MODEL_NOT_SUPPORTED", model.getClass().getName() ) ); //$NON-NLS-1$
        }
      }

      // CWMRDB:Table: physicalTables

      for ( IPhysicalModel model : domain.getPhysicalModels() ) {
        if ( model instanceof SqlPhysicalModel ) {
          SqlPhysicalModel sqlModel = (SqlPhysicalModel) model;
          for ( SqlPhysicalTable table : sqlModel.getPhysicalTables() ) {
            Element cwmRdbTable = doc.createElement( "CWMRDB:Table" ); //$NON-NLS-1$
            cwmRdbTable.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
            cwmRdbTable.setAttribute( "isSystem", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
            cwmRdbTable.setAttribute( "isTemporary", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
            cwmRdbTable.setAttribute( "name", table.getId() ); //$NON-NLS-1$
            String idstr = idGen.getNextId();
            cwmRdbTable.setAttribute( "xmi.id", idstr ); //$NON-NLS-1$
            createDescriptions( doc, table, "CWMRDB:Table", idstr, allDescriptions, idGen ); //$NON-NLS-1$

            Element modelElement = null;
            if ( !model.getId().equals( "__MISSING_PARENT_PHYSICAL_MODEL__" ) ) { //$NON-NLS-1$
              modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
              modelElement.appendChild( createTaggedValue( doc,
                  "TABLE_TARGET_DATABASE_NAME", model.getId(), idGen.getNextId() ) ); //$NON-NLS-1$
            }
            if ( table.getParentConcept() != null ) {
              if ( modelElement == null ) {
                modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
              }
              modelElement.appendChild( createTaggedValue( doc,
                  "CONCEPT_PARENT_NAME", table.getParentConcept().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
            }
            if ( modelElement != null ) {
              cwmRdbTable.appendChild( modelElement );
            }

            Element ownedElement = doc.createElement( "CWM:Namespace.ownedElement" ); //$NON-NLS-1$
            for ( IPhysicalColumn column : table.getPhysicalColumns() ) {
              SqlPhysicalColumn sqlColumn = (SqlPhysicalColumn) column;
              Element rdbColumn = doc.createElement( "CWMRDB:Column" ); //$NON-NLS-1$
              rdbColumn.setAttribute( "name", sqlColumn.getId() ); //$NON-NLS-1$
              idstr = idGen.getNextId();
              rdbColumn.setAttribute( "xmi.id", idstr ); //$NON-NLS-1$
              createDescriptions( doc, column, "CWMRDB:Column", idstr, allDescriptions, idGen ); //$NON-NLS-1$
              if ( sqlColumn.getParentConcept() != null ) {
                modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
                modelElement.appendChild( createTaggedValue( doc,
                    "CONCEPT_PARENT_NAME", sqlColumn.getParentConcept().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
                rdbColumn.appendChild( modelElement );
              }
              ownedElement.appendChild( rdbColumn );
            }
            cwmRdbTable.appendChild( ownedElement );
            xmiContent.appendChild( cwmRdbTable );
          }
        }
      }

      // CWMMDB:Schema: logical categories

      for ( LogicalModel model : domain.getLogicalModels() ) {
        Element mdbSchema = doc.createElement( "CWMMDB:Schema" ); //$NON-NLS-1$
        mdbSchema.setAttribute( "name", model.getId() ); //$NON-NLS-1$
        String idstr = idGen.getNextId();
        mdbSchema.setAttribute( "xmi.id", idstr ); //$NON-NLS-1$
        createDescriptions( doc, model, "CWMMDB:Schema", idstr, allDescriptions, idGen ); //$NON-NLS-1$

        // Serialize all calculated members across cubes into a single XML string and store as a description
        @SuppressWarnings( "unchecked" )
        List<OlapCube> cubes = (List<OlapCube>) model.getProperty( "olap_cubes" );
        if ( cubes != null ) {
          StringBuffer buffer = new StringBuffer();
          buffer.append( "<cubes>" );
          for ( OlapCube cube : cubes ) {
            if ( cube.getOlapCalculatedMembers() != null && cube.getOlapCalculatedMembers().size() > 0 ) {
              buffer.append( "<cube>" );
              buffer.append( XMLHandler.addTagValue( "name", cube.getName() ) );
              buffer.append( OlapUtil.toXmlCalculatedMembers( cube.getOlapCalculatedMembers() ) );
              buffer.append( "</cube>" );
            }
          }
          buffer.append( "</cubes>" );
          createDescription( doc, buffer.toString(), LogicalModel.PROPERTY_OLAP_CALCULATED_MEMBERS, "String", null,
              idGen, "CWMMDB:Schema", idstr, allDescriptions );
        }

        Element ownedElement = doc.createElement( "CWM:Namespace.ownedElement" ); //$NON-NLS-1$
        mdbSchema.appendChild( ownedElement );
        for ( Category category : model.getCategories() ) {
          Element extent = doc.createElement( "CWM:Extent" ); //$NON-NLS-1$
          extent.setAttribute( "name", category.getId() ); //$NON-NLS-1$
          idstr = idGen.getNextId();
          extent.setAttribute( "xmi.id", idstr ); //$NON-NLS-1$
          createDescriptions( doc, category, "CWM:Extent", idstr, allDescriptions, idGen ); //$NON-NLS-1$
          Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
          modelElement.appendChild(
              createTaggedValue( doc, "BUSINESS_CATEGORY_ROOT", "Y", idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$
          if ( category.getParentConcept() != null ) {
            modelElement.appendChild( createTaggedValue( doc,
                "CONCEPT_PARENT_NAME", category.getParentConcept().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          extent.appendChild( modelElement );
          Element cOwnedElement = doc.createElement( "CWM:Namespace.ownedElement" ); //$NON-NLS-1$
          extent.appendChild( cOwnedElement );
          for ( LogicalColumn col : category.getLogicalColumns() ) {
            Element attribute = doc.createElement( "CWM:Attribute" ); //$NON-NLS-1$
            attribute.setAttribute( "name", col.getId() ); //$NON-NLS-1$
            attribute.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
            modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
            modelElement.appendChild( createTaggedValue( doc, "BUSINESS_CATEGORY_TYPE", "Column",
                idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$
            attribute.appendChild( modelElement );
            cOwnedElement.appendChild( attribute );
          }

          ownedElement.appendChild( extent );
        }

        for ( LogicalRelationship rel : model.getLogicalRelationships() ) {
          Element keyRel = doc.createElement( "CWM:KeyRelationship" ); //$NON-NLS-1$
          keyRel.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
          Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
          keyRel.appendChild( modelElement );
          modelElement.appendChild( createTaggedValue( doc,
              "RELATIONSHIP_TYPE", rel.getRelationshipType().getType(), idGen.getNextId() ) ); //$NON-NLS-1$

          if ( rel.getToColumn() != null ) {
            modelElement.appendChild( createTaggedValue( doc,
                "RELATIONSHIP_FIELDNAME_CHILD", rel.getToColumn().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          if ( rel.getFromColumn() != null ) {
            modelElement.appendChild( createTaggedValue( doc,
                "RELATIONSHIP_FIELDNAME_PARENT", rel.getFromColumn().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          if ( rel.getToTable() != null ) {
            modelElement.appendChild( createTaggedValue( doc,
                "RELATIONSHIP_TABLENAME_CHILD", rel.getToTable().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          if ( rel.getFromTable() != null ) {
            modelElement.appendChild( createTaggedValue( doc,
                "RELATIONSHIP_TABLENAME_PARENT", rel.getFromTable().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          if ( rel.isComplex() ) {
            modelElement.appendChild( createTaggedValue( doc, "RELATIONSHIP_IS_COMPLEX", "Y",
                idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$
            modelElement.appendChild( createTaggedValue( doc,
                "RELATIONSHIP_COMPLEX_JOIN", rel.getComplexJoin(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          if ( rel.getDescription() != null ) {
            modelElement.appendChild( createTaggedValue( doc,
                "RELATIONSHIP_DESCRIPTION", rel.getRelationshipDescription(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          if ( rel.getJoinOrderKey() != null ) {
            modelElement.appendChild( createTaggedValue( doc,
                "RELATIONSHIP_JOIN_ORDER_KEY", rel.getJoinOrderKey(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          ownedElement.appendChild( keyRel );
        }

        Element sdo = doc.createElement( "CWMMDB:Schema.dimensionedObject" ); //$NON-NLS-1$
        Element sd = doc.createElement( "CWMMDB:Schema.dimension" ); //$NON-NLS-1$
        mdbSchema.appendChild( sdo );
        mdbSchema.appendChild( sd );
        for ( LogicalTable table : model.getLogicalTables() ) {
          Element dim = doc.createElement( "CWMMDB:Dimension" ); //$NON-NLS-1$
          sd.appendChild( dim );
          dim.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
          dim.setAttribute( "name", table.getId() ); //$NON-NLS-1$
          String tblidstr = idGen.getNextId();
          dim.setAttribute( "xmi.id", tblidstr ); //$NON-NLS-1$
          createDescriptions( doc, table, "CWMMDB:Dimension", tblidstr, allDescriptions, idGen ); //$NON-NLS-1$
          Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
          if ( table.getProperty( "__LEGACY_TABLE_IS_DRAWN" ) != null ) { //$NON-NLS-1$
            modelElement.appendChild( createTaggedValue( doc,
                "TABLE_IS_DRAWN", (String) table.getProperty( "__LEGACY_TABLE_IS_DRAWN" ),
                idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$
          }
          if ( table.getProperty( "__LEGACY_TAG_POSITION_Y" ) != null ) { //$NON-NLS-1$
            modelElement.appendChild( createTaggedValue( doc,
                "TAG_POSITION_Y", (String) table.getProperty( "__LEGACY_TAG_POSITION_Y" ),
                idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$
          }
          if ( table.getProperty( "__LEGACY_TAG_POSITION_X" ) != null ) { //$NON-NLS-1$
            modelElement.appendChild( createTaggedValue( doc,
                "TAG_POSITION_X", (String) table.getProperty( "__LEGACY_TAG_POSITION_X" ),
                idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$
          }
          if ( table.getParentConcept() != null ) {
            modelElement.appendChild( createTaggedValue( doc,
                "CONCEPT_PARENT_NAME", table.getParentConcept().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
          }
          modelElement.appendChild( createTaggedValue( doc,
              "BUSINESS_TABLE_PHYSICAL_TABLE_NAME", table.getPhysicalTable().getId(),
              idGen.getNextId() ) ); //$NON-NLS-1$
          dim.appendChild( modelElement );
          Element dimObjs = doc.createElement( "CWMMDB:Dimension.dimensionedObject" ); //$NON-NLS-1$
          dim.appendChild( dimObjs );

          for ( LogicalColumn column : table.getLogicalColumns() ) {
            Element dimObj = doc.createElement( "CWMMDB:DimensionedObject" ); //$NON-NLS-1$
            sdo.appendChild( dimObj );
            dimObj.setAttribute( "name", column.getId() ); //$NON-NLS-1$
            idstr = idGen.getNextId();
            createDescriptions( doc, column, "CWMMDB:DimensionedObject", idstr, allDescriptions, idGen ); //$NON-NLS-1$
            dimObj.setAttribute( "xmi.id", idstr ); //$NON-NLS-1$
            modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
            modelElement.appendChild( createTaggedValue( doc,
                "BUSINESS_COLUMN_BUSINESS_TABLE", column.getLogicalTable().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
            modelElement.appendChild( createTaggedValue( doc,
                "BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME", column.getPhysicalColumn().getId(),
                idGen.getNextId() ) ); //$NON-NLS-1$
            if ( column.getParentConcept() != null ) {
              modelElement.appendChild( createTaggedValue( doc,
                  "CONCEPT_PARENT_NAME", column.getParentConcept().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
            }
            dimObj.appendChild( modelElement );
            /*
             * <CWMMDB:DimensionedObject.dimension> <CWMMDB:Dimension xmi.idref="a23"/>
             * </CWMMDB:DimensionedObject.dimension>
             */

            Element parentRoot = doc.createElement( "CWMMDB:DimensionedObject.dimension" ); //$NON-NLS-1$
            Element parent = doc.createElement( "CWMMDB:Dimension" ); //$NON-NLS-1$
            parent.setAttribute( "xmi.idref", tblidstr ); //$NON-NLS-1$
            dimObj.appendChild( parentRoot );
            parentRoot.appendChild( parent );

            // CWMMDB:DimensionedObject xmi.idref="a1365"/>
            Element dimObjLink = doc.createElement( "CWMMDB:DimensionedObject" ); //$NON-NLS-1$
            dimObjLink.setAttribute( "xmi.idref", idstr ); //$NON-NLS-1$
            dimObjs.appendChild( dimObjLink );
          }
        }

        if ( model.getParentConcept() != null ) {
          Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
          modelElement.appendChild( createTaggedValue( doc,
              "CONCEPT_PARENT_NAME", model.getParentConcept().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
          mdbSchema.appendChild( modelElement );
        }

        xmiContent.appendChild( mdbSchema );
      }

      for ( Element element : allDescriptions ) {
        xmiContent.insertBefore( element, beforeDesc );
      }

      return doc;
    } catch ( Exception e ) {
      logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0002_TO_DOCUMENT_FAILED" ), e ); //$NON-NLS-1$
    }
    return null;

  }

  @SuppressWarnings( "unchecked" )
  protected void generateOlapXmi( Domain domain, Document doc, IdGen idGen, Element xmiContent ) {
    for ( LogicalModel model : domain.getLogicalModels() ) {
      List<OlapDimension> dims = (List<OlapDimension>) model.getProperty( "olap_dimensions" ); //$NON-NLS-1$
      List<OlapCube> cubes = (List<OlapCube>) model.getProperty( "olap_cubes" ); //$NON-NLS-1$
      Map<OlapDimension, String> dimMap = new HashMap<OlapDimension, String>();
      Map<OlapDimensionUsage, String> dimUsageIdMap = new HashMap<OlapDimensionUsage, String>();
      Map<OlapDimension, List<OlapDimensionUsage>> dimUsageMap = new HashMap<OlapDimension, List<OlapDimensionUsage>>();

      // if there is at least one dimension or cube...
      if ( ( dims != null && dims.size() > 0 ) || ( cubes != null && cubes.size() > 0 ) ) {
        Element olapSchema = doc.createElement( "CWMOLAP:Schema" ); //$NON-NLS-1$
        olapSchema.setAttribute( "name", model.getId() ); //$NON-NLS-1$
        olapSchema.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
        if ( cubes != null && cubes.size() > 0 ) {
          Element cubesElement = doc.createElement( "CWMOLAP:Schema.cube" ); //$NON-NLS-1$
          for ( OlapCube cube : cubes ) {
            Element cubeElement = doc.createElement( "CWMOLAP:Cube" ); //$NON-NLS-1$
            cubeElement.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
            cubeElement.setAttribute( "name", cube.getName() ); //$NON-NLS-1$
            cubeElement.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$  //$NON-NLS-2$
            cubeElement.setAttribute( "isVirtual", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
            cubesElement.appendChild( cubeElement );

            Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
            modelElement.appendChild( createTaggedValue( doc,
                "CUBE_BUSINESS_TABLE", cube.getLogicalTable().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
            cubeElement.appendChild( modelElement );

            if ( cube.getOlapMeasures() != null && cube.getOlapMeasures().size() > 0 ) {
              Element ownedElement = doc.createElement( "CWM:Namespace.ownedElement" ); //$NON-NLS-1$
              // add measures
              for ( OlapMeasure measure : cube.getOlapMeasures() ) {
                Element measureElement = doc.createElement( "CWMOLAP:Measure" ); //$NON-NLS-1$
                measureElement.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
                measureElement.setAttribute( "name", measure.getName() ); //$NON-NLS-1$

                Element measModelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
                measModelElement.appendChild( createTaggedValue( doc,
                    "MEASURE_BUSINESS_COLUMN", measure.getLogicalColumn().getId(), idGen.getNextId() ) ); //$NON-NLS-1$

                if ( measure.isHidden() ) {
                  measModelElement.appendChild(
                      createTaggedValue( doc, OlapMeasure.MEASURE_HIDDEN, measure.isHidden() + "",
                          idGen.getNextId() ) );
                }

                measureElement.appendChild( measModelElement );
                ownedElement.appendChild( measureElement );
              }
              cubeElement.appendChild( ownedElement );
            }

            if ( cube.getOlapDimensionUsages() != null && cube.getOlapDimensionUsages().size() > 0 ) {
              Element cubeDimAssoc = doc.createElement( "CWMOLAP:Cube.cubeDimensionAssociation" ); //$NON-NLS-1$

              for ( OlapDimensionUsage usage : cube.getOlapDimensionUsages() ) {
                Element assoc = doc.createElement( "CWMOLAP:CubeDimensionAssociation" ); //$NON-NLS-1$
                assoc.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$  //$NON-NLS-2$
                String usageId = idGen.getNextId();
                dimUsageIdMap.put( usage, usageId );
                List<OlapDimensionUsage> list = dimUsageMap.get( usage.getOlapDimension() );
                if ( list == null ) {
                  list = new ArrayList<OlapDimensionUsage>();
                  dimUsageMap.put( usage.getOlapDimension(), list );
                }
                list.add( usage );

                assoc.setAttribute( "xmi.id", usageId ); //$NON-NLS-1$
                assoc.setAttribute( "name", usage.getName() ); //$NON-NLS-1$
                // generate dimension now

                Element cda = doc.createElement( "CWMOLAP:CubeDimensionAssociation.dimension" ); //$NON-NLS-1$
                Element dim = doc.createElement( "CWMOLAP:Dimension" ); //$NON-NLS-1$
                String id = idGen.getNextId();
                dimMap.put( usage.getOlapDimension(), id );
                dim.setAttribute( "xmi.idref", id ); //$NON-NLS-1$
                cda.appendChild( dim );
                assoc.appendChild( cda );
                cubeDimAssoc.appendChild( assoc );
              }
              cubeElement.appendChild( cubeDimAssoc );
            }
          }
          olapSchema.appendChild( cubesElement );
        }

        if ( dims != null && dims.size() > 0 ) {
          Element dimsElement = doc.createElement( "CWMOLAP:Schema.dimension" ); //$NON-NLS-1$
          for ( OlapDimension dim : dims ) {
            Element dimElement = doc.createElement( "CWMOLAP:Dimension" ); //$NON-NLS-1$
            String id = dimMap.get( dim );
            if ( id == null ) {
              id = idGen.getNextId();
            }
            dimElement.setAttribute( "xmi.id", id ); //$NON-NLS-1$
            dimElement.setAttribute( "name", dim.getName() ); //$NON-NLS-1$
            dimElement.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$  //$NON-NLS-2$
            dimElement.setAttribute( "isTime", "" + dim.isTimeDimension() ); //$NON-NLS-1$  //$NON-NLS-2$
            dimElement.setAttribute( "isMeasure", "false" ); //$NON-NLS-1$  //$NON-NLS-2$

            List<OlapDimensionUsage> list = dimUsageMap.get( dim );
            if ( list != null && list.size() > 0 ) {
              Element cubeDimAssoc = doc.createElement( "CWMOLAP:Dimension.cubeDimensionAssociation" ); //$NON-NLS-1$
              for ( OlapDimensionUsage usage : list ) {
                Element cda = doc.createElement( "CWMOLAP:CubeDimensionAssociation" ); //$NON-NLS-1$
                cda.setAttribute( "xmi.idref", dimUsageIdMap.get( usage ) ); //$NON-NLS-1$
                cubeDimAssoc.appendChild( cda );
              }
              dimElement.appendChild( cubeDimAssoc );
            }

            if ( dim.getHierarchies() != null && dim.getHierarchies().size() > 0 ) {
              Element hierElement = doc.createElement( "CWMOLAP:Dimension.hierarchy" ); //$NON-NLS-1$
              Element memberSelElement = null;
              for ( OlapHierarchy hier : dim.getHierarchies() ) {
                Element hierarchyElement = doc.createElement( "CWMOLAP:LevelBasedHierarchy" ); //$NON-NLS-1$
                String hierId = idGen.getNextId();
                hierarchyElement.setAttribute( "xmi.id", hierId ); //$NON-NLS-1$
                hierarchyElement.setAttribute( "name", hier.getName() ); //$NON-NLS-1$
                hierarchyElement.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$  //$NON-NLS-2$

                Element modelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
                modelElement.appendChild( createTaggedValue( doc,
                    "HIERARCHY_BUSINESS_TABLE", hier.getLogicalTable().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
                if ( hier.getPrimaryKey() != null ) {
                  modelElement.appendChild( createTaggedValue( doc,
                      "HIERARCHY_PRIMARY_KEY", hier.getPrimaryKey().getId(), idGen.getNextId() ) ); //$NON-NLS-1$
                }
                modelElement.appendChild( createTaggedValue( doc,
                    "HIERARCHY_HAVING_ALL", hier.isHavingAll() ? "Y" : "N",
                    idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                hierarchyElement.appendChild( modelElement );

                if ( hier.getHierarchyLevels() != null && hier.getHierarchyLevels().size() > 0 ) {
                  Element
                      hla =
                      doc.createElement( "CWMOLAP:LevelBasedHierarchy.hierarchyLevelAssociation" ); //$NON-NLS-1$
                  for ( OlapHierarchyLevel level : hier.getHierarchyLevels() ) {

                    Element hierLvlAssoc = doc.createElement( "CWMOLAP:HierarchyLevelAssociation" ); //$NON-NLS-1$
                    String hlaId = idGen.getNextId();
                    hierLvlAssoc.setAttribute( "xmi.id", hlaId ); //$NON-NLS-1$
                    hierLvlAssoc.setAttribute( "name", level.getName() ); //$NON-NLS-1$
                    hierLvlAssoc.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$  //$NON-NLS-2$
                    Element
                        currLvl =
                        doc.createElement( "CWMOLAP:HierarchyLevelAssociation.currentLevel" ); //$NON-NLS-1$
                    Element lvlref = doc.createElement( "CWMOLAP:Level" ); //$NON-NLS-1$
                    String lvlId = idGen.getNextId();

                    if ( memberSelElement == null ) {
                      memberSelElement = doc.createElement( "CWMOLAP:Dimension.memberSelection" ); //$NON-NLS-1$
                    }

                    Element lvlElement = doc.createElement( "CWMOLAP:Level" ); //$NON-NLS-1$
                    lvlElement.setAttribute( "xmi.id", lvlId ); //$NON-NLS-1$
                    lvlElement.setAttribute( "name", level.getName() ); //$NON-NLS-1$
                    lvlElement.setAttribute( "isAbstract", "false" ); //$NON-NLS-1$  //$NON-NLS-2$

                    Element lvlModelElement = doc.createElement( "CWM:ModelElement.taggedValue" ); //$NON-NLS-1$
                    lvlModelElement
                        .appendChild( createTaggedValue(
                            doc,
                            "HIERARCHY_LEVEL_UNIQUE_MEMBERS", level.isHavingUniqueMembers() ? "Y" : "N",
                            idGen.getNextId() ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                    LogicalColumn logicalColumn;
                    String tag;

                    logicalColumn = level.getReferenceColumn();
                    if ( logicalColumn != null ) {
                      tag = "HIERARCHY_LEVEL_REFERENCE_COLUMN"; //$NON-NLS-1$
                      lvlModelElement.appendChild( createTaggedValue( doc, tag, logicalColumn.getId(), idGen
                          .getNextId() ) );
                    }

                    logicalColumn = level.getReferenceOrdinalColumn();
                    if ( logicalColumn != null ) {
                      tag = "HIERARCHY_LEVEL_REFERENCE_ORDINAL_COLUMN"; //$NON-NLS-1$
                      lvlModelElement.appendChild( createTaggedValue( doc, tag, logicalColumn.getId(), idGen
                          .getNextId() ) );
                    }

                    logicalColumn = level.getReferenceCaptionColumn();
                    if ( logicalColumn != null ) {
                      tag = "HIERARCHY_LEVEL_REFERENCE_CAPTION_COLUMN"; //$NON-NLS-1$
                      lvlModelElement.appendChild( createTaggedValue( doc, tag, logicalColumn.getId(), idGen
                          .getNextId() ) );
                    }

                    if ( dim.isTimeDimension() ) {
                      lvlModelElement.appendChild( createTaggedValue( doc, "HIERARCHY_LEVEL_TYPE",
                          level.getLevelType(), idGen.getNextId() ) );
                    }

                    if ( level.isHidden() ) {
                      lvlModelElement.appendChild(
                          createTaggedValue( doc, OlapHierarchyLevel.HIERARCHY_LEVEL_HIDDEN, level.isHidden() + "",
                              idGen.getNextId() ) );
                    }

                    if ( !StringUtils.isBlank( level.getFormatter() ) ) {
                      lvlModelElement.appendChild(
                        createTaggedValue( doc, OlapHierarchyLevel.HIERARCHY_LEVEL_FORMATTER, level.getFormatter(),
                          idGen.getNextId() ) );
                    }

                    // add annotations as tagged values
                    if ( level.getAnnotations() != null & level.getAnnotations().size() > 0 ) {
                      for ( OlapAnnotation annotation : level.getAnnotations() ) {
                        Element annotationElement =
                            createTaggedValue( doc, "ANNOTATION_" + annotation.getName(), annotation.getValue(), idGen
                                .getNextId() ); // $NON-NLS1$
                        lvlModelElement.appendChild( annotationElement );
                      }
                    }

                    lvlElement.appendChild( lvlModelElement );

                    if ( level.getLogicalColumns() != null && level.getLogicalColumns().size() > 0 ) {
                      Element ownedElement = doc.createElement( "CWM:Namespace.ownedElement" ); //$NON-NLS-1$
                      for ( LogicalColumn col : level.getLogicalColumns() ) {
                        Element dimObj = doc.createElement( "CWMMDB:DimensionedObject" ); //$NON-NLS-1$
                        dimObj.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
                        dimObj.setAttribute( "name", col.getId() ); //$NON-NLS-1$
                        ownedElement.appendChild( dimObj );
                      }
                      lvlElement.appendChild( ownedElement );
                    }

                    // add any annotations
                    // if(level.getAnnotations() != null & level.getAnnotations().size() > 0) {
                    // Element annotationsElement = doc.createElement("CWMOLAP:Level.annotation");
                    // for(OlapAnnotation annotation : level.getAnnotations()) {
                    // Element annotationElement = doc.createElement("CWMOLAP:Annotation");
                    // annotationElement.setAttribute("name", annotation.getName());
                    // annotationElement.setAttribute("value", annotation.getValue());
                    // annotationsElement.appendChild(annotationElement);
                    // }
                    // lvlElement.appendChild(annotationsElement);
                    // }

                    Element
                        lvlHierLvlAssoc =
                        doc.createElement( "CWMOLAP:Level.hierarchyLevelAssociation" ); //$NON-NLS-1$
                    Element lhla = doc.createElement( "CWMOLAP:HierarchyLevelAssociation" ); //$NON-NLS-1$
                    lhla.setAttribute( "xmi.idref", hlaId ); //$NON-NLS-1$
                    lvlHierLvlAssoc.appendChild( lhla );
                    lvlElement.appendChild( lvlHierLvlAssoc );
                    memberSelElement.appendChild( lvlElement );

                    lvlref.setAttribute( "xmi.idref", lvlId ); //$NON-NLS-1$
                    currLvl.appendChild( lvlref );
                    hierLvlAssoc.appendChild( currLvl );
                    hla.appendChild( hierLvlAssoc );
                  }
                  hierarchyElement.appendChild( hla );
                }
                hierElement.appendChild( hierarchyElement );
              }
              dimElement.appendChild( hierElement );
              if ( memberSelElement != null ) {
                dimElement.appendChild( memberSelElement );
              }
            }
            dimsElement.appendChild( dimElement );
          }
          olapSchema.appendChild( dimsElement );
        }
        xmiContent.appendChild( olapSchema );
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  protected void createDescriptions( Document doc, IConcept concept, String parentTag, String idstr,
      List<Element> allDescriptions, IdGen idGen ) {
    for ( String key : concept.getChildProperties().keySet() ) {

      String body = null;
      String type = null;

      Object val = concept.getChildProperty( key );
      /*
       * <CWM:Description body="POSTALCODE" name="formula" type="String" xmi.id="a927"> <CWM:Description.modelElement>
       * <CWMRDB:Column xmi.idref="a922"/> </CWM:Description.modelElement> </CWM:Description>
       */
      if ( val instanceof String ) {
        if ( key.equals( SqlPhysicalColumn.TARGET_COLUMN ) ) {
          key = "formula"; //$NON-NLS-1$
        }
        String str = (String) val;
        body = str;
        type = "String"; //$NON-NLS-1$
      } else if ( val instanceof Boolean ) {
        Boolean bool = (Boolean) val;
        type = "Boolean"; //$NON-NLS-1$
        body = bool.booleanValue() ? "Y" : "N"; //$NON-NLS-1$ //$NON-NLS-2$
      } else if ( val instanceof Color ) {
        Color c = (Color) val;
        ColorSettings cs = new ColorSettings( c.getRed(), c.getGreen(), c.getBlue() );
        body = cs.toString();
        type = "Color"; //$NON-NLS-1$
      } else if ( val instanceof URL ) {
        body = val.toString();
        type = "URL"; //$NON-NLS-1$
      } else if ( val instanceof org.pentaho.metadata.model.concept.types.ColumnWidth ) {
        org.pentaho.metadata.model.concept.types.ColumnWidth ncw =
            (org.pentaho.metadata.model.concept.types.ColumnWidth) val;
        type = "ColumnWidth"; //$NON-NLS-1$
        ColumnWidth cw = new ColumnWidth( ncw.getType().ordinal(), ncw.getWidth() );
        body = cw.toString();
      } else if ( val instanceof Double ) {
        type = "Number"; //$NON-NLS-1$
        BigDecimal bd = new BigDecimal( (Double) val );
        body = bd.toString();
      } else if ( val instanceof Alignment ) {
        Alignment alignment = (Alignment) val;
        AlignmentSettings as = AlignmentSettings.types[alignment.ordinal()];
        body = as.toString();
        type = "Alignment"; //$NON-NLS-1$
      } else if ( val instanceof org.pentaho.metadata.model.concept.security.Security ) {
        org.pentaho.metadata.model.concept.security.Security security =
            (org.pentaho.metadata.model.concept.security.Security) val;
        Map<org.pentaho.pms.schema.security.SecurityOwner, Integer> map =
            new HashMap<org.pentaho.pms.schema.security.SecurityOwner, Integer>();
        for ( SecurityOwner owner : security.getOwners() ) {
          org.pentaho.pms.schema.security.SecurityOwner ownerObj =
              new org.pentaho.pms.schema.security.SecurityOwner( owner.getOwnerType().ordinal(), owner.getOwnerName() );
          map.put( ownerObj, security.getOwnerRights( owner ) );
        }
        Security legacySecurity = new Security( map );
        body = legacySecurity.toXML();
        type = "Security"; //$NON-NLS-1$
      } else if ( val instanceof RowLevelSecurity ) {
        RowLevelSecurity nrls = (RowLevelSecurity) val;
        org.pentaho.pms.schema.security.RowLevelSecurity rls = new org.pentaho.pms.schema.security.RowLevelSecurity();
        rls.setType( Type.values()[nrls.getType().ordinal()] );
        rls.setGlobalConstraint( nrls.getGlobalConstraint() );
        Map<org.pentaho.pms.schema.security.SecurityOwner, String> roleBasedConstraintMap =
            new HashMap<org.pentaho.pms.schema.security.SecurityOwner, String>();
        for ( SecurityOwner owner : nrls.getRoleBasedConstraintMap().keySet() ) {
          org.pentaho.pms.schema.security.SecurityOwner ownerObj =
              new org.pentaho.pms.schema.security.SecurityOwner( owner.getOwnerType().ordinal(), owner.getOwnerName() );
          roleBasedConstraintMap.put( ownerObj, nrls.getRoleBasedConstraintMap().get( owner ) );
        }
        rls.setRoleBasedConstraintMap( roleBasedConstraintMap );

        body = rls.toXML();
        type = "RowLevelSecurity"; //$NON-NLS-1$
      } else if ( val instanceof Font ) {
        ConceptPropertyFont
            font =
            (ConceptPropertyFont) ThinModelConverter.convertPropertyToLegacy( "font", val ); //$NON-NLS-1$
        body = ( (FontSettings) font.getValue() ).toString();
        type = "Font"; //$NON-NLS-1$
      } else if ( val instanceof TargetTableType ) {
        TargetTableType ttt = (TargetTableType) val;
        if ( !( ttt == TargetTableType.TABLE ) ) {
          type = "TargetTableType"; //$NON-NLS-1$
          body = ttt.toString();
        }
      } else if ( val instanceof TableType ) {
        TableType tt = (TableType) val;
        body = TableTypeSettings.getTypeDescriptions()[tt.ordinal()];
        type = "TableType"; //$NON-NLS-1$
      } else if ( val instanceof LocalizedString ) {
        // need to add description for each locale
        LocalizedString lstr = (LocalizedString) val;
        for ( String locale : lstr.getLocales() ) {
          createDescription( doc, lstr.getLocalizedString( locale ), key,
              "LocString", locale, idGen, parentTag, idstr, allDescriptions ); //$NON-NLS-1$
        }
      } else if ( val instanceof TargetColumnType ) {
        TargetColumnType tct = (TargetColumnType) val;
        body = tct == TargetColumnType.OPEN_FORMULA ? "Y" : "N"; //$NON-NLS-1$ //$NON-NLS-2$
        key = "exact"; //$NON-NLS-1$
        type = "Boolean"; //$NON-NLS-1$
      } else if ( val instanceof FieldType ) {
        FieldType ft = (FieldType) val;
        // concept.setProperty(name, FieldType.values()[FieldTypeSettings.getType(body).getType()]);
        body = FieldTypeSettings.getTypeDescriptions()[ft.ordinal()];
        type = "FieldType"; //$NON-NLS-1$
      } else if ( val instanceof DataType ) {
        body = DataTypeSettings.types[( (DataType) val ).ordinal()].getCode();
        type = "DataType"; //$NON-NLS-1$
      } else if ( val instanceof AggregationType ) {
        AggregationType at = (AggregationType) val;
        body = AggregationSettings.types[at.ordinal()].getCode();
        type = "Aggregation"; //$NON-NLS-1$
      } else if ( val instanceof List ) {
        List objs = (List) val;
        if ( objs.size() == 0 && "aggregation_list".equals( key ) ) {
          // assume this is an agg list
          ConceptPropertyAggregationList list =
              new ConceptPropertyAggregationList( key, new ArrayList<AggregationSettings>() );
          type = "AggregationList"; //$NON-NLS-1$
          body = list.toXML();

        } else {
          if ( objs.get( 0 ) instanceof AggregationType ) {
            List<AggregationType> aggTypes = (List<AggregationType>) objs;
            type = "AggregationList"; //$NON-NLS-1$
            List<AggregationSettings> aggSettings = new ArrayList<AggregationSettings>();
            for ( AggregationType aggType : aggTypes ) {
              aggSettings.add( AggregationSettings.types[aggType.ordinal()] );
            }
            ConceptPropertyAggregationList list = new ConceptPropertyAggregationList( key, aggSettings );
            type = "AggregationList"; //$NON-NLS-1$
            body = list.toXML();
          } else if ( objs.get( 0 ) instanceof OlapRole ) {
            body = OlapUtil.toXmlRoles( (List<OlapRole>) objs );
            type = "String";
          } else if ( !( objs.get( 0 ) instanceof OlapCube || objs.get( 0 ) instanceof OlapDimension ) ) {
            logger.error( Messages.getErrorString(
              "XmiParser.ERROR_0004_UNSUPPORTED_CONCEPT_PROPERTY_LIST", objs.get( 0 ).getClass() ) ); //$NON-NLS-1$
          }
        }
      } else {
        if ( val == null ) {
          logger.error( Messages.getErrorString( "XmiParser.ERROR_0005_UNSUPPORTED_CONCEPT_PROPERTY",
              "null" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
          logger.error( Messages
              .getErrorString( "XmiParser.ERROR_0005_UNSUPPORTED_CONCEPT_PROPERTY", val.getClass() ) ); //$NON-NLS-1$
        }
      }
      if ( type != null ) {
        createDescription( doc, body, key, type, null, idGen, parentTag, idstr, allDescriptions );
      }
    }
  }

  protected void createDescription( Document doc, String body, String key, String type, String locale, IdGen idGen,
      String parentTag, String idstr, List<Element> allDescriptions ) {
    Element desc = doc.createElement( "CWM:Description" ); //$NON-NLS-1$
    desc.setAttribute( "body", body ); //$NON-NLS-1$
    if ( locale != null ) {
      desc.setAttribute( "language", locale ); //$NON-NLS-1$
    }
    desc.setAttribute( "name", key ); //$NON-NLS-1$
    desc.setAttribute( "type", type ); //$NON-NLS-1$
    desc.setAttribute( "xmi.id", idGen.getNextId() ); //$NON-NLS-1$
    Element modelElement = doc.createElement( "CWM:Description.modelElement" ); //$NON-NLS-1$
    Element parent = doc.createElement( parentTag );
    modelElement.appendChild( parent );
    parent.setAttribute( "xmi.idref", idstr ); //$NON-NLS-1$
    desc.appendChild( modelElement );
    allDescriptions.add( desc );
  }

  protected Element createTaggedValue( Document doc, String tagName, String value, String id ) {
    Element taggedValue = doc.createElement( "CWM:TaggedValue" ); //$NON-NLS-1$
    taggedValue.setAttribute( "tag", tagName ); //$NON-NLS-1$
    taggedValue.setAttribute( "value", value ); //$NON-NLS-1$
    taggedValue.setAttribute( "xmi.id", id ); //$NON-NLS-1$
    return taggedValue;
  }

  protected void addTextElement( Document doc, Element element, String elementName, String text ) {
    Element childElement = doc.createElement( elementName );
    childElement.appendChild( doc.createTextNode( text ) );
    element.appendChild( childElement );
  }

  /**
   * @param xmi
   * @return
   * @throws PentahoMetadataException
   */
  public Domain parseXmi( InputStream xmi ) throws Exception {
    Document doc;

    // Check and open XML document
    try {
      DocumentBuilderFactory dbf = XMLParserFactoryProducer.createSecureDocBuilderFactory();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.parse( new InputSource( xmi ) );
    } catch ( ParserConfigurationException pcx ) {
      throw new PentahoMetadataException( pcx );
    } catch ( SAXException sax ) {
      throw new PentahoMetadataException( sax );
    } catch ( IOException iex ) {
      throw new PentahoMetadataException( iex );
    }
    Element content = null;
    NodeList list = doc.getElementsByTagName( "XMI.content" ); //$NON-NLS-1$
    if ( ( list != null ) && ( list.getLength() > 0 ) ) {
      content = (Element) list.item( 0 );
    }

    // skipping CWM:Event = Security Service (skip for now)

    List<Element> concepts = new ArrayList<Element>();
    List<Element> descriptions = new ArrayList<Element>();
    List<Element> datasources = new ArrayList<Element>();
    List<Element> physicalTables = new ArrayList<Element>();
    List<Element> parameters = new ArrayList<Element>();
    List<Element> schemas = new ArrayList<Element>();
    List<Element> events = new ArrayList<Element>();
    List<Element> olapSchemas = new ArrayList<Element>();
    list = content.getChildNodes();
    for ( int i = 0; i < list.getLength(); i++ ) {
      Node node = list.item( i );
      if ( node.getNodeType() == Node.ELEMENT_NODE ) {
        if ( node.getNodeName().equals( "CWM:Class" ) ) { //$NON-NLS-1$
          concepts.add( (Element) node );
        } else if ( node.getNodeName().equals( "CWM:Parameter" ) ) { //$NON-NLS-1$
          parameters.add( (Element) node );
        } else if ( node.getNodeName().equals( "CWMRDB:Catalog" ) ) { //$NON-NLS-1$
          datasources.add( (Element) node );
        } else if ( node.getNodeName().equals( "CWMRDB:Table" ) ) { //$NON-NLS-1$
          physicalTables.add( (Element) node );
        } else if ( node.getNodeName().equals( "CWMMDB:Schema" ) ) { //$NON-NLS-1$
          schemas.add( (Element) node );
        } else if ( node.getNodeName().equals( "CWM:Description" ) ) { //$NON-NLS-1$
          descriptions.add( (Element) node );
        } else if ( node.getNodeName().equals( "CWM:Event" ) ) { //$NON-NLS-1$
          events.add( (Element) node );
        } else if ( node.getNodeName().equals( "CWMOLAP:Schema" ) ) { //$NON-NLS-1$
          olapSchemas.add( (Element) node );
        } else {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "Ignoring root : " + node.getNodeName() ); //$NON-NLS-1$
          }
        }
      }
    }

    Domain domain = new Domain();
    Map<String, Concept> xmiConceptMap = new HashMap<String, Concept>();

    for ( Element event : events ) {
      Map<String, String>
          kvp =
          getKeyValuePairs( event, "CWM:TaggedValue", "tag", "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      for ( String key : kvp.keySet() ) {
        domain.setProperty( "LEGACY_EVENT_" + key, kvp.get( key ) ); //$NON-NLS-1$
      }
    }
    populateLocales( domain, parameters );

    for ( Element concept : concepts ) {
      /*
       * <CWM:Class isAbstract="false" name="Date" xmi.id="a1"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue
       * tag="CONCEPT_PARENT_NAME" value="Base" xmi.id="a2"/> </CWM:ModelElement.taggedValue> </CWM:Class>
       */
      Concept c = new Concept();
      String name = concept.getAttribute( "name" ); //$NON-NLS-1$
      c.setId( name );
      String xmiId = concept.getAttribute( "xmi.id" ); //$NON-NLS-1$
      String
          parentName =
          getKeyValue( concept, "CWM:TaggedValue", "tag", "value",
              "CONCEPT_PARENT_NAME" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      if ( parentName != null ) {
        c.setProperty( "__TMP_CONCEPT_PARENT_NAME", parentName ); //$NON-NLS-1$
      }
      xmiConceptMap.put( xmiId, c );
      domain.addConcept( c );
    }

    // second pass to bind parents to children
    for ( Concept concept : domain.getConcepts() ) {
      String parentName = (String) concept.getChildProperty( "__TMP_CONCEPT_PARENT_NAME" ); //$NON-NLS-1$
      if ( parentName != null ) {
        concept.removeChildProperty( "__TMP_CONCEPT_PARENT_NAME" ); //$NON-NLS-1$
        Concept conceptParent = domain.findConcept( parentName );
        concept.setParentConcept( conceptParent );
        conceptParent.addChild( concept );

      }

    }

    for ( Element datasource : datasources ) {
      /*
       * <CWMRDB:Catalog name="SampleData" xmi.id="a1165"> <CWM:ModelElement.taggedValue>
       * 
       * <CWM:TaggedValue tag="DATABASE_INDEX_TABLESPACE" value="" xmi.id="a1173"/> <CWM:TaggedValue
       * tag="DATABASE_DATA_TABLESPACE" value="" xmi.id="a1174"/> <CWM:TaggedValue tag="DATABASE_SERVERNAME" value=""
       * xmi.id="a1175"/> <CWM:TaggedValue tag="DATABASE_PASSWORD" value="" xmi.id="a1176"/> <CWM:TaggedValue
       * tag="DATABASE_USERNAME" value="" xmi.id="a1177"/> <CWM:TaggedValue tag="DATABASE_PORT" value=""
       * xmi.id="a1178"/> <CWM:TaggedValue tag="DATABASE_DATABASE" value="SampleData" xmi.id="a1179"/> <CWM:TaggedValue
       * tag="DATABASE_ACCESS" value="JNDI" xmi.id="a1180"/> <CWM:TaggedValue tag="DATABASE_TYPE" value="HYPERSONIC"
       * xmi.id="a1181"/> <CWM:TaggedValue tag="DATABASE_SERVER" value="localhost" xmi.id="a1182"/>
       * </CWM:ModelElement.taggedValue> </CWMRDB:Catalog>
       * 
       * 
       * <CWM:TaggedValue xmi.id = 'a700' tag = 'DATABASE_SERVER' value = 'localhost'/> <CWM:TaggedValue xmi.id = 'a701'
       * tag = 'DATABASE_TYPE' value = 'MYSQL'/> <CWM:TaggedValue xmi.id = 'a702' tag = 'DATABASE_ACCESS' value =
       * 'Native'/> <CWM:TaggedValue xmi.id = 'a703' tag = 'DATABASE_DATABASE' value = 'foodmart'/> <CWM:TaggedValue
       * xmi.id = 'a704' tag = 'DATABASE_PORT' value = '3306'/> <CWM:TaggedValue xmi.id = 'a705' tag =
       * 'DATABASE_USERNAME' value = 'foodmart'/> <CWM:TaggedValue xmi.id = 'a706' tag = 'DATABASE_PASSWORD' value =
       * 'foodmart'/> <CWM:TaggedValue xmi.id = 'a707' tag = 'DATABASE_SERVERNAME'/> <CWM:TaggedValue xmi.id = 'a708'
       * tag = 'DATABASE_DATA_TABLESPACE'/> <CWM:TaggedValue xmi.id = 'a709' tag = 'DATABASE_INDEX_TABLESPACE'/>
       * 
       * 
       * <CWM:TaggedValue xmi.id = 'a710' tag = 'DATABASE_ATTRIBUTE_PREFIX_EXTRA_OPTION_MYSQL.useCursorFetch' value =
       * 'true'/> <CWM:TaggedValue xmi.id = 'a711' tag = 'DATABASE_ATTRIBUTE_PREFIX_USE_POOLING' value = 'N'/>
       * <CWM:TaggedValue xmi.id = 'a712' tag = 'DATABASE_ATTRIBUTE_PREFIX_IS_CLUSTERED' value = 'N'/> <CWM:TaggedValue
       * xmi.id = 'a713' tag = 'DATABASE_ATTRIBUTE_PREFIX_STREAM_RESULTS' value = 'Y'/> <CWM:TaggedValue xmi.id = 'a714'
       * tag = 'DATABASE_ATTRIBUTE_PREFIX_EXTRA_OPTION_MYSQL.defaultFetchSize' value = '500'/> <CWM:TaggedValue xmi.id =
       * 'a715' tag = 'DATABASE_ATTRIBUTE_PREFIX_PORT_NUMBER' value = '3306'/> <CWM:TaggedValue xmi.id = 'a716' tag =
       * 'DATABASE_ATTRIBUTE_PREFIX_FORCE_IDENTIFIERS_TO_UPPERCASE' value = 'Y'/> <CWM:TaggedValue xmi.id = 'a717' tag =
       * 'DATABASE_ATTRIBUTE_PREFIX_FORCE_IDENTIFIERS_TO_LOWERCASE' value = 'Y'/> <CWM:TaggedValue xmi.id = 'a718' tag =
       * 'DATABASE_ATTRIBUTE_PREFIX_QUOTE_ALL_FIELDS' value = 'Y'/> <CWM:TaggedValue xmi.id = 'a719' tag =
       * 'DATABASE_JDBC_URL' value =
       * 'jdbc:mysql://localhost:3306/foodmart?defaultFetchSize=500&amp;useCursorFetch=true'/>
       */
      SqlPhysicalModel sqlPhysicalModel = new SqlPhysicalModel();
      domain.addPhysicalModel( sqlPhysicalModel );
      SqlDataSource sqlDataSource = new SqlDataSource();
      sqlPhysicalModel.setDatasource( sqlDataSource );

      String name = datasource.getAttribute( "name" ); //$NON-NLS-1$
      sqlPhysicalModel.setId( name );
      Map<String, String>
          kvp =
          getKeyValuePairs( datasource, "CWM:TaggedValue", "tag", "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

      String database_access_type = kvp.get( "DATABASE_ACCESS" );
      if ( database_access_type.equals( ", " ) ) {
        logger.warn( Messages.getErrorString( "XmiParser.ERROR_0011_UNSUPPORTED_DOMAIN", database_access_type ) );
        database_access_type = "JNDI";
      }

      sqlDataSource
          .setType( DataSourceType.values()[DatabaseMeta.getAccessType( database_access_type )] ); //$NON-NLS-1$
      sqlDataSource.setDatabaseName( kvp.get( "DATABASE_DATABASE" ) ); //$NON-NLS-1$
      sqlDataSource.setHostname( kvp.get( "DATABASE_SERVER" ) ); //$NON-NLS-1$
      sqlDataSource.setPort( kvp.get( "DATABASE_PORT" ) ); //$NON-NLS-1$
      sqlDataSource.setUsername( kvp.get( "DATABASE_USERNAME" ) ); //$NON-NLS-1$
      sqlDataSource.setPassword( kvp.get( "DATABASE_PASSWORD" ) ); //$NON-NLS-1$
      sqlDataSource.setDialectType( kvp.get( "DATABASE_TYPE" ) ); //$NON-NLS-1$
      sqlDataSource.setServername( kvp.get( "DATABASE_SERVER_INSTANCE" ) ); //$NON-NLS-1$

      // And now load the attributes...
      for ( String tag : kvp.keySet() ) {
        if ( tag.startsWith( CWM.TAG_DATABASE_ATTRIBUTE_PREFIX ) ) {
          String key = tag.substring( CWM.TAG_DATABASE_ATTRIBUTE_PREFIX.length() );
          String attribute = kvp.get( tag );
          // Add the attribute
          sqlDataSource.getAttributes().put( key, attribute );
        }
      }
    }

    SqlPhysicalModel missingParentModel = null;

    for ( Element physicalTable : physicalTables ) {
      String name = physicalTable.getAttribute( "name" ); //$NON-NLS-1$
      Element tagged = null;
      Element owned = null;
      NodeList ptcn = physicalTable.getChildNodes();
      for ( int i = 0; i < ptcn.getLength(); i++ ) {
        if ( ptcn.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
          if ( ptcn.item( i ).getNodeName().equals( "CWM:ModelElement.taggedValue" ) ) { //$NON-NLS-1$
            tagged = (Element) ptcn.item( i );
          }
          if ( ptcn.item( i ).getNodeName().equals( "CWM:Namespace.ownedElement" ) ) { //$NON-NLS-1$
            owned = (Element) ptcn.item( i );
          }
        }
      }
      String
          databaseName =
          getKeyValue( tagged, "CWM:TaggedValue", "tag", "value",
              "TABLE_TARGET_DATABASE_NAME" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

      if ( databaseName == null ) {
        logger.warn( Messages.getErrorString( "XmiParser.ERROR_0009_MISSING_DATABASE_PARENT", name ) ); //$NON-NLS-1$
        if ( missingParentModel == null ) {
          missingParentModel = new SqlPhysicalModel();
          missingParentModel.setId( "__MISSING_PARENT_PHYSICAL_MODEL__" ); //$NON-NLS-1$
          domain.addPhysicalModel( missingParentModel );
        }
        databaseName = "__MISSING_PARENT_PHYSICAL_MODEL__"; //$NON-NLS-1$
      }
      SqlPhysicalModel model = (SqlPhysicalModel) domain.findPhysicalModel( databaseName );
      SqlPhysicalTable table = new SqlPhysicalTable( model );
      table.setId( physicalTable.getAttribute( "name" ) ); //$NON-NLS-1$
      xmiConceptMap.put( physicalTable.getAttribute( "xmi.id" ), table ); //$NON-NLS-1$
      model.addPhysicalTable( table );
      bindParentConcept( physicalTable, domain, table );
      NodeList columns = owned.getElementsByTagName( "CWMRDB:Column" ); //$NON-NLS-1$
      for ( int i = 0; i < columns.getLength(); i++ ) {
        Element colelement = (Element) columns.item( i );

        SqlPhysicalColumn col = new SqlPhysicalColumn( table );
        col.setId( colelement.getAttribute( "name" ) ); //$NON-NLS-1$
        xmiConceptMap.put( colelement.getAttribute( "xmi.id" ), col ); //$NON-NLS-1$
        table.addPhysicalColumn( col );
        bindParentConcept( colelement, domain, col );
      }

      /*
       * <CWMRDB:Table isAbstract="false" isSystem="false" isTemporary="false" name="PT_TRIAL_BALANCE" xmi.id="a143">
       * <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="TABLE_TARGET_DATABASE_NAME" value="SampleData"
       * xmi.id="a1183"/> </CWM:ModelElement.taggedValue> <CWM:Namespace.ownedElement> <CWMRDB:Column name="Amount"
       * xmi.id="a89"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="CONCEPT_PARENT_NAME" value="Base"
       * xmi.id="a1184"/> </CWM:ModelElement.taggedValue> </CWMRDB:Column> <CWMRDB:Column name="Detail" xmi.id="a98">
       * <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="CONCEPT_PARENT_NAME" value="Base" xmi.id="a1185"/>
       * </CWM:ModelElement.taggedValue> </CWMRDB:Column> </CWM:Namespace.ownedElement> </CWMRDB:Table>
       */
    }

    for ( Element schema : schemas ) {
      LogicalModel logicalModel = new LogicalModel();

      logicalModel.setId( schema.getAttribute( "name" ) ); //$NON-NLS-1$
      xmiConceptMap.put( schema.getAttribute( "xmi.id" ), logicalModel ); //$NON-NLS-1$

      bindParentConcept( schema, domain, logicalModel );

      domain.addLogicalModel( logicalModel );

      Element dimension = null;
      Element dimensionedObject = null;
      Element ownedElement = null;
      NodeList schemaChildren = schema.getChildNodes();
      for ( int i = 0; i < schemaChildren.getLength(); i++ ) {
        if ( schemaChildren.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
          if ( schemaChildren.item( i ).getNodeName().equals( "CWMMDB:Schema.dimension" ) ) { //$NON-NLS-1$
            dimension = (Element) schemaChildren.item( i );
          } else if ( schemaChildren.item( i ).getNodeName()
              .equals( "CWMMDB:Schema.dimensionedObject" ) ) { //$NON-NLS-1$
            dimensionedObject = (Element) schemaChildren.item( i );
          } else if ( schemaChildren.item( i ).getNodeName().equals( "CWM:Namespace.ownedElement" ) ) { //$NON-NLS-1$
            ownedElement = (Element) schemaChildren.item( i );
          } else {
            if ( logger.isDebugEnabled() ) {
              logger.debug( "Schema ignored: " + schemaChildren.item( i ).getNodeName() ); //$NON-NLS-1$
            }
          }
        }
      }

      if ( dimension != null ) {
        // first read all biz tables
        NodeList bizTables = dimension.getElementsByTagName( "CWMMDB:Dimension" ); //$NON-NLS-1$
        for ( int i = 0; i < bizTables.getLength(); i++ ) {
          Element biztable = (Element) bizTables.item( i );
          LogicalTable table = new LogicalTable();
          table.setId( biztable.getAttribute( "name" ) ); //$NON-NLS-1$
          bindParentConcept( biztable, domain, table );
          Map<String, String>
              nvp =
              getKeyValuePairs( biztable, "CWM:TaggedValue", "tag", "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          String pt = nvp.get( "BUSINESS_TABLE_PHYSICAL_TABLE_NAME" ); //$NON-NLS-1$
          IPhysicalTable physTable = domain.findPhysicalTable( pt );

          // set the model's physical table if not already set and if available
          if ( physTable != null && logicalModel.getPhysicalModel() == null ) {
            logicalModel.setPhysicalModel( physTable.getPhysicalModel() );
          }
          table.setPhysicalTable( physTable );
          table.setLogicalModel( logicalModel );
          // store legacy values
          if ( nvp.containsKey( "TABLE_IS_DRAWN" ) ) { //$NON-NLS-1$
            table.setProperty( "__LEGACY_TABLE_IS_DRAWN", nvp.get( "TABLE_IS_DRAWN" ) ); //$NON-NLS-1$ //$NON-NLS-2$
          }
          if ( nvp.containsKey( "TAG_POSITION_Y" ) ) { //$NON-NLS-1$
            table.setProperty( "__LEGACY_TAG_POSITION_Y", nvp.get( "TAG_POSITION_Y" ) ); //$NON-NLS-1$ //$NON-NLS-2$
          }
          if ( nvp.containsKey( "TAG_POSITION_X" ) ) { //$NON-NLS-1$
            table.setProperty( "__LEGACY_TAG_POSITION_X", nvp.get( "TAG_POSITION_X" ) ); //$NON-NLS-1$ //$NON-NLS-2$
          }
          xmiConceptMap.put( biztable.getAttribute( "xmi.id" ), table ); //$NON-NLS-1$
          logicalModel.addLogicalTable( table );
          /*
           * <CWMMDB:Dimension isAbstract="false" name="BT_EMPLOYEES_EMPLOYEES" xmi.id="a21"> <- Biz table
           * <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="TABLE_IS_DRAWN" value="Y" xmi.id="a1396"/>
           * <CWM:TaggedValue tag="TAG_POSITION_Y" value="151" xmi.id="a1397"/> <CWM:TaggedValue tag="TAG_POSITION_X"
           * value="213" xmi.id="a1398"/> <CWM:TaggedValue tag="BUSINESS_TABLE_PHYSICAL_TABLE_NAME" value="PT_EMPLOYEES"
           * xmi.id="a1399"/> </CWM:ModelElement.taggedValue> <CWMMDB:Dimension.dimensionedObject>
           * <CWMMDB:DimensionedObject xmi.idref="a1365"/> <CWMMDB:DimensionedObject xmi.idref="a1362"/>
           * </CWMMDB:Dimension.dimensionedObject> </CWMMDB:Dimension>
           */
        }
      }

      if ( dimensionedObject != null ) {
        // second read all biz cols
        NodeList bizcols = dimensionedObject.getElementsByTagName( "CWMMDB:DimensionedObject" ); //$NON-NLS-1$
        for ( int i = 0; i < bizcols.getLength(); i++ ) {
          /*
           * <CWMMDB:DimensionedObject name="BC_EMPLOYEES_JOBTITLE" xmi.id="a1344"> <CWM:ModelElement.taggedValue>
           * <CWM:TaggedValue tag="BUSINESS_COLUMN_BUSINESS_TABLE" value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1345"/>
           * <CWM:TaggedValue tag="BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME" value="JOBTITLE" xmi.id="a1346"/>
           * </CWM:ModelElement.taggedValue> <CWMMDB:DimensionedObject.dimension> <CWMMDB:Dimension xmi.idref="a21"/>
           * </CWMMDB:DimensionedObject.dimension> </CWMMDB:DimensionedObject>
           */
          Element bizcol = (Element) bizcols.item( i );
          LogicalColumn col = new LogicalColumn();
          col.setId( bizcol.getAttribute( "name" ) ); //$NON-NLS-1$
          xmiConceptMap.put( bizcol.getAttribute( "xmi.id" ), col ); //$NON-NLS-1$
          bindParentConcept( bizcol, domain, col );

          Map<String, String>
              nvp =
              getKeyValuePairs( bizcol, "CWM:TaggedValue", "tag", "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          String biztbl = nvp.get( "BUSINESS_COLUMN_BUSINESS_TABLE" ); //$NON-NLS-1$
          String pcol = nvp.get( "BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME" ); //$NON-NLS-1$
          LogicalTable parent = logicalModel.findLogicalTable( biztbl );
          if ( parent != null ) {
            col.setLogicalTable( parent );
            parent.addLogicalColumn( col );
            for ( IPhysicalColumn phycol : parent.getPhysicalTable().getPhysicalColumns() ) {
              if ( phycol.getId().equals( pcol ) ) {
                col.setPhysicalColumn( phycol );
                break;
              }
            }
          }
        }
      }

      if ( ownedElement != null ) {
        // third read categories
        NodeList categories = ownedElement.getElementsByTagName( "CWM:Extent" ); //$NON-NLS-1$
        for ( int i = 0; i < categories.getLength(); i++ ) {
          /*
           * <CWM:Extent name="BC_OFFICES_" xmi.id="a13"> <-- Category <CWM:ModelElement.taggedValue> <CWM:TaggedValue
           * tag="BUSINESS_CATEGORY_ROOT" value="Y" xmi.id="a1304"/> </CWM:ModelElement.taggedValue>
           * <CWM:Namespace.ownedElement> <CWM:Attribute name="BC_OFFICES_TERRITORY" xmi.id="a1305">
           * <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="BUSINESS_CATEGORY_TYPE" value="Column"
           * xmi.id="a1306"/> </CWM:ModelElement.taggedValue> </CWM:Attribute> <CWM:Attribute
           * name="BC_OFFICES_POSTALCODE" xmi.id="a1307"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue
           * tag="BUSINESS_CATEGORY_TYPE" value="Column" xmi.id="a1308"/> </CWM:ModelElement.taggedValue>
           * </CWM:Attribute> </CWM:Namespace.ownedElement> </CWM:Extent>
           */

          Element category = (Element) categories.item( i );
          Category cat = new Category( logicalModel );
          cat.setId( category.getAttribute( "name" ) ); //$NON-NLS-1$
          xmiConceptMap.put( category.getAttribute( "xmi.id" ), cat ); //$NON-NLS-1$
          bindParentConcept( category, domain, cat );
          NodeList columns = category.getElementsByTagName( "CWM:Attribute" ); //$NON-NLS-1$
          for ( int j = 0; j < columns.getLength(); j++ ) {
            Element column = (Element) columns.item( j );
            String name = column.getAttribute( "name" ); //$NON-NLS-1$
            LogicalColumn col = logicalModel.findLogicalColumn( name );
            if ( col == null ) {
              logger.warn( Messages.getString(
                  "XmiParser.ERROR_0010_UNABLE_TO_FIND_COL_FOR_CATEGORY", name, cat.getId() ) ); //$NON-NLS-1$
            } else {
              cat.addLogicalColumn( col );
            }
          }
          logicalModel.addCategory( cat );
        }

        // fourth read relationships
        NodeList rels = ownedElement.getElementsByTagName( "CWM:KeyRelationship" ); //$NON-NLS-1$
        for ( int i = 0; i < rels.getLength(); i++ ) {
          Element rel = (Element) rels.item( i );
          /*
           * <CWM:KeyRelationship xmi.id="a1338"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue
           * tag="RELATIONSHIP_TYPE" value="1:N" xmi.id="a1339"/> <CWM:TaggedValue tag="RELATIONSHIP_FIELDNAME_CHILD"
           * value="BC_EMPLOYEES_OFFICECODE" xmi.id="a1340"/> <CWM:TaggedValue tag="RELATIONSHIP_FIELDNAME_PARENT"
           * value="BC_OFFICES_OFFICECODE" xmi.id="a1341"/> <CWM:TaggedValue tag="RELATIONSHIP_TABLENAME_CHILD"
           * value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1342"/> <CWM:TaggedValue tag="RELATIONSHIP_TABLENAME_PARENT"
           * value="BT_OFFICES_OFFICES" xmi.id="a1343"/> </CWM:ModelElement.taggedValue> </CWM:KeyRelationship>
           */
          Map<String, String>
              nvp =
              getKeyValuePairs( rel, "CWM:TaggedValue", "tag", "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          LogicalRelationship relation = new LogicalRelationship();
          String type = nvp.get( "RELATIONSHIP_TYPE" ); //$NON-NLS-1$
          RelationshipType reltype = RelationshipType.values()[RelationshipMeta.getType( type )];
          relation.setRelationshipType( reltype );

          relation.setLogicalModel( logicalModel );

          String tablechild = nvp.get( "RELATIONSHIP_TABLENAME_CHILD" ); // to //$NON-NLS-1$
          String tableparent = nvp.get( "RELATIONSHIP_TABLENAME_PARENT" ); // from //$NON-NLS-1$
          String fieldchild = nvp.get( "RELATIONSHIP_FIELDNAME_CHILD" ); //$NON-NLS-1$
          String fieldparent = nvp.get( "RELATIONSHIP_FIELDNAME_PARENT" ); //$NON-NLS-1$

          relation.setFromTable( logicalModel.findLogicalTable( tableparent ) );
          if ( fieldparent != null ) {
            relation.setFromColumn( logicalModel.findLogicalColumn( fieldparent ) );
          }
          relation.setToTable( logicalModel.findLogicalTable( tablechild ) );
          if ( fieldchild != null ) {
            relation.setToColumn( logicalModel.findLogicalColumn( fieldchild ) );
          }

          relation.setComplex( "Y".equals( nvp.get( "RELATIONSHIP_IS_COMPLEX" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
          String val = nvp.get( "RELATIONSHIP_COMPLEX_JOIN" ); //$NON-NLS-1$
          if ( val != null ) {
            relation.setComplexJoin( val );
          }
          if ( !StringUtil.isEmpty( nvp.get( "RELATIONSHIP_DESCRIPTION" ) ) ) { //$NON-NLS-1$
            relation.setRelationshipDescription( nvp.get( "RELATIONSHIP_DESCRIPTION" ) ); //$NON-NLS-1$
          }
          String joinOrderKey = nvp.get( "RELATIONSHIP_JOIN_ORDER_KEY" ); //$NON-NLS-1$
          if ( joinOrderKey != null ) {
            relation.setJoinOrderKey( joinOrderKey );
          }

          logicalModel.addLogicalRelationship( relation );
        }

        // fourth read categories

        // second read tables
        /*
         * <CWMMDB:Schema name="BV_HUMAN_RESOURCES" xmi.id="a25"> <CWM:Namespace.ownedElement> <CWM:Extent
         * name="BC_OFFICES_" xmi.id="a13"> <-- Category <CWM:ModelElement.taggedValue> <CWM:TaggedValue
         * tag="BUSINESS_CATEGORY_ROOT" value="Y" xmi.id="a1304"/> </CWM:ModelElement.taggedValue>
         * <CWM:Namespace.ownedElement> <CWM:Attribute name="BC_OFFICES_TERRITORY" xmi.id="a1305">
         * <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="BUSINESS_CATEGORY_TYPE" value="Column" xmi.id="a1306"/>
         * </CWM:ModelElement.taggedValue> </CWM:Attribute> <CWM:Attribute name="BC_OFFICES_POSTALCODE" xmi.id="a1307">
         * <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="BUSINESS_CATEGORY_TYPE" value="Column" xmi.id="a1308"/>
         * </CWM:ModelElement.taggedValue> </CWM:Attribute> </CWM:Namespace.ownedElement> </CWM:Extent>
         * <CWM:KeyRelationship xmi.id="a1338"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="RELATIONSHIP_TYPE"
         * value="1:N" xmi.id="a1339"/> <CWM:TaggedValue tag="RELATIONSHIP_FIELDNAME_CHILD"
         * value="BC_EMPLOYEES_OFFICECODE" xmi.id="a1340"/> <CWM:TaggedValue tag="RELATIONSHIP_FIELDNAME_PARENT"
         * value="BC_OFFICES_OFFICECODE" xmi.id="a1341"/> <CWM:TaggedValue tag="RELATIONSHIP_TABLENAME_CHILD"
         * value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1342"/> <CWM:TaggedValue tag="RELATIONSHIP_TABLENAME_PARENT"
         * value="BT_OFFICES_OFFICES" xmi.id="a1343"/> </CWM:ModelElement.taggedValue> </CWM:KeyRelationship>
         * </CWM:Namespace.ownedElement> <CWMMDB:Schema.dimensionedObject> <CWMMDB:DimensionedObject
         * name="BC_EMPLOYEES_JOBTITLE" xmi.id="a1344"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue
         * tag="BUSINESS_COLUMN_BUSINESS_TABLE" value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1345"/> <CWM:TaggedValue
         * tag="BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME" value="JOBTITLE" xmi.id="a1346"/> </CWM:ModelElement.taggedValue>
         * <CWMMDB:DimensionedObject.dimension> <CWMMDB:Dimension xmi.idref="a21"/>
         * </CWMMDB:DimensionedObject.dimension> </CWMMDB:DimensionedObject> <CWMMDB:DimensionedObject
         * name="BC_EMPLOYEES_REPORTSTO" xmi.id="a1347"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue
         * tag="BUSINESS_COLUMN_BUSINESS_TABLE" value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1348"/> <CWM:TaggedValue
         * tag="BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME" value="REPORTSTO" xmi.id="a1349"/> </CWM:ModelElement.taggedValue>
         * <CWMMDB:DimensionedObject.dimension> <CWMMDB:Dimension xmi.idref="a21"/>
         * </CWMMDB:DimensionedObject.dimension> </CWMMDB:DimensionedObject> </CWMMDB:Schema.dimensionedObject>
         * <CWMMDB:Schema.dimension> <CWMMDB:Dimension isAbstract="false" name="BT_EMPLOYEES_EMPLOYEES" xmi.id="a21"> <-
         * Biz table <CWM:ModelElement.taggedValue> <CWM:TaggedValue tag="TABLE_IS_DRAWN" value="Y" xmi.id="a1396"/>
         * <CWM:TaggedValue tag="TAG_POSITION_Y" value="151" xmi.id="a1397"/> <CWM:TaggedValue tag="TAG_POSITION_X"
         * value="213" xmi.id="a1398"/> <CWM:TaggedValue tag="BUSINESS_TABLE_PHYSICAL_TABLE_NAME" value="PT_EMPLOYEES"
         * xmi.id="a1399"/> </CWM:ModelElement.taggedValue> <CWMMDB:Dimension.dimensionedObject>
         * <CWMMDB:DimensionedObject xmi.idref="a1365"/> <CWMMDB:DimensionedObject xmi.idref="a1362"/>
         * </CWMMDB:Dimension.dimensionedObject> </CWMMDB:Dimension> </CWMMDB:Schema.dimension> </CWMMDB:Schema>
         */
      }
    }

    // parse CWMOLAP:Schema
    populateOlapSchemas( olapSchemas, domain );

    for ( Element description : descriptions ) {
      /*
       * <CWM:Description body="N" name="hidden" type="Boolean" xmi.id="a989"> <CWM:Description.modelElement>
       * <CWMRDB:Column xmi.idref="a985"/> </CWM:Description.modelElement> </CWM:Description>
       */
      Element
          modelElem =
          (Element) description.getElementsByTagName( "CWM:Description.modelElement" ).item( 0 ); //$NON-NLS-1$
      if ( modelElem == null ) {
        continue;
      }

      NodeList mecn = modelElem.getChildNodes();
      String parentRef = null;
      String type = null;
      for ( int i = 0; i < mecn.getLength(); i++ ) {
        if ( mecn.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
          parentRef = ( (Element) mecn.item( i ) ).getAttribute( "xmi.idref" ); //$NON-NLS-1$
          // temporary, not really needed
          type = mecn.item( i ).getNodeName();
          break;
        }
      }
      if ( parentRef == null ) {
        logger.error( Messages.getErrorString( "XmiParser.ERROR_0007_PARENT_REF_NULL" ) ); //$NON-NLS-1$
      } else {
        Concept concept = xmiConceptMap.get( parentRef );
        String name = description.getAttribute( "name" ); //$NON-NLS-1$
        String body = description.getAttribute( "body" ); //$NON-NLS-1$
        if ( concept == null ) {
          logger.error(
              Messages.getErrorString( "XmiParser.ERROR_0010_CANNOT_FIND_PARENT", type, parentRef ) ); //$NON-NLS-1$
        } else {
          // ADD PROPERTY
          String propType = description.getAttribute( "type" ); //$NON-NLS-1$
          if ( propType.equals( "LocString" ) ) { //$NON-NLS-1$
            addLocalizedString( description, concept, name, body );
          } else if ( propType.equals( "String" ) ) { //$NON-NLS-1$
            if ( name.equals( LogicalModel.PROPERTY_OLAP_ROLES ) ) {
              // De-serialize roles and set directly into the LogicalModel
              List<OlapRole> roles = OlapUtil.fromXmlRoles( body );
              concept.setProperty( name, roles );
            } else if ( name.equals( LogicalModel.PROPERTY_OLAP_CALCULATED_MEMBERS ) ) {
              // De-serialize calculated members by cube
              Map<String, List<OlapCalculatedMember>> cubeMembers = OlapUtil.fromXmlCalculatedMembers( body );
              @SuppressWarnings( "unchecked" )
              List<OlapCube> cubes = (List<OlapCube>) concept.getProperty( LogicalModel.PROPERTY_OLAP_CUBES );
              for ( OlapCube cube : cubes ) {
                // Set the calculated members into the cube model objects
                if ( cubeMembers.containsKey( cube.getName() ) ) {
                  cube.setOlapCalculatedMembers( cubeMembers.get( cube.getName() ) );
                }
              }
            } else {
              // <CWM:Description body="" name="mask" type="String" xmi.id="a90">
              if ( name.equals( "formula" ) ) { //$NON-NLS-1$
                name = SqlPhysicalColumn.TARGET_COLUMN;
              }
              concept.setProperty( name, body );
            }
          } else if ( propType.equals( "Boolean" ) ) { //$NON-NLS-1$
            if ( name.equals( "exact" ) ) { //$NON-NLS-1$
              concept.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE,
                  "Y".equals( body ) ? TargetColumnType.OPEN_FORMULA : TargetColumnType.COLUMN_NAME ); //$NON-NLS-1$
            } else {
              // <CWM:Description body="N" name="exact" type="Boolean" xmi.id="a92">
              concept.setProperty( name, new Boolean( "Y".equals( body ) ) ); //$NON-NLS-1$
            }
          } else if ( propType.equals( "FieldType" ) ) { //$NON-NLS-1$
            // <CWM:Description body="Dimension" name="fieldtype" type="FieldType" xmi.id="a97">
            concept.setProperty( name, FieldType.values()[FieldTypeSettings.getType( body ).getType()] );
          } else if ( propType.equals( "TableType" ) ) { //$NON-NLS-1$
            concept.setProperty( name, TableType.values()[TableTypeSettings.getType( body ).getType()] );
          } else if ( propType.equals( "DataType" ) ) { //$NON-NLS-1$
            // <CWM:Description body="String,50,-1" name="datatype" type="DataType" xmi.id="a100">
            DataTypeSettings setting = DataTypeSettings.fromString( body );
            concept.setProperty( name, DataType.valueOf( setting.getCode().toUpperCase() ) );
            if ( setting.getPrecision() > 0 ) {
              String mask = "#.";
              for ( int i = 0; i < setting.getPrecision(); i++ ) {
                mask += "0";
              }
              concept.setProperty( "mask", mask );
            }
          } else if ( propType.equals( "Security" ) ) { //$NON-NLS-1$
            // <CWM:Description
            // body="&lt;security&gt;&#10;  &lt;owner-rights&gt;&#10;  &lt;owner&gt;&lt;type&gt;user&lt;/type&gt;&lt;name&gt;suzy&lt;/name&gt;&lt;/owner&gt; &lt;rights&gt;31&lt;/rights&gt;&#10;  &lt;/owner-rights&gt;&#10;  &lt;owner-rights&gt;&#10;  &lt;owner&gt;&lt;type&gt;role&lt;/type&gt;&lt;name&gt;Admin&lt;/name&gt;&lt;/owner&gt; &lt;rights&gt;31&lt;/rights&gt;&#10;  &lt;/owner-rights&gt;&#10;&lt;/security&gt;&#10;"
            // name="security" type="Security" xmi.id="a84">
            Security security = Security.fromXML( body );
            Map<SecurityOwner, Integer> map = new HashMap<SecurityOwner, Integer>();
            for ( org.pentaho.pms.schema.security.SecurityOwner owner : security.getOwners() ) {
              SecurityOwner ownerObj =
                  new SecurityOwner( SecurityOwner.OwnerType.values()[owner.getOwnerType()], owner.getOwnerName() );
              Integer val = security.getOwnerRights( owner );
              map.put( ownerObj, val );
            }
            concept.setProperty( name, new org.pentaho.metadata.model.concept.security.Security( map ) );
          } else if ( propType.equals( "RowLevelSecurity" ) ) { //$NON-NLS-1$
            org.pentaho.pms.schema.security.RowLevelSecurity security =
                org.pentaho.pms.schema.security.RowLevelSecurity.fromXML( body );

            RowLevelSecurity securityObj = new RowLevelSecurity();
            securityObj.setType( RowLevelSecurity.Type.values()[security.getType().ordinal()] );
            securityObj.setGlobalConstraint( security.getGlobalConstraint() );

            Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
            for ( org.pentaho.pms.schema.security.SecurityOwner owner : security.getRoleBasedConstraintMap()
                .keySet() ) {
              SecurityOwner ownerObj =
                  new SecurityOwner( SecurityOwner.OwnerType.values()[owner.getOwnerType()], owner.getOwnerName() );
              map.put( ownerObj, security.getRoleBasedConstraintMap().get( owner ) );
            }
            securityObj.setRoleBasedConstraintMap( map );
            concept.setProperty( name, securityObj );
          } else if ( propType.equals( "Aggregation" ) ) { //$NON-NLS-1$
            // <CWM:Description body="none" name="aggregation" type="Aggregation" xmi.id="a104">

            concept.setProperty( name, AggregationType.values()[AggregationSettings.getType( body ).getType()] );
          } else if ( propType.equals( "AggregationList" ) ) { //$NON-NLS-1$
            List<AggregationSettings> settings = ConceptPropertyAggregationList.fromXML( body );
            List<AggregationType> aggTypes = new ArrayList<AggregationType>();
            if ( settings != null ) {
              for ( AggregationSettings setting : settings ) {
                aggTypes.add( AggregationType.values()[setting.getType()] );
              }
            }
            concept.setProperty( name, aggTypes );
          } else if ( propType.equals( "Font" ) ) { //$NON-NLS-1$
            FontSettings font = FontSettings.fromString( body );
            concept.setProperty( name, new Font( font.getName(), font.getHeight(), font.isBold(), font.isItalic() ) );
          } else if ( propType.equals( "Color" ) ) { //$NON-NLS-1$
            ColorSettings color = ColorSettings.fromString( body );
            concept.setProperty( name, new Color( color.getRed(), color.getGreen(), color.getBlue() ) );
            // TODO: } else if (propType.equals("AggregationList")) {
            // TODO: ALL Others: URL, Number, etc
          } else if ( propType.equals( "Alignment" ) ) { //$NON-NLS-1$
            AlignmentSettings alignment = AlignmentSettings.fromString( body );
            concept.setProperty( name, Alignment.values()[alignment.getType()] );
          } else if ( propType.equals( "Number" ) ) { //$NON-NLS-1$
            BigDecimal bd = new BigDecimal( body );
            concept.setProperty( name, bd.doubleValue() );
          } else if ( propType.equals( "ColumnWidth" ) ) { //$NON-NLS-1$
            ColumnWidth cw = ColumnWidth.fromString( body );
            WidthType cwt = WidthType.values()[cw.getType()];
            org.pentaho.metadata.model.concept.types.ColumnWidth ncw =
                new org.pentaho.metadata.model.concept.types.ColumnWidth( cwt, cw.getWidth().doubleValue() );
            concept.setProperty( name, ncw );
          } else if ( propType.equals( "URL" ) ) { //$NON-NLS-1$
            // NOTE: URL is not compatible with GWT at this time
            URL url = new URL( body );
            concept.setProperty( name, url );
          } else if ( propType.equals( "TargetTableType" ) ) {
            concept.setProperty( name, TargetTableType.valueOf( body ) );
          } else {
            logger.error( Messages.getErrorString(
                "XmiParser.ERROR_0008_FAILED_TO_CONVERT_PROPERTY", propType, concept.getId() ) ); //$NON-NLS-1$
          }
        }
      }
    }
    return domain;
  }

  protected void populateOlapSchemas( List<Element> olapSchemas, Domain domain ) {

    for ( Element olapSchema : olapSchemas ) {
      // lookup metadata model
      LogicalModel model = domain.findLogicalModel( olapSchema.getAttribute( "name" ) ); //$NON-NLS-1$

      Element dimensionList = null;
      Element cubeList = null;
      NodeList schemaChildren = olapSchema.getChildNodes();
      for ( int i = 0; i < schemaChildren.getLength(); i++ ) {
        if ( schemaChildren.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
          if ( schemaChildren.item( i ).getNodeName().equals( "CWMOLAP:Schema.cube" ) ) { //$NON-NLS-1$
            cubeList = (Element) schemaChildren.item( i );
          } else if ( schemaChildren.item( i ).getNodeName().equals( "CWMOLAP:Schema.dimension" ) ) { //$NON-NLS-1$
            dimensionList = (Element) schemaChildren.item( i );
          } else {
            if ( logger.isDebugEnabled() ) {
              logger.debug( "Schema ignored: " + schemaChildren.item( i ).getNodeName() ); //$NON-NLS-1$
            }
          }
        }
      }

      Map<String, OlapDimension> dimensionMap = new HashMap<String, OlapDimension>();

      if ( dimensionList != null ) {

        List<OlapDimension> dimensionObjs = new ArrayList<OlapDimension>();

        NodeList dimensions = dimensionList.getElementsByTagName( "CWMOLAP:Dimension" ); //$NON-NLS-1$
        for ( int i = 0; i < dimensions.getLength(); i++ ) {
          Element dim = (Element) dimensions.item( i );
          OlapDimension dimensionObj = new OlapDimension();
          dimensionObj.setName( dim.getAttribute( "name" ) ); //$NON-NLS-1$
          boolean isTimeDimension = "true".equals( dim.getAttribute( "isTime" ) ); //$NON-NLS-1$ //$NON-NLS-2$
          dimensionObj.setTimeDimension( isTimeDimension );
          dimensionMap.put( dim.getAttribute( "xmi.id" ), dimensionObj ); //$NON-NLS-1$
          Element hierarchies = null;
          Element memberSelections = null;
          NodeList dimensionChildren = dim.getChildNodes();
          for ( int j = 0; j < dimensionChildren.getLength(); j++ ) {
            if ( dimensionChildren.item( j ).getNodeType() == Node.ELEMENT_NODE ) {
              if ( dimensionChildren.item( j ).getNodeName().equals( "CWMOLAP:Dimension.hierarchy" ) ) { //$NON-NLS-1$
                hierarchies = (Element) dimensionChildren.item( j );
              } else if ( dimensionChildren.item( j ).getNodeName()
                  .equals( "CWMOLAP:Dimension.memberSelection" ) ) { //$NON-NLS-1$
                memberSelections = (Element) dimensionChildren.item( j );
              } else if ( !dimensionChildren.item( j ).getNodeName().equals(
                "CWMOLAP:Dimension.cubeDimensionAssociation" ) ) { //$NON-NLS-1$
                //  cubes and dimensions are mapped later through dimension usages.
                if ( logger.isDebugEnabled() ) {
                  logger
                    .debug( "Dimension object ignored: " + dimensionChildren.item( j ).getNodeName() ); //$NON-NLS-1$
                }
              }
            }
          }
          Map<String, OlapHierarchyLevel> levelMap = new HashMap<String, OlapHierarchyLevel>();

          if ( hierarchies != null ) {
            NodeList hiers = hierarchies.getElementsByTagName( "CWMOLAP:LevelBasedHierarchy" ); //$NON-NLS-1$
            for ( int j = 0; j < hiers.getLength(); j++ ) {
              Element hierarchy = (Element) hiers.item( j );
              OlapHierarchy hierarchyObj = new OlapHierarchy( dimensionObj );
              hierarchyObj.setName( hierarchy.getAttribute( "name" ) ); //$NON-NLS-1$
              Map<String, String>
                  nvp =
                  getKeyValuePairs( hierarchy, "CWM:TaggedValue", "tag",
                      "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

              // tagged values
              hierarchyObj.setHavingAll( "Y".equals( nvp.get( "HIERARCHY_HAVING_ALL" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
              String ltblId = nvp.get( "HIERARCHY_BUSINESS_TABLE" ); //$NON-NLS-1$

              LogicalTable table = null;
              if ( ltblId != null ) {
                table = model.findLogicalTable( ltblId );
              }

              String lcolId = nvp.get( "HIERARCHY_PRIMARY_KEY" ); //$NON-NLS-1$
              LogicalColumn primaryKey = null;
              if ( lcolId != null ) {
                primaryKey = table.findLogicalColumn( lcolId );
              }

              hierarchyObj.setLogicalTable( table );
              hierarchyObj.setPrimaryKey( primaryKey );

              dimensionObj.getHierarchies().add( hierarchyObj );

              NodeList levels = hierarchy.getElementsByTagName( "CWMOLAP:HierarchyLevelAssociation" ); //$NON-NLS-1$
              List<OlapHierarchyLevel> hierarchyLevels = new ArrayList<OlapHierarchyLevel>();
              for ( int k = 0; k < levels.getLength(); k++ ) {
                Element level = (Element) levels.item( k );
                OlapHierarchyLevel levelObj = new OlapHierarchyLevel( hierarchyObj );
                levelObj.setName( level.getAttribute( "name" ) ); //$NON-NLS-1$
                hierarchyLevels.add( levelObj );

                NodeList levelrefs = level.getElementsByTagName( "CWMOLAP:Level" ); //$NON-NLS-1$
                if ( levelrefs.getLength() == 1 ) {
                  Element levelRefElem = (Element) levelrefs.item( 0 );
                  String xmiid = levelRefElem.getAttribute( "xmi.idref" ); //$NON-NLS-1$
                  levelMap.put( xmiid, levelObj );
                }

              }
              hierarchyObj.setHierarchyLevels( hierarchyLevels );

              /*
               * <CWMOLAP:LevelBasedHierarchy.hierarchyLevelAssociation> <CWMOLAP:HierarchyLevelAssociation xmi.id =
               * 'a326' name = 'Lname - L' isAbstract = 'false'> <CWMOLAP:HierarchyLevelAssociation.currentLevel>
               * <CWMOLAP:Level xmi.idref = 'a327'/> </CWMOLAP:HierarchyLevelAssociation.currentLevel>
               * </CWMOLAP:HierarchyLevelAssociation> <CWMOLAP:HierarchyLevelAssociation xmi.id = 'a328' name = 'Mi'
               * isAbstract = 'false'> <CWMOLAP:HierarchyLevelAssociation.currentLevel> <CWMOLAP:Level xmi.idref =
               * 'a329'/> </CWMOLAP:HierarchyLevelAssociation.currentLevel> </CWMOLAP:HierarchyLevelAssociation>
               * </CWMOLAP:LevelBasedHierarchy.hierarchyLevelAssociation>
               */
            }

            dimensionObjs.add( dimensionObj );
          }

          model.setProperty( "olap_dimensions", dimensionObjs ); //$NON-NLS-1$

          if ( memberSelections != null ) {
            NodeList levels = memberSelections.getElementsByTagName( "CWMOLAP:Level" ); //$NON-NLS-1$
            for ( int j = 0; j < levels.getLength(); j++ ) {
              Element level = (Element) levels.item( j );
              String xmiid = level.getAttribute( "xmi.id" ); //$NON-NLS-1$
              OlapHierarchyLevel levelObj = levelMap.get( xmiid );
              Map<String, String>
                  nvp =
                  getKeyValuePairs( level, "CWM:TaggedValue", "tag",
                      "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
              levelObj.setHavingUniqueMembers(
                  "Y".equals( nvp.get( "HIERARCHY_LEVEL_UNIQUE_MEMBERS" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
              levelObj.setLevelType( nvp.get( "HIERARCHY_LEVEL_TYPE" ) ); //$NON-NLS-1$
              String levelRefCol, tag;

              tag = "HIERARCHY_LEVEL_REFERENCE_COLUMN"; //$NON-NLS-1$
              levelRefCol = nvp.get( tag );
              if ( levelRefCol != null ) {
                levelObj.setReferenceColumn( model.findLogicalColumn( levelRefCol ) );
              }

              tag = "HIERARCHY_LEVEL_REFERENCE_ORDINAL_COLUMN"; //$NON-NLS-1$
              levelRefCol = nvp.get( tag );
              if ( levelRefCol != null ) {
                levelObj.setReferenceOrdinalColumn( model.findLogicalColumn( levelRefCol ) );
              }

              tag = "HIERARCHY_LEVEL_REFERENCE_CAPTION_COLUMN"; //$NON-NLS-1$
              levelRefCol = nvp.get( tag );
              if ( levelRefCol != null ) {
                levelObj.setReferenceCaptionColumn( model.findLogicalColumn( levelRefCol ) );
              }
              // CWMMDB:DimensionedObject xmi.id = 'a340' name
              List<LogicalColumn> referenceCols = new ArrayList<LogicalColumn>();
              NodeList dimensionedObjs = level.getElementsByTagName( "CWMMDB:DimensionedObject" ); //$NON-NLS-1$
              for ( int k = 0; k < dimensionedObjs.getLength(); k++ ) {
                Element col = (Element) dimensionedObjs.item( k );
                referenceCols.add( model.findLogicalColumn( col.getAttribute( "name" ) ) ); //$NON-NLS-1$
              }

              levelObj.setHidden( nvp.get( OlapHierarchyLevel.HIERARCHY_LEVEL_HIDDEN ) != null
                  ? Boolean.parseBoolean( nvp.get( OlapHierarchyLevel.HIERARCHY_LEVEL_HIDDEN ) ) : false );

              levelObj.setFormatter( nvp.get( OlapHierarchyLevel.HIERARCHY_LEVEL_FORMATTER ) );

              // CWM:TaggedValue tag="ANNOTATION_*
              for ( String taggedValueKey : nvp.keySet() ) {
                if ( taggedValueKey != null && taggedValueKey.startsWith( "ANNOTATION_" ) ) {
                  String name = taggedValueKey.substring( 11 );
                  String value = nvp.get( taggedValueKey );
                  OlapAnnotation annotationObj = new OlapAnnotation();
                  annotationObj.setName( name );
                  annotationObj.setValue( value );
                  levelObj.getAnnotations().add( annotationObj );
                }
              }
              // NodeList annotations = level.getElementsByTagName("CWMOLAP:Annotation");
              // for(int count = 0; count < annotations.getLength(); count++) {
              // Element annotation = (Element)annotations.item(count);
              // OlapAnnotation annotationObj = new OlapAnnotation();
              // annotationObj.setName(annotation.getAttribute("name"));
              // annotationObj.setValue(annotation.getAttribute("value"));
              // levelObj.getAnnotations().add(annotationObj);
              // }

              levelObj.setLogicalColumns( referenceCols );
            }

          }
        }
      }

      if ( cubeList != null ) {

        List<OlapCube> cubesList = new ArrayList<OlapCube>();

        NodeList cubes = cubeList.getElementsByTagName( "CWMOLAP:Cube" ); //$NON-NLS-1$
        for ( int i = 0; i < cubes.getLength(); i++ ) {
          Element cube = (Element) cubes.item( i );
          OlapCube cubeObj = new OlapCube();
          cubeObj.setName( cube.getAttribute( "name" ) ); //$NON-NLS-1$
          Map<String, String>
              nvp =
              getKeyValuePairs( cube, "CWM:TaggedValue", "tag", "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          cubeObj.setLogicalTable( model.findLogicalTable( nvp.get( "CUBE_BUSINESS_TABLE" ) ) ); //$NON-NLS-1$

          List<OlapMeasure> measureList = new ArrayList<OlapMeasure>();
          NodeList measures = cube.getElementsByTagName( "CWMOLAP:Measure" ); //$NON-NLS-1$
          for ( int j = 0; j < measures.getLength(); j++ ) {
            Element measure = (Element) measures.item( j );
            OlapMeasure measureObj = new OlapMeasure();
            measureObj.setName( measure.getAttribute( "name" ) ); //$NON-NLS-1$
            Map<String, String>
                nvp2 =
                getKeyValuePairs( measure, "CWM:TaggedValue", "tag",
                    "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            measureObj
                .setLogicalColumn( model.findLogicalColumn( nvp2.get( "MEASURE_BUSINESS_COLUMN" ) ) ); //$NON-NLS-1$

            measureObj.setHidden( nvp2.get( OlapMeasure.MEASURE_HIDDEN ) != null
                ? Boolean.parseBoolean( nvp2.get( OlapMeasure.MEASURE_HIDDEN ) ) : false );

            measureList.add( measureObj );
          }
          cubeObj.setOlapMeasures( measureList );

          List<OlapDimensionUsage> dimensionUsages = new ArrayList<OlapDimensionUsage>();
          NodeList usedDims = cube.getElementsByTagName( "CWMOLAP:CubeDimensionAssociation" ); //$NON-NLS-1$
          for ( int j = 0; j < usedDims.getLength(); j++ ) {
            Element dim = (Element) usedDims.item( j );
            OlapDimensionUsage dimUsage = new OlapDimensionUsage();
            dimUsage.setName( dim.getAttribute( "name" ) ); //$NON-NLS-1$
            dimensionUsages.add( dimUsage );
            // CWMOLAP:Dimension xmi.idref
            NodeList dimensionLinks = dim.getElementsByTagName( "CWMOLAP:Dimension" ); //$NON-NLS-1$
            if ( dimensionLinks.getLength() == 1 ) {
              Element dimensionLink = (Element) dimensionLinks.item( 0 );
              dimUsage.setOlapDimension( dimensionMap.get( dimensionLink.getAttribute( "xmi.idref" ) ) ); //$NON-NLS-1$
            }
          }
          cubeObj.setOlapDimensionUsages( dimensionUsages );

          cubesList.add( cubeObj );
        }

        model.setProperty( "olap_cubes", cubesList ); //$NON-NLS-1$
      }

      /*
       * 
       * <CWMOLAP:Dimension.memberSelection> <CWMOLAP:Level xmi.id = 'a318' name = 'fname' isAbstract = 'false'>
       * <CWM:ModelElement.taggedValue> <CWM:TaggedValue xmi.id = 'a319' tag = 'HIERARCHY_LEVEL_UNIQUE_MEMBERS' value =
       * 'Y'/> <CWM:TaggedValue xmi.id = 'a320' tag = 'HIERARCHY_LEVEL_REFERENCE_COLUMN' value = 'LC_CUSTOMER2_FNAME'/>
       * </CWM:ModelElement.taggedValue> <CWMOLAP:Level.hierarchyLevelAssociation> <CWMOLAP:HierarchyLevelAssociation
       * xmi.idref = 'a317'/> </CWMOLAP:Level.hierarchyLevelAssociation> </CWMOLAP:Level>
       * </CWMOLAP:Dimension.memberSelection>
       * 
       * 
       * 
       * <CWMOLAP:Schema xmi.id = 'a698' name = 'BV_MODEL_1'> <CWMOLAP:Schema.cube> <CWMOLAP:Cube xmi.id = 'a699' name =
       * 'Customer' isAbstract = 'false' isVirtual = 'false'> <CWM:ModelElement.taggedValue> <CWM:TaggedValue xmi.id =
       * 'a700' tag = 'CUBE_BUSINESS_TABLE' value = 'BT_CUSTOMER_CUSTOMER'/> </CWM:ModelElement.taggedValue>
       * <CWM:Namespace.ownedElement> <CWMOLAP:Measure xmi.id = 'a701' name = 'Num cars owned'>
       * <CWM:ModelElement.taggedValue> <CWM:TaggedValue xmi.id = 'a702' tag = 'MEASURE_BUSINESS_COLUMN' value =
       * 'BC_CUSTOMER_NUM_CARS_OWNED'/> </CWM:ModelElement.taggedValue> </CWMOLAP:Measure> </CWM:Namespace.ownedElement>
       * <CWMOLAP:Cube.cubeDimensionAssociation> <CWMOLAP:CubeDimensionAssociation xmi.id = 'a703' name = 'A Dimension'
       * isAbstract = 'false'> <CWMOLAP:CubeDimensionAssociation.dimension> <CWMOLAP:Dimension xmi.idref = 'a704'/>
       * </CWMOLAP:CubeDimensionAssociation.dimension> </CWMOLAP:CubeDimensionAssociation>
       * </CWMOLAP:Cube.cubeDimensionAssociation> </CWMOLAP:Cube> </CWMOLAP:Schema.cube> <CWMOLAP:Schema.dimension>
       * <CWMOLAP:Dimension xmi.id = 'a704' name = 'A Dimension' isAbstract = 'false' isTime = 'false' isMeasure =
       * 'false'> <CWMOLAP:Dimension.cubeDimensionAssociation> <CWMOLAP:CubeDimensionAssociation xmi.idref = 'a703'/>
       * </CWMOLAP:Dimension.cubeDimensionAssociation> <CWMOLAP:Dimension.hierarchy> <CWMOLAP:LevelBasedHierarchy xmi.id
       * = 'a705' name = 'A Hierarchy' isAbstract = 'false'> <CWM:ModelElement.taggedValue> <CWM:TaggedValue xmi.id =
       * 'a706' tag = 'HIERARCHY_BUSINESS_TABLE' value = 'BT_CUSTOMER_CUSTOMER'/> <CWM:TaggedValue xmi.id = 'a707' tag =
       * 'HIERARCHY_PRIMARY_KEY' value = 'BC_CUSTOMER_CUSTOMER_ID'/> <CWM:TaggedValue xmi.id = 'a708' tag =
       * 'HIERARCHY_HAVING_ALL' value = 'Y'/> </CWM:ModelElement.taggedValue> </CWMOLAP:LevelBasedHierarchy>
       * </CWMOLAP:Dimension.hierarchy> </CWMOLAP:Dimension> </CWMOLAP:Schema.dimension> </CWMOLAP:Schema>
       */
    }

  }

  protected void bindParentConcept( Element element, Domain domain, IConcept concept ) {
    Element tagged = null;
    NodeList pccn = element.getChildNodes();
    for ( int j = 0; j < pccn.getLength(); j++ ) {
      if ( pccn.item( j ).getNodeType() == Node.ELEMENT_NODE ) {
        if ( pccn.item( j ).getNodeName().equals( "CWM:ModelElement.taggedValue" ) ) { //$NON-NLS-1$
          tagged = (Element) pccn.item( j );
        }
      }
    }
    if ( tagged != null ) {
      String
          conceptParentName =
          getKeyValue( tagged, "CWM:TaggedValue", "tag", "value",
              "CONCEPT_PARENT_NAME" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      if ( conceptParentName != null ) {
        Concept parent = domain.findConcept( conceptParentName );
        if ( parent == null ) {
          logger.error( Messages
              .getErrorString( "XmiParser.ERROR_0006_FAILED_TO_LOCATE_CONCEPT", conceptParentName ) ); //$NON-NLS-1$
        } else {
          concept.setParentConcept( parent );
        }
      }
    }
  }

  protected void addLocalizedString( Element description, Concept concept, String name, String body ) {
    /*
     * <CWM:Description body="Oficinas" language="es" name="name" type="LocString" xmi.id="a14">
     */

    String lang = description.getAttribute( "language" ); //$NON-NLS-1$
    LocalizedString str = (LocalizedString) concept.getChildProperty( name );
    if ( str == null ) {
      str = new LocalizedString();
      concept.setProperty( name, str );
    }
    str.setString( lang, body );
  }

  protected void populateLocales( Domain domain, List<Element> parameters ) {

    List<LocaleInterface> legacyLocaleList = new ArrayList<LocaleInterface>();
    for ( Element parameter : parameters ) {
      /*
       * <CWM:Parameter name="es" xmi.id="a1154"> <CWM:ModelElement.taggedValue> <CWM:TaggedValue
       * tag="LOCALE_IS_DEFAULT" value="N" xmi.id="a1155"/> <CWM:TaggedValue tag="LOCALE_ORDER" value="2"
       * xmi.id="a1156"/> <CWM:TaggedValue tag="LOCALE_DESCRIPTION" value="Spanish" xmi.id="a1157"/>
       * </CWM:ModelElement.taggedValue> </CWM:Parameter>
       */
      LocaleInterface locale = new LocaleMeta();
      locale.setCode( parameter.getAttribute( "name" ) ); //$NON-NLS-1$
      Map<String, String>
          kvp =
          getKeyValuePairs( parameter, "CWM:TaggedValue", "tag", "value" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

      // The description
      String description = kvp.get( "LOCALE_DESCRIPTION" ); //$NON-NLS-1$
      if ( !Const.isEmpty( description ) ) {
        locale.setDescription( description );
      }

      // The order
      String strOrder = kvp.get( "LOCALE_ORDER" ); //$NON-NLS-1$
      locale.setOrder( Const.toInt( strOrder, -1 ) );

      // Active?

      legacyLocaleList.add( locale );
    }

    List<LocaleType> localeTypes = new ArrayList<LocaleType>();

    // the new model uses the natural ordering of the list vs. a separate ordinal

    Collections.sort( legacyLocaleList, new Comparator<LocaleInterface>() {
      // TODO: Test ordering
      public int compare( LocaleInterface o1, LocaleInterface o2 ) {
        if ( o1.getOrder() > o2.getOrder() ) {
          return 1;
        } else if ( o1.getOrder() < o2.getOrder() ) {
          return -1;
        } else {
          return 0;
        }
      }
    } );

    for ( LocaleInterface locale : legacyLocaleList ) {
      LocaleType localeType = new LocaleType();
      localeType.setDescription( locale.getDescription() );
      localeType.setCode( locale.getCode() );
      localeTypes.add( localeType );
    }
    domain.setLocales( localeTypes );

  }

  private static Map<String, String> getKeyValuePairs( Element parent, String childName, String keyAttrib,
      String valAttrib ) {
    HashMap<String, String> map = new HashMap<String, String>();
    NodeList nodeList = parent.getElementsByTagName( childName );
    for ( int i = 0; i < nodeList.getLength(); i++ ) {
      map.put( ( (Element) nodeList.item( i ) ).getAttribute( keyAttrib ), ( (Element) nodeList.item( i ) )
          .getAttribute( valAttrib ) );
    }
    return map;
  }

  private static String getKeyValue( Element parent, String childName,
      String keyAttrib, String valAttrib, String keyVal ) {
    if ( parent == null ) {
      return null;
    }
    NodeList nodeList = parent.getElementsByTagName( childName );
    for ( int i = 0; i < nodeList.getLength(); i++ ) {
      String key = ( (Element) nodeList.item( i ) ).getAttribute( keyAttrib );
      if ( key.equals( keyVal ) ) {
        return ( (Element) nodeList.item( i ) ).getAttribute( valAttrib );
      }
    }
    return null;
  }

  /**
   * Delegates creating of DocumentBuilderFactory to {@link XMLParserFactoryProducer#createSecureDocBuilderFactory}.
   *
   * @throws ParserConfigurationException if feature can't be enabled
   *
   */
  public static DocumentBuilderFactory createSecureDocBuilderFactory() throws ParserConfigurationException {
    DocumentBuilderFactory documentBuilderFactory = XMLParserFactoryProducer.createSecureDocBuilderFactory();

    return documentBuilderFactory;
  }
}
