package org.pentaho.pms.schema.concept.editor;

import java.math.BigDecimal;
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

public class NumberPropertyEditorWidget extends AbstractPropertyEditorWidget implements FocusListener{ 

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(NumberPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Text numberField;

  private FocusListener focusListener;

  Label numberLabel;
  
  // ~ Constructors ====================================================================================================

  public NumberPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
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

    // final DecoratedField field = new DecoratedField(parent, SWT.BORDER, new TextControlCreator());
    numberField = new Text(parent, SWT.BORDER);
    final ControlDecoration controlDecoration = new ControlDecoration(numberField, SWT.TOP | SWT.RIGHT);
    
    final FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    FieldDecoration fieldDecoration = registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
    Image decorationImage = fieldDecoration.getImage();
    controlDecoration.setImage(decorationImage);
    controlDecoration.setDescriptionText(fieldDecoration.getDescription());
    controlDecoration.hide();

    numberLabel = new Label(parent, SWT.LEFT);
    numberLabel.setText("Value:");

    FormData fdLabel = new FormData();
    fdLabel.left = new FormAttachment(0, 0);
    fdLabel.top = new FormAttachment(numberField, 0, SWT.CENTER);
    numberLabel.setLayoutData(fdLabel);

    FormData fd1 = new FormData();
    fd1.left = new FormAttachment(0, 0);
    fd1.top = new FormAttachment(0, 0);
    fd1.right= new FormAttachment(100, -decorationImage.getBounds().width);
    numberField.setLayoutData(fd1);

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
          controlDecoration.show();
          if (Const.isEmpty(text)) {
        	  controlDecoration.showHoverText(Messages.getString("NumberPropertyEditorWidget.USER_FEEDBACK_MESSAGE_NUMBER_CANT_BE_EMPTY", text));
          } else {
        	  controlDecoration.showHoverText(Messages.getString("NumberPropertyEditorWidget.USER_FEEDBACK_MESSAGE_NOT_A_BIGNUMBER", text));
          }
          return;
        }
        controlDecoration.hide();
        controlDecoration.hideHover();
      }
    };

    numberField.addListener(SWT.MouseDown, listener);
    numberField.addListener(SWT.MouseUp, listener);
    numberField.addListener(SWT.KeyDown, listener);
    numberField.addListener(SWT.KeyUp, listener);
    
    numberField.addFocusListener(this);
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

  protected boolean isValid() {
    try {
      new BigDecimal(numberField.getText());
    } catch (NumberFormatException e) {
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
    numberField.setEnabled(isEditable());
    numberLabel.setEnabled(isEditable());
    setValue(getProperty().getValue());
  }

  public void cleanup() {
    numberField.removeFocusListener(this);
  }
}