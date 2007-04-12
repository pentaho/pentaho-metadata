package org.pentaho.pms.schema.concept.editor;

import java.lang.reflect.Field;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;

public class PropertyModificationEvent extends ConceptModificationEvent {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyModificationEvent.class);

  private static final long serialVersionUID = -5810693858905811872L;

  public static final int ADD_PROPERTY = 0;

  public static final int CHANGE_PROPERTY = 1;

  public static final int REMOVE_PROPERTY = 2;

  // ~ Instance fields =================================================================================================

  private String propertyId;

  private int type;

  private ConceptPropertyInterface oldValue;

  private ConceptPropertyInterface newValue;

  // ~ Constructors ====================================================================================================

  public PropertyModificationEvent(final Object source, final int type, final String propertyId,
      final ConceptPropertyInterface oldValue, final ConceptPropertyInterface newValue) {
    super(source);
    this.type = type;
    this.propertyId = propertyId;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  // ~ Methods =========================================================================================================

  public String getPropertyId() {
    return propertyId;
  }

  /**
   * Returns one of the <code>static final</code> members of this class.
   * @return
   */
  public int getType() {
    return type;
  }

  public ConceptPropertyInterface getOldValue() {
    return oldValue;
  }

  public ConceptPropertyInterface getNewValue() {
    return newValue;
  }

  public String toString() {
    return (new ReflectionToStringBuilder(this) {
      protected Object getValue(Field f) {
        if (f.getName().equals("type")) {
          switch (type) {
            case ADD_PROPERTY: {
              return "PropertyModificationEvent.ADD_PROPERTY";
            }
            case CHANGE_PROPERTY: {
              return "PropertyModificationEvent.CHANGE_PROPERTY";
            }
            case REMOVE_PROPERTY: {
              return "PropertyModificationEvent.REMOVE_PROPERTY";
            }
            default:
              return "<unknown>";
          }
        } else {
          try {
            return super.getValue(f);
          } catch (IllegalArgumentException e) {
            return "<exception occurred>";
          } catch (IllegalAccessException e) {
            return "<exception occurred>";
          }
        }

      }
    }).toString();
  }

}
