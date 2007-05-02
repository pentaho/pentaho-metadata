package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
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

  private Control topControl;

  private ToolBar toolBar;

  private ToolItem overrideButton;

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

  protected final void createContents() {
    addToolBar();
    addTitleLabel();
    Composite parent = new Composite(this, SWT.NONE);
    parent.setLayout(new FormLayout());
    FormData fdParent = new FormData();
    fdParent.left = new FormAttachment(0, 0);
    fdParent.right = new FormAttachment(100, 0);
    fdParent.bottom = new FormAttachment(100, 0);
    fdParent.top = new FormAttachment(topControl, 10);
    parent.setLayoutData(fdParent);
    createContents(parent);
  }

  /**
   * Subclasses should:
   * <ol>
   * <li>Add a dispose listener.</li>
   * <li>Adds itself as a focus listener.</li>
   * <li>Sets itself editable according to <code>isEditable()</code>.</li>
   * <li></li>
   * <li></li>
   * <li></li>
   * </ol>
   */
  protected abstract void createContents(final Composite parent);

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
  protected void addTitleLabel() {
    Label titleLabel = new Label(this, SWT.NONE);
    titleLabel.setText(DefaultPropertyID.findDefaultPropertyID(propertyId).getDescription());
    Label sep = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
    topControl = sep;


    FormData fdTitle = new FormData();
    FormData fdSep = new FormData();
    fdSep.top = new FormAttachment(0, 28);
    fdSep.left = new FormAttachment(0, 0);
    fdSep.right = new FormAttachment(100, 0);
    fdTitle.left = new FormAttachment(0, 0);
    fdTitle.bottom = new FormAttachment(sep, 0);

    //    if (null != toolBar) {
//
//    } else {
//      fdTitle.top = new FormAttachment(0, 0);
////      fdSep.top = new FormAttachment(titleLabel, 10);
//    }


    titleLabel.setLayoutData(fdTitle);

    sep.setLayoutData(fdSep);

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

  protected void putPropertyValue() {
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
          putPropertyValue();
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
//      if (!isEditable() && !hasWarned()) {
//        setWarned(true);
//        boolean override = showOverrideConfirmDialog();
//        if (override) {
//          if (logger.isDebugEnabled()) {
//            logger.debug("user chose to override after viewing override confirm dialog");
//          }
//          conceptModel.setProperty(DefaultPropertyID.findDefaultPropertyID(propertyId).getDefaultValue());
//        }
//      }
    }

  }

  protected class PropertyEditorWidgetModifyListener implements ModifyListener {

    public void modifyText(final ModifyEvent e) {
      putPropertyValue();
    }

  }

  protected class PropertyEditorWidgetSelectionChangedListener implements ISelectionChangedListener {
    public void selectionChanged(final SelectionChangedEvent e) {
      putPropertyValue();
    }
  }

  protected void addToolBar() {
    if (null != toolBar) {
      toolBar.dispose();
    }
    if (null != propertyId) {
      toolBar = new ToolBar(this, SWT.FLAT);

      FormData fdToolBar = new FormData();
//      fdToolBar.bottom = new FormAttachment(topControl, -10);
      fdToolBar.top = new FormAttachment(0, 0);
      fdToolBar.right = new FormAttachment(100, 0);
      //      fdToolBar.bottom = new FormAttachment(topControl, 0);
      toolBar.setLayoutData(fdToolBar);

      // override button
      if (conceptModel.canOverride(propertyId)) {
        overrideButton = new ToolItem(toolBar, SWT.PUSH);
        if (conceptModel.isOverridden(propertyId)) {
          overrideButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("stop-override-button"));
          overrideButton.setToolTipText("Stop Override");
        } else {
          overrideButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("override-button"));
          overrideButton.setToolTipText("Override");
        }
        overrideButton.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(final SelectionEvent e) {
            overridePressed();
          }
        });
      }
    }
  }

  protected void overridePressed() {
    /*
     * Remember: override button is a button with toggle behavior.
     * If the top property id is an existing child property, prompt to delete property and delete it if true,
     * else add the child property. Concept model will fire events accordingly.
     */
    if (null != conceptModel.getProperty(propertyId, IConceptModel.REL_THIS)) {
      boolean delete = MessageDialog.openConfirm(getShell(), "Confirm",
          "Are you sure you want to stop overriding the property '"
              + DefaultPropertyID.findDefaultPropertyID(propertyId).getDescription() + "'?");
      if (delete) {
        conceptModel.removeProperty(propertyId);
        // no need to update override button selection status; concept mod event will do that
      } else {
        // user canceled; set override button back to "pressed" status
        //        overrideButton.setSelection(true);
      }
    } else {
      conceptModel.setProperty(DefaultPropertyID.findDefaultPropertyID(propertyId).getDefaultValue());
      overrideButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("stop-override-button"));
      overrideButton.setToolTipText("Stop Override");
      //      overrideButton.setSelection(true);
    }
  }

}