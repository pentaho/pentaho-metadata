/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
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
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.swt.widgets.Button;
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
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.BusinessView;
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
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.dialog.BusinessCategoriesDialog;
import org.pentaho.pms.schema.dialog.BusinessCategoryDialog;
import org.pentaho.pms.schema.dialog.BusinessTableDialog;
import org.pentaho.pms.schema.dialog.BusinessViewDialog;
import org.pentaho.pms.schema.dialog.PhysicalTableDialog;
import org.pentaho.pms.schema.dialog.RelationshipDialog;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.schema.security.SecurityService;
import org.pentaho.pms.schema.security.SecurityServiceDialog;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;
import org.pentaho.pms.util.Settings;
import org.pentaho.pms.util.Splash;
import org.pentaho.pms.util.dialog.EnterOptionsDialog;
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

/**
 * Class to edit the metadata model (Schema Metadata), load/store into the MDR/CWM model
 * 
 * @since 16-may-2003
 */
public class MetaEditor
{
    private CWM                       cwm;

    private LogWriter                 log;

    private Display                   disp;

    private Shell                     shell;

    private MetaEditorGraph           metaEditorGraph;

    private MetaEditorLog             metaEditorLog;

    private MetaEditorConcepts        metaEditorConcept;

    private MetaEditorOlap            metaEditorOlap;

    private SashForm                  sashform;

    private CTabFolder                tabfolder;

    private SchemaMeta                schemaMeta;

    private MQLQuery                  query;

    private ToolBar                   tBar;

    private Menu                      mBar;

    private MenuItem                  mFile;

    private Menu                      msFile;

    private MenuItem                  miFileOpen, miFileNew, miFileSave, miFileSaveAs, miFileExport, miFileImport, miFileDelete, miFilePrint,
            miFileSep3, miFileQuit;

    private Listener                  lsFileOpen, lsFileNew, lsFileSave, lsFileSaveAs, lsFileExport, lsFileImport, lsFileDelete, lsFilePrint,
            lsFileQuit;

    private MenuItem                  mEdit;

    private Menu                      msEdit;

    private MenuItem                  miEditSelectAll, miEditUnselectAll, miEditOptions;

    private Listener                  lsEditSelectAll, lsEditUnselectAll, lsEditOptions;

    private MenuItem                  mHelp;

    private Menu                      msHelp;

    private MenuItem                  miHelpAbout;

    private Listener                  lsHelpAbout;

    private SelectionAdapter          lsEditDef, lsEditMainSel, lsEditCatSel;

    public static final String        STRING_CONNECTIONS     = "Connections";

    public static final String        STRING_BUSINESS_VIEWS  = "Business Views";

    public static final String        STRING_BUSINESS_TABLES = "Business Tables";

    public static final String        STRING_RELATIONSHIPS   = "Relationships";

    public static final String        STRING_CATEGORIES      = "Categories";

    public static final String        APPLICATION_NAME       = "Pentaho Metadata Editor";

    private static final String       STRING_MAIN_TREE       = "MainTree";

    public static final String        STRING_CATEGORIES_TREE = "CategoriesTree";

    private Tree                      mainTree;

    private TreeItem                  tiConnections, tiViews;

    private Tree                      catTree;

    private TreeItem                  tiCategories;

    public KeyAdapter                 defKeys;

    public KeyAdapter                 modKeys;

    private Props                     props;

    private Menu                      catMenu;

    private MetaEditorLocales         metaEditorLocales;

    private MenuItem                  mSecurity;

    private Menu                      msSecurity;

    private MenuItem                  miSecurityService;

    private Listener                  lsSecurityService;

    private CwmSchemaFactoryInterface cwmSchemaFactory;

    private Menu                      mainMenu;

    public MetaEditor(LogWriter log)
    {
        this(log, null);
    }

    public MetaEditor(LogWriter log, Display display)
    {
        this.log = log;

        if (display != null)
        {
            disp = display;
        }
        else
        {
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

        // shell.setFont(GUIResource.getInstance().getFontDefault());
        shell.setImage(new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "icon.png")));

        defKeys = new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                boolean control = (e.stateMask & SWT.CONTROL) != 0;
                boolean alt = (e.stateMask & SWT.ALT) != 0;

                BusinessView activeView = schemaMeta.getActiveView();

                // ESC --> Unselect All steps
                if (e.keyCode == SWT.ESC)
                {
                    if (activeView != null)
                    {
                        activeView.unselectAll();
                        refreshGraph();
                    }
                    metaEditorGraph.control = false;
                }
                ;

                // F5 --> refresh
                if (e.keyCode == SWT.F5)
                {
                    refreshAll();
                    metaEditorGraph.control = false;
                }

                // F8 --> generate Mondrian model
                if (e.keyCode == SWT.F8)
                {
                    getMondrianModel();
                    metaEditorGraph.control = false;
                }

                // CTRL-A --> Select All steps
                if ((int) e.character == 1 && control && !alt)
                {
                    if (activeView != null)
                    {
                        activeView.selectAll();
                        refreshGraph();
                    }
                    metaEditorGraph.control = false;
                }
                ;
                // CTRL-E --> Select All steps
                if ((int) e.character == 5 && control && !alt)
                {
                    exportToXMI();
                    metaEditorGraph.control = false;
                }
                ;
                // CTRL-I --> Select All steps
                if ((int) e.character == 9 && control && !alt)
                {
                    importFromXMI();
                    metaEditorGraph.control = false;
                }
                ;
                // CTRL-N --> new
                if ((int) e.character == 14 && control && !alt)
                {
                    newFile();
                    metaEditorGraph.control = false;
                }
                // CTRL-O --> open
                if ((int) e.character == 15 && control && !alt)
                {
                    openFile();
                    metaEditorGraph.control = false;
                }
                // CTRL-P --> print
                if ((int) e.character == 16 && control && !alt)
                {
                    printFile();
                    metaEditorGraph.control = false;
                }
                // CTRL-S --> save
                if ((int) e.character == 19 && control && !alt)
                {
                    saveFile();
                    metaEditorGraph.control = false;
                }
                // CTRL-T --> Test
                if ((int) e.character == 20 && control && !alt)
                {
                    testQR();
                    metaEditorGraph.control = false;
                }
            }
        };
        modKeys = new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.keyCode == SWT.SHIFT) metaEditorGraph.shift = true;
                if (e.keyCode == SWT.CONTROL) metaEditorGraph.control = true;
            }

            public void keyReleased(KeyEvent e)
            {
                if (e.keyCode == SWT.SHIFT) metaEditorGraph.shift = false;
                if (e.keyCode == SWT.CONTROL) metaEditorGraph.control = false;
            }
        };

        addBar();

        FormData fdBar = new FormData();
        fdBar.left = new FormAttachment(0, 0);
        fdBar.top = new FormAttachment(0, 0);
        tBar.setLayoutData(fdBar);

        sashform = new SashForm(shell, SWT.HORIZONTAL);

        FormData fdSash = new FormData();
        fdSash.left = new FormAttachment(0, 0);
        fdSash.top = new FormAttachment(tBar, 0);
        fdSash.bottom = new FormAttachment(100, 0);
        fdSash.right = new FormAttachment(100, 0);
        sashform.setLayoutData(fdSash);

        addMenu();
        addTree();
        addTabs();

        setTreeImages();

        // In case someone dares to press the [X] in the corner ;-)
        shell.addShellListener(new ShellAdapter()
        {
            public void shellClosed(ShellEvent e)
            {
                e.doit = quitFile();
            }
        });
        int weights[] = props.getSashWeights();
        sashform.setWeights(weights);
        sashform.setVisible(true);

        shell.layout();
    }

    public void exportToXMI()
    {
        boolean goAhead = true;
        if (schemaMeta.hasChanged())
        {
            MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
            mb.setMessage("This model first needs to be saved into the repository before we can export from it.");
            mb.setText("Do you want to continue?");
            if (mb.open() == SWT.YES)
            {
                goAhead = saveFile();
            }
            else
            {
                goAhead = false;
            }
        }
        if (goAhead)
        {
            FileDialog dialog = new FileDialog(shell, SWT.SAVE);
            dialog.setFilterExtensions(new String[] { "*.xmi", "*.xml", "*.*" });
            dialog.setFilterNames(new String[] { "XMI files", "XML files", "All files" });
            String filename = dialog.open();
            if (filename != null)
            {
                if (!filename.toLowerCase().endsWith(".xmi") && !filename.toLowerCase().endsWith(".xml"))
                {
                    filename += ".xmi";
                }

                // Get back the result of the last save operation...
                CWM cwm = CWM.getInstance(schemaMeta.getModelName());

                if (cwm != null)
                {
                    try
                    {
                        cwm.exportToXMI(filename);
                    }
                    catch (Exception e)
                    {
                        new ErrorDialog(shell, "Error", "There was an error exporting to XMI", e);
                    }
                }
            }
        }
    }

    public void importFromXMI()
    {
        if (showChangedWarning())
        {
            FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
            fileDialog.setFilterExtensions(new String[] { "*.xmi", "*.xml", "*.*" });
            fileDialog.setFilterNames(new String[] { "XMI files", "XML files", "All files" });
            String filename = fileDialog.open();
            if (filename != null)
            {
                try
                {
                    // Ask for a new model to import into...
                    //
                    EnterStringDialog stringDialog = new EnterStringDialog(shell, "", "Save model", "Enter a name for this model");
                    String modelName = stringDialog.open();
                    if (modelName != null)
                    {
                        int id = SWT.YES;
                        if (CWM.exists(modelName))
                        {
                            MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
                            mb.setMessage("This model already exists in the repository.  Do you want to continue and overwrite it?");
                            mb.setText("This model already exists!");
                            id = mb.open();
                        }
                        if (id == SWT.YES)
                        {
                            CWM delCwm = CWM.getInstance(modelName);
                            delCwm.removeModel();
                        }
                        else
                        {
                            return; // no selected.
                        }

                        // Now create a new Model...
                        CWM cwm = CWM.getInstance(modelName);

                        // import it all...
                        cwm.importFromXMI(filename);

                        // convert to a schema
                        schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);

                        // refresh it all...
                        refreshAll();
                    }
                }
                catch (Exception e)
                {
                    new ErrorDialog(shell, "Error saving model", "There was a severe error loading this model!", e);
                }
            }
        }

    }

    public void open()
    {
        // Set the shell size, based upon previous time...
        WindowProperty winprop = props.getScreen(shell.getText());
        if (winprop != null)
            winprop.setShell(shell);
        else
            shell.pack();

        shell.open();

        // Perhaps the transformation contains elements at startup?
        if (schemaMeta.nrTables() > 0 || schemaMeta.nrDatabases() > 0)
        {
            refreshAll(); // Do a complete refresh then...
        }
    }

    public boolean readAndDispatch()
    {
        return disp.readAndDispatch();
    }

    public void dispose()
    {
        try
        {
            CWM.quitAndSync();
            disp.dispose();
        }
        catch (Exception e)
        {
            new ErrorDialog(shell, "Error shutting down repository!", "There was an unexpected error shutting down the repository!", e);
        }
    }

    public boolean isDisposed()
    {
        return disp.isDisposed();
    }

    public void sleep()
    {
        disp.sleep();
    }

    public void addMenu()
    {
        mBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(mBar);

        // main File menu...
        mFile = new MenuItem(mBar, SWT.CASCADE);
        mFile.setText("&File");
        msFile = new Menu(shell, SWT.DROP_DOWN);
        mFile.setMenu(msFile);

        miFileNew = new MenuItem(msFile, SWT.CASCADE);
        miFileNew.setText("&New \tCTRL-N");
        miFileOpen = new MenuItem(msFile, SWT.CASCADE);
        miFileOpen.setText("&Open \tCTRL-O");
        miFileSave = new MenuItem(msFile, SWT.CASCADE);
        miFileSave.setText("&Save \tCTRL-S");
        miFileSaveAs = new MenuItem(msFile, SWT.CASCADE);
        miFileSaveAs.setText("Save &as...");
        new MenuItem(msFile, SWT.SEPARATOR);
        miFileImport = new MenuItem(msFile, SWT.CASCADE);
        miFileImport.setText("&Import from an XMI file\tCTRL-I");
        miFileExport = new MenuItem(msFile, SWT.CASCADE);
        miFileExport.setText("&Export to an XMI file\tCTRL-E");
        new MenuItem(msFile, SWT.SEPARATOR);
        miFileDelete = new MenuItem(msFile, SWT.CASCADE);
        miFileDelete.setText("Delete model");
        new MenuItem(msFile, SWT.SEPARATOR);
        miFilePrint = new MenuItem(msFile, SWT.CASCADE);
        miFilePrint.setText("&Print \tCTRL-P");
        new MenuItem(msFile, SWT.SEPARATOR);
        miFileQuit = new MenuItem(msFile, SWT.CASCADE);
        miFileQuit.setText("&Quit");
        miFileSep3 = new MenuItem(msFile, SWT.SEPARATOR);
        addMenuLast();

        lsFileOpen = new Listener()
        {
            public void handleEvent(Event e)
            {
                openFile();
            }
        };
        lsFileNew = new Listener()
        {
            public void handleEvent(Event e)
            {
                newFile();
            }
        };
        lsFileSave = new Listener()
        {
            public void handleEvent(Event e)
            {
                saveFile();
            }
        };
        lsFileSaveAs = new Listener()
        {
            public void handleEvent(Event e)
            {
                saveFileAs();
            }
        };
        lsFileExport = new Listener()
        {
            public void handleEvent(Event e)
            {
                exportToXMI();
            }
        };
        lsFileImport = new Listener()
        {
            public void handleEvent(Event e)
            {
                importFromXMI();
            }
        };
        lsFileDelete = new Listener()
        {
            public void handleEvent(Event e)
            {
                deleteFile();
            }
        };
        lsFilePrint = new Listener()
        {
            public void handleEvent(Event e)
            {
                printFile();
            }
        };
        lsFileQuit = new Listener()
        {
            public void handleEvent(Event e)
            {
                quitFile();
            }
        };

        miFileOpen.addListener(SWT.Selection, lsFileOpen);
        miFileNew.addListener(SWT.Selection, lsFileNew);
        miFileSave.addListener(SWT.Selection, lsFileSave);
        miFileSaveAs.addListener(SWT.Selection, lsFileSaveAs);
        miFileExport.addListener(SWT.Selection, lsFileExport);
        miFileImport.addListener(SWT.Selection, lsFileImport);
        miFileDelete.addListener(SWT.Selection, lsFileDelete);
        miFilePrint.addListener(SWT.Selection, lsFilePrint);
        miFileQuit.addListener(SWT.Selection, lsFileQuit);

        // main Edit menu...
        mEdit = new MenuItem(mBar, SWT.CASCADE);
        mEdit.setText("&Edit");
        msEdit = new Menu(shell, SWT.DROP_DOWN);
        mEdit.setMenu(msEdit);
        miEditUnselectAll = new MenuItem(msEdit, SWT.CASCADE);
        miEditUnselectAll.setText("&Clear selection \tESC");
        miEditSelectAll = new MenuItem(msEdit, SWT.CASCADE);
        miEditSelectAll.setText("&Select all steps \tCTRL-A");
        new MenuItem(msEdit, SWT.SEPARATOR);
        miEditOptions = new MenuItem(msEdit, SWT.CASCADE);
        miEditOptions.setText("&Refresh \tF5");
        new MenuItem(msEdit, SWT.SEPARATOR);
        miEditOptions = new MenuItem(msEdit, SWT.CASCADE);
        miEditOptions.setText("&Options...");

        lsEditUnselectAll = new Listener()
        {
            public void handleEvent(Event e)
            {
                editUnselectAll();
            }
        };
        lsEditSelectAll = new Listener()
        {
            public void handleEvent(Event e)
            {
                editSelectAll();
            }
        };
        lsEditOptions = new Listener()
        {
            public void handleEvent(Event e)
            {
                editOptions();
            }
        };

        miEditUnselectAll.addListener(SWT.Selection, lsEditUnselectAll);
        miEditSelectAll.addListener(SWT.Selection, lsEditSelectAll);
        miEditOptions.addListener(SWT.Selection, lsEditOptions);

        // Security
        mSecurity = new MenuItem(mBar, SWT.CASCADE);
        mSecurity.setText("&Security");
        msSecurity = new Menu(shell, SWT.DROP_DOWN);
        mSecurity.setMenu(msSecurity);
        miSecurityService = new MenuItem(msSecurity, SWT.CASCADE);
        miSecurityService.setText("&Configure security service");

        lsSecurityService = new Listener()
        {
            public void handleEvent(Event e)
            {
                editSecurityService();
            }
        };
        miSecurityService.addListener(SWT.Selection, lsSecurityService);

        // main Help menu...
        mHelp = new MenuItem(mBar, SWT.CASCADE);
        mHelp.setText("&Help");
        msHelp = new Menu(shell, SWT.DROP_DOWN);
        mHelp.setMenu(msHelp);
        miHelpAbout = new MenuItem(msHelp, SWT.CASCADE);
        miHelpAbout.setText("&About");
        lsHelpAbout = new Listener()
        {
            public void handleEvent(Event e)
            {
                helpAbout();
            }
        };
        miHelpAbout.addListener(SWT.Selection, lsHelpAbout);
    }

    private void addMenuLast()
    {
        int idx = msFile.indexOf(miFileSep3);
        int max = msFile.getItemCount();

        // Remove everything until end...
        for (int i = max - 1; i > idx; i--)
        {
            MenuItem mi = msFile.getItem(i);
            mi.dispose();
        }

        // Previously loaded files...
        String lf[] = props.getLastFiles();

        for (int i = 0; i < lf.length; i++)
        {
            MenuItem miFileLast = new MenuItem(msFile, SWT.CASCADE);
            char chr = (char) ('1' + i);
            int accel = SWT.CTRL | chr;
            miFileLast.setText("&" + chr + "  " + lf[i] + " \tCTRL-" + chr);
            miFileLast.setAccelerator(accel);
            final String fn = lf[i];

            Listener lsFileLast = new Listener()
            {
                public void handleEvent(Event e)
                {
                    if (showChangedWarning())
                    {
                        if (readData(fn))
                        {
                            schemaMeta.clearChanged();
                            setModelname(fn);
                            metaEditorGraph.control = false;
                        }
                        else
                        {
                            MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                            mb.setMessage("Error opening model : " + fn + Const.CR + "See the log for more information.");
                            mb.setText("Error!");
                            mb.open();
                        }
                    }
                }
            };
            miFileLast.addListener(SWT.Selection, lsFileLast);
        }
    }

    private void addBar()
    {
        tBar = new ToolBar(shell, SWT.HORIZONTAL | SWT.FLAT);
        // tBar.setFont(GUIResource.getInstance().getFontDefault());

        // tBar.setSize(200, 20);
        final ToolItem tiFileNew = new ToolItem(tBar, SWT.PUSH);
        final Image imFileNew = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "new.png"));
        tiFileNew.setImage(imFileNew);
        tiFileNew.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                newFile();
            }
        });
        tiFileNew.setToolTipText("New file, clear all settings");

        final ToolItem tiFileOpen = new ToolItem(tBar, SWT.PUSH);
        final Image imFileOpen = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "open.png"));
        tiFileOpen.setImage(imFileOpen);
        tiFileOpen.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                openFile();
            }
        });
        tiFileOpen.setToolTipText("Open file");

        final ToolItem tiFileSave = new ToolItem(tBar, SWT.PUSH);
        final Image imFileSave = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "save.png"));
        tiFileSave.setImage(imFileSave);
        tiFileSave.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                saveFile();
            }
        });
        tiFileSave.setToolTipText("Save current file");

        final ToolItem tiFileSaveAs = new ToolItem(tBar, SWT.PUSH);
        final Image imFileSaveAs = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "saveas.png"));
        tiFileSaveAs.setImage(imFileSaveAs);
        tiFileSaveAs.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                saveFileAs();
            }
        });
        tiFileSaveAs.setToolTipText("Save file with different name");

        new ToolItem(tBar, SWT.SEPARATOR);
        final ToolItem tiFilePrint = new ToolItem(tBar, SWT.PUSH);
        final Image imFilePrint = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "print.png"));
        tiFilePrint.setImage(imFilePrint);
        tiFilePrint.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                printFile();
            }
        });
        tiFilePrint.setToolTipText("Print");

        new ToolItem(tBar, SWT.SEPARATOR);
        final ToolItem tiSQL = new ToolItem(tBar, SWT.PUSH);
        final Image imSQL = new Image(disp, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "SQLbutton.png"));
        // Can't seem to get the transparency correct for this image!
        ImageData idSQL = imSQL.getImageData();
        int sqlPixel = idSQL.palette.getPixel(new RGB(255, 255, 255));
        idSQL.transparentPixel = sqlPixel;
        Image imSQL2 = new Image(disp, idSQL);
        tiSQL.setImage(imSQL2);
        tiSQL.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                testQR();
            }
        });
        tiSQL.setToolTipText("Test Querying and Reporting : Selection and SQL generation");

        tBar.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                imFileNew.dispose();
                imFileOpen.dispose();
                imFileSave.dispose();
                imFileSaveAs.dispose();
            }
        });
        tBar.addKeyListener(defKeys);
        tBar.addKeyListener(modKeys);
        tBar.pack();
    }

    private void addTree()
    {
        SashForm leftsplit = new SashForm(sashform, SWT.VERTICAL);
        leftsplit.setLayout(new FillLayout());

        // Main: the top left tree containing connections, physical tables, business views, etc.
        //
        Composite compMain = new Composite(leftsplit, SWT.NONE);
        compMain.setLayout(new FillLayout());

        // Now set up the main tree (top left part of the screen)
        int treeFlags = SWT.BORDER;
        if (Const.isOSX())
        {
            treeFlags|=SWT.SINGLE;
        }
        else
        {
            treeFlags|=SWT.MULTI;
        }
        mainTree = new Tree(compMain, treeFlags);
        mainTree.setHeaderVisible(true);

        // Show the concept in an extra column next to the tree
        TreeColumn mainObject = new TreeColumn(mainTree, SWT.LEFT);
        mainObject.setText(""); //$NON-NLS-1$
        mainObject.setWidth(200);

        TreeColumn mainConcept = new TreeColumn(mainTree, SWT.LEFT);
        mainConcept.setText("Parent concept"); //$NON-NLS-1$
        mainConcept.setWidth(200);

        // mainTree.setFont(GUIResource.getInstance().getFontDefault());
        tiConnections = new TreeItem(mainTree, SWT.NONE);
        tiConnections.setText(STRING_CONNECTIONS);

        tiConnections.setExpanded(true);

        mainTree.setBackground(GUIResource.getInstance().getColorBackground());

        // Default selection (double-click, enter)
        lsEditDef = new SelectionAdapter()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                doubleClickedMain();
            }
        };
        mainTree.addSelectionListener(lsEditDef); // double click somewhere in the tree...

        // Normal selection: right click
        lsEditMainSel = new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                setMenuMain(e);
            }
        };
        mainTree.addSelectionListener(lsEditMainSel);

        // Normal selection: left click to select business view
        SelectionListener lsSelView = new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                setActiveBusinessView(e);
            }
        };
        mainTree.addSelectionListener(lsSelView);

        tiViews = new TreeItem(mainTree, SWT.NONE);
        tiViews.setText(STRING_BUSINESS_VIEWS);
        tiViews.setExpanded(true);

        // Left bottom tree containing categories and selectable business columns.
        //
        Composite compCategories = new Composite(leftsplit, SWT.NONE);
        compCategories.setLayout(new FormLayout());

        Button editCategories = new Button(compCategories, SWT.PUSH);
        editCategories.setText("Categories editor");
        props.setLook(editCategories);
        editCategories.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent arg0)
            {
                BusinessView activeView = schemaMeta.getActiveView();
                if (activeView != null)
                {
                    BusinessCategoriesDialog dialog = new BusinessCategoriesDialog(shell, activeView, schemaMeta.getLocales(), schemaMeta
                            .getSecurityReference());
                    dialog.open();
                    refreshCategoriesTree();
                }
            }
        });
        FormData fdEditCategories = new FormData();
        fdEditCategories.top = new FormAttachment(0, 0);
        fdEditCategories.left = new FormAttachment(0, Const.MARGIN);
        fdEditCategories.right = new FormAttachment(100, -Const.MARGIN);
        editCategories.setLayoutData(fdEditCategories);

        // Now set up the main CSH tree
        catTree = new Tree(compCategories, SWT.MULTI | SWT.BORDER);
        catTree.setHeaderVisible(true);
        tiCategories = new TreeItem(catTree, SWT.NONE);
        tiCategories.setText(STRING_CATEGORIES);
        FormData fdCatTree = new FormData();
        fdCatTree.left = new FormAttachment(0, 0);
        fdCatTree.right = new FormAttachment(100, 0);
        fdCatTree.top = new FormAttachment(editCategories, 0);
        fdCatTree.bottom = new FormAttachment(100, 0);
        catTree.setLayoutData(fdCatTree);

        // Show the concept in an extra column next to the tree
        TreeColumn catObject = new TreeColumn(catTree, SWT.LEFT);
        catObject.setText("Category/Column");
        catObject.setWidth(200);

        TreeColumn catConcept = new TreeColumn(catTree, SWT.LEFT);
        catConcept.setText("Parent concept");
        catConcept.setWidth(200);

        // Right click in tree: set a menu
        lsEditCatSel = new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                setMenuCategories(e);
            }
        };
        catTree.addSelectionListener(lsEditCatSel);

        // Double click in categories menu
        catTree.addSelectionListener(new SelectionAdapter()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                doubleClickedCategories();
            }
        });

        leftsplit.setWeights(new int[] { 60, 40 });

        addDragSourceToTree(mainTree);
        addDropTargetToTree(catTree);
        addKeyListenerToCategoriesTree();

        // Add tree memories to the trees.
        TreeMemory.addTreeListener(mainTree, STRING_MAIN_TREE);
        TreeMemory.addTreeListener(catTree, STRING_CATEGORIES_TREE);

        // Set the Views item expanded by default...
        TreeMemory.getInstance().storeExpanded(STRING_MAIN_TREE, Const.getTreeStrings(tiViews), true);

        // Keyboard shortcuts!
        mainTree.addKeyListener(defKeys);
        mainTree.addKeyListener(modKeys);
    }

    private void addKeyListenerToCategoriesTree()
    {
        catTree.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                final BusinessView activeView = schemaMeta.getActiveView();
                if (activeView == null) return;
                final String activeLocale = schemaMeta.getActiveLocale();

                TreeItem[] selection = catTree.getSelection();
                if (selection.length == 0 || selection.length > 1) return;
                final TreeItem treeItem = selection[0];

                final String itemText = treeItem.getText();
                final String[] path = Const.getTreeStrings(treeItem, 1);
                final boolean isLowestLevel = treeItem.getItemCount() == 0; // no children

                final BusinessCategory businessCategory = activeView.findBusinessCategory(path, activeLocale);
                final BusinessCategory parentCategory;

                if (path.length > 1)
                {
                    String[] parentPath = new String[path.length - 1];
                    for (int i = 0; i < parentPath.length; i++)
                        parentPath[i] = path[i];
                    parentCategory = activeView.findBusinessCategory(parentPath, activeLocale);
                }
                else
                {
                    parentCategory = activeView.getRootCategory();
                }

                if (path.length == 0)
                {
                    // Nothing really
                }
                else
                    //
                    if (isLowestLevel && businessCategory != null)
                    {
                        final BusinessColumn businessColumn = businessCategory.findBusinessColumn(itemText, false, activeLocale);
                        if (businessColumn != null)
                        {
                            if (e.keyCode == SWT.ARROW_UP && ((e.stateMask & SWT.CTRL) != 0))
                            {
                                moveBusinessColumnUp(businessCategory, businessColumn);
                                selectTreeItem(catTree, path);
                            }
                            if (e.keyCode == SWT.ARROW_DOWN && ((e.stateMask & SWT.CTRL) != 0))
                            {
                                moveBusinessColumnDown(businessCategory, businessColumn);
                                selectTreeItem(catTree, path);
                            }
                        }
                    }
                    // We're typing in the tree, not at the top, not at the column level: it's in a business category
                    // 
                    else
                    {
                        if (e.keyCode == SWT.ARROW_UP && ((e.stateMask & SWT.CTRL) != 0))
                        {
                            moveBusinessCategoryUp(parentCategory, businessCategory);
                            selectTreeItem(catTree, path);
                        }
                        if (e.keyCode == SWT.ARROW_DOWN && ((e.stateMask & SWT.CTRL) != 0))
                        {
                            moveBusinessCategoryDown(parentCategory, businessCategory);
                            selectTreeItem(catTree, path);
                        }
                    }
            }
        });

    }

    public static final void selectTreeItem(Tree tree, String[] path)
    {
        TreeItem findIt = findTreeItem(tree, path);
        if (findIt != null) tree.setSelection(findIt);
    }

    public static final TreeItem findTreeItem(Tree tree, String[] path)
    {
        if (path == null || path.length == 0) return null;

        int depth = 0;
        TreeItem[] treeItems = tree.getItems();
        TreeItem follow = null;
        for (int i = 0; i < treeItems.length && follow == null; i++)
        {
            if (treeItems[i].getText().equals(path[depth])) follow = treeItems[i];
        }
        depth++;
        while (follow != null && depth < path.length)
        {
            treeItems = follow.getItems();
            follow = null;
            for (int i = 0; i < treeItems.length && follow == null; i++)
            {
                if (treeItems[i].getText().equals(path[depth])) follow = treeItems[i];
            }
            depth++;
        }
        return follow;
    }

    private void addDropTargetToTree(final Tree tree)
    {
        // Drag & Drop for tables etc.
        Transfer[] ttypes = new Transfer[] { XMLTransfer.getInstance() };
        DropTarget ddTarget = new DropTarget(tree, DND.DROP_MOVE);
        ddTarget.setTransfer(ttypes);
        ddTarget.addDropListener(new DropTargetListener()
        {
            public void dragEnter(DropTargetEvent event)
            {
            }

            public void dragLeave(DropTargetEvent event)
            {
            }

            public void dragOperationChanged(DropTargetEvent event)
            {
            }

            public void dragOver(DropTargetEvent event)
            {
            }

            public void drop(DropTargetEvent event)
            {
                BusinessView activeView = schemaMeta.getActiveView();
                String activeLocale = schemaMeta.getActiveLocale();

                // no data to copy, indicate failure in event.detail
                if (event.data == null || activeView == null)
                {
                    event.detail = DND.DROP_NONE;
                    return;
                }

                try
                {
                    // 
                    // Where exactly did we drop in the tree?
                    TreeItem treeItem = (TreeItem) event.item;
                    String path[] = Const.getTreeStrings(treeItem, 1);

                    // OK, So which BusinessCategory is this?
                    // If the category is null, we are talking about the root
                    BusinessCategory parentCategory = activeView.findBusinessCategory(path, activeLocale);

                    // We expect a Drag and Drop container... (encased in XML & Base64)
                    // 
                    DragAndDropContainer container = (DragAndDropContainer) event.data;

                    // Block sub-categories & columns in the root for now, until Ad-hoc & MDR follow
                    //
                    if ((container.getType() == DragAndDropContainer.TYPE_BUSINESS_TABLE && !parentCategory.isRootCategory())
                            || (container.getType() == DragAndDropContainer.TYPE_BUSINESS_COLUMN && parentCategory.isRootCategory()))
                    {
                        MessageBox mb = new MessageBox(shell, SWT.CLOSE | SWT.ICON_INFORMATION);
                        mb
                                .setMessage("At this moment, we only support one level of categories.\nAlso, buisness columns can only placed in categories.\n\nThank you for your understanding");
                        mb.setText("Sorry");
                        mb.open();
                        return;
                    }

                    switch (container.getType())
                    {
                    // 
                    // Drag business table in categories: make business table name a new category
                    //
                    case DragAndDropContainer.TYPE_BUSINESS_TABLE:
                    {
                        BusinessTable businessTable = activeView.findBusinessTable(container.getData()); // search by
                                                                                                            // ID!
                        if (businessTable != null)
                        {
                            // Create a new category
                            //
                            BusinessCategory businessCategory = new BusinessCategory();

                            // The id is the table name, prefixes etc.
                            String id = Settings.getBusinessCategoryIDPrefix() + businessTable.getTargetTable();
                            int catNr = 1;
                            String newId = id;
                            while (activeView.getRootCategory().findBusinessCategory(newId) != null)
                            {
                                catNr++;
                                newId = id + "_" + catNr;
                            }
                            if (Settings.isAnIdUppercase()) newId = newId.toUpperCase();
                            businessCategory.setId(newId);

                            // The name is the same as the table...
                            String categoryName = businessTable.getDisplayName(activeLocale);
                            catNr = 1;
                            while (activeView.getRootCategory().findBusinessCategory(activeLocale, categoryName) != null)
                            {
                                catNr++;
                                categoryName = businessTable.getDisplayName(activeLocale) + " " + catNr;
                            }
                            businessCategory.getConcept().setName(activeLocale, categoryName);

                            // add the business columns to the category
                            //
                            for (int i = 0; i < businessTable.nrBusinessColumns(); i++)
                            {
                                businessCategory.addBusinessColumn(businessTable.getBusinessColumn(i));
                            }

                            // Add the category to the business view or category
                            //
                            parentCategory.addBusinessCategory(businessCategory);

                            // Expand the parent tree item
                            TreeMemory.getInstance().storeExpanded(STRING_CATEGORIES_TREE, path, true);

                            // Done!
                            //
                            refreshAll();
                        }
                    }
                        break;
                    case DragAndDropContainer.TYPE_BUSINESS_COLUMN:
                    {
                        String columnID = container.getData();
                        BusinessColumn businessColumn = activeView.findBusinessColumn(columnID);
                        if (businessColumn != null)
                        {
                            BusinessColumn existing = activeView.getRootCategory().findBusinessColumn(columnID); // search
                                                                                                                    // by
                                                                                                                    // ID
                            if (existing != null && businessColumn.equals(existing))
                            {
                                MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_WARNING);
                                mb.setMessage("This business column already existis in the categories." + Const.CR
                                        + "Are you sure you want to add it again?");
                                mb.setText("Warning");
                                int answer = mb.open();
                                if (answer == SWT.NO) return;
                            }

                            // Add the column to the parentCategory
                            parentCategory.addBusinessColumn(businessColumn);
                            refreshAll();
                        }
                    }
                        break;

                    //
                    // Nothing we can use: give an error!
                    //  
                    default:
                    {
                        MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                        mb.setMessage("Sorry, this item can't be placed in the categories tree (type=" + container.getTypeCode() + ")");
                        mb.setText("Sorry");
                        mb.open();
                        return;
                    }
                    }
                }
                catch (Exception e)
                {
                    new ErrorDialog(shell, "Error in drag and drop", "There was an error dragging and dropping an object onto the canvas", e);
                }
            }

            public void dropAccept(DropTargetEvent event)
            {
            }
        });

    }

    public void setActiveBusinessView(SelectionEvent e)
    {
        TreeItem treeItem = (TreeItem) e.item;
        if (treeItem != null)
        {
            String[] path = Const.getTreeStrings(treeItem);
            if (path.length >= 2 && path[0].equals(STRING_BUSINESS_VIEWS)) // Did we select a business view name or
                                                                            // below?
            {
                String businessViewName = path[1];
                setActiveBusinessView(businessViewName);
            }
        }
    }

    public void setActiveBusinessView(String businessViewName)
    {
        BusinessView businessView = schemaMeta.findView(schemaMeta.getActiveLocale(), businessViewName);
        if (businessView != null)
        {
            schemaMeta.setActiveView(businessView);
            refreshGraph();
            refreshCategoriesTree();
            if (metaEditorOlap != null) metaEditorOlap.refreshScreen();
        }
    }

    private void addDragSourceToTree(final Tree fTree)
    {
        // Drag & Drop for steps

        Transfer[] ttypes = new Transfer[] { XMLTransfer.getInstance() };

        DragSource ddSource = new DragSource(fTree, DND.DROP_MOVE);
        ddSource.setTransfer(ttypes);
        ddSource.addDragListener(new DragSourceListener()
        {
            public void dragStart(DragSourceEvent event)
            {
            }

            public void dragSetData(DragSourceEvent event)
            {
                TreeItem ti[] = fTree.getSelection();

                if (ti.length > 0)
                {
                    String data = null;
                    int type = 0;

                    String ts[] = Const.getTreeStrings(ti[0]);

                    if (ts != null && ts.length > 0)
                    {
                        // Drop of physical table onto canvas?
                        if (ts[0].equals(STRING_CONNECTIONS))
                        {
                            PhysicalTable physicalTable = null;
                            PhysicalColumn physicalColumn = null;
                            if (ts.length > 2) physicalTable = schemaMeta.findPhysicalTable(schemaMeta.getActiveLocale(), ts[2]);
                            if (ts.length > 3 && physicalTable != null)
                                physicalColumn = physicalTable.findPhysicalColumn(schemaMeta.getActiveLocale(), ts[3]);

                            switch (ts.length)
                            {
                            case 1: // parent of connections tree
                                break;
                            case 2: // 1 deep: a database connection
                                type = DragAndDropContainer.TYPE_DATABASE_CONNECTION;
                                data = ts[1]; // name of the connection.
                                break;
                            case 3: // 2 deep: a physical table
                                type = DragAndDropContainer.TYPE_PHYSICAL_TABLE;
                                if (physicalTable != null) data = physicalTable.getId(); // ID of the table.
                                break;
                            case 4: // 3 deep: a physical column
                                type = DragAndDropContainer.TYPE_PHYSICAL_COLUMN;
                                if (physicalColumn != null) data = physicalColumn.getId(); // ID of the column.
                                break;
                            default:
                                break;
                            }
                        }
                        else
                        {
                            if (ts[0].equalsIgnoreCase(STRING_BUSINESS_VIEWS))
                            {
                                BusinessView businessView = null;
                                BusinessTable businessTable = null;
                                BusinessColumn businessColumn = null;
                                if (ts.length > 1) businessView = schemaMeta.findView(schemaMeta.getActiveLocale(), ts[1]);
                                if (ts.length > 3 && businessView != null)
                                    businessTable = businessView.findBusinessTable(schemaMeta.getActiveLocale(), ts[3]);
                                if (ts.length > 4 && businessTable != null)
                                    businessColumn = businessTable.findBusinessColumn(schemaMeta.getActiveLocale(), ts[4]);

                                switch (ts.length)
                                {
                                case 1: // parent of business views tree
                                    break;
                                case 2: // Name of the business view
                                    type = DragAndDropContainer.TYPE_BUSINESS_VIEW;
                                    if (businessView != null) data = businessView.getId(); // the ID of the business
                                                                                            // view
                                    break;
                                case 3: // Business tables "title"
                                    break;
                                case 4: // Name of the business table
                                    type = DragAndDropContainer.TYPE_BUSINESS_TABLE;
                                    if (businessTable != null) data = businessTable.getId(); // the ID of the
                                                                                                // business table
                                    break;
                                case 5: // Name of the business column
                                    type = DragAndDropContainer.TYPE_BUSINESS_COLUMN;
                                    if (businessColumn != null) data = businessColumn.getId(); // the ID of the
                                                                                                // business column
                                    break;
                                default:
                                    break;
                                }
                            }
                        }

                        if (type == 0 || Const.isEmpty(data))
                        {
                            event.doit = false;
                            return; // ignore anything else you drag.
                        }

                        DragAndDropContainer container = new DragAndDropContainer(type, data);
                        event.data = container;
                    }
                }
                else
                // Nothing got dragged, only can happen on OSX :-)
                {
                    event.doit = false;
                    System.out.println("NOTHING DRAGGED! WTF!");
                }
            }

            public void dragFinished(DragSourceEvent event)
            {
            }
        });

    }

    /**
     * Only one selected item possible
     * 
     * @param e
     */
    private void setMenuMain(SelectionEvent e)
    {
        TreeItem ti = (TreeItem) e.item;
        log.logDebug(APPLICATION_NAME, "Clicked on  " + ti.getText());

        if (mainMenu == null)
        {
            mainMenu = new Menu(shell, SWT.POP_UP);
        }
        else
        {
            MenuItem[] items = mainMenu.getItems();
            for (int i = 0; i < items.length; i++)
                items[i].dispose();
        }

        final String itemText = ti.getText();
        final String[] path = Const.getTreeStrings(ti);

        if (path[0].equals(STRING_CONNECTIONS))
        {
            switch (path.length)
            {
            case 1: // Database connections
            {
                MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                miNew.setText("New");
                miNew.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        newConnection();
                    }
                });
                MenuItem miCache = new MenuItem(mainMenu, SWT.PUSH);
                miCache.setText("Clear complete DB Cache");
                miCache.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        clearDBCache();
                    }
                });
            }
                break;

            case 2: // Name of a database connection
            {
                MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                miNew.setText("New");
                miNew.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        newConnection();
                    }
                });
                MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
                miEdit.setText("Edit");
                miEdit.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        editConnection(itemText);
                    }
                });
                MenuItem miDupe = new MenuItem(mainMenu, SWT.PUSH);
                miDupe.setText("Duplicate");
                miDupe.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        dupeConnection(itemText);
                    }
                });
                MenuItem miDel = new MenuItem(mainMenu, SWT.PUSH);
                miDel.setText("Delete");
                miDel.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        delConnection(itemText);
                    }
                });
                new MenuItem(mainMenu, SWT.SEPARATOR);
                MenuItem miImp = new MenuItem(mainMenu, SWT.PUSH);
                miImp.setText("Import from explorer...");
                miImp.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        importTables(path[1]);
                    }
                });
                MenuItem miMImp = new MenuItem(mainMenu, SWT.PUSH);
                miMImp.setText("Import multiple tables");
                miMImp.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        importMultipleTables(path[1]);
                    }
                });
                MenuItem miSQL = new MenuItem(mainMenu, SWT.PUSH);
                miSQL.setText("SQL Editor");
                miSQL.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        sqlSelected(itemText);
                    }
                });
                MenuItem miCache = new MenuItem(mainMenu, SWT.PUSH);
                miCache.setText("Clear DB Cache of " + ti.getText());
                miCache.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        clearDBCache();
                    }
                });
                new MenuItem(mainMenu, SWT.SEPARATOR);
                MenuItem miExpl = new MenuItem(mainMenu, SWT.PUSH);
                miExpl.setText("Explore...");
                miExpl.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        exploreDB();
                    }
                });
            }
                break;

            case 3: // Name of a physical table
            {
                MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                miNew.setText("New");
                miNew.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        importTables(path[1]);
                    }
                });
                MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
                miEdit.setText("Edit");
                miEdit.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        editPhysicalTable(path[2]);
                    }
                });
                MenuItem miDel = new MenuItem(mainMenu, SWT.PUSH);
                miDel.setText("Delete");
                miDel.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        delPhysicalTable(itemText);
                    }
                });
            }
                break;
            case 4: // Name of a physical column
            {
                MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
                miEdit.setText("Edit");
                miEdit.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        editPhysicalTable(path[2]);
                    }
                });
            }
                break;
            }
        }
        else
            if (path[0].equals(STRING_BUSINESS_VIEWS))
            {
                switch (path.length)
                {
                case 1: // Business Views
                {
                    MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                    miNew.setText("New");
                    miNew.addListener(SWT.Selection, new Listener()
                    {
                        public void handleEvent(Event e)
                        {
                            newBusinessView();
                        }
                    });
                }
                    break;
                case 2: // Business View name
                {
                    MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                    miNew.setText("New");
                    miNew.addListener(SWT.Selection, new Listener()
                    {
                        public void handleEvent(Event e)
                        {
                            newBusinessView();
                        }
                    });
                    MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
                    miEdit.setText("Edit");
                    miEdit.addListener(SWT.Selection, new Listener()
                    {
                        public void handleEvent(Event e)
                        {
                            editBusinessView(path[1]);
                        }
                    });
                    MenuItem miDelete = new MenuItem(mainMenu, SWT.PUSH);
                    miDelete.setText("Delete");
                    miDelete.addListener(SWT.Selection, new Listener()
                    {
                        public void handleEvent(Event e)
                        {
                            deleteBusinessView(path[1]);
                        }
                    });
                }
                    break;
                case 3: // Business Tables or Relationships "title"
                    if (path[2].equals(STRING_BUSINESS_TABLES))
                    {
                        MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                        miNew.setText("New Business Table");
                        miNew.addListener(SWT.Selection, new Listener()
                        {
                            public void handleEvent(Event e)
                            {
                                newBusinessTable(null);
                            }
                        });
                    }
                    else
                        if (path[2].equals(STRING_RELATIONSHIPS))
                        {
                            MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                            miNew.setText("New Relationship");
                            miNew.addListener(SWT.Selection, new Listener()
                            {
                                public void handleEvent(Event e)
                                {
                                    newRelationship();
                                }
                            });
                        }
                    break;
                case 4: // Business Tables or Relationships
                    if (path[2].equals(STRING_BUSINESS_TABLES))
                    {
                        MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                        miNew.setText("New Business Table");
                        miNew.addListener(SWT.Selection, new Listener()
                        {
                            public void handleEvent(Event e)
                            {
                                newBusinessTable(null);
                            }
                        });
                        MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
                        miEdit.setText("Edit");
                        miEdit.addListener(SWT.Selection, new Listener()
                        {
                            public void handleEvent(Event e)
                            {
                                editBusinessTable(itemText);
                            }
                        });
                        MenuItem miDel = new MenuItem(mainMenu, SWT.PUSH);
                        miDel.setText("Delete");
                        miDel.addListener(SWT.Selection, new Listener()
                        {
                            public void handleEvent(Event e)
                            {
                                delBusinessTable(itemText);
                            }
                        });
                    }
                    else
                        if (path[2].equals(STRING_RELATIONSHIPS))
                        {
                            MenuItem miNew = new MenuItem(mainMenu, SWT.PUSH);
                            miNew.setText("New Relationship");
                            miNew.addListener(SWT.Selection, new Listener()
                            {
                                public void handleEvent(Event e)
                                {
                                    newRelationship();
                                }
                            });
                            MenuItem miEdit = new MenuItem(mainMenu, SWT.PUSH);
                            miEdit.setText("Edit");
                            miEdit.addListener(SWT.Selection, new Listener()
                            {
                                public void handleEvent(Event e)
                                {
                                    editRelationship(itemText);
                                }
                            });
                            MenuItem miDel = new MenuItem(mainMenu, SWT.PUSH);
                            miDel.setText("Delete");
                            miDel.addListener(SWT.Selection, new Listener()
                            {
                                public void handleEvent(Event e)
                                {
                                    delRelationship(itemText);
                                }
                            });
                        }
                    break;
                }
            }

        final ConceptUtilityInterface[] utilityInterfaces = getSelectedConceptUtilityInterfacesInMainTree();
        if (utilityInterfaces.length > 0)
        {
            if (mainMenu.getItemCount() > 0)
            {
                new MenuItem(mainMenu, SWT.SEPARATOR);
            }

            MenuItem miSetConcept = new MenuItem(mainMenu, SWT.PUSH);
            miSetConcept.setText("Set parent concept");
            miSetConcept.addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    setParentConcept(utilityInterfaces);
                }
            });

            MenuItem miClearConcept = new MenuItem(mainMenu, SWT.PUSH);
            miClearConcept.setText("Clear parent concept");
            miClearConcept.addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    clearParentConcept(utilityInterfaces);
                }
            });

            MenuItem miRemoveProperty = new MenuItem(mainMenu, SWT.PUSH);
            miRemoveProperty.setText("Remove child properties");
            miRemoveProperty.addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    removeChildProperties(utilityInterfaces);
                }
            });

        }

        mainTree.setMenu(mainMenu);
    }

    /**
     * Only one selected item possible
     * 
     * @param e
     */
    private void setMenuCategories(SelectionEvent e)
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return; // perhaps give some feedback why nothing is happening?
        String activeLocale = schemaMeta.getActiveLocale();

        final int nrSelected = catTree.getSelectionCount();

        final TreeItem treeItem = (TreeItem) e.item;
        log.logDebug(APPLICATION_NAME, "Clicked in categories tree on " + treeItem.getText());

        if (catMenu == null)
        {
            catMenu = new Menu(shell, SWT.POP_UP);
        }
        else
        {
            MenuItem items[] = catMenu.getItems();
            for (int i = 0; i < items.length; i++)
                items[i].dispose();
        }

        final String itemText = treeItem.getText();
        final String[] path = Const.getTreeStrings(treeItem, 1);
        final boolean isLowestLevel = treeItem.getItemCount() == 0; // no children

        final BusinessCategory businessCategory = activeView.findBusinessCategory(path, activeLocale);
        final BusinessCategory parentCategory;

        if (path.length > 0)
        {
            String[] parentPath = new String[path.length - 1];
            for (int i = 0; i < parentPath.length; i++)
                parentPath[i] = path[i];
            parentCategory = activeView.findBusinessCategory(parentPath, activeLocale);
        }
        else
        {
            parentCategory = activeView.getRootCategory();
        }

        // The top level Categories tree item
        if (path.length == 0)
        {
            setMenuCategoriesTopLevel(catMenu, businessCategory, path);
        }
        else
            if (isLowestLevel && businessCategory != null)
            {
                final BusinessColumn businessColumn = businessCategory.findBusinessColumn(itemText, false, activeLocale);
                if (businessColumn != null)
                {
                    setMenuCategoriesBusinessColumn(catMenu, businessCategory, businessColumn, activeView, nrSelected);
                }
                else
                // it's a category without selected columns in it.
                {
                    setMenuCategoriesBusinessCategory(catMenu, parentCategory, businessCategory, path);
                }
            }
            // We clicked in the tree, not at the top, not at the column level: it's on a business category
            // Here we can add, delete or edit categories
            else
            {
                setMenuCategoriesBusinessCategory(catMenu, parentCategory, businessCategory, path);
            }

        final ConceptUtilityInterface[] utilityInterfaces = getSelectedConceptUtilityInterfacesInCategoriesTree();
        if (utilityInterfaces.length > 0)
        {
            if (catMenu.getItemCount() > 0)
            {
                new MenuItem(catMenu, SWT.SEPARATOR);
            }

            MenuItem miSetConcept = new MenuItem(catMenu, SWT.PUSH);
            miSetConcept.setText("Set parent concept");
            miSetConcept.addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    setParentConcept(utilityInterfaces);
                }
            });

            MenuItem miClearConcept = new MenuItem(catMenu, SWT.PUSH);
            miClearConcept.setText("Clear parent concept");
            miClearConcept.addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    clearParentConcept(utilityInterfaces);
                }
            });
        }

        catTree.setMenu(catMenu);
    }

    private void setMenuCategoriesTopLevel(Menu menu, final BusinessCategory businessCategory, final String[] path)
    {
        MenuItem miNew = new MenuItem(menu, SWT.PUSH);
        miNew.setText("New Category");
        miNew.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                TreeMemory.getInstance().storeExpanded(STRING_CATEGORIES_TREE, path, true); // Expand the parent item on
                                                                                            // the next refresh.
                newBusinessCategory(businessCategory);
            }
        });
    }

    private void setMenuCategoriesBusinessCategory(Menu menu, final BusinessCategory parentCategory, final BusinessCategory businessCategory,
            final String[] path)
    {
        MenuItem miNew = new MenuItem(menu, SWT.PUSH);
        miNew.setText("New category");
        miNew.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                newBusinessCategory(businessCategory);
            }
        });

        MenuItem miEdit = new MenuItem(menu, SWT.PUSH);
        miEdit.setText("Edit category");
        miEdit.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                editBusinessCategory(businessCategory);
            }
        });

        MenuItem miDelete = new MenuItem(menu, SWT.PUSH);
        miDelete.setText("Remove category");
        miDelete.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                delBusinessCategory(parentCategory, businessCategory);
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        MenuItem miUp = new MenuItem(menu, SWT.PUSH);
        miUp.setText("Move up \tCTRL-UP");
        miUp.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                moveBusinessCategoryUp(parentCategory, businessCategory);
                selectTreeItem(catTree, path);
            }
        });

        MenuItem miDown = new MenuItem(menu, SWT.PUSH);
        miDown.setText("Move down \tCTRL-DOWN");
        miDown.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                moveBusinessCategoryDown(parentCategory, businessCategory);
                selectTreeItem(catTree, path);
            }
        });
    }

    private void setMenuCategoriesBusinessColumn(Menu menu, final BusinessCategory businessCategory, final BusinessColumn businessColumn,
            final BusinessView activeView, int nrSelected)
    {
        // Edit the business column by going to the business table editor
        //
        final BusinessTable businessTable = activeView.findBusinessTable(businessColumn);
        if (businessTable != null)
        {
            if (nrSelected == 1)
            {
                MenuItem miEdit = new MenuItem(menu, SWT.PUSH);
                miEdit.setText("Edit business column");
                miEdit.addListener(SWT.Selection, new Listener()
                {
                    public void handleEvent(Event e)
                    {
                        editBusinessColumn(businessTable, businessColumn);
                    }
                });
            }
        }

        // Delete the business column from the parent category
        //
        if (nrSelected == 1)
        {
            MenuItem miDel = new MenuItem(menu, SWT.PUSH);
            miDel.setText("Remove business column from category");
            miDel.addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    delColumnFromCategory(businessCategory, businessColumn);
                }
            });

            // Move up or down
            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem miUp = new MenuItem(menu, SWT.PUSH);
            miUp.setText("Move up \tCTRL-UP");
            miUp.addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    moveBusinessColumnUp(businessCategory, businessColumn);
                }
            });

            MenuItem miDown = new MenuItem(menu, SWT.PUSH);
            miDown.setText("Move down \tCTRL-DOWN");
            miDown.addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    moveBusinessColumnDown(businessCategory, businessColumn);
                }
            });
        }
    }

    protected void setCategoriesParentConcepts()
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return; // perhaps give some feedback why nothing is happening?
        String activeLocale = schemaMeta.getActiveLocale();

        String[] concepts = schemaMeta.getConceptNames();

        // Ask the user to pick a parent concept...
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, concepts, "Select a parent concept",
                "Please select a parent concept to set");
        String conceptName = dialog.open();
        if (conceptName != null)
        {
            ConceptInterface parentInterface = schemaMeta.findConcept(conceptName);

            TreeItem treeItems[] = catTree.getSelection();
            for (int i = 0; i < treeItems.length; i++)
            {
                TreeItem treeItem = treeItems[i];
                String itemText = treeItem.getText();
                String[] path = Const.getTreeStrings(treeItem);
                boolean isLowestLevel = treeItem.getItemCount() == 0; // no children

                BusinessCategory businessCategory = activeView.findBusinessCategory(path, activeLocale);
                if (businessCategory != null)
                {
                    if (isLowestLevel)
                    {
                        BusinessColumn businessColumn = businessCategory.findBusinessColumn(itemText, false, activeLocale);
                        if (businessColumn != null)
                        {
                            businessColumn.getConcept().setParentInterface(parentInterface);
                        }
                    }
                    else
                    {
                        // A category was selected: simply set the parent category for the concept...
                        businessCategory.getConcept().setParentInterface(parentInterface);
                    }
                }
            }

            // refresh the whole thing...
            refreshAll();
        }

    }

    public void delColumnFromCategory(BusinessCategory businessCategory, BusinessColumn businessColumn)
    {
        int idx = businessCategory.indexOfBusinessColumn(businessColumn);
        if (idx >= 0)
        {
            businessCategory.removeBusinessColumn(idx);
            refreshCategoriesTree();
        }
    }

    public void moveBusinessColumnDown(BusinessCategory businessCategory, BusinessColumn businessColumn)
    {
        int index = businessCategory.indexOfBusinessColumn(businessColumn);
        if (index < businessCategory.nrBusinessColumns() - 1)
        {
            businessCategory.removeBusinessColumn(index);
            businessCategory.addBusinessColumn(index + 1, businessColumn);
            refreshCategoriesTree();
        }

    }

    public void moveBusinessColumnUp(BusinessCategory businessCategory, BusinessColumn businessColumn)
    {
        int index = businessCategory.indexOfBusinessColumn(businessColumn);
        if (index > 0)
        {
            businessCategory.removeBusinessColumn(index);
            businessCategory.addBusinessColumn(index - 1, businessColumn);
            refreshCategoriesTree();
        }
    }

    /**
     * Add a new business category to the specified parent.
     */
    public void newBusinessCategory(BusinessCategory parentCategory)
    {
        if (!parentCategory.isRootCategory()) return; // Block for now, until Ad-hoc & MDR follow

        BusinessCategory businessCategory = new BusinessCategory();
        businessCategory.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(parentCategory.getBusinessCategories()));
        
        while(true)
        {
            BusinessCategoryDialog dialog = new BusinessCategoryDialog(shell, businessCategory, schemaMeta.getLocales(), schemaMeta.getSecurityReference());
            if (dialog.open() != null)
            {
                // Add this to the parent.
                try
                {
                    parentCategory.addBusinessCategory(businessCategory);
                    // refresh it all...
                    refreshAll();
                    break;
                }
                catch (ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, "Error", "A business category with id '"+businessCategory.getId()+"' already exists.", e);
                }
            }
            else
            {
                break;
            }
        }
    }

    public void delBusinessCategory(BusinessCategory parentCategory, BusinessCategory businessCategory)
    {
        int index = parentCategory.indexOfBusinessCategory(businessCategory);
        if (index >= 0)
        {
            parentCategory.removeBusinessCategory(index);
            refreshCategoriesTree();
        }
    }

    public void editBusinessCategory(BusinessCategory businessCategory)
    {
        BusinessCategoryDialog dialog = new BusinessCategoryDialog(shell, businessCategory, schemaMeta.getLocales(), schemaMeta
                .getSecurityReference());
        if (dialog.open() != null)
        {
            // refresh it all...
            refreshAll();
        }
    }

    public void moveBusinessCategoryDown(BusinessCategory parentCategory, BusinessCategory businessCategory)
    {
        int index = parentCategory.indexOfBusinessCategory(businessCategory);
        if (index < parentCategory.nrBusinessCategories() - 1)
        {
            parentCategory.removeBusinessCategory(index);
            try
            {
                parentCategory.addBusinessCategory(index + 1, businessCategory);
            }
            catch (ObjectAlreadyExistsException e)
            {
                // Moving anything should not have any impact.
            }
            refreshCategoriesTree();
        }
    }

    public void moveBusinessCategoryUp(BusinessCategory parentCategory, BusinessCategory businessCategory)
    {
        int index = parentCategory.indexOfBusinessCategory(businessCategory);
        if (index > 0)
        {
            parentCategory.removeBusinessCategory(index);
            try
            {
                parentCategory.addBusinessCategory(index - 1, businessCategory);
            }
            catch (ObjectAlreadyExistsException e)
            {
                // Moving anything should not have any impact.
            }
            refreshCategoriesTree();
        }
    }

    public BusinessTable newBusinessTable(PhysicalTable physicalTable)
    {
        BusinessView activeView = schemaMeta.getActiveView();
        String activeLocale = schemaMeta.getActiveLocale();
        if (activeView == null) return null;

        // Ask a name for the business table.
        //
        String tableName = "";
        if (physicalTable != null) tableName = physicalTable.getDisplayName(activeLocale);
        EnterStringDialog enterStringDialog = new EnterStringDialog(shell, tableName, "Enter a name", "Enter a name for this business table");
        tableName = enterStringDialog.open();
        if (tableName == null) return null;

        // Create a new ID based on this...
        //
        String newId = null;
        if (physicalTable != null)
        {
            newId = Settings.getBusinessTableIDPrefix() + Const.toID(tableName);
            if (Settings.isAnIdUppercase()) newId = newId.toUpperCase();
        }

        // Create a business table with the new ID and localized name
        //
        BusinessTable businessTable = new BusinessTable(newId, physicalTable);
        businessTable.getConcept().setName(activeLocale, tableName);
        
        // Add a unique ID enforcer...
        businessTable.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(activeView.getBusinessTables()));

        // Add columns to this if we have a physical table to import from...
        //
        if (physicalTable != null)
        {
            // copy the physical columns to the business columns...
            for (int i = 0; i < physicalTable.nrPhysicalColumns(); i++)
            {
                PhysicalColumn physicalColumn = physicalTable.getPhysicalColumn(i);
                BusinessColumn businessColumn = new BusinessColumn(physicalColumn.getId(), physicalColumn, businessTable);

                // Add a unique ID enforcer...
                businessColumn.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(businessTable.getBusinessColumns()));

                // We're done, add the business column.
                try
                {
                    // Propose a new ID
                    businessColumn.setId(BusinessColumn.proposeId(activeLocale, businessTable, physicalColumn));
                    businessTable.addBusinessColumn(businessColumn);
                }
                catch (ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, "Error", "A business column with ID '"+businessColumn.getId()+"' already exists and was not added", e);
                }
            }
        }

        // Show it to the user...
        //
        while (true)
        {
            BusinessTableDialog dialog = new BusinessTableDialog(shell, businessTable, schemaMeta);
            String name = dialog.open();
            if (name != null)
            {
                try
                {
                    activeView.addBusinessTable(businessTable);
                    refreshAll();
                    return businessTable;
                }
                catch(ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, "Error", "A business table with id '"+businessTable.getId()+"' already exists", e);
                }
            }
            else
            {
                break;
            }
        }
        return null;
    }

    public void delBusinessTable(String businessTableID)
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return;
        String activeLocale = schemaMeta.getActiveLocale();

        BusinessTable businessTable = activeView.findBusinessTable(activeLocale, businessTableID);
        if (businessTable != null)
        {
            // First delete the relationships it uses.
            RelationshipMeta[] relationships = activeView.findRelationshipsUsing(businessTable);
            for (int i = 0; i < relationships.length; i++)
            {
                int idx = activeView.indexOfRelationship(relationships[i]);
                if (idx >= 0) activeView.removeRelationship(idx);
            }

            int idx = activeView.indexOfBusinessTable(businessTable);
            activeView.removeBusinessTable(idx);
            refreshAll();
        }
    }

    private void addTabs()
    {
        Composite child = new Composite(sashform, SWT.BORDER);
        child.setLayout(new FillLayout());

        tabfolder = new CTabFolder(child, SWT.BORDER);
        // tabfolder.setFont(GUIResource.getInstance().getFontDefault());
        // tabfolder.setBackground(GUIResource.getInstance().getColorBackground());
        tabfolder.setSimple(false);
        // tabfolder.setSelectionBackground(GUIResource.getInstance().getColorTab());

        CTabItem tiTabsGraph = new CTabItem(tabfolder, SWT.NONE);
        tiTabsGraph.setText("  Graphical view  ");
        tiTabsGraph.setToolTipText("Displays the schema graphically.");

        CTabItem tiTabsConcept = new CTabItem(tabfolder, SWT.NULL);
        tiTabsConcept.setText("  Concepts  ");
        tiTabsConcept.setToolTipText("Shows and edits the concepts.");

        CTabItem tiTabsLocale = new CTabItem(tabfolder, SWT.NULL);
        tiTabsLocale.setText("  Locales  ");
        tiTabsLocale.setToolTipText("Displays the defined locale.");

        CTabItem tiTabsLog = new CTabItem(tabfolder, SWT.NULL);
        tiTabsLog.setText("  Log view  ");
        tiTabsLog.setToolTipText("Displays the log.");

        metaEditorGraph = new MetaEditorGraph(tabfolder, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND, this);
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

    public void addOlapTab()
    {
        CTabItem tiTabsOlap = new CTabItem(tabfolder, SWT.NULL); 
        tiTabsOlap.setText("  OLAP  ");
        tiTabsOlap.setToolTipText("Shows and edits the defined OLAP dimensions and their hierarchies.");
        metaEditorOlap = new MetaEditorOlap(tabfolder, SWT.NONE, this);

        tiTabsOlap.setControl(metaEditorOlap);        
    }

    private boolean readData(String modelName)
    {
        try
        {
            props.addLastFile(LastUsedFile.FILE_TYPE_SCHEMA, modelName, "", false, "");
            saveSettings();
            addMenuLast();

            // Get a new cwm instance for the selected model...
            //
            if (cwm != null)
            {
                cwm.removeFromList();
            }
            cwm = CWM.getInstance(modelName);

            // Read some data from the model...
            schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);

            refreshAll();
            return true;
        }
        catch (Exception e)
        {
            new ErrorDialog(shell, "Error reading model", "There was an error reading the model", e);
            return false;
        }
    }

    public void newSelected()
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return;

        log.logDebug(APPLICATION_NAME, "New Selected");
        // Determine what menu we selected from...

        TreeItem ti[] = mainTree.getSelection();

        // Then call newConnection or newTrans
        if (ti.length >= 1)
        {
            String name = ti[0].getText();
            TreeItem parent = ti[0].getParentItem();
            if (parent == null)
            {
                log.logDebug(APPLICATION_NAME, "Element has no parent");
                if (name.equalsIgnoreCase(STRING_CONNECTIONS)) newConnection();
                if (name.equalsIgnoreCase(STRING_RELATIONSHIPS)) newRelationship();
                if (name.equalsIgnoreCase(STRING_BUSINESS_TABLES))
                {
                    MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
                    mb.setMessage("Please import tables via the connections.");
                    mb.setText("Tip!");
                    mb.open();
                }
            }
            else
            {
                String section = parent.getText();
                log.logDebug(APPLICATION_NAME, "Element has parent: " + section);
                if (section.equalsIgnoreCase(STRING_CONNECTIONS)) newConnection();
            }
        }
    }

    public void doubleClickedMain()
    {
        // Determine what tree-item we selected from...

        TreeItem ti[] = mainTree.getSelection();

        // Then call editConnection or editStep or editTrans
        if (ti.length == 1)
        {
            String[] path = Const.getTreeStrings(ti[0]);

            if (path[0].equals(STRING_CONNECTIONS))
            {
                switch (path.length)
                {
                case 1: // Double clicked on Connections: create new one
                    newConnection();
                    break;
                case 2: // Double clicked on a database connection: edit
                    editConnection(path[1]);
                    break;
                case 3: // Double clicked on a physical table : edit
                    editPhysicalTable(path[2]);
                    break;
                case 4: // Double clicked on a physical column : edit table
                    editPhysicalColumn(path[2], path[3]);
                    break;
                default:
                    break;
                }
            }
            else
                if (path[0].equals(STRING_BUSINESS_VIEWS))
                {
                    switch (path.length)
                    {
                    case 1: // Double clicked on Business Views: create new one
                        newBusinessView();
                        break;
                    case 2: // Double clicked on a Business View name: edit
                        editBusinessView(path[1]);
                        break;
                    case 3: // Double clicked on the business table category : new table or relationship
                        if (path[2].equals(STRING_BUSINESS_TABLES))
                        {
                            newBusinessTable(null);
                        }
                        else
                            if (path[2].equals(STRING_RELATIONSHIPS))
                            {
                                newRelationship();
                            }
                        break;
                    case 4: // Double clicked on a business table : edit table
                        if (path[2].equals(STRING_BUSINESS_TABLES))
                        {
                            editBusinessTable(path[3]);
                        }
                        else
                            if (path[2].equals(STRING_RELATIONSHIPS))
                            {
                                editRelationship(path[3]);
                            }
                        break;
                    case 5: // Double clicked on a business column : edit table
                        if (path[2].equals(STRING_BUSINESS_TABLES))
                        {
                            editBusinessColumn(path[1], path[3], path[4]); // view, table, column
                        }
                        break;
                    default:
                        break;
                    }
                }
        }
    }

    public void doubleClickedCategories()
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return;
        String activeLocale = schemaMeta.getActiveLocale();

        // Determine what tree-item we double clicked on...

        TreeItem ti[] = catTree.getSelection();
        if (ti.length != 1) return;

        final TreeItem treeItem = ti[0];

        final String[] path = Const.getTreeStrings(treeItem, 1);
        final boolean isLowestLevel = treeItem.getItemCount() == 0; // no children

        final BusinessCategory businessCategory = activeView.findBusinessCategory(path, activeLocale);
        final BusinessCategory parentCategory;

        if (path.length > 1)
        {
            String[] parentPath = new String[path.length - 1];
            for (int i = 0; i < parentPath.length; i++)
                parentPath[i] = path[i];
            parentCategory = activeView.findBusinessCategory(parentPath, activeLocale);
        }
        else
        {
            parentCategory = activeView.getRootCategory();
        }

        // The top level Categories tree item
        if (path[0].equals(STRING_CATEGORIES) && path.length == 1)
        {
            newBusinessCategory(parentCategory);
        }
        else
            if (isLowestLevel && businessCategory != null)
            {
                final BusinessColumn businessColumn = businessCategory.findBusinessColumn(treeItem.getText(), false, activeLocale);
                if (businessColumn != null)
                {
                    editBusinessColumn(businessColumn.getBusinessTable(), businessColumn);
                }
                else
                // it's a category without selected columns in it.
                {
                    editBusinessCategory(businessCategory);
                }
            }
            // We clicked in the tree, not at the top, not at the column level: it's on a business category
            // Here we can add, delete or edit categories
            else
            {
                editBusinessCategory(businessCategory);
            }
    }

    private void editBusinessColumn(String businessViewName, String businessTableName, String businessColumnName)
    {
        String locale = schemaMeta.getActiveLocale();
        BusinessView view = schemaMeta.findView(locale, businessViewName);
        if (view != null)
        {
            BusinessTable table = view.findBusinessTable(locale, businessTableName);
            if (table != null)
            {
                BusinessColumn column = table.findBusinessColumn(locale, businessColumnName);
                if (column != null)
                {
                    editBusinessColumn(table, column);
                }
            }
        }
    }

    private void editBusinessColumn(BusinessTable businessTable, BusinessColumn businessColumn)
    {
        String columnName = businessColumn.getDisplayName(schemaMeta.getActiveLocale());
        String tableName = businessTable.getDisplayName(schemaMeta.getActiveLocale());
        editProperties("Enter the properties for column [" + columnName + "] of table [" + tableName + "]", businessColumn);
    }

    private void editPhysicalColumn(String physicalTableName, String physicalColumnName)
    {
        String activeLocale = schemaMeta.getActiveLocale();
        PhysicalTable physicalTable = schemaMeta.findPhysicalTable(activeLocale, physicalTableName);
        if (physicalTable != null)
        {
            PhysicalColumn physicalColumn = physicalTable.findPhysicalColumn(activeLocale, physicalColumnName);
            if (physicalColumn != null)
            {
                editProperties("Physical Column [" + physicalColumnName + "] properties", physicalColumn);
            }

        }
    }

    private void editProperties(String message, ConceptUtilityInterface utilityInterface)
    {
        ConceptDialog dialog = new ConceptDialog(shell, "Enter the properties", message, utilityInterface, schemaMeta);
        String id = dialog.open();
        if (id != null)
        {
            refreshAll();
        }
    }

    public BusinessView newBusinessView()
    {
        int nr = schemaMeta.nrViews() + 1;
        String id = Settings.getBusinessViewIDPrefix() + "view_" + nr;
        if (Settings.isAnIdUppercase()) id = id.toUpperCase();
        BusinessView businessView = new BusinessView(id);
        businessView.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(schemaMeta.getViews()));
        businessView.getConcept().setName(schemaMeta.getActiveLocale(), "View " + nr);

        while (true)
        {
            BusinessViewDialog dialog = new BusinessViewDialog(shell, businessView, schemaMeta.getLocales(), schemaMeta.getSecurityReference());
            String viewName = dialog.open();
            if (viewName != null)
            {
                try
                {
                    schemaMeta.addView(businessView);
                    schemaMeta.setActiveView(businessView);
                    refreshAll();
    
                    return businessView;
                }
                catch(ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, "Error", "A business view with this name already exists", e);
                }
            }
            else
            {
                break;
            }
        }
        return null;
    }

    public void editBusinessView(String businessViewName)
    {
        BusinessView businessView = schemaMeta.findView(schemaMeta.getActiveLocale(), businessViewName);
        if (businessView != null)
        {
            BusinessViewDialog dialog = new BusinessViewDialog(shell, businessView, schemaMeta.getLocales(), schemaMeta.getSecurityReference());
            String viewName = dialog.open();
            if (viewName != null)
            {
                refreshAll();
            }
        }
    }

    public void deleteBusinessView(String businessViewName)
    {
        BusinessView businessView = schemaMeta.findView(schemaMeta.getActiveLocale(), businessViewName);
        if (businessView != null)
        {
            MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
            box.setText("Warning!");
            box.setMessage("Are you sure you want to delete business view [" + businessView.getDisplayName(schemaMeta.getActiveLocale()) + "]");
            int answer = box.open();
            if (answer == SWT.YES)
            {
                int idx = schemaMeta.indexOfView(businessView);
                if (idx >= 0)
                {
                    schemaMeta.removeView(idx);
                    schemaMeta.setActiveView(null);
                    refreshAll();
                }
            }
        }
    }

    public void sqlSelected(String connectionName)
    {
        DatabaseMeta databaseMeta = schemaMeta.findDatabase(connectionName);
        if (databaseMeta != null)
        {
            SQLEditor sql = new SQLEditor(shell, SWT.NONE, databaseMeta, DBCache.getInstance(), "");
            sql.open();
        }
    }

    public void editConnection(String name)
    {
        DatabaseMeta db = schemaMeta.findDatabase(name);
        if (db != null)
        {
            DatabaseDialog con = new DatabaseDialog(shell, db);
            String newname = con.open();
            if (newname != null) // null: CANCEL
            {
                refreshTree();
            }
        }
        setShellText();
    }

    public void dupeConnection(String name)
    {
        DatabaseMeta databaseMeta = schemaMeta.findDatabase(name);
        if (databaseMeta != null)
        {
            try
            {
                int pos = schemaMeta.indexOfDatabase(databaseMeta);
                DatabaseMeta newdb = (DatabaseMeta) databaseMeta.clone();
                String dupename = "(copy of) " + name;
                newdb.setName(dupename);
                schemaMeta.addDatabase(pos + 1, newdb);
                refreshTree();
    
                DatabaseDialog con = new DatabaseDialog(shell, newdb);
                String newname = con.open();
                if (newname != null) // null: CANCEL
                {
                    schemaMeta.removeDatabaseMeta(pos + 1);
                    schemaMeta.addDatabase(pos + 1, newdb);
    
                    if (!newname.equalsIgnoreCase(dupename)) refreshTree();
                }
            }
            catch(ObjectAlreadyExistsException e)
            {
                new ErrorDialog(shell, "Error", "A database connection with this name already exists", e);
            }
        }
    }

    public void delConnection(String name)
    {
        int i, pos = 0;
        DatabaseMeta db = null, look = null;

        for (i = 0; i < schemaMeta.nrDatabases() && db == null; i++)
        {
            look = schemaMeta.getDatabase(i);
            if (look.getName().equalsIgnoreCase(name))
            {
                db = look;
                pos = i;
            }
        }
        if (db != null)
        {
            schemaMeta.removeDatabaseMeta(pos);
            refreshAll();
        }
        setShellText();
    }

    public void editPhysicalTable(String name)
    {
        log.logDebug(APPLICATION_NAME, "Edit table: " + name);
        ediPhysicalTable(schemaMeta.findPhysicalTable(schemaMeta.getActiveLocale(), name));
    }

    public void ediPhysicalTable(PhysicalTable physicalTable)
    {
        if (physicalTable != null)
        {
            PhysicalTableDialog td = new PhysicalTableDialog(shell, SWT.NONE, physicalTable, schemaMeta.getLocales(), schemaMeta.getSecurityReference());
            String tablename = td.open();
            if (tablename != null)
            {
                // OK, so the table has changed...
                //
                refreshGraph();
                refreshTree(); // Perhaps other objects where touched in the dialog.
                setShellText();
                return;
            }
        }
    }

    public void dupePhysicalTable(PhysicalTable physicalTable)
    {
        if (physicalTable != null)
        {
            log.logDebug(APPLICATION_NAME, "Duplicate table: " + physicalTable.getId());

            PhysicalTable newTable = (PhysicalTable) physicalTable.clone();
            if (newTable != null)
            {
                try
                {
                    String newname = physicalTable.getId() + " (copy)";
                    int nr = 2;
                    while (schemaMeta.findPhysicalTable(newname) != null)
                    {
                        newname = physicalTable.getId() + " (copy " + nr + ")";
                        nr++;
                    }
                    newTable.setId(newname);
    
                    schemaMeta.addTable(newTable);
                    refreshTree();
                    refreshGraph();
                }
                catch(ObjectAlreadyExistsException e)
                {
                    new ErrorDialog(shell, "Error", "A physical table with this name already exists", e);
                }
            }
        }
    }

    public void delPhysicalTable(String name)
    {
        log.logDebug(APPLICATION_NAME, "Delete table: " + name);
        PhysicalTable physicalTable = schemaMeta.findPhysicalTable(schemaMeta.getActiveLocale(), name);
        if (physicalTable != null)
        {
            int pos = schemaMeta.indexOfTable(physicalTable);
            schemaMeta.removeTable(pos);
            for (int i = schemaMeta.nrViews() - 1; i >= 0; i--)
            {
                BusinessView ri = schemaMeta.getView(i);
                ri.deletePhysicalTableReferences(physicalTable);
            }
            refreshTree();
            refreshGraph();
        }
        else
        {
            log.logDebug(APPLICATION_NAME, "Couldn't find table [" + name + "] to delete...");
        }
    }

    public void editRelationship(String name)
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return;

        RelationshipMeta ri = activeView.findRelationship(name);
        if (ri != null)
        {
            RelationshipDialog rd = new RelationshipDialog(shell, SWT.NONE, log, ri, activeView);
            if (rd.open() != null)
            {
                String newname = ri.toString();
                if (!name.equalsIgnoreCase(newname))
                {
                    refreshTree();
                }
                refreshGraph(); // color, nr of copies...
            }
        }
        setShellText();
    }

    public void delRelationship(String name)
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return;

        for (int i = 0; i < activeView.nrRelationships(); i++)
        {
            RelationshipMeta meta = activeView.getRelationship(i);
            if (meta.toString().equals(name))
            {
                activeView.removeRelationship(i);
                refreshTree();
                refreshGraph();
            }
        }
        setShellText();
    }

    public void newRelationship()
    {
        newRelationship(null, null);
    }

    public void newRelationship(BusinessTable from, BusinessTable to)
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return;

        RelationshipMeta relationship = new RelationshipMeta();
        relationship.setTableFrom(from);
        relationship.setTableTo(to);
        RelationshipDialog dialog = new RelationshipDialog(shell, SWT.NONE, log, relationship, schemaMeta.getActiveView());
        if (dialog.open() != null)
        {
            activeView.addRelationship(relationship);
    
            refreshTree();
            refreshGraph();

        }
    }

    public void newConnection()
    {
        DatabaseMeta db = new DatabaseMeta();
        DatabaseDialog con = new DatabaseDialog(shell, db);
        String con_name = con.open();
        if (con_name != null)
        {
            try
            {
                schemaMeta.addDatabase(db);
                refreshTree();
            }
            catch(ObjectAlreadyExistsException e)
            {
                new ErrorDialog(shell, "Error", "A database with this name already exists", e);
            }
        }
    }

    public boolean showChangedWarning()
    {
        return showChangedWarning("This model has changed.   Are you sure you want to open a new file?");
    }

    public boolean showChangedWarning(String message)
    {
        boolean answer = true;
        if (schemaMeta.hasChanged())
        {
            MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_WARNING | SWT.APPLICATION_MODAL);
            mb.setMessage(message);
            mb.setText("Warning!");
            answer = mb.open() == SWT.YES;
        }
        return answer;
    }

    public void openFile()
    {
        if (showChangedWarning())
        {
            try
            {
                // Get the available models in the CWM repository
                String[] modelNames = CWM.getModelNames();

                // Show a dialog to select a model
                EnterSelectionDialog selectionDialog = new EnterSelectionDialog(shell, modelNames, "Select a model",
                        "Select a model");
                String modelName = selectionDialog.open();
                if (modelName != null)
                {
                    readData(modelName);
                }
            }
            catch (CWMException e)
            {
                new ErrorDialog(shell, "Error getting model list", "There was an unexpected getting a list of available models from the repository!",
                        e);
            }
        }
    }

    public void newFile()
    {
        //
        boolean goAhead = false;
        if (schemaMeta.hasChanged())
        {
            MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_WARNING);
            mb.setMessage("This model has changed.  Do you want to save it first?");
            mb.setText("Warning");
            int answer = mb.open();
            switch (answer)
            {
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
        }
        else
        {
            goAhead = true;
        }

        if (goAhead)
        {
            schemaMeta.clear();
            schemaMeta.addDefaults();
            schemaMeta.clearChanged();
            setModelname(null);
            refreshAll();
        }
    }

    public boolean quitFile()
    {
        boolean retval = true;

        log.logDetailed(APPLICATION_NAME, "Quit application.");
        saveSettings();
        if (schemaMeta.hasChanged())
        {
            MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_WARNING);
            mb.setMessage("File has changed!  Do you want to save first?");
            mb.setText("Warning!");
            int answer = mb.open();

            switch (answer)
            {
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
        }
        else
        {
            dispose();
        }
        return retval;
    }

    public boolean saveFile()
    {
        log.logDetailed(APPLICATION_NAME, "Save file...");
        if (schemaMeta.modelName != null)
        {
            return save(schemaMeta.modelName);
        }
        else
        {
            return saveFileAs();
        }
    }

    public boolean saveFileAs()
    {
        try
        {
            log.logBasic(APPLICATION_NAME, "Save file as...");

            EnterStringDialog dialog = new EnterStringDialog(shell, "", "Save model", "Enter a name for this model");
            String modelName = dialog.open();

            if (modelName != null)
            {
                int id = SWT.YES;
                if (CWM.exists(modelName))
                {
                    MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
                    mb.setMessage("This model already exists in the repository.  Do you want to overwrite it?");
                    mb.setText("This model already exists!");
                    id = mb.open();
                }
                if (id == SWT.YES)
                {
                    save(modelName);
                    setModelname(modelName);
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            new ErrorDialog(shell, "Error saving model", "There was a severe error saving this model!", e);
        }
        return false;
    }

    private boolean save(String modelName)
    {
        try
        {
            // Save the schema in the MDR
            SchemaSaveProgressDialog dialog = new SchemaSaveProgressDialog(shell, modelName, schemaMeta);
            cwm = dialog.open();

            // Handle last opened files...
            props.addLastFile(LastUsedFile.FILE_TYPE_SCHEMA, modelName, Const.FILE_SEPARATOR, false, "");
            saveSettings();
            addMenuLast();

            schemaMeta.clearChanged();
            setShellText();
            log.logDebug(APPLICATION_NAME, "File written to the repository with extent [" + modelName + "]");
            return true;
        }
        catch (Exception e)
        {
            new ErrorDialog(shell, "ERROR", "Error saving model", e);
        }
        return false;
    }

    public void deleteFile()
    {
        try
        {
            // Get the available models in the CWM repository
            String[] modelNames = CWM.getModelNames();

            // Show a dialog to select a model
            EnterSelectionDialog selectionDialog = new EnterSelectionDialog(shell, modelNames, "Delete model",
                    "Select the model to delete");
            String modelName = selectionDialog.open();
            if (modelName != null)
            {
                MessageBox mb = new MessageBox(shell, SWT.NO | SWT.YES | SWT.ICON_WARNING);
                mb.setMessage("Are you sure you want to delete this model?");
                mb.setText("Sure about this?!");
                int answer = mb.open();
                if (answer == SWT.YES)
                {
                    CWM delCwm = CWM.getInstance(modelName);
                    delCwm.removeModel();
                }
            }
        }
        catch (Throwable e)
        {
            new ErrorDialog(shell, "Error getting model list", "There was an unexpected getting a list of available models from the repository!",
                    new Exception(e));
        }
    }

    public void helpAbout()
    {
        MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION | SWT.CENTER);
        
        StringBuffer message = new StringBuffer();
        message.append( "Pentaho Metadata Editor " + Const.VERSION + Const.CR + Const.CR  );
        message.append( "(c) 2006-2007 Pentaho Corporation" + Const.CR + "http://www.pentaho.org" + Const.CR );

        message.append( "Copyright 2006 Pentaho Corporation. All rights reserved.")
        		.append( Const.CR ).append( Const.CR )
        		.append( "This software was developed by Pentaho Corporation and is provided under the terms of the Mozilla Public License, Version 1.1, or any later version. If you need a copy of the license, please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho BI Platform. The Initial Developer is Pentaho Corporation.")
			.append( Const.CR ).append( Const.CR )
			.append( "Software distributed under the Mozilla Public License is distributed on an \"AS IS\" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to the license for the specific language governing your rights and limitations. " );
        
        mb.setMessage(message.toString());
        mb.setText("Metadata Editor");
        mb.open();
    }

    public void editUnselectAll()
    {
        if (schemaMeta.getActiveView() == null) return;

        schemaMeta.getActiveView().unselectAll();
        metaEditorGraph.redraw();
    }

    public void editSelectAll()
    {
        if (schemaMeta.getActiveView() == null) return;

        schemaMeta.getActiveView().selectAll();
        metaEditorGraph.redraw();
    }

    public void editOptions()
    {
        EnterOptionsDialog eod = new EnterOptionsDialog(shell, props);
        if (eod.open() != null)
        {
            props.saveProps();
            loadSettings();
            changeLooks();
        }
    }

    public int getTreePosition(TreeItem ti, String item)
    {
        if (ti != null)
        {
            TreeItem items[] = ti.getItems();
            for (int x = 0; x < items.length; x++)
            {
                if (items[x].getText().equalsIgnoreCase(item)) { return x; }
            }
        }
        return -1;
    }

    public void refreshAll()
    {
        refreshTree();
        refreshCategoriesTree();
        refreshGraph();
        metaEditorConcept.refreshTree();
        metaEditorConcept.refreshScreen();
        metaEditorLocales.refreshScreen();
        if (metaEditorOlap != null) metaEditorOlap.refreshScreen();
    }

    public void refreshTree()
    {
        String activeLocale = schemaMeta.getActiveLocale();

        // Remove all connections...
        tiConnections.removeAll();

        // Remove all Views
        tiViews.removeAll();

        for (int d = 0; d < schemaMeta.nrDatabases(); d++)
        {
            DatabaseMeta databaseMeta = schemaMeta.getDatabase(d);

            TreeItem databaseItem = new TreeItem(tiConnections, SWT.NONE);
            databaseItem.setText(databaseMeta.getName());
            databaseItem.setForeground(GUIResource.getInstance().getColorBlack());
            databaseItem.setImage(GUIResource.getInstance().getImageConnection());

            // Below this database we put all the tables that use this database connection...
            PhysicalTable[] tables = schemaMeta.getTablesOnDatabase(databaseMeta);
            for (int t = 0; t < tables.length; t++)
            {
                PhysicalTable table = tables[t];

                TreeItem tableItem = new TreeItem(databaseItem, SWT.NONE);
                tableItem.setText(table.getDisplayName(activeLocale));
                tableItem.setForeground(GUIResource.getInstance().getColorBlack());
                tableItem.setImage(GUIResource.getInstance().getImageBol());

                // Below this we put the columns...
                // OK, now add the columns of the table...
                for (int c = 0; c < table.nrPhysicalColumns(); c++)
                {
                    PhysicalColumn column = table.getPhysicalColumn(c);
                    ConceptInterface concept = column.getConcept();
                    TreeItem columnItem = new TreeItem(tableItem, SWT.NONE);
                    columnItem.setText(0, column.getDisplayName(activeLocale));
                    if (concept != null && concept.findFirstParentConcept() != null)
                    {
                        columnItem.setText(1, concept.findFirstParentConcept().getName());
                    }
                    columnItem.setForeground(GUIResource.getInstance().getColorBlue());
                    columnItem.setImage(GUIResource.getInstance().getImageBol());
                }
            }
        }

        // Refresh views...
        for (int v = 0; v < schemaMeta.nrViews(); v++)
        {
            BusinessView businessView = schemaMeta.getView(v);
            ConceptInterface viewConcept = businessView.getConcept();

            TreeItem viewItem = new TreeItem(tiViews, SWT.NONE);
            viewItem.setText(0, businessView.getDisplayName(activeLocale));
            if (viewConcept != null && viewConcept.findFirstParentConcept() != null)
            {
                viewItem.setText(1, viewConcept.findFirstParentConcept().getName());
            }

            viewItem.setForeground(GUIResource.getInstance().getColorBlack());
            viewItem.setImage(GUIResource.getInstance().getImageBol());

            TreeItem tableParent = new TreeItem(viewItem, SWT.NONE);
            tableParent.setText(STRING_BUSINESS_TABLES);
            tableParent.setForeground(GUIResource.getInstance().getColorBlack());
            tableParent.setImage(GUIResource.getInstance().getImageBol());

            for (int t = 0; t < businessView.nrBusinessTables(); t++)
            {
                BusinessTable businessTable = businessView.getBusinessTable(t);
                ConceptInterface tableConcept = businessTable.getConcept();

                TreeItem tableItem = new TreeItem(tableParent, SWT.NONE);
                tableItem.setText(0, businessTable.getDisplayName(activeLocale));
                if (tableConcept != null && tableConcept.findFirstParentConcept() != null)
                {
                    tableItem.setText(1, tableConcept.findFirstParentConcept().getName());
                }
                tableItem.setForeground(GUIResource.getInstance().getColorBlack());
                tableItem.setImage(GUIResource.getInstance().getImageBol());

                for (int c = 0; c < businessTable.nrBusinessColumns(); c++)
                {
                    BusinessColumn businessColumn = businessTable.getBusinessColumn(c);
                    ConceptInterface columnConcept = businessColumn.getConcept();

                    TreeItem columnItem = new TreeItem(tableItem, SWT.NONE);
                    columnItem.setText(0, businessColumn.getDisplayName(activeLocale));
                    if (columnConcept != null && columnConcept.findFirstParentConcept() != null)
                    {
                        columnItem.setText(1, columnConcept.findFirstParentConcept().getName());
                    }

                    columnItem.setForeground(GUIResource.getInstance().getColorBlue());
                    columnItem.setImage(GUIResource.getInstance().getImageBol());
                }
            }

            TreeItem relationParent = new TreeItem(viewItem, SWT.NONE);
            relationParent.setText(STRING_RELATIONSHIPS);
            relationParent.setForeground(GUIResource.getInstance().getColorBlack());
            relationParent.setImage(GUIResource.getInstance().getImageBol());

            for (int r = 0; r < businessView.nrRelationships(); r++)
            {
                RelationshipMeta relationshipMeta = businessView.getRelationship(r);

                TreeItem columnItem = new TreeItem(relationParent, SWT.NONE);
                columnItem.setText(0, relationshipMeta.toString());
                columnItem.setForeground(GUIResource.getInstance().getColorBlack());
                columnItem.setImage(GUIResource.getInstance().getImageBol());
            }

        }

        // Set expanded from memory...
        TreeMemory.setExpandedFromMemory(mainTree, STRING_MAIN_TREE);

        refreshCategoriesTree();

        setShellText();
    }

    private void refreshCategoriesTree()
    {
        tiCategories.removeAll();

        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView != null)
        {
            String activeLocale = schemaMeta.getActiveLocale();
            addTreeCategories(tiCategories, activeView.getRootCategory(), activeLocale, true);
        }
        TreeMemory.setExpandedFromMemory(catTree, STRING_CATEGORIES_TREE);

        setShellText();
    }

    public static final void addTreeCategories(TreeItem tiParent, BusinessCategory parentCategory, String locale, boolean hiddenToo)
    {
        // Draw the categories tree...
        for (int i = 0; i < parentCategory.nrBusinessCategories(); i++)
        {
            BusinessCategory businessCategory = (BusinessCategory) parentCategory.getBusinessCategory(i);
            ConceptInterface concept = businessCategory.getConcept();

            TreeItem tiCategory = new TreeItem(tiParent, SWT.NONE);
            String name = businessCategory.getDisplayName(locale);
            tiCategory.setText(0, name);
            if (concept != null && concept.findFirstParentConcept() != null)
            {
                tiCategory.setText(1, concept.findFirstParentConcept().getName());
            }
            tiCategory.setForeground(GUIResource.getInstance().getColorBlack());

            // First add the sub-categories...
            addTreeCategories(tiCategory, businessCategory, locale, hiddenToo);
        }

        // Then add the business columns...
        for (int c = 0; c < parentCategory.nrBusinessColumns(); c++)
        {
            BusinessColumn businessColumn = parentCategory.getBusinessColumn(c);

            if (hiddenToo || !businessColumn.isHidden())
            {
                ConceptInterface concept = businessColumn.getConcept();

                TreeItem tiColumn = new TreeItem(tiParent, SWT.NONE);
                tiColumn.setText(0, businessColumn.getDisplayName(locale));
                if (concept != null && concept.findFirstParentConcept() != null)
                {
                    tiColumn.setText(1, concept.findFirstParentConcept().getName());
                }
                tiColumn.setForeground(GUIResource.getInstance().getColorBlue());
            }
        }
    }

    public void refreshGraph()
    {
        metaEditorGraph.redraw();
        setShellText();
    }

    private void setTreeImages()
    {
        tiConnections.setImage(GUIResource.getInstance().getImageConnection());
        tiViews.setImage(GUIResource.getInstance().getImageBol());
    }

    public DatabaseMeta getConnection(String name)
    {
        int i;

        for (i = 0; i < schemaMeta.nrDatabases(); i++)
        {
            DatabaseMeta ci = schemaMeta.getDatabase(i);
            if (ci.getName().equalsIgnoreCase(name)) { return ci; }
        }
        return null;
    }

    public void setShellText()
    {
        String fname = schemaMeta.modelName;
        if (shell.isDisposed()) return;
        if (fname != null)
        {
            shell.setText(APPLICATION_NAME + " - " + fname + (schemaMeta.hasChanged() ? " (changed)" : ""));
        }
        else
        {
            shell.setText(APPLICATION_NAME + (schemaMeta.hasChanged() ? " (changed)" : ""));
        }
    }

    public void setModelname(String modelName)
    {
        schemaMeta.modelName = modelName;
        setShellText();
    }

    private void printFile()
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return;

        PrintSpool ps = new PrintSpool();
        Printer printer = ps.getPrinter(shell);

        // Create an image of the screen
        Point max = activeView.getMaximum();

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

    public void saveSettings()
    {
        WindowProperty winprop = new WindowProperty(shell);
        props.setScreen(winprop);
        props.setLogLevel(log.getLogLevelDesc());
        props.setSashWeights(sashform.getWeights());
        props.saveProps();
    }

    public void loadSettings()
    {
        log.setLogLevel(props.getLogLevel());

        GUIResource.getInstance().reload();

        DBCache.getInstance().setActive(props.useDBCache());
    }

    public void changeLooks()
    {
        mainTree.setBackground(GUIResource.getInstance().getColorBackground());
        metaEditorGraph.newProps();

        refreshAll();
    }

    public void clearDBCache()
    {
        // Determine what menu we selected from...

        TreeItem ti[] = mainTree.getSelection();

        // Then call editConnection or editStep or editTrans
        if (ti.length == 1)
        {
            String name = ti[0].getText();
            TreeItem parent = ti[0].getParentItem();
            if (parent != null)
            {
                String type = parent.getText();
                if (type.equalsIgnoreCase(STRING_CONNECTIONS))
                {
                    DBCache.getInstance().clear(name);
                }
            }
            else
            {
                if (name.equalsIgnoreCase(STRING_CONNECTIONS)) DBCache.getInstance().clear(null);
            }
        }
    }

    public void importTables(String databaseName)
    {
        DatabaseMeta databaseMeta = schemaMeta.findDatabase(databaseName);
        if (databaseMeta != null)
        {
            DatabaseExplorerDialog std = new DatabaseExplorerDialog(shell, SWT.NONE, databaseMeta, schemaMeta.databases.getList(), false, true);
            if (std.open() != null)
            {
                String schemaName = std.getSchemaName();
                String tableName = std.getTableName();

                Database database = new Database(databaseMeta);
                try
                {
                    database.connect();

                    importTableDefinition(database, schemaName, tableName);
                }
                catch (KettleException e)
                {
                    new ErrorDialog(shell, "Error", "Error reading fields of table : " + tableName
                            + ((schemaName != null) ? ("(schema=" + schemaName + ")") : ""), e);
                }
                finally
                {
                    if (database != null) database.disconnect();
                }
            }
        }
    }

    public void importMultipleTables(String databaseName)
    {
        DatabaseMeta databaseMeta = schemaMeta.findDatabase(databaseName);
        if (databaseMeta != null)
        {
            Database database = null;
            try
            {
                database = new Database(databaseMeta);
                database.connect();

                // Get the list of tables...
                String[] tableNames = database.getTablenames();

                // Select from it...
                EnterSelectionDialog dialog = new EnterSelectionDialog(shell, tableNames, "Import tables", "Select the tables to import");
                dialog.setMulti(true);
                if (dialog.open() != null)
                {
                    int[] indexes = dialog.getSelectionIndeces();
                    for (int i = 0; i < indexes.length; i++)
                    {
                        String tableName = tableNames[indexes[i]];
                        importTableDefinition(database, null, tableName);
                    }
                }

            }
            catch (Exception e)
            {
                new ErrorDialog(shell, "Error", "There was an error importing physical tables", e);
            }
            finally
            {
                if (database != null) database.disconnect();
            }
        }
    }

    private void importTableDefinition(Database database, String schemaName, String tableName) throws KettleException
    {
        UniqueArrayList fields = new UniqueArrayList();

        String id = tableName;
        String tablename = tableName;

        // Remove
        id = Const.toID(tableName);

        // Set the id to a certain standard...
        id = Settings.getPhysicalTableIDPrefix() + id;
        if (Settings.isAnIdUppercase()) id = id.toUpperCase();

        if (schemaMeta.findPhysicalTable(id) != null)
        {
            // find a new name for the table: add " 2", " 3", " 4", ... to name:
            int copy = 2;
            String newname = id + " " + copy;
            while (schemaMeta.findPhysicalTable(newname) != null)
            {
                copy++;
                newname = id + " " + copy;
            }
            id = newname;
        }

        PhysicalTable physicalTable = new PhysicalTable(id, schemaName, tableName, database.getDatabaseMeta(), fields);

        // Also set a localized description...
        String niceName = beautifyName(tablename);
        physicalTable.getConcept().setName(schemaMeta.getActiveLocale(), niceName);

        DatabaseMeta dbMeta = database.getDatabaseMeta();
        String schemaTableCombination = dbMeta.getSchemaTableCombination(dbMeta.quoteField(schemaName), dbMeta.quoteField(tableName));

        Row row = database.getTableFields(schemaTableCombination);

        if (row != null && row.size() > 0)
        {
            for (int i = 0; i < row.size(); i++)
            {
                Value v = row.getValue(i);
                PhysicalColumn physicalColumn = importPhysicalColumnDefinition(v, physicalTable);
                try
                {
                    fields.add(physicalColumn);
                }
                catch (ObjectAlreadyExistsException e)
                {
                    // Don't add this column
                    // TODO: show error dialog.
                }
            }
        }
        String upper = tablename.toUpperCase();

        if (upper.startsWith("D_") || upper.startsWith("DIM") || upper.endsWith("DIM")) physicalTable.setTableType(TableTypeSettings.DIMENSION);
        if (upper.startsWith("F_") || upper.startsWith("FACT") || upper.endsWith("FACT")) physicalTable.setTableType(TableTypeSettings.FACT);

        try
        {
            schemaMeta.addTable(physicalTable);
        }
        catch(ObjectAlreadyExistsException e)
        {
            new ErrorDialog(shell, "Error", "A physical table with id '"+physicalTable.getId()+"' already exists", e);
        }
        
        refreshTree();
    }

    private PhysicalColumn importPhysicalColumnDefinition(Value v, PhysicalTable physicalTable)
    {
        // The id
        String id = Settings.getPhysicalColumnIDPrefix() + v.getName();
        if (Settings.isAnIdUppercase()) id = id.toUpperCase();

        // The name of the column in the database
        String dbname = v.getName();

        // The field type?
        FieldTypeSettings fieldType = FieldTypeSettings.guessFieldType(v.getName());

        // Create a physical column.
        PhysicalColumn physicalColumn = new PhysicalColumn(v.getName(), dbname, fieldType, AggregationSettings.NONE, physicalTable);

        // Set the localised name...
        String niceName = beautifyName(v.getName());
        physicalColumn.setName(schemaMeta.getActiveLocale(), niceName);

        // Set the parent concept to the base concept...
        physicalColumn.getConcept().setParentInterface(schemaMeta.findConcept(Settings.getConceptNameBase()));

        // The data type...
        DataTypeSettings dataTypeSettings = getDataTypeSettings(v);
        ConceptPropertyInterface dataTypeProperty = new ConceptPropertyDataType(DefaultPropertyID.DATA_TYPE.getId(), dataTypeSettings);
        physicalColumn.getConcept().addProperty(dataTypeProperty);

        // It this a key field? If yes: set the appropriate parent concept...
        if (fieldType.equals(FieldTypeSettings.KEY))
        {
            ConceptInterface parentIDConcept = schemaMeta.findConcept(Settings.getConceptNameID());
            if (parentIDConcept != null) physicalColumn.getConcept().setParentInterface(parentIDConcept);
        }

        return physicalColumn;
    }

    private static final String beautifyName(String name)
    {
        return new Value("niceName", name).replace("_", " ").initcap().getString();
    }

    private DataTypeSettings getDataTypeSettings(Value v)
    {
        DataTypeSettings dataTypeSettings = new DataTypeSettings(DataTypeSettings.DATA_TYPE_STRING);
        switch (v.getType())
        {
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

    public void exploreDB()
    {
        // Determine what menu we selected from...

        TreeItem ti[] = mainTree.getSelection();

        // Then call editConnection or editStep or editTrans
        if (ti.length == 1)
        {
            String name = ti[0].getText();
            TreeItem parent = ti[0].getParentItem();
            if (parent != null)
            {
                String type = parent.getText();
                if (type.equalsIgnoreCase(STRING_CONNECTIONS))
                {
                    DatabaseMeta dbinfo = schemaMeta.findDatabase(name);
                    if (dbinfo != null)
                    {
                        DatabaseExplorerDialog std = new DatabaseExplorerDialog(shell, SWT.NONE, dbinfo, schemaMeta.databases.getList(), true);
                        std.open();
                    }
                    else
                    {
                        MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                        mb.setMessage("Couldn't find connection, please refresh the tree (F5)!");
                        mb.setText("Error!");
                        mb.open();
                    }
                }
            }
            else
            {
                if (name.equalsIgnoreCase(STRING_CONNECTIONS)) DBCache.getInstance().clear(null);
            }
        }
    }

    public String toString()
    {
        return this.getClass().getName();
    }

    public static void main(String[] args) throws Exception
    {
        EnvUtil.environmentInit();
        LogWriter log = LogWriter.getInstance(Const.META_EDITOR_LOG_FILE, false, LogWriter.LOG_LEVEL_BASIC);
        LogWriter.setLayout(new Log4jPMELayout(true));

        Display display = new Display();

        if (!Props.isInitialized())
        {
            Const.checkPentahoMetadataDirectory();
            Props.init(display, Const.getPropertiesFile()); // things to remember...
        }

        Splash splash = new Splash(display);

        final MetaEditor win = new MetaEditor(log, display);

        // Read kettle transformation specified on command-line?
        if (args.length == 1 && !Const.isEmpty(args[0]))
        {
            if (CWM.exists(args[0])) // Only try to load the model if it exists.
            {
                win.cwm = CWM.getInstance(args[0]);
                CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
                win.schemaMeta = cwmSchemaFactory.getSchemaMeta(win.cwm);
                win.setModelname(args[0]);
                win.schemaMeta.clearChanged();
            }
            else
            {
                win.newFile();
            }
        }
        else
        {
            if (win.props.openLastFile())
            {
                String lastfiles[] = win.props.getLastFiles();
                if (lastfiles.length > 0)
                {
                    try
                    {
                        if (CWM.exists(lastfiles[0])) // Only try to load the model if it exists.
                        {
                            win.cwm = CWM.getInstance(lastfiles[0]);
                            CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
                            win.schemaMeta = cwmSchemaFactory.getSchemaMeta(win.cwm);
                            win.setModelname(lastfiles[0]);
                            win.schemaMeta.clearChanged();
                        }
                        else
                        {
                            win.newFile();
                        }
                    }
                    catch (Exception e)
                    {
                        log.logError(APPLICATION_NAME, "Unable to check for existance of a model: " + e.toString());
                        log.logError(APPLICATION_NAME, Const.getStackTracker(e));
                    }
                }
                else
                {
                    win.newFile();
                }
            }
            else
            {
                win.newFile();
            }
        }

        splash.hide();

        win.open();
        while (!win.isDisposed())
        {
            if (!win.readAndDispatch()) win.sleep();
        }
        win.dispose();

        // Close the logfile...
        log.close();
    }

    /**
     * @return the schemaMeta
     */
    public SchemaMeta getSchemaMeta()
    {
        return schemaMeta;
    }

    /**
     * @param schemaMeta the schemaMeta to set
     */
    public void setSchemaMeta(SchemaMeta schemaMeta)
    {
        this.schemaMeta = schemaMeta;
    }

    public void editBusinessTable(String businessTableName)
    {
        BusinessView activeView = schemaMeta.getActiveView();
        if (activeView == null) return;

        BusinessTable businessTable = activeView.findBusinessTable(schemaMeta.getActiveLocale(), businessTableName);
        if (businessTable != null)
        {
            editBusinessTable(businessTable);
        }
    }

    public void editBusinessTable(BusinessTable businessTable)
    {
        BusinessTableDialog dialog = new BusinessTableDialog(shell, businessTable, schemaMeta);
        if (dialog.open() != null)
        {
            refreshAll();
        }
    }

    /**
     * Test Query & Reporting
     * 
     */
    protected void testQR()
    {
        try
        {
            QueryDialog queryDialog = new QueryDialog(shell, schemaMeta, query);
            MQLQuery lastQuery = queryDialog.open();
            if (lastQuery != null)
            {
                query = lastQuery;
                saveQuery();
            }
            /*
             * query = MakeSelectionDemo.executeDemo(shell, props, query, false); // Don't shut down, let it be. if
             * (query!=null) { saveQuery(); }
             */
        }
        catch (Exception e)
        {
            new ErrorDialog(shell, "Demo erro", "There was an error running the demo", e);
        }
    }

    private void saveQuery()
    {
        try
        {
            if (query != null) query.save(Const.getQueryFile());
        }
        catch (Exception e)
        {
            log.logError(APPLICATION_NAME, "Unable to save query :" + e.toString());
            log.logError(APPLICATION_NAME, Const.getStackTracker(e));
        }
    }

    private void loadQuery()
    {
        try
        {
            File file = new File(Const.getQueryFile());
            FileInputStream fileInputStream = new FileInputStream(file);
            byte bytes[] = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            fileInputStream.close();

            query = new MQLQuery(new String(bytes, Const.XML_ENCODING), Const.XML_ENCODING, cwmSchemaFactory);
        }
        catch (Exception e)
        {
            log.logError(APPLICATION_NAME, "Unable to load query :" + e.toString());
        }
    }

    public void editSecurityService()
    {
        SecurityServiceDialog dialog = new SecurityServiceDialog(shell, schemaMeta.getSecurityReference().getSecurityService());
        if (dialog.open())
        {
            // try to grab it from the security service if it exists...
            SecurityService securityService = schemaMeta.getSecurityReference().getSecurityService();
            if (securityService != null)
            {
                try
                {
                    schemaMeta.setSecurityReference(new SecurityReference(securityService));
                }
                catch (Throwable e)
                {
                    new ErrorDialog(shell, "Error", "There was an error loading the security reference information from the security service",
                            new Exception(e));
                }
            }

            refreshAll();
        }
    }

    public void getMondrianModel()
    {
        BusinessView activeView = schemaMeta.getActiveView();
        String locale = schemaMeta.getActiveLocale();

        if (activeView != null)
        {
            try
            {
                String xml = activeView.getMondrianModel(locale);

                EnterTextDialog dialog = new EnterTextDialog(shell, "Mondrian XML", "This is the generated Mondrian model", xml);
                dialog.open();
            }
            catch (Exception e)
            {
                new ErrorDialog(shell, "Model error", "There was an error during model creation", e);
            }
        }
    }

    /**
     * @return the selected concept utility interfaces...
     */
    public ConceptUtilityInterface[] getSelectedConceptUtilityInterfacesInMainTree()
    {
        List list = new ArrayList();

        String locale = schemaMeta.getActiveLocale();

        // The main tree
        TreeItem[] selection = mainTree.getSelection();
        for (int i = 0; i < selection.length; i++)
        {
            TreeItem treeItem = selection[i];
            String[] path = Const.getTreeStrings(treeItem);
            if (path[0].equals(STRING_CONNECTIONS))
            {
                switch (path.length)
                {
                case 3: // physical table
                {
                    PhysicalTable table = schemaMeta.findPhysicalTable(locale, path[2]);
                    if (table != null) list.add(table);
                }
                    break;
                case 4: // Name of a physical column
                {
                    PhysicalTable table = schemaMeta.findPhysicalTable(locale, path[2]);
                    if (table != null)
                    {
                        PhysicalColumn column = table.findPhysicalColumn(locale, path[3]);
                        if (column != null) list.add(column);
                    }
                }
                    break;
                }
            }
            else
                if (path[0].equals(STRING_BUSINESS_VIEWS))
                {
                    switch (path.length)
                    {
                    case 2: // Business View name
                    {
                        BusinessView view = schemaMeta.findView(locale, path[1]);
                        if (view != null) list.add(view);
                    }
                        break;
                    case 4: // Business Tables or Relationships
                        if (path[2].equals(STRING_BUSINESS_TABLES))
                        {
                            BusinessView view = schemaMeta.findView(locale, path[1]);
                            if (view != null)
                            {
                                BusinessTable table = view.findBusinessTable(locale, path[3]);
                                if (table != null) list.add(table);
                            }
                        }
                        break;
                    case 5: // Business Column
                        if (path[2].equals(STRING_BUSINESS_TABLES))
                        {
                            BusinessView view = schemaMeta.findView(locale, path[1]);
                            if (view != null)
                            {
                                BusinessTable table = view.findBusinessTable(locale, path[3]);
                                if (table != null)
                                {
                                    BusinessColumn column = table.findBusinessColumn(locale, path[4]);
                                    if (column != null) list.add(column);
                                }
                            }
                        }
                        break;
                    }
                }
        }

        return (ConceptUtilityInterface[]) list.toArray(new ConceptUtilityInterface[list.size()]);
    }

    /**
     * @return the selected concept utility interfaces...
     */
    public ConceptUtilityInterface[] getSelectedConceptUtilityInterfacesInCategoriesTree()
    {
        List list = new ArrayList();

        BusinessView activeView = schemaMeta.getActiveView();
        String locale = schemaMeta.getActiveLocale();
        if (activeView == null) return new ConceptUtilityInterface[0];

        // The main tree
        TreeItem[] selection = catTree.getSelection();
        for (int i = 0; i < selection.length; i++)
        {
            TreeItem treeItem = selection[i];
            String[] path = Const.getTreeStrings(treeItem, 1);

            if (path.length > 0)
            {
                BusinessCategory category = activeView.findBusinessCategory(path, locale, true); // exact match
                if (category != null)
                {
                    list.add(category);
                }
                else
                {
                    String[] parentPath = new String[path.length - 1];
                    for (int x = 0; x < parentPath.length; x++)
                        parentPath[x] = path[x];

                    BusinessCategory parentCategory = activeView.getRootCategory();
                    if (parentPath.length > 0) activeView.findBusinessCategory(parentPath, locale, true);
                    if (parentCategory != null)
                    {
                        // Now get the business column below that...
                        BusinessColumn businessColumn = parentCategory.findBusinessColumn(treeItem.getText(), locale);
                        if (businessColumn != null)
                        {
                            list.add(businessColumn);
                        }
                        else
                        {
                            list.add(parentCategory);
                        }
                    }
                }
            }
        }

        return (ConceptUtilityInterface[]) list.toArray(new ConceptUtilityInterface[list.size()]);
    }

    protected void setParentConcept(ConceptUtilityInterface[] utilityInterfaces)
    {
        String[] concepts = schemaMeta.getConceptNames();

        // Ask the user to pick a parent concept...
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, concepts, "Select a parent concept",
                "Please select a parent concept to set");
        String conceptName = dialog.open();
        if (conceptName != null)
        {
            ConceptInterface parentInterface = schemaMeta.findConcept(conceptName);

            for (int u = 0; u < utilityInterfaces.length; u++)
            {
                utilityInterfaces[u].getConcept().setParentInterface(parentInterface);
                utilityInterfaces[u].setChanged();
            }

            refreshAll();
        }
    }

    protected void clearParentConcept(ConceptUtilityInterface[] utilityInterfaces)
    {
        for (int u = 0; u < utilityInterfaces.length; u++)
        {
            utilityInterfaces[u].getConcept().setParentInterface(null);
            utilityInterfaces[u].setChanged();
        }

        refreshAll();
    }

    protected void removeChildProperties(ConceptUtilityInterface[] utilityInterfaces)
    {
        // First we need a distinct list of all property IDs...
        Map all = new Hashtable();
        for (int u = 0; u < utilityInterfaces.length; u++)
        {
            String ids[] = utilityInterfaces[u].getConcept().getChildPropertyIDs();
            for (int i = 0; i < ids.length; i++)
            {
                all.put(ids[i], "");
            }
        }
        Set keySet = all.keySet();
        String ids[] = (String[]) keySet.toArray(new String[keySet.size()]);
        String names[] = new String[ids.length];

        // Get the descriptions to show...
        for (int i = 0; i < ids.length; i++)
        {
            DefaultPropertyID propertyID = DefaultPropertyID.findDefaultPropertyID(ids[i]);
            if (propertyID != null)
                names[i] = propertyID.getDescription();
            else
                names[i] = ids[i];
        }

        // Ask the user to pick the child properties to delete...
        EnterSelectionDialog dialog = new EnterSelectionDialog(shell, names, "Delete properties", "Select the child properties to delete:");
        String conceptName = dialog.open();
        if (conceptName != null)
        {

            for (int u = 0; u < utilityInterfaces.length; u++)
            {
                ConceptInterface concept = utilityInterfaces[u].getConcept();

                int idxs[] = dialog.getSelectionIndeces();
                for (int i = 0; i < idxs.length; i++)
                {
                    ConceptPropertyInterface property = concept.getChildProperty(ids[idxs[i]]);
                    if (property != null)
                    {
                        concept.removeChildProperty(property);
                        utilityInterfaces[u].setChanged();
                    }
                }
            }

            refreshAll();
        }
    }
}
