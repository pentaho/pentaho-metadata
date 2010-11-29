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
 * Copyright 2005 - 2010 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.metadata.query.model.util;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.metadata.TestHelper;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class QueryXmlHelperTest {

  private QueryXmlHelper helper;
  private DocumentBuilderFactory documentBuilderFactory;
  private DocumentBuilder db;
  private Document doc;
  private Query query;

  @Before
  public void init() throws Exception {
    helper = new QueryXmlHelper();
    query = new Query(TestHelper.getBasicDomain(), TestHelper.buildDefaultModel());
    documentBuilderFactory = DocumentBuilderFactory.newInstance();
    db = documentBuilderFactory.newDocumentBuilder();
    doc = db.newDocument();
  }

  @Test
  public void testAddParameterFromXmlNode() throws Exception {
    Element paramElement = doc.createElement("parameter"); //$NON-NLS-1$
    paramElement.setAttribute("name", "param1"); //$NON-NLS-1$
    paramElement.setAttribute("type", "STRING"); //$NON-NLS-1$
    paramElement.setAttribute("defaultValue", "a;b"); //$NON-NLS-1$ //$NON-NLS-2$

    helper.addParameterFromXmlNode(query, paramElement);
    assertEquals(2, ((Object[])query.getParameters().get(0).getDefaultValue()).length);
  }

  @Test
  public void testParseMutivaluedDefault_String() throws Exception {
    Object values = helper.parseDefaultValue("a;b;c;d;e", DataType.STRING);
    assertTrue(values instanceof String[]);
    String[] stringValues = (String[])values;
    assertEquals(5, stringValues.length);
    assertEquals("a", stringValues[0]);
    assertEquals("b", stringValues[1]);
    assertEquals("c", stringValues[2]);
    assertEquals("d", stringValues[3]);
    assertEquals("e", stringValues[4]);

    values = helper.parseDefaultValue("a", DataType.STRING);
    assertTrue(values instanceof String);
    assertEquals("a", values);

    // try "a;b";"c;d"
    values = helper.parseDefaultValue("\"a;b\";\"c;d\"", DataType.STRING);
    assertTrue(values instanceof String[]);
    stringValues = (String[])values;
    assertEquals(2, stringValues.length);
    assertEquals("a;b", stringValues[0]);
    assertEquals("c;d", stringValues[1]);    
  }

  @Test
  public void testParseMultivaluedDefault_Numeric() throws Exception {
    Object values = helper.parseDefaultValue("1;2;3;4;5", DataType.NUMERIC);
    assertTrue(values instanceof Double[]);
    Double[] numericValues = (Double[])values;
    assertEquals(5, numericValues.length);
    assertEquals(1D, numericValues[0]);
    assertEquals(2D, numericValues[1]);
    assertEquals(3D, numericValues[2]);
    assertEquals(4D, numericValues[3]);
    assertEquals(5D, numericValues[4]);
  }

  @Test
  public void testParseMultivaluedDefault_Boolean() throws Exception {
    Object values = helper.parseDefaultValue("true;false", DataType.BOOLEAN);
    assertTrue(values instanceof Boolean[]);
    Boolean[] boolValues = (Boolean[])values;
    assertEquals(2, boolValues.length);
    assertTrue(boolValues[0]);
    assertFalse(boolValues[1]);
  }
}
