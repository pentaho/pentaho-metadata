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
