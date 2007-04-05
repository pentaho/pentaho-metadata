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
import java.util.List;

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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.pms.editor.MetaEditor;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.widget.TreeMemory;
import be.ibridge.kettle.trans.step.BaseStepDialog;

/**
 * Allows the Categories to be populated with columns from the business tables.
 * 
 * @since  31-Oct-2006
 * @author Matt
 *
 */
public class BusinessCategoriesDialog extends Dialog
{
	private static final String STRING_TABLES_TREE = "TablesTree"; //$NON-NLS-1$

    private LogWriter    log;

	private Button wClose;
	private Listener lsClose;

	private Shell         shell;
	
    private Props props;

    private BusinessModel  businessModel;

    private Locales locales;

    private String activeLocale;

    private Tree wTables;

    private Tree wCategories;

    private Button wAddSelection;

    private Button wDelSelection;

    private Button wDelAll;

    private Button wAddAll;

    private Button wNew;

    private SecurityReference securityReference;

	public BusinessCategoriesDialog(Shell parent, BusinessModel businessModel, Locales locales, SecurityReference securityReference)
	{
		super(parent, SWT.NONE);
		this.businessModel = businessModel;
        this.locales = locales;
        this.securityReference = securityReference;
        
        log=LogWriter.getInstance();
        props=Props.getInstance();

        activeLocale = locales.getActiveLocale();
	}

	public void open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL);
 		props.setLook(shell);
        
        log.logDebug(this.getClass().getName(), Messages.getString("General.DEBUG_OPENING_DIALOG")); //$NON-NLS-1$

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("BusinessCategoriesDialog.USER_BUSINESS_CATEGORIES_EDITOR")); //$NON-NLS-1$
		
		int middle = 45;
		int margin = Const.MARGIN;
        
		wClose=new Button(shell, SWT.PUSH);
		wClose.setText(Messages.getString("BusinessCategoriesDialog.USER_CLOSE")); //$NON-NLS-1$
        lsClose = new Listener() { public void handleEvent(Event e) { close(); } };
        wClose.addListener(SWT.Selection, lsClose );

        BaseStepDialog.positionBottomButtons(shell, new Button[] { wClose }, margin, null);
        
        // First we need a label with a tree below it for the Business tables to select from
        //
        Label wlTables = new Label(shell, SWT.LEFT);
        props.setLook(wlTables);
        wlTables.setText(Messages.getString("BusinessCategoriesDialog.USER_BUSINESS_TABLES")); //$NON-NLS-1$
        FormData fdlTables = new FormData();
        fdlTables.left  = new FormAttachment(0, 0);
        fdlTables.right = new FormAttachment(middle, 0);
        fdlTables.top   = new FormAttachment(0,0);
        wlTables.setLayoutData(fdlTables);
        
        wTables = new Tree(shell, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        props.setLook(wTables);
        FormData fdTables = new FormData();
        fdTables.left   = new FormAttachment(0, 0);
        fdTables.right  = new FormAttachment(middle, 0);
        fdTables.top    = new FormAttachment(wlTables, margin);
        fdTables.bottom = new FormAttachment(wClose, -margin);
        wTables.setLayoutData(fdTables);

        // Some buttons in the middle
        //
        wDelAll = new Button(shell, SWT.PUSH);
        wDelAll.setText(Messages.getString("BusinessCategoriesDialog.USER_REMOVE_ALL")); //$NON-NLS-1$
        props.setLook(wDelAll);
        FormData fdDelAll = new FormData();
        fdDelAll.left   = new FormAttachment(middle, margin);
        fdDelAll.top    = new FormAttachment(wTables, -20, SWT.CENTER);
        wDelAll.setLayoutData(fdDelAll);
        wDelAll.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { delAll(); } } );

        wDelSelection = new Button(shell, SWT.PUSH);
        wDelSelection.setText(Messages.getString("BusinessCategoriesDialog.USER_REMOVE")); //$NON-NLS-1$
        props.setLook(wDelSelection);
        FormData fdDelSelection = new FormData();
        fdDelSelection.left   = new FormAttachment(wDelAll, 0, SWT.CENTER);
        fdDelSelection.bottom = new FormAttachment(wDelAll, -margin);
        wDelSelection.setLayoutData(fdDelSelection);
        wDelSelection.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { delSelection(); } } );

        wAddSelection = new Button(shell, SWT.PUSH);
        wAddSelection.setText(Messages.getString("BusinessCategoriesDialog.USER_ADD")); //$NON-NLS-1$
        props.setLook(wAddSelection);
        FormData fdAddSelection = new FormData();
        fdAddSelection.left   = new FormAttachment(wDelAll, 0, SWT.CENTER);
        fdAddSelection.bottom = new FormAttachment(wDelSelection, -margin);
        wAddSelection.setLayoutData(fdAddSelection);
        wAddSelection.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { addSelection(); } } );

        wAddAll = new Button(shell, SWT.PUSH);
        wAddAll.setText(Messages.getString("BusinessCategoriesDialog.USER_ADD_ALL")); //$NON-NLS-1$
        props.setLook(wAddAll);
        FormData fdAddAll = new FormData();
        fdAddAll.left   = new FormAttachment(wDelAll, 0, SWT.CENTER);
        fdAddAll.bottom = new FormAttachment(wAddSelection, -margin);
        wAddAll.setLayoutData(fdAddAll);
        wAddAll.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { addAll(); } } );

        
        wNew = new Button(shell, SWT.PUSH);
        wNew.setText(Messages.getString("BusinessCategoriesDialog.USER_NEW")); //$NON-NLS-1$
        props.setLook(wNew);
        FormData fdNew = new FormData();
        fdNew.left   = new FormAttachment(wDelAll, 0, SWT.CENTER);
        fdNew.top    = new FormAttachment(wDelAll, 4*margin);
        wNew.setLayoutData(fdNew);
        wNew.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newCategory(); } } );

        
        // Then we need a tree with the categories and columns on the right...
        //
        Label wlCategories = new Label(shell, SWT.LEFT);
        props.setLook(wlCategories);
        wlCategories.setText(Messages.getString("BusinessCategoriesDialog.USER_BUSINESS_CATEGORIES")); //$NON-NLS-1$
        FormData fdlCategories = new FormData();
        fdlCategories.left  = new FormAttachment(wDelAll, margin);
        fdlCategories.right = new FormAttachment(100, 0);
        fdlCategories.top   = new FormAttachment(0,0);
        wlCategories.setLayoutData(fdlCategories);
        
        wCategories = new Tree(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        props.setLook(wCategories);
        FormData fdCategories = new FormData();
        fdCategories.left   = new FormAttachment(wDelAll, margin);
        fdCategories.right  = new FormAttachment(100, 0);
        fdCategories.top    = new FormAttachment(wlCategories, margin);
        fdCategories.bottom = new FormAttachment(wClose, -margin);
        wCategories.setLayoutData(fdCategories);

        // Add tree memories to the trees.
        TreeMemory.addTreeListener(wTables, STRING_TABLES_TREE);
        TreeMemory.addTreeListener(wCategories, MetaEditor.STRING_CATEGORIES_TREE);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { close(); } } );

		WindowProperty winprop = props.getScreen(shell.getText());
		if (winprop!=null) winprop.setShell(shell); else shell.pack();
		
		getData();
		
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
	}


    /**
     * Add all business tables & columns to the categories
     *
     */
	protected void addAll()
    {
        // Where do we copy to?
        // What tree item was selected?
        TreeItem items[] = wCategories.getSelection();
        BusinessCategory parentCategory;
        String[] path;
        if (items!=null && items.length>0)
        {
            path = Const.getTreeStrings(items[0]);
            parentCategory = businessModel.findBusinessCategory(path, activeLocale);
        }
        else
        {
            path = new String[] { MetaEditor.STRING_CATEGORIES };
            parentCategory = businessModel.getRootCategory();
        }
        
        if (!parentCategory.isRootCategory()) return; // Block for now, until Ad-hoc & MDR follow
        
        for (int i=0;i<businessModel.nrBusinessTables();i++)
        {
            BusinessTable businessTable = businessModel.getBusinessTable(i);

            addBusinessTable(parentCategory, businessTable, path);
        }
        
        refreshCategories();
    }


    private void addBusinessTable(BusinessCategory parentCategory, BusinessTable businessTable, String[] parentPath)
    {
        // The id is the table name, prefixes etc.
        String id = Settings.getBusinessCategoryIDPrefix()+businessTable.getTargetTable();
        int catNr = 1;
        String newId = id;
        while (businessModel.getRootCategory().findBusinessCategory(newId)!=null)
        {
            catNr++;
            newId = id+"_"+catNr; //$NON-NLS-1$
        }
        if (Settings.isAnIdUppercase()) newId = newId.toUpperCase();
        
        // Create a new category
        //
        BusinessCategory businessCategory = new BusinessCategory(newId);
        
        // The name is the same as the table...
        String categoryName = businessTable.getDisplayName(activeLocale);
        catNr = 1;
        while (businessModel.getRootCategory().findBusinessCategory(activeLocale, categoryName)!=null)
        {
            catNr++;
            categoryName = businessTable.getDisplayName(activeLocale)+" "+catNr; //$NON-NLS-1$
        }
        businessCategory.setName(activeLocale, categoryName);
        
        // add the business columns to the category
        //
        for (int c=0;c<businessTable.nrBusinessColumns();c++)
        {
            businessCategory.addBusinessColumn(businessTable.getBusinessColumn(c));
        }
        
        // Add the category to the business model or category
        //
        try
        {
            parentCategory.addBusinessCategory(businessCategory);
        }
        catch (ObjectAlreadyExistsException e)
        {
            new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessCategoriesDialog.USER_ERROR_BUSINESS_CATEGORY_EXISTS", businessCategory.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
            return;
        }
        
        // Expand the parent tree item
        TreeMemory.getInstance().storeExpanded(MetaEditor.STRING_CATEGORIES_TREE, parentPath, true);
    }

    protected void addSelection()
    {
        // Where do we copy to?
        // What tree item was selected?
        TreeItem target[] = wCategories.getSelection();
        BusinessCategory parentCategory;
        String[] path;
        if (target!=null && target.length>0)
        {
            path = Const.getTreeStrings(target[0], 1);
            parentCategory = businessModel.findBusinessCategory(path, activeLocale);
        }
        else
        {
            path = new String[] { };
            parentCategory = businessModel.getRootCategory();
        }
    
        // OK, now loop over the selected items
        TreeItem[] source = wTables.getSelection();
        for (int i=0;i<source.length;i++)
        {
            String[] sourcePath = Const.getTreeStrings(source[i], 2);
            BusinessTable businessTable = null;
            BusinessColumn businessColumn = null;
            if (sourcePath.length>0) businessTable = businessModel.findBusinessTable(activeLocale, sourcePath[0]);
            if (sourcePath.length>1 && businessTable!=null) businessColumn = businessTable.findBusinessColumn(activeLocale, sourcePath[1]);
            
            if (businessColumn!=null)
            {
                parentCategory.addBusinessColumn(businessColumn);
            }
            else
            if (businessTable!=null)
            {
                addBusinessTable(parentCategory, businessTable, path);
            }
        }
        refreshCategories();
    }

    protected void delSelection()
    {
        // Where do we delete?
        // What tree item was selected?
        TreeItem target[] = wCategories.getSelection();
        String[] path;
        if (target!=null && target.length>0)
        {
            TreeItem treeItem = target[0];
            path = Const.getTreeStrings(treeItem);
            String[] newPath = new String[path.length-1];
            for (int p=1; p < path.length; p++){
              newPath[p-1]=path[p];
            }
            path=newPath;
            
            if (path.length>1)
            {
                BusinessCategory businessCategory = businessModel.findBusinessCategory(path, activeLocale);
                BusinessColumn businessColumn = null;
                if (businessCategory!=null) businessColumn = businessCategory.findBusinessColumn(treeItem.getText(), activeLocale);
                
                if (businessColumn!=null)
                {
                    int idx = businessCategory.indexOfBusinessColumn(businessColumn);
                    if (idx>=0) businessCategory.removeBusinessColumn(idx);
                }
                else
                {
                    BusinessCategory parentCategory;
                    if (path.length>2)
                    {
                        // Find the parentCategory
                        String parentPath[] = new String[path.length-1];
                        for (int i=0;i<path.length-1;i++) parentPath[i] = path[i];
                        parentCategory = businessModel.findBusinessCategory(parentPath, activeLocale);
                    }
                    else
                    {
                        parentCategory = businessModel.getRootCategory();
                    }
                    
                    if (!businessCategory.equals(parentCategory))
                    {
                        int idx = parentCategory.indexOfBusinessCategory(businessCategory);
                        if (idx>=0) parentCategory.removeBusinessCategory(idx);
                    }
                }
            }
            refreshCategories();
        }

    }

    protected void delAll()
    {
        businessModel.getRootCategory().getBusinessCategories().clear();
        refreshCategories();
    }

    protected void newCategory()
    {
        // Where do we copy to?
        // What tree item was selected?
        TreeItem target[] = wCategories.getSelection();
        BusinessCategory parentCategory;
        String[] path;
        if (target!=null && target.length>0)
        {
            path = Const.getTreeStrings(target[0]);
            parentCategory = businessModel.findBusinessCategory(path, activeLocale);
        }
        else
        {
            path = new String[] { MetaEditor.STRING_CATEGORIES };
            parentCategory = businessModel.getRootCategory();
        }

        if (!parentCategory.isRootCategory()) return; // Block for now, until Ad-hoc & MDR follow
        
        while(true)
        {
            BusinessCategory businessCategory = new BusinessCategory();
            BusinessCategoryDialog dialog = new BusinessCategoryDialog(shell, businessCategory, locales, securityReference);
            if (dialog.open()!=null)
            {
                // Add this to the parent.
                try
                {
                    parentCategory.addBusinessCategory(businessCategory);

                    TreeMemory.getInstance().storeExpanded(MetaEditor.STRING_CATEGORIES_TREE, path, true);
                    
                    // refresh the categories
                    refreshCategories();
                    break;
                }
                catch (ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessCategoriesDialog.USER_ERROR_BUSINESS_CATEGORY_EXISTS", businessCategory.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
                }
            }
            else
            {
                break;
            }
        }
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
        refreshCategories();
        refreshTables();
	}

    private void refreshCategories()
    {
        wCategories.removeAll();

        TreeItem treeItem = new TreeItem(wCategories, SWT.NONE);
        treeItem.setText(MetaEditor.STRING_CATEGORIES);
        TreeMemory.getInstance().storeExpanded(MetaEditor.STRING_CATEGORIES_TREE, new String[] { MetaEditor.STRING_CATEGORIES }, true);
        
        MetaEditor.addTreeCategories(treeItem, businessModel.getRootCategory(), activeLocale, true);

        TreeMemory.setExpandedFromMemory(wCategories, MetaEditor.STRING_CATEGORIES_TREE);
    }

	private void refreshTables()
    {
        wTables.removeAll();
        
        TreeItem modelItem = new TreeItem(wTables, SWT.NONE);
        String modelName = businessModel.getDisplayName(activeLocale);
        modelItem.setText(0, modelName);
        modelItem.setForeground(GUIResource.getInstance().getColorBlack());
        TreeMemory.getInstance().storeExpanded(STRING_TABLES_TREE, new String[] { modelName }, true);
        
        TreeItem tableParent = new TreeItem(modelItem, SWT.NONE);
        tableParent.setText(MetaEditor.STRING_BUSINESS_TABLES);
        tableParent.setForeground(GUIResource.getInstance().getColorBlack());
        TreeMemory.getInstance().storeExpanded(STRING_TABLES_TREE, new String[] { modelName, MetaEditor.STRING_BUSINESS_TABLES }, true);
        
        for (int t=0;t<businessModel.nrBusinessTables();t++)
        {
            BusinessTable businessTable = businessModel.getBusinessTable(t);
            
            TreeItem tableItem = new TreeItem(tableParent, SWT.NONE);
            tableItem.setText(0, businessTable.getDisplayName(activeLocale));
            tableItem.setForeground(GUIResource.getInstance().getColorBlack());
            
            for (int c=0;c<businessTable.nrBusinessColumns();c++)
            {
                BusinessColumn businessColumn = businessTable.getBusinessColumn(c);
                
                TreeItem columnItem = new TreeItem(tableItem, SWT.NONE);
                columnItem.setText(0, businessColumn.getDisplayName(activeLocale));
                columnItem.setForeground(GUIResource.getInstance().getColorBlue());
            }
        }
        
        TreeMemory.setExpandedFromMemory(wTables, STRING_TABLES_TREE);
    }
	
	private void close()
	{
        dispose();
	}
}
