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
 * Copyright (c) 2016 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.query.model.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.pentaho.metadata.query.model.Selection;

public class QueryModelMetaDataTest {
  @Test
  public void testColumnHeadersWorkWithMixedCaseColumnMapAndLowerCasedAlias() {
    // This tests BISERVER-11022 (Impala with dashboards)
    Map<String, String> columnsMap = new HashMap<String, String>();
    columnsMap.put( "CamelCase", "test" );
    assertEquals( "test", new QueryModelMetaData( columnsMap, new Object[][] { new Object[] { "camelcase" } },
        new Object[][] {}, new ArrayList<Selection>() ).getColumnHeaders()[0][0] );
  }
}
