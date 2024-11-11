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

package org.pentaho.metadata.query.model.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This helper class serializes/deserializes the Query object to an MQL XML schema.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class QueryXmlHelper {

  private static final Log logger = LogFactory.getLog( QueryXmlHelper.class );

  //
  // TO XML
  //

  public String toXML( final Query query ) {
    if ( query == null ) {
      logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0000_QUERY_MUST_NOT_BE_NULL" ) ); //$NON-NLS-1$
      return null;
    }

    try {
      StringWriter stringWriter = new StringWriter();
      StreamResult result = new StreamResult();
      result.setWriter( stringWriter );
      TransformerFactory factory = TransformerFactory.newInstance();
      Document doc = toDocument( query );
      if ( doc != null ) {
        factory.newTransformer().transform( new DOMSource( doc ), result );
        return stringWriter.getBuffer().toString();
      }
    } catch ( Exception e ) {
      logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0001_TO_XML_FAILED" ), e ); //$NON-NLS-1$
    }
    return null;
  }

  public Document toDocument( Query query ) {

    if ( query == null ) {
      logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0000_QUERY_MUST_NOT_BE_NULL" ) ); //$NON-NLS-1$
      return null;
    }

    Document doc;
    try {
      // create an XML document
      DocumentBuilderFactory dbf = XmiParser.createSecureDocBuilderFactory();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.newDocument();
      Element mqlElement = doc.createElement( "mql" ); //$NON-NLS-1$
      doc.appendChild( mqlElement );

      if ( addToDocument( mqlElement, doc, query ) ) {
        return doc;
      } else {
        return null;
      }
    } catch ( Exception e ) {
      logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0002_TO_DOCUMENT_FAILED" ), e ); //$NON-NLS-1$
    }
    return null;
  }

  protected boolean addToDocument( Element mqlElement, Document doc, Query query ) {
    try {
      if ( query.getDomain() == null ) {
        logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0003_META_SCHEMA_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      if ( query.getLogicalModel() == null ) {
        logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0004_BUSINESS_MODEL_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      // insert the domain information
      String data = query.getDomain().getId();
      if ( data != null ) {
        addTextElement( doc, mqlElement, "domain_id", data ); //$NON-NLS-1$
      } else {
        logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0005_DOMAIN_ID_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      // insert the model information
      data = query.getLogicalModel().getId();
      if ( data != null ) {
        addTextElement( doc, mqlElement, "model_id", data ); //$NON-NLS-1$
      } else {
        logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0006_MODEL_ID_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      // Add additional options
      Element optionsElement = doc.createElement( "options" ); //$NON-NLS-1$
      mqlElement.appendChild( optionsElement );
      addOptionsToDocument( doc, optionsElement, query );

      if ( query.getParameters() != null && query.getParameters().size() > 0 ) {
        Element parametersElement = doc.createElement( "parameters" ); //$NON-NLS-1$
        mqlElement.appendChild( parametersElement );
        addParametersToDocument( doc, parametersElement, query );
      }

      // insert the selections
      Element selectionsElement = doc.createElement( "selections" ); //$NON-NLS-1$
      for ( Selection selection : query.getSelections() ) {
        Element selectionElement = doc.createElement( "selection" ); //$NON-NLS-1$
        addSelectionToDocument( doc, selection, selectionElement );
        selectionsElement.appendChild( selectionElement );
      }
      mqlElement.appendChild( selectionsElement );

      // insert the contraints
      Element constraintsElement = doc.createElement( "constraints" ); //$NON-NLS-1$
      for ( Constraint condition : query.getConstraints() ) {
        Element constraintElement = doc.createElement( "constraint" ); //$NON-NLS-1$
        addConstraintToDocument( doc, condition, constraintElement );
        constraintsElement.appendChild( constraintElement );
      }
      mqlElement.appendChild( constraintsElement );

      // insert the orders
      Element ordersElement = doc.createElement( "orders" ); //$NON-NLS-1$
      for ( Order order : query.getOrders() ) {
        Element orderElement = doc.createElement( "order" ); //$NON-NLS-1$
        addOrderByToDocument( doc, order, orderElement );
        ordersElement.appendChild( orderElement );

      }
      mqlElement.appendChild( ordersElement );

    } catch ( Exception e ) {
      logger.error( Messages.getErrorString( "QueryXmlHelper.ERROR_0007_ADD_TO_DOCUMENT_FAILED" ), e ); //$NON-NLS-1$
    }
    return true;

  }

  protected void addOptionsToDocument( Document doc, Element optionsElement, Query query ) {
    addTextElement( doc, optionsElement, "disable_distinct", Boolean.toString( query.getDisableDistinct() ) ); //$NON-NLS-1$
    addTextElement( doc, optionsElement, "limit", String.valueOf( query.getLimit() ) ); //$NON-NLS-1$
  }

  protected void addParametersToDocument( Document doc, Element parametersElement, Query query ) {
    for ( Parameter param : query.getParameters() ) {
      Element paramElement = doc.createElement( "parameter" ); //$NON-NLS-1$
      paramElement.setAttribute( "name", param.getName() ); //$NON-NLS-1$
      paramElement.setAttribute( "type", param.getType().toString() ); //$NON-NLS-1$
      paramElement.setAttribute(
          "defaultValue", param.getDefaultValue() == null ? "" : param.getDefaultValue().toString() ); //$NON-NLS-1$ //$NON-NLS-2$
      parametersElement.appendChild( paramElement );
    }
  }

  protected void addParameterFromXmlNode( Query query, Element paramElement ) {
    String name = paramElement.getAttribute( "name" ); //$NON-NLS-1$
    String type = paramElement.getAttribute( "type" ); //$NON-NLS-1$
    String defaultValue = paramElement.getAttribute( "defaultValue" ); //$NON-NLS-1$

    Object defaultVal = null;
    if ( name != null && type != null && StringUtils.isNotEmpty( defaultValue ) ) {
      DataType dataType = DataType.valueOf( type.toUpperCase() );
      defaultVal = parseDefaultValue( defaultValue, dataType );
    }
    Parameter param = new Parameter( name, DataType.valueOf( type.toUpperCase() ), defaultVal );
    query.getParameters().add( param );
  }

  protected Object parseDefaultValue( String defaultValue, DataType dataType ) {
    if ( defaultValue == null ) {
      return null;
    }

    CSVTokenizer csvt = new CSVTokenizer( defaultValue, "|", "\"", true );
    switch ( dataType ) {
      case BOOLEAN:
        return parseBooleanDefaultParam( defaultValue, csvt );
      case NUMERIC:
        return parseDoubleDefaultParam( defaultValue, csvt );
      default:
        return parseStringDefaultParam( defaultValue, csvt );
    }
  }

  private Object parseBooleanDefaultParam( String defaultValue, CSVTokenizer csvt ) {
    if ( csvt.countTokens() == 1 ) {
      return Boolean.parseBoolean( csvt.nextToken() );
    } else {
      ArrayList<Boolean> vals = new ArrayList<Boolean>();
      while ( csvt.hasMoreTokens() ) {
        String token = csvt.nextToken();
        vals.add( Boolean.parseBoolean( token ) );
      }
      return vals.toArray( new Boolean[0] );
    }
  }

  private Object parseDoubleDefaultParam( String defaultValue, CSVTokenizer csvt ) {
    if ( csvt.countTokens() == 1 ) {
      return Double.parseDouble( csvt.nextToken() );
    } else {
      ArrayList<Double> vals = new ArrayList<Double>();
      while ( csvt.hasMoreTokens() ) {
        String token = csvt.nextToken();
        vals.add( Double.parseDouble( token ) );
      }
      return vals.toArray( new Double[0] );
    }
  }

  private Object parseStringDefaultParam( String defaultValue, CSVTokenizer csvt ) {
    if ( csvt.countTokens() == 1 ) {
      return csvt.nextToken();
    } else {
      ArrayList<String> vals = new ArrayList<String>();
      while ( csvt.hasMoreTokens() ) {
        String token = csvt.nextToken();
        vals.add( token );
      }
      return vals.toArray( new String[0] );
    }
  }

  protected void addSelectionToDocument( Document doc, Selection selection, Element selectionElement ) {
    addTextElement( doc, selectionElement, "view", selection.getCategory().getId() ); //$NON-NLS-1$
    addTextElement( doc, selectionElement, "column", selection.getLogicalColumn().getId() ); //$NON-NLS-1$
    if ( selection.getAggregationType() != null ) {
      addTextElement( doc, selectionElement, "aggregation", selection.getAggregationType().toString() ); //$NON-NLS-1$
    }
  }

  protected void addConstraintToDocument( Document doc, Constraint constraint, Element constraintElement ) {
    addTextElement( doc, constraintElement,
        "operator", constraint.getCombinationType() == null ? "" : constraint.getCombinationType().toString() ); //$NON-NLS-1$  //$NON-NLS-2$
    addTextElement( doc, constraintElement, "condition", constraint.getFormula() ); //$NON-NLS-1$
  }

  protected void addOrderByToDocument( Document doc, Order order, Element orderElement ) {

    // test that this is lower case
    addTextElement( doc, orderElement, "direction", order.getType().toString() ); //$NON-NLS-1$
    addTextElement( doc, orderElement, "view_id", order.getSelection().getCategory().getId() ); //$NON-NLS-1$
    addTextElement( doc, orderElement, "column_id", order.getSelection().getLogicalColumn().getId() ); //$NON-NLS-1$
    if ( order.getSelection().getAggregationType() != null ) {
      addTextElement( doc, orderElement, "aggregation", order.getSelection().getAggregationType().toString() ); //$NON-NLS-1$
    }

  }

  protected void addTextElement( Document doc, Element element, String elementName, String text ) {
    Element childElement = doc.createElement( elementName );
    childElement.appendChild( doc.createTextNode( text ) );
    element.appendChild( childElement );
  }

  //
  // FROM XML
  //

  public Query fromXML( IMetadataDomainRepository repo, String XML ) throws PentahoMetadataException {
    if ( XML == null ) {
      throw new PentahoMetadataException( Messages.getErrorString( "QueryXmlHelper.ERROR_0008_XML_NULL" ) ); //$NON-NLS-1$
    }
    Document doc;

    // Check and open XML document
    try {
      DocumentBuilderFactory dbf = XmiParser.createSecureDocBuilderFactory();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.parse( new InputSource( new java.io.StringReader( XML ) ) );
    } catch ( ParserConfigurationException pcx ) {
      throw new PentahoMetadataException( pcx );
    } catch ( SAXException sex ) {
      throw new PentahoMetadataException( sex );
    } catch ( IOException iex ) {
      throw new PentahoMetadataException( iex );
    }
    return fromXML( repo, doc );
  }

  public Query fromXML( IMetadataDomainRepository repo, Document doc ) throws PentahoMetadataException {

    // get the domain id
    String domainId = getElementText( doc, "domain_id" ); //$NON-NLS-1$
    Domain domain = repo.getDomain( domainId );
    if ( domain == null ) {
      if ( domainId != null && !domainId.contains( ".xmi" ) ) {
        domain = repo.getDomain( domainId + ".xmi" );
      }
      if ( domain != null ) {
        logger.warn( String.format( "Metadata model [%1$s] was requested, but the model doesn't exist. "
            + "Substituting [%1$s.xmi] instead as a legacy fallback. "
            + "Please change your reports to reference %1$s.xmi instead", domainId ) );
      }
    }
    if ( domain == null ) {
      // need to throw an error
      logger.error( String.format( "Metadata model [%1$s] doesn't exist. "
          + "Please check the existence of the model", domainId ) );
      throw new PentahoMetadataException( Messages.getErrorString(
          "QueryXmlHelper.ERROR_0009_DOMAIN_INSTANCE_NULL", domainId ) ); //$NON-NLS-1$
    }

    return fromXML( doc, domain );
  }

  protected Query fromXML( Document doc, Domain domain ) throws PentahoMetadataException {

    // get the model id
    String modelId = getElementText( doc, "model_id" ); //$NON-NLS-1$
    LogicalModel model = domain.findLogicalModel( modelId ); // This is the business model that was selected.

    if ( model == null ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "QueryXmlHelper.ERROR_0010_MODEL_NOT_FOUND", modelId ) ); //$NON-NLS-1$
    }

    Query query = new Query( domain, model );

    // get the options node if it exists...
    NodeList nList = doc.getElementsByTagName( "options" ); //$NON-NLS-1$
    if ( nList != null ) {
      Element optionElement;
      for ( int i = 0; i < nList.getLength(); i++ ) {
        optionElement = (Element) nList.item( i );
        setOptionsFromXmlNode( query, optionElement );
      }
    }

    NodeList paramNodes = doc.getElementsByTagName( "parameter" ); //$NON-NLS-1$
    Element paramElement;
    for ( int idx = 0; idx < paramNodes.getLength(); idx++ ) {
      paramElement = (Element) paramNodes.item( idx );
      addParameterFromXmlNode( query, paramElement );
    }

    // process the selections
    NodeList nodes = doc.getElementsByTagName( "selection" ); //$NON-NLS-1$
    Element selectionElement;
    for ( int idx = 0; idx < nodes.getLength(); idx++ ) {
      selectionElement = (Element) nodes.item( idx );
      addSelectionFromXmlNode( query, selectionElement );
    }

    // process the constraints
    nodes = doc.getElementsByTagName( "constraint" ); //$NON-NLS-1$
    Element constraintElement;
    for ( int idx = 0; idx < nodes.getLength(); idx++ ) {
      constraintElement = (Element) nodes.item( idx );
      addConstraintFromXmlNode( query, constraintElement );
    }

    // process the constraints
    nodes = doc.getElementsByTagName( "order" ); //$NON-NLS-1$
    Element orderElement;
    for ( int idx = 0; idx < nodes.getLength(); idx++ ) {
      orderElement = (Element) nodes.item( idx );
      addOrderByFromXmlNode( query, orderElement );
    }

    return query;
  }

  protected void setOptionsFromXmlNode( Query query, Element optionElement ) throws PentahoMetadataException {
    // Keep default behavior...
    query.setDisableDistinct( false );
    query.setLimit( -1 );
    if ( optionElement != null ) {
      String disableStr = getElementText( optionElement, "disable_distinct" ); //$NON-NLS-1$
      if ( disableStr != null ) {
        query.setDisableDistinct( disableStr.equalsIgnoreCase( "true" ) ); //$NON-NLS-1$
      }
      String limitStr = getElementText( optionElement, "limit" );
      if ( limitStr != null ) {
        try {
          query.setLimit( Integer.parseInt( limitStr ) );
        } catch ( NumberFormatException e ) {
          throw new PentahoMetadataException( Messages.getErrorString( "QueryXmlHelper.ERROR_0017_CANNOT_PARSE_LIMIT" ) ); //$NON-NLS-1$
        }
      }
    }

  }

  protected void addSelectionFromXmlNode( Query query, Element selectionElement ) {

    NodeList viewnodes = selectionElement.getElementsByTagName( "view" ); //$NON-NLS-1$
    NodeList nodes = selectionElement.getElementsByTagName( "column" ); //$NON-NLS-1$
    if ( nodes.getLength() == 0 ) {
      // should throw exception here
      return;
    }
    String columnId = XMLHandler.getNodeValue( nodes.item( 0 ) );
    String viewId = null;
    Category category = null;
    if ( viewnodes.getLength() != 0 ) {
      // this is due to legacy reasons, the query doesn't really need the category.
      viewId = XMLHandler.getNodeValue( viewnodes.item( 0 ) );
      category = query.getLogicalModel().findCategory( viewId );
    }
    LogicalColumn column = null;
    if ( category != null ) {
      column = category.findLogicalColumn( columnId );
    } else {
      column = query.getLogicalModel().findLogicalColumnInCategories( columnId );
    }
    if ( column != null ) {
      AggregationType aggsetting = null;
      NodeList aggnodes = selectionElement.getElementsByTagName( "aggregation" ); //$NON-NLS-1$
      if ( aggnodes.getLength() > 0 ) {
        String aggvalue = XMLHandler.getNodeValue( aggnodes.item( 0 ) );
        AggregationType setting = AggregationType.valueOf( aggvalue.toUpperCase() );
        if ( setting == null ) {
          Messages.getErrorString( "QueryXmlHelper.ERROR_0011_AGG_NOT_RECOGNIZED", columnId, aggvalue ); //$NON-NLS-1$
        } else {
          // verify that the setting is one of the options for this business column
          if ( ( column.getAggregationType() == setting ) || column.getAggregationList() != null
              && column.getAggregationList().contains( setting ) ) {
            aggsetting = setting;
          } else {
            Messages.getErrorString( "QueryXmlHelper.ERROR_0012_INVALID_AGG_FOR_BUSINESS_COL", columnId, aggvalue ); //$NON-NLS-1$
          }
        }
      }

      query.getSelections().add( new Selection( category, column, aggsetting ) );
    } else {
      // print a warning message
      Messages.getErrorString( "QueryXmlHelper.ERROR_0013_BUSINESS_COL_NOT_FOUND", viewId, columnId ); //$NON-NLS-1$
    }
  }

  protected void addConstraintFromXmlNode( Query query, Element constraintElement ) throws PentahoMetadataException {

    NodeList nodes = constraintElement.getElementsByTagName( "operator" ); //$NON-NLS-1$
    String operator = null;
    if ( nodes.getLength() > 0 ) {
      operator = XMLHandler.getNodeValue( nodes.item( 0 ) );
    }

    if ( ( operator == null ) || ( operator.trim().equals( "" ) ) ) { //$NON-NLS-1$
      operator = "AND"; //$NON-NLS-1$
    }

    nodes = constraintElement.getElementsByTagName( "condition" ); //$NON-NLS-1$
    String cond = null;
    if ( nodes.getLength() > 0 ) {
      cond = XMLHandler.getNodeValue( nodes.item( 0 ) );
    }

    nodes = constraintElement.getElementsByTagName( "view_id" ); //$NON-NLS-1$
    String view_id = null;
    if ( nodes.getLength() > 0 ) {
      view_id = XMLHandler.getNodeValue( nodes.item( 0 ) );
    }

    nodes = constraintElement.getElementsByTagName( "column_id" ); //$NON-NLS-1$
    String column_id = null;
    if ( nodes.getLength() > 0 ) {
      column_id = XMLHandler.getNodeValue( nodes.item( 0 ) );
    }

    if ( cond == null ) {
      throw new PentahoMetadataException( Messages.getErrorString( "QueryXmlHelper.ERROR_0014_NULL_CONDITION" ) ); //$NON-NLS-1$
    }

    if ( view_id == null || column_id == null ) {
      // new function support
      query.getConstraints().add( new Constraint( CombinationType.getCombinationType( operator.toUpperCase() ), cond ) );
    } else {
      // backwards compatibility
      query.getConstraints().add(
          new Constraint( CombinationType.getCombinationType( operator.toUpperCase() ),
              "[" + view_id + "." + column_id + "] " + cond ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

  }

  protected void addOrderByFromXmlNode( Query query, Element orderElement ) throws PentahoMetadataException {
    String view_id = null;
    String column_id = null;
    String aggregation = null;
    Order.Type orderType = Order.Type.ASC;
    NodeList nodes = orderElement.getElementsByTagName( "direction" ); //$NON-NLS-1$
    if ( nodes.getLength() > 0 ) {
      String orderTypeStr = XMLHandler.getNodeValue( nodes.item( 0 ) );
      if ( orderTypeStr != null ) {
        orderType = Order.Type.valueOf( orderTypeStr.toUpperCase() );
      }
    }
    nodes = orderElement.getElementsByTagName( "view_id" ); //$NON-NLS-1$
    if ( nodes.getLength() > 0 ) {
      view_id = XMLHandler.getNodeValue( nodes.item( 0 ) );
    }
    nodes = orderElement.getElementsByTagName( "column_id" ); //$NON-NLS-1$
    if ( nodes.getLength() > 0 ) {
      column_id = XMLHandler.getNodeValue( nodes.item( 0 ) );
    }

    nodes = orderElement.getElementsByTagName( "aggregation" ); //$NON-NLS-1$
    if ( nodes.getLength() > 0 ) {
      aggregation = XMLHandler.getNodeValue( nodes.item( 0 ) );
    }

    if ( ( view_id != null ) && ( column_id != null ) ) {
      Category category = query.getLogicalModel().findCategory( view_id );
      if ( category.findLogicalColumn( column_id ) == null ) {
        return;
      }
      addOrderBy( query, view_id, column_id, aggregation, orderType );
    }
  }

  protected void addOrderBy( Query query, String categoryId, String columnId, String aggregation, Order.Type orderType )
    throws PentahoMetadataException {
    Category category = query.getLogicalModel().findCategory( categoryId );
    if ( category == null ) {
      throw new PentahoMetadataException( Messages.getErrorString(
          "QueryXmlHelper.ERROR_0015_BUSINESS_CATEGORY_NOT_FOUND", categoryId ) ); //$NON-NLS-1$ 
    }
    addOrderBy( query, category, columnId, aggregation, orderType );
  }

  protected void addOrderBy( Query query, Category category, String columnId, String aggregation, Order.Type orderType )
    throws PentahoMetadataException {

    if ( category == null ) {
      throw new PentahoMetadataException( Messages.getErrorString( "QueryXmlHelper.ERROR_0016_BUSINESS_CATEGORY_NULL" ) ); //$NON-NLS-1$ 
    }

    LogicalColumn column = category.findLogicalColumn( columnId );
    if ( column == null ) {
      throw new PentahoMetadataException( Messages.getErrorString(
          "QueryXmlHelper.ERROR_0013_BUSINESS_COL_NOT_FOUND", category.getId(), columnId ) ); //$NON-NLS-1$ 
    }

    // this code verifies the aggregation setting provided is a
    // valid option
    AggregationType aggsetting = null;
    if ( aggregation != null ) {
      AggregationType setting = AggregationType.valueOf( aggregation.toUpperCase() );
      if ( ( column.getAggregationType() == setting ) || column.getAggregationList() != null
          && column.getAggregationList().contains( setting ) ) {
        aggsetting = setting;
      }
    }

    query.getOrders().add( new Order( new Selection( category, column, aggsetting ), orderType ) );
  }

  protected String getElementText( Element ele, String name ) {
    try {
      return ele.getElementsByTagName( name ).item( 0 ).getFirstChild().getNodeValue();
    } catch ( Exception e ) {
      return null;
    }
  }

  protected String getElementText( Document doc, String name ) {
    try {
      return doc.getElementsByTagName( name ).item( 0 ).getFirstChild().getNodeValue();
    } catch ( Exception e ) {
      return null;
    }
  }

}
