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
 * Copyright (c) 2006 - 20011 Pentaho Corporation..  All rights reserved.
 * 
 * Contributed by Nick Coleman
 */
package org.pentaho.pms.mql.graph;

import java.util.Iterator;
import java.util.PriorityQueue;

public class GraphElementQueue extends PriorityQueue<GraphElement> {
  private static final long serialVersionUID = 1L;

  public boolean offer( GraphElement e ) {
    if ( e.isQueued() ) {
      return false;
    }

    if ( super.offer( e ) ) {
      e.setQueued( true );
      return true;
    }

    return false;
  }

  @Override
  public boolean remove( Object o ) {
    GraphElement e = (GraphElement) o;

    if ( super.remove( e ) ) {
      e.setQueued( false );
      return true;
    }

    return false;
  }

  @Override
  public Iterator<GraphElement> iterator() {
    return new Iter( super.iterator() );
  }

  private static final class Iter implements Iterator<GraphElement> {
    private Iterator<GraphElement> parentIter;
    private GraphElement last;

    public Iter( Iterator<GraphElement> parentIter ) {
      this.parentIter = parentIter;
    }

    public boolean hasNext() {
      return parentIter.hasNext();
    }

    public GraphElement next() {
      last = parentIter.next();
      return last;
    }

    public void remove() {
      parentIter.remove();
      if ( last != null ) {
        last.setQueued( false );
      }
    }
  }
}
