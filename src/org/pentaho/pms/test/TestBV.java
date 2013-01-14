/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.test;

import org.pentaho.pms.schema.BusinessModel;

@SuppressWarnings("deprecation")
public class TestBV
{
    public static void main(String[] args)
    {
        // Top level object: BusinessModel
        //      this contains one or more localized business model metadata sets.
        //
        BusinessModel businessModel = new BusinessModel();
        businessModel.getConcept().setName("en_US", "Customer information"); //$NON-NLS-1$ //$NON-NLS-2$
        businessModel.getConcept().setDescription("en_US", "This is the customer information Business Model"); //$NON-NLS-1$ //$NON-NLS-2$
        
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
