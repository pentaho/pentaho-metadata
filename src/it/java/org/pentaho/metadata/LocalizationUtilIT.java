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

package org.pentaho.metadata;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.util.LocalizationUtil;
import org.pentaho.metadata.util.XmiParser;

/**
 * TODO: Bring sorter over from code
 * 
 * @author gorman
 * 
 */
public class LocalizationUtilIT {

  @Test
  public void testImportExportOfMultibyteChars() throws Exception {
    // 日本語

    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/simple_model.xmi" ) );
    Category category = new Category( domain.getLogicalModels().get( 0 ) );
    category.setId( "TEST_WITH_日本語_CHARS" );
    LocalizedString str = new LocalizedString( "en_US", "日本語" );
    category.setName( str );
    domain.getLogicalModels().get( 0 ).addCategory( category );

    LocalizationUtil util = new LocalizationUtil();
    Properties props = util.exportLocalizedProperties( domain, "en_US" );

    Assert.assertEquals( props.getProperty( "[LogicalModel-BV_MODEL_1].[Category-TEST_WITH_日本語_CHARS].[name]" ),
        "日本語" );

    props.setProperty( "[LogicalModel-BV_MODEL_1].[name]", "日本語" );
    props
        .setProperty( "[LogicalModel-BV_MODEL_1].[Category-TEST_WITH_日本語_CHARS].[name]", "2nd Version 日本語" );

    Assert.assertEquals( "en_US", domain.getLocales().get( 0 ).getCode() );

    util.importLocalizedProperties( domain, props, "jp" );

    Assert.assertEquals( "en_US", domain.getLocales().get( 0 ).getCode() );
    Assert.assertEquals( "jp", domain.getLocales().get( 1 ).getCode() );

    Assert.assertEquals( domain.getLogicalModels().get( 0 ).getName( "jp" ), "日本語" );
    Assert.assertEquals( domain.getLogicalModels().get( 0 ).getCategories().get( 1 ).getName( "jp" ),
        "2nd Version 日本語" );
  }

  @Test
  public void testNonHappyPaths() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/simple_model.xmi" ) );
    LocalizationUtil util = new LocalizationUtil();
    Properties props = util.exportLocalizedProperties( domain, "en_US" );

    // invalid references
    props.setProperty( "[Test].[Property]", "Test Value" );
    props.setProperty( "[PhysicalModel-SampleDataDatasource2].[PT_ORDERDETAILS].[name]", "ORDERDETAILS" );
    props.setProperty( "[LogicalModel-BV_MODEL_2].[LogicalTable-BT_ORDERDETAILS_ORDERDETAILS].[name]", "ORDERDETAILS" );
    props.setProperty( "[PhysicalModel-SampleDataDatasource].[PT_ORDERS2].[ORDERNUMBER].[name]", "ORDERNUMBER" );
    props.setProperty( "[LogicalModel-BV_MODEL_1].[Category-2CAT_Category].[name]", "Category" );
    props.setProperty( "[PhysicalModel-SampleDataDatasource].[PT_ORDERDETAILS2].[ORDERLINENUMBER].[name]",
        "ORDERLINENUMBER" );
    props.setProperty( "[Base].[comments2]", "Comment String" );
    props.setProperty( "[PhysicalModel-SampleDataDatasource].[PT_ORDERS].[STATUS2].[name]", "STATUS" );
    props.setProperty( "[LogicalModel-BV_MODEL_1].[LogicalTable2-BT_ORDERS_ORDERS].[name]", "ORDERS" );
    props.setProperty( "[PhysicalModel-SampleDataDatasource].[PT_ORDERDETAILS].[ORDERNUMBER2].[name]", "ORDERNUMBER" );
    props.setProperty( "[LogicalModel-BV_MODEL_1].[name2]", "Model 1" );
    props.setProperty( "[PhysicalModel-SampleDataDatasource].[PT_ORDERS2].[name]", "ORDERS" );
    props.setProperty( "[Base].[Base Child2].[comments]", "Child Comment String" );

    // unparsable strings
    props.setProperty( "[Base].[Ba[[se Child2].[comments]", "Child Comment String" );
    props.setProperty( "[Base].[Base Chi]]ld2].[comments]", "Child Comment String" );
    props.setProperty( "[Base].[Base Chi]]ld2].[comm[]ents]", "Child Comment String" );
    props.setProperty( "[B..[]ase].[Base Chi]]ld2].[comm..ents]", "Child Comment String" );
    props.setProperty( "[Base", "Child Comment String" );
    props.setProperty( "Base", "Child Comment String" );

    List<String> messages = util.analyzeImport( domain, props, "en_TEST" );

    Assert.assertEquals( 19, messages.size() );

    Assert.assertEquals( "en_US", domain.getLocales().get( 0 ).getCode() );

    util.importLocalizedProperties( domain, props, "en_TEST" );

    Assert.assertEquals( "en_US", domain.getLocales().get( 0 ).getCode() );
    Assert.assertEquals( "en_TEST", domain.getLocales().get( 1 ).getCode() );

  }

  @Test
  public void testAnalyzeImport() throws Exception {
    // this test exercises all known places where localized strings are located
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/simple_model.xmi" ) );
    LocalizationUtil util = new LocalizationUtil();

    Properties props = util.exportLocalizedProperties( domain, "en_US" );

    List<String> messages = util.analyzeImport( domain, props, "en_US" );

    Assert.assertEquals( 0, messages.size() );

    props.remove( "[Base].[comments]" );
    props.setProperty( "[Test].[Property]", "Test Value" );

    messages = util.analyzeImport( domain, props, "en_US" );

    Assert.assertEquals( 2, messages.size() );
    Assert.assertEquals( messages.get( 0 ), "Key [Base].[comments] is missing from imported bundle" );
    Assert.assertEquals( messages.get( 1 ), "Imported key [Test].[Property] is not referenced in domain" );
  }

  @Test
  public void testSimpleModel() throws Exception {
    // this test exercises all known places where localized strings are located
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/simple_model.xmi" ) );
    LocalizationUtil util = new LocalizationUtil();

    Properties props = util.exportLocalizedProperties( domain, "en_US" );

    // There are 153 externalized strings in steel wheels
    Assert.assertEquals( 19, props.size() );
    for ( Object key : props.keySet() ) {
      System.out.println( key + "=" + props.get( key ) );
    }

    Assert.assertEquals( props.getProperty( "[IPhysicalModel-SampleDataDatasource].[PT_ORDERDETAILS].[name]" ),
        "ORDERDETAILS" );
    Assert.assertEquals( props
        .getProperty( "[LogicalModel-BV_MODEL_1].[LogicalTable-BT_ORDERDETAILS_ORDERDETAILS].[name]" ), "ORDERDETAILS" );
    Assert.assertEquals( props.getProperty( "[IPhysicalModel-SampleDataDatasource].[PT_ORDERS].[ORDERNUMBER].[name]" ),
        "ORDERNUMBER" );
    Assert.assertEquals( props.getProperty( "[LogicalModel-BV_MODEL_1].[Category-CAT_Category].[name]" ), "Category" );
    Assert.assertEquals( props
        .getProperty( "[IPhysicalModel-SampleDataDatasource].[PT_ORDERDETAILS].[ORDERLINENUMBER].[name]" ),
        "ORDERLINENUMBER" );
    Assert.assertEquals( props.getProperty( "[Base].[comments]" ), "Comment String" );
    Assert.assertEquals( props.getProperty( "[IPhysicalModel-SampleDataDatasource].[PT_ORDERS].[STATUS].[name]" ),
        "STATUS" );
    Assert.assertEquals( props.getProperty( "[LogicalModel-BV_MODEL_1].[LogicalTable-BT_ORDERS_ORDERS].[name]" ),
        "ORDERS" );
    Assert.assertEquals( props
        .getProperty( "[IPhysicalModel-SampleDataDatasource].[PT_ORDERDETAILS].[ORDERNUMBER].[name]" ), "ORDERNUMBER" );
    Assert.assertEquals( props.getProperty( "[LogicalModel-BV_MODEL_1].[name]" ), "Model 1" );
    Assert.assertEquals( props.getProperty( "[IPhysicalModel-SampleDataDatasource].[PT_ORDERS].[name]" ), "ORDERS" );
    Assert.assertEquals( props.getProperty( "[Base].[Base Child].[comments]" ), "Child Comment String" );

    util.importLocalizedProperties( domain, props, "en_TEST" );

    Properties newProps = util.exportLocalizedProperties( domain, "en_TEST" );

    Assert.assertEquals( newProps.size(), props.size() );
    for ( Object key : props.keySet() ) {
      String k = (String) key;
      Assert.assertEquals( props.getProperty( k ), newProps.getProperty( k ) );
    }

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes() );
    Domain domain2 = parser.parseXmi( is );
    Properties newProps2 = util.exportLocalizedProperties( domain, "en_TEST" );
    Assert.assertEquals( newProps2.size(), props.size() );

    for ( Object key : props.keySet() ) {
      String k = (String) key;
      Assert.assertEquals( props.getProperty( k ), newProps2.getProperty( k ) );
    }

    Assert.assertEquals( "en_US", domain.getLocales().get( 0 ).getCode() );
    Assert.assertEquals( "en_TEST", domain.getLocales().get( 1 ).getCode() );
  }

  @Test
  public void testLocalization() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/samples/steelwheels.xmi" ) );
    LocalizationUtil util = new LocalizationUtil();

    //
    // Test exporting
    //

    Properties props = util.exportLocalizedProperties( domain, "en_US" );

    // There are 277 externalized strings in steel wheels
    Assert.assertEquals( 277, props.size() );

    // Spot Checks

    // physical column name
    Assert.assertEquals( props.getProperty( "[IPhysicalModel-SampleData].[PT_EMPLOYEES].[EMAIL].[name]" ), "Email" );
    // category name
    Assert.assertEquals( props.getProperty( "[LogicalModel-BV_ORDERS].[Category-BC_CUSTOMER_W_TER_].[name]" ),
        "Customer" );
    // model name
    Assert.assertEquals( props.getProperty( "[LogicalModel-BV_ORDERS].[name]" ), "Orders" );
    // logical table name
    Assert.assertEquals( props.getProperty( "[LogicalModel-BV_ORDERS].[LogicalTable-BT_PRODUCTS_PRODUCTS].[name]" ),
        "Products" );
    // logical column comments
    Assert
        .assertEquals(
            props
                .getProperty( "[LogicalModel-BV_ORDERS].[LogicalTable-BT_ORDERDETAILS_ORDERDETAILS].[BC_ORDERDETAILS_TOTAL].[comments]" ),
            "This field is computed as Quantity Ordered times Price Sold" );

    //
    // test importing
    //

    util.importLocalizedProperties( domain, props, "en_TEST" );
    Properties newProps = util.exportLocalizedProperties( domain, "en_TEST" );
    Assert.assertEquals( newProps.size(), props.size() );

    for ( Object key : props.keySet() ) {
      String k = (String) key;
      Assert.assertEquals( props.getProperty( k ), newProps.getProperty( k ) );
    }

    //
    // test serializing import
    //

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes( "UTF-8" ) );
    Domain domain2 = parser.parseXmi( is );
    Properties newProps2 = util.exportLocalizedProperties( domain, "en_TEST" );
    Assert.assertEquals( newProps2.size(), props.size() );

    for ( Object key : props.keySet() ) {
      String k = (String) key;
      Assert.assertEquals( props.getProperty( k ), newProps2.getProperty( k ) );
    }

    Assert.assertEquals( "en_US", domain.getLocales().get( 0 ).getCode() );
    Assert.assertEquals( "es", domain.getLocales().get( 1 ).getCode() );
    Assert.assertEquals( "en_TEST", domain.getLocales().get( 2 ).getCode() );
  }

  /**
   * Tests the import of localization properties exported from a model that was published from aAgile BI to a BI Server.
   **/
  @Test
  public void testImportPropertiesIntoAgileBiPublishedModel() throws Exception {

    // this test exercises all known places where localized strings are located
    XmiParser parser = new XmiParser();
    Domain domain = null;
    domain = parser.parseXmi( getClass().getResourceAsStream( "/agileBiGenerated.xmi" ) );
    LocalizationUtil util = new LocalizationUtil();

    // Load the properties from the exported properties file
    Properties exportedPropertyFileProps = new Properties();
    exportedPropertyFileProps.load( getClass().getResourceAsStream( "/agileBiGenerated_en_US.properties" ) );

    // import the properties into the domain
    List<String> messages = util.analyzeImport( domain, exportedPropertyFileProps, "en_US" );
    if ( messages.isEmpty() ) {
      Assert.assertTrue( messages.isEmpty() );
    } else {
      for ( String message : messages ) {
        System.out.println( message );
      }
      Assert.fail( "The analysis of the export failed." );
    }
  }

  @Test
  public void testImportedLocaleIntoDomainWithSameLocale() throws Exception {

    // this test exercises all known places where localized strings are located
    String locale = "en_US";
    XmiParser parser = new XmiParser();
    Domain domain = null;
    domain = parser.parseXmi( getClass().getResourceAsStream( "/modelWith_EN_US.xmi" ) );
    LocalizationUtil util = new LocalizationUtil();

    // Load the properties from the exported properties file
    Properties exportedPropertyFileProps = new Properties();
    exportedPropertyFileProps.load( getClass().getResourceAsStream( "/modelWith_EN_US_en_US.properties" ) );

    // import the properties into the domain
    util.importLocalizedProperties( domain, exportedPropertyFileProps, locale );

    // Out imported localization will have the string "en_US" before each non empty string.
    // take the printing of all the properties out before checking in
    Properties en_US_FromDomain = util.exportLocalizedProperties( domain, locale );
    for ( Entry<Object, Object> entry : en_US_FromDomain.entrySet() ) {
      System.out.println( entry.getKey().toString() + " => " + entry.getValue().toString() );
    }

    assertEquals( "en_US Num children at home", en_US_FromDomain
        .get( "[IPhysicalModel-foodmart].[PT_CUSTOMER].[num_children_at_home].[name]" ) );

  }

  /**
   * Tests the import of localization properties exported from a model that was published from aAgile BI to a BI Server.
   **/
  @Test
  public void testImportPropertiesExportedFromPME() throws Exception {

    // this test exercises all known places where localized strings are located
    XmiParser parser = new XmiParser();
    Domain domain = null;
    domain = parser.parseXmi( getClass().getResourceAsStream( "/exportedFromPME.xmi" ) );
    LocalizationUtil util = new LocalizationUtil();

    // Load the properties from the exported properties file
    Properties exportedPropertyFileProps = new Properties();
    exportedPropertyFileProps.load( getClass().getResourceAsStream( "/exportedFromPME_en_US.properties" ) );

    // import the properties into the domain
    List<String> messages = util.analyzeImport( domain, exportedPropertyFileProps, "en_US" );
    if ( messages.isEmpty() ) {
      Assert.assertTrue( messages.isEmpty() );
    } else {
      for ( String message : messages ) {
        System.out.println( message );
      }
      Assert.fail( "The analysis of the export failed." );
    }
  }

}
