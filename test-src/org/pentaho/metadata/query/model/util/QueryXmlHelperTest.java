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
 * Copyright 2005 - 2017 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.metadata.query.model.util;

import junit.framework.Assert;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.metadata.TestHelper;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class QueryXmlHelperTest {

  private QueryXmlHelper helper;
  private DocumentBuilderFactory documentBuilderFactory;
  private DocumentBuilder db;
  private Document doc;
  private Query query;
  private IMetadataDomainRepository metadataDomainRepository;

  @Before
  public void init() throws Exception {
    helper = new QueryXmlHelper();
    Domain domain = TestHelper.getBasicDomain();
    LogicalModel model = TestHelper.buildDefaultModel();
    domain.addLogicalModel( model );
    model.setId( "MODEL1" );
    query = new Query( domain, model );

    LogicalColumn column = new LogicalColumn();
    column.setId( "LC_Test_Column1" );
    LogicalColumn column2 = new LogicalColumn();
    column2.setId( "LC_Test_Column2" );
    Category category = new Category();
    category.getLogicalColumns().add( column );
    query.getLogicalModel().getCategories().add( category );

    LogicalTable lt = new LogicalTable();
    lt.getLogicalColumns().add( column );
    lt.getLogicalColumns().add( column2 );
    query.getLogicalModel().getLogicalTables().add( lt );

    documentBuilderFactory = DocumentBuilderFactory.newInstance();
    db = documentBuilderFactory.newDocumentBuilder();
    doc = db.newDocument();
    metadataDomainRepository = new InMemoryMetadataDomainRepository();
    metadataDomainRepository.storeDomain( domain, true );
  }

  @Test
  public void testAddParameterFromXmlNode() throws Exception {
    Element paramElement = doc.createElement( "parameter" ); //$NON-NLS-1$
    paramElement.setAttribute( "name", "param1" ); //$NON-NLS-1$
    paramElement.setAttribute( "type", "STRING" ); //$NON-NLS-1$
    paramElement.setAttribute( "defaultValue", "a|b" ); //$NON-NLS-1$ //$NON-NLS-2$

    helper.addParameterFromXmlNode( query, paramElement );
    Assert.assertEquals( 2, ( (Object[]) query.getParameters().get( 0 ).getDefaultValue() ).length );
  }

  @Test
  public void testAddSelectionFromXmlNode() throws Exception {
    String mql =
        "<mql>" + "<domain_type>relational</domain_type>" + "<domain_id>DOMAIN</domain_id>"
            + "<model_id>MODEL1</model_id>" + "<options>" + "<disable_distinct>false</disable_distinct>"
            + "</options>" + "<parameters></parameters>" + "<selections>" + "<selection>" + "<table>Test</table>"
            + "<column>LC_Test_Column1</column>" + "<aggregation>NONE</aggregation>"
            + "</selection><selection>" + "<table>Test</table>" + "<column>LC_Test_Column2</column>"
            + "<aggregation>NONE</aggregation>" + "</selection></selections>" + "<constraints></constraints>"
            + "<orders></orders>" + "</mql>";
    Query q = helper.fromXML( metadataDomainRepository, mql );

    Assert.assertEquals( 1, q.getSelections().size() );
    Assert.assertEquals( "LC_Test_Column1", q.getSelections().get( 0 ).getLogicalColumn().getId() );
  }

  @Test
  public void testParseMutivaluedDefault_String() throws Exception {
    Object values = helper.parseDefaultValue( "a|b|c|d|e", DataType.STRING );
    Assert.assertTrue( values instanceof String[] );
    String[] stringValues = (String[]) values;
    Assert.assertEquals( 5, stringValues.length );
    Assert.assertEquals( "a", stringValues[0] );
    Assert.assertEquals( "b", stringValues[1] );
    Assert.assertEquals( "c", stringValues[2] );
    Assert.assertEquals( "d", stringValues[3] );
    Assert.assertEquals( "e", stringValues[4] );

    values = helper.parseDefaultValue( "a", DataType.STRING );
    Assert.assertTrue( values instanceof String );
    Assert.assertEquals( "a", values );

    // try "a|b"|"c|d"
    values = helper.parseDefaultValue( "\"a|b\"|\"c|d\"", DataType.STRING );
    Assert.assertTrue( values instanceof String[] );
    stringValues = (String[]) values;
    Assert.assertEquals( 2, stringValues.length );
    Assert.assertEquals( "a|b", stringValues[0] );
    Assert.assertEquals( "c|d", stringValues[1] );
  }

  @Test
  public void testParseMultivaluedDefault_Numeric() throws Exception {
    Object values = helper.parseDefaultValue( "1|2|3|4|5", DataType.NUMERIC );
    Assert.assertTrue( values instanceof Double[] );
    Double[] numericValues = (Double[]) values;
    Assert.assertEquals( 5, numericValues.length );
    Assert.assertEquals( 1D, numericValues[0] );
    Assert.assertEquals( 2D, numericValues[1] );
    Assert.assertEquals( 3D, numericValues[2] );
    Assert.assertEquals( 4D, numericValues[3] );
    Assert.assertEquals( 5D, numericValues[4] );
  }

  @Test
  public void testParseMultivaluedDefault_Boolean() throws Exception {
    Object values = helper.parseDefaultValue( "true|false", DataType.BOOLEAN );
    Assert.assertTrue( values instanceof Boolean[] );
    Boolean[] boolValues = (Boolean[]) values;
    Assert.assertEquals( 2, boolValues.length );
    Assert.assertTrue( boolValues[0] );
    Assert.assertFalse( boolValues[1] );
  }

  @Test
  public void testLimit() throws Exception {
    final int LIMIT = 10;

    String xml;
    String limitString;

    // to xml, no limit
    xml = helper.toXML( query );
    limitString = getLimitFromXML( xml );
    Assert.assertTrue( Integer.parseInt( limitString ) < 0 );

    // from xml, no limit
    query = helper.fromXML( metadataDomainRepository, xml );
    Assert.assertTrue( query.getLimit() < 0 );

    // to xml, limit
    query.setLimit( LIMIT );
    xml = helper.toXML( query );
    limitString = getLimitFromXML( xml );
    Assert.assertEquals( String.valueOf( LIMIT ), limitString );

    // from xml, limit
    query = helper.fromXML( metadataDomainRepository, xml );
    Assert.assertEquals( LIMIT, query.getLimit() );

    // legacy (no limit element in xml)
    xml = helper.toXML( query );
    xml = xml.replaceAll( "<limit>\\s*\\w*\\s*</limit>", "" );
    query = helper.fromXML( metadataDomainRepository, xml );
    Assert.assertTrue( query.getLimit() < 0 );

    // invalid limit in xml
    query.setLimit( 123 );
    xml = helper.toXML( query );
    xml = xml.replaceAll( "<limit>\\s*123\\s*</limit>", "<limit>abc</limit>" );

    try {
      query = helper.fromXML( metadataDomainRepository, xml );
      Assert.fail();
    } catch ( PentahoMetadataException e ) {
      // expected
    }
  }

  @Test
  public void testFromXML() throws Exception {
    Domain domain = TestHelper.getBasicDomain();
    LogicalModel model = TestHelper.buildDefaultModel();
    domain.addLogicalModel( model );
    model.setId( "MODEL2" );
    Query query = new Query( domain, model );
    String xml = helper.toXML( query );
    try {
      query = helper.fromXML( metadataDomainRepository, xml );
      Assert.fail();
    } catch ( PentahoMetadataException e ) {
      // expected
    }
  }

  private String getLimitFromXML( String xml ) throws Exception {
    Document doc = db.parse( new InputSource( new java.io.StringReader( xml ) ) );
    if ( doc.getElementsByTagName( "options" ).getLength() > 0 ) {
      Element optionsElement = ( (Element) doc.getElementsByTagName( "options" ).item( 0 ) );
      if ( optionsElement.getElementsByTagName( "limit" ).getLength() > 0 ) {
        return optionsElement.getElementsByTagName( "limit" ).item( 0 ).getFirstChild().getNodeValue();
      }
    }
    return null;
  }

}
