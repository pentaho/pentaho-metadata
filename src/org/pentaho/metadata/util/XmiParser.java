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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalColumn;
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
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.Color;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.RelationshipType;
import org.pentaho.metadata.model.concept.types.TableType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.color.ColorSettings;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.security.Security;
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
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class XmiParser {
  
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
    
    // started  CWM:Class = domain concepts
    // started  CWM:Description = Concept Property
    // skipping CWM:Event = Security Service (skip for now)
    // completed CWM:Parameter = Locale info
    // skipping CWMOLAP:Schema = not populated, name of business view?
    // started  CWMRDB:Catalog = DatabaseMeta in old model, SqlPhysicalModel -> DataSource in new model
    // started  CWMRDB:Table = Sql Physical Table
    // TODO     CWMMDB:Schema = Logical Category
    
    
    
    List<Element> concepts = new ArrayList<Element>();
    List<Element> descriptions = new ArrayList<Element>();
    List<Element> datasources = new ArrayList<Element>();
    List<Element> physicalTables = new ArrayList<Element>();
    List<Element> parameters = new ArrayList<Element>();
    List<Element> schemas = new ArrayList<Element>();
    
    list = content.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        if (node.getNodeName().equals("CWM:Class")) {
          concepts.add((Element)node);
        } else if (node.getNodeName().equals("CWM:Parameter")) {
          concepts.add((Element)node);
        } else if (node.getNodeName().equals("CWMRDB:Catalog")) {
          datasources.add((Element)node);
        } else if (node.getNodeName().equals("CWMRDB:Table")) {
          physicalTables.add((Element)node);
        } else if (node.getNodeName().equals("CWMMDB:Schema")) {
          schemas.add((Element)node);
        } else if (node.getNodeName().equals("CWM:Description")) {
          descriptions.add((Element)node);
        } else {
          System.out.println("IGNORED: " + node.getNodeName());
        }
      }
    }
    
    Domain domain = new Domain();
    Map<String, Concept> xmiConceptMap = new HashMap<String, Concept>();
    
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
      String parentName = getKeyValue(concept, "CWM:ModelElement.taggedValue", "tag", "value", "CONCEPT_PARENT_NAME");
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
        <CWM:TaggedValue tag="DATABASE_JDBC_URL" value="HYPERSONIC" xmi.id="a1166"/>
        <CWM:TaggedValue tag="DATABASE_ATTRIBUTE_PREFIX_MAXIMUM_POOL_SIZE" value="10" xmi.id="a1167"/>
        <CWM:TaggedValue tag="DATABASE_ATTRIBUTE_PREFIX_IS_CLUSTERED" value="N" xmi.id="a1168"/>
        <CWM:TaggedValue tag="DATABASE_ATTRIBUTE_PREFIX_USE_POOLING" value="N" xmi.id="a1169"/>
        <CWM:TaggedValue tag="DATABASE_ATTRIBUTE_PREFIX_EXTRA_OPTION_MYSQL.useCursorFetch" value="true" xmi.id="a1170"/>
        <CWM:TaggedValue tag="DATABASE_ATTRIBUTE_PREFIX_STREAM_RESULTS" value="Y" xmi.id="a1171"/>
        <CWM:TaggedValue tag="DATABASE_ATTRIBUTE_PREFIX_EXTRA_OPTION_MYSQL.defaultFetchSize" value="500" xmi.id="a1172"/>
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
       */
      SqlPhysicalModel sqlPhysicalModel = new SqlPhysicalModel();
      domain.addPhysicalModel(sqlPhysicalModel);
      SqlDataSource sqlDataSource = new SqlDataSource();
      sqlPhysicalModel.setDatasource(sqlDataSource);
      
      String name = datasource.getAttribute("name");
      sqlPhysicalModel.setId(name);
      Map<String, String> kvp = getKeyValuePairs(datasource, "CWM:TaggedValue", "tag", "value");
      sqlDataSource.setDatabaseName(kvp.get("DATABASE_DATABASE"));
      // sqlDataSource.setDriverClass(kvp.get("DATABASE_DATABASE"));
      if (kvp.get("DATABASE_ACCESS").equals("JNDI")) {
        sqlDataSource.setType(DataSourceType.JNDI);
      } else {
        // TODO: NEED A BETTER CONVERSION
        sqlDataSource.setType(DataSourceType.JDBC);
//        sqlDataSource.setUsername(kvp.get("DATABASE_USERNAME"));
//        sqlDataSource.setPassword(kvp.get("DATABASE_PASSWORD"));
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
            System.out.println("failed to located concept : " + conceptParentName);
            //TODO: This should go away once things are fully glued together
          } else {
            col.setParentConcept(parent);
          }
        }
        
        
        // 
        
        // TODO: Properties (CWM:Description)
        // maybe add to a temp map of concepts + their xmiid's, and then go through?
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
        relation.setFromColumn(logicalModel.findLogicalColumn(fieldparent));
        relation.setToTable(logicalModel.findLogicalTable(tablechild));
        relation.setToColumn(logicalModel.findLogicalColumn(fieldchild));
        
        relation.setComplex("Y".equals(nvp.get("RELATIONSHIP_IS_COMPLEX")));
        relation.setComplexJoin(nvp.get(nvp.get("RELATIONSHIP_COMPLEX_JOIN")));
        if (nvp.get("RELATIONSHIP_DESCRIPTION") != null) {
          LocalizedString str = new LocalizedString();
          str.setString(domain.getLocales().get(0).getCode(), nvp.get("RELATIONSHIP_DESCRIPTION"));
        }
        relation.setJoinOrderKey(nvp.get("RELATIONSHIP_JOIN_ORDER_KEY"));
        
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

    int totalMissed = 0;

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
          } else if (propType.equals("Aggregation")) {
            // <CWM:Description body="none" name="aggregation" type="Aggregation" xmi.id="a104">
            
            concept.setProperty(name, AggregationType.values()[AggregationSettings.getType(body).getType()]);
          } else if (propType.equals("Font")) {
            FontSettings font = FontSettings.fromString(body);
            concept.setProperty(name, new Font(font.getName(), font.getHeight(), font.isBold(), font.isItalic()));
          } else if (propType.equals("Color")) {
            ColorSettings color = ColorSettings.fromString(body);
            concept.setProperty(name, new Color(color.getRed(), color.getGreen(), color.getBlue()));
            // TODO: } else if (propType.equals("AggregationList")) {
            // TODO: ALL Others: URL, Number, etc
          } else {
            System.out.println("ADDING PROPERTY OF TYPE " + propType + " to " + concept.getId());
            totalMissed++;
          }
        }
      }
    }
    System.out.println("TOTAL MISSED: " + totalMissed);
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
      
    }
    
    
    List<LocaleType> localeTypes = new ArrayList<LocaleType>();

    // the new model uses the natural ordering of the list vs. a separate ordinal
    
    
    Collections.sort(legacyLocaleList, new Comparator<LocaleInterface>() {
      // TODO: Test ordering
      public int compare(LocaleInterface o1, LocaleInterface o2) {
        if (o1.getOrder() > o2.getOrder()) {
          return -1;
        } else if (o1.getOrder() < o2.getOrder()) {
          return 1;
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
