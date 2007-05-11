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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.ConceptEditorWidget;
import org.pentaho.pms.schema.concept.editor.ConceptModelRegistry;
import org.pentaho.pms.schema.concept.editor.Constants;
import org.pentaho.pms.schema.concept.editor.IConceptModel;
import org.pentaho.pms.schema.concept.editor.ITableModel;
import org.pentaho.pms.schema.concept.editor.TableColumnTreeWidget;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class TableDialog extends Dialog {

  private Text wId;

  private String tablename;

  private ITableModel tableModel;

  private String activeLocale;

  private Composite detailsComposite;

  private StackLayout stackLayout;

  private Composite cardComposite;

  private Map propertyEditorContext = new HashMap();

  private ConceptModelRegistry conceptModelRegistry = new ConceptModelRegistry();

  private Map cards = new HashMap();

  private Control defaultCard;

  private TableColumnTreeWidget tableColumnTree;

  private AddColumnHandler addColumnHandler;

  private static final Log logger = LogFactory.getLog(TableDialog.class);

  public TableDialog(Shell parent, int style, ITableModel tableModel, Locales locales,
      SecurityReference securityReference, AddColumnHandler addColumnHandler) {
    super(parent);
    this.tableModel = tableModel;
    activeLocale = locales.getActiveLocale();
    propertyEditorContext.put("locales", locales);
    this.addColumnHandler = addColumnHandler;
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.getString("PhysicalTableDialog.USER_PHYSICAL_TABLE_PROPERTIES"));
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Point getInitialSize() {
    return new Point(800, 500);
  }

  protected Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new FormLayout());
    GridData gdContainer = new GridData(GridData.FILL_BOTH);
    container.setLayoutData(gdContainer);

    //    lsMod = new ModifyListener() {
    //      public void modifyText(ModifyEvent e) {
    //        modifiedTableModel.setChanged();
    //      }
    //    };

    Label wlId = new Label(container, SWT.RIGHT);
    wlId.setText(Messages.getString("PhysicalTableDialog.USER_NAME_ID")); //$NON-NLS-1$
    wId = new Text(container, SWT.SINGLE | SWT.LEFT | SWT.BORDER);

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

    SashForm s0 = new SashForm(container, SWT.HORIZONTAL);
    FormData fdSash = new FormData();
    fdSash.left = new FormAttachment(0, 0);
    fdSash.top = new FormAttachment(wId, 10);
    fdSash.right = new FormAttachment(100, 0);
    fdSash.bottom = new FormAttachment(100, 0);
    s0.setLayoutData(fdSash);

    Composite c12 = new Composite(s0, SWT.NONE);
    c12.setLayout(new FormLayout());

    detailsComposite = new Composite(c12, SWT.NONE);
    FormData fdDetailsComposite = new FormData();
    fdDetailsComposite.top = new FormAttachment(0, 0);
    fdDetailsComposite.left = new FormAttachment(0, 0);
    fdDetailsComposite.right = new FormAttachment(100, -5);
    fdDetailsComposite.bottom = new FormAttachment(100, 0);
    detailsComposite.setLayoutData(fdDetailsComposite);

    detailsComposite.setLayout(new FormLayout());

    Label wlList = new Label(detailsComposite, SWT.NONE);
    wlList.setText(Messages.getString("PhysicalTableDialog.USER_SUBJECT")); //$NON-NLS-1$
    wlList.setFont(Constants.getFontRegistry(Display.getCurrent()).get("prop-mgmt-title"));

    tableColumnTree = new TableColumnTreeWidget(detailsComposite, SWT.NONE, tableModel, true);

    tableColumnTree.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent e) {
        if (logger.isDebugEnabled()) {
          logger.debug("heard tableColumnTree selection changed event: " + e);
          logger.debug("attempting to swap cards");
        }
        if (!e.getSelection().isEmpty()) {
          TreeSelection treeSel = (TreeSelection) e.getSelection();
          if (treeSel.getFirstElement() instanceof ConceptUtilityInterface) {
            ConceptUtilityInterface cu = (ConceptUtilityInterface) treeSel.getFirstElement();
            swapCard(cu.getConcept());
          }
        }
      }
    });

    FormData fdList = new FormData();
    fdList.top = new FormAttachment(0, 38);
    fdList.left = new FormAttachment(0, 0);
    fdList.right = new FormAttachment(100, 0);
    fdList.bottom = new FormAttachment(100, 0);
    tableColumnTree.setLayoutData(fdList);

    FormData fdlList = new FormData();
    fdlList.left = new FormAttachment(0, 0);
    fdlList.bottom = new FormAttachment(tableColumnTree, -10);
    wlList.setLayoutData(fdlList);

    ToolBar tb = new ToolBar(detailsComposite, SWT.FLAT);
    FormData fdToolBar = new FormData();
    fdToolBar.top = new FormAttachment(0, 0);
    fdToolBar.right = new FormAttachment(100, 0);
    tb.setLayoutData(fdToolBar);

    ToolItem addButton = new ToolItem(tb, SWT.PUSH);

    addButton.setToolTipText(Messages.getString("PhysicalTableDialog.USER_ADD_NEW_COLUMN")); //$NON-NLS-1$
    addButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("column-add-button"));
    addButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent arg0) {
        addColumn();
      }
    });

    ToolItem delButton = new ToolItem(tb, SWT.PUSH);
    delButton.setToolTipText(Messages.getString("PhysicalTableDialog.USER_DELETE_COLUMN")); //$NON-NLS-1$
    delButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("column-del-button"));
    delButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent arg0) {
        delColumn();
      }
    });

    if (tableModel.getId() != null) {
      wId.setText(tableModel.getId());
      wId.selectAll();
    }

    Composite spacer = new Composite(s0, SWT.NONE);
    spacer.setLayout(new FormLayout());

    cardComposite = new Composite(spacer, SWT.NONE);

    FormData fdCardComposite = new FormData();
    fdCardComposite.top = new FormAttachment(0, 0);
    fdCardComposite.left = new FormAttachment(0, 5);
    fdCardComposite.right = new FormAttachment(100, 0);
    fdCardComposite.bottom = new FormAttachment(100, 0);
    cardComposite.setLayoutData(fdCardComposite);

    stackLayout = new StackLayout();
    cardComposite.setLayout(stackLayout);

    defaultCard = new DefaultCard(cardComposite, SWT.NONE);

    swapCard(null);

    s0.setWeights(new int[] { 1, 3 });

    return c0;
  }

  private void addColumn() {
    addColumnHandler.handleAddColumn(getShell(), tableModel, activeLocale);
  }

  private void delColumn() {
    // get the currently selected column
    TreeSelection treeSel = (TreeSelection) tableColumnTree.getSelection();
    ConceptUtilityInterface conceptHolder = (ConceptUtilityInterface) treeSel.getFirstElement();
    boolean delete = MessageDialog.openConfirm(getShell(), "Confirm Column Delete",
        "Are you sure you want to delete the column with id '" + conceptHolder.getId() + "'?");
    if (delete) {
      tableModel.removeColumn(conceptHolder.getId());
    }
  }

  private void swapCard(final ConceptInterface concept) {
    if (null == concept) {
      stackLayout.topControl = defaultCard;
    } else {
      if (null == cards.get(concept)) {
        IConceptModel conceptModel = conceptModelRegistry.getConceptModel(concept);
        ConceptEditorWidget conceptEditor = new ConceptEditorWidget(cardComposite, SWT.NONE, conceptModel,
            propertyEditorContext);
        cards.put(concept, conceptEditor);
      }
      stackLayout.topControl = (Control) cards.get(concept);
    }
    cardComposite.layout();
  }

  protected void cancelPressed() {
    tablename = null;
    //		dispose();
    super.cancelPressed();
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

  public String getTablename() {
    return tablename;
  }

  /**
   * The card that shows when there is no selection in the table-column tree.
   */
  private class DefaultCard extends Composite {

    public DefaultCard(final Composite parent, final int style) {
      super(parent, style);
      createContents();
    }

    private void createContents() {
      setLayout(new GridLayout());
      Label lab0 = new Label(this, SWT.CENTER);
      lab0.setText("Select the table or any of its columns to begin editing properties.");
      GridData gd = new GridData();
      gd.verticalAlignment = GridData.CENTER;
      gd.horizontalAlignment = GridData.CENTER;
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      lab0.setLayoutData(gd);
    }

  }

  public interface AddColumnHandler {
    void handleAddColumn(final Shell shell, final ITableModel tableModel, final String activeLocale);
  }
}
