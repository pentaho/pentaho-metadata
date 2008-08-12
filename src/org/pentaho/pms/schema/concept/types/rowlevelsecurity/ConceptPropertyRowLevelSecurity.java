package org.pentaho.pms.schema.concept.types.rowlevelsecurity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.security.RowLevelSecurity;

public class ConceptPropertyRowLevelSecurity extends ConceptPropertyBase implements Cloneable {

  private RowLevelSecurity value;

  public ConceptPropertyRowLevelSecurity(String id, RowLevelSecurity value) {
    this(id, value, false);
  }

  public ConceptPropertyRowLevelSecurity(String id, RowLevelSecurity value, boolean required) {
    super(id, required);
    if (null != value) {
      this.value = value;
    } else {
      this.value = new RowLevelSecurity();
    }
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.ROW_LEVEL_SECURITY;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = (RowLevelSecurity) value;
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyRowLevelSecurity clone = (ConceptPropertyRowLevelSecurity) super.clone();
    clone.setValue(value.clone());
    return clone;
  }

  /**
   * TODO mlowery Why is super.equals not called? super.equals doesn't even compare IDs! 
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ConceptPropertyRowLevelSecurity == false) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    ConceptPropertyRowLevelSecurity rhs = (ConceptPropertyRowLevelSecurity) obj;
    return new EqualsBuilder().append(value, rhs.value).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 157).append(value).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("value", value).toString();
  }

}
