package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public class AddPropertyDialog extends TitleAreaDialog {

  private static final Log logger = LogFactory.getLog(PropertyTreeWidget.class);

  private IConceptModel conceptModel;

  private PropertyTreeWidget propertyTree;

  public AddPropertyDialog(Shell parentShell, IConceptModel conceptModel) {
    super(parentShell);
    this.conceptModel = conceptModel;
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Add New Property");
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Control createDialogArea(final Composite parent) {
    // composite below (from framework) uses GridLayout
    Composite c0 = (Composite) super.createDialogArea(parent);
    Composite c1 = new Composite(c0, SWT.NONE);
    c1.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridLayout gl1 = new GridLayout();
    gl1.marginHeight = 10;
    gl1.marginWidth = 10;
    c1.setLayout(gl1);
    setTitle("Properties");
    setMessage("Add a property to the current concept.");
    propertyTree = new PropertyTreeWidget(c1, SWT.NONE, conceptModel, PropertyTreeWidget.SHOW_UNUSED, false);
    propertyTree.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(final SelectionChangedEvent e) {
        if (e.getSelection() instanceof PropertyTreeSelection) {
          PropertyTreeSelection sel = (PropertyTreeSelection) e.getSelection();
          if (sel.isGroup()) {
            setErrorMessage("Please select a property within a group.");
            getButton(IDialogConstants.OK_ID).setEnabled(false);
          } else {
            setErrorMessage(null);
            getButton(IDialogConstants.OK_ID).setEnabled(true);
          }
        }
      }

    });
    GridData gd1 = new GridData(GridData.FILL_BOTH);
    propertyTree.setLayoutData(gd1);
    return c0;
  }

  protected Point getInitialSize() {
    return new Point(400, 300);
  }

  protected void okPressed() {
    // might be null
    ISelection sel = propertyTree.getSelection();
    if (sel instanceof PropertyTreeSelection) {
      PropertyTreeSelection treeSel = (PropertyTreeSelection) sel;
      String propertyId = treeSel.getName();
      conceptModel.setProperty(DefaultPropertyID.findDefaultPropertyID(propertyId).getDefaultValue());
      // event should now fire from the model
    } else {
      if (logger.isWarnEnabled()) {
        logger.warn("unknown node selected in property tree");
      }
    }
    super.okPressed();
  }

}