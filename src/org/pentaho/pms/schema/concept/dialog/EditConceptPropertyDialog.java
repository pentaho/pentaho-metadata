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
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.trans.step.BaseStepDialog;


/***
 * Represents a concept property
 * 
 * @since 12-okt-2006
 *
 */
public class EditConceptPropertyDialog extends Dialog
{
	private LogWriter    log;

	private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private Shell         shell;
	
    private Props props;

    private ConceptInterface concept;
    private ConceptPropertyInterface property;

    private ConceptPropertyType type;

    private Map conceptPropertyInterfaces;

    private Locales locales;

    private SecurityReference securityReference;

	public EditConceptPropertyDialog(Shell parent, ConceptInterface concept, ConceptPropertyInterface property, Locales locales, SecurityReference securityReference)
	{
		super(parent, SWT.NONE);
        this.concept = concept;
        this.property = property;
        this.locales = locales;
        this.securityReference = securityReference;

        log=LogWriter.getInstance();
        props=Props.getInstance();
        
        conceptPropertyInterfaces = new Hashtable();
        type = property.getType();
	}

	public ConceptPropertyInterface open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
        
        log.logDebug(this.getClass().getName(), Messages.getString("General.DEBUG_OPENING_DIALOG")); //$NON-NLS-1$

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("EditConceptPropertyDialog.USER_CONCEPT_PROPERY_EDITOR", type.getDescription())); //$NON-NLS-1$ 
		
        wOK=new Button(shell, SWT.PUSH);
        wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
        wCancel=new Button(shell, SWT.PUSH);
        wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, Const.MARGIN, null);
        
        ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.H_SCROLL );
        scrolledComposite.setLayout(new FormLayout());
        
        Composite composite = new Composite(scrolledComposite, SWT.NONE);
        props.setLook(composite);
        FormLayout compLayout = new FormLayout();
        composite.setLayout(compLayout);
        
        String description = Messages.getString("EditConceptPropertyDialog.USER_ENTER_PROPERTY_VALUE", property.getId()); //$NON-NLS-1$ 
        ConceptDefaultsDialog.addControl(composite, concept, description, property, null, conceptPropertyInterfaces, locales, securityReference);

        composite.layout(true, true);
        composite.pack();

        scrolledComposite.setContent(composite);
        
        scrolledComposite.pack();
        
        Rectangle bounds = composite.getBounds();
        
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setMinWidth(bounds.width);
        scrolledComposite.setMinHeight(bounds.height);
        
        FormData fdComposite =new FormData();
        fdComposite.left   = new FormAttachment(0, 0);
        fdComposite.right  = new FormAttachment(100, 0);
        fdComposite.top    = new FormAttachment(0, 0);
        fdComposite.bottom = new FormAttachment(100, 0);
        composite.setLayoutData(fdComposite);
        
        FormData fdScrolled =new FormData();
        fdScrolled.left   = new FormAttachment(0, 0);
        fdScrolled.right  = new FormAttachment(100, 0);
        fdScrolled.top    = new FormAttachment(0, 0);
        fdScrolled.bottom = new FormAttachment(wOK, -Const.MARGIN);
        scrolledComposite.setLayoutData(fdScrolled);
		
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
        return property;
	}

	public void dispose()
	{
		props.setScreen(new WindowProperty(shell));
		shell.dispose();
	}
	
	/**
	 * Copy information from the input to the dialog fields.
	 */ 
	public void getData()
	{
        // Already done by the getControl() method
	}
	
	private void cancel()
	{
        property=null;
		dispose();
	}
	
	private void ok()
	{
        if (ConceptDefaultsDialog.setPropertyValues(shell, conceptPropertyInterfaces))
        {
            dispose();
        }
	}
}
