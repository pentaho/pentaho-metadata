package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.eclipse.jface.viewers.ISelection;

public class PropertyTreeSelection implements ISelection {

  // ~ Static fields/initializers ======================================================================================

  // ~ Instance fields =================================================================================================

  private String name;

  private boolean section;

  // ~ Constructors ====================================================================================================

  public PropertyTreeSelection(final String name, final boolean section) {
    super();
    this.name = name;
    this.section = section;
  }

  // ~ Methods =========================================================================================================

  public boolean isEmpty() {
    return false;
  }

  public String getName() {
    return name;
  }

  public boolean isSection() {
    return section;
  }

  public String toString() {
    return new ReflectionToStringBuilder(this).toString();
  }

}
