package org.pentaho.pms.schema.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.pentaho.di.core.xml.XMLHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Stores constraints that are used to filter rows. Row level security is specified at the business model level.
 * 
 * @author mlowery
 */
public class RowLevelSecurity implements Cloneable {

  private static final String CDATA_END = "]]>";

  private static final String CDATA_BEGIN = "<![CDATA[";

  private static final String EMPTY_ELEM_NAME_END = " />";

  private static final String FINISH_ELEM_NAME_BEGIN = "</";

  private static final String ELEM_NAME_END = ">";

  private static final String START_ELEM_NAME_BEGIN = "<";

  private RowLevelSecurityConstraints constraints;

  private static final String ELEM_NULL = "null";

  private static final String ELEM_FORMULAS = "formulas";

  private static final String ELEM_FORMULA = "formula";

  private static final String ELEM_ROW_LEVEL_SECURITY = "row-level-security";

  private static final String ELEM_RLS_CONSTRAINTS = "constraints";

  private static final String ELEM_RLS_CONSTRAINT = "constraint";

  private static final String ELEM_OWNER = "owner";

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

  public String toXml() {
    StringBuilder xml = new StringBuilder();

    xml.append(START_ELEM_NAME_BEGIN).append(ELEM_ROW_LEVEL_SECURITY).append(ELEM_NAME_END);
    xml.append(START_ELEM_NAME_BEGIN).append(ELEM_RLS_CONSTRAINTS).append(ELEM_NAME_END);

    for (Map.Entry<SecurityOwner, List<String>> entry : constraints.getConstraintsMap().entrySet()) {
      xml.append(START_ELEM_NAME_BEGIN).append(ELEM_RLS_CONSTRAINT).append(ELEM_NAME_END);
      if (null == entry.getKey()) {
        xml.append(START_ELEM_NAME_BEGIN).append(ELEM_OWNER).append(ELEM_NAME_END).append(START_ELEM_NAME_BEGIN)
            .append(ELEM_NULL).append(EMPTY_ELEM_NAME_END).append(FINISH_ELEM_NAME_BEGIN).append(ELEM_OWNER).append(
                ELEM_NAME_END);
      } else {
        xml.append(entry.getKey().toXML());
      }
      xml.append(START_ELEM_NAME_BEGIN).append(ELEM_FORMULAS).append(ELEM_NAME_END);
      for (String formula : entry.getValue()) {
        xml.append(START_ELEM_NAME_BEGIN).append(ELEM_FORMULA).append(ELEM_NAME_END);
        xml.append(CDATA_BEGIN).append(formula).append(CDATA_END);
        xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_FORMULA).append(ELEM_NAME_END);
      }
      xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_FORMULAS).append(ELEM_NAME_END);
      xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_RLS_CONSTRAINT).append(ELEM_NAME_END);
    }
    xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_RLS_CONSTRAINTS).append(ELEM_NAME_END);
    xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_ROW_LEVEL_SECURITY).append(ELEM_NAME_END);

    return xml.toString();
  }

  public RowLevelSecurity(Node rlsNode) throws Exception {
    Map<SecurityOwner, List<String>> map = new HashMap<SecurityOwner, List<String>>();
    Node constraintsNode = XMLHandler.getSubNode(rlsNode, ELEM_RLS_CONSTRAINTS);
    try {
      int constraintCount = XMLHandler.countNodes(constraintsNode, ELEM_RLS_CONSTRAINT);
      for (int i = 0; i < constraintCount; i++) {
        Node constraintNode = XMLHandler.getSubNodeByNr(constraintsNode, ELEM_RLS_CONSTRAINT, i);
        Node ownerNode = XMLHandler.getSubNode(constraintNode, ELEM_OWNER);
        SecurityOwner owner = null;
        final String XML_ELEM_NULL = START_ELEM_NAME_BEGIN + ELEM_NULL + ELEM_NAME_END;
        if (null == XMLHandler.getSubNode(ownerNode, ELEM_NULL)) {
          // we're here if a <null /> element is not present
          owner = new SecurityOwner(ownerNode);
        }
        List<String> formulas = new ArrayList<String>();
        Node formulasNode = XMLHandler.getSubNode(constraintNode, ELEM_FORMULAS);
        int formulaCount = XMLHandler.countNodes(formulasNode, ELEM_FORMULA);
        for (int j = 0; j < formulaCount; j++) {
          Node formulaNode = XMLHandler.getSubNodeByNr(formulasNode, ELEM_FORMULA, j);
          String formula = formulaNode.getTextContent();
          formulas.add(formula);
        }
        map.put(owner, formulas);
      }
      constraints = createConstraintsFromMap(map);
    } catch (Exception e) {
      throw new Exception("unable to create row level security object", e);
    }
  }

  /**
   * Override to customize instantiation logic.
   * 
   * <p>If one created a custom RowLevelSecurityConstraints class, it would need to be instantiated here during the
   * read from XML.</p>
   */
  protected RowLevelSecurityConstraints createConstraintsFromMap(Map<SecurityOwner, List<String>> map) {
    if (map.keySet().size() == 1 && map.get(null) != null) {
      return new GlobalSecurityConstraints(map.get(null).get(0));
    } else {
      return new RoleBasedSecurityConstraints(map); 
    }
  }
  
  public static RowLevelSecurity fromXML(String value) throws Exception {
    Document doc = XMLHandler.loadXMLString(value);
    return new RowLevelSecurity(XMLHandler.getSubNode(doc, ELEM_ROW_LEVEL_SECURITY)); //$NON-NLS-1$
  }

}
