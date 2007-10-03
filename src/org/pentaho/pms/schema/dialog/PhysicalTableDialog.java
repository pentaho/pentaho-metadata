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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.PhysicalTableModel;
import org.pentaho.pms.schema.concept.editor.PropertyNavigationWidget;
import org.pentaho.pms.schema.concept.editor.PropertyWidgetManager2;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class PhysicalTableDialog extends AbstractTableDialog {

  private static final Log logger = LogFactory.getLog(PhysicalTableDialog.class);

  HashMap modificationsMap = new HashMap();

  public PhysicalTableDialog(Shell parent, PhysicalColumn origPhysicalColumn, SchemaMeta schemaMeta) {
    super(parent);
    PhysicalTable originalPhysicalTable = origPhysicalColumn.getTable();
    PhysicalTable newPhysicalTable = (PhysicalTable) originalPhysicalTable.clone();
    PhysicalTableModel tableModel = new PhysicalTableModel(newPhysicalTable);
    initModificationsMap(originalPhysicalTable, newPhysicalTable);
    init(tableModel, schemaMeta, origPhysicalColumn);
  }
  

  public PhysicalTableDialog(Shell parent, PhysicalTable origPhysicalTable, SchemaMeta schemaMeta) {
    super(parent);
    PhysicalTable newPhysicalTable = (PhysicalTable) origPhysicalTable.clone();
    PhysicalTableModel tableModel = new PhysicalTableModel(newPhysicalTable);
    initModificationsMap(origPhysicalTable, newPhysicalTable);
    init(tableModel, schemaMeta, origPhysicalTable);
  }
  
  private void initModificationsMap(PhysicalTable origPhysicalTable, PhysicalTable workingPhysicalTable) {
    modificationsMap.put(workingPhysicalTable, origPhysicalTable);
    List workingPhysicalColumns = workingPhysicalTable.getPhysicalColumns().getList();
    for (Iterator workingIter = workingPhysicalColumns.iterator(); workingIter.hasNext();) {
      PhysicalColumn workingPhysicalColumn = (PhysicalColumn)workingIter.next();
      List origPhysicalColumns = origPhysicalTable.getPhysicalColumns().getList();
      for (Iterator origIter = origPhysicalColumns.iterator(); origIter.hasNext();) {
        PhysicalColumn origPhysicalColumn = (PhysicalColumn) origIter.next();
        if (origPhysicalColumn.equals(workingPhysicalColumn)) {
          modificationsMap.put(workingPhysicalColumn, origPhysicalColumn);
          break;
        }
      }
    }
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

  protected void okPressed() {
    try {
      if (lastSelection != null) {
        String id = conceptIdText.getText();
        if (id.trim().length() == 0) {
          MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("PhysicalTableDialog.USER_ERROR_INVALID_ID", conceptIdText.getText()));
          tableColumnTree.setSelection(new StructuredSelection(lastSelection));
          conceptIdText.forceFocus();
          conceptIdText.selectAll();
        } else {
          lastSelection.setId(conceptIdText.getText());
          updateOriginalPhysicalTable();
          super.okPressed();
        }
      } else {
        updateOriginalPhysicalTable();
        super.okPressed();
      }
    } catch (ObjectAlreadyExistsException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("PhysicalTableDialog.USER_ERROR_PHYSICAL_TABLE_ID_EXISTS", conceptIdText.getText()));
    }
  }
    
  public void selectionChanged(SelectionChangedEvent e) {
    if (lastSelection != null) {
      try {
        String id = conceptIdText.getText();
        if (id.trim().length() == 0){ 
          // gmoran, PMD-227: Without this check we get into an infinite loop since we reset the selection after 
          // throwing the error message...
          if (!lastSelection.equals(((StructuredSelection)e.getSelection()).getFirstElement())) {
            MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
                "PhysicalTableDialog.USER_ERROR_INVALID_ID", conceptIdText.getText()));
            
            tableColumnTree.setSelection(new StructuredSelection(lastSelection));
            conceptIdText.forceFocus();
            conceptIdText.selectAll();
          }
        } else {
          lastSelection.setId(conceptIdText.getText());
          super.selectionChanged(e);
        }
      } catch (ObjectAlreadyExistsException e1) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e1);
        }
        MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
            "PhysicalTableDialog.USER_ERROR_PHYSICAL_TABLE_ID_EXISTS", conceptIdText.getText()));
      }
    } else {
      super.selectionChanged(e);
    }
  }
  
  private void updateOriginalPhysicalTable() {
    // Find the original physical table.
    PhysicalTable origTable = null;
    for (Iterator iterator = modificationsMap.values().iterator(); iterator.hasNext() && (origTable == null);) {
      Object target = iterator.next();
      if (target instanceof PhysicalTable) {
        origTable = (PhysicalTable)target;
      }
    }
    
    // Remove any columns from the original physical table that were removed from the working copy.
    ArrayList entriesToRemove = new ArrayList();
    Set entrySet = modificationsMap.entrySet();
    for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
      boolean found = false;
      Map.Entry entry = (Map.Entry)iterator.next();
      if (entry.getKey() instanceof PhysicalColumn) {
        ConceptUtilityInterface[] workingColumns = tableModel.getColumns();
        for (int i = 0; (i < workingColumns.length) && !found; i++) {
          found = (workingColumns[i] == entry.getKey());
        }
        if (!found) {
          PhysicalColumn column = origTable.findPhysicalColumn(((PhysicalColumn)entry.getValue()).getId());
          int index = origTable.indexOfPhysicalColumn(column);
          origTable.removePhysicalColumn(index);
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
          origTable.addPhysicalColumn((PhysicalColumn)workingColumns[i]);
        } catch (ObjectAlreadyExistsException e) {
          // This should not happen as this exception would already have been caught earlier...
        }
      }
    }
  }
  
  protected Composite createConceptEditor() {
    Composite conceptEditor = new Composite(cardComposite, SWT.NONE);
    conceptEditor.setLayout(new FillLayout());

    Group group = new Group(conceptEditor, SWT.SHADOW_OUT);
    group.setText("Properties");
    group.setLayout(new GridLayout());
    
    SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
    s0.SASH_WIDTH = 10;
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
    new Label(rightComposite, SWT.RIGHT);
    Combo fillerCombo = new Combo(rightComposite, SWT.NONE);
    fillerCombo.setVisible(false);
    
    propertyWidgetManager = new PropertyWidgetManager2(rightComposite, SWT.NONE, propertyEditorContext, schemaMeta.getSecurityReference());
    propertyWidgetManager.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.heightHint = 20;
    s0.setLayoutData(gridData);
    s0.setWeights(new int[] { 1, 2 });
    return conceptEditor;
  }
  





}
