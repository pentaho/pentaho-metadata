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

package org.pentaho.pms.schema.dialog;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.ConceptEditorWidget;
import org.pentaho.pms.schema.concept.editor.ConceptModelRegistry;
import org.pentaho.pms.schema.concept.editor.Constants;
import org.pentaho.pms.schema.concept.editor.IConceptModel;
import org.pentaho.pms.schema.concept.editor.ITableModel;
import org.pentaho.pms.schema.concept.editor.TableColumnTreeWidget;

public abstract class AbstractTableDialog extends Dialog {

  protected ITableModel tableModel;

  protected String activeLocale;

  protected Composite detailsComposite;

  protected StackLayout stackLayout;

  protected Composite cardComposite;

  protected Map propertyEditorContext = new HashMap();

  protected ConceptModelRegistry conceptModelRegistry = new ConceptModelRegistry();

  protected Map cards = new HashMap();

  protected Control defaultCard;

  protected TableColumnTreeWidget tableColumnTree;

  protected SchemaMeta schemaMeta;

  protected ISelectionChangedListener tableColumTreeSelectionChangedListener;

  private static final Log logger = LogFactory.getLog(AbstractTableDialog.class);

  public AbstractTableDialog(final Shell parent, final int style, final ITableModel tableModel, final SchemaMeta schemaMeta) {
    super(parent);
    this.tableModel = tableModel;
    this.schemaMeta = schemaMeta;
    Locales locales = schemaMeta.getLocales();
    activeLocale = locales.getActiveLocale();
    propertyEditorContext.put("locales", locales);
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Point getInitialSize() {
    return new Point(800, 500);
  }

  protected final Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new FormLayout());
    GridData gdContainer = new GridData(GridData.FILL_BOTH);
    container.setLayoutData(gdContainer);

    Control top = createTop(container);

    FormData fdTop = new FormData();
    fdTop.left = new FormAttachment(0, 0);
    fdTop.top = new FormAttachment(0, 0);
    fdTop.right = new FormAttachment(100, 0);
    top.setLayoutData(fdTop);

    SashForm s0 = new SashForm(container, SWT.HORIZONTAL);
    FormData fdSash = new FormData();
    fdSash.left = new FormAttachment(0, 0);
    fdSash.top = new FormAttachment(top, 10);
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
      public void widgetSelected(SelectionEvent e) {
        addColumnPressed();
      }
    });

    final ToolItem delButton = new ToolItem(tb, SWT.PUSH);
    delButton.setToolTipText(Messages.getString("PhysicalTableDialog.USER_DELETE_COLUMN")); //$NON-NLS-1$
    delButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("column-del-button"));
    delButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        delColumnPressed();
      }
    });

    tableColumTreeSelectionChangedListener = new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent e) {
        if (logger.isDebugEnabled()) {
          logger.debug("heard tableColumnTree selection changed event: " + e);
          logger.debug("attempting to swap cards");
        }
        if (!e.getSelection().isEmpty()) {
          TreeSelection treeSel = (TreeSelection) e.getSelection();
          if (treeSel.getFirstElement() instanceof ConceptUtilityInterface) {
            ConceptUtilityInterface cu = (ConceptUtilityInterface) treeSel.getFirstElement();
            if (tableModel.isColumn(cu)) {
              delButton.setEnabled(true);
            } else {
              delButton.setEnabled(false);
            }
            swapCard(cu.getConcept());
          }
        }
      }
    };

    tableColumnTree.addSelectionChangedListener(tableColumTreeSelectionChangedListener);

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

  /**
   * Creates the top of the dialog area. Below this will be the columns and property editors.
   */
  protected abstract Control createTop(final Composite parent);

  protected abstract void addColumnPressed();

  protected void okPressed() {
    cleanup();
    super.okPressed();
  }

  protected void cleanup() {
      tableColumnTree.removeSelectionChangedListener(tableColumTreeSelectionChangedListener);
  }

  protected void delColumnPressed() {
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
    cleanup();
    super.cancelPressed();
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

}
