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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.pms.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface UniqueList<E> extends Iterable<E> {
  public boolean add( E o ) throws ObjectAlreadyExistsException;

  public void add( int index, E element ) throws ObjectAlreadyExistsException;

  public boolean addAll( Collection<E> c ) throws ObjectAlreadyExistsException;

  public void clear();

  public boolean contains( E o );

  public boolean containsAll( Collection<E> c );

  public E get( int index );

  public int indexOf( E o );

  public boolean isEmpty();

  public Iterator<E> iterator();

  public int lastIndexOf( E o );

  public ListIterator listIterator();

  public ListIterator listIterator( int index );

  public E remove( int index );

  public boolean remove( E o );

  public boolean removeAll( Collection<E> c );

  public boolean retainAll( Collection<E> c );

  public Object set( int index, E element ) throws ObjectAlreadyExistsException;

  public int size();

  public List subList( int fromIndex, int toIndex );

  public Object[] toArray();

  public <T> T[] toArray( T[] a );

  public List<E> getList();
}
