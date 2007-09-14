package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

/**
 * This is actually a model of a forest (set of trees).
 * @author mlowery
 */
public class ConceptTreeModel implements IConceptTreeModel {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptTreeModel.class);

  // ~ Instance fields =================================================================================================

  EventSupport eventSupport = new EventSupport();

  SchemaMeta schemaMeta;

  /**
   * Used to synchronize original tree with modified tree when user saves changes in concept editor. Keys and values are
   * instances of <code>ConceptInterface</code>.
   */
  BidiMap origModBidiMap = new DualHashBidiMap();

  /**
   * List of concepts marked for deletion since either instantiation of this class or last <code>save</code> call.
   * (Cleared on save.)
   */
  List deletedConcepts = new ArrayList();

  /**
   * List of concepts marked for creation since either instantiation of this class or last <code>save</code> call.
   * (Cleared on save.)
   */
  List newConcepts = new ArrayList();

  /**
   * Stores children of concepts. (Concepts already know about their parents.) Keys are instances of
   * <code>ConceptInterface</code>. Values are Collections of (<code>ConceptInterface</code>s)--the children.
   */
  MultiMap parentToChildrenMap = new MultiValueMap();

  /**
   * Points of entry into the trees.
   */
  List roots = new ArrayList();

  // ~ Constructors ====================================================================================================

  public ConceptTreeModel(final SchemaMeta schemaMeta) {
    super();
    Validate.notNull(schemaMeta);
    this.schemaMeta = schemaMeta;
    buildTree();
  }

  // ~ Methods =========================================================================================================

  private void buildTree() {
    // clone all concepts and remember how to get from modified to original and vice versa
    List clones = new ArrayList();
    Iterator iter1 = schemaMeta.getConcepts().iterator();
    while (iter1.hasNext()) {
      ConceptInterface orig = (ConceptInterface) iter1.next();
      ConceptInterface mod = (ConceptInterface) orig.clone();
      clones.add(mod);
      origModBidiMap.put(mod, orig);
    }

    // if cloned concepts have parent links to original concepts, re-point them to cloned concepts
    Iterator iter2 = clones.iterator();
    while (iter2.hasNext()) {
      ConceptInterface clone = (ConceptInterface) iter2.next();
      if (origModBidiMap.inverseBidiMap().containsKey(clone.getParentInterface())) {
        clone.setParentInterface((ConceptInterface) origModBidiMap.inverseBidiMap().get(clone.getParentInterface()));
      }
      // remember the children...they are our future
      parentToChildrenMap.put(clone.getParentInterface(), clone);
    }
  }

  public void addConcept(final ConceptInterface parent, final ConceptInterface newChild) {
    // parent can be null but concept cannot
    Validate.notNull(newChild);
    newChild.setParentInterface(parent);
    parentToChildrenMap.put(parent, newChild);
    newConcepts.add(newChild);
    fireConceptTreeModificationEvent(new ConceptTreeModificationEvent(this));
  }

  public ConceptInterface[] getChildren(final ConceptInterface parent) {
    if (parentToChildrenMap.containsKey(parent)) {
      Collection c = (Collection) parentToChildrenMap.get(parent);
      return (ConceptInterface[]) c.toArray(new ConceptInterface[0]);
    } else {
      return new ConceptInterface[0];
    }
  }

  public ConceptInterface getParent(final ConceptInterface concept) {
    Validate.notNull(concept);
    return concept.getParentInterface();
  }

  public void removeConcept(final ConceptInterface concept) {
    Validate.notNull(concept);
    // TODO cleanup children of children, etc.
    ConceptInterface parent = concept.getParentInterface();
    Collection children = (Collection) parentToChildrenMap.get(parent);
    children.remove(concept);

    ConceptInterface orig = (ConceptInterface) origModBidiMap.remove(concept);
    if (null != orig) {
      // if this concept exists in schema meta, it needs to be marked for removal
      markForRemoval(orig);
    } else {
      // concept has been added since last save; simply remove it from the list of concepts to be added
      newConcepts.remove(concept);
    }
    fireConceptTreeModificationEvent(new ConceptTreeModificationEvent(this));
  }

  private void markForRemoval(final ConceptInterface concept) {
    deletedConcepts.add(concept);
  }

  public void save() throws ObjectAlreadyExistsException {
    // process additions
    Iterator iter1 = newConcepts.iterator();
    while (iter1.hasNext()) {
      ConceptInterface mod = (ConceptInterface) iter1.next();
      ConceptInterface orig = (ConceptInterface) mod.clone();
      schemaMeta.addConcept(orig);
      origModBidiMap.put(mod, orig);
    }
    newConcepts.clear();

    // process deletions
    Iterator iter2 = deletedConcepts.iterator();
    while (iter2.hasNext()) {
      ConceptInterface mod = (ConceptInterface) iter2.next();
      ConceptInterface orig = (ConceptInterface) origModBidiMap.get(mod);
      origModBidiMap.remove(mod);
      removeConceptFromSchemaMeta(orig);
    }
    deletedConcepts.clear();

    // process mods
    MapIterator iter = origModBidiMap.mapIterator();
    while (iter.hasNext()) {
      ConceptInterface mod = (ConceptInterface) iter.next();
      ConceptInterface orig = (ConceptInterface) iter.getValue();
      orig.clearChildProperties();
      orig.getChildPropertyInterfaces().putAll(mod.getChildPropertyInterfaces());
      orig.setName(mod.getName());
      orig.setParentInterface((ConceptInterface) origModBidiMap.get(mod.getParentInterface()));
    }
  }

  /**
   * The only way to remove the concept is via index. Find the index by comparing object identity.
   */
  private void removeConceptFromSchemaMeta(final ConceptInterface concept) {
    for (int i = 0; i < schemaMeta.nrConcepts(); i++) {
      if (schemaMeta.getConcept(i) == concept) {
        schemaMeta.removeConcept(i);
        return;
      }
    }
  }

  public static void main(String[] args) throws ObjectAlreadyExistsException {
    ConceptInterface c = new Concept();
    IConceptModel conceptModel = new ConceptModel(c);
    LocalizedStringSettings s1 = new LocalizedStringSettings();
    s1.setLocaleString("en_US", "chicken");
    s1.setLocaleString("es_ES", "pollo");
    conceptModel.setProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.NAME.getId(), s1));

    LocalizedStringSettings s2 = new LocalizedStringSettings();
    s2.setLocaleString("en_US", "Where is the library?");
    s2.setLocaleString("es_ES", "¿Dónde está la biblioteca?");
    conceptModel.setProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.DESCRIPTION.getId(), s2));
    conceptModel
        .setProperty(new ConceptPropertyColumnWidth(DefaultPropertyID.COLUMN_WIDTH.getId(), ColumnWidth.INCHES));
    conceptModel.setProperty(new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(), "overridden_table"));
    Concept parentConcept = new Concept();
    ConceptPropertyInterface prop1 = new ConceptPropertyString(DefaultPropertyID.FORMULA.getId(), "e=mc2");
    parentConcept.addProperty(prop1);
    ConceptPropertyInterface prop2 = new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(), "test_schema");
    parentConcept.addProperty(prop2);
    conceptModel.setRelatedConcept(parentConcept, IConceptModel.REL_PARENT);

    Concept secConcept = new Concept();
    ConceptPropertyInterface sec2 = new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(), "test_table");
    secConcept.addProperty(sec2);
    conceptModel.setRelatedConcept(secConcept, IConceptModel.REL_INHERITED);

    Concept inConcept = new Concept();
    ConceptPropertyInterface in1 = new ConceptPropertyBoolean(DefaultPropertyID.EXACT.getId(), true);
    inConcept.addProperty(in1);
    ConceptPropertyInterface in2 = new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(), "test_table");
    inConcept.addProperty(in2);
    new ConceptModel(parentConcept).setRelatedConcept(inConcept, IConceptModel.REL_PARENT);

    SchemaMeta schemaMeta = new SchemaMeta();
    schemaMeta.addConcept(parentConcept);
    schemaMeta.addConcept(c);
    schemaMeta.addConcept(inConcept);
    schemaMeta.addConcept(secConcept);

    IConceptTreeModel model = new ConceptTreeModel(schemaMeta);

    ConceptInterface[] childrenOfRoots = model.getChildren(null);
    for (int i = 0; i < childrenOfRoots.length; i++) {
      ConceptInterface[] childrenOfRoot = model.getChildren(childrenOfRoots[i]);
    }

    model.addConcept(model.getChildren(null)[0], secConcept);

    childrenOfRoots = model.getChildren(null);
    for (int i = 0; i < childrenOfRoots.length; i++) {
      ConceptInterface[] childrenOfRoot = model.getChildren(childrenOfRoots[i]);
    }

    model.removeConcept(secConcept);

    childrenOfRoots = model.getChildren(null);
    for (int i = 0; i < childrenOfRoots.length; i++) {
      ConceptInterface[] childrenOfRoot = model.getChildren(childrenOfRoots[i]);
    }

  }

  private void fireConceptTreeModificationEvent(final ConceptTreeModificationEvent e) {
    Set listeners = eventSupport.getListeners();
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ((IConceptTreeModificationListener) iter.next()).conceptTreeModified(e);
    }
  }

  public void addConceptTreeModificationListener(final IConceptTreeModificationListener listener) {
    eventSupport.addListener(listener);
  }

  public void removeConceptTreeModificationListener(final IConceptTreeModificationListener listener) {
    eventSupport.removeListener(listener);
  }

  public SchemaMeta getSchemaMeta() {
    return schemaMeta;
  }

}
