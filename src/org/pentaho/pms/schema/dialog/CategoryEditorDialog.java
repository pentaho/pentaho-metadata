
/**
 * 
 */
package org.pentaho.pms.schema.dialog;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.ITreeNodeChangedListener;
import org.pentaho.pms.jface.tree.TreeContentProvider;
import org.pentaho.pms.jface.tree.TreeLabelProvider;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.editor.Constants;
import org.pentaho.pms.ui.tree.BusinessColumnTreeNode;
import org.pentaho.pms.ui.tree.BusinessTableTreeNode;
import org.pentaho.pms.ui.tree.BusinessTablesTreeNode;
import org.pentaho.pms.ui.tree.BusinessViewTreeNode;
import org.pentaho.pms.ui.tree.CategoryTreeNode;
import org.pentaho.pms.ui.tree.ConceptTreeNode;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.widget.TreeMemory;

/**
 * @author Gretchen Moran
 *
 */
public class CategoryEditorDialog extends TitleAreaDialog {

  private static final String STRING_TABLES_TREE = "TablesTree"; //$NON-NLS-1$

  private static final String STRING_CATEGORIES_TREE = "CategoriesTree"; //$NON-NLS-1$

  private static final Log logger = LogFactory.getLog(CategoryEditorDialog.class);

  private BusinessModel businessModel;

  private SchemaMeta schemaMeta;
  
  private TreeViewer wTables;

  private TreeViewer wCategories;

  private BusinessViewTreeNode rootCategory;

  private ToolItem wAddSelection;

  private ToolItem wDelSelection;

  private ToolItem wAddAll;

  private ToolItem wNew;

  private Menu categoryMenu;

  
  public CategoryEditorDialog(Shell shell, BusinessModel businessModel, SchemaMeta schemaMeta) {
    super(shell);
    this.businessModel = businessModel;
    this.schemaMeta = schemaMeta;

  }

  protected Control createContents(Composite parent) {
    Control contents = super.createContents(parent);
    setMessage(Messages.getString("CategoryEditorDialog.USER_DIALOG_MESSAGE")); //$NON-NLS-1$
    setTitle(Messages.getString("CategoryEditorDialog.USER_DIALOG_TITLE")); //$NON-NLS-1$
    return contents;
  }

  protected Control createDialogArea(final Composite parent) {

    Composite c0 = (Composite) super.createDialogArea(parent);

    c0.setBackground(GUIResource.getInstance().getColorWhite());
    Composite c1 = new Composite(c0, SWT.BORDER);
    c1.setBackground(GUIResource.getInstance().getColorWhite());
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    c1.setLayout(gridLayout);

    GridData data = new GridData(GridData.FILL_BOTH);
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    c1.setLayoutData(data);

    Composite composite0 = new Composite(c1, SWT.NONE);
    composite0.setBackground(GUIResource.getInstance().getColorWhite());
    data = new GridData(GridData.FILL_BOTH);
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    composite0.setLayoutData(data);
    buildBusinessTablePanel(composite0);

    Composite composite1 = new Composite(c1, SWT.NONE);
    composite1.setBackground(GUIResource.getInstance().getColorWhite());
    data = new GridData();
    data.horizontalAlignment = GridData.CENTER;
    composite1.setLayoutData(data);
    buildGridButtonPanel(composite1);

    Composite composite2 = new Composite(c1, SWT.NONE);
    composite2.setBackground(GUIResource.getInstance().getColorWhite());
    data = new GridData(GridData.FILL_BOTH);
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    composite2.setLayoutData(data);
    buildBusinessCategoryPanel(composite2);

    getData();

    return c0;
  }

  private void buildBusinessTablePanel(Composite parent) {

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    parent.setLayout(gridLayout);

    Label label0 = new Label(parent, SWT.NONE);
    label0.setText(Messages.getString("CategoryEditorDialog.USER_AVAILABLE_BUSINESS_TABLES")); //$NON-NLS-1$
    label0.setBackground(GUIResource.getInstance().getColorWhite());
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.END;
    data.grabExcessHorizontalSpace = true;
    label0.setLayoutData(data);

    Composite composite0 = new Composite(parent, SWT.NONE);
    data = new GridData();
    data.heightHint = 22;
    data.widthHint = 44;
    data.horizontalAlignment = GridData.END;
    composite0.setLayoutData(data);
    composite0.setBackground(GUIResource.getInstance().getColorWhite());

    wTables = new TreeViewer(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    data = new GridData(GridData.FILL_BOTH);
    data.verticalAlignment = GridData.BEGINNING;
    data.horizontalSpan = 3;
    data.minimumHeight = 217;
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    wTables.getTree().setLayoutData(data);
  }

  private void buildBusinessCategoryPanel(Composite parent) {

    GridLayout gridLayout = new GridLayout(2, false);
    parent.setLayout(gridLayout);

    Label label0 = new Label(parent, SWT.NONE);
    label0.setText(Messages.getString("CategoryEditorDialog.USER_BUSINESS_VIEW_CATEGORIES")); //$NON-NLS-1$
    label0.setBackground(GUIResource.getInstance().getColorWhite());
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.END;
    data.grabExcessHorizontalSpace = true;
    label0.setLayoutData(data);

    ToolBar tb = new ToolBar(parent, SWT.FLAT);
    tb.setBackground(GUIResource.getInstance().getColorWhite());
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalAlignment = SWT.END;
    tb.setLayoutData(gridData);
    
    wNew = new ToolItem(tb, SWT.PUSH);
    wNew.setImage(Constants.getImageRegistry(Display.getCurrent()).get("add-button"));
    wNew.setToolTipText(Messages.getString("CategoryEditorDialog.USER_ADD_NEW_CATEGORY")); //$NON-NLS-1$
    wNew.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        newCategory();
      }
    });

    wDelSelection = new ToolItem(tb, SWT.PUSH);
    wDelSelection.setImage(Constants.getImageRegistry(Display.getCurrent()).get("del-button"));
    wDelSelection.setToolTipText(Messages.getString("CategoryEditorDialog.USER_DELETE")); //$NON-NLS-1$
    wDelSelection.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        deleteSelections();
      }
    });

    wCategories = new TreeViewer(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    data = new GridData(GridData.FILL_BOTH);
    data.verticalAlignment = GridData.BEGINNING;
    data.horizontalSpan = 3;
    data.minimumHeight = 217;
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    wCategories.getTree().setLayoutData(data);
    wCategories.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {

        if(event.getSelection().isEmpty()) {
            return;
        }
        if(event.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection)event.getSelection();
            if ((selection.getFirstElement() instanceof CategoryTreeNode) || (selection.getFirstElement() instanceof BusinessViewTreeNode)) {
              setCategoryMenu();
            }else{
              wCategories.getTree().setMenu(null);
            }
        }
      }
    });

  }

  private void buildGridButtonPanel(Composite parent) {
    GridLayout gridLayout = new GridLayout();
    gridLayout.makeColumnsEqualWidth = true;
    parent.setLayout(gridLayout);

    Composite composite0 = new Composite(parent, SWT.NONE);
    GridData data = new GridData();
    data.horizontalAlignment = GridData.CENTER;
    data.grabExcessVerticalSpace = true;
    data.widthHint = 10;
    composite0.setLayoutData(data);
    composite0.setBackground(GUIResource.getInstance().getColorWhite());

    ToolBar tb1 = new ToolBar(parent, SWT.FLAT);
    tb1.setBackground(GUIResource.getInstance().getColorWhite());
    wAddSelection = new ToolItem(tb1, SWT.PUSH);
    wAddSelection.setImage(Constants.getImageRegistry(Display.getCurrent()).get("add-arrow")); //$NON-NLS-1$
    wAddSelection.setToolTipText(Messages.getString("CategoryEditorDialog.USER_ADD")); //$NON-NLS-1$
    wAddSelection.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        addSelection();
      }
    });

    ToolBar tb2 = new ToolBar(parent, SWT.FLAT);
    tb2.setBackground(GUIResource.getInstance().getColorWhite());
    wAddAll = new ToolItem(tb2, SWT.PUSH);
    wAddAll.setImage(Constants.getImageRegistry(Display.getCurrent()).get("add-all-arrow")); //$NON-NLS-1$
    wAddAll.setToolTipText(Messages.getString("CategoryEditorDialog.USER_ADD_ALL")); //$NON-NLS-1$
    wAddAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        addAll();
      }
    });

    Composite composite5 = new Composite(parent, SWT.NONE);
    data = new GridData();
    data.horizontalAlignment = GridData.CENTER;
    data.grabExcessVerticalSpace = true;
    data.widthHint = 10;
    composite5.setLayoutData(data);
    composite5.setBackground(GUIResource.getInstance().getColorWhite());
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.getString("CategoryEditorDialog.USER_CATEGORY_EDITOR")); //$NON-NLS-1$
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Point getInitialSize() {
    return new Point(662, 420);
  }

  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
  }

  protected void buttonPressed(int buttonId) {
    setReturnCode(buttonId);
    close();
  }
  
  protected void setCategoryMenu(){

    if (categoryMenu == null) {
      categoryMenu = new Menu(this.getShell(), SWT.POP_UP);
    } else {
      MenuItem[] items = categoryMenu.getItems();
      for (int i = 0; i < items.length; i++)
        items[i].dispose();
    }
    MenuItem miNew = new MenuItem(categoryMenu, SWT.PUSH);
    miNew.setText(Messages.getString("CategoryEditorDialog.USER_ADD_NEW_CATEGORY"));  //$NON-NLS-1$
    miNew.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event evt) {
        newCategory();
      }
    });
    wCategories.getTree().setMenu(categoryMenu);
  }

  /***************** BUSINESSS LOGIC FOR DIALOG ***************************************************/
  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    refreshCategories();
    refreshTables();
  }

  private void refreshCategories() {

    wCategories.setContentProvider(new TreeContentProvider());
    wCategories.setLabelProvider(new TreeLabelProvider());
    rootCategory = new BusinessViewTreeNode(null, businessModel.getRootCategory(), schemaMeta.getActiveLocale());
    rootCategory.addTreeNodeChangeListener((ITreeNodeChangedListener) wCategories.getContentProvider());
    wCategories.setInput(rootCategory);
    
    wCategories.refresh();
    TreeMemory.setExpandedFromMemory(wCategories.getTree(), STRING_CATEGORIES_TREE);

  }

  private void refreshTables() {

    wTables.setContentProvider(new TreeContentProvider());
    wTables.setLabelProvider(new TreeLabelProvider());
    ITreeNode root = new BusinessTablesTreeNode(null, businessModel, schemaMeta.getActiveLocale());
    root.addTreeNodeChangeListener((ITreeNodeChangedListener) wTables.getContentProvider());
    wTables.setInput(root);
    wTables.refresh();

    TreeMemory.setExpandedFromMemory(wTables.getTree(), STRING_TABLES_TREE);
  }

  protected void newCategory() {

    while (true) {
      BusinessCategory businessCategory = new BusinessCategory();
      BusinessCategoryDialog dialog = new BusinessCategoryDialog(this.getShell(), businessCategory, schemaMeta);
      if (dialog.open() == Window.OK) {
        // Add this to the parent.
        try {
          businessModel.getRootCategory().addBusinessCategory(businessCategory);
          //TreeMemory.getInstance().storeExpanded(MetaEditor.STRING_CATEGORIES_TREE, path, true);
          rootCategory.addDomainChild(businessCategory);
          break;
        } catch (ObjectAlreadyExistsException e) {
          new ErrorDialog(
              this.getShell(),
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessCategoriesDialog.USER_ERROR_BUSINESS_CATEGORY_EXISTS", businessCategory.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
        }
      } else {
        break;
      }
    }
  }

  protected void deleteSelections() {

    IStructuredSelection selection = (IStructuredSelection) wCategories.getSelection();

    for (Iterator iterator = selection.iterator(); iterator.hasNext();) {

      Object domainObject = iterator.next();

      if (domainObject instanceof BusinessColumnTreeNode) {
        BusinessColumnTreeNode columnTreeNode = (BusinessColumnTreeNode) domainObject;
        CategoryTreeNode categoryTreeNode = (CategoryTreeNode) columnTreeNode.getParent();
        BusinessCategory category = (BusinessCategory) categoryTreeNode.getDomainObject();
        category.removeBusinessColumn((BusinessColumn) columnTreeNode.getDomainObject());
        categoryTreeNode.removeChild(columnTreeNode);
      } else if (domainObject instanceof CategoryTreeNode) {
        CategoryTreeNode categoryTreeNode = (CategoryTreeNode) domainObject;
        BusinessCategory category = (BusinessCategory) categoryTreeNode.getDomainObject();
        BusinessCategory parentCategory = businessModel.getRootCategory();
        parentCategory.removeBusinessCategory(category);
        rootCategory.removeChild(categoryTreeNode);
      }
    }
  }

  private void addCategoryFromBusinessTable(BusinessCategory parentCategory, BusinessTable businessTable) {

    // Create a new category
    BusinessCategory businessCategory = businessTable.generateCategory(schemaMeta.getActiveLocale(), businessModel.getRootCategory().getBusinessCategories());

    // Add the category to the business model or category
    try {
      parentCategory.addBusinessCategory(businessCategory);
    } catch (ObjectAlreadyExistsException e) {
      new ErrorDialog(
          this.getShell(),
          Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("BusinessCategoriesDialog.USER_ERROR_BUSINESS_CATEGORY_EXISTS", businessCategory.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$ 
      return;
    }
    rootCategory.addDomainChild(businessCategory);

  }
  protected void addSelection(){
    
    boolean foundSelectedParent = false;
    BusinessCategory parentCategory = null;
    Object firstSelection = null;
    
    // Find the first selected category in the category tree... If there are columns to
    // be added, this is the category they will be added to
    IStructuredSelection categorySelection = (IStructuredSelection) wCategories.getSelection();
    for (Iterator iterator = categorySelection.iterator(); iterator.hasNext();) {
      firstSelection = iterator.next();
      if ((firstSelection instanceof CategoryTreeNode) || (firstSelection instanceof BusinessViewTreeNode)){
        parentCategory = (BusinessCategory)((CategoryTreeNode)firstSelection).getDomainObject();
        foundSelectedParent = true;
        break;
      }
    }

    // First, add all of the selected business tables as new categories
    IStructuredSelection selection = (IStructuredSelection) wTables.getSelection();
    for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
      Object domainObject = iterator.next();
      if (domainObject instanceof BusinessTableTreeNode) {
        addCategoryFromBusinessTable((BusinessCategory)rootCategory.getDomainObject(), (BusinessTable)((BusinessTableTreeNode)domainObject).getDomainObject());
      }
    }

    for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
      Object domainObject = iterator.next();
      if (domainObject instanceof BusinessColumnTreeNode){
        // There are no categories selected in the category tree, so ... 
        if (!foundSelectedParent){
          MessageBox mb = new MessageBox(this.getShell(), SWT.OK | SWT.ICON_ERROR);
          mb.setMessage(Messages.getString("CategoryEditorDialog.USER_SELECT_CATEGORY_ON_RIGHT"));   //$NON-NLS-1$
          mb.setText(Messages.getString("CategoryEditorDialog.USER_NO_SELECTION_FOUND")); //$NON-NLS-1$
          mb.open();
          break;
        }
        // Add the column to the category
        try {
          parentCategory.addBusinessColumn(((BusinessColumnTreeNode)domainObject).getBusinessColumn());
          ((ConceptTreeNode)firstSelection).addDomainChild(((BusinessColumnTreeNode)domainObject).getBusinessColumn());
        } catch (ObjectAlreadyExistsException e) {
          // Should not happen here, programmatically generating new ids.
          logger.error(Messages.getErrorString("CategoryEditorDialog.ERROR_0001_COLUMN_SKIPPED_DUPLICATE_ID",((BusinessColumnTreeNode)domainObject).getBusinessColumn().getId()), e); //$NON-NLS-1$
        }

      }
    }
  }
  
  protected void addAll(){
    
    for (int i=0;i<businessModel.nrBusinessTables();i++) {
        BusinessTable businessTable = businessModel.getBusinessTable(i);
        addCategoryFromBusinessTable((BusinessCategory)rootCategory.getDomainObject(), businessTable);
    }
  }

}
