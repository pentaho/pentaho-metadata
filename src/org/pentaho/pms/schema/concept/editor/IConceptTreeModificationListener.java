package org.pentaho.pms.schema.concept.editor;

import java.util.EventListener;

public interface IConceptTreeModificationListener extends EventListener {
  void conceptTreeModified(final ConceptTreeModificationEvent e);
}
