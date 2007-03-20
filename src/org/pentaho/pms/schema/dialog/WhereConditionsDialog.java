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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessColumnString;
import org.pentaho.pms.schema.BusinessView;
import org.pentaho.pms.schema.WhereCondition;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.trans.step.BaseStepDialog;

public class WhereConditionsDialog extends Dialog
{
    private Button wOK, wCancel;
    private Listener lsOK, lsCancel;

    private Shell  shell;
    private ModifyListener lsMod;
    private Props props;
    // private String locale;
    private TableView wFields;
    // private BusinessView businessView;
    private WhereCondition[] whereConditions;
    private List businessColumnStrings;
    private String[] flatView;

    public WhereConditionsDialog(Shell parent, BusinessView businessView, WhereCondition[] whereConditions, String locale)
    {
        super(parent, SWT.NONE);
        // this.businessView = businessView;
        this.whereConditions = whereConditions; 
        // this.locale = locale;
        
        businessColumnStrings = businessView.getFlatCategoriesView(locale);
        flatView = BusinessColumnString.getFlatRepresentations(businessColumnStrings);

        props = Props.getInstance();
    }
    
    public WhereCondition[] open()
    {
        Shell parent = getParent();
        Display display = parent.getDisplay();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
        shell.setBackground(GUIResource.getInstance().getColorBackground());
        

        FormLayout formLayout = new FormLayout ();
        formLayout.marginWidth  = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;
        shell.setLayout(formLayout);
        
        shell.setText("Create a where clause");
        
        // The buttons...
        wOK=new Button(shell, SWT.PUSH);
        wOK.setText("  &OK  ");
        wCancel=new Button(shell, SWT.PUSH);
        wCancel.setText("  &Cancel  ");
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, Const.MARGIN, null);

        // Add a label
        Label label = new Label(shell, SWT.LEFT);
        label.setText("Construct the where clause:");
        props.setLook(label);
        FormData fdLabel = new FormData();
        fdLabel.left = new FormAttachment(0,0);
        fdLabel.top  = new FormAttachment(0,0);
        label.setLayoutData(fdLabel);

        // Add a table view
        ColumnInfo[] columns =new ColumnInfo[]
          {
            new ColumnInfo("Operator",        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "AND", "OR" }, false),
            new ColumnInfo("Column",          ColumnInfo.COLUMN_TYPE_CCOMBO, flatView, false),
            new ColumnInfo("Condition",       ColumnInfo.COLUMN_TYPE_TEXT,   false, false),
          };
                          
        wFields=new TableView(shell, 
                                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                                columns, 
                                whereConditions.length,  
                                false, // read-only
                                lsMod,
                                props
                                );
        FormData fdFields = new FormData();
        fdFields.left   = new FormAttachment(0,0);
        fdFields.right  = new FormAttachment(100,0);
        fdFields.top    = new FormAttachment(label, Const.MARGIN);
        fdFields.bottom = new FormAttachment(wOK, -Const.MARGIN);
        wFields.setLayoutData(fdFields);
        
        
        // Wrap it up ...
        //
        
        // Add listeners
        lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
        lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
        
        wOK.addListener    (SWT.Selection, lsOK     );
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

        return whereConditions;
    }
    
    private void getData()
    {
        for (int i=0;i<whereConditions.length;i++)
        {
            WhereCondition whereCondition = whereConditions[i];
            TableItem item = wFields.table.getItem(i);
            
            // The operator?
            if (whereCondition.getOperator()!=null) item.setText(1, whereCondition.getOperator());
            
            // Find the position in the List...
            int idx = BusinessColumnString.getBusinessColumnIndex(businessColumnStrings, whereCondition.getField());
            if (idx>=0) item.setText(2, flatView[idx]);
            
            // The condition
            if (whereCondition.getCondition()!=null) item.setText(3, whereCondition.getCondition());
        }
        wFields.optWidth(true);
    }

    public void dispose()
    {
        props.setScreen(new WindowProperty(shell));
        shell.dispose();
    }
    
    private void cancel()
    {
        dispose();
    }
    
    private void ok()
    {
        List conditions = new ArrayList();
        
        for (int i=0;i<wFields.nrNonEmpty();i++)
        {
            TableItem item = wFields.getNonEmpty(i);
            String operator   = item.getText(1);
            String columnName = item.getText(2);
            String condition  = item.getText(3);
            
            int idx = Const.indexOfString(columnName, flatView);
            if (idx>=0)
            {
                BusinessColumnString bcs = (BusinessColumnString)businessColumnStrings.get(idx);
                BusinessColumn column = bcs.getBusinessColumn();
                
                WhereCondition whereCondition = new WhereCondition(operator, column, condition);
                conditions.add(whereCondition);
            }
        }
        
        whereConditions = (WhereCondition[]) conditions.toArray(new WhereCondition[conditions.size()]);

        dispose();
    }
}
