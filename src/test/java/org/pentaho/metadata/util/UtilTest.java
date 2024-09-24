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
 * Copyright (c) 2016 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.pentaho.metadata.util.validation.ValidationStatus.StatusEnum.VALID;

public class UtilTest {

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
    assertTrue( Util.validateId( "qwerty" ) );
    assertTrue( Util.validateId( "qwerty1" ) );
    assertTrue( Util.validateId( "0qwerty" ) );
    assertTrue( Util.validateId( "qwerty_1" ) );
    assertTrue( Util.validateId( "qwerty_$1" ) );
    assertTrue( Util.validateId( "qWerTy_$1" ) );
    assertTrue( Util.validateId( "Кириллические_символы" ) );
    assertTrue( Util.validateId( "日本の手紙" ) );
    assertTrue( Util.validateId( "caractères_français" ) );
  }

  @Test
  public void validateId_ValidateTestToIdSamples() {
    for ( Map.Entry<String, String> entry : prepareCorrectionMapping().entrySet() ) {
      assertFalse( entry.getKey(), Util.validateId( entry.getKey() ) );
      assertTrue( entry.getValue(), Util.validateId( entry.getValue() ) );
    }
  }

  @Test
  public void validateId_Null() {
    assertFalse( Util.validateId( null ) );
  }

  @Test
  public void validateId_Empty() {
    assertFalse( Util.validateId( "" ) );
  }

  @Test
  public void validateEntityId_Acceptable() {
    assertTrue( Util.validateEntityId( "qwerty" ).statusEnum.equals( VALID ) );
    assertTrue( Util.validateEntityId( "qwerty1" ).statusEnum.equals( VALID ) );
    assertTrue( Util.validateEntityId( "0qwerty" ).statusEnum.equals( VALID ) );
    assertTrue( Util.validateEntityId( "qwerty_1" ).statusEnum.equals( VALID ) );
    assertTrue( Util.validateEntityId( "qwerty_$1" ).statusEnum.equals( VALID ) );
    assertTrue( Util.validateEntityId( "qWerTy_$1" ).statusEnum.equals( VALID ) );
    assertTrue( Util.validateEntityId( "Кириллические_символы" ).statusEnum.equals( VALID ) );
    assertTrue( Util.validateEntityId( "日本の手紙" ).statusEnum.equals( VALID ) );
    assertTrue( Util.validateEntityId( "caractères_français" ).statusEnum.equals( VALID ) );
  }

  @Test
  public void validateEntityId_ValidateTestToIdSamples() {
    for ( Map.Entry<String, String> entry : prepareCorrectionMapping().entrySet() ) {
      assertFalse( entry.getKey(), Util.validateEntityId( entry.getKey() ).statusEnum.equals( VALID ) );
      assertTrue( entry.getValue(), Util.validateEntityId( entry.getValue() ).statusEnum.equals( VALID ) );
    }
  }

  @Test
  public void validateEntityId_Null() {
    assertFalse( Util.validateEntityId( null ).statusEnum.equals( VALID ) );
  }

  @Test
  public void validateEntityId_Empty() {
    assertFalse( Util.validateEntityId( "" ).statusEnum.equals( VALID ) );
  }
}
