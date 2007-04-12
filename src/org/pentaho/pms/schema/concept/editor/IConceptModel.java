package org.pentaho.pms.schema.concept.editor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;

/**
 * The model of a concept in the MVC sense. Acts as intermediary between concept and widget. Concept is modified through
 * this interface and events are fired accordingly.
 * @author mlowery
 */
public interface IConceptModel {
  /**
   * Relation type indicating the current concept.
   */
  int REL_THIS = 0;

  /**
   * Relation type indicating the parent of the current concept.
   */
  int REL_PARENT = 1;

  /**
   * Relation type indicating the concept of the parent subject.
   */
  int REL_INHERITED = 2;

  /**
   * Relation type indicating the concept's security parent.
   */
  int REL_SECURITY = 3;

  void setRelatedConcept(final ConceptInterface relatedConcept, final int relType);

  void removeRelatedConcept(final int relType);

  boolean hasRelatedConcept(final int relType);

  void setProperty(final ConceptPropertyInterface property);

  void removeProperty(final String id);

  ConceptPropertyInterface getEffectiveProperty(final String id);

  ConceptPropertyInterface getProperty(final String id, final int relType);

  Map getEffectivePropertyMap();

  Map getPropertyMap(final int relType);

  void clearProperties();

  Set getPropertyIds(final int relType);

  List getEffectivePropertySource(final String id);

  void addConceptModificationListener(final IConceptModificationListener conceptModelListener);

  void removeConceptModificationListener(final IConceptModificationListener conceptModelListener);
}
