package org.pentaho.pms.schema.concept.editor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.Const;

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

    urlField = new Text(parent, SWT.BORDER);
    final ControlDecoration controlDecoration = new ControlDecoration(urlField, SWT.TOP | SWT.RIGHT);
    
    final FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    FieldDecoration fieldDecoration = registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
    Image decorationImage = fieldDecoration.getImage();
    controlDecoration.setImage(decorationImage);
    controlDecoration.setDescriptionText(fieldDecoration.getDescription());
    controlDecoration.hide();

    urlLabel = new Label(parent, SWT.LEFT);
    urlLabel.setText("URL (including http://):");

    FormData fdLabel = new FormData();
    fdLabel.left = new FormAttachment(0, 0);
    fdLabel.top = new FormAttachment(urlField, 0, SWT.CENTER);
    urlLabel.setLayoutData(fdLabel);

    FormData fdField = new FormData();
    fdField.left = new FormAttachment(urlLabel, 10);
    fdField.top = new FormAttachment(0, 0);
    fdField.right = new FormAttachment(100, -decorationImage.getBounds().width);
    urlField.setLayoutData(fdField);

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
          controlDecoration.show();
          if (Const.isEmpty(text)) {
        	  controlDecoration.showHoverText(Messages.getString("UrlPropertyEditorWidget.USER_FEEDBACK_MESSAGE_URL_CANT_BE_EMPTY", text));
          } else {
        	  controlDecoration.showHoverText(Messages.getString("UrlPropertyEditorWidget.USER_FEEDBACK_MESSAGE_NOT_A_VALID_URL", text));
          }
          return;
        }
        controlDecoration.hide();
        controlDecoration.hideHover();
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