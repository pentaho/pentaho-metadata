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


package org.pentaho.pms.schema.concept.dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.RequiredProperties;
import org.pentaho.pms.schema.DefaultProperty;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.trans.step.BaseStepDialog;


/***
 * Represents a business category
 * 
 * @since 30-aug-2006
 *
 */
public class ShowDefaultPropertiesDialog extends Dialog
{
	private LogWriter    log;

	private Label        wlMessage;
	private FormData     fdlMessage;
    
    private Label        wlList;
    private List         wList;
    private FormData     fdlList, fdList;

    private TableView    wFields;
    private FormData     fdFields;

	private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private Shell         shell;
	
    private Props props;

    private RequiredProperties requiredProperties;
    private String title;
    private String message;

	public ShowDefaultPropertiesDialog(Shell parent, String title, String messsage, RequiredProperties requiredProperties)
	{
		super(parent, SWT.NONE);
		this.requiredProperties = requiredProperties;
        this.title = title;
        this.message = messsage;
        
        log=LogWriter.getInstance();
        props=Props.getInstance();
	}

	public RequiredProperties open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
        
        log.logDebug(this.getClass().getName(), Messages.getString("General.DEBUG_OPENING_DIALOG")); //$NON-NLS-1$

		ModifyListener lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				requiredProperties.setChanged();
			}
		};

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(title);
		
		int middle = props.getMiddlePct()/2;
		int margin = Const.MARGIN;

        // List line
        wlMessage=new Label(shell, SWT.LEFT);
        wlMessage.setText(message);
        props.setLook(wlMessage);
        fdlMessage=new FormData();
        fdlMessage.left = new FormAttachment(0, 0);
        fdlMessage.right= new FormAttachment(100, 0);
        fdlMessage.top  = new FormAttachment(0, 0);
        wlMessage.setLayoutData(fdlMessage);

        // List line
        wlList=new Label(shell, SWT.LEFT);
        wlList.setText(Messages.getString("ShowDefaultPropertiesDialog.USER_SUBJECT")); //$NON-NLS-1$
        props.setLook(wlList);
        fdlList=new FormData();
        fdlList.left = new FormAttachment(0, 0);
        fdlList.right= new FormAttachment(middle, 0);
        fdlList.top  = new FormAttachment(wlMessage, 2*margin);
        wlList.setLayoutData(fdlList);
        wList=new List(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        props.setLook(wList);
        Class[] subjects = requiredProperties.getSubjects();
        for (int i=0;i<subjects.length;i++)
        {
            wList.add(subjects[i].getName());
        }
        if (subjects.length>0)
        {
            wList.select(0);
        }
        wList.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent arg0)
                {
                    getData();
                }
            }
        );

        // Add the table to the right of the listbox.
        ColumnInfo[] colinf=new ColumnInfo[]
            {
              new ColumnInfo(Messages.getString("ShowDefaultPropertiesDialog.USER_NAME"),           ColumnInfo.COLUMN_TYPE_CCOMBO, DefaultPropertyID.getDefaultPropertyIDs()), //$NON-NLS-1$
              new ColumnInfo(Messages.getString("ShowDefaultPropertiesDialog.USER_DESCRIPTION"),    ColumnInfo.COLUMN_TYPE_TEXT,   false), //$NON-NLS-1$
              new ColumnInfo(Messages.getString("ShowDefaultPropertiesDialog.USER_PROPERTY_TYPE"),  ColumnInfo.COLUMN_TYPE_CCOMBO, ConceptPropertyType.getTypeDescriptions()), //$NON-NLS-1$
              new ColumnInfo(Messages.getString("ShowDefaultPropertiesDialog.USER_DEFAULT_VALUE"),  ColumnInfo.COLUMN_TYPE_TEXT,   false), //$NON-NLS-1$
            };
        
        wFields=new TableView(shell, 
                              SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                              colinf, 
                              1,  
                              false, // true=read-only
                              lsMod,
                              props
                              );

        wOK=new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

        fdFields=new FormData();
        fdFields.left   = new FormAttachment(middle, margin);
        fdFields.top    = new FormAttachment(wlList, margin);
        fdFields.right  = new FormAttachment(100, 0);
        fdFields.bottom = new FormAttachment(wOK, -margin);
        wFields.setLayoutData(fdFields);
        
        fdList=new FormData();
        fdList.left   = new FormAttachment(0, 0);
        fdList.right  = new FormAttachment(middle, 0);
        fdList.top    = new FormAttachment(wlList, margin);
        fdList.bottom = new FormAttachment(wOK, -margin);
        wList.setLayoutData(fdList);

		// Add listeners
		lsCancel = new Listener() { public void handleEvent(Event e) { cancel();  } };
		lsOK     = new Listener() { public void handleEvent(Event e) { ok(); } };
		
		wCancel.addListener(SWT.Selection, lsCancel );
		wOK    .addListener(SWT.Selection, lsOK );

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		WindowProperty winprop = props.getScreen(shell.getText());
		if (winprop!=null) winprop.setShell(shell); else shell.pack();
		
		getData();
		
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
        return requiredProperties;
	}

	public void dispose()
	{
		props.setScreen(new WindowProperty(shell));
		shell.dispose();
	}
	
	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */ 
	public void getData()
	{
        // Remove all items...
        wFields.removeAll();
        
        String selection[] = wList.getSelection();
        if (selection.length==1)
        {
            Class[] subjects = requiredProperties.getSubjects();
            for (int i=0;i<subjects.length;i++)
            {
                if (subjects[i].getName().equals(selection[0]))
                {
                    java.util.List list = requiredProperties.getDefaultProperties(subjects[i]);
                    for (int x=0;x<list.size();x++)
                    {
                        DefaultProperty defaultProperty = (DefaultProperty) list.get(x);
                        TableItem item = new TableItem(wFields.table, SWT.NONE);
                        
                        String name                           = defaultProperty.getName();
                        String description                    = defaultProperty.getDescription();
                        ConceptPropertyType type              = defaultProperty.getConceptPropertyType();
                        ConceptPropertyInterface defaultValue = defaultProperty.getDefaultValue();
                        
                        if (name!=null)         item.setText(1, name);
                        if (description!=null)  item.setText(2, name);
                        if (type!=null)         item.setText(3, type.getDescription());
                        if (defaultValue!=null && defaultValue.toString()!=null) 
                                                item.setText(4, defaultValue.toString());
                    }
                }
            }
        }
        
        wFields.removeEmptyRows();
        wFields.setRowNums();
        wFields.optWidth(true);
	}
	
	private void cancel()
	{
        requiredProperties=null;
		dispose();
	}
	
	private void ok()
	{
		dispose();
	}
}
