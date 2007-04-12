package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

/**
 * TODO synchronization!
 * @author mlowery
 *
 */
public class ConceptModel implements IConceptModel {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptModel.class);

  // ~ Instance fields =================================================================================================

  /**
   * Contains event listeners.
   */
  private EventSupport eventSupport = new EventSupport();

  /**
   * The concept to which this class delegates.
   */
  private ConceptInterface concept;

  // ~ Constructors ====================================================================================================

  public ConceptModel(final ConceptInterface concept) {
    super();
    this.concept = concept;
  }

  // ~ Methods =========================================================================================================

  public void clearProperties() {
    String[] ids = concept.getChildPropertyIDs();
    if (0 == ids.length) {
      return;
    }
    Map childPropertyMap = concept.getChildPropertyInterfaces();
    for (int i = 0; i < ids.length; i++) {
      removeProperty(ids[i]);
    }
  }

  public ConceptPropertyInterface getEffectiveProperty(final String id) {
    return concept.getProperty(id);
  }

  public Map getEffectivePropertyMap() {
    return concept.getPropertyInterfaces();
  }

  /**
   * Experimental at this point.
   */
  public List getEffectivePropertySource(final String id) {
    // quick check to see if id is inherited at all
    if (concept.getPropertyInterfaces().containsKey(id)) {
      return Collections.EMPTY_LIST;
    }
    // otherwise, start the recursion
    List path = new ArrayList();
    getEffectivePropertySourceInternal(concept, id, path);
    return path;
  }

  /**
   * Recursively searches related concepts (i.e. itself, its parent, its security parent, and its inherited). Returns
   * the concept which has contributed the property with <code>id</code> to the leaf concept.
   */
  protected void getEffectivePropertySourceInternal(final ConceptInterface concept, final String id, final List path) {
    Map childPropertiesMap = concept.getChildPropertyInterfaces();
    if (childPropertiesMap.containsKey(id)) {
      path.add("source");
    } else if (DefaultPropertyID.SECURITY.getId().equals(id) && concept.hasSecurityParentConcept()
        && concept.getSecurityPropertyInterfaces().containsKey(id)) {
      path.add("security");
      getEffectivePropertySourceInternal(concept.getSecurityParentInterface(), id, path);
    } else if (concept.hasParentConcept() && concept.getParentPropertyInterfaces().containsKey(id)) {
      path.add("parent");
      getEffectivePropertySourceInternal(concept.getParentInterface(), id, path);
    } else if (concept.hasInheritedConcept() && concept.getInheritedPropertyInterfaces().containsKey(id)) {
      path.add("inherited");
      getEffectivePropertySourceInternal(concept.getInheritedInterface(), id, path);
    } else {
      // a default property perhaps?
      path.add("default");
    }
  }

  public ConceptPropertyInterface getProperty(final String id, final int relType) {
    switch (relType) {
      case IConceptModel.REL_THIS: {
        return concept.getChildProperty(id);
      }
      case IConceptModel.REL_SECURITY: {
        return concept.getSecurityProperty(id);
      }
      case IConceptModel.REL_PARENT: {
        return concept.getParentProperty(id);
      }
      case IConceptModel.REL_INHERITED: {
        return concept.getInheritedProperty(id);
      }
      default: {
        throw new IllegalArgumentException("illegal argument");
      }
    }
  }

  public Set getPropertyIds(final int relType) {
    String[] ids;
    switch (relType) {
      case IConceptModel.REL_THIS: {
        ids = concept.getChildPropertyIDs();
        break;
      }
      case IConceptModel.REL_SECURITY: {
        ids = concept.getSecurityParentInterface().getPropertyIDs();
        break;
      }
      case IConceptModel.REL_PARENT: {
        ids = concept.getParentInterface().getPropertyIDs();
        break;
      }
      case IConceptModel.REL_INHERITED: {
        ids = concept.getInheritedInterface().getPropertyIDs();
        break;
      }
      default: {
        throw new IllegalArgumentException("illegal argument");
      }
    }
    return new HashSet(Arrays.asList(ids));
  }

  public Map getPropertyMap(final int relType) {
    switch (relType) {
      case IConceptModel.REL_THIS: {
        return concept.getChildPropertyInterfaces();
      }
      case IConceptModel.REL_SECURITY: {
        return concept.getSecurityPropertyInterfaces();
      }
      case IConceptModel.REL_PARENT: {
        return concept.getParentPropertyInterfaces();
      }
      case IConceptModel.REL_INHERITED: {
        return concept.getInheritedPropertyInterfaces();
      }
      default: {
        throw new IllegalArgumentException("illegal argument");
      }
    }
  }

  public boolean hasRelatedConcept(final int relType) {
    switch (relType) {
      case IConceptModel.REL_THIS: {
        return true;
      }
      case IConceptModel.REL_SECURITY: {
        return concept.hasSecurityParentConcept();
      }
      case IConceptModel.REL_PARENT: {
        return concept.hasParentConcept();
      }
      case IConceptModel.REL_INHERITED: {
        return concept.hasInheritedConcept();
      }
      default: {
        throw new IllegalArgumentException("illegal argument");
      }
    }
  }

  public void setProperty(final ConceptPropertyInterface property) {
    int type = -1;
    ConceptPropertyInterface oldValue = null;
    if (concept.getChildPropertyInterfaces().containsKey(property.getId())) {
      type = PropertyModificationEvent.CHANGE_PROPERTY;
      oldValue = (ConceptPropertyInterface) concept.getChildPropertyInterfaces().get(property.getId());
    } else {
      type = PropertyModificationEvent.ADD_PROPERTY;
    }
    concept.addProperty(property);
    PropertyModificationEvent e = new PropertyModificationEvent(this, type, property.getId(), oldValue, property);
    fireConceptModificationEvent(e);
  }

  public void removeProperty(final String id) {
    ConceptPropertyInterface oldValue = (ConceptPropertyInterface) concept.getChildProperty(id);
    if (null != oldValue) {
      concept.getChildPropertyInterfaces().remove(id);
      PropertyModificationEvent e = new PropertyModificationEvent(this, PropertyModificationEvent.REMOVE_PROPERTY, id,
          oldValue, null);
      fireConceptModificationEvent(e);
    }
  }

  public void setRelatedConcept(final ConceptInterface relatedConcept, final int relType) {
    if (null == relatedConcept) {
      removeRelatedConcept(relType);
    }
    ConceptInterface oldValue = null;
    int type = -1;
    switch (relType) {
      case IConceptModel.REL_THIS: {
        throw new IllegalArgumentException("REL_THIS is an illegal argument");
      }
      case IConceptModel.REL_SECURITY: {
        oldValue = concept.getSecurityParentInterface();
        concept.setSecurityParentInterface(relatedConcept);
        break;
      }
      case IConceptModel.REL_PARENT: {
        oldValue = concept.getParentInterface();
        concept.setParentInterface(relatedConcept);
        break;
      }
      case IConceptModel.REL_INHERITED: {
        oldValue = concept.getInheritedInterface();
        concept.setInheritedInterface(relatedConcept);
        break;
      }
      default: {
        throw new IllegalArgumentException("illegal argument");
      }
    }
    if (null != oldValue) {
      type = RelatedConceptModificationEvent.CHANGE_RELATED_CONCEPT;
    } else {
      type = RelatedConceptModificationEvent.ADD_RELATED_CONCEPT;
    }
    RelatedConceptModificationEvent e = new RelatedConceptModificationEvent(this, type, relType, oldValue,
        relatedConcept);
    fireConceptModificationEvent(e);
  }

  public void removeRelatedConcept(final int relType) {
    ConceptInterface oldValue = null;
    int type = RelatedConceptModificationEvent.REMOVE_RELATED_CONCEPT;
    switch (relType) {
      case IConceptModel.REL_THIS: {
        throw new IllegalArgumentException("REL_THIS is an illegal argument");
      }
      case IConceptModel.REL_SECURITY: {
        oldValue = concept.getSecurityParentInterface();
        concept.setSecurityParentInterface(null);
        break;
      }
      case IConceptModel.REL_PARENT: {
        oldValue = concept.getParentInterface();
        concept.setParentInterface(null);
        break;
      }
      case IConceptModel.REL_INHERITED: {
        oldValue = concept.getInheritedInterface();
        concept.setInheritedInterface(null);
        break;
      }
      default: {
        throw new IllegalArgumentException("REL_THIS is an illegal argument");
      }
    }
    if (null != oldValue) {
      RelatedConceptModificationEvent e = new RelatedConceptModificationEvent(this, type, relType, oldValue, null);
      fireConceptModificationEvent(e);
    }
  }

  public void addConceptModificationListener(final IConceptModificationListener conceptModelListener) {
    eventSupport.addListener(conceptModelListener);
  }

  public void removeConceptModificationListener(final IConceptModificationListener conceptModelListener) {
    eventSupport.removeListener(conceptModelListener);
  }

  protected void fireConceptModificationEvent(final ConceptModificationEvent e) {
    for (Iterator iter = eventSupport.getListeners().iterator(); iter.hasNext();) {
      IConceptModificationListener target = (IConceptModificationListener) iter.next();
      target.conceptModified(e);
    }
  }

}