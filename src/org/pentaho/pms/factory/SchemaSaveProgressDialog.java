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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.editor.MetaEditor;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.dialog.ErrorDialog;


/**
 * Takes care of displaying a dialog that will handle the wait while saving the schema...
 * 
 * @author Matt
 * @since  13-mrt-2005
 */
public class SchemaSaveProgressDialog
{
    private class CwmContainer
    {
        public CWM cwm;
    }
	private Shell shell;
	private String domainName;
	private SchemaMeta schemaMeta;
	
	/**
	 * Creates a new dialog that will handle the wait while saving a transformation...
	 */
	public SchemaSaveProgressDialog(Shell shell, String domainName, SchemaMeta schemaMeta)
	{
		this.shell = shell;
		this.domainName = domainName;
		this.schemaMeta = schemaMeta;
	}
	
	public CWM open()
	{
        final CwmContainer container = new CwmContainer();
        
		IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
                if (monitor!=null)
                {
                    int size = 30;
                    size+=schemaMeta.nrDatabases();
                    size+=schemaMeta.nrConcepts();
                    size+=schemaMeta.nrTables();
                    schemaMeta.nrBusinessModels();
                    
                    monitor.beginTask("Storing the metadata into the CWM model", size);
                }

                monitor.subTask("Getting old domain instance from the repository");
                container.cwm = CWM.getInstance(domainName);
                monitor.worked(10);
                
                // First delete this one...
                monitor.subTask("Removing old domain from the repository");
                try
                {
                    container.cwm.removeDomain();
                }
                catch(Exception e)
                {
                    LogWriter.getInstance().logError(MetaEditor.APPLICATION_NAME, "Error removing domain:"+e.toString());
                    LogWriter.getInstance().logError(MetaEditor.APPLICATION_NAME, Const.getStackTracker(e));
                }
                monitor.worked(10);
                
                // then re-create it
                monitor.subTask("Create new domain instance");
                container.cwm = CWM.getInstance(domainName);
                monitor.worked(10);

                CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
                
                try
                {
                    cwmSchemaFactory.storeSchemaMeta(container.cwm, schemaMeta, monitor);
                }
                catch(Exception e)
                {
                    throw new InvocationTargetException(e, "Unable to save the schema to the repository.");
                }
			}
		};
		
		try
		{
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
			pmd.run(false, false, op);
		}
		catch (InvocationTargetException e)
		{
			new ErrorDialog(shell, "Error", "There was an error saving the schema", e);
            container.cwm=null;
		}
		catch (InterruptedException e)
		{
            new ErrorDialog(shell, "Error", "There was an error saving the schema", e);
            container.cwm=null;
		}

		return container.cwm;
	}
}
