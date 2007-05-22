/**
 * 
 */
package org.pentaho.pms.schema.security;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.dialog.EnterTextDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;

/**
 * @author Gretchen Moran
 *
 */
public class SecurityDialog extends TitleAreaDialog {

  private Button wbTest;
  
  private CTabFolder wTabFolder;

  private FormData fdTabFolder;

  private CTabItem wServiceTab, wProxyTab, wFileTab;

  private Composite wServiceComp, wProxyComp, wFileComp;

  private FormData fdServiceComp, fdProxyComp, fdFileComp;

  // Service
  private Label wlServiceURL, wlUsername, wlPassword;

  private Text wServiceURL, wUsername, wPassword;

  private Label wlDetailType;

  private List wDetailType;

  // Proxy
  private Label wlProxyHost, wlProxyPort, wlNonProxyHosts;

  private Text wProxyHost, wProxyPort, wNonProxyHosts;

  private Label wlFile;

  private Text wFile;

  private ModifyListener lsMod;

  
  /**************************************************************************************/

  private Props props;

  private int middle;

  private int margin;

  private SecurityService originalService;

  private SecurityService securityService;

  private boolean reload;

  public SecurityDialog(Shell shell, SecurityService service) {
    super(shell);
    originalService = service;
    securityService = (SecurityService) originalService.clone();
    props = Props.getInstance();
    reload = false;
  }

  protected Control createContents(Composite parent) {
    Control contents = super.createContents(parent);
    setMessage("Retrieve the security information from the Pentaho server. Choose to set up a security service, or get the values from a local file.");
    setTitle("Security Service");
    return contents;
  }

  protected Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);
    c0.setBackground(GUIResource.getInstance().getColorWhite());
    Composite c1 = new Composite(c0, SWT.BORDER);
    c1.setBackground(GUIResource.getInstance().getColorWhite());

    middle = 25; props.getMiddlePct();
    margin = Const.MARGIN;

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    c1.setLayout(formLayout);
    props.setLook(c1);

    lsMod = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        securityService.setChanged();
      }
    };

    wTabFolder = new CTabFolder(c1, SWT.BORDER);
    props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

    addServiceTab();
    addProxyTab();
    addFileTab();

    fdTabFolder = new FormData();
    fdTabFolder.left = new FormAttachment(0, 0);
    fdTabFolder.top = new FormAttachment(0, margin);
    fdTabFolder.right = new FormAttachment(100, 0);
    fdTabFolder.bottom = new FormAttachment(100, 0);
    wTabFolder.setLayoutData(fdTabFolder);

    SelectionAdapter selAdapter = new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent e) {
        ok();
      }
    };

    wUsername.addSelectionListener(selAdapter);
    wPassword.addSelectionListener(selAdapter);
    wServiceURL.addSelectionListener(selAdapter);

    // Detect X or ALT-F4 or something that kills this window...
    getShell().addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        cancel();
      }
    });

    if (securityService.hasFile() && !securityService.hasService()) {
      wTabFolder.setSelection(2);
    } else {
      wTabFolder.setSelection(0);
    }

    getData();

    return c0;

  }

  private void addServiceTab() {
    //////////////////////////
    // START OF DB TAB   ///
    //////////////////////////
    wServiceTab = new CTabItem(wTabFolder, SWT.NONE);
    wServiceTab.setText(Messages.getString("SecurityServiceDialog.USER_SERVICE")); //$NON-NLS-1$

    wServiceComp = new Composite(wTabFolder, SWT.NONE);
    props.setLook(wServiceComp);

    FormLayout GenLayout = new FormLayout();
    GenLayout.marginWidth = Const.FORM_MARGIN;
    GenLayout.marginHeight = Const.FORM_MARGIN;
    wServiceComp.setLayout(GenLayout);

    // What's the service URL?
    wlServiceURL = new Label(wServiceComp, SWT.RIGHT);
    props.setLook(wlServiceURL);
    wlServiceURL.setText(Messages.getString("SecurityServiceDialog.USER_SERVICE_URL")); //$NON-NLS-1$
    FormData fdlServiceURL = new FormData();
    fdlServiceURL.top = new FormAttachment(0, 0);
    fdlServiceURL.left = new FormAttachment(0, 0); // First one in the left top corner
    fdlServiceURL.right = new FormAttachment(middle, -margin);
    wlServiceURL.setLayoutData(fdlServiceURL);

    wServiceURL = new Text(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wServiceURL);
    wServiceURL.addModifyListener(lsMod);
    FormData fdServiceURL = new FormData();
    fdServiceURL.top = new FormAttachment(0, 0);
    fdServiceURL.left = new FormAttachment(middle, margin); // To the right of the label
    fdServiceURL.right = new FormAttachment(95, 0);
    wServiceURL.setLayoutData(fdServiceURL);

    // Port
    wlDetailType = new Label(wServiceComp, SWT.RIGHT);
    wlDetailType.setText(Messages.getString("SecurityServiceDialog.USER_DETAIL_TYPE")); //$NON-NLS-1$
    props.setLook(wlDetailType);
    FormData fdlDetailType = new FormData();
    fdlDetailType.top = new FormAttachment(wServiceURL, margin);
    fdlDetailType.left = new FormAttachment(0, 0);
    fdlDetailType.right = new FormAttachment(middle, -margin);
    wlDetailType.setLayoutData(fdlDetailType);

    wDetailType = new List(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wDetailType);
    FormData fdDetailType = new FormData();
    fdDetailType.top = new FormAttachment(wServiceURL, margin);
    fdDetailType.left = new FormAttachment(middle, margin);
    fdDetailType.right = new FormAttachment(95, 0);
    wDetailType.setLayoutData(fdDetailType);
    wDetailType.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent arg0) {
        lsMod.modifyText(null);
      }
    });
    wDetailType.setItems(SecurityService.serviceTypeDescriptions);

    // Username
    wlUsername = new Label(wServiceComp, SWT.RIGHT);
    wlUsername.setText(Messages.getString("SecurityServiceDialog.USER_USERNAME")); //$NON-NLS-1$
    props.setLook(wlUsername);
    FormData fdlUsername = new FormData();
    fdlUsername.top = new FormAttachment(wDetailType, margin);
    fdlUsername.left = new FormAttachment(0, 0);
    fdlUsername.right = new FormAttachment(middle, -margin);
    wlUsername.setLayoutData(fdlUsername);

    wUsername = new Text(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wUsername);
    wUsername.addModifyListener(lsMod);
    FormData fdUsername = new FormData();
    fdUsername.top = new FormAttachment(wDetailType, margin);
    fdUsername.left = new FormAttachment(middle, margin);
    fdUsername.right = new FormAttachment(95, 0);
    wUsername.setLayoutData(fdUsername);

    // Password
    wlPassword = new Label(wServiceComp, SWT.RIGHT);
    wlPassword.setText(Messages.getString("SecurityServiceDialog.USER_PASSWORD")); //$NON-NLS-1$
    props.setLook(wlPassword);
    FormData fdlPassword = new FormData();
    fdlPassword.top = new FormAttachment(wUsername, margin);
    fdlPassword.left = new FormAttachment(0, 0);
    fdlPassword.right = new FormAttachment(middle, -margin);
    wlPassword.setLayoutData(fdlPassword);

    wPassword = new Text(wServiceComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wPassword);
    wPassword.setEchoChar('*');
    wPassword.addModifyListener(lsMod);
    FormData fdPassword = new FormData();
    fdPassword.top = new FormAttachment(wUsername, margin);
    fdPassword.left = new FormAttachment(middle, margin);
    fdPassword.right = new FormAttachment(95, 0);
    wPassword.setLayoutData(fdPassword);

    wbTest = new Button(wServiceComp, SWT.PUSH | SWT.CENTER);
    props.setLook(wbTest);
    wbTest.setText("Test");     
    wbTest.setToolTipText("Test the Pentaho Security Service" ); 
    FormData fdbFile = new FormData();
    fdbFile.top = new FormAttachment(wPassword, margin);
    fdbFile.left = new FormAttachment(middle, 0);
    fdbFile.right = new FormAttachment(middle + 15, 0);
    wbTest.setLayoutData(fdbFile);
    wbTest.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
        test();
      }
    });
    
    fdServiceComp = new FormData();
    fdServiceComp.left = new FormAttachment(0, 0);
    fdServiceComp.top = new FormAttachment(0, 0);
    fdServiceComp.right = new FormAttachment(100, 0);
    fdServiceComp.bottom = new FormAttachment(100, 0);
    wServiceComp.setLayoutData(fdServiceComp);

    wServiceComp.layout();
    wServiceTab.setControl(wServiceComp);

    /////////////////////////////////////////////////////////////
    /// END OF GEN TAB
    /////////////////////////////////////////////////////////////
  }

  private void addProxyTab() {
    //////////////////////////
    // START OF POOL TAB///
    ///
    wProxyTab = new CTabItem(wTabFolder, SWT.NONE);
    wProxyTab.setText(Messages.getString("SecurityServiceDialog.USER_PROXY")); //$NON-NLS-1$

    FormLayout poolLayout = new FormLayout();
    poolLayout.marginWidth = Const.FORM_MARGIN;
    poolLayout.marginHeight = Const.FORM_MARGIN;

    wProxyComp = new Composite(wTabFolder, SWT.NONE);
    props.setLook(wProxyComp);
    wProxyComp.setLayout(poolLayout);

    // What's the data tablespace name?
    wlProxyHost = new Label(wProxyComp, SWT.RIGHT);
    props.setLook(wlProxyHost);
    wlProxyHost.setText(Messages.getString("SecurityServiceDialog.USER_PROXY_SERVER_HOSTNAME")); //$NON-NLS-1$
    FormData fdlProxyHost = new FormData();
    fdlProxyHost.top = new FormAttachment(0, 0);
    fdlProxyHost.left = new FormAttachment(0, 0); // First one in the left top corner
    fdlProxyHost.right = new FormAttachment(middle, -margin);
    wlProxyHost.setLayoutData(fdlProxyHost);

    wProxyHost = new Text(wProxyComp, SWT.BORDER | SWT.LEFT | SWT.SINGLE);
    props.setLook(wProxyHost);
    wProxyHost.addModifyListener(lsMod);
    FormData fdProxyHost = new FormData();
    fdProxyHost.top = new FormAttachment(0, 0);
    fdProxyHost.left = new FormAttachment(middle, margin); // To the right of the label
    fdProxyHost.right = new FormAttachment(95, 0);
    wProxyHost.setLayoutData(fdProxyHost);

    // What's the initial pool size
    wlProxyPort = new Label(wProxyComp, SWT.RIGHT);
    props.setLook(wlProxyPort);
    wlProxyPort.setText(Messages.getString("SecurityServiceDialog.USER_PROXY_SERVE_PORT")); //$NON-NLS-1$
    FormData fdlProxyPort = new FormData();
    fdlProxyPort.top = new FormAttachment(wProxyHost, margin);
    fdlProxyPort.left = new FormAttachment(0, 0); // First one in the left top corner
    fdlProxyPort.right = new FormAttachment(middle, -margin);
    wlProxyPort.setLayoutData(fdlProxyPort);

    wProxyPort = new Text(wProxyComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wProxyPort);
    wProxyPort.addModifyListener(lsMod);
    FormData fdProxyPort = new FormData();
    fdProxyPort.top = new FormAttachment(wProxyHost, margin);
    fdProxyPort.left = new FormAttachment(middle, margin); // To the right of the label
    fdProxyPort.right = new FormAttachment(95, 0);
    wProxyPort.setLayoutData(fdProxyPort);

    // What's the maximum pool size
    wlNonProxyHosts = new Label(wProxyComp, SWT.RIGHT);
    props.setLook(wlNonProxyHosts);
    wlNonProxyHosts.setText(Messages.getString("SecurityServiceDialog.USER_IGNORE_PRXY_FOR_HOSTS")); //$NON-NLS-1$
    FormData fdlNonProxyHosts = new FormData();
    fdlNonProxyHosts.top = new FormAttachment(wProxyPort, margin);
    fdlNonProxyHosts.left = new FormAttachment(0, 0); // First one in the left top corner
    fdlNonProxyHosts.right = new FormAttachment(middle, -margin);
    wlNonProxyHosts.setLayoutData(fdlNonProxyHosts);

    wNonProxyHosts = new Text(wProxyComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wNonProxyHosts);
    wNonProxyHosts.addModifyListener(lsMod);
    FormData fdNonProxyHosts = new FormData();
    fdNonProxyHosts.top = new FormAttachment(wProxyPort, margin);
    fdNonProxyHosts.left = new FormAttachment(middle, margin); // To the right of the label
    fdNonProxyHosts.right = new FormAttachment(95, 0);
    wNonProxyHosts.setLayoutData(fdNonProxyHosts);

    fdProxyComp = new FormData();
    fdProxyComp.left = new FormAttachment(0, 0);
    fdProxyComp.top = new FormAttachment(0, 0);
    fdProxyComp.right = new FormAttachment(100, 0);
    fdProxyComp.bottom = new FormAttachment(100, 0);
    wProxyComp.setLayoutData(fdProxyComp);

    wProxyComp.layout();
    wProxyTab.setControl(wProxyComp);
  }

  private void addFileTab() {
    //////////////////////////
    // START OF POOL TAB///
    ///
    wFileTab = new CTabItem(wTabFolder, SWT.NONE);
    wFileTab.setText(Messages.getString("SecurityServiceDialog.USER_FILE")); //$NON-NLS-1$

    FormLayout poolLayout = new FormLayout();
    poolLayout.marginWidth = Const.FORM_MARGIN;
    poolLayout.marginHeight = Const.FORM_MARGIN;

    wFileComp = new Composite(wTabFolder, SWT.NONE);
    props.setLook(wFileComp);
    wFileComp.setLayout(poolLayout);

    Button wbbFile = new Button(wFileComp, SWT.PUSH | SWT.CENTER);
    props.setLook(wbbFile);
    wbbFile.setText(Messages.getString("SecurityServiceDialog.USER_BROWSE")); //$NON-NLS-1$
    wbbFile.setToolTipText(Messages.getString("SecurityServiceDialog.USER_SELECT_XML_FILE")); //$NON-NLS-1$
    FormData fdbFile = new FormData();
    fdbFile.right = new FormAttachment(95, 0);
    fdbFile.top = new FormAttachment(0, 0);
    wbbFile.setLayoutData(fdbFile);

    // What's the data tablespace name?
    wlFile = new Label(wFileComp, SWT.RIGHT);
    props.setLook(wlFile);
    wlFile.setText(Messages.getString("SecurityServiceDialog.USER_FILENAME")); //$NON-NLS-1$
    FormData fdlFile = new FormData();
    fdlFile.top = new FormAttachment(0, 0);
    fdlFile.left = new FormAttachment(0, 0); // First one in the left top corner
    fdlFile.right = new FormAttachment(middle, -margin);
    wlFile.setLayoutData(fdlFile);

    wFile = new Text(wFileComp, SWT.BORDER | SWT.LEFT | SWT.SINGLE);
    props.setLook(wFile);
    wFile.addModifyListener(lsMod);
    FormData fdFile = new FormData();
    fdFile.top = new FormAttachment(0, 0);
    fdFile.left = new FormAttachment(middle, margin); // To the right of the label
    fdFile.right = new FormAttachment(wbbFile, -margin);
    wFile.setLayoutData(fdFile);

    // Listen to the Browse... button
    wbbFile.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] { "*.xml", "*" }); //$NON-NLS-1$ //$NON-NLS-2$
        if (!Const.isEmpty(wFile.getText())) {
          dialog.setFileName(wFile.getText());
        }
        dialog
            .setFilterNames(new String[] {
                Messages.getString("SecurityServiceDialog.USER_XML_FILES"), Messages.getString("SecurityServiceDialog.USER_ALL_FILES") }); //$NON-NLS-1$ //$NON-NLS-2$
        if (dialog.open() != null) {
          String str = dialog.getFilterPath() + System.getProperty("file.separator") + dialog.getFileName(); //$NON-NLS-1$
          wFile.setText(str);
        }
      }
    });

    fdFileComp = new FormData();
    fdFileComp.left = new FormAttachment(0, 0);
    fdFileComp.top = new FormAttachment(0, 0);
    fdFileComp.right = new FormAttachment(100, 0);
    fdFileComp.bottom = new FormAttachment(100, 0);
    wFileComp.setLayoutData(fdFileComp);

    wFileComp.layout();
    wFileTab.setControl(wFileComp);
  }

  public void dispose() {
    props.setScreen(new WindowProperty(getShell()));
    getShell().dispose();
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Security Service");
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Point getInitialSize() {
    return new Point(524, 351);
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

  /***************** BUSINESSS LOGIC FOR DIALOG ***************************************************/
  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wServiceURL.setText(Const.NVL(securityService.getServiceURL(), "")); //$NON-NLS-1$
    wDetailType.select(securityService.getDetailServiceType());
    wUsername.setText(Const.NVL(securityService.getUsername(), "")); //$NON-NLS-1$
    wPassword.setText(Const.NVL(securityService.getPassword(), "")); //$NON-NLS-1$

    wProxyHost.setText(Const.NVL(securityService.getProxyHostname(), "")); //$NON-NLS-1$
    wProxyPort.setText(Const.NVL(securityService.getProxyPort(), "")); //$NON-NLS-1$
    wNonProxyHosts.setText(Const.NVL(securityService.getNonProxyHosts(), "")); //$NON-NLS-1$

    wFile.setText(Const.NVL(securityService.getFilename(), "")); //$NON-NLS-1$

    wServiceURL.setFocus();
  }

  private void cancel() {
    originalService = null;
    dispose();
  }

  public void ok() {
    getInfo();
    originalService.setServiceURL(securityService.getServiceURL());
    originalService.setDetailServiceType(securityService.getDetailServiceType());
    originalService.setUsername(securityService.getUsername());
    originalService.setPassword(securityService.getPassword());

    originalService.setProxyHostname(securityService.getProxyHostname());
    originalService.setProxyPort(securityService.getProxyPort());
    originalService.setNonProxyHosts(securityService.getNonProxyHosts());

    originalService.setFilename(securityService.getFilename());

    originalService.setChanged();

    dispose();
  }

  public void load() {
    reload = true;
    ok();
  }

  // Get dialog info in securityService
  private void getInfo() {
    securityService.setServiceURL(wServiceURL.getText());
    securityService.setDetailServiceType(wDetailType.getSelectionIndex());
    securityService.setUsername(wUsername.getText());
    securityService.setPassword(wPassword.getText());

    securityService.setProxyHostname(wProxyHost.getText());
    securityService.setProxyPort(wProxyPort.getText());
    securityService.setNonProxyHosts(wNonProxyHosts.getText());

    securityService.setFilename(wFile.getText());
  }

  public void test() {
    try {
      getInfo();

      // Load the security reference information...
      SecurityReference securityReference = new SecurityReference(securityService);
      String xml = securityReference.toXML();

      String message = Messages.getString("SecurityServiceDialog.USER_CONNECTION_INFO"); //$NON-NLS-1$
      if (securityService.hasService())
        message += Messages.getString(
            "SecurityServiceDialog.USER_FROM_SERVER_URL", securityService.getURL() + Const.CR + Const.CR); //$NON-NLS-1$
      else
        message += Messages.getString(
            "SecurityServiceDialog.USER_FROM_FILE", securityService.getFilename() + Const.CR + Const.CR); //$NON-NLS-1$
      message += xml;

      EnterTextDialog dialog = new EnterTextDialog(
          getShell(),
          Messages.getString("SecurityServiceDialog.USER_TITLE_XML"), Messages.getString("SecurityServiceDialog.USER_XML_RETURNED"), message); //$NON-NLS-1$ //$NON-NLS-2$
      dialog.open();
    } catch (Exception e) {
      new ErrorDialog(
          getShell(),
          Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("SecurityServiceDialog.USER_ERROR_CANT_GET_SECURITY_INFO", securityService.getURL()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
    }
  }
}
