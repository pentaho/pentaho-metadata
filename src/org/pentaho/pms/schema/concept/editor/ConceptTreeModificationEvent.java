package org.pentaho.pms.schema.concept.editor;

import java.util.EventObject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConceptTreeModificationEvent extends EventObject {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptModificationEvent.class);

  private static final long serialVersionUID = -5854497170418174812L;

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public ConceptTreeModificationEvent(final Object source) {
    super(source);
  }

  // ~ Methods =========================================================================================================

  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
