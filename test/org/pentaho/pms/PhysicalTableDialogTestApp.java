package org.pentaho.pms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.editor.ConceptModel;
import org.pentaho.pms.schema.concept.editor.ConceptModificationEvent;
import org.pentaho.pms.schema.concept.editor.Constants;
import org.pentaho.pms.schema.concept.editor.IConceptModel;
import org.pentaho.pms.schema.concept.editor.IConceptModificationListener;
import org.pentaho.pms.schema.concept.editor.ITableModel;
import org.pentaho.pms.schema.concept.editor.PhysicalTableModel;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.dialog.PhysicalTableDialog;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.util.EnvUtil;

public class PhysicalTableDialogTestApp extends ApplicationWindow {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PhysicalTableDialogTestApp.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private ITableModel tableModel;

  private Map context = new HashMap();

  private PhysicalTable tab;

  private SchemaMeta schemaMeta;

  private Locales locales;

  // ~ Constructors ====================================================================================================

  public PhysicalTableDialogTestApp() {
    super(null);
    initModel();
  }

  // ~ Methods =========================================================================================================

  protected void initModel() {
    conceptModel = new ConceptModel(new Concept());
    LocalizedStringSettings s1 = new LocalizedStringSettings();
    s1.setLocaleString("en_US", "chicken");
    s1.setLocaleString("es_ES", "pollo");
    conceptModel.setProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.NAME.getId(), s1));

    LocalizedStringSettings s2 = new LocalizedStringSettings();
    s2.setLocaleString("en_US", "Where is the library?");
    s2.setLocaleString("es_ES", "¿Dónde está la biblioteca?");
    conceptModel.setProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.DESCRIPTION.getId(), s2));
    conceptModel
        .setProperty(new ConceptPropertyColumnWidth(DefaultPropertyID.COLUMN_WIDTH.getId(), ColumnWidth.INCHES));
    conceptModel.setProperty(new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(), "overridden_table"));
    Concept parentConcept = new Concept();
    ConceptPropertyInterface prop1 = new ConceptPropertyString(DefaultPropertyID.FORMULA.getId(), "e=mc2");
    parentConcept.addProperty(prop1);
    ConceptPropertyInterface prop2 = new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(), "test_schema");
    parentConcept.addProperty(prop2);
    conceptModel.setRelatedConcept(parentConcept, IConceptModel.REL_PARENT);

    Concept secConcept = new Concept();
    //    ConceptPropertyInterface sec1 = new ConceptPropertySecurity(DefaultPropertyID.SECURITY.getId(), new Security());
    //    secConcept.addProperty(sec1);
    ConceptPropertyInterface sec2 = new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(), "test_table");
    secConcept.addProperty(sec2);
    conceptModel.setRelatedConcept(secConcept, IConceptModel.REL_INHERITED);

    Concept inConcept = new Concept();
    ConceptPropertyInterface in1 = new ConceptPropertyBoolean(DefaultPropertyID.EXACT.getId(), true);
    inConcept.addProperty(in1);
    ConceptPropertyInterface in2 = new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(), "test_table");
    inConcept.addProperty(in2);
    conceptModel.setRelatedConcept(inConcept, IConceptModel.REL_SECURITY);

    locales = new Locales();
    //    locales.addLocale(new LocaleMeta(Locales.EN_US, Messages.getString("Locales.USER_LOCALE_DESCRIPTION"), 1, true));
    locales.addLocale(new LocaleMeta("fr_FR", "French (France)", 2, true));
    locales.addLocale(new LocaleMeta("it_IT", "Italian (Italy)", 3, true));
    locales.addLocale(new LocaleMeta("es_ES", "Spanish (Spain)", 4, true));
    locales.addLocale(new LocaleMeta("de_DE", "German (Germany)", 5, true));
    schemaMeta = new SchemaMeta();
    schemaMeta.setLocales(locales);
    conceptModel.addConceptModificationListener(new IConceptModificationListener() {
      public void conceptModified(final ConceptModificationEvent e) {
        PhysicalTableDialogTestApp.this.conceptModified(e);
      }
    });

    context.put("locales", schemaMeta.getLocales());

    tab = new PhysicalTable("PT_TEST_TABLE_1");
    try {
      PhysicalColumn pc1 = new PhysicalColumn("COL_1");
      Concept c1 = new Concept();
      LocalizedStringSettings s10 = new LocalizedStringSettings();
      s10.setLocaleString("en_US", "beans");
      s10.setLocaleString("es_ES", "frijoles");
      c1.addProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.NAME.getId(), s10));
      pc1.setConcept(c1);

      tab.addPhysicalColumn(pc1);
      //    tab.addPhysicalColumn(new PhysicalColumn("COL_2"));
      //    tab.addPhysicalColumn(new PhysicalColumn("COL_3"));

    } catch (ObjectAlreadyExistsException e1) {
      if (logger.isErrorEnabled()) {
        // TODO Auto-generated catch block
        logger.error("an exception occurred", e1);
      }
    }

    PhysicalTable tab = new PhysicalTable("my_physical_table");
    PhysicalColumn col1 = new PhysicalColumn("my_col1");
    PhysicalColumn col2 = new PhysicalColumn("my_col2");
    try {
      tab.addPhysicalColumn(col1);
      tab.addPhysicalColumn(col2);
    } catch (ObjectAlreadyExistsException e1) {
      if (logger.isErrorEnabled()) {
        // TODO Auto-generated catch block
        logger.error("an exception occurred", e1);
      }
    }

    tableModel = new PhysicalTableModel(tab);

  }

  protected void conceptModified(final ConceptModificationEvent e) {
    if (logger.isDebugEnabled()) {
      logger.debug("heard concept modified event: " + e);
    }
  }

  public void run() {
    EnvUtil.environmentInit();
    if (!Props.isInitialized()) {
      Const.checkPentahoMetadataDirectory();
      Props.init(new Display(), Const.getPropertiesFile());
    }
    setBlockOnOpen(true);
    open();
    Display.getCurrent().dispose();
  }

  protected Point getInitialSize() {
    return new Point(300, 300);
  }

  protected Control createContents(final Composite parent) {
    Composite c0 = new Composite(parent, SWT.NONE);
    c0.setLayout(new FormLayout());

    if (logger.isDebugEnabled()) {
      logger.debug("orig table model: " + tableModel);
    }

    final PhysicalTableDialog diag = new PhysicalTableDialog(this.getShell(), tab, schemaMeta);

    if (logger.isDebugEnabled()) {
      logger.debug("mod'ed table model: " + tableModel);
    }

    //    final TableColumnTreeWidget t = new TableColumnTreeWidget(c0, SWT.NONE, tableModel, true);

    final Button b = new Button(c0, SWT.NONE);
    FormData fdButton = new FormData();
    fdButton.left = new FormAttachment(0, 10);
    fdButton.top = new FormAttachment(0, 10);
    b.setLayoutData(fdButton);

    //    FormData fdTree = new FormData();
    //    fdTree.left = new FormAttachment(0, 10);
    //    fdTree.top = new FormAttachment(b, 10);
    //    t.setLayoutData(fdTree);

    b.setText("Go");
    b.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        int result = diag.open();
        if (Window.OK == result) {
          if (logger.isDebugEnabled()) {
            logger.debug("user clicked ok; time to save");
          }
          saveTable();
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug("user clicked cancel");
          }
        }
      }

    });

    return c0;
  }

  private void saveTable() {
    if (logger.isDebugEnabled()) {
      logger.debug("saving table");
    }

  }

  public static void main(final String[] args) {
    new PhysicalTableDialogTestApp().run();
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Physical Table Dialog Test Application");
    shell.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-editor-app"));
  }

}