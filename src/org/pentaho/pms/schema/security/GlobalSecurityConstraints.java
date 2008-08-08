package org.pentaho.pms.schema.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Specifies all row level constraints for the entire model in a single formula.
 * 
 * @author mlowery
 */
public class GlobalSecurityConstraints extends AbstractRowLevelSecurityConstraints implements Cloneable {

  private String formula;

  /**
   * Constructs a new instance from the given formula.
   * @param formula the formula to wrap 
   */
  public GlobalSecurityConstraints(String formula) {
    super();
    this.formula = formula;
  }

  /**
   * Since the security owners are in the formula itself, there are no security owners to map. This method always 
   * returns a map with a single entry where the key is <code>null</code> and the value is a list with one element--the 
   * global constraints formula.
   */
  public Map<SecurityOwner, List<String>> getConstraintsMap() {
    Map<SecurityOwner, List<String>> map = new HashMap<SecurityOwner, List<String>>();
    List<String> list = new ArrayList<String>();
    list.add(formula);
    map.put(null, list);
    return map;
  }

  public String getSingleFormula() {
    return formula;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new GlobalSecurityConstraints(formula);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GlobalSecurityConstraints == false) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    GlobalSecurityConstraints rhs = (GlobalSecurityConstraints) obj;
    return new EqualsBuilder().append(formula, rhs.formula).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(11, 53).append(formula).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("formula", formula).toString();
  }

}
