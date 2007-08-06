package org.pentaho.pms.schema.concept.editor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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
   * If this widget needs "outside" information, for example a list of locales, then this information will be available
   * via this map.
   */
  private Map context;

  /**
   * The id of the property for which this widget is an editor.
   */
  private String propertyId;

  private Control topControl;

  private ToolBar toolBar;

  private ToolItem overrideButton;

  // ~ Constructors ====================================================================================================

  public AbstractPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style);
    this.conceptModel = conceptModel;
    this.propertyId = propertyId;
    this.context = context;
    createContents();
    // subclasses can ditch this layout if they like
    setLayout(new FormLayout());
  }

  // ~ Methods =========================================================================================================

  protected final void createContents() {
    createToolBar();
    createTitleLabel();
    createContents(createWidgetArea());
    //    addDisposeListener(new DisposeListener() {
    //      public void widgetDisposed(final DisposeEvent e) {
    //        removeModificationListeners();
    //      }
    //    });
  }

  protected final Composite createWidgetArea() {
    Composite parent = new Composite(this, SWT.NONE);
    parent.setLayout(new FormLayout());
    FormData fdParent = new FormData();
    fdParent.left = new FormAttachment(0, 0);
    fdParent.right = new FormAttachment(100, 0);
    fdParent.bottom = new FormAttachment(100, 0);
    fdParent.top = new FormAttachment(topControl, 10);
    parent.setLayoutData(fdParent);
    return parent;
  }


  /**
   * Returns whether or not the value encapsulated by this widget is valid so that it can be saved.
   */
  protected abstract boolean isValid();

  /**
   * Subclasses should:
   * <ol>
   * <li>Add a dispose listener.</li>
   * <li>Adds itself as a focus/modify/selectionChanged listener or some other way to track modification.</li>
   * <li>Sets itself editable according to <code>isEditable()</code>.</li>
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
  protected final void createTitleLabel() {
    Label titleLabel = new Label(this, SWT.NONE);
    titleLabel.setText(PredefinedVsCustomPropertyHelper.getDescription(propertyId));
    Label sep = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
    topControl = sep;

    FormData fdTitle = new FormData();
    FormData fdSep = new FormData();
    fdSep.top = new FormAttachment(0, 28);
    fdSep.left = new FormAttachment(0, 0);
    fdSep.right = new FormAttachment(100, 0);
    fdTitle.left = new FormAttachment(0, 0);
    fdTitle.bottom = new FormAttachment(sep, 0);

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

  protected synchronized void putPropertyValue() {
    if (isValid()) {
      if (logger.isDebugEnabled()) {
        logger.debug("writing to the concept model");
      }
      getConceptModel().setPropertyValue(propertyId, getValue());
    }
  }

  protected final void createToolBar() {
    if (null != toolBar) {
      toolBar.dispose();
    }
    if (null != propertyId) {
      toolBar = new ToolBar(this, SWT.FLAT);

      FormData fdToolBar = new FormData();
      fdToolBar.top = new FormAttachment(0, 0);
      fdToolBar.right = new FormAttachment(100, 0);
      toolBar.setLayoutData(fdToolBar);

      // override button
      if (conceptModel.canOverride(propertyId)) {
        overrideButton = new ToolItem(toolBar, SWT.PUSH);
        overrideButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("stop-override-button"));
        overrideButton.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(final SelectionEvent e) {
            overridePressed();
          }
        });
      }
    }
  }

  protected void overridePressed() {
    if (null != conceptModel.getProperty(propertyId, IConceptModel.REL_THIS)) {
      boolean delete = MessageDialog.openConfirm(getShell(), "Confirm",
          "Are you sure you want to stop overriding the property '"
              + PredefinedVsCustomPropertyHelper.getDescription(propertyId) + "'?");
      if (delete) {
        // no need to update override button selection status; concept mod event will do that
        conceptModel.removeProperty(propertyId);
      }
    } else {
      ConceptPropertyInterface effectiveProperty = conceptModel.getEffectiveProperty(propertyId);
      try {
        conceptModel.setProperty((ConceptPropertyInterface)PredefinedVsCustomPropertyHelper.createEmptyProperty(propertyId, effectiveProperty
            .getType()).clone());
      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    refresh();
  }

  protected Map getContext() {
    return context;
  }

  protected void refreshOverrideButton() {
    if (overrideButton != null) {
      if (conceptModel.isOverridden(propertyId)) {
        overrideButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("stop-override-button"));
        overrideButton.setToolTipText("Stop Override");
      } else {
        overrideButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("override-button"));
        overrideButton.setToolTipText("Override");
      }
    }
  }
  
  public abstract void cleanup();

  public abstract void refresh();
}