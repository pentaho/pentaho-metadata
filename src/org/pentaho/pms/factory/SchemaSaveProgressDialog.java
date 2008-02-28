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
/*
 *
 *
 */

package org.pentaho.pms.factory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.editor.MetaEditor;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;

/**
 * Takes care of displaying a dialog that will handle the wait while saving the schema...
 * 
 * @author Matt
 * @since  13-mrt-2005
 */
public class SchemaSaveProgressDialog {
  private class CwmContainer {
    public CWM cwm;
  }

  private Shell shell;

  private String domainName;

  private SchemaMeta schemaMeta;

  /**
   * Creates a new dialog that will handle the wait while saving a transformation...
   */
  public SchemaSaveProgressDialog(Shell shell, String domainName, SchemaMeta schemaMeta) {
    this.shell = shell;
    this.domainName = domainName;
    this.schemaMeta = schemaMeta;
  }

  public CWM open() {
    final CwmContainer container = new CwmContainer();

    IRunnableWithProgress op = new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        if (monitor != null) {
          int size = 40;
          size += schemaMeta.nrDatabases();
          size += schemaMeta.nrConcepts();
          size += schemaMeta.nrTables();
          schemaMeta.nrBusinessModels();

          monitor.beginTask(Messages.getString("SchemaSaveProgressDialog.USER_STORING_METADATA_TO_CWM_MODEL"), size); //$NON-NLS-1$
        }

        monitor
            .subTask(Messages.getString("SchemaSaveProgressDialog.USER_GETTING_OLD_DOMAIN_INSTANCE_FROM_REPOSITORY")); //$NON-NLS-1$
        container.cwm = CWM.getInstance(domainName);
        monitor.worked(10);

        // First delete this one...
        monitor.subTask(Messages.getString("SchemaSaveProgressDialog.USER_REMOVING_DOMAIN_INSTANCE_FROM_REPOSITORY")); //$NON-NLS-1$
        try {
          container.cwm.removeDomain();
        } catch (Exception e) {
          LogWriter.getInstance().logError(MetaEditor.APPLICATION_NAME,
              Messages.getString("SchemaSaveProgressDialog.ERROR_0001_ERROR_REMOVING_DOMAIN", e.toString())); //$NON-NLS-1$
          LogWriter.getInstance().logError(MetaEditor.APPLICATION_NAME, Const.getStackTracker(e));
        }
        monitor.worked(10);

        // then re-create it
        monitor.subTask(Messages.getString("SchemaSaveProgressDialog.USER_CREATE_NEW_DOMAIN_INSTANCE")); //$NON-NLS-1$
        container.cwm = CWM.getInstance(domainName);
        monitor.worked(10);

        CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();

        try {
          cwmSchemaFactory.storeSchemaMeta(container.cwm, schemaMeta, monitor);
        } catch (Exception e) {

          // Attempt to recover from recovery file, before re-throwing exception ...
          container.cwm = CWM.getInstance(domainName);

          if (container.cwm != null) {
            try {
              File file = new File(Const.getDomainRecoveryFile() + domainName + ".xmi");
              if (file.exists()) {
                container.cwm.importFromXMI(Const.getDomainRecoveryFile() + domainName + ".xmi");
              }
            } catch (Exception ex) {
              LogWriter.getInstance().logError(
                      Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_IMPORTING_XMI")); //$NON-NLS-1$ //$NON-NLS-2$
              LogWriter.getInstance().logError(MetaEditor.APPLICATION_NAME, Const.getStackTracker(e));
            }
          }

          throw new InvocationTargetException(e, Messages
              .getString("SchemaSaveProgressDialog.ERROR_0002_ERROR_SAVING_SCHEMA_TO_REPOSITORY")); //$NON-NLS-1$
        }

        monitor.subTask(Messages.getString("SchemaSaveProgressDialog.USER_BACKING_UP_TO_RECOVERY_FILE")); //$NON-NLS-1$
        container.cwm = CWM.getInstance(domainName);
        monitor.worked(10);

        // PMD-169 Failure on save smokes the model...
        // Assuming at this point we have successfully saved... 
        // Get back the result of the last save operation, and export to recovery file
        // If anything bad happens during save, at least the user can recover from the recovery file.
        // This is necessary because the save operation performs a delete of the domain
        // before save, and allows no room for error during the save process. 

        if (container.cwm != null) {
          try {
            container.cwm.exportToXMI(Const.getDomainRecoveryFile() + domainName + ".xmi");
          } catch (Exception e) {
            // Not a core function of save, so proceed even on failure    
            LogWriter.getInstance().logError(
                    Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_EXPORTING_XMI")); //$NON-NLS-1$ //$NON-NLS-2$
            LogWriter.getInstance().logError(MetaEditor.APPLICATION_NAME, Const.getStackTracker(e));
          }
        }
        monitor.done();
      }
    };

    try {
      ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
      pmd.run(false, false, op);
    } catch (InvocationTargetException e) {
      new ErrorDialog(
          shell,
          Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("SchemaSaveProgressDialog.USER_ERROR_SAVING_SCHEMA"), e); //$NON-NLS-1$ //$NON-NLS-2$
      container.cwm = null;
    } catch (InterruptedException e) {
      new ErrorDialog(
          shell,
          Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("SchemaSaveProgressDialog.USER_ERROR_SAVING_SCHEMA"), e); //$NON-NLS-1$ //$NON-NLS-2$
      container.cwm = null;
    }

    return container.cwm;
  }
}
