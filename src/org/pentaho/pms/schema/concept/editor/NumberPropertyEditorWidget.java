package org.pentaho.pms.schema.concept.editor;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class NumberPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(NumberPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Text numberField;

  private FocusListener focusListener;

  // ~ Constructors ====================================================================================================

  public NumberPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    setValue(getProperty().getValue());
    if (logger.isDebugEnabled()) {
      logger.debug("created NumberPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        NumberPropertyEditorWidget.this.widgetDisposed(e);
      }
    });

    final DecoratedField field = new DecoratedField(parent, SWT.BORDER, new TextControlCreator());
    numberField = (Text) field.getControl();
    numberField.setEnabled(isEditable());

    final FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    field
        .addFieldDecoration(registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR), SWT.TOP | SWT.RIGHT, false);
    field.hideDecoration(registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR));

    Label numberLabel = new Label(parent, SWT.NONE);
    numberLabel.setText("Value:");
    numberLabel.setEnabled(isEditable());

    FormData fd2 = new FormData();
    fd2.left = new FormAttachment(numberLabel, 10);
    fd2.top = new FormAttachment(0, 0);
    fd2.right = new FormAttachment(100, 0);
    field.getLayoutControl().setLayoutData(fd2);

    FormData fd1 = new FormData();
    fd1.left = new FormAttachment(0, 0);
    fd1.top = new FormAttachment(numberField, 0, SWT.CENTER);
    numberLabel.setLayoutData(fd1);

    Listener listener = new Listener() {
      public void handleEvent(final Event e) {
        if (logger.isDebugEnabled()) {
          logger.debug("heard event on numberField");
        }
        String text = numberField.getText();
        try {
          new BigDecimal(text);
          if (logger.isDebugEnabled()) {
            logger.debug("numberField contains a valid BigDecimal (" + text + ")");
          }
        } catch (NumberFormatException nfe) {
          if (logger.isDebugEnabled()) {
            logger.debug("numberField contains a invalid BigDecimal (" + text + ")");
          }
          field.showDecoration(registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR));
          return;
        }
        field.hideDecoration(registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR));
      }
    };

    numberField.addListener(SWT.MouseDown, listener);
    numberField.addListener(SWT.MouseUp, listener);
    numberField.addListener(SWT.KeyDown, listener);
    numberField.addListener(SWT.KeyUp, listener);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    try {
      return new BigDecimal(numberField.getText());
    } catch (NumberFormatException e) {
      return new BigDecimal(0);
    }
  }

  protected void setValue(final Object value) {
    if (value instanceof BigDecimal) {
      numberField.setText(((BigDecimal) value).toString());
    }
  }

  protected void addModificationListeners() {
    if (null == focusListener) {
      focusListener = new PropertyEditorWidgetFocusListener();
      numberField.addFocusListener(focusListener);
    }
  }

  protected void removeModificationListeners() {
    numberField.removeFocusListener(focusListener);
  }

  protected boolean isValid() {
    try {
      new BigDecimal(numberField.getText());
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
}