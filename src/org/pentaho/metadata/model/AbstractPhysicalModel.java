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
 * Copyright (c) 2011 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;

/**
 * Acts as a parent for all physical models to be used for common attributes shared
 * by all implementations. 
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public abstract class AbstractPhysicalModel extends Concept implements IPhysicalModel {

  private static final long serialVersionUID = -3317176543443308764L;
  
  private Domain domain;
  
  @Override
  public IConcept getParent() {
    return domain;
  }
  
  public void setDomain(Domain domain) {
    this.domain = domain;
  }
  
  public Domain getDomain() {
    return domain;
  }
  
  @Override
  public List<String> getUniqueId() {
    List<String> uid = new ArrayList<String>();
    uid.add(IPhysicalModel.class.getSimpleName() + UID_TYPE_SEPARATOR + getId());
    return uid;
  }
  
}
