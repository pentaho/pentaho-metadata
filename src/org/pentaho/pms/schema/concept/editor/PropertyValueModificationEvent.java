package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyValueModificationEvent extends PropertyModificationEvent {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyValueModificationEvent.class);

  private static final long serialVersionUID = 3440212934863199561L;

  // ~ Instance fields =================================================================================================

  private Object oldValue;

  private Object newValue;

  // ~ Constructors ====================================================================================================

  public PropertyValueModificationEvent(final Object source, final String propertyId, final Object oldValue,
      final Object newValue) {
    super(source, propertyId);
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  // ~ Methods =========================================================================================================

  public Object getOldValue() {
    return oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }

}
