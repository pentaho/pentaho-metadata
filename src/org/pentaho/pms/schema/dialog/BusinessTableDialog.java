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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.dialog.ConceptDefaultsDialog;
import org.pentaho.pms.schema.concept.dialog.ConceptDialog;
import org.pentaho.pms.schema.concept.dialog.NewPropertyDialog;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.dialog.EnterSelectionDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.trans.step.BaseStepDialog;

public class BusinessTableDialog extends Dialog
{
	private LogWriter    log;

	private Label        wlName;
	private Text         wName;
	private FormData     fdlName, fdName;

	private Label        wlTable;
	private CCombo       wTable;
	private FormData     fdlTable, fdTable;

	private CTabFolder   wTabfolder;
	private FormData     fdTabfolder;

    private Button wOK, wGet, wAdd, wCancel;
	private Listener lsOK, lsGet, lsAdd, lsCancel;

	private Shell         shell;
	
	private SelectionAdapter lsDef;
    private Props props;

    private BusinessTable businessTable;
    private SchemaMeta    schemaMeta;
    private String        tableName;

    private Map widgetInterfaces;

    private Map detailsWidgetInterfaces;

    private ModifyListener lsMod;

    private SelectionAdapter listSelectionAdapter;

    private String activeLocale;

    private List wList;

    private Composite propertiesComposite;

    private ConceptInterface conceptInterface;

    private BusinessTable originalTable;

    private Text wColId;

    private BusinessColumn previousColumn;

    private Composite detailsComposite;

    private Button wAddProperty;

    private Button wDelProperty;
    
	public BusinessTableDialog(Shell parent, BusinessTable businessTable, SchemaMeta schemaMeta)
	{
		super(parent, SWT.NONE);
		this.originalTable = businessTable;
        this.businessTable = (BusinessTable) businessTable.clone();
		this.schemaMeta = schemaMeta;
        
        log=LogWriter.getInstance();
        props=Props.getInstance();
        
        conceptInterface = this.businessTable.getConcept();
        widgetInterfaces = new Hashtable();
        detailsWidgetInterfaces = new Hashtable();
        
        activeLocale = schemaMeta.getActiveLocale();
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
        
        log.logDebug(this.getClass().getName(), Messages.getString("General.DEBUG_OPENING_DIALOG")); //$NON-NLS-1$

		lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				businessTable.setChanged();
			}
		};

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("BusinessTableDialog.USER_BUSINESS_TABLE_PROPERTIES")); //$NON-NLS-1$
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Name line
		wlName=new Label(shell, SWT.RIGHT);
		wlName.setText(Messages.getString("BusinessTableDialog.USER_NAME_ID")); //$NON-NLS-1$
 		props.setLook(wlName);
		fdlName=new FormData();
		fdlName.left = new FormAttachment(0, 0);
		fdlName.right= new FormAttachment(middle, -margin);
		fdlName.top  = new FormAttachment(0, margin);
		wlName.setLayoutData(fdlName);
		wName=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wName.setText(""); //$NON-NLS-1$
 		props.setLook(wName);
		wName.addModifyListener(lsMod);
		fdName=new FormData();
		fdName.left = new FormAttachment(middle, 0);
		fdName.top  = new FormAttachment(0, margin);
		fdName.right= new FormAttachment(middle, 350);
		wName.setLayoutData(fdName);

        // Table line...
        wlTable=new Label(shell, SWT.RIGHT);
        wlTable.setText(Messages.getString("BusinessTableDialog.USER_PHYSICAL_TABLE")); //$NON-NLS-1$
        props.setLook(wlTable);
        fdlTable=new FormData();
        fdlTable.left = new FormAttachment(0, 0);
        fdlTable.right= new FormAttachment(middle, -margin);
        fdlTable.top  = new FormAttachment(wName, margin*2);
        wlTable.setLayoutData(fdlTable);
        wTable=new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        for (int i=0;i<schemaMeta.nrTables();i++)
        {
            wTable.add( schemaMeta.getTable(i).getId() );
        }
        props.setLook(wTable);
        wTable.addModifyListener(lsMod);
        fdTable=new FormData();
        fdTable.left = new FormAttachment(middle, 0);
        fdTable.right= new FormAttachment(100, 0);
        fdTable.top  = new FormAttachment(wName, margin*2);
        wTable.setLayoutData(fdTable);
        
        // Disable this for now: nobody needs it anyway.
        // wTable.setEnabled(false);
        // wlTable.setEnabled(false);
        
        wOK=new Button(shell, SWT.PUSH);
        wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
        wGet=new Button(shell, SWT.PUSH);
        wGet.setText(Messages.getString("BusinessTableDialog.USER_GET_UNUSED_COLUMNS")); //$NON-NLS-1$
        wAdd=new Button(shell, SWT.PUSH);
        wAdd.setText(Messages.getString("BusinessTableDialog.USER_ADD_COLUMNS")); //$NON-NLS-1$
        wCancel=new Button(shell, SWT.PUSH);
        wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wGet, wAdd, wCancel }, margin, null);

        wTabfolder = new CTabFolder(shell, SWT.BORDER);
        props.setLook(wTabfolder, Props.WIDGET_STYLE_TAB);

        addPropertiesTab();
        addColumnsDetailsTab();
        
		fdTabfolder=new FormData();
		fdTabfolder.left   = new FormAttachment(0, 0);
		fdTabfolder.top    = new FormAttachment(wTable, margin);
		fdTabfolder.right  = new FormAttachment(100, 0);
		fdTabfolder.bottom = new FormAttachment(wOK, -margin);
		wTabfolder.setLayoutData(fdTabfolder);


		// Add listeners
		lsCancel = new Listener() { public void handleEvent(Event e) { cancel();  } };
		lsGet    = new Listener() { public void handleEvent(Event e) { addUnusedColumns(); } };
        lsAdd    = new Listener() { public void handleEvent(Event e) { addColumn(); } };
		lsOK     = new Listener() { public void handleEvent(Event e) { ok(); } };
		
		wCancel.addListener(SWT.Selection, lsCancel );
		wGet   .addListener(SWT.Selection, lsGet );
        wAdd   .addListener(SWT.Selection, lsAdd );
		wOK    .addListener(SWT.Selection, lsOK );

		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wName.addSelectionListener(lsDef);		
		// wTable.addSelectionListener(lsDef);	

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

	
		WindowProperty winprop = props.getScreen(shell.getText());
		if (winprop!=null) winprop.setShell(shell); else shell.pack();
				
		wTabfolder.setSelection(0);
		
		getData();
		
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
        return tableName;
	}
    
    private void addColumnsDetailsTab()
    {
        CTabItem wItemDetails = new CTabItem(wTabfolder, SWT.NONE);
        wItemDetails.setText(Messages.getString("BusinessTableDialog.USER_COLUMN_DETAILS")); //$NON-NLS-1$
        
        detailsComposite = new Composite(wTabfolder, SWT.NONE);
        props.setLook(detailsComposite);
        detailsComposite.setLayout(new FormLayout());
        
        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // List to the left with all the columns
        Label wlList = new Label(detailsComposite, SWT.LEFT);
        wlList.setText(Messages.getString("BusinessTableDialog.USER_SUBJECT")); //$NON-NLS-1$
        props.setLook(wlList);
        FormData fdlList = new FormData();
        fdlList.left = new FormAttachment(0, 0);
        fdlList.right= new FormAttachment(middle/2, 0);
        fdlList.top  = new FormAttachment(0, 0);
        wlList.setLayoutData(fdlList);
        wList = new List(detailsComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        props.setLook(wList);
        FormData fdList = new FormData();
        fdList.left   = new FormAttachment(0, 0);
        fdList.right  = new FormAttachment(middle/2, 0);
        fdList.top    = new FormAttachment(wlList, Const.MARGIN);
        fdList.bottom = new FormAttachment(100, 0);
        wList.setLayoutData(fdList);
        
        // Show the column ID if a column is selected
        //
        Label wlColId = new Label(detailsComposite, SWT.RIGHT);
        wlColId.setText(Messages.getString("BusinessTableDialog.USER_PHYSICAL_COLUMN_ID")); //$NON-NLS-1$
        props.setLook(wlColId);
        FormData fdlColId = new FormData();
        fdlColId.left   = new FormAttachment(middle/2, margin);
        fdlColId.right  = new FormAttachment(middle+middle/3, 0);
        fdlColId.top    = new FormAttachment(wlList, 0);
        wlColId.setLayoutData(fdlColId);
        
        wColId = new Text(detailsComposite, SWT.BORDER);
        props.setLook(wColId);
        FormData fdColId = new FormData();
        fdColId.left   = new FormAttachment(middle+middle/3, margin);
        fdColId.right  = new FormAttachment(100-middle/3, 0);
        fdColId.top    = new FormAttachment(wlList, 0);
        wColId.setLayoutData(fdColId);

        // Allow columns to be added
        Button wAddColumn = new Button(detailsComposite, SWT.PUSH);
        props.setLook(wAddColumn);
        wAddColumn.setText(Messages.getString("BusinessTableDialog.USER_ADD_NEW_COLUMN")); //$NON-NLS-1$
        wAddColumn.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { addColumn(); } });
        
        // Allow columns to be deleted
        Button wDelColumn = new Button(detailsComposite, SWT.PUSH);
        props.setLook(wDelColumn);
        wDelColumn.setText(Messages.getString("BusinessTableDialog.USER_DELETE_COLUMN")); //$NON-NLS-1$
        wDelColumn.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { delColumn(); } });

        // Add a property
        wAddProperty = new Button(detailsComposite, SWT.PUSH);
        props.setLook(wAddProperty);
        wAddProperty.setText(Messages.getString("BusinessTableDialog.USER_ADD_PROPERTY")); //$NON-NLS-1$
        wAddProperty.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { addProperty(); } } );
        
        // Delete a property
        wDelProperty = new Button(detailsComposite, SWT.PUSH);
        props.setLook(wDelProperty);
        wDelProperty.setText(Messages.getString("BusinessTableDialog.USER_DELETE_PROPERTY")); //$NON-NLS-1$
        wDelProperty.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { delProperty(); } });
        
        BaseStepDialog.positionBottomButtons(detailsComposite, new Button[] { wAddColumn, wDelColumn, wAddProperty, wDelProperty }, Const.MARGIN, wColId);
         
        // A composite to the right containing all the properties
        
        propertiesComposite = new Composite(detailsComposite, SWT.NONE);
        propertiesComposite.setLayout(new FormLayout());
        props.setLook(propertiesComposite);
        
        
        FormData fdRight = new FormData();
        fdRight.top    = new FormAttachment(wAddProperty, 3*Const.MARGIN);
        fdRight.left   = new FormAttachment(props.getMiddlePct()/2, Const.MARGIN);
        fdRight.bottom = new FormAttachment(100, 0);
        fdRight.right  = new FormAttachment(100, 0);
        propertiesComposite.setLayoutData(fdRight);

        listSelectionAdapter = new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent event)
                {
                    changeColumn();
                }
            };
            
            
        wList.addSelectionListener(listSelectionAdapter);
        
        FormData fdComposite=new FormData();
        fdComposite.left   = new FormAttachment(0, 0);
        fdComposite.top    = new FormAttachment(0, 0);
        fdComposite.right  = new FormAttachment(100, 0);
        fdComposite.bottom = new FormAttachment(100, 0);
        detailsComposite.setLayoutData(fdComposite);
        
        wItemDetails.setControl(detailsComposite);        
    }

    private void addProperty()
    {
        BusinessColumn column = businessTable.findBusinessColumn(activeLocale, wList.getSelection()[0]);
        if (column!=null)
        {
            ConceptPropertyInterface property = NewPropertyDialog.addNewProperty(shell, column.getConcept());
            if (property!=null)
            {
                refreshConceptProperties(column);
                // Set the focus on the this property
                ConceptPropertyWidgetInterface widgetInterface = (ConceptPropertyWidgetInterface) detailsWidgetInterfaces.get(property.getId());
                if (widgetInterface!=null)
                {
                    widgetInterface.setFocus();
                }
            }
        }
    }
    
    private void delProperty()
    {
        BusinessColumn column = businessTable.findBusinessColumn(activeLocale, wList.getSelection()[0]);
        if (column!=null)
        {
            if (ConceptDialog.delChildProperty(shell, column.getConcept()))
            {
                changeColumn();
                refreshList();
            }
        }
    }

    private void delColumn()
    {
        String[] columnNames = businessTable.getColumnNames(activeLocale);
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, columnNames, Messages.getString("BusinessTableDialog.USER_TITLE_DELETE_COLUMNS"), Messages.getString("BusinessTableDialog.USER_DELETE_COLUMNS")); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.setMulti(true);
        if (dialog.open()!=null)
        {
            int[] idxs = dialog.getSelectionIndeces();

            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
            box.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
            box.setMessage(Messages.getString("BusinessTableDialog.USER_WARNING_CONFIRM_DELETE_COLUMNS", Integer.toString(idxs.length))+ //$NON-NLS-1$
                (idxs.length!=1?Messages.getString("BusinessTableDialog.USER_COLUMNS"):Messages.getString("BusinessTableDialog.USER_COLUMN"))+ //$NON-NLS-1$ //$NON-NLS-2$
                Messages.getString("BusinessTableDialog.USER_CONFIRM_OPERATION_CANNOT_BE_UNDONE")); //$NON-NLS-1$ 
            if (box.open()!=SWT.YES) return;
                
            BusinessColumn columns[] = new BusinessColumn[idxs.length];
            for (int i=0;i<idxs.length;i++)
            {
                columns[i] = businessTable.getBusinessColumn(idxs[i]);
            }
            
            for (int i=0;i<columns.length;i++)
            {
                int idx = businessTable.indexOfBusinessColumn(columns[i]);
                if (idx>=0) businessTable.removeBusinessColumn(idx);
                
                // The original table too.
                idx = originalTable.indexOfBusinessColumn(columns[i]);
                if (idx>=0) originalTable.removeBusinessColumn(idx);
            }
            
            refreshList();
            previousColumn=null;
            changeColumn();
         }
    }

    private void changeColumn()
    {
        if (wList.getSelection().length>0)
        {
            int idx = wList.getSelectionIndex();
            
            while(true)
            {
                try
                {
                    BusinessColumn column = businessTable.findBusinessColumn(activeLocale, wList.getItem(idx));
                    if (column!=null)
                    {
                        // OK, we changed to another column.
                        if (previousColumn!=null && detailsWidgetInterfaces.size()>0) // This ain't the first time we run it.                                
                        {
                            // Set the ID
                            previousColumn.setId( wColId.getText() );
        
                            // Apply all changes for the available widget interfaces (just one normally)
                            //
                            Set set = detailsWidgetInterfaces.keySet();
                            for (Iterator iter = set.iterator(); iter.hasNext();)
                            {
                                String id = (String) iter.next();
                                
                                ConceptPropertyWidgetInterface widgetInterface = (ConceptPropertyWidgetInterface) detailsWidgetInterfaces.get(id);
                                ConceptPropertyInterface property = null;
                                try
                                {
                                    property = widgetInterface.getValue();
                                }
                                catch(Exception e)
                                {
                                    new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_GETTING_PROPERTY_VALUE", id), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                                }
                                if (property!=null)
                                {
                                    ConceptDefaultsDialog.setPropertyValues(shell, detailsWidgetInterfaces);
        
                                    // Everything is applied, clear the properties map.
                                    detailsWidgetInterfaces.clear();
                                    
                                    // Clear the previous concept marker as well
                                    previousColumn = null;
                                    
                                    // Refresh the list because the localized name might have changed!
                                    refreshList();
                                    
                                    // This looses the selection, so select a line again...
                                    wList.select(idx);
                                }
                            }
                        }
                        
                        refreshConceptProperties(column);
                        return;
                    }
                    else
                    {
                        break;
                    }
                }
                catch(ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, Messages.getString("BusinessTableDialog.USER_ERROR_TITLE"), Messages.getString("BusinessTableDialog.USER_ERROR_COLUMN_ID_IN_USE", wColId.getText()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                    // Go back to the previous column
                    // It's obvious that's where the problem was...
                    int prevIndex = businessTable.indexOfBusinessColumn(previousColumn);
                    if (prevIndex>=0) wList.select(prevIndex);
                    break;
                }
            }
        }
    }

    protected void refreshConceptProperties(BusinessColumn column)
    {
        wColId.setText( column.getId() );
        
        String message = Messages.getString("BusinessTableDialog.USER_PROPERTIES_FOR_COLUMN", column.getDisplayName(activeLocale)); //$NON-NLS-1$ 

        ConceptDefaultsDialog.getControls(propertiesComposite, column, message, column.getConcept(), detailsWidgetInterfaces, schemaMeta.getLocales(), schemaMeta.getSecurityReference());
        previousColumn = column;
        
        // detailsComposite.layout(true, true);
        detailsComposite.layout();
        detailsComposite.getParent().layout(true, true);
        enableFields();
    }

    private void refreshList()
    {
        wList.removeAll();
        wList.setItems(businessTable.getColumnNames(activeLocale));
        enableFields();
    }

    /**
     * Add a tab for all the properties of the physical table...
     */
    private void addPropertiesTab()
    {        
        CTabItem wItemProps = new CTabItem(wTabfolder, SWT.NONE);
        wItemProps.setText(Messages.getString("BusinessTableDialog.USER_TABLE_PROPERTIES")); //$NON-NLS-1$

        Composite composite = new Composite(wTabfolder, SWT.NONE);
        props.setLook(composite);
        composite.setLayout(new FormLayout());
        
        ConceptDefaultsDialog.getControls(composite, businessTable, Messages.getString("BusinessTableDialog.USER_BUSINESS_TABLE_NAME", businessTable.getDisplayName(activeLocale)), null, null, conceptInterface, widgetInterfaces, schemaMeta.getLocales(), schemaMeta.getSecurityReference()); //$NON-NLS-1$ 

        FormData fdProperties=new FormData();
        fdProperties.left   = new FormAttachment(0, 0);
        fdProperties.top    = new FormAttachment(0, 0);
        fdProperties.right  = new FormAttachment(100, 0);
        fdProperties.bottom = new FormAttachment(100, 0);
        composite.setLayoutData(fdProperties);
        
        wItemProps.setControl(composite);
    }

    private void enableFields()
    {
        // Only enable the buttons if a column is selected in the list...
        boolean enableButton = wList.getSelectionCount()==1;
        wAddProperty.setEnabled(enableButton);
        wDelProperty.setEnabled(enableButton);
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
		if (businessTable.getId()!=null) wName.setText(businessTable.getId());	
		if (businessTable.getPhysicalTable()!=null) wTable.setText(businessTable.getPhysicalTable().getId());  // --> Readonly at the moment	
        
        refreshList();
        
		wName.selectAll();
	}


    private void cancel()
	{
		tableName=null;
		dispose();
	}
	
	private void ok()
	{
        try
        {
            originalTable.setId(wName.getText());
        }
        catch (ObjectAlreadyExistsException e)
        {
            new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_TABLE_ID_IN_USE", wName.getText()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
            return;
        }
        
        PhysicalTable physicalTable = schemaMeta.findPhysicalTable(wTable.getText());
        originalTable.setPhysicalTable( physicalTable );
        
        // Clear the child options from the business table concept...
        originalTable.getConcept().clearChildProperties();
        
        // Get the table values back...
        ConceptDefaultsDialog.setPropertyValues(shell, widgetInterfaces);

        // Get the unchanged column property values back...
        ConceptDefaultsDialog.setPropertyValues(shell, detailsWidgetInterfaces);

        // Same for the id of the last changed column
        if (previousColumn!=null) 
        {
            try
            {
                previousColumn.setId( wColId.getText() ); // Set the ID
            }
            catch (ObjectAlreadyExistsException e)
            {
                new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_COLUMN_ID_IN_USE", wColId.getText()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                return;
            } 
        }

        // Copy these to the business table concept
        originalTable.getConcept().getChildPropertyInterfaces().putAll(conceptInterface.getChildPropertyInterfaces());
        
        
        // Now that we have all the properties and settings, copy these over to the original business table...
        // 
        // The concept stuff: just overwrite it, there are no references from/to these...
        originalTable.setConcept(businessTable.getConcept());
        
        // The columns: we do have references to these: only updates are possible at the moment, no deletes or inserts
        //
        // Nothing can be deleted or re-ordered in the GUI so we can just do a 1-on-1 copy
        //
        for (int i=0;i<businessTable.nrBusinessColumns();i++)
        {
            BusinessColumn businessColumn = businessTable.getBusinessColumn(i);
            
            if (i<originalTable.nrBusinessColumns())
            {
                BusinessColumn originalColumn = originalTable.getBusinessColumn(i);
                
                originalColumn.setConcept(businessColumn.getConcept());
            }
            else
            {
                try
                {
                    originalTable.addBusinessColumn(businessColumn);
                }
                catch (ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_BUSINESS_COLUMN_ID_IN_USE", businessColumn.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                    return;
                }
            }

            // Change the ID too...
            try
            {
                originalTable.getBusinessColumn(i).setId( businessColumn.getId() );
            }
            catch (ObjectAlreadyExistsException e)
            {
                new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_BUSINESS_COLUMN_ID_IN_USE", businessColumn.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                return;
            }
        }
        
		tableName = wName.getText();
		dispose();
	}
	
    public void addColumn()
    {
        PhysicalTable physicalTable = schemaMeta.findPhysicalTable(wTable.getText());
        if (physicalTable!=null)
        {
            String[] columns = physicalTable.getColumnNames(activeLocale);
            EnterSelectionDialog dialog = new EnterSelectionDialog(shell, columns, Messages.getString("BusinessTableDialog.USER_TITLE_SELECT_COLUMN"), Messages.getString("BusinessTableDialog.USER_SELECT_COLUMN")); //$NON-NLS-1$ //$NON-NLS-2$
            String name = dialog.open();
            if (name!=null)
            {
                PhysicalColumn physicalColumn = physicalTable.findPhysicalColumn(activeLocale, name);
                String id = BusinessColumn.proposeId(activeLocale, businessTable, physicalColumn);
                BusinessColumn businessColumn = new BusinessColumn(id, physicalColumn, businessTable);
                try
                {
                    businessTable.addBusinessColumn(businessColumn);
                }
                catch (ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_ID_EXISTS_NOT_ADDED", businessColumn.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                }
            }
            
            refreshList();
            wList.select(physicalTable.nrPhysicalColumns()-1);
            changeColumn();
        }
    }
    
	private void addUnusedColumns()
	{
        PhysicalTable physicalTable = schemaMeta.findPhysicalTable(wTable.getText());
		if (physicalTable!=null)
		{
            businessTable.setPhysicalTable(physicalTable);

            if (!Const.isEmpty(wName.getText())) 
            {
                try
                {
                    businessTable.setId(wName.getText());
                }
                catch (ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_BUSINESS_TABLE_ID_EXISTS", wName.getText()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                    return;
                }
            }
            
            // The pysical column IDs?
            String used[] = physicalTable.getColumnIDs();
            
			for (int i=0;i<physicalTable.nrPhysicalColumns();i++)
			{
                PhysicalColumn column = physicalTable.getPhysicalColumn(i);
                if (Const.indexOfString(column.getId(), used)<0)
                {
                    String newId = BusinessColumn.proposeId(schemaMeta.getActiveLocale(), businessTable, column);
                    BusinessColumn businessColumn = new BusinessColumn(newId, column, businessTable);
                    
                    try
                    {
                        businessTable.addBusinessColumn(businessColumn);
                    }
                    catch (ObjectAlreadyExistsException e)
                    {
                        new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_ID_EXISTS_NOT_ADDED", businessColumn.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                    }
                }
			}
            
            refreshList();
		}
        else
		{
			MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
			mb.setMessage(Messages.getString("BusinessTableDialog.USER_ERROR_CANT_FIND_PHYSICAL_TABLE")); //$NON-NLS-1$
			mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
			mb.open();
		}
	}
}
