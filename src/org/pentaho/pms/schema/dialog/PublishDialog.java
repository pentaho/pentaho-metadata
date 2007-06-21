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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
public class PublishDialog extends Dialog {
  private SchemaMeta schemaMeta;
  
  private LogWriter    log;
  private Props props;
  
  private String serverURL;
  private String solutionName;
  private String fileName = "metadata.xmi"; //$NON-NLS-1$
  
  private String userId;
  private String userPassword;
  private String publishPassword;
  
  private Text tServerURL;
  private Text tSolutionName;
//  private Text tFileName;
  
  private Text tUserId;
  private Text tUserPassword;
  private Text tPublishPassword;
  
  private Button wOK, wCancel;
  private Listener lsOK, lsCancel;

  Shell dialog;
  /**
   * @param parent
   */
  public PublishDialog(Shell parent, SchemaMeta schemaMeta) {
    super(parent, SWT.NONE);
    
    this.schemaMeta = schemaMeta;
    
    log = LogWriter.getInstance();
    props = Props.getInstance();
  }

  public void open()
  {
    log.logDebug(this.getClass().getName(), Messages.getString("General.DEBUG_OPENING_DIALOG")); //$NON-NLS-1$

    Shell parent = getParent();
    Display display = parent.getDisplay();
    
    dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
    dialog.setText(Messages.getString("PublishDialog.TITLE")); //$NON-NLS-1$

    GridLayout gridLayout = new GridLayout(2, false);
    dialog.setLayout(gridLayout);
    
    GridData fieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
    Label label = new Label(dialog, SWT.LEFT);
    label.setText(Messages.getString("PublishDialog.LABEL_USER")); //$NON-NLS-1$
    tUserId = new Text(dialog, SWT.LEFT | SWT.SINGLE);
    tUserId.setLayoutData(fieldLayoutData);
    label = new Label(dialog, SWT.LEFT);
    label.setText(Messages.getString("PublishDialog.LABEL_PASSWORD")); //$NON-NLS-1$
    tUserPassword = new Text(dialog, SWT.LEFT | SWT.SINGLE | SWT.PASSWORD);
    tUserPassword.setLayoutData(fieldLayoutData);
    label = new Label(dialog, SWT.LEFT);
    label.setText(Messages.getString("PublishDialog.LABEL_PUBLISH_PASSWORD")); //$NON-NLS-1$
    tPublishPassword = new Text(dialog, SWT.LEFT | SWT.SINGLE | SWT.PASSWORD);
    tPublishPassword.setLayoutData(fieldLayoutData);
    
    label = new Label(dialog, SWT.LEFT);
    label.setText(Messages.getString("PublishDialog.LABEL_SERVER")); //$NON-NLS-1$
    tServerURL = new Text(dialog, SWT.LEFT | SWT.SINGLE);
    tServerURL.setLayoutData(fieldLayoutData);
    label = new Label(dialog, SWT.LEFT);
    label.setText(Messages.getString("PublishDialog.LABEL_SOLUTION")); //$NON-NLS-1$
    tSolutionName = new Text(dialog, SWT.LEFT | SWT.SINGLE);
    tSolutionName.setLayoutData(fieldLayoutData);
//    label = new Label(dialog, SWT.LEFT);
//    label.setText(Messages.getString("PublishDialog.LABEL_FILENAME")); //$NON-NLS-1$
//    tFileName = new Text(dialog, SWT.LEFT | SWT.SINGLE);
//    tFileName.setLayoutData(fieldLayoutData);

    // The buttons...
    wCancel = new Button(dialog, SWT.PUSH);
    wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$  
    wOK = new Button(dialog, SWT.PUSH);
    wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
    
    lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
    lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
    
    wOK.addListener    (SWT.Selection, lsOK     );
    wCancel.addListener(SWT.Selection, lsCancel );
    
    // Detect [X] or ALT-F4 or something that kills this window...
    dialog.addShellListener( new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

    WindowProperty winprop = props.getScreen(dialog.getText());
    if (winprop!=null) {
      winprop.setShell(dialog);
    } else {
      dialog.pack();
    }
    
    dialog.open();
    while (!dialog.isDisposed())
    {
            if (!display.readAndDispatch()) display.sleep();
    }

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
      int result = PublisherUtil.publish(serverURL + "RepositoryFilePublisher", solutionName, files, publishPassword, userId, userPassword, false); //$NON-NLS-1$
      if (result == ISolutionRepository.FILE_EXISTS) {
        MessageBox mb = new MessageBox(dialog, SWT.NO | SWT.YES | SWT.ICON_WARNING);
        mb.setText(Messages.getString("PublishDialog.FILE_EXISTS")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("PublishDialog.FILE_OVERWRITE")); //$NON-NLS-1$
        if (mb.open() == SWT.YES) {
          result = PublisherUtil.publish(serverURL + "RepositoryFilePublisher", solutionName, files, publishPassword, userId, userPassword, true); //$NON-NLS-1$
        } else {
          return;
        }
      }
      if (result != ISolutionRepository.FILE_ADD_SUCCESSFUL) {
        MessageBox mb = new MessageBox(dialog, SWT.OK | SWT.ICON_ERROR);
        mb.setText(Messages.getString("PublishDialog.ACTION_FAILED")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("PublishDialog.FILE_SAVE_FAILED", fileName)); //$NON-NLS-1$
        mb.open();
      } else {  // We did it!
        MessageBox mb = new MessageBox(dialog, SWT.OK | SWT.ICON_INFORMATION);
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
    if (!serverURL.endsWith(seperatorFwd)) {
      serverURL += seperatorFwd;
    }
    solutionName = tSolutionName.getText();
    if (solutionName.indexOf(seperatorFwd) >= 0 || solutionName.indexOf(seperatorBck) >= 0) {
      MessageBox mb = new MessageBox(dialog, SWT.OK | SWT.ICON_ERROR);
      mb.setText(Messages.getString("PublishDialog.LOCATION_ERROR"));
      mb.setMessage(Messages.getString("PublishDialog.LOCATION_ERROR_INFO"));
      mb.open();
      return false;
    }
//    fileName = tFileName.getText();
//    
//    if (!fileName.endsWith(suffix)) {
//      fileName += suffix;
//    }
    
    userId = tUserId.getText();
    userPassword = tUserPassword.getText();
    publishPassword = tPublishPassword.getText();
    return true;
  }

  private void cancel() {
      dispose();
  }

  public void dispose() {
      props.setScreen(new WindowProperty(dialog));
      dialog.dispose();
  }
}
