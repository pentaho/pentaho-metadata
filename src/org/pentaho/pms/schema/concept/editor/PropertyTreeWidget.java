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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public class PropertyTreeWidget extends Composite implements ISelectionProvider {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyTreeWidget.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private EventSupport eventSupport = new EventSupport();

  // ~ Constructors ====================================================================================================

  public PropertyTreeWidget(final Composite parent, final int style, final IConceptModel conceptModel) {
    super(parent, style);
    this.conceptModel = conceptModel;
    createContents();
  }

  // ~ Methods =========================================================================================================

  protected void createContents() {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PropertyTreeWidget.this.widgetDisposed(e);
      }
    });
    Composite c0 = new Composite(this, SWT.NONE);
    FormLayout propListLayout = new FormLayout();
    c0.setLayout(propListLayout);
    Label lab0 = new Label(c0, SWT.NONE);
    lab0.setText("Properties");
    FormData fd0 = new FormData();
    fd0.top = new FormAttachment(0, 3);
    fd0.left = new FormAttachment(0, 3);
    lab0.setLayoutData(fd0);

    Tree tree2 = new Tree(c0, SWT.SINGLE); // single selection at a time
    FormData fd1 = new FormData();
    fd1.top = new FormAttachment(lab0, 3);
    fd1.left = new FormAttachment(0, 3);
    fd1.right = new FormAttachment(100, -3);
    fd1.bottom = new FormAttachment(100, -3);
    tree2.setLayoutData(fd1);
    TreeViewer tv = new TreeViewer(tree2);

    tv.setContentProvider(new PropertyTreeContentProvider());
    tv.setLabelProvider(new PropertyTreeLabelProvider());
    tv.setInput("ignored");
    tv.expandAll();

    tv.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(final SelectionChangedEvent e) {
        // propagate the event, but first create a high-level event from the low-level event

        TreeSelection treeSelection = (TreeSelection) e.getSelection();
        Object objectSelected = treeSelection.getFirstElement();

        ISelection highLevelSelection = null;
        if (objectSelected instanceof SectionNode) {
          SectionNode n = (SectionNode) objectSelected;
          highLevelSelection = new PropertyTreeSelection(n.getSectionName(), true);
        } else if (objectSelected instanceof PropertyNode) {
          PropertyNode n = (PropertyNode) objectSelected;
          highLevelSelection = new PropertyTreeSelection(n.getId(), false);
        } else {
          if (logger.isWarnEnabled()) {
            logger.warn("dropped event since it is an unknown type:" + treeSelection.getClass().getName());
          }
        }
        fireSelectionChangedEvent(new SelectionChangedEvent(PropertyTreeWidget.this, highLevelSelection));
      }
    });
    setLayout(new FillLayout());
  }

  protected void widgetDisposed(final DisposeEvent e) {
    if (logger.isDebugEnabled()) {
      logger.debug("heard dispose event");
    }
  }

  /**
   * Abstract parent of two child node types: <code>PropertyNode</code> and <code>SectionNode</code>.
   * @author mlowery
   */
  private abstract class PropertyTreeNode {
  }

  /**
   * Section nodes contain property nodes.
   * @author mlowery
   */
  private class SectionNode extends PropertyTreeNode {
    private String sectionName;

    public SectionNode(final String sectionName) {
      this.sectionName = sectionName;
    }

    public String getSectionName() {
      return sectionName;
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

  private class PropertyTreeContentProvider implements ITreeContentProvider {

    private final Object[] EMPTY_ARRAY = new Object[0];

    //    private IConceptModel conceptModel;
    private TreeViewer viewer;

    public PropertyTreeContentProvider() {
      //      this.conceptModel = conceptModel;
      conceptModel.addConceptModificationListener(new IConceptModificationListener() {
        public void conceptModified(ConceptModificationEvent e) {
          PropertyTreeContentProvider.this.conceptModified(e);
        }
      });
    }

    protected void conceptModified(ConceptModificationEvent e) {
      if (logger.isDebugEnabled()) {
        logger.debug("heard concept modified event; event is " + e);
      }
      // tree is small enough that we don't need to be smart about painting only changed nodes; paint everything
      viewer.refresh(true);
    }

    public Object[] getChildren(final Object parentElement) {
      if (parentElement instanceof SectionNode) {
        // a section node
        SectionNode n = (SectionNode) parentElement;
        return this.makeTreeNodesFromPropertyIds(PropertyListSectionHelper.getRelevantPropertiesForSection(
            n.getSectionName(), conceptModel).toArray());
      } else {
        // a property node
        return EMPTY_ARRAY;
      }
    }

    public Object getParent(final Object element) {
      if (element instanceof PropertyNode) {
        // a property element
        PropertyNode n = (PropertyNode) element;
        return new SectionNode(PropertyListSectionHelper.getSectionForProperty(n.getId()));
      } else {
        return null;
      }
    }

    public boolean hasChildren(final Object element) {
      if (element instanceof SectionNode) {
        return true;
      } else {
        return false;
      }
    }

    public Object[] getElements(final Object inputElement) {
      return makeTreeNodesFromSectionNames(PropertyListSectionHelper.getRelevantSections(conceptModel).toArray());
    }

    public void dispose() {
      // nothing to dispose
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      this.viewer = (TreeViewer) viewer;
      // no need to adjust listeners
    }

    private PropertyTreeNode[] makeTreeNodesFromSectionNames(final Object[] sectionNames) {
      List propertyTreeNodes = new ArrayList();
      for (int i = 0; i < sectionNames.length; i++) {
        propertyTreeNodes.add(new SectionNode((String) sectionNames[i]));
      }
      return (PropertyTreeNode[]) propertyTreeNodes.toArray(new PropertyTreeNode[0]);
    }

    private PropertyTreeNode[] makeTreeNodesFromPropertyIds(final Object[] propertyIds) {
      List propertyTreeNodes = new ArrayList();
      for (int i = 0; i < propertyIds.length; i++) {
        propertyTreeNodes.add(new PropertyNode((String) propertyIds[i]));
      }
      return (PropertyTreeNode[]) propertyTreeNodes.toArray(new PropertyTreeNode[0]);
    }
  }

  private class PropertyTreeLabelProvider implements ILabelProvider {

    public Image getImage(Object element) {
      return null;
    }

    public String getText(Object element) {
      if (element instanceof SectionNode) {
        SectionNode n = (SectionNode) element;
        return n.getSectionName();
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

  public void addSelectionChangedListener(ISelectionChangedListener listener) {
    eventSupport.addListener(listener);
  }

  public ISelection getSelection() {
    // not currently supported
    throw new UnsupportedOperationException();
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