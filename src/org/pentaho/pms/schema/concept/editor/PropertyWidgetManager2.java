package org.pentaho.pms.schema.concept.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;

public class PropertyWidgetManager2 extends Composite implements ISelectionChangedListener {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyWidgetManager2.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private Layout layout;

  private Map widgets = new HashMap();

  private Composite widgetArea;

  //  private Control defaultWidget;

  //  private ToolBar toolBar;

  //  private String topPropertyId;

  //  private ToolItem overrideButton;

  private SchemaMeta schemaMeta;

  private ScrolledComposite widgetAreaWrapper;

  private Map groupNameWidgets = new HashMap();

  // ~ Constructors ====================================================================================================

  public PropertyWidgetManager2(final Composite parent, final int style, final SchemaMeta schemaMeta,
      final IConceptModel conceptModel) {
    super(parent, style);
    this.schemaMeta = schemaMeta;
    this.conceptModel = conceptModel;
    conceptModel.addConceptModificationListener(new IConceptModificationListener() {
      public void conceptModified(final ConceptModificationEvent e) {
        //        // either need to add or delete a property editor widget
        if (e instanceof PropertyExistenceModificationEvent) {
          refreshMe();
        }
        //          PropertyExistenceModificationEvent pe = (PropertyExistenceModificationEvent) e;
        //          if (PropertyExistenceModificationEvent.ADD_PROPERTY == pe.getType()) {
        //            // add property editor widget
        //            addWidget((ConceptPropertyInterface) pe.getNewValue());
        //          } else if (PropertyExistenceModificationEvent.OVERRIDE_PROPERTY == pe.getType()
        //              || PropertyExistenceModificationEvent.INHERIT_PROPERTY == pe.getType()) {
        //            // remove property editor widget
        //            removeWidget((ConceptPropertyInterface) pe.getOldValue());
        //            // add the new one whether its a new child (an override) or a parent/inherited/security becoming visible
        //            //   (an inherit)
        //            addWidget((ConceptPropertyInterface) pe.getNewValue());
        //          } else if (PropertyExistenceModificationEvent.REMOVE_PROPERTY == pe.getType()) {
        //            // remove property editor widget
        //            removeWidget((ConceptPropertyInterface) pe.getOldValue());
        //          }
        //        }
      }
    });
    createContents();
  }

  // ~ Methods =========================================================================================================

  protected void createContents() {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PropertyWidgetManager2.this.widgetDisposed(e);
      }
    });
    setLayout(new FormLayout());

    Label title = new Label(this, SWT.NONE);
    title.setText("Property Editor");
    title.setFont(Constants.getFontRegistry(getDisplay()).get("card-title"));

    //    Label lab7 = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);

    widgetAreaWrapper = new ScrolledComposite(this, SWT.V_SCROLL | SWT.BORDER);
    widgetAreaWrapper.setAlwaysShowScrollBars(true);
    //    widgetAreaWrapper.setLayout(new FillLayout());
    //    Composite widgetAreaWrapper = new Composite(this, SWT.BORDER);
    FormData fd9 = new FormData();
    fd9.top = new FormAttachment(0, 16 + 10 + 6);
    fd9.left = new FormAttachment(0, 0);
    fd9.right = new FormAttachment(100, 0);
    fd9.bottom = new FormAttachment(100, 0);
    widgetAreaWrapper.setLayoutData(fd9);

    widgetArea = new Composite(widgetAreaWrapper, SWT.NONE);

    FormData fd10 = new FormData();
    fd10.bottom = new FormAttachment(widgetAreaWrapper, -10);
    fd10.left = new FormAttachment(0, 0);
    title.setLayoutData(fd10);

    layout = new GridLayout(1, true);
    ((GridLayout) layout).verticalSpacing = 20;
    //    ((RowLayout) layout).justify = true;
    widgetArea.setLayout(layout);

    refreshMe();

    //    FormData fd7 = new FormData();
    //    fd7.top = new FormAttachment(0, 16 + 10 + 10 + 2);
    //    fd7.left = new FormAttachment(0, 0);
    //    fd7.right = new FormAttachment(100, 0);
    //    lab7.setLayoutData(fd7);

    widgetAreaWrapper.setContent(widgetArea);
    //    resizeWidgetArea();

    //    defaultWidget = new DefaultWidget(widgetArea, SWT.NONE);
    focusWidget(null);

    // resize widgetArea when this control (property widget mgmr) is resized
    addControlListener(new ControlAdapter() {
      public void controlResized(final ControlEvent e) {
        resizeWidgetArea();
      }
    });

    ScrollBar scrollBar = widgetAreaWrapper.getVerticalBar();
    if (logger.isDebugEnabled()) {
      logger.debug("thumb: " + scrollBar.getThumb());
      logger.debug("increment: " + scrollBar.getIncrement());
      logger.debug("min: " + scrollBar.getMinimum());
      logger.debug("max: " + scrollBar.getMaximum());
      logger.debug("page increment: " + scrollBar.getPageIncrement());
      logger.debug("selection: " + scrollBar.getSelection());
      logger.debug("style: " + scrollBar.getStyle());
    }

  }

  protected void refreshMe() {
    // throw out all the widgets
    if (logger.isDebugEnabled()) {
      logger.debug("widgets.keySet()=" + widgets.keySet());
    }
    for (Iterator widgetIter = widgets.keySet().iterator(); widgetIter.hasNext();) {
      Control control = (Control) widgets.get(widgetIter.next());
      control.dispose();
    }
    widgets.clear();
    // throw out all the group name widgets
    if (logger.isDebugEnabled()) {
      logger.debug("groupNameWidgets.keySet()=" + groupNameWidgets.keySet());
    }
    for (Iterator groupNameWidgetIter = groupNameWidgets.keySet().iterator(); groupNameWidgetIter.hasNext();) {
      Control control = (Control) groupNameWidgets.get(groupNameWidgetIter.next());
      control.dispose();
    }
    groupNameWidgets.clear();
    // put all the widgets back
    List usedGroups = PropertyGroupHelper.getUsedGroups(conceptModel);
    for (Iterator groupIter = usedGroups.iterator(); groupIter.hasNext();) {
      String groupName = (String) groupIter.next();
      Control groupNameWidget = new GroupNameWidget(widgetArea, SWT.NONE, groupName);
      addWidget(groupNameWidget);
      groupNameWidgets.put(groupName, groupNameWidget);
      List usedPropertiesForGroup = PropertyGroupHelper.getUsedPropertiesForGroup(groupName, conceptModel);
      for (Iterator propIter = usedPropertiesForGroup.iterator(); propIter.hasNext();) {
        String propertyId = (String) propIter.next();
        ConceptPropertyInterface prop = (ConceptPropertyInterface) conceptModel.getEffectiveProperty(propertyId);
        // add widget
        IPropertyEditorWidget widget = PropertyEditorWidgetFactory.getWidget(schemaMeta, prop.getType(), widgetArea,
            SWT.NONE, conceptModel, prop.getId());
        if (logger.isDebugEnabled()) {
          logger.debug("adding widget for property with id \"" + propertyId + "\"");
        }
        addWidget((Control) widget);
        widgets.put(prop.getId(), widget);
        focusWidget(prop.getId());
        resizeWidgetArea();
      }
    }
  }

  protected void addWidget(final Control widget) {
    GridData gdWidget = new GridData();
    gdWidget.grabExcessHorizontalSpace = true;
    gdWidget.horizontalAlignment = GridData.FILL;
    widget.setLayoutData(gdWidget);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  //  protected void addToolBar(final String propertyId) {
  //    if (null != toolBar) {
  //      toolBar.dispose();
  //    }
  //    if (null != propertyId) {
  //      toolBar = new ToolBar(this, SWT.FLAT);
  //
  //      FormData fdToolBar = new FormData();
  //      fdToolBar.top = new FormAttachment(0, 0);
  //      fdToolBar.right = new FormAttachment(100, 0);
  //      toolBar.setLayoutData(fdToolBar);
  //
  //      // override button
  //      if (conceptModel.canOverride(propertyId)) {
  //        overrideButton = new ToolItem(toolBar, SWT.CHECK);
  ////        overrideButton.setText("OVR");
  //        overrideButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("override-button"));
  //        overrideButton.setToolTipText("Override");
  //        if (conceptModel.isOverridden(propertyId)) {
  //          overrideButton.setSelection(true);
  //        }
  //        overrideButton.addSelectionListener(new SelectionAdapter() {
  //          public void widgetSelected(final SelectionEvent e) {
  //            overridePressed();
  //          }
  //        });
  //      }
  //    }
  //    // removed and possibly added new toolbar; need to layout
  //    layout();
  //  }

  //  protected void overridePressed() {
  //    /*
  //     * Remember: override button is a button with toggle behavior.
  //     * If the top property id is an existing child property, prompt to delete property and delete it if true,
  //     * else add the child property. Concept model will fire events accordingly.
  //     */
  //    if (null != conceptModel.getProperty(topPropertyId, IConceptModel.REL_THIS)) {
  //      boolean delete = MessageDialog.openConfirm(getShell(), "Confirm",
  //          "Are you sure you want to stop overriding the property '"
  //              + DefaultPropertyID.findDefaultPropertyID(topPropertyId).getDescription() + "'?");
  //      if (delete) {
  //        conceptModel.removeProperty(topPropertyId);
  //        // no need to update override button selection status; concept mod event will do that
  //      } else {
  //        // user canceled; set override button back to "pressed" status
  //        overrideButton.setSelection(true);
  //      }
  //    } else {
  //      conceptModel.setProperty(DefaultPropertyID.findDefaultPropertyID(topPropertyId).getDefaultValue());
  //      overrideButton.setSelection(true);
  //    }
  //  }

  public void selectionChanged(final SelectionChangedEvent e) {
    // TODO mlowery this class should not be coupled to property "tree" selection;
    // TODO mlowery maybe just property selection
    if (null != e && e.getSelection() instanceof PropertyTreeSelection) {
      PropertyTreeSelection sel = (PropertyTreeSelection) e.getSelection();
      if (!sel.isGroup()) {
        focusWidget(sel.getName());
      } else {
        focusWidget(null);
      }
    }
  }

  //  /**
  //   * Should only be called from refreshMe
  //   */
  //  private void addWidget(final ConceptPropertyInterface prop) {
  //    IPropertyEditorWidget widget = PropertyEditorWidgetFactory.getWidget(schemaMeta, prop.getType(), widgetArea,
  //        SWT.NONE, conceptModel, prop.getId());
  //    GridData gdWidget = new GridData();
  //    gdWidget.grabExcessHorizontalSpace = true;
  //    gdWidget.horizontalAlignment = GridData.FILL;
  //    ((Control) widget).setLayoutData(gdWidget);
  //    widgets.put(prop.getId(), widget);
  //    focusWidget(prop.getId());
  //    resizeWidgetArea();
  //  }
  //
  //  /**
  //   * Should only be called from refreshMe
  //   */
  //  private void removeWidget(final ConceptPropertyInterface prop) {
  //    IPropertyEditorWidget widget = (IPropertyEditorWidget) widgets.get(prop.getId());
  //    widgets.remove(prop.getId());
  //    ((Control) widget).dispose();
  //    focusWidget(null);
  //    resizeWidgetArea();
  //  }

  protected void resizeWidgetArea() {
    widgetArea.setSize(widgetAreaWrapper.getClientArea().width, widgetArea.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
  }

  /**
   * Swaps top control to property editor for given property id.
   */
  protected void focusWidget(final String propertyId) {
    if (null != propertyId) {
      //      ScrollBar scrollBar = widgetAreaWrapper.getVerticalBar();
      //      int controlTopY = ((Control) widgets.get(propertyId)).getBounds().y;
      //      if (logger.isDebugEnabled()) {
      //        logger.debug("controlTopY: " + controlTopY);
      //      }
      //      int totalWidgetAreaHeight = widgetArea.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
      //      if (logger.isDebugEnabled()) {
      //        logger.debug("widget area height: " + totalWidgetAreaHeight);
      //        logger.debug("calc: " + (float) controlTopY * 100 / totalWidgetAreaHeight);
      //      }
      //      int scrollBarSelection = Math.round((float) controlTopY * 100 / totalWidgetAreaHeight);
      //      if (logger.isDebugEnabled()) {
      //        logger.debug("scrollbar selection: " + scrollBarSelection);
      //      }
      widgetAreaWrapper.setOrigin(((Control) widgets.get(propertyId)).getBounds().x,
          ((Control) widgets.get(propertyId)).getBounds().y);
      //      if (logger.isDebugEnabled()) {
      //        logger.debug("thumb: " + scrollBar.getThumb());
      //        logger.debug("increment: " + scrollBar.getIncrement());
      //        logger.debug("min: " + scrollBar.getMinimum());
      //        logger.debug("max: " + scrollBar.getMaximum());
      //        logger.debug("page increment: " + scrollBar.getPageIncrement());
      //        logger.debug("selection: " + scrollBar.getSelection());
      //      }

      //      addToolBar(propertyId);
      //      ((Control) widgets.get(propertyId)).setFocus();
      //      topPropertyId = propertyId;
    } else {
      //      addToolBar(null);
      //      layout.topControl = defaultWidget;
      //      topPropertyId = null;
    }
    // needed to show repaint current card
    //    widgetArea.layout();
    //    widgetAreaWrapper.layout();
  }

  //  private class DefaultWidget extends Composite {
  //
  //    public DefaultWidget(final Composite parent, final int style) {
  //      super(parent, style);
  //      createContents();
  //    }
  //
  //    protected void createContents() {
  //      setLayout(new FormLayout());
  //      setLayout(new FormLayout());
  //      Label lab11 = new Label(this, SWT.WRAP | SWT.CENTER);
  //      lab11.setText("Select a property to begin editing that property. Your changes will be saved automatically when "
  //          + "you switch to a new property.");
  //      FormData fd11 = new FormData();
  //      fd11.left = new FormAttachment(0, 15);
  //      fd11.top = new FormAttachment(40, 0);
  //      fd11.right = new FormAttachment(100, -15);
  //      lab11.setLayoutData(fd11);
  //    }
  //
  //  }

  protected class GroupNameWidget extends Composite {
    private String groupName;

    public GroupNameWidget(final Composite parent, final int style, final String groupName) {
      super(parent, style);
      this.groupName = groupName;
      createContents();
    }

    protected void createContents() {
      setLayout(new FormLayout());
      Label nameLabel = new Label(this, SWT.NONE);
      nameLabel.setText(groupName);
      nameLabel.setFont(Constants.getFontRegistry(getDisplay()).get("group-name"));
      FormData fdNameLabel = new FormData();
      fdNameLabel.top = new FormAttachment(0, 0);
      fdNameLabel.left = new FormAttachment(0, 0);
      nameLabel.setLayoutData(fdNameLabel);
    }

  }
}
