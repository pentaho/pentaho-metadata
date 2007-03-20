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
 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/

 
/*
 * Created on 19-jun-2003
 *
 */

package org.pentaho.pms.schema.dialog;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.WhereCondition;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.dialog.ConceptDefaultsDialog;
import org.pentaho.pms.schema.concept.dialog.ConceptDialog;
import org.pentaho.pms.schema.concept.dialog.NewPropertyDialog;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.dialog.DatabaseExplorerDialog;
import be.ibridge.kettle.core.dialog.EnterSelectionDialog;
import be.ibridge.kettle.core.dialog.EnterStringDialog;
import be.ibridge.kettle.core.dialog.EnterTextDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.trans.step.BaseStepDialog;


public class PhysicalTableDialog extends Dialog
{
	private Label        wlId;
	private Text         wId;
	private FormData     fdlId, fdId;

	private CTabFolder   wTabfolder;
	private FormData     fdTabfolder;

	private TableView    wConditions;
	private FormData     fdConditions;

	private Button wOK, wTarget, wCancel;
	private Listener lsOK, lsTarget, lsCancel;

	private Shell         shell;
	private PhysicalTable     physicalTable;
	private String        tablename;
	
	private SelectionAdapter lsDef;
    private Props props;


    private PhysicalColumn previousColumn;
    
    private Map widgetInterfaces;

    private ModifyListener lsMod;

    private DatabaseMeta databaseMeta;
    private Map detailsWidgetInterfaces;
    private SelectionAdapter listSelectionAdapter;
    private Composite propertiesComposite;
    private Locales locales;
    private ConceptInterface conceptInterface;
    private List wList;
    private PhysicalTable originalTable;
    private String activeLocale;
    private Text wColId;
    private Composite detailsComposite;
    private SecurityReference securityReference;
    private Button wAddColumn;
    private Button wDelColumn;
    private Button wAddProperty;
    private Button wDelProperty;
	
	public PhysicalTableDialog(Shell parent, int style, PhysicalTable physicalTable, Locales locales, SecurityReference securityReference)
	{
		super(parent, style);
		this.originalTable=physicalTable;
        this.physicalTable = (PhysicalTable) physicalTable.clone();
        this.databaseMeta = physicalTable.getDatabaseMeta();
        this.locales = locales;
        this.securityReference = securityReference;
        
        props=Props.getInstance();
        widgetInterfaces = new Hashtable();
        
        detailsWidgetInterfaces = new Hashtable();
        
        conceptInterface = (ConceptInterface)physicalTable.getConcept().clone();
        
        activeLocale = locales.getActiveLocale();
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);

		lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				physicalTable.setChanged();
			}
		};

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText("Physical Table properties");
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Name line
		wlId=new Label(shell, SWT.RIGHT);
		wlId.setText("Name/ID ");
 		props.setLook(wlId);
		fdlId=new FormData();
		fdlId.left = new FormAttachment(0, 0);
		fdlId.right= new FormAttachment(middle, -margin);
		fdlId.top  = new FormAttachment(0, margin);
		wlId.setLayoutData(fdlId);
		wId=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wId.setText("");
 		props.setLook(wId);
		wId.addModifyListener(lsMod);
		fdId=new FormData();
		fdId.left = new FormAttachment(middle, 0);
		fdId.top  = new FormAttachment(0, margin);
		fdId.right= new FormAttachment(middle, 350);
		wId.setLayoutData(fdId);

        wTabfolder = new CTabFolder(shell, SWT.BORDER);
		props.setLook(wTabfolder, Props.WIDGET_STYLE_TAB);

        addPropertiesTab();
        addColumnsDetailsTab();
        addConditionsTab();
        
		fdTabfolder=new FormData();
		fdTabfolder.left   = new FormAttachment(0, 0);
		fdTabfolder.top    = new FormAttachment(wId, margin);
		fdTabfolder.right  = new FormAttachment(100, 0);
		fdTabfolder.bottom = new FormAttachment(100, -50);
		wTabfolder.setLayoutData(fdTabfolder);

		
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(" &OK ");
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(" &Cancel ");
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

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
				
		wTabfolder.setSelection(0);
		
		getData();
		
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return tablename;
	}

    private void addConditionsTab()
    {
        CTabItem wItemConditions = new CTabItem(wTabfolder, SWT.NONE);
        wItemConditions.setText("Conditions");

        final int ConditionsCols=5;
        final int ConditionsRows=0;
        
        ColumnInfo[] colinf=new ColumnInfo[ConditionsCols];
        colinf[0]=new ColumnInfo("Name",        ColumnInfo.COLUMN_TYPE_TEXT, false, false);
        colinf[1]=new ColumnInfo("Fieldname",   ColumnInfo.COLUMN_TYPE_TEXT, false, false);
        colinf[2]=new ColumnInfo("Comparator",  ColumnInfo.COLUMN_TYPE_CCOMBO, WhereCondition.comparators );
        colinf[3]=new ColumnInfo("DB Formula",  ColumnInfo.COLUMN_TYPE_TEXT, false, false);
        colinf[4]=new ColumnInfo("Description", ColumnInfo.COLUMN_TYPE_BUTTON, "", "...");
        
        wConditions=new TableView(wTabfolder, 
                              SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                              colinf, 
                              ConditionsRows,  
                              false, // read-only
                              lsMod,
                              props
                              );

        SelectionAdapter selCondition = new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                String str = wConditions.getButtonString();
                EnterTextDialog etd = new EnterTextDialog(shell, "Button", "enter text", str);
                etd.setModal();
                String res = etd.open();
                if (res!=null)
                {
                    wConditions.setButtonString(res);
                    wConditions.closeActiveButton();
                }
            }
        }
        ;

        colinf[2].setSelectionAdapter(selCondition);
        colinf[2].setToolTip("Click on this button to edit the description...");

        fdConditions=new FormData();
        fdConditions.left   = new FormAttachment(0, 0);
        fdConditions.top    = new FormAttachment(0, 0);
        fdConditions.right  = new FormAttachment(100, 0);
        fdConditions.bottom = new FormAttachment(100, 0);
        wConditions.setLayoutData(fdConditions);
        
        wItemConditions.setControl(wConditions);
    }

    /**
     * Add a tab for all the properties of the physical table...
     */
	private void addPropertiesTab()
    {        
        CTabItem wItemProps = new CTabItem(wTabfolder, SWT.NONE);
        wItemProps.setText("Table properties");

        Composite composite = new Composite(wTabfolder, SWT.NONE);
        props.setLook(composite);
        composite.setLayout(new FormLayout());
        
        wTarget=new Button(composite, SWT.PUSH);
        wTarget.setText(" &Find target table ");
        lsTarget = new Listener() { public void handleEvent(Event e) { getTableName(); } };
        wTarget.addListener(SWT.Selection, lsTarget);

        ConceptDefaultsDialog.getControls(composite, physicalTable, "Physical table '"+physicalTable.getDisplayName(activeLocale)+"'", conceptInterface, widgetInterfaces, locales, securityReference);

        BaseStepDialog.positionBottomButtons(composite, new Button[] { wTarget }, Const.MARGIN, null);

        FormData fdProperties=new FormData();
        fdProperties.left   = new FormAttachment(0, 0);
        fdProperties.top    = new FormAttachment(0, 0);
        fdProperties.right  = new FormAttachment(100, 0);
        fdProperties.bottom = new FormAttachment(wTarget, -Const.MARGIN);
        composite.setLayoutData(fdProperties);
        
        wItemProps.setControl(composite);
    }
    
    private void addColumnsDetailsTab()
    {
        CTabItem wItemDetails = new CTabItem(wTabfolder, SWT.NONE);
        wItemDetails.setText("Columns details");
        
        detailsComposite = new Composite(wTabfolder, SWT.NONE);
        props.setLook(detailsComposite);
        detailsComposite.setLayout(new FormLayout());

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;
        
        // List to the left with all the columns
        Label wlList = new Label(detailsComposite, SWT.LEFT);
        wlList.setText("Subject");
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
        fdList.top    = new FormAttachment(wlList, margin);
        fdList.bottom = new FormAttachment(100, 0);
        wList.setLayoutData(fdList);

        // Show the column ID if a column is selected
        //
        Label wlColId = new Label(detailsComposite, SWT.RIGHT);
        wlColId.setText("Physical Column ID ");
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

        // Allow buttons to be added
        wAddColumn = new Button(detailsComposite, SWT.PUSH);
        props.setLook(wAddColumn);
        wAddColumn.setText("Add new column");
        wAddColumn.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { addColumn(); } });
        
        // Allow buttons to be deleted
        wDelColumn = new Button(detailsComposite, SWT.PUSH);
        props.setLook(wDelColumn);
        wDelColumn.setText("Delete column");
        wDelColumn.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { delColumn(); } });
        
        // Allow buttons to be added
        wAddProperty = new Button(detailsComposite, SWT.PUSH);
        props.setLook(wAddProperty);
        wAddProperty.setText("Add property");
        wAddProperty.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { addProperty(); } });
        
        wDelProperty = new Button(detailsComposite, SWT.PUSH);
        props.setLook(wDelProperty);
        wDelProperty.setText("Delete property");
        wDelProperty.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent arg0) { delProperty(); } });
        
        BaseStepDialog.positionBottomButtons(detailsComposite, new Button[] { wAddColumn, wDelColumn, wAddProperty, wDelProperty }, margin, wColId);
        
        // A composite to the right containing all the properties
        
        propertiesComposite = new Composite(detailsComposite, SWT.NONE);
        propertiesComposite.setLayout(new FormLayout());
        props.setLook(propertiesComposite);
        

        FormData fdRight = new FormData();
        fdRight.top    = new FormAttachment(wAddProperty, Const.MARGIN);
        fdRight.left   = new FormAttachment(props.getMiddlePct()/2, Const.MARGIN);
        fdRight.bottom = new FormAttachment(100, 0);
        fdRight.right  = new FormAttachment(100, 0);
        propertiesComposite.setLayoutData(fdRight);
        
        listSelectionAdapter = new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent event)
                {
                    try
                    {
                        changeColumn();
                    }
                    catch (ObjectAlreadyExistsException e)
                    {
                        new ErrorDialog(shell, "Error", "There was an error applying column changes (duplicate column ID)", e);
                    }
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

    private void addColumn()
    {
        // Ask for the ID
        String startID = Settings.getPhysicalColumnIDPrefix();
        if (Settings.isAnIdUppercase()) startID = startID.toUpperCase();
        
        while (true)
        {
            EnterStringDialog dialog = new EnterStringDialog(shell, startID, "New column", "Enter the name of the new physical column" );
            String id = dialog.open();
            if (id!=null)
            {
                PhysicalColumn physicalColumn = new PhysicalColumn(id);
                physicalColumn.setTable(physicalTable);
                
                String name = id;
                if (name.startsWith(startID)) // PC_
                {
                    name = name.substring(startID.length());
                    name = Const.fromID(name);
                }
                physicalColumn.setName(activeLocale, name);
                
                try
                {
                    physicalTable.addPhysicalColumn(physicalColumn);
                    refreshList();
                    wList.select(physicalTable.nrPhysicalColumns()-1);
                    changeColumn();
                }
                catch (ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, "Error", "There was an error while trying to change a column:", e);
                }
            }
        }
    }
    
    private void delColumn()
    {
        String[] columnNames = physicalTable.getColumnNames(activeLocale);
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, columnNames, "Delete columns", "Select the columns to delete");
        dialog.setMulti(true);
        if (dialog.open()!=null)
        {
            int[] idxs = dialog.getSelectionIndeces();

            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
            box.setText("Warning!");
            box.setMessage("Warning, you are about to delete "+idxs.length+" "+(idxs.length!=1?"columns":"column")+".\nThis operation can not be undone!\n\nAre you sure you want to continue?");
            if (box.open()!=SWT.YES) return;
                
            PhysicalColumn columns[] = new PhysicalColumn[idxs.length];
            for (int i=0;i<idxs.length;i++)
            {
                columns[i] = physicalTable.getPhysicalColumn(idxs[i]);
            }
            
            for (int i=0;i<columns.length;i++)
            {
                int idx = physicalTable.indexOfPhysicalColumn(columns[i]);
                if (idx>=0) physicalTable.removePhysicalColumn(idx);
                
                // The original table too.
                idx = originalTable.indexOfPhysicalColumn(columns[i]);
                if (idx>=0) originalTable.removePhysicalColumn(idx);
            }
            
            refreshList();
            previousColumn=null;
            try
            {
                changeColumn();
            }
            catch (ObjectAlreadyExistsException e)
            {
                // delete should be ok
            }
         }
    }

    private void addProperty()
    {
        PhysicalColumn column = physicalTable.findPhysicalColumn(activeLocale, wList.getSelection()[0]);
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
        PhysicalColumn column = physicalTable.findPhysicalColumn(activeLocale, wList.getSelection()[0]);
        if (column!=null)
        {
            if (ConceptDialog.delChildProperty(shell, column.getConcept()))
            {
                try
                {
                    changeColumn();
                }
                catch (ObjectAlreadyExistsException e)
                {
                    // should be ok
                }
                refreshList();
            }
        }
    }


    private void changeColumn() throws ObjectAlreadyExistsException
    {
        if (wList.getSelection().length>0)
        {
            int idx = wList.getSelectionIndex();
            
            PhysicalColumn column = physicalTable.findPhysicalColumn(activeLocale, wList.getItem(idx));
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
                            new ErrorDialog(shell, "Error", "Error getting value for property '"+id+"'", e);
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
            }
            else
            {
                clearPropertiesComposite();
            }
        }
        else
        {
            clearPropertiesComposite();
        }

    }

    private void clearPropertiesComposite()
    {
        // remove controls from the properties composite.
        Control[] children = propertiesComposite.getChildren();
        for (int i=0;i<children.length;i++) children[i].dispose();
        detailsComposite.layout(true, true);
        
        // clear the id too
        wColId.setText("");
    }

    private void refreshList()
    {
        wList.removeAll();
        wList.setItems(physicalTable.getColumnNames(activeLocale));
        enableFields();
    }

    protected void refreshConceptProperties(PhysicalColumn column)
    {
        wColId.setText( column.getId() );
        
        String message = "Properties for column '"+column.getDisplayName(locales.getActiveLocale())+"'";

        ConceptDefaultsDialog.getControls(propertiesComposite, column, message, column.getConcept(), detailsWidgetInterfaces, locales, securityReference);
        previousColumn = column;
        
        detailsComposite.layout(true, true);
        
        enableFields();
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
		if (physicalTable.getId()!=null) wId.setText(physicalTable.getId());	
        refreshList();
		wId.selectAll();
	}
	
	private void cancel()
	{
		tablename=null;
		dispose();
	}
	
	private void ok()
	{
		try
        {
            physicalTable.setId(wId.getText());
        }
        catch (ObjectAlreadyExistsException e)
        {
            new ErrorDialog(shell, "Error", "A physical table with the same id '"+wId.getText()+"' already exists.", e);
            return;
        }
		
        // Clear the child options from the physical table concept...
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
                previousColumn.setId( wColId.getText() );
            }
            catch (ObjectAlreadyExistsException e)
            {
                new ErrorDialog(shell, "Error", "A column with the same id '"+wColId.getText()+"' already exists.", e);
            }
        }

        // Copy these to the physical table concept
        originalTable.getConcept().getChildPropertyInterfaces().putAll(conceptInterface.getChildPropertyInterfaces());
        
        
        // Now that we have all the properties and settings, copy these over to the original business table...
        // 
        // The concept stuff: just overwrite it, there are no references from/to these...
        originalTable.setConcept(physicalTable.getConcept());
        
        // The columns: we do have references to these: only updates are possible at the moment, no deletes or inserts
        //
        // Nothing can be deleted or re-ordered in the GUI so we can just do a 1-on-1 copy
        //
        for (int i=0;i<physicalTable.nrPhysicalColumns();i++)
        {
            PhysicalColumn physicalColumn = physicalTable.getPhysicalColumn(i);
            
            // Change the ID too...            
            try
            {
                if (i<originalTable.nrPhysicalColumns())
                {
                    PhysicalColumn originalColumn = originalTable.getPhysicalColumn(i);
                    
                    originalColumn.setConcept(physicalColumn.getConcept());
                }
                else
                {
                    originalTable.addPhysicalColumn(physicalColumn);
                }
            
                originalTable.getPhysicalColumn(i).setId( physicalColumn.getId() );
            }
            catch (ObjectAlreadyExistsException e)
            {
                new ErrorDialog(shell, "Error", "A physical column with the same id '"+physicalColumn.getId()+"' already exists.", e);
            }
        }

        
		tablename = wId.getText();
		dispose();
	}
	
    private void getTableName()
    {
        try
        {
            // New class: SelectTableDialog
            ConceptPropertyWidgetInterface okWidgetInterface = (ConceptPropertyWidgetInterface) widgetInterfaces.get(DefaultPropertyID.TARGET_TABLE.getId());
            if (okWidgetInterface!=null)
            {
                DatabaseExplorerDialog std = new DatabaseExplorerDialog(shell, SWT.NONE, databaseMeta, null);
    
                ConceptPropertyInterface property = okWidgetInterface.getValue();
                if (property!=null)
                {
                    std.setSelectedTable(property.toString());
                }
            
                String tableName = (String)std.open();
                if (tableName != null)
                {
                    okWidgetInterface.setValue(new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(), tableName));
                }
            }
        }
        catch(Exception e)
        {
            new ErrorDialog(shell, "Error", "There was an error getting the target table name", e);
        }
    }
}
