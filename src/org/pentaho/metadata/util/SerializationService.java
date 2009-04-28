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
