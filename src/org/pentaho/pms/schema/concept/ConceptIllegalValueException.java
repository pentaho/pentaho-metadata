/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
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
package org.pentaho.pms.schema.concept;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptIllegalValueException extends Exception
{
    private static final long serialVersionUID = 4397770344129366440L;

    /**
     * 
     */
    public ConceptIllegalValueException()
    {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ConceptIllegalValueException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public ConceptIllegalValueException(String arg0)
    {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public ConceptIllegalValueException(Throwable arg0)
    {
        super(arg0);
    }

}
