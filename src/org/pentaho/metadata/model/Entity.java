package org.pentaho.metadata.model;

import java.io.Serializable;

public class Entity implements Serializable {
  
  private static final long serialVersionUID = 591803771762090261L;

  private long id;
  
  private String name;
  
  private String description;

  /**
   * Returns the id of this object
   * @return
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the id of this object
   * @param id
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the localized name of this object
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the localized name of this object
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the localized description of this object
   * @return
   */
  public String getDescription() {
    return description;
  }
  
  /**
   * Sets the localized description of this object
   * @param name
   */
  public void setDescription(String description) {
    this.description = description;
  }

}
