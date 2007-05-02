package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.schema.SchemaMeta;

public abstract class AbstractSchemaMetaAwarePropertyEditorWidget extends AbstractPropertyEditorWidget implements
    ISchemaMetaAwarePropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(AbstractSchemaMetaAwarePropertyEditorWidget.class);

  // ~ Instance fields ===================================================================================================

  private SchemaMeta schemaMeta;

  // ~ Constructors ======================================================================================================

  public AbstractSchemaMetaAwarePropertyEditorWidget(final Composite parent, final int style,
      final IConceptModel conceptModel, final String propertyId) {
    super(parent, style, conceptModel, propertyId);
  }

  // ~ Methods ===========================================================================================================

  protected SchemaMeta getSchemaMeta() {
    return schemaMeta;
  }

  public void setSchemaMeta(final SchemaMeta schemaMeta) {
    this.schemaMeta = schemaMeta;
  }

}
