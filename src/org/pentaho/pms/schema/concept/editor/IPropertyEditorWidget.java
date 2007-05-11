package org.pentaho.pms.schema.concept.editor;

/**
 * A graphical control that edits a property of a concept.
 * @author mlowery
 */
public interface IPropertyEditorWidget {
  /**
   * Returns a value suitable for passing to <code>ConceptPropertyInterface.setValue()</code>.
   */
  Object getValue();

  /**
   * Property editor widgets might simply wrap a single SWT widget or may wrap multiple SWT widgets.  This method should
   * set the focus on one of the wrapped widgets.
   */
//  void focus();
}
