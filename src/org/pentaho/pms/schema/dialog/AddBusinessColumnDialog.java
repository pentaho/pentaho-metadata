package org.pentaho.pms.schema.dialog;

import java.util.ArrayList;

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
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.ITableModel;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;


public class AddBusinessColumnDialog extends TitleAreaDialog {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(AddBusinessColumnDialog.class);

  // ~ Instance fields =================================================================================================

  private org.eclipse.swt.widgets.List businessColumnList;

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

    businessColumnList = new org.eclipse.swt.widgets.List(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

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

  private void addUnusedColumns() {
    ConceptUtilityInterface busCols[] = tableModel.getColumns();

    java.util.List<String>busColIds = new ArrayList<String>();
    for (int i = 0; i < busCols.length; i++) {
      busColIds.add(busCols[i].getId());
    }
    String used[] = (String[]) busColIds.toArray(new String[0]);

    ConceptUtilityInterface[] phyCols = tableModel.getParentAsTableModel().getColumns();

    java.util.List<ConceptUtilityInterface> newBusCols = new ArrayList<ConceptUtilityInterface>();
    for (int i = 0; i < phyCols.length; i++) {
      PhysicalColumn column = (PhysicalColumn) phyCols[i];

      // TODO We are trying to determine if the column is already in play. Our two logical options are to
      // test the id of the physical column, and the proposed id of the physical column, to see
      // if those values exist in our "used" list. If the user has re-named the id, this logic will
      // not catch the duplicate. We may want to revisit this and make the logic more robust.

      String newId = BusinessColumn.proposeId(activeLocale, (BusinessTable) tableModel.getWrappedTable(), column);

      if ((Const.indexOfString(column.getId(), used) < 0) && (Const.indexOfString(newId, used) < 0)) {
        BusinessColumn businessColumn = new BusinessColumn(newId, column, (BusinessTable) tableModel.getWrappedTable());

        newBusCols.add(businessColumn);
      }
    }
    try {
      tableModel.addAllColumns((ConceptUtilityInterface[]) newBusCols.toArray(new ConceptUtilityInterface[0]));
    } catch (ObjectAlreadyExistsException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      MessageDialog.openError(getShell(), "Column Add Error", "A column with an already existing id cannot be added.");
    }

    // treat this button similar to OK button
    super.okPressed();
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
