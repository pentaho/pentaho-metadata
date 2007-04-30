package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public abstract class AbstractPropertyEditorWidget extends Composite implements IPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(StringPropertyEditorWidget.class);

  /**
   * Flag to indicate that the user has been shown the override error message. (We only show this once per widget.)
   */
  private boolean warned;

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  /**
   * The id of the property for which this widget is an editor.
   */
  private String propertyId;

  private CLabel titleLabel;

  // ~ Constructors ====================================================================================================

  public AbstractPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId) {
    super(parent, style);
    this.conceptModel = conceptModel;
    this.propertyId = propertyId;
    createContents();
    // subclasses can ditch this layout if they like
    setLayout(new FormLayout());
  }

  // ~ Methods =========================================================================================================

  /**
   * Subclasses should:
   * <ol>
   * <li>Add a dispose listener.</li>
   * <li>Add a selection or modification listener that sets <code>modified</code> flag to <code>true</code>.</li>
   * <li>Adds itself as a focus listener.</li>
   * <li>Sets itself editable according to <code>isEditable()</code>.</li>
   * <li></li>
   * <li></li>
   * <li></li>
   * </ol>
   */
  protected abstract void createContents();

  protected IConceptModel getConceptModel() {
    return conceptModel;
  }

  protected String getPropertyId() {
    return propertyId;
  }

  /**
   * Creates title label according to this widget's property id. Assumes <code>FormLayout</code>.
   * @return title control
   */
  protected Control createTitleLabel() {
    if (null == titleLabel) {
      Image titleImage = null;

      switch (conceptModel.getPropertyContributor(propertyId)) {
        case IConceptModel.REL_THIS: {
          titleImage = Constants.getImageRegistry(Display.getCurrent()).get("child-property");
          break;
        }
        case IConceptModel.REL_SECURITY: {
          titleImage = Constants.getImageRegistry(Display.getCurrent()).get("security-property");
          break;
        }
        case IConceptModel.REL_PARENT: {
          titleImage = Constants.getImageRegistry(Display.getCurrent()).get("parent-property");
          break;
        }
        case IConceptModel.REL_INHERITED: {
          titleImage = Constants.getImageRegistry(Display.getCurrent()).get("inherited-property");
          break;
        }
        default: {
          titleImage = null;
        }
      }

      titleLabel = new CLabel(this, SWT.NONE);
      titleLabel.setImage(titleImage);

      titleLabel.setText(DefaultPropertyID.findDefaultPropertyID(propertyId).getDescription());
      // Set the background gradient
//      titleLabel.setBackground(
//          new Color[] { Display.getCurrent().getSystemColor(SWT.COLOR_WHITE),
//              Display.getCurrent().getSystemColor(SWT.COLOR_GRAY),
//              Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY) }, new int[] { 33, 100 });
      FormData fdTitle = new FormData();
      fdTitle.top = new FormAttachment(0, 0);
      fdTitle.left = new FormAttachment(0, 0);
      fdTitle.right = new FormAttachment(100, 0);
      titleLabel.setLayoutData(fdTitle);
    }
    return titleLabel;
  }

  protected boolean showOverrideConfirmDialog() {
    return MessageDialog.openConfirm(getShell(), "Confirm",
        "You are attempting to override a property. Are you sure you want to do this?");
  }

  protected boolean isEditable() {
    // is editable if either (it can be overridden and has been) or (didn't come from a parent)
    return conceptModel.canOverride(propertyId) && conceptModel.isOverridden(propertyId)
        || !conceptModel.canOverride(propertyId);
  }

  protected boolean hasWarned() {
    return warned;
  }

  protected void setWarned(final boolean warned) {
    this.warned = warned;
  }

  protected ConceptPropertyInterface getProperty() {
    return conceptModel.getEffectiveProperty(getPropertyId());
  }

  private void putPropertyValue(final String propertyId) {
    getConceptModel().setPropertyValue(propertyId, getValue());
  }

  protected class PropertyEditorWidgetFocusListener implements FocusListener {

    private Object oldValue;

    protected boolean isModified() {
      return !getValue().equals(oldValue);
    }

    protected void resetModified() {
      oldValue = getValue();
    }

    public void focusLost(final FocusEvent e) {
      if (logger.isDebugEnabled()) {
        logger.debug("focus lost on control for property with id \"" + propertyId + "\"");
      }
      if (logger.isDebugEnabled()) {
        logger.debug("control value = " + getValue());
      }
      synchronized (this) {
        if (isModified()) {
          if (logger.isDebugEnabled()) {
            logger.debug("control's value has been modified");
          }
          putPropertyValue(propertyId);
          resetModified();
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug("control's value has not been modified");
          }
        }
      }
    }

    public void focusGained(final FocusEvent e) {
      synchronized (this) {
        resetModified();
      }
      if (logger.isDebugEnabled()) {
        logger.debug("focus gained on control for property with id \"" + propertyId + "\"");
      }
      /*
       * Warn the user if this control is not editable but the user is trying to click (to edit) the value.
       */
      if (!isEditable() && !hasWarned()) {
        setWarned(true);
        boolean override = showOverrideConfirmDialog();
        if (override) {
          if (logger.isDebugEnabled()) {
            logger.debug("user chose to override after viewing override confirm dialog");
          }
          conceptModel.setProperty(DefaultPropertyID.findDefaultPropertyID(propertyId).getDefaultValue());
        }
      }
    }

  }

  protected class PropertyEditorWidgetModifyListener implements ModifyListener {

    public void modifyText(final ModifyEvent e) {
      putPropertyValue(propertyId);
    }

  }

  protected class PropertyEditorWidgetSelectionChangedListener implements ISelectionChangedListener {
    public void selectionChanged(final SelectionChangedEvent e) {
      putPropertyValue(propertyId);
    }
  }
}