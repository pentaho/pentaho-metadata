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
package org.pentaho.metadata.query.impl.sql.graph;

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
