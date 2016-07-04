package org.pentaho.metadata.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
}
