/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
package org.pentaho.metadata.libformula;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * This function is similar to the LIKE function in SQL, and is needed
 * for the inline ETL implementation of Pentaho Metadata.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class LikeFunction implements Function {
  private static final long serialVersionUID = 5834421661720115093L;
  
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair(LogicalType.TYPE, Boolean.FALSE);
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair(LogicalType.TYPE, Boolean.TRUE);

  public LikeFunction(){
  }

  public TypeValuePair evaluate(final FormulaContext context, final ParameterCallback parameters) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if (parameterCount != 2) {
      throw new EvaluationException(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type textType1 = parameters.getType(0);
    final Object textValue1 = parameters.getValue(0);
    final Type textType2 = parameters.getType(1);
    final Object textValue2 = parameters.getValue(1);

    final String text = typeRegistry.convertToText(textType1, textValue1);
    
    String regex = typeRegistry.convertToText(textType2, textValue2);

    // replace any * or % with .*
    regex = regex.replaceAll("\\*", ".*").replaceAll("%", ".*");
    
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(text);
    
    return m.find() ? RETURN_TRUE : RETURN_FALSE;
  }

  public String getCanonicalName()
  {
    return "LIKE";
  }

}