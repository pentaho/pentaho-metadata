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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.trans.step.BaseStepDialog;



public class RelationshipDialog extends Dialog
{
	private Label        wlFrom;
	private CCombo       wFrom;
    private FormData     fdlFrom, fdFrom;
	
	private Label        wlTo;
	private CCombo       wTo;
	private FormData     fdlTo,fdTo;

	private CCombo       wFromField;
	private FormData     fdFromField;

	private CCombo       wToField;
	private FormData     fdToField;
	
	private Button   wGuess;
	private FormData fdGuess;
	private Listener lsGuess;

	private Label        wlRelation;
	private CCombo       wRelation;
	private FormData     fdlRelation, fdRelation;

	private Button   wGuessRel;
	private FormData fdGuessRel;
	private Listener lsGuessRel;

	private Label        wlComplex;
	private Button       wComplex;
	private FormData     fdlComplex, fdComplex;

	private Label        wlComplexJoin;
	private Text         wComplexJoin;
	private FormData     fdlComplexJoin, fdComplexJoin;

	private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private RelationshipMeta relationshipMeta;
	private Shell  shell;
	private BusinessModel businessModel;
	
	private BusinessTable fromtable, totable;
		
	private ModifyListener lsMod;
	
	private boolean changed, backupComplex;
	
	public RelationshipDialog(Shell parent, int style, LogWriter l, RelationshipMeta relationshipMeta, BusinessModel businessModel)
	{
		super(parent, style);
		this.relationshipMeta=relationshipMeta;
		this.businessModel=businessModel;
		
		fromtable = relationshipMeta.getTableFrom();
		totable   = relationshipMeta.getTableTo();
	}

	public Object open()
	{
        Props props = Props.getInstance();
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setBackground(GUIResource.getInstance().getColorBackground());
		
		lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				relationshipMeta.setChanged();
			}
		};
		changed = relationshipMeta.hasChanged();
		backupComplex = relationshipMeta.isComplex();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText("Hop: From --> To");
		
		int middle = props.getMiddlePct();
		int length = 350;
		int margin = Const.MARGIN;

		// From step line
		wlFrom=new Label(shell, SWT.RIGHT);
		wlFrom.setText("From table / field: ");
        props.setLook(wlFrom);
		fdlFrom=new FormData();
		fdlFrom.left = new FormAttachment(0, 0);
		fdlFrom.right= new FormAttachment(middle, -margin);
		fdlFrom.top  = new FormAttachment(0, margin);
		wlFrom.setLayoutData(fdlFrom);
		wFrom=new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wFrom.setText("Select the source table");
        props.setLook(wFrom);

		for (int i=0;i<businessModel.nrBusinessTables();i++)
		{
			BusinessTable ti = businessModel.getBusinessTable(i);
			wFrom.add(ti.getId());
		}
		wFrom.addModifyListener(lsMod);
		wFrom.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					// grab the new fromtable:
					fromtable=businessModel.findBusinessTable(wFrom.getText());
					refreshFromFields();
				}
			}
		);

		fdFrom=new FormData();
		fdFrom.left = new FormAttachment(middle, 0);
		fdFrom.top  = new FormAttachment(0, margin);
		fdFrom.right= new FormAttachment(60, 0);
		wFrom.setLayoutData(fdFrom);

		wFromField=new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wFromField.setText("");
        props.setLook(wFromField);
        refreshFromFields();		
		wFromField.addModifyListener(lsMod);

		fdFromField=new FormData();
		fdFromField.left = new FormAttachment(wFrom, margin*2);
		fdFromField.top  = new FormAttachment(0, margin);
		fdFromField.right= new FormAttachment(100, 0);
		wFromField.setLayoutData(fdFromField);

		// To line
		wlTo=new Label(shell, SWT.RIGHT);
		wlTo.setText("To table / field: ");
        props.setLook(wlTo);
		fdlTo=new FormData();
		fdlTo.left = new FormAttachment(0, 0);
		fdlTo.right= new FormAttachment(middle, -margin);
		fdlTo.top  = new FormAttachment(wFrom, margin);
		wlTo.setLayoutData(fdlTo);
		wTo=new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wTo.setText("Select the destination table");
        props.setLook(wTo);

		for (int i=0;i<businessModel.nrBusinessTables();i++)
		{
            BusinessTable ti = businessModel.getBusinessTable(i);
			wTo.add(ti.getId());
		} 
		wTo.addModifyListener(lsMod);
		wTo.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					// grab the new fromtable:
					totable=businessModel.findBusinessTable(wTo.getText());
					refreshToFields();
				}
			}
		);
		
		fdTo=new FormData();
		fdTo.left = new FormAttachment(middle, 0);
		fdTo.top  = new FormAttachment(wFrom, margin);
		fdTo.right= new FormAttachment(60, 0);
		wTo.setLayoutData(fdTo);


		// ToField step line
		wToField=new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wToField.setText("Select the field");
        props.setLook(wToField);
		refreshToFields();
		wToField.addModifyListener(lsMod);

		fdToField=new FormData();
		fdToField.left = new FormAttachment(wTo, margin*2);
		fdToField.top  = new FormAttachment(wFromField, margin);
		fdToField.right= new FormAttachment(100, 0);
		wToField.setLayoutData(fdToField);

		wGuess=new Button(shell, SWT.PUSH);
		wGuess.setText("  &Guess matching fields  ");
		lsGuess = new Listener() { public void handleEvent(Event e) { guess(); } };
		wGuess.addListener(SWT.Selection, lsGuess );
		fdGuess=new FormData();
		fdGuess.left       = new FormAttachment(wTo, margin*2);
		fdGuess.top        = new FormAttachment(wToField, margin);
		wGuess.setLayoutData(fdGuess);

		// Relation line
		wlRelation=new Label(shell, SWT.RIGHT);
		wlRelation.setText("Relationship : ");
        props.setLook(wlRelation);
		fdlRelation=new FormData();
		fdlRelation.left = new FormAttachment(0, 0);
		fdlRelation.right= new FormAttachment(middle, -margin);
		fdlRelation.top  = new FormAttachment(wGuess, margin*2);
		wlRelation.setLayoutData(fdlRelation);
		wRelation=new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wRelation);

		for (int i=0;i<RelationshipMeta.typeRelationshipDesc.length;i++)
		{
			wRelation.add(RelationshipMeta.typeRelationshipDesc[i]);
		}
		wRelation.addModifyListener(lsMod);

		fdRelation=new FormData();
		fdRelation.left = new FormAttachment(middle, 0);
		fdRelation.top  = new FormAttachment(wGuess, margin*2);
		fdRelation.right= new FormAttachment(60, 0);
		wRelation.setLayoutData(fdRelation);

		wGuessRel=new Button(shell, SWT.PUSH);
		wGuessRel.setText("  &Guess relationship  ");
		lsGuessRel = new Listener() { public void handleEvent(Event e) { guessRelationship(); } };
		wGuessRel.addListener(SWT.Selection, lsGuessRel );
		fdGuessRel=new FormData();
		fdGuessRel.left       = new FormAttachment(wRelation, margin*2);
		fdGuessRel.top        = new FormAttachment(wGuess, margin*2);
		wGuessRel.setLayoutData(fdGuessRel);

		// Complex checkbox
		wlComplex=new Label(shell, SWT.RIGHT);
		wlComplex.setText("Complex join? ");
        props.setLook(wlComplex);
		fdlComplex=new FormData();
		fdlComplex.left = new FormAttachment(0, 0);
		fdlComplex.right= new FormAttachment(middle, -margin);
		fdlComplex.top  = new FormAttachment(wGuessRel, margin);
		wlComplex.setLayoutData(fdlComplex);
		wComplex=new Button(shell, SWT.CHECK);
        props.setLook(wComplex);
		fdComplex=new FormData();
		fdComplex.left = new FormAttachment(middle, 0);
		fdComplex.right= new FormAttachment(0, middle+length);
		fdComplex.top  = new FormAttachment(wGuessRel, margin);
		wComplex.setLayoutData(fdComplex);
		wComplex.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent e) 
				{
					relationshipMeta.flipComplex();
					relationshipMeta.setChanged();
					setComplex();
				}
			}
		);

		// ComplexJoin line
		wlComplexJoin=new Label(shell, SWT.RIGHT);
		wlComplexJoin.setText("Complex join expression: ");
        props.setLook(wlComplexJoin);
		fdlComplexJoin=new FormData();
		fdlComplexJoin.left = new FormAttachment(0, 0);
		fdlComplexJoin.right= new FormAttachment(middle, -margin);
		fdlComplexJoin.top  = new FormAttachment(wComplex, margin);
		wlComplexJoin.setLayoutData(fdlComplexJoin);
		wComplexJoin=new Text(shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		wComplexJoin.setText("");
        props.setLook(wComplexJoin);
		wComplexJoin.addModifyListener(lsMod);
		fdComplexJoin=new FormData();
		fdComplexJoin.left   = new FormAttachment(0, 0);
		fdComplexJoin.right  = new FormAttachment(100, 0);
		fdComplexJoin.top    = new FormAttachment(wlComplexJoin, margin);
		fdComplexJoin.bottom = new FormAttachment(100, -50);
		wComplexJoin.setLayoutData(fdComplexJoin);

		// Some buttons
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText("  &OK  ");
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText("  &Cancel  ");
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

		// Add listeners
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		
		wOK.addListener    (SWT.Selection, lsOK     );
		wCancel.addListener(SWT.Selection, lsCancel );
		
		// Detect [X] or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		getData();
		relationshipMeta.setChanged(changed);
        
        WindowProperty winprop = props.getScreen(shell.getText());
        if (winprop!=null) winprop.setShell(shell); else shell.pack();
        	
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return relationshipMeta;
	}
	
	public void setComplex()
	{
		wFromField.setEnabled(relationshipMeta.isRegular());
		wToField.setEnabled(relationshipMeta.isRegular());
		wComplexJoin.setEnabled(relationshipMeta.isComplex());
		wlComplexJoin.setEnabled(relationshipMeta.isComplex());

        /*
		if (relationshipMeta.isRegular())
		{
			wFromField.setBackground(bg);
			wToField.setBackground(bg);
			wComplexJoin.setBackground(gray);
		}
		else
		{
			wFromField.setBackground(gray);
			wToField.setBackground(gray);
			wComplexJoin.setBackground(bg);
		}
		*/
	}

	public void refreshFromFields()
	{
		wFromField.removeAll();
		if (fromtable!=null)
		{
			for (int i=0;i<fromtable.nrBusinessColumns();i++)
			{
				BusinessColumn f = fromtable.getBusinessColumn(i);
				wFromField.add(f.getId());
			}
		}
	}
	
	public void refreshToFields()
	{
		wToField.removeAll();
		if (totable!=null)
		{
			for (int i=0;i<totable.nrBusinessColumns();i++)
			{
                BusinessColumn f = totable.getBusinessColumn(i);
				wToField.add(f.getId());
			}
		}
	}

	public void dispose()
	{
        Props.getInstance().setScreen(new WindowProperty(shell));
        shell.dispose();
	}
	
	/**
	 * Copy information from the meta-data relationshipMeta to the dialog fields.
	 */ 
	public void getData()
	{
		if (relationshipMeta.getTableFrom() != null) wFrom.setText(relationshipMeta.getTableFrom().getId());
		if (relationshipMeta.getTableTo()   != null) wTo.setText(relationshipMeta.getTableTo().getId());
		
		if (relationshipMeta.getFieldFrom()!=null)
        {
            int idx = wFromField.indexOf(relationshipMeta.getFieldFrom().getId());
            if (idx>=0) wFromField.select(idx);
        }
		if (relationshipMeta.getFieldTo()!=null)
        {
            int idx = wToField.indexOf(relationshipMeta.getFieldTo().getId());
            if (idx>=0) wToField.select(idx);
        }
		
		wRelation.select(relationshipMeta.getType());
		wComplex.setSelection(relationshipMeta.isComplex());
		if (relationshipMeta.getComplexJoin()!=null) wComplexJoin.setText(relationshipMeta.getComplexJoin());
		setComplex();
	}
	
	private void cancel()
	{
		relationshipMeta.setChanged(changed);
		relationshipMeta.setComplex(backupComplex);
		relationshipMeta=null;
		dispose();
	}
	
	private void ok()
	{
        BusinessTable tableFrom = businessModel.findBusinessTable(wFrom.getText());
		relationshipMeta.setTableFrom( tableFrom );
        
        BusinessTable tableTo = businessModel.findBusinessTable(wTo  .getText());
		relationshipMeta.setTableTo  ( tableTo );
		
        if (tableFrom!=null)
        {
            BusinessColumn fieldFrom = tableFrom.findBusinessColumn(wFromField.getText());
            relationshipMeta.setFieldFrom( fieldFrom );
        }
        if (tableTo!=null)
        {
            BusinessColumn fieldTo = tableTo.findBusinessColumn( wToField.getText() );
            relationshipMeta.setFieldTo  ( fieldTo );
        }
		
		relationshipMeta.setType       ( wRelation.getSelectionIndex());
		
		relationshipMeta.setComplexJoin( wComplexJoin.getText());
		
		if (relationshipMeta.getTableFrom()==null)
		{
			MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING );
			mb.setMessage("Table ["+wFrom.getText()+"] doesn't exist!");
			mb.setText("Warning!");
			mb.open();
		}
		else
		{
			if (relationshipMeta.getTableTo()==null)
			{
				MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING );
				mb.setMessage("Table ["+wTo.getText()+"] doesn't exist!");
				mb.setText("Warning!");
				mb.open();
			}
			else
			{
				if (relationshipMeta.getTableFrom().getId().equalsIgnoreCase(relationshipMeta.getTableTo().getId()))
				{
					MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING );
					mb.setMessage("A relationship can't be made to the same table!");
					mb.setText("Warning!");
					mb.open();
				}
				else
				{
					dispose();
				}
			}
		}
	}
	
	// Try to find fields with the same name in both tables...
	public void guess()
	{
        String from[] = wFromField.getItems();
        String to[]   = wToField.getItems();
        
        // What is the longest string?
        int longest = -1;
        for (int i=0;i<from.length;i++) if (from[i].length()>longest) longest = from[i].length();
        for (int i=0;i<to.length;i++) if (to[i].length()>longest) longest = to[i].length();
        
        for (int length=longest;length>3;length--)
        {
    		for (int i=0;i<from.length;i++)
    		{
                
    			for (int j=0;j<to.length;j++)
    			{
                    String one = wFromField.getItem(i);
                    String two = wToField.getItem(j);
                    
                    int endOne = length;
                    if (endOne>one.length()) endOne = one.length();
                    int endTwo = length;
                    if (endTwo>two.length()) endTwo = two.length();
                    
    				String leftOne = one.substring(0, endOne);
    				String leftTwo = two.substring(0, endTwo);
    				
    				if (leftOne.equalsIgnoreCase(leftTwo))
    				{
    					wFromField.select(i);
    					wToField.select(j);
    					return;
    				}

                    int startOne = one.length()-length;
                    if (startOne<0) startOne = 0;
                    int startTwo = two.length()-length;
                    if (startTwo<0) startTwo = 0;
                    
                    String rightOne = one.substring(startOne, one.length());
                    String rightTwo = two.substring(startTwo, two.length());
                    
                    if (rightOne.equalsIgnoreCase(rightTwo))
                    {
                        wFromField.select(i);
                        wToField.select(j);
                        return;
                    }
    			}  
    		}
        }
	}

	// Try to find fields with the same name in both tables...
	public void guessRelationship()
	{
		if (fromtable!=null && totable!=null)
		{
			if (fromtable.isFactTable() && totable.isDimensionTable()) wRelation.select(RelationshipMeta.TYPE_RELATIONSHIP_N_1);
			if (fromtable.isDimensionTable() && totable.isFactTable()) wRelation.select(RelationshipMeta.TYPE_RELATIONSHIP_1_N);
			if (fromtable.isFactTable() && totable.isFactTable())      wRelation.select(RelationshipMeta.TYPE_RELATIONSHIP_N_N);
		}
	}
}
