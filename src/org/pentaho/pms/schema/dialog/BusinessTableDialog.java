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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.ITableModel;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class BusinessTableDialog extends AbstractTableDialog {

  private static final Log logger = LogFactory.getLog(BusinessTableDialog.class);

  private Combo physicalTableText;

  private Text businessTableText;

  private BusinessTable businessTable;

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.getString("BusinessTableDialog.USER_BUSINESS_TABLE_PROPERTIES"));
  }

  public BusinessTableDialog(Shell parent, int style, ITableModel tableModel, SchemaMeta schemaMeta, ConceptUtilityInterface selectedTableOrColumn) {
    super(parent, style, tableModel, schemaMeta, selectedTableOrColumn);
    businessTable = (BusinessTable) tableModel.getWrappedTable();
  }
  
  public BusinessTableDialog(Shell parent, int style, ITableModel tableModel, SchemaMeta schemaMeta) {
    this(parent, style, tableModel, schemaMeta, null);
  }

  protected void addColumnPressed() {
    PhysicalTable physicalTable = schemaMeta.findPhysicalTable(physicalTableText.getText());
    if (physicalTable != null) {
      AddBusinessColumnDialog dialog = new AddBusinessColumnDialog(getShell(), tableModel, schemaMeta.getActiveLocale());
      dialog.open();
    } else {
      showMissingPhysicalTableError();
    }
  }

  private void showMissingPhysicalTableError() {
    MessageDialog.openError(getShell(), "Error", "You must select a physical table first.");
  }

  protected Control createTop(final Composite parent) {
    Composite c0 = new Composite(parent, SWT.NONE);
    c0.setLayout(new FormLayout());

    Label businessTableLabel = new Label(c0, SWT.RIGHT);
    businessTableLabel.setText(Messages.getString("BusinessTableDialog.USER_NAME_ID")); //$NON-NLS-1$
    businessTableText = new Text(c0, SWT.SINGLE | SWT.LEFT | SWT.BORDER);

    Label physicalTableLabel = new Label(c0, SWT.RIGHT);
    physicalTableLabel.setText(Messages.getString("BusinessTableDialog.USER_PHYSICAL_TABLE")); //$NON-NLS-1$
    physicalTableText = new Combo(c0, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);

    FormData fdlId = new FormData();
    fdlId.left = new FormAttachment(0, 0);
    fdlId.top = new FormAttachment(businessTableText, 0, SWT.CENTER);
    businessTableLabel.setLayoutData(fdlId);

    businessTableText.setText(""); //$NON-NLS-1$

    if (null != businessTable.getId()) {
      businessTableText.setText(businessTable.getId());
    }

    //    wId.addModifyListener(lsMod);
    FormData fdBusinessTableText = new FormData();
    fdBusinessTableText.left = new FormAttachment(businessTableLabel, 10);
    fdBusinessTableText.top = new FormAttachment(0, 0);
    fdBusinessTableText.right = new FormAttachment(physicalTableLabel, -10);
    businessTableText.setLayoutData(fdBusinessTableText);

    FormData fdPhysicalTableLabel = new FormData();
    fdPhysicalTableLabel.left = new FormAttachment(50, 0);
    fdPhysicalTableLabel.top = new FormAttachment(physicalTableText, 0, SWT.CENTER);
    physicalTableLabel.setLayoutData(fdPhysicalTableLabel);

    FormData fdPhysicalTableText = new FormData();
    fdPhysicalTableText.left = new FormAttachment(physicalTableLabel, 10);
    fdPhysicalTableText.top = new FormAttachment(0, 0);
    fdPhysicalTableText.right = new FormAttachment(100, 0);
    physicalTableText.setLayoutData(fdPhysicalTableText);

    if (tableModel.getId() != null) {
      businessTableText.setText(tableModel.getId());
      if (initialTableOrColumnSelection == null) {
        businessTableText.selectAll();
      }
    }

    int selectedIndex = -1;
    for (int i = 0; i < schemaMeta.nrTables(); i++) {
      physicalTableText.add(schemaMeta.getTable(i).getId());
      if (null != businessTable.getPhysicalTable()
          && businessTable.getPhysicalTable().getId().equals(schemaMeta.getTable(i).getId())) {
        selectedIndex = i;
      }
    }
    if (-1 != selectedIndex) {
      physicalTableText.select(selectedIndex);
    }

    physicalTableText.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(final SelectionEvent e) {
      }

      public void widgetSelected(final SelectionEvent e) {
        tableModel.setParent(schemaMeta.findPhysicalTable(physicalTableText.getText()));
      }

    });

    return c0;
  }

  protected void okPressed() {
    if (null == tableModel.getParent()) {
      showMissingPhysicalTableError();
      return;
    }
    try {
      // TODO mlowery hack; call should go through a model
      tableModel.getWrappedTable().setId(businessTableText.getText());
    } catch (ObjectAlreadyExistsException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
          "BusinessTableDialog.USER_ERROR_BUSINESS_TABLE_ID_EXISTS", businessTableText.getText()));
      return;
    }

    super.okPressed();
  }

}
