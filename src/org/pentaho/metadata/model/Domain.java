package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.types.LocaleType;

public class Domain extends Concept {

  private static final long serialVersionUID = -9093116797722021640L;

  public static final String LOCALES_PROPERTY = "locales";
  
  public Domain() {
    super();
    // TODO Auto-generated constructor stub
  }

  private List<IPhysicalModel> physicalModels = new ArrayList<IPhysicalModel>();
  private List<LogicalModel> logicalModels = new ArrayList<LogicalModel>();  
  private List<Concept> concepts = new ArrayList<Concept>();
  
  public List<IPhysicalModel> getPhysicalModels() {
    return physicalModels;
  }
  
  public void setPhysicalModels(List<IPhysicalModel> physicalModels) {
    this.physicalModels = physicalModels;
  }
  
  public void addPhysicalModel(IPhysicalModel physicalModel) {
    physicalModels.add(physicalModel);
  }

  public List<LogicalModel> getLogicalModels() {
    return logicalModels;
  }
  
  public void setLogicalModels(List<LogicalModel> logicalModels) {
    this.logicalModels = logicalModels;
  }
  
  public void addLogicalModel(LogicalModel physicalModel) {
    logicalModels.add(physicalModel);
  }

  public List<Concept> getConcepts() {
    return concepts;
  }
  
  public void setConcepts(List<Concept> concepts) {
    this.concepts = concepts;
  }
  
  public void addConcept(Concept concept) {
    concepts.add(concept);
  }
  
  public void setLocales(List<LocaleType> locales) {
    setProperty(LOCALES_PROPERTY, locales);
  }
  
  @SuppressWarnings("unchecked")
  public List<LocaleType> getLocales() {
    return (List<LocaleType>)getProperty(LOCALES_PROPERTY);
  }
  
  // utility methods
  
  public LogicalModel findLogicalModel(String modelId) {
    for (LogicalModel model : getLogicalModels()) {
      if (modelId.equals(model.getId())) {
        return model;
      }
    }
    return null;
  }
  
  public IPhysicalTable findPhysicalTable(String tableId) {
    for (IPhysicalModel model : getPhysicalModels()) {
      for (IPhysicalTable table : model.getPhysicalTables()) {
        if (tableId.equals(table.getId())) {
          return table;
        }
      }
    }
    return null;
  }

}
