/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.metadata.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.metadata.model.concept.security.RowLevelSecurity;
import org.pentaho.metadata.model.concept.security.SecurityOwner;

/**
 * This helper class returns an open formula constraint relevant to the current user and role.
 * 
 * @author Mat Lowery
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class RowLevelSecurityHelper {

  private static final String EMPTY_STRING = ""; //$NON-NLS-1$
  private static final String FUNC_OR = "OR"; //$NON-NLS-1$
  private static final String PARAM_LIST_BEGIN = "("; //$NON-NLS-1$
  private static final String PARAM_LIST_END = ")"; //$NON-NLS-1$
  private static final String PARAM_SEPARATOR = ";"; //$NON-NLS-1$

  public String getOpenFormulaSecurityConstraint( RowLevelSecurity rls, String user, List<String> roles ) {
    switch ( rls.getType() ) {
      case GLOBAL:
        return expandFunctions( rls.getGlobalConstraint(), user, roles );
      case ROLEBASED:
        return generateRuleBasedConstraint( rls, user, roles );
      case NONE:
      default:
        return EMPTY_STRING;
    }
  }

  protected String expandFunctions( String formula, String user, List<String> roles ) {
    // "expand" USER() function (regex: escape parentheses and escape backslash that escapes parentheses
    formula = formula.replaceAll( "USER\\(\\)", String.format( "\"%s\"", user ) ); //$NON-NLS-1$  //$NON-NLS-2$

    // "expand" ROLES() function (regex: escape parentheses and escape backslash that escapes parentheses
    StringBuilder buf = new StringBuilder();
    int i = 0;
    for ( String role : roles ) {
      if ( i > 0 ) {
        buf.append( ";" ); //$NON-NLS-1$
      }
      buf.append( String.format( "\"%s\"", role ) ); //$NON-NLS-1$
      i++;
    }
    return formula.replaceAll( "ROLES\\(\\)", buf.toString() ); //$NON-NLS-1$
  }

  /**
   * this method returns an OR'ed list of all the constraints that pertain to a specific user/role list
   * 
   * @param user
   *          current user
   * @param roles
   *          current roles
   * @return OR'ed list of constraints that apply to this user.
   * 
   */
  protected String generateRuleBasedConstraint( RowLevelSecurity rls, String user, List<String> roles ) {

    List<String> pieces = new ArrayList<String>();
    for ( Map.Entry<SecurityOwner, String> entry : rls.getRoleBasedConstraintMap().entrySet() ) {
      SecurityOwner owner = entry.getKey();
      String formula = entry.getValue();

      // if the user or a user role matches this constraint
      // add it to the pieces list

      if ( ( owner.getOwnerType() == SecurityOwner.OwnerType.USER && owner.getOwnerName().equals( user ) )
          || ( roles != null && owner.getOwnerType() == SecurityOwner.OwnerType.ROLE && roles.contains( owner
              .getOwnerName() ) ) ) {
        pieces.add( formula );
      }
    }

    if ( pieces.size() == 0 ) {
      return "FALSE()"; //$NON-NLS-1$
    } else if ( pieces.size() == 1 ) {
      return pieces.get( 0 );
    } else {

      // generate an OR(PIECE0;PIECE1;...) list

      StringBuilder buf = new StringBuilder();
      buf.append( FUNC_OR );
      buf.append( PARAM_LIST_BEGIN );
      int index = 0;
      for ( String piece : pieces ) {
        if ( index > 0 ) {
          buf.append( PARAM_SEPARATOR );
        }
        buf.append( piece );
        index++;
      }
      buf.append( PARAM_LIST_END );
      return buf.toString();
    }

  }
}
