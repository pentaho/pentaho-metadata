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

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;
import org.pentaho.reporting.libraries.formula.function.text.TextFunctionCategory;

/**
 * Describes the CONTAINS function
 *
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class ContainsFunctionDescription extends AbstractFunctionDescription {

  private static final long serialVersionUID = 8787262061507547672L;

  public ContainsFunctionDescription() {
    super("Contains", "org.pentaho.metadata.libformula.Contains-Function");
  }

  public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 2;
  }

  public Type getParameterType(final int position) {
    return TextType.TYPE;
  }

  public Type getValueType() {
    return LogicalType.TYPE;
  }

  public boolean isParameterMandatory(final int position) {
    return true;
  }

}
