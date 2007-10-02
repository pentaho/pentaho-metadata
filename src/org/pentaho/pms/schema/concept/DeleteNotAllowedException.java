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

public class DeleteNotAllowedException extends Exception
{
  private static final long serialVersionUID = -7170300183550487484L;

    /**
     * 
     */
    public DeleteNotAllowedException()
    {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public DeleteNotAllowedException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public DeleteNotAllowedException(String arg0)
    {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public DeleteNotAllowedException(Throwable arg0)
    {
        super(arg0);
    }

}
