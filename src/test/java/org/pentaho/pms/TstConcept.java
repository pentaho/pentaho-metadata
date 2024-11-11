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

package org.pentaho.pms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.fieldtype.ConceptPropertyFieldType;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumber;

@SuppressWarnings( "deprecation" )
public class TstConcept {

  /**
   * @param args
   */
  public static void main( String[] args ) {
    Concept baseConcept = new Concept( "BaseConcept" ); //$NON-NLS-1$
    baseConcept.addProperty( new ConceptPropertyFont( "field.font", new FontSettings( "Arial", 10, false, false ) ) ); //$NON-NLS-1$ //$NON-NLS-2$

    Concept stringConcept = new Concept( "StringConcept", baseConcept ); //$NON-NLS-1$
    stringConcept.addProperty( ConceptPropertyDataType.STRING );
    stringConcept.addProperty( new ConceptPropertyNumber( "field.data.length", 50 ) ); //$NON-NLS-1$
    stringConcept.addProperty( new ConceptPropertyFieldType(
        "field.type", new FieldTypeSettings( FieldTypeSettings.TYPE_DIMENSION ) ) ); //$NON-NLS-1$

    Concept nameConcept = new Concept( "NameConcept", stringConcept ); //$NON-NLS-1$
    nameConcept.addProperty( new ConceptPropertyNumber( "field.data.length", 35 ) ); //$NON-NLS-1$

    List<ConceptPropertyInterface> values =
        new ArrayList<ConceptPropertyInterface>( nameConcept.getPropertyInterfaces().values() );
    for ( int i = 0; i < values.size(); i++ ) {
      ConceptPropertyInterface property = (ConceptPropertyInterface) values.get( i );
    }

    ConceptPropertyInterface lookupLength = stringConcept.getProperty( "field.data.length" ); //$NON-NLS-1$
    if ( lookupLength != null ) {
      BigDecimal length = (BigDecimal) lookupLength.getValue();
    }
  }
}
