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
package org.pentaho.pms.schema.concept.types.datatype;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

public class ConceptPropertyDataTypeWidget  extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private ConceptInterface concept;
    private String name;
    private CCombo combo;
    private Text length;
    private Text precision;
    private boolean overwrite;
    
    public ConceptPropertyDataTypeWidget(ConceptInterface concept, String name, CCombo combo, Text length, Text precision)
    {
        super();
        
        this.concept = concept;
        this.name = name;
        this.combo = combo;
        this.length = length;
        this.precision = precision;
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

    public ConceptPropertyInterface getValue()
    {
        if (!hasChanged()) return null; // Return null if nothing changed! 
        return new ConceptPropertyDataType(name, getDataType(combo.getText(), length.getText(), precision.getText()));
    }

    public void setValue(ConceptPropertyInterface property)
    {
        DataTypeSettings value = (DataTypeSettings)property.getValue();
        if (value!=null)
        {
            combo.setText(value.getDescription());
            if (value.getLength()>=0) length.setText(Integer.toString(value.getLength()));
            if (value.getPrecision()>=0) precision.setText(Integer.toString(value.getPrecision()));
        }
    }

    public void setEnabled(boolean enabled)
    {
        combo.setEnabled(enabled);
        length.setEnabled(enabled);
        precision.setEnabled(enabled);
    }

    
    private static DataTypeSettings getDataType(String typeDescription, String lengthString, String precisionString)
    {
        int type = DataTypeSettings.getType(typeDescription).getType();
        int length = Const.toInt(lengthString, -1);
        int precision = Const.toInt(precisionString, -1);
        return new DataTypeSettings(type, length, precision);
    }

    public void setFocus()
    {
        combo.setFocus();
    }

    public static final Control getControl(Composite composite, ConceptInterface concept, final String name, Control lastControl, Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces)
    {
        PropsUI props = PropsUI.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);

        Composite dataType = new Composite(composite, SWT.NONE);
        FormLayout dataTypeLayout = new FormLayout();
        dataType.setLayout(dataTypeLayout);
        props.setLook(dataType);
        
        // A combo with the data type and 2 text fields with length and precision
        //
        final CCombo combo = new CCombo(dataType, SWT.BORDER);
        combo.setItems(DataTypeSettings.getTypeDescriptions());
        props.setLook(combo);
        FormData fdCombo = new FormData();
        fdCombo.left  = new FormAttachment(0, 0);
        fdCombo.top   = new FormAttachment(0, 0);
        combo.setLayoutData(fdCombo);

        // Length
        Label lengthLabel = new Label(dataType, SWT.LEFT);
        props.setLook(lengthLabel);
        lengthLabel.setText(Messages.getString("ConceptPropertyDataTypeWidget.USER_LENGTH"));  //$NON-NLS-1$
        FormData fdLengthLabel = new FormData();
        fdLengthLabel.left   = new FormAttachment(combo, 4*Const.MARGIN);
        fdLengthLabel.top    = new FormAttachment(combo, 0, SWT.CENTER);
        lengthLabel.setLayoutData(fdLengthLabel);

        final Text length = new Text(dataType, SWT.BORDER);
        props.setLook(length);
        FormData fdLength = new FormData();
        fdLength.left  = new FormAttachment(lengthLabel, Const.MARGIN);
        fdLength.top   = new FormAttachment(combo, 0, SWT.CENTER);
        length.setLayoutData(fdLength);
        
        // Precision
        Label precisionLabel = new Label(dataType, SWT.LEFT);
        props.setLook(precisionLabel);
        precisionLabel.setText(Messages.getString("ConceptPropertyDataTypeWidget.USER_PRECISION")); //$NON-NLS-1$
        FormData fdPrecisionLabel = new FormData();
        fdPrecisionLabel.left   = new FormAttachment(length, 4*Const.MARGIN);
        fdPrecisionLabel.top    = new FormAttachment(combo, 0, SWT.CENTER);
        precisionLabel.setLayoutData(fdPrecisionLabel);

        final Text precision = new Text(dataType, SWT.BORDER);
        props.setLook(precision);
        FormData fdPrecision = new FormData();
        fdPrecision.left  = new FormAttachment(precisionLabel, Const.MARGIN);
        fdPrecision.top   = new FormAttachment(combo, 0, SWT.CENTER);
        precision.setLayoutData(fdPrecision);
          
        FormData fdDataType = new FormData();
        fdDataType.left  = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        if (lastControl!=null) fdDataType.top   = new FormAttachment(lastControl, Const.MARGIN); else fdDataType.top   = new FormAttachment(0, 0);
        dataType.setLayoutData(fdDataType);
        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyDataTypeWidget(concept, name, combo, length, precision);
            
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);
        
        combo.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        length.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        precision.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });

        return dataType;
    }


}
