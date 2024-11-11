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

package org.pentaho.pms.test;

import org.pentaho.pms.schema.BusinessModel;

@SuppressWarnings( "deprecation" )
public class TestBV {
  public static void main( String[] args ) {
    // Top level object: BusinessModel
    // this contains one or more localized business model metadata sets.
    //
    BusinessModel businessModel = new BusinessModel();
    businessModel.getConcept().setName( "en_US", "Customer information" ); //$NON-NLS-1$ //$NON-NLS-2$
    businessModel.getConcept().setDescription( "en_US", "This is the customer information Business Model" ); //$NON-NLS-1$ //$NON-NLS-2$

    // Add one or more business Tables to this...
    /*
     * BusinessTable table1 = new BusinessTable(); BusinessColumn table1Column1 = new BusinessColumn("Customer name");
     * table1.addColumn(table1Column1);
     */
    // The business column uses a physical column
    //
  }
}
