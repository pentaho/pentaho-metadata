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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.dialog.EnterStringDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TreeMemory;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.dialog.DialogGetDataInterface;
import org.pentaho.pms.schema.concept.dialog.EditConceptPropertyDialog;
import org.pentaho.pms.schema.concept.dialog.NewPropertyDialog;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;


/**
 * This class allows concepts in a model to be edited and linked to parents.
 * 
 * @since 3-okt-2006
 *
 */
public class MetaEditorConcepts extends Composite implements DialogGetDataInterface
{
    public static final String STRING_CONCEPTS = Messages.getString("MetaEditorConcepts.USER_CONCEPTS"); //$NON-NLS-1$
    private static final String STRING_CONCEPTS_TREE = "ConceptsTree";  //$NON-NLS-1$

    private PropsUI props;
	private Shell shell;
    
    private ConceptInterface activeConcept;
    private ConceptInterface changesConcept; 

    private SashForm   sashform;

    private Tree   wTree;
    
	private TreeItem tiConcepts;
    private Menu menu;
    private MetaEditor metaEditor;
    private Combo wParent;
    private Label wActive;
    private Text wName;
    private TableView wPProps;
    private TableView wProps;
    private LogWriter log;
    private Button wbParent;
    private ColumnInfo[] colProps;

    private Button wNew;
    private Button wApply;
    private Button wRevert;
    
    private Button newProp;
    private Button delProp;
    private Button editProp;
    private Button wDelete;
    private Button wReplace;

	public MetaEditorConcepts(Composite parent, int style, MetaEditor metaEditor)
	{
		super(parent, style);
        this.metaEditor = metaEditor;
		shell=parent.getShell();
        props = PropsUI.getInstance();
        log = LogWriter.getInstance();

        // Fill the complete tab with the sash-form.
		setLayout(new FillLayout());
        
        // First create the sash-form to create our tree on the left
        //
        sashform = new SashForm(this, SWT.HORIZONTAL);
        props.setLook(sashform);

        // Now create a tree on the left side of the screen
        //
        addTree();
        
        // Add the right side of the tab: a new composite 
        //
        addConcept();     
        
        sashform.setWeights(new int[] { 20, 80 } );
        sashform.setVisible(true);
				
        setVisible(true);
        enableFields();

		layout();
	}

    private void addTree()
    {
        // Main: the top left tree containing connections, physical tables, business models, etc.
        //
        Composite compMain = new Composite(sashform, SWT.NONE);
        FormLayout formLayout = new FormLayout();
        formLayout.spacing = Const.MARGIN;
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

        tiConcepts = new TreeItem(wTree, SWT.NONE); 
        tiConcepts.setText(STRING_CONCEPTS);
        
        tiConcepts.setExpanded(true);

        props.setLook(wTree);

        // Popup-menu: right click
        SelectionAdapter lsEditMainSel = new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { setMenu(e); } };
        wTree.addSelectionListener(lsEditMainSel);

        // Normal selection: left click to select business model
        SelectionListener lsSelModel = new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { setActiveContext(e); } };
        wTree.addSelectionListener(lsSelModel);
        
        // A key gets pressed...
        KeyAdapter lsKey = new KeyAdapter() { public void keyPressed(KeyEvent event) { keyPressedInTree(event); } };
        wTree.addKeyListener(lsKey);
        
        // Add tree memories to the trees.
        TreeMemory.addTreeListener(wTree, STRING_CONCEPTS_TREE);
    }

    protected void keyPressedInTree(KeyEvent event)
    {
        
    }

    private void addConcept()
    {
        int margin = Const.MARGIN;
        int middle = props.getMiddlePct();

        // composite for the right side of the screen
        //
        Composite compConcept = new Composite(sashform, SWT.NONE);
        props.setLook(compConcept);

        // The composite takes the whole right side of the sash form...
        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;
        compConcept.setLayout(formLayout);   
        
        FormData fdConcept= new FormData();
        fdConcept.left   = new FormAttachment(0,0);
        fdConcept.right  = new FormAttachment(100, 0);
        fdConcept.top    = new FormAttachment(0,0);
        fdConcept.bottom = new FormAttachment(100, 0);
        compConcept.setLayoutData(fdConcept);

        // The name of the active concept
        // 
        wActive = new Label(compConcept, SWT.LEFT );
        props.setLook(wActive);
        FormData fdActive = new FormData();
        fdActive.left  = new FormAttachment(middle, 2*margin);
        fdActive.right = new FormAttachment(100, 0);
        fdActive.top   = new FormAttachment(0, 0);
        wActive.setLayoutData(fdActive);

        Label wlActive = new Label(compConcept, SWT.RIGHT);
        props.setLook(wlActive);
        wlActive.setText(Messages.getString("MetaEditorConcepts.USER_ACTIVE_CONCEPT_NAME")); //$NON-NLS-1$
        FormData fdlActive = new FormData();
        fdlActive.left  = new FormAttachment(0,0);
        fdlActive.right = new FormAttachment(middle, 0);
        fdlActive.top   = new FormAttachment(wActive, 0, SWT.CENTER);
        wlActive.setLayoutData(fdlActive);

        // The name of this concept
        // 
        wName = new Text(compConcept, SWT.LEFT | SWT.BORDER);
        props.setLook(wName);
        FormData fdName = new FormData();
        fdName.left  = new FormAttachment(middle,  margin);
        fdName.right = new FormAttachment(100, 0);
        fdName.top   = new FormAttachment(wActive, 2*margin);
        wName.setLayoutData(fdName);

        Label wlName = new Label(compConcept, SWT.RIGHT);
        props.setLook(wlName);
        wlName.setText(Messages.getString("MetaEditorConcepts.USER_NEW_CONCEPT_NAME")); //$NON-NLS-1$
        FormData fdlName = new FormData();
        fdlName.left  = new FormAttachment(0,0);
        fdlName.right = new FormAttachment(middle, 0);
        fdlName.top   = new FormAttachment(wName, 0, SWT.CENTER);
        wlName.setLayoutData(fdlName);

        
        // Add the parent line
        //
        wbParent = new Button(compConcept, SWT.PUSH);
        props.setLook(wbParent);
        wbParent.setText(Messages.getString("MetaEditorConcepts.USER_GO_TO_PARENT")); //$NON-NLS-1$
        FormData fdbParent = new FormData();
        fdbParent.right = new FormAttachment(100, 0);
        fdbParent.top   = new FormAttachment(wName, margin);
        wbParent.setLayoutData(fdbParent);
        wbParent.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { showParent(); } } );
        
        Label wlParent = new Label(compConcept, SWT.RIGHT);
        props.setLook(wlParent);
        wlParent.setText(Messages.getString("MetaEditorConcepts.USER_PARENT_CONCEPT")); //$NON-NLS-1$
        FormData fdlParent = new FormData();
        fdlParent.left  = new FormAttachment(0,0);
        fdlParent.right = new FormAttachment(middle, 0);
        fdlParent.top   = new FormAttachment(wbParent, 0, SWT.CENTER);
        wlParent.setLayoutData(fdlParent);

        wParent = new Combo(compConcept, SWT.LEFT);
        props.setLook(wParent);
        FormData fdParent = new FormData();
        fdParent.left  = new FormAttachment(middle,  margin);
        fdParent.right = new FormAttachment(wbParent, -margin);
        fdParent.top   = new FormAttachment(wbParent, 0, SWT.CENTER);
        wParent.setLayoutData(fdParent);
        setParentCombo(null);
        wParent.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { displayParentInformation(); } } );

        
        // Show the parent properties in a grid...
        //
        Label wlPProps = new Label(compConcept, SWT.LEFT);
        props.setLook(wlPProps);
        wlPProps.setText(Messages.getString("MetaEditorConcepts.USER_PARENT_PROPERTIES")); //$NON-NLS-1$
        FormData fdlPProps = new FormData();
        fdlPProps.left  = new FormAttachment(0,0);
        fdlPProps.top   = new FormAttachment(wbParent, margin);
        wlPProps.setLayoutData(fdlPProps);

        ColumnInfo[] colPProps = new ColumnInfo[]
          {
            new ColumnInfo(Messages.getString("MetaEditorConcepts.USER_PROPERTY_ID"),           ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("MetaEditorConcepts.USER_PROPERTY_TYPE"),         ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("MetaEditorConcepts.USER_VALUE"),                 ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
          };
        wPProps=new TableView(new Variables(), compConcept, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colPProps, 1, true, null, props );
        FormData fdPProps = new FormData();
        fdPProps.left   = new FormAttachment(0,0);
        fdPProps.right  = new FormAttachment(100, 0);
        fdPProps.top    = new FormAttachment(wlPProps, margin);
        fdPProps.bottom = new FormAttachment(45, 0);
        wPProps.setLayoutData(fdPProps);
        wPProps.setEnabled(false);
        // wPProps.table.setEnabled(false);

        // Now add some buttons all at the bottom
        wNew = new Button(compConcept, SWT.PUSH);
        wNew.setText(Messages.getString("MetaEditorConcepts.USER_CREATE_NEW_CONCEPT")); //$NON-NLS-1$
        wApply = new Button(compConcept, SWT.PUSH);
        wApply.setText(Messages.getString("MetaEditorConcepts.USER_APPLY_CHANGES")); //$NON-NLS-1$
        wRevert= new Button(compConcept, SWT.PUSH);
        wRevert.setText(Messages.getString("MetaEditorConcepts.USER_REVERT_CHANGES")); //$NON-NLS-1$
        wDelete= new Button(compConcept, SWT.PUSH);
        wDelete.setText(Messages.getString("MetaEditorConcepts.USER_DELETE_CONCEPT")); //$NON-NLS-1$
        wReplace= new Button(compConcept, SWT.PUSH);
        wReplace.setText(Messages.getString("MetaEditorConcepts.USER_REPLACE_PARENTS")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(compConcept, new Button[] { wNew, wApply, wRevert, wDelete, wReplace }, margin, null );

        wNew.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { newConcept(); } } );
        wApply.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { applyConceptChanges(); } } );
        wRevert.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { revertConceptChanges(); } } );
        wDelete.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { delConcept(); } } );
        wReplace.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { replaceConcept(); } } );
        
        // Show the child properties in a grid...
        //
        Label wlProps = new Label(compConcept, SWT.LEFT);
        props.setLook(wlProps);
        wlProps.setText(Messages.getString("MetaEditorConcepts.USER_CONCEPT_PROPERTIES")); //$NON-NLS-1$
        FormData fdlProps = new FormData();
        fdlProps.left  = new FormAttachment(0,0);
        fdlProps.top   = new FormAttachment(wPProps, margin);
        wlProps.setLayoutData(fdlProps);

        newProp = new Button(compConcept, SWT.PUSH);
        props.setLook(newProp);
        newProp.setText(Messages.getString("MetaEditorConcepts.USER_ADD_NEW_PROPERTY")); //$NON-NLS-1$
        FormData fdNewProp = new FormData();
        fdNewProp.left = new FormAttachment(0, 0);
        fdNewProp.top   = new FormAttachment(wlProps, 30+margin);
        newProp.setLayoutData(fdNewProp);
        
        delProp = new Button(compConcept, SWT.PUSH);
        props.setLook(delProp);
        delProp.setText(Messages.getString("MetaEditorConcepts.USER_DELETE_SELECTED_PROPERTIES")); //$NON-NLS-1$
        FormData fdDelProp = new FormData();
        fdDelProp.left = new FormAttachment(0, 0);
        fdDelProp.top   = new FormAttachment(newProp, margin);
        delProp.setLayoutData(fdDelProp);

        editProp = new Button(compConcept, SWT.PUSH);
        props.setLook(editProp);
        editProp.setText(Messages.getString("MetaEditorConcepts.USER_EDIT_SELECTED_PROPERTIES")); //$NON-NLS-1$
        FormData fdEditProp = new FormData();
        fdEditProp.left = new FormAttachment(0, 0);
        fdEditProp.top   = new FormAttachment(delProp, margin);
        editProp.setLayoutData(fdEditProp);

        colProps = new ColumnInfo[]
          {
            new ColumnInfo(Messages.getString("MetaEditorConcepts.USER_PROPERTY_ID"), ColumnInfo.COLUMN_TYPE_CCOMBO, DefaultPropertyID.getDefaultPropertyIDs(), true), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("MetaEditorConcepts.USER_PROPERTY_TYPE"), ColumnInfo.COLUMN_TYPE_CCOMBO, ConceptPropertyType.getTypeDescriptions(), true), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("MetaEditorConcepts.USER_VALUE_DOUBLE_CLICK"), ColumnInfo.COLUMN_TYPE_TEXT,   false, true), //$NON-NLS-1$
          };

        SelectionAdapter selID = new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    CCombo combo = (CCombo)e.widget;
                    if (combo!=null)
                    {
                        Table parent = (Table) combo.getParent();
                        if (parent.getSelectionCount()==1)
                        {
                            TableItem tableItem = parent.getSelection()[0];
                            // Which default id did we select?
                            int idx = Const.indexOfString(combo.getText() ,colProps[0].getComboValues());
                            if (idx>=0)
                            {
                                // Set the corresponing type to avoid confusion.
                                ConceptPropertyType type = DefaultPropertyID.getDefaultPropertyTypes()[idx];
                                
                                tableItem.setText(2, type.getDescription());
                            }
                        }
                    }
                }
            };
        colProps[0].setSelectionAdapter(selID);
        colProps[0].setToolTip(Messages.getString("MetaEditorConcepts.USER_SELECT_DEFAULT_PROPERTY_IDS")); //$NON-NLS-1$ 

        wProps=new TableView(new Variables(),compConcept, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colProps, 1, true, null, props ); // a read-only table
        FormData fdProps = new FormData();
        fdProps.left   = new FormAttachment(delProp, 2*margin);
        fdProps.right  = new FormAttachment(100, 0);
        fdProps.top    = new FormAttachment(wlProps, margin);
        fdProps.bottom = new FormAttachment(wApply, -margin);
        wProps.setLayoutData(fdProps);
        
        newProp.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { if (NewPropertyDialog.addNewProperty(shell, changesConcept)!=null) refreshScreen(); } });
        delProp.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { delProperties(); } });
        editProp.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { editProperty(); } });

        // Double click on a row?
        wProps.table.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent event)
            {
                editProperty();
            }

            public void widgetDefaultSelected(SelectionEvent event)
            {
                editProperty();
            }
        });
    }
    
    public static final List getConceptUtilityInterfacesWithParentInterface(SchemaMeta schemaMeta, ConceptInterface parentInterface)
    {
        List list = schemaMeta.getConceptUtilityInterfaces();
        // Remove those that don't use this concept
        for (int i=list.size()-1;i>=0;i--)
        {
            ConceptUtilityInterface utilityInterface = (ConceptUtilityInterface) list.get(i);
            ConceptInterface checkParentInterface = utilityInterface.getConcept().getParentInterface();
            if (checkParentInterface==null || !parentInterface.equals(checkParentInterface))
            {
                list.remove(i);
            }
        }            

        return list;
    }

    /**
     * Deletes the active concept, but only if it has no child concepts.
     */
    protected void delConcept()
    {
        if (activeConcept!=null)
        {
            List list = getConceptUtilityInterfacesWithParentInterface(metaEditor.getSchemaMeta(), activeConcept);
            
            int answer = SWT.YES;
            if (list.size()>0)
            {
                MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_WARNING);
                mb.setMessage(Messages.getString("MetaEditorConcepts.USER_DELETE_CONCEPT_CONFIRM",activeConcept.getName(), Integer.toString(list.size()))); //$NON-NLS-1$ 
                mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
                answer = mb.open();
            }
            if (answer==SWT.YES)
            {
                for (int i=0;i<list.size();i++)
                {
                    ConceptUtilityInterface utilityInterface = (ConceptUtilityInterface) list.get(i);
                    utilityInterface.getConcept().setParentInterface(activeConcept.getParentInterface()); // switch to the parent
                }
                
                int index = metaEditor.getSchemaMeta().indexOfConcept(activeConcept);
                if (index>0) metaEditor.getSchemaMeta().removeConcept(index);
                
                activeConcept = null;
                changesConcept = null;
                
                metaEditor.refreshAll();
            }
        }
    }
    
    /**
     * replace the active concept with another one in the model elements.
     */
    protected void replaceConcept()
    {
        if (activeConcept!=null)
        {
            SchemaMeta schemaMeta = metaEditor.getSchemaMeta();
            List list = getConceptUtilityInterfacesWithParentInterface(schemaMeta, activeConcept);
            
            if (list.size()>0)
            {
                String[] names = schemaMeta.getConceptNames();
                
                EnterSelectionDialog dialog = new EnterSelectionDialog(shell, names, Messages.getString("MetaEditorConcepts.USER_REPLACE_PARENT_CONCEPTS"), //$NON-NLS-1$   
                    Messages.getString("MetaEditorConcepts.USER_REPLACE_PARENT_CONCEPT", activeConcept.getName())); //$NON-NLS-1$ 
                String conceptName = dialog.open();
                if (conceptName!=null)
                {
                    ConceptInterface parentInterface = schemaMeta.findConcept(conceptName);
                    
                    for (int i=0;i<list.size();i++)
                    {
                        ConceptUtilityInterface utilityInterface = (ConceptUtilityInterface) list.get(i);
                        utilityInterface.getConcept().setParentInterface(parentInterface); // switch to the new parent
                    }
                    
                    metaEditor.refreshAll();
                }
            }
        }
    }


    public void delProperties()
    {
        String[] ids = wProps.getItems(0);
        int[] indices = wProps.getSelectionIndices();
        for (int i=0;i<indices.length;i++)
        {
            String id = ids[indices[i]];
            ConceptPropertyInterface property = changesConcept.getChildProperty(id);
            if (property!=null) // should always work though
            {
                changesConcept.removeChildProperty(property);
            }
        }
        refreshScreen();
    }
    
    public void enableFields()
    {
        boolean enabled = changesConcept!=null;
        
        wName.setEnabled( enabled );
        wParent.setEnabled( enabled );
        wbParent.setEnabled( enabled && changesConcept.getParentInterface()!=null );
        newProp.setEnabled( enabled );
        delProp.setEnabled( enabled );
        editProp.setEnabled( enabled );
        wProps.setEnabled( enabled );

        // if nothing was changed in changesConcept: don't set the apply button.
        wApply.setEnabled( enabled && changesConcept.hasChanged() );
    }
    
    public void editProperty()
    {
        if (changesConcept==null) return;
        
        boolean refresh = false;
        String[] ids = wProps.getItems(0);
        int[] indices = wProps.getSelectionIndices();
        for (int i=0;i<indices.length;i++)
        {
            String id = ids[indices[i]];
            ConceptPropertyInterface property = changesConcept.getChildProperty(id);
            if (property!=null) // should always work though
            {
                EditConceptPropertyDialog dialog = new EditConceptPropertyDialog(shell, changesConcept, property, metaEditor.getSchemaMeta().getLocales(), metaEditor.getSchemaMeta().getSecurityReference());
                if (dialog.open()!=null)
                {
                    refresh = true;
                }
            }
        }
        if (refresh) refreshScreen();
    }

    /**
     * when the value in the parent combo box changes, look up the parent, change the changesConcept and refresh the screen
     *
     */
    protected void displayParentInformation()
    {
        ConceptInterface parentInterface = metaEditor.getSchemaMeta().findConcept(wParent.getText());
        if (changesConcept!=null)
        {
            changesConcept.setParentInterface(parentInterface);
            refreshScreen();
            refreshTree();
        }
    }

    public void showParent()
    {
        if (activeConcept!=null && activeConcept.getParentInterface()!=null)
        {
            activeConcept = activeConcept.getParentInterface();
            refreshScreen();
            selectActiveConceptInTree();
        }
    }

    private void selectActiveConceptInTree()
    {
        if (activeConcept!=null)
        {
            TreeItem parent = tiConcepts;
            String path[] = activeConcept.getPath();
            for (int i=0;i<path.length && parent!=null;i++)
            {
                TreeItem child = Const.findTreeItem(parent, path[i]);
                if (child!=null && i==path.length-1) // this is it
                {
                    wTree.setSelection(child);
                }
                parent=child;
            }
        }
    }

    /**
     * Refresh the concepts tree
     */
    public void refreshTree()
    {
        // Is there an item selected?
        String selectionPath[] = null;
        if (wTree.getSelection().length==1)
        {
            selectionPath = Const.getTreeStrings(wTree.getSelection()[0]);
        }
        
        // Clear the tree first...
        //
        TreeItem[] items = tiConcepts.getItems();
        for (int i=0;i<items.length;i++) items[i].dispose();
            
        // Then re-draw
        //
        SchemaMeta schemaMeta = metaEditor.getSchemaMeta();
        int depth=1;
        boolean found=true;
        while (found && depth<20)
        {
            found=false;
            for (int i=0;i<schemaMeta.nrConcepts();i++)
            {
                ConceptInterface concept = schemaMeta.getConcept(i);
                if (concept.getDepth()==depth)
                {
                    String[] path = concept.getPath();
                    TreeItem root = tiConcepts;
                    for (int p=0;p<path.length;p++)
                    {
                        TreeItem treeItem = Const.findTreeItem(root, path[p]);
                        if (treeItem==null)
                        {
                            treeItem = new TreeItem(root, SWT.NONE);
                            treeItem.setText(path[p]);
                            
                            // See if we need to select this tree item
                            if (selectionPath!=null && selectionPath.length>1 && depth==selectionPath.length)
                            {
                                boolean allOK = true;
                                for (int d=0;d<depth && allOK;d++)
                                {
                                    if (!selectionPath[d].equals(path[p-1])) allOK=false;
                                }
                                if (allOK)
                                {
                                    wTree.setSelection(new TreeItem[] { treeItem });
                                }
                            }
                        }
                        root = treeItem;
                    }
                    found=true;
                }
            }
            depth++;
        }
        
        TreeMemory.setExpandedFromMemory(wTree, STRING_CONCEPTS_TREE);
    }
    
    /**
     * Somewhere in the tree an item just got selected.  Which one was it? 
     * @param e
     */
	private void setActiveContext(SelectionEvent e)
    {
        final TreeItem treeItem = (TreeItem)e.item;
        final String[] completePath = Const.getTreeStrings(treeItem);
        
        final String[] path = new String[completePath.length-1];
        for (int i=0;i<path.length;i++) path[i] = completePath[i+1];
        
        ConceptInterface concept = metaEditor.getSchemaMeta().findConcept(path);
        setActiveConcept(concept, true);
    }
    
    public synchronized void refreshScreen()
    {
       // Clear it all first...
       wPProps.clearAll(false);
       wProps.clearAll(false);
       wName.setText(""); //$NON-NLS-1$
       wParent.setText(""); //$NON-NLS-1$
        
       // Then fill 'er back up
       //
       if (activeConcept!=null && changesConcept!=null)
       {
           // Also refresh the available options in the parent combo box.
           setParentCombo(changesConcept);

           wActive.setText(activeConcept.getName());
           
           // The name of the concept
           if (changesConcept.getName() != null)
           {
               wName.setText(changesConcept.getName());
           }
           
           ConceptInterface parent = changesConcept.getParentInterface(); 
           if (parent!=null)
           {
               // First the parent name
               if (parent.getName()!=null)
               {
                   wParent.setText(parent.getName()); 
               }
               
               // Then the parent properties...
               //
               refreshTableView(wPProps, parent, false);
           }
           
           refreshTableView(wProps, changesConcept, true);
           
           wTree.setFocus();
       }
       else
       {
           setParentCombo(null);
       }

       // Something changes: we want to know!
       metaEditor.setShellText();
       
       enableFields();
    }

    private void setParentCombo(ConceptInterface ignoreThisOne)
    {
        // wParent.removeAll();
        for (int i=0;i<metaEditor.getSchemaMeta().nrConcepts();i++)
        {
            ConceptInterface concept = metaEditor.getSchemaMeta().getConcept(i); 
            if (ignoreThisOne==null || !ignoreThisOne.equals(concept))
            {
                wParent.add(concept.getName());
            }
        }
        
        if (changesConcept!=null && changesConcept.getParentInterface()!=null)
        {
            String name = changesConcept.getParentInterface().getName();
            int idx = wParent.indexOf(name);
            if (idx>=0) wParent.select(idx);
        }
    }

    private void refreshTableView(TableView tableView, ConceptInterface concept, boolean onlyChildProperties)
    {
        // Clear the Parent properties table view
        tableView.clearAll(false);

        // Redraw the grid
        String[] ids;
        if (onlyChildProperties)
        {
            ids = concept.getChildPropertyIDs();
        }
        else
        {
            ids = concept.getPropertyIDs();
        }
        
        for (int i=0;i<ids.length;i++)
        {
            ConceptPropertyInterface property = concept.getProperty(ids[i]);
            
            TableItem tableItem = new TableItem(tableView.table, SWT.NONE);
            
            String type = property.getType().getDescription();
            String value = property.toString();
            
            tableItem.setText(1, ids[i]);
            if (type!=null) tableItem.setText(2, type);
            if (value!=null) tableItem.setText(3, value);
        }
        tableView.removeEmptyRows();
        tableView.setRowNums();
        tableView.optWidth(true);
    }

    protected void setMenu(SelectionEvent e)
    {
        // final TreeItem treeItem = (TreeItem)e.item;

        if (menu==null)
        {
            menu = new Menu(shell, SWT.POP_UP);
        }
        else
        {
            MenuItem items[] = menu.getItems();
            for (int i=0;i<items.length;i++) items[i].dispose();
        }
        
        // final String itemText = treeItem.getText();
        // final String[] path = Const.getTreeStrings(treeItem);

        // if (path.length==1 && itemText.equals(STRING_CONCEPTS))
        {
            MenuItem miNew = new MenuItem(menu, SWT.NONE);
            miNew.setText(Messages.getString("MetaEditorConcepts.USER_CREATE_NEW_CONCEPT_TEXT")); //$NON-NLS-1$
            miNew.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { newConcept(); } } );
        }
        
        wTree.setMenu(menu);
    }

	public String toString()
	{
		return "MetaEditorConcepts"; //$NON-NLS-1$
	}

    /**
     * @return the active Concept
     */
    public ConceptInterface getActiveConcept()
    {
        return activeConcept;
    }

    /**
     * @param concept the active Concept to set
     */
    public void setActiveConcept(ConceptInterface concept, boolean showConfirmation)
    {
        // First check if the changesConcept was changed.
        if (showConfirmation && !checkChangesConceptHasChanged())
        {
            return;
        }

        this.activeConcept = concept;
        
        if (concept!=null)
        {
            log.logDetailed(toString(), Messages.getString("MetaEditorConcepts.USER_SET_ACTIVE_CONCEPT", concept.getName())); //$NON-NLS-1$ 
            
            changesConcept = (ConceptInterface) concept.clone();
            changesConcept.clearChanged();
        }
        else
        {
            changesConcept = null;
        }

        refreshScreen();
    }

    /**
     * Create a new concept, ask a name first.
     */
    private void newConcept()
    {
        // First check if the changesConcept was changed.
        if (!checkChangesConceptHasChanged())
        {
            return;
        }
        
        // Ask a name...
        EnterStringDialog dialog = new EnterStringDialog(shell, "", Messages.getString("MetaEditorConcepts.USER_TITLE_NEW_CONCEPT"), Messages.getString("MetaEditorConcepts.USER_ENTER_NEW_CONCEPT_NAME")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String conceptName = dialog.open();
        if (conceptName!=null)
        {
            // See if it already exists
            if (metaEditor.getSchemaMeta().findConcept(conceptName)!=null)
            {
                MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                mb.setMessage(Messages.getString("MetaEditorConcepts.USER_ERROR_CONCEPT_ALREADY_EXISTS", conceptName)); //$NON-NLS-1$ 
                mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
                mb.open();
                return;
            }
            
            ConceptInterface concept = new Concept(conceptName);
            if (activeConcept!=null) concept.setParentInterface(activeConcept);
            
            // Add it to the list
            try
            {
                metaEditor.getSchemaMeta().addConcept(concept);
                
                // set this the active concept and load the screen
                setActiveConcept(concept, true);
            }
            catch(ObjectAlreadyExistsException e)
            {
                new ErrorDialog(shell, Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditorConcepts.USER_ERROR_CONCEPT_ID_EXISTS", concept.getName()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
            }
            
            // refresh the tree on the left too.
            refreshTree();
        }
    }
    
    
    /**
     * @return true if the concept has NOT changed and it's OK to go ahead with the operation
     */
    private boolean checkChangesConceptHasChanged()
    {
        if (changesConcept==null) return true;
        
        if (changesConcept.hasChanged())
        {
            MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_WARNING);
            mb.setMessage(Messages.getString("MetaEditorConcepts.USER_CONFIRM_CHANGES")); //$NON-NLS-1$
            mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
            int answer = mb.open();
            if (answer==SWT.YES)
            {
                applyConceptChanges();
                return true;
            }
            else
            if (answer==SWT.NO)
            {
                return true;
            }
            else
            {
                return false; // Cancel
            }
        }
        return true; // OK, nothing changed
    }

    private void revertConceptChanges()
    {
        setActiveConcept(activeConcept, false);
        refreshScreen();
    }

    private void applyConceptChanges()
    {
        if (activeConcept!=null && changesConcept!=null)
        {
            SchemaMeta schemaMeta = metaEditor.getSchemaMeta();
            
            String newName = wName.getText();

            // See if the new name is empty
            if (!Const.isEmpty(newName))
            {
                // if the name has changed, see if the new name isn't already used.
                if (!newName.equals(activeConcept.getName()))
                {
                    if (schemaMeta.findConcept(newName)!=null)
                    {
                        MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                        mb.setMessage(Messages.getString("MetaEditorConcepts.USER_SPECIFY_DIFFERENT_CONCEPT_NAME", newName)); //$NON-NLS-1$ 
                        mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
                        mb.open();
                        return;
                    }
                }
                
                // Set the new name
                activeConcept.setName(newName);
                
                // load the other properties from the changesConcept
                // First clear the concepts...
                activeConcept.clearChildProperties();
                
                // Then put all our concepts into it.
                activeConcept.getChildPropertyInterfaces().putAll(changesConcept.getChildPropertyInterfaces());
                
                // How about the parent?
                String parentConceptName = wParent.getText();
                if (!Const.isEmpty(parentConceptName))
                {
                    ConceptInterface parentConcept = schemaMeta.findConcept(parentConceptName);
                    activeConcept.setParentInterface(parentConcept);
                }

                // clear the changed flag..
                changesConcept.clearChanged();
                
                // Just to make sure... (we should capture the change, but you never know)
                activeConcept.setChanged();

                // another refresh: just for sanity, verification and to set the name of the active concept
                //
                refreshScreen(); 
                
                // refresh the tree in case the name changed or the parent changes
                refreshTree();
                
            }
            else
            {
                MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                mb.setMessage(Messages.getString("MetaEditorConcepts.USER_SPECIFY_NAME_FOR_CONCEPT")); //$NON-NLS-1$
                mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
                mb.open();
                return;
            }
        }
    }
}
    
