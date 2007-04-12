package org.pentaho.pms.schema.concept.editor;

import java.util.EventObject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An event fired from the concept model when modifications are made.
 * @author mlowery
 */
public abstract class ConceptModificationEvent extends EventObject {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptModificationEvent.class);

  private static final long serialVersionUID = 3595437838419848420L;

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public ConceptModificationEvent(final Object source) {
    super(source);
  }

  // ~ Methods =========================================================================================================

  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
