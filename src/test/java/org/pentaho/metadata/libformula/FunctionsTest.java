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
package org.pentaho.metadata.libformula;

import org.junit.Test;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FunctionsTest {

  @Test
  public void testBeginsWithPositive() {
    BeginsWithFunction bwf = new BeginsWithFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.TRUE);
  }

  @Test
  public void testBeginsWithNegative() {
    BeginsWithFunction bwf = new BeginsWithFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "string", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.FALSE);
  }

  @Test
  public void testContainsPositive() {
    ContainsFunction bwf = new ContainsFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.TRUE);
  }

  @Test
  public void testContainsNegative() {
    ContainsFunction bwf = new ContainsFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "string1", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.FALSE);
  }

  @Test
  public void testEndsWithPositive() {
    EndsWithFunction bwf = new EndsWithFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "string", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.TRUE);
  }

  @Test
  public void testEndsWithNegative() {
    EndsWithFunction bwf = new EndsWithFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.FALSE);
  }

  @Test
  public void testEqualsPositive() {
    EqualsFunction bwf = new EqualsFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my favorite string", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.TRUE);
  }

  @Test
  public void testEqualsNegative() {
    EqualsFunction bwf = new EqualsFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.FALSE);
  }

  private void evaluateAndCheck( Function function, StaticValue[] parameters, Boolean expectedResult ){
    try {
      TypeValuePair tvp = function.evaluate( new DefaultFormulaContext(), new TestParameterCallback( parameters ) );
      assertEquals( expectedResult, tvp.getValue() );
    } catch ( Exception ex ) {
      fail();
    }
  }

  @Test
  public void testInPositive() {
    InFunction bwf = new InFunction();
    StaticValue[] params = new StaticValue[5];
    params[0] = new StaticValue( "string", TextType.TYPE );
    params[1] = new StaticValue( "my favorite string", TextType.TYPE );
    params[2] = new StaticValue( "my string", TextType.TYPE );
    params[3] = new StaticValue( "my favorite", TextType.TYPE );
    params[4] = new StaticValue( "string", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.TRUE);
  }

  @Test
  public void testInNegative() {
    InFunction bwf = new InFunction();
    StaticValue[] params = new StaticValue[4];
    params[0] = new StaticValue( "string", TextType.TYPE );
    params[1] = new StaticValue( "my favorite string", TextType.TYPE );
    params[2] = new StaticValue( "my string", TextType.TYPE );
    params[3] = new StaticValue( "my favorite", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.FALSE);
  }

  @Test
  public void testLikePositive() {
    LikeFunction bwf = new LikeFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "string", TextType.TYPE );
    params[1] = new StaticValue( "*in*", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.TRUE );

    params[ 1 ] = new StaticValue( "*ing", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.TRUE );
  }

  @Test
  public void testLikeNegative() {
    LikeFunction bwf = new LikeFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "string", TextType.TYPE );
    params[1] = new StaticValue( "bin", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.FALSE);

    params[1] = new StaticValue( "ltrin", TextType.TYPE );

    evaluateAndCheck( bwf, params, Boolean.FALSE);
  }

  private static class TestParameterCallback implements ParameterCallback {
    private final StaticValue[] values;

    public TestParameterCallback( StaticValue[] vals ) {
      values = vals;
    }

    @Override
    public Object getValue( int position ) throws EvaluationException {
      return values[position].getValue();
    }

    @Override
    public Type getType( int position ) throws EvaluationException {
      return values[position].getValueType();
    }

    @Override
    public LValue getRaw( int position ) {
      return values[position];
    }

    @Override
    public int getParameterCount() {
      return values.length;
    }
  }
}
