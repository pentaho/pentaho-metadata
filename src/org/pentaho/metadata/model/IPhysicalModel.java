package org.pentaho.metadata.model;

import java.util.List;

import org.pentaho.metadata.model.concept.IConcept;

public interface IPhysicalModel extends IConcept {
  public List<? extends IPhysicalTable> getPhysicalTables();
}
