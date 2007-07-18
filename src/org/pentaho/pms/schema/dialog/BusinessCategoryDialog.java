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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.ConceptModel;
import org.pentaho.pms.schema.concept.editor.IConceptModel;
import org.pentaho.pms.schema.concept.editor.PropertyNavigationWidget;
import org.pentaho.pms.schema.concept.editor.PropertyWidgetManager2;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;


/***
 * Represents a business category
 * 
 * @since 30-aug-2006
 *
 */
public class BusinessCategoryDialog extends Dialog
{
  private static final Log logger = LogFactory.getLog(BusinessCategoryDialog.class);
  protected Map propertyEditorContext = new HashMap();
  private Text wId;
  private IConceptModel conceptModel;
  private ConceptUtilityInterface conceptUtil;
  private SchemaMeta schemaMeta;

  public BusinessCategoryDialog(Shell parent, ConceptUtilityInterface conceptUtil, SchemaMeta schemaMeta) {
    super(parent);
    this.conceptModel = new ConceptModel(conceptUtil.getConcept());
    this.conceptUtil = conceptUtil;
    this.schemaMeta = schemaMeta;
    propertyEditorContext.put("locales", schemaMeta.getLocales());
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Business Model Properties");
  }

  protected Point getInitialSize() {
    return new Point(1000, 800);
  }

  protected final Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new GridLayout(2, false));
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    Label wlId = new Label(container, SWT.RIGHT);
    wlId.setText(Messages.getString("PhysicalTableDialog.USER_NAME_ID")); //$NON-NLS-1$
    wId = new Text(container, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    if (conceptUtil.getId() != null) {
      wId.setText(conceptUtil.getId());
      wId.selectAll();
    }
    
    Group group = new Group(container, SWT.SHADOW_OUT);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    group.setLayoutData(gridData);
    group.setText("Properties");
    group.setLayout(new FillLayout());
    SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
    s0.SASH_WIDTH = 10;
    PropertyNavigationWidget propertyNavigationWidget = new PropertyNavigationWidget(s0, SWT.NONE);
    propertyNavigationWidget.setConceptModel(conceptModel);
    PropertyWidgetManager2 propertyWidgetManager = new PropertyWidgetManager2(s0, SWT.NONE, propertyEditorContext, schemaMeta.getSecurityReference());
    propertyWidgetManager.setConceptModel(conceptModel);
    propertyNavigationWidget.addSelectionChangedListener(propertyWidgetManager);
    s0.setWeights(new int[] { 1, 2 });
    
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
          "PhysicalTableDialog.USER_ERROR_CATEGORY_ID_EXISTS", wId.getText()));
      return;
    }

    super.okPressed();
  }
}
