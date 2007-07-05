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
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.security.SecurityReference;

public class PropertyWidgetManager2 extends Composite implements ISelectionChangedListener {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyWidgetManager2.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private Layout layout;

  private Map widgets = new HashMap();

  private Composite widgetArea;

  private Map context;

  private ScrolledComposite widgetAreaWrapper;

  private Map groupNameWidgets = new HashMap();
  
  private SecurityReference securityReference;

  // ~ Constructors ====================================================================================================

  public PropertyWidgetManager2(final Composite parent, final int style,
      final IConceptModel conceptModel, final Map context, SecurityReference securityReference) {
    super(parent, style);
    this.context = context;
    this.conceptModel = conceptModel;
    this.securityReference = securityReference;
    conceptModel.addConceptModificationListener(new IConceptModificationListener() {
      public void conceptModified(final ConceptModificationEvent e) {
        if (e instanceof PropertyExistenceModificationEvent) {
          refreshMe();
        }
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
    setLayout(new GridLayout());

    Label title = new Label(this, SWT.NONE);
    title.setText("Settings");
    GridData gridData = new GridData();
    gridData.heightHint = 22;
    title.setLayoutData(gridData);

    widgetAreaWrapper = new ScrolledComposite(this, SWT.V_SCROLL | SWT.BORDER);
    widgetAreaWrapper.setAlwaysShowScrollBars(true);
    widgetAreaWrapper.setExpandHorizontal(true);
    widgetAreaWrapper.setMinWidth(50);
    widgetAreaWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));

    widgetArea = new Composite(widgetAreaWrapper, SWT.NONE);
    layout = new GridLayout(1, true);
    ((GridLayout) layout).verticalSpacing = 20;

    widgetArea.setLayout(layout);

    widgetAreaWrapper.setContent(widgetArea);

    focusWidget(null);

    // resize widgetArea when this control (property widget mgmr) is resized
    addControlListener(new ControlAdapter() {
      public void controlResized(final ControlEvent e) {
        resizeWidgetArea();
      }
    });

    refreshMe();
  }

  protected void refreshMe() {
    // throw out all the widgets
    if (logger.isDebugEnabled()) {
      logger.debug("widgets.keySet()=" + widgets.keySet());
    }
    for (Iterator widgetIter = widgets.keySet().iterator(); widgetIter.hasNext();) {
      IPropertyEditorWidget control = (IPropertyEditorWidget) widgets.get(widgetIter.next());
      control.cleanup();
      ((Control) control).dispose();
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
        if (logger.isDebugEnabled()) {
          logger.debug("creating widget for property with id \"" + propertyId + "\"");
        }
        IPropertyEditorWidget widget = PropertyEditorWidgetFactory.getWidget(prop.getType(), widgetArea, SWT.NONE,
            conceptModel, prop.getId(), context, securityReference);
        if (widget != null) {
          addWidget((Control) widget);
          widgets.put(prop.getId(), widget);
          focusWidget(prop.getId());
        } else {
          logger.error("failed to get widget " + propertyId);
        }
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

  protected void resizeWidgetArea() {
    widgetArea.setSize(widgetAreaWrapper.getClientArea().width, widgetArea.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
  }

  /**
   * Swaps top control to property editor for given property id.
   */
  protected void focusWidget(final String propertyId) {
    if (null != propertyId) {
      widgetAreaWrapper.setOrigin(((Control) widgets.get(propertyId)).getBounds().x,
          ((Control) widgets.get(propertyId)).getBounds().y);
    }
  }

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
