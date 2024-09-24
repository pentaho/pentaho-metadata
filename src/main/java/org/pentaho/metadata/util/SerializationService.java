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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */
package org.pentaho.metadata.util;

import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.io.xml.AbstractXmlDriver;
import org.pentaho.metadata.model.Domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SerializationService {

  public String serializeDomain( Domain domain ) {


    XStream xstream = createXStreamWithAllowedTypes( new DomDriver(), null);
    return xstream.toXML( domain );
  }

  public void serializeDomain( Domain domain, OutputStream out ) {
    XStream xstream = createXStreamWithAllowedTypes( new DomDriver() );
    xstream.toXML( domain, out );
  }

  public Domain deserializeDomain( String xml ) {

    try {
      XStream xstream = createXStreamWithAllowedTypes(new DomDriver(), Domain.class );
      return (Domain) xstream.fromXML( xml );
    } catch ( StreamException e ) {
      // try to load ASCII. This addresses sample domains being mixed with customer created ones in
      // a different encoding.
      XStream xstream = createXStreamWithAllowedTypes( new DomDriver("ISO-8859-1" ), Domain.class );
      return (Domain) xstream.fromXML( xml );
    }
  }

  public Domain deserializeDomain( InputStream stream ) {

    try {
      XStream xstream = createXStreamWithAllowedTypes( new DomDriver(), Domain.class );
      return (Domain) xstream.fromXML( stream );
    } catch ( StreamException e ) {
      // try to load ASCII. This addresses sample domains being mixed with customer created ones in
      // a different encoding.
      XStream xstream = createXStreamWithAllowedTypes( new DomDriver("ISO-8859-1" ), Domain.class );
      return (Domain) xstream.fromXML( stream );
    }
  }

  public static XStream createXStreamWithAllowedTypes( AbstractXmlDriver driver, Class ... classes ) {
    XStream xstream = driver == null ? new XStream() : new XStream( driver );
      if( classes != null ) {
        xstream.allowTypes( classes );
      }
      return xstream;
  }


}
