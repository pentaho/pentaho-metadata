package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class AddPropertyDialog extends TitleAreaDialog {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyTreeWidget.class);

  // ~ Instance fields ===================================================================================================

  private IConceptModel conceptModel;

  private PropertyTreeWidget propertyTree;

  private Button predefinedButton;

  private Button customButton;

  private Text idField;

  private Combo typeField;

  private ComboViewer comboViewer;

  // ~ Constructors ======================================================================================================

  public AddPropertyDialog(Shell parentShell, IConceptModel conceptModel) {
    super(parentShell);
    this.conceptModel = conceptModel;
  }

  // ~ Methods ===========================================================================================================

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
//    Composite c3 = new Composite(c0, SWT.BORDER);
//    c3.setLayoutData(new GridData(GridData.FILL_BOTH));
//    GridLayout gl3 = new GridLayout();
//    gl3.marginHeight = 10;
//    gl3.marginWidth = 10;
//    c3.setLayout(new FormLayout());
    Composite c1 = new Composite(c0, SWT.NONE);
    c1.setLayoutData(new GridData(GridData.FILL_BOTH));
    c1.setLayout(new FormLayout());
    setTitle("Properties");
    setMessage("Add a property to the current concept.");
    predefinedButton = new Button(c1, SWT.RADIO);
    FormData fdPreDefButton = new FormData();
    fdPreDefButton.left = new FormAttachment(0, 10);
    fdPreDefButton.top = new FormAttachment(0, 10);
    predefinedButton.setLayoutData(fdPreDefButton);

    predefinedButton.setText("Add a pre-defined property");
    predefinedButton.addSelectionListener(new DisableFieldsListener());
    propertyTree = new PropertyTreeWidget(c1, SWT.NONE, conceptModel, PropertyTreeWidget.SHOW_UNUSED, false);
    propertyTree.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(final SelectionChangedEvent e) {
        validatePredefined();
      }

    });

    customButton = new Button(c1, SWT.RADIO);

    FormData fdTree = new FormData();
    fdTree.left = new FormAttachment(0, 10);
    fdTree.top = new FormAttachment(predefinedButton, 10);
    fdTree.right = new FormAttachment(100, -10);
    fdTree.bottom = new FormAttachment(65, 0);
    propertyTree.setLayoutData(fdTree);

    FormData fdCustomButton = new FormData();
    fdCustomButton.left = new FormAttachment(0, 10);
    fdCustomButton.top = new FormAttachment(propertyTree, 10);
    customButton.setLayoutData(fdCustomButton);

    customButton.setText("Add a custom property");
    customButton.addSelectionListener(new DisableFieldsListener());

    Composite c2 = new Composite(c1, SWT.NONE);
    FormData fdC2 = new FormData();
    fdC2.left = new FormAttachment(0, 0);
    fdC2.top = new FormAttachment(customButton, 10);
    fdC2.right = new FormAttachment(100, 0);
    c2.setLayoutData(fdC2);

    c2.setLayout(new FormLayout());
    //    GridData gd2 = new GridData(GridData.FILL_BOTH);
    //    c2.setLayoutData(gd2);

    Label lab1 = new Label(c2, SWT.RIGHT);
    idField = new Text(c2, SWT.BORDER);
    Label lab2 = new Label(c2, SWT.RIGHT);
    typeField = new Combo(c2, SWT.NONE | SWT.READ_ONLY);

    lab1.setText("ID:");
    FormData fdLab1 = new FormData();
    fdLab1.left = new FormAttachment(0, 10);
    fdLab1.top = new FormAttachment(idField, 0, SWT.CENTER);
    lab1.setLayoutData(fdLab1);

    // default to predefined property (the other radio group)
    idField.setEnabled(false);

    idField.addModifyListener(new ModifyListener() {

      public void modifyText(ModifyEvent e) {
        validateCustom();
      }

    });
    FormData fdIdField = new FormData();
    fdIdField.left = new FormAttachment(lab1, 10);
    fdIdField.right = new FormAttachment(100, -10);
    idField.setLayoutData(fdIdField);

    lab2.setText("Type:");
    FormData fdLab2 = new FormData();
    fdLab2.left = new FormAttachment(0, 10);
    fdLab2.top = new FormAttachment(typeField, 0, SWT.CENTER);
    lab2.setLayoutData(fdLab2);

    // default to predefined property (the other radio group)
    typeField.setEnabled(false);
    FormData fdTypeField = new FormData();
    fdTypeField.left = new FormAttachment(lab2, 10);
    fdTypeField.right = new FormAttachment(100, -10);
    fdTypeField.top = new FormAttachment(idField, 10);
    typeField.setLayoutData(fdTypeField);

    comboViewer = new ComboViewer(typeField);
    comboViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(final Object inputElement) {
        List ul = (List) inputElement;
        return ul.toArray();
      }

      public void dispose() {
        if (logger.isDebugEnabled()) {
          logger.debug("Disposing ...");
        }
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (logger.isDebugEnabled()) {
          logger.debug("Input changed: old=" + oldInput + ", new=" + newInput);
        }
      }
    });

    List list2 = new ArrayList();
    list2.add("");
    list2.addAll(Arrays.asList(ConceptPropertyType.propertyTypes));

    comboViewer.setInput(list2);

    comboViewer.setLabelProvider(new LabelProvider() {
      public Image getImage(Object element) {
        return null;
      }

      public String getText(Object element) {
        if (element instanceof ConceptPropertyType) {
          ConceptPropertyType type = (ConceptPropertyType) element;
          if (logger.isDebugEnabled()) {
            logger.debug("desc: " + type.getDescription());
          }
          return type.getDescription();
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug("obj class: " + element.getClass());
          }
          return "";
        }
      }
    });

    comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      public void selectionChanged(SelectionChangedEvent e) {
        validateCustom();
      }

    });

    predefinedButton.setSelection(true);
    return c0;
  }

  private void validatePredefined() {
    if (logger.isDebugEnabled()) {
      logger.debug("prop tree sel: " + propertyTree.getSelection());
    }
    if (propertyTree.getSelection() instanceof PropertyTreeSelection) {
      PropertyTreeSelection sel = (PropertyTreeSelection) propertyTree.getSelection();
      if (!sel.isGroup()) {
        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return;
      }
    }
    setErrorMessage("Please select a property within a group.");
    getButton(IDialogConstants.OK_ID).setEnabled(false);
  }

  private void validateCustom() {
    if (StringUtils.isBlank(idField.getText())) {
      setErrorMessage("Please enter an ID.");
      getButton(IDialogConstants.OK_ID).setEnabled(false);
      return;
    } else if (isPredefinedPropertyId(idField.getText())) {
      setErrorMessage("The ID entered cannot be a pre-defined property ID. Please enter a different ID.");
      getButton(IDialogConstants.OK_ID).setEnabled(false);
      return;
    }  else {
      setErrorMessage(null);
      getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
    if (selection.getFirstElement() instanceof ConceptPropertyType) {
      setErrorMessage(null);
      getButton(IDialogConstants.OK_ID).setEnabled(true);
    } else {
      setErrorMessage("Please select a type.");
      getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
  }

  /**
   * Returns true if the given id is a pre-defined property id.
   */
  private boolean isPredefinedPropertyId(final String propertyId) {
    String[] propertyIds = DefaultPropertyID.getDefaultPropertyIDs();
    for (int i = 0; i < propertyIds.length; i++) {
      if (propertyIds[i].equals(propertyId)) {
        return true;
      }
    }
    return false;
  }

  protected Control createContents(Composite parent) {
    // start with the OK button disabled
    Control c = super.createContents(parent);
    getButton(IDialogConstants.OK_ID).setEnabled(false);
    return c;
  }

  protected Point getInitialSize() {
    return new Point(500, 500);
  }

  protected void okPressed() {
    String propertyId = null;
    ConceptPropertyType type = null;
    if (predefinedButton.getSelection()) {
      // might be null
      ISelection sel = propertyTree.getSelection();
      if (sel instanceof PropertyTreeSelection) {
        PropertyTreeSelection treeSel = (PropertyTreeSelection) sel;
        propertyId = treeSel.getName();
      } else {
        if (logger.isWarnEnabled()) {
          logger.warn("unknown node selected in property tree");
        }
        return;
      }
    } else {
      IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
      if (selection.getFirstElement() instanceof ConceptPropertyType) {
        propertyId = idField.getText();
        type = (ConceptPropertyType) selection.getFirstElement();

      } else {
        if (logger.isWarnEnabled()) {
          logger.warn("unknown item selected in concept property type combo");
        }
        return;
      }
    }

    conceptModel.setProperty(PredefinedVsCustomPropertyHelper.createEmptyProperty(propertyId, type));

    super.okPressed();
  }

  private class DisableFieldsListener implements SelectionListener {

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
      if (predefinedButton.getSelection()) {
        propertyTree.setEnabled(true);
        idField.setEnabled(false);
        typeField.setEnabled(false);
        validatePredefined();
      } else {
        propertyTree.setEnabled(false);
        idField.setEnabled(true);
        typeField.setEnabled(true);
        validateCustom();
      }
    }

  }

}