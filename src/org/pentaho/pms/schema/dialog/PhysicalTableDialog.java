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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.ITableModel;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class PhysicalTableDialog extends AbstractTableDialog {

  private static final Log logger = LogFactory.getLog(PhysicalTableDialog.class);

  private Text wId;

  public PhysicalTableDialog(Shell parent, int style, ITableModel tableModel, SchemaMeta schemaMeta) {   
    this(parent, style, tableModel, schemaMeta, null);
  }
  
  public PhysicalTableDialog(Shell parent, int style, ITableModel tableModel, SchemaMeta schemaMeta, ConceptUtilityInterface selectedTableOrColumn) {
    super(parent, style, tableModel, schemaMeta, selectedTableOrColumn);
  }
  

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.getString("PhysicalTableDialog.USER_TABLE_PROPERTIES"));
  }

  protected void addColumnPressed() {
    // Ask for the ID
    String startId = Settings.getPhysicalColumnIDPrefix();
    if (Settings.isAnIdUppercase()) {
      startId = startId.toUpperCase();
    }
    InputDialog dialog = new InputDialog(getShell(), Messages.getString("PhysicalTableDialog.USER_TITLE_NEW_COLUMN"),
        Messages.getString("PhysicalTableDialog.USER_NEW_COLUMN_NAME"), startId, null);
    dialog.open();
    String id = dialog.getValue();
    if (null != id) {
      try {
        tableModel.addColumn(id, activeLocale);
      } catch (ObjectAlreadyExistsException e) {
        MessageDialog.openError(getShell(), "Column Add Error", "A column with id '" + id + "' already exists.");
      }
    }
  }

  protected Control createTop(final Composite parent) {
    Composite c0 = new Composite(parent, SWT.NONE);
    c0.setLayout(new FormLayout());

    Label wlId = new Label(c0, SWT.RIGHT);
    wlId.setText(Messages.getString("PhysicalTableDialog.USER_NAME_ID")); //$NON-NLS-1$
    wId = new Text(c0, SWT.SINGLE | SWT.LEFT | SWT.BORDER);

    FormData fdlId = new FormData();
    fdlId.left = new FormAttachment(0, 0);

    fdlId.top = new FormAttachment(wId, 0, SWT.CENTER);
    wlId.setLayoutData(fdlId);

    wId.setText(""); //$NON-NLS-1$

    //    wId.addModifyListener(lsMod);
    FormData fdId = new FormData();
    fdId.left = new FormAttachment(wlId, 10);
    fdId.top = new FormAttachment(0, 0);
    fdId.right = new FormAttachment(100, 0);
    wId.setLayoutData(fdId);

    if (tableModel.getId() != null) {
      wId.setText(tableModel.getId());
      if (initialTableOrColumnSelection == null) {
        wId.selectAll();
      }
    }
    return c0;
  }

  protected void okPressed() {
    try {
      // TODO mlowery hack; call should go through a model
      tableModel.getWrappedTable().setId(wId.getText());
    } catch (ObjectAlreadyExistsException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
          "PhysicalTableDialog.USER_ERROR_PHYSICAL_TABLE_ID_EXISTS", wId.getText()));
      return;
    }

    super.okPressed();
  }

}
