package org.pentaho.metadata.model.concept;

import java.util.Map;

/**
 * this is the root container for our metadata.  Metadata objects are concepts,
 * and concepts may have properties.
 * 
 * Concepts have three forms of inheritance
 * 
 * - the first form is inherit, which is derived from the relationships of 
 *   the metadata objects
 *   
 * - the second form is parent, which may be explicitly configured in the UI 
 * 
 * - the third form is security parent, which is derived from the relationships
 *   of the metadata objects and only applies to the security types.
 * 
 * @author gorman
 *
 */
public interface IConcept {

  /** @return get the id of the property */
  public String getId();

  /** @param id the property id to set */
  public void setId(String id);
  
  /**
   * returns the active property for id
   * 
   * @param name 
   * 
   * @return concept property
   */
  public Object getProperty(String name);

  /**
   * sets the property
   * @param property
   */
  public void setProperty(String name, Object property);
  
  /**
   * removes the property
   * @param property
   */
  public void removeChildProperty(String name);
  
  /**
   * returns the local property for id
   * 
   * @param name
   * @return
   */
  public Object getChildProperty(String name);
  
  /**
   * this is an unmodifiable map of properties
   * 
   * @return property
   */
  public Map<String, Object> getProperties();

  /**
   * this is an unmodifiable map of the current concept properties
   * 
   * @return property
   */  
  public Map<String, Object> getChildProperties();
  
  /**
   * returns the inherited concept
   * 
   * @return inherited concept
   */
  public IConcept getInheritedConcept();
  
  /**
   * sets the inherited concept
   * 
   * @param concept inherited concept
   */
  public void setInheritedConcept(IConcept concept);
  
  /**
   * returns the inherited concept
   * 
   * @return inherited concept
   */
  public IConcept getParentConcept();
  
  /**
   * sets the parent concept
   * 
   * @param concept inherited concept
   */
  public void setParentConcept(IConcept concept);
  
  /**
   * returns the inherited concept
   * 
   * @return inherited concept
   */
  public IConcept getSecurityParentConcept();
  
  /**
   * sets the security parent concept
   * 
   * @param concept inherited concept
   */
  public void setSecurityParentConcept(IConcept concept);
  
}
