package org.pentaho.pms.schema.concept.editor;

import org.pentaho.pms.schema.SchemaMeta;

/**
 * A specialized <code>IPropertyEditorWidget</code> that is aware of <code>SchemaMeta</code>. When instances of this
 * class are created, a <code>SchemaMeta</code> instance should be passed in to the
 * <code>ISchemaMetaAwarePropertyEditorWidget</code> instance.
 * @author mlowery
 */
public interface ISchemaMetaAwarePropertyEditorWidget extends IPropertyEditorWidget {
  void setSchemaMeta(final SchemaMeta schemaMeta);
}
