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
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.util.Const;


public class NewPropertyDialog extends Dialog
{
	private LogWriter    log;

    private Label wNameLabel;
    private FormData fdNameLabel;

    private Button       wUseDefault;
    private FormData     fdUseDefault;

    private List         wDefaults;
    private FormData     fdDefaults;

    private Text         wName;
    private FormData     fdName;

    private Label wTypeLabel;
    private FormData fdTypeLabel;

    private List         wTypes;
    private FormData     fdTypes;

    private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private Shell shell;
	
    private PropsUI propsUI;

    private String title;
    private String message;

    private ConceptPropertyInterface property;

    private DefaultPropertyID[] defaults;
    
	public NewPropertyDialog(Shell parent, String title, String message)
	{
		super(parent, SWT.NONE);
        this.title = title;
        this.message = message;

        log=LogWriter.getInstance();
        propsUI=PropsUI.getInstance();
        
        defaults = DefaultPropertyID.getDefaults();
        property = null;
	}

	public ConceptPropertyInterface open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		propsUI.setLook(shell);
        
        log.logDebug(this.getClass().getName(), Messages.getString("General.DEBUG_OPENING_DIALOG")); //$NON-NLS-1$

        int middle = 50;
        int margin = Const.MARGIN;

        FormLayout formLayout = new FormLayout ();
        formLayout.marginWidth  = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;

        shell.setLayout(formLayout);
        shell.setText(title);

        // Start by placing the buttons at the bottom
        // That way we know how low we can go...
        
        wOK=new Button(shell, SWT.PUSH);
        wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
        wCancel=new Button(shell, SWT.PUSH);
        wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, Const.MARGIN, null);

        // ::::::::::::::::::::::::::::
        // LEFT SIDE OF THE SCREEN
        // ::::::::::::::::::::::::::::
        
        // Ask for an (optional) name for this property.
        //
        wName=new Text(shell, SWT.LEFT | SWT.BORDER);
        wName.setToolTipText(Messages.getString("NewPropertyDialog.USER_SPECIFY_NAME")); //$NON-NLS-1$
        propsUI.setLook(wName);
        fdName=new FormData();
        fdName.left   = new FormAttachment(0, 0);
        fdName.right  = new FormAttachment(middle, 0);
        fdName.bottom = new FormAttachment(wOK, -margin);
        wName.setLayoutData(fdName);
        wName.setEnabled(false);

        wNameLabel=new Label(shell, SWT.LEFT);
        wNameLabel.setText(Messages.getString("NewPropertyDialog.USER_PROPERTY_NAME")); //$NON-NLS-1$
        propsUI.setLook(wNameLabel);
        fdNameLabel=new FormData();
        fdNameLabel.left   = new FormAttachment(0, 0);
        fdNameLabel.right  = new FormAttachment(middle, 0);
        fdNameLabel.top    = new FormAttachment(0, 0);
        wNameLabel.setLayoutData(fdNameLabel);

        // Use a default property
        //
        wUseDefault=new Button(shell, SWT.CHECK);
        propsUI.setLook(wUseDefault);
        wUseDefault.setText(Messages.getString("NewPropertyDialog.USER_USE_DEFAULT_PROPERTY")); //$NON-NLS-1$
        wUseDefault.setSelection(true);
        fdUseDefault=new FormData();
        fdUseDefault.top  = new FormAttachment(wNameLabel, margin*2);
        fdUseDefault.left = new FormAttachment(0, 0);
        fdUseDefault.right= new FormAttachment(middle, 0);
        wUseDefault.setLayoutData(fdUseDefault);

        wDefaults=new List(shell, SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        propsUI.setLook(wDefaults);
        fdDefaults=new FormData();
        fdDefaults.left   = new FormAttachment(0, 0);
        fdDefaults.right  = new FormAttachment(middle, 0);
        fdDefaults.top    = new FormAttachment(wUseDefault, margin);
        fdDefaults.bottom = new FormAttachment(wName, -margin);
        wDefaults.setLayoutData(fdDefaults);
        for (int i=0;i<defaults.length;i++)
        {
            wDefaults.add(defaults[i].getDescription());
        }
        wDefaults.addSelectionListener(new SelectionAdapter()
        {
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
                ok();
            }
        });
        
        // ::::::::::::::::::::::::::::
        // RIGHT SIDE OF THE SCREEN
        // ::::::::::::::::::::::::::::

        wTypeLabel=new Label(shell, SWT.LEFT);
        wTypeLabel.setText(Messages.getString("NewPropertyDialog.USER_PROPERTY_TYPE")); //$NON-NLS-1$
        propsUI.setLook(wTypeLabel);
        fdTypeLabel=new FormData();
        fdTypeLabel.left   = new FormAttachment(middle, margin);
        fdTypeLabel.right  = new FormAttachment(100, 0);
        fdTypeLabel.top    = new FormAttachment(0, 0);
        wTypeLabel.setLayoutData(fdTypeLabel);
        //wTypeLabel.setEnabled(false);
        
        wTypes=new List(shell, SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        propsUI.setLook(wTypes);
        fdTypes=new FormData();
        fdTypes.left   = new FormAttachment(middle, margin);
        fdTypes.right  = new FormAttachment(100, 0);
        fdTypes.top    = new FormAttachment(wUseDefault, margin);
        fdTypes.bottom = new FormAttachment(wName, -margin);
        wTypes.setLayoutData(fdTypes);
        wTypes.setItems( ConceptPropertyType.getTypeDescriptions() );

		// Add listeners
		lsCancel = new Listener() { public void handleEvent(Event e) { cancel();  } };
		lsOK     = new Listener() { public void handleEvent(Event e) { ok(); } };
		
		wCancel.addListener(SWT.Selection, lsCancel );
		wOK    .addListener(SWT.Selection, lsOK );
        
        wDefaults.addSelectionListener
        (
            new SelectionAdapter() 
            { 
                public void widgetSelected(SelectionEvent event) 
                {  
                    // What was selected?
                    int idx = wDefaults.getSelectionIndex();
                    if (idx>=0 && idx<defaults.length)
                    {
                        String typeDesc = defaults[idx].getType().getDescription();
                        int typesIndex = wTypes.indexOf(typeDesc);
                        if (typesIndex>=0) wTypes.select(typesIndex);
                    }
                } 
            }
        );

        wUseDefault.addSelectionListener
            (
                new SelectionAdapter() 
                { 
                    public void widgetSelected(SelectionEvent event) 
                    {  
                      /*
                       * (GEM) 
                        Here is how the dialog functions now, which resolves several small issues:
                        When "Use a Default Property" is checked:
                        1. Default properties and types are both enabled.
                        2. The text box at the bottom of the dialog (for defining custom properties) is disabled.

                        When "Use a Default Property" is unchecked:
                        1. If a default property was selected at the time that the checkbox is unchecked, it will be deselected.
                        2. The default properteis are disabled
                        3. The types are left enabled, do that atype can be selected for the custom property being defined
                        4. The text box at the bottom of the dialog (for defining custom properties) is enabled.
                       */
                      
                        wDefaults.setEnabled(wUseDefault.getSelection());
                        wName.setEnabled(!wUseDefault.getSelection());
                        if (!wDefaults.isEnabled()){
                          wDefaults.deselectAll();
                          wTypes.deselectAll(); 
                        }
                       // wTypes.setEnabled(!wUseDefault.getSelection());
                    }
                }
            );
        

		// lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );
	
		WindowProperty winprop = propsUI.getScreen(shell.getText());
		if (winprop!=null) winprop.setShell(shell); else shell.pack();
				
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
        return property;
	}

    public void dispose()
	{
        shell.dispose();
	}
    
    private void cancel()
	{
        property=null;
		dispose();
	}
	
	private void ok()
	{
	    if (wUseDefault.getSelection())
        {
            if (wDefaults.getSelectionCount()!=1) return;
            
            int idx = wDefaults.getSelectionIndex();
            
            property = defaults[idx].getDefaultValue();
        }
        else
        {
            if (Const.isEmpty(wName.getText()) || wTypes.getSelectionCount()!=1) return;
            
            String id = wName.getText();
            ConceptPropertyType type = ConceptPropertyType.getType( wTypes.getSelection()[0] );
            
            property = DefaultPropertyID.getDefaultEmptyProperty(type, id);
        }
        
        dispose();
	}
    
    public static final ConceptPropertyInterface addNewProperty(Shell shell, ConceptInterface concept)
    {
        if (concept==null) return null;
        
        // First ask for a new property
        NewPropertyDialog newPropertyDialog = new NewPropertyDialog(shell, Messages.getString("NewPropertyDialog.USER_NEW_PROPERTY"), Messages.getString("NewPropertyDialog.USER_ENTER_PROPERTY_NAME_TYPE")); //$NON-NLS-1$ //$NON-NLS-2$
        ConceptPropertyInterface property = newPropertyDialog.open();
        if (property!=null)
        {
            // see if the property already exists.
            // If this is the case, copy the value over...
            ConceptPropertyInterface previousProperty = concept.getProperty(property.getId());
            if (previousProperty!=null)
            {
                property.setValue(previousProperty.getValue());
            }
            
            // Add this one to the concept so that we can edit it...
            concept.addProperty(property);
            
            
            
            return property;
        }
        return null;
    }


    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }
}
