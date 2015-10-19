package org.pentaho.metadata.libformula;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class FunctionsTest {

  @Test
  public void testBegingsWithPositive() {
    BeginsWithFunction bwf = new BeginsWithFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.TRUE ) );
    }
    catch( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }

  @Test
  public void testBegingsWithNegative() {
    BeginsWithFunction bwf = new BeginsWithFunction();
    StaticValue[] params = new StaticValue[ 2 ];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "string", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.FALSE ) );
    }
    catch( Exception ex ) {
      fail();
    }
  }

  @Test
  public void testContainsPositive() {
    ContainsFunction bwf = new ContainsFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.TRUE ) );
    }
    catch( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }

  @Test
  public void testContainsNegative() {
    ContainsFunction bwf = new ContainsFunction();
    StaticValue[] params = new StaticValue[ 2 ];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "string1", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.FALSE ) );
    }
    catch( Exception ex ) {
      fail();
    }
  }

  @Test
  public void testEndsWithPositive() {
    EndsWithFunction bwf = new EndsWithFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "string", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.TRUE ) );
    }
    catch( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }

  @Test
  public void testEndsWithNegative() {
    EndsWithFunction bwf = new EndsWithFunction();
    StaticValue[] params = new StaticValue[ 2 ];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.FALSE ) );
    }
    catch( Exception ex ) {
      fail();
    }
  }

  @Test
  public void testEqualsPositive() {
    EqualsFunction bwf = new EqualsFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my favorite string", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.TRUE ) );
    }
    catch( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }

  @Test
  public void testEqualsNegative() {
    EqualsFunction bwf = new EqualsFunction();
    StaticValue[] params = new StaticValue[ 2 ];
    params[0] = new StaticValue( "my favorite string", TextType.TYPE );
    params[1] = new StaticValue( "my", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.FALSE ) );
    }
    catch( Exception ex ) {
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
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.TRUE ) );
    }
    catch( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }

  @Test
  public void testInNegative() {
    InFunction bwf = new InFunction();
    StaticValue[] params = new StaticValue[4];
    params[0] = new StaticValue( "string", TextType.TYPE );
    params[1] = new StaticValue( "my favorite string", TextType.TYPE );
    params[2] = new StaticValue( "my string", TextType.TYPE );
    params[3] = new StaticValue( "my favorite", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.FALSE ) );
    }
    catch( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }

  @Test
  public void testLikePositive() {
    LikeFunction bwf = new LikeFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "string", TextType.TYPE );
    params[1] = new StaticValue( "*in*", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.TRUE ) );
      params[1] = new StaticValue( "*ing", TextType.TYPE );      
      tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.TRUE ) );
    }
    catch( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }

  @Test
  public void testLikeNegative() {
    LikeFunction bwf = new LikeFunction();
    StaticValue[] params = new StaticValue[2];
    params[0] = new StaticValue( "string", TextType.TYPE );
    params[1] = new StaticValue( "bin", TextType.TYPE );
    ParameterCallback parameters = new TestParameterCallback( params );
    try {
      TypeValuePair tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.FALSE ) );
      params[1] = new StaticValue( "ltrin", TextType.TYPE );
      tvp = bwf.evaluate( new DefaultFormulaContext(), parameters );
      assertTrue( tvp.getValue().equals( Boolean.FALSE ) );
    }
    catch( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }

  class TestParameterCallback implements ParameterCallback {
    private StaticValue[] values = new StaticValue[0];

    public TestParameterCallback ( StaticValue[] vals ) {
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
