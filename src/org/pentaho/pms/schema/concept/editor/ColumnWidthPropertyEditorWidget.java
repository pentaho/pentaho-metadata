package org.pentaho.pms.schema.concept.editor;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;

public class ColumnWidthPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ColumnWidthPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private ComboViewer typeComboViewer;

  private Text width;

  private FocusListener focusListener;

  private ISelectionChangedListener selectionChangedListener;

  // ~ Constructors ====================================================================================================

  public ColumnWidthPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId) {
    super(parent, style, conceptModel, propertyId);
    setValue(getProperty().getValue());
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        ColumnWidthPropertyEditorWidget.this.widgetDisposed(e);
      }
    });
    Label typeLabel = new Label(parent, SWT.NONE);
    typeLabel.setText("Column Width Type:"); //$NON-NLS-1$

    Combo type = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);

    typeComboViewer = new ComboViewer(type);

    typeComboViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(final Object inputElement) {
        return (ColumnWidth[]) inputElement;
      }

      public void dispose() {
      }

      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      }
    });

    typeComboViewer.setInput(ColumnWidth.types);

    typeComboViewer.setLabelProvider(new LabelProvider() {
      public Image getImage(final Object element) {
        // no images in this combo
        return null;
      }

      public String getText(final Object element) {
        return ((ColumnWidth) element).getDescription();
      }
    });

    FormData fdType = new FormData();
    fdType.left = new FormAttachment(typeLabel, 10);
    fdType.top = new FormAttachment(0, 0);
    type.setLayoutData(fdType);

    FormData fdTypeLabel = new FormData();
    fdTypeLabel.left = new FormAttachment(0, 0);
    fdTypeLabel.top = new FormAttachment(type, 0, SWT.CENTER);
    typeLabel.setLayoutData(fdTypeLabel);

    Label widthLabel = new Label(parent, SWT.NONE);
    widthLabel.setText("Column Width:"); //$NON-NLS-1$

    width = new Text(parent, SWT.BORDER | SWT.SINGLE | SWT.LEFT);

    // only allow digits
    width.addListener(SWT.Verify, new Listener() {
      public void handleEvent(final Event e) {
        String string = e.text;
        char[] chars = new char[string.length()];
        string.getChars(0, chars.length, chars, 0);
        for (int i = 0; i < chars.length; i++) {
          if (!(Character.isDigit(chars[i]))) {
            e.doit = false;
            return;
          }
        }
      }
    });

    //    width.setToolTipText(Messages.getString("ConceptPropertyColumnWidthWidget.USER_SELECT_PROPERTY_WIDTH", name)); //$NON-NLS-1$
    FormData fdWidth = new FormData();
    fdWidth.left = new FormAttachment(widthLabel, 10);
    fdWidth.right = new FormAttachment(100, 0);
    fdWidth.top = new FormAttachment(type, 10);
    width.setLayoutData(fdWidth);

    FormData fdWidthLabel = new FormData();
    fdWidthLabel.left = new FormAttachment(0, 0);
    fdWidthLabel.top = new FormAttachment(width, 0, SWT.CENTER);
    widthLabel.setLayoutData(fdWidthLabel);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {

    IStructuredSelection selection = (IStructuredSelection) typeComboViewer.getSelection();
    ColumnWidth columnWidth = (ColumnWidth) selection.getFirstElement();
    int widthType = columnWidth.getType();
    BigDecimal widthValue;
    try {
      widthValue = new BigDecimal(width.getText());
    } catch (Exception e) {
      widthValue = new BigDecimal(0);
    }
    return new ColumnWidth(widthType, widthValue);
  }

  protected void setValue(final Object value) {
    if (value instanceof ColumnWidth) {
      ColumnWidth columnWidth = (ColumnWidth) value;
      typeComboViewer.setSelection(new StructuredSelection(ColumnWidth.getType(columnWidth.getCode())));
      if (columnWidth.getWidth() != null) {
        width.setText(columnWidth.getWidth().toString());
      } else {
        width.setText("");
      }
    }
  }

  protected void addModificationListeners() {
    if (null == selectionChangedListener) {
      selectionChangedListener = new PropertyEditorWidgetSelectionChangedListener();
      focusListener = new PropertyEditorWidgetFocusListener();
      typeComboViewer.addSelectionChangedListener(selectionChangedListener);
      width.addFocusListener(focusListener);
    }
  }

  protected void removeModificationListeners() {
    typeComboViewer.removeSelectionChangedListener(selectionChangedListener);
    width.removeFocusListener(focusListener);
  }

  protected boolean isValid() {
    try {
      new BigDecimal(width.getText());
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

}
