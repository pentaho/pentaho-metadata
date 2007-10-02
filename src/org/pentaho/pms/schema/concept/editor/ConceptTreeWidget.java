package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.lang.StringUtils;
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
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.pentaho.pms.messages.Messages;
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
    lab1.setFont(Constants.getFontRegistry(getDisplay()).get("prop-mgmt-title")); //$NON-NLS-1$
    lab1.setText(Messages.getString("ConceptTreeWidget.USER_TREE_LABEL")); //$NON-NLS-1$

    Tree tree2 = new Tree(this, SWT.SINGLE | SWT.BORDER); // single selection at a time
    treeViewer = new TreeViewer(tree2);

    ToolBar tb3 = new ToolBar(this, SWT.FLAT);

    ToolItem ti4 = new ToolItem(tb3, SWT.PUSH);
    ti4.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-add-button")); //$NON-NLS-1$
    ti4.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(final SelectionEvent e) {
      }

      public void widgetSelected(final SelectionEvent e) {
        addButtonPressed();
      }

    });
    final ToolItem delButton = new ToolItem(tb3, SWT.PUSH);
    delButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-del-button")); //$NON-NLS-1$
    delButton.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(final SelectionEvent e) {
      }

      public void widgetSelected(final SelectionEvent e) {
        deleteButtonPressed();
      }
    });

    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(final SelectionChangedEvent e) {
        TreeSelection sel = (TreeSelection) e.getSelection();
        Object selectedObject = sel.getFirstElement();
        if (selectedObject instanceof ConceptInterface) {
          delButton.setEnabled(true);
        } else {
          delButton.setEnabled(false);
        }
      }
    });

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

    treeViewer.setInput("ignored"); //$NON-NLS-1$

    treeViewer.expandAll();
  }

  protected void deleteButtonPressed() {
    TreeSelection treeSelection = (TreeSelection) treeViewer.getSelection();
    ConceptInterface selected = (ConceptInterface) treeSelection.getFirstElement();
    boolean delete = MessageDialog.openConfirm(this.getShell(), Messages
        .getString("ConceptTreeWidget.USER_CONFIRM_DELETE_TITLE"), //$NON-NLS-1$
        Messages.getString("ConceptTreeWidget.USER_CONFIRM_DELETE_MESSAGE", selected.getName())); //$NON-NLS-1$ //$NON-NLS-2$
    if (logger.isDebugEnabled()) {
      logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_DELETE") + delete); //$NON-NLS-1$
    }
    if (delete) {
      conceptTreeModel.removeConcept(selected);
    }
  }

  protected void addButtonPressed() {
    InputDialog dialog = new InputDialog(getShell(), Messages.getString("ConceptTreeWidget.USER_ADD_CONCEPT_TITLE"), //$NON-NLS-1$
        Messages.getString("ConceptTreeWidget.USER_ADD_CONCEPT_MESSAGE"), "", null); //$NON-NLS-1$ //$NON-NLS-2$
    dialog.open();
    String name = dialog.getValue();
    if (StringUtils.isNotBlank(name)) {
      TreeSelection treeSelection = (TreeSelection) treeViewer.getSelection();
      Object selectedObject = treeSelection.getFirstElement();
      ConceptInterface selected = null;
      if (selectedObject instanceof ConceptInterface) {
        selected = (ConceptInterface) treeSelection.getFirstElement();
      }

      ConceptInterface newConcept = new Concept(name, selected);
      try {
        conceptTreeModel.addConcept(selected, newConcept);
      } catch (ObjectAlreadyExistsException e) {
        MessageDialog
            .openError(
                getShell(),
                Messages.getString("ConceptTreeWidget.USER_DUPE_TITLE"), Messages.getString("ConceptTreeWidget.USER_DUPE_MESSAGE")); //$NON-NLS-1$ //$NON-NLS-2$
      }

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
        logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_PARENT_ARG") + element); //$NON-NLS-1$
      }
      if (element instanceof ConceptInterface) {
        return ((ConceptInterface) element).getParentInterface();
      }
      return null;
    }

    public boolean hasChildren(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_HASCHILDREN_ARG") + element); //$NON-NLS-1$
      }
      if (element instanceof ConceptInterface) {
        ConceptInterface[] children = conceptTreeModel.getChildren((ConceptInterface) element);
        if (null != children && children.length > 0) {
          return true;
        }
      } else if (element instanceof String) {
        // this is the root
        return true;
      }
      return false;
    }

    public void dispose() {
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_DISPOSE")); //$NON-NLS-1$
      }
      // nothing to dispose
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_INPUTCHANGED")); //$NON-NLS-1$
      }
      this.viewer = (TreeViewer) viewer;
      // no need to adjust listeners
    }

    public Object[] getChildren(final Object parentElement) {
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_GETCHILDREN_ARG") + parentElement); //$NON-NLS-1$
      }
      if (parentElement instanceof ConceptInterface) {
        return conceptTreeModel.getChildren((ConceptInterface) parentElement);
      } else if (parentElement instanceof String) {
        // this is the root
        return conceptTreeModel.getChildren(null);
      }
      return null;
    }

    public Object[] getElements(final Object inputElement) {
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_GETELEMENTS_ARG") + inputElement); //$NON-NLS-1$
      }
      return new String[] { Messages.getString("ConceptTreeWidget.USER_CONCEPTS_ROOT_NODE") }; //$NON-NLS-1$
    }

  }

  private class ConceptTreeLabelProvider implements ILabelProvider {

    public Image getImage(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_GETIMAGE_ARG") + element); //$NON-NLS-1$
      }
      if (decorate) {
        return Constants.getImageRegistry(Display.getCurrent()).get("concept"); //$NON-NLS-1$
      }
      return null;
    }

    public String getText(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("ConceptTreeWidget.DEBUG_GETTEXT_ARG") + element); //$NON-NLS-1$
      }
      if (element instanceof ConceptInterface) {
        return ((ConceptInterface) element).getName();
      } else if (element instanceof String) {
        return (String) element;
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
