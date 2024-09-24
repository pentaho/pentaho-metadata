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
 * Copyright (c) 2016 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.metadata.util;

import java.util.Comparator;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import org.pentaho.metadata.query.model.util.BooleanComparator;

public class BooleanComparatorTest {

  @Test
  public void testCompareBooleanString() {
    Comparator<Object> bc = BooleanComparator.getComparator();
    assertTrue( bc.compare( "true", Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( "y", Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( "1", Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( "yes", Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( "trUe", Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( "Y", Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( "yeS", Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( "false", Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( "N", Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( "0", Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( "no", Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( "faLse", Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( "n", Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( "nO", Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( "hehe", Boolean.FALSE ) == 0 );
  }

  @Test
  public void testCompareBooleanNumber() {
    Comparator<Object> bc = BooleanComparator.getComparator();
    assertTrue( bc.compare( 1, Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( 5, Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( 9, Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( 1.1, Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( 100500, Boolean.TRUE ) == 0 );
    assertTrue( bc.compare( -2, Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( 0, Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( 0.3, Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( 0.99, Boolean.FALSE ) == 0 );
    assertTrue( bc.compare( -100500, Boolean.FALSE ) == 0 );
  }

  @Test
  public void testCompareStringNumber() {
    Comparator<Object> bc = BooleanComparator.getComparator();
    assertTrue( bc.compare( 1, "1" ) == 0 );
    assertTrue( bc.compare( 5, "yes" ) == 0 );
    assertTrue( bc.compare( 9, "true" ) == 0 );
    assertTrue( bc.compare( -2, "false" ) == 0 );
    assertTrue( bc.compare( 0, "5" ) == 0 );
    assertTrue( bc.compare( -2, "no" ) == 0 );
    assertTrue( bc.compare( 1, "y" ) == 0 );
    assertTrue( bc.compare( 0.3, "n" ) == 0 );
  }
}
