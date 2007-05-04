package org.pentaho.pms.schema.concept.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public class PropertyWidgetManager extends Composite implements ISelectionChangedListener {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyWidgetManager.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private StackLayout stackLayout;

  private Map widgets = new HashMap();

  private Composite stack;

  private Control defaultWidget;

  private ToolBar toolBar;

  private String topPropertyId;

  private ToolItem overrideButton;

  private SchemaMeta schemaMeta;

  // ~ Constructors ====================================================================================================

  public PropertyWidgetManager(final Composite parent, final int style, final SchemaMeta schemaMeta,
      final IConceptModel conceptModel) {
    super(parent, style);
    this.schemaMeta = schemaMeta;
    this.conceptModel = conceptModel;
    conceptModel.addConceptModificationListener(new IConceptModificationListener() {
      public void conceptModified(final ConceptModificationEvent e) {
        // either need to add or delete a property editor widget
        if (e instanceof PropertyExistenceModificationEvent) {
          PropertyExistenceModificationEvent pe = (PropertyExistenceModificationEvent) e;
          if (PropertyExistenceModificationEvent.ADD_PROPERTY == pe.getType()) {
            // add property editor widget
            addWidget((ConceptPropertyInterface) pe.getNewValue());
          } else if (PropertyExistenceModificationEvent.OVERRIDE_PROPERTY == pe.getType()
              || PropertyExistenceModificationEvent.INHERIT_PROPERTY == pe.getType()) {
            // remove property editor widget
            removeWidget((ConceptPropertyInterface) pe.getOldValue());
            // add the new one whether its a new child (an override) or a parent/inherited/security becoming visible
            //   (an inherit)
            addWidget((ConceptPropertyInterface) pe.getNewValue());
          } else if (PropertyExistenceModificationEvent.REMOVE_PROPERTY == pe.getType()) {
            // remove property editor widget
            removeWidget((ConceptPropertyInterface) pe.getOldValue());
          }
        }
      }
    });
    createContents();
  }

  // ~ Methods =========================================================================================================

  protected void createContents() {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PropertyWidgetManager.this.widgetDisposed(e);
      }
    });
    setLayout(new FormLayout());

    Label title = new Label(this, SWT.NONE);
    title.setText("Property Editor");
    title.setFont(Constants.getFontRegistry(getDisplay()).get("card-title"));

    Label lab7 = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);

    stack = new Composite(this, SWT.NONE);
    FormData fd9 = new FormData();
    fd9.top = new FormAttachment(lab7, 10);
    fd9.left = new FormAttachment(0, 0);
    fd9.right = new FormAttachment(100, 0);
    fd9.bottom = new FormAttachment(100, 0);
    stack.setLayoutData(fd9);

    FormData fd10 = new FormData();
    fd10.bottom = new FormAttachment(lab7, -10);
    fd10.left = new FormAttachment(0, 0);
    title.setLayoutData(fd10);

    stackLayout = new StackLayout();
    stack.setLayout(stackLayout);

    Map propertyMap = conceptModel.getEffectivePropertyMap();

    Collection properties = propertyMap.values();
    for (Iterator iter = properties.iterator(); iter.hasNext();) {
      ConceptPropertyInterface prop = (ConceptPropertyInterface) iter.next();
      addWidget(prop);
    }

    FormData fd7 = new FormData();
    fd7.top = new FormAttachment(0, 16 + 10 + 10 + 2);
    fd7.left = new FormAttachment(0, 0);
    fd7.right = new FormAttachment(100, 0);
    lab7.setLayoutData(fd7);

    defaultWidget = new DefaultWidget(stack, SWT.NONE);
    swapWidget(null);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  protected void addToolBar(final String propertyId) {
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
        overrideButton = new ToolItem(toolBar, SWT.CHECK);
        //        overrideButton.setText("OVR");
        overrideButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("override-button"));
        overrideButton.setToolTipText("Override");
        if (conceptModel.isOverridden(propertyId)) {
          overrideButton.setSelection(true);
        }
        overrideButton.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(final SelectionEvent e) {
            overridePressed();
          }
        });
      }
    }
    // removed and possibly added new toolbar; need to layout
    layout();
  }

  protected void overridePressed() {
    /*
     * Remember: override button is a button with toggle behavior.
     * If the top property id is an existing child property, prompt to delete property and delete it if true,
     * else add the child property. Concept model will fire events accordingly.
     */
    if (null != conceptModel.getProperty(topPropertyId, IConceptModel.REL_THIS)) {
      boolean delete = MessageDialog.openConfirm(getShell(), "Confirm",
          "Are you sure you want to stop overriding the property '"
              + DefaultPropertyID.findDefaultPropertyID(topPropertyId).getDescription() + "'?");
      if (delete) {
        conceptModel.removeProperty(topPropertyId);
        // no need to update override button selection status; concept mod event will do that
      } else {
        // user canceled; set override button back to "pressed" status
        overrideButton.setSelection(true);
      }
    } else {
      conceptModel.setProperty(DefaultPropertyID.findDefaultPropertyID(topPropertyId).getDefaultValue());
      overrideButton.setSelection(true);
    }
  }

  public void selectionChanged(final SelectionChangedEvent e) {
    // TODO mlowery this class should not be coupled to property "tree" selection;
    // TODO mlowery maybe just property selection
    if (null != e && e.getSelection() instanceof PropertyTreeSelection) {
      PropertyTreeSelection sel = (PropertyTreeSelection) e.getSelection();
      if (!sel.isGroup()) {
        swapWidget(sel.getName());
      } else {
        swapWidget(null);
      }
    }
  }

  protected void addWidget(final ConceptPropertyInterface prop) {
    IPropertyEditorWidget widget = PropertyEditorWidgetFactory.getWidget(prop.getType(), stack, SWT.NONE,
        conceptModel, prop.getId(), Collections.EMPTY_MAP);
    widgets.put(prop.getId(), widget);
    swapWidget(prop.getId());
  }

  protected void removeWidget(final ConceptPropertyInterface prop) {
    IPropertyEditorWidget widget = (IPropertyEditorWidget) widgets.get(prop.getId());
    widgets.remove(prop.getId());
    ((Control) widget).dispose();
    swapWidget(null);
  }

  /**
   * Swaps top control to property editor for given property id.
   */
  protected void swapWidget(final String propertyId) {
    if (null != propertyId) {
      addToolBar(propertyId);
      stackLayout.topControl = (Control) widgets.get(propertyId);
      topPropertyId = propertyId;
    } else {
      addToolBar(null);
      stackLayout.topControl = defaultWidget;
      topPropertyId = null;
    }
    // needed to show repaint current card
    stack.layout();
  }

  private class DefaultWidget extends Composite {

    public DefaultWidget(final Composite parent, final int style) {
      super(parent, style);
      createContents();
    }

    protected void createContents() {
      setLayout(new FormLayout());
      setLayout(new FormLayout());
      Label lab11 = new Label(this, SWT.WRAP | SWT.CENTER);
      lab11.setText("Select a property to begin editing that property. Your changes will be saved automatically when "
          + "you switch to a new property.");
      FormData fd11 = new FormData();
      fd11.left = new FormAttachment(0, 15);
      fd11.top = new FormAttachment(40, 0);
      fd11.right = new FormAttachment(100, -15);
      lab11.setLayoutData(fd11);
    }

  }
}
