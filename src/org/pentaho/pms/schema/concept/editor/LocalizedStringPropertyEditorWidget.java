package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.widget.TableView;

public class LocalizedStringPropertyEditorWidget extends AbstractSchemaMetaAwarePropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(LocalizedStringPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private TableView table;

  // ~ Constructors ====================================================================================================

  public LocalizedStringPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId) {
    super(parent, style, conceptModel, propertyId);
    if (logger.isDebugEnabled()) {
      logger.debug("created LocalizedStringPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    Props props = Props.getInstance();

    ColumnInfo[] colinf = new ColumnInfo[] {
        new ColumnInfo(Messages.getString("ConceptPropertyLocalizedStringWidget.USER_LOCALE_DESC"),
            ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
        new ColumnInfo(Messages.getString("ConceptPropertyLocalizedStringWidget.USER_STRING_DESC"),
            ColumnInfo.COLUMN_TYPE_TEXT, false, false), //$NON-NLS-1$
    };

    table = new TableView(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, 0, true, null,
        props);

//    createTitleLabel();
    FormData fdFields = new FormData();
    fdFields.left = new FormAttachment(0, 0);
    fdFields.top = new FormAttachment(0, 0);
    fdFields.right = new FormAttachment(100, 0);
    table.setLayoutData(fdFields);
    //table.table.addFocusListener(this);
//    table.addModifyListener(new PropertyEditorWidgetModifyListener());
    table.addKeyListener(new KeyListener() {

      public void keyPressed(KeyEvent arg0) {
        if (logger.isDebugEnabled()) {
          logger.debug("keypressed");
        }

        for (int i = 0; i < table.nrNonEmpty(); i++)
        {
            TableItem item = table.getNonEmpty(i);
            String locale = item.getText(1);
            String string = item.getText(2);

            if (!Const.isEmpty(locale) && !Const.isEmpty(string))
            {
                if (logger.isDebugEnabled()) {
                  logger.debug(locale + ", " + string);
                }
            }
        }

      }

      public void keyReleased(KeyEvent arg0) {
        if (logger.isDebugEnabled()) {
          logger.debug("keyreleased");
        }

      }

    });



  }

  public Object getValue() {
    LocalizedStringSettings settings = new LocalizedStringSettings();
    for (int i = 0; i < table.nrNonEmpty(); i++)
    {
        TableItem item = table.getNonEmpty(i);
        String locale = item.getText(1);
        String string = item.getText(2);

        if (!Const.isEmpty(locale) && !Const.isEmpty(string))
        {
            settings.setLocaleString(locale, string);
        }
    }
    return settings;
  }

  protected void setValue(Object value) {
    LocalizedStringSettings settings = (LocalizedStringSettings) value;
    String[] locs = getSchemaMeta().getLocales().getLocaleCodes();

    table.removeAll();

    for (int i=0;i<locs.length;i++)
    {
        TableItem item = new TableItem(table.table, SWT.NONE);
        String string = null;
        if (settings!=null) string = settings.getString(locs[i]);

        item.setText(1, locs[i]);
        if (string!=null) item.setText(2, string);
    }
    table.removeEmptyRows();
    table.setRowNums();
    table.optWidth(true);
  }

  public void setSchemaMeta(SchemaMeta schemaMeta) {
    super.setSchemaMeta(schemaMeta);
    // schemaMeta must be set before we can call setValue since it uses the schemaMeta object
    setValue(getProperty().getValue());
  }

}
