package org.pentaho.pms.schema.security;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Stores constraints that are used to filter rows. Row level security is specified at the business model level.
 * 
 * @author mlowery
 */
public class RowLevelSecurity implements Cloneable {

  private RowLevelSecurityConstraints constraints;

  public RowLevelSecurity(RowLevelSecurityConstraints constraints) {
    super();
    this.constraints = constraints;
  }

  public RowLevelSecurityConstraints getConstraints() {
    return constraints;
  }

  public void setConstraints(RowLevelSecurityConstraints constraints) {
    this.constraints = constraints;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new RowLevelSecurity((RowLevelSecurityConstraints) constraints.clone());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof RowLevelSecurity == false) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    RowLevelSecurity rhs = (RowLevelSecurity) obj;
    return new EqualsBuilder().append(constraints, rhs.constraints).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(23, 71).append(constraints).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("constraints", constraints).toString();
  }

}
