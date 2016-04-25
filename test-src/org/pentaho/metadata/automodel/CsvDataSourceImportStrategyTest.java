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
 * Copyright (c) 2016 Pentaho Corporation.  All rights reserved.
 */

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
