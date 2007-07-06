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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.BusinessTableModel;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class BusinessTableDialog extends AbstractTableDialog {

  private static final Log logger = LogFactory.getLog(BusinessTableDialog.class);

  private Combo physicalTableText;

  private Text businessTableText;

  private BusinessTable businessTable;

  HashMap modificationsMap = new HashMap();

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.getString("BusinessTableDialog.USER_BUSINESS_TABLE_PROPERTIES"));
  }

  public BusinessTableDialog(Shell parent, BusinessColumn businessColumn, SchemaMeta schemaMeta) {
    super(parent);
    BusinessTable originalBusinessTable = businessColumn.getBusinessTable();
    businessTable = (BusinessTable) originalBusinessTable.clone();
    BusinessTableModel tableModel = new BusinessTableModel(businessTable, schemaMeta.getActiveModel());
    initModificationsMap(originalBusinessTable, businessTable);
    init(tableModel, schemaMeta, businessColumn);
  }
  
  public BusinessTableDialog(Shell parent, BusinessTable originalBusinessTable, SchemaMeta schemaMeta) {
    super(parent);
    businessTable = (BusinessTable) originalBusinessTable.clone();
    BusinessTableModel tableModel = new BusinessTableModel(businessTable, schemaMeta.getActiveModel());
    initModificationsMap(originalBusinessTable, businessTable);
    init(tableModel, schemaMeta, businessTable);
  }
  
  private void initModificationsMap(BusinessTable origBusinessTable, BusinessTable workingBusinessTable) {
    modificationsMap.put(workingBusinessTable, origBusinessTable);
    List workingBusinessColumns = workingBusinessTable.getBusinessColumns().getList();
    for (Iterator workingIter = workingBusinessColumns.iterator(); workingIter.hasNext();) {
      BusinessColumn workingBusinessColumn = (BusinessColumn)workingIter.next();
      List origBusinessColumns = origBusinessTable.getBusinessColumns().getList();
      for (Iterator origIter = origBusinessColumns.iterator(); origIter.hasNext();) {
        BusinessColumn origBusinessColumn = (BusinessColumn) origIter.next();
        if (origBusinessColumn.equals(workingBusinessColumn)) {
          modificationsMap.put(workingBusinessColumn, origBusinessColumn);
          break;
        }
      }
    }
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
    try {
      if (lastSelection != null) {
        String id = businessTableText.getText();
        if (id.trim().length() == 0) {
          MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_INVALID_ID", businessTableText.getText()));
          tableColumnTree.setSelection(new StructuredSelection(lastSelection));
          businessTableText.forceFocus();
          businessTableText.selectAll();
        } else {
          lastSelection.setId(businessTableText.getText());
          updateOriginalBusinessTable();
          super.okPressed();
        }
      } else {
        updateOriginalBusinessTable();
        super.okPressed();
      }
    } catch (ObjectAlreadyExistsException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_PHYSICAL_TABLE_ID_EXISTS", businessTableText.getText()));
    }
  }

  protected void showId(String id) {
    businessTableText.setText(id);
  }
  
  public void selectionChanged(SelectionChangedEvent e) {
    if (lastSelection != null) {
      try {
        String id = businessTableText.getText();
        if (id.trim().length() == 0) {
          MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
              "BusinessTableDialog.USER_ERROR_INVALID_ID", businessTableText.getText()));
          tableColumnTree.setSelection(new StructuredSelection(lastSelection));
          businessTableText.forceFocus();
          businessTableText.selectAll();
        } else {
          lastSelection.setId(businessTableText.getText());
          super.selectionChanged(e);
        }
      } catch (ObjectAlreadyExistsException e1) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e1);
        }
        MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
            "BusinessTableDialog.USER_ERROR_BUSINESS_TABLE_ID_EXISTS", businessTableText.getText()));
      }
    } else {
      super.selectionChanged(e);
    }
  }

  private void updateOriginalBusinessTable() {
    // Find the original physical table.
    BusinessTable origTable = null;
    for (Iterator iterator = modificationsMap.values().iterator(); iterator.hasNext() && (origTable == null);) {
      Object target = iterator.next();
      if (target instanceof BusinessTable) {
        origTable = (BusinessTable)target;
      }
    }
    
    // Remove any columns from the original physical table that were removed from the working copy.
    ArrayList entriesToRemove = new ArrayList();
    Set entrySet = modificationsMap.entrySet();
    for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
      boolean found = false;
      Map.Entry entry = (Map.Entry)iterator.next();
      if (entry.getKey() instanceof BusinessColumn) {
        ConceptUtilityInterface[] workingColumns = tableModel.getColumns();
        for (int i = 0; (i < workingColumns.length) && !found; i++) {
          found = (workingColumns[i] == entry.getKey());
        }
        if (!found) {
          BusinessColumn column = origTable.findBusinessColumn(((BusinessColumn)entry.getValue()).getId());
          int index = origTable.indexOfBusinessColumn(column);
          origTable.removeBusinessColumn(index);
          entriesToRemove.add(entry);
        }
      }
    }
    entrySet.removeAll(entriesToRemove);
    
    // Update the remaining columns in the physical table with the working info.
    for (Iterator iterator = modificationsMap.entrySet().iterator(); iterator.hasNext();) {
      Map.Entry entry = (Map.Entry)iterator.next();
      ConceptUtilityInterface origConcept = (ConceptUtilityInterface)entry.getValue();
      ConceptUtilityInterface workingConcept = (ConceptUtilityInterface)entry.getKey();
      try {
        origConcept.setId(workingConcept.getId());
      } catch (ObjectAlreadyExistsException e) {
        // This should not happen as this exception would already have been caught earlier...
      }
      origConcept.getConcept().clearChildProperties();
      origConcept.getConcept().getChildPropertyInterfaces().putAll(workingConcept.getConcept().getChildPropertyInterfaces());
    }

    // Add any columns from the working table that don't exist in the original table.
    ConceptUtilityInterface[] workingColumns = tableModel.getColumns();
    for (int i = 0; i < workingColumns.length; i++) {
      boolean found = false;
      for (Iterator iterator = modificationsMap.keySet().iterator(); iterator.hasNext() && !found;) {
        found = (workingColumns[i] == iterator.next());
      }
      if (!found) {
        try {
          origTable.addBusinessColumn((BusinessColumn)workingColumns[i]);
        } catch (ObjectAlreadyExistsException e) {
          // This should not happen as this exception would already have been caught earlier...
        }
      }
    }
  }
}
