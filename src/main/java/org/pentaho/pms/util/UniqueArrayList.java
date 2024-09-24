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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */
package org.pentaho.pms.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * We want to enforce uniqueness in the list. The objects stored added need to have the "equals" implemented
 * 
 * @author Matt
 * @since 2007-03-19
 * 
 */
public class UniqueArrayList<E> implements UniqueList<E> {
  private List<E> list;

  private static final long serialVersionUID = -4032535311575763475L;

  public UniqueArrayList() {
    this.list = new ArrayList<E>();
  }

  /**
   * @param c
   */
  public UniqueArrayList( Collection<E> c ) {
    this.list = new ArrayList<E>( c );
  }

  /**
   * @param initialCapacity
   */
  public UniqueArrayList( int initialCapacity ) {
    this.list = new ArrayList<E>( initialCapacity );
  }

  public boolean add( E o ) throws ObjectAlreadyExistsException {
    if ( list.contains( o ) ) {
      throw new ObjectAlreadyExistsException();
    }
    return list.add( o );
  }

  public void add( int index, E element ) throws ObjectAlreadyExistsException {
    if ( list.contains( element ) ) {
      throw new ObjectAlreadyExistsException();
    }
    list.add( index, element );

  }

  public boolean addAll( Collection<E> c ) throws ObjectAlreadyExistsException {
    for ( Iterator<E> iter = c.iterator(); iter.hasNext(); ) {
      E element = iter.next();
      if ( list.contains( element ) ) {
        throw new ObjectAlreadyExistsException();
      }
    }
    return list.addAll( c );
  }

  public boolean addAll( int index, Collection<E> c ) throws ObjectAlreadyExistsException {
    for ( Iterator<E> iter = c.iterator(); iter.hasNext(); ) {
      E element = iter.next();
      if ( list.contains( element ) ) {
        throw new ObjectAlreadyExistsException();
      }
    }
    return list.addAll( index, c );
  }

  public void clear() {
    list.clear();
  }

  public boolean contains( E o ) {
    return list.contains( o );
  }

  public boolean containsAll( Collection<E> c ) {
    return list.containsAll( c );
  }

  public E get( int index ) {
    return list.get( index );
  }

  public int indexOf( E o ) {
    return list.indexOf( o );
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public Iterator<E> iterator() {
    return list.iterator();
  }

  public int lastIndexOf( E o ) {
    return list.lastIndexOf( o );
  }

  public ListIterator<E> listIterator() {
    return list.listIterator();
  }

  public ListIterator<E> listIterator( int index ) {
    return list.listIterator( index );
  }

  public E remove( int index ) {
    return list.remove( index );
  }

  public boolean remove( E o ) {
    return list.remove( o );
  }

  public boolean removeAll( Collection<E> c ) {
    return list.removeAll( c );
  }

  public boolean retainAll( Collection<E> c ) {
    return list.retainAll( c );
  }

  public Object set( int index, E element ) throws ObjectAlreadyExistsException {
    return list.set( index, element );
  }

  public int size() {
    return list.size();
  }

  public List<E> subList( int fromIndex, int toIndex ) {
    return list.subList( fromIndex, toIndex );
  }

  public Object[] toArray() {
    return list.toArray();
  }

  public <T> T[] toArray( T[] a ) {
    return list.toArray( a );
  }

  public List<E> getList() {
    return list;
  }
}
