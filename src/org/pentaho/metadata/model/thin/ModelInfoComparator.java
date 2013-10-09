/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2011 Pentaho Corporation.  All rights reserved.
 * 
 * Created Jun, 2012
 * @author jdixon
 */
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
