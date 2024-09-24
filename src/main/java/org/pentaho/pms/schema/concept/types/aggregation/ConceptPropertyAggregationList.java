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
package org.pentaho.pms.schema.concept.types.aggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This property allows for a list of aggregations to be selected.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyAggregationList extends ConceptPropertyBase implements Cloneable {
  private List<AggregationSettings> value;

  public ConceptPropertyAggregationList( String name, List<AggregationSettings> value ) {
    this( name, value, false );
  }

  public ConceptPropertyAggregationList( String name, List<AggregationSettings> value, boolean required ) {
    super( name, required );
    setValue( value );
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( getId() ).append( isRequired() )
        .append( value ).toString();
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyAggregationList rtn = (ConceptPropertyAggregationList) super.clone();
    if ( value != null ) {
      rtn.value = new ArrayList<AggregationSettings>();
      rtn.value.addAll( value );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.AGGREGATION_LIST;
  }

  public Object getValue() {
    return value;
  }

  public String toXML() {
    StringBuffer xml = new StringBuffer();

    xml.append( "<aggregationlist>" ).append( Const.CR ); //$NON-NLS-1$
    for ( AggregationSettings setting : value ) {
      xml.append( "  <aggregation>" ).append( setting.getCode() );
      xml.append( "</aggregation>" ).append( Const.CR ); //$NON-NLS-1$
    }
    xml.append( "</aggregationlist>" ).append( Const.CR ); //$NON-NLS-1$
    return xml.toString();
  }

  public static List<AggregationSettings> fromXML( String value ) throws Exception {
    try {
      Document doc = XMLHandler.loadXMLString( value );
      List<AggregationSettings> aggSettings = new ArrayList<AggregationSettings>();
      Node node = XMLHandler.getSubNode( doc, "aggregationlist" ); //$NON-NLS-1$
      int nrAggs = XMLHandler.countNodes( node, "aggregation" ); //$NON-NLS-1$
      for ( int i = 0; i < nrAggs; i++ ) {
        Node aggNode = XMLHandler.getSubNodeByNr( node, "aggregation", i ); //$NON-NLS-1$
        String type = XMLHandler.getNodeValue( aggNode ); //$NON-NLS-1$
        if ( type != null ) {
          AggregationSettings setting = AggregationSettings.getType( type );
          if ( setting != null ) {
            aggSettings.add( setting );
          }
        }
      }
      if ( aggSettings.size() != 0 ) {
        return aggSettings;
      } else {
        return null;
      }
    } catch ( Exception e ) {
      throw new Exception(
          Messages.getString( "ConceptPropertyAggregationList.ERROR_0001_CANT_CREATE_AGGLIST_OBJECT" ), e ); //$NON-NLS-1$
    }
  }

  @SuppressWarnings( "unchecked" )
  public void setValue( Object value ) {
    if ( value != null ) {
      this.value = (List<AggregationSettings>) value;
    } else {
      this.value = new ArrayList<AggregationSettings>();
    }
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
