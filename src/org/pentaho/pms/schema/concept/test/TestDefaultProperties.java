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
package org.pentaho.pms.schema.concept.test;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Props;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.RequiredProperties;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.dialog.ConceptDefaultsDialog;
import org.pentaho.pms.schema.concept.dialog.ShowDefaultPropertiesDialog;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.security.SecurityReference;

public class TestDefaultProperties
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // Initialisation stuff for SWT and Kettle, ignore it for Web-based reporting.
        //
        Display display = new Display();
        Shell shell = new Shell(display);
        PropsUI.init(display, PropsUI.TYPE_PROPERTIES_MENU);
        
        // Test the default properties dialog...
        RequiredProperties requiredProperties = new RequiredProperties();
        
        ShowDefaultPropertiesDialog dialog = new ShowDefaultPropertiesDialog(shell, Messages.getString("TestDefaultProperties.USER_TITLE_DEFAULT_PROPERTIES"), Messages.getString("TestDefaultProperties.USER_DEFAULT_PROPERTIES"), requiredProperties); //$NON-NLS-1$ //$NON-NLS-2$
        RequiredProperties dp = dialog.open();
        if (dp!=null)
        {
            BusinessColumn businessColumn = new BusinessColumn();
            
            // Set some parent on the business column concept...
            Concept baseConcept = new Concept("BaseConcept"); //$NON-NLS-1$
            baseConcept.addProperty(new ConceptPropertyFont(DefaultPropertyID.FONT.getId(), new FontSettings("Arial", 14, true, true))); //$NON-NLS-1$

            businessColumn.getConcept().setParentInterface(baseConcept);

            
            ConceptDefaultsDialog defaultsDialog = new ConceptDefaultsDialog(shell, Messages.getString("TestDefaultProperties.USER_TITLE_BUSINESS_COLUMN_PROPERTIES"), businessColumn, dp, new Locales(), new SecurityReference()); //$NON-NLS-1$
            if (defaultsDialog.open()!=null)
            {
                ConceptInterface concept = businessColumn.getConcept();
                
                
                String[] ids = concept.getChildPropertyIDs();
                for (int i=0;i<ids.length;i++)
                {
                    ConceptPropertyInterface property = concept.getProperty(ids[i]);
                }
            }
        }
        

        Props.getInstance().saveProps();
    }

}
