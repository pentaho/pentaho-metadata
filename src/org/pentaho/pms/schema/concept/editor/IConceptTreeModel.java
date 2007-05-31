package org.pentaho.pms.schema.concept.editor;

import org.pentaho.pms.schema.concept.ConceptInterface;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

/**
 * Wraps the tree of concepts stored in the schema meta. This model is ONLY concerned with querying or changing the
 * structure of the tree.
 *
 * @author mlowery
 */
public interface IConceptTreeModel {

  /**
   * Returns the children of parent.
   */
  ConceptInterface[] getChildren(ConceptInterface parent);

  /**
   * Adds the concept with parent as parent.
   */
  void addConcept(ConceptInterface parent, ConceptInterface newChild);

  /**
   * Removes the concept.
   */
  void removeConcept(ConceptInterface concept);

  /**
   * Returns the parent of the concept.
   */
  ConceptInterface getParent(ConceptInterface concept);

  /**
   * Write the changes made since instantiation into the schema meta.
   */
  void save() throws ObjectAlreadyExistsException;

  void addConceptTreeModificationListener(IConceptTreeModificationListener listener);

  void removeConceptTreeModificationListener(IConceptTreeModificationListener listener);
}
