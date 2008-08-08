package org.pentaho.pms.schema.security;

import java.util.List;
import java.util.Map;

public interface RowLevelSecurityConstraints {

  /**
   * Returns a map with SecurityOwners as keys and the row constraints for that owner in a list as values. Formulas 
   * stored under the key <code>null</code> are assumed to have security owner information in the formula itself.
   * <p>Example:</p>
   * <p>RoleA -&gt; {column1 = 'East', column2 = 'Sales'}</p>
   * @return a map of owners to one or more formulas (in a list)
   */
  Map<SecurityOwner, List<String>> getConstraintsMap();

  /**
   * Returns a single formula representing these constraints. This string encapsulates all data in this object--owners 
   * and per-owner constraints.
   * @return a formula
   */
  String getSingleFormula();
  
  /**
   * Same as {@link #getSingleFormula()} but with all security-related functions "pre-processed". This string should be
   * suitable for adding to an MQL where clause.
   * @return the MQL-ready formula
   */
  String getMQLFormula(String user, List<String> roles);
  
  /**
   * All row level security constraints must be cloneable.
   * @return a cloned object
   * @throws CloneNotSupportedException should never happen
   */
  Object clone() throws CloneNotSupportedException;
  
}
