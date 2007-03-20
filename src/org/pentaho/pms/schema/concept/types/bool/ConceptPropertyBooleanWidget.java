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
package org.pentaho.pms.schema.concept.types.bool;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.Props;

public class ConceptPropertyBooleanWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private ConceptInterface concept;
    private String name;
    private Button button;
    private boolean overwrite;
    
    public ConceptPropertyBooleanWidget(ConceptInterface concept, String name, Button button)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.button = button;
    }

    public ConceptInterface getConcept()
    {
        return concept;
    }
    
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

    public ConceptPropertyInterface getValue()
    {
       if (!hasChanged()) return null; // Return null if nothing changed! 
       return new ConceptPropertyBoolean(name, new Boolean(button.getSelection()));
    }
    
    public void setValue(ConceptPropertyInterface property)
    {
        Boolean value = (Boolean) property.getValue();
        if (value!=null)
        {
            button.setSelection(value.booleanValue());
        }
    }
    
    public void setEnabled(boolean enabled)
    {
        button.setEnabled(enabled);
    }
    
    public void setFocus()
    {
        button.setFocus();
    }


    public static final Control getControl(Composite composite, ConceptInterface concept, final String name, Control lastControl, Map conceptPropertyInterfaces)
    {
        Props props = Props.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);

        final Button button = new Button(composite, SWT.CHECK);
        props.setLook(button);
        FormData fdBoolean = new FormData();
        fdBoolean.left  = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        if (lastControl!=null) fdBoolean.top   = new FormAttachment(lastControl, Const.MARGIN); else fdBoolean.top   = new FormAttachment(0, 0);
        button.setLayoutData(fdBoolean);
        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyBooleanWidget(concept, name, button);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);    
        button.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { widgetInterface.setChanged(); } });

        return button;
    }
    
}
