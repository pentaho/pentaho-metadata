package org.pentaho.metadata.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalModel;
import org.pentaho.metadata.model.IPhysicalTable;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

public class ThinModelConverter {
  
  private static final Log logger = LogFactory.getLog(ThinModelConverter.class);
  
  public static SchemaMeta convertToLegacy(Domain domain) throws ObjectAlreadyExistsException {
    SchemaMeta schemaMeta = new SchemaMeta();
    for (IPhysicalModel physicalModel : domain.getPhysicalModels()) {
      if (physicalModel instanceof SqlPhysicalModel) {
        SqlPhysicalModel sqlModel = (SqlPhysicalModel)physicalModel;
        // hardcode to mysql, the platform will autodetect the correct datasource
        // type before generating SQL.

        DatabaseMeta meta = new DatabaseMeta(
            ((SqlPhysicalModel) physicalModel).getDatasource(), 
            "MYSQL", 
            "JNDI", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        
        // set the JNDI connection string
        
        meta.getDatabaseInterface().setDatabaseName(((SqlPhysicalModel) physicalModel).getDatasource());
        schemaMeta.addDatabase(meta);
        
        // TODO: convert domain concepts
        
        // convert physical tables
        
        for (IPhysicalTable table : sqlModel.getPhysicalTables()) {
          SqlPhysicalTable sqlTable = (SqlPhysicalTable)table;
          PhysicalTable physicalTable = new PhysicalTable();
          convertConceptToLegacy(table, physicalTable);
          
          // TODO: convert physical columns
          
          schemaMeta.addTable(physicalTable);
        }
        
      } else {
        logger.error("physical model not supported " + physicalModel.getClass());
      }
    }
    
    for (LogicalModel logicalModel : domain.getLogicalModels()) {
      
      // TODO: convert logical models
      
    }
    
    return schemaMeta;
  }
  
  private static void convertConceptToLegacy(IConcept concept, org.pentaho.pms.schema.concept.ConceptUtilityInterface legacy) throws ObjectAlreadyExistsException {
    
    legacy.setId(concept.getId());
    
    for (String propertyName : concept.getChildProperties().keySet()) {
      Object property = concept.getChildProperty(propertyName);
      legacy.getConcept().addProperty(convertPropertyToLegacy(propertyName, property));
    }
  }
  
  private static ConceptPropertyInterface convertPropertyToLegacy(String propertyName, Object property) {
    // TODO: certain names have been changed, for instance formula
    
    if (property instanceof String) {
      ConceptPropertyString string = new ConceptPropertyString(propertyName, (String)property);
      return string;
    } else if (property instanceof LocalizedString) {
      LocalizedString str = (LocalizedString)property;
      LocalizedStringSettings value = new LocalizedStringSettings(str.getLocaleStringMap());
      ConceptPropertyLocalizedString string = new ConceptPropertyLocalizedString(propertyName, value);
      return string;
    } else if (property instanceof DataType) {
      
    } else if (property instanceof TargetTableType) {
      
    } else if (property instanceof TargetColumnType) {
      
    }
    logger.error("unsupported property: " + property);
    return null;
  }
  
  public static Object convertFromLegacy(SchemaMeta meta) {
    // TODO
    return null;
  }
}
