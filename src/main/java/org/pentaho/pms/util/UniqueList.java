/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
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
