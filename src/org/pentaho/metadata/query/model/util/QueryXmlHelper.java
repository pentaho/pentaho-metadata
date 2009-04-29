package org.pentaho.metadata.query.model.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.messages.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class QueryXmlHelper {
  // to and from XML
  private static final Log logger = LogFactory.getLog(QueryXmlHelper.class);
  
  //
  // TO XML
  //
  
  public Document toDocument(Query query) {
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

      if (addToDocument(mqlElement, doc, query)) {
        return doc;
      } else {
        return null;
      }
    } catch (Exception e) {
      logger.error(Messages.getErrorString("MQLQuery.ERROR_00)X_GET_DOCUMENT_FAILED"), e); //$NON-NLS-1$
    }
    return null;
  }
  
  protected boolean addToDocument(Element mqlElement, Document doc, Query query) {
    try {
      if (query.getDomain() == null) {
        logger.error(Messages.getErrorString("MQLQuery.ERROR_0002_META_SCHEMA_NULL")); //$NON-NLS-1$
        return false;
      }

      if (query.getLogicalModel() == null) {
        logger.error(Messages.getErrorString("MQLQuery.ERROR_0003_BUSINESS_MODEL_NULL")); //$NON-NLS-1$
        return false;
      }

      // insert the domain information
      String data = query.getDomain().getId();
      if (data != null) {
        addTextElement(doc, mqlElement, "domain_id", data);
      } else {
        logger.error(Messages.getErrorString("MQLQuery.ERROR_0004_DOMAIN_ID_NULL")); //$NON-NLS-1$
        return false;
      }

      // insert the model information
      data = query.getLogicalModel().getId();
      if (data != null) {
        addTextElement(doc, mqlElement, "model_id", data);
      } else {
        logger.error(Messages.getErrorString("MQLQuery.ERROR_0005_MODEL_ID_NULL")); //$NON-NLS-1$
        return false;
      }

      // Add additional options
      Element optionsElement = doc.createElement("options"); //$NON-NLS-1$
      mqlElement.appendChild(optionsElement);
      addOptionsToDocument(doc, optionsElement, query);      
      
      // insert the selections
      Element selectionsElement = doc.createElement("selections"); //$NON-NLS-1$
      for (Selection selection : query.getSelections()) {
        Element selectionElement = doc.createElement("selection"); //$NON-NLS-1$
        addSelectionToDocument(doc, selection, selectionElement);
        selectionsElement.appendChild(selectionElement);
      }
      mqlElement.appendChild(selectionsElement);
      
      // insert the contraints
      Element constraintsElement = doc.createElement("constraints"); //$NON-NLS-1$
      for (Constraint condition : query.getConstraints()) {
        Element constraintElement = doc.createElement("constraint"); //$NON-NLS-1$
        addConstraintToDocument(doc, condition, constraintElement);
        constraintsElement.appendChild(constraintElement);
      }
      mqlElement.appendChild(constraintsElement);
      
      // insert the orders
      Element ordersElement = doc.createElement("orders"); //$NON-NLS-1$
      for (Order order : query.getOrders()) {
        Element orderElement = doc.createElement("order"); //$NON-NLS-1$
        addOrderByToDocument(doc, order, orderElement);
        ordersElement.appendChild(orderElement);
        
      }
      mqlElement.appendChild(ordersElement);
      
    } catch (Exception e) {
      logger.error(Messages.getErrorString("MQLQuery.ERROR_0011_ADD_TO_DOCUMENT_FAILED"), e); //$NON-NLS-1$
    }
    return true;

  }
  
  protected void addOptionsToDocument(Document doc, Element optionsElement, Query query) {
    addTextElement(doc, optionsElement, "disable_distinct", Boolean.toString(query.getDisableDistinct())); //$NON-NLS-1$
  }

  protected void addSelectionToDocument(Document doc, Selection selection, Element selectionElement) {
    addTextElement(doc, selectionElement, "view", selection.getCategory().getId()); //$NON-NLS-1$
    addTextElement(doc, selectionElement, "column", selection.getLogicalColumn().getId()); //$NON-NLS-1$
    if (selection.getAggregationType() != null) {
      addTextElement(doc, selectionElement, "aggregation", selection.getAggregationType().toString());
    }
  }
  
  protected void addConstraintToDocument(Document doc, Constraint constraint, Element constraintElement) {
    addTextElement(doc, constraintElement, "operator", constraint.getCombinationType() == null ? "" : constraint.getCombinationType().toString());
    addTextElement(doc, constraintElement, "condition", constraint.getFormula());
  }
  
  protected void addOrderByToDocument(Document doc, Order order, Element orderElement) {

    // test that this is lower case
    addTextElement(doc, orderElement, "direction", order.getType().toString()); //$NON-NLS-1$
    addTextElement(doc, orderElement, "view_id", order.getSelection().getCategory().getId()); //$NON-NLS-1$
    addTextElement(doc, orderElement, "column_id", order.getSelection().getLogicalColumn().getId()); //$NON-NLS-1$
    if (order.getSelection().getAggregationType() != null) {
      addTextElement(doc, orderElement, "aggregation", order.getSelection().getAggregationType().toString());
    }
    
  }

  protected void addTextElement(Document doc, Element element, String elementName, String text) {
    Element childElement = doc.createElement(elementName); //$NON-NLS-1$
    childElement.appendChild(doc.createTextNode(text)); //$NON-NLS-1$ //$NON-NLS-2$
    element.appendChild(childElement);
  }
  
}
