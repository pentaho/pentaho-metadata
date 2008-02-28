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
package org.pentaho.pms.schema.concept.types.localstring;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

public class ConceptPropertyLocalizedStringWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private String name;
    private TableView wFields;
    private Locales locales;
    
    private boolean overwrite;
    private ConceptInterface concept;
    
    /**
     * @param name The property name
     * @param wFields
     * @param locales
     */
    public ConceptPropertyLocalizedStringWidget(ConceptInterface concept, String name, TableView wFields, Locales locales)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.wFields = wFields;
        this.locales = locales;
    }

    /**
     * @return the concept
     */
    public ConceptInterface getConcept()
    {
        return concept;
    }

    /**
     * @param concept the concept to set
     */
    public void setConcept(ConceptInterface concept)
    {
        this.concept = concept;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    public ConceptPropertyInterface getValue() throws Exception
    {
        if (!hasChanged()) return null; // Return null if nothing changed! 
        LocalizedStringSettings settings = new LocalizedStringSettings();
        for (int i=0;i<wFields.nrNonEmpty();i++)
        {
            TableItem item = wFields.getNonEmpty(i);
            String locale = item.getText(1);
            String string = item.getText(2);
            
            if (!Const.isEmpty(locale) && !Const.isEmpty(string))
            {
                settings.setLocaleString(locale, string);
            }
        }
        return new ConceptPropertyLocalizedString(name, settings);
    }

    public void setValue(ConceptPropertyInterface property)
    {
        LocalizedStringSettings value = (LocalizedStringSettings) property.getValue();
        String[] locs = locales.getLocaleCodes();

        wFields.removeAll();
        
        for (int i=0;i<locs.length;i++)
        {
            TableItem item = new TableItem(wFields.table, SWT.NONE);
            String string = null;
            if (value!=null) string = value.getString(locs[i]);
            
            item.setText(1, locs[i]);
            if (string!=null) item.setText(2, string);
        }
        wFields.removeEmptyRows();
        wFields.setRowNums();
        wFields.optWidth(true);
    }

    public void setEnabled(boolean enabled)
    {
       wFields.setEnabled(enabled);
       wFields.setReadonly(enabled);
    }

    public void setFocus()
    {
        wFields.setFocusOnFirstEditableField();
    }



    public static final Control getControl(Composite composite, ConceptInterface concept, final String name, Control lastControl, Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces, final Locales locales)
    {
        PropsUI props = PropsUI.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);

        ColumnInfo[] colinf=new ColumnInfo[]
           {
              new ColumnInfo(Messages.getString("ConceptPropertyLocalizedStringWidget.USER_LOCALE_DESC"),      ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
              new ColumnInfo(Messages.getString("ConceptPropertyLocalizedStringWidget.USER_STRING_DESC"),      ColumnInfo.COLUMN_TYPE_TEXT, false, false), //$NON-NLS-1$
           };
                                    
        final TableView wFields=new TableView( new Variables(),composite, 
                                          SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                                          colinf, 
                                          0,  
                                          true, // true = read-only
                                          null,
                                          props
                                          );
        
        
        FormData fdFields = new FormData();
        fdFields.left   = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        fdFields.right  = new FormAttachment(100, 0);
        if (lastControl!=null)
        {
            fdFields.top   = new FormAttachment(lastControl, Const.MARGIN); 
        }
        else 
        { 
            fdFields.top   = new FormAttachment(0, 0); 
        }

        wFields.setLayoutData(fdFields);
        wFields.pack();
        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyLocalizedStringWidget(concept, name, wFields, locales);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);
        
        wFields.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); }});
        
        return wFields;
    }
}
