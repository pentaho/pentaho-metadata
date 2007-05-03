package org.pentaho.pms.schema.concept.editor;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.pentaho.pms.schema.concept.types.alignment.AlignmentSettings;

public class AlignmentPropertyEditorWidget extends AbstractPropertyEditorWidget {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(AlignmentPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private ComboViewer typeComboViewer;

  private ISelectionChangedListener selectionChangedListener;

  // ~ Constructors ====================================================================================================

  public AlignmentPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId) {
    super(parent, style, conceptModel, propertyId);
    setValue(getProperty().getValue());
    if (logger.isDebugEnabled()) {
      logger.debug("created AlignmentPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        AlignmentPropertyEditorWidget.this.widgetDisposed(e);
      }
    });
    Label typeLabel = new Label(parent, SWT.NONE);
    typeLabel.setText("Type:");
    Combo type = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);

    typeComboViewer = new ComboViewer(type);

    typeComboViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(final Object inputElement) {
        return (AlignmentSettings[]) inputElement;
      }

      public void dispose() {
      }

      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      }
    });

    typeComboViewer.setInput(AlignmentSettings.types);

    typeComboViewer.setLabelProvider(new LabelProvider() {
      public Image getImage(final Object element) {
        // no images in this combo
        return null;
      }

      public String getText(final Object element) {
        return ((AlignmentSettings) element).getDescription();
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
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    IStructuredSelection selection = (IStructuredSelection) typeComboViewer.getSelection();
    AlignmentSettings aggSettings = (AlignmentSettings) selection.getFirstElement();
    int aggType = aggSettings.getType();
    return new AlignmentSettings(aggType);
  }

  protected void setValue(final Object value) {
    if (value instanceof AlignmentSettings) {
      AlignmentSettings aggSettings = (AlignmentSettings) value;
      typeComboViewer.setSelection(new StructuredSelection(AlignmentSettings.getType(aggSettings.getCode())));
    }
  }

  protected void addModificationListeners() {
    if (null == selectionChangedListener) {
      selectionChangedListener = new PropertyEditorWidgetSelectionChangedListener();
      typeComboViewer.addSelectionChangedListener(selectionChangedListener);
    }
  }

  protected void removeModificationListeners() {
    typeComboViewer.removeSelectionChangedListener(selectionChangedListener);
  }

  protected boolean isValid() {
    return true;
  }

}
