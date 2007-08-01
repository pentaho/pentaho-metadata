/*
 * Copyright 2007 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
 *
 * @created May 22, 2007 
 * @author wseyler
 */


package org.pentaho.pms.schema.dialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.core.repository.ISolutionRepository;
import org.pentaho.core.util.PublisherUtil;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;

/**
 * @author wseyler
 *
 */
public class PublishDialog extends TitleAreaDialog {
  private SchemaMeta schemaMeta;
  
  private LogWriter log;
  private Props props;
  
  private String serverURL;
  private String solutionName;
  private String fileName = "metadata.xmi"; //$NON-NLS-1$
  
  private String userId;
  private String userPassword;
  private String publishPassword;
  
  private Text tServerURL;
  private Text tSolutionName;
  
  private Text tUserId;
  private Text tUserPassword;
  private Text tPublishPassword;
  

  /**
   * @param parent
   */
  public PublishDialog(Shell parent, SchemaMeta schemaMeta) {
    super(parent);
    
    this.schemaMeta = schemaMeta;
    log = LogWriter.getInstance();
    props = Props.getInstance();
  }

  protected Control createContents(Composite parent) {
    Control contents = super.createContents(parent);
    setMessage(Messages.getString("PublishDialog.USER_DIALOG_MESSAGE")); //$NON-NLS-1$
    setTitle(Messages.getString("PublishDialog.USER_DIALOG_TITLE")); //$NON-NLS-1$
    return contents;
  }

  protected Control createDialogArea(final Composite parent) {
    
    Composite c0 = (Composite) super.createDialogArea(parent);
    Composite c1 = new Composite(c0, SWT.NONE);
    
    GridLayout gridLayout = new GridLayout ();
    
    c1.setLayout(gridLayout);
    props.setLook(c1);

    GridData data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    

    Label label0 = new Label (c1, SWT.NONE);
    label0.setText (Messages.getString("PublishDialog.LABEL_SOLUTION"));
    label0.setLayoutData (data);
    
    c0.setBackground(label0.getBackground());
    c1.setBackground(label0.getBackground());

    tSolutionName = new Text (c1, SWT.BORDER);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    tSolutionName.setText("samples"); //default 
    tSolutionName.setLayoutData (data);

    Label label2 = new Label (c1, SWT.NONE);
    label2.setText (Messages.getString("PublishDialog.LABEL_SERVER"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    label2.setLayoutData (data);

    tServerURL = new Text (c1, SWT.BORDER);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    tServerURL.setText("http://localhost:8080/pentaho/RepositoryFilePublisher"); //default 
    tServerURL.setLayoutData (data);

    Label label4 = new Label (c1, SWT.NONE);
    label4.setText (Messages.getString("PublishDialog.LABEL_PUBLISH_PASSWORD"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    label4.setLayoutData (data);

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    
    tPublishPassword = new Text (c1, SWT.BORDER | SWT.PASSWORD);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    tPublishPassword.setLayoutData (data);

    Label label6 = new Label (c1, SWT.NONE);
    label6.setText (Messages.getString("PublishDialog.LABEL_USER"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    label6.setLayoutData (data);

    tUserId = new Text (c1, SWT.BORDER);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    tUserId.setLayoutData (data);

    Label label8 = new Label (c1, SWT.NONE);
    label8.setText (Messages.getString("PublishDialog.LABEL_PASSWORD"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    label8.setLayoutData (data);

    tUserPassword = new Text (c1, SWT.BORDER | SWT.PASSWORD);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    tUserPassword.setLayoutData (data);

    return c0;

  }

  public void dispose() {
    props.setScreen(new WindowProperty(getShell()));
    getShell().dispose();
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.getString("PublishDialog.TITLE")); //$NON-NLS-1$
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Point getInitialSize() {
    return new Point(524, 400);
  }

  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  protected void buttonPressed(int buttonId) {

    switch (buttonId) {
      case IDialogConstants.OK_ID:
        ok();
        break;
      case IDialogConstants.CANCEL_ID:
        cancel();
        break;
    }

    setReturnCode(buttonId);
    close();
  }
  
  private void ok() {
    if (!populateStrings()) {
      return;
    }
    
    CWM cwmInstance = CWM.getInstance(schemaMeta.getDomainName());
    try {
      String xmi = cwmInstance.getXMI();
      BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
      out.write(xmi);
      out.close();
      File file = new File(fileName);
      file.deleteOnExit();
      File[] files = {file};
      int result = PublisherUtil.publish(serverURL , solutionName, files, publishPassword, userId, userPassword, false); //$NON-NLS-1$
      if (result == ISolutionRepository.FILE_EXISTS) {
        MessageBox mb = new MessageBox(getShell(), SWT.NO | SWT.YES | SWT.ICON_WARNING);
        mb.setText(Messages.getString("PublishDialog.FILE_EXISTS")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("PublishDialog.FILE_OVERWRITE")); //$NON-NLS-1$
        if (mb.open() == SWT.YES) {
          result = PublisherUtil.publish(serverURL, solutionName, files, publishPassword, userId, userPassword, true); //$NON-NLS-1$
        } else {
          return;
        }
      }
      if (result != ISolutionRepository.FILE_ADD_SUCCESSFUL) {
        MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
        mb.setText(Messages.getString("PublishDialog.ACTION_FAILED")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("PublishDialog.FILE_SAVE_FAILED", fileName)); //$NON-NLS-1$
        mb.open();
      } else {  // We did it!
        MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
        mb.setText(Messages.getString("PublishDialog.ACTION_SUCCEEDED")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("PublishDialog.FILE_SAVE_SUCCEEDED", fileName)); //$NON-NLS-1$
        mb.open();
        dispose();
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * 
   */
  private boolean populateStrings() {
    String seperatorFwd = "/"; //$NON-NLS-1$
    String seperatorBck = "\\"; //$NON-NLS-1$
    
    serverURL = tServerURL.getText();
//    if (!serverURL.endsWith(seperatorFwd)) {
//      serverURL += seperatorFwd;
//    }
    solutionName = tSolutionName.getText();
    if (solutionName.indexOf(seperatorFwd) >= 0 || solutionName.indexOf(seperatorBck) >= 0) {
      MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
      mb.setText(Messages.getString("PublishDialog.LOCATION_ERROR"));
      mb.setMessage(Messages.getString("PublishDialog.LOCATION_ERROR_INFO"));
      mb.open();
      return false;
    }
   
    userId = tUserId.getText();
    userPassword = tUserPassword.getText();
    publishPassword = tPublishPassword.getText();
    return true;
  }

  private void cancel() {
      dispose();
  }

}
