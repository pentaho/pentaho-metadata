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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.ConceptEditorWidget;
import org.pentaho.pms.schema.concept.editor.ConceptModel;
import org.pentaho.pms.schema.concept.editor.IConceptModel;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

/***
 * Represents a business model
 *
 * @since 30-aug-2006
 *
 */
public class BusinessModelDialog extends Dialog {

  private static final Log logger = LogFactory.getLog(BusinessModelDialog.class);

  protected String activeLocale;

  protected Map propertyEditorContext = new HashMap();

  private Text wId;

  private IConceptModel conceptModel;

  private ConceptUtilityInterface conceptUtil;

  private Locales locales;

  public BusinessModelDialog(final Shell parent, final int style, final ConceptUtilityInterface conceptUtil,
      final SchemaMeta schemaMeta) {
    super(parent);
    this.conceptModel = new ConceptModel(conceptUtil.getConcept());
    this.conceptUtil = conceptUtil;
    Locales locales = schemaMeta.getLocales();
    activeLocale = locales.getActiveLocale();
    propertyEditorContext.put("locales", locales);
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Business Model Properties");
  }

  protected Point getInitialSize() {
    return new Point(600, 500);
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

    ConceptEditorWidget conceptEditor = new ConceptEditorWidget(container, SWT.NONE, conceptModel,
        propertyEditorContext);

    FormData fdConcept = new FormData();
    fdConcept.left = new FormAttachment(0, 0);
    fdConcept.top = new FormAttachment(top, 10);
    fdConcept.right = new FormAttachment(100, 0);
    fdConcept.bottom = new FormAttachment(100, 0);
    conceptEditor.setLayoutData(fdConcept);

    return c0;
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

    FormData fdId = new FormData();
    fdId.left = new FormAttachment(wlId, 10);
    fdId.top = new FormAttachment(0, 0);
    fdId.right = new FormAttachment(100, 0);
    wId.setLayoutData(fdId);

    if (conceptUtil.getId() != null) {
      wId.setText(conceptUtil.getId());
      wId.selectAll();
    }
    return c0;
  }

  protected void okPressed() {
    try {
      conceptUtil.setId(wId.getText());
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
