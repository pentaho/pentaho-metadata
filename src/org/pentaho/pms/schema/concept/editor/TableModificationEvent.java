package org.pentaho.pms.schema.concept.editor;

import java.util.EventObject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An event fired when columns are added or removed from a table. While UI elements should be concerned with name
 * changes on the columns, they can "hear" those events by subscribing to the columns' concept models.
 * @author mlowery
 */
public class TableModificationEvent extends EventObject {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(TableModificationEvent.class);

  private static final long serialVersionUID = -7930085568962265187L;

  public static final int ADD_COLUMN = 1;

  public static final int REMOVE_COLUMN = -1;

  // ~ Instance fields =================================================================================================

  private String id;

  private int type;

  // ~ Constructors ====================================================================================================

  public TableModificationEvent(final Object source, final String id, final int type) {
    super(source);
    this.id = id;
    this.type = type;
  }

  // ~ Methods =========================================================================================================

  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
