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
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.DefaultProperties;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.dialog.ConceptDefaultsDialog;
import org.pentaho.pms.schema.concept.dialog.ShowDefaultPropertiesDialog;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.security.SecurityReference;

import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.util.EnvUtil;

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
        EnvUtil.environmentInit();
        Props.init(display, Props.TYPE_PROPERTIES_MENU);
        
        // Test the default properties dialog...
        DefaultProperties defaultProperties = new DefaultProperties();
        
        ShowDefaultPropertiesDialog dialog = new ShowDefaultPropertiesDialog(shell, "Default properties", "Select the list of default properties for each subject", defaultProperties);
        DefaultProperties dp = dialog.open();
        if (dp!=null)
        {
            BusinessColumn businessColumn = new BusinessColumn();
            
            // Set some parent on the business column concept...
            Concept baseConcept = new Concept("BaseConcept");
            baseConcept.addProperty(new ConceptPropertyFont(DefaultPropertyID.FONT.getId(), new FontSettings("Arial", 14, true, true)));

            businessColumn.getConcept().setParentInterface(baseConcept);

            
            ConceptDefaultsDialog defaultsDialog = new ConceptDefaultsDialog(shell, "Business column properties", businessColumn, dp, new Locales(), new SecurityReference());
            if (defaultsDialog.open()!=null)
            {
                ConceptInterface concept = businessColumn.getConcept();
                
                
                String[] ids = concept.getChildPropertyIDs();
                for (int i=0;i<ids.length;i++)
                {
                    ConceptPropertyInterface property = concept.getProperty(ids[i]);
                    System.out.println("#"+(i+1)+" : "+property.getId()+" = "+property.toString());
                }
            }
        }
        

        Props.getInstance().saveProps();
    }

}
