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
 * Created on 18-mei-2003
 *
 */

package org.pentaho.pms.schema.dialog;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.WhereCondition;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.trans.step.BaseStepDialog;



public class SelectFieldDialog extends Dialog 
{
	private LogWriter log;
	private SchemaMeta schema;
	
	private static final String STRING_FIELDS     = "Fields"; //$NON-NLS-1$
	private static final String STRING_CONDITIONS = "Conditions"; //$NON-NLS-1$
	
	private Shell     shell;
	private Tree      wTree;
	private Button    wOK;
	private Button    wCancel;

	private Label     wlList, wlCondition;
	private List      wList, wCondition;
	
	private boolean   retval;
	public  BusinessColumn     fields[];
	public  WhereCondition conditions[];
    
    private Props props;
		
	public SelectFieldDialog(Shell par, int style, LogWriter l, SchemaMeta sch)
	{
		super(par, style);
		schema=sch;
		log=l;
		
		retval=false;
        props=Props.getInstance();
	}

	public boolean open() 
	{
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
		shell.setText(Messages.getString("SelectFieldDialog.USER_FIELD_SELECTION_SCREEN")); //$NON-NLS-1$
		
        int margin = Const.MARGIN;
        
        FormLayout formLayout = new FormLayout();
        formLayout.marginTop    = margin;
        formLayout.marginBottom = margin;
        formLayout.marginLeft   = margin;
        formLayout.marginRight  = margin;
		shell.setLayout(formLayout);
				
		////////////////////////////////////////////////////
		// Sashform
		////////////////////////////////////////////////////
		
		
		SashForm sashform = new SashForm(shell, SWT.HORIZONTAL); 
		sashform.setLayout(new FillLayout());
 		props.setLook(sashform);
		
		Composite leftsplit = new Composite(sashform, SWT.NONE);
 		props.setLook(leftsplit);
		
		FormLayout leftLayout = new FormLayout ();
		leftLayout.marginWidth  = margin;
		leftLayout.marginHeight = margin;		
        leftLayout.marginTop    = margin;
        leftLayout.marginBottom = margin;       
		leftsplit.setLayout (leftLayout);
 		
 		// Tree
 		wTree = new Tree(leftsplit, SWT.MULTI | SWT.BORDER );
 		props.setLook( 		wTree);
 		
 		// TreeItem tiTree = new TreeItem(wTree, SWT.NONE); tiTree.setText("Schema");
 		
 		// List all tables and fields...
		// The catalogs...				
		TreeItem tiFld = new TreeItem(wTree, SWT.NONE); tiFld.setText(STRING_FIELDS);
		TreeItem tiCon = new TreeItem(wTree, SWT.NONE); tiCon.setText(STRING_CONDITIONS);

		FormData fdSash  = new FormData(); 
		fdSash.left   = new FormAttachment(0, 0); // To the right of the label
		fdSash.top    = new FormAttachment(0, 0);
		fdSash.right  = new FormAttachment(100, 0);
		fdSash.bottom = new FormAttachment(100, 0);
		sashform.setLayoutData(fdSash);

		// Fill in the field info...
		for (int i=0;i<schema.nrTables();i++)
		{
			PhysicalTable table = schema.getTable(i);
			
			String tablename = table.getId();
			TreeItem newTab = new TreeItem(tiFld, SWT.NONE); newTab.setText(tablename);
			
			for (int j = 0; j < table.nrPhysicalColumns(); j++)
			{
				PhysicalColumn f = table.getPhysicalColumn(j);
				if (!f.isHidden())
				{
					String fieldname = f.getId();
					
					TreeItem ti = new TreeItem(newTab, SWT.NONE); ti.setText(fieldname);
				}
			}
 		}
 		
		//tiTree.setExpanded(true);
		tiFld.setExpanded(true);
		tiCon.setExpanded(true);
 		
 		// Buttons
		wOK = new Button(leftsplit, SWT.PUSH); 
		wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
		
		wCancel = new Button(leftsplit, SWT.PUSH);
		wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
		
		FormData fdTree      = new FormData(); 

		fdTree.left   = new FormAttachment(0, 0); // To the right of the label
		fdTree.top    = new FormAttachment(0, 0);
		fdTree.right  = new FormAttachment(100, 0);
		fdTree.bottom = new FormAttachment(100, -50);
		wTree.setLayoutData(fdTree);

        BaseStepDialog.positionBottomButtons(leftsplit, new Button[] { wOK, wCancel }, margin, null);
	
		// Add listeners
		wCancel.addListener(SWT.Selection, new Listener ()
			{
				public void handleEvent (Event e) 
				{
					log.logDebug(this.getClass().getName(), Messages.getString("SelectFieldDialog.DEBUG_CANCEL_DIALOG")); //$NON-NLS-1$
					dispose();
				}
			}
		);

		// Add listeners
		wOK.addListener(SWT.Selection, new Listener ()
			{
				public void handleEvent (Event e) 
				{
					handleOK();
				}
			}
		);
		
		
		
		Composite compmiddle = new Composite(sashform, SWT.NONE);
 		props.setLook(compmiddle);
		
		FormLayout middleLayout = new FormLayout ();
		middleLayout.marginWidth  = margin;
		middleLayout.marginHeight = margin;		
		compmiddle.setLayout (middleLayout);

		Button wAdd = new Button(compmiddle, SWT.PUSH);
		wAdd.setText(" > "); //$NON-NLS-1$

		Button wRemove = new Button(compmiddle, SWT.PUSH);
		wRemove.setText(" < "); //$NON-NLS-1$

		FormData fdAdd = new FormData();
		fdAdd.left   = new FormAttachment(0, 0); // To the right of the label
		fdAdd.top    = new FormAttachment(40, 0);
		fdAdd.right  = new FormAttachment(100, 0);
		wAdd.setLayoutData(fdAdd);

		FormData fdRemove = new FormData();
		fdRemove.left   = new FormAttachment(0, 0); // To the right of the label
		fdRemove.top    = new FormAttachment(wAdd, margin*2);
		fdRemove.right  = new FormAttachment(100, 0);
		wRemove.setLayoutData(fdRemove);
		
		Composite rightsplit = new Composite(sashform, SWT.NONE);
 		props.setLook(rightsplit);
		
		FormLayout rightLayout = new FormLayout ();
		rightLayout.marginWidth  = margin;
		rightLayout.marginHeight = margin;		
		rightsplit.setLayout (rightLayout);

		wlList = new Label(rightsplit, SWT.LEFT);
		wlList.setText(Messages.getString("SelectFieldDialog.USER_SELECTED_FIELDS")); //$NON-NLS-1$
 		props.setLook(wlList);
		
		
		wList = new List(rightsplit, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
 		props.setLook(wList);

		wlCondition = new Label(rightsplit, SWT.LEFT);
		wlCondition.setText(Messages.getString("SelectFieldDialog.USER_SELECTED_CONDITIONS")); //$NON-NLS-1$
 		props.setLook(wlCondition);

		wCondition = new List(rightsplit, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
 		props.setLook(wCondition);

		FormData fdlList = new FormData();
		fdlList.left   = new FormAttachment(0, 0);
		fdlList.top    = new FormAttachment(0, margin);
		wlList.setLayoutData(fdlList);

		FormData fdList = new FormData();
		fdList.left   = new FormAttachment(0, 0); 
		fdList.top    = new FormAttachment(wlList, 0);
		fdList.right  = new FormAttachment(100, 0);
		fdList.bottom = new FormAttachment(75, 0);
		wList.setLayoutData(fdList);

		FormData fdlCondition = new FormData();
		fdlCondition.left   = new FormAttachment(0, 0);
		fdlCondition.top    = new FormAttachment(wList, margin*2);
		wlCondition.setLayoutData(fdlCondition);

		FormData fdCondition = new FormData();
		fdCondition.left   = new FormAttachment(0, 0); // To the right of the label
		fdCondition.top    = new FormAttachment(wlCondition, 0);
		fdCondition.right  = new FormAttachment(100, 0);
		fdCondition.bottom = new FormAttachment(100, 0);
		wCondition.setLayoutData(fdCondition);
		 		
		sashform.setWeights(new int[] { 46, 8, 46 });
		
		// Drag & Drop for steps
		Transfer[] ttypes = new Transfer[] {TextTransfer.getInstance() };
		
		DragSource ddSource = new DragSource(wTree, DND.DROP_MOVE | DND.DROP_COPY);
		ddSource.setTransfer(ttypes);
		ddSource.addDragListener(new DragSourceListener() 
			{
				public void dragStart(DragSourceEvent event){ }
	
				public void dragSetData(DragSourceEvent event) 
				{
					TreeItem ti[] = wTree.getSelection();
					String data = new String();
					for (int i=0;i<ti.length;i++) 
					{
						String itemname = ti[i].getText();
						TreeItem thisParent = ti[i].getParentItem();
						if (thisParent!=null)
						{
							String parentname = thisParent.getText();
							TreeItem grandparent = thisParent.getParentItem();
							if (grandparent!=null)
							{
								String grandparentname = grandparent.getText();
								
								if (grandparentname.equalsIgnoreCase(STRING_FIELDS) ||
								    grandparentname.equalsIgnoreCase(STRING_CONDITIONS))
								{
									data+=STRING_FIELDS+"\t"+parentname+"\t"+itemname+Const.CR; //$NON-NLS-1$ //$NON-NLS-2$
								}
							}
						}
					} 
					event.data = data;
				}
	
				public void dragFinished(DragSourceEvent event) {}
			}
		);
		DropTarget ddTarget = new DropTarget(wList, DND.DROP_MOVE | DND.DROP_COPY);
		ddTarget.setTransfer(ttypes);
		ddTarget.addDropListener(new DropTargetListener() 
		{
			public void dragEnter(DropTargetEvent event) { }
			public void dragLeave(DropTargetEvent event) { }
			public void dragOperationChanged(DropTargetEvent event) { }
			public void dragOver(DropTargetEvent event) { }
			public void drop(DropTargetEvent event) 
			{
				if (event.data == null) { // no data to copy, indicate failure in event.detail
					event.detail = DND.DROP_NONE;
					return;
				}
				StringTokenizer strtok = new StringTokenizer((String)event.data, Const.CR);
				while (strtok.hasMoreTokens())
				{
					String   source = strtok.nextToken();
					int idx  = source.indexOf("\t"); //$NON-NLS-1$
					int idx2 = source.indexOf("\t", idx+1);  //$NON-NLS-1$
					if (idx>=0)
					{
						String fieldtype = source.substring(0, idx);
						String tablename = source.substring(idx+1, idx2);
						String fieldname = source.substring(idx2+1);
						if (fieldtype.equalsIgnoreCase(STRING_FIELDS))
						{
							addToFieldsList(tablename, fieldname);
						}
					}
				}
			}

			public void dropAccept(DropTargetEvent event) 
			{
			}
		});
		
		// Double click adds to List.
		wTree.addSelectionListener(new SelectionAdapter()
			{
				public void widgetDefaultSelected(SelectionEvent e)
				{
					addSelectedToList();					
				}
			}
		);
		wAdd.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					addSelectedToList();					
				}
			}
		);
		
		wList.addKeyListener(new KeyAdapter() 
			{
				public void keyPressed(KeyEvent e) 
				{
					// Delete the selected items, last to first
					if (e.character == SWT.DEL)
					{
						delSelectedFields();
					}
				}
			}
		);

		wCondition.addKeyListener(new KeyAdapter() 
			{
				public void keyPressed(KeyEvent e) 
				{
					// Delete the selected items, last to first
					if (e.character == SWT.DEL)
					{
						delSelectedConditions();
					}
				}
			}
		);
		
		wRemove.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent e)
				{
					delSelectedFields();					
					delSelectedConditions();					
				}
			}
		);
		
		getData();

		WindowProperty winprop = props.getScreen(shell.getText());
		if (winprop!=null) winprop.setShell(shell); else shell.pack();
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch()) display.sleep();
		}
		return retval;
	}
	
	public void getData()
	{
	}
	
	public void addSelectedToList()
	{

		TreeItem ti[] = wTree.getSelection();
		
        for (int i=0;i<ti.length;i++) 
		{
			String itemname = ti[i].getText();
			TreeItem parent = ti[i].getParentItem();
			if (parent!=null)
			{
				String parentname = parent.getText();
				TreeItem grandparent = parent.getParentItem();
				if (grandparent!=null)
				{
					String grandparentname = grandparent.getText();
								
					if (grandparentname.equalsIgnoreCase(STRING_FIELDS))
					{
						addToFieldsList(parentname, itemname);
					}
					else
					if (grandparentname.equalsIgnoreCase(STRING_CONDITIONS))
					{
						addToConditionsList(parentname, itemname);
					}
				}
			}
		} 
	}

	public void addTableToList(String tablename)
	{	
		PhysicalTable tab = schema.findPhysicalTable(tablename);
		if (tab!=null)
		{
			for (int j=0;j<tab.nrPhysicalColumns();j++)
			{
				PhysicalColumn f = tab.getPhysicalColumn(j);
				if (!f.isHidden())
				{
					String fieldname = tab.getPhysicalColumn(j).getId();
					addToFieldsList(tablename, fieldname);
				}
			}
		}
	}


	public void addToFieldsList(String tablename, String fieldname)
	{
		String display = tablename+" . "+fieldname; //$NON-NLS-1$
		int idx = wList.indexOf(display);
		if (idx<0) wList.add(display);
	}

	public void addToConditionsList(String tablename, String conditionname)
	{
		String display = tablename+" . "+conditionname; //$NON-NLS-1$
		int idx = wCondition.indexOf(display);
		if (idx<0) wCondition.add(display);
	}
	
	public void delSelectedFields()
	{
		int idx[] = wList.getSelectionIndices();
		wList.remove(idx);
	}

	public void delSelectedConditions()
	{
		int idx[] = wCondition.getSelectionIndices();
		wCondition.remove(idx);
	}
	
	public void dispose()
	{
        props.setScreen(new WindowProperty(shell));
		shell.dispose();
	}
	
	public void handleOK()
	{
		retval=true;
		String str[] = wList.getItems();
		String con[] = wCondition.getItems();
		
		fields     = new BusinessColumn[str.length];
		conditions = new WhereCondition[con.length];

		// The selected fields...		
		for (int i=0;i<str.length;i++)
		{
			int idx = str[i].indexOf(" . "); //$NON-NLS-1$
			if (idx>=0)
			{
				String tablename = str[i].substring(0, idx);
				String fieldname = str[i].substring(idx+3);
				
				BusinessTable tab = schema.getActiveModel().findBusinessTable(tablename);
				if (tab!=null)
				{
					fields[i] = tab.findBusinessColumn(fieldname);
				}
			}
		}


		dispose();
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}

}
