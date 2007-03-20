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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.trans.step.BaseStepDialog;

public class MetaEditorLocales extends Composite
{
	private Props props;
	
	private TableView wLocales;
	private Button wRefresh;
	private Button wApply;

	private SelectionListener lsRefresh, lsApply;
    private MetaEditor metaEditor;

	public MetaEditorLocales(Composite parent, int style, MetaEditor metaEditor)
	{
		super(parent, style);
		this.metaEditor = metaEditor;

        props = Props.getInstance();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		
		setLayout(formLayout);
		
        props.setLook(this);

        // Buttons at the bottom to form a line of reference...
        //
        wRefresh = new Button(this, SWT.PUSH);
        wRefresh.setText("&Refresh");
        wApply = new Button(this, SWT.PUSH);
        wApply.setText("&Apply changes");
        wApply.setEnabled(false);

        BaseStepDialog.positionBottomButtons(this, new Button[] { wApply, wRefresh }, Const.MARGIN, null);

        ModifyListener lsMod = new ModifyListener()
        {
            public void modifyText(ModifyEvent arg0)
            {
                wApply.setEnabled(true);
            }
        };

        // Show the parent properties in a grid...
        //
        Label wlLocales = new Label(this, SWT.LEFT);
        props.setLook(wlLocales);
        wlLocales.setText("The locales to use:");
        FormData fdlLocales = new FormData();
        fdlLocales.left  = new FormAttachment(0, 0);
        fdlLocales.top   = new FormAttachment(0, 0);
        wlLocales.setLayoutData(fdlLocales);

        ColumnInfo[] colLocales = new ColumnInfo[]
          {
            new ColumnInfo("Code",                  ColumnInfo.COLUMN_TYPE_TEXT, false, false),
            new ColumnInfo("Description",           ColumnInfo.COLUMN_TYPE_TEXT, false, false),
            new ColumnInfo("Order",                 ColumnInfo.COLUMN_TYPE_TEXT, false, false),
            new ColumnInfo("Active",                ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "Y", "N" }, false),
          };
        wLocales=new TableView(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colLocales, 1, false, lsMod, props );
        FormData fdLocales = new FormData();
        fdLocales.left   = new FormAttachment(0,0);
        fdLocales.right  = new FormAttachment(100, 0);
        fdLocales.top    = new FormAttachment(wlLocales, Const.MARGIN);
        fdLocales.bottom = new FormAttachment(wApply, -Const.MARGIN);
        wLocales.setLayoutData(fdLocales);

		lsRefresh = new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				refreshScreen();
			}
		};
		
		lsApply = new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				apply();
			}
		};
		
		wRefresh.addSelectionListener(lsRefresh);
		wApply.addSelectionListener(lsApply);

        getData();
	}
	
    public void refreshScreen()
    {
        Locales locales = metaEditor.getSchemaMeta().getLocales();
        
        wLocales.clearAll(false);
        
        for (int i=0;i<locales.nrLocales();i++)
        {
            LocaleInterface locale = locales.getLocale(i);
            TableItem item = new TableItem(wLocales.table, SWT.NONE);
            
            if (locale.getCode()!=null) item.setText(1, locale.getCode());
            if (locale.getDescription()!=null) item.setText(2, locale.getDescription());
            if (locale.getOrder()>=0) item.setText(3, Integer.toString(locale.getOrder()));
            item.setText(4, locale.isActive()?"Y":"N");
        }
        
        wLocales.removeEmptyRows();
        wLocales.setRowNums();
        wLocales.optWidth(true);
    }

    public void apply()
    {
        Locales locales = metaEditor.getSchemaMeta().getLocales();
        locales.getLocaleList().clear();
        
        for (int i=0;i<wLocales.nrNonEmpty();i++)
        {
            TableItem item = wLocales.getNonEmpty(i);
            
            String code   = item.getText(1);
            String desc   = item.getText(2);
            String order  = item.getText(3);
            String active = item.getText(4);
            
            if (!Const.isEmpty(code))
            {
                LocaleInterface locale = new LocaleMeta(code, desc, Const.toInt(order, -1), "Y".equalsIgnoreCase(active));
                locales.addLocale(locale);
            }
        }
        
        metaEditor.refreshAll();
        refreshScreen();
        wApply.setEnabled(false);
    }
}
