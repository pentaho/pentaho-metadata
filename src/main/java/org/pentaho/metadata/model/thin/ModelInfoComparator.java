/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package org.pentaho.metadata.model.thin;

import java.util.Comparator;

/**
 * compares two model info objects so that they can be sorted by name
 * 
 * @author jamesdixon
 * 
 */
public class ModelInfoComparator implements Comparator {

  @Override
  public int compare( Object obj1, Object obj2 ) {
    ModelInfo model1 = (ModelInfo) obj1;
    ModelInfo model2 = (ModelInfo) obj2;
    return model1.getName().compareTo( model2.getName() );
  }

}
