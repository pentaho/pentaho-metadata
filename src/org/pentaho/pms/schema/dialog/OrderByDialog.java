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
import java.util.Hashtable;
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
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.OrderBy;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.trans.step.BaseStepDialog;

public class OrderByDialog extends Dialog
{
    private static final String STRING_ASCENDING = "Ascending"; //$NON-NLS-1$
    private static final String STRING_DESCENDING = "Descending"; //$NON-NLS-1$
    
    private Button wOK, wCancel;
    private Listener lsOK, lsCancel;

    private Shell  shell;
    private ModifyListener lsMod;
    private BusinessColumn[] selectedColumns;
    private Props props;
    private OrderBy[] orderBy;
    private String locale;
    private TableView wFields;
    private String[] tableNames;
    private String[] columnNames;


    public OrderByDialog(Shell parent, BusinessColumn[] selectedColumns, String locale)
    {
        this(parent, selectedColumns, null, locale);
    }
    
    public OrderByDialog(Shell parent, BusinessColumn[] selectedColumns, OrderBy[] orderBy, String locale)
    {
        super(parent, SWT.NONE);
        this.selectedColumns = selectedColumns;
        this.locale = locale;
        
        props = Props.getInstance();
        this.orderBy = orderBy;

        Hashtable columns = new Hashtable();
        Hashtable tables = new Hashtable();
        for (int i=0;i<selectedColumns.length;i++)
        {
            columns.put(selectedColumns[i].getDisplayName(locale), ""); //$NON-NLS-1$
            tables.put(selectedColumns[i].getBusinessTable().getDisplayName(locale), ""); //$NON-NLS-1$
        }
        tableNames = (String[]) tables.keySet().toArray(new String[tables.keySet().size()]);
        columnNames = (String[]) columns.keySet().toArray(new String[columns.keySet().size()]);
    }
    
    public OrderBy[] open()
    {
        Shell parent = getParent();
        Display display = parent.getDisplay();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
        shell.setBackground(GUIResource.getInstance().getColorBackground());
        

        FormLayout formLayout = new FormLayout ();
        formLayout.marginWidth  = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;
        shell.setLayout(formLayout);
        
        shell.setText(Messages.getString("OrderByDialog.USER_SELECT_COLUMN_ORDERING")); //$NON-NLS-1$
        
        // The buttons...
        wOK=new Button(shell, SWT.PUSH);
        wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
        wCancel=new Button(shell, SWT.PUSH);
        wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, Const.MARGIN, null);

        // Add a label
        Label label = new Label(shell, SWT.LEFT);
        label.setText(Messages.getString("OrderByDialog.USER_SELECT_COLUMN_ORDERING")); //$NON-NLS-1$
        props.setLook(label);
        FormData fdLabel = new FormData();
        fdLabel.left = new FormAttachment(0,0);
        fdLabel.top  = new FormAttachment(0,0);
        label.setLayoutData(fdLabel);

        // Add a table view
        ColumnInfo[] columns =new ColumnInfo[]
          {
            new ColumnInfo(Messages.getString("OrderByDialog.USER_TABLE"),           ColumnInfo.COLUMN_TYPE_CCOMBO, tableNames, false), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("OrderByDialog.USER_COLUMN"),          ColumnInfo.COLUMN_TYPE_CCOMBO, columnNames, false), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("OrderByDialog.USER_ORDERING"),        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { STRING_ASCENDING, STRING_DESCENDING }, false), //$NON-NLS-1$
          };
                          
        wFields=new TableView(shell, 
                                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                                columns, 
                                selectedColumns.length,  
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

        return orderBy;
    }
    
    private void getData()
    {
        if (orderBy==null)
        {
            for (int i=0;i<selectedColumns.length;i++)
            {
                BusinessColumn businessColumn = selectedColumns[i];
                TableItem item = wFields.table.getItem(i);
                
                item.setText(1, businessColumn.getBusinessTable().getDisplayName(locale));
                item.setText(2, businessColumn.getDisplayName(locale));
                item.setText(3, STRING_ASCENDING);
            }
        }
        else
        {
            wFields.clearAll(false);
            for (int i=0;i<orderBy.length;i++)
            {
                TableItem item = new TableItem(wFields.table, SWT.NONE);
                item.setText(1, orderBy[i].getBusinessColumn().getBusinessTable().getDisplayName(locale));
                item.setText(2, orderBy[i].getBusinessColumn().getDisplayName(locale));
                item.setText(3, orderBy[i].isAscending()?STRING_ASCENDING:STRING_DESCENDING);
            }
            wFields.removeEmptyRows();
        }
        wFields.setRowNums();
        wFields.optWidth(true);
    }

    public void dispose()
    {
        props.setScreen(new WindowProperty(shell));
        shell.dispose();
    }
    
    private void cancel()
    {
        orderBy=null;
        dispose();
    }
    
    private void ok()
    {
        List order = new ArrayList();
        
        for (int i=0;i<wFields.nrNonEmpty();i++)
        {
            TableItem item = wFields.getNonEmpty(i);
            String tableName = item.getText(1);
            String columnName = item.getText(2);
            String direction       = item.getText(3);
            
            BusinessColumn businessColumn = findColumn(tableName, columnName); // search on table and column 
            if (businessColumn!=null)
            {
                boolean ascending = STRING_ASCENDING.equals( direction );
                
                OrderBy by = new OrderBy(businessColumn, ascending);
                order.add(by);
            }
        }
        
        orderBy = (OrderBy[]) order.toArray(new OrderBy[order.size()]);

        dispose();
    }
    
    private BusinessColumn findColumn(String tableName, String columnName)
    {
        for (int i=0;i<selectedColumns.length;i++)
        {
            BusinessColumn column = selectedColumns[i];
            
            String cmpColumnName = column.getDisplayName(locale);
            String cmpTableName = column.getBusinessTable().getDisplayName(locale);
            
            if (tableName.equals(cmpTableName) && columnName.equals(cmpColumnName)) return column;
        }
        return null;
    }
    
    
}
