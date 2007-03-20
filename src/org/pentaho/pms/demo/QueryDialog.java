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
package org.pentaho.pms.demo;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.pms.editor.MetaEditor;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessView;
import org.pentaho.pms.schema.OrderBy;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.WhereCondition;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.database.Database;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.dialog.EnterTextDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.dialog.PreviewRowsDialog;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.core.widget.TreeMemory;
import be.ibridge.kettle.trans.step.BaseStepDialog;

/**
 * The pentaho metadata query editor
 * 
 * TODO: capture view change and show warning that all previous selections will be erased.
 *       Perhaps we can save a query per view too.
 * 
 * @author Matt
 *
 */
public class QueryDialog extends Dialog
{
    private static final String STRING_ASCENDING = "Ascending";
    private static final String STRING_DESCENDING = "Descending";
    
    private static final String STRING_CATEGORIES_TREE = MetaEditor.STRING_CATEGORIES_TREE;
    private static final String STRING_CATEGORIES      = MetaEditor.STRING_CATEGORIES;

    private Button wOK, wSQL, wCancel;
    private Listener lsOK, lsSQL, lsCancel;

    private Shell  shell;
    private ModifyListener lsMod;
    private Props props;

    private MQLQuery query;

    private SchemaMeta schemaMeta;
    private String locale;

    private List wViews;
    private Tree wCat;
    private Button wAddColumn;
    private Button wDelColumn;
    private TableView wColumns;
    private Button wAddCondition;
    private Button wDelCondition;
    private TableView wConditions;
    private TreeItem tiCategories;

    private java.util.List columns;
    private java.util.List conditions;
    private java.util.List orders;

    private TableView wOrder;
    private Button wAddOrder;
    private Button wDelOrder;
    private Button wAscending;
    private Button wDescending;
    private Button wUpColumn;
    private Button wDownColumn;
    private Label wlComments;

    public QueryDialog(Shell parent, SchemaMeta schemaMeta, MQLQuery query)
    {
        super(parent, SWT.NONE);
        this.schemaMeta = schemaMeta;
        this.query = query;
        
        // If the model is not the same as the previous: clear the previous query.
        // Just as a precaution.
        //
        if (query!=null && query.getSchemaMeta()!=null && query.getSchemaMeta().getModelName()!=null && !query.getSchemaMeta().getModelName().equals(schemaMeta.getModelName()))
        {
            query=null;
        }
        
        props = Props.getInstance();
        locale = schemaMeta.getActiveLocale();
        
        clearSelection(); // just to make sure.
    }
    
    public MQLQuery open()
    {
        Shell parent = getParent();
        Display display = parent.getDisplay();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
        shell.setBackground(GUIResource.getInstance().getColorBackground());
        
        FormLayout formLayout = new FormLayout ();
        formLayout.marginWidth  = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;
        shell.setLayout(formLayout);
        
        shell.setText("Query editor");
        
        addMenu();
        
        int margin = Const.MARGIN;
        int middle = props.getMiddlePct()/2;
        
        // The buttons...
        wOK=new Button(shell, SWT.PUSH);
        wOK.setText("  &OK  ");
        wSQL=new Button(shell, SWT.PUSH);
        wSQL.setText("  &SQL  ");
        wCancel=new Button(shell, SWT.PUSH);
        wCancel.setText("  &Cancel  ");
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wSQL, wCancel }, Const.MARGIN, null);

        // Add a label for the business view
        Label wlViews = new Label(shell, SWT.LEFT);
        wlViews.setText("Select the business view :");
        props.setLook(wlViews);
        FormData fdlViews = new FormData();
        fdlViews.left = new FormAttachment(0,0);
        fdlViews.top  = new FormAttachment(0,0);
        wlViews.setLayoutData(fdlViews);
        
        // Add a List for the business views
        wViews = new List(shell, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        props.setLook(wViews);
        FormData fdViews = new FormData();
        fdViews.left   = new FormAttachment(0 , 0);
        fdViews.right  = new FormAttachment(middle, 0);
        fdViews.top    = new FormAttachment(wlViews, margin);
        fdViews.bottom = new FormAttachment(wlViews, 70);
        wViews.setLayoutData(fdViews);
        addListenersToViews();
        
        // Below those 2, we show the categories tree
        
        // Add a label for the tree
        Label wlCat = new Label(shell, SWT.LEFT);
        wlCat.setText("The categories and columns:");
        props.setLook(wlCat);
        FormData fdlCat = new FormData();
        fdlCat.left = new FormAttachment(0,0);
        fdlCat.top  = new FormAttachment(wViews, 2*margin);
        wlCat.setLayoutData(fdlCat);
        
        // Add the categories tree itself
        wCat = new Tree(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        props.setLook(wCat);
        FormData fdCat = new FormData();
        fdCat.left   = new FormAttachment(0 , 0);
        fdCat.right  = new FormAttachment(middle, 0);
        fdCat.top    = new FormAttachment(wlCat, margin);
        fdCat.bottom = new FormAttachment(wOK, -2*margin);
        wCat.setLayoutData(fdCat);
        TreeMemory.addTreeListener(wCat, STRING_CATEGORIES_TREE);
        addListenersToCat();
        
        // Then we add the selected columns grid and a couple of buttons
        //
        
        // the add column button
        wAddColumn = new Button(shell, SWT.PUSH);
        wAddColumn.setText("Add");
        wAddColumn.setToolTipText("Add columns to the selection");
        props.setLook(wAddColumn);
        FormData fdAddColumn = new FormData();
        fdAddColumn.left   = new FormAttachment(wCat , 2*margin);
        fdAddColumn.top    = new FormAttachment(wlCat, margin+20);
        wAddColumn.setLayoutData(fdAddColumn);
        wAddColumn.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { addColumnsToSelection(); }});
        
        // The delete column button
        wDelColumn = new Button(shell, SWT.PUSH);
        wDelColumn.setText("Del");
        wDelColumn.setToolTipText("Remove columns from the selection");
        props.setLook(wDelColumn);
        FormData fdDelColumn = new FormData();
        fdDelColumn.left   = new FormAttachment(wCat , 2*margin);
        fdDelColumn.top    = new FormAttachment(wAddColumn, 3*margin);
        wDelColumn.setLayoutData(fdDelColumn);
        wDelColumn.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeColumnsFromSelection(); }});

        // Move column up button
        wUpColumn = new Button(shell, SWT.PUSH);
        wUpColumn.setText("Up");
        wUpColumn.setToolTipText("Move columns up");
        props.setLook(wUpColumn);
        FormData fdUpColumn = new FormData();
        fdUpColumn.left   = new FormAttachment(wCat , 2*margin);
        fdUpColumn.top    = new FormAttachment(wDelColumn, 3*margin);
        wUpColumn.setLayoutData(fdUpColumn);
        wUpColumn.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { moreColumnsUp(); }});

        // Move column up button
        wDownColumn = new Button(shell, SWT.PUSH);
        wDownColumn.setText("Down");
        wDownColumn.setToolTipText("Move columns down");
        props.setLook(wDownColumn);
        FormData fdDownColumn = new FormData();
        fdDownColumn.left   = new FormAttachment(wCat , 2*margin);
        fdDownColumn.top    = new FormAttachment(wUpColumn, 3*margin);
        wDownColumn.setLayoutData(fdDownColumn);
        wDownColumn.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { moreColumnsDown(); }});

        // Add a label for the selected columns
        Label wlColumns = new Label(shell, SWT.LEFT);
        wlColumns.setText("The selected columns:");
        props.setLook(wlColumns);
        FormData fdlColumns = new FormData();
        fdlColumns.left = new FormAttachment(wDownColumn, 2*margin);
        fdlColumns.top  = new FormAttachment(wlCat, margin);
        wlColumns.setLayoutData(fdlColumns);

        // Then we add a grid with the selected columns
        ColumnInfo[] columnFields =new ColumnInfo[]
          {
            new ColumnInfo("Table",     ColumnInfo.COLUMN_TYPE_TEXT, false, true),
            new ColumnInfo("Column",    ColumnInfo.COLUMN_TYPE_TEXT, false, true),
          };
                          
        wColumns=new TableView(shell, 
                                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                                columnFields, 
                                1, // updated elsewhere  
                                true, // read-only
                                lsMod,
                                props
                                );
        FormData fdColumns = new FormData();
        fdColumns.left   = new FormAttachment(wDownColumn, 2*margin);
        fdColumns.right  = new FormAttachment(60,  0);
        fdColumns.top    = new FormAttachment(wlColumns, margin);
        fdColumns.bottom = new FormAttachment(wCat, 0, SWT.CENTER);
        wColumns.setLayoutData(fdColumns);
        addListenersToColumns();
        
        
        // To the right of that, we add an ordering view...
        
        // First a couple of buttons...
        
        // the add order button
        wAddOrder = new Button(shell, SWT.PUSH);
        wAddOrder.setText("Add");
        wAddOrder.setToolTipText("Add column order");
        props.setLook(wAddOrder);
        FormData fdAddOrder = new FormData();
        fdAddOrder.left   = new FormAttachment(wColumns , 2*margin);
        fdAddOrder.top    = new FormAttachment(wlCat, margin+20);
        wAddOrder.setLayoutData(fdAddOrder);
        wAddOrder.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { addColumnsToOrder(); }});
        
        // The delete column button
        wDelOrder = new Button(shell, SWT.PUSH);
        wDelOrder.setText("Del");
        wDelOrder.setToolTipText("Remove columns order");
        props.setLook(wDelOrder);
        FormData fdDelOrder = new FormData();
        fdDelOrder.left   = new FormAttachment(wColumns , 2*margin);
        fdDelOrder.top    = new FormAttachment(wAddOrder, 3*margin);
        wDelOrder.setLayoutData(fdDelOrder);
        wDelOrder.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeOrderLines(); }});
        
        // Then we add a grid with the ordering
        
        // First we add a few buttons on the right side
        //
        
        // the add order button
        // The delete column button
        wDescending = new Button(shell, SWT.PUSH);
        wDescending.setText("DESC");
        wDescending.setToolTipText("Sort descending");
        props.setLook(wDescending);
        FormData fdDescending = new FormData();
        fdDescending.right = new FormAttachment(100, 0);
        fdDescending.top   = new FormAttachment(wlCat, margin+20);
        wDescending.setLayoutData(fdDescending);
        wDescending.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { setOrderAscending(false); }});
        
        wAscending = new Button(shell, SWT.PUSH);
        wAscending.setText("ASC");
        wAscending.setToolTipText("Sort ascending");
        props.setLook(wAscending);
        FormData fdAscending = new FormData();
        fdAscending.left  = new FormAttachment(wDescending, 0, SWT.CENTER);
        fdAscending.top   = new FormAttachment(wDescending, 3*margin);
        wAscending.setLayoutData(fdAscending);
        wAscending.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { setOrderAscending(true); }});
        
        // The orders label & grid
        
        // Add a label for the order
        Label wlOrder = new Label(shell, SWT.LEFT);
        wlOrder.setText("The order by clause:");
        props.setLook(wlOrder);
        FormData fdlOrder = new FormData();
        fdlOrder.left = new FormAttachment(wAddOrder, 2*margin);
        fdlOrder.top  = new FormAttachment(wlCat, margin);
        wlOrder.setLayoutData(fdlOrder);

        ColumnInfo[] orderFields =new ColumnInfo[]
          {
            new ColumnInfo("Table",     ColumnInfo.COLUMN_TYPE_TEXT, false, true),
            new ColumnInfo("Column",    ColumnInfo.COLUMN_TYPE_TEXT, false, true),
            new ColumnInfo("Ordering",  ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "-", STRING_ASCENDING, STRING_DESCENDING }, true),
          };
                          
        wOrder=new TableView(shell, 
                                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                                orderFields, 
                                1, // updated elsewhere  
                                true, // read-only
                                lsMod,
                                props
                                );
        FormData fdOrder = new FormData();
        fdOrder.left   = new FormAttachment(wAddOrder, 2*margin);
        fdOrder.right  = new FormAttachment(wDescending,  -2*margin);
        fdOrder.top    = new FormAttachment(wlOrder, margin);
        fdOrder.bottom = new FormAttachment(wCat, 0, SWT.CENTER);
        wOrder.setLayoutData(fdOrder);
        addListenersToOrders();
        
        // Now we add a few conditions
        //
        
        // the add condition button
        wAddCondition = new Button(shell, SWT.PUSH);
        wAddCondition.setText("Add");
        wAddCondition.setToolTipText("Add columns to the conditions");
        props.setLook(wAddCondition);
        FormData fdAddCondition = new FormData();
        fdAddCondition.left   = new FormAttachment(wCat, 2*margin);
        fdAddCondition.top    = new FormAttachment(wColumns, 2*margin+20);
        wAddCondition.setLayoutData(fdAddCondition);
        wAddCondition.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { addSelectionToConditions(); }});
        
        // The delete column button
        wDelCondition = new Button(shell, SWT.PUSH);
        wDelCondition.setText("Del");
        wDelCondition.setToolTipText("Remove columns from the conditions");
        props.setLook(wDelCondition);
        FormData fdDelCondition = new FormData();
        fdDelCondition.left   = new FormAttachment(wCat , 2*margin);
        fdDelCondition.top    = new FormAttachment(wAddCondition, 3*margin);
        wDelCondition.setLayoutData(fdDelCondition);
        wDelCondition.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeConditionsFromSelection(); }});

        // Then we add a grid with the selected columns
        
        // Add a label too
        Label wlConditions = new Label(shell, SWT.LEFT);
        wlConditions.setText("The conditions: ");
        props.setLook(wlConditions);
        FormData fdlConditions = new FormData();
        fdlConditions.left = new FormAttachment(wDownColumn, 2*margin);
        fdlConditions.top  = new FormAttachment(wColumns, 3*margin);
        wlConditions.setLayoutData(fdlConditions);

        ColumnInfo[] conditionFields =new ColumnInfo[]
          {
            new ColumnInfo("Operator",        ColumnInfo.COLUMN_TYPE_TEXT, false, false),
            new ColumnInfo("Column",          ColumnInfo.COLUMN_TYPE_TEXT, false, true),
            new ColumnInfo("Condition",       ColumnInfo.COLUMN_TYPE_TEXT, false, false),
          };
                          
        wConditions=new TableView(shell, 
                                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                                conditionFields, 
                                1, // updated elsewhere  
                                true, // read-only
                                lsMod,
                                props
                                );
        FormData fdConditions = new FormData();
        fdConditions.left   = new FormAttachment(wDownColumn, 2*margin);
        fdConditions.right  = new FormAttachment(100,  0);
        fdConditions.top    = new FormAttachment(wlConditions, margin);
        fdConditions.bottom = new FormAttachment(wOK, -margin);
        wConditions.setLayoutData(fdConditions);
        
        
        // Finally, add a comments zone, for now a title
        wlComments = new Label(shell, SWT.LEFT);
        wlComments.setText("Pentaho Metadata Query Editor");
        props.setLook(wlComments);
        wlComments.setFont(GUIResource.getInstance().getFontLarge());
        FormData fdlComments = new FormData();
        fdlComments.left = new FormAttachment(wDownColumn, 2*margin);
        fdlComments.top  = new FormAttachment(wlViews, margin);
        wlComments.setLayoutData(fdlComments);

        // Wrap it up ...
        //
        
        // Add listeners
        lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
        lsSQL      = new Listener() { public void handleEvent(Event e) { showSQL(); } };
        lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
        
        wOK.addListener    (SWT.Selection, lsOK     );
        wSQL.addListener   (SWT.Selection, lsSQL    );
        wCancel.addListener(SWT.Selection, lsCancel );
        
        // Detect [X] or ALT-F4 or something that kills this window...
        shell.addShellListener( new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

        WindowProperty winprop = props.getScreen(shell.getText());
        if (winprop!=null) winprop.setShell(shell); else shell.pack();
        
        getData();
        
        shell.open();
        while (!shell.isDisposed())
        {
                if (!display.readAndDispatch()) display.sleep();
        }

        return query;
    }
    
    public void addMenu()
    {
        Menu mBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(mBar);
        
        // main File menu...
        MenuItem mFile = new MenuItem(mBar, SWT.CASCADE); mFile.setText("&File");
        Menu msFile = new Menu(shell, SWT.DROP_DOWN);
        mFile.setMenu(msFile);
        
        MenuItem miFileNew = new MenuItem(msFile, SWT.CASCADE); miFileNew.setText("&New");
        MenuItem miFileOpen = new MenuItem(msFile, SWT.CASCADE); miFileOpen.setText("&Open");
        MenuItem miFileSave = new MenuItem(msFile, SWT.CASCADE); miFileSave.setText("&Save");
        new MenuItem(msFile, SWT.SEPARATOR);
        MenuItem miFileSQL = new MenuItem(msFile, SWT.CASCADE); miFileSQL.setText("S&QL");
        new MenuItem(msFile, SWT.SEPARATOR);
        MenuItem miFileQuit = new MenuItem(msFile, SWT.CASCADE); miFileQuit.setText("&Quit");
        
        Listener lsFileNew  = new Listener() { public void handleEvent(Event e) { newFile();  } };
        Listener lsFileOpen = new Listener() { public void handleEvent(Event e) { openFile(); } };
        Listener lsFileSave = new Listener() { public void handleEvent(Event e) { saveFile(); } };
        Listener lsFileSQL  = new Listener() { public void handleEvent(Event e) { showSQL();  } };
        Listener lsFileQuit = new Listener() { public void handleEvent(Event e) { quitFile(); } };
        
        miFileNew       .addListener (SWT.Selection, lsFileNew    );
        miFileOpen      .addListener (SWT.Selection, lsFileOpen   );
        miFileSave      .addListener (SWT.Selection, lsFileSave   );
        miFileSQL       .addListener (SWT.Selection, lsFileSQL    );
        miFileQuit      .addListener (SWT.Selection, lsFileQuit   );
    }

    private void newFile()
    {
        query = null;
        getData();
    }

    private void clearSelection()
    {
        columns = new ArrayList();
        conditions = new ArrayList();
        orders = new ArrayList();    
    }

    private void openFile()
    {
        FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
        fileDialog.setFilterExtensions(new String[] { "*.mql", "*.xml", "*.*" } );
        fileDialog.setFilterNames(new String[] { "MQL queries", "XML files", "All files" } );
        String filename = fileDialog.open();
        if (filename!=null) 
        {
            try
            {
                query = new MQLQuery(filename);
                getData();
            }
            catch(Exception e)
            {
                new ErrorDialog(shell, "Error loading Metadata Query", "There was an error loading the metadata query!", e);
            }
        }
    }

    private void saveFile()
    {
        FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
        fileDialog.setFilterExtensions(new String[] { "*.mql", "*.xml", "*.*" } );
        fileDialog.setFilterNames(new String[] { "MQL queries", "XML files", "All files" } );
        String filename = fileDialog.open();
        if (filename!=null) 
        {
            try
            {
                if (getView()!=null)
                {
                    query = getQuery();
                    
                    query.save(filename);
                }
                else
                {
                    throw new Exception("No business view was selected, nothing to save.");
                }
            }
            catch(Exception e)
            {
                new ErrorDialog(shell, "Error loading Metadata Query", "There was an error loading the metadata query!", e);
            }
        }
    }

    private void quitFile()
    {
        cancel();
    }


    private void addSelectionToConditions()
    {
        TreeItem[] items = wCat.getSelection();
        
        java.util.List indices = new ArrayList();
        for (int i=0;i<items.length;i++)
        {
            TreeItem treeItem = items[i];
            
            if (treeItem.getItemCount()!=0) return; // not at the lowest level, not a business column
            
            String[] path = Const.getTreeStrings(treeItem, 1);
            
            BusinessView activeView = getView();
            
            BusinessCategory businessCategory = activeView.findBusinessCategory(path, locale);
            BusinessColumn businessColumn =  businessCategory.findBusinessColumn(treeItem.getText(), false, locale);
            
            if (businessColumn!=null)
            {
                WhereCondition whereCondition = new WhereCondition(null, businessColumn, "");
                conditions.add(whereCondition);
                indices.add(new Integer(conditions.size()-1));
            }
        }
        updateConditions();
        
        int idxs[] = new int[indices.size()];
        for (int i=0;i<idxs.length;i++) idxs[i] = ((Integer)indices.get(i)).intValue();
        wConditions.table.select(idxs);
        wConditions.setFocus();
    }

    protected void moreColumnsUp()
    {
        int idxs[] = wColumns.getSelectionIndices();
        BusinessColumn[] cols = new BusinessColumn[idxs.length];
        for (int i=0;i<idxs.length;i++) cols[i] = (BusinessColumn) columns.get(idxs[i]);
        
        // All these columns need to be moved up in the ranking
        for (int i=0;i<cols.length;i++)
        {
            int idx = columns.indexOf(cols[i]);
            if (idx>0)
            {
                BusinessColumn one = (BusinessColumn) columns.get(idx);
                BusinessColumn two = (BusinessColumn) columns.get(idx-1);
                
                columns.set(idx,   two);
                columns.set(idx-1, one);
                
                idxs[i] = idx-1;
            }
        }
        updateColumns();
        wColumns.table.select(idxs);
        wColumns.setFocus();
    }

    protected void moreColumnsDown()
    {
        int idxs[] = wColumns.getSelectionIndices();
        BusinessColumn[] cols = new BusinessColumn[idxs.length];
        for (int i=0;i<idxs.length;i++) cols[i] = (BusinessColumn) columns.get(idxs[i]);
        
        // All these columns need to be moved up in the ranking
        for (int i=0;i<cols.length;i++)
        {
            int idx = columns.indexOf(cols[i]);
            if (idx+1<columns.size())
            {
                BusinessColumn one = (BusinessColumn) columns.get(idx);
                BusinessColumn two = (BusinessColumn) columns.get(idx+1);
                
                columns.set(idx,   two);
                columns.set(idx+1, one);
                
                idxs[i] = idx+1;
            }
        }
        updateColumns();
        wColumns.table.select(idxs);
        wColumns.setFocus();
    }
    protected void removeOrderLines()
    {
        int[] idxs = wOrder.getSelectionIndices();
        for (int i=idxs.length-1;i>=0;i--)
        {
            if (idxs[i]>=0 && idxs[i]<orders.size())
            {
                orders.remove(idxs[i]);
            }
        }
        updateOrders();
        wOrder.setFocus();
    }

    protected void setOrderAscending(boolean ascending)
    {
        int[] idxs = wOrder.getSelectionIndices();
        for (int i=0;i<idxs.length;i++)
        {
            if (idxs[i]>=0 && idxs[i]<orders.size())
            {
                OrderBy orderBy = (OrderBy) orders.get(idxs[i]);
                orderBy.setAscending(ascending);
            }
        }
        updateOrders();
        wOrder.table.setSelection(idxs); // refresh looses selection
        wOrder.setFocus();
    }

    private void removeColumnsFromSelection()
    {
        int idxs[] = wColumns.getSelectionIndices();
        BusinessColumn[] cols = new BusinessColumn[idxs.length];
        for (int i=0;i<idxs.length;i++) cols[i] = (BusinessColumn) columns.get(idxs[i]);
        for (int i=0;i<cols.length;i++) columns.remove(cols[i]);
        updateColumns();
        wColumns.setFocus();
    }

    private void removeConditionsFromSelection()
    {
        int idxs[] = wConditions.getSelectionIndices();
        WhereCondition[] conds = new WhereCondition[idxs.length];
        for (int i=0;i<idxs.length;i++) conds[i] = (WhereCondition) conditions.get(idxs[i]);
        for (int i=0;i<conds.length;i++) conditions.remove(conds[i]);
        updateConditions();
        wConditions.setFocus();
    }
    
    private void addListenersToOrders()
    {
        wOrder.table.addSelectionListener(new SelectionAdapter()
            {
                public void widgetDefaultSelected(SelectionEvent event)
                {
                    // double clicked: change the order
                    int idx = wOrder.getSelectionIndex();
                    if (idx>=0 && idx<orders.size())
                    {
                        OrderBy orderBy = (OrderBy)orders.get(idx);
                        orderBy.setAscending(!orderBy.isAscending());
                        updateOrders();
                        wOrder.table.select(idx);
                        wOrder.setFocus();
                    }
                }
            }
        );
    }

    private void addListenersToColumns()
    {
        wColumns.table.addSelectionListener(new SelectionAdapter()
            {
                public void widgetDefaultSelected(SelectionEvent event)
                {
                    addColumnsToOrder();
                }
            }
        );
    }

    protected void addColumnsToOrder()
    {
        // Double clicked on a row.
        // The rows are read-only so we can just take the index.
        int[] idxs = wColumns.getSelectionIndices();
        for (int i=0;i<idxs.length;i++)
        {
            if (idxs[i]>=0 && idxs[i]<columns.size()) 
            {
                BusinessColumn businessColumn = (BusinessColumn) columns.get(idxs[i]);
                addColumnToOrder(businessColumn);
                updateOrders();
                wOrder.table.select(columns.size()-1);
                wOrder.setFocus();
            }
        }
    }

    private void addColumnToOrder(BusinessColumn businessColumn)
    {
        boolean exists = false;
        for (int i=0;i<orders.size() && !exists;i++)
        {
            OrderBy orderBy = (OrderBy) orders.get(i);
            if (orderBy.getBusinessColumn().equals(businessColumn)) exists=true;
        }
        if (exists) return;
        
        OrderBy orderBy = new OrderBy(businessColumn);
        orders.add(orderBy);
        updateOrders();
        wOrder.setFocus();
    }

    private void addListenersToViews()
    {
        wViews.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent event)
                {
                    // If you select another view: change the categories tree
                    updateCategories();
                }
            }
        );
    }
    
    private void addListenersToCat()
    {
        wCat.addSelectionListener(new SelectionAdapter()
            {
                public void widgetDefaultSelected(SelectionEvent event)
                {
                    addColumnsToSelection();
                }
            }
        );
    }

    private void addColumnsToSelection()
    {
        TreeItem[] items = wCat.getSelection();
        
        java.util.List indices = new ArrayList();
        for (int i=0;i<items.length;i++)
        {
            TreeItem treeItem = items[i];
            
            if (treeItem.getItemCount()!=0) return; // not at the lowest level, not a business column
            
            String[] path = Const.getTreeStrings(treeItem, 1);
            
            BusinessView activeView = getView();
            
            BusinessCategory businessCategory = activeView.findBusinessCategory(path, locale);
            BusinessColumn businessColumn =  businessCategory.findBusinessColumn(treeItem.getText(), false, locale);
            
            if (businessColumn!=null && columns.indexOf(businessColumn)<0)
            {
                columns.add(businessColumn);
                indices.add(new Integer(columns.size()-1));
            }
        }
        updateColumns();
        
        int idxs[] = new int[indices.size()];
        for (int i=0;i<idxs.length;i++) idxs[i] = ((Integer)indices.get(i)).intValue();
        wColumns.table.select(idxs);
        wColumns.setFocus();
    }

    private void updateColumns()
    {
        wColumns.clearAll(false);
        
        for (int i=0;i<columns.size();i++)
        {
            BusinessColumn businessColumn = (BusinessColumn) columns.get(i);
            if( businessColumn == null ) {
            		continue;
            }
            TableItem tableItem = new TableItem(wColumns.table, SWT.NONE);
            
            tableItem.setText(1, businessColumn.getBusinessTable().getDisplayName(locale));
            tableItem.setText(2, businessColumn.getDisplayName(locale));
            tableItem.setText(3, "-");
        }
        wColumns.removeEmptyRows();
        wColumns.setRowNums();
        wColumns.optWidth(true);
    }
    
    private void updateConditions()
    {
        wConditions.clearAll(false);
        
        for (int i=0;i<conditions.size();i++)
        {
            WhereCondition whereCondition = (WhereCondition) conditions.get(i);
            TableItem tableItem = new TableItem(wConditions.table, SWT.NONE);
            
            tableItem.setText(1, Const.NVL(whereCondition.getOperator(), ""));
            tableItem.setText(2, whereCondition.getField().getDisplayName(locale));
            tableItem.setText(3, Const.NVL(whereCondition.getCondition(), ""));
        }
        wConditions.removeEmptyRows();
        wConditions.setRowNums();
        wConditions.optWidth(true);
    }

    private void updateOrders()
    {
        wOrder.clearAll(false);
        
        for (int i=0;i<orders.size();i++)
        {
            OrderBy orderBy = (OrderBy) orders.get(i);
            TableItem tableItem = new TableItem(wOrder.table, SWT.NONE);
            if (orderBy.getBusinessColumn()!=null)
            {
                tableItem.setText(1, orderBy.getBusinessColumn().getBusinessTable().getDisplayName(locale));
                tableItem.setText(2, orderBy.getBusinessColumn().getDisplayName(locale));
            }
            tableItem.setText(3, orderBy.isAscending()?STRING_ASCENDING:STRING_DESCENDING);
        }
        wOrder.removeEmptyRows();
        wOrder.setRowNums();
        wOrder.optWidth(true);
    }


    private void updateViewList()
    {
        wViews.removeAll();
        wViews.setItems( schemaMeta.getViewNames(locale) );
    }
    
    private BusinessView getView()
    {
        if (wViews.getSelectionCount()==1)
        {
            return schemaMeta.findView(locale, wViews.getSelection()[0]);
        }
        return null;
    }
    
    private void updateCategories()
    {
        wCat.removeAll();
        
        tiCategories = new TreeItem(wCat, SWT.NONE); 
        tiCategories.setText(STRING_CATEGORIES);
        
        BusinessView activeView = getView(); 
        if (activeView!=null)
        {
            MetaEditor.addTreeCategories(tiCategories, activeView.getRootCategory(), locale, false);
        }
        TreeMemory.setExpandedFromMemory(wCat, STRING_CATEGORIES_TREE);
    }

    public void dispose()
    {
        props.setScreen(new WindowProperty(shell));
        shell.dispose();
    }
    
    private void cancel()
    {
        query=null;
        dispose();
    }
    
    private void getData()
    {
        if (schemaMeta!=null)
        {
            updateViewList();
        }
        
        if (query!=null && wViews!= null && query.getView()!=null && query.getView().getDisplayName(locale)!=null)
        {
            int idx = wViews.indexOf( query.getView().getDisplayName(locale) );
            if (idx>=0 && idx<wViews.getItemCount()) wViews.select(idx);
            
            clearSelection();
            for (int i=0;i<query.getOrder().size();i++) orders.add(query.getOrder().get(i));
            for (int i=0;i<query.getSelections().size();i++) columns.add(query.getSelections().get(i));
            for (int i=0;i<query.getConstraints().size();i++) conditions.add(query.getConstraints().get(i));
        }
        
        updateCategories();
        updateColumns();
        updateCategories();
        updateConditions();
        updateOrders();
    }

    private MQLQuery getQuery()
    {
        MQLQuery mqlQuery = new MQLQuery(schemaMeta, getView(), locale);

        // Get the conditions and operators.
        for (int i=0;i<conditions.size();i++)
        {
            WhereCondition wc = (WhereCondition) conditions.get(i);
            wc.setOperator(wConditions.getItem(i)[0]);
            wc.setCondition(wConditions.getItem(i)[2]);
        }
        
        mqlQuery.setSelections(columns);
        mqlQuery.setConstraints(conditions);
        mqlQuery.setOrder(orders);
        
        return mqlQuery;
    }

    private void ok()
    { 
        if (getView()!=null)
        {
            query = getQuery();
        }
        else
        {
            query = null;
        }
        
        dispose();
    }
    
    public void showSQL()
    {
        try
        {
            MQLQuery mqlQuery = getQuery();
            if (mqlQuery!=null)
            {
                String sql = mqlQuery.getQuery( true );
                if (sql!=null)
                {
                    EnterTextDialog showSQL = new EnterTextDialog(shell, "The generated SQL", "Here is the generated SQL", sql, true);
                    sql = showSQL.open();
                    if (!Const.isEmpty(sql))
                    {
                        
                        DatabaseMeta databaseMeta = ((BusinessColumn)mqlQuery.getSelections().get(0)).getPhysicalColumn().getTable().getDatabaseMeta();
                        executeSQL(databaseMeta, sql);
                    }
                }
            }
        }
        catch(Throwable e)
        {
            new ErrorDialog(shell, "Error", "There was an error during query generation", new Exception(e));
        }
    }

    private void executeSQL(DatabaseMeta databaseMeta, String sql)
    {
        // Now execute the query:
        Database database = null;
        java.util.List rows = null;
        try
        {
            String path = "";
            try {
                File file = new File( "simple-jndi" );
                path= file.getCanonicalPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory"); //$NON-NLS-1$ //$NON-NLS-2$
            System.setProperty("org.osjava.sj.root", path ); //$NON-NLS-1$ //$NON-NLS-2$
            System.setProperty("org.osjava.sj.delimiter", "/"); //$NON-NLS-1$ //$NON-NLS-2$
            database = new Database(databaseMeta);
            database.connect();
            rows = database.getRows(sql, 5000); // get the first 5000 rows from the query for demo-purposes.
        }
        catch(Exception e)
        {
            new ErrorDialog(shell, "Error executing query", "There was an error executing the query", e);
        }
        finally
        {
            if (database!=null) database.disconnect();
        }

        // Show the rows in a dialog.
        if (rows!=null)
        {
            PreviewRowsDialog previewRowsDialog = new PreviewRowsDialog(shell, SWT.NONE, "The first 5000 rows from the generated query", rows);
            previewRowsDialog.open();
        }    
    }
}
