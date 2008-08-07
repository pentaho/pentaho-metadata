package org.pentaho.pms.schema.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Stores its data in a map and converts to a single formula on-the-fly.
 * 
 * @author mlowery
 */
public class MapBasedSecurityConstraints implements RowLevelSecurityConstraints, Cloneable {

  private static final Log logger = LogFactory.getLog(MapBasedSecurityConstraints.class);

  private Map<SecurityOwner, List<String>> map;

  private static final String FUNC_AND = "AND";

  private static final String FUNC_OR = "OR";

  private static final String FUNC_IN = "IN";

  private static final String PARAM_LIST_BEGIN = "(";

  private static final String PARAM_LIST_END = ")";

  private static final String PARAM_SEPARATOR = ";";

  private static final String FUNC_ROLES = "ROLES()";

  private static final String FUNC_USER = "USER()";

  public MapBasedSecurityConstraints(Map<SecurityOwner, List<String>> map) {
    super();
    this.map = map;
  }

  public Map<SecurityOwner, List<String>> getConstraintsMap() {
    return map;
  }

  public String getSingleFormula() {
    List<String> pieces = new ArrayList<String>();
    for (Map.Entry<SecurityOwner, List<String>> entry : map.entrySet()) {
      SecurityOwner owner = entry.getKey();
      List<String> formulas = entry.getValue();

      if (owner != null) {
        for (String formula : formulas) {
          StringBuilder formulaBuf = new StringBuilder();
          formulaBuf.append(FUNC_AND).append(PARAM_LIST_BEGIN).append(FUNC_IN).append(PARAM_LIST_BEGIN).append(
              owner.getOwnerName()).append(PARAM_SEPARATOR).append(
              owner.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE ? FUNC_ROLES : FUNC_USER).append(PARAM_LIST_END)
              .append(PARAM_SEPARATOR).append(formula).append(PARAM_LIST_END);
          pieces.add(formulaBuf.toString());
        }

      } else {
        // this is a global formula (owner info is in the formula)
        pieces.addAll(formulas);
      }
    }

    StringBuilder buf = new StringBuilder();
    buf.append(FUNC_OR);
    buf.append(PARAM_LIST_BEGIN);
    int index = 0;
    for (String piece : pieces) {
      if (index > 0) {
        buf.append(PARAM_SEPARATOR);
      }
      buf.append(piece);
      index++;
    }
    buf.append(PARAM_LIST_END);

    logger.debug("singleFormula: " + buf);

    return buf.toString();
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    // make a deep copy
    Map<SecurityOwner, List<String>> newMap = new HashMap<SecurityOwner, List<String>>();

    for (Map.Entry<SecurityOwner, List<String>> entry : map.entrySet()) {
      SecurityOwner clonedOwner = (SecurityOwner) entry.getKey().clone();
      List<String> clonedFormulas = new ArrayList<String>((List<String>) entry.getValue());
      newMap.put(clonedOwner, clonedFormulas);
    }
    return new MapBasedSecurityConstraints(newMap);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof MapBasedSecurityConstraints == false) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    MapBasedSecurityConstraints rhs = (MapBasedSecurityConstraints) obj;
    return new EqualsBuilder().append(map, rhs.map).isEquals();

  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(5, 101).append(map).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("map", map).toString();
  }

}