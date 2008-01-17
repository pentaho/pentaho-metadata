package org.pentaho.pms.util.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.util.GUIResource;

public class ListSelectionDialog extends TitleAreaDialog {
  
  ListViewer selectionControl = null;
  
  private String title, message = null;
  private Object[] selections = null;
  Object returnSelection = null;
  
  public ListSelectionDialog(Shell shell, String message, String title, Object[] selections) {
    super(shell);
    this.title = title;
    this.message = message;
    this.selections = selections;
  }

  private static final Log logger = LogFactory.getLog(ListSelectionDialog.class);

  protected Control createContents(Composite parent) {
    Control contents = super.createContents(parent);
    setMessage(message);
    setTitle(title);
    return contents;
  }

   protected Control createDialogArea(final Composite parent) {

    Composite c0 = (Composite) super.createDialogArea(parent);
        
    c0.setBackground(GUIResource.getInstance().getColorWhite());
    Composite c1 = new Composite(c0, SWT.BORDER);
    c1.setBackground(GUIResource.getInstance().getColorWhite());
    GridData data = new GridData (GridData.FILL_BOTH);
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    c1.setLayoutData (data);

    GridLayout gridLayout = new GridLayout ();
    c1.setLayout (gridLayout);

    selectionControl = new ListViewer (c1, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
     
    selectionControl.setContentProvider(new ListProvider());
    selectionControl.setLabelProvider(new ListLabelProvider());
    selectionControl.setInput (selections);
    data = new GridData ();
    data.horizontalAlignment = GridData.CENTER;
    data.horizontalIndent = 1;
    data.grabExcessHorizontalSpace = true;
    selectionControl.getList().setLayoutData (data);

    return c0;
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Make Selection");
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Point getInitialSize() {
    return new Point(400, 235);
  }

  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  public Object getSelection(){
    return returnSelection; 
  }
  
  protected void buttonPressed(int buttonId) {
    returnSelection = null;
    if (buttonId == IDialogConstants.OK_ID){
      returnSelection = ((IStructuredSelection)selectionControl.getSelection()).getFirstElement();
    }else{
      returnSelection = null;
    }
    
    setReturnCode(buttonId);      
    close();
  }

  // inner classes supporting the listViewer
  class ListProvider implements IStructuredContentProvider{

    public Object[] getElements(Object arg0) {
      return selections;
    }

    public void dispose() {
     // this.dispose();
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
      
    }
  }
  
  class ListLabelProvider extends LabelProvider{

    public Image getImage(Object arg0) {
      return null;
    }

    public String getText(Object obj) {
      return obj.toString();
    }
  }
  

}

