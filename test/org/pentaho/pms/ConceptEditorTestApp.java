package org.pentaho.pms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.editor.ConceptEditorWidget;
import org.pentaho.pms.schema.concept.editor.ConceptModel;
import org.pentaho.pms.schema.concept.editor.ConceptModificationEvent;
import org.pentaho.pms.schema.concept.editor.Constants;
import org.pentaho.pms.schema.concept.editor.IConceptModel;
import org.pentaho.pms.schema.concept.editor.IConceptModificationListener;
import org.pentaho.pms.schema.concept.editor.PropertyNavigationWidget;
import org.pentaho.pms.schema.concept.editor.PropertyWidgetManager2;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.util.EnvUtil;

public class ConceptEditorTestApp extends ApplicationWindow {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptEditorTestApp.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private Map context = new HashMap();

  // ~ Constructors ====================================================================================================

  public ConceptEditorTestApp() {
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

    Locales locales = new Locales();
    //    locales.addLocale(new LocaleMeta(Locales.EN_US, Messages.getString("Locales.USER_LOCALE_DESCRIPTION"), 1, true));
    locales.addLocale(new LocaleMeta("fr_FR", "French (France)", 2, true));
    locales.addLocale(new LocaleMeta("it_IT", "Italian (Italy)", 3, true));
    locales.addLocale(new LocaleMeta("es_ES", "Spanish (Spain)", 4, true));
    locales.addLocale(new LocaleMeta("de_DE", "German (Germany)", 5, true));
    SchemaMeta schemaMeta = new SchemaMeta();
    schemaMeta.setLocales(locales);
    conceptModel.addConceptModificationListener(new IConceptModificationListener() {
      public void conceptModified(final ConceptModificationEvent e) {
        ConceptEditorTestApp.this.conceptModified(e);
      }
    });

    context.put("locales", schemaMeta.getLocales());
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
    return new Point(600, 400);
  }

  protected Control createContents(final Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new FillLayout());

    Group group = new Group(composite, SWT.SHADOW_OUT);
    group.setText("Properties");
    group.setLayout(new FillLayout());
    SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
    s0.SASH_WIDTH = 10;
    PropertyNavigationWidget propertyNavigationWidget = new PropertyNavigationWidget(s0, SWT.NONE, conceptModel);
    PropertyWidgetManager2 propertyWidgetManager = new PropertyWidgetManager2(s0, SWT.NONE, conceptModel, context, null);
    propertyNavigationWidget.addSelectionChangedListener(propertyWidgetManager);
    s0.setWeights(new int[] { 1, 2 });
    return composite;
  }

  public static void main(final String[] args) {
    new ConceptEditorTestApp().run();
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Concept Editor");
    shell.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-editor-app"));
  }
  

}
