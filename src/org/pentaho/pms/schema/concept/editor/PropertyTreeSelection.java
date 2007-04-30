package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.eclipse.jface.viewers.ISelection;

public class PropertyTreeSelection implements ISelection {

  // ~ Static fields/initializers ======================================================================================

  // ~ Instance fields =================================================================================================

  private String name;

  private boolean group;

  // ~ Constructors ====================================================================================================

  public PropertyTreeSelection(final String name, final boolean group) {
    super();
    this.name = name;
    this.group = group;
  }

  // ~ Methods =========================================================================================================

  public boolean isEmpty() {
    return false;
  }

  public String getName() {
    return name;
  }

  public boolean isGroup() {
    return group;
  }

  public String toString() {
    return new ReflectionToStringBuilder(this).toString();
  }

}
