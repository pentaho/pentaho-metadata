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
package org.pentaho.pms;

import junit.framework.TestCase;

import org.pentaho.pms.messages.Messages;

/**
 * Tests the MetaData version of the localices messages to ensure we are getting the properties / messages from the
 * correct messages.properties file.
 * 
 * @author David Kincade
 */
@SuppressWarnings( "deprecation" )
public class MessageTest extends TestCase {
  public void testMessages() {
    // In the messages.propeties file, the following is defined:
    // MessagesTest.CODE_TEST_MESSAGE=Test Message
    String result = Messages.getString( "MessagesTest.CODE_TEST_MESSAGE" ); //$NON-NLS-1$
    assertEquals( "Test Message", result ); //$NON-NLS-1$
  }
}
