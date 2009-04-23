package org.pentaho.metadata.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.pms.schema.SchemaMeta;

public class ThinModelConverter {
  
  private static final Log logger = LogFactory.getLog(ThinModelConverter.class);
  
  public static SchemaMeta convertToLegacy(Domain domain) {
    SchemaMeta schemaMeta = new SchemaMeta();
    for (IPhysicalModel physicalModel : domain.getPhysicalModels()) {
      if (physicalModel instanceof SqlPhysicalModel) {
        // TODO
        
        
      } else {
        logger.error("physical model not supported " + physicalModel.getClass());
      }
    }
    
    return schemaMeta;
  }
  
  public static Object convertFromLegacy(SchemaMeta meta) {
    // TODO
    return null;
  }
}
