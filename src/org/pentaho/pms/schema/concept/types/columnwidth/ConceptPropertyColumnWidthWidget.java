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
package org.pentaho.pms.schema.concept.types.columnwidth;

import java.math.BigDecimal;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.Props;

public class ConceptPropertyColumnWidthWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private ConceptInterface concept;
    private String name;
    private CCombo type;
    private boolean overwrite;
    private Text width;
    
    public ConceptPropertyColumnWidthWidget(ConceptInterface concept, String name, CCombo type, Text width)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.type = type;
        this.width = width;
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
        int widthType = ColumnWidth.getType(type.getText()).getType();
        BigDecimal widthValue;
        try { widthValue = new BigDecimal(width.getText()); } catch(Exception e) { widthValue = new BigDecimal(0); }
        
        return new ConceptPropertyColumnWidth(name, new ColumnWidth(widthType, widthValue));
    }

    public void setValue(ConceptPropertyInterface property)
    {
        ColumnWidth value = (ColumnWidth) property.getValue();
        if (value!=null)
        {
            type.setText(value.getDescription());
            if (value.getWidth()!=null) width.setText(value.getWidth().toString());
            else width.setText(""); //$NON-NLS-1$ 
        }
    }

    public void setEnabled(boolean enabled)
    {
        type.setEnabled(enabled);
    }
    
    public void setFocus()
    {
        type.setFocus();
    }
    

    public static final Control getControl(Composite composite, ConceptInterface concept, final String name, Control lastControl, Map conceptPropertyInterfaces)
    {
        Props props = Props.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);

        Label typeLabel = new Label(composite, SWT.NONE);
        typeLabel.setText(Messages.getString("ConceptPropertyColumnWidthWidget.USER_COLUMN_WIDTH_TYPE")); //$NON-NLS-1$
        FormData fdTypeLabel = new FormData();
        fdTypeLabel.left  = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        if (lastControl!=null) fdTypeLabel.top   = new FormAttachment(lastControl, Const.MARGIN); else fdTypeLabel.top   = new FormAttachment(0, 0);
        typeLabel.setLayoutData(fdTypeLabel);
        
        final CCombo type = new CCombo(composite, SWT.BORDER);
        type.setItems(ColumnWidth.typeDescriptions);
        type.setToolTipText(Messages.getString("ConceptPropertyColumnWidthWidget.USER_SELECT_PROPERTY_TYPE_WIDTH", name)); //$NON-NLS-1$ 
        props.setLook(type);
        FormData fdType = new FormData();
        fdType.left  = new FormAttachment(typeLabel, Const.MARGIN);
        if (lastControl!=null) fdType.top   = new FormAttachment(lastControl, Const.MARGIN); else fdType.top   = new FormAttachment(0, 0);
        type.setLayoutData(fdType);

        Label widthLabel = new Label(composite, SWT.NONE);
        widthLabel.setText(Messages.getString("ConceptPropertyColumnWidthWidget.USER_COLUMN_WIDTH")); //$NON-NLS-1$
        FormData fdWidthLabel = new FormData();
        fdWidthLabel.left  = new FormAttachment(type, 3*Const.MARGIN);
        if (lastControl!=null) fdWidthLabel.top   = new FormAttachment(lastControl, Const.MARGIN); else fdWidthLabel.top   = new FormAttachment(0, 0);
        widthLabel.setLayoutData(fdWidthLabel);
        
        final Text width = new Text(composite, SWT.BORDER | SWT.SINGLE | SWT.LEFT);
        width.setToolTipText(Messages.getString("ConceptPropertyColumnWidthWidget.USER_SELECT_PROPERTY_WIDTH", name)); //$NON-NLS-1$ 
        props.setLook(width);
        FormData fdWidth = new FormData();
        fdWidth.left  = new FormAttachment(widthLabel, Const.MARGIN);
        fdWidth.right = new FormAttachment(widthLabel, Const.MARGIN+250);
        if (lastControl!=null) fdWidth.top   = new FormAttachment(lastControl, Const.MARGIN); else fdWidth.top   = new FormAttachment(0, 0);
        width.setLayoutData(fdWidth);

        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyColumnWidthWidget(concept, name, type, width);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);    
        type.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { widgetInterface.setChanged(); } });
        type.addModifyListener(new ModifyListener() {  public void modifyText(ModifyEvent arg0) { widgetInterface.setChanged(); } });

        return type;
    }
    
}
