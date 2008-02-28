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
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.editor.BusinessTableModel;
import org.pentaho.pms.schema.concept.editor.ConceptModel;
import org.pentaho.pms.schema.concept.editor.ConceptModificationEvent;
import org.pentaho.pms.schema.concept.editor.Constants;
import org.pentaho.pms.schema.concept.editor.IConceptModel;
import org.pentaho.pms.schema.concept.editor.IConceptModificationListener;
import org.pentaho.pms.schema.concept.editor.ITableModel;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.dialog.BusinessTableDialog;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

public class BusinessTableDialogTestApp extends ApplicationWindow
{
	// ~ Static fields/initializers
	// ======================================================================================

	private static final Log logger = LogFactory.getLog(BusinessTableDialogTestApp.class);

	// ~ Instance fields
	// =================================================================================================

	private IConceptModel conceptModel;

	private ITableModel tableModel;

	private Map<String,Locales> context = new HashMap<String,Locales>();

	private BusinessTable tab;

	private SchemaMeta schemaMeta;

	private Locales locales;

	// ~ Constructors
	// ====================================================================================================

	public BusinessTableDialogTestApp()
	{
		super(null);
		try
		{
			initModel();
		} catch (ObjectAlreadyExistsException e)
		{
			throw new RuntimeException(e);
		}
	}

	// ~ Methods
	// =========================================================================================================

	protected void initModel() throws ObjectAlreadyExistsException
	{
		conceptModel = new ConceptModel(new Concept());
		LocalizedStringSettings s1 = new LocalizedStringSettings();
		s1.setLocaleString("en_US", "chicken");
		s1.setLocaleString("es_ES", "pollo");
		conceptModel.setProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.NAME.getId(), s1));

		LocalizedStringSettings s2 = new LocalizedStringSettings();
		s2.setLocaleString("en_US", "Where is the library?");
		s2.setLocaleString("es_ES", "¿Dónde está la biblioteca?");
		conceptModel
				.setProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.DESCRIPTION.getId(), s2));
		conceptModel.setProperty(new ConceptPropertyColumnWidth(DefaultPropertyID.COLUMN_WIDTH.getId(),
				ColumnWidth.INCHES));
		conceptModel.setProperty(new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(),
				"overridden_table"));
		Concept parentConcept = new Concept();
		ConceptPropertyInterface prop1 = new ConceptPropertyString(DefaultPropertyID.FORMULA.getId(), "e=mc2");
		parentConcept.addProperty(prop1);
		ConceptPropertyInterface prop2 = new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(),
				"test_schema");
		parentConcept.addProperty(prop2);
		conceptModel.setRelatedConcept(parentConcept, IConceptModel.REL_PARENT);

		Concept secConcept = new Concept();
		// ConceptPropertyInterface sec1 = new
		// ConceptPropertySecurity(DefaultPropertyID.SECURITY.getId(), new
		// Security());
		// secConcept.addProperty(sec1);
		ConceptPropertyInterface sec2 = new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(),
				"test_table");
		secConcept.addProperty(sec2);
		conceptModel.setRelatedConcept(secConcept, IConceptModel.REL_INHERITED);

		Concept inConcept = new Concept();
		ConceptPropertyInterface in1 = new ConceptPropertyBoolean(DefaultPropertyID.EXACT.getId(), true);
		inConcept.addProperty(in1);
		ConceptPropertyInterface in2 = new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(),
				"test_table");
		inConcept.addProperty(in2);
		conceptModel.setRelatedConcept(inConcept, IConceptModel.REL_SECURITY);

		locales = new Locales();
		// locales.addLocale(new LocaleMeta(Locales.EN_US,
		// Messages.getString("Locales.USER_LOCALE_DESCRIPTION"), 1, true));
		locales.addLocale(new LocaleMeta("fr_FR", "French (France)", 2, true));
		locales.addLocale(new LocaleMeta("it_IT", "Italian (Italy)", 3, true));
		locales.addLocale(new LocaleMeta("es_ES", "Spanish (Spain)", 4, true));
		locales.addLocale(new LocaleMeta("de_DE", "German (Germany)", 5, true));
		schemaMeta = new SchemaMeta();
		schemaMeta.setLocales(locales);
		conceptModel.addConceptModificationListener(new IConceptModificationListener()
		{
			public void conceptModified(final ConceptModificationEvent e)
			{
				BusinessTableDialogTestApp.this.conceptModified(e);
			}
		});

		context.put("locales", schemaMeta.getLocales());

		tab = new BusinessTable("PT_TEST_TABLE_1");

		BusinessColumn pc1 = new BusinessColumn("COL_1");
		Concept c1 = new Concept();
		LocalizedStringSettings s10 = new LocalizedStringSettings();
		s10.setLocaleString("en_US", "beans");
		s10.setLocaleString("es_ES", "frijoles");
		c1.addProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.NAME.getId(), s10));
		pc1.setConcept(c1);

		tab.addBusinessColumn(pc1);
		// tab.addBusinessColumn(new BusinessColumn("COL_2"));
		// tab.addBusinessColumn(new BusinessColumn("COL_3"));

		BusinessTable tab = new BusinessTable("my_Business_table");
		BusinessColumn col1 = new BusinessColumn("my_col1");
		BusinessColumn col2 = new BusinessColumn("my_col2");

		tab.addBusinessColumn(col1);
		tab.addBusinessColumn(col2);

		tableModel = new BusinessTableModel(tab, schemaMeta.getActiveModel());
		tableModel.setParent(new PhysicalTable("customers"));

	}

	protected void conceptModified(final ConceptModificationEvent e)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("heard concept modified event: " + e);
		}
	}

	public void run()
	{
		if (!PropsUI.isInitialized())
		{
			Const.checkPentahoMetadataDirectory();
			PropsUI.init(new Display(), Const.getPropertiesFile());
		}
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}

	protected Point getInitialSize()
	{
		return new Point(300, 300);
	}

	protected Control createContents(final Composite parent)
	{
		Composite c0 = new Composite(parent, SWT.NONE);
		c0.setLayout(new FormLayout());

		// let the table diag operate on copy of the original in case they
		// cancel
		ITableModel copy = new BusinessTableModel((BusinessTable) tab.clone(), schemaMeta.getActiveModel());

		if (logger.isDebugEnabled())
		{
			logger.debug("orig table model: " + tableModel);
		}

		final BusinessTableDialog diag = new BusinessTableDialog(this.getShell(), tab, schemaMeta);

		if (logger.isDebugEnabled())
		{
			logger.debug("mod'ed table model: " + copy);
		}

		// final TableColumnTreeWidget t = new TableColumnTreeWidget(c0,
		// SWT.NONE, tableModel, true);

		final Button b = new Button(c0, SWT.NONE);
		FormData fdButton = new FormData();
		fdButton.left = new FormAttachment(0, 10);
		fdButton.top = new FormAttachment(0, 10);
		b.setLayoutData(fdButton);

		// FormData fdTree = new FormData();
		// fdTree.left = new FormAttachment(0, 10);
		// fdTree.top = new FormAttachment(b, 10);
		// t.setLayoutData(fdTree);

		b.setText("Go");
		b.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
			}

			public void widgetSelected(SelectionEvent arg0)
			{
				int result = diag.open();
				if (Window.OK == result)
				{
					if (logger.isDebugEnabled())
					{
						logger.debug("user clicked ok; time to save");
					}
					saveTable();
				} else
				{
					if (logger.isDebugEnabled())
					{
						logger.debug("user clicked cancel");
					}
				}
			}

		});

		return c0;
	}

	private void saveTable()
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("saving table");
		}

	}

	public static void main(final String[] args)
	{
		new BusinessTableDialogTestApp().run();
	}

	protected void configureShell(final Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Business Table Dialog Test Application");
		shell.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-editor-app"));
	}

}