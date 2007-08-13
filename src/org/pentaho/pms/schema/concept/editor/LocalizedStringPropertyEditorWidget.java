package org.pentaho.pms.schema.concept.editor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.locale.Locales;

public class LocalizedStringPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(LocalizedStringPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private LocalizedStringTableWidget table;

  // ~ Constructors ====================================================================================================

  public LocalizedStringPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    Locales locales = (Locales) getContext().get("locales");
    if (null == locales) {
      if (logger.isWarnEnabled()) {
        logger.warn("missing required context attribute 'locales'; continuing anyway");
      }
      locales = new Locales();
    }
    table = new LocalizedStringTableWidget(parent, SWT.NONE, getConceptModel(), getPropertyId(), locales);


    FormData fdTable = new FormData();
    fdTable.left = new FormAttachment(0, 0);
    fdTable.top = new FormAttachment(0, 0);
    fdTable.right = new FormAttachment(100, 0);
    table.setLayoutData(fdTable);
    table.setEnabled(isEditable());
  }

  /**
   * Not used since the table widget persists values to model.
   */
  public Object getValue() {
    throw new UnsupportedOperationException();
  }

  protected boolean isValid() {
    return true;
  }

  public void cleanup() {
  }

  public void refresh() {
    refreshOverrideButton();
    table.setEnabled(isEditable());
    table.refresh();
  }

}
