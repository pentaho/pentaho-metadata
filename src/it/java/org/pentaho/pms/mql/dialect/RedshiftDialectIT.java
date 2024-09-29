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


package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class RedshiftDialectIT extends MetadataTestBase {

  public void testQuoteStringLiteralNLS() {
    DefaultSQLDialect redshiftDialect = new RedshiftDialect();
    String quotedWord = redshiftDialect.quoteStringLiteral( "Instalação" );
    assertEquals( "'Instalação'", quotedWord );
  }
}
