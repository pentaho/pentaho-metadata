package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptInterface;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

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
    conceptTreeModel.addConceptTreeModificationListener(new IConceptTreeModificationListener() {
      public void conceptTreeModified(ConceptTreeModificationEvent e) {
        refreshTree();
      }
    });
    this.decorate = decorate;
    createContents();
  }

  // ~ Methods =========================================================================================================

  protected void refreshTree() {
    treeViewer.refresh(true);
    treeViewer.expandAll();
  }

  protected void createContents() {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        ConceptTreeWidget.this.widgetDisposed(e);
      }
    });
    setLayout(new FormLayout());

    Label lab1 = new Label(this, SWT.NONE);
    lab1.setFont(Constants.getFontRegistry(getDisplay()).get("prop-mgmt-title"));
    lab1.setText("Concepts");

    Tree tree2 = new Tree(this, SWT.SINGLE | SWT.BORDER); // single selection at a time
    treeViewer = new TreeViewer(tree2);

    ToolBar tb3 = new ToolBar(this, SWT.FLAT);

    ToolItem ti4 = new ToolItem(tb3, SWT.PUSH);
    ti4.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-add-button"));
    ti4.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(final SelectionEvent e) {
      }

      public void widgetSelected(final SelectionEvent e) {
        addButtonPressed();
      }

    });
    ToolItem delButton = new ToolItem(tb3, SWT.PUSH);
    delButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-del-button"));
    delButton.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(final SelectionEvent e) {
      }

      public void widgetSelected(final SelectionEvent e) {
        deleteButtonPressed();
      }
    });

    //    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
    //      public void selectionChanged(final SelectionChangedEvent e) {
    //
    //      }
    //    });

    FormData fd1 = new FormData();
    fd1.bottom = new FormAttachment(tree2, -10);
    fd1.left = new FormAttachment(0, 0);
    lab1.setLayoutData(fd1);

    FormData fd6 = new FormData();
    fd6.top = new FormAttachment(0, 38);
    fd6.left = new FormAttachment(0, 0);
    fd6.right = new FormAttachment(100, 0);
    fd6.bottom = new FormAttachment(100, 0);
    tree2.setLayoutData(fd6);

    FormData fd3 = new FormData();
    fd3.top = new FormAttachment(0, 0);
    fd3.right = new FormAttachment(100, 0);
    tb3.setLayoutData(fd3);

    ITreeContentProvider contentProvider = null;
    contentProvider = new ConceptTreeContentProvider();
    treeViewer.setContentProvider(contentProvider);
    treeViewer.setLabelProvider(new ConceptTreeLabelProvider());

    treeViewer.setInput("ignored");

    treeViewer.expandAll();
  }

  protected void deleteButtonPressed() {
    TreeSelection treeSelection = (TreeSelection) treeViewer.getSelection();
    ConceptInterface selected = (ConceptInterface) treeSelection.getFirstElement();
    boolean delete = MessageDialog.openConfirm(this.getShell(), "Confirm",
        "Are you sure you want to delete the concept '" + selected.getName() + "'?");
    if (logger.isDebugEnabled()) {
      logger.debug("user chose to delete: " + delete);
    }
    if (delete) {
      conceptTreeModel.removeConcept(selected);
    }
  }

  protected void addButtonPressed() {
    InputDialog dialog = new InputDialog(getShell(), "New Concept",
        "Enter the name of the new concept. The parent concept of the new concept will "
            + "be the concept currently selected.", "", null);
    dialog.open();
    String name = dialog.getValue();
    if (null != name) {
      TreeSelection treeSelection = (TreeSelection) treeViewer.getSelection();
      ConceptInterface selected = (ConceptInterface) treeSelection.getFirstElement();
      ConceptInterface newConcept = new Concept(name, selected);
      conceptTreeModel.addConcept(selected, newConcept);
    }
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
