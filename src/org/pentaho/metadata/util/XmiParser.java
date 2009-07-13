/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
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
package org.pentaho.metadata.util;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.messages.LocaleHelper;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.IPhysicalModel;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.SqlDataSource.DataSourceType;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.security.RowLevelSecurity;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.metadata.model.concept.types.Color;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.RelationshipType;
import org.pentaho.metadata.model.concept.types.TableType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.model.concept.types.ColumnWidth.WidthType;
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
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.schema.security.RowLevelSecurity.Type;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This code parses an XMI xml file.
 * 
 * Note: 
 *  olap support (CWMOLAP:Schema) is not supported at this time. 
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class XmiParser {
  
  private static final Log logger = LogFactory.getLog(XmiParser.class);
  
  public String generateXmi(Domain domain) {
    if (domain == null) {
      logger.error("generateXML: domain must not be null");
      return null;
    }
    
    try {
      StringWriter stringWriter = new StringWriter();
      StreamResult result = new StreamResult();
      result.setWriter(stringWriter);
      TransformerFactory factory = TransformerFactory.newInstance();
      Document doc = toXmiDocument(domain);
      if (doc != null) {
        factory.newTransformer().transform(new DOMSource(doc), result);
        return stringWriter.getBuffer().toString();
      }
    } catch (Exception e) {
      logger.error(Messages.getErrorString("QueryXmlHelper.ERROR_0001_TO_XML_FAILED"), e); //$NON-NLS-1$
    }
    return null;
  }
  
  public Document toXmiDocument(Domain domain) {
    if (domain == null) {
      logger.error("toDocument: query must not be null");
      return null;
    }
    
    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    Document doc;

    try {
      // create an XML document
      dbf = DocumentBuilderFactory.newInstance();
      db = dbf.newDocumentBuilder();
      doc = db.newDocument();
      Element xmiElement = doc.createElement("XMI"); //$NON-NLS-1$
      xmiElement.setAttribute("xmlns:CWM", "org.omg.xmi.namespace.CWM");
      xmiElement.setAttribute("xmlns:CWMMDB", "org.omg.xmi.namespace.CWMMDB");
      xmiElement.setAttribute("xmlns:CWMOLAP", "org.omg.xmi.namespace.CWMOLAP");
      xmiElement.setAttribute("xmlns:CWMRDB", "org.omg.xmi.namespace.CWMRDB");
      xmiElement.setAttribute("xmlns:CWMTFM", "org.omg.xmi.namespace.CWMTFM");
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
      xmiElement.setAttribute("timestamp", sdf.format(date));
      xmiElement.setAttribute("xmi.version", "1.2");
      doc.appendChild(xmiElement);
      Element xmiHeader = doc.createElement("XMI.header");
      xmiElement.appendChild(xmiHeader);
      Element xmiDocumentation = doc.createElement("XMI.documentation");
      xmiHeader.appendChild(xmiDocumentation);
      addTextElement(doc, xmiDocumentation, "XMI.exporter", "Pentaho XMI Generator");
      addTextElement(doc, xmiDocumentation, "XMI.exporterVersion", "1.0");
      
      Element xmiContent = doc.createElement("XMI.content");
      xmiElement.appendChild(xmiContent);
      
      // first add concepts, the order is ???
//      Map<Concept, String> conceptToId = new HashMap<Concept, String>();
      List<Element> allDescriptions = new ArrayList<Element>();

      int id = 1;
      
      for (Concept concept : domain.getConcepts()) {
        /*
             <CWM:Class isAbstract="false" name="Date" xmi.id="a1">
      <CWM:ModelElement.taggedValue>
        <CWM:TaggedValue tag="CONCEPT_PARENT_NAME" value="Base" xmi.id="a2"/>
      </CWM:ModelElement.taggedValue>
    </CWM:Class>

         */
        Element cwmClass = doc.createElement("CWM:Class");
        cwmClass.setAttribute("isAbstract", "false");
        cwmClass.setAttribute("name", concept.getId());
        String idstr = "a" + id++;

        id = createDescriptions(doc, concept, "CWM:Class", idstr, allDescriptions, id);
        
        cwmClass.setAttribute("xmi.id", idstr);
        
        if (concept.getParentConcept() != null) {
          Element modelElement = doc.createElement("CWM:ModelElement.taggedValue");
          modelElement.appendChild(createTaggedValue(doc, "CONCEPT_PARENT_NAME", concept.getParentConcept().getId(), "a" + id++));
          cwmClass.appendChild(modelElement);
        }
        
        xmiContent.appendChild(cwmClass);
      }
      
      // Description
      
      
      Element beforeDesc = null;

      
      // Event Support
      
      Element eventModelElement = null;
      for (String key : domain.getChildProperties().keySet()) {
        if (key.startsWith("LEGACY_EVENT_")) {
          // if any keys event, create a model element
          if (eventModelElement == null) {
            eventModelElement = doc.createElement("CWM:ModelElement.taggedValue");
          }
          String shortkey = key.substring("LEGACY_EVENT_".length());
          eventModelElement.appendChild(createTaggedValue(doc, shortkey, (String)domain.getChildProperties().get(key), "a" + id++));
        }
      }
      // only add cwm:event if one or more keys exist
      if (eventModelElement != null) {
        Element event = doc.createElement("CWM:Event");
        event.setAttribute("xmi.id", "a" + id++);
        event.setAttribute("name", "SECURITY_SERVICE");
        event.appendChild(eventModelElement);
        xmiContent.appendChild(event);
      }
      
      // Parameter / Locale info
      int val = 1;
      for (LocaleType localeType : domain.getLocales()) {
        Element cwmParameter = doc.createElement("CWM:Parameter");
        if (beforeDesc == null) {
          beforeDesc = cwmParameter;
        }
        cwmParameter.setAttribute("name", localeType.getCode());
        cwmParameter.setAttribute("xmi.id", "a" + id++);
        Element modelElement = doc.createElement("CWM:ModelElement.taggedValue");
        modelElement.appendChild(createTaggedValue(doc, "LOCALE_IS_DEFAULT", "" + ((val == 1) ? "Y" : "N"), "a" + id++));
        modelElement.appendChild(createTaggedValue(doc, "LOCALE_ORDER", "" + val++, "a" + id++));
        modelElement.appendChild(createTaggedValue(doc, "LOCALE_DESCRIPTION", localeType.getDescription(), "a" + id++));
        cwmParameter.appendChild(modelElement);
        xmiContent.appendChild(cwmParameter);
      }
      
      // CWMOLAP:Schema elements don't get converted
      
      // CWMRDB:Catalog: Data Source objects
      // TODO: We really need the thin version of DBMeta to accomplish this,
      // or we could store all the stuff in __LEGACY_ properties
      
      for (IPhysicalModel model : domain.getPhysicalModels()) {
        if (model instanceof SqlPhysicalModel) {
          SqlPhysicalModel sqlModel = (SqlPhysicalModel)model;
          SqlDataSource datasource = sqlModel.getDatasource();
          Element catalog = doc.createElement("CWMRDB:Catalog");
          catalog.setAttribute("name", model.getId());
          catalog.setAttribute("xmi.id", "a" + id++);
          Element modelElement = doc.createElement("CWM:ModelElement.taggedValue");

          modelElement.appendChild(createTaggedValue(doc, "DATABASE_TYPE", datasource.getDialectType(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "DATABASE_ACCESS", datasource.getType().toString(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "DATABASE_DATABASE", datasource.getDatabaseName(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "DATABASE_SERVER", datasource.getHostname(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "DATABASE_PORT", datasource.getPort(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "DATABASE_USERNAME", datasource.getUsername(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "DATABASE_PASSWORD", datasource.getPassword(), "a" + id++));
          
          for (String attribute : datasource.getAttributes().keySet()) {
            modelElement.appendChild(createTaggedValue(doc, CWM.TAG_DATABASE_ATTRIBUTE_PREFIX + attribute, datasource.getAttributes().get(attribute), "a" + id++));
          }
          
          catalog.appendChild(modelElement);
          xmiContent.appendChild(catalog);
        } else {
          // we do not support CSV to XMI yet
        }
      }
      
      // CWMRDB:Table: physicalTables
      
      for (IPhysicalModel model : domain.getPhysicalModels()) {
        if (model instanceof SqlPhysicalModel) {
          SqlPhysicalModel sqlModel = (SqlPhysicalModel)model;
          for (SqlPhysicalTable table : sqlModel.getPhysicalTables()) {
            Element cwmRdbTable = doc.createElement("CWMRDB:Table");
            cwmRdbTable.setAttribute("isAbstract", "false");
            cwmRdbTable.setAttribute("isSystem", "false");
            cwmRdbTable.setAttribute("isTemporary", "false");
            cwmRdbTable.setAttribute("name", table.getId());
            String idstr = "a" + id++;
            cwmRdbTable.setAttribute("xmi.id", idstr);
            id = createDescriptions(doc, table, "CWMRDB:Table", idstr, allDescriptions, id);

            Element modelElement = doc.createElement("CWM:ModelElement.taggedValue");
            modelElement.appendChild(createTaggedValue(doc, "TABLE_TARGET_DATABASE_NAME", model.getId(), "a" + id++));
            cwmRdbTable.appendChild(modelElement);

            Element ownedElement = doc.createElement("CWM:Namespace.ownedElement");
            for (IPhysicalColumn column : table.getPhysicalColumns()) {
              SqlPhysicalColumn sqlColumn = (SqlPhysicalColumn)column;
              Element rdbColumn = doc.createElement("CWMRDB:Column");
              rdbColumn.setAttribute("name", sqlColumn.getId());
              idstr = "a" + id++;
              rdbColumn.setAttribute("xmi.id", idstr);
              id = createDescriptions(doc, column, "CWMRDB:Column", idstr, allDescriptions, id);
              if (sqlColumn.getParentConcept() != null) {
                modelElement = doc.createElement("CWM:ModelElement.taggedValue");
                modelElement.appendChild(createTaggedValue(doc, "CONCEPT_PARENT_NAME", sqlColumn.getParentConcept().getId(), "a" + id++));
                rdbColumn.appendChild(modelElement);
              }
              ownedElement.appendChild(rdbColumn);
            }
            cwmRdbTable.appendChild(ownedElement);            
            xmiContent.appendChild(cwmRdbTable);
          }
        }
      }

      // CWMMDB:Schema: logical categories
      
      for (LogicalModel model : domain.getLogicalModels()) {
        Element mdbSchema = doc.createElement("CWMMDB:Schema");
        mdbSchema.setAttribute("name", model.getId());
        String idstr = "a" + id++;
        mdbSchema.setAttribute("xmi.id", idstr);
        id = createDescriptions(doc, model, "CWMMDB:Schema", idstr, allDescriptions, id);
        
        Element ownedElement = doc.createElement("CWM:Namespace.ownedElement");
        mdbSchema.appendChild(ownedElement);
        for (Category category : model.getCategories()) {
          Element extent = doc.createElement("CWM:Extent");
          extent.setAttribute("name", category.getId());
          idstr = "a" + id++;
          extent.setAttribute("xmi.id", idstr);
          id = createDescriptions(doc, category, "CWM:Extent", idstr, allDescriptions, id);
          Element modelElement = doc.createElement("CWM:ModelElement.taggedValue");
          modelElement.appendChild(createTaggedValue(doc, "BUSINESS_CATEGORY_ROOT", "Y", "a" + id++));
          extent.appendChild(modelElement);
          Element cOwnedElement = doc.createElement("CWM:Namespace.ownedElement");
          extent.appendChild(cOwnedElement);
          for (LogicalColumn col : category.getLogicalColumns()) {
            Element attribute = doc.createElement("CWM:Attribute");
            attribute.setAttribute("name", col.getId());
            attribute.setAttribute("xmi.id", "a" + id++);
            modelElement = doc.createElement("CWM:ModelElement.taggedValue");
            modelElement.appendChild(createTaggedValue(doc, "BUSINESS_CATEGORY_TYPE", "Column", "a" + id++));
            attribute.appendChild(modelElement);
            cOwnedElement.appendChild(attribute);
          }
          
          ownedElement.appendChild(extent);
        }
        
        for (LogicalRelationship rel : model.getLogicalRelationships()) {
          Element keyRel = doc.createElement("CWM:KeyRelationship");
          keyRel.setAttribute("xmi.id", "a" + id++);
          Element modelElement = doc.createElement("CWM:ModelElement.taggedValue");
          keyRel.appendChild(modelElement);
          modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_TYPE", rel.getRelationshipType().getType(), "a" + id++));
          // check for nulls?
          modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_FIELDNAME_CHILD", rel.getToColumn().getId(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_FIELDNAME_PARENT", rel.getFromColumn().getId(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_TABLENAME_CHILD", rel.getToTable().getId(), "a" + id++));
          modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_TABLENAME_PARENT", rel.getFromTable().getId(), "a" + id++));
          if (rel.isComplex()) {
            modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_IS_COMPLEX", "Y", "a" + id++));
            modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_COMPLEX_JOIN", rel.getComplexJoin(), "a" + id++));
          }
          if (rel.getDescription() != null) {
            for (String locale : rel.getDescription().getLocales()) {
              modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_DESCRIPTION", rel.getDescription(locale), "a" + id++));
              break;
            }
          }
          if (rel.getJoinOrderKey() != null) {
            modelElement.appendChild(createTaggedValue(doc, "RELATIONSHIP_JOIN_ORDER_KEY", rel.getJoinOrderKey(), "a" + id++));
          }
          ownedElement.appendChild(keyRel);
        }
        
        Element sdo = doc.createElement("CWMMDB:Schema.dimensionedObject");
        Element sd = doc.createElement("CWMMDB:Schema.dimension");
        mdbSchema.appendChild(sdo);
        mdbSchema.appendChild(sd);
        for (LogicalTable table : model.getLogicalTables()) {
          Element dim = doc.createElement("CWMMDB:Dimension");
          sd.appendChild(dim);
          dim.setAttribute("isAbstract", "false");
          dim.setAttribute("name", table.getId());
          String tblidstr = "a" + id++;
          dim.setAttribute("xmi.id", tblidstr);
          id = createDescriptions(doc, table, "CWMMDB:Dimension", tblidstr, allDescriptions, id);
          Element modelElement = doc.createElement("CWM:ModelElement.taggedValue");
          if (table.getProperty("__LEGACY_TABLE_IS_DRAWN") != null) {
            modelElement.appendChild(createTaggedValue(doc, "TABLE_IS_DRAWN", (String)table.getProperty("__LEGACY_TABLE_IS_DRAWN"), "a" + id));
          }
          if (table.getProperty("__LEGACY_TAG_POSITION_Y") != null) {
            modelElement.appendChild(createTaggedValue(doc, "TAG_POSITION_Y", (String)table.getProperty("__LEGACY_TAG_POSITION_Y"), "a" + id));
          }
          if (table.getProperty("__LEGACY_TAG_POSITION_X") != null) {
            modelElement.appendChild(createTaggedValue(doc, "TAG_POSITION_X", (String)table.getProperty("__LEGACY_TAG_POSITION_X"), "a" + id));
          }
          
          modelElement.appendChild(createTaggedValue(doc, "BUSINESS_TABLE_PHYSICAL_TABLE_NAME", table.getPhysicalTable().getId(), "a" + id));
          dim.appendChild(modelElement);
          Element dimObjs = doc.createElement("CWMMDB:Dimension.dimensionedObject");
          dim.appendChild(dimObjs);
          
          for (LogicalColumn column : table.getLogicalColumns()) {
            Element dimObj = doc.createElement("CWMMDB:DimensionedObject");
            sdo.appendChild(dimObj);
            dimObj.setAttribute("name", column.getId());
            idstr = "a" + id++;
            id = createDescriptions(doc, column, "CWMMDB:DimensionedObject", idstr, allDescriptions, id);
            dimObj.setAttribute("xmi.id", idstr);
            modelElement = doc.createElement("CWM:ModelElement.taggedValue");
            modelElement.appendChild(createTaggedValue(doc, "BUSINESS_COLUMN_BUSINESS_TABLE", column.getLogicalTable().getId(), "a" + id++));
            modelElement.appendChild(createTaggedValue(doc, "BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME", column.getPhysicalColumn().getId(), "a" + id++));
            dimObj.appendChild(modelElement);
            /*
            <CWMMDB:DimensionedObject.dimension>
              <CWMMDB:Dimension xmi.idref="a23"/>
            </CWMMDB:DimensionedObject.dimension>
             */
            
            Element parentRoot = doc.createElement("CWMMDB:DimensionedObject.dimension");
            Element parent = doc.createElement("CWMMDB:Dimension");
            parent.setAttribute("xmi.idref", tblidstr);
            dimObj.appendChild(parentRoot);
            parentRoot.appendChild(parent);
            
            // CWMMDB:DimensionedObject xmi.idref="a1365"/>
            Element dimObjLink = doc.createElement("CWMMDB:DimensionedObject");
            dimObjLink.setAttribute("xmi.idref", idstr);
            dimObjs.appendChild(dimObjLink);
          }
        }
        
        xmiContent.appendChild(mdbSchema);
      }
      
      for (Element element : allDescriptions) {
        xmiContent.insertBefore(element, beforeDesc);
      }
      
      return doc;
    } catch (Exception e) {
      logger.error(Messages.getErrorString("QueryXmlHelper.ERROR_0002_TO_DOCUMENT_FAILED"), e); //$NON-NLS-1$
    }
    return null;

  }
  
  @SuppressWarnings("unchecked")
  protected int createDescriptions(Document doc, IConcept concept, String parentTag, String idstr, List<Element> allDescriptions, int id) {
    for (String key : concept.getChildProperties().keySet()) {

      String body = null;
      String type = null;
      
      Object val = concept.getChildProperty(key);
      /*
           <CWM:Description body="POSTALCODE" name="formula" type="String" xmi.id="a927">
      <CWM:Description.modelElement>
        <CWMRDB:Column xmi.idref="a922"/>
      </CWM:Description.modelElement>
    </CWM:Description>
       */
      if (val instanceof String) {
        if (key.equals(SqlPhysicalColumn.TARGET_COLUMN)) {
          key = "formula";
        }
        String str = (String)val;
        body = str;
        type = "String";
      } else if (val instanceof Boolean) {
        Boolean bool = (Boolean)val;
        type = "Boolean";
        body = bool.booleanValue() ? "Y" : "N";
      } else if (val instanceof Color) {
        Color c = (Color)val;
        ColorSettings cs = new ColorSettings(c.getRed(), c.getGreen(), c.getBlue());
        body = cs.toString();
        type = "Color";
      } else if (val instanceof URL) {
        body = val.toString();
        type = "URL";
      } else if (val instanceof org.pentaho.metadata.model.concept.types.ColumnWidth) {
        org.pentaho.metadata.model.concept.types.ColumnWidth ncw = (org.pentaho.metadata.model.concept.types.ColumnWidth)val;
        type = "ColumnWidth";
        ColumnWidth cw = new ColumnWidth(ncw.getType().ordinal(), ncw.getWidth());
        //             ColumnWidth cw = ColumnWidth.fromString(body);
//        WidthType cwt = WidthType.values()[cw.getType()];
//        org.pentaho.metadata.model.concept.types.ColumnWidth ncw = new org.pentaho.metadata.model.concept.types.ColumnWidth(cwt, cw.getWidth().doubleValue());
//        concept.setProperty(name, ncw);
        body = cw.toString();
      } else if (val instanceof Double) {
        type = "Number";
        BigDecimal bd = new BigDecimal((Double)val);
        body = bd.toString();
      } else if (val instanceof Alignment) {
//      AlignmentSettings alignment = (AlignmentSettings)property.getValue();
//      return Alignment.values()[alignment.getType()];
        Alignment alignment = (Alignment)val;
        AlignmentSettings as = AlignmentSettings.types[alignment.ordinal()];
        body = as.toString();
        type = "Alignment";
      } else if (val instanceof org.pentaho.metadata.model.concept.security.Security) {
        org.pentaho.metadata.model.concept.security.Security security = (org.pentaho.metadata.model.concept.security.Security)val;
        Map<org.pentaho.pms.schema.security.SecurityOwner, Integer> map = new HashMap<org.pentaho.pms.schema.security.SecurityOwner, Integer>();
        for (SecurityOwner owner : security.getOwners()) {
          org.pentaho.pms.schema.security.SecurityOwner ownerObj = new org.pentaho.pms.schema.security.SecurityOwner(owner.getOwnerType().ordinal(), owner.getOwnerName()); 
          map.put(ownerObj, security.getOwnerRights(owner));
        }
        Security legacySecurity = new Security(map);
        body = legacySecurity.toXML();
        type = "Security";
      } else if (val instanceof RowLevelSecurity) {
        RowLevelSecurity nrls = (RowLevelSecurity)val;
        org.pentaho.pms.schema.security.RowLevelSecurity rls = new org.pentaho.pms.schema.security.RowLevelSecurity();
        rls.setType(Type.values()[nrls.getType().ordinal()]);
        rls.setGlobalConstraint(nrls.getGlobalConstraint());
        Map<org.pentaho.pms.schema.security.SecurityOwner, String> roleBasedConstraintMap = new HashMap<org.pentaho.pms.schema.security.SecurityOwner, String>();
        for (SecurityOwner owner : nrls.getRoleBasedConstraintMap().keySet()) {
          org.pentaho.pms.schema.security.SecurityOwner ownerObj = new org.pentaho.pms.schema.security.SecurityOwner(owner.getOwnerType().ordinal(), owner.getOwnerName()); 
          roleBasedConstraintMap.put(ownerObj, nrls.getRoleBasedConstraintMap().get(owner));
        }
        rls.setRoleBasedConstraintMap(roleBasedConstraintMap);
        
        body = rls.toXML();
        type = "RowLevelSecurity";
      } else if (val instanceof Font) {
        ConceptPropertyFont font = (ConceptPropertyFont)ThinModelConverter.convertPropertyToLegacy("font", val);
        body = ((FontSettings)font.getValue()).toString();
        type = "Font";
      } else if (val instanceof TargetTableType) {
        TargetTableType ttt = (TargetTableType)val;
        if (ttt == TargetTableType.TABLE) {
          // do nothing
        } else {
          type = "TargetTableType";
          body = ttt.toString();
        }
      } else if (val instanceof TableType) {
        TableType tt = (TableType)val;
        body = TableTypeSettings.getTypeDescriptions()[tt.ordinal()];
        type = "TableType";
        // concept.setProperty(name, TableType.values()[TableTypeSettings.getType(body).getType()]);
      } else if (val instanceof LocalizedString) {
        // need to add description for each locale
        LocalizedString lstr = (LocalizedString)val;
        for (String locale : lstr.getLocales()) {
          createDescription(doc, lstr.getLocalizedString(locale), key, "LocString", locale, id++, parentTag, idstr, allDescriptions);
        }
      } else if (val instanceof TargetColumnType) {
        TargetColumnType tct = (TargetColumnType)val;
        body = tct == TargetColumnType.OPEN_FORMULA ? "Y": "N";
        key = "exact";
        type = "Boolean";
      } else if (val instanceof FieldType) {
        FieldType ft = (FieldType)val;
        // concept.setProperty(name, FieldType.values()[FieldTypeSettings.getType(body).getType()]);
        body = FieldTypeSettings.getTypeDescriptions()[ft.ordinal()];
        type = "FieldType";
      } else if (val instanceof DataType) {
        body = DataTypeSettings.types[((DataType)val).ordinal()].getCode();
        type = "DataType";
      } else if (val instanceof AggregationType) {
        AggregationType at = (AggregationType)val;
        body = AggregationSettings.types[at.ordinal()].getCode();
        type = "Aggregation";
      } else if (val instanceof List) {
        List objs = (List)val;
        if (objs.size() == 0) {
          // assume this is an agg list
          ConceptPropertyAggregationList list = new ConceptPropertyAggregationList(key, new ArrayList<AggregationSettings>());
          type = "AggregationList";
          body = list.toXML();
        } else {
          if (objs.get(0) instanceof AggregationType) {
            List<AggregationType> aggTypes = (List<AggregationType>)objs;
            type = "AggregationList";
            List<AggregationSettings> aggSettings = new ArrayList<AggregationSettings>();
            for (AggregationType aggType : aggTypes) {
              aggSettings.add(AggregationSettings.types[aggType.ordinal()]);
            }
            ConceptPropertyAggregationList list = new ConceptPropertyAggregationList(key, aggSettings);
            type = "AggregationList";
            body = list.toXML();
          } else {
            logger.error("Unsupported Concept Property List: " + objs.get(0).getClass());
          }
        }
      } else {
        logger.error("Unsupported Concept Property: " + val.getClass());
      }
      if (type != null) {
        createDescription(doc, body, key, type, null, id++, parentTag, idstr, allDescriptions);
      }
    }
    return id;
  }
  
  protected void createDescription(Document doc, String body, String key, String type, String locale, int id, String parentTag, String idstr, List<Element> allDescriptions) {
    Element desc = doc.createElement("CWM:Description");
    desc.setAttribute("body", body);
    if (locale != null) {
      desc.setAttribute("language", locale);
    }
    desc.setAttribute("name", key);
    desc.setAttribute("type", type);
    desc.setAttribute("xmi.id", "a" + id++);
    Element modelElement = doc.createElement("CWM:Description.modelElement");
    Element parent = doc.createElement(parentTag);
    modelElement.appendChild(parent);
    parent.setAttribute("xmi.idref", idstr);
    desc.appendChild(modelElement);
    allDescriptions.add(desc);    
  }
  
  protected Element createTaggedValue(Document doc, String tagName, String value, String id) {
    Element taggedValue = doc.createElement("CWM:TaggedValue");
    taggedValue.setAttribute("tag", tagName);
    taggedValue.setAttribute("value", value);
    taggedValue.setAttribute("xmi.id", id);
    return taggedValue;
  }
  
  protected void addTextElement(Document doc, Element element, String elementName, String text) {
    Element childElement = doc.createElement(elementName); //$NON-NLS-1$
    childElement.appendChild(doc.createTextNode(text)); //$NON-NLS-1$ //$NON-NLS-2$
    element.appendChild(childElement);
  }
  
  
  /**
   * @param xmi
   * @return
   * @throws PentahoMetadataException
   */
  public Domain parseXmi(InputStream xmi) throws Exception {
    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    Document doc;

    // Check and open XML document
    dbf = DocumentBuilderFactory.newInstance();
    try {
      db = dbf.newDocumentBuilder();
      doc = db.parse(new InputSource(xmi));
    } catch (ParserConfigurationException pcx) {
      throw new PentahoMetadataException(pcx);
    } catch (SAXException sex) {
      throw new PentahoMetadataException(sex);
    } catch (IOException iex) {
      throw new PentahoMetadataException(iex);
    }
    Element content = null;
    NodeList list = doc.getElementsByTagName("XMI.content");
    for (int i = 0; i < list.getLength(); i++) {
      content = (Element)list.item(i);
      break;
    }
    
    // skipping CWM:Event = Security Service (skip for now)
    // skipping CWMOLAP:Schema = not populated, name of business view?
    
    List<Element> concepts = new ArrayList<Element>();
    List<Element> descriptions = new ArrayList<Element>();
    List<Element> datasources = new ArrayList<Element>();
    List<Element> physicalTables = new ArrayList<Element>();
    List<Element> parameters = new ArrayList<Element>();
    List<Element> schemas = new ArrayList<Element>();
    List<Element> events = new ArrayList<Element>();
    list = content.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        if (node.getNodeName().equals("CWM:Class")) {
          concepts.add((Element)node);
        } else if (node.getNodeName().equals("CWM:Parameter")) {
          parameters.add((Element)node);
        } else if (node.getNodeName().equals("CWMRDB:Catalog")) {
          datasources.add((Element)node);
        } else if (node.getNodeName().equals("CWMRDB:Table")) {
          physicalTables.add((Element)node);
        } else if (node.getNodeName().equals("CWMMDB:Schema")) {
          schemas.add((Element)node);
        } else if (node.getNodeName().equals("CWM:Description")) {
          descriptions.add((Element)node);
        } else if (node.getNodeName().equals("CWM:Event")) {
          events.add((Element)node);
        } else {
          logger.debug("IGNORED: " + node.getNodeName());
        }
      }
    }
    
    Domain domain = new Domain();
    Map<String, Concept> xmiConceptMap = new HashMap<String, Concept>();
    
    for (Element event : events) {
      Map<String, String> kvp = getKeyValuePairs(event, "CWM:TaggedValue", "tag", "value");
      for (String key : kvp.keySet()) {
        domain.setProperty("LEGACY_EVENT_" + key, kvp.get(key));
      }
    }
    populateLocales(domain, parameters);
    
    for (Element concept : concepts) {
      /*
           <CWM:Class isAbstract="false" name="Date" xmi.id="a1">
      <CWM:ModelElement.taggedValue>
        <CWM:TaggedValue tag="CONCEPT_PARENT_NAME" value="Base" xmi.id="a2"/>
      </CWM:ModelElement.taggedValue>
    </CWM:Class>
       */
      Concept c = new Concept();
      String name = concept.getAttribute("name");
      c.setId(name);
      String xmiId = concept.getAttribute("xmi.id");
      String parentName = getKeyValue(concept, "CWM:TaggedValue", "tag", "value", "CONCEPT_PARENT_NAME");
      if (parentName != null) {
        c.setProperty("__TMP_CONCEPT_PARENT_NAME", parentName);
      }
      xmiConceptMap.put(xmiId, c);
      domain.addConcept(c);
    }
    
    // second pass to bind parents to children
    for (Concept concept : domain.getConcepts()) {
      String parentName = (String)concept.getChildProperty("__TMP_CONCEPT_PARENT_NAME");
      if (parentName != null) {
        concept.removeChildProperty("__TMP_CONCEPT_PARENT_NAME");
        concept.setParentConcept(domain.findConcept(parentName));
      }
      
    }
    
    for (Element datasource : datasources) {
      /*
           <CWMRDB:Catalog name="SampleData" xmi.id="a1165">
      <CWM:ModelElement.taggedValue>

        <CWM:TaggedValue tag="DATABASE_INDEX_TABLESPACE" value="" xmi.id="a1173"/>
        <CWM:TaggedValue tag="DATABASE_DATA_TABLESPACE" value="" xmi.id="a1174"/>
        <CWM:TaggedValue tag="DATABASE_SERVERNAME" value="" xmi.id="a1175"/>
        <CWM:TaggedValue tag="DATABASE_PASSWORD" value="" xmi.id="a1176"/>
        <CWM:TaggedValue tag="DATABASE_USERNAME" value="" xmi.id="a1177"/>
        <CWM:TaggedValue tag="DATABASE_PORT" value="" xmi.id="a1178"/>
        <CWM:TaggedValue tag="DATABASE_DATABASE" value="SampleData" xmi.id="a1179"/>
        <CWM:TaggedValue tag="DATABASE_ACCESS" value="JNDI" xmi.id="a1180"/>
        <CWM:TaggedValue tag="DATABASE_TYPE" value="HYPERSONIC" xmi.id="a1181"/>
        <CWM:TaggedValue tag="DATABASE_SERVER" value="localhost" xmi.id="a1182"/>
      </CWM:ModelElement.taggedValue>
    </CWMRDB:Catalog>
    
    
        <CWM:TaggedValue xmi.id = 'a700' tag = 'DATABASE_SERVER' value = 'localhost'/>
        <CWM:TaggedValue xmi.id = 'a701' tag = 'DATABASE_TYPE' value = 'MYSQL'/>
        <CWM:TaggedValue xmi.id = 'a702' tag = 'DATABASE_ACCESS' value = 'Native'/>
        <CWM:TaggedValue xmi.id = 'a703' tag = 'DATABASE_DATABASE' value = 'foodmart'/>
        <CWM:TaggedValue xmi.id = 'a704' tag = 'DATABASE_PORT' value = '3306'/>
        <CWM:TaggedValue xmi.id = 'a705' tag = 'DATABASE_USERNAME' value = 'foodmart'/>
        <CWM:TaggedValue xmi.id = 'a706' tag = 'DATABASE_PASSWORD' value = 'foodmart'/>
        <CWM:TaggedValue xmi.id = 'a707' tag = 'DATABASE_SERVERNAME'/>
        <CWM:TaggedValue xmi.id = 'a708' tag = 'DATABASE_DATA_TABLESPACE'/>
        <CWM:TaggedValue xmi.id = 'a709' tag = 'DATABASE_INDEX_TABLESPACE'/>
        
        
        <CWM:TaggedValue xmi.id = 'a710' tag = 'DATABASE_ATTRIBUTE_PREFIX_EXTRA_OPTION_MYSQL.useCursorFetch' value = 'true'/>
        <CWM:TaggedValue xmi.id = 'a711' tag = 'DATABASE_ATTRIBUTE_PREFIX_USE_POOLING' value = 'N'/>
        <CWM:TaggedValue xmi.id = 'a712' tag = 'DATABASE_ATTRIBUTE_PREFIX_IS_CLUSTERED' value = 'N'/>
        <CWM:TaggedValue xmi.id = 'a713' tag = 'DATABASE_ATTRIBUTE_PREFIX_STREAM_RESULTS' value = 'Y'/>
        <CWM:TaggedValue xmi.id = 'a714' tag = 'DATABASE_ATTRIBUTE_PREFIX_EXTRA_OPTION_MYSQL.defaultFetchSize' value = '500'/>
        <CWM:TaggedValue xmi.id = 'a715' tag = 'DATABASE_ATTRIBUTE_PREFIX_PORT_NUMBER' value = '3306'/>
        <CWM:TaggedValue xmi.id = 'a716' tag = 'DATABASE_ATTRIBUTE_PREFIX_FORCE_IDENTIFIERS_TO_UPPERCASE' value = 'Y'/>
        <CWM:TaggedValue xmi.id = 'a717' tag = 'DATABASE_ATTRIBUTE_PREFIX_FORCE_IDENTIFIERS_TO_LOWERCASE' value = 'Y'/>
        <CWM:TaggedValue xmi.id = 'a718' tag = 'DATABASE_ATTRIBUTE_PREFIX_QUOTE_ALL_FIELDS' value = 'Y'/>
        <CWM:TaggedValue xmi.id = 'a719' tag = 'DATABASE_JDBC_URL' value = 'jdbc:mysql://localhost:3306/foodmart?defaultFetchSize=500&amp;useCursorFetch=true'/>
       */
      SqlPhysicalModel sqlPhysicalModel = new SqlPhysicalModel();
      domain.addPhysicalModel(sqlPhysicalModel);
      SqlDataSource sqlDataSource = new SqlDataSource();
      sqlPhysicalModel.setDatasource(sqlDataSource);
      
      String name = datasource.getAttribute("name");
      sqlPhysicalModel.setId(name);
      Map<String, String> kvp = getKeyValuePairs(datasource, "CWM:TaggedValue", "tag", "value");
      
      sqlDataSource.setType(DataSourceType.values()[DatabaseMeta.getAccessType(kvp.get("DATABASE_ACCESS"))]);
      sqlDataSource.setDatabaseName(kvp.get("DATABASE_DATABASE"));
      sqlDataSource.setHostname(kvp.get("DATABASE_SERVER"));
      sqlDataSource.setPort(kvp.get("DATABASE_PORT"));
      sqlDataSource.setUsername(kvp.get("DATABASE_USERNAME"));
      sqlDataSource.setPassword(kvp.get("DATABASE_PASSWORD"));
      sqlDataSource.setDialectType(kvp.get("DATABASE_TYPE"));
      
      // And now load the attributes...
      for (String tag : kvp.keySet()) {
          if (tag.startsWith(CWM.TAG_DATABASE_ATTRIBUTE_PREFIX)) {
              String key = tag.substring(CWM.TAG_DATABASE_ATTRIBUTE_PREFIX.length());
              String attribute = kvp.get(tag);
              // Add the attribute
              sqlDataSource.getAttributes().put(key, attribute);
          }
      }
    }
    
    for (Element physicalTable : physicalTables) {
      String name = physicalTable.getAttribute("name");
      Element tagged = null;
      Element owned = null;
      NodeList ptcn = physicalTable.getChildNodes();
      for (int i = 0; i < ptcn.getLength(); i++) {
        if (ptcn.item(i).getNodeType() == Node.ELEMENT_NODE) {
          if (ptcn.item(i).getNodeName().equals("CWM:ModelElement.taggedValue")) {
            tagged = (Element)ptcn.item(i);
          }
          if (ptcn.item(i).getNodeName().equals("CWM:Namespace.ownedElement")) {
            owned = (Element)ptcn.item(i);
          }
        }
      }
      String databaseName = getKeyValue(tagged, "CWM:TaggedValue", "tag", "value", "TABLE_TARGET_DATABASE_NAME");
      SqlPhysicalModel model = (SqlPhysicalModel)domain.findPhysicalModel(databaseName);
      SqlPhysicalTable table = new SqlPhysicalTable(model);
      table.setId(physicalTable.getAttribute("name"));
      xmiConceptMap.put(physicalTable.getAttribute("xmi.id"), table);
      model.addPhysicalTable(table);
      NodeList columns = owned.getElementsByTagName("CWMRDB:Column");
      for (int i = 0; i < columns.getLength(); i++) {
        Element colelement = (Element)columns.item(i);
        
        SqlPhysicalColumn col = new SqlPhysicalColumn(table);
        col.setId(colelement.getAttribute("name"));
        xmiConceptMap.put(colelement.getAttribute("xmi.id"), col);
        table.addPhysicalColumn(col);
        NodeList pccn = colelement.getChildNodes();
        for (int j = 0; j < pccn.getLength(); j++) {
          if (pccn.item(j).getNodeType() == Node.ELEMENT_NODE) {
            if (pccn.item(j).getNodeName().equals("CWM:ModelElement.taggedValue")) {
              tagged = (Element)pccn.item(j);
            }
          }
        }
        
        String conceptParentName = getKeyValue(tagged, "CWM:TaggedValue", "tag", "value", "CONCEPT_PARENT_NAME");
        if (conceptParentName != null) {
          Concept parent = domain.findConcept(conceptParentName);
          if (parent == null) {
            logger.error("failed to located concept : " + conceptParentName);
          } else {
            col.setParentConcept(parent);
          }
        }
      }
      
      /*
       <CWMRDB:Table isAbstract="false" isSystem="false" isTemporary="false" name="PT_TRIAL_BALANCE" xmi.id="a143">
      <CWM:ModelElement.taggedValue>
        <CWM:TaggedValue tag="TABLE_TARGET_DATABASE_NAME" value="SampleData" xmi.id="a1183"/>
      </CWM:ModelElement.taggedValue>
      <CWM:Namespace.ownedElement>
        <CWMRDB:Column name="Amount" xmi.id="a89">
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="CONCEPT_PARENT_NAME" value="Base" xmi.id="a1184"/>
          </CWM:ModelElement.taggedValue>
        </CWMRDB:Column>
        <CWMRDB:Column name="Detail" xmi.id="a98">
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="CONCEPT_PARENT_NAME" value="Base" xmi.id="a1185"/>
          </CWM:ModelElement.taggedValue>
        </CWMRDB:Column>
      </CWM:Namespace.ownedElement>
    </CWMRDB:Table>
       */
    }
    
    for (Element schema : schemas) {
      LogicalModel logicalModel = new LogicalModel();
      logicalModel.setId(schema.getAttribute("name"));
      xmiConceptMap.put(schema.getAttribute("xmi.id"), logicalModel);
      
      domain.addLogicalModel(logicalModel);
      
      Element dimension = null;
      Element dimensionedObject = null;
      Element ownedElement = null;
      NodeList schemaChildren = schema.getChildNodes();
      for (int i = 0; i < schemaChildren.getLength(); i++) {
        if (schemaChildren.item(i).getNodeType() == Node.ELEMENT_NODE) {
          if (schemaChildren.item(i).getNodeName().equals("CWMMDB:Schema.dimension")) {
            dimension = (Element)schemaChildren.item(i);
          } else if (schemaChildren.item(i).getNodeName().equals("CWMMDB:Schema.dimensionedObject")) {
            dimensionedObject = (Element)schemaChildren.item(i);
          } else if (schemaChildren.item(i).getNodeName().equals("CWM:Namespace.ownedElement")) {
            ownedElement = (Element)schemaChildren.item(i);
          } else {
            System.out.println("SCHEMA IGNORED: " + schemaChildren.item(i).getNodeName());
          }
        }
      }
      
      // first read all biz tables
      NodeList bizTables = dimension.getElementsByTagName("CWMMDB:Dimension");
      for (int i = 0; i < bizTables.getLength(); i++) {
        Element biztable = (Element)bizTables.item(i);
        LogicalTable table = new LogicalTable();
        table.setId(biztable.getAttribute("name"));
        Map<String, String> nvp = getKeyValuePairs(biztable, "CWM:TaggedValue", "tag", "value");
        String pt = nvp.get("BUSINESS_TABLE_PHYSICAL_TABLE_NAME");
        table.setPhysicalTable(domain.findPhysicalTable(pt));
        // store legacy values
        if (nvp.containsKey("TABLE_IS_DRAWN")) {
          table.setProperty("__LEGACY_TABLE_IS_DRAWN", nvp.get("TABLE_IS_DRAWN"));
        }
        if (nvp.containsKey("TAG_POSITION_Y")) {
          table.setProperty("__LEGACY_TAG_POSITION_Y", nvp.get("TAG_POSITION_Y"));
        }
        if (nvp.containsKey("TAG_POSITION_X")) {
          table.setProperty("__LEGACY_TAG_POSITION_X", nvp.get("TAG_POSITION_X"));
        }
        xmiConceptMap.put(biztable.getAttribute("xmi.id"), table);
        logicalModel.addLogicalTable(table);
        /*
         <CWMMDB:Dimension isAbstract="false" name="BT_EMPLOYEES_EMPLOYEES" xmi.id="a21"> <- Biz table
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="TABLE_IS_DRAWN" value="Y" xmi.id="a1396"/>
            <CWM:TaggedValue tag="TAG_POSITION_Y" value="151" xmi.id="a1397"/>
            <CWM:TaggedValue tag="TAG_POSITION_X" value="213" xmi.id="a1398"/>
            <CWM:TaggedValue tag="BUSINESS_TABLE_PHYSICAL_TABLE_NAME" value="PT_EMPLOYEES" xmi.id="a1399"/>
          </CWM:ModelElement.taggedValue>
          <CWMMDB:Dimension.dimensionedObject>
            <CWMMDB:DimensionedObject xmi.idref="a1365"/>
            <CWMMDB:DimensionedObject xmi.idref="a1362"/>
          </CWMMDB:Dimension.dimensionedObject>
        </CWMMDB:Dimension>
         */
      }
            
      // second read all biz cols
      NodeList bizcols = dimensionedObject.getElementsByTagName("CWMMDB:DimensionedObject");
      for (int i = 0; i < bizcols.getLength(); i++) {
        /*
         <CWMMDB:DimensionedObject name="BC_EMPLOYEES_JOBTITLE" xmi.id="a1344">
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="BUSINESS_COLUMN_BUSINESS_TABLE" value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1345"/>
            <CWM:TaggedValue tag="BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME" value="JOBTITLE" xmi.id="a1346"/>
          </CWM:ModelElement.taggedValue>
          <CWMMDB:DimensionedObject.dimension>
            <CWMMDB:Dimension xmi.idref="a21"/>
          </CWMMDB:DimensionedObject.dimension>
         </CWMMDB:DimensionedObject>
         */
        Element bizcol = (Element)bizcols.item(i);
        LogicalColumn col = new LogicalColumn();
        col.setId(bizcol.getAttribute("name"));
        xmiConceptMap.put(bizcol.getAttribute("xmi.id"), col);
        
        Map<String, String> nvp = getKeyValuePairs(bizcol, "CWM:TaggedValue", "tag", "value");
        String biztbl = nvp.get("BUSINESS_COLUMN_BUSINESS_TABLE");
        String pcol = nvp.get("BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME");
        LogicalTable parent = logicalModel.findLogicalTable(biztbl);
        col.setLogicalTable(parent);
        parent.addLogicalColumn(col);
        for (IPhysicalColumn phycol : parent.getPhysicalTable().getPhysicalColumns()) {
          if (phycol.getId().equals(pcol)) {
            col.setPhysicalColumn(phycol);
            break;
          }
        }
      }
      
      // third read categories 
      NodeList categories = ownedElement.getElementsByTagName("CWM:Extent");
      for (int i = 0; i < categories.getLength(); i++) {
        /*
         <CWM:Extent name="BC_OFFICES_" xmi.id="a13"> <-- Category
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="BUSINESS_CATEGORY_ROOT" value="Y" xmi.id="a1304"/>
          </CWM:ModelElement.taggedValue>
          <CWM:Namespace.ownedElement>
            <CWM:Attribute name="BC_OFFICES_TERRITORY" xmi.id="a1305">
              <CWM:ModelElement.taggedValue>
                <CWM:TaggedValue tag="BUSINESS_CATEGORY_TYPE" value="Column" xmi.id="a1306"/>
              </CWM:ModelElement.taggedValue>
            </CWM:Attribute>
            <CWM:Attribute name="BC_OFFICES_POSTALCODE" xmi.id="a1307">
              <CWM:ModelElement.taggedValue>
                <CWM:TaggedValue tag="BUSINESS_CATEGORY_TYPE" value="Column" xmi.id="a1308"/>
              </CWM:ModelElement.taggedValue>
            </CWM:Attribute>
          </CWM:Namespace.ownedElement>
        </CWM:Extent>
         */
        
        Element category = (Element)categories.item(i);
        Category cat = new Category();
        cat.setId(category.getAttribute("name"));
        xmiConceptMap.put(category.getAttribute("xmi.id"), cat);
        NodeList columns = category.getElementsByTagName("CWM:Attribute");
        for (int j = 0; j < columns.getLength(); j++) {
          Element column = (Element)columns.item(j);
          String name = column.getAttribute("name");
          LogicalColumn col = logicalModel.findLogicalColumn(name);
          cat.addLogicalColumn(col);
        }
        
        logicalModel.addCategory(cat);
      }
      
      
      // fourth read relationships
      NodeList rels = ownedElement.getElementsByTagName("CWM:KeyRelationship");
      for (int i = 0; i < rels.getLength(); i++) {
        Element rel = (Element)rels.item(i);
        /*
        <CWM:KeyRelationship xmi.id="a1338">
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="RELATIONSHIP_TYPE" value="1:N" xmi.id="a1339"/>
            <CWM:TaggedValue tag="RELATIONSHIP_FIELDNAME_CHILD" value="BC_EMPLOYEES_OFFICECODE" xmi.id="a1340"/>
            <CWM:TaggedValue tag="RELATIONSHIP_FIELDNAME_PARENT" value="BC_OFFICES_OFFICECODE" xmi.id="a1341"/>
            <CWM:TaggedValue tag="RELATIONSHIP_TABLENAME_CHILD" value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1342"/>
            <CWM:TaggedValue tag="RELATIONSHIP_TABLENAME_PARENT" value="BT_OFFICES_OFFICES" xmi.id="a1343"/>
          </CWM:ModelElement.taggedValue>
        </CWM:KeyRelationship>
        */
        Map<String, String> nvp = getKeyValuePairs(rel, "CWM:TaggedValue", "tag", "value");
        LogicalRelationship relation = new LogicalRelationship();
        String type = nvp.get("RELATIONSHIP_TYPE");
        RelationshipType reltype = RelationshipType.values()[RelationshipMeta.getType(type)];
        relation.setRelationshipType(reltype);
        
        String tablechild = nvp.get("RELATIONSHIP_TABLENAME_CHILD"); // to
        String tableparent = nvp.get("RELATIONSHIP_TABLENAME_PARENT"); // from
        String fieldchild = nvp.get("RELATIONSHIP_FIELDNAME_CHILD");
        String fieldparent = nvp.get("RELATIONSHIP_FIELDNAME_PARENT");

        relation.setFromTable(logicalModel.findLogicalTable(tableparent));
        if (fieldparent != null) {
          relation.setFromColumn(logicalModel.findLogicalColumn(fieldparent));
        }
        relation.setToTable(logicalModel.findLogicalTable(tablechild));
        if (fieldchild != null) {
          relation.setToColumn(logicalModel.findLogicalColumn(fieldchild));
        }
        
        relation.setComplex("Y".equals(nvp.get("RELATIONSHIP_IS_COMPLEX")));
        String val = nvp.get(nvp.get("RELATIONSHIP_COMPLEX_JOIN"));
        if (val != null) {
          relation.setComplexJoin(val);
        }
        if (nvp.get("RELATIONSHIP_DESCRIPTION") != null) {
          LocalizedString str = new LocalizedString();
          String locale = null;
          if (domain.getLocales().size() >= 0) {
            locale = domain.getLocales().get(0).getCode();
          } else {
            locale = LocaleHelper.getDefaultLocale().toString();
          }
          str.setString(locale, nvp.get("RELATIONSHIP_DESCRIPTION"));
        }
        String joinOrderKey = nvp.get("RELATIONSHIP_JOIN_ORDER_KEY");
        if (joinOrderKey != null) {
          relation.setJoinOrderKey(joinOrderKey);
        }
        
        logicalModel.addLogicalRelationship(relation);
      }
      
      
      // fourth read categories
      
      
      // second read tables
      /*
      <CWMMDB:Schema name="BV_HUMAN_RESOURCES" xmi.id="a25">
      <CWM:Namespace.ownedElement>
        <CWM:Extent name="BC_OFFICES_" xmi.id="a13"> <-- Category
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="BUSINESS_CATEGORY_ROOT" value="Y" xmi.id="a1304"/>
          </CWM:ModelElement.taggedValue>
          <CWM:Namespace.ownedElement>
            <CWM:Attribute name="BC_OFFICES_TERRITORY" xmi.id="a1305">
              <CWM:ModelElement.taggedValue>
                <CWM:TaggedValue tag="BUSINESS_CATEGORY_TYPE" value="Column" xmi.id="a1306"/>
              </CWM:ModelElement.taggedValue>
            </CWM:Attribute>
            <CWM:Attribute name="BC_OFFICES_POSTALCODE" xmi.id="a1307">
              <CWM:ModelElement.taggedValue>
                <CWM:TaggedValue tag="BUSINESS_CATEGORY_TYPE" value="Column" xmi.id="a1308"/>
              </CWM:ModelElement.taggedValue>
            </CWM:Attribute>
          </CWM:Namespace.ownedElement>
        </CWM:Extent>
        <CWM:KeyRelationship xmi.id="a1338">
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="RELATIONSHIP_TYPE" value="1:N" xmi.id="a1339"/>
            <CWM:TaggedValue tag="RELATIONSHIP_FIELDNAME_CHILD" value="BC_EMPLOYEES_OFFICECODE" xmi.id="a1340"/>
            <CWM:TaggedValue tag="RELATIONSHIP_FIELDNAME_PARENT" value="BC_OFFICES_OFFICECODE" xmi.id="a1341"/>
            <CWM:TaggedValue tag="RELATIONSHIP_TABLENAME_CHILD" value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1342"/>
            <CWM:TaggedValue tag="RELATIONSHIP_TABLENAME_PARENT" value="BT_OFFICES_OFFICES" xmi.id="a1343"/>
          </CWM:ModelElement.taggedValue>
        </CWM:KeyRelationship>
      </CWM:Namespace.ownedElement>
      <CWMMDB:Schema.dimensionedObject>
        <CWMMDB:DimensionedObject name="BC_EMPLOYEES_JOBTITLE" xmi.id="a1344">
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="BUSINESS_COLUMN_BUSINESS_TABLE" value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1345"/>
            <CWM:TaggedValue tag="BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME" value="JOBTITLE" xmi.id="a1346"/>
          </CWM:ModelElement.taggedValue>
          <CWMMDB:DimensionedObject.dimension>
            <CWMMDB:Dimension xmi.idref="a21"/>
          </CWMMDB:DimensionedObject.dimension>
        </CWMMDB:DimensionedObject>
        <CWMMDB:DimensionedObject name="BC_EMPLOYEES_REPORTSTO" xmi.id="a1347">
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="BUSINESS_COLUMN_BUSINESS_TABLE" value="BT_EMPLOYEES_EMPLOYEES" xmi.id="a1348"/>
            <CWM:TaggedValue tag="BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME" value="REPORTSTO" xmi.id="a1349"/>
          </CWM:ModelElement.taggedValue>
          <CWMMDB:DimensionedObject.dimension>
            <CWMMDB:Dimension xmi.idref="a21"/>
          </CWMMDB:DimensionedObject.dimension>
        </CWMMDB:DimensionedObject>
      </CWMMDB:Schema.dimensionedObject>
      <CWMMDB:Schema.dimension>
        <CWMMDB:Dimension isAbstract="false" name="BT_EMPLOYEES_EMPLOYEES" xmi.id="a21"> <- Biz table
          <CWM:ModelElement.taggedValue>
            <CWM:TaggedValue tag="TABLE_IS_DRAWN" value="Y" xmi.id="a1396"/>
            <CWM:TaggedValue tag="TAG_POSITION_Y" value="151" xmi.id="a1397"/>
            <CWM:TaggedValue tag="TAG_POSITION_X" value="213" xmi.id="a1398"/>
            <CWM:TaggedValue tag="BUSINESS_TABLE_PHYSICAL_TABLE_NAME" value="PT_EMPLOYEES" xmi.id="a1399"/>
          </CWM:ModelElement.taggedValue>
          <CWMMDB:Dimension.dimensionedObject>
            <CWMMDB:DimensionedObject xmi.idref="a1365"/>
            <CWMMDB:DimensionedObject xmi.idref="a1362"/>
          </CWMMDB:Dimension.dimensionedObject>
        </CWMMDB:Dimension>
      </CWMMDB:Schema.dimension>
    </CWMMDB:Schema>
       */
    }

    for (Element description : descriptions) {
      /*
           <CWM:Description body="N" name="hidden" type="Boolean" xmi.id="a989">
      <CWM:Description.modelElement>
        <CWMRDB:Column xmi.idref="a985"/>
      </CWM:Description.modelElement>
    </CWM:Description>
       */
      Element modelElem = (Element)description.getElementsByTagName("CWM:Description.modelElement").item(0);
      NodeList mecn = modelElem.getChildNodes();
      String parentRef = null;
      String type = null;
      for (int i = 0; i < mecn.getLength(); i++) {
        if (mecn.item(i).getNodeType() == Node.ELEMENT_NODE) {
           parentRef = ((Element)mecn.item(i)).getAttribute("xmi.idref");
           // temporary, not really needed
           type = mecn.item(i).getNodeName();
           break;
        }
      }
      if (parentRef == null) {
        System.out.println("PARENT REF NULL");
      } else {
        Concept concept = xmiConceptMap.get(parentRef);
        String name = description.getAttribute("name");
        String body = description.getAttribute("body");
        if (concept == null) {
          System.out.println("CANT FIND PARENT: " + type + " : " + parentRef);
        } else {
          // ADD PROPERTY
          String propType = description.getAttribute("type");
          if (propType.equals("LocString")) {
            addLocalizedString(description, concept, name, body);
          } else if (propType.equals("String")) {
            // <CWM:Description body="" name="mask" type="String" xmi.id="a90">
            if (name.equals("formula")) {
              name = SqlPhysicalColumn.TARGET_COLUMN;
            }
            concept.setProperty(name, body);
          } else if (propType.equals("Boolean")) {
            if (name.equals("exact")) {
              concept.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, "Y".equals(body) ? TargetColumnType.OPEN_FORMULA : TargetColumnType.COLUMN_NAME);
            } else {
              // <CWM:Description body="N" name="exact" type="Boolean" xmi.id="a92">
              concept.setProperty(name, new Boolean("Y".equals(body)));
            }
          } else if (propType.equals("FieldType")) {
            //     <CWM:Description body="Dimension" name="fieldtype" type="FieldType" xmi.id="a97">
            concept.setProperty(name, FieldType.values()[FieldTypeSettings.getType(body).getType()]);
          } else if (propType.equals("TableType")) {
            concept.setProperty(name, TableType.values()[TableTypeSettings.getType(body).getType()]);
          } else if (propType.equals("DataType")) {
            // <CWM:Description body="String,50,-1" name="datatype" type="DataType" xmi.id="a100">
            DataTypeSettings setting = DataTypeSettings.fromString(body);
            concept.setProperty(name, DataType.valueOf(setting.getCode().toUpperCase()));
          } else if (propType.equals("Security")) {
            // <CWM:Description body="&lt;security&gt;&#10;  &lt;owner-rights&gt;&#10;  &lt;owner&gt;&lt;type&gt;user&lt;/type&gt;&lt;name&gt;suzy&lt;/name&gt;&lt;/owner&gt; &lt;rights&gt;31&lt;/rights&gt;&#10;  &lt;/owner-rights&gt;&#10;  &lt;owner-rights&gt;&#10;  &lt;owner&gt;&lt;type&gt;role&lt;/type&gt;&lt;name&gt;Admin&lt;/name&gt;&lt;/owner&gt; &lt;rights&gt;31&lt;/rights&gt;&#10;  &lt;/owner-rights&gt;&#10;&lt;/security&gt;&#10;" name="security" type="Security" xmi.id="a84">
            Security security = Security.fromXML(body);
            Map<SecurityOwner, Integer> map = new HashMap<SecurityOwner, Integer>();
            for (org.pentaho.pms.schema.security.SecurityOwner owner : security.getOwners()) {
              SecurityOwner ownerObj = new SecurityOwner(SecurityOwner.OwnerType.values()[owner.getOwnerType()], owner.getOwnerName());
              Integer val = security.getOwnerRights(owner);
              map.put(ownerObj, val);
            }
            concept.setProperty(name, new org.pentaho.metadata.model.concept.security.Security(map));
          } else if (propType.equals("RowLevelSecurity")) {
            org.pentaho.pms.schema.security.RowLevelSecurity security = 
              org.pentaho.pms.schema.security.RowLevelSecurity.fromXML(body);
            
            RowLevelSecurity securityObj = new RowLevelSecurity();
            securityObj.setType(RowLevelSecurity.Type.values()[security.getType().ordinal()]);
            securityObj.setGlobalConstraint(security.getGlobalConstraint());
            
            Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
            for (org.pentaho.pms.schema.security.SecurityOwner owner : security.getRoleBasedConstraintMap().keySet()) {
              SecurityOwner ownerObj = new SecurityOwner(SecurityOwner.OwnerType.values()[owner.getOwnerType()], owner.getOwnerName());
              map.put(ownerObj, security.getRoleBasedConstraintMap().get(owner));
            }
            securityObj.setRoleBasedConstraintMap(map);
            concept.setProperty(name, securityObj);
          } else if (propType.equals("Aggregation")) {
            // <CWM:Description body="none" name="aggregation" type="Aggregation" xmi.id="a104">
            
            concept.setProperty(name, AggregationType.values()[AggregationSettings.getType(body).getType()]);
          } else if (propType.equals("AggregationList")) {
            List<AggregationSettings> settings = ConceptPropertyAggregationList.fromXML(body);
            List<AggregationType> aggTypes = new ArrayList<AggregationType>();
            if (settings != null) {
              for (AggregationSettings setting : settings) {
                aggTypes.add(AggregationType.values()[setting.getType()]);
              }
            }
            concept.setProperty(name, aggTypes);
          } else if (propType.equals("Font")) {
            FontSettings font = FontSettings.fromString(body);
            concept.setProperty(name, new Font(font.getName(), font.getHeight(), font.isBold(), font.isItalic()));
          } else if (propType.equals("Color")) {
            ColorSettings color = ColorSettings.fromString(body);
            concept.setProperty(name, new Color(color.getRed(), color.getGreen(), color.getBlue()));
            // TODO: } else if (propType.equals("AggregationList")) {
            // TODO: ALL Others: URL, Number, etc
          } else if (propType.equals("Alignment")) {
            AlignmentSettings alignment = AlignmentSettings.fromString(body);
            concept.setProperty(name, Alignment.values()[alignment.getType()]);
          } else if (propType.equals("Number")) {
            BigDecimal bd = new BigDecimal(body);
            concept.setProperty(name, bd.doubleValue());
          } else if (propType.equals("ColumnWidth")) {
            ColumnWidth cw = ColumnWidth.fromString(body);
            WidthType cwt = WidthType.values()[cw.getType()];
            org.pentaho.metadata.model.concept.types.ColumnWidth ncw = new org.pentaho.metadata.model.concept.types.ColumnWidth(cwt, cw.getWidth().doubleValue());
            concept.setProperty(name, ncw);
          } else if (propType.equals("URL")) {
            // NOTE: URL is not compatible with GWT at this time
            URL url = new URL(body);
            concept.setProperty(name, url);
          } else {
            logger.error("Failed to convert PROPERTY OF TYPE " + propType + " of " + concept.getId());
          }
        }
      }
    }
    return domain;
  }
  
  protected void addLocalizedString(Element description, Concept concept, String name, String body) {
    /*
     <CWM:Description body="Oficinas" language="es" name="name" type="LocString" xmi.id="a14">
     */

    String lang = description.getAttribute("language");
    LocalizedString str = (LocalizedString)concept.getChildProperty(name);
    if (str == null) {
      str = new LocalizedString();
      concept.setProperty(name, str);
    }
    str.setString(lang, body);
  }

  
  protected void populateLocales(Domain domain, List<Element> parameters) {

    List<LocaleInterface> legacyLocaleList = new ArrayList<LocaleInterface>();
    for (Element parameter : parameters) {
      /*
           <CWM:Parameter name="es" xmi.id="a1154">
      <CWM:ModelElement.taggedValue>
        <CWM:TaggedValue tag="LOCALE_IS_DEFAULT" value="N" xmi.id="a1155"/>
        <CWM:TaggedValue tag="LOCALE_ORDER" value="2" xmi.id="a1156"/>
        <CWM:TaggedValue tag="LOCALE_DESCRIPTION" value="Spanish" xmi.id="a1157"/>
      </CWM:ModelElement.taggedValue>
    </CWM:Parameter>
       */
      LocaleInterface locale = new LocaleMeta();
      locale.setCode(parameter.getAttribute("name"));
      Map<String, String> kvp = getKeyValuePairs(parameter, "CWM:TaggedValue", "tag", "value");
      
      // The description
      String description = kvp.get("LOCALE_DESCRIPTION");
      if (!Const.isEmpty(description)) {
        locale.setDescription(description);
      }
      
      // The order
      String strOrder = kvp.get("LOCALE_ORDER");
      locale.setOrder(Const.toInt(strOrder, -1));
      
      // Active?
      
      legacyLocaleList.add(locale);
    }
    
    
    List<LocaleType> localeTypes = new ArrayList<LocaleType>();

    // the new model uses the natural ordering of the list vs. a separate ordinal
    
    
    Collections.sort(legacyLocaleList, new Comparator<LocaleInterface>() {
      // TODO: Test ordering
      public int compare(LocaleInterface o1, LocaleInterface o2) {
        if (o1.getOrder() > o2.getOrder()) {
          return 1;
        } else if (o1.getOrder() < o2.getOrder()) {
          return -1;
        } else {
          return 0;
        }
      }
    });
    
    for (LocaleInterface locale : legacyLocaleList) {
      LocaleType localeType = new LocaleType();
      localeType.setDescription(locale.getDescription());
      localeType.setCode(locale.getCode());
      localeTypes.add(localeType);
    }
    domain.setLocales(localeTypes);
    
  }
  
  private static Map<String, String> getKeyValuePairs(Element parent, String childName, String keyAttrib, String valAttrib) {
    HashMap<String, String> map = new HashMap<String, String>();
    NodeList nodeList = parent.getElementsByTagName(childName);
    for (int i = 0; i < nodeList.getLength(); i++) {
      map.put( ((Element)nodeList.item(i)).getAttribute(keyAttrib), ((Element)nodeList.item(i)).getAttribute(valAttrib));
    }
    return map;
  }
  
  private static String getKeyValue(Element parent, String childName, String keyAttrib, String valAttrib, String keyVal) {
    NodeList nodeList = parent.getElementsByTagName(childName);
    for (int i = 0; i < nodeList.getLength(); i++) {
      String key = ((Element)nodeList.item(i)).getAttribute(keyAttrib);
      if (key.equals(keyVal)) {
        return ((Element)nodeList.item(i)).getAttribute(valAttrib);
      }
    }
    return null;
  }
}
