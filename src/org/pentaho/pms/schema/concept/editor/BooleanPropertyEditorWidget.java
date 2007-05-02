package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public class BooleanPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(BooleanPropertyEditorWidget.class);

  private Button button;

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public BooleanPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId) {
    super(parent, style, conceptModel, propertyId);
    setValue(getProperty().getValue());
    if (logger.isDebugEnabled()) {
      logger.debug("created BooleanPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        BooleanPropertyEditorWidget.this.widgetDisposed(e);
      }
    });
    button = new Button(parent, SWT.CHECK);
    button.setText(DefaultPropertyID.findDefaultPropertyID(getPropertyId()).getDescription());

    FormData fdCheck = new FormData();
    fdCheck.left = new FormAttachment(0, 0);
    fdCheck.top = new FormAttachment(0, 0);
    button.setLayoutData(fdCheck);

    button.addFocusListener(new PropertyEditorWidgetFocusListener());

    button.setEnabled(isEditable());
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    return new Boolean(button.getSelection());
  }

  protected void setValue(final Object value) {
    button.setSelection(((Boolean) value).booleanValue());
  }

}
