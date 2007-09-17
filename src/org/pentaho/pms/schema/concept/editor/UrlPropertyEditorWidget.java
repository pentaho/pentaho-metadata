package org.pentaho.pms.schema.concept.editor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class UrlPropertyEditorWidget extends AbstractPropertyEditorWidget implements FocusListener {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(UrlPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Text urlField;

  private FocusListener focusListener;

  private Label urlLabel;

  private URL defaultUrl;

  // ~ Constructors ====================================================================================================

  public UrlPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    try {
      defaultUrl = new URL("http:");
    } catch (MalformedURLException e) {
      // should never happen
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
    }
    refresh();
    if (logger.isDebugEnabled()) {
      logger.debug("created UrlPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        UrlPropertyEditorWidget.this.widgetDisposed(e);
      }
    });

    final DecoratedField field = new DecoratedField(parent, SWT.BORDER, new TextControlCreator());
    urlField = (Text) field.getControl();

    final FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    field
        .addFieldDecoration(registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR), SWT.TOP | SWT.RIGHT, false);
    field.hideDecoration(registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR));

    urlLabel = new Label(parent, SWT.NONE);
    urlLabel.setText("URL (including http://):");

    FormData fd2 = new FormData();
    fd2.left = new FormAttachment(urlLabel, 10);
    fd2.top = new FormAttachment(0, 0);
    fd2.right = new FormAttachment(100, 0);
    field.getLayoutControl().setLayoutData(fd2);

    FormData fd1 = new FormData();
    fd1.left = new FormAttachment(0, 0);
    fd1.top = new FormAttachment(urlField, 0, SWT.CENTER);
    urlLabel.setLayoutData(fd1);

    Listener listener = new Listener() {
      public void handleEvent(final Event e) {
        if (logger.isDebugEnabled()) {
          logger.debug("heard event on urlField");
        }
        String text = urlField.getText();
        try {
          new URL(text);
          if (logger.isDebugEnabled()) {
            logger.debug("urlField contains a valid URL (" + text + ")");
          }
        } catch (MalformedURLException mue) {
          if (logger.isDebugEnabled()) {
            logger.debug("urlField contains a invalid URL (" + text + ")");
          }
          field.showDecoration(registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR));
          return;
        }
        field.hideDecoration(registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR));
      }
    };

    urlField.addListener(SWT.MouseDown, listener);
    urlField.addListener(SWT.MouseUp, listener);
    urlField.addListener(SWT.KeyDown, listener);
    urlField.addListener(SWT.KeyUp, listener);

    urlField.addFocusListener(this);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    try {
      return new URL(urlField.getText());
    } catch (MalformedURLException e) {
      urlField.setText(defaultUrl.toString());
      return defaultUrl;
    }
  }

  protected void setValue(final Object value) {
    if (value instanceof URL) {
      urlField.setText(((URL) value).toString());
    }
  }

  protected boolean isValid() {
    try {
      new URL(urlField.getText());
    } catch (MalformedURLException e) {
      return false;
    }
    return true;
  }

  public void focusGained(FocusEvent arg0) {
    // Do nothing

  }

  public void focusLost(FocusEvent arg0) {
    if (!getValue().equals(getProperty().getValue())) {
      putPropertyValue();
    }
  }

  public void refresh() {
    refreshOverrideButton();
    urlField.setEnabled(isEditable());
    urlLabel.setEnabled(isEditable());
    setValue(getProperty().getValue());
  }

  public void cleanup() {
    urlField.removeFocusListener(this);
  }
}