package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public class PropertyTreeWidget extends Composite implements ISelectionProvider {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyTreeWidget.class);

  /**
   * Flag to show all properties.
   */
  public static final int SHOW_ALL = 0;

  /**
   * Flag to show only used properties.
   */
  public static final int SHOW_USED = 1;

  /**
   * Flag to show only unused properties.
   */
  public static final int SHOW_UNUSED = -1;

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private EventSupport eventSupport = new EventSupport();

  private int visibility = SHOW_ALL;

  private TreeViewer treeViewer;

  private boolean decorate = true;

  // ~ Constructors ====================================================================================================

  /**
   * Shows all properties in a tree along with group names. Can get selected property via <code>getSelection()</code>.
   */
  public PropertyTreeWidget(final Composite parent, final int style) {
    this(parent, style, null, SHOW_ALL, false);
  }

  /**
   * Shows only the properties defined in the given concept model. Refreshes itself in reaction to concept model
   * changes.
   */
  public PropertyTreeWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final int visibility, final boolean decorate) {
    super(parent, style);
    this.visibility = visibility;
    this.conceptModel = conceptModel;
    this.decorate = decorate;
    createContents();
  }

  // ~ Methods =========================================================================================================

  /**
   * Returns whether or not this property tree is listening to a concept model or not. If it is listening to a concept
   * model, it only shows properties defined on the concept model. If it is not listening to a concept model, it shows
   * all properties available.
   */
  protected boolean showOnlyRelevantProperties() {
    return null != conceptModel;
  }

  protected void createContents() {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PropertyTreeWidget.this.widgetDisposed(e);
      }
    });
    setLayout(new FillLayout());
    Tree tree2 = new Tree(this, SWT.SINGLE | SWT.BORDER); // single selection at a time
    treeViewer = new TreeViewer(tree2);
    ITreeContentProvider contentProvider = null;
    if (showOnlyRelevantProperties()) {
      if (SHOW_USED == visibility) {
        contentProvider = new UsedPropertiesContentProvider();
      } else {
        contentProvider = new UnusedPropertiesContentProvider();
      }
    } else {
      contentProvider = new AllPropertiesContentProvider();
    }
    treeViewer.setContentProvider(contentProvider);
    treeViewer.setLabelProvider(new PropertyTreeLabelProvider());
    treeViewer.setInput("ignored");

    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(final SelectionChangedEvent e) {
        // propagate the event, but first create a high-level event from the low-level event

        TreeSelection treeSelection = (TreeSelection) e.getSelection();
        ISelection highLevelSelection = transformTreeSelection(treeSelection);

        if (null == highLevelSelection) {
          if (logger.isWarnEnabled()) {
            logger.warn("dropped event since it is an unknown type: " + treeSelection.getClass().getName());
          }
          return;
        }
        fireSelectionChangedEvent(new SelectionChangedEvent(PropertyTreeWidget.this, highLevelSelection));
      }
    });
    treeViewer.expandAll();
  }

  /**
   * Transforms low-level tree event (i.e. <code>TreeSelection</code>) into high-level property tree event (i.e.
   * <code>PropertyTreeSelection</code>).
   * @param sel selection to transform
   * @return transformed selection or <code>null</code> if unknown selection type
   */
  protected PropertyTreeSelection transformTreeSelection(final TreeSelection sel) {
    Object objectSelected = sel.getFirstElement();
    if (objectSelected instanceof GroupNode) {
      GroupNode n = (GroupNode) objectSelected;
      return new PropertyTreeSelection(n.getGroupName(), true);
    } else if (objectSelected instanceof PropertyNode) {
      PropertyNode n = (PropertyNode) objectSelected;
      return new PropertyTreeSelection(n.getId(), false);
    } else {
      return null;
    }
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  /**
   * Abstract parent of two child node types: <code>PropertyNode</code> and <code>GroupNode</code>.
   * @author mlowery
   */
  private abstract class PropertyTreeNode {
  }

  /**
   * Group nodes contain property nodes.
   * @author mlowery
   */
  private class GroupNode extends PropertyTreeNode {
    private String groupName;

    public GroupNode(final String groupName) {
      this.groupName = groupName;
    }

    public String getGroupName() {
      return groupName;
    }
  }

  /**
   * Property nodes are leaf nodes.
   * @author mlowery
   */
  private class PropertyNode extends PropertyTreeNode {
    private String id;

    public PropertyNode(final String id) {
      super();
      this.id = id;
    }

    public boolean canHaveChildren() {
      return false;
    }

    public String getId() {
      return id;
    }
  }

  private abstract class AbstractPropertyTreeContentProvider implements ITreeContentProvider {
    protected final Object[] EMPTY_ARRAY = new Object[0];

    private TreeViewer viewer;

    protected TreeViewer getViewer() {
      return viewer;
    }

    public Object getParent(final Object element) {
      if (element instanceof PropertyNode) {
        // a property element
        PropertyNode n = (PropertyNode) element;
        return new GroupNode(PropertyGroupHelper.getGroupForProperty(n.getId()));
      } else {
        return null;
      }
    }

    public boolean hasChildren(final Object element) {
      if (element instanceof GroupNode) {
        return true;
      } else {
        return false;
      }
    }

    public void dispose() {
      // nothing to dispose
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      this.viewer = (TreeViewer) viewer;
      // no need to adjust listeners
    }

    protected PropertyTreeNode[] makeTreeNodesFromGroupNames(final Object[] groupNames) {
      List propertyTreeNodes = new ArrayList();
      for (int i = 0; i < groupNames.length; i++) {
        propertyTreeNodes.add(new GroupNode((String) groupNames[i]));
      }
      return (PropertyTreeNode[]) propertyTreeNodes.toArray(new PropertyTreeNode[0]);
    }

    protected PropertyTreeNode[] makeTreeNodesFromPropertyIds(final Object[] propertyIds) {
      List propertyTreeNodes = new ArrayList();
      for (int i = 0; i < propertyIds.length; i++) {
        propertyTreeNodes.add(new PropertyNode((String) propertyIds[i]));
      }
      return (PropertyTreeNode[]) propertyTreeNodes.toArray(new PropertyTreeNode[0]);
    }
  }

  private class AllPropertiesContentProvider extends AbstractPropertyTreeContentProvider {

    public Object[] getChildren(final Object parentElement) {
      if (parentElement instanceof GroupNode) {
        // a group node
        GroupNode n = (GroupNode) parentElement;
        return this.makeTreeNodesFromPropertyIds(PropertyGroupHelper.getPropertiesForGroup(n.getGroupName()).toArray());
      } else {
        // a property node
        return EMPTY_ARRAY;
      }
    }

    public Object[] getElements(final Object inputElement) {
      return makeTreeNodesFromGroupNames(PropertyGroupHelper.getGroups().toArray());
    }

  }

  private abstract class RelevantPropertiesContentProvider extends AbstractPropertyTreeContentProvider implements
      IConceptModificationListener {
    public RelevantPropertiesContentProvider() {
      //      this.conceptModel = conceptModel;
      conceptModel.addConceptModificationListener(this);
    }

    public void conceptModified(final ConceptModificationEvent e) {
      if (logger.isDebugEnabled()) {
        logger.debug("heard concept modified event; event is " + e);
      }
      // tree is small enough that we don't need to be smart about painting only changed nodes; paint everything
      getViewer().refresh(true);
      getViewer().expandAll();
    }

    public void dispose() {
      // remove the concept modification listener
      conceptModel.removeConceptModificationListener(this);
    }

  }

  private class UsedPropertiesContentProvider extends RelevantPropertiesContentProvider {

    public Object[] getChildren(final Object parentElement) {
      if (parentElement instanceof GroupNode) {
        // a group node
        GroupNode n = (GroupNode) parentElement;
        return this.makeTreeNodesFromPropertyIds(PropertyGroupHelper.getUsedPropertiesForGroup(n.getGroupName(),
            conceptModel).toArray());
      } else {
        // a property node
        return EMPTY_ARRAY;
      }
    }

    public Object[] getElements(final Object inputElement) {
      return makeTreeNodesFromGroupNames(PropertyGroupHelper.getUsedGroups(conceptModel).toArray());
    }

  }

  private class UnusedPropertiesContentProvider extends RelevantPropertiesContentProvider {

    public Object[] getChildren(final Object parentElement) {
      if (parentElement instanceof GroupNode) {
        // a group node
        GroupNode n = (GroupNode) parentElement;
        return this.makeTreeNodesFromPropertyIds(PropertyGroupHelper.getUnusedPropertiesForGroup(n.getGroupName(),
            conceptModel).toArray());
      } else {
        // a property node
        return EMPTY_ARRAY;
      }
    }

    public Object[] getElements(final Object inputElement) {
      return makeTreeNodesFromGroupNames(PropertyGroupHelper.getUnusedGroups(conceptModel).toArray());
    }

  }

  private class PropertyTreeLabelProvider implements ILabelProvider {

    public Image getImage(Object element) {
      if (decorate) {
        if (element instanceof GroupNode) {
          return Constants.getImageRegistry(Display.getCurrent()).get("property-group");
        } else if (element instanceof PropertyNode) {
          PropertyNode node = (PropertyNode) element;
          String propertyId = node.getId();
          switch (conceptModel.getPropertyContributor(propertyId)) {
            case IConceptModel.REL_THIS: {
              return Constants.getImageRegistry(Display.getCurrent()).get("child-property");
            }
            case IConceptModel.REL_SECURITY: {
              return Constants.getImageRegistry(Display.getCurrent()).get("security-property");
            }
            case IConceptModel.REL_PARENT: {
              return Constants.getImageRegistry(Display.getCurrent()).get("parent-property");
            }
            case IConceptModel.REL_INHERITED: {
              return Constants.getImageRegistry(Display.getCurrent()).get("inherited-property");
            }
            default: {
              return null;
            }
          }
        } else {
          return null;
        }
      } else {
        return null;
      }
    }

    public String getText(Object element) {
      if (element instanceof GroupNode) {
        GroupNode n = (GroupNode) element;
        return n.getGroupName();
      } else if (element instanceof PropertyNode) {
        PropertyNode n = (PropertyNode) element;
        return DefaultPropertyID.findDefaultPropertyID(n.getId()).getDescription();
      } else {
        return "";
      }
    }

    public void addListener(ILabelProviderListener listener) {
      // not used
    }

    public void dispose() {
      // not used
    }

    public boolean isLabelProperty(Object element, String property) {
      // not used
      return false;
    }

    public void removeListener(ILabelProviderListener listener) {
      // not used
    }
  }

  public void addSelectionChangedListener(final ISelectionChangedListener listener) {
    eventSupport.addListener(listener);
  }

  public ISelection getSelection() {
    TreeSelection origSel = (TreeSelection) treeViewer.getSelection();
    return transformTreeSelection(origSel);
  }

  public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
    eventSupport.removeListener(listener);
  }

  protected void fireSelectionChangedEvent(final SelectionChangedEvent e) {
    Set listeners = eventSupport.getListeners();
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ISelectionChangedListener listener = (ISelectionChangedListener) iter.next();
      listener.selectionChanged(e);
    }
  }

  public void setSelection(ISelection selection) {
    //  not currently supported
    throw new UnsupportedOperationException();
  }

}