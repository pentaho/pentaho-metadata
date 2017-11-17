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
 * Copyright (c) 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.util;

import org.junit.Test;
import org.pentaho.metadata.util.validation.IdValidationUtil;
import org.pentaho.metadata.util.validation.ValidationStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Yury_Bakhmutski on 10/25/2017.
 */
public class IdValidationUtilTest {

    private final ValidationStatus.StatusEnum VALID = ValidationStatus.StatusEnum.VALID;

    private static Map<String, String> prepareCorrectionMapping() {
        Map<String, String> result = new HashMap<String, String>();
        result.put( "\"Hello World\"", "Hello_World" );
        result.put( "`Hello World`", "Hello_World" );
        result.put( "'Hello World'", "Hello_World" );
        result.put( "\"Hello*World\"", "Hello_TIMES_World" );
        result.put( "\"Hello.World\"", "Hello_World" );
        result.put( "\"Hello/World\"", "Hello_DIVIDED_BY_World" );
        result.put( "\"Hello+World\"", "Hello_PLUS_World" );
        result.put( "{[Hello].(World)}", "_Hello_World_" );
        return result;
    }

    @Test
    public void testToId() {
        for ( Map.Entry<String, String> entry : prepareCorrectionMapping().entrySet() ) {
            assertEquals( entry.getValue(), Util.toId( entry.getKey() ) );
        }
    }

    @Test
    public void validateId_Acceptable() {
        assertTrue( IdValidationUtil.validateId( "qwerty" ).statusEnum.equals( VALID ) );
        assertTrue( IdValidationUtil.validateId( "qwerty1" ).statusEnum.equals( VALID ) );
        assertTrue( IdValidationUtil.validateId( "0qwerty" ).statusEnum.equals( VALID ) );
        assertTrue( IdValidationUtil.validateId( "qwerty_1" ).statusEnum.equals( VALID ) );
        assertTrue( IdValidationUtil.validateId( "qwerty_$1" ).statusEnum.equals( VALID ) );
        assertTrue( IdValidationUtil.validateId( "qWerTy_$1" ).statusEnum.equals( VALID ) );
    }

    @Test
    public void validateId_ValidateTestToIdSamples() {
        for ( Map.Entry<String, String> entry : prepareCorrectionMapping().entrySet() ) {
            assertFalse( entry.getKey(), IdValidationUtil.validateId( entry.getKey() ).statusEnum.equals( VALID ) );
            assertTrue( entry.getValue(), IdValidationUtil.validateId( entry.getValue() ).statusEnum.equals( VALID ) );
        }
    }

    @Test
    public void validateId_Null() {
        assertFalse( IdValidationUtil.validateId( null ).statusEnum.equals( VALID ) );
    }

    @Test
    public void validateId_Empty() {
        assertFalse( IdValidationUtil.validateId( "" ).statusEnum.equals( VALID ) );
    }

}
