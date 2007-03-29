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

package org.pentaho.pms.schema.security;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.widget.TreeMemory;
import be.ibridge.kettle.trans.step.BaseStepDialog;



public class SelectSecurityOwnerRightsDialog extends Dialog
{
	private static final String STRING_SECURITY_TREE = "SecurityTree"; //$NON-NLS-1$
    
    private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private Shell  shell;
		
    private SecurityReference securityReference;
    private Text wFilter;
    
    private int rights;
    private int ownerType;
    private String ownerName;
    
    private boolean ok;
    private Tree wTree;
    
    private boolean firstRefresh;

    private SecurityOwner owner;
		
	public SelectSecurityOwnerRightsDialog(Shell shell, SecurityReference securityReference, SecurityOwner owner, int rights)
    {
        super(shell, SWT.NONE);
        this.securityReference = securityReference;
        this.owner = owner;
        this.rights = rights;
        ok=false;
        firstRefresh=true;
    }

    public boolean open()
	{
        Props props = Props.getInstance();
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setBackground(GUIResource.getInstance().getColorBackground());
		
		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("SelectSecurityOwnerRightsDialog.USER_SELECT_OWNER_RIGHTS")); //$NON-NLS-1$
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

        // Some buttons at the bottom to create a baseline
        //
        wOK=new Button(shell, SWT.PUSH);
        wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
        wCancel=new Button(shell, SWT.PUSH);
        wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
        
        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

		// From step line
		Label wlFilter = new Label(shell, SWT.LEFT);
		wlFilter.setText(Messages.getString("SelectSecurityOwnerRightsDialog.USER_FILTER")); //$NON-NLS-1$
        props.setLook(wlFilter);
		FormData fdlFilter = new FormData();
		fdlFilter.left = new FormAttachment(0, 0);
		fdlFilter.top  = new FormAttachment(0, margin);
		wlFilter.setLayoutData(fdlFilter);
		wFilter = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wFilter);
		wFilter.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent e) { refreshTree(); } } );
		FormData fdFilter = new FormData();
		fdFilter.left = new FormAttachment(wlFilter, 2*margin);
		fdFilter.top  = new FormAttachment(0, margin);
		fdFilter.right= new FormAttachment(100, 0);
		wFilter.setLayoutData(fdFilter);
        if (owner!=null)
        {
            wFilter.setEnabled(false);
        }

        
		wTree = new Tree(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wTree);
		FormData fdTree = new FormData();
		fdTree.left   = new FormAttachment(0, 0);
		fdTree.top    = new FormAttachment(wFilter, margin);
		fdTree.right  = new FormAttachment(middle, 0);
        fdTree.bottom = new FormAttachment(wOK, -margin);
		wTree.setLayoutData(fdTree);
        TreeMemory.addTreeListener(wTree, STRING_SECURITY_TREE);
        
        if (owner!=null)
        {
            // wTree.setEnabled(false);
        }

        Control lastControl = wFilter;
        final List checkBoxes = new ArrayList();
        final List masks = new ArrayList();
        
        for (int i=0;i<securityReference.getAcls().size();i++)
        {
            final SecurityACL acl = (SecurityACL) securityReference.getAcls().get(i);
            final Button checkBox = new Button(shell, SWT.CHECK);
            checkBox.setText(acl.getName());
            props.setLook(checkBox);
            FormData fdCheckBox = new FormData();
            fdCheckBox.left  = new FormAttachment(middle, margin);
            fdCheckBox.right = new FormAttachment(100, 0);
            fdCheckBox.top   = new FormAttachment(lastControl, margin);
            checkBox.setLayoutData(fdCheckBox);
            checkBoxes.add(checkBox);
            masks.add(new Integer(acl.getMask()));
            
            checkBox.addSelectionListener(new SelectionAdapter()
                {
                    public void widgetSelected(SelectionEvent e)
                    {
                        // The rights:
                        int mask = securityReference.findAcl((((Button)e.widget).getText())).getMask();
                        
                        rights &= (0xFFFF-mask); // clear the mask
                        if (checkBox.getSelection()) 
                        {
                            rights |= mask; // set the mask

                            // Optionally set other the appropriate checkboxes too...
                            for (int ix=0;ix<checkBoxes.size();ix++)
                            {
                                Button b = (Button) checkBoxes.get(ix);
                                int checkMask = securityReference.findAcl(b.getText()).getMask();
                                if ( (mask & checkMask)==checkMask && checkMask!=0)
                                {
                                    b.setSelection(checkBox.getSelection());
                                }
                            }
                        }
                    }
                }
            );
            
            if ((rights & acl.getMask())==acl.getMask() && acl.getMask()>0)
            {
                checkBox.setSelection(true);
            }
            if (acl.getMask()==0)
            {
                if (rights==0)
                {
                    checkBox.setSelection(true);
                }
                checkBox.addSelectionListener(new SelectionAdapter()
                    {
                        public void widgetSelected(SelectionEvent e)
                        {
                            for (int ix=0;ix<checkBoxes.size();ix++)
                            {
                                Button b = (Button) checkBoxes.get(ix);
                                b.setSelection(false);
                            }
                            checkBox.setSelection(true);
                            rights=0;
                        }
                    }
                );
            }
            else
            {
                checkBox.addSelectionListener(new SelectionAdapter()
                    {
                        public void widgetSelected(SelectionEvent e)
                        {
                            for (int ix=0;ix<checkBoxes.size();ix++)
                            {
                                Button b = (Button)checkBoxes.get(ix);
                                int mask = ((Integer)masks.get(ix)).intValue();
                                if (mask==0) b.setSelection(rights==0);
                            }
                        }
                    }
                );
            }
            
            lastControl = checkBox;
        }
        
        // Add the "All" button too 
        final Button checkBox = new Button(shell, SWT.PUSH);
        checkBox.setText(Messages.getString("SelectSecurityOwnerRightsDialog.USER_CHECK_ALL")); //$NON-NLS-1$
        props.setLook(checkBox);
        FormData fdCheckBox = new FormData();
        fdCheckBox.left  = new FormAttachment(middle, margin);
        fdCheckBox.top   = new FormAttachment(lastControl, margin);
        checkBox.setLayoutData(fdCheckBox);
        checkBox.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent arg0)
                {
                    rights=0;
                    for (int i=0;i<checkBoxes.size();i++)
                    {
                        Button b = (Button)checkBoxes.get(i);
                        int mask = ((Integer)masks.get(i)).intValue();
                        b.setSelection(mask!=0);
                        rights|=mask;
                    }
                }
            }
        );
        
        
		// Add listeners
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		
		wOK.addListener    (SWT.Selection, lsOK     );
		wCancel.addListener(SWT.Selection, lsCancel );
		
		// Detect [X] or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		getData();
        
        WindowProperty winprop = props.getScreen(shell.getText());
        if (winprop!=null) winprop.setShell(shell); else shell.pack();
        	
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return ok;
	}
	
    private boolean filterMatches(String string, String filter)
    {
        if (Const.isEmpty(filter)) return true; // no filter set
        
        String upperString = string.toUpperCase();
        String upperFilter = filter.toUpperCase();
        
        return upperString.indexOf(upperFilter)>=0;
    }
    
	private void refreshTree()
    {
        TreeItem[] selection = null;
        wTree.removeAll();
        
        TreeItem tiUsers = new TreeItem(wTree, SWT.NONE);
        tiUsers.setText(SecurityOwner.STRING_USER_DESC);
        
        if (firstRefresh)
        {
            TreeMemory.getInstance().storeExpanded(STRING_SECURITY_TREE, tiUsers, true);
        }
        
        String filter = wFilter.getText();
        
        for (int i=0;i<securityReference.getUsers().size();i++)
        {
            String user = (String)securityReference.getUsers().get(i);
            if (filterMatches(user, filter))
            {
                TreeItem tiUser = new TreeItem(tiUsers, SWT.NONE);
                tiUser.setText(user);
                tiUser.setForeground(GUIResource.getInstance().getColorBlue());
                
                if (owner!=null && owner.getOwnerType()==SecurityOwner.OWNER_TYPE_USER && owner.getOwnerName().equals(user))
                {
                    selection = new TreeItem[] { tiUser };
                }
            }
        }
        
        TreeItem tiRoles = new TreeItem(wTree, SWT.NONE);
        tiRoles.setText(SecurityOwner.STRING_ROLE_DESC);

        if (firstRefresh)
        {
            TreeMemory.getInstance().storeExpanded(STRING_SECURITY_TREE, tiRoles, true);
        }

        for (int i=0;i<securityReference.getRoles().size();i++)
        {
            String role = (String)securityReference.getRoles().get(i);
            if (filterMatches(role, filter))
            {
                TreeItem tiRole = new TreeItem(tiRoles, SWT.NONE);
                tiRole.setText(role);
                tiRole.setForeground(GUIResource.getInstance().getColorBlue());
                
                if (owner!=null && owner.getOwnerType()==SecurityOwner.OWNER_TYPE_ROLE && owner.getOwnerName().equals(role))
                {
                    selection = new TreeItem[] { tiRole };
                }

            }
        }
        
        firstRefresh=false;
        TreeMemory.setExpandedFromMemory(wTree, STRING_SECURITY_TREE);
        
        if (selection!=null)
        {
            wTree.setSelection(selection);
            wTree.showSelection();
        }
    }


    public void dispose()
	{
        Props.getInstance().setScreen(new WindowProperty(shell));
        shell.dispose();
	}
	
	/**
	 * Copy information from the meta-data relationshipMeta to the dialog fields.
	 */ 
	public void getData()
	{
        refreshTree();
	}
	
	private void cancel()
	{
        ok=false;
		dispose();
	}
	
	private void ok()
	{
        if (wTree.getSelectionCount()==1)
        {
            String[] path = Const.getTreeStrings(wTree.getSelection()[0]);
            
            if (path.length==2)
            {
                ownerType = SecurityOwner.getOwnerType(path[0]);
                ownerName = path[1];
                
                ok=true;
                dispose();
            }
        }
	}
	

    public int getRights()
    {
        return rights;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public int getOwnerType()
    {
        return ownerType;
    }
}
