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


package org.pentaho.pms.schema.dialog;
import java.util.Hashtable;
import java.util.Map;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.dialog.ConceptDefaultsDialog;
import org.pentaho.pms.schema.concept.dialog.ConceptDialog;
import org.pentaho.pms.schema.concept.dialog.NewPropertyDialog;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.trans.step.BaseStepDialog;


/***
 * Represents a business category
 * 
 * @since 30-aug-2006
 *
 */
public class BusinessCategoryDialog extends Dialog
{
	private LogWriter    log;

	private Label        wlId;
	private Text         wId;
	private FormData     fdlId, fdId;

	private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private Shell         shell;
	
	private SelectionAdapter lsDef;
    private Props props;

    private BusinessCategory businessCategory;
    private String           categoryId;
    
    private Map conceptWidgetInterfaces;

    private Locales locales;

    private SecurityReference securityReference;

    private Composite propertiesComposite;

    private ConceptInterface conceptInterface;

    private BusinessCategory originalCategory;

	public BusinessCategoryDialog(Shell parent, BusinessCategory businessCategory, Locales locales, SecurityReference securityReference)
	{
		super(parent, SWT.NONE);
        this.originalCategory = businessCategory;
		this.businessCategory = (BusinessCategory) businessCategory.clone();
        this.locales = locales;
        this.securityReference = securityReference;
        
        log=LogWriter.getInstance();
        props=Props.getInstance();
        
        conceptInterface = this.businessCategory.getConcept();

        conceptWidgetInterfaces = new Hashtable();
	}

	public String open()
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
				businessCategory.setChanged();
			}
		};

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("BusinessCategoryDialog.USER_BUSINESS_CATEGORY_PROPERTIES")); //$NON-NLS-1$
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Name line
		wlId=new Label(shell, SWT.RIGHT);
		wlId.setText(Messages.getString("BusinessCategoryDialog.USER_NAME_ID")); //$NON-NLS-1$
 		props.setLook(wlId);
		fdlId=new FormData();
		fdlId.left = new FormAttachment(0, 0);
		fdlId.right= new FormAttachment(middle, -margin);
		fdlId.top  = new FormAttachment(0, margin);
		wlId.setLayoutData(fdlId);
		wId=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wId.setText(""); //$NON-NLS-1$
 		props.setLook(wId);
		wId.addModifyListener(lsMod);
		fdId=new FormData();
		fdId.left = new FormAttachment(middle, 0);
		fdId.top  = new FormAttachment(0, margin);
		fdId.right= new FormAttachment(middle, 350);
		wId.setLayoutData(fdId);

        wOK=new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);
        
        Composite composite = new Composite(shell, SWT.NONE);
        FormLayout compLayout = new FormLayout();
        composite.setLayout(compLayout);
        
        props.setLook(composite);
        
        // Add a property
        Button wAddProperty = new Button(composite, SWT.PUSH);
        props.setLook(wAddProperty);
        wAddProperty.setText(Messages.getString("BusinessCategoryDialog.USER_ADD_PROPERTY")); //$NON-NLS-1$
        wAddProperty.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { addProperty(); } } );
        
        // Delete a property
        Button wDelProperty = new Button(composite, SWT.PUSH);
        props.setLook(wDelProperty);
        wDelProperty.setText(Messages.getString("BusinessCategoryDialog.USER_DELETE_PROPERTY")); //$NON-NLS-1$
        wDelProperty.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { if (ConceptDialog.delChildProperty(shell, conceptInterface)) refreshConceptProperties(); } });
        
        BaseStepDialog.positionBottomButtons(composite, new Button[] { wAddProperty, wDelProperty }, Const.MARGIN, wId);
        
        propertiesComposite = new Composite(composite, SWT.NONE);
        propertiesComposite.setLayout(new FormLayout());
        props.setLook(propertiesComposite);
        
        ConceptDefaultsDialog.getControls(propertiesComposite, businessCategory, Messages.getString("BusinessCategoryDialog.USER_BUSINESS_CATEGORY_PROPERTIES"), conceptInterface, conceptWidgetInterfaces, locales, securityReference); //$NON-NLS-1$
        
        FormData fdRight = new FormData();
        fdRight.top    = new FormAttachment(wAddProperty, 3*Const.MARGIN);
        fdRight.left   = new FormAttachment(props.getMiddlePct()/2, Const.MARGIN);
        fdRight.bottom = new FormAttachment(100, 0);
        fdRight.right  = new FormAttachment(100, 0);
        propertiesComposite.setLayoutData(fdRight);

        FormData fdComposite = new FormData();
        fdComposite.left   = new FormAttachment(0,0);
        fdComposite.right  = new FormAttachment(100,0);
        fdComposite.top    = new FormAttachment(wId, margin);
        fdComposite.bottom = new FormAttachment(wOK, -margin);
        composite.setLayoutData(fdComposite);

		// Add listeners
		lsCancel = new Listener() { public void handleEvent(Event e) { cancel();  } };
		lsOK     = new Listener() { public void handleEvent(Event e) { ok(); } };
		
		wCancel.addListener(SWT.Selection, lsCancel );
		wOK    .addListener(SWT.Selection, lsOK );

		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wId.addSelectionListener(lsDef);		

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
        return categoryId;
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
		if (businessCategory.getId()!=null) wId.setText(businessCategory.getId());
 
		wId.selectAll();
	}
	
	private void cancel()
	{
		categoryId=null;
		dispose();
	}
	
    private void addProperty()
    {
        ConceptPropertyInterface property = NewPropertyDialog.addNewProperty(shell, conceptInterface);
        if (property!=null)
        {
            refreshConceptProperties();
            // Set the focus on the this property
            ConceptPropertyWidgetInterface widgetInterface = (ConceptPropertyWidgetInterface) conceptWidgetInterfaces.get(property.getId());
            if (widgetInterface!=null)
            {
                widgetInterface.setFocus();
            }
        }
    }

    protected void refreshConceptProperties()
    {
        ConceptDefaultsDialog.getControls(propertiesComposite, Messages.getString("BusinessCategoryDialog.USER_BUSINESS_CATEGORY_PROPERTIES"), conceptInterface, conceptWidgetInterfaces, locales, securityReference); //$NON-NLS-1$
        propertiesComposite.layout(true, true);
    }

	private void ok()
	{
		try
        {
            originalCategory.setId(wId.getText());
        }
        catch (ObjectAlreadyExistsException e)
        {
            new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessCategoryDialog.USER_ERROR_BUSINESS_CATEGORY_EXISTS", wId.getText()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
            return;
        }

        // Clear the properties
        originalCategory.getConcept().clearChildProperties();

        // Get the widget values back...
        ConceptDefaultsDialog.setPropertyValues(shell, conceptWidgetInterfaces);

        // Copy these to the business category concept
        originalCategory.getConcept().getChildPropertyInterfaces().putAll(conceptInterface.getChildPropertyInterfaces());

        // The concept stuff: just overwrite it, there are no references from/to these...
        originalCategory.setConcept(businessCategory.getConcept());
        
        categoryId = wId.getText();
		dispose();
	}
}
