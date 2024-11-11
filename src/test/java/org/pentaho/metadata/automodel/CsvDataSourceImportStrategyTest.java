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


package org.pentaho.metadata.automodel;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.metadata.automodel.importing.strategy.CsvDatasourceImportStrategy;
import org.pentaho.metadata.model.thin.Column;

public class CsvDataSourceImportStrategyTest {

  @Test
  public void nameWithProhibitChars_DisplayedWithNoChanges() {
    final String nameWithProhibitChars = "Month 1-2";
    final String id = "Month 1_HYPHEN_2";

    Column[] columns = new Column[] { createColumn( id, nameWithProhibitChars ) };

    CsvDatasourceImportStrategy importStrategy = new CsvDatasourceImportStrategy( columns );
    ValueMetaInterface meta = new ValueMetaString( id );

    Assert.assertEquals( importStrategy.displayName( meta ), nameWithProhibitChars );
  }

  @Test
  public void nameWithNoProhibitChars_DisplayedWithNoChanges() {
    final String nameWithProhibitChars = "Month 1";
    final String id = "Month 1";

    Column[] columns = new Column[] { createColumn( id, nameWithProhibitChars ) };

    CsvDatasourceImportStrategy importStrategy = new CsvDatasourceImportStrategy( columns );
    ValueMetaInterface meta = new ValueMetaString( id );

    Assert.assertEquals( importStrategy.displayName( meta ), nameWithProhibitChars );
  }

  private Column createColumn( String id, String name ) {
    Column column = new Column();
    column.setId( id );
    column.setName( name );

    return column;
  }
}
