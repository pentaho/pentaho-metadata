/* Copyright 2006 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
package org.pentaho.pms.editor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.CWMException;
import org.pentaho.pms.demo.QueryDialog;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.factory.SchemaSaveProgressDialog;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.ITreeNodeChangedListener;
import org.pentaho.pms.jface.tree.TreeContentProvider;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.dialog.ConceptDialog;
import org.pentaho.pms.schema.concept.editor.BusinessTableModel;
import org.pentaho.pms.schema.concept.editor.PhysicalTableModel;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.dialog.BusinessCategoryDialog;
import org.pentaho.pms.schema.dialog.BusinessModelDialog;
import org.pentaho.pms.schema.dialog.BusinessTableDialog;
import org.pentaho.pms.schema.dialog.CategoryEditorDialog;
import org.pentaho.pms.schema.dialog.PhysicalTableDialog;
import org.pentaho.pms.schema.dialog.PublishDialog;
import org.pentaho.pms.schema.dialog.RelationshipDialog;
import org.pentaho.pms.schema.security.SecurityDialog;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.schema.security.SecurityService;
import org.pentaho.pms.ui.tree.BusinessColumnTreeNode;
import org.pentaho.pms.ui.tree.BusinessModelTreeNode;
import org.pentaho.pms.ui.tree.BusinessModelsTreeNode;
import org.pentaho.pms.ui.tree.BusinessTableTreeNode;
import org.pentaho.pms.ui.tree.BusinessTablesTreeNode;
import org.pentaho.pms.ui.tree.BusinessViewTreeNode;
import org.pentaho.pms.ui.tree.CategoryTreeNode;
import org.pentaho.pms.ui.tree.ConceptLabelProvider;
import org.pentaho.pms.ui.tree.ConceptTreeNode;
import org.pentaho.pms.ui.tree.ConnectionsTreeNode;
import org.pentaho.pms.ui.tree.DatabaseMetaTreeNode;
import org.pentaho.pms.ui.tree.LabelTreeNode;
import org.pentaho.pms.ui.tree.PhysicalColumnTreeNode;
import org.pentaho.pms.ui.tree.PhysicalTableTreeNode;
import org.pentaho.pms.ui.tree.RelationshipTreeNode;
import org.pentaho.pms.ui.tree.RelationshipsTreeNode;
import org.pentaho.pms.ui.tree.SchemaMetaTreeNode;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;
import org.pentaho.pms.util.Settings;
import org.pentaho.pms.util.Splash;
import org.pentaho.pms.util.dialog.EnterOptionsDialog;
import org.pentaho.pms.util.dialog.ListSelectionDialog;
import org.pentaho.pms.util.logging.Log4jPMELayout;

import be.ibridge.kettle.core.DBCache;
import be.ibridge.kettle.core.DragAndDropContainer;
import be.ibridge.kettle.core.LastUsedFile;
import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Point;
import be.ibridge.kettle.core.PrintSpool;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.XMLTransfer;
import be.ibridge.kettle.core.database.Database;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.dialog.DatabaseDialog;
import be.ibridge.kettle.core.dialog.DatabaseExplorerDialog;
import be.ibridge.kettle.core.dialog.EnterSelectionDialog;
import be.ibridge.kettle.core.dialog.EnterStringDialog;
import be.ibridge.kettle.core.dialog.EnterTextDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.dialog.SQLEditor;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.list.UniqueArrayList;
import be.ibridge.kettle.core.util.EnvUtil;
import be.ibridge.kettle.core.value.Value;
import be.ibridge.kettle.core.widget.TreeMemory;
import be.ibridge.kettle.job.JobEntryLoader;
import be.ibridge.kettle.trans.StepLoader;

/**
 * Class to edit the metadata domain (Schema Metadata), load/store into the MDR/CWM model
 *
 * @since 16-may-2003
 */
public class MetaEditor {
  private CWM cwm;

  private LogWriter log;

  private Display disp;

  private Shell shell;

  private MetaEditorGraph metaEditorGraph;

  private MetaEditorLog metaEditorLog;

  private MetaEditorConcepts metaEditorConcept;

  private MetaEditorOlap metaEditorOlap;

  private SashForm sashform;

  private CTabFolder tabfolder;

  private SchemaMeta schemaMeta;

  private MQLQuery query;

  private ToolBar tBar;

  private Menu mBar;

  private Listener mainListener;

  private MenuItem mFile;

  private Menu msFile;

  private MenuItem miFileOpen, miFileNew, miFileSave, miFileSaveAs, miFileExport, miPublish, miFileImport, miFileDelete,
      miFilePrint, miFileSep3, miFileQuit;

  private MenuItem miNewDomain, miNewConnection, miNewPTable, miNewBTable, miNewBModel, miNewRel, miNewCat;

  private MenuItem miNewDomainTB, miNewConnectionTB, miNewPTableTB, miNewBTableTB, miNewBModelTB, miNewRelTB,
      miNewCatTB;

  private Listener lsDomainNew, lsConnectionNew, lsPTableNew, lsBTableNew, lsBModelNew, lsRelationNew, lsCategoryNew,
      lsFileOpen, lsFileSave, lsFileSaveAs, lsFileExport, lsPublish, lsFileImport, lsFileDelete, lsFilePrint, lsFileQuit,
      lsEditLocales, lsEditConcepts, lsEditCategories, lsAlignRight, lsAlignLeft, lsAlignTop, lsAlignBottom,
      lsDistribHoriz, lsDistribVert;

  private MenuItem mEdit;

  private ToolItem tiAlignLeft, tiAlignRight, tiAlignTop, tiAlignBottom;

  private Menu msEdit;

  private Menu mPopAD;

  private MenuItem miEditSelectAll, miEditUnselectAll, miEditOptions, miEditRefresh;

  private Listener lsEditSelectAll, lsEditUnselectAll, lsEditOptions, lsEditRefresh;

  private MenuItem mHelp;

  private Menu msHelp;

  private MenuItem miHelpAbout;

  private Listener lsHelpAbout;

  private SelectionAdapter lsEditDef, lsEditMainSel;

  public static final String STRING_CONNECTIONS = Messages.getString("MetaEditor.USER_CONNECTIONS"); //$NON-NLS-1$

  public static final String STRING_BUSINESS_MODELS = Messages.getString("MetaEditor.USER_BUSINESS_MODELS"); //$NON-NLS-1$

  public static final String STRING_BUSINESS_TABLES = Messages.getString("MetaEditor.USER_BUSINESS_TABLES"); //$NON-NLS-1$

  public static final String STRING_RELATIONSHIPS = Messages.getString("MetaEditor.USER_RELATIONSHIPS"); //$NON-NLS-1$

  public static final String STRING_CATEGORIES = Messages.getString("MetaEditor.USER_CATEGORIES"); //$NON-NLS-1$

  public static final String APPLICATION_NAME = Messages.getString("MetaEditor.USER_METADATA_EDITOR"); //$NON-NLS-1$

  private static final String STRING_MAIN_TREE = "MainTree"; //$NON-NLS-1$

  public static final String STRING_CATEGORIES_TREE = "CategoriesTree"; //$NON-NLS-1$

  private TreeViewer treeViewer;

  private SchemaMetaTreeNode mainTreeNode;

  private BusinessModelTreeNode activeModelTreeNode;

  public KeyAdapter defKeys;

  public KeyAdapter modKeys;

  private Props props;

  private MetaEditorLocales metaEditorLocales;

  private MenuItem mTools;

  private Menu msTools;

  private MenuItem miSecurityService, miLocalesEditor, miConceptEditor, miCategoryEditor, miLogging;

  private Listener lsSecurityService;

  private CwmSchemaFactoryInterface cwmSchemaFactory;

  private Menu mainMenu;

  public MetaEditor(LogWriter log) {
    this(log, null);
  }

  public MetaEditor(LogWriter log, Display display) {
    this.log = log;

    if (display != null) {
      disp = display;
    } else {
      disp = new Display();
    }
    shell = new Shell(disp);
    shell.setText(APPLICATION_NAME);
    FormLayout layout = new FormLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    shell.setLayout(layout);

    props = Props.getInstance();

    cwmSchemaFactory = Settings.getCwmSchemaFactory();

    // INIT Data structure
    schemaMeta = new SchemaMeta();
    loadQuery();

    // Load settings in the props
    loadSettings();
    shell.setImage(new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "icon.png"))); //$NON-NLS-1$

    initGlobalKeyBindings();
    initGlobalListeners();
    initToolBar();
    initMainForm();
    initMenu();
    initTree();
    initTabs();

    // In case someone dares to press the [X] in the corner ;-)
    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        e.doit = quitFile();
      }
    });
    int weights[] = props.getSashWeights();
    sashform.setWeights(weights);
    sashform.setVisible(true);

    shell.layout();
    getMainListener().handleEvent(null); // Force everything to match the current state
  }

  private void initGlobalKeyBindings() {
    defKeys = new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        boolean control = (e.stateMask & SWT.CONTROL) != 0;
        boolean alt = (e.stateMask & SWT.ALT) != 0;

        BusinessModel activeModel = schemaMeta.getActiveModel();

        // ESC --> Unselect All steps
        if (e.keyCode == SWT.ESC) {
          if (activeModel != null) {
            activeModel.unselectAll();
            refreshGraph();
          }
          metaEditorGraph.control = false;
        }

        // F5 --> refresh
        if (e.keyCode == SWT.F5) {
          refreshAll();
          metaEditorGraph.control = false;
        }

        // F8 --> generate Mondrian model
        if (e.keyCode == SWT.F8) {
          getMondrianModel();
          metaEditorGraph.control = false;
        }

        // CTRL-A --> Select All steps
        if (e.character == 1 && control && !alt) {
          if (activeModel != null) {
            activeModel.selectAll();
            refreshGraph();
          }
          metaEditorGraph.control = false;
        }
        ;
        // CTRL-E --> Select All steps
        if (e.character == 5 && control && !alt) {
          exportToXMI();
          metaEditorGraph.control = false;
        }
        ;
        // CTRL-I --> Select All steps
        if (e.character == 9 && control && !alt) {
          importFromXMI();
          metaEditorGraph.control = false;
        }
        ;
        // CTRL-N --> new
        if (e.character == 14 && control && !alt) {
          newFile();
          metaEditorGraph.control = false;
        }
        // CTRL-O --> open
        if (e.character == 15 && control && !alt) {
          openFile();
          metaEditorGraph.control = false;
        }
        // CTRL-P --> print
        if (e.character == 16 && control && !alt) {
          printFile();
          metaEditorGraph.control = false;
        }
        // CTRL-S --> save
        if (e.character == 19 && control && !alt) {
          saveFile();
          metaEditorGraph.control = false;
        }
        // CTRL-T --> Test
        if (e.character == 20 && control && !alt) {
          testQR();
          metaEditorGraph.control = false;
        }
      }
    };
    modKeys = new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.keyCode == SWT.SHIFT)
          metaEditorGraph.shift = true;
        if (e.keyCode == SWT.CONTROL)
          metaEditorGraph.control = true;
      }

      public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.SHIFT)
          metaEditorGraph.shift = false;
        if (e.keyCode == SWT.CONTROL)
          metaEditorGraph.control = false;
      }
    };
  }

  private void initGlobalListeners() {
    lsDomainNew = new Listener() {
      public void handleEvent(Event e) {
        newFile();
      }
    };
    lsConnectionNew = new Listener() {
      public void handleEvent(Event e) {
        newConnection();
      }
    };
    lsPTableNew = new Listener() {
      public void handleEvent(Event e) {
        newConnection();
      }
    };
    lsBTableNew = new Listener() {
      public void handleEvent(Event e) {
        newBusinessTable(null);
      }
    };
    lsBModelNew = new Listener() {
      public void handleEvent(Event e) {
        newBusinessModel();
      }
    };
    lsRelationNew = new Listener() {
      public void handleEvent(Event e) {
        newRelationship();
      }
    };
    lsCategoryNew = new Listener() {
      public void handleEvent(Event e) {
        editBusinessCategories();
      }
    };
    lsFileOpen = new Listener() {
      public void handleEvent(Event e) {
        openFile();
      }
    };
    lsFileSave = new Listener() {
      public void handleEvent(Event e) {
        saveFile();
      }
    };
    lsFileSaveAs = new Listener() {
      public void handleEvent(Event e) {
        saveFileAs();
      }
    };
    lsFileExport = new Listener() {
      public void handleEvent(Event e) {
        exportToXMI();
      }
    };
    lsPublish = new Listener() {
      public void handleEvent(Event e) {
        publishXmi();
      }
    };
    lsFileImport = new Listener() {
      public void handleEvent(Event e) {
        importFromXMI();
      }
    };
    lsFileDelete = new Listener() {
      public void handleEvent(Event e) {
        deleteFile();
      }
    };
    lsFilePrint = new Listener() {
      public void handleEvent(Event e) {
        printFile();
      }
    };
    lsFileQuit = new Listener() {
      public void handleEvent(Event e) {
        quitFile();
      }
    };
    lsEditUnselectAll = new Listener() {
      public void handleEvent(Event e) {
        editUnselectAll();
      }
    };
    lsEditSelectAll = new Listener() {
      public void handleEvent(Event e) {
        editSelectAll();
      }
    };
    lsEditOptions = new Listener() {
      public void handleEvent(Event e) {
        editOptions();
      }
    };
    lsEditRefresh = new Listener() {
      public void handleEvent(Event e) {
        refreshAll();
      }
    };
    lsSecurityService = new Listener() {
      public void handleEvent(Event e) {
        editSecurityService();
      }
    };
    lsEditLocales = new Listener() {
      public void handleEvent(Event e) {
        tabfolder.setSelection(2);
      }
    };
    lsEditConcepts = new Listener() {
      public void handleEvent(Event e) {
        tabfolder.setSelection(1);
      }
    };
    lsEditCategories = new Listener() {
      public void handleEvent(Event e) {
        BusinessModel activeModel = schemaMeta.getActiveModel();
        if (activeModel != null) {
          CategoryEditorDialog dialog = new CategoryEditorDialog(shell,activeModel, schemaMeta.getLocales(),
              schemaMeta.getSecurityReference());
          dialog.open();
          if (activeModelTreeNode != null)
            activeModelTreeNode.getBusinessViewRoot().sync();
        }
      }
    };
    lsAlignLeft = new Listener() {
      public void handleEvent(Event e) {
        metaEditorGraph.allignleft();
      }
    };
    lsAlignRight = new Listener() {
      public void handleEvent(Event e) {
        metaEditorGraph.allignright();
      }
    };
    lsAlignTop = new Listener() {
      public void handleEvent(Event e) {
        metaEditorGraph.alligntop();
      }
    };
    lsAlignBottom = new Listener() {
      public void handleEvent(Event e) {
        metaEditorGraph.allignbottom();
      }
    };
    lsDistribHoriz = new Listener() {
      public void handleEvent(Event e) {
        metaEditorGraph.distributehorizontal();
      }
    };
    lsDistribVert = new Listener() {
      public void handleEvent(Event e) {
        metaEditorGraph.distributevertical();
      }
    };

    lsHelpAbout = new Listener() {
      public void handleEvent(Event e) {
        helpAbout();
      }
    };
  }

  private void initMainForm() {
    sashform = new SashForm(shell, SWT.HORIZONTAL);

    FormData fdSash = new FormData();
    fdSash.left = new FormAttachment(0, 0);
    fdSash.top = new FormAttachment(tBar, 0);
    fdSash.bottom = new FormAttachment(100, 0);
    fdSash.right = new FormAttachment(100, 0);
    sashform.setLayoutData(fdSash);
  }

  public void exportToXMI() {
    boolean goAhead = true;

    if (Const.isEmpty(schemaMeta.getDomainName())) {
        MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
        mb.setMessage(Messages.getString("MetaEditor.USER_NO_NAME_CAN_NOT_EXPORT")); //$NON-NLS-1$
        mb.setText(Messages.getString("MetaEditor.USER_SORRY")); //$NON-NLS-1$
        if (mb.open() != SWT.YES) {
          goAhead = false;
      }
    }

    if (schemaMeta.hasChanged()) {
      MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
      mb.setMessage(Messages.getString("MetaEditor.USER_SAVE_DOMAIN")); //$NON-NLS-1$
      mb.setText(Messages.getString("MetaEditor.USER_CONTINUE")); //$NON-NLS-1$
      if (mb.open() == SWT.YES) {
        goAhead = saveFile();
      } else {
        goAhead = false;
      }
    }
    if (goAhead) {
      FileDialog dialog = new FileDialog(shell, SWT.SAVE);
      dialog.setFilterExtensions(new String[] { "*.xmi", "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      dialog
          .setFilterNames(new String[] {
              Messages.getString("MetaEditor.USER_XMI_FILES"), Messages.getString("MetaEditor.USER_XML_FILES"), Messages.getString("MetaEditor.USER_ALL_FILES") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      String filename = dialog.open();
      if (filename != null) {
        if (!filename.toLowerCase().endsWith(".xmi") && !filename.toLowerCase().endsWith(".xml")) //$NON-NLS-1$ //$NON-NLS-2$
        {
          filename += ".xmi"; //$NON-NLS-1$
        }

        // Get back the result of the last save operation...
        CWM cwmInstance = CWM.getInstance(schemaMeta.getDomainName());

        if (cwmInstance != null) {
          try {
            cwmInstance.exportToXMI(filename);
          } catch (Exception e) {
            new ErrorDialog(
                shell,
                Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_EXPORTING_XMI"), e); //$NON-NLS-1$ //$NON-NLS-2$
          }
        }
      }
    }
  }
  
  public void publishXmi() {
    boolean goAhead = true;
    if (schemaMeta.hasChanged()) {
      MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
      mb.setMessage(Messages.getString("MetaEditor.USER_SAVE_DOMAIN")); //$NON-NLS-1$
      mb.setText(Messages.getString("MetaEditor.USER_CONTINUE")); //$NON-NLS-1$
      if (mb.open() == SWT.YES) {
        goAhead = saveFile();
      } else {
        goAhead = false;
      }
    }
    if (goAhead) {
      PublishDialog publishDialog = new PublishDialog(shell, schemaMeta);
      publishDialog.open();
    }
  }
  
  public void importFromXMI() {
    if (showChangedWarning()) {
      FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
      fileDialog.setFilterExtensions(new String[] { "*.xmi", "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      fileDialog
          .setFilterNames(new String[] {
              Messages.getString("MetaEditor.USER_XMI_FILES"), Messages.getString("MetaEditor.USER_XML_FILES"), Messages.getString("MetaEditor.USER_ALL_FILES") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      String filename = fileDialog.open();
      if (filename != null) {
        try {
          // Ask for a new domain to import into...
          //
          EnterStringDialog stringDialog = new EnterStringDialog(
              shell,
              "", Messages.getString("MetaEditor.USER_TITLE_SAVE_DOMAIN"), Messages.getString("MetaEditor.USER_ENTER_DOMAIN_NAME")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          String domainName = stringDialog.open();
          if (domainName != null) {
            int id = SWT.YES;
            if (CWM.exists(domainName)) {
              MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
              mb.setMessage(Messages.getString("MetaEditor.USER_DOMAIN_EXISTS_OVERWRITE")); //$NON-NLS-1$
              mb.setText(Messages.getString("MetaEditor.USER_TITLE_DOMAIN_EXISTS")); //$NON-NLS-1$
              id = mb.open();
            }
            if (id == SWT.YES) {
              CWM delCwm = CWM.getInstance(domainName);
              delCwm.removeDomain();
            } else {
              return; // no selected.
            }

            // Now create a new domain...
            CWM cwmInstance = CWM.getInstance(domainName);

            // import it all...
            cwmInstance.importFromXMI(filename);

            // convert to a schema
            schemaMeta = cwmSchemaFactory.getSchemaMeta(cwmInstance);

            // Here, we are getting a whole new model, so rebuild the whole tree
            refreshTree();
          }
        } catch (Exception e) {
          new ErrorDialog(
              shell,
              Messages.getString("MetaEditor.USER_TITLE_ERROR_SAVE_DOMAIN"), Messages.getString("MetaEditor.USER_ERROR_LOADING_DOMAIN"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }

  }

  public void open() {
    // Set the shell size, based upon previous time...
    WindowProperty winprop = props.getScreen(shell.getText());
    if (winprop != null)
      winprop.setShell(shell);
    else
      shell.pack();

    shell.open();

    // Perhaps the transformation contains elements at startup?
    if (schemaMeta.nrTables() > 0 || schemaMeta.nrDatabases() > 0) {
      refreshTree();
      refreshAll(); // Do a complete refresh then...
    }
  }

  public boolean readAndDispatch() {
    return disp.readAndDispatch();
  }

  public void dispose() {
    try {
      CWM.quitAndSync();
      disp.dispose();
    } catch (Exception e) {
      new ErrorDialog(
          shell,
          Messages.getString("MetaEditor.USER_TITLE_ERROR_STOPPING_REPOSITORY"), Messages.getString("MetaEditor.USER_ERROR_STOPPING_REPOSITORY"), e); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public boolean isDisposed() {
    return disp.isDisposed();
  }

  public void sleep() {
    disp.sleep();
  }

  public void initMenu() {
    mBar = new Menu(shell, SWT.BAR);
    shell.setMenuBar(mBar);

    // main File menu...
    mFile = new MenuItem(mBar, SWT.CASCADE);
    mFile.setText(Messages.getString("MetaEditor.USER_FILE")); //$NON-NLS-1$
    msFile = new Menu(shell, SWT.DROP_DOWN);

    mFile.setMenu(msFile);

    miFileNew = new MenuItem(msFile, SWT.CASCADE);
    miFileNew.setText(Messages.getString("MetaEditor.USER_NEW")); //$NON-NLS-1$

    Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
    miFileNew.setMenu(fileMenu);

    miNewDomain = new MenuItem(fileMenu, SWT.CASCADE);
    miNewDomain.setText(Messages.getString("MetaEditor.USER_NEW_DOMAIN_MENU")); //$NON-NLS-1$
    miNewDomain.addListener(SWT.Selection, lsDomainNew);

    new MenuItem(fileMenu, SWT.SEPARATOR);
    miNewConnection = new MenuItem(fileMenu, SWT.CASCADE);
    miNewConnection.setText(Messages.getString("MetaEditor.USER_NEW_CONNECTION_MENU")); //$NON-NLS-1$
    miNewConnection.addListener(SWT.Selection, lsConnectionNew);

    miNewPTable = new MenuItem(fileMenu, SWT.CASCADE);
    miNewPTable.setText(Messages.getString("MetaEditor.USER_NEW_PHYSICAL_TABLE_MENU")); //$NON-NLS-1$
    miNewPTable.addListener(SWT.Selection, lsPTableNew);

    new MenuItem(fileMenu, SWT.SEPARATOR);
    miNewBTable = new MenuItem(fileMenu, SWT.CASCADE);
    miNewBTable.setText(Messages.getString("MetaEditor.USER_NEW_BUSINESS_TABLE_MENU")); //$NON-NLS-1$
    miNewBTable.addListener(SWT.Selection, lsBTableNew);

    miNewBModel = new MenuItem(fileMenu, SWT.CASCADE);
    miNewBModel.setText(Messages.getString("MetaEditor.USER_NEW_BUSINESS_MODEL_MENU"));//$NON-NLS-1$
    miNewBModel.addListener(SWT.Selection, lsBModelNew);

    miNewRel = new MenuItem(fileMenu, SWT.CASCADE);
    miNewRel.setText(Messages.getString("MetaEditor.USER_NEW_RELATIONSHIP_MENU")); //$NON-NLS-1$
    miNewRel.addListener(SWT.Selection, lsRelationNew);

    miNewCat = new MenuItem(fileMenu, SWT.CASCADE);
    miNewCat.setText(Messages.getString("MetaEditor.USER_NEW_CATEGORY_MENU")); //$NON-NLS-1$
    miNewCat.addListener(SWT.Selection, lsCategoryNew);

    miFileOpen = new MenuItem(msFile, SWT.CASCADE);
    miFileOpen.setText(Messages.getString("MetaEditor.USER_OPEN")); //$NON-NLS-1$
    miFileOpen.addListener(SWT.Selection, lsFileOpen);

    miFileSave = new MenuItem(msFile, SWT.CASCADE);
    miFileSave.setText(Messages.getString("MetaEditor.USER_SAVE")); //$NON-NLS-1$
    miFileSave.addListener(SWT.Selection, lsFileSave);

    miFileSaveAs = new MenuItem(msFile, SWT.CASCADE);
    miFileSaveAs.setText(Messages.getString("MetaEditor.USER_SAVE_AS")); //$NON-NLS-1$
    miFileSaveAs.addListener(SWT.Selection, lsFileSaveAs);

    new MenuItem(msFile, SWT.SEPARATOR);
    miFileImport = new MenuItem(msFile, SWT.CASCADE);
    miFileImport.setText(Messages.getString("MetaEditor.USER_IMPORT")); //$NON-NLS-1$
    miFileImport.addListener(SWT.Selection, lsFileImport);

    miFileExport = new MenuItem(msFile, SWT.CASCADE);
    miFileExport.setText(Messages.getString("MetaEditor.USER_EXPORT")); //$NON-NLS-1$
    miFileExport.addListener(SWT.Selection, lsFileExport);
    
    miPublish = new MenuItem(msFile, SWT.CASCADE);
    miPublish.setText(Messages.getString("MetaEditor.PUBLISH")); //$NON-NLS-1$
    miPublish.addListener(SWT.Selection, lsPublish);

    new MenuItem(msFile, SWT.SEPARATOR);
    miFileDelete = new MenuItem(msFile, SWT.CASCADE);
    miFileDelete.setText(Messages.getString("MetaEditor.USER_DELETE_DOMAIN")); //$NON-NLS-1$
    miFileDelete.addListener(SWT.Selection, lsFileDelete);

    new MenuItem(msFile, SWT.SEPARATOR);
    miFilePrint = new MenuItem(msFile, SWT.CASCADE);
    miFilePrint.setText(Messages.getString("MetaEditor.USER_PRINT")); //$NON-NLS-1$
    miFilePrint.addListener(SWT.Selection, lsFilePrint);

    new MenuItem(msFile, SWT.SEPARATOR);
    miFileQuit = new MenuItem(msFile, SWT.CASCADE);
    miFileQuit.setText(Messages.getString("MetaEditor.USER_QUIT")); //$NON-NLS-1$
    miFileQuit.addListener(SWT.Selection, lsFileQuit);

    miFileSep3 = new MenuItem(msFile, SWT.SEPARATOR);
    addMenuLast();

    // main Edit menu...
    mEdit = new MenuItem(mBar, SWT.CASCADE);
    mEdit.setText(Messages.getString("MetaEditor.USER_EDIT")); //$NON-NLS-1$
    msEdit = new Menu(shell, SWT.DROP_DOWN);
    mEdit.setMenu(msEdit);

    miEditOptions = new MenuItem(msEdit, SWT.CASCADE);
    miEditOptions.setText(Messages.getString("MetaEditor.USER_EDIT_PROPS")); //$NON-NLS-1$
    miEditOptions.addListener(SWT.Selection, lsEditOptions);

    new MenuItem(msEdit, SWT.SEPARATOR);
    miEditUnselectAll = new MenuItem(msEdit, SWT.CASCADE);
    miEditUnselectAll.setText(Messages.getString("MetaEditor.USER_CLEAR_SELECTION")); //$NON-NLS-1$
    miEditUnselectAll.addListener(SWT.Selection, lsEditUnselectAll);

    miEditSelectAll = new MenuItem(msEdit, SWT.CASCADE);
    miEditSelectAll.setText(Messages.getString("MetaEditor.USER_SELECT_ALL_STEPS")); //$NON-NLS-1$
    miEditSelectAll.addListener(SWT.Selection, lsEditSelectAll);

    new MenuItem(msEdit, SWT.SEPARATOR);
    miEditRefresh = new MenuItem(msEdit, SWT.CASCADE);
    miEditRefresh.setText(Messages.getString("MetaEditor.USER_REFRESH")); //$NON-NLS-1$
    miEditRefresh.addListener(SWT.Selection, lsEditRefresh);

    // Tools
    mTools = new MenuItem(mBar, SWT.CASCADE);
    mTools.setText(Messages.getString("MetaEditor.USER_TOOLS")); //$NON-NLS-1$
    msTools = new Menu(shell, SWT.DROP_DOWN);
    mTools.setMenu(msTools);

    miSecurityService = new MenuItem(msTools, SWT.CASCADE);
    miSecurityService.setText(Messages.getString("MetaEditor.USER_CONFIGURE_SECURITY_SERVICE")); //$NON-NLS-1$
    miSecurityService.addListener(SWT.Selection, lsSecurityService);

    new MenuItem(msTools, SWT.SEPARATOR);
    miLocalesEditor = new MenuItem(msTools, SWT.CASCADE);
    miLocalesEditor.setText(Messages.getString("MetaEditor.USER_CONFIGURE_LOCALES"));//$NON-NLS-1$
    miLocalesEditor.addListener(SWT.Selection, lsEditLocales);

    miConceptEditor = new MenuItem(msTools, SWT.CASCADE);
    miConceptEditor.setText(Messages.getString("MetaEditor.USER_CONFIGURE_CONCEPTS"));//$NON-NLS-1$
    miConceptEditor.addListener(SWT.Selection, lsEditConcepts);

    miCategoryEditor = new MenuItem(msTools, SWT.CASCADE);
    miCategoryEditor.setText(Messages.getString("MetaEditor.USER_CONFIGURE_CATEGORYS"));//$NON-NLS-1$
    miCategoryEditor.addListener(SWT.Selection, lsEditCategories);

    new MenuItem(msTools, SWT.SEPARATOR);
    miLogging = new MenuItem(msTools, SWT.CASCADE);
    miLogging.setText(Messages.getString("MetaEditor.USER_CONFIGURE_LOGGING"));//$NON-NLS-1$

    new MenuItem(msTools, SWT.SEPARATOR);
    MenuItem miPopAD = new MenuItem(msTools, SWT.CASCADE);
    miPopAD.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_DISTRIBUTE")); //$NON-NLS-1$
    mPopAD = new Menu(miPopAD);

    MenuItem miPopALeft = new MenuItem(mPopAD, SWT.CASCADE);
    miPopALeft.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_LEFT")); //$NON-NLS-1$
    miPopALeft.addListener(SWT.Selection, lsAlignLeft);

    MenuItem miPopARight = new MenuItem(mPopAD, SWT.CASCADE);
    miPopARight.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_RIGHT")); //$NON-NLS-1$
    miPopARight.addListener(SWT.Selection, lsAlignRight);

    MenuItem miPopATop = new MenuItem(mPopAD, SWT.CASCADE);
    miPopATop.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_TOP")); //$NON-NLS-1$
    miPopATop.addListener(SWT.Selection, lsAlignTop);

    MenuItem miPopABottom = new MenuItem(mPopAD, SWT.CASCADE);
    miPopABottom.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_BOTTOM")); //$NON-NLS-1$
    miPopABottom.addListener(SWT.Selection, lsAlignBottom);

    new MenuItem(mPopAD, SWT.SEPARATOR);
    MenuItem miPopDHoriz = new MenuItem(mPopAD, SWT.CASCADE);
    miPopDHoriz.setText(Messages.getString("MetaEditorGraph.USER_DISTRIBUTE_HORIZ")); //$NON-NLS-1$
    miPopDHoriz.addListener(SWT.Selection, lsDistribHoriz);

    MenuItem miPopDVertic = new MenuItem(mPopAD, SWT.CASCADE);
    miPopDVertic.setText(Messages.getString("MetaEditorGraph.USER_DISTRIBUTE_VERT")); //$NON-NLS-1$
    miPopDVertic.addListener(SWT.Selection, lsDistribVert);

    new MenuItem(mPopAD, SWT.SEPARATOR);
    MenuItem miPopSSnap = new MenuItem(mPopAD, SWT.CASCADE);
    miPopSSnap.setText(Messages.getString("MetaEditorGraph.USER_SNAP_TO_GRID", Integer.toString(Const.GRID_SIZE))); //$NON-NLS-1$
    miPopAD.setMenu(mPopAD);

    miPopSSnap.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        metaEditorGraph.snaptogrid(Const.GRID_SIZE);
      }
    });

    miLogging.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        tabfolder.setSelection(3);
      }
    });

    // main Help menu...
    mHelp = new MenuItem(mBar, SWT.CASCADE);
    mHelp.setText(Messages.getString("MetaEditor.USER_HELP")); //$NON-NLS-1$
    msHelp = new Menu(shell, SWT.DROP_DOWN);

    mHelp.setMenu(msHelp);
    miHelpAbout = new MenuItem(msHelp, SWT.CASCADE);
    miHelpAbout.setText(Messages.getString("MetaEditor.USER_ABOUT")); //$NON-NLS-1$
    miHelpAbout.addListener(SWT.Selection, lsHelpAbout);
  }

  /**
   * @return
   */
  private Listener getMainListener() {
    if (mainListener == null) {
      mainListener = new Listener() {
        public void handleEvent(Event e) {
          BusinessModel activeModel = schemaMeta.getActiveModel();
          boolean hasActiveModel = false;
          int nrSelected = 0;
          if (activeModel != null) {
            hasActiveModel = true;
            nrSelected = activeModel.nrSelected();
          }
          // Enable/disable menus that rely on having an active model
          miNewBTable.setEnabled(hasActiveModel);

          // Enable/disable menus that rely on having more than 1 graph item selected
          mPopAD.setEnabled(nrSelected > 1);
          tiAlignLeft.setEnabled(nrSelected > 1);
          tiAlignRight.setEnabled(nrSelected > 1);
          tiAlignTop.setEnabled(nrSelected > 1);
          tiAlignBottom.setEnabled(nrSelected > 1);
        }
      };
    }
    return mainListener;
  }

  private void addMenuLast() {
    int idx = msFile.indexOf(miFileSep3);
    int max = msFile.getItemCount();

    // Remove everything until end...
    for (int i = max - 1; i > idx; i--) {
      MenuItem mi = msFile.getItem(i);
      mi.dispose();
    }

    // Previously loaded files...
    String lf[] = props.getLastFiles();

    for (int i = 0; i < lf.length; i++) {
      MenuItem miFileLast = new MenuItem(msFile, SWT.CASCADE);
      char chr = (char) ('1' + i);
      int accel = SWT.CTRL | chr;
      miFileLast.setText("&" + chr + "  " + lf[i] + " \tCTRL-" + chr); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      miFileLast.setAccelerator(accel);
      final String fn = lf[i];

      Listener lsFileLast = new Listener() {
        public void handleEvent(Event e) {
          if (showChangedWarning()) {
            if (readData(fn)) {
              schemaMeta.clearChanged();
              setDomainName(fn);
              metaEditorGraph.control = false;
            } else {
              MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
              mb.setMessage(Messages.getString("MetaEditor.USER_ERROR_OPENING_DOMAIN", fn)); //$NON-NLS-1$
              mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
              mb.open();
            }
          }
        }
      };
      miFileLast.addListener(SWT.Selection, lsFileLast);
    }
  }

  private void initToolBar() {
    // First get the toolbar images
    // Make sure that any images we get are disposed of down below in the DisposeListener
    final Image imFileNew = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "new.png")); //$NON-NLS-1$
    final Image imFileOpen = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "open.png")); //$NON-NLS-1$
    final Image imFileSave = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "save.png")); //$NON-NLS-1$
    final Image imFileSaveAs = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "saveas.png")); //$NON-NLS-1$
    final Image imFilePrint = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "print.png")); //$NON-NLS-1$
    final Image imSQL = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "SQLbutton.png")); //$NON-NLS-1$
    final Image imConceptEdit = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "concept-editor.png")); //$NON-NLS-1$
    final Image imLocaleEdit = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "locale-editor.png")); //$NON-NLS-1$
    final Image imCategoryEdit = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "category-editor.png")); //$NON-NLS-1$
    final Image imPropertyEdit = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "property-editor.png")); //$NON-NLS-1$
    final Image imAlignLeft = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "align-left.png")); //$NON-NLS-1$
    final Image imAlignRight = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "align-right.png")); //$NON-NLS-1$
    final Image imAlignTop = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "align-top.png")); //$NON-NLS-1$
    final Image imAlignBottom = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "align-bottom.png")); //$NON-NLS-1$
    
    // Can't seem to get the transparency correct for this image!
    ImageData idSQL = imSQL.getImageData();
    int sqlPixel = idSQL.palette.getPixel(new RGB(255, 255, 255));
    idSQL.transparentPixel = sqlPixel;
    final Image imSQL2 = new Image(disp, idSQL);

    tBar = new ToolBar(shell, SWT.HORIZONTAL | SWT.FLAT);
    tBar.addListener(SWT.MouseEnter, getMainListener());

    final Menu fileMenus = new Menu(shell, SWT.NONE);

    // Add the new file toolbar items dropdowns
    miNewDomainTB = new MenuItem(fileMenus, SWT.CASCADE);
    miNewDomainTB.setText(Messages.getString("MetaEditor.USER_NEW_DOMAIN_MENU")); //$NON-NLS-1$
    miNewDomainTB.addListener(SWT.Selection, lsDomainNew);

    new MenuItem(fileMenus, SWT.SEPARATOR);
    miNewConnectionTB = new MenuItem(fileMenus, SWT.CASCADE);
    miNewConnectionTB.setText(Messages.getString("MetaEditor.USER_NEW_CONNECTION_MENU")); //$NON-NLS-1$
    miNewConnectionTB.addListener(SWT.Selection, lsConnectionNew);

    miNewPTableTB = new MenuItem(fileMenus, SWT.CASCADE);
    miNewPTableTB.setText(Messages.getString("MetaEditor.USER_NEW_PHYSICAL_TABLE_MENU")); //$NON-NLS-1$
    miNewPTableTB.addListener(SWT.Selection, lsPTableNew);

    new MenuItem(fileMenus, SWT.SEPARATOR);
    miNewBTableTB = new MenuItem(fileMenus, SWT.CASCADE);
    miNewBTableTB.setText(Messages.getString("MetaEditor.USER_NEW_BUSINESS_TABLE_MENU")); //$NON-NLS-1$
    miNewBTableTB.addListener(SWT.Selection, lsBTableNew);

    miNewBModelTB = new MenuItem(fileMenus, SWT.CASCADE);
    miNewBModelTB.setText(Messages.getString("MetaEditor.USER_NEW_BUSINESS_MODEL_MENU"));//$NON-NLS-1$
    miNewBModelTB.addListener(SWT.Selection, lsBModelNew);

    miNewRelTB = new MenuItem(fileMenus, SWT.CASCADE);
    miNewRelTB.setText(Messages.getString("MetaEditor.USER_NEW_RELATIONSHIP_MENU")); //$NON-NLS-1$
    miNewRelTB.addListener(SWT.Selection, lsRelationNew);

    miNewCatTB = new MenuItem(fileMenus, SWT.CASCADE);
    miNewCatTB.setText(Messages.getString("MetaEditor.USER_NEW_CATEGORY_MENU")); //$NON-NLS-1$
    miNewCatTB.addListener(SWT.Selection, lsCategoryNew);

    final ToolItem tiFileNew = new ToolItem(tBar, SWT.DROP_DOWN);

    tiFileNew.setImage(imFileNew);
    tiFileNew.setToolTipText(Messages.getString("MetaEditorUSER_NEW_FILE_CLEAR_SETTINGS")); //$NON-NLS-1$
    // Handles creating a drop down on top of the button if the user clicks on the drop down arrow
    tiFileNew.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (e.detail == SWT.ARROW) {
          Point pt = new Point(shell.getLocation().x, shell.getLocation().y + tiFileNew.getBounds().height);
          fileMenus.setLocation(pt.x, pt.y);
          fileMenus.setVisible(true);
        } else {
          newFile();
        }
      }
    });

    final ToolItem tiFileOpen = new ToolItem(tBar, SWT.PUSH);
    tiFileOpen.setImage(imFileOpen);
    tiFileOpen.setToolTipText(Messages.getString("MetaEditor.USER_OPEN_FILE")); //$NON-NLS-1$
    tiFileOpen.addListener(SWT.Selection, lsFileOpen);

    final ToolItem tiFileSave = new ToolItem(tBar, SWT.PUSH);
    tiFileSave.setImage(imFileSave);
    tiFileSave.setToolTipText(Messages.getString("MetaEditor.USER_SAVE_FILE")); //$NON-NLS-1$
    tiFileSave.addListener(SWT.Selection, lsFileSave);

    final ToolItem tiFileSaveAs = new ToolItem(tBar, SWT.PUSH);
    tiFileSaveAs.setImage(imFileSaveAs);
    tiFileSaveAs.setToolTipText(Messages.getString("MetaEditor.USER_SAVE_FILE_NEW_NAME")); //$NON-NLS-1$
    tiFileSaveAs.addListener(SWT.Selection, lsFileSaveAs);

    new ToolItem(tBar, SWT.SEPARATOR);
    final ToolItem tiFilePrint = new ToolItem(tBar, SWT.PUSH);
    tiFilePrint.setImage(imFilePrint);
    tiFilePrint.setToolTipText(Messages.getString("MetaEditor.USER_PRINT_TEXT")); //$NON-NLS-1$
    tiFilePrint.addListener(SWT.Selection, lsFilePrint);

    new ToolItem(tBar, SWT.SEPARATOR);
    final ToolItem tiSQL = new ToolItem(tBar, SWT.PUSH);
    tiSQL.setImage(imSQL2);
    tiSQL.setToolTipText(Messages.getString("MetaEditor.USER_TEST_Q_AND_R")); //$NON-NLS-1$
    tiSQL.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        testQR();
      }
    });

    final ToolItem tiConceptEdit = new ToolItem(tBar, SWT.PUSH);
    tiConceptEdit.setImage(imConceptEdit);
    tiConceptEdit.addListener(SWT.Selection, lsEditConcepts);

    final ToolItem tiLocaleEdit = new ToolItem(tBar, SWT.PUSH);
    tiLocaleEdit.setImage(imLocaleEdit);
    tiLocaleEdit.addListener(SWT.Selection, lsEditLocales);

    final ToolItem tiCategoryEdit = new ToolItem(tBar, SWT.PUSH);
    tiCategoryEdit.setImage(imCategoryEdit);
    tiCategoryEdit.addListener(SWT.Selection, lsEditCategories);

    new ToolItem(tBar, SWT.SEPARATOR);
    final ToolItem tiProperties = new ToolItem(tBar, SWT.PUSH);
    tiProperties.setImage(imPropertyEdit);
    tiProperties.addListener(SWT.Selection, lsEditOptions);

    new ToolItem(tBar, SWT.SEPARATOR);
    tiAlignLeft = new ToolItem(tBar, SWT.PUSH);
    tiAlignLeft.setImage(imAlignLeft);
    tiAlignLeft.addListener(SWT.Selection, lsAlignLeft);

    tiAlignRight = new ToolItem(tBar, SWT.PUSH);
    tiAlignRight.setImage(imAlignRight);
    tiAlignRight.addListener(SWT.Selection, lsAlignRight);

    tiAlignTop = new ToolItem(tBar, SWT.PUSH);
    tiAlignTop.setImage(imAlignTop);
    tiAlignTop.addListener(SWT.Selection, lsAlignTop);

    tiAlignBottom = new ToolItem(tBar, SWT.PUSH);
    tiAlignBottom.setImage(imAlignBottom);
    tiAlignBottom.addListener(SWT.Selection, lsAlignBottom);

    tBar.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        imFileNew.dispose();
        imFileOpen.dispose();
        imFileSave.dispose();
        imFileSaveAs.dispose();
        imFilePrint.dispose();
        imSQL.dispose();
        imSQL2.dispose();
        imConceptEdit.dispose();
        imLocaleEdit.dispose();
        imCategoryEdit.dispose();
        imPropertyEdit.dispose();
        imAlignLeft.dispose();
        imAlignRight.dispose();
        imAlignTop.dispose();
        imAlignBottom.dispose();
      }
    });

    tBar.addKeyListener(defKeys);
    tBar.addKeyListener(modKeys);
    tBar.pack();
    FormData fdBar = new FormData();
    fdBar.left = new FormAttachment(0, 0);
    fdBar.top = new FormAttachment(0, 0);
    tBar.setLayoutData(fdBar);
  }

  private void initTree() {
    SashForm leftsplit = new SashForm(sashform, SWT.VERTICAL);
    leftsplit.setLayout(new FillLayout());

    // Main: the top left tree containing connections, physical tables, business models, etc.
    Composite compMain = new Composite(leftsplit, SWT.NONE);
    compMain.setLayout(new FillLayout());

    // Now set up the main tree (top left part of the screen)
    int treeFlags = SWT.BORDER;
    if (Const.isOSX()) {
      treeFlags |= SWT.SINGLE;
    } else {
      treeFlags |= SWT.MULTI;
    }
    treeViewer = new TreeViewer(compMain, treeFlags);
    treeViewer.setContentProvider(new TreeContentProvider());
    treeViewer.setLabelProvider(new ConceptLabelProvider());
    mainTreeNode = new SchemaMetaTreeNode(null, schemaMeta);
    mainTreeNode.addTreeNodeChangeListener((ITreeNodeChangedListener) treeViewer.getContentProvider());

    treeViewer.getTree().setHeaderVisible(true);

    // Show the concept in an extra column next to the tree
    TreeColumn mainObject = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
    mainObject.setText(""); //$NON-NLS-1$
    mainObject.setWidth(200);

    TreeColumn mainConcept = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
    mainConcept.setText(Messages.getString("MetaEditor.USER_PARENT_CONCEPT")); //$NON-NLS-1$
    mainConcept.setWidth(200);

    treeViewer.getTree().setBackground(GUIResource.getInstance().getColorBackground());

    // Default selection (double-click, enter)
    lsEditDef = new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent e) {
        doubleClickedMain();
      }
    };
    treeViewer.getTree().addSelectionListener(lsEditDef); // double click somewhere in the tree...

    // Normal selection: right click
    lsEditMainSel = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        setMenuMain(e);
      }
    };
    treeViewer.getTree().addSelectionListener(lsEditMainSel);

    // Normal selection: left click to select business model
    SelectionListener lsSelBusinessModel = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        setActiveBusinessModel(e);
      }
    };
    treeViewer.getTree().addSelectionListener(lsSelBusinessModel);

    addDragSourceToTree(treeViewer.getTree());
    addDropTargetToTree(treeViewer.getTree());

    // Add tree memories to the trees.
    TreeMemory.addTreeListener(treeViewer.getTree(), STRING_MAIN_TREE);

    // Keyboard shortcuts!
    treeViewer.getTree().addKeyListener(defKeys);
    treeViewer.getTree().addKeyListener(modKeys);
  }

  private void addDropTargetToTree(final Tree tree) {
    // Drag & Drop for tables etc.
    Transfer[] ttypes = new Transfer[] { XMLTransfer.getInstance() };
    DropTarget ddTarget = new DropTarget(tree, DND.DROP_MOVE);
    ddTarget.setTransfer(ttypes);
    ddTarget.addDropListener(new DropTargetListener() {
      public void dragEnter(DropTargetEvent event) {
      }

      public void dragLeave(DropTargetEvent event) {
      }

      public void dragOperationChanged(DropTargetEvent event) {
      }

      public void dragOver(DropTargetEvent event) {
      }

      public void drop(DropTargetEvent event) {
        BusinessModel activeModel = schemaMeta.getActiveModel();
        String activeLocale = schemaMeta.getActiveLocale();

        // no data to copy, indicate failure in event.detail
        if (event.data == null || activeModel == null) {
          event.detail = DND.DROP_NONE;
          return;
        }

        try {
          //
          // Where exactly did we drop in the tree?
          TreeItem treeItem = (TreeItem) event.item;
          ConceptTreeNode node = (ConceptTreeNode)treeItem.getData();
          
          // Prevent the user from dropping nodes from a different model
          if (activeModelTreeNode.findNode(node.getDomainObject()) == null){
            MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
            mb.setMessage(Messages.getString("MetaEditor.USER_ERROR_SHARING_ACROSS_MODELS")); //$NON-NLS-1$
            mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
            mb.open();
            return;
          }

          // Retrieve the category that the drop was aimed at.
          BusinessCategory parentCategory = null;
          if (treeItem.getData() instanceof CategoryTreeNode){
            parentCategory = ((CategoryTreeNode)treeItem.getData()).getCategory();
          }else{
            parentCategory = activeModel.getRootCategory();
          }
            
          // We expect a Drag and Drop container... (encased in XML & Base64)
          //
          DragAndDropContainer container = (DragAndDropContainer) event.data;

          // Block sub-categories & columns in the root for now, until Ad-hoc & MDR follow
          //
          if ((container.getType() == DragAndDropContainer.TYPE_BUSINESS_TABLE && !parentCategory.isRootCategory())
              || (container.getType() == DragAndDropContainer.TYPE_BUSINESS_COLUMN && parentCategory.isRootCategory())) {
            MessageBox mb = new MessageBox(shell, SWT.CLOSE | SWT.ICON_INFORMATION);
            mb.setMessage(Messages.getString("MetaEditor.USER_CATEGORY_COLUMN_SUPPORT")); //$NON-NLS-1$
            mb.setText(Messages.getString("MetaEditor.USER_SORRY")); //$NON-NLS-1$
            mb.open();
            return;
          }

          switch (container.getType()) {
            //
            // Drag business table in categories: make business table name a new category
            //
            case DragAndDropContainer.TYPE_BUSINESS_TABLE: {
              BusinessTable businessTable = activeModel.findBusinessTable(container.getData()); // search by
              // ID!
              if (businessTable != null) {
                // Create a new category
                //
                BusinessCategory businessCategory = new BusinessCategory();

                // The id is the table name, prefixes etc.
                String id = Settings.getBusinessCategoryIDPrefix() + businessTable.getTargetTable();
                int catNr = 1;
                String newId = id;
                while (activeModel.getRootCategory().findBusinessCategory(newId) != null) {
                  catNr++;
                  newId = id + "_" + catNr; //$NON-NLS-1$
                }
                if (Settings.isAnIdUppercase())
                  newId = newId.toUpperCase();
                businessCategory.setId(newId);

                // The name is the same as the table...
                String categoryName = businessTable.getDisplayName(activeLocale);
                catNr = 1;
                while (activeModel.getRootCategory().findBusinessCategory(activeLocale, categoryName) != null) {
                  catNr++;
                  categoryName = businessTable.getDisplayName(activeLocale) + " " + catNr; //$NON-NLS-1$
                }
                businessCategory.getConcept().setName(activeLocale, categoryName);

                // add the business columns to the category
                //
                for (int i = businessTable.nrBusinessColumns() - 1; i >= 0; i--) {
                  businessCategory.addBusinessColumn(businessTable.getBusinessColumn(i));
                }

                // Add the category to the business model or category
                //
                parentCategory.addBusinessCategory(businessCategory);
                activeModelTreeNode.getBusinessViewRoot().addDomainChild(businessCategory);

                // Done!
                //
                refreshAll();
              }
            }
              break;
            case DragAndDropContainer.TYPE_BUSINESS_COLUMN: {
              String columnID = container.getData();
              BusinessColumn businessColumn = activeModel.findBusinessColumn(columnID);
              if (businessColumn != null) {

              // Make sure that we are not trying to add a physical table from a 
              // different connection than the active model's connection
                if (!activeModel.verify(businessColumn.getPhysicalColumn())){
                  MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                  mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
                  mb
                      .setMessage(Messages.getString("MetaEditor.USER_ERROR_CANNOT_USE_COLUMN", //$NON-NLS-1$
                          businessColumn.getName(schemaMeta.getActiveLocale()), 
                          activeModel.getDisplayName(schemaMeta.getActiveLocale()),
                          activeModel.getConnection().getName()));
                  mb.open();
                  return;
                }

                BusinessColumn existing = activeModel.getRootCategory().findBusinessColumn(columnID); // search
                // by
                // ID
                if (existing != null && businessColumn.equals(existing)) {
                  MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_WARNING);
                  mb.setMessage(Messages.getString("MetaEditor.USER_BUSINESS_COLUMN_EXISTS")); //$NON-NLS-1$
                  mb.setText(Messages.getString("MetaEditor.USER_WARNING")); //$NON-NLS-1$
                  int answer = mb.open();
                  if (answer == SWT.NO)
                    return;
                }

                // Add the column to the parentCategory
                parentCategory.addBusinessColumn(businessColumn);
                synchronize(parentCategory);
                refreshAll();
              }
            }
              break;

            //
            // Nothing we can use: give an error!
            //
            default: {
              MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
              mb.setMessage(Messages.getString("MetaEditor.USER_CANT_PUT_IN_CATEGORIES_TREE", container.getTypeCode())); //$NON-NLS-1$
              mb.setText(Messages.getString("MetaEditor.USER_SORRY")); //$NON-NLS-1$
              mb.open();
              return;
            }
          }
        } catch (Exception e) {
          new ErrorDialog(shell,
              Messages.getString("MetaEditor.USER_TITLE_ERROR_DND"), Messages.getString("MetaEditor.USER_ERROR_DND"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

      public void dropAccept(DropTargetEvent event) {
      }
    });

  }

  public void setActiveBusinessModel(SelectionEvent e) {
    ITreeNode dataNode = (ITreeNode) e.item.getData();
    // Walk up the current node looking for a BusinessModel
    while (!(dataNode instanceof BusinessModelTreeNode) && dataNode.getParent() != null) {
      dataNode = dataNode.getParent();
    }

    if (dataNode instanceof BusinessModelTreeNode) {
      setActiveBusinessModel(((BusinessModelTreeNode)dataNode).getBusinessModel());
      activeModelTreeNode = (BusinessModelTreeNode)dataNode;
    }
  }

  public void setActiveBusinessModel(BusinessModel businessModel) {
    if (businessModel != null) {
      schemaMeta.setActiveModel(businessModel);
      refreshGraph();
      if (metaEditorOlap != null)
        metaEditorOlap.refreshScreen();
    }
  }

  private void addDragSourceToTree(final Tree fTree) {
    // Drag & Drop for steps

    Transfer[] ttypes = new Transfer[] { XMLTransfer.getInstance() };

    DragSource ddSource = new DragSource(fTree, DND.DROP_MOVE);
    ddSource.setTransfer(ttypes);
    ddSource.addDragListener(new DragSourceListener() {
      public void dragStart(DragSourceEvent event) {
      }

      public void dragSetData(DragSourceEvent event) {
        TreeItem ti[] = fTree.getSelection();
        String data = null;
        int type = 0;
        
        if (ti.length == 1) { // ensure we've only got one thing selected
          ConceptTreeNode node = (ConceptTreeNode)ti[0].getData();
          data = node.getId();
          type = node.getDragAndDropType();
          if (type == 0 || Const.isEmpty(data)) {
            event.doit = false;
            return; // ignore anything else you drag.
          }

          DragAndDropContainer container = new DragAndDropContainer(type, data);
          event.data = container;
        } else {
        // Nothing got dragged, only can happen on OSX :-)
          event.doit = false;
          System.out.println(Messages.getString("MetaEditor.DEBUG_NOTHING_DRAGGED")); //$NON-NLS-1$
        }
      }

      public void dragFinished(DragSourceEvent event) {
      }
    });

  }

  /**
   * Only one selected item possible
   *
   * @param e
   */
  private void setMenuMain(SelectionEvent e) {
    final TreeItem ti = (TreeItem) e.item;
    final ConceptTreeNode node = (ConceptTreeNode) ti.getData();

    log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_CLICKED_ON", ti.getText())); //$NON-NLS-1$

    if (mainMenu == null) {
      mainMenu = new Menu(shell, SWT.POP_UP);
    } else {
      MenuItem[] items = mainMenu.getItems();
      for (int i = 0; i < items.length; i++)
        items[i].dispose();
    }

    if (node instanceof ConnectionsTreeNode) {
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_TEXT")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          newConnection();
        }
      });
      MenuItem miCache = new MenuItem(mainMenu, SWT.PUSH);
      miCache.setText(Messages.getString("MetaEditor.USER_TITLE_CLEAR_CACHE")); //$NON-NLS-1$
      miCache.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          clearDBCache();
        }
      });
    } else if (node instanceof BusinessModelsTreeNode) {
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_MODEL_TEXT")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          newBusinessModel();
        }
      });
    } else if (node instanceof DatabaseMetaTreeNode) { // We clicked on a database node
      final DatabaseMeta databaseMeta = ((DatabaseMetaTreeNode) node).getDatabaseMeta();
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_TEXT")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          newConnection();
        }
      });
      MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
      miEdit.setText(Messages.getString("MetaEditor.USER_EDIT_TEXT")); //$NON-NLS-1$
      miEdit.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          editConnection(databaseMeta);
          treeViewer.update(node, null);
        }
      });
      MenuItem miDupe = new MenuItem(mainMenu, SWT.PUSH);
      miDupe.setText(Messages.getString("MetaEditor.USER_DUPLICATE_TEXT")); //$NON-NLS-1$
      miDupe.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          dupeConnection(databaseMeta);
        }
      });
      MenuItem miDel = new MenuItem(mainMenu, SWT.PUSH);
      miDel.setText(Messages.getString("MetaEditor.USER_DELETE_TEXT")); //$NON-NLS-1$
      miDel.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          delConnection(databaseMeta);
        }
      });
      new MenuItem(mainMenu, SWT.SEPARATOR);
      MenuItem miMImp = new MenuItem(mainMenu, SWT.PUSH);
      miMImp.setText(Messages.getString("MetaEditor.USER_IMPORT_MULTIPLE_TABLES")); //$NON-NLS-1$
      miMImp.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          importMultipleTables(databaseMeta);
          node.sync();
        }
      });
      MenuItem miCache = new MenuItem(mainMenu, SWT.PUSH);
      miCache.setText(Messages.getString("MetaEditor.USER_CLEAR_DB_CACHE", ti.getText())); //$NON-NLS-1$
      miCache.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          clearDBCache();
        }
      });
      new MenuItem(mainMenu, SWT.SEPARATOR);
      MenuItem miSQL = new MenuItem(mainMenu, SWT.PUSH);
      miSQL.setText(Messages.getString("MetaEditor.USER_SQL_EDITOR")); //$NON-NLS-1$
      miSQL.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          sqlSelected(databaseMeta);
        }
      });
      MenuItem miExpl = new MenuItem(mainMenu, SWT.PUSH);
      miExpl.setText(Messages.getString("MetaEditor.USER_EXPLORE")); //$NON-NLS-1$
      miExpl.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          exploreDB();
        }
      });
    } else if (node instanceof PhysicalTableTreeNode) { // We clicked on a physical table
      final PhysicalTable physicalTable = (PhysicalTable) ((PhysicalTableTreeNode) node).getDomainObject();
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_PHYSICAL_TABLETEXT")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          importTables(physicalTable.getDatabaseMeta());
          ((ConceptTreeNode)node.getParent()).sync();
        }
      });
      MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
      miEdit.setText(Messages.getString("MetaEditor.USER_EDIT_TEXT")); //$NON-NLS-1$
      miEdit.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          editPhysicalTable(physicalTable);
          treeViewer.update(node, null);
        }
      });
      MenuItem miDel = new MenuItem(mainMenu, SWT.PUSH);
      miDel.setText(Messages.getString("MetaEditor.USER_DELETE_TEXT")); //$NON-NLS-1$
      miDel.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          delPhysicalTable(physicalTable);
        }
      });
    } else if (node instanceof PhysicalColumnTreeNode) {
      final PhysicalColumn physicalColumn = (PhysicalColumn) ((PhysicalColumnTreeNode) node).getDomainObject();
      MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
      miEdit.setText(Messages.getString("MetaEditor.USER_EDIT_TEXT")); //$NON-NLS-1$
      miEdit.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          editPhysicalTable(physicalColumn.getTable());
          treeViewer.update(node.getParent(), null);
        }
      });
    } else if (node instanceof BusinessModelTreeNode) {
      final BusinessModel businessModel = ((BusinessModelTreeNode) node).getBusinessModel();
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_MODEL_INSTANCE")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          newBusinessModel();
        }
      });
      MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
      miEdit.setText(Messages.getString("MetaEditor.USER_EDIT_TEXT")); //$NON-NLS-1$
      miEdit.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          editBusinessModel(businessModel, node);
        }
      });
      MenuItem miDelete = new MenuItem(mainMenu, SWT.PUSH);
      miDelete.setText(Messages.getString("MetaEditor.USER_DELETE_TEXT")); //$NON-NLS-1$
      miDelete.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          deleteBusinessModel(businessModel);
        }
      });
    } else if (node instanceof BusinessTablesTreeNode) {
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_BUSINESS_TABLE")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          newBusinessTable(null);
        }
      });
    } else if (node instanceof RelationshipsTreeNode) {
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_RELATIONSHIP")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          newRelationship();
        }
      });
    } else if (node instanceof BusinessViewTreeNode) {
      final BusinessCategory businessCategory = ((BusinessViewTreeNode) node).getCategory();
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_CATEGORY")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event ev) {
          newBusinessCategory(businessCategory);
        }
      });
    } else if (node instanceof BusinessTableTreeNode) {
      final BusinessTable businessTable = (BusinessTable) ((BusinessTableTreeNode) node).getDomainObject();
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_BUSINESS_TABLE")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          newBusinessTable(null);
        }
      });
      MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
      miEdit.setText(Messages.getString("MetaEditor.USER_EDIT_TEXT")); //$NON-NLS-1$
      miEdit.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          editBusinessTable(businessTable, node);
        }
      });
      MenuItem miDuplicate = new MenuItem(mainMenu, SWT.PUSH);
      miDuplicate.setText(Messages.getString("MetaEditor.USER_DUPLICATE")); //$NON-NLS-1$
      miDuplicate.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          dupeBusinessTable(businessTable);
        }
      });
      MenuItem miDel = new MenuItem(mainMenu, SWT.PUSH);
      miDel.setText(Messages.getString("MetaEditor.USER_DELETE_TEXT")); //$NON-NLS-1$
      miDel.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          delBusinessTable(businessTable);
        }
      });
    } else if (node instanceof RelationshipTreeNode) {
      final RelationshipMeta relationshipMeta = (RelationshipMeta) ((RelationshipTreeNode) node).getDomainObject();
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_RELATIONSHIP")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          newRelationship();

        }
      });
      MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
      miEdit.setText(Messages.getString("MetaEditor.USER_EDIT_TEXT")); //$NON-NLS-1$
      miEdit.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          editRelationship(relationshipMeta);
          treeViewer.update(node,null);
        }
      });
      MenuItem miDel = new MenuItem(mainMenu, SWT.PUSH);
      miDel.setText(Messages.getString("MetaEditor.USER_DELETE_TEXT")); //$NON-NLS-1$
      miDel.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          delRelationship(relationshipMeta);
          if (activeModelTreeNode != null)
            activeModelTreeNode.getRelationshipsRoot().removeDomainChild(relationshipMeta);
        }
      });
    } else if (node instanceof CategoryTreeNode) {
      final BusinessModel activeModel = schemaMeta.getActiveModel();
      if (activeModel == null) {
        return;
      }

      final CategoryTreeNode parentNode = (CategoryTreeNode)node.getParent();
      final BusinessCategory currentCategory = ((CategoryTreeNode) node).getCategory();
      final BusinessCategory parentCategory = parentNode.getCategory();

      // Get the actual parent and current category
      MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
      miNew.setText(Messages.getString("MetaEditor.USER_NEW_CATEGORY")); //$NON-NLS-1$
      miNew.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event ev) {
          newBusinessCategory(currentCategory);
        }
      });

      MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
      miEdit.setText(Messages.getString("MetaEditor.USER_EDIT_CATEGORY")); //$NON-NLS-1$
      miEdit.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event ev) {
          editBusinessCategory(currentCategory);
          treeViewer.update(node, null);
        }
      });

      MenuItem miDelete = new MenuItem(mainMenu, SWT.PUSH);
      miDelete.setText(Messages.getString("MetaEditor.USER_REMOVE_CATEGORY")); //$NON-NLS-1$
      miDelete.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event ev) {
          delBusinessCategory(parentCategory, currentCategory);
        }
      });

      new MenuItem(mainMenu, SWT.SEPARATOR);

      MenuItem miUp = new MenuItem(mainMenu, SWT.PUSH);
      miUp.setText(Messages.getString("MetaEditor.USER_MOVE_UP")); //$NON-NLS-1$
      miUp.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event ev) {
          moveBusinessCategoryUp(parentCategory, currentCategory);
          //          treeViewer.getTree().setSelection(ti);
        }
      });

      MenuItem miDown = new MenuItem(mainMenu, SWT.PUSH);
      miDown.setText(Messages.getString("MetaEditor.USER_MOVE_DOWN")); //$NON-NLS-1$
      miDown.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event ev) {
          moveBusinessCategoryDown(parentCategory, currentCategory);
          //          treeViewer.getTree().setSelection(ti);
        }
      });
    }

    final ConceptUtilityInterface[] utilityInterfaces = getSelectedConceptUtilityInterfacesInMainTree();
    if (utilityInterfaces.length > 0) {
      if (mainMenu.getItemCount() > 0) {
        new MenuItem(mainMenu, SWT.SEPARATOR);
      }

      MenuItem miSetConcept = new MenuItem(mainMenu, SWT.PUSH);
      miSetConcept.setText(Messages.getString("MetaEditor.USER_SET_PARENT_CONCEPT")); //$NON-NLS-1$
      miSetConcept.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          setParentConcept(utilityInterfaces);
          treeViewer.refresh(mainTreeNode);
        }
      });

      MenuItem miClearConcept = new MenuItem(mainMenu, SWT.PUSH);
      miClearConcept.setText(Messages.getString("MetaEditor.USER_CLEAR_PARENT_CONCEPT")); //$NON-NLS-1$
      miClearConcept.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          clearParentConcept(utilityInterfaces);
          treeViewer.refresh(mainTreeNode);
        }
      });

      MenuItem miRemoveProperty = new MenuItem(mainMenu, SWT.PUSH);
      miRemoveProperty.setText(Messages.getString("MetaEditor.USER_REMOVE_CHILD_PROPERTIES")); //$NON-NLS-1$
      miRemoveProperty.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event evt) {
          removeChildProperties(utilityInterfaces);
          treeViewer.refresh(mainTreeNode);
        }
      });

    }

    treeViewer.getTree().setMenu(mainMenu);
  }

  /*
  public void delColumnFromCategory(BusinessCategory businessCategory, BusinessColumn businessColumn) {
    int idx = businessCategory.indexOfBusinessColumn(businessColumn);
    if (idx >= 0) {
      businessCategory.removeBusinessColumn(idx);
      refreshTree();
    }
  }

  public void moveBusinessColumnDown(BusinessCategory businessCategory, BusinessColumn businessColumn) {
    int index = businessCategory.indexOfBusinessColumn(businessColumn);
    if (index < businessCategory.nrBusinessColumns() - 1) {
      businessCategory.removeBusinessColumn(index);
      businessCategory.addBusinessColumn(index + 1, businessColumn);
      refreshTree();
    }

  }

  public void moveBusinessColumnUp(BusinessCategory businessCategory, BusinessColumn businessColumn) {
    int index = businessCategory.indexOfBusinessColumn(businessColumn);
    if (index > 0) {
      businessCategory.removeBusinessColumn(index);
      businessCategory.addBusinessColumn(index - 1, businessColumn);
      refreshTree();
    }
  }
*/
  /**
   * Add a new business category to the specified parent.
   */
  public void newBusinessCategory(BusinessCategory parentCategory) {

    if ((!parentCategory.isRootCategory() && (schemaMeta.getActiveModel() != null))){
      parentCategory = schemaMeta.getActiveModel().getRootCategory(); 
    }
    // Block for now, until Ad-hoc & MDR follow

    BusinessCategory businessCategory = new BusinessCategory();
    businessCategory.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(parentCategory
        .getBusinessCategories()));

    while (true) {
      BusinessCategoryDialog dialog = new BusinessCategoryDialog(shell, businessCategory, schemaMeta.getLocales(),
          schemaMeta.getSecurityReference());
      if (dialog.open() != null) {
        // Add this to the parent.
        try {
          parentCategory.addBusinessCategory(businessCategory);
          if (activeModelTreeNode != null)
            activeModelTreeNode.getBusinessViewRoot().addDomainChild(businessCategory);
          break;
        } catch (ObjectAlreadyExistsException e) {
          new ErrorDialog(
              shell,
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_BUSINESS_CATEGORY_EXISTS", businessCategory.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      } else {
        break;
      }
    }
  }

  public void delBusinessCategory(BusinessCategory parentCategory, BusinessCategory businessCategory) {
    int index = parentCategory.indexOfBusinessCategory(businessCategory);
    if (index >= 0) {
      parentCategory.removeBusinessCategory(index);
      if (activeModelTreeNode != null)
        activeModelTreeNode.getBusinessViewRoot().removeDomainChild(businessCategory);
    }
  }

  public void editBusinessCategory(BusinessCategory businessCategory) {
    BusinessCategoryDialog dialog = new BusinessCategoryDialog(shell, businessCategory, schemaMeta.getLocales(),
        schemaMeta.getSecurityReference());
    if (dialog.open() != null) {
        // refresh it all...
        refreshAll();
    }
    
  }

  public void editBusinessCategories() {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if (activeModel != null) {
      CategoryEditorDialog dialog = new CategoryEditorDialog(shell, activeModel, schemaMeta.getLocales(), schemaMeta
          .getSecurityReference());
        /*
      BusinessCategoriesDialog dialog = new BusinessCategoriesDialog(shell, activeModel, schemaMeta.getLocales(),
          schemaMeta.getSecurityReference());
          */
      dialog.open();
      if(activeModelTreeNode != null)
        activeModelTreeNode.getBusinessViewRoot().sync();
    }
  }

  public void moveBusinessCategoryDown(BusinessCategory parentCategory, BusinessCategory businessCategory) {
    int index = parentCategory.indexOfBusinessCategory(businessCategory);
    if (index < parentCategory.nrBusinessCategories() - 1) {
      parentCategory.removeBusinessCategory(index);
      try {
        parentCategory.addBusinessCategory(index + 1, businessCategory);
      } catch (ObjectAlreadyExistsException e) {
        // Moving anything should not have any impact.
      }
      if (activeModelTreeNode != null)
        activeModelTreeNode.getBusinessViewRoot().prune();
    }
  }

  public void moveBusinessCategoryUp(BusinessCategory parentCategory, BusinessCategory businessCategory) {
    int index = parentCategory.indexOfBusinessCategory(businessCategory);
    if (index > 0) {
      parentCategory.removeBusinessCategory(index);
      try {
        parentCategory.addBusinessCategory(index - 1, businessCategory);
      } catch (ObjectAlreadyExistsException e) {
        // Moving anything should not have any impact.
      }
      if (activeModelTreeNode != null)
        activeModelTreeNode.getBusinessViewRoot().prune();
    }
  }

  public BusinessTable newBusinessTable(PhysicalTable physicalTable) {

    String activeLocale = schemaMeta.getActiveLocale();
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if (activeModel == null){
      return null;
    }
    // Make sure that we are not trying to add a physical table from a 
    // different connection than the active model's connection
    if (physicalTable != null){
      if (!activeModel.verify(physicalTable)){
        MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
        mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("MetaEditor.USER_ERROR_CANNOT_USE_TABLE", //$NON-NLS-1$ 
            physicalTable.getName(schemaMeta.getActiveLocale()),
            activeModel.getDisplayName(schemaMeta.getActiveLocale()),
            activeModel.getConnection().getName()));
        mb.open();
        
        return null;
      }
    }
    
    if (physicalTable == null) {
      ListSelectionDialog comboDialog = new ListSelectionDialog(shell,
          Messages.getString("MetaEditor.USER_SELECT_PHYSICAL_TABLE_MESSAGE"), Messages.getString("MetaEditor.USER_TITLE_SELECT_PHYSICAL_TABLE"), //$NON-NLS-1$ //$NON-NLS-2$
          schemaMeta.getTables().toArray());
      comboDialog.open();
      physicalTable = (PhysicalTable) comboDialog.getSelection();
      if (physicalTable == null) {
        return null;
      }
    }

    String tableName = ""; //$NON-NLS-1$
    if (physicalTable != null) {
      tableName = physicalTable.getDisplayName(activeLocale);
    }

    // Create a new ID based on this...

    String newId = null;
    int tableNr = 1;
    if (physicalTable != null) {
      String id = Settings.getBusinessTableIDPrefix() + Const.toID(tableName);
      newId = id;
      while (activeModel.findBusinessTable(newId) != null) {
        tableNr++;
        newId = id + "_" + tableNr; //$NON-NLS-1$
      }

      if (Settings.isAnIdUppercase())
        newId = newId.toUpperCase();
    }

    // Create a business table with the new ID and localized name
    BusinessTable businessTable = new BusinessTable(newId, physicalTable);
    businessTable.getConcept().setName(activeLocale, tableName);

    // Add a unique ID enforcer...
    businessTable.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(activeModel.getBusinessTables()));

    // Add columns to this if we have a physical table to import from...
    if (physicalTable != null) {
      // copy the physical columns to the business columns...
      for (int i = 0; i < physicalTable.nrPhysicalColumns(); i++) {
        PhysicalColumn physicalColumn = physicalTable.getPhysicalColumn(i);
        BusinessColumn businessColumn = new BusinessColumn(physicalColumn.getId(), physicalColumn, businessTable);

        // Add a unique ID enforcer...
        businessColumn.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(businessTable
            .getBusinessColumns()));

        // We're done, add the business column.
        try {
          // Propose a new ID
          businessColumn.setId(BusinessColumn.proposeId(activeLocale, businessTable, physicalColumn));
          businessTable.addBusinessColumn(businessColumn);
        } catch (ObjectAlreadyExistsException e) {
          new ErrorDialog(
              shell,
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_BUSINESS_COLUMN_EXISTS", businessColumn.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }

    if (businessTable != null) {

      BusinessTableModel tableModel = new BusinessTableModel(businessTable);

      BusinessTableDialog td = new BusinessTableDialog(shell, SWT.NONE, tableModel, schemaMeta);
      int res = td.open();

      if (Window.OK == res) {

        try {
          activeModel.addBusinessTable(businessTable);
          if (activeModelTreeNode != null)
            activeModelTreeNode.getBusinessTablesRoot().addDomainChild(businessTable);
          refreshGraph();
          return businessTable;
        } catch (ObjectAlreadyExistsException e) {
          new ErrorDialog(
              shell,
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_BUSINESS_TABLE_EXISTS", businessTable.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
    return null;

  }

  public void delBusinessTable(BusinessTable businessTable) {
    if (businessTable != null) {
      BusinessModel activeModel = schemaMeta.getActiveModel();
      if (activeModel == null)
        return;
      // First delete the relationships it uses.
      RelationshipMeta[] relationships = activeModel.findRelationshipsUsing(businessTable);
      for (int i = 0; i < relationships.length; i++) {
        int idx = activeModel.indexOfRelationship(relationships[i]);
        if (idx >= 0){
          activeModel.removeRelationship(idx);
          if (activeModelTreeNode != null)
            activeModelTreeNode.getRelationshipsRoot().removeDomainChild(relationships[i]);
        }
      }

      int idx = activeModel.indexOfBusinessTable(businessTable);
      activeModel.removeBusinessTable(idx);
      if (activeModelTreeNode != null)
        activeModelTreeNode.getBusinessTablesRoot().removeDomainChild(businessTable);
      // call refresh all to refresh the rest of the UI - does not refresh the tree
      refreshAll();
    }
  }

  private void initTabs() {
    Composite child = new Composite(sashform, SWT.BORDER);
    child.setLayout(new FillLayout());

    tabfolder = new CTabFolder(child, SWT.BORDER);
    tabfolder.setSimple(false);

    CTabItem tiTabsGraph = new CTabItem(tabfolder, SWT.NONE);
    tiTabsGraph.setText(Messages.getString("MetaEditor.USER_GRAPHICAL_VIEW")); //$NON-NLS-1$
    tiTabsGraph.setToolTipText(Messages.getString("MetaEditor.USER_GRAPHICAL_VIEW_TEXT")); //$NON-NLS-1$

    CTabItem tiTabsConcept = new CTabItem(tabfolder, SWT.NULL);
    tiTabsConcept.setText(Messages.getString("MetaEditor.USER_CONCEPTS")); //$NON-NLS-1$
    tiTabsConcept.setToolTipText(Messages.getString("MetaEditor.USER_CONCEPTS_TEXT")); //$NON-NLS-1$

    CTabItem tiTabsLocale = new CTabItem(tabfolder, SWT.NULL);
    tiTabsLocale.setText(Messages.getString("MetaEditor.USER_LOCALES")); //$NON-NLS-1$
    tiTabsLocale.setToolTipText(Messages.getString("MetaEditor.USER_LOCALES_TEXT")); //$NON-NLS-1$

    CTabItem tiTabsLog = new CTabItem(tabfolder, SWT.NULL);
    tiTabsLog.setText(Messages.getString("MetaEditor.USER_LOG_VIEW")); //$NON-NLS-1$
    tiTabsLog.setToolTipText(Messages.getString("MetaEditor.USER_LOG_VIEW_TEXT")); //$NON-NLS-1$

    metaEditorGraph = new MetaEditorGraph(tabfolder, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND, this);
    metaEditorGraph.addListener(SWT.MouseExit, getMainListener());
    metaEditorConcept = new MetaEditorConcepts(tabfolder, SWT.NONE, this);
    metaEditorLocales = new MetaEditorLocales(tabfolder, SWT.NONE, this);
    metaEditorLog = new MetaEditorLog(tabfolder, SWT.NONE, null);

    tiTabsGraph.setControl(metaEditorGraph);
    tiTabsConcept.setControl(metaEditorConcept);
    tiTabsLocale.setControl(metaEditorLocales);
    tiTabsLog.setControl(metaEditorLog);

    tabfolder.setSelection(0);

    sashform.addKeyListener(defKeys);
    sashform.addKeyListener(modKeys);

    addOlapTab();
  }

  public void addOlapTab() {
    CTabItem tiTabsOlap = new CTabItem(tabfolder, SWT.NULL);
    tiTabsOlap.setText(Messages.getString("MetaEditor.USER_OLAP")); //$NON-NLS-1$
    tiTabsOlap.setToolTipText(Messages.getString("MetaEditor.USER_OLAP_TEXT")); //$NON-NLS-1$
    metaEditorOlap = new MetaEditorOlap(tabfolder, SWT.NONE, this);

    tiTabsOlap.setControl(metaEditorOlap);
  }

  private boolean readData(String domainName) {
    try {
      props.addLastFile(LastUsedFile.FILE_TYPE_SCHEMA, domainName, "", false, ""); //$NON-NLS-1$ //$NON-NLS-2$
      saveSettings();
      addMenuLast();

      // Get a new cwm instance for the selected model...
      if (cwm != null) {
        cwm.removeFromList();
      }
      cwm = CWM.getInstance(domainName);

      // Read some data from the domain...
      schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);

      refreshTree();
      refreshAll();
      return true;
    } catch (Exception e) {
      new ErrorDialog(
          shell,
          Messages.getString("MetaEditor.USER_TITLE_ERROR_READING_DOMAIN"), Messages.getString("MetaEditor.USER_ERROR_READING_DOMAIN"), e); //$NON-NLS-1$ //$NON-NLS-2$
      return false;
    }
  }

/*
  public void newSelected() {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if (activeModel == null)
      return;

    log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_NEW_SELECTED")); //$NON-NLS-1$
    // Determine what menu we selected from...

    TreeItem ti[] = treeViewer.getTree().getSelection();

    // Then call newConnection or newTrans
    if (ti.length >= 1) {
      String name = ti[0].getText();
      TreeItem parent = ti[0].getParentItem();
      if (parent == null) {
        log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_ELEMENT_HAS_NO_PARENT")); //$NON-NLS-1$
        if (name.equalsIgnoreCase(STRING_CONNECTIONS))
          newConnection();
        if (name.equalsIgnoreCase(STRING_RELATIONSHIPS))
          newRelationship();
        if (name.equalsIgnoreCase(STRING_BUSINESS_TABLES)) {
          MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
          mb.setMessage(Messages.getString("MetaEditor.USER_IMPORT_TABLES_VIA_CONNECTIONS")); //$NON-NLS-1$
          mb.setText(Messages.getString("MetaEditor.USER_TITLE_IMPORT_TABLES")); //$NON-NLS-1$
          mb.open();
        }
      } else {
        String section = parent.getText();
        log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_ELEMENT_HAS_PARENT", section)); //$NON-NLS-1$
        if (section.equalsIgnoreCase(STRING_CONNECTIONS))
          newConnection();
      }
    }
  }
*/

  public void doubleClickedMain() {
    // Determine what tree-item we selected from...

    TreeItem ti[] = treeViewer.getTree().getSelection();
    if (ti.length == 1) { // ensure we've only got one thing selected
      ConceptTreeNode node = (ConceptTreeNode)ti[0].getData();
      final String itemText = ti[0].getText();
      if (node instanceof LabelTreeNode) { // We clicked on one of the labels... not an actual object
        if (itemText.equals(STRING_CONNECTIONS)) {
          newConnection();
        } else if (itemText.equals(STRING_BUSINESS_MODELS)) {
          newBusinessModel();
        }
      } else if (node instanceof DatabaseMetaTreeNode) {
        DatabaseMeta databaseMeta = ((DatabaseMetaTreeNode) node).getDatabaseMeta();
        editConnection(databaseMeta);
      } else if (node instanceof PhysicalTableTreeNode) {
        PhysicalTable physicalTable = (PhysicalTable) ((PhysicalTableTreeNode) node).getDomainObject();
        editPhysicalTable(physicalTable);
      } else if (node instanceof PhysicalColumnTreeNode) {
        PhysicalColumn physicalColumn = (PhysicalColumn) ((PhysicalColumnTreeNode) node).getDomainObject();
        editPhysicalColumn(physicalColumn);
      } else if (node instanceof BusinessModelTreeNode) {
        BusinessModel businessModel = ((BusinessModelTreeNode) node).getBusinessModel();
        editBusinessModel(businessModel, node);
      } else if (node instanceof BusinessTablesTreeNode) {
        newBusinessTable(null);
      } else if (node instanceof RelationshipsTreeNode) {
        newRelationship();
      } else if (node instanceof BusinessTableTreeNode) {
        BusinessTable businessTable = (BusinessTable) ((BusinessTableTreeNode) node).getDomainObject();
        editBusinessTable(businessTable, node);
      } else if (node instanceof RelationshipTreeNode) {
        RelationshipMeta relationship = (RelationshipMeta) ((RelationshipTreeNode) node).getDomainObject();
        editRelationship(relationship);
      } else if (node instanceof BusinessColumnTreeNode) {
        BusinessColumn businessColumn = ((BusinessColumnTreeNode) node).getBusinessColumn();
        editBusinessColumn(businessColumn.getBusinessTable(), businessColumn);
      }else if (node instanceof CategoryTreeNode) {
        BusinessCategory businessCategory = ((CategoryTreeNode) node).getCategory();
        if (businessCategory.isRootCategory()){
          editBusinessCategories();
        }else{
          editBusinessCategory(businessCategory);
        }
      }
      treeViewer.update(node,null);
    }
  }

  private void editBusinessColumn(BusinessTable businessTable, BusinessColumn businessColumn) {
    String columnName = businessColumn.getDisplayName(schemaMeta.getActiveLocale());
    String tableName = businessTable.getDisplayName(schemaMeta.getActiveLocale());
    editProperties(Messages.getString("MetaEditor.USER_ENTER_COLUMN_PROPERTIES", columnName, tableName), businessColumn); //$NON-NLS-1$
  }

  private void editPhysicalColumn(PhysicalColumn physicalColumn) {
    if (physicalColumn != null) {
      String activeLocale = schemaMeta.getActiveLocale();
      editProperties(Messages.getString(
          "MetaEditor.USER_PHYSICAL_COLUMN_PROPERTIES", physicalColumn.getName(activeLocale)), physicalColumn); //$NON-NLS-1$
    }
  }

  private void editProperties(String message, ConceptUtilityInterface utilityInterface) {
    ConceptDialog dialog = new ConceptDialog(shell,
        Messages.getString("MetaEditor.USER_ENTER_PROPERTIES"), message, utilityInterface, schemaMeta); //$NON-NLS-1$
    String id = dialog.open();
    if (id != null) {
      mainTreeNode.sync();
      refreshAll();
    }
  }

  public BusinessModel newBusinessModel() {
    int nr = schemaMeta.nrBusinessModels() + 1;
    String id = Settings.getBusinessModelIDPrefix() + "model_" + nr; //$NON-NLS-1$
    if (Settings.isAnIdUppercase())
      id = id.toUpperCase();
    BusinessModel businessModel = new BusinessModel(id);
    businessModel.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(schemaMeta.getBusinessModels()));
    businessModel.getConcept().setName(schemaMeta.getActiveLocale(), "Model " + nr); //$NON-NLS-1$

    while (true) {
      BusinessModelDialog dialog = new BusinessModelDialog(shell, businessModel, schemaMeta.getLocales(), schemaMeta
          .getSecurityReference());
      String modelName = dialog.open();
      if (modelName != null) {
        try {
          schemaMeta.addModel(businessModel);
          mainTreeNode.getBusinessModelsRoot().addDomainChild(businessModel);
          schemaMeta.setActiveModel(businessModel);
          activeModelTreeNode = (BusinessModelTreeNode)mainTreeNode.getBusinessModelsRoot().findNode(businessModel);
          refreshAll();

          return businessModel;
        } catch (ObjectAlreadyExistsException e) {
          new ErrorDialog(
              shell,
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_BUSINESS_MODEL_NAME_EXISTS"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      } else {
        break;
      }
    }
    return null;
  }

  public void editBusinessModel(BusinessModel businessModel) {
    editBusinessModel(businessModel, null);
  }

  public void editBusinessModel(BusinessModel businessModel, ConceptTreeNode node) {
    if (businessModel != null) {
      BusinessModelDialog dialog = new BusinessModelDialog(shell, businessModel, schemaMeta.getLocales(), schemaMeta
          .getSecurityReference());
      String modelName = dialog.open();
      if (modelName != null) {
        if (node != null){
          node.sync();
        } else{
          synchronize(businessModel);
        }
        refreshAll();
      }
    }
  }

  public void deleteBusinessModel(BusinessModel businessModel) {
    if (businessModel != null) {
      MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
      box.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      box.setMessage(Messages.getString(
          "MetaEditor.USER_DELETE_BUSINESS_MODEL", businessModel.getDisplayName(schemaMeta.getActiveLocale()))); //$NON-NLS-1$
      int answer = box.open();
      if (answer == SWT.YES) {
        schemaMeta.removeBusinessModel(businessModel);
        schemaMeta.setActiveModel(null);
        mainTreeNode.getBusinessModelsRoot().removeDomainChild(businessModel);
        refreshAll();
      }
    }
  }

  public void sqlSelected(DatabaseMeta databaseMeta) {
    if (databaseMeta != null) {
      SQLEditor sql = new SQLEditor(shell, SWT.NONE, databaseMeta, DBCache.getInstance(), ""); //$NON-NLS-1$
      sql.open();
    }
  }

  public void editConnection(DatabaseMeta db) {
    if (db != null) {
      DatabaseDialog con = new DatabaseDialog(shell, db);
      con.open();
    }
    setShellText();
  }

  public void dupeConnection(DatabaseMeta databaseMeta) {
    if (databaseMeta != null) {
      try {
        int pos = schemaMeta.indexOfDatabase(databaseMeta);
        DatabaseMeta newdb = (DatabaseMeta) databaseMeta.clone();
        String dupename = Messages.getString("MetaEditor.USER_COPY_OF", databaseMeta.getName()); //$NON-NLS-1$
        newdb.setName(dupename);
        schemaMeta.addDatabase(pos + 1, newdb);

        DatabaseDialog con = new DatabaseDialog(shell, newdb);
        String newname = con.open();
        if (newname != null) // null: CANCEL
        {
          schemaMeta.removeDatabaseMeta(pos + 1);
          schemaMeta.addDatabase(pos + 1, newdb);

        }
        mainTreeNode.getConnectionsRoot().addDomainChild(newdb);
      } catch (ObjectAlreadyExistsException e) {
        new ErrorDialog(
            shell,
            Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_CONNECTION_NAME_EXISTS"), e); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  public void delConnection(DatabaseMeta databaseMeta) {
    if (databaseMeta != null) {
      schemaMeta.removeDatabaseMeta(databaseMeta);
      mainTreeNode.getConnectionsRoot().removeDomainChild(databaseMeta);
    }
    setShellText();
  }

  public void editPhysicalTable(PhysicalTable physicalTable) {
    if (physicalTable != null) {

      PhysicalTable copy = (PhysicalTable) physicalTable.clone();
      PhysicalTableModel tableModel = new PhysicalTableModel(copy);

      PhysicalTableDialog td = new PhysicalTableDialog(shell, SWT.NONE, tableModel, schemaMeta);
      int res = td.open();

      if (Window.OK == res) {

        // It's important to preserve the ConceptInterface instances (rather 
        // than replacing them), as the instance references are important to  
        // the inheritance chain among the concept business objects. 

        ConceptInterface originalInterface = physicalTable.getConcept();
        originalInterface.clearChildProperties();
        originalInterface.getChildPropertyInterfaces().putAll(copy.getConcept().getChildPropertyInterfaces());        

    physicalTable.removeAllPhysicalColumns();
    Iterator iter = copy.getPhysicalColumns().iterator();
    while (iter.hasNext()) {
      PhysicalColumn column = (PhysicalColumn) iter.next();
      try {
        physicalTable.addPhysicalColumn(column);
      } catch (ObjectAlreadyExistsException e) {
        e.printStackTrace();
            log.logDebug(APPLICATION_NAME,"This should not happen as this exception would already have been caught earlier..."); //$NON-NLS-1$
      }
    }

        refreshGraph();
        mainTreeNode.sync();
        setShellText();
        return;
    }
    }
  }

  public void dupePhysicalTable(PhysicalTable physicalTable) {
    if (physicalTable != null) {
      log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_DUPLICATE_TABLE", physicalTable.getId())); //$NON-NLS-1$

      PhysicalTable newTable = (PhysicalTable) physicalTable.clone();
      if (newTable != null) {
        try {
          String newname = physicalTable.getId() + " (copy)"; //$NON-NLS-1$
          int nr = 2;
          while (schemaMeta.findPhysicalTable(newname) != null) {
            newname = physicalTable.getId() + " (copy " + nr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            nr++;
          }
          newTable.setId(newname);
          schemaMeta.addTable(newTable);
          mainTreeNode.sync();
          refreshGraph();
        } catch (ObjectAlreadyExistsException e) {
          new ErrorDialog(
              shell,
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_PHYSICAL_TABLE_NAME_EXISTS"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
  }

  public void delPhysicalTable(String name) {
    PhysicalTable physicalTable = schemaMeta.findPhysicalTable(schemaMeta.getActiveLocale(), name);
    delPhysicalTable(physicalTable);
    //mainTreeNode.sync();
  }

  public void delPhysicalTable(PhysicalTable physicalTable) {
    log
        .logDebug(
            APPLICATION_NAME,
            Messages
                .getString(
                    "MetaEditor.DEBUG_DELETE_TABLE", physicalTable == null ? "null" : physicalTable.getName(schemaMeta.getActiveLocale()))); //$NON-NLS-1$ //$NON-NLS-2$
    if (physicalTable != null) {
      int pos = schemaMeta.indexOfTable(physicalTable);
      schemaMeta.removeTable(pos);
      for (int i = schemaMeta.nrBusinessModels() - 1; i >= 0; i--) {
        BusinessModel ri = schemaMeta.getModel(i);
        ri.deletePhysicalTableReferences(physicalTable);
      }
      mainTreeNode.sync();
      refreshGraph();
    } else {
      log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_CANT_FIND_TABLE", "null")); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public void editRelationship(RelationshipMeta ri) {
    if (ri != null) {
      String name = ri.toString();
      BusinessModel activeModel = schemaMeta.getActiveModel();
      if (activeModel == null)
        return;
      RelationshipDialog rd = new RelationshipDialog(shell, SWT.NONE, log, ri, activeModel);
      if (rd.open() != null) {
        String newname = ri.toString();

        if (!name.equalsIgnoreCase(newname)) {
          treeViewer.update(mainTreeNode, null);
        }
        refreshGraph(); // color, nr of copies...
      }
    }
    setShellText();
  }

  public void delRelationship(RelationshipMeta relationship) {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if (activeModel == null)
      return;
    activeModel.removeRelationship(relationship);
    refreshGraph();
  }

  public void newRelationship() {
    newRelationship(null, null);
  }

  public void newRelationship(BusinessTable from, BusinessTable to) {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if (activeModel == null) {
      return;
    }

    RelationshipMeta relationship = new RelationshipMeta();
    relationship.setTableFrom(from);
    relationship.setTableTo(to);
    RelationshipDialog dialog = new RelationshipDialog(shell, SWT.NONE, log, relationship, schemaMeta.getActiveModel());
    if (dialog.open() != null) {
      activeModel.addRelationship(relationship);
      if (activeModelTreeNode != null)
        activeModelTreeNode.getRelationshipsRoot().addDomainChild(relationship);
      refreshGraph();
    }
  }

  public void newConnection() {
    DatabaseMeta db = new DatabaseMeta();
    DatabaseDialog con = new DatabaseDialog(shell, db);
    String con_name = con.open();
    if (con_name != null) {
      try {
        schemaMeta.addDatabase(db);
        mainTreeNode.getConnectionsRoot().addDomainChild(db);
      } catch (ObjectAlreadyExistsException e) {
        new ErrorDialog(
            shell,
            Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_DATABASE_NAME_EXISTS"), e); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  public boolean showChangedWarning() {
    return showChangedWarning(Messages.getString("MetaEditor.USER_DOMAIN_CHANGED")); //$NON-NLS-1$
  }

  public boolean showChangedWarning(String message) {
    boolean answer = true;
    if (schemaMeta.hasChanged()) {
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_WARNING | SWT.APPLICATION_MODAL);
      mb.setMessage(message);
      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      answer = mb.open() == SWT.YES;
    }
    return answer;
  }

  public void openFile() {
    if (showChangedWarning()) {
      try {
        // Get the available models in the CWM repository
        String[] domainNames = CWM.getDomainNames();

        // Show a dialog to select a model
        EnterSelectionDialog selectionDialog = new EnterSelectionDialog(shell, domainNames, Messages
            .getString("MetaEditor.USER_SELECT_DOMAIN"), //$NON-NLS-1$
            Messages.getString("MetaEditor.USER_SELECT_DOMAIN")); //$NON-NLS-1$
        String domainName = selectionDialog.open();
        if (domainName != null) {
          readData(domainName);
        }
      } catch (CWMException e) {
        new ErrorDialog(
            shell,
            Messages.getString("MetaEditor.USER_TITLE_ERROR_GETTING_DOMAINS"), Messages.getString("MetaEditor.USER_ERROR_GETTING_DOMAINS"), //$NON-NLS-1$ //$NON-NLS-2$
            e);
      }
    }
  }

  public void newFile() {
    boolean goAhead = false;
    if (schemaMeta.hasChanged()) {
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_WARNING);
      mb.setMessage(Messages.getString("MetaEditor.USER_DOMAIN_CHANGED_SAVE")); //$NON-NLS-1$
      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      int answer = mb.open();
      switch (answer) {
        case SWT.YES:
          goAhead = saveFile();
          break;
        case SWT.NO:
          goAhead = true;
          break;
        case SWT.CANCEL:
          goAhead = false;
          break;
      }
    } else {
      goAhead = true;
    }

    if (goAhead) {
      schemaMeta.clear();
      schemaMeta.addDefaults();
      schemaMeta.clearChanged();
      setDomainName(null);
      refreshTree();
      refreshAll();
    }
  }

  public boolean quitFile() {
    boolean retval = true;

    log.logDetailed(APPLICATION_NAME, Messages.getString("MetaEditor.INFO_QUIT_APPLICATION")); //$NON-NLS-1$
    saveSettings();
    if (schemaMeta.hasChanged()) {
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_WARNING);
      mb.setMessage(Messages.getString("MetaEditor.USER_FILE_CHANGED_SAVE")); //$NON-NLS-1$
      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      int answer = mb.open();

      switch (answer) {
        case SWT.YES:
          saveFile();
          dispose();
          break;
        case SWT.NO:
          dispose();
          break;
        case SWT.CANCEL:
          retval = false;
          break;
      }
    } else {
      dispose();
    }
    return retval;
  }

  public boolean saveFile() {
    log.logDetailed(APPLICATION_NAME, Messages.getString("MetaEditor.INFO_SAVE_FILE")); //$NON-NLS-1$
    if (schemaMeta.domainName != null) {
      return save(schemaMeta.domainName);
    } else {
      return saveFileAs();
    }
  }

  public boolean saveFileAs() {
    try {
      log.logBasic(APPLICATION_NAME, Messages.getString("MetaEditor.INFO_SAVE_FILE_AS")); //$NON-NLS-1$

      EnterStringDialog dialog = new EnterStringDialog(
          shell,
          "", Messages.getString("MetaEditor.USER_TITLE_SAVE_DOMAIN_NAME"), Messages.getString("MetaEditor.USER_SAVE_DOMAIN_NAME")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      String domainName = dialog.open();

      if (domainName != null) {
        int id = SWT.YES;
        if (CWM.exists(domainName)) {
          MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
          mb.setMessage(Messages.getString("MetaEditor.USER_DOMAIN_EXISTS")); //$NON-NLS-1$
          mb.setText(Messages.getString("MetaEditor.USER_TITLE_DOMAIN_EXISTS")); //$NON-NLS-1$
          id = mb.open();
        }
        if (id == SWT.YES) {
          save(domainName);
          setDomainName(domainName);
          return true;
        }
      }
    } catch (Exception e) {
      new ErrorDialog(
          shell,
          Messages.getString("MetaEditor.USER_TITLE_ERROR_SAVING_DOMAIN"), Messages.getString("MetaEditor.USER_ERROR_SAVING_DOMAIN_SEVERE"), e); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return false;
  }

  private boolean save(String domainName) {
    try {
      // Save the schema in the MDR
      SchemaSaveProgressDialog dialog = new SchemaSaveProgressDialog(shell, domainName, schemaMeta);
      cwm = dialog.open();

      // Handle last opened files...
      props.addLastFile(LastUsedFile.FILE_TYPE_SCHEMA, domainName, Const.FILE_SEPARATOR, false, ""); //$NON-NLS-1$
      saveSettings();
      addMenuLast();

      schemaMeta.clearChanged();
      setShellText();
      log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_FILE_WRITTEN_TO_REPOSITORY", domainName)); //$NON-NLS-1$
      return true;
    } catch (Exception e) {
      new ErrorDialog(shell,
          Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_SAVING_DOMAIN"), e); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return false;
  }

  public void deleteFile() {
    try {
      // Get the available domains in the CWM repository
      String[] domainNames = CWM.getDomainNames();

      // Show a dialog to select a model
      EnterSelectionDialog selectionDialog = new EnterSelectionDialog(shell, domainNames, Messages
          .getString("MetaEditor.USER_DELETE_DOMAIN"), //$NON-NLS-1$
          Messages.getString("MetaEditor.USER_SELECT_DOMAIN_FOR_DELETE")); //$NON-NLS-1$
      String domainName = selectionDialog.open();
      if (domainName != null) {
        MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
        mb.setMessage(Messages.getString("MetaEditor.USER_DELETE_DOMAIN_CONFIRM")); //$NON-NLS-1$
        mb.setText(Messages.getString("MetaEditor.USER_SURE_CONFIRM")); //$NON-NLS-1$
        int answer = mb.open();
        if (answer == SWT.YES) {
          CWM delCwm = CWM.getInstance(domainName);
          delCwm.removeDomain();
          if (schemaMeta.getDomainName().equalsIgnoreCase(domainName)){
            schemaMeta.clear();
            schemaMeta.addDefaults();
            schemaMeta.clearChanged();
            setDomainName(null);
            refreshTree();
            refreshAll();
          }
        }
      }
    } catch (Throwable e) {
      new ErrorDialog(
          shell,
          Messages.getString("MetaEditor.USER_TITLE_ERROR_RETRIEVING_DOMAIN_LIST"), Messages.getString("MetaEditor.USER_ERROR_RETRIEVING_DOMAIN_LIST"), //$NON-NLS-1$ //$NON-NLS-2$
          new Exception(e));
    }
  }

  public void helpAbout() {
    MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION | SWT.CENTER);

    StringBuffer message = new StringBuffer();
    message
        .append(Messages.getString("MetaEditor.USER_HELP_METADATA_EDITOR")).append(Const.VERSION).append(Const.CR).append(Const.CR); //$NON-NLS-1$
    message.append(Messages.getString("MetaEditor.USER_HELP_PENTAHO_CORPORATION")).append(Const.CR); //$NON-NLS-1$
    message.append(Messages.getString("MetaEditor.USER_HELP_PENTAHO_URL")).append(Const.CR); //$NON-NLS-1$

    message.append(Messages.getString("MetaEditor.USER_HELP_PENTAHO_COPYRIGHT")) //$NON-NLS-1$
        .append(Const.CR).append(Const.CR).append(Messages.getString("MetaEditor.USER_HELP_PENTAHO_MESSAGE")) //$NON-NLS-1$
        .append(Const.CR).append(Const.CR).append(Messages.getString("MetaEditor.USER_HELP_PENTAHO_MESSAGE2")); //$NON-NLS-1$

    mb.setMessage(message.toString());
    mb.setText(Messages.getString("MetaEditor.USER_HELP_METADATA_EDITOR")); //$NON-NLS-1$
    mb.open();
  }

  public void editUnselectAll() {
    if (schemaMeta.getActiveModel() == null)
      return;

    schemaMeta.getActiveModel().unselectAll();
    metaEditorGraph.redraw();
  }

  public void editSelectAll() {
    if (schemaMeta.getActiveModel() == null)
      return;

    schemaMeta.getActiveModel().selectAll();
    metaEditorGraph.redraw();
  }

  public void editOptions() {
    EnterOptionsDialog eod = new EnterOptionsDialog(shell, props);
    if (eod.open() != null) {
      props.saveProps();
      loadSettings();
      changeLooks();
    }
  }

  public int getTreePosition(TreeItem ti, String item) {
    if (ti != null) {
      TreeItem items[] = ti.getItems();
      for (int x = 0; x < items.length; x++) {
        if (items[x].getText().equalsIgnoreCase(item)) {
          return x;
        }
      }
    }
    return -1;
  }

  public void refreshAll() {
    refreshGraph();
    metaEditorConcept.refreshTree();
    metaEditorConcept.refreshScreen();
    metaEditorLocales.refreshScreen();
    if (metaEditorOlap != null) {
      metaEditorOlap.refreshScreen();
    }
  }

  public void refreshTree() {
    mainTreeNode = new SchemaMetaTreeNode(null, schemaMeta);
    mainTreeNode.addTreeNodeChangeListener((ITreeNodeChangedListener) treeViewer.getContentProvider());

    // This next line is only necessary so that the nodes are realized ahead of time, in order for the tree to reflect
    // changes from the graph, regardless of whether the tree was expanded or not...
    mainTreeNode.sync();

    treeViewer.setInput(mainTreeNode);

    // And this line is to prevent a bug where the viewer will display duplicate nodes when setInput() is called
    treeViewer.refresh();

    if (mainTreeNode.getBusinessModelsRoot().hasChildren())
      activeModelTreeNode = (BusinessModelTreeNode) mainTreeNode.getBusinessModelsRoot().getChildren().get(0);
  }

  public void synchronize (Object businessObject){
    ConceptTreeNode node = mainTreeNode.findNode(businessObject);
    node.sync();
  }

  public static final void addTreeCategories(TreeItem tiParent, BusinessCategory parentCategory, String locale,
      boolean hiddenToo) {
    // Draw the categories tree...
    for (int i = 0; i < parentCategory.nrBusinessCategories(); i++) {
      BusinessCategory businessCategory = parentCategory.getBusinessCategory(i);
      ConceptInterface concept = businessCategory.getConcept();

      TreeItem tiCategory = new TreeItem(tiParent, SWT.NONE);
      String name = businessCategory.getDisplayName(locale);
      tiCategory.setText(0, name);
      if (concept != null && concept.findFirstParentConcept() != null) {
        tiCategory.setText(1, concept.findFirstParentConcept().getName());
      }
      tiCategory.setForeground(GUIResource.getInstance().getColorBlack());

      // First add the sub-categories...
      addTreeCategories(tiCategory, businessCategory, locale, hiddenToo);
    }

    // Then add the business columns...
    for (int c = 0; c < parentCategory.nrBusinessColumns(); c++) {
      BusinessColumn businessColumn = parentCategory.getBusinessColumn(c);

      if (hiddenToo || !businessColumn.isHidden()) {
        ConceptInterface concept = businessColumn.getConcept();

        TreeItem tiColumn = new TreeItem(tiParent, SWT.NONE);
        tiColumn.setText(0, businessColumn.getDisplayName(locale));
        if (concept != null && concept.findFirstParentConcept() != null) {
          tiColumn.setText(1, concept.findFirstParentConcept().getName());
        }
        tiColumn.setForeground(GUIResource.getInstance().getColorBlue());
      }
    }
  }

  public void refreshGraph() {
    metaEditorGraph.redraw();
    setShellText();
  }

  public DatabaseMeta getConnection(String name) {
    int i;

    for (i = 0; i < schemaMeta.nrDatabases(); i++) {
      DatabaseMeta ci = schemaMeta.getDatabase(i);
      if (ci.getName().equalsIgnoreCase(name)) {
        return ci;
      }
    }
    return null;
  }

  public void setShellText() {
    String fname = schemaMeta.domainName;
    if (shell.isDisposed())
      return;
    if (fname != null) {
      shell.setText(APPLICATION_NAME
          + " - " + fname + (schemaMeta.hasChanged() ? Messages.getString("MetaEditor.USER_CHANGED") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    } else {
      shell.setText(APPLICATION_NAME + (schemaMeta.hasChanged() ? Messages.getString("MetaEditor.USER_CHANGED") : "")); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public void setDomainName(String domainName) {
    schemaMeta.domainName = domainName;

    setShellText();
  }

  private void printFile() {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if (activeModel == null)
      return;

    PrintSpool ps = new PrintSpool();
    Printer printer = ps.getPrinter(shell);

    // Create an image of the screen
    Point max = activeModel.getMaximum();

    // Image img_screen = new Image(trans, max.x, max.y);
    // img_screen.dispose();

    PaletteData pal = ps.getPaletteData();

    ImageData imd = new ImageData(max.x, max.y, printer.getDepth(), pal);
    Image img = new Image(printer, imd);

    GC img_gc = new GC(img);

    // Clear the background first, fill with background color...
    Color bg = new Color(printer, props.getBackgroundRGB());
    img_gc.setForeground(bg);
    img_gc.fillRectangle(0, 0, max.x, max.y);
    bg.dispose();

    // Draw the transformation...
    metaEditorGraph.drawSchema(img_gc);

    // ShowImageDialog sid = new ShowImageDialog(shell, transMeta.props, img);
    // sid.open();

    ps.printImage(shell, img);

    img_gc.dispose();
    img.dispose();
    ps.dispose();
  }

  public void saveSettings() {
    WindowProperty winprop = new WindowProperty(shell);
    props.setScreen(winprop);
    props.setLogLevel(log.getLogLevelDesc());
    props.setSashWeights(sashform.getWeights());
    props.saveProps();
  }

  public void loadSettings() {
    log.setLogLevel(props.getLogLevel());

    GUIResource.getInstance().reload();

    DBCache.getInstance().setActive(props.useDBCache());
  }

  public void changeLooks() {
    treeViewer.getTree().setBackground(GUIResource.getInstance().getColorBackground());
    metaEditorGraph.newProps();

    refreshAll();
  }

  public void clearDBCache() {
    // Determine what menu we selected from...

    TreeItem ti[] = treeViewer.getTree().getSelection();

    // Then call editConnection or editStep or editTrans
    if (ti.length == 1) {
      String name = ti[0].getText();
      TreeItem parent = ti[0].getParentItem();
      if (parent != null) {
        String type = parent.getText();
        if (type.equalsIgnoreCase(STRING_CONNECTIONS)) {
          DBCache.getInstance().clear(name);
        }
      } else {
        if (name.equalsIgnoreCase(STRING_CONNECTIONS))
          DBCache.getInstance().clear(null);
      }
    }
  }

  public void importTables(DatabaseMeta databaseMeta) {
    if (databaseMeta != null) {
      DatabaseExplorerDialog std = new DatabaseExplorerDialog(shell, SWT.NONE, databaseMeta, schemaMeta.databases
          .getList(), false, true);
      if (std.open() != null) {
        String schemaName = std.getSchemaName();
        String tableName = std.getTableName();

        Database database = new Database(databaseMeta);
        try {
          database.connect();

          importTableDefinition(database, schemaName, tableName);
        } catch (KettleException e) {
          new ErrorDialog(
              shell,
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_READING_TABLE_FIELDS", tableName) //$NON-NLS-1$ //$NON-NLS-2$
                  + ((schemaName != null) ? ("(schema=" + schemaName + ")") : ""), e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } finally {
          if (database != null)
            database.disconnect();
        }
      }
    }
  }

  public void importMultipleTables(DatabaseMeta databaseMeta) {
    if (databaseMeta != null) {
      Database database = null;
      try {
        database = new Database(databaseMeta);
        database.connect();

        // Get the list of tables...
        String[] tableNames = database.getTablenames();

        // Select from it...
        EnterSelectionDialog dialog = new EnterSelectionDialog(
            shell,
            tableNames,
            Messages.getString("MetaEditor.USER_TITLE_IMPORT_TABLES"), Messages.getString("MetaEditor.USER_SELECT_IMPORT_TABLES")); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.setMulti(true);
        if (dialog.open() != null) {
          int[] indexes = dialog.getSelectionIndeces();
          for (int i = 0; i < indexes.length; i++) {
            String tableName = tableNames[indexes[i]];
            importTableDefinition(database, null, tableName);
          }
        }

      } catch (Exception e) {
        new ErrorDialog(
            shell,
            Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_IMPORTING_PHYSICAL_TABLES"), e); //$NON-NLS-1$ //$NON-NLS-2$
      } finally {
        if (database != null)
          database.disconnect();
      }
    }
  }

  private void importTableDefinition(Database database, String schemaName, String tableName) throws KettleException {
    UniqueArrayList fields = new UniqueArrayList();

    String id = tableName;
    String tablename = tableName;

    // Remove
    id = Const.toID(tableName);

    // Set the id to a certain standard...
    id = Settings.getPhysicalTableIDPrefix() + id;
    if (Settings.isAnIdUppercase())
      id = id.toUpperCase();

    if (schemaMeta.findPhysicalTable(id) != null) {
      // find a new name for the table: add " 2", " 3", " 4", ... to name:
      int copy = 2;
      String newname = id + " " + copy; //$NON-NLS-1$
      while (schemaMeta.findPhysicalTable(newname) != null) {
        copy++;
        newname = id + " " + copy; //$NON-NLS-1$
      }
      id = newname;
    }

    PhysicalTable physicalTable = new PhysicalTable(id, schemaName, tableName, database.getDatabaseMeta(), fields);

    // Also set a localized description...
    String niceName = beautifyName(tablename);
    physicalTable.getConcept().setName(schemaMeta.getActiveLocale(), niceName);

    DatabaseMeta dbMeta = database.getDatabaseMeta();
    String schemaTableCombination = dbMeta.getSchemaTableCombination(dbMeta.quoteField(schemaName), dbMeta
        .quoteField(tableName));

    Row row = database.getTableFields(schemaTableCombination);

    if (row != null && row.size() > 0) {
      for (int i = 0; i < row.size(); i++) {
        Value v = row.getValue(i);
        PhysicalColumn physicalColumn = importPhysicalColumnDefinition(v, physicalTable);
        try {
          fields.add(physicalColumn);
        } catch (ObjectAlreadyExistsException e) {
          // Don't add this column
          // TODO: show error dialog.
        }
      }
    }
    String upper = tablename.toUpperCase();

    if (upper.startsWith("D_") || upper.startsWith("DIM") || upper.endsWith("DIM"))physicalTable.setTableType(TableTypeSettings.DIMENSION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    if (upper.startsWith("F_") || upper.startsWith("FACT") || upper.endsWith("FACT"))physicalTable.setTableType(TableTypeSettings.FACT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    try {
      schemaMeta.addTable(physicalTable);
    } catch (ObjectAlreadyExistsException e) {
      new ErrorDialog(
          shell,
          Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_PHYICAL_TABLE_EXISTS", physicalTable.getId()), e); //$NON-NLS-1$ //$NON-NLS-2$
    }

  }

  private PhysicalColumn importPhysicalColumnDefinition(Value v, PhysicalTable physicalTable) {
    // The id
    String id = Settings.getPhysicalColumnIDPrefix() + v.getName();
    if (Settings.isAnIdUppercase())
      id = id.toUpperCase();

    // The name of the column in the database
    String dbname = v.getName();

    // The field type?
    FieldTypeSettings fieldType = FieldTypeSettings.guessFieldType(v.getName());

    // Create a physical column.
    PhysicalColumn physicalColumn = new PhysicalColumn(v.getName(), dbname, fieldType, AggregationSettings.NONE,
        physicalTable);

    // Set the localised name...
    String niceName = beautifyName(v.getName());
    physicalColumn.setName(schemaMeta.getActiveLocale(), niceName);

    // Set the parent concept to the base concept...
    physicalColumn.getConcept().setParentInterface(schemaMeta.findConcept(Settings.getConceptNameBase()));

    // The data type...
    DataTypeSettings dataTypeSettings = getDataTypeSettings(v);
    ConceptPropertyInterface dataTypeProperty = new ConceptPropertyDataType(DefaultPropertyID.DATA_TYPE.getId(),
        dataTypeSettings);
    physicalColumn.getConcept().addProperty(dataTypeProperty);

    // It this a key field? If yes: set the appropriate parent concept...
    if (fieldType.equals(FieldTypeSettings.KEY)) {
      ConceptInterface parentIDConcept = schemaMeta.findConcept(Settings.getConceptNameID());
      if (parentIDConcept != null)
        physicalColumn.getConcept().setParentInterface(parentIDConcept);
    }

    return physicalColumn;
  }

  private static final String beautifyName(String name) {
    return new Value("niceName", name).replace("_", " ").initcap().getString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  private DataTypeSettings getDataTypeSettings(Value v) {
    DataTypeSettings dataTypeSettings = new DataTypeSettings(DataTypeSettings.DATA_TYPE_STRING);
    switch (v.getType()) {
      case Value.VALUE_TYPE_BIGNUMBER:
      case Value.VALUE_TYPE_INTEGER:
      case Value.VALUE_TYPE_NUMBER:
        dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_NUMERIC);
        break;

      case Value.VALUE_TYPE_BINARY:
        dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_BINARY);
        break;

      case Value.VALUE_TYPE_BOOLEAN:
        dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_BOOLEAN);
        break;

      case Value.VALUE_TYPE_DATE:
        dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_DATE);
        break;

      case Value.VALUE_TYPE_STRING:
        dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_STRING);
        break;

      case Value.VALUE_TYPE_NONE:
        dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_UNKNOWN);
        break;

      default:
        break;
    }
    dataTypeSettings.setLength(v.getLength());
    dataTypeSettings.setPrecision(v.getPrecision());

    return dataTypeSettings;
  }

  public void exploreDB() {
    // Determine what menu we selected from...

    TreeItem ti[] = treeViewer.getTree().getSelection();

    // Then call editConnection or editStep or editTrans
    if (ti.length == 1) {
      String name = ti[0].getText();
      TreeItem parent = ti[0].getParentItem();
      if (parent != null) {
        String type = parent.getText();
        if (type.equalsIgnoreCase(STRING_CONNECTIONS)) {
          DatabaseMeta dbinfo = schemaMeta.findDatabase(name);
          if (dbinfo != null) {
            DatabaseExplorerDialog std = new DatabaseExplorerDialog(shell, SWT.NONE, dbinfo, schemaMeta.databases
                .getList(), true);
            std.open();
          } else {
            MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
            mb.setMessage(Messages.getString("MetaEditor.USER_ERROR_CANT_FIND_CONNECTION")); //$NON-NLS-1$
            mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
            mb.open();
          }
        }
      } else {
        if (name.equalsIgnoreCase(STRING_CONNECTIONS))
          DBCache.getInstance().clear(null);
      }
    }
  }

  public String toString() {
    return this.getClass().getName();
  }

  public static void main(String[] args) throws Exception {
    EnvUtil.environmentInit();
    LogWriter log = LogWriter.getInstance(Const.META_EDITOR_LOG_FILE, false, LogWriter.LOG_LEVEL_BASIC);
    LogWriter.setLayout(new Log4jPMELayout(true));

    Display display = new Display();

    if (!Props.isInitialized()) {
      Const.checkPentahoMetadataDirectory();
      Props.init(display, Const.getPropertiesFile()); // things to remember...
    }

    // Init steps, jobentries, plugins...
    StepLoader.getInstance().read();
    JobEntryLoader.getInstance().read();

    Splash splash = new Splash(display);

    final MetaEditor win = new MetaEditor(log, display);

    // Read kettle transformation specified on command-line?
    if (args.length == 1 && !Const.isEmpty(args[0])) {
      if (CWM.exists(args[0])) // Only try to load the domain if it exists.
      {
        win.cwm = CWM.getInstance(args[0]);
        CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
        win.schemaMeta = cwmSchemaFactory.getSchemaMeta(win.cwm);
        win.setDomainName(args[0]);
        win.schemaMeta.clearChanged();
      } else {
        win.newFile();
      }
    } else {
      if (win.props.openLastFile()) {
        String lastfiles[] = win.props.getLastFiles();
        if (lastfiles.length > 0) {
          try {
            if (CWM.exists(lastfiles[0])) // Only try to load the domain if it exists.
            {
              win.cwm = CWM.getInstance(lastfiles[0]);
              CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
              win.schemaMeta = cwmSchemaFactory.getSchemaMeta(win.cwm);
              win.setDomainName(lastfiles[0]);
              win.schemaMeta.clearChanged();
            } else {
              win.newFile();
            }
          } catch (Exception e) {
            log.logError(APPLICATION_NAME, Messages.getString(
                "MetaEditor.ERROR_0001_CANT_CHECK_DOMAIN_EXISTENCE", e.toString())); //$NON-NLS-1$
            log.logError(APPLICATION_NAME, Const.getStackTracker(e));
          }
        } else {
          win.newFile();
        }
      } else {
        win.newFile();
      }
    }

    splash.hide();

    win.open();
    while (!win.isDisposed()) {
      if (!win.readAndDispatch())
        win.sleep();
    }
    win.dispose();

    // Close the logfile...
    log.close();
  }

  /**
   * @return the schemaMeta
   */
  public SchemaMeta getSchemaMeta() {
    return schemaMeta;
  }

  /**
   * @param schemaMeta the schemaMeta to set
   */
  public void setSchemaMeta(SchemaMeta schemaMeta) {
    this.schemaMeta = schemaMeta;
  }

/*
  public void editBusinessTable(String businessTableName) {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if (activeModel == null)
      return;

    BusinessTable businessTable = activeModel.findBusinessTable(schemaMeta.getActiveLocale(), businessTableName);
    if (businessTable != null) {
      editBusinessTable(businessTable);
    }
  }
*/

  public void editBusinessTable(BusinessTable businessTable) {
    editBusinessTable(businessTable, null);
  }

  /**
   * TODO mlowery move this business save logic to a method for reuse
   */

  private void editBusinessTable(BusinessTable businessTable, ConceptTreeNode node) {

    if (businessTable != null) {

      BusinessTable copy = (BusinessTable) businessTable.clone();
      BusinessTableModel tableModel = new BusinessTableModel(copy);

      BusinessTableDialog td = new BusinessTableDialog(shell, SWT.NONE, tableModel, schemaMeta);
      int res = td.open();

      if (Window.OK == res) {

        // It's important to preserve the ConceptInterface instances (rather 
        // than replacing them), as the instance references are important to  
        // the inheritance chain among the concept business objects. 

        ConceptInterface originalInterface = businessTable.getConcept();
        originalInterface.clearChildProperties();
        originalInterface.getChildPropertyInterfaces().putAll(copy.getConcept().getChildPropertyInterfaces());        

    businessTable.setPhysicalTable(copy.getPhysicalTable());

    for (int i = businessTable.nrBusinessColumns() - 1; i >= 0; i--) {
      businessTable.removeBusinessColumn(i);
    }

    Iterator iter = copy.getBusinessColumns().iterator();
    while (iter.hasNext()) {
      BusinessColumn column = (BusinessColumn) iter.next();
      try {
        businessTable.addBusinessColumn(column);
      } catch (ObjectAlreadyExistsException e) {
        e.printStackTrace();
            log.logDebug(APPLICATION_NAME,"This should not happen as this exception would already have been caught earlier..."); //$NON-NLS-1$
      }
    }

    if (node != null){
      node.sync();
    } else{
      synchronize(businessTable);
    }
    refreshAll();


    }
    }


   }

  public void dupeBusinessTable(BusinessTable businessTable) {
    if (businessTable != null) {
      log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_DUPLICATE_TABLE", businessTable.getId())); //$NON-NLS-1$
      BusinessModel activeModel = schemaMeta.getActiveModel();
      String locale = schemaMeta.getActiveLocale();

      BusinessTable newTable = (BusinessTable) businessTable.clone();
      if (newTable != null) {
        try {
          String newname = businessTable.getId() + " (copy)"; //$NON-NLS-1$
          int nr = 2;
          while (activeModel.findBusinessTable(locale, newname) != null) {
            newname = businessTable.getId() + " (copy " + nr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            nr++;
          }
          newTable.setId(newname);

          activeModel.addBusinessTable(newTable);
          if (activeModelTreeNode != null)
            activeModelTreeNode.getBusinessTablesRoot().addDomainChild(newTable);
          refreshGraph();
        } catch (ObjectAlreadyExistsException e) {
          new ErrorDialog(
              shell,
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_BUSINESS_TABLE_NAME_EXISTS"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
  }

  /**
   * Test Query & Reporting
   *
   */
  protected void testQR() {
    try {
      QueryDialog queryDialog = new QueryDialog(shell, schemaMeta, query);
      MQLQuery lastQuery = queryDialog.open();
      if (lastQuery != null) {
        query = lastQuery;
        saveQuery();
      }
      /*
       * query = MakeSelectionDemo.executeDemo(shell, props, query, false); // Don't shut down, let it be. if
       * (query!=null) { saveQuery(); }
       */
    } catch (Exception e) {
      new ErrorDialog(shell,
          Messages.getString("MetaEditor.USER_TITLE_DEMO_ERROR"), Messages.getString("MetaEditor.USER_DEMO_ERROR"), e); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void saveQuery() {
    try {
      if (query != null)
        query.save(Const.getQueryFile());
    } catch (Exception e) {
      log.logError(APPLICATION_NAME, Messages.getString("MetaEditor.ERROR_0002_CANT_SAVE_QUERY") + e.toString()); //$NON-NLS-1$
      log.logError(APPLICATION_NAME, Const.getStackTracker(e));
    }
  }

  private void loadQuery() {
    try {
      File file = new File(Const.getQueryFile());
      FileInputStream fileInputStream = new FileInputStream(file);
      byte bytes[] = new byte[(int) file.length()];
      fileInputStream.read(bytes);
      fileInputStream.close();

      query = new MQLQuery(new String(bytes, Const.XML_ENCODING), Const.XML_ENCODING, cwmSchemaFactory);
    } catch (Exception e) {
      log.logError(APPLICATION_NAME, Messages.getString("MetaEditor.ERROR_0003_CANT_LOAD_QUERY", e.toString())); //$NON-NLS-1$
    }
  }

  public void editSecurityService() {
    SecurityDialog dialog = new SecurityDialog(shell, schemaMeta.getSecurityReference().getSecurityService());
 
//    SecurityServiceDialog dialog = new SecurityServiceDialog(shell, schemaMeta.getSecurityReference()
//        .getSecurityService());
    if (dialog.open() == IDialogConstants.OK_ID) {
      // try to grab it from the security service if it exists...
      SecurityService securityService = schemaMeta.getSecurityReference().getSecurityService();
      if (securityService != null) {
        try {
          schemaMeta.setSecurityReference(new SecurityReference(securityService));
        } catch (Throwable e) {
          new ErrorDialog(
              shell,
              Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_LOADING_SECURITY_INFORMATION"), //$NON-NLS-1$ //$NON-NLS-2$
              new Exception(e));
        }
      }

      refreshAll();
    }
  }

  public void getMondrianModel() {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    String locale = schemaMeta.getActiveLocale();

    if (activeModel != null) {
      try {
        String xml = activeModel.getMondrianModel(locale);

        EnterTextDialog dialog = new EnterTextDialog(
            shell,
            Messages.getString("MetaEditor.USER_TITLE_MONDRIAN_XML"), Messages.getString("MetaEditor.USER_MONDRIAN_XML"), xml); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.open();
      } catch (Exception e) {
        new ErrorDialog(
            shell,
            Messages.getString("MetaEditor.USER_TITLE_MODEL_ERROR"), Messages.getString("MetaEditor.USER_MONDRIAN_MODEL_ERROR"), e); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  /**
   * @return the selected concept utility interfaces...
   */
  public ConceptUtilityInterface[] getSelectedConceptUtilityInterfacesInMainTree() {
    List list = new ArrayList();
    TreeItem[] selection = treeViewer.getTree().getSelection();
    
    for (int i = 0; i < selection.length; i++) {
      TreeItem treeItem = selection[i];
      ConceptTreeNode node = (ConceptTreeNode) treeItem.getData();
      if (node instanceof PhysicalTableTreeNode) {
        list.add(((PhysicalTableTreeNode)node).getDomainObject());
      } else if (node instanceof PhysicalColumnTreeNode) {
        list.add(((PhysicalColumnTreeNode)node).getDomainObject());
      } else if (node instanceof BusinessModelTreeNode) {
        list.add(((BusinessModelTreeNode)node).getDomainObject());
      } else if (node instanceof BusinessTableTreeNode) {
        list.add(((BusinessTableTreeNode)node).getDomainObject());
      } else if (node instanceof CategoryTreeNode) {
        list.add(((CategoryTreeNode)node).getDomainObject());
      } else if (node instanceof BusinessColumnTreeNode) {
        list.add(((BusinessColumnTreeNode)node).getDomainObject());
      } else if (node instanceof BusinessViewTreeNode) {
        BusinessModelTreeNode modelNode = (BusinessModelTreeNode) node.getParent();
        BusinessModel model = (BusinessModel) modelNode.getDomainObject();
        BusinessCategory category = model.getRootCategory();
        if (category != null) {
          list.add(category);
        }
      }
    }

    return (ConceptUtilityInterface[]) list.toArray(new ConceptUtilityInterface[list.size()]);
  }

  protected void setParentConcept(ConceptUtilityInterface[] utilityInterfaces) {
    String[] concepts = schemaMeta.getConceptNames();

    // Ask the user to pick a parent concept...
    EnterSelectionDialog dialog = new EnterSelectionDialog(shell, concepts, Messages
        .getString("MetaEditor.USER_TITLE_SELECT_PARENT_CONCEPT"), //$NON-NLS-1$
        Messages.getString("MetaEditor.USER_SELECT_PARENT_CONCEPT")); //$NON-NLS-1$
    String conceptName = dialog.open();
    if (conceptName != null) {
      ConceptInterface parentInterface = schemaMeta.findConcept(conceptName);

      for (int u = 0; u < utilityInterfaces.length; u++) {
        utilityInterfaces[u].getConcept().setParentInterface(parentInterface);
        utilityInterfaces[u].setChanged();
      }

      refreshAll();
    }
  }

  protected void clearParentConcept(ConceptUtilityInterface[] utilityInterfaces) {
    for (int u = 0; u < utilityInterfaces.length; u++) {
      utilityInterfaces[u].getConcept().setParentInterface(null);
      utilityInterfaces[u].setChanged();
    }

    refreshAll();
  }

  protected void removeChildProperties(ConceptUtilityInterface[] utilityInterfaces) {
    // First we need a distinct list of all property IDs...
    Map all = new Hashtable();
    for (int u = 0; u < utilityInterfaces.length; u++) {
      String ids[] = utilityInterfaces[u].getConcept().getChildPropertyIDs();
      for (int i = 0; i < ids.length; i++) {
        all.put(ids[i], ""); //$NON-NLS-1$
      }
    }
    Set keySet = all.keySet();
    String ids[] = (String[]) keySet.toArray(new String[keySet.size()]);
    String names[] = new String[ids.length];

    // Get the descriptions to show...
    for (int i = 0; i < ids.length; i++) {
      DefaultPropertyID propertyID = DefaultPropertyID.findDefaultPropertyID(ids[i]);
      if (propertyID != null)
        names[i] = propertyID.getDescription();
      else
        names[i] = ids[i];
    }

    // Ask the user to pick the child properties to delete...
    EnterSelectionDialog dialog = new EnterSelectionDialog(shell, names, Messages
        .getString("MetaEditor.USER_TITLE_DELETE_PROPERTIES"), Messages.getString("MetaEditor.USER_DELETE_PROPERTIES")); //$NON-NLS-1$ //$NON-NLS-2$
    String conceptName = dialog.open();
    if (conceptName != null) {

      for (int u = 0; u < utilityInterfaces.length; u++) {
        ConceptInterface concept = utilityInterfaces[u].getConcept();

        int idxs[] = dialog.getSelectionIndeces();
        for (int i = 0; i < idxs.length; i++) {
          ConceptPropertyInterface property = concept.getChildProperty(ids[idxs[i]]);
          if (property != null) {
            concept.removeChildProperty(property);
            utilityInterfaces[u].setChanged();
          }
        }
      }

      refreshAll();
    }
  }
  
}
