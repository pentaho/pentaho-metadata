package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public class PropertyListWidget extends Composite {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyListWidget.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  // ~ Constructors ====================================================================================================

  public PropertyListWidget(final Composite parent, final int style, final IConceptModel conceptModel) {
    super(parent, style);
    this.conceptModel = conceptModel;
    //    conceptModel.addConceptModificationListener(new IConceptModificationListener() {
    //      public void conceptModified(ConceptModificationEvent e) {
    //        PropertyListWidget.this.conceptModified(e);
    //      }
    //    });
    createContents();

    //    try {
    //      Thread.sleep(1000);
    //
    //      conceptModel.setProperty(new ConceptPropertyString("blah", "blah"));
    //
    //      Thread.sleep(1000);
    //
    //      conceptModel.removeProperty("blah");
    //
    //      Thread.sleep(1000);
    //    } catch (InterruptedException e1) {
    //      if (logger.isErrorEnabled()) {
    //      	// TODO Auto-generated catch block
    //      	logger.error("an exception occurred", e1);
    //      }
    //    }

  }

  // ~ Methods =========================================================================================================

  protected void createContents() {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PropertyListWidget.this.widgetDisposed(e);
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

    final TreeViewer tv = new TreeViewer(c0);
    FormData fd1 = new FormData();
    fd1.top = new FormAttachment(lab0, 3);
    fd1.left = new FormAttachment(0, 3);
    fd1.right = new FormAttachment(100, -3);
    fd1.bottom = new FormAttachment(100, -3);
    tv.getTree().setLayoutData(fd1);
    tv.setContentProvider(new PropertyListContentProvider());
    tv.setLabelProvider(new PropertyListLabelProvider());
    tv.setInput("ignored");
    tv.expandAll();
    //    tv.setInput("root"); // ignored
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

  private class PropertyListContentProvider implements ITreeContentProvider {

    private final Object[] EMPTY_ARRAY = new Object[0];

    //    private IConceptModel conceptModel;
    private TreeViewer viewer;

    public PropertyListContentProvider() {
      //      this.conceptModel = conceptModel;
      conceptModel.addConceptModificationListener(new IConceptModificationListener() {
        public void conceptModified(ConceptModificationEvent e) {
          PropertyListContentProvider.this.conceptModified(e);
        }
      });
    }

    public void conceptModified(ConceptModificationEvent e) {
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
        return new Object[0];
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

  private class PropertyListLabelProvider implements ILabelProvider {

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

}