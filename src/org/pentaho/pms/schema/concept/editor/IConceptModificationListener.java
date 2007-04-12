package org.pentaho.pms.schema.concept.editor;

import java.util.EventListener;

public interface IConceptModificationListener extends EventListener {
  void conceptModified(final ConceptModificationEvent e);
}
