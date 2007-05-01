package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.concept.editor.AbstractPropertyEditorWidget.PropertyEditorWidgetFocusListener;

public class StringPropertyEditorWidget extends AbstractPropertyEditorWidget {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(StringPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Text string;

  private Label stringLabel;

  // ~ Constructors ====================================================================================================

  public StringPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId) {
    super(parent, style, conceptModel, propertyId);
    setValue(getProperty().getValue());
    if (logger.isDebugEnabled()) {
      logger.debug("created StringPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        StringPropertyEditorWidget.this.widgetDisposed(e);
      }
    });

    stringLabel = new Label(parent, SWT.NONE);
    stringLabel.setText("Value:");

    string = new Text(parent, SWT.BORDER);

    FormData fd1 = new FormData();
    fd1.left = new FormAttachment(0, 0);
    fd1.top = new FormAttachment(string, 0, SWT.CENTER);
    stringLabel.setLayoutData(fd1);

    FormData fd2 = new FormData();
    fd2.left = new FormAttachment(stringLabel, 10);
    fd2.top = new FormAttachment(0, 0);
    fd2.right = new FormAttachment(100, 0);
    string.setLayoutData(fd2);

//    string.addModifyListener(new ModifyListener() {
//      public void modifyText(final ModifyEvent e) {
//        // setting modified now; remember to reset when control loses focus
//        synchronized (StringPropertyEditorWidget.this) {
//
//          setModified(true);
//        }
//      }
//    });

    string.addFocusListener(new PropertyEditorWidgetFocusListener());

    string.setEditable(isEditable());
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    return string.getText();
  }

  protected void setValue(final Object value) {
    string.setText(value.toString());
  }
}
