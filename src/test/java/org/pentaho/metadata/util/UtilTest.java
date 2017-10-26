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
 * Copyright (c) 2016 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTest {
    @Test
    public void testToId() {
        assertEquals("Hello_World", Util.toId("\"Hello World\""));
        assertEquals("Hello_World", Util.toId("`Hello World`"));
        assertEquals("Hello_World", Util.toId("'Hello World'"));
        assertEquals("Hello_TIMES_World", Util.toId("\"Hello*World\""));
        assertEquals("Hello_World", Util.toId("\"Hello.World\""));
        assertEquals("Hello_DIVIDED_BY_World", Util.toId("\"Hello/World\""));
        assertEquals("Hello_PLUS_World", Util.toId("\"Hello+World\""));
        assertEquals("_Hello_World_", Util.toId("{[Hello].(World)}"));
    }
}