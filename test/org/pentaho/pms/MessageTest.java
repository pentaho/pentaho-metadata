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
package org.pentaho.pms;

import junit.framework.TestCase;

import org.pentaho.pms.messages.Messages;

/**
 * Tests the MetaData version of the localices messages to ensure we are getting the 
 * properties / messages from the correct messages.properties file.
 *  
 * @author David Kincade
 */
@SuppressWarnings("deprecation")
public class MessageTest extends TestCase {
  public void testMessages() {
    // In the messages.propeties file, the following is defined:
    // MessagesTest.CODE_TEST_MESSAGE=Test Message
    String result = Messages.getString("MessagesTest.CODE_TEST_MESSAGE"); //$NON-NLS-1$
    assertEquals("Test Message", result); //$NON-NLS-1$
  }
}
