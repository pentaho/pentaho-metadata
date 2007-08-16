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
package org.pentaho.pms.mql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.OrderBy;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.WhereCondition;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.XMLHandler;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.core.logging.Log4jStringAppender;
import be.ibridge.kettle.trans.Trans;
import be.ibridge.kettle.trans.TransMeta;
import be.ibridge.kettle.trans.step.RowListener;
import be.ibridge.kettle.trans.step.StepInterface;

public class MQLQuery {

  private static final Log logger = LogFactory.getLog(MQLQuery.class);

  public static int DOMAIN_TYPE_RELATIONAL = 1;

  public static int DOMAIN_TYPE_OLAP = 2; // NOT SUPPORTED YET

  private int domainType = DOMAIN_TYPE_RELATIONAL;

  private List selections = new ArrayList();

  private List constraints = new ArrayList();

  private List order = new ArrayList();

  private BusinessModel model;

  private String locale;

  private SchemaMeta schemaMeta;

  private CwmSchemaFactoryInterface cwmSchemaFactory;

  public MQLQuery(SchemaMeta schemaMeta, BusinessModel model, String locale) {
    this.schemaMeta = schemaMeta;
    this.model = model;
    this.locale = locale;
  }

  public MQLQuery(String XML, String locale, CwmSchemaFactoryInterface factory) throws PentahoMetadataException {
    // this.schemaMeta = schemaMeta;
    this.locale = locale;
    this.cwmSchemaFactory = factory;
    fromXML(XML);
  }

  public MQLQuery(String filename) throws IOException, PentahoMetadataException {
    File file = new File(filename);
    FileInputStream fileInputStream = new FileInputStream(file);
    byte bytes[] = new byte[(int) file.length()];
    fileInputStream.read(bytes);
    fileInputStream.close();

    fromXML(new String(bytes, Const.XML_ENCODING));
  }

  public void save(String queryFile) throws IOException {
    File file = new File(queryFile);
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    fileOutputStream.write(getXML().getBytes(Const.XML_ENCODING));
    fileOutputStream.close();
  }

  public void addSelection(BusinessColumn column) {
    if (!selections.contains(column)) {
      selections.add(column);
    }
  }

  public List getSelections() {
    return selections;
  }

  public DatabaseMeta getDatabaseMeta() {
    if (selections.size() > 0) {
      return ((BusinessColumn) selections.get(0)).getPhysicalColumn().getTable().getDatabaseMeta();
    }
    return null;
  }

  public void addConstraint(String operator, String condition) throws PentahoMetadataException {
    WhereCondition where = new WhereCondition(model, operator, condition);
    constraints.add(where);
  }

  public void addOrderBy(String categoryId, String columnId, boolean ascending) throws PentahoMetadataException {
    BusinessCategory rootCat = model.getRootCategory();
    BusinessCategory businessCategory = rootCat.findBusinessCategory(categoryId);
    // BusinessCategory businessCategory = model.findBusinessTable( tableId );
    if (businessCategory == null) {
      throw new PentahoMetadataException(Messages.getErrorString(
          "MQLQuery.ERROR_0014_BUSINESS_CATEGORY_NOT_FOUND", categoryId)); //$NON-NLS-1$ 
    }
    addOrderBy(businessCategory, columnId, ascending);
  }

  public void addOrderBy(BusinessCategory businessCategory, String columnId, boolean ascending)
      throws PentahoMetadataException {

    if (businessCategory == null) {
      throw new PentahoMetadataException(Messages.getErrorString("MQLQuery.ERROR_0015_BUSINESS_CATEGORY_NULL")); //$NON-NLS-1$ 
    }

    BusinessColumn businessColumn = businessCategory.findBusinessColumn(columnId);
    if (businessColumn == null) {
      throw new PentahoMetadataException(Messages.getErrorString(
          "MQLQuery.ERROR_0016_BUSINESS_COLUMN_NOT_FOUND", businessCategory.getId(), columnId)); //$NON-NLS-1$ 
    }

    OrderBy orderBy = new OrderBy(businessColumn, ascending);
    order.add(orderBy);
  }

  public String getQuery(boolean useDisplayNames) throws PentahoMetadataException {
    if (model == null || selections.size() == 0) {
      return null;
    }
    BusinessColumn selection[] = (BusinessColumn[]) selections.toArray(new BusinessColumn[selections.size()]);
    WhereCondition conditions[] = (WhereCondition[]) constraints.toArray(new WhereCondition[constraints.size()]);
    OrderBy orderBy[] = (OrderBy[]) order.toArray(new OrderBy[order.size()]);

    return model.getSQL(selection, conditions, orderBy, locale, useDisplayNames);
  }

  public TransMeta getTransformation(boolean useDisplayNames) throws PentahoMetadataException {
    if (model == null || selections.size() == 0) {
      return null;
    }
    BusinessColumn selection[] = (BusinessColumn[]) selections.toArray(new BusinessColumn[selections.size()]);
    WhereCondition conditions[] = (WhereCondition[]) constraints.toArray(new WhereCondition[constraints.size()]);
    OrderBy orderBy[] = (OrderBy[]) order.toArray(new OrderBy[order.size()]);

    return model.getTransformationMeta(selection, conditions, orderBy, locale, useDisplayNames);
  }

  public List getRowsUsingTransformation(boolean useDisplayNames, StringBuffer logBuffer) throws KettleException,
      PentahoMetadataException {
    final List list = new ArrayList();
    TransMeta transMeta = getTransformation(useDisplayNames);
    LogWriter log = LogWriter.getInstance();
    Log4jStringAppender stringAppender = LogWriter.createStringAppender();
    stringAppender.setBuffer(logBuffer);

    log.addAppender(stringAppender);
    Trans trans = new Trans(log, transMeta);
    trans.prepareExecution(null);
    for (int i = 0; i < transMeta.getStep(0).getCopies(); i++) {
      StepInterface stepInterface = trans.getStepInterface(transMeta.getStep(0).getName(), i);
      stepInterface.addRowListener(new RowListener() {
        public void rowWrittenEvent(Row row) {
          list.add(row); // later: clone to be safe 
        }

        public void rowReadEvent(Row row) {
        }

        public void errorRowWrittenEvent(Row row) {
        }
      });
    }
    trans.startThreads();
    trans.waitUntilFinished();
    log.removeAppender(stringAppender);

    if (trans.getErrors() > 0)
      throw new KettleException(
          Messages
              .getString(
                  "MQLQuery.ERROR_0001_ERROR_TRANSFORMATION_QUERY_EXECUTE", Const.CR + Const.CR + stringAppender.getBuffer().toString())); //$NON-NLS-1$

    return list;

  }

  public String getXML() {

    try {
      StringWriter stringWriter = new StringWriter();
      StreamResult result = new StreamResult();
      result.setWriter(stringWriter);
      TransformerFactory factory = TransformerFactory.newInstance();
      Document doc = getDocument();
      if (doc != null) {
        factory.newTransformer().transform(new DOMSource(doc), result);
        return stringWriter.getBuffer().toString();
      }
    } catch (Exception e) {
      logger.error(Messages.getErrorString("MQLQuery.ERROR_0013_GET_XML_FAILED"), e); //$NON-NLS-1$
    }
    return null;
  }

  public Document getDocument() {
    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    Document doc;

    try {
      // create an XML document
      dbf = DocumentBuilderFactory.newInstance();
      db = dbf.newDocumentBuilder();
      doc = db.newDocument();
      Element mqlElement = doc.createElement("mql"); //$NON-NLS-1$
      doc.appendChild(mqlElement);

      if (addToDocument(mqlElement, doc)) {
        return doc;
      } else {
        return null;
      }
    } catch (Exception e) {
      logger.error(Messages.getErrorString("MQLQuery.ERROR_0012_GET_DOCUMENT_FAILED"), e); //$NON-NLS-1$
    }
    return null;
  }

  public boolean addToDocument(Element mqlElement, Document doc) {

    try {

      if (schemaMeta == null) {
        logger.error(Messages.getString("MQLQuery.ERROR_0002_META_SCHEMA_NULL")); //$NON-NLS-1$
        return false;
      }

      if (model == null) {
        logger.error(Messages.getString("MQLQuery.ERROR_0003_BUSINESS_MODEL_NULL")); //$NON-NLS-1$
        return false;
      }

      // insert the domain information
      Element typeElement = doc.createElement("domain_type"); //$NON-NLS-1$
      typeElement.appendChild(doc.createTextNode((domainType == DOMAIN_TYPE_RELATIONAL) ? "relational" : "olap")); //$NON-NLS-1$ //$NON-NLS-2$
      mqlElement.appendChild(typeElement);

      // insert the domain information
      String data = schemaMeta.getDomainName();
      if (data != null) {
        Element domainIdElement = doc.createElement("domain_id"); //$NON-NLS-1$
        domainIdElement.appendChild(doc.createTextNode(data));
        mqlElement.appendChild(domainIdElement);
      } else {
        logger.error(Messages.getString("MQLQuery.ERROR_0004_DOMAIN_ID_NULL")); //$NON-NLS-1$
        return false;
      }

      // insert the model information
      data = model.getId();
      if (data != null) {
        Element modelIdElement = doc.createElement("model_id"); //$NON-NLS-1$
        modelIdElement.appendChild(doc.createTextNode(data));
        mqlElement.appendChild(modelIdElement);
      } else {
        logger.error(Messages.getString("MQLQuery.ERROR_0005_MODEL_ID_NULL")); //$NON-NLS-1$
        return false;
      }

      data = model.getDisplayName(locale);
      if (data != null) {
        Element modelNameElement = doc.createElement("model_name"); //$NON-NLS-1$
        modelNameElement.appendChild(doc.createTextNode(data));
        mqlElement.appendChild(modelNameElement);
      } else {
        logger.error(Messages.getString("MQLQuery.ERROR_0006_MODEL_NAME_NULL")); //$NON-NLS-1$
        return false;
      }

      // insert the selections
      Element selectionsElement = doc.createElement("selections"); //$NON-NLS-1$
      mqlElement.appendChild(selectionsElement);
      Iterator it = selections.iterator();
      Element selectionElement;
      Element element;
      while (it.hasNext()) {
        BusinessColumn column = (BusinessColumn) it.next();
        if (column.getBusinessTable() != null) {
          selectionElement = doc.createElement("selection"); //$NON-NLS-1$

          element = doc.createElement("view"); //$NON-NLS-1$

          // element.appendChild( doc.createTextNode( column.getBusinessTable().getId() ) );
          //
          // Work-around for PMD-93 - not using BusinessView in the MQL.
          BusinessCategory rootCat = model.getRootCategory();
          BusinessCategory businessCategory = rootCat.findBusinessCategoryForBusinessColumn(column);
          element.appendChild(doc.createTextNode(businessCategory.getId()));

          selectionElement.appendChild(element);

          element = doc.createElement("column"); //$NON-NLS-1$
          element.appendChild(doc.createTextNode(column.getId()));
          selectionElement.appendChild(element);

          selectionsElement.appendChild(selectionElement);
        }
      }
      // insert the contraints
      Element contraintsElement = doc.createElement("constraints"); //$NON-NLS-1$
      mqlElement.appendChild(contraintsElement);
      it = constraints.iterator();
      Element constraintElement;
      while (it.hasNext()) {
        WhereCondition condition = (WhereCondition) it.next();
        constraintElement = doc.createElement("constraint"); //$NON-NLS-1$

        element = doc.createElement("operator"); //$NON-NLS-1$
        element.appendChild(doc.createTextNode(condition.getOperator() == null ? "" : condition.getOperator())); //$NON-NLS-1$
        constraintElement.appendChild(element);

        element = doc.createElement("condition"); //$NON-NLS-1$
        element.appendChild(doc.createTextNode(condition.getCondition()));
        constraintElement.appendChild(element);

        // Save the localized names...
        /* 
         * TODO
         String[] locales = condition.getConcept().getUsedLocale();
         
         element = doc.createElement( "name" );
         for (int i=0;i<locales.length;i++) {
         String name = condition.getName(locales[i]);
         if (!Const.isEmpty( name) ) {
         Element locElement = doc.createElement("locale");
         locElement.appendChild( doc.createTextNode(locales[i]) );
         element.appendChild(locElement);
         Element valElement = doc.createElement("value");
         locElement.appendChild( doc.createTextNode(name) );
         element.appendChild(valElement);
         }
         }
         constraintElement.appendChild( element );

         element = doc.createElement( "description" );
         for (int i=0;i<locales.length;i++) {
         String description = condition.getDescription(locales[i]);
         if (!Const.isEmpty( description) ) {
         Element locElement = doc.createElement("locale");
         locElement.appendChild( doc.createTextNode(locales[i]) );
         element.appendChild(locElement);
         Element valElement = doc.createElement("value");
         locElement.appendChild( doc.createTextNode(description) );
         element.appendChild(valElement);
         }
         }
         constraintElement.appendChild( element );
         */

        contraintsElement.appendChild(constraintElement);
      }
      // insert the contraints
      Element ordersElement = doc.createElement("orders"); //$NON-NLS-1$
      mqlElement.appendChild(ordersElement);
      it = order.iterator();
      Element orderElement;
      while (it.hasNext()) {
        OrderBy orderBy = (OrderBy) it.next();
        orderElement = doc.createElement("order"); //$NON-NLS-1$

        element = doc.createElement("direction"); //$NON-NLS-1$
        element.appendChild(doc.createTextNode(orderBy.isAscending() ? "asc" : "desc")); //$NON-NLS-1$ //$NON-NLS-2$
        orderElement.appendChild(element);

        // Work-around for PMD-93 - Need this to be better into the future...
        element = doc.createElement("view_id"); //$NON-NLS-1$
        BusinessCategory rootCat = model.getRootCategory();
        BusinessCategory businessView = rootCat.findBusinessCategoryForBusinessColumn(orderBy.getBusinessColumn());
        element.appendChild(doc.createTextNode(businessView.getId()));

        orderElement.appendChild(element);

        element = doc.createElement("column_id"); //$NON-NLS-1$
        element.appendChild(doc.createTextNode(orderBy.getBusinessColumn().getId()));
        orderElement.appendChild(element);

        ordersElement.appendChild(orderElement);
      }

    } catch (Exception e) {
      logger.error(Messages.getErrorString("MQLQuery.ERROR_0011_ADD_TO_DOCUMENT_FAILED"), e); //$NON-NLS-1$
    }
    return true;

  }

  public void fromXML(String XML) throws PentahoMetadataException {
    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    Document doc;

    // Check and open XML document
    dbf = DocumentBuilderFactory.newInstance();
    try {
      db = dbf.newDocumentBuilder();
      doc = db.parse(new InputSource(new java.io.StringReader(XML)));
    } catch (ParserConfigurationException pcx) {
      throw new PentahoMetadataException(pcx);
    } catch (SAXException sex) {
      throw new PentahoMetadataException(sex);
    } catch (IOException iex) {
      throw new PentahoMetadataException(iex);
    }
    // get the domain type
    String domainTypeStr = getElementText(doc, "domain_type"); //$NON-NLS-1$
    if ("relational".equals(domainTypeStr)) { //$NON-NLS-1$
      domainType = DOMAIN_TYPE_RELATIONAL;
    } else if ("olap".equals(domainTypeStr)) { //$NON-NLS-1$
      domainType = DOMAIN_TYPE_OLAP;
    } else {
      // need to throw an error
      throw new PentahoMetadataException(Messages.getErrorString(
          "MQLQuery.ERROR_0007_INVALID_DOMAIN_TYPE", domainTypeStr)); //$NON-NLS-1$
    }

    // get the domain id
    String domainId = getElementText(doc, "domain_id"); //$NON-NLS-1$
    CWM cwm = CWM.getInstance(domainId);
    if (cwm == null) {
      // need to throw an error
      throw new PentahoMetadataException(Messages.getErrorString(
          "MQLQuery.ERROR_0008_CWM_DOMAIN_INSTANCE_NULL", domainId)); //$NON-NLS-1$
    }

    if (null == cwmSchemaFactory) {
      cwmSchemaFactory = Settings.getCwmSchemaFactory();
    }
    schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);

    // get the Business View id
    String modelId = getElementText(doc, "model_id"); //$NON-NLS-1$
    System.out.println(modelId);
    model = schemaMeta.findModel(modelId); // This is the business model that was selected.

    if (model == null) {
      throw new PentahoMetadataException(Messages.getErrorString("MQLQuery.ERROR_0009_MODEL_NOT_FOUND", modelId)); //$NON-NLS-1$
    }

    // process the selections
    NodeList nodes = doc.getElementsByTagName("selection"); //$NON-NLS-1$
    Element selectionElement;
    for (int idx = 0; idx < nodes.getLength(); idx++) {
      selectionElement = (Element) nodes.item(idx);
      addBusinessColumnFromXmlNode(selectionElement);
    }

    // process the constraints
    nodes = doc.getElementsByTagName("constraint"); //$NON-NLS-1$
    Element constraintElement;
    for (int idx = 0; idx < nodes.getLength(); idx++) {
      constraintElement = (Element) nodes.item(idx);
      addConstraintFromXmlNode(constraintElement);
    }

    // process the constraints
    nodes = doc.getElementsByTagName("order"); //$NON-NLS-1$
    Element orderElement;
    for (int idx = 0; idx < nodes.getLength(); idx++) {
      orderElement = (Element) nodes.item(idx);
      addOrderByFromXmlNode(orderElement);
    }
  }

  private void addBusinessColumnFromXmlNode(Element selectionElement) {
    NodeList nodes = selectionElement.getElementsByTagName("column"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      String columnId = XMLHandler.getNodeValue(nodes.item(0));
      BusinessColumn businessColumn = model.findBusinessColumn(columnId);
      if (businessColumn != null) {
        addSelection(businessColumn);
      }
    }
  }

  private void addOrderByFromXmlNode(Element orderElement) throws PentahoMetadataException {
    boolean ascending = true;
    String view_id = null;
    String column_id = null;

    NodeList nodes = orderElement.getElementsByTagName("direction"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      ascending = XMLHandler.getNodeValue(nodes.item(0)).equals("asc"); //$NON-NLS-1$
    }
    nodes = orderElement.getElementsByTagName("view_id"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      view_id = XMLHandler.getNodeValue(nodes.item(0));
    }
    nodes = orderElement.getElementsByTagName("column_id"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      column_id = XMLHandler.getNodeValue(nodes.item(0));
    }

    if ((view_id != null) && (column_id != null)) {
      addOrderBy(view_id, column_id, ascending);
    }
  }

  private void addConstraintFromXmlNode(Element constraintElement) throws PentahoMetadataException {

    NodeList nodes = constraintElement.getElementsByTagName("operator"); //$NON-NLS-1$
    String operator = null;
    if (nodes.getLength() > 0) {
      operator = XMLHandler.getNodeValue(nodes.item(0));
    }

    nodes = constraintElement.getElementsByTagName("condition"); //$NON-NLS-1$
    String cond = null;
    if (nodes.getLength() > 0) {
      cond = XMLHandler.getNodeValue(nodes.item(0));
    }

    nodes = constraintElement.getElementsByTagName("view_id"); //$NON-NLS-1$
    String view_id = null;
    if (nodes.getLength() > 0) {
      view_id = XMLHandler.getNodeValue(nodes.item(0));
    }

    nodes = constraintElement.getElementsByTagName("column_id"); //$NON-NLS-1$
    String column_id = null;
    if (nodes.getLength() > 0) {
      column_id = XMLHandler.getNodeValue(nodes.item(0));
    }

    if (cond == null) {
      logger.error(Messages.getErrorString("MQLQuery.ERROR_0001_NULL_CONDITION")); //$NON-NLS-1$
    }

    if (cond != null) {
      if (view_id == null || column_id == null) {
        // new function support
        addConstraint(operator, cond);
      } else {
        // backwards compatibility
        if (view_id != null && column_id != null && cond != null) {
          addConstraint(operator, "[" + view_id + "." + column_id + "] " + cond); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
      }
    }
  }

  private String getElementText(Document doc, String name) {
    try {
      return doc.getElementsByTagName(name).item(0).getFirstChild().getNodeValue();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * @return the constraints
   */
  public List getConstraints() {
    return constraints;
  }

  /**
   * @param constraints the constraints to set
   */
  public void setConstraints(List constraints) {
    this.constraints = constraints;
  }

  /**
   * @return the locale
   */
  public String getLocale() {
    return locale;
  }

  /**
   * @param locale the locale to set
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  /**
   * @return the order
   */
  public List getOrder() {
    return order;
  }

  /**
   * @param order the order to set
   */
  public void setOrder(List order) {
    this.order = order;
  }

  /**
   * @return the schemaMeta
   */
  public SchemaMeta getSchemaMeta() {
    return schemaMeta;
  }

  /**
   * @param schemaMeta the schemaMeta to set
   */
  public void setSchemaMeta(SchemaMeta schemaMeta) {
    this.schemaMeta = schemaMeta;
  }

  /**
   * @return the model
   */
  public BusinessModel getModel() {
    return model;
  }

  /**
   * @param model the model to set
   */
  public void setModel(BusinessModel model) {
    this.model = model;
  }

  /**
   * @param selections the selections to set
   */
  public void setSelections(List selections) {
    this.selections = selections;
  }

}