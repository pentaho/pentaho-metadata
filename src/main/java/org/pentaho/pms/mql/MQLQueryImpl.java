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

package org.pentaho.pms.mql;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.CWMException;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.util.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.query.model.Query
 */
public class MQLQueryImpl implements MQLQuery {

  private static final Log logger = LogFactory.getLog( MQLQuery.class );

  public static int DOMAIN_TYPE_RELATIONAL = 1;

  public static int DOMAIN_TYPE_OLAP = 2; // NOT SUPPORTED YET

  private int domainType = DOMAIN_TYPE_RELATIONAL;

  private List<Selection> selections = new ArrayList<Selection>();

  private List<WhereCondition> constraints = new ArrayList<WhereCondition>();

  private List<OrderBy> order = new ArrayList<OrderBy>();

  private BusinessModel model;

  private String locale;

  private SchemaMeta schemaMeta;

  private boolean disableDistinct; // = false;

  private int limit = -1;

  private CwmSchemaFactoryInterface cwmSchemaFactory;

  private SQLGenerator sqlGenerator = new SQLGenerator();

  // if null, pull databaseMeta out of selections()
  private DatabaseMeta databaseMeta = null;

  /**
   * This constructor is used when constructing an MQL Query from scratch
   *
   * @param schemaMeta   schema metadata
   * @param model        business model
   * @param databaseMeta database metadata
   * @param locale       locale string
   */
  public MQLQueryImpl( SchemaMeta schemaMeta, BusinessModel model, DatabaseMeta databaseMeta, String locale ) {
    this.databaseMeta = databaseMeta;
    this.schemaMeta = schemaMeta;
    this.model = model;
    this.locale = locale;
  }

  /**
   * This constructor is used when constructing an MQL Query from XML
   *
   * @param XML          xml to parse
   * @param databaseMeta database metadata
   * @param locale       locale string
   * @param factory      cwm schema factory.
   * @throws PentahoMetadataException
   */
  public MQLQueryImpl( String XML, DatabaseMeta databaseMeta, String locale, CwmSchemaFactoryInterface factory )
    throws PentahoMetadataException {
    this.databaseMeta = databaseMeta;
    this.locale = locale;
    this.cwmSchemaFactory = factory;
    fromXML( XML );
  }

  public void addSelection( Selection selection ) {
    if ( !selections.contains( selection ) ) {
      selections.add( selection );
    }
  }

  public List<Selection> getSelections() {
    return selections;
  }

  public DatabaseMeta getDatabaseMeta() {
    if ( databaseMeta == null && selections.size() > 0 ) {
      return selections.get( 0 ).getBusinessColumn().getPhysicalColumn().getTable().getDatabaseMeta();
    }
    return databaseMeta;
  }

  public void addConstraint( String operator, String condition ) throws PentahoMetadataException {
    WhereCondition where = new WhereCondition( model, getDatabaseMeta(), operator, condition );
    constraints.add( where );
  }

  public void addOrderBy( String categoryId, String columnId, String aggregation, boolean ascending )
    throws PentahoMetadataException {
    BusinessCategory rootCat = model.getRootCategory();
    BusinessCategory businessCategory = rootCat.findBusinessCategory( categoryId );
    if ( businessCategory == null ) {
      throw new PentahoMetadataException( Messages.getErrorString(
        "MQLQuery.ERROR_0014_BUSINESS_CATEGORY_NOT_FOUND", categoryId ) ); //$NON-NLS-1$
    }
    addOrderBy( businessCategory, columnId, aggregation, ascending );
  }

  public void addOrderBy( BusinessCategory businessCategory, String columnId, String aggregation, boolean ascending )
    throws PentahoMetadataException {

    if ( businessCategory == null ) {
      throw new PentahoMetadataException(
        Messages.getErrorString( "MQLQuery.ERROR_0015_BUSINESS_CATEGORY_NULL" ) ); //$NON-NLS-1$
    }

    BusinessColumn businessColumn = businessCategory.findBusinessColumn( columnId );
    if ( businessColumn == null ) {
      throw new PentahoMetadataException( Messages.getErrorString(
        "MQLQuery.ERROR_0016_BUSINESS_COLUMN_NOT_FOUND", businessCategory.getId(), columnId ) ); //$NON-NLS-1$
    }

    // this code verifies the aggregation setting provided is a
    // valid option
    AggregationSettings aggsetting = null;
    if ( aggregation != null ) {
      AggregationSettings setting = AggregationSettings.getType( aggregation );
      if ( ( businessColumn.getAggregationType() == setting ) || businessColumn.getAggregationList() != null
        && businessColumn.getAggregationList().contains( setting ) ) {
        aggsetting = setting;
      }
    }

    OrderBy orderBy = new OrderBy( new Selection( businessColumn, aggsetting ), ascending );
    order.add( orderBy );
  }

  public MappedQuery getQuery() throws PentahoMetadataException {
    if ( model == null || selections.size() == 0 ) {
      return null;
    }

    // generate global row level security constraint
    WhereCondition securityConstraint = null;
    if ( cwmSchemaFactory != null ) {
      String mqlSecurityConstraint = cwmSchemaFactory.generateRowLevelSecurityConstraint( model );
      if ( StringUtils.isNotBlank( mqlSecurityConstraint ) ) {
        securityConstraint = new WhereCondition( model, getDatabaseMeta(), "AND", mqlSecurityConstraint );
      }
    }

    return sqlGenerator.getSQL( model, selections, constraints, order, getDatabaseMeta(), locale, this.disableDistinct,
      this.limit, securityConstraint );
  }

  public String getXML() {
    try {
      StringWriter stringWriter = new StringWriter();
      StreamResult result = new StreamResult();
      result.setWriter( stringWriter );
      TransformerFactory factory = TransformerFactory.newInstance();
      Document doc = getDocument();
      if ( doc != null ) {
        factory.newTransformer().transform( new DOMSource( doc ), result );
        return stringWriter.getBuffer().toString();
      }
    } catch ( Exception e ) {
      logger.error( Messages.getErrorString( "MQLQuery.ERROR_0013_GET_XML_FAILED" ), e ); //$NON-NLS-1$
    }
    return null;
  }

  public Document getDocument() {
    Document doc;

    try {
      // create an XML document
      DocumentBuilderFactory dbf = XmiParser.createSecureDocBuilderFactory();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.newDocument();
      Element mqlElement = doc.createElement( "mql" ); //$NON-NLS-1$
      doc.appendChild( mqlElement );

      if ( addToDocument( mqlElement, doc ) ) {
        return doc;
      } else {
        return null;
      }
    } catch ( Exception e ) {
      logger.error( Messages.getErrorString( "MQLQuery.ERROR_0012_GET_DOCUMENT_FAILED" ), e ); //$NON-NLS-1$
    }
    return null;
  }

  public boolean addToDocument( Element mqlElement, Document doc ) {

    try {

      if ( schemaMeta == null ) {
        logger.error( Messages.getErrorString( "MQLQuery.ERROR_0002_META_SCHEMA_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      if ( model == null ) {
        logger.error( Messages.getErrorString( "MQLQuery.ERROR_0003_BUSINESS_MODEL_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      // insert the domain information
      Element typeElement = doc.createElement( "domain_type" ); //$NON-NLS-1$
      typeElement.appendChild( doc.createTextNode(
        ( domainType == DOMAIN_TYPE_RELATIONAL ) ? "relational" : "olap" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      mqlElement.appendChild( typeElement );

      // insert the domain information
      String data = schemaMeta.getDomainName();
      if ( data != null ) {
        Element domainIdElement = doc.createElement( "domain_id" ); //$NON-NLS-1$
        domainIdElement.appendChild( doc.createTextNode( data ) );
        mqlElement.appendChild( domainIdElement );
      } else {
        logger.error( Messages.getErrorString( "MQLQuery.ERROR_0004_DOMAIN_ID_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      // insert the model information
      data = model.getId();
      if ( data != null ) {
        Element modelIdElement = doc.createElement( "model_id" ); //$NON-NLS-1$
        modelIdElement.appendChild( doc.createTextNode( data ) );
        mqlElement.appendChild( modelIdElement );
      } else {
        logger.error( Messages.getErrorString( "MQLQuery.ERROR_0005_MODEL_ID_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      data = model.getDisplayName( locale );
      if ( data != null ) {
        Element modelNameElement = doc.createElement( "model_name" ); //$NON-NLS-1$
        modelNameElement.appendChild( doc.createTextNode( data ) );
        mqlElement.appendChild( modelNameElement );
      } else {
        logger.error( Messages.getErrorString( "MQLQuery.ERROR_0006_MODEL_NAME_NULL" ) ); //$NON-NLS-1$
        return false;
      }

      // Add additional options
      Element optionsElement = doc.createElement( "options" ); //$NON-NLS-1$
      mqlElement.appendChild( optionsElement );
      addOptionsToDocument( doc, optionsElement );

      // insert the selections
      Element selectionsElement = doc.createElement( "selections" ); //$NON-NLS-1$
      for ( Selection selection : selections ) {
        Element selectionElement = doc.createElement( "selection" ); //$NON-NLS-1$
        addSelectionToDocument( doc, selection, selectionElement );
        selectionsElement.appendChild( selectionElement );
      }
      mqlElement.appendChild( selectionsElement );

      // insert the contraints
      Element constraintsElement = doc.createElement( "constraints" ); //$NON-NLS-1$
      for ( WhereCondition condition : constraints ) {
        Element constraintElement = doc.createElement( "constraint" ); //$NON-NLS-1$
        addConstraintToDocument( doc, condition, constraintElement );
        constraintsElement.appendChild( constraintElement );
      }
      mqlElement.appendChild( constraintsElement );

      // insert the orders
      Element ordersElement = doc.createElement( "orders" ); //$NON-NLS-1$
      for ( OrderBy orderBy : order ) {
        Element orderElement = doc.createElement( "order" ); //$NON-NLS-1$
        addOrderByToDocument( doc, orderBy, orderElement );
        ordersElement.appendChild( orderElement );

      }
      mqlElement.appendChild( ordersElement );

    } catch ( Exception e ) {
      logger.error( Messages.getErrorString( "MQLQuery.ERROR_0011_ADD_TO_DOCUMENT_FAILED" ), e ); //$NON-NLS-1$
    }
    return true;

  }

  protected void addOptionsToDocument( Document doc, Element optionsElement ) {
    Element disableDistinct = doc.createElement( "disable_distinct" ); //$NON-NLS-1$
    String data = Boolean.toString( this.disableDistinct );
    disableDistinct.appendChild( doc.createTextNode( data ) );
    optionsElement.appendChild( disableDistinct );

    Element limit = doc.createElement( "limit" ); //$NON-NLS-1$
    String limitData = String.valueOf( this.limit );
    limit.appendChild( doc.createTextNode( limitData ) );
    optionsElement.appendChild( limit );
  }

  protected void addSelectionToDocument( Document doc, Selection selection, Element selectionElement ) {
    BusinessColumn column = selection.getBusinessColumn();
    Element element = doc.createElement( "view" ); //$NON-NLS-1$

    // element.appendChild( doc.createTextNode( column.getBusinessTable().getId() ) );
    //
    // Work-around for PMD-93 - not using BusinessView in the MQL.
    BusinessCategory rootCat = model.getRootCategory();
    BusinessCategory businessCategory = rootCat.findBusinessCategoryForBusinessColumn( column );
    element.appendChild( doc.createTextNode( businessCategory.getId() ) );

    selectionElement.appendChild( element );

    element = doc.createElement( "column" ); //$NON-NLS-1$
    element.appendChild( doc.createTextNode( column.getId() ) );
    selectionElement.appendChild( element );

    if ( selection.getAggregationType() != null ) {
      element = doc.createElement( "aggregation" );
      element.appendChild( doc.createTextNode( selection.getAggregationType().getCode() ) );
      selectionElement.appendChild( element );
    }
  }

  protected void addConstraintToDocument( Document doc, WhereCondition condition, Element constraintElement ) {
    Element element = doc.createElement( "operator" ); //$NON-NLS-1$
    element.appendChild(
      doc.createTextNode( condition.getOperator() == null ? "" : condition.getOperator() ) ); //$NON-NLS-1$
    constraintElement.appendChild( element );

    element = doc.createElement( "condition" ); //$NON-NLS-1$
    element.appendChild( doc.createTextNode( condition.getCondition() ) );
    constraintElement.appendChild( element );

    // Save the localized names...
    /*
     * TODO String[] locales = condition.getConcept().getUsedLocale();
     *
     * element = doc.createElement( "name" ); for (int i=0;i<locales.length;i++) { String name =
     * condition.getName(locales[i]); if (!Const.isEmpty( name) ) { Element locElement = doc.createElement("locale");
     * locElement.appendChild( doc.createTextNode(locales[i]) ); element.appendChild(locElement); Element valElement =
     * doc.createElement("value"); locElement.appendChild( doc.createTextNode(name) ); element.appendChild(valElement);
     * } } constraintElement.appendChild( element );
     *
     * element = doc.createElement( "description" ); for (int i=0;i<locales.length;i++) { String description =
     * condition.getDescription(locales[i]); if (!Const.isEmpty( description) ) { Element locElement =
     * doc.createElement("locale"); locElement.appendChild( doc.createTextNode(locales[i]) );
     * element.appendChild(locElement); Element valElement = doc.createElement("value"); locElement.appendChild(
     * doc.createTextNode(description) ); element.appendChild(valElement); } } constraintElement.appendChild( element );
     */
  }

  protected void addOrderByToDocument( Document doc, OrderBy orderBy, Element orderElement ) {
    Element element = doc.createElement( "direction" ); //$NON-NLS-1$
    element.appendChild( doc.createTextNode( orderBy.isAscending() ? "asc" : "desc" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    orderElement.appendChild( element );

    // Work-around for PMD-93 - Need this to be better into the future...
    element = doc.createElement( "view_id" ); //$NON-NLS-1$
    BusinessCategory rootCat = model.getRootCategory();
    BusinessCategory businessView =
      rootCat.findBusinessCategoryForBusinessColumn( orderBy.getSelection().getBusinessColumn() );
    element.appendChild( doc.createTextNode( businessView.getId() ) );

    orderElement.appendChild( element );

    element = doc.createElement( "column_id" ); //$NON-NLS-1$
    element.appendChild( doc.createTextNode( orderBy.getSelection().getBusinessColumn().getId() ) );
    orderElement.appendChild( element );

    if ( orderBy.getSelection().getAggregationType() != null ) {
      element = doc.createElement( "aggregation" );
      element.appendChild( doc.createTextNode( orderBy.getSelection().getAggregationType().getCode() ) );
      orderElement.appendChild( element );
    }

  }

  public void fromXML( String XML ) throws PentahoMetadataException {
    if ( XML == null ) {
      throw new PentahoMetadataException( Messages.getErrorString( "MQLQuery.ERROR_0017_XML_NULL" ) ); //$NON-NLS-1$
    }

    Document doc;
    // Check and open XML document
    try {
      DocumentBuilderFactory dbf = XmiParser.createSecureDocBuilderFactory();
      DocumentBuilder docBuilder = dbf.newDocumentBuilder();
      doc = docBuilder.parse( new InputSource( new java.io.StringReader( XML ) ) );
    } catch ( ParserConfigurationException pcx ) {
      throw new PentahoMetadataException( pcx );
    } catch ( SAXException sex ) {
      throw new PentahoMetadataException( sex );
    } catch ( IOException iex ) {
      throw new PentahoMetadataException( iex );
    }
    fromXML( doc );
  }

  public void fromXML( String XML, SchemaMeta localSchemaMeta ) throws PentahoMetadataException {
    if ( XML == null ) {
      throw new PentahoMetadataException( Messages.getErrorString( "MQLQuery.ERROR_0017_XML_NULL" ) ); //$NON-NLS-1$
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
    fromXML( doc, localSchemaMeta );
  }

  public void fromXML( Document doc ) throws PentahoMetadataException {
    // get the domain type
    String domainTypeStr = getElementText( doc, "domain_type" ); //$NON-NLS-1$
    if ( "relational".equals( domainTypeStr ) ) { //$NON-NLS-1$
      domainType = DOMAIN_TYPE_RELATIONAL;
    } else if ( "olap".equals( domainTypeStr ) ) { //$NON-NLS-1$
      domainType = DOMAIN_TYPE_OLAP;
    } else {
      // need to throw an error
      throw new PentahoMetadataException( Messages.getErrorString(
        "MQLQuery.ERROR_0007_INVALID_DOMAIN_TYPE", domainTypeStr ) ); //$NON-NLS-1$
    }

    // get the domain id
    String domainId = getElementText( doc, "domain_id" ); //$NON-NLS-1$

    checkDomainExists( domainId );

    CWM cwm = CWM.getInstance( domainId );
    if ( cwm == null ) {
      // need to throw an error
      throw new PentahoMetadataException( Messages.getErrorString(
        "MQLQuery.ERROR_0008_CWM_DOMAIN_INSTANCE_NULL", domainId ) ); //$NON-NLS-1$
    }

    if ( cwmSchemaFactory == null ) {
      cwmSchemaFactory = Settings.getCwmSchemaFactory();
    }

    fromXML( doc, cwmSchemaFactory.getSchemaMeta( cwm ) );
  }

  public void fromXML( Document doc, SchemaMeta localSchemaMeta ) throws PentahoMetadataException {

    schemaMeta = localSchemaMeta;

    // get the model id
    String modelId = getElementText( doc, "model_id" ); //$NON-NLS-1$
    model = localSchemaMeta.findModel( modelId ); // This is the business model that was selected.

    if ( model == null ) {
      throw new PentahoMetadataException(
        Messages.getErrorString( "MQLQuery.ERROR_0009_MODEL_NOT_FOUND", modelId ) ); //$NON-NLS-1$
    }

    // get the options node if it exists...
    NodeList nList = doc.getElementsByTagName( "options" ); //$NON-NLS-1$
    if ( nList != null ) {
      Element optionElement;
      for ( int i = 0; i < nList.getLength(); i++ ) {
        optionElement = (Element) nList.item( i );
        setOptionsFromXmlNode( optionElement );
      }
    }

    // process the selections
    NodeList nodes = doc.getElementsByTagName( "selection" ); //$NON-NLS-1$
    Element selectionElement;
    for ( int idx = 0; idx < nodes.getLength(); idx++ ) {
      selectionElement = (Element) nodes.item( idx );
      addSelectionFromXmlNode( selectionElement );
    }

    // process the constraints
    nodes = doc.getElementsByTagName( "constraint" ); //$NON-NLS-1$
    Element constraintElement;
    for ( int idx = 0; idx < nodes.getLength(); idx++ ) {
      constraintElement = (Element) nodes.item( idx );
      addConstraintFromXmlNode( constraintElement );
    }

    // process the constraints
    nodes = doc.getElementsByTagName( "order" ); //$NON-NLS-1$
    Element orderElement;
    for ( int idx = 0; idx < nodes.getLength(); idx++ ) {
      orderElement = (Element) nodes.item( idx );
      addOrderByFromXmlNode( orderElement );
    }
  }

  protected void addSelectionFromXmlNode( Element selectionElement ) {
    NodeList nodes = selectionElement.getElementsByTagName( "column" ); //$NON-NLS-1$
    if ( nodes.getLength() > 0 ) {
      String columnId = XMLHandler.getNodeValue( nodes.item( 0 ) );
      BusinessColumn businessColumn = getModel().findBusinessColumn( columnId );
      if ( businessColumn != null ) {
        AggregationSettings aggsetting = null;
        NodeList aggnodes = selectionElement.getElementsByTagName( "aggregation" ); //$NON-NLS-1$
        if ( aggnodes.getLength() > 0 ) {
          String aggvalue = XMLHandler.getNodeValue( aggnodes.item( 0 ) );
          AggregationSettings setting = AggregationSettings.getType( aggvalue );
          if ( setting == null ) {
            Messages.getErrorString( "MQLQuery.ERROR_0018_AGG_NOT_RECOGNIZED", columnId, aggvalue );
          } else {
            // verify that the setting is one of the options for this business column
            if ( ( businessColumn.getAggregationType() == setting ) || businessColumn.getAggregationList() != null
              && businessColumn.getAggregationList().contains( setting ) ) {
              aggsetting = setting;
            } else {
              Messages.getErrorString( "MQLQuery.ERROR_0019_INVALID_AGG_FOR_BUSINESS_COL", columnId, aggvalue );
            }
          }
        }

        addSelection( new Selection( businessColumn, aggsetting ) );
      } else {
        // print a warning message
        Messages.getErrorString( "MQLQuery.ERROR_0020_BUSINESS_COL_NOT_FOUND", columnId );
      }
    }
  }

  protected void setOptionsFromXmlNode( Element optionElement ) throws PentahoMetadataException {
    // Keep default behavior...
    this.disableDistinct = false;
    this.limit = -1;
    if ( optionElement != null ) {
      String disableStr = getElementText( optionElement, "disable_distinct" ); //$NON-NLS-1$
      if ( disableStr != null ) {
        this.disableDistinct = disableStr.equalsIgnoreCase( "true" ); //$NON-NLS-1$
      }
      String limitStr = getElementText( optionElement, "limit" );
      if ( limitStr != null ) {
        try {
          this.limit = Integer.parseInt( limitStr );
        } catch ( NumberFormatException e ) {
          throw new PentahoMetadataException(
            Messages.getErrorString( "MQLQuery.ERROR_0021_CANNOT_PARSE_LIMIT" ) ); //$NON-NLS-1$
        }
      }
    }
  }

  protected void addOrderByFromXmlNode( Element orderElement ) throws PentahoMetadataException {
    boolean ascending = true;
    String view_id = null;
    String column_id = null;
    String aggregation = null;

    NodeList nodes = orderElement.getElementsByTagName( "direction" ); //$NON-NLS-1$
    if ( nodes.getLength() > 0 ) {
      ascending = XMLHandler.getNodeValue( nodes.item( 0 ) ).equals( "asc" ); //$NON-NLS-1$
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
      addOrderBy( view_id, column_id, aggregation, ascending );
    }
  }

  protected void addConstraintFromXmlNode( Element constraintElement ) throws PentahoMetadataException {

    NodeList nodes = constraintElement.getElementsByTagName( "operator" ); //$NON-NLS-1$
    String operator = null;
    if ( nodes.getLength() > 0 ) {
      operator = XMLHandler.getNodeValue( nodes.item( 0 ) );
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
      throw new PentahoMetadataException(
        Messages.getErrorString( "MQLQuery.ERROR_0001_NULL_CONDITION" ) ); //$NON-NLS-1$
    }

    if ( view_id == null || column_id == null ) {
      // new function support
      addConstraint( operator, cond );
    } else {
      // backwards compatibility
      addConstraint( operator,
        "[" + view_id + "." + column_id + "] " + cond ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

  }

  /**
   * @return the constraints
   */
  public List<WhereCondition> getConstraints() {
    return constraints;
  }

  /**
   * @param constraints the constraints to set
   */
  public void setConstraints( List<WhereCondition> constraints ) {
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
  public void setLocale( String locale ) {
    this.locale = locale;
  }

  /**
   * @return the order
   */
  public List<OrderBy> getOrder() {
    return order;
  }

  /**
   * @param order the order to set
   */
  public void setOrder( List<OrderBy> order ) {
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
  public void setSchemaMeta( SchemaMeta schemaMeta ) {
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
  public void setModel( BusinessModel model ) {
    this.model = model;
  }

  /**
   * @param selections the selections to set
   */
  public void setSelections( List<Selection> selections ) {
    this.selections = selections;
  }

  public void setDisableDistinct( boolean value ) {
    this.disableDistinct = value;
  }

  public boolean getDisableDistinct() {
    return this.disableDistinct;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit( int limit ) {
    this.limit = limit;
  }

  // dom utility methods

  protected String getElementText( Document doc, String name ) {
    try {
      Node item = doc.getElementsByTagName( name ).item( 0 );
      if ( item == null ) {
        return null;
      }
      Node firstChild = item.getFirstChild();
      if ( firstChild == null ) {
        return "";
      }
      return firstChild.getNodeValue();
    } catch ( Exception e ) {
      return null;
    }
  }

  protected String getElementText( Element ele, String name ) {
    try {
      return ele.getElementsByTagName( name ).item( 0 ).getFirstChild().getNodeValue();
    } catch ( Exception e ) {
      return null;
    }
  }

  @VisibleForTesting
  protected void checkDomainExists( String domainId ) throws PentahoMetadataException {
    try {
      if ( !CWM.exists( domainId ) ) {
        throw new PentahoMetadataException( Messages.getErrorString(
                "MQLQuery.ERROR_0010_CWM_DOMAIN_NOT_FOUND", domainId ) );
      }
    } catch ( CWMException e ) {
      throw new PentahoMetadataException( Messages.getErrorString(
              "MQLQuery.ERROR_0010_CWM_DOMAIN_NOT_FOUND", domainId ) );
    }
  }
}
