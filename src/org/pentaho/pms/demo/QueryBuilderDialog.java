package org.pentaho.pms.demo;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.commons.mql.ui.mqldesigner.MQLQueryBuilderDialog;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.database.Database;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.dialog.EnterTextDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.dialog.PreviewRowsDialog;
import be.ibridge.kettle.core.value.Value;

public class QueryBuilderDialog extends MQLQueryBuilderDialog {

  
  class TextDialog extends Dialog {
    String textMsg;
    String title;

    public TextDialog(Shell parentShell, String title, String text) {
      super(parentShell);
      this.title = title;
      textMsg = text;
      setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
    }

    protected Control createDialogArea(Composite arg0) {
      Composite parent = (Composite) super.createDialogArea(arg0);
      GridLayout gridLayout = new GridLayout();
      gridLayout.marginWidth = 5;
      gridLayout.marginHeight = 5;
      parent.setLayout(gridLayout);
      Text text = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
      text.setText(textMsg);
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.widthHint = 500;
      gridData.heightHint = 500;
      text.setLayoutData(gridData);
      return parent;
    }

    protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    protected void configureShell(Shell arg0) {
      // TODO Auto-generated method stub
      super.configureShell(arg0);
      if (title != null) {
        arg0.setText(title);
      }
    }
  }

  String lastFileName;
  Map columnsMap = null;

  public QueryBuilderDialog(Shell parentShell, SchemaMeta schemaMeta) {
    super(parentShell, schemaMeta);
  }

  public QueryBuilderDialog(Shell parentShell, MQLQuery mqlQuery) {
    super(parentShell, mqlQuery);
  }

  protected Control createContents(Composite arg0) {
    createMenuBar();
    createToolBar();
    return super.createContents(arg0);
  }

  private ToolBar createToolBar() {
    Image imFileNew = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "new.png")); //$NON-NLS-1$
    Image imFileOpen = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "open.png")); //$NON-NLS-1$
    Image imFileSave = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "save.png")); //$NON-NLS-1$
    Image imFileSaveAs = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "saveas.png")); //$NON-NLS-1$
    Image imViewMQL = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "view_mql.png")); //$NON-NLS-1$
    Image imViewSQL = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "view_sql.png")); //$NON-NLS-1$
    Image imExecute = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "execute.png")); //$NON-NLS-1$
    Image imReset = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "reset.png")); //$NON-NLS-1$
    
    ToolBar toolBar = new ToolBar(getShell(), SWT.FLAT);
    ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imFileNew);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        newQuery();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imFileOpen);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        openQuery();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imFileSave);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        saveQuery();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imFileSaveAs);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        saveQueryAs();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imViewMQL);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        viewMql();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imViewSQL);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        viewSql();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imExecute);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        executeQuery();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imReset);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        setMqlQuery(null);
      }
    });
    return toolBar;
  }

  private Menu createMenuBar() {
    Menu menu = new Menu(getShell(), SWT.BAR);

    MenuItem fileMenuHeader = new MenuItem(menu, SWT.CASCADE);
    fileMenuHeader.setText("File");
    Menu fileMenu = new Menu(getShell(), SWT.DROP_DOWN);
    fileMenuHeader.setMenu(fileMenu);
    MenuItem fileNewItem = new MenuItem(fileMenu, SWT.PUSH);
    fileNewItem.setText("New");
    fileNewItem.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        newQuery();
      }
    });
    MenuItem fileOpenItem = new MenuItem(fileMenu, SWT.PUSH);
    fileOpenItem.setText("Open...");
    fileOpenItem.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        openQuery();
      }
    });
    MenuItem fileSaveItem = new MenuItem(fileMenu, SWT.PUSH);
    fileSaveItem.setText("Save");
    fileSaveItem.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        saveQuery();
      }
    });
    MenuItem fileSaveAsItem = new MenuItem(fileMenu, SWT.PUSH);
    fileSaveAsItem.setText("Save As...");
    fileSaveAsItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        saveQueryAs();
      }
    });
    new MenuItem(fileMenu, SWT.SEPARATOR);
    MenuItem fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
    fileExitItem.setText("Exit");
    fileExitItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        exit();
      }
    });

    MenuItem toolsMenuHeader = new MenuItem(menu, SWT.CASCADE);
    toolsMenuHeader.setText("Tools");
    Menu toolsMenu = new Menu(getShell(), SWT.DROP_DOWN);
    toolsMenuHeader.setMenu(toolsMenu);
    MenuItem toolsViewMqlItem = new MenuItem(toolsMenu, SWT.PUSH);
    toolsViewMqlItem.setText("View MQL");
    toolsViewMqlItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        viewMql();
      }
    });
    MenuItem toolsViewSqlItem = new MenuItem(toolsMenu, SWT.PUSH);
    toolsViewSqlItem.setText("View SQL");
    toolsViewSqlItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        viewSql();
      }
    });
    MenuItem toolsExecuteQueryItem = new MenuItem(toolsMenu, SWT.PUSH);
    toolsExecuteQueryItem.setText("Execute Query");
    toolsExecuteQueryItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        executeQuery();
      }
    });
    new MenuItem(toolsMenu, SWT.SEPARATOR);
    MenuItem toolsResetQueryItem = new MenuItem(toolsMenu, SWT.PUSH);
    toolsResetQueryItem.setText("Reset Query");
    toolsResetQueryItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        setMqlQuery(null);
      }
    });
    getShell().setMenuBar(menu);
    return menu;
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = new Composite(parent, SWT.BORDER);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    return super.createDialogArea(composite);
  }

  public void showSQL() {
    try {
      MQLQuery mqlQuery = getMqlQuery();
      if (mqlQuery != null) {
        
        // Here we will generate the SQL with the truncated column ids, and
        // intentionally show those truncated ids as that IS the SQL that will be executing.
        
        String sql = mqlQuery.getQuery().getQuery();
        if (sql != null) {
          EnterTextDialog showSQL = new EnterTextDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_GENERATED_SQL"), Messages.getString("QueryDialog.USER_GENERATED_SQL"), sql, true); //$NON-NLS-1$ //$NON-NLS-2$
          sql = showSQL.open();
          if (!Const.isEmpty(sql)) {
            DatabaseMeta databaseMeta = ((BusinessColumn) mqlQuery.getSelections().get(0)).getPhysicalColumn().getTable().getDatabaseMeta();
            executeSQL(databaseMeta, sql);
          }
        }
      }
    } catch (Throwable e) {
      new ErrorDialog(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("QueryDialog.USER_ERROR_QUERY_GENERATION"), new Exception(e)); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void executeSQL(DatabaseMeta databaseMeta, String sql) {
    Database database = null;
    java.util.List rows = null;
    try {
      String path = ""; //$NON-NLS-1$
      try {
        File file = new File("simple-jndi"); //$NON-NLS-1$
        path = file.getCanonicalPath();
      } catch (Exception e) {
        e.printStackTrace();
      }

      System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory"); //$NON-NLS-1$ //$NON-NLS-2$
      System.setProperty("org.osjava.sj.root", path); //$NON-NLS-1$
      System.setProperty("org.osjava.sj.delimiter", "/"); //$NON-NLS-1$ //$NON-NLS-2$
      database = new Database(databaseMeta);
      database.connect();
      rows = database.getRows(sql, 5000); // get the first 5000 rows from the query for demo-purposes.
    } catch (Exception e) {
      new ErrorDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_ERROR_EXECUTING_QUERY"), Messages.getString("QueryDialog.USER_ERROR_EXECUTING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
    } finally {
      if (database != null)
        database.disconnect();
    }
    
    // Show the rows in a dialog.
    if (rows != null) {
      
      //Reinstate the actual "as" column identifiers here, before preview. 
      if (columnsMap != null){
        Row row = (Row)rows.get(0);
        for (int i = 0; i < row.size(); i++){
          Value value = row.getValue(i);
          value.setName((String)columnsMap.get(row.getValue(i).getName()));
        }        
      }
      
      PreviewRowsDialog previewRowsDialog = new PreviewRowsDialog(getShell(), SWT.NONE, Messages.getString("QueryDialog.USER_FIRST_5000_ROWS"), rows); //$NON-NLS-1$
      previewRowsDialog.open();
    }
  }

  private void newQuery() {
    lastFileName = null;
    setMqlQuery(null);
  }

  private void openQuery() {
    FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
    fileDialog.setFilterExtensions(new String[] { "*.mql", "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    fileDialog.setFilterNames(new String[] { Messages.getString("QueryDialog.USER_MQL_QUERIES"), Messages.getString("QueryDialog.USER_XML_FILES"), Messages.getString("QueryDialog.USER_ALL_FILES") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String filename = fileDialog.open();
    if (filename != null) {
      try {
        setMqlQuery(new MQLQuery(filename), false);
        
      } catch (Exception e) {
        new ErrorDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_ERROR_LOADING_QUERY"), Messages.getString("QueryDialog.USER_ERROR_LOADING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  private void executeQuery() {
    try {
      MQLQuery mqlQuery = getMqlQuery();
      if (mqlQuery != null) {
        
        // This map  holds references from the truncated column ids used to the actual column ids; 
        // we'll use the map later to reinstate the real column ids for display. This is a work
        // around for databases that limit the length of column ids in the "as" portion of the SQL.
        
        MappedQuery q = mqlQuery.getQuery();
        String sql = q.getQuery();
        columnsMap = q.getMap();
        DatabaseMeta databaseMeta = ((BusinessColumn) mqlQuery.getSelections().get(0)).getPhysicalColumn().getTable().getDatabaseMeta();
        executeSQL(databaseMeta, sql);
      }
    } catch (Throwable e) {
      new ErrorDialog(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("QueryDialog.USER_ERROR_QUERY_GENERATION"), new Exception(e)); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void viewMql() {
    try {
      MQLQuery mqlQuery = getMqlQuery();
      if (mqlQuery != null) {
        Document document = DocumentHelper.parseText(mqlQuery.getXML());
        TextDialog textDialog = new TextDialog(getShell(), "MQL Query", prettyPrint(document).getRootElement().asXML()); //$NON-NLS-1$ //$NON-NLS-2$
        textDialog.open();
      }
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  public void viewSql() {
    try {
      MQLQuery mqlQuery = getMqlQuery();
      if (mqlQuery != null) {
        MappedQuery q = mqlQuery.getQuery();
        String sql = q.getQuery();
        columnsMap = q.getMap();
        if (sql != null) {
          TextDialog textDialog = new TextDialog(getShell(), "SQL Query", sql); //$NON-NLS-1$ //$NON-NLS-2$
          textDialog.open();
        }
      }
    } catch (Throwable e) {
      new ErrorDialog(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("QueryDialog.USER_ERROR_QUERY_GENERATION"), new Exception(e)); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void exit() {
    cancelPressed();
  }

  private void saveQuery() {
    MQLQuery query = getMqlQuery();
    if (query != null) {
      if (lastFileName != null) {
        try {
          query.save(lastFileName);
        } catch (Exception e) {
          new ErrorDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_ERROR_LOADING_QUERY"), Messages.getString("QueryDialog.USER_ERROR_LOADING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      } else {
        saveQueryAs();
      }
    }
  }

  private void saveQueryAs() {
    MQLQuery query = getMqlQuery();
    if (query != null) {
      FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
      fileDialog.setFilterExtensions(new String[] { "*.mql", "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      fileDialog
          .setFilterNames(new String[] { Messages.getString("QueryDialog.USER_MQL_QUERIES"), Messages.getString("QueryDialog.USER_XML_FILES"), Messages.getString("QueryDialog.USER_ALL_FILES") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      String filename = fileDialog.open();
      if (filename != null) {
        try {
          query.save(filename);
          lastFileName = filename;
        } catch (Exception e) {
          new ErrorDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_ERROR_LOADING_QUERY"), Messages.getString("QueryDialog.USER_ERROR_LOADING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
  }

  public Document prettyPrint( Document document ) {
    try {
      OutputFormat format = OutputFormat.createPrettyPrint();
      format.setEncoding(document.getXMLEncoding());
      StringWriter stringWriter = new StringWriter();
      XMLWriter writer = new XMLWriter( stringWriter, format );
      // XMLWriter has a bug that is avoided if we reparse the document
      // prior to calling XMLWriter.write()
      writer.write(DocumentHelper.parseText(document.asXML()));
      writer.close();
      document = DocumentHelper.parseText( stringWriter.toString() );
    }
    catch ( Exception e ){
      e.printStackTrace();
            return( null );
    }
    return( document );
  } 
}
