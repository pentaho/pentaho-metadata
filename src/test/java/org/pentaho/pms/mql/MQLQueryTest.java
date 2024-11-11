/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.mql;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.schema.SchemaMeta;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

public class MQLQueryTest {
  private MQLQueryImpl mqlQuery;

  private static final String MALICIOUS_XML =
    "<?xml version=\"1.0\"?>\n"
      + "<!DOCTYPE lolz [\n"
      + " <!ENTITY lol \"lol\">\n"
      + " <!ELEMENT lolz (#PCDATA)>\n"
      + " <!ENTITY lol1 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n"
      + " <!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">\n"
      + " <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n"
      + " <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n"
      + " <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n"
      + " <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n"
      + " <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n"
      + " <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n"
      + " <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n"
      + "]>\n"
      + "<lolz>&lol9;</lolz>";

  @Before
  public void setUp() throws Exception {
    mqlQuery = mock( MQLQueryImpl.class );
    doCallRealMethod().when( mqlQuery ).fromXML( anyString() );
    doCallRealMethod().when( mqlQuery ).fromXML( anyString(), any( SchemaMeta.class ) );
  }

  /**
   * Testing {@link MQLQueryImpl#fromXML(String)} method
   */
  @Test( expected = PentahoMetadataException.class )
  public void exceptionThrown_WhenParsingXmlWith_BigNumberOfExternalEntities1() throws Exception {
    mqlQuery.fromXML( MALICIOUS_XML );
  }


  /**
   * Testing {@link MQLQueryImpl#fromXML(String, SchemaMeta)} method
   */
  @Test( expected = PentahoMetadataException.class )
  public void exceptionThrown_WhenParsingXmlWith_BigNumberOfExternalEntities2() throws Exception {
    mqlQuery.fromXML( MALICIOUS_XML, mock( SchemaMeta.class ) );
  }
}
