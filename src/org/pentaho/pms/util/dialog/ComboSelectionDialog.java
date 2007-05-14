package org.pentaho.pms.util.dialog;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.pms.schema.dialog.CategoryEditorDialog;
import org.pentaho.pms.util.GUIResource;

public class ComboSelectionDialog extends TitleAreaDialog {
  
  Combo selectionControl = null;
  
  private String title, message = null;
  private String[] selections = null;
  String returnSelection = null;
  
  public ComboSelectionDialog(Shell shell, String message, String title, String[] selections) {
    super(shell);
    this.title = title;
    this.message = message;
    this.selections = selections;
  }

  private static final Log logger = LogFactory.getLog(CategoryEditorDialog.class);

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

    selectionControl = new Combo (c1, SWT.NONE);
    selectionControl.setItems (selections);
    data = new GridData ();
    data.horizontalAlignment = GridData.CENTER;
    data.horizontalIndent = 1;
    data.grabExcessHorizontalSpace = true;
    selectionControl.setLayoutData (data);

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
    return new Point(400, 200);
  }

  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  public String getSelection(){
    return returnSelection; 
  }
  
  protected void buttonPressed(int buttonId) {
    if (buttonId == IDialogConstants.OK_ID){
      returnSelection = selectionControl.getItem(selectionControl.getSelectionIndex());
    }else{
      returnSelection = null;
    }
    
    setReturnCode(buttonId);      
    close();
  }
  

}
