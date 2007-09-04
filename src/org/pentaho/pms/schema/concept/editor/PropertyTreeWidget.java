package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class PropertyTreeWidget extends TreeViewer implements ISelectionProvider, IConceptModificationListener {

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

  private int visibility = SHOW_ALL;

  private boolean decorate = true;

  // ~ Constructors ====================================================================================================

  /**
   * Shows only the properties defined in the given concept model. Refreshes itself in reaction to concept model
   * changes.
   */
  public PropertyTreeWidget(final Composite parent, final int visibility, final boolean decorate) {
    super(parent, SWT.SINGLE | SWT.BORDER);
    this.visibility = visibility;
    this.decorate = decorate;
    setContentProvider(new PropertyTreeContentProvider());
    setLabelProvider(new PropertyTreeLabelProvider());

    if (null != getTree()) {
      getTree().addDisposeListener(new DisposeListener() {

        public void widgetDisposed(DisposeEvent e) {
          PropertyTreeWidget.this.widgetDisposed(e);
        }

      });
    }

  }

  public void setConceptModel(IConceptModel cm) {
    if (conceptModel != null) {
      conceptModel.removeConceptModificationListener(this);
    }

    this.conceptModel = cm;
    setInput(conceptModel);
    expandAll();

    if (conceptModel != null) {
      conceptModel.addConceptModificationListener(this);
    }
  }

  // ~ Methods =========================================================================================================

  protected void widgetDisposed(final DisposeEvent e) {
    if (conceptModel != null) {
      conceptModel.removeConceptModificationListener(this);
    }
  }

  /**
   * Abstract parent of two child node types: <code>PropertyNode</code> and <code>GroupNode</code>.
   * @author mlowery
   */
  public abstract class PropertyTreeNode {
  }

  /**
   * Group nodes contain property nodes.
   * @author mlowery
   */
  public class GroupNode extends PropertyTreeNode {
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
  public class PropertyNode extends PropertyTreeNode {
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

  private class PropertyTreeContentProvider implements ITreeContentProvider {
    protected final Object[] EMPTY_ARRAY = new Object[0];

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

    public Object[] getChildren(final Object parentElement) {
      Object[] children = EMPTY_ARRAY;
      if (parentElement instanceof GroupNode) {
        GroupNode n = (GroupNode) parentElement;
        if (visibility == SHOW_ALL) {
          children = this.makeTreeNodesFromPropertyIds(PropertyGroupHelper.getPropertiesForGroup(n.getGroupName()).toArray());
        } else if ((visibility == SHOW_USED) && (conceptModel != null)) {
          children =  this.makeTreeNodesFromPropertyIds(PropertyGroupHelper.getUsedPropertiesForGroup(n.getGroupName(),
              conceptModel).toArray());
        } else if ((visibility == SHOW_UNUSED) && (conceptModel != null)) {
          children = this.makeTreeNodesFromPropertyIds(PropertyGroupHelper.getUnusedPropertiesForGroup(n.getGroupName(),
              conceptModel).toArray());

        }
      }
      return children;
    }

    public Object[] getElements(final Object inputElement) {
      Object[] elements = EMPTY_ARRAY;
      if (visibility == SHOW_ALL) {
        elements = makeTreeNodesFromGroupNames(PropertyGroupHelper.getGroups().toArray());
      } else if ((visibility == SHOW_USED) && (conceptModel != null)) {
        elements = makeTreeNodesFromGroupNames(PropertyGroupHelper.getUsedGroups(conceptModel).toArray());
      } else if ((visibility == SHOW_UNUSED) && (conceptModel != null)) {
        elements = makeTreeNodesFromGroupNames(PropertyGroupHelper.getUnusedGroups(conceptModel).toArray());
      }
      return elements;
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
              conceptModel.getPropertyContributor(propertyId);
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
        return PredefinedVsCustomPropertyHelper.getDescription(n.getId());
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

  public void conceptModified(final ConceptModificationEvent e) {
    refresh(true);
    expandAll();
  }
}