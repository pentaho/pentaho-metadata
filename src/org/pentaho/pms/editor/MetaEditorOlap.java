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
package org.pentaho.pms.editor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.BusinessView;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.dialog.DialogGetDataInterface;
import org.pentaho.pms.schema.olap.OlapCube;
import org.pentaho.pms.schema.olap.OlapDimension;
import org.pentaho.pms.schema.olap.OlapDimensionUsage;
import org.pentaho.pms.schema.olap.OlapHierarchy;
import org.pentaho.pms.schema.olap.OlapHierarchyLevel;
import org.pentaho.pms.schema.olap.OlapMeasure;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.dialog.EnterSelectionDialog;
import be.ibridge.kettle.core.dialog.EnterStringDialog;
import be.ibridge.kettle.core.widget.LabelText;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.core.widget.TreeMemory;

/**
 * This class allows concepts in a model to be edited and linked to parents.
 * 
 * @since 3-okt-2006
 *
 */
public class MetaEditorOlap extends Composite implements DialogGetDataInterface
{
    public static final String STRING_DIMENSIONS = "Dimensions";
    public static final String STRING_CUBES      = "Cubes";

    private static final String STRING_OLAP_TREE = "DimensionsTree"; 

    private Props props;
	private Shell shell;
    
    private SashForm   sashform;

    private Tree   wTree;
    
	private TreeItem tiDimensions;
    private TreeItem tiCubes;
    
    private Menu menu;
    private MetaEditor metaEditor;
    
    private Label viewLabel;
    private Composite compDynamic;
    private int middle;
    private int margin;
    protected boolean hierarchyHadFocus;
    protected int hierarchyPosition;

	public MetaEditorOlap(Composite parent, int style, final MetaEditor metaEditor)
	{
		super(parent, style);
        this.metaEditor = metaEditor;
		shell=parent.getShell();
        props = Props.getInstance();
        props.setLook(this);

        middle = props.getMiddlePct();
        margin = Const.MARGIN;
        

        // Fill the complete tab with the sash-form.
		setLayout(new FormLayout());

        Button genModel = new Button(this, SWT.PUSH);
        genModel.setText("Generate Mondrian model");
        props.setLook(genModel);
        FormData fdGenModel = new FormData();
        fdGenModel.top   = new FormAttachment(0,0);
        fdGenModel.right = new FormAttachment(100, 0);
        genModel.setLayoutData(fdGenModel);
        genModel.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { metaEditor.getMondrianModel(); } });
        
        // Show the active business view...
        viewLabel = new Label(this, SWT.LEFT);
        viewLabel.setFont(GUIResource.getInstance().getFontLarge());
        props.setLook(viewLabel);
        FormData fdViewLabel = new FormData();
        fdViewLabel.left  = new FormAttachment(0,0);
        fdViewLabel.top   = new FormAttachment(0,0);
        fdViewLabel.right = new FormAttachment(genModel, -Const.MARGIN);
        viewLabel.setLayoutData(fdViewLabel);
        
        // First create the sash-form to create our tree on the left
        //
        sashform = new SashForm(this, SWT.HORIZONTAL);
        props.setLook(sashform);

        // Now create a tree on the left side of the screen
        //
        addTree();
        
        // Add the right side of the tab: a new composite 
        //
        addDynamicComposite();   
        
        sashform.setWeights(new int[] { 30, 70 } );
        sashform.setVisible(true);

        FormData fdSashForm = new FormData();
        fdSashForm.left   = new FormAttachment(0, 0);
        fdSashForm.right  = new FormAttachment(100, 0);
        fdSashForm.top    = new FormAttachment(viewLabel, margin);
        fdSashForm.bottom = new FormAttachment(100, 0);
        sashform.setLayoutData(fdSashForm);
        
        setVisible(true);

        String path[] = new String[] { STRING_DIMENSIONS }; 
        selectTreeItem(path);
        TreeMemory.getInstance().storeExpanded(STRING_OLAP_TREE, path, true);
        
		layout();
	}

    private void addTree()
    {
        // Main: the top left tree containing connections, physical tables, business views, etc.
        //
        Composite compMain = new Composite(sashform, SWT.NONE);
        FormLayout formLayout = new FormLayout();
        formLayout.spacing = margin;
        compMain.setLayout(formLayout);
                    
        // Now set up the concepts tree
        wTree = new Tree(compMain, SWT.SINGLE | SWT.BORDER);
        props.setLook(wTree);
        FormData fdTree = new FormData();
        fdTree.left   = new FormAttachment(0,0);
        fdTree.top    = new FormAttachment(0,0);
        fdTree.right  = new FormAttachment(100,0);
        fdTree.bottom = new FormAttachment(100,0);
        wTree.setLayoutData(fdTree);

        tiDimensions = new TreeItem(wTree, SWT.NONE); 
        tiDimensions.setText(STRING_DIMENSIONS);
        tiDimensions.setExpanded(true);

        tiCubes = new TreeItem(wTree, SWT.NONE); 
        tiCubes.setText(STRING_CUBES);
        tiCubes.setExpanded(true);

        // Popup-menu: right click
        SelectionAdapter lsEditMainSel = new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { setMenu(e); } };
        wTree.addSelectionListener(lsEditMainSel);

        // Normal selection: left click to select business view
        SelectionListener lsSelView = new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { showDynamicComposite(); } };
        wTree.addSelectionListener(lsSelView);
                
        // Add tree memories to the trees.
        TreeMemory.addTreeListener(wTree, STRING_OLAP_TREE);
    }
    
    private void addDynamicComposite()
    {
        // composite for the right side of the screen
        //
        compDynamic = new Composite(sashform, SWT.NONE);
        props.setLook(compDynamic);

        // The composite takes the whole right side of the sash form...
        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = margin;
        formLayout.marginHeight = margin;
        compDynamic.setLayout(formLayout);   
        
        showDynamicComposite();
        
        FormData fdConcept= new FormData();
        fdConcept.left   = new FormAttachment(0,0);
        fdConcept.right  = new FormAttachment(100, 0);
        fdConcept.top    = new FormAttachment(0,0);
        fdConcept.bottom = new FormAttachment(100, 0);
        compDynamic.setLayoutData(fdConcept);

    }

    private void showDynamicComposite()
    {
        // Clear the composite!
        Control[] children = compDynamic.getChildren();
        for (int i=0;i<children.length;i++) children[i].dispose();

        BusinessView activeView = metaEditor.getSchemaMeta().getActiveView();
        String locale = metaEditor.getSchemaMeta().getActiveLocale();
        
        if (activeView!=null)
        {
            Object selectedObject = getSelectedObject(activeView, locale);
            if (selectedObject!=null)
            {
                if (selectedObject instanceof OlapDimension)      showDimension((OlapDimension)selectedObject, locale);
                if (selectedObject instanceof OlapHierarchy)      showHierarchy((OlapHierarchy)selectedObject, locale);
                if (selectedObject instanceof OlapHierarchyLevel) showLevel((OlapHierarchyLevel)selectedObject, locale);

                if (selectedObject instanceof OlapCube)           showCube((OlapCube)selectedObject, locale);
            }
            else
            {
                showEmpty();
            }
        }
        
        compDynamic.layout(true, true);
    }


    private void showEmpty()
    {
        Button addDimension = new Button(compDynamic, SWT.PUSH);
        addDimension.setText("Add a new public dimension");
        addDimension.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newDimension(); } });
        FormData fdDimension = new FormData();
        fdDimension.left = new FormAttachment(0,0);
        fdDimension.top  = new FormAttachment(0,0);
        addDimension.setLayoutData(fdDimension);
        
        Button addCube = new Button(compDynamic, SWT.PUSH);
        addCube.setText("Add a new cube");
        addCube.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newCube(); } });
        FormData fdCube = new FormData();
        fdCube.left = new FormAttachment(addDimension, Const.MARGIN*5);
        fdCube.top  = new FormAttachment(0,0);
        addCube.setLayoutData(fdCube);

    }

    private void showDimension(final OlapDimension dimension, final String locale)
    {
        final LabelText name = new LabelText(compDynamic, "Dimension name", "Enter the dimension name");
        name.setText(dimension.getName());
        
        Button apply = new Button(compDynamic, SWT.PUSH);
        apply.setText("apply name change");
        apply.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) 
                { 
                    dimension.setName(name.getText());
                    refreshScreen();
                    selectTreeItem(new String[] { STRING_DIMENSIONS, dimension.getName() });
                } 
            } 
        );

        FormData fdApply = new FormData();
        fdApply.right = new FormAttachment(100,0);
        fdApply.top = new FormAttachment(name, 0, SWT.CENTER);
        apply.setLayoutData(fdApply);

        FormData fdName = new FormData();
        fdName.left = new FormAttachment(0,0);
        fdName.right = new FormAttachment(apply, -margin);
        fdName.top = new FormAttachment(0,0);
        name.setLayoutData(fdName);

        Button add = new Button(compDynamic, SWT.PUSH);
        add.setText("Add a new hierarchy");
        add.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newHierarchy(dimension, locale); } });
        FormData fdAdd = new FormData();
        fdAdd.left = new FormAttachment(0,0);
        fdAdd.top  = new FormAttachment(name, 10*margin);
        add.setLayoutData(fdAdd);
        
        Button remove = new Button(compDynamic, SWT.PUSH);
        remove.setText("Remove this dimension");
        remove.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeDimension(dimension); } });
        FormData fdRemove = new FormData();
        fdRemove.left = new FormAttachment(add, 2*margin);
        fdRemove.top  = new FormAttachment(name, 10*margin);
        remove.setLayoutData(fdRemove);
        
    }

    private void showHierarchy(final OlapHierarchy hierarchy, final String locale)
    {
        // The name of the hierarchy
        Label nameLabel = new Label(compDynamic, SWT.RIGHT);
        props.setLook(nameLabel);
        nameLabel.setText("Hierarchy name");
        FormData fdNameLabel = new FormData();
        fdNameLabel.left = new FormAttachment(0,0);
        fdNameLabel.right = new FormAttachment(middle, 0);
        fdNameLabel.top = new FormAttachment(0,0);
        nameLabel.setLayoutData(fdNameLabel);
        
        final Text name = new Text(compDynamic, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
        name.setToolTipText("Enter the hierarchy name");
        name.setText(hierarchy.getName());
        
        Button apply = new Button(compDynamic, SWT.PUSH);
        apply.setText("apply name change");
        apply.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) 
                { 
                    hierarchy.setName(name.getText()); 
                    refreshScreen();
                    selectTreeItem(new String[] { STRING_DIMENSIONS, hierarchy.getOlapDimension().getName(), hierarchy.getName() });
                } 
            } 
        );
        
        FormData fdApply = new FormData();
        fdApply.right = new FormAttachment(100,0);
        fdApply.top = new FormAttachment(name,0,SWT.CENTER);
        apply.setLayoutData(fdApply);

        FormData fdName = new FormData();
        fdName.left = new FormAttachment(middle, margin);
        fdName.right = new FormAttachment(apply, -margin);
        fdName.top = new FormAttachment(0,0);
        name.setLayoutData(fdName);


        // The name of the business table we link to
        Label tableLabel = new Label(compDynamic, SWT.RIGHT);
        props.setLook(tableLabel);
        tableLabel.setText("Table name");
        FormData fdTableLabel = new FormData();
        fdTableLabel.left = new FormAttachment(0,0);
        fdTableLabel.right = new FormAttachment(middle, 0);
        fdTableLabel.top = new FormAttachment(name, margin);
        tableLabel.setLayoutData(fdTableLabel);

        final Text table = new Text(compDynamic, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
        props.setLook(table);
        table.setToolTipText("This is the selected business table name");
        table.setText(hierarchy.getName());
        BusinessTable businessTable = hierarchy.getBusinessTable();
        if (businessTable!=null) table.setText(businessTable.getDisplayName(locale)+" : "+businessTable.getTargetTable());
        table. setEditable(false);
        FormData fdTable = new FormData();
        fdTable.left = new FormAttachment(middle, margin);
        fdTable.right = new FormAttachment(apply, -margin);
        fdTable.top = new FormAttachment(name, margin);
        table.setLayoutData(fdTable);
        
        // The name of the business column that is the primary key 
        Label keyLabel = new Label(compDynamic, SWT.RIGHT);
        props.setLook(keyLabel);
        keyLabel.setText("Primary key column");
        FormData fdKeyLabel = new FormData();
        fdKeyLabel.left = new FormAttachment(0,0);
        fdKeyLabel.right = new FormAttachment(middle, 0);
        fdKeyLabel.top = new FormAttachment(table, margin);
        keyLabel.setLayoutData(fdKeyLabel);

        final Text key = new Text(compDynamic, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
        props.setLook(key);
        BusinessColumn primaryKey = hierarchy.getPrimaryKey();
        key.setText(primaryKey.getDisplayName(locale)+" : "+primaryKey.getFunctionTableAndColumnForSQL(locale));
        key.setToolTipText("This is the selected primary key column");
        key.setEditable(false);
        FormData fdKey = new FormData();
        fdKey.left = new FormAttachment(middle, margin);
        fdKey.right = new FormAttachment(apply, -margin);
        fdKey.top = new FormAttachment(table, margin);
        key.setLayoutData(fdKey);
        
        // The has all flag?
        Label allLabel = new Label(compDynamic, SWT.RIGHT);
        allLabel.setText("Has all?");
        props.setLook(allLabel);
        FormData fdAllLabel = new FormData();
        fdAllLabel.left = new FormAttachment(0,0);
        fdAllLabel.right = new FormAttachment(middle, 0);
        fdAllLabel.top = new FormAttachment(key, margin);
        allLabel.setLayoutData(fdAllLabel);
        
        final Button all = new Button(compDynamic, SWT.CHECK );
        all.setSelection(hierarchy.isHavingAll());
        props.setLook(all);
        FormData fdAll = new FormData();
        fdAll.left = new FormAttachment(middle, margin);
        fdAll.right = new FormAttachment(100, 0);
        fdAll.top = new FormAttachment(key, margin);
        all.setLayoutData(fdAll);
        all.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { hierarchy.setHavingAll(all.getSelection()); } });
        
        // Buttons at the bottom...
        Button add = new Button(compDynamic, SWT.PUSH);
        add.setText("Add a new hierarchy level");
        add.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newLevel(hierarchy, locale); } });
        FormData fdAdd = new FormData();
        fdAdd.left = new FormAttachment(0,0);
        fdAdd.top  = new FormAttachment(all, 10*margin);
        add.setLayoutData(fdAdd);
        
        Button remove = new Button(compDynamic, SWT.PUSH);
        remove.setText("Remove this hierarchy");
        remove.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeHierarchy(hierarchy); } });
        FormData fdRemove = new FormData();
        fdRemove.left = new FormAttachment(add, 2*margin);
        fdRemove.top  = new FormAttachment(all, 10*margin);
        remove.setLayoutData(fdRemove);
    }
    

    private void showLevel(final OlapHierarchyLevel level, final String locale)
    {
        Label nameLabel = new Label(compDynamic, SWT.RIGHT);
        props.setLook(nameLabel);
        nameLabel.setText("Hierarchy level name");
        FormData fdNameLabel = new FormData();
        fdNameLabel.left = new FormAttachment(0,0);
        fdNameLabel.right = new FormAttachment(middle, 0);
        fdNameLabel.top = new FormAttachment(0,0);
        nameLabel.setLayoutData(fdNameLabel);
        
        final Text name = new Text(compDynamic, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
        props.setLook(name);
        name.setToolTipText("Enter the hierarchy level name");
        name.setText(level.getName());

        Button apply = new Button(compDynamic, SWT.PUSH);
        apply.setText("apply name change");
        apply.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) 
                { 
                    level.setName(name.getText()); 
                    refreshScreen();
                    selectTreeItem(new String[] { STRING_DIMENSIONS, level.getOlapHierarchy().getOlapDimension().getName(), level.getOlapHierarchy().getName(), level.getName() });
                } 
            } 
        );

        FormData fdApply = new FormData();
        fdApply.right = new FormAttachment(100,0);
        fdApply.top = new FormAttachment(name, 0, SWT.CENTER);
        apply.setLayoutData(fdApply);

        FormData fdName = new FormData();
        fdName.left = new FormAttachment(middle, margin);
        fdName.right = new FormAttachment(apply, -margin);
        fdName.top = new FormAttachment(0,0);
        name.setLayoutData(fdName);

        
        // The name of the business column we link to
        Label refColumnLabel = new Label(compDynamic, SWT.RIGHT);
        props.setLook(refColumnLabel);
        refColumnLabel.setText("Reference column name");
        FormData fdRefColumnLabel = new FormData();
        fdRefColumnLabel.left = new FormAttachment(0,0);
        fdRefColumnLabel.right = new FormAttachment(middle, 0);
        fdRefColumnLabel.top = new FormAttachment(name, margin);
        refColumnLabel.setLayoutData(fdRefColumnLabel);

        final Text refColumn = new Text(compDynamic, SWT.BORDER | SWT.LEFT | SWT.SINGLE);
        props.setLook(refColumn);
        BusinessColumn referenceColumn = level.getReferenceColumn(); 
        refColumn.setText(referenceColumn.getDisplayName(locale)+" : "+referenceColumn.getFunctionTableAndColumnForSQL(locale));
        refColumn.setEditable(false);
        refColumn.setToolTipText("This is the reference column name");
        FormData fdRefColumn = new FormData();
        fdRefColumn.left = new FormAttachment(middle, margin);
        fdRefColumn.right = new FormAttachment(apply, -margin);
        fdRefColumn.top = new FormAttachment(name, margin);
        refColumn.setLayoutData(fdRefColumn);
        
        // The unique members flag
        Label uniqueLabel = new Label(compDynamic, SWT.RIGHT);
        uniqueLabel.setText("Unique members?");
        props.setLook(uniqueLabel);
        FormData fdUniqueLabel = new FormData();
        fdUniqueLabel.left = new FormAttachment(0,0);
        fdUniqueLabel.right = new FormAttachment(middle, 0);
        fdUniqueLabel.top = new FormAttachment(refColumn, margin);
        uniqueLabel.setLayoutData(fdUniqueLabel);
        
        final Button unique = new Button(compDynamic, SWT.CHECK );
        unique.setSelection(level.isHavingUniqueMembers());
        props.setLook(unique);
        FormData fdUnique = new FormData();
        fdUnique.left = new FormAttachment(uniqueLabel, margin);
        fdUnique.right = new FormAttachment(apply, -margin);
        fdUnique.top = new FormAttachment(refColumn, margin);
        unique.setLayoutData(fdUnique);
        unique.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { level.setHavingUniqueMembers(unique.getSelection()); } });

        // The list of columns
        Label columnsLabel = new Label(compDynamic, SWT.RIGHT);
        columnsLabel.setText("List of columns: ");
        props.setLook(columnsLabel);
        FormData fdColumnsLabel = new FormData();
        fdColumnsLabel.left = new FormAttachment(0,0);
        fdColumnsLabel.right = new FormAttachment(middle, 0);
        fdColumnsLabel.top = new FormAttachment(unique, margin);
        columnsLabel.setLayoutData(fdColumnsLabel);
        
        final org.eclipse.swt.widgets.List columns = new org.eclipse.swt.widgets.List(compDynamic, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        props.setLook(unique);
        List cols = level.getBusinessColumns();
        for (int i=0;i<cols.size();i++)
        {
            BusinessColumn col = (BusinessColumn) cols.get(i);
            columns.add(col.getDisplayName(locale) );
        }
        FormData fdColumns = new FormData();
        fdColumns.left   = new FormAttachment(middle, margin);
        fdColumns.right  = new FormAttachment(apply, -margin);
        fdColumns.top    = new FormAttachment(unique, margin);
        fdColumns.bottom = new FormAttachment(unique, margin+200);
        columns.setLayoutData(fdColumns);
        
        
        
        // Buttons at the bottom
        Button add = new Button(compDynamic, SWT.PUSH);
        add.setText("Add business columns to this level");
        add.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { addColumns(level, locale); } });
        FormData fdAdd = new FormData();
        fdAdd.left = new FormAttachment(0,0);
        fdAdd.top  = new FormAttachment(columns, 10*margin);
        add.setLayoutData(fdAdd);

        Button del = new Button(compDynamic, SWT.PUSH);
        del.setText("remove business columns from this level");
        del.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { delColumns(level, columns, locale); } });
        FormData fdDel = new FormData();
        fdDel.left = new FormAttachment(add, 2*margin);
        fdDel.top  = new FormAttachment(columns, 10*margin);
        del.setLayoutData(fdDel);

        Button remove = new Button(compDynamic, SWT.PUSH);
        remove.setText("Remove this hierarchy level");
        remove.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeLevel(level); } });
        FormData fdRemove = new FormData();
        fdRemove.left = new FormAttachment(del, 2*margin);
        fdRemove.top  = new FormAttachment(columns, 10*margin);
        remove.setLayoutData(fdRemove);

    }

    private void showCube(final OlapCube olapCube, final String locale)
    {
        final LabelText name = new LabelText(compDynamic, "Cube name", "Enter the cube name");
        name.setText(olapCube.getName());

        Button apply = new Button(compDynamic, SWT.PUSH);
        apply.setText("apply name change");
        apply.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) 
                { 
                    olapCube.setName(name.getText()); 
                    refreshScreen();
                    selectTreeItem(new String[] { STRING_CUBES, olapCube.getName() });
                } 
            } 
        );

        FormData fdApply = new FormData();
        fdApply.right = new FormAttachment(100,0);
        fdApply.top = new FormAttachment(name, 0, SWT.CENTER);
        apply.setLayoutData(fdApply);

        FormData fdName = new FormData();
        fdName.left = new FormAttachment(0,0);
        fdName.right = new FormAttachment(apply, -margin);
        fdName.top = new FormAttachment(0,0);
        name.setLayoutData(fdName);



        // The name of the business table we link to
        final LabelText table = new LabelText(compDynamic, "Table name", "This is the selected business table name");
        props.setLook(table.getTextWidget());
        BusinessTable businessTable = olapCube.getBusinessTable();
        table.setText(businessTable.getDisplayName(locale)+" : "+businessTable.getTargetTable());
        table.getTextWidget().setEditable(false);
        FormData fdTable = new FormData();
        fdTable.left = new FormAttachment(0,0);
        fdTable.right = new FormAttachment(apply, -margin);
        fdTable.top = new FormAttachment(name, margin);
        table.setLayoutData(fdTable);

        Button addDimension = new Button(compDynamic, SWT.PUSH);
        addDimension.setText("Link dimensions");
        FormData fdAddDimension = new FormData();
        fdAddDimension.left = new FormAttachment(0,0);
        fdAddDimension.top  = new FormAttachment(table, 10*margin);
        addDimension.setLayoutData(fdAddDimension);
        
        Button removeDimension = new Button(compDynamic, SWT.PUSH);
        removeDimension.setText("Remove dimensions");
        FormData fdRemoveDimension = new FormData();
        fdRemoveDimension.left = new FormAttachment(addDimension, 2*margin);
        fdRemoveDimension.top  = new FormAttachment(table, 10*margin);
        removeDimension.setLayoutData(fdRemoveDimension);

        Button addMeasure = new Button(compDynamic, SWT.PUSH);
        addMeasure.setText("Add measure");
        FormData fdAddMeasure = new FormData();
        fdAddMeasure.left = new FormAttachment(50,0);
        fdAddMeasure.top  = new FormAttachment(table, 10*margin);
        addMeasure.setLayoutData(fdAddMeasure);
        
        Button removeMeasure = new Button(compDynamic, SWT.PUSH);
        removeMeasure.setText("Remove Measures");
        FormData fdRemoveMeasure = new FormData();
        fdRemoveMeasure.left = new FormAttachment(addMeasure, 2*margin);
        fdRemoveMeasure.top  = new FormAttachment(table, 10*margin);
        removeMeasure.setLayoutData(fdRemoveMeasure);

        // For this cube, we can show 2 lists: one for the used dimensions and one for the measures
        //
        ColumnInfo[] dimensionColumns = new ColumnInfo[]
             {
               new ColumnInfo("Dimension name",        ColumnInfo.COLUMN_TYPE_TEXT, false, true),
             };
        final TableView dimensions = new TableView(compDynamic, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, dimensionColumns, 1, true, null, props );
        FormData fdDimensions = new FormData();
        fdDimensions.left   = new FormAttachment(0,0);
        fdDimensions.right  = new FormAttachment(30, 0);
        fdDimensions.top    = new FormAttachment(addDimension, 5*Const.MARGIN);
        fdDimensions.bottom = new FormAttachment(addDimension, 5*Const.MARGIN+300);
        dimensions.setLayoutData(fdDimensions);
        
        ColumnInfo[] measureColumns = new ColumnInfo[]
           {
             new ColumnInfo("Measure name",         ColumnInfo.COLUMN_TYPE_TEXT, false, true),
             new ColumnInfo("column name",          ColumnInfo.COLUMN_TYPE_TEXT, false, true),
             new ColumnInfo("formula (column)",     ColumnInfo.COLUMN_TYPE_TEXT, false, true),
           };
        final TableView measures = new TableView(compDynamic, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, measureColumns, 1, true, null, props );
        FormData fdMeasures = new FormData();
        fdMeasures.left   = new FormAttachment(30, Const.MARGIN);
        fdMeasures.right  = new FormAttachment(100, 0);
        fdMeasures.top    = new FormAttachment(addMeasure, 5*Const.MARGIN);
        fdMeasures.bottom = new FormAttachment(addMeasure, 5*Const.MARGIN+300);
        measures.setLayoutData(fdMeasures);
        
        Button removeCube = new Button(compDynamic, SWT.PUSH);
        removeCube.setText("Remove this cube");
        FormData fdRemoveCube = new FormData();
        fdRemoveCube.left = new FormAttachment(0,0);
        fdRemoveCube.top  = new FormAttachment(dimensions, 10*margin);
        removeCube.setLayoutData(fdRemoveCube);

        addDimension.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newCubeDimension(olapCube, locale); } });
        removeDimension.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeCubeDimension(olapCube, dimensions, locale); } });
        addMeasure.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newCubeMeasure(olapCube, locale);  } });
        removeMeasure.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeCubeMeasure(olapCube, measures, locale); } });
        removeCube.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { removeCube(olapCube); } });
        
        refreshCubeDimensions(olapCube, dimensions, locale);
        refreshCubeMeasures(olapCube, measures, locale);
    }


    protected void newCubeDimension(OlapCube cube, String locale)
    {
        // Pick one from the available dimensions...
        BusinessView activeView = metaEditor.getSchemaMeta().getActiveView();
        List dimensions = activeView.getOlapDimensions();
        List usages = cube.getOlapDimensionUsages();
        
        // Get a list of unused shared/public olap dimensions
        List unUsed = new ArrayList();
        unUsed.addAll(dimensions);
        
        for (int u=0;u<usages.size();u++)
        {
            OlapDimensionUsage usage = (OlapDimensionUsage) usages.get(u);
            OlapDimension dimension = usage.getOlapDimension();
            int idx = unUsed.indexOf(dimension);
            if (idx>=0) unUsed.remove(idx);
        }
        
        // Remove the fact table as well (cube table)
        //
        for (int u=0;u<usages.size();u++)
        {
            OlapDimensionUsage usage = (OlapDimensionUsage) usages.get(u);
            OlapDimension olapDimension = usage.getOlapDimension();
            List hierarchies = olapDimension.getHierarchies();
            for (int h=0;h<hierarchies.size();h++)
            {
                OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get(h);
                if (hierarchy.getBusinessTable().equals( cube.getBusinessTable() ))
                {
                    unUsed.remove(u);
                }
            }
        }
        
        // OK, now that we have the list of unused dimensions, let the user selection one.
        String dimensionNames[] = new String[unUsed.size()];
        for (int i=0;i<dimensionNames.length;i++) dimensionNames[i] = ((OlapDimension)unUsed.get(i)).getName();
        
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, dimensionNames, "Select the dimension", "Select the dimension(s) to add to the cube:");
        dialog.setMulti(true);
        if (dialog.open()!=null)
        {
            int[] indices = dialog.getSelectionIndeces();
            for (int i=0;i<indices.length;i++)
            {
                OlapDimension olapDimension = (OlapDimension) unUsed.get(indices[i]);
                
                // Create new usage
                OlapDimensionUsage usage = new OlapDimensionUsage(olapDimension.getName(), olapDimension);
                cube.getOlapDimensionUsages().add(usage);
            }
            
            refreshScreen();
            
            String path[] = new String[] { STRING_CUBES, cube.getName() };
            selectTreeItem(path);
        }

    }

    protected void removeCubeDimension(OlapCube cube, TableView dimensions, String locale)
    {
        int[] indices = dimensions.table.getSelectionIndices();
        OlapDimensionUsage usage[] = new OlapDimensionUsage[indices.length];
        for (int i=0;i<indices.length;i++) usage[i] = (OlapDimensionUsage) cube.getOlapDimensionUsages().get(indices[i]);
        for (int i=0;i<indices.length;i++)
        {
            int idx = cube.getOlapDimensionUsages().indexOf(usage[i]);
            if (idx>=0)
            {
                cube.getOlapDimensionUsages().remove(idx);
                cube.setChanged();
            }
        }
        refreshScreen();
        String path[] = new String[] { STRING_CUBES, cube.getName() };
        selectTreeItem(path);
    }

    protected void newCubeMeasure(OlapCube cube, String locale)
    {
        String columnNames[] = cube.getUnusedColumnNames(locale);
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, columnNames, "Select measure", "Select the column(s) to add as a measure:");
        dialog.setMulti(true);
        if (dialog.open()!=null)
        {
            int[] indices = dialog.getSelectionIndeces();
            for (int i=0;i<indices.length;i++)
            {
                String columnName = columnNames[indices[i]];
                
                BusinessColumn businessColumn = cube.getBusinessTable().findBusinessColumn(locale, columnName);
                OlapMeasure measure = new OlapMeasure();
                measure.setName(businessColumn.getDisplayName(locale));
                measure.setBusinessColumn(businessColumn);

                // Add it to the cube
                cube.getOlapMeasures().add(measure);
            }
            
            refreshScreen();
            
            String path[] = new String[] { STRING_CUBES, cube.getName() };
            selectTreeItem(path);
        }
    }

    protected void removeCubeMeasure(OlapCube cube, TableView measures, String locale)
    {
        int[] indices = measures.table.getSelectionIndices();
        OlapMeasure measure[] = new OlapMeasure[indices.length];
        for (int i=0;i<indices.length;i++) measure[i] = (OlapMeasure) cube.getOlapMeasures().get(indices[i]);
        for (int i=0;i<indices.length;i++)
        {
            int idx = cube.getOlapMeasures().indexOf(measure[i]);
            if (idx>=0) cube.getOlapMeasures().remove(idx);
        }
        refreshScreen();
        String path[] = new String[] { STRING_CUBES, cube.getName() };
        selectTreeItem(path);
    }

    protected void refreshCubeDimensions(OlapCube cube, TableView dimensions, String locale)
    {
        dimensions.clearAll(false);
        
        List usages = cube.getOlapDimensionUsages();
        for (int i=0;i<usages.size();i++)
        {
            OlapDimensionUsage usage = (OlapDimensionUsage) usages.get(i);
            TableItem tableItem = new TableItem(dimensions.table, SWT.NONE);
            tableItem.setText(1, usage.getName());
        }
        
        dimensions.removeEmptyRows();
        dimensions.setRowNums();
        dimensions.optWidth(true);
    }

    protected void refreshCubeMeasures(OlapCube cube, TableView measures, String locale)
    {
        measures.clearAll(false);
        
        List olapMeasures = cube.getOlapMeasures();
        for (int i=0;i<olapMeasures.size();i++)
        {
            OlapMeasure measure = (OlapMeasure) olapMeasures.get(i);
            BusinessColumn column = measure.getBusinessColumn();
            
            TableItem tableItem = new TableItem(measures.table, SWT.NONE);
            tableItem.setText(1, measure.getName());
            tableItem.setText(2, column.getDisplayName(locale));
            tableItem.setText(3, column.getFormula());
        }
        measures.removeEmptyRows();
        measures.setRowNums();
        measures.optWidth(true);
    }

    protected void setMenu(SelectionEvent e)
    {
        final TreeItem treeItem = (TreeItem)e.item;

        if (menu==null)
        {
            menu = new Menu(shell, SWT.POP_UP);
        }
        else
        {
            MenuItem items[] = menu.getItems();
            for (int i=0;i<items.length;i++) items[i].dispose();
        }
        
        final String itemText = treeItem.getText();
        final String[] path = Const.getTreeStrings(treeItem);

        if (path.length==1 && itemText.equals(STRING_DIMENSIONS))
        {
            MenuItem miNew = new MenuItem(menu, SWT.NONE);
            miNew.setText("New");
            miNew.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newDimension(); } } );
        }
        
        wTree.setMenu(menu);
    }

    protected void newDimension()
    {
        EnterStringDialog dialog = new EnterStringDialog(shell, "", "New dimension", "Enter the name of the new dimension:");
        String dimensionName = dialog.open();
        if (dimensionName!=null)
        {
            OlapDimension dimension = new OlapDimension();
            dimension.setName(dimensionName);
            dimension.setChanged();
            
            // Add it to the business view
            metaEditor.getSchemaMeta().getActiveView().getOlapDimensions().add(dimension);
            String path[] = new String[] { STRING_DIMENSIONS };
            TreeMemory.getInstance().storeExpanded(STRING_OLAP_TREE, path, true);
            path = new String[] { STRING_DIMENSIONS, dimensionName };
            TreeMemory.getInstance().storeExpanded(STRING_OLAP_TREE, path, true);
            refreshScreen();  
            selectTreeItem(path);
        }
    }
    
    protected void removeDimension(OlapDimension dimension)
    {
        BusinessView activeView = metaEditor.getSchemaMeta().getActiveView();
        int index = activeView.getOlapDimensions().indexOf(dimension);
        if (index>=0)
        {
            activeView.getOlapDimensions().remove(index);
            activeView.setChanged();
        }
        refreshScreen();
    }

    protected void removeCube(OlapCube olapCube)
    {
        BusinessView activeView = metaEditor.getSchemaMeta().getActiveView();
        int index = activeView.getOlapCubes().indexOf(olapCube);
        if (index>=0)
        {
            activeView.getOlapCubes().remove(index);
            activeView.setChanged();
        }
        refreshScreen();
    }

    protected void newHierarchy(OlapDimension olapDimension, String locale)
    {
        EnterStringDialog nameDialog = new EnterStringDialog(shell, "", "New hierarchy", "Enter the name of the new hierarchy");
        String hierarchyName = nameDialog.open();
        if (hierarchyName!=null)
        {
            // Pick one from the available tables...
            //
            BusinessView activeView = metaEditor.getSchemaMeta().getActiveView();
            
            String tableNames[] = activeView.getBusinessTableNames(locale);
            EnterSelectionDialog dialog = new EnterSelectionDialog(shell, tableNames, "New Hierarchy", "Select the table to create the hierarchy for");
            String tableName = dialog.open();
            if (tableName!=null)
            {
                // Ask about the primary key for this hierarchy...
                BusinessTable businessTable = activeView.findBusinessTable(locale, tableName);
                String columnNames[] = businessTable.getColumnNames(locale);
    
                EnterSelectionDialog keyDialog = new EnterSelectionDialog(shell, columnNames, "Select primary key", "Select the primary key column for this hierarchy:");
                String columnName = keyDialog.open();
                if (columnName!=null)
                {
                    OlapHierarchy hierarchy = new OlapHierarchy(olapDimension);
                    hierarchy.setName(hierarchyName);
                    hierarchy.setBusinessTable(businessTable);
                    hierarchy.setPrimaryKey(businessTable.findBusinessColumn(locale, columnName));
                    
                    // Add it to the dimension
                    olapDimension.getHierarchies().add(hierarchy);
                    olapDimension.setChanged();
                    
                    String path[] = new String[] { STRING_DIMENSIONS, olapDimension.getName() };
                    TreeMemory.getInstance().storeExpanded(STRING_OLAP_TREE, path, true);
                    refreshScreen();
                    selectTreeItem(path);
                }
            }
        }
    }
    
    protected void removeHierarchy(OlapHierarchy olapHierarchy)
    {
        OlapDimension olapDimension = olapHierarchy.getOlapDimension();
        int index = olapDimension.getHierarchies().indexOf(olapHierarchy);
        if (index>=0)
        {
            olapDimension.getHierarchies().remove(index);
            olapDimension.setChanged();
        }
        String[] path = new String[] { STRING_DIMENSIONS, olapHierarchy.getOlapDimension().getName(), olapHierarchy.getName() };
        refreshScreen();
        selectTreeItem(path);
    }



    protected void newLevel(OlapHierarchy olapHierarchy, String locale)
    {
        String columnNames[] = olapHierarchy.getUnusedColumnNames(locale);
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, columnNames, "Select column", "Select the column to base this level on:");
        String columnName = dialog.open();
        if (columnName!=null)
        {
            BusinessColumn businessColumn = olapHierarchy.getBusinessTable().findBusinessColumn(locale, columnName);
            OlapHierarchyLevel level = new OlapHierarchyLevel(olapHierarchy);
            level.setName(businessColumn.getDisplayName(locale));
            level.setReferenceColumn(businessColumn);

            // Add it to the hierarchy
            olapHierarchy.getHierarchyLevels().add(level);
            OlapDimension dimension = olapHierarchy.getOlapDimension();
            
            // If this level is the first level that gets added to the hierarchy, enable the uniqueMembers property, said Julian
            if (olapHierarchy.getHierarchyLevels().size()==1)
            {
                level.setHavingUniqueMembers(true);
            }
            
            olapHierarchy.setChanged();
            
            String path[] = new String[] { STRING_DIMENSIONS, dimension.getName(), olapHierarchy.getName() };
            TreeMemory.getInstance().storeExpanded(STRING_OLAP_TREE, path, true);
            refreshScreen();      
            selectTreeItem(path);
        }
    }
    


    protected void removeLevel(OlapHierarchyLevel level)
    {
        OlapHierarchy olapHierarchy = level.getOlapHierarchy();
        int index = olapHierarchy.getHierarchyLevels().indexOf(level);
        if (index>=0)
        {
            olapHierarchy.getHierarchyLevels().remove(index);
            olapHierarchy.setChanged();
        }
        
        String[] path = new String[] { STRING_DIMENSIONS, level.getOlapHierarchy().getOlapDimension().getName(), level.getOlapHierarchy().getName() };
        refreshScreen();
        selectTreeItem(path);
    }

    protected void addColumns(OlapHierarchyLevel level, String locale)
    {
        // What's the business table to add columns from?
        //
        OlapHierarchy hierarchy = level.getOlapHierarchy();
        OlapDimension dimension = hierarchy.getOlapDimension();
        BusinessTable businessTable = hierarchy.getBusinessTable();
        String columnNames[] = hierarchy.getUnusedColumnNames(locale);
        
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, columnNames, "New column", "Enter the name of the new column");
        dialog.setMulti(true);
        if (dialog.open()!=null)
        {
            int[] indeces = dialog.getSelectionIndeces();
            for (int i=0;i<indeces.length;i++)
            {
                String columnName = columnNames[indeces[i]];
                BusinessColumn column = businessTable.findBusinessColumn(locale, columnName);
                level.getBusinessColumns().add(column);
                level.setChanged();
            }
            
            String[] path = new String[] { STRING_DIMENSIONS, dimension.getName(), hierarchy.getName(), level.getName() };
            TreeMemory.getInstance().storeExpanded(STRING_OLAP_TREE, path, true);
            refreshScreen();
            selectTreeItem(path);
        }
    }
    
    protected void delColumns(OlapHierarchyLevel level, org.eclipse.swt.widgets.List columns, String locale)
    {
        int[] indices = columns.getSelectionIndices();
        BusinessColumn[] businessColumns = new BusinessColumn[indices.length];
        for (int i=0;i<indices.length;i++) businessColumns[i] = (BusinessColumn) level.getBusinessColumns().get(indices[i]);
        for (int i=0;i<indices.length;i++)
        {
            int index = level.getBusinessColumns().indexOf(businessColumns[i]);
            if (index>=0)
            {
                level.getBusinessColumns().remove(index);
                level.setChanged();
            }
        }
        OlapHierarchy hierarchy = level.getOlapHierarchy();
        OlapDimension dimension = hierarchy.getOlapDimension();
        String[] path = new String[] { STRING_DIMENSIONS, dimension.getName(), hierarchy.getName(), level.getName() };
        refreshScreen();
        selectTreeItem(path);
    }


    protected void newCube()
    {
        // Pick one from the available tables...
        BusinessView activeView = metaEditor.getSchemaMeta().getActiveView();
        String locale = metaEditor.getSchemaMeta().getActiveLocale();
        
        String tableNames[] = activeView.getBusinessTableNames(locale);
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, tableNames, "New cube", "Select the base table to create the cube from");
        String cubeName = dialog.open();
        if (cubeName!=null)
        {
            OlapCube cube = new OlapCube();
            cube.setName(cubeName);
            
            // Set the table-name too
            cube.setBusinessTable(activeView.findBusinessTable(locale, cubeName));
            cube.setChanged();
            
            // Add it to the business view
            metaEditor.getSchemaMeta().getActiveView().getOlapCubes().add(cube);
            String path[] = new String[] { STRING_CUBES };
            TreeMemory.getInstance().storeExpanded(STRING_OLAP_TREE, path, true);
            path = new String[] { STRING_CUBES, cubeName };
            TreeMemory.getInstance().storeExpanded(STRING_OLAP_TREE, path, true);
            refreshScreen();  
            selectTreeItem(path);
        }
    }
    
    
    
    
	private void selectTreeItem(String[] path)
    {
        TreeItem treeItem ;
        if (path[0].equals(STRING_DIMENSIONS))
        {
            treeItem = tiDimensions;
        }
        else
        {
            treeItem = tiCubes;
        }
        for (int p=1;p<path.length;p++)
        {
            TreeItem[] items = treeItem.getItems();
            boolean found=false;
            for (int i=0;i<items.length && !found;i++)
            {
                if (items[i].getText().equals(path[p]))
                {
                    found=true;
                    treeItem = items[i];
                }
            }
            if (!found) return;
        }
        wTree.setSelection(treeItem);
    }

    public String toString()
	{
		return "MetaEditorOlap";
	}
    
    public void refreshScreen()
    {
        tiDimensions.removeAll();
        tiCubes.removeAll();
        
        SchemaMeta schemaMeta = metaEditor.getSchemaMeta();
        BusinessView activeView = schemaMeta.getActiveView();
        String locale = schemaMeta.getActiveLocale();
        if (activeView!=null)
        {
            viewLabel.setText("Active view: "+activeView.getDisplayName(locale));
            
            // refresh the tree on the left...
            refreshTree(activeView, locale);
        }
        else
        {
            viewLabel.setText("No view selected.");
        }
        
        metaEditor.setShellText(); // changed flag.
        showDynamicComposite();
    }

    private void refreshTree(BusinessView activeView, String locale)
    {
        List dimensions = activeView.getOlapDimensions();
        for (int d=0;d<dimensions.size();d++)
        {
            OlapDimension dimension = (OlapDimension) dimensions.get(d);
            TreeItem dimItem = new TreeItem(tiDimensions, SWT.NONE);
            dimItem.setText(dimension.getName());
            
            // now do the hierarchies
            List hierarchies = dimension.getHierarchies();
            for (int h=0;h<hierarchies.size();h++)
            {
                OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get(h);
                TreeItem hierarchyItem = new TreeItem(dimItem, SWT.NONE);
                hierarchyItem.setText(hierarchy.getName());
                
                // Now add the levels
                List levels = hierarchy.getHierarchyLevels();
                for (int hl=0;hl<levels.size();hl++)
                {
                    OlapHierarchyLevel level = (OlapHierarchyLevel) levels.get(hl);
                    TreeItem levelItem = new TreeItem(hierarchyItem, SWT.NONE);
                    levelItem.setText(level.getName());
                    
                    // Now draw the colums...
                    List columns = level.getBusinessColumns();
                    for (int c=0;c<columns.size();c++)
                    {
                        BusinessColumn column = (BusinessColumn) columns.get(c);
                        TreeItem columnItem = new TreeItem(levelItem, SWT.NONE);
                        columnItem.setText(column.getDisplayName(locale));
                        columnItem.setForeground(GUIResource.getInstance().getColorBlue());
                    }
                }
            }
        }
        
        List cubes = activeView.getOlapCubes();
        for (int c=0;c<cubes.size();c++)
        {
            OlapCube cube = (OlapCube) cubes.get(c);
            TreeItem cubeItem = new TreeItem(tiCubes, SWT.NONE);
            cubeItem.setText(cube.getName());
        }
        
        
        TreeMemory.setExpandedFromMemory(wTree, STRING_OLAP_TREE);
    }
    
    public Object getSelectedObject(BusinessView activeView, String locale)
    {
        if (wTree.getSelectionCount()==1)
        {
            TreeItem treeItem = wTree.getSelection()[0];
            String[] path = Const.getTreeStrings(treeItem);
            
            if (path[0].equals(STRING_DIMENSIONS))
            {
                OlapDimension      dimension = null;
                OlapHierarchy      hierarchy  = null;
                OlapHierarchyLevel level  = null;
                // BusinessColumn     column  = null;  Perhaps later we can show column information too.
                
                if (path.length>1) dimension = activeView.findOlapDimension(path[1]);
                if (path.length>2 && dimension!=null) hierarchy = dimension.findOlapHierarchy(path[2]);
                if (path.length>3 && hierarchy!=null) level = hierarchy.findOlapHierarchyLevel(path[3]);
                // if (path.length>4 && level!=null) column = level.findBusinessColumn(path[4], locale);
                
                switch(path.length)
                {
                case 1 : break; // the STRING_DIMENSIONS title
                case 2 : return dimension; // One of the defined dimensions
                case 3 : return hierarchy;  // One of the defined hierarchies
                case 4 : return level;  // One of the defined hierarchy levels
                case 5 : return level;  // a business column, return the level screen
                }
            }
            if (path[0].equals(STRING_CUBES))
            {
                OlapCube           cube = null;
                OlapMeasure        measure = null;
                
                if (path.length>1) cube = activeView.findOlapCube(path[1]);
                if (path.length>2 && cube!=null) measure = cube.findOlapMeasure(path[2]);
                
                switch(path.length)
                {
                case 1 : break; // the STRING_CUBES title
                case 2 : return cube; // One of the defined cubes
                case 3 : return measure;  // One of the defined measures
                }
            }
            
        }
        return null;
    }
}
