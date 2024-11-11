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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.pms.MetadataTestBase;
import org.pentaho.pms.mql.MQLQueryImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings( "deprecation" )
public class ThinQueryIT {

  @BeforeClass
  public static void initKettle() throws Exception {
    MetadataTestBase.initKettleEnvironment();
  }

  @Test
  public void testQueryXmlSerialization() {
    Domain domain = TestHelper.getBasicDomain();
    LogicalModel model = domain.findLogicalModel( "MODEL" );
    Query query = new Query( domain, model );

    Category category = model.findCategory( "CATEGORY" );
    LogicalColumn column = category.findLogicalColumn( "LC_CUSTOMERNAME" );

    query.getParameters().add( new Parameter( "test", DataType.STRING, "val" ) );

    query.getSelections().add( new Selection( category, column, null ) );

    query.getConstraints().add( new Constraint( CombinationType.AND, "[CATEGORY.LC_CUSTOMERNAME] = \"bob\"" ) );

    query.getOrders().add( new Order( new Selection( category, column, null ), Order.Type.ASC ) );

    QueryXmlHelper helper = new QueryXmlHelper();
    String xml = helper.toXML( query );

    InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
    try {
      repo.storeDomain( domain, true );
    } catch ( Exception e ) {
      fail();
    }
    Query newQuery = null;
    try {
      newQuery = helper.fromXML( repo, xml );
    } catch ( Exception e ) {
      fail();
    }
    // verify that when we serialize and deserialize, the xml stays the same.
    assertEquals( xml, helper.toXML( newQuery ) );
  }

  @Test
  public void testQueryConversion() throws Exception {
    Domain domain = TestHelper.getBasicDomain();
    LogicalModel model = domain.findLogicalModel( "MODEL" );
    Query query = new Query( domain, model );

    Category category = model.findCategory( "CATEGORY" );
    LogicalColumn column = category.findLogicalColumn( "LC_CUSTOMERNAME" );
    query.getSelections().add( new Selection( category, column, null ) );

    query.getConstraints().add( new Constraint( CombinationType.AND, "[CATEGORY.LC_CUSTOMERNAME] = \"bob\"" ) );

    query.getOrders().add( new Order( new Selection( category, column, null ), Order.Type.ASC ) );

    MQLQueryImpl impl = null;
    try {
      impl = ThinModelConverter.convertToLegacy( query, null );
    } catch ( Exception e ) {
      fail();
    }
    Assert.assertNotNull( impl );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          LT.customername AS COL0\n" + "FROM \n"
        + "          (select * from customers) LT\n" + "WHERE \n" + "        (\n" + "          (\n"
        + "              LT.customername  = 'bob'\n" + "          )\n" + "        )\n" + "ORDER BY \n"
        + "          COL0\n", impl.getQuery().getQuery() );

    query.setLimit( 10 );
    impl = ThinModelConverter.convertToLegacy( query, null );
    assertEquals( 10, impl.getLimit() );
  }

  public static void printOutJava( String sql ) {
    String[] lines = sql.split( "\n" );
    for ( int i = 0; i < lines.length; i++ ) {
      System.out.print( "        \"" + lines[i] );
      if ( i == lines.length - 1 ) {
        System.out.println( "\\n\"" );
      } else {
        System.out.println( "\\n\" + " );
      }
    }
  }
}
