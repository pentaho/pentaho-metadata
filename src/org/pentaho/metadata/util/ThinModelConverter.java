package org.pentaho.metadata.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.IPhysicalModel;
import org.pentaho.metadata.model.IPhysicalTable;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregation;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

public class ThinModelConverter {
  
  private static final Log logger = LogFactory.getLog(ThinModelConverter.class);
  
  public static SchemaMeta convertToLegacy(Domain domain) throws ObjectAlreadyExistsException {
    SchemaMeta schemaMeta = new SchemaMeta();
    
    schemaMeta.setDomainName(domain.getId());
    
    DatabaseMeta database = null; 
    
    // only support a single database in a domain for now
    
    for (IPhysicalModel physicalModel : domain.getPhysicalModels()) {
      if (physicalModel instanceof SqlPhysicalModel) {
        SqlPhysicalModel sqlModel = (SqlPhysicalModel)physicalModel;
        
        // hardcode to mysql, the platform will autodetect the correct datasource
        // type before generating SQL.

        database = new DatabaseMeta(
            ((SqlPhysicalModel) physicalModel).getDatasource(), 
            "MYSQL", 
            "JNDI", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        
        // set the JNDI connection string
        
        database.getDatabaseInterface().setDatabaseName(((SqlPhysicalModel) physicalModel).getDatasource());
        schemaMeta.addDatabase(database);
        
        // TODO: convert domain concepts
        
        // convert physical tables
        
        for (IPhysicalTable table : sqlModel.getPhysicalTables()) {
          SqlPhysicalTable sqlTable = (SqlPhysicalTable)table;
          PhysicalTable physicalTable = new PhysicalTable();
          convertConceptToLegacy(table, physicalTable);
          
          for (IPhysicalColumn col : sqlTable.getPhysicalColumns()) {
            PhysicalColumn column = new PhysicalColumn();
            column.setTable(physicalTable);
            convertConceptToLegacy(col, column);
            physicalTable.addPhysicalColumn(column);
          }
          
          schemaMeta.addTable(physicalTable);
        }
        
      } else {
        logger.error("physical model not supported " + physicalModel.getClass());
      }
    }

    // convert logical models
    
    for (LogicalModel logicalModel : domain.getLogicalModels()) {
      BusinessModel model = new BusinessModel();
      model.setConnection(database);
      convertConceptToLegacy(logicalModel, model);
      
      // convert logical tables
      
      for (LogicalTable table : logicalModel.getLogicalTables()) {
        BusinessTable biztable = new BusinessTable();
        
        PhysicalTable pt = schemaMeta.findPhysicalTable(table.getPhysicalTable().getId());
        
        convertConceptToLegacy(table, biztable);
        
        biztable.setPhysicalTable(pt);
        
        // convert business columns

        for (LogicalColumn column : table.getLogicalColumns()) {
          
          BusinessColumn col = new BusinessColumn();
          convertConceptToLegacy(column, col);
          
          col.setBusinessTable(biztable);
          PhysicalColumn physicalColumn = schemaMeta.findPhysicalColumn(
              column.getPhysicalColumn().getPhysicalTable().getId(), 
              column.getPhysicalColumn().getId());
          
          col.setPhysicalColumn(physicalColumn);

          // Set the security parent
          col.getConcept().setSecurityParentInterface(biztable.getConcept());
          
          biztable.addBusinessColumn(col);
        }
        model.addBusinessTable(biztable);
      }
      
      // convert categories
      
      BusinessCategory root = new BusinessCategory();
      root.setRootCategory(true);
      model.setRootCategory(root);
      
      for (Category category : logicalModel.getCategories()) {
        BusinessCategory cat = new BusinessCategory();
        convertConceptToLegacy(category, cat);
        for (LogicalColumn column : category.getLogicalColumns()) {
          BusinessColumn col = model.findBusinessColumn(column.getId());
          cat.addBusinessColumn(col);
        }
        root.addBusinessCategory(cat);
      }
      
      schemaMeta.addModel(model);
    }
    
    return schemaMeta;
  }
  
  private static void convertConceptToLegacy(IConcept concept, org.pentaho.pms.schema.concept.ConceptUtilityInterface legacy) throws ObjectAlreadyExistsException {
    
    legacy.setId(concept.getId());
    
    for (String propertyName : concept.getChildProperties().keySet()) {
      Object property = concept.getChildProperty(propertyName);
      ConceptPropertyInterface prop = convertPropertyToLegacy(propertyName, property);
      if (prop != null) {
        legacy.getConcept().addProperty(prop);
      }
    }
  }
  
  private static ConceptPropertyInterface convertPropertyToLegacy(String propertyName, Object property) {

    if (property instanceof String) {
      if (propertyName.equals(SqlPhysicalColumn.TARGET_COLUMN)) {
        propertyName = "formula";
      }
      ConceptPropertyString string = new ConceptPropertyString(propertyName, (String)property);
      return string;
    } else if (property instanceof LocalizedString) {
      LocalizedString str = (LocalizedString)property;
      LocalizedStringSettings value = new LocalizedStringSettings(str.getLocaleStringMap());
      ConceptPropertyLocalizedString string = new ConceptPropertyLocalizedString(propertyName, value);
      return string;
    } else if (property instanceof DataType) {
      DataType dt = (DataType)property;
      DataTypeSettings datatypeSettings = DataTypeSettings.types[dt.ordinal()];
      ConceptPropertyDataType cpdt = new ConceptPropertyDataType(propertyName, datatypeSettings);
      return cpdt;
    } else if (property instanceof List) {
      // TODO: List<AggregationType>

    } else if (property instanceof AggregationType) {
      AggregationSettings aggSettings = convertToLegacy((AggregationType)property);
      ConceptPropertyAggregation agg = new ConceptPropertyAggregation(propertyName, aggSettings);
      return agg;
    } else if (property instanceof TargetTableType) {
      // this property is not relevant in the old model
      return null;
    } else if (property instanceof TargetColumnType) {
      TargetColumnType colType = (TargetColumnType)property;
      if (propertyName.equals(SqlPhysicalColumn.TARGET_COLUMN_TYPE)) {
        propertyName = "exact";
      }
      ConceptPropertyBoolean bool = new ConceptPropertyBoolean(propertyName, colType == TargetColumnType.OPEN_FORMULA);
      return bool;
    }
    
    logger.error("unsupported property: " + property);
    return null;
  }
  
  public static MQLQueryImpl convertToLegacy(Query query, DatabaseMeta databaseMeta) throws Exception {
    // first convert the query domain
    SchemaMeta meta = convertToLegacy(query.getDomain());
    BusinessModel model = meta.findModel(query.getLogicalModel().getId());
    
    if (databaseMeta == null) {
      databaseMeta = meta.getDatabase(0);
    }
    
    MQLQueryImpl impl = new MQLQueryImpl(meta, model, databaseMeta, null);
    
    // Options 
    
    impl.setDisableDistinct(query.getDisableDistinct());
    
    // Selections
    
    for (Selection sel : query.getSelections()) {
      BusinessColumn column = model.findBusinessColumn(sel.getLogicalColumn().getId());
      org.pentaho.pms.mql.Selection legSel = new org.pentaho.pms.mql.Selection(column, convertToLegacy(sel.getAggregationType()));
      impl.addSelection(legSel);
    }
    
    // Constraints
    
    for (Constraint constr : query.getConstraints()) {
      impl.addConstraint(constr.getCombinationType().toString(), constr.getFormula());
    }
    
    for (Order order : query.getOrders()) {
      
      AggregationSettings aggregation = convertToLegacy(order.getSelection().getAggregationType());
      String aggStr = null;
      if (aggregation != null) {
        aggStr = aggregation.getCode();
      }
      impl.addOrderBy(order.getSelection().getCategory().getId(),
          order.getSelection().getLogicalColumn().getId(),
          aggStr, order.getType() == Order.Type.ASC);
    }
    
    // then populate the mql query impl
    return impl;
  }
  
  public static AggregationSettings convertToLegacy(AggregationType aggType) {
    if (aggType == null) {
      return null;
    }
    return AggregationSettings.types[aggType.ordinal()];
  }
  
  public static Domain convertFromLegacy(SchemaMeta meta) {
    // TODO
    return null;
  }
}
