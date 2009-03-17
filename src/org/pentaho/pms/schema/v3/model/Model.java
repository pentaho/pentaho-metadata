/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 *
 * Copyright 2009 Pentaho Corporation.  All rights reserved. 
 *
 * @created Mar, 2009
 * @author James Dixon
 * 
*/
package org.pentaho.pms.schema.v3.model;

/**
 * A thin model of a metadata model.
 * This class stores the root category, and the dimensions
 * and cubes of a model.
 * 
 * The categories of the model contain business (logical) columns.
 * The OLAP dimensions contain hierarchies made of those columns.
 * The OLAP cube(s) of the model use the dimensions.
 * 
 * This class extends ModelEnvelope. Model envelope (and its Envelope 
 * superclass) provide the id, name, description, and domain of this
 * model.
 * @author jamesdixon
 *
 */
public class Model extends ModelEnvelope {

  private Dimension[] dimensions;
  
  private Cube[] cubes;
  
  private Category rootCategory;

  /**
   * Returns the array dimensions of this model. The cube or cubes defined
   * will use these dimensions.
   * @return
   */
  public Dimension[] getDimensions() {
    return dimensions;
  }

  /**
   * Sets the array of dimensions used by the cube(s) of this model
   * @param dimensions
   */
  public void setDimensions(Dimension[] dimensions) {
    this.dimensions = dimensions;
  }

  /**
   * Returns the array of cubes defined for this model. The cube(s) use
   * the dimensions.
   * @return
   */
  public Cube[] getCubes() {
    return cubes;
  }

  /**
   * Sets the array of cubes defined for this model.
   * @param cubes
   */
  public void setCubes(Cube[] cubes) {
    this.cubes = cubes;
  }

  /**
   * Returns the root category of this model. The root category will
   * contain columns and/or sub-categories that have columns.
   * @return
   */
  public Category getRootCategory() {
    return rootCategory;
  }

  /**
   * Sets the root category of this model
   * @param rootCategory
   */
  public void setRootCategory(Category rootCategory) {
    this.rootCategory = rootCategory;
  }
  
}
