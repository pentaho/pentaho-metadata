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

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * This function checks to see if a substring is within a larger string and is needed for the inline ETL implementation
 * of Pentaho Metadata.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class BeginsWithFunction implements Function {
  private static final long serialVersionUID = 5834421661720115093L;

  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );

  public BeginsWithFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 2 ) {
      throw new EvaluationException( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type textType1 = parameters.getType( 0 );
    final Object textValue1 = parameters.getValue( 0 );
    final Type textType2 = parameters.getType( 1 );
    final Object textValue2 = parameters.getValue( 1 );

    final String text = typeRegistry.convertToText( textType1, textValue1 );
    final String substring = typeRegistry.convertToText( textType2, textValue2 );

    return text.startsWith( substring ) ? RETURN_TRUE : RETURN_FALSE;
  }

  public String getCanonicalName() {
    return "BEGINSWITH";
  }

}
