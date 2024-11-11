/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.metadata;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.security.SecurityOwner.OwnerType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.LocalizationUtil;
import org.pentaho.metadata.util.SerializationService;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.MetadataTestBase;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.messages.util.LocaleHelper;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

@SuppressWarnings( "deprecation" )
public class ThinModelIT {

  @BeforeClass
  public static void initKettle() throws Exception {
    MetadataTestBase.initKettleEnvironment();
  }

  @Test
  public void testSqlPhysicalModel() {

    // this is the minimum physical sql model, it could
    // theoretically be used to execute sql directly.

    SqlPhysicalModel model = new SqlPhysicalModel();
    SqlDataSource dataSource = new SqlDataSource();
    dataSource.setDatabaseName( "SampleData" );
    model.setDatasource( dataSource );
    SqlPhysicalTable table = new SqlPhysicalTable( model );
    model.getPhysicalTables().add( table );
    table.setTargetTableType( TargetTableType.INLINE_SQL );
    table.setTargetTable( "select * from customers" );

    // basic tests

    assertEquals( "SampleData", model.getDatasource().getDatabaseName() );
    assertEquals( 1, model.getPhysicalTables().size() );
    assertEquals( TargetTableType.INLINE_SQL, model.getPhysicalTables().get( 0 ).getTargetTableType() );
    assertEquals( "select * from customers", model.getPhysicalTables().get( 0 ).getTargetTable() );
    assertEquals( 1, model.getPhysicalTables().size() );
  }

  @Test
  public void testSqlLogicalModel() {

    String locale = LocaleHelper.getLocale().toString();

    // this sql model is the minimum required for
    // MQL execution

    SqlPhysicalModel model = new SqlPhysicalModel();
    SqlDataSource dataSource = new SqlDataSource();
    dataSource.setDatabaseName( "SampleData" );
    model.setDatasource( dataSource );
    SqlPhysicalTable table = new SqlPhysicalTable( model );
    table.setId( "PT1" );
    model.getPhysicalTables().add( table );
    table.setTargetTableType( TargetTableType.INLINE_SQL );
    table.setTargetTable( "select distinct customername from customers" );

    SqlPhysicalColumn column = new SqlPhysicalColumn( table );
    column.setId( "PC1" );
    column.setTargetColumn( "customername" );
    column.setName( new LocalizedString( locale, "Customer Name" ) );
    column.setDescription( new LocalizedString( locale, "Customer Description" ) );
    column.setDataType( DataType.STRING );

    // logical model

    LogicalModel logicalModel = new LogicalModel();
    model.setId( "MODEL" );
    model.setName( new LocalizedString( locale, "My Model" ) );
    model.setDescription( new LocalizedString( locale, "A Description of the Model" ) );

    LogicalTable logicalTable = new LogicalTable();
    logicalTable.setPhysicalTable( table );

    LogicalColumn logicalColumn = new LogicalColumn();
    logicalColumn.setId( "LC_CUSTOMERNAME" );
    logicalColumn.setPhysicalColumn( column );

    // test name inheritance
    assertEquals( column.getName().getString( Locale.getDefault().toString() ), logicalColumn.getName()
        .getString( Locale.getDefault().toString() ) );

    // test datatype inheritance
    assertEquals( column.getDataType(), logicalColumn.getDataType() );

    Category mainCategory = new Category();
    mainCategory.setId( "CATEGORY" );
    mainCategory.setName( new LocalizedString( locale, "Category" ) );

    // replacement for formula / is exact could be
    // target column + target column type (calculated, exact, etc)
  }

  @Test
  public void testSerializeSqlPhysicalModel() {

    String locale = LocaleHelper.getLocale().toString();

    SqlPhysicalModel model = new SqlPhysicalModel();
    SqlDataSource dataSource = new SqlDataSource();
    dataSource.setDatabaseName( "SampleData" );
    model.setDatasource( dataSource );
    SqlPhysicalTable table = new SqlPhysicalTable( model );
    model.getPhysicalTables().add( table );
    table.setTargetTableType( TargetTableType.INLINE_SQL );
    table.setTargetTable( "select * from customers" );

    SqlPhysicalColumn column = new SqlPhysicalColumn( table );
    column.setTargetColumn( "customername" );
    column.setName( new LocalizedString( locale, "Customer Name" ) );
    column.setDescription( new LocalizedString( locale, "Customer Name Desc" ) );
    column.setDataType( DataType.STRING );

    table.getPhysicalColumns().add( column );

    LogicalModel logicalModel = new LogicalModel();
    model.setId( "MODEL" );
    model.setName( new LocalizedString( locale, "My Model" ) );
    model.setDescription( new LocalizedString( locale, "A Description of the Model" ) );

    LogicalTable logicalTable = new LogicalTable();
    logicalTable.setPhysicalTable( table );

    logicalModel.getLogicalTables().add( logicalTable );

    LogicalColumn logicalColumn = new LogicalColumn();
    logicalColumn.setId( "LC_CUSTOMERNAME" );
    logicalColumn.setPhysicalColumn( column );

    logicalTable.addLogicalColumn( logicalColumn );

    Category mainCategory = new Category();
    mainCategory.setId( "CATEGORY" );
    mainCategory.setName( new LocalizedString( locale, "Category" ) );
    mainCategory.addLogicalColumn( logicalColumn );

    logicalModel.getCategories().add( mainCategory );

    Domain domain = new Domain();
    domain.addPhysicalModel( model );
    domain.addLogicalModel( logicalModel );

    // basic tests
    SerializationService service = new SerializationService();

    String xml = service.serializeDomain( domain );

    Domain domain2 = service.deserializeDomain( xml );

    assertEquals( 1, domain2.getPhysicalModels().size() );
    SqlPhysicalModel model2 = (SqlPhysicalModel) domain2.getPhysicalModels().get( 0 );
    assertEquals( "SampleData", model2.getDatasource().getDatabaseName() );
    assertEquals( 1, model2.getPhysicalTables().size() );
    assertEquals( TargetTableType.INLINE_SQL, model2.getPhysicalTables().get( 0 ).getTargetTableType() );

    assertEquals( 1, domain2.getLogicalModels().size() );
    assertEquals( 1, domain2.getLogicalModels().get( 0 ).getCategories().size() );
    assertEquals( 1, domain2.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );
    assertEquals( domain2.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().get( 0 ),
        domain2.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().get( 0 ) );
    assertEquals( "Customer Name", domain2.getLogicalModels().get( 0 ).getCategories().get( 0 )
        .getLogicalColumns().get( 0 ).getName().getString( locale ) );
    assertEquals( "Customer Name Desc", domain2.getLogicalModels().get( 0 ).getCategories().get( 0 )
        .getLogicalColumns().get( 0 ).getDescription().getString( locale ) );

  }

  private void deleteFile( String filename ) {
    File f = new File( filename );
    if ( f.exists() ) {
      f.delete();
    }
  }

  @Test
  public void testLocalizationUtilWithConversion() throws Exception {

    deleteFile( "mdr.btb" );
    deleteFile( "mdr.btd" );
    deleteFile( "mdr.btx" );

    CWM cwm = null;
    try {
      cwm = CWM.getInstance( "SteelWheels", true ); //$NON-NLS-1$
      assertNotNull( "CWM singleton instance is null", cwm );
      cwm.importFromXMI( getClass().getResourceAsStream( "/samples/steelwheels.xmi" ) ); //$NON-NLS-1$
    } catch ( Exception e ) {
      fail();
    }
    CwmSchemaFactory factory = new CwmSchemaFactory();

    SchemaMeta schemaMeta = factory.getSchemaMeta( cwm );

    Domain domain = null;

    try {
      domain = ThinModelConverter.convertFromLegacy( schemaMeta );
    } catch ( Exception e ) {
      fail();
    }

    LocalizationUtil util = new LocalizationUtil();
    Properties props = util.exportLocalizedProperties( domain, "en_US" );

    XmiParser parser = new XmiParser();

    Domain domain2 = parser.parseXmi( getClass().getResourceAsStream( "/samples/steelwheels.xmi" ) );

    List<String> list = util.analyzeImport( domain2, props, "en_US" );

    assertEquals( 0, list.size() );
  }

  @Test
  public void loadLegacyXMI() {

    deleteFile( "mdr.btb" );
    deleteFile( "mdr.btd" );
    deleteFile( "mdr.btx" );

    CWM cwm = null;
    try {
      cwm = CWM.getInstance( "Orders", true ); //$NON-NLS-1$
      assertNotNull( "CWM singleton instance is null", cwm );
      cwm.importFromXMI( getClass().getResourceAsStream( "/samples/steelwheels.xmi" ) ); //$NON-NLS-1$
    } catch ( Exception e ) {
      fail();
    }
    CwmSchemaFactory factory = new CwmSchemaFactory();

    SchemaMeta schemaMeta = factory.getSchemaMeta( cwm );

    try {
      ThinModelConverter.convertFromLegacy( schemaMeta );
    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testLocalizedString() {
    LocalizedString ls = new LocalizedString();
    ls.setString( "en_US", "Test 1" );
    ls.setString( "es", "Test 2" );

    String result = ls.getString( "nl_BE.UTF-8" );
    Assert.assertNull( result );
    result = ls.getLocalizedString( "nl_BE.UTF-8" );
    assertEquals( "Test 1", result );
    result = ls.getLocalizedString( "en_US" );
    assertEquals( "Test 1", result );

    ls = new LocalizedString();
    ls.setString( "es", "Test 2" );
    result = ls.getLocalizedString( "en_US" );
    Assert.assertNull( result );
  }

  @Test
  public void testToFromLegacy() {
    Domain domain = TestHelper.getBasicDomain();
    SchemaMeta meta = null;
    try {
      meta = ThinModelConverter.convertToLegacy( domain );
    } catch ( Exception e ) {
      fail();
    }

    String locale = Locale.getDefault().toString();

    assertEquals( 1, meta.getLocales().nrLocales() );
    assertEquals( "en_US", meta.getLocales().getLocale( 0 ).getCode() );

    // verify conversion worked.
    BusinessModel model = meta.findModel( "MODEL" );
    assertNotNull( model );
    assertEquals( "My Model", model.getName( locale ) );
    assertEquals( "A Description of the Model", model.getDescription( locale ) );

    BusinessCategory cat = model.getRootCategory().findBusinessCategory( "CATEGORY" );
    assertNotNull( cat );
    assertEquals( "Category", cat.getName( locale ) );

    assertEquals( 1, cat.getBusinessColumns().size() );

    // this tests the inheritance of physical cols made it through
    BusinessColumn col = cat.getBusinessColumn( 0 );
    assertEquals( "Customer Name", col.getName( locale ) );
    assertEquals( "Customer Name Desc", col.getDescription( locale ) );
    assertNotNull( col.getBusinessTable() );
    assertEquals( "LT", col.getBusinessTable().getId() );

    assertEquals( col.getDataType(), DataTypeSettings.STRING );
    assertEquals( "select * from customers", col.getBusinessTable().getTargetTable() );
    assertEquals( "select * from customers", col.getPhysicalColumn().getTable().getTargetTable() );
    assertEquals( "customername", col.getPhysicalColumn().getFormula() );
    assertFalse( col.getPhysicalColumn().isExact() );

    Domain domain2 = null;

    try {
      domain2 = ThinModelConverter.convertFromLegacy( meta );
    } catch ( Exception e ) {
      fail();
    }

    assertEquals( 1, domain2.getLocales().size() );
    assertEquals( "en_US", domain2.getLocales().get( 0 ).getCode() );

    // verify conversion worked.
    LogicalModel logicalModel = domain2.findLogicalModel( "MODEL" );
    assertNotNull( logicalModel );
    assertEquals( "My Model", logicalModel.getName().getString( locale ) );
    assertEquals( "A Description of the Model", logicalModel.getDescription().getString( locale ) );

    Category category = logicalModel.findCategory( "CATEGORY" );
    assertNotNull( category );
    assertEquals( "Category", category.getName().getString( locale ) );

    assertEquals( 1, category.getLogicalColumns().size() );

    // this tests the inheritance of physical cols made it through
    LogicalColumn column = category.getLogicalColumns().get( 0 );
    assertEquals( "Customer Name", column.getName().getString( locale ) );
    assertEquals( "Customer Name Desc", column.getDescription().getString( locale ) );
    assertNotNull( column.getLogicalTable() );
    assertEquals( "LT", column.getLogicalTable().getId() );

    assertEquals( DataType.STRING, column.getDataType() );
    assertEquals( "select * from customers", column.getLogicalTable()
        .getProperty( SqlPhysicalTable.TARGET_TABLE ) );
    assertEquals( "select * from customers", column.getPhysicalColumn().getPhysicalTable().getProperty(
        SqlPhysicalTable.TARGET_TABLE ) );
    assertEquals( "customername", column.getPhysicalColumn().getProperty( SqlPhysicalColumn.TARGET_COLUMN ) );
    assertEquals( TargetColumnType.COLUMN_NAME, column.getPhysicalColumn().getProperty(
        SqlPhysicalColumn.TARGET_COLUMN_TYPE ) );
  }

  @Test
  public void testCloning() {

    Domain domain = TestHelper.getBasicDomain();
    Domain domain2 = (Domain) domain.clone();

    assertEquals( domain.getLogicalModels().get( 0 ).getId(), domain2.getLogicalModels().get( 0 ).getId() );

    domain2.getLogicalModels().get( 0 ).setName( new LocalizedString( "en_US", "TEST" ) );

    // equals uses the id for comparison, so these objects are still identical
    assertEquals( domain.getLogicalModels().get( 0 ).getId(), domain2.getLogicalModels().get( 0 ).getId() );

    domain2.getLogicalModels().get( 0 ).setId( "BLAH" );

    // once the id has changed, they appear as different elements.
    assertNotSame( domain.getLogicalModels().get( 0 ).getId(), domain2.getLogicalModels().get( 0 ).getId() );
  }

  static class SecureRepo extends InMemoryMetadataDomainRepository {

    SecurityOwner currentOwner = null;

    public boolean hasAccess( int accessType, IConcept aclHolder ) {
      Security s = (Security) aclHolder.getProperty( Concept.SECURITY_PROPERTY );
      if ( s == null ) {
        return false;
      }
      if ( currentOwner == null ) {
        return false;
      }

      Integer val = (Integer) s.getOwnerAclMap().get( currentOwner );

      return val != null;
    }
  }

  @Test
  public void testSecurity() throws Exception {
    Domain domain = TestHelper.getBasicDomain();
    SecureRepo repo = new SecureRepo();
    repo.storeDomain( domain, false );

    LogicalModel model = domain.getLogicalModels().get( 0 );

    Security globalSecurity = new Security();
    SecurityOwner joe = new SecurityOwner( OwnerType.USER, "joe" );
    SecurityOwner suzy = new SecurityOwner( OwnerType.USER, "suzy" );
    globalSecurity.putOwnerRights( joe, 1 );
    globalSecurity.putOwnerRights( suzy, 1 );

    model.setProperty( Concept.SECURITY_PROPERTY, globalSecurity );

    Security security = new Security();
    security.putOwnerRights( joe, 1 );

    LogicalTable table = model.getLogicalTables().get( 0 );
    LogicalColumn column = table.getLogicalColumns().get( 0 );
    Category category = model.getCategories().get( 0 );

    column.setProperty( Concept.SECURITY_PROPERTY, security );

    repo.currentOwner = joe;

    Domain joesDomain = repo.getDomain( domain.getId() );

    assertEquals( "{class=SecurityOwner, ownerType=USER, ownerName=joe}", joe.toString() );

    assertEquals( 1, joesDomain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns()
        .size() );
    assertEquals( 1, joesDomain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );

    assertEquals( 1, domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().size() );
    assertEquals( 1, domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );

    repo.currentOwner = suzy;

    Domain suzysDomain = repo.getDomain( domain.getId() );

    assertEquals( 0, suzysDomain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns()
        .size() );
    assertEquals( 0, suzysDomain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );

    column.removeChildProperty( Concept.SECURITY_PROPERTY );

    // add security to the table

    table.setProperty( Concept.SECURITY_PROPERTY, security );

    repo.currentOwner = joe;

    joesDomain = repo.getDomain( domain.getId() );

    assertEquals( 1, joesDomain.getLogicalModels().get( 0 ).getLogicalTables().size() );
    assertEquals( 1, joesDomain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );

    repo.currentOwner = suzy;

    suzysDomain = repo.getDomain( domain.getId() );

    assertEquals( 0, suzysDomain.getLogicalModels().get( 0 ).getLogicalTables().size() );
    // the individual columns shouldn't appear either in the category
    assertEquals( 0, suzysDomain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );

    table.removeChildProperty( Concept.SECURITY_PROPERTY );

    // add securiry to the category
    category.setProperty( Concept.SECURITY_PROPERTY, security );

    repo.currentOwner = joe;

    joesDomain = repo.getDomain( domain.getId() );

    assertEquals( 1, joesDomain.getLogicalModels().get( 0 ).getCategories().size() );

    repo.currentOwner = suzy;

    suzysDomain = repo.getDomain( domain.getId() );

    assertEquals( 0, suzysDomain.getLogicalModels().get( 0 ).getCategories().size() );

    category.removeChildProperty( Concept.SECURITY_PROPERTY );

    // add security to model

    model.setProperty( Concept.SECURITY_PROPERTY, security );

    repo.currentOwner = joe;

    joesDomain = repo.getDomain( domain.getId() );

    assertEquals( 1, joesDomain.getLogicalModels().size() );

    repo.currentOwner = suzy;

    suzysDomain = repo.getDomain( domain.getId() );

    assertEquals( 0, suzysDomain.getLogicalModels().size() );
  }

}
