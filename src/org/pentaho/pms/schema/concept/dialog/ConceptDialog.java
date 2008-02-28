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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;


public class ConceptDialog extends Dialog
{
	private LogWriter    log;

    private Label        wlId;
    private Text         wId;
    private FormData     fdlId, fdId;

	private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private Shell         shell;
	
    private PropsUI props;

    private String title;
    private String message;
    private ConceptInterface originalInterface;
    private SchemaMeta schemaMeta;
    private ConceptInterface conceptInterface;

    private Map<String,ConceptPropertyWidgetInterface> conceptWidgetInterfaces;

    private Composite propertiesComposite;

    private String id;

    private ConceptUtilityInterface utilityInterface;

	public ConceptDialog(Shell parent, String title, String message, ConceptUtilityInterface utilityInterface, SchemaMeta schemaMeta)
	{
		super(parent, SWT.NONE);
        this.id = utilityInterface.getId();
        this.utilityInterface = utilityInterface;
		this.originalInterface = utilityInterface.getConcept();
        this.conceptInterface = (ConceptInterface) utilityInterface.getConcept().clone();
        this.title = title;
        this.message = message;
        this.schemaMeta = schemaMeta;

        log=LogWriter.getInstance();
        props=PropsUI.getInstance();

        conceptWidgetInterfaces = new Hashtable<String,ConceptPropertyWidgetInterface>();
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
        
        log.logDebug(this.getClass().getName(), Messages.getString("General.DEBUG_OPENING_DIALOG")); //$NON-NLS-1$

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // put the buttons below to get a base-line to work from...
        //
        wOK=new Button(shell, SWT.PUSH);
        wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
        wCancel=new Button(shell, SWT.PUSH);
        wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, Const.MARGIN, null);

        // Ask for an ID for this model element
        //
        wlId=new Label(shell, SWT.RIGHT);
        wlId.setText(Messages.getString("ConceptDialog.USER_MODEL_ELEMENT_ID")); //$NON-NLS-1$
        props.setLook(wlId);
        fdlId=new FormData();
        fdlId.left = new FormAttachment(0, 0);
        fdlId.right= new FormAttachment(middle, 0);
        fdlId.top  = new FormAttachment(0, 0);
        wlId.setLayoutData(fdlId);
       
        wId=new Text(shell, SWT.LEFT | SWT.BORDER);
        props.setLook(wId);
        wId.setText(id);
        fdId=new FormData();
        fdId.left = new FormAttachment(middle, margin);
        fdId.right= new FormAttachment(100, 0);
        fdId.top  = new FormAttachment(0, 0);
        wId.setLayoutData(fdId);


        // The group takes the rest of the screen...
        //
        Group wGroup = new Group(shell, SWT.NONE);
        props.setLook(wGroup);
        wGroup.setText(message);
        wGroup.setToolTipText(message);
        FormData fdGroup = new FormData();
        fdGroup.left   = new FormAttachment(  0, 0);
        fdGroup.right  = new FormAttachment(100, 0);
        fdGroup.top    = new FormAttachment(wId, 3*margin);
        fdGroup.bottom = new FormAttachment(wOK, -margin);
        wGroup.setLayoutData(fdGroup);
        
        FormLayout groupLayout = new FormLayout();
        groupLayout.marginHeight = Const.FORM_MARGIN;
        groupLayout.marginWidth  = Const.FORM_MARGIN;
        wGroup.setLayout(groupLayout);
        
        // Add a property
        Button wAddProperty = new Button(wGroup, SWT.PUSH);
        props.setLook(wAddProperty);
        wAddProperty.setText(Messages.getString("ConceptDialog.USER_ADD_PROPERTY")); //$NON-NLS-1$
        wAddProperty.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { addProperty(); } } );
        
        // Delete a property
        Button wDelProperty = new Button(wGroup, SWT.PUSH);
        props.setLook(wDelProperty);
        wDelProperty.setText(Messages.getString("ConceptDialog.USER_DELETE_PROPERTY")); //$NON-NLS-1$
        wDelProperty.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { if (delChildProperty(shell, conceptInterface)) refreshConceptProperties(); }});

        BaseStepDialog.positionBottomButtons(wGroup, new Button[] { wAddProperty, wDelProperty }, Const.MARGIN, wId);
        
		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(title);
		
        
        propertiesComposite = new Composite(wGroup, SWT.NONE);
        propertiesComposite.setLayout(new FormLayout());
        
        FormData fdComposite=new FormData();
		fdComposite.left   = new FormAttachment(0, 0);
		fdComposite.top    = new FormAttachment(wId, margin*10);
		fdComposite.right  = new FormAttachment(100, 0);
		fdComposite.bottom = new FormAttachment(100, 0);
		propertiesComposite.setLayoutData(fdComposite);
		
		// Add listeners
		lsCancel = new Listener() { public void handleEvent(Event e) { cancel();  } };
		lsOK     = new Listener() { public void handleEvent(Event e) { ok(); } };
		
		wCancel.addListener(SWT.Selection, lsCancel );
		wOK    .addListener(SWT.Selection, lsOK );

		// lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );
	
		WindowProperty winprop = props.getScreen(shell.getText());
		if (winprop!=null) winprop.setShell(shell); else shell.pack();
				
        refreshConceptProperties();
        
        shell.layout();
        
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
        return id;
	}

    public static final boolean delChildProperty(Shell shell, ConceptInterface conceptInterface)
    {
       String[] allIDs = conceptInterface.getChildPropertyIDs();
       String[] choices = new String[allIDs.length];
       for (int i=0;i<choices.length;i++)
       {
           ConceptPropertyInterface property = conceptInterface.getChildProperty(allIDs[i]);
           choices[i] = allIDs[i];
           // If it's a default ID, add a description
           DefaultPropertyID defaultId = DefaultPropertyID.findDefaultPropertyID(allIDs[i]);
           if (defaultId!=null)
           {
               choices[i]+=" : "+defaultId.getDescription(); //$NON-NLS-1$
           }
           
           choices[i]+=" ("+property.getType().getDescription()+")"; //$NON-NLS-1$ //$NON-NLS-2$
       }
       
       // Display this list of choices...
       EnterSelectionDialog selectionDialog = new EnterSelectionDialog(shell, choices, Messages.getString("ConceptDialog.USER_DELETE_PROPERTY"), Messages.getString("ConceptDialog.USER_SELECT_PROPERTIES_TO_DELETE")); //$NON-NLS-1$ //$NON-NLS-2$
       selectionDialog.setMulti(true);
       if (selectionDialog.open()!=null)
       {
           int[] idxs = selectionDialog.getSelectionIndeces();
           for (int i=0;i<idxs.length;i++)
           {
               String id = allIDs[idxs[i]];
               ConceptPropertyInterface property = conceptInterface.getChildProperty(id);
               conceptInterface.removeChildProperty(property);
           }
           return true; // do refresh after this.
       }
       return false;
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

    private void refreshConceptProperties()
    {
        ConceptDefaultsDialog.getControls(propertiesComposite, message, conceptInterface, conceptWidgetInterfaces, schemaMeta.getLocales(), schemaMeta.getSecurityReference());
        propertiesComposite.layout(true, true);
    }
    
    public static final SelectionAdapter createValueSelectionAdapter(final Shell shell, final ConceptInterface concept, final DialogGetDataInterface getDataInterface, final Locales locales, final SecurityReference securityReference)
    {
        return new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                try
                {
                    TableView tableView = (TableView)e.widget;
                    TableItem tableItem = tableView.table.getItem(e.y);
                    if (tableItem!=null)
                    {
                        String id = tableItem.getText(1);
                        if (!Const.isEmpty(id))
                        {
                            ConceptPropertyInterface property = concept.getChildProperty(id);
                            
                            String typeDesc = tableItem.getText(2);
                            ConceptPropertyType type = ConceptPropertyType.getType(typeDesc);

                            if (property==null)
                            {
                                property = DefaultPropertyID.getDefaultEmptyProperty(type, id);
                                concept.addProperty(property);
                            }
                            EditConceptPropertyDialog dialog = new EditConceptPropertyDialog(shell, concept, property, locales, securityReference);
                            if (dialog.open()!=null)
                            {
                                getDataInterface.refreshScreen();
                            }
                        }
                    }
                }
                catch(Exception exception)
                {
                    new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("ConceptDialog.USER_ERROR_SETTING_PROPERTY"), exception); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        };
    }

    public void dispose()
	{
        try
        {
            props.setScreen(new WindowProperty(shell));
        }
        catch(Exception e)
        {
            
        }
		shell.dispose();
	}
	
    private void cancel()
	{
        id=null;
		dispose();
	}
	
	private void ok()
	{
        // First clear the concepts...
        originalInterface.clearChildProperties();
        
        // Capture all the changes for the cloned concept
        ConceptDefaultsDialog.setPropertyValues(shell, conceptWidgetInterfaces);
        
        // Then put all our concepts into it.
        originalInterface.getChildPropertyInterfaces().putAll(conceptInterface.getChildPropertyInterfaces());
        
        // Is the ID name Set?
        
        if ( !Const.isEmpty(wId.getText()) )
        {
            id = wId.getText();
            try
            {
                utilityInterface.setId(id);
            }
            catch (ObjectAlreadyExistsException e)
            {
                new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("ConceptDialog.USER_ERROR_PROPERTY_ID_EXISTS", id), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                return;
            }
        }
        
        originalInterface.setChanged();
        
        dispose();
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
