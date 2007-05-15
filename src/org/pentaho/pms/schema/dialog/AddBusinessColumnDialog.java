package org.pentaho.pms.schema.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.schema.concept.editor.ITableModel;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class AddBusinessColumnDialog extends TitleAreaDialog {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(AddBusinessColumnDialog.class);

  // ~ Instance fields =================================================================================================

  private List businessColumnList;

  private ITableModel tableModel;

  private String activeLocale;

  // ~ Constructors ====================================================================================================

  public AddBusinessColumnDialog(final Shell parentShell, final ITableModel tableModel, final String activeLocale) {
    super(parentShell);
    this.tableModel = tableModel;
    this.activeLocale = activeLocale;
  }

  // ~ Methods =========================================================================================================

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Add New Column");
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Control createDialogArea(Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);

    setTitle("Columns");
    setMessage("Add a column to the business table.");

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new FormLayout());
    GridData gdContainer = new GridData(GridData.FILL_BOTH);
    container.setLayoutData(gdContainer);

    businessColumnList = new List(container, SWT.MULTI | SWT.BORDER);

    businessColumnList.setItems(tableModel.getParentAsTableModel().getColumnNames(activeLocale));

    FormData fdBusinessColumnList = new FormData();
    fdBusinessColumnList.left = new FormAttachment(0, 0);
    fdBusinessColumnList.top = new FormAttachment(0, 0);
    fdBusinessColumnList.right = new FormAttachment(100, 0);
    fdBusinessColumnList.bottom = new FormAttachment(100, 0);
    businessColumnList.setLayoutData(fdBusinessColumnList);

    return c0;
  }

  protected void createButtonsForButtonBar(final Composite parent) {
    Button addUnusedColumnsButton = createButton(parent, 500, "Add Unused Columns", false);
    addUnusedColumnsButton.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        addUnusedColumns();
      }
    });

    super.createButtonsForButtonBar(parent);
  }

  /**
   * TODO mlowery needs to be implemented; was copied from old BusinessTableDialog
   */
  private void addUnusedColumns() {
    //    PhysicalTable physicalTable = schemaMeta.findPhysicalTable(physicalTableText.getText());
    //    if (physicalTable != null) {
    //      businessTable.setPhysicalTable(physicalTable);
    //
    //      if (!Const.isEmpty(wName.getText())) {
    //        try {
    //          businessTable.setId(wName.getText());
    //        } catch (ObjectAlreadyExistsException e) {
    //          new ErrorDialog(
    //              shell,
    //              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_BUSINESS_TABLE_ID_EXISTS", wName.getText()), e); //$NON-NLS-1$ //$NON-NLS-2$
    //          return;
    //        }
    //      }
    //
    //      // The pysical column IDs?
    //      //String used[] = physicalTable.getColumnIDs();
    //      String used[] = businessTable.getColumnIDs();
    //
    //      for (int i = 0; i < physicalTable.nrPhysicalColumns(); i++) {
    //        PhysicalColumn column = physicalTable.getPhysicalColumn(i);
    //
    //        // TODO We are trying to determine if the column is already in play. Our two logical options are to
    //        // test the id of the physical column, and the proposed id of the physical column, to see
    //        // if those values exist in our "used" list. If the user has re-named the id, this logic will
    //        // not catch the duplicate. We may want to revisit this and make the logic more robust.
    //
    //        String newId = BusinessColumn.proposeId(schemaMeta.getActiveLocale(), businessTable, column);
    //
    //        if ((Const.indexOfString(column.getId(), used) < 0) && (Const.indexOfString(newId, used) < 0)) {
    //          BusinessColumn businessColumn = new BusinessColumn(newId, column, businessTable);
    //
    //          try {
    //            businessTable.addBusinessColumn(businessColumn);
    //          } catch (ObjectAlreadyExistsException e) {
    //            new ErrorDialog(
    //                shell,
    //                Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessTableDialog.USER_ERROR_ID_EXISTS_NOT_ADDED", businessColumn.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$
    //          }
    //        }
    //      }
    //
    //    } else {
    //      MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
    //      mb.setMessage(Messages.getString("BusinessTableDialog.USER_ERROR_CANT_FIND_PHYSICAL_TABLE")); //$NON-NLS-1$
    //      mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
    //      mb.open();
    //    }
  }

  protected void okPressed() {
    String selections[] = businessColumnList.getSelection();

    for (int i = 0; i < selections.length; i++) {
      try {
        tableModel.addColumn(selections[i], activeLocale);
      } catch (ObjectAlreadyExistsException e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e);
        }
        MessageDialog.openError(getShell(), "Column Add Error", "A column with id '" + selections[i]
            + "' already exists.");
      }
    }
    super.okPressed();
  }

}
