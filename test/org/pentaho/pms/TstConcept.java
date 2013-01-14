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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
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

@SuppressWarnings("deprecation")
public class TstConcept
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Concept baseConcept = new Concept("BaseConcept"); //$NON-NLS-1$
        baseConcept.addProperty(new ConceptPropertyFont("field.font", new FontSettings("Arial", 10, false, false))); //$NON-NLS-1$ //$NON-NLS-2$

        Concept stringConcept = new Concept("StringConcept", baseConcept); //$NON-NLS-1$
        stringConcept.addProperty(ConceptPropertyDataType.STRING);
        stringConcept.addProperty(new ConceptPropertyNumber("field.data.length", 50)); //$NON-NLS-1$
        stringConcept.addProperty(new ConceptPropertyFieldType("field.type", new FieldTypeSettings(FieldTypeSettings.TYPE_DIMENSION))); //$NON-NLS-1$
        
        Concept nameConcept = new Concept("NameConcept", stringConcept); //$NON-NLS-1$
        nameConcept.addProperty(new ConceptPropertyNumber("field.data.length", 35)); //$NON-NLS-1$
        
        List<ConceptPropertyInterface> values = new ArrayList<ConceptPropertyInterface>(nameConcept.getPropertyInterfaces().values());
        for (int i = 0; i < values.size(); i++)
        {
            ConceptPropertyInterface property = (ConceptPropertyInterface) values.get(i);
        }
        
        ConceptPropertyInterface lookupLength = stringConcept.getProperty("field.data.length"); //$NON-NLS-1$
        if (lookupLength!=null)
        {
            BigDecimal length = (BigDecimal) lookupLength.getValue();
        }
    }
}
