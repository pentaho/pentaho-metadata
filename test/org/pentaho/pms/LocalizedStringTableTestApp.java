package org.pentaho.pms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.editor.ConceptModel;
import org.pentaho.pms.schema.concept.editor.ConceptModificationEvent;
import org.pentaho.pms.schema.concept.editor.Constants;
import org.pentaho.pms.schema.concept.editor.IConceptModel;
import org.pentaho.pms.schema.concept.editor.IConceptModificationListener;
import org.pentaho.pms.schema.concept.editor.LocalizedStringTableWidget;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.util.Const;

public class LocalizedStringTableTestApp extends ApplicationWindow {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(LocalizedStringTableTestApp.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private SchemaMeta schemaMeta;

  // ~ Constructors ====================================================================================================

  public LocalizedStringTableTestApp() {
    super(null);
    initModel();
    conceptModel.addConceptModificationListener(new IConceptModificationListener() {
      public void conceptModified(final ConceptModificationEvent e) {
        LocalizedStringTableTestApp.this.conceptModified(e);
      }
    });
  }

  // ~ Methods =========================================================================================================

  protected void conceptModified(final ConceptModificationEvent e) {
    if (logger.isDebugEnabled()) {
      logger.debug("heard concept modified event: " + e);
    }

  }

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

    Locales locales = new Locales();
    //    locales.addLocale(new LocaleMeta(Locales.EN_US, Messages.getString("Locales.USER_LOCALE_DESCRIPTION"), 1, true));
    locales.addLocale(new LocaleMeta("fr_FR", "French (France)", 2, true));
    locales.addLocale(new LocaleMeta("it_IT", "Italian (Italy)", 3, true));
    locales.addLocale(new LocaleMeta("es_ES", "Spanish (Spain)", 4, true));
    locales.addLocale(new LocaleMeta("de_DE", "German (Germany)", 5, true));
    schemaMeta = new SchemaMeta();
    schemaMeta.setLocales(locales);
  }

  public void run() {
    if (!PropsUI.isInitialized()) {
      Const.checkPentahoMetadataDirectory();
      PropsUI.init(new Display(), Const.getPropertiesFile());
    }
    setBlockOnOpen(true);
    open();
    Display.getCurrent().dispose();
  }

  protected Point getInitialSize() {
    return new Point(600, 400);
  }

  protected Control createContents(final Composite parent) {
    return new LocalizedStringTableWidget(parent, SWT.NONE, conceptModel, "name", schemaMeta.getLocales());

  }

  public static void main(final String[] args) {
    new LocalizedStringTableTestApp().run();
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Localized String Table Test Application");
    shell.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-editor-app"));
  }

}
