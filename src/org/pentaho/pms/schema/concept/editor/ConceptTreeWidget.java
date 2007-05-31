package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.pentaho.pms.schema.concept.ConceptInterface;

public class ConceptTreeWidget extends Composite implements ISelectionProvider {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(TableColumnTreeWidget.class);

  // ~ Instance fields =================================================================================================

  private TreeViewer treeViewer;

  private boolean decorate = true;

  private IConceptTreeModel conceptTreeModel;

  // ~ Constructors ====================================================================================================

  /**
   * Shows only the properties defined in the given concept model. Refreshes itself in reaction to concept model
   * changes.
   */
  public ConceptTreeWidget(final Composite parent, final int style, final IConceptTreeModel conceptTreeModel,
      final boolean decorate) {
    super(parent, style);
    this.conceptTreeModel = conceptTreeModel;
    this.decorate = decorate;
    createContents();
  }

  // ~ Methods =========================================================================================================

  protected void createContents() {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        ConceptTreeWidget.this.widgetDisposed(e);
      }
    });
    setLayout(new FillLayout());
    Tree tree2 = new Tree(this, SWT.SINGLE | SWT.BORDER); // single selection at a time
    treeViewer = new TreeViewer(tree2);
    ITreeContentProvider contentProvider = null;
    contentProvider = new ConceptTreeContentProvider();
    treeViewer.setContentProvider(contentProvider);
    treeViewer.setLabelProvider(new ConceptTreeLabelProvider());

    treeViewer.setInput("ignored");

    treeViewer.expandAll();
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  private class ConceptTreeContentProvider implements ITreeContentProvider {
    protected final Object[] EMPTY_ARRAY = new Object[0];

    private TreeViewer viewer;

    protected TreeViewer getViewer() {
      return viewer;
    }

    public Object getParent(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug("getParent arg is " + element);
      }
      if (element instanceof ConceptInterface) {
        return ((ConceptInterface) element).getParentInterface();
      }
      return null;
    }

    public boolean hasChildren(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug("hasChildren arg is " + element);
      }
      if (element instanceof ConceptInterface) {
        ConceptInterface[] children = conceptTreeModel.getChildren((ConceptInterface) element);
        if (null != children && children.length > 0) {
          return true;
        }
      }
      return false;
    }

    public void dispose() {
      if (logger.isDebugEnabled()) {
        logger.debug("dispose");
      }
      // nothing to dispose
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      if (logger.isDebugEnabled()) {
        logger.debug("inputChanged");
      }
      this.viewer = (TreeViewer) viewer;
      // no need to adjust listeners
    }

    public Object[] getChildren(final Object parentElement) {
      if (logger.isDebugEnabled()) {
        logger.debug("getChildren arg is " + parentElement);
      }
      if (parentElement instanceof ConceptInterface) {
        return conceptTreeModel.getChildren((ConceptInterface) parentElement);
      }
      return null;
    }

    public Object[] getElements(final Object inputElement) {
      if (logger.isDebugEnabled()) {
        logger.debug("getElements arg is " + inputElement);
      }
      return conceptTreeModel.getChildren(null);
    }

  }

  private class ConceptTreeLabelProvider implements ILabelProvider {

    public Image getImage(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug("getImage arg is " + element);
      }
      if (decorate) {
        return Constants.getImageRegistry(Display.getCurrent()).get("concept");
      }
      return null;
    }

    public String getText(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug("getText arg is " + element);
      }
      if (element instanceof ConceptInterface) {
        return ((ConceptInterface) element).getName();

      }
      return null;
    }

    public void addListener(final ILabelProviderListener listener) {
      // not used
    }

    public void dispose() {
      // not used
    }

    public boolean isLabelProperty(final Object element, final String property) {
      // not used
      return false;
    }

    public void removeListener(final ILabelProviderListener listener) {
      // not used
    }
  }

  public void addSelectionChangedListener(final ISelectionChangedListener listener) {
    treeViewer.addSelectionChangedListener(listener);
  }

  public ISelection getSelection() {
    return treeViewer.getSelection();
  }

  public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
    treeViewer.removeSelectionChangedListener(listener);
  }

  public void setSelection(final ISelection selection) {
    treeViewer.setSelection(selection);
  }

}
