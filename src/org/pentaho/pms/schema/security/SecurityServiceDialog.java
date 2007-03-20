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
package org.pentaho.pms.schema.security;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.dialog.EnterTextDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.trans.step.BaseStepDialog;


/**
 * 
 * Dialog that allows you to edit the settings of the security service connection
 * 
 * @see <code>SecurityService</code>
 * @author Matt
 * @since 31-10-2006
 *
 */

public class SecurityServiceDialog extends Dialog 
{
	private SecurityService securityService;
	
	private CTabFolder   wTabFolder;
	private FormData     fdTabFolder;
	
	private CTabItem     wServiceTab, wProxyTab, wFileTab;

	private Composite    wServiceComp, wProxyComp, wFileComp;
	private FormData     fdServiceComp, fdProxyComp, fdFileComp;

	private Shell     shell;

    // Service
	private Label    wlServiceURL, wlDetailsName, wlDetailName, wlUsername, wlPassword;
	private Text     wServiceURL,  wDetailsName,  wDetailName,  wUsername,  wPassword;

    private Label    wlDetailType;
	private List     wDetailType;
	
    // Proxy
    private Label    wlProxyHost, wlProxyPort, wlNonProxyHosts;
    private Text     wProxyHost, wProxyPort,  wNonProxyHosts;

    private Label    wlFile;
    private Text     wFile;

	private Button    wLoad, wOK, wTest, wCancel;
	
    private ModifyListener lsMod;

	private Props     props;

    private int middle;
    private int margin;

    private SecurityService originalService;
    private boolean reload;
    
	public SecurityServiceDialog(Shell par, SecurityService securityService)
	{
		super(par, SWT.NONE);
		this.securityService=(SecurityService) securityService.clone();
        this.originalService=securityService;
		props=Props.getInstance();
        reload=false;
	}
	
	public boolean open() 
	{
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
 		props.setLook(shell);
		
		lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				securityService.setChanged();
			}
		};

		middle = props.getMiddlePct();
		margin = Const.MARGIN;

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		
		shell.setText("Security service dialog");
		shell.setLayout (formLayout);
 		
		// First, add the buttons...
		
		// Buttons
		wOK     = new Button(shell, SWT.PUSH); 
		wOK.setText(" &OK ");

        wLoad     = new Button(shell, SWT.PUSH); 
        wLoad.setText(" &Load ");

		wTest    = new Button(shell, SWT.PUSH); 
		wTest.setText(" &Test ");

		wCancel = new Button(shell, SWT.PUSH); 
		wCancel.setText(" &Cancel ");

		Button[] buttons = new Button[] { wOK, wLoad, wTest, wCancel };
		BaseStepDialog.positionBottomButtons(shell, buttons, margin, null);
		
		// The rest stays above the buttons...
		
		wTabFolder = new CTabFolder(shell, SWT.BORDER);
 		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

        addServiceTab();
        addProxyTab();
        addFileTab();
        
		fdTabFolder = new FormData();
		fdTabFolder.left  = new FormAttachment(0, 0);
		fdTabFolder.top   = new FormAttachment(0, margin);
		fdTabFolder.right = new FormAttachment(100, 0);
		fdTabFolder.bottom= new FormAttachment(wOK, -margin);
		wTabFolder.setLayoutData(fdTabFolder);

		
		// Add listeners
		wOK.addListener(SWT.Selection, new Listener () { public void handleEvent (Event e) { ok(); } } );
        wLoad.addListener(SWT.Selection, new Listener () { public void handleEvent (Event e) { load(); } } );
        wCancel.addListener(SWT.Selection, new Listener () { public void handleEvent (Event e) { cancel(); } } );
		wTest.addListener(SWT.Selection, new Listener () { public void handleEvent (Event e) { test(); } } );
		
        SelectionAdapter selAdapter=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
        wDetailsName.addSelectionListener(selAdapter);
		wDetailName.addSelectionListener(selAdapter);
		wUsername.addSelectionListener(selAdapter);
		wPassword.addSelectionListener(selAdapter);
		wServiceURL.addSelectionListener(selAdapter);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );
	
        if (securityService.hasFile() && !securityService.hasService())
        {
            wTabFolder.setSelection(2);
        }
        else
        {
            wTabFolder.setSelection(0);
        }
        
		getData();

		BaseStepDialog.setSize(shell);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return reload;
	}
	
    private void addServiceTab()
    {
        //////////////////////////
        // START OF DB TAB   ///
        //////////////////////////
        wServiceTab=new CTabItem(wTabFolder, SWT.NONE);
        wServiceTab.setText("Service");
        
        wServiceComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wServiceComp);

        FormLayout GenLayout = new FormLayout();
        GenLayout.marginWidth  = Const.FORM_MARGIN;
        GenLayout.marginHeight = Const.FORM_MARGIN;
        wServiceComp.setLayout(GenLayout);

        // What's the service URL?
        wlServiceURL = new Label(wServiceComp, SWT.RIGHT); 
        props.setLook(wlServiceURL);
        wlServiceURL.setText("Service URL: ");
        FormData fdlServiceURL = new FormData();
        fdlServiceURL.top   = new FormAttachment(0, 0);
        fdlServiceURL.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlServiceURL.right = new FormAttachment(middle, -margin);
        wlServiceURL.setLayoutData(fdlServiceURL);

        wServiceURL = new Text(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wServiceURL);
        wServiceURL.addModifyListener(lsMod);
        FormData fdServiceURL = new FormData();
        fdServiceURL.top  = new FormAttachment(0, 0);
        fdServiceURL.left = new FormAttachment(middle, 0); // To the right of the label
        fdServiceURL.right= new FormAttachment(95, 0);
        wServiceURL.setLayoutData(fdServiceURL);

        // Security Details service name
        wlDetailsName = new Label(wServiceComp, SWT.RIGHT); 
        wlDetailsName.setText("Security details service name: "); 
        props.setLook(wlDetailsName);
        FormData fdlDetailsName = new FormData();
        fdlDetailsName.top  = new FormAttachment(wServiceURL, margin);
        fdlDetailsName.left = new FormAttachment(0,0);
        fdlDetailsName.right= new FormAttachment(middle, -margin);
        wlDetailsName.setLayoutData(fdlDetailsName);

        wDetailsName = new Text(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wDetailsName);
        wDetailsName.addModifyListener(lsMod);
        FormData fdDetailsName = new FormData();
        fdDetailsName.top  = new FormAttachment(wServiceURL, margin);
        fdDetailsName.left = new FormAttachment(middle, 0); 
        fdDetailsName.right= new FormAttachment(95, 0);
        wDetailsName.setLayoutData(fdDetailsName);
        
        // Detail name service
        wlDetailName = new Label(wServiceComp, SWT.RIGHT ); 
        wlDetailName.setText("Security detail service name: "); 
        props.setLook(wlDetailName);
        FormData fdlDetailName = new FormData();
        fdlDetailName.top  = new FormAttachment(wDetailsName, margin);
        fdlDetailName.left = new FormAttachment(0,0);   
        fdlDetailName.right= new FormAttachment(middle, -margin);
        wlDetailName.setLayoutData(fdlDetailName);

        wDetailName = new Text(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wDetailName);
        wDetailName.addModifyListener(lsMod);
        FormData fdDetailName = new FormData();
        fdDetailName.top  = new FormAttachment(wDetailsName, margin);
        fdDetailName.left = new FormAttachment(middle, 0);
        fdDetailName.right= new FormAttachment(95, 0);
        wDetailName.setLayoutData(fdDetailName);
                
        // Port
        wlDetailType = new Label(wServiceComp, SWT.RIGHT ); 
        wlDetailType.setText("Detail type: "); 
        props.setLook(wlDetailType);
        FormData fdlDetailType = new FormData();
        fdlDetailType.top  = new FormAttachment(wDetailName, margin);
        fdlDetailType.left = new FormAttachment(0,0);
        fdlDetailType.right= new FormAttachment(middle, -margin);
        wlDetailType.setLayoutData(fdlDetailType);

        wDetailType = new List(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wDetailType);
        FormData fdDetailType = new FormData();
        fdDetailType.top  = new FormAttachment(wDetailName, margin);
        fdDetailType.left = new FormAttachment(middle, 0); 
        fdDetailType.right= new FormAttachment(95, 0);
        wDetailType.setLayoutData(fdDetailType);
        wDetailType.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent arg0)
            {
                lsMod.modifyText(null);
            }
        });
        wDetailType.setItems(SecurityService.serviceTypeDescriptions);
        
        // Username
        wlUsername = new Label(wServiceComp, SWT.RIGHT ); 
        wlUsername.setText("Username: "); 
        props.setLook(wlUsername);
        FormData fdlUsername = new FormData();
        fdlUsername.top  = new FormAttachment(wDetailType, margin);
        fdlUsername.left = new FormAttachment(0,0); 
        fdlUsername.right= new FormAttachment(middle, -margin);
        wlUsername.setLayoutData(fdlUsername);

        wUsername = new Text(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wUsername);
        wUsername.addModifyListener(lsMod);
        FormData fdUsername = new FormData();
        fdUsername.top  = new FormAttachment(wDetailType, margin);
        fdUsername.left = new FormAttachment(middle, 0); 
        fdUsername.right= new FormAttachment(95, 0);
        wUsername.setLayoutData(fdUsername);

        
        // Password
        wlPassword = new Label(wServiceComp, SWT.RIGHT ); 
        wlPassword.setText("Password: "); 
        props.setLook(wlPassword);
        FormData fdlPassword = new FormData();
        fdlPassword.top  = new FormAttachment(wUsername, margin);
        fdlPassword.left = new FormAttachment(0,0);
        fdlPassword.right= new FormAttachment(middle, -margin);
        wlPassword.setLayoutData(fdlPassword);

        wPassword = new Text(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wPassword);
        wPassword.setEchoChar('*');
        wPassword.addModifyListener(lsMod);
        FormData fdPassword = new FormData();
        fdPassword.top  = new FormAttachment(wUsername, margin);
        fdPassword.left = new FormAttachment(middle, 0); 
        fdPassword.right= new FormAttachment(95, 0);
        wPassword.setLayoutData(fdPassword);

        
        fdServiceComp=new FormData();
        fdServiceComp.left  = new FormAttachment(0, 0);
        fdServiceComp.top   = new FormAttachment(0, 0);
        fdServiceComp.right = new FormAttachment(100, 0);
        fdServiceComp.bottom= new FormAttachment(100, 0);
        wServiceComp.setLayoutData(fdServiceComp);
    
        wServiceComp.layout();
        wServiceTab.setControl(wServiceComp);
        
        /////////////////////////////////////////////////////////////
        /// END OF GEN TAB
        /////////////////////////////////////////////////////////////
    }
    
    private void addProxyTab()
    {
        //////////////////////////
        // START OF POOL TAB///
        ///
        wProxyTab=new CTabItem(wTabFolder, SWT.NONE);
        wProxyTab.setText("Proxy");

        FormLayout poolLayout = new FormLayout ();
        poolLayout.marginWidth  = Const.FORM_MARGIN;
        poolLayout.marginHeight = Const.FORM_MARGIN;
        
        wProxyComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wProxyComp);
        wProxyComp.setLayout(poolLayout);

        // What's the data tablespace name?
        wlProxyHost = new Label(wProxyComp, SWT.RIGHT); 
        props.setLook(wlProxyHost);
        wlProxyHost.setText("Proxy server hostname: "); 
        FormData fdlProxyHost = new FormData();
        fdlProxyHost.top   = new FormAttachment(0, 0);
        fdlProxyHost.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlProxyHost.right = new FormAttachment(middle, -margin);
        wlProxyHost.setLayoutData(fdlProxyHost);

        wProxyHost = new Text(wProxyComp, SWT.BORDER | SWT.LEFT | SWT.SINGLE );
        props.setLook(wProxyHost);
        wProxyHost.addModifyListener(lsMod);
        FormData fdProxyHost = new FormData();
        fdProxyHost.top  = new FormAttachment(0, 0);
        fdProxyHost.left = new FormAttachment(middle, 0); // To the right of the label
        fdProxyHost.right= new FormAttachment(95, 0);
        wProxyHost.setLayoutData(fdProxyHost);

        // What's the initial pool size
        wlProxyPort = new Label(wProxyComp, SWT.RIGHT); 
        props.setLook(wlProxyPort);
        wlProxyPort.setText("The proxy server port: "); 
        FormData fdlProxyPort = new FormData();
        fdlProxyPort.top   = new FormAttachment(wProxyHost, margin);
        fdlProxyPort.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlProxyPort.right = new FormAttachment(middle, -margin);
        wlProxyPort.setLayoutData(fdlProxyPort);

        wProxyPort = new Text(wProxyComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wProxyPort);
        wProxyPort.addModifyListener(lsMod);
        FormData fdProxyPort = new FormData();
        fdProxyPort.top  = new FormAttachment(wProxyHost, margin);
        fdProxyPort.left = new FormAttachment(middle, 0); // To the right of the label
        fdProxyPort.right= new FormAttachment(95, 0);
        wProxyPort.setLayoutData(fdProxyPort);

        // What's the maximum pool size
        wlNonProxyHosts = new Label(wProxyComp, SWT.RIGHT); 
        props.setLook(wlNonProxyHosts);
        wlNonProxyHosts.setText("Ignore proxy for hosts: regexp | separated: "); 
        FormData fdlNonProxyHosts = new FormData();
        fdlNonProxyHosts.top   = new FormAttachment(wProxyPort, margin);
        fdlNonProxyHosts.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlNonProxyHosts.right = new FormAttachment(middle, -margin);
        wlNonProxyHosts.setLayoutData(fdlNonProxyHosts);

        wNonProxyHosts = new Text(wProxyComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wNonProxyHosts);
        wNonProxyHosts.addModifyListener(lsMod);
        FormData fdNonProxyHosts = new FormData();
        fdNonProxyHosts.top  = new FormAttachment(wProxyPort, margin);
        fdNonProxyHosts.left = new FormAttachment(middle, 0); // To the right of the label
        fdNonProxyHosts.right= new FormAttachment(95, 0);
        wNonProxyHosts.setLayoutData(fdNonProxyHosts);

        
        fdProxyComp = new FormData();
        fdProxyComp.left  = new FormAttachment(0, 0);
        fdProxyComp.top   = new FormAttachment(0, 0);
        fdProxyComp.right = new FormAttachment(100, 0);
        fdProxyComp.bottom= new FormAttachment(100, 0);
        wProxyComp.setLayoutData(fdProxyComp);

        wProxyComp.layout();
        wProxyTab.setControl(wProxyComp);
    }

    private void addFileTab()
    {
        //////////////////////////
        // START OF POOL TAB///
        ///
        wFileTab=new CTabItem(wTabFolder, SWT.NONE);
        wFileTab.setText("File");

        FormLayout poolLayout = new FormLayout ();
        poolLayout.marginWidth  = Const.FORM_MARGIN;
        poolLayout.marginHeight = Const.FORM_MARGIN;
        
        wFileComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wFileComp);
        wFileComp.setLayout(poolLayout);

        Button wbbFile = new Button(wFileComp, SWT.PUSH| SWT.CENTER);
        props.setLook(wbbFile);
        wbbFile.setText("Browse...");
        wbbFile.setToolTipText("Select the XML file to use");
        FormData fdbFile = new FormData();
        fdbFile.right= new FormAttachment(95, 0);
        fdbFile.top  = new FormAttachment(0, 0);
        wbbFile.setLayoutData(fdbFile);
        
        // What's the data tablespace name?
        wlFile = new Label(wFileComp, SWT.RIGHT); 
        props.setLook(wlFile);
        wlFile.setText("Filename: "); 
        FormData fdlFile = new FormData();
        fdlFile.top   = new FormAttachment(0, 0);
        fdlFile.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlFile.right = new FormAttachment(middle, -margin);
        wlFile.setLayoutData(fdlFile);

        wFile = new Text(wFileComp, SWT.BORDER | SWT.LEFT | SWT.SINGLE );
        props.setLook(wFile);
        wFile.addModifyListener(lsMod);
        FormData fdFile = new FormData();
        fdFile.top  = new FormAttachment(0, 0);
        fdFile.left = new FormAttachment(middle, 0); // To the right of the label
        fdFile.right= new FormAttachment(wbbFile, -margin);
        wFile.setLayoutData(fdFile);


        // Listen to the Browse... button
        wbbFile.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e) 
                {
                    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
                    dialog.setFilterExtensions(new String[] {"*.xml", "*"});
                    if (!Const.isEmpty(wFile.getText()))
                    {
                        dialog.setFileName( wFile.getText() );
                    }
                    dialog.setFilterNames(new String[] {"XML files", "All files" });
                    if (dialog.open()!=null)
                    {
                        String str = dialog.getFilterPath()+System.getProperty("file.separator")+dialog.getFileName();
                        wFile.setText(str);
                    }
                }
            }
        );
        
        fdFileComp = new FormData();
        fdFileComp.left  = new FormAttachment(0, 0);
        fdFileComp.top   = new FormAttachment(0, 0);
        fdFileComp.right = new FormAttachment(100, 0);
        fdFileComp.bottom= new FormAttachment(100, 0);
        wFileComp.setLayoutData(fdFileComp);

        wFileComp.layout();
        wFileTab.setControl(wFileComp);
    }

    public void dispose()
	{
		props.setScreen(new WindowProperty(shell));
		shell.dispose();
	}
    
    public void getData()
	{
		wServiceURL.setText( Const.NVL(securityService.getServiceURL(), "") );
		wDetailsName.setText( Const.NVL(securityService.getDetailsServiceName(), "") );
		wDetailName.setText( Const.NVL(securityService.getDetailServiceName(), "") );
		wDetailType.select( securityService.getDetailServiceType() );
        wUsername.setText( Const.NVL(securityService.getUsername(), "") );
		wPassword.setText( Const.NVL(securityService.getPassword(), "") );

        wProxyHost.setText( Const.NVL(securityService.getProxyHostname(), ""));
        wProxyPort.setText( Const.NVL(securityService.getProxyPort(), ""));
        wNonProxyHosts.setText( Const.NVL(securityService.getNonProxyHosts(), ""));
        
        wFile.setText( Const.NVL(securityService.getFilename(), ""));
        
		wServiceURL.setFocus();
	}
    
	private void cancel()
	{
		originalService = null;
		dispose();
	}
	
	public void ok()
	{
        getInfo();
        originalService.setServiceURL(securityService.getServiceURL());
        originalService.setDetailsServiceName(securityService.getDetailsServiceName());
        originalService.setDetailServiceName(securityService.getDetailServiceName());
        originalService.setDetailServiceType(securityService.getDetailServiceType());
        originalService.setUsername(securityService.getUsername());
        originalService.setPassword(securityService.getPassword());

        originalService.setProxyHostname(securityService.getProxyHostname());
        originalService.setProxyPort(securityService.getProxyPort());
        originalService.setNonProxyHosts(securityService.getNonProxyHosts());
        
        originalService.setFilename( securityService.getFilename() );
        
        originalService.setChanged();

        dispose();
	}
    
    public void load()
    {
        reload=true;
        ok();
    }
	
    // Get dialog info in securityService
	private void getInfo()
    {
        securityService.setServiceURL(wServiceURL.getText());
        securityService.setDetailsServiceName(wDetailsName.getText());
        securityService.setDetailServiceName(wDetailName.getText());
        securityService.setDetailServiceType(wDetailType.getSelectionIndex());
        securityService.setUsername(wUsername.getText());
        securityService.setPassword(wPassword.getText());

        securityService.setProxyHostname(wProxyHost.getText());
        securityService.setProxyPort(wProxyPort.getText());
        securityService.setNonProxyHosts(wNonProxyHosts.getText());

        securityService.setFilename(wFile.getText());
    }

	public void test()
	{
		try
		{
			getInfo();
            
            // Load the security reference information...
            SecurityReference securityReference = new SecurityReference(securityService);
            String xml = securityReference.toXML();
            
            String message = "Connection info ";
            if (securityService.hasService()) message+="from server URL: "+securityService.getURL()+Const.CR+Const.CR;
            else message+="from file: "+securityService.getFilename()+Const.CR+Const.CR;
            message+=xml;
            
			EnterTextDialog dialog = new EnterTextDialog(shell, "XML", "The XML returned is:", message);
            dialog.open();
		}
		catch(Exception e)
		{
			new ErrorDialog(shell, "Error", "Unable to get security XML information back from URL ["+securityService.getURL()+"]", e);
		}		
	}
}