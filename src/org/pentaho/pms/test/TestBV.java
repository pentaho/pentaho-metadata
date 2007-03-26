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
package org.pentaho.pms.test;

import org.pentaho.pms.schema.BusinessModel;

public class TestBV
{
    public static void main(String[] args)
    {
        // Top level object: BusinessModel
        //      this contains one or more localized business model metadata sets.
        //
        BusinessModel businessModel = new BusinessModel();
        businessModel.getConcept().setName("en_US", "Customer information");
        businessModel.getConcept().setDescription("en_US", "This is the customer information Business Model");
        
        // Add one or more business Tables to this...
        /*
        BusinessTable table1 = new BusinessTable();
        BusinessColumn table1Column1 = new BusinessColumn("Customer name");
        table1.addColumn(table1Column1);
        */
        // The business column uses a physical column
        //
    }
}
