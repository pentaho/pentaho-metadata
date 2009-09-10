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
package org.pentaho.metadata.util;

import java.io.InputStream;
import java.io.OutputStream;

import org.pentaho.metadata.model.Domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SerializationService {
  
  public String serializeDomain(Domain domain) {
    XStream xstream = new XStream(new DomDriver());
    return xstream.toXML(domain);
  }
  
  public void serializeDomain(Domain domain, OutputStream out) {
    XStream xstream = new XStream(new DomDriver());
    xstream.toXML(domain, out);
  }
  
  public Domain deserializeDomain(String xml) {
    XStream xstream = new XStream(new DomDriver());
    return (Domain)xstream.fromXML(xml);
  }
  
  public Domain deserializeDomain(InputStream xml) {
    XStream xstream = new XStream(new DomDriver());
    return (Domain)xstream.fromXML(xml);
  }

}
