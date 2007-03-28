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
package org.pentaho.pms.schema.concept.types.partitioning;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.partition.PartitionSchema;
import be.ibridge.kettle.trans.step.StepPartitioningMeta;

public class ConceptPropertyDatabasePartitioningWidget  extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private ConceptInterface concept;
    private String name;
    
    private StepPartitioningMeta stepPartitioningMeta;
    private PartitionSchema partitionSchema;

    private boolean overwrite;
    
    public ConceptPropertyDatabasePartitioningWidget(ConceptInterface concept, String name, StepPartitioningMeta partitioningMeta, PartitionSchema partitionSchema)
    {
        super();
        
        this.concept = concept;
        this.name = name;
        this.stepPartitioningMeta = partitioningMeta;
        this.partitionSchema = partitionSchema;
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
        return new ConceptPropertyDatabasePartitioning(name, getDataType(stepPartitioningMeta, partitionSchema)); // TODO: create the two partitioning objects from widgets...
    }

    public void setValue(ConceptPropertyInterface property)
    {
        StepPartitioningMeta value = (StepPartitioningMeta)property.getValue();
        if (value!=null)
        {
            // TODO: set widgets
        }
    }

    public void setEnabled(boolean enabled)
    {
        // TODO: disable or enable widgets 
    }

    
    private static StepPartitioningMeta getDataType(StepPartitioningMeta stepPartitioningMeta, PartitionSchema partitionSchema)
    {
        // TODO: create step partitioning metadata from input data
        //
        return new StepPartitioningMeta(StepPartitioningMeta.PARTITIONING_METHOD_NONE, null, null);
    }

    public void setFocus()
    {
        // TODO: set focus on most obvious widget
    }

    public static final Control getControl(Composite composite, ConceptInterface concept, final String name, Control lastControl, Map conceptPropertyInterfaces)
    {
        Props props = Props.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);

        Composite dataType = new Composite(composite, SWT.NONE);
        FormLayout dataTypeLayout = new FormLayout();
        dataType.setLayout(dataTypeLayout);
        props.setLook(dataType);
        
        // TODO: create widgets 

        FormData fdDataType = new FormData();
        fdDataType.left  = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        if (lastControl!=null) fdDataType.top   = new FormAttachment(lastControl, Const.MARGIN); else fdDataType.top   = new FormAttachment(0, 0);
        dataType.setLayoutData(fdDataType);
        
        // 
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyDatabasePartitioningWidget(concept, name, null, null); // TODO: create partitioning objects from widgets...
            
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);
        
        // TODO: add modifyListeners to widgets...
        // combo.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        
        return dataType;
    }


}
