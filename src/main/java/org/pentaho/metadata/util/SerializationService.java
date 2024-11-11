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
