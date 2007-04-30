package org.pentaho.pms.schema.concept.editor;

import org.pentaho.pms.schema.SchemaMeta;

public interface ISchemaMetaAwarePropertyEditorWidget extends IPropertyEditorWidget {
  void setSchemaMeta(final SchemaMeta schemaMeta);
}
