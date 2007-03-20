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
package org.pentaho.pms.schema.concept.types.alignment;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.Props;

public class ConceptPropertyAlignmentWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private ConceptInterface concept;
    private String name;
    private CCombo combo;
    private boolean overwrite;
    
    public ConceptPropertyAlignmentWidget(ConceptInterface concept, String name, CCombo combo)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.combo = combo;
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
        return new ConceptPropertyAlignment(name, AlignmentSettings.getType(combo.getText()));
    }

    public void setValue(ConceptPropertyInterface property)
    {
        AlignmentSettings value = (AlignmentSettings) property.getValue();
        if (value!=null) combo.setText(value.getDescription());
    }

    public void setEnabled(boolean enabled)
    {
        combo.setEnabled(enabled);
    }
    
    public void setFocus()
    {
        combo.setFocus();
    }
    

    public static final Control getControl(Composite composite, ConceptInterface concept, final String name, Control lastControl, Map conceptPropertyInterfaces)
    {
        Props props = Props.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);

        final CCombo combo = new CCombo(composite, SWT.BORDER);
        combo.setItems(AlignmentSettings.typeDescriptions);
        combo.setToolTipText("Select the text alignment rule for property '"+name+"'");
        props.setLook(combo);
        FormData fdCombo = new FormData();
        fdCombo.left  = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        if (lastControl!=null) fdCombo.top   = new FormAttachment(lastControl, Const.MARGIN); else fdCombo.top   = new FormAttachment(0, 0);
        combo.setLayoutData(fdCombo);
        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyAlignmentWidget(concept, name, combo);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);    
        combo.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { widgetInterface.setChanged(); } });

        return combo;
    }
    
}
