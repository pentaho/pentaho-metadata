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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.metadata.model.concept;

import java.io.Serializable;

/**
 * 
 * This class was introduced as part of the thin model cleanup. Gwt's compilation does not work well when
 * making references to Object so instead the Property class which makes use of generics was created and
 * all instances in the thin model were replaced.
 * 
 * @author Ezequiel Cuellar (ecuellar@pentaho.com)
 */
public class Property<T> implements Serializable {
  
  private static final long serialVersionUID = -9212834283678090814L;

  private T value;
  
  public Property() {
  }
  
  public Property(T t) {
    this.value = t;
  }

  public void setValue( T t ) {
    this.value = t;
  }

  public T getValue() {
    return value;
  }
}
