package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
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
  public List getPropertySource(final String id) {
    // quick check to see if id is inherited at all
    if (concept.getPropertyInterfaces().containsKey(id)) {
      return Collections.EMPTY_LIST;
    }
    // otherwise, start the recursion
    List<String> path = new ArrayList<String>();
    getPropertySourceInternal(concept, id, path);
    return path;
  }

  /**
   * Recursively searches related concepts (i.e. itself, its parent, its security parent, and its inherited). Returns
   * the concept which has contributed the property with <code>id</code> to the leaf concept.
   */
  protected void getPropertySourceInternal(final ConceptInterface concept, final String id, final List<String> path) {
    Map childPropertiesMap = concept.getChildPropertyInterfaces();
    if (childPropertiesMap.containsKey(id)) {
      path.add("source");
    } else if (DefaultPropertyID.SECURITY.getId().equals(id) && concept.hasSecurityParentConcept()
        && concept.getSecurityPropertyInterfaces().containsKey(id)) {
      path.add("security");
      getPropertySourceInternal(concept.getSecurityParentInterface(), id, path);
    } else if (concept.hasParentConcept() && concept.getParentPropertyInterfaces().containsKey(id)) {
      path.add("parent");
      getPropertySourceInternal(concept.getParentInterface(), id, path);
    } else if (concept.hasInheritedConcept() && concept.getInheritedPropertyInterfaces().containsKey(id)) {
      path.add("inherited");
      getPropertySourceInternal(concept.getInheritedInterface(), id, path);
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
    return new HashSet<String>(Arrays.asList(ids));
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
    /*
     * Logic goes like this:
     * if child prop already exists, set old value to value of existing child prop and fire change event
     * else if the prop is from inherited/parent/security, set the old value to the inherited/parent/security value and
     *   fire override event
     * else, the prop is a new child prop so fire add event
     */
    int type = -1;
    ConceptPropertyInterface oldValue = null;
    if (concept.getChildPropertyInterfaces().containsKey(property.getId())) {
      type = PropertyExistenceModificationEvent.CHANGE_PROPERTY;
      oldValue = (ConceptPropertyInterface) concept.getChildPropertyInterfaces().get(property.getId());
    } else if (null != concept.getInheritedProperty(property.getId())
        || null != concept.getParentProperty(property.getId())
        || (null != concept.getSecurityProperty(property.getId()) && DefaultPropertyID.SECURITY.getId().equals(
            property.getId()))) {
      type = PropertyExistenceModificationEvent.OVERRIDE_PROPERTY;
      // get old value which is the value from parent/inherited/security; order matters here!
      if (null != concept.getSecurityProperty(property.getId())
          && DefaultPropertyID.SECURITY.getId().equals(property.getId())) {
        oldValue = (ConceptPropertyInterface) concept.getSecurityProperty(property.getId());
      } else if (null != concept.getParentProperty(property.getId())) {
        oldValue = concept.getParentProperty(property.getId());
      } else {
        oldValue = concept.getInheritedProperty(property.getId());
      }
    } else {
      type = PropertyExistenceModificationEvent.ADD_PROPERTY;
    }
    concept.addProperty(property);
    PropertyExistenceModificationEvent e = new PropertyExistenceModificationEvent(this, property.getId(), type,
        oldValue, property);
    fireConceptModificationEvent(e);
  }

  public void removeProperty(final String id) {
    /*
     * Logic goes like this:
     * if property was overriding a property from parent/inherited/security, the new value in fired event will be the
     *   value from parent/inherited/security and event will be an "inherit" event (the opposite of "override")
     * else new value is null and event will be a remove event
     */
    ConceptPropertyInterface oldValue = (ConceptPropertyInterface) concept.getChildProperty(id);
    int type = -1;
    ConceptPropertyInterface newValue = null;
    if (null == oldValue) {
      // property with given id does not exist
      return;
    }
    if (null != concept.getInheritedProperty(id) || null != concept.getParentProperty(id)
        || (null != concept.getSecurityProperty(id) && DefaultPropertyID.SECURITY.getId().equals(id))) {
      type = PropertyExistenceModificationEvent.INHERIT_PROPERTY;
      if (null != concept.getSecurityProperty(id) && DefaultPropertyID.SECURITY.getId().equals(id)) {
        newValue = (ConceptPropertyInterface) concept.getSecurityProperty(id);
      } else if (null != concept.getParentProperty(id)) {
        newValue = concept.getParentProperty(id);
      } else {
        newValue = concept.getInheritedProperty(id);
      }
    } else {
      type = PropertyExistenceModificationEvent.REMOVE_PROPERTY;
    }
    concept.getChildPropertyInterfaces().remove(id);
    PropertyModificationEvent e = new PropertyExistenceModificationEvent(this, id, type, oldValue, newValue);
    fireConceptModificationEvent(e);
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

  /**
   * Returns whether or not this property can be overridden.  This can return false if the property is a "default"
   * property.
   */
  public boolean canOverride(final String id) {

    ConceptPropertyInterface propFromThis = this.getProperty(id, IConceptModel.REL_THIS);
    ConceptPropertyInterface propFromSecurity = this.getProperty(id, IConceptModel.REL_SECURITY);
    ConceptPropertyInterface propFromInherited = this.getProperty(id, IConceptModel.REL_INHERITED);
    ConceptPropertyInterface propFromParent = this.getProperty(id, IConceptModel.REL_PARENT);

    if (null != propFromThis && propFromThis.isRequired()) {
      return false;
    } else if (null != propFromInherited || null != propFromParent) {
      return true;
    } else if (null != propFromSecurity && DefaultPropertyID.SECURITY.getId().equals(id)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isOverridden(final String id) {
    return null != getProperty(id, IConceptModel.REL_THIS) && canOverride(id);
  }

  public void setPropertyValue(final String id, final Object value) {
    ConceptPropertyInterface prop = concept.getChildProperty(id);
    if (null == prop) {
      if (logger.isWarnEnabled()) {
        logger.warn("property with id \"" + id + "\" does not exist in the concept; ignoring");
      }
      return;
    }
    Object oldValue = prop.getValue();
    prop.setValue(value);
    fireConceptModificationEvent(new PropertyValueModificationEvent(this, id, oldValue, value));
  }

  public String toString() {
    return new ReflectionToStringBuilder(this).toString();
  }

  public int getPropertyContributor(final String id) {
    Map childPropertiesMap = concept.getChildPropertyInterfaces();
    if (childPropertiesMap.containsKey(id)) {
      return IConceptModel.REL_THIS;
    } else if (DefaultPropertyID.SECURITY.getId().equals(id) && concept.hasSecurityParentConcept()
        && concept.getSecurityPropertyInterfaces().containsKey(id)) {
      return IConceptModel.REL_SECURITY;
    } else if (concept.hasParentConcept() && concept.getParentPropertyInterfaces().containsKey(id)) {
      return IConceptModel.REL_PARENT;
    } else if (concept.hasInheritedConcept() && concept.getInheritedPropertyInterfaces().containsKey(id)) {
      return IConceptModel.REL_INHERITED;
    } else {
      return -1;
    }
  }

}