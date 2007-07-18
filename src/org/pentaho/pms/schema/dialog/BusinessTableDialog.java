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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
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
import org.pentaho.pms.schema.concept.editor.PropertyNavigationWidget;
import org.pentaho.pms.schema.concept.editor.PropertyWidgetManager2;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class BusinessTableDialog extends AbstractTableDialog implements SelectionListener {

  private static final Log logger = LogFactory.getLog(BusinessTableDialog.class);

  private Combo physicalTableText;
  
  private Label physicalTableLabel;

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


  protected void okPressed() {
    try {
      if (lastSelection != null) {
        String id = conceptIdText.getText();
        if (id.trim().length() == 0) {
          MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_INVALID_ID", conceptIdText.getText()));
          tableColumnTree.setSelection(new StructuredSelection(lastSelection));
          conceptIdText.forceFocus();
          conceptIdText.selectAll();
        } else {
          lastSelection.setId(conceptIdText.getText());
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
      MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_PHYSICAL_TABLE_ID_EXISTS", conceptIdText.getText()));
    }
  }

  public void selectionChanged(SelectionChangedEvent e) {
    if (lastSelection != null) {
      try {
        String id = conceptIdText.getText();
        if (id.trim().length() == 0) {
          MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
              "BusinessTableDialog.USER_ERROR_INVALID_ID", conceptIdText.getText()));
          tableColumnTree.setSelection(new StructuredSelection(lastSelection));
          conceptIdText.forceFocus();
          conceptIdText.selectAll();
        } else {
          lastSelection.setId(conceptIdText.getText());
          super.selectionChanged(e);
        }
      } catch (ObjectAlreadyExistsException e1) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e1);
        }
        MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
            "BusinessTableDialog.USER_ERROR_BUSINESS_TABLE_ID_EXISTS", conceptIdText.getText()));
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
  
  protected void editConcept(ConceptUtilityInterface cu) {
    if (cu instanceof BusinessTable) {
      physicalTableLabel.setVisible(true);
      physicalTableText.setVisible(true);
    } else {
      physicalTableLabel.setVisible(false);
      physicalTableText.setVisible(false);
    }
    
    super.editConcept(cu);
  }
  
  protected Composite createConceptEditor() {
    
    Composite conceptEditor = new Composite(cardComposite, SWT.NONE);
    conceptEditor.setLayout(new FillLayout());

    Group group = new Group(conceptEditor, SWT.SHADOW_OUT);
    group.setText("Properties");
    group.setLayout(new GridLayout());
    
    SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
    s0.SASH_WIDTH = 5;
    
    Composite leftComposite = new Composite(s0, SWT.NONE);
    leftComposite.setLayout(new GridLayout());
    Label wlId = new Label(leftComposite, SWT.RIGHT);
    wlId.setText(Messages.getString("PhysicalTableDialog.USER_NAME_ID")); //$NON-NLS-1$
    conceptIdText = new Text(leftComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    conceptIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    propertyNavigationWidget = new PropertyNavigationWidget(leftComposite, SWT.NONE);
    propertyNavigationWidget.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    Composite rightComposite = new Composite(s0, SWT.NONE);
    rightComposite.setLayout(new GridLayout());
    physicalTableLabel = new Label(rightComposite, SWT.RIGHT);
    physicalTableLabel.setText(Messages.getString("BusinessTableDialog.USER_PHYSICAL_TABLE")); //$NON-NLS-1$
    physicalTableText = new Combo(rightComposite, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
    physicalTableText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
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
    
    physicalTableText.addSelectionListener(this);
    
    propertyWidgetManager = new PropertyWidgetManager2(rightComposite, SWT.NONE, propertyEditorContext, schemaMeta.getSecurityReference());
    propertyWidgetManager.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.heightHint = 20;
    s0.setLayoutData(gridData);
    s0.setWeights(new int[] { 1, 2 });
    
    if (tableModel.getId() != null) {
      conceptIdText.setText(tableModel.getId());
      if (initialTableOrColumnSelection == null) {
        conceptIdText.selectAll();
      }
    }
    
    return conceptEditor;
  }

  public void widgetDefaultSelected(SelectionEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  public void widgetSelected(SelectionEvent arg0) {
    tableModel.setParent(schemaMeta.findPhysicalTable(physicalTableText.getText()));
  }
}
