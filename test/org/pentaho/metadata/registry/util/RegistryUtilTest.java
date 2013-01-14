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
package org.pentaho.metadata.registry.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RegistryUtilTest {
  @Test
  public void generateCompositeId_null_empty() {
    RegistryUtil util = new RegistryUtil();

    assertNull(util.generateCompositeId());
    assertNull(util.generateCompositeId(null));
    assertEquals("$NULL$~$EMPTY$~ ~  ", util.generateCompositeId(null, "", " ", "  "));
  }

  @Test
  public void generateCompositeId() {
    RegistryUtil util = new RegistryUtil();
    String id = util.generateCompositeId("test");
    assertEquals("test", id);

    id = util.generateCompositeId("a", "b", "c");
    assertEquals("a~b~c", id);
  }

  @Test
  public void splitCompositeId() {
    RegistryUtil util = new RegistryUtil();
    assertArrayEquals(new String[]{"a"}, util.splitCompositeId("a"));
    assertArrayEquals(new String[]{"a", "b", "c"}, util.splitCompositeId("a~b~c"));
  }

  @Test
  public void splitCompositeId_null_empty() {
    RegistryUtil util = new RegistryUtil();
    assertNull(util.splitCompositeId(null));
    assertArrayEquals(new String[]{" ", null, ""}, util.splitCompositeId(" ~$NULL$~$EMPTY$"));
  }
  
  @Test 
  public void testTableId() {
	    RegistryUtil util = new RegistryUtil();
	  String id = util.generateTableId("database1", null, "table1");
	  String parts[] = util.splitCompositeId(id);
	  assertEquals("Wrong number of parts", 3, parts.length);
	  assertEquals("Database name is wrong","database1",parts[0]);
	  assertEquals("Schema name is wrong",null,parts[1]);
	  assertEquals("Table name is wrong","table1",parts[2]);
	  
	  id = util.generateTableId("database1", "", "table1");
	  parts = util.splitCompositeId(id);
	  assertEquals("Database name is wrong","database1",parts[0]);
	  assertEquals("Schema name is wrong","",parts[1]);
	  assertEquals("Table name is wrong","table1",parts[2]);	  
  }
  
  @Test 
  public void testDocumentId() {
	    RegistryUtil util = new RegistryUtil();
	  String id = util.generateDocumentId("name1", "id1");
	  String parts[] = util.splitCompositeId(id);
	  assertEquals("Wrong number of parts", 2, parts.length);
	  assertEquals("Document name is wrong","name1",parts[0]);
	  assertEquals("Document id is wrong","id1",parts[1]);
  }
  
  @Test 
  public void testTypedId() {
	    RegistryUtil util = new RegistryUtil();
	  String id = util.generateTypedId("name1", "type1");
	  String parts[] = util.splitCompositeId(id);
	  assertEquals("Wrong number of parts", 2, parts.length);
	  assertEquals("Document name is wrong","type1",parts[0]);
	  assertEquals("Document id is wrong","name1",parts[1]);
  }
  
}
