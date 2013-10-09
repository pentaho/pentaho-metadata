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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.IPhysicalModel;
import org.pentaho.metadata.model.IPhysicalTable;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlDataSource.DataSourceType;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.security.RowLevelSecurity;
import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.metadata.model.concept.types.Color;
import org.pentaho.metadata.model.concept.types.ColumnWidth.WidthType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.RelationshipType;
import org.pentaho.metadata.model.concept.types.TableType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregation;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregationList;
import org.pentaho.pms.schema.concept.types.alignment.AlignmentSettings;
import org.pentaho.pms.schema.concept.types.alignment.ConceptPropertyAlignment;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.color.ColorSettings;
import org.pentaho.pms.schema.concept.types.color.ConceptPropertyColor;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.date.ConceptPropertyDate;
import org.pentaho.pms.schema.concept.types.fieldtype.ConceptPropertyFieldType;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumber;
import org.pentaho.pms.schema.concept.types.rowlevelsecurity.ConceptPropertyRowLevelSecurity;
import org.pentaho.pms.schema.concept.types.security.ConceptPropertySecurity;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.concept.types.tabletype.ConceptPropertyTableType;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.concept.types.url.ConceptPropertyURL;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

/**
 * This class converts to and from the original metadata model (org.pentaho.pms.schema) to the new model
 * (org.pentaho.metadata.model)
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
@SuppressWarnings( "deprecation" )
public class ThinModelConverter {

  private static final Log logger = LogFactory.getLog( ThinModelConverter.class );

  public static DatabaseMeta convertToLegacy( String name, SqlDataSource datasource ) {
    // make sure that the Kettle environment is initialized before DatabaseMeta creation
    try {
      KettleEnvironment.init( false );
    } catch ( KettleException e ) {
      logger.error( "Error initializing the Kettle Environment", e );
      throw new RuntimeException( "Error initializing the Kettle Environment", e );
    }

    DatabaseMeta databaseMeta = new DatabaseMeta();

    databaseMeta.setName( name );

    databaseMeta.setHostname( datasource.getHostname() );
    if ( datasource.getDialectType() == null ) {
      // default to mysql if dialect is null
      databaseMeta.setDatabaseType( "GENERIC" ); //$NON-NLS-1$
    } else {
      databaseMeta.setDatabaseType( datasource.getDialectType() );
    }
    databaseMeta.setAccessType( DatabaseMeta.getAccessType( datasource.getType().toString() ) );
    databaseMeta.setDBName( datasource.getDatabaseName() );
    databaseMeta.setDBPort( datasource.getPort() );
    databaseMeta.setUsername( datasource.getUsername() );
    databaseMeta.setPassword( datasource.getPassword() );

    // And now load the attributes...
    for ( String key : datasource.getAttributes().keySet() ) {
      databaseMeta.getAttributes().put( key, datasource.getAttributes().get( key ) );
    }
    return databaseMeta;
  }

  /**
   * Convert a thin model to the legacy API
   * 
   * @param domain
   *          a domain object to convert to legacy
   * @return a schema meta object
   * @throws ObjectAlreadyExistsException
   * 
   * @deprecated this method isn't fully supported, it was implemented during a transition phase between the old and new
   *             models
   */
  public static SchemaMeta convertToLegacy( Domain domain ) throws ObjectAlreadyExistsException {
    SchemaMeta schemaMeta = new SchemaMeta();

    schemaMeta.setDomainName( domain.getId() );

    // convert locale list
    if ( domain.getLocales() != null ) {
      int order = 0;
      for ( LocaleType localeType : domain.getLocales() ) {
        boolean found = false;
        for ( String code : schemaMeta.getLocales().getLocaleCodes() ) {
          if ( code.equals( localeType.getCode() ) ) {
            found = true;
            break;
          }
        }
        if ( !found ) {
          LocaleInterface localeInterface = new LocaleMeta();
          localeInterface.setCode( localeType.getCode() );
          localeInterface.setDescription( localeType.getDescription() );
          localeInterface.setOrder( order++ );
          localeInterface.setActive( true );
          schemaMeta.getLocales().addLocale( localeInterface );
        }
      }
    }

    DatabaseMeta database = null;

    // only support a single database in a domain for now

    for ( IPhysicalModel physicalModel : domain.getPhysicalModels() ) {
      if ( physicalModel instanceof SqlPhysicalModel ) {
        SqlPhysicalModel sqlModel = (SqlPhysicalModel) physicalModel;

        // hardcode to mysql, the platform will autodetect the correct datasource
        // type before generating SQL.
        if ( sqlModel.getDatasource().getType() == DataSourceType.JNDI ) {
          database = new DatabaseMeta( ( (SqlPhysicalModel) physicalModel ).getDatasource().getDatabaseName(), "MYSQL", //$NON-NLS-1$
              "JNDI", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
          database.getDatabaseInterface().setDatabaseName(
              ( (SqlPhysicalModel) physicalModel ).getDatasource().getDatabaseName() );
        } else {
          // TODO: support JDBC
        }
        // set the JNDI connection string

        schemaMeta.addDatabase( database );

        // TODO: convert domain concepts

        // convert physical tables

        for ( IPhysicalTable table : sqlModel.getPhysicalTables() ) {
          SqlPhysicalTable sqlTable = (SqlPhysicalTable) table;
          PhysicalTable physicalTable = new PhysicalTable();
          physicalTable.setDatabaseMeta( database );
          convertConceptToLegacy( table, physicalTable );

          for ( IPhysicalColumn col : sqlTable.getPhysicalColumns() ) {
            PhysicalColumn column = new PhysicalColumn();
            column.setTable( physicalTable );
            convertConceptToLegacy( col, column );
            physicalTable.addPhysicalColumn( column );
          }

          schemaMeta.addTable( physicalTable );
        }

      } else {
        logger.error( "physical model not supported " + physicalModel.getClass() ); //$NON-NLS-1$
      }
    }

    // convert logical models

    for ( LogicalModel logicalModel : domain.getLogicalModels() ) {
      BusinessModel model = new BusinessModel();
      model.setConnection( database );
      convertConceptToLegacy( logicalModel, model );

      // convert logical tables

      for ( LogicalTable table : logicalModel.getLogicalTables() ) {
        BusinessTable biztable = new BusinessTable();

        PhysicalTable pt = schemaMeta.findPhysicalTable( table.getPhysicalTable().getId() );

        convertConceptToLegacy( table, biztable );

        biztable.setPhysicalTable( pt );

        // convert business columns

        for ( LogicalColumn column : table.getLogicalColumns() ) {

          BusinessColumn col = new BusinessColumn();
          convertConceptToLegacy( column, col );

          col.setBusinessTable( biztable );
          PhysicalColumn physicalColumn =
              schemaMeta.findPhysicalColumn( column.getPhysicalColumn().getPhysicalTable().getId(), column
                  .getPhysicalColumn().getId() );

          col.setPhysicalColumn( physicalColumn );

          // Set the security parent
          col.getConcept().setSecurityParentInterface( biztable.getConcept() );

          biztable.addBusinessColumn( col );
        }
        model.addBusinessTable( biztable );
      }

      // convert categories

      BusinessCategory root = new BusinessCategory();
      root.setRootCategory( true );
      model.setRootCategory( root );

      for ( Category category : logicalModel.getCategories() ) {
        BusinessCategory cat = new BusinessCategory();
        convertConceptToLegacy( category, cat );
        for ( LogicalColumn column : category.getLogicalColumns() ) {
          BusinessColumn col = model.findBusinessColumn( column.getId() );
          cat.addBusinessColumn( col );
        }
        root.addBusinessCategory( cat );
      }

      schemaMeta.addModel( model );
    }

    return schemaMeta;
  }

  private static void convertConceptToLegacy( IConcept concept,
      org.pentaho.pms.schema.concept.ConceptUtilityInterface legacy ) throws ObjectAlreadyExistsException {

    legacy.setId( concept.getId() );

    for ( String propertyName : concept.getChildProperties().keySet() ) {
      Object property = concept.getChildProperty( propertyName );
      ConceptPropertyInterface prop = convertPropertyToLegacy( propertyName, property );
      if ( prop != null ) {
        legacy.getConcept().addProperty( prop );
      }
    }
  }

  public static ConceptPropertyInterface convertPropertyToLegacy( String propertyName, Object property ) {

    if ( property instanceof String ) {
      if ( propertyName.equals( SqlPhysicalColumn.TARGET_COLUMN ) ) {
        propertyName = "formula"; //$NON-NLS-1$
      }
      ConceptPropertyString string = new ConceptPropertyString( propertyName, (String) property );
      return string;
    } else if ( property instanceof LocalizedString ) {
      LocalizedString str = (LocalizedString) property;
      LocalizedStringSettings value = new LocalizedStringSettings( str.getLocaleStringMap() );
      ConceptPropertyLocalizedString string = new ConceptPropertyLocalizedString( propertyName, value );
      return string;
    } else if ( property instanceof DataType ) {
      DataType dt = (DataType) property;
      DataTypeSettings datatypeSettings = DataTypeSettings.types[dt.ordinal()];
      ConceptPropertyDataType cpdt = new ConceptPropertyDataType( propertyName, datatypeSettings );
      return cpdt;
    } else if ( property instanceof List ) {
      // TODO: List<AggregationType>

    } else if ( property instanceof AggregationType ) {
      AggregationSettings aggSettings = convertToLegacy( (AggregationType) property );
      ConceptPropertyAggregation agg = new ConceptPropertyAggregation( propertyName, aggSettings );
      return agg;
    } else if ( property instanceof TargetTableType ) {
      // this property is not relevant in the old model
      return null;
    } else if ( property instanceof TargetColumnType ) {
      TargetColumnType colType = (TargetColumnType) property;
      if ( propertyName.equals( SqlPhysicalColumn.TARGET_COLUMN_TYPE ) ) {
        propertyName = "exact"; //$NON-NLS-1$
      }
      ConceptPropertyBoolean bool = new ConceptPropertyBoolean( propertyName, colType == TargetColumnType.OPEN_FORMULA );
      return bool;
    } else if ( property instanceof Font ) {
      Font font = (Font) property;
      FontSettings value = new FontSettings( font.getName(), font.getHeight(), font.isBold(), font.isItalic() );
      ConceptPropertyFont fontprop = new ConceptPropertyFont( propertyName, value );
      return fontprop;
    } else if ( property instanceof TableType ) {
      TableType tt = (TableType) property;
      ConceptPropertyTableType tbltype =
          new ConceptPropertyTableType( propertyName, TableTypeSettings.types[tt.ordinal()] );
      return tbltype;
    } else if ( property instanceof List ) {
      // aggregation lists here
      List list = (List) property;
      if ( propertyName.equals( "aggregation_list" ) ) { //$NON-NLS-1$
        List<AggregationSettings> listaggs = new ArrayList<AggregationSettings>();
        ConceptPropertyAggregationList agglist = new ConceptPropertyAggregationList( propertyName, listaggs );
        for ( Object obj : list ) {
          AggregationType aggType = (AggregationType) obj;
          listaggs.add( AggregationSettings.types[aggType.ordinal()] );
        }
        return agglist;
      }
    }

    logger.error( "unsupported property: " + property ); //$NON-NLS-1$
    return null;
  }

  public static MQLQueryImpl convertToLegacy( Query query, DatabaseMeta databaseMeta ) throws Exception {
    // first convert the query domain
    SchemaMeta meta = convertToLegacy( query.getDomain() );
    BusinessModel model = meta.findModel( query.getLogicalModel().getId() );

    if ( databaseMeta == null ) {
      databaseMeta = meta.getDatabase( 0 );
    }

    MQLQueryImpl impl = new MQLQueryImpl( meta, model, databaseMeta, null );

    // Options

    impl.setDisableDistinct( query.getDisableDistinct() );
    impl.setLimit( query.getLimit() );

    // Selections

    for ( Selection sel : query.getSelections() ) {
      BusinessColumn column = model.findBusinessColumn( sel.getLogicalColumn().getId() );
      org.pentaho.pms.mql.Selection legSel =
          new org.pentaho.pms.mql.Selection( column, convertToLegacy( sel.getAggregationType() ) );
      impl.addSelection( legSel );
    }

    // Constraints

    for ( Constraint constr : query.getConstraints() ) {
      impl.addConstraint( constr.getCombinationType().toString(), constr.getFormula() );
    }

    for ( Order order : query.getOrders() ) {

      AggregationSettings aggregation = convertToLegacy( order.getSelection().getAggregationType() );
      String aggStr = null;
      if ( aggregation != null ) {
        aggStr = aggregation.getCode();
      }
      impl.addOrderBy( order.getSelection().getCategory().getId(), order.getSelection().getLogicalColumn().getId(),
          aggStr, order.getType() == Order.Type.ASC );
    }

    // then populate the mql query impl
    return impl;
  }

  public static AggregationSettings convertToLegacy( AggregationType aggType ) {
    if ( aggType == null ) {
      return null;
    }
    return AggregationSettings.types[aggType.ordinal()];
  }

  public static void convertConceptFromLegacy( org.pentaho.pms.schema.concept.ConceptUtilityInterface legacy,
      IConcept concept ) {
    concept.setId( legacy.getId() );
    for ( String propertyName : legacy.getConcept().getChildPropertyIDs() ) { // concept.getChildProperties().keySet())
                                                                              // {
      ConceptPropertyInterface property = legacy.getConcept().getChildProperty( propertyName );
      Object prop = convertPropertyFromLegacy( propertyName, property );
      String newPropertyName = convertPropertyNameFromLegacy( propertyName );
      if ( prop != null ) {
        concept.setProperty( newPropertyName, prop );
      }
    }
  }

  private static String convertPropertyNameFromLegacy( String propertyName ) {
    if ( "formula".equals( propertyName ) ) { //$NON-NLS-1$
      return SqlPhysicalColumn.TARGET_COLUMN;
    } else if ( "exact".equals( propertyName ) ) { //$NON-NLS-1$
      return SqlPhysicalColumn.TARGET_COLUMN_TYPE;
    } else {
      return propertyName;
    }
  }

  @SuppressWarnings( "unchecked" )
  private static Object convertPropertyFromLegacy( String propertyName, ConceptPropertyInterface property ) {

    if ( property instanceof ConceptPropertyString ) {
      return (String) property.getValue();
    } else if ( property instanceof ConceptPropertyLocalizedString ) {
      LocalizedStringSettings settings = (LocalizedStringSettings) property.getValue();
      return new LocalizedString( settings.getLocaleStringMap() );
    } else if ( property instanceof ConceptPropertyDataType ) {
      DataTypeSettings settings = (DataTypeSettings) property.getValue();
      if ( settings == null ) {
        return null;
      }
      return DataType.values()[settings.getType()];
    } else if ( property instanceof ConceptPropertyAggregationList ) {
      List<AggregationSettings> orig = (List<AggregationSettings>) property.getValue();
      List<AggregationType> list = new ArrayList<AggregationType>();
      for ( AggregationSettings setting : orig ) {
        list.add( AggregationType.values()[setting.getType()] );
      }
      return list;
    } else if ( property instanceof ConceptPropertyAggregation ) {
      AggregationSettings aggSettings = (AggregationSettings) property.getValue();
      return AggregationType.values()[aggSettings.getType()];
    } else if ( property instanceof ConceptPropertyBoolean ) {
      Boolean boolVal = (Boolean) property.getValue();
      if ( propertyName.equals( "exact" ) ) { //$NON-NLS-1$
        if ( boolVal ) {
          return TargetColumnType.OPEN_FORMULA;
        } else {
          return TargetColumnType.COLUMN_NAME;
        }
      } else {
        return boolVal;
      }
    } else if ( property instanceof ConceptPropertySecurity ) {
      org.pentaho.pms.schema.security.Security security =
          (org.pentaho.pms.schema.security.Security) property.getValue();
      Map<SecurityOwner, Integer> map = new HashMap<SecurityOwner, Integer>();
      for ( org.pentaho.pms.schema.security.SecurityOwner owner : security.getOwners() ) {
        SecurityOwner ownerObj =
            new SecurityOwner( SecurityOwner.OwnerType.values()[owner.getOwnerType()], owner.getOwnerName() );
        Integer val = security.getOwnerRights( owner );
        map.put( ownerObj, val );
      }
      return new Security( map );
    } else if ( property instanceof ConceptPropertyRowLevelSecurity ) {
      org.pentaho.pms.schema.security.RowLevelSecurity security =
          (org.pentaho.pms.schema.security.RowLevelSecurity) property.getValue();
      RowLevelSecurity securityObj = new RowLevelSecurity();
      securityObj.setType( RowLevelSecurity.Type.values()[security.getType().ordinal()] );
      securityObj.setGlobalConstraint( security.getGlobalConstraint() );

      Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
      for ( org.pentaho.pms.schema.security.SecurityOwner owner : security.getRoleBasedConstraintMap().keySet() ) {
        SecurityOwner ownerObj =
            new SecurityOwner( SecurityOwner.OwnerType.values()[owner.getOwnerType()], owner.getOwnerName() );
        map.put( ownerObj, security.getRoleBasedConstraintMap().get( owner ) );
      }
      securityObj.setRoleBasedConstraintMap( map );
      return securityObj;
    } else if ( property instanceof ConceptPropertyAlignment ) {
      AlignmentSettings alignment = (AlignmentSettings) property.getValue();
      return Alignment.values()[alignment.getType()];
    } else if ( property instanceof ConceptPropertyColor ) {
      ColorSettings color = (ColorSettings) property.getValue();
      return new Color( color.getRed(), color.getGreen(), color.getBlue() );
    } else if ( property instanceof ConceptPropertyColumnWidth ) {
      org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth colWidth =
          (org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth) property.getValue();
      return new org.pentaho.metadata.model.concept.types.ColumnWidth( WidthType.values()[colWidth.getType()], colWidth
          .getWidth().doubleValue() );
    } else if ( property instanceof ConceptPropertyDate ) {
      return property.getValue();
    } else if ( property instanceof ConceptPropertyURL ) {
      return property.getValue();
    } else if ( property instanceof ConceptPropertyFieldType ) {
      FieldTypeSettings fieldType = (FieldTypeSettings) property.getValue();
      return FieldType.values()[fieldType.getType()];
    } else if ( property instanceof ConceptPropertyTableType ) {
      TableTypeSettings tableType = (TableTypeSettings) property.getValue();
      return TableType.values()[tableType.getType()];
    } else if ( property instanceof ConceptPropertyFont ) {
      FontSettings font = (FontSettings) property.getValue();
      return new Font( font.getName(), font.getHeight(), font.isBold(), font.isItalic() );
    } else if ( property instanceof ConceptPropertyNumber ) {
      if ( property.getValue() != null ) {
        return ( (BigDecimal) property.getValue() ).doubleValue();
      } else {
        return null;
      }
    }

    logger.error( "unsupported property: " + property ); //$NON-NLS-1$
    return null;
  }

  public static SqlDataSource convertFromLegacy( DatabaseMeta database ) {
    SqlDataSource dataSource = new SqlDataSource();

    dataSource.setDialectType( database.getDatabaseTypeDesc() );
    dataSource.setDatabaseName( database.environmentSubstitute( database.getDatabaseName() ) );
    dataSource.setHostname( database.environmentSubstitute( database.getHostname() ) );
    dataSource.setPort( database.environmentSubstitute( database.getDatabasePortNumberString() ) );
    dataSource.setUsername( database.environmentSubstitute( database.getUsername() ) );
    dataSource.setPassword( database.environmentSubstitute( database.getPassword() ) );

    if ( database.getAccessType() == DatabaseMeta.TYPE_ACCESS_JNDI ) {
      dataSource.setType( DataSourceType.values()[database.getAccessType()] );
    } else if ( database.getAccessType() == DatabaseMeta.TYPE_ACCESS_NATIVE ) {
      dataSource.setType( DataSourceType.NATIVE );
    }

    // And now load the attributes...
    if ( database.getAttributes() != null ) {
      for ( Object key : database.getAttributes().keySet() ) {
        dataSource.getAttributes().put( (String) key,
            database.environmentSubstitute( (String) database.getAttributes().get( key ) ) );
      }
    }

    return dataSource;
  }

  @SuppressWarnings( "unchecked" )
  public static Domain convertFromLegacy( SchemaMeta schemaMeta ) throws Exception {
    // SchemaMeta schemaMeta = new SchemaMeta();
    Domain domain = new Domain();
    domain.setId( schemaMeta.getDomainName() );
    List<LocaleType> localeTypes = new ArrayList<LocaleType>();

    // the new model uses the natural ordering of the list vs. a separate ordinal

    List<LocaleInterface> list = (List<LocaleInterface>) schemaMeta.getLocales().getLocaleList();
    Collections.sort( list, new Comparator<LocaleInterface>() {
      // TODO: Test ordering
      public int compare( LocaleInterface o1, LocaleInterface o2 ) {
        if ( o1.getOrder() > o2.getOrder() ) {
          return 1;
        } else if ( o1.getOrder() < o2.getOrder() ) {
          return -1;
        } else {
          return 0;
        }
      }
    } );

    for ( LocaleInterface locale : list ) {
      LocaleType localeType = new LocaleType();
      localeType.setDescription( locale.getDescription() );
      localeType.setCode( locale.getCode() );
      localeTypes.add( localeType );
    }

    domain.setLocales( localeTypes );

    for ( DatabaseMeta database : schemaMeta.getDatabases() ) {
      SqlPhysicalModel sqlModel = new SqlPhysicalModel();

      SqlDataSource dataSource = convertFromLegacy( database );
      sqlModel.setDatasource( dataSource );

      sqlModel.setId( database.getName() );

      PhysicalTable[] tables = schemaMeta.getTablesOnDatabase( database );
      for ( PhysicalTable table : tables ) {
        SqlPhysicalTable sqlTable = new SqlPhysicalTable( sqlModel );

        convertConceptFromLegacy( table, sqlTable );

        // Specify TargetTableType

        if ( table.getTargetTable().toLowerCase().startsWith( "select " ) ) { //$NON-NLS-1$
          sqlTable.setTargetTableType( TargetTableType.INLINE_SQL );
        }

        for ( PhysicalColumn column : table.getPhysicalColumns() ) {
          SqlPhysicalColumn sqlColumn = new SqlPhysicalColumn( sqlTable );
          convertConceptFromLegacy( column, sqlColumn );
          sqlTable.getPhysicalColumns().add( sqlColumn );
        }
        sqlModel.getPhysicalTables().add( sqlTable );
      }

      domain.addPhysicalModel( sqlModel );
    }

    // convert logical models

    for ( BusinessModel model : schemaMeta.getBusinessModels() ) {
      LogicalModel logicalModel = new LogicalModel();
      convertConceptFromLegacy( model, logicalModel );

      for ( Object biztable : model.getBusinessTables() ) {
        BusinessTable businessTable = (BusinessTable) biztable;
        LogicalTable logicalTable = new LogicalTable();
        IPhysicalTable physicalTable = domain.findPhysicalTable( businessTable.getPhysicalTable().getId() );
        if ( physicalTable != null && logicalModel.getPhysicalModel() == null ) {
          logicalModel.setPhysicalModel( physicalTable.getPhysicalModel() );
        }
        logicalTable.setPhysicalTable( physicalTable );
        logicalTable.setLogicalModel( logicalModel );

        if ( businessTable.getLocation() != null ) {
          logicalTable.setProperty( "__LEGACY_TAG_POSITION_X", "" + businessTable.getLocation().x );
          logicalTable.setProperty( "__LEGACY_TAG_POSITION_Y", "" + businessTable.getLocation().y );
        }

        logicalTable.setProperty( "__LEGACY_TABLE_IS_DRAWN", "" + businessTable.isDrawn() );

        convertConceptFromLegacy( businessTable, logicalTable );

        for ( BusinessColumn column : businessTable.getBusinessColumns() ) {
          LogicalColumn logicalColumn = new LogicalColumn();
          for ( IPhysicalColumn physicalColumn : physicalTable.getPhysicalColumns() ) {
            if ( physicalColumn.getId().equals( column.getPhysicalColumn().getId() ) ) {
              logicalColumn.setPhysicalColumn( physicalColumn );
            }
          }
          logicalColumn.setLogicalTable( logicalTable );
          convertConceptFromLegacy( column, logicalColumn );
          logicalTable.getLogicalColumns().add( logicalColumn );
        }

        logicalModel.getLogicalTables().add( logicalTable );
      }

      for ( RelationshipMeta rel : (List<RelationshipMeta>) model.getRelationships() ) {
        LogicalRelationship logical = new LogicalRelationship();
        logical.setComplex( rel.isComplex() );
        logical.setComplexJoin( rel.getComplexJoin() );
        logical.setJoinOrderKey( rel.getJoinOrderKey() );
        if ( domain.getLocales().size() > 0 ) {
          logical.setRelationshipDescription( rel.getDescription() );
        }

        logical.setRelationshipType( RelationshipType.values()[rel.getType()] );

        // what happens if we set a null value for a property? from an inheritance perspective, there should be a
        // difference
        // between null and inherited.
        LogicalTable toTable = null;
        LogicalTable fromTable = null;
        LogicalColumn toColumn = null;
        LogicalColumn fromColumn = null;

        if ( rel.getTableTo() != null ) {
          toTable = logicalModel.findLogicalTable( rel.getTableTo().getId() );
        }
        if ( rel.getTableFrom() != null ) {
          fromTable = logicalModel.findLogicalTable( rel.getTableFrom().getId() );
        }
        if ( rel.getFieldTo() != null ) {
          toColumn = logicalModel.findLogicalColumn( rel.getFieldTo().getId() );
        }
        if ( rel.getFieldFrom() != null ) {
          fromColumn = logicalModel.findLogicalColumn( rel.getFieldFrom().getId() );
        }

        logical.setToTable( toTable );
        logical.setToColumn( toColumn );
        logical.setFromTable( fromTable );
        logical.setFromColumn( fromColumn );

        /*
         * Build the logical relationship's id using as much information is available:
         * 
         * toTable.toColumn-fromTable.fromColumn
         */
        String logicalRelationshipId = "";
        if ( fromTable != null ) {
          logicalRelationshipId += fromTable.getId();
          if ( fromColumn != null ) {
            logicalRelationshipId += "." + fromColumn.getId();
          }
        }

        if ( fromTable != null && toTable != null ) {
          logicalRelationshipId += "-";
        }

        if ( toTable != null ) {
          logicalRelationshipId += toTable.getId();
          if ( toColumn != null ) {
            logicalRelationshipId += "." + toColumn.getId();
          }
        }

        logical.setId( logicalRelationshipId );
        logical.setLogicalModel( logicalModel );
        logicalModel.getLogicalRelationships().add( logical );
      }

      for ( BusinessCategory bizCategory : model.getRootCategory().getBusinessCategories() ) {
        Category category = new Category( logicalModel );
        convertConceptFromLegacy( bizCategory, category );

        for ( Object bizColumn : bizCategory.getBusinessColumns() ) {
          BusinessColumn businessColumn = (BusinessColumn) bizColumn;
          category.getLogicalColumns().add( logicalModel.findLogicalColumn( businessColumn.getId() ) );
        }

        logicalModel.getCategories().add( category );
      }
      domain.addLogicalModel( logicalModel );
    }
    return domain;
  }
}
