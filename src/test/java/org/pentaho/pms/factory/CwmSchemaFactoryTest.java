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

package org.pentaho.pms.factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.CwmDescription;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmModelElement;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregation;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregationList;
import org.pentaho.pms.schema.concept.types.alignment.AlignmentSettings;
import org.pentaho.pms.schema.concept.types.alignment.ConceptPropertyAlignment;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.color.ColorSettings;
import org.pentaho.pms.schema.concept.types.color.ConceptPropertyColor;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.date.ConceptPropertyDate;
import org.pentaho.pms.schema.concept.types.fieldtype.ConceptPropertyFieldType;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumber;
import org.pentaho.pms.schema.concept.types.rowlevelsecurity.ConceptPropertyRowLevelSecurity;
import org.pentaho.pms.schema.concept.types.security.ConceptPropertySecurity;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.concept.types.tabletype.ConceptPropertyTableType;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.concept.types.url.ConceptPropertyURL;
import org.pentaho.pms.schema.security.RowLevelSecurity;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.util.Const;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Andrei Abramov
 */
public class CwmSchemaFactoryTest {

    CWM cwm;
    CwmModelElement modelElement;
    ConceptInterface concept;
    CwmDescription description;
    String[] ids= {
            "LOCALIZED_STRING",
            "STRING",
            "BOOLEAN",
            "DATE",
            "TABLETYPE",
            "FIELDTYPE",
            "AGGREGATION",
            "AGGREGATION_LIST",
            "NUMBER",
            "COLOR",
            "DATATYPE",
            "FONT",
            "URL",
            "SECURITY",
            "ROW_LEVEL_SECURITY",
            "ALIGNMENT",
            "COLUMN_WIDTH"
    };

    private abstract class CwmDescriptionImpl implements CwmDescription {

        String body;
        String language;
        String type;
        String name;

        public String getBody() {
            return body;
        }

        public void setBody( String body ) {
            this.body = body;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage( String language ) {
            this.language = language;
        }

        public String getType() {
            return type;
        }

        public void setType( String type ) {
            this.type = type;
        }

        public Collection getModelElement() {
            return Collections.EMPTY_LIST;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Before
    public void setUp() throws Exception {
        cwm = mock( CWM.class );
        modelElement = mock( CwmModelElement.class );
        concept = mock( ConceptInterface.class );
        description = mock( CwmDescriptionImpl.class, Mockito.CALLS_REAL_METHODS );

        when( concept.getParentInterface() ).thenReturn( null );
        when( concept.getChildPropertyIDs() ).thenReturn( ids );
    }

    @Test
    public void testStoreConceptProperties() throws Exception {
        Date date = new Date( System.currentTimeMillis() );
        DateFormat df = new SimpleDateFormat( "yyyy/MM/dd'T'HH:mm:ss" );
        String dateString = df.format( date );

        when( concept.getProperty( anyString() ) ).thenReturn(
                new ConceptPropertyLocalizedString( "Localized String", LocalizedStringSettings.EMPTY ),
                new ConceptPropertyString( "String", "Value" ),
                new ConceptPropertyBoolean( "Boolean", true ),
                new ConceptPropertyDate( "Date", date ),
                new ConceptPropertyTableType( "TableType", TableTypeSettings.OTHER ),
                new ConceptPropertyFieldType( "FieldType", FieldTypeSettings.OTHER ),
                new ConceptPropertyAggregation( "Aggregation", AggregationSettings.NONE ),
                new ConceptPropertyAggregationList( "AggregationList", new ArrayList<>() ),
                new ConceptPropertyNumber( "Number", 123 ),
                new ConceptPropertyColor( "Color", ColorSettings.BLACK ),
                new ConceptPropertyDataType( "DataType", DataTypeSettings.UNKNOWN ),
                new ConceptPropertyFont( "Font", new FontSettings( "FontSettings", 12, true, true ) ),
                new ConceptPropertyURL( "URL", new URL("http://example.com/pages/") ),
                new ConceptPropertySecurity( "Security", new Security( Collections.EMPTY_MAP ) ),
                new ConceptPropertyRowLevelSecurity( "RowLevelSecurity", new RowLevelSecurity() ),
                new ConceptPropertyAlignment( "Alignment", AlignmentSettings.CENTERED ),
                new ConceptPropertyColumnWidth( "ColumnWidth", new ColumnWidth( ColumnWidth.TYPE_WIDTH_PIXELS, 20 ) )
        );
        CwmSchemaFactory csf = spy( new CwmSchemaFactory() );

        Answer<CwmDescription> answerForCreateDescription = invocation -> {
            String string = (String) invocation.getArguments()[0];
            description.setBody( string );
            return description;
        };

        HashMap<String, String> descriptions = new HashMap<>();

        Answer<Void> answerForSetDescription = invocation -> {
            CwmDescription description = (CwmDescription) invocation.getArguments()[1];
            descriptions.put( description.getType(), description.getBody() );
            return null;
        };

        when( cwm.createDescription( anyString() ) ).thenAnswer( answerForCreateDescription );
        doAnswer( answerForSetDescription ).when( cwm ).setDescription( modelElement, description );

        csf.storeConceptProperties( cwm, modelElement, concept );

        String[] expectedDescriptions = {
                "0,20",
                "Other",
                "0,0,0",
                "<row-level-security type=\"none\"><formula><![CDATA[]]></formula><entries></entries></row-level-security>",
                "Value",
                "<security>" + Const.CR + "</security>" + Const.CR,
                "<aggregationlist>" + Const.CR + "</aggregationlist>" + Const.CR,
                dateString,
                "FontSettings-12-bold-italic",
                "http://example.com/pages/",
                "Other",
                "none",
                "centered",
                "123",
                "Unknown,-1,-1",
                "Y" };

        int i = 0;
        for( Map.Entry<String, String> entry : descriptions.entrySet() ) {
            assertEquals( "storeConceptProperties() for " + entry.getKey() + " property type is invalid", expectedDescriptions[i], entry.getValue() );
            i++;
        }
        assertEquals( 16, descriptions.size() );
    }
}
