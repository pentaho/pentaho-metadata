/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
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
import org.pentaho.di.core.xml.XMLHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Stores constraints that are used to filter rows. Row level security is specified at the business model level.
 * 
 * @author mlowery
 * 
 * @deprecated as of metadata 3.0. please use org.pentaho.metadata.model.concept.security.RowLevelSecurity
 */
public class RowLevelSecurity implements Cloneable {

  private static final String ATTR_VALUE_NONE = "none";

  private static final String ATTR_VALUE_ROLE_BASED = "role-based";

  private static final String ATTR_VALUE_GLOBAL = "global";

  private static final String EQUALS = "=";

  private static final String SPACE = " ";

  private static final Log logger = LogFactory.getLog(RowLevelSecurity.class);

  public static enum Type {
    NONE, GLOBAL, ROLEBASED
  }

  private static final String EMPTY_STRING = "";

  private static final String CDATA_END = "]]>";

  private static final String CDATA_BEGIN = "<![CDATA[";

  private static final String EMPTY_ELEM_NAME_END = " />";

  private static final String FINISH_ELEM_NAME_BEGIN = "</";

  private static final String ELEM_NAME_END = ">";

  private static final String START_ELEM_NAME_BEGIN = "<";

  private static final String ELEM_FORMULA = "formula";

  private static final String ELEM_ROW_LEVEL_SECURITY = "row-level-security";

  private static final String ELEM_ENTRIES = "entries";

  private static final String ELEM_ENTRY = "entry";

  private static final String ELEM_OWNER = "owner";

  private static final String FUNC_AND = "AND";

  private static final String FUNC_OR = "OR";

  private static final String FUNC_IN = "IN";

  private static final String PARAM_LIST_BEGIN = "(";

  private static final String PARAM_LIST_END = ")";

  private static final String PARAM_SEPARATOR = ";";

  private static final String FUNC_ROLES = "ROLES()";

  private static final String FUNC_USER = "USER()";

  private static final String ATTR_TYPE = "type";

  private static final String ATTR_QUOTE = "\"";

  private static final String PARAM_QUOTE = "\"";

  private Type type;

  /**
   * See {@link #getType()}. Type will tell you which of three options are in effect: no RLS, global, or role-based.
   */
  private String globalConstraint = EMPTY_STRING;

  /**
   * See {@link #getType()}. Type will tell you which of three options are in effect: no RLS, global, or role-based.
   */
  private Map<SecurityOwner, String> roleBasedConstraintMap = new HashMap<SecurityOwner, String>();

  public RowLevelSecurity(String globalConstraint) {
    this(Type.GLOBAL, globalConstraint, null);
  }

  public RowLevelSecurity(Map<SecurityOwner, String> roleBasedConstraintMap) {
    this(Type.ROLEBASED, null, roleBasedConstraintMap);
  }

  public RowLevelSecurity() {
    this(Type.NONE, null, null);
  }

  public RowLevelSecurity(Type type, String globalConstraint, Map<SecurityOwner, String> roleBasedConstraintMap) {
    setGlobalConstraint(globalConstraint);
    setRoleBasedConstraintMap(roleBasedConstraintMap);
    this.type = type;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new RowLevelSecurity(type, globalConstraint,
        roleBasedConstraintMap != null ? cloneRoleBasedConstraintMap(roleBasedConstraintMap) : null);
  }

  protected Map<SecurityOwner, String> cloneRoleBasedConstraintMap(Map<SecurityOwner, String> map) {
    Map<SecurityOwner, String> copy = new HashMap<SecurityOwner, String>();
    for (Map.Entry<SecurityOwner, String> entry : map.entrySet()) {
      SecurityOwner clonedOwner = (SecurityOwner) entry.getKey().clone();
      copy.put(clonedOwner, entry.getValue());
    }
    return copy;
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
    return new EqualsBuilder().append(type, rhs.type).append(globalConstraint, rhs.globalConstraint).append(
        roleBasedConstraintMap, rhs.roleBasedConstraintMap).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(23, 71).append(type).append(globalConstraint).append(roleBasedConstraintMap)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("type", type).append("globalConstraint", globalConstraint).append(
        "roleBasedConstraintMap", roleBasedConstraintMap).toString();
  }

  public String toXML() {
    StringBuilder xml = new StringBuilder();

    // print rls begin element
    xml.append(START_ELEM_NAME_BEGIN).append(ELEM_ROW_LEVEL_SECURITY).append(SPACE).append(ATTR_TYPE).append(EQUALS);
    if (isGlobal()) {
      xml.append(ATTR_QUOTE).append(ATTR_VALUE_GLOBAL).append(ATTR_QUOTE);
    } else if (isRoleBased()) {
      xml.append(ATTR_QUOTE).append(ATTR_VALUE_ROLE_BASED).append(ATTR_QUOTE);
    } else {
      xml.append(ATTR_QUOTE).append(ATTR_VALUE_NONE).append(ATTR_QUOTE);
    }
    xml.append(ELEM_NAME_END);

    // print global
    xml.append(START_ELEM_NAME_BEGIN).append(ELEM_FORMULA).append(ELEM_NAME_END);
    xml.append(CDATA_BEGIN).append(globalConstraint).append(CDATA_END);
    xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_FORMULA).append(ELEM_NAME_END);
    // print role-based
    xml.append(START_ELEM_NAME_BEGIN).append(ELEM_ENTRIES).append(ELEM_NAME_END);
    for (Map.Entry<SecurityOwner, String> entry : roleBasedConstraintMap.entrySet()) {
      xml.append(START_ELEM_NAME_BEGIN).append(ELEM_ENTRY).append(ELEM_NAME_END);
      SecurityOwner owner = entry.getKey();
      String formula = entry.getValue();
      xml.append(owner.toXML());
      xml.append(START_ELEM_NAME_BEGIN).append(ELEM_FORMULA).append(ELEM_NAME_END);
      xml.append(CDATA_BEGIN).append(formula).append(CDATA_END);
      xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_FORMULA).append(ELEM_NAME_END);
      xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_ENTRY).append(ELEM_NAME_END);
    }
    xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_ENTRIES).append(ELEM_NAME_END);

    // print rls end element
    xml.append(FINISH_ELEM_NAME_BEGIN).append(ELEM_ROW_LEVEL_SECURITY).append(ELEM_NAME_END);

    return xml.toString();
  }

  public RowLevelSecurity(Node rlsNode) throws Exception {
    String typeString = rlsNode.getAttributes().getNamedItem(ATTR_TYPE).getTextContent();
    if (ATTR_VALUE_GLOBAL.equals(typeString)) {
      type = Type.GLOBAL;
    } else if (ATTR_VALUE_ROLE_BASED.equals(typeString)) {
      type = Type.ROLEBASED;
    } else {
      type = Type.NONE;
    }
    // global
    Node globalFormulaNode = XMLHandler.getSubNode(rlsNode, ELEM_FORMULA);
    globalConstraint = globalFormulaNode.getTextContent();
    // role-based
    Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
    Node entriesNode = XMLHandler.getSubNode(rlsNode, ELEM_ENTRIES);
    int entryCount = XMLHandler.countNodes(entriesNode, ELEM_ENTRY);
    for (int i = 0; i < entryCount; i++) {
      Node entryNode = XMLHandler.getSubNodeByNr(entriesNode, ELEM_ENTRY, i);
      Node ownerNode = XMLHandler.getSubNode(entryNode, ELEM_OWNER);
      // build owner using its node constructor
      SecurityOwner owner = new SecurityOwner(ownerNode);
      Node formulaNode = XMLHandler.getSubNode(entryNode, ELEM_FORMULA);
      String formula = formulaNode.getTextContent();
      map.put(owner, formula);
    }
    roleBasedConstraintMap = map;

  }

  public static RowLevelSecurity fromXML(String value) throws Exception {
    Document doc = XMLHandler.loadXMLString(value);
    return new RowLevelSecurity(XMLHandler.getSubNode(doc, ELEM_ROW_LEVEL_SECURITY)); //$NON-NLS-1$
  }

  public boolean isGlobal() {
    return type == Type.GLOBAL;
  }

  public boolean isRoleBased() {
    return type == Type.ROLEBASED;
  }

  /**
   * this method returns an OR'ed list of all the constraints that pertain
   * to a specific user/role list
   * 
   * @param user current user
   * @param roles current roles
   * @return OR'ed list of constraints that apply to this user.
   * 
   */
  protected String generateRuleBasedConstraint(String user, List<String> roles) {
    
    List<String> pieces = new ArrayList<String>();
    for (Map.Entry<SecurityOwner, String> entry : roleBasedConstraintMap.entrySet()) {
      SecurityOwner owner = entry.getKey();
      String formula = entry.getValue();
      
      // if the user or a user role matches this constraint
      // add it to the pieces list
      
      if ((owner.getOwnerType() == SecurityOwner.OWNER_TYPE_USER && 
          owner.getOwnerName().equals(user)) ||
          (roles != null && 
          owner.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE &&
          roles.contains(owner.getOwnerName()))) {
        pieces.add(formula);
      }
    }
    
    if (pieces.size() == 0) {
      return "FALSE()";
    } else if (pieces.size() == 1) {
      return pieces.get(0);
    } else {
      
      // generate an OR(PIECE0;PIECE1;...) list
      
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
      return buf.toString();
    }
    
  }

  /**
   * WG: Note, this method is not used at the moment, we may in the future use
   *     this if MQL is partially executed before passing down the SQL to 
   *     database engines.
   */
  protected String getIntermediateFormula() {
    if (isGlobal()) {
      return globalConstraint;
    } else if (isRoleBased()) {
      return roleMapToFormula();
    } else {
      // rls is disabled
      return EMPTY_STRING;
    }
  }
  
  /**
   * WG: Note, this method is not used at the moment, we may in the future use
   *     this if MQL is partially executed before passing down the SQL to 
   *     database engines.
   */
  protected String roleMapToFormula() {
    List<String> pieces = new ArrayList<String>();
    for (Map.Entry<SecurityOwner, String> entry : roleBasedConstraintMap.entrySet()) {
      SecurityOwner owner = entry.getKey();
      String formula = entry.getValue();

      StringBuilder formulaBuf = new StringBuilder();
      formulaBuf.append(FUNC_AND).append(PARAM_LIST_BEGIN).append(FUNC_IN).append(PARAM_LIST_BEGIN).append(PARAM_QUOTE)
          .append(owner.getOwnerName()).append(PARAM_QUOTE).append(PARAM_SEPARATOR).append(
              owner.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE ? FUNC_ROLES : FUNC_USER).append(PARAM_LIST_END)
          .append(PARAM_SEPARATOR).append(formula).append(PARAM_LIST_END);
      pieces.add(formulaBuf.toString());

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
  
  public String getMQLFormula(String user, List<String> roles) {
    if (isGlobal()) {
      return expandFunctions(globalConstraint, user, roles);
    } else if (isRoleBased()) {
      return generateRuleBasedConstraint(user, roles);
    } else {
      // rls is disabled
      return EMPTY_STRING;
    }
  }

  protected String expandFunctions(String formula, String user, List<String> roles) {
    // "expand" USER() function (regex: escape parentheses and escape backslash that escapes parentheses
    formula = formula.replaceAll("USER\\(\\)", String.format("\"%s\"", user));

    // "expand" ROLES() function (regex: escape parentheses and escape backslash that escapes parentheses
    StringBuilder buf = new StringBuilder();
    int i = 0;
    for (String role : roles) {
      if (i > 0) {
        buf.append(";");
      }
      buf.append(String.format("\"%s\"", role));
      i++;
    }
    return formula.replaceAll("ROLES\\(\\)", buf.toString());
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getGlobalConstraint() {
    return globalConstraint;
  }

  public void setGlobalConstraint(String globalConstraint) {
    if (globalConstraint != null) {
      this.globalConstraint = globalConstraint;
    } else {
      this.globalConstraint = EMPTY_STRING;
    }
  }

  public Map<SecurityOwner, String> getRoleBasedConstraintMap() {
    return roleBasedConstraintMap;
  }

  public void setRoleBasedConstraintMap(Map<SecurityOwner, String> roleBasedConstraintMap) {
    if (roleBasedConstraintMap != null) {
      this.roleBasedConstraintMap = roleBasedConstraintMap;
    } else {
      this.roleBasedConstraintMap = new HashMap<SecurityOwner, String>();
    }
  }
  
  public static void main(String[] args) {
    String formula = "OR(AND(IN(\"suzy\";USER());[BT_CUSTOMER_W_TER_CUSTOMER_W_TER.BC_CUSTOMER_W_TER_COUNTRY] = \"USA\"))";
    // "expand" USER() function (regex: escape parentheses and escape backslash that escapes parentheses
    formula = formula.replaceAll("USER\\(\\)", String.format("\"%s\"", "whatever"));

    System.out.println(formula);
  }

}
