package org.pentaho.pms.util.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ListSelectionDialog extends TitleAreaDialog {

  // ~ Static fields/initializers ====================================================================================== 

  private static final Log logger = LogFactory.getLog(ListSelectionDialog.class);

  // ~ Instance fields =================================================================================================

  ListViewer selectionControl = null;

  private String title, message = null;

  private Object[] selections = null;

  Object returnSelection = null;

  // ~ Constructors   ==================================================================================================

  public ListSelectionDialog(Shell shell, String message, String title, Object[] selections) {
    super(shell);
    this.title = title;
    this.message = message;
    this.selections = selections;
  }

  // ~ Methods =========================================================================================================

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Make Selection");
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Control createDialogArea(final Composite parent) {

    Composite c0 = (Composite) super.createDialogArea(parent);

    setTitle(title);
    setMessage(message);

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new FormLayout());
    GridData gdContainer = new GridData(GridData.FILL_BOTH);
    container.setLayoutData(gdContainer);

    selectionControl = new ListViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    selectionControl.setContentProvider(new ListProvider());
    selectionControl.setLabelProvider(new ListLabelProvider());
    selectionControl.setInput(selections);

    FormData fdSelectionControl = new FormData();
    fdSelectionControl.left = new FormAttachment(0, 0);
    fdSelectionControl.top = new FormAttachment(0, 0);
    fdSelectionControl.right = new FormAttachment(100, 0);
    fdSelectionControl.bottom = new FormAttachment(100, 0);
    selectionControl.getList().setLayoutData(fdSelectionControl);

    return c0;
  }

  protected Point getInitialSize() {
    return new Point(400, 235);
  }

  public Object getSelection() {
    return returnSelection;
  }

  @Override
  protected void okPressed() {
    returnSelection = ((IStructuredSelection) selectionControl.getSelection()).getFirstElement();
    super.okPressed();
  }

  @Override
  protected void cancelPressed() {
    returnSelection = null;
    super.cancelPressed();
  }

  // inner classes supporting the listViewer
  class ListProvider implements IStructuredContentProvider {

    public Object[] getElements(Object arg0) {
      return selections;
    }

    public void dispose() {
      // this.dispose();
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

    }
  }

  class ListLabelProvider extends LabelProvider {

    public Image getImage(Object arg0) {
      return null;
    }

    public String getText(Object obj) {
      return obj.toString();
    }
  }

}
