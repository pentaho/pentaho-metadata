package org.pentaho.pms.schema.concept.editor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SecurityPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(SecurityPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public SecurityPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    setValue(getProperty().getValue());
    if (logger.isDebugEnabled()) {
      logger.debug("created SecurityPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================


  protected void addModificationListeners() {
    // TODO Auto-generated method stub

  }

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        SecurityPropertyEditorWidget.this.widgetDisposed(e);
      }
    });

    Label label = new Label(parent, SWT.CENTER);
    label.setText("Not implemented yet.");
    label.setEnabled(isEditable());

    FormData fdLabel = new FormData();
    fdLabel.left = new FormAttachment(0, 0);
    fdLabel.right = new FormAttachment(100, 0);
    fdLabel.top = new FormAttachment(0, 0);
    label.setLayoutData(fdLabel);

  }

  protected void widgetDisposed(final DisposeEvent e) {
    // TODO Auto-generated method stub

  }

  protected boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }

  protected void removeModificationListeners() {
    // TODO Auto-generated method stub
  }

  public Object getValue() {
    // TODO Auto-generated method stub
    return null;
  }

  protected void setValue(final Object value) {
  }
}
