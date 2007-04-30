package org.pentaho.pms.schema.concept.editor;

import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.schema.SchemaMeta;

public abstract class AbstractSchemaMetaAwarePropertyEditorWidget extends AbstractPropertyEditorWidget implements
    ISchemaMetaAwarePropertyEditorWidget {

  private SchemaMeta schemaMeta;

  public AbstractSchemaMetaAwarePropertyEditorWidget(final Composite parent, final int style,
      final IConceptModel conceptModel, final String propertyId) {
    super(parent, style, conceptModel, propertyId);
  }

  protected SchemaMeta getSchemaMeta() {
    return schemaMeta;
  }

  public void setSchemaMeta(final SchemaMeta schemaMeta) {
    this.schemaMeta = schemaMeta;
  }

}
