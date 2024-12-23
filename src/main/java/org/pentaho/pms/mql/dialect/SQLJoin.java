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

package org.pentaho.pms.mql.dialect;

import static org.pentaho.pms.mql.dialect.JoinType.INNER_JOIN;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLWhereFormula;
import org.pentaho.pms.util.Const;

/**
 * This class defines the join paths between the tables
 */
public class SQLJoin implements Comparable<SQLJoin> {
  private String leftTablename;
  private String leftTableAlias;
  private String rightTablename;
  private String rightTableAlias;
  private SQLWhereFormula sqlWhereFormula;
  private JoinType joinType;
  private String joinOrderKey;
  private boolean legacyJoinOrder = false;

  private static final Log logger = LogFactory.getLog( SQLJoin.class );

  /**
   * @param leftTablename   the name of the left join table
   * @param leftTableAlias  the alias of the left join table
   * @param rightTablename  the name of the right join table
   * @param rightTableAlias the alias of the right join table
   * @param sqlWhereFormula the SQL formula to do the join (the operator is ignored)
   * @param joinType        the join type (inner, left outer, right outer, full outer)
   * @param joinOrderKey    the join order key
   */
  public SQLJoin( String leftTablename, String leftTableAlias, String rightTablename, String rightTableAlias,
                  SQLWhereFormula sqlWhereFormula, JoinType joinType, String joinOrderKey ) {
    this.leftTablename = leftTablename;
    this.leftTableAlias = leftTableAlias;
    this.rightTablename = rightTablename;
    this.rightTableAlias = rightTableAlias;
    this.sqlWhereFormula = sqlWhereFormula;
    this.joinType = joinType;
    this.joinOrderKey = joinOrderKey;
  }

  public SQLJoin( String leftTablename, String leftTableAlias, String rightTablename, String rightTableAlias,
                  SQLWhereFormula sqlWhereFormula, JoinType joinType, String joinOrderKey, boolean legacyJoinOrder ) {
    this( leftTablename, leftTableAlias, rightTablename, rightTableAlias, sqlWhereFormula, joinType, joinOrderKey );
    this.legacyJoinOrder = legacyJoinOrder;
  }

  /**
   * @return the leftTablename
   */
  public String getLeftTablename() {
    return leftTablename;
  }

  /**
   * @param leftTablename the leftTablename to set
   */
  public void setLeftTablename( String leftTablename ) {
    this.leftTablename = leftTablename;
  }

  /**
   * @return the rightTablename
   */
  public String getRightTablename() {
    return rightTablename;
  }

  /**
   * @param rightTablename the rightTablename to set
   */
  public void setRightTablename( String rightTablename ) {
    this.rightTablename = rightTablename;
  }

  /**
   * @return the sqlWhereFormula
   */
  public SQLWhereFormula getSqlWhereFormula() {
    return sqlWhereFormula;
  }

  /**
   * @param sqlWhereFormula the sqlWhereFormula to set
   */
  public void setSqlWhereFormula( SQLWhereFormula sqlWhereFormula ) {
    this.sqlWhereFormula = sqlWhereFormula;
  }

  /**
   * @return the join type : INNER_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN, FULL_OUTER_JOIN
   */
  public JoinType getJoinType() {
    return joinType;
  }

  /**
   * @param joinType the join type : INNER_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN, FULL_OUTER_JOIN
   */
  public void setJoinType( JoinType joinType ) {
    this.joinType = joinType;
  }

  /**
   * @return the joinOrderKey
   */
  public String getJoinOrderKey() {
    return joinOrderKey;
  }

  /**
   * @param joinOrderKey the joinOrderKey to set
   */
  public void setJoinOrderKey( String joinOrderKey ) {
    this.joinOrderKey = joinOrderKey;
  }

  public int compareTo( SQLJoin other ) {
    // Case: No join order / no join order
    //
    if ( Const.isEmpty( getJoinOrderKey() ) && Const.isEmpty( other.getJoinOrderKey() ) ) {
      if ( legacyJoinOrder ) {
        return legacyCompare();
      }

      if ( getJoinType() == other.getJoinType() ) {
        // no order key and same join type
        return 0;
      }

      // Case: inner join : goes below
      //
      if ( getJoinType() == INNER_JOIN ) {
        return -1;
      } else if ( other.getJoinType() == INNER_JOIN ) {
        // CASE: no inner join / inner join : goes to the top
        return 1;
      } else {
        // CASE: no inner join / no inner join : nothing to work with:
        return 0;
      }
    } else if ( !Const.isEmpty( getJoinOrderKey() ) && Const.isEmpty( other.getJoinOrderKey() ) ) {
      // Case: Join order / no join order
          //
      // Case: ? / inner join : goes to the top
      //
      if ( getJoinType() != INNER_JOIN ) {
        return 1;
      } else {
        // CASE: ? / no inner join : nothing to work with:
        return 0;
      }
    } else if ( Const.isEmpty( getJoinOrderKey() ) && !Const.isEmpty( other.getJoinOrderKey() ) ) {
      // Case: No join order / join order
      //
      // Case: inner join / ? : goes to the bottom
      //
      if ( getJoinType() == INNER_JOIN ) {
        return -1;
      } else {
        // CASE: no inner join / ? : nothing to work with:
        return 0;
      }
    } else {
      // Case: both join orders are specified
      return -getJoinOrderKey().compareTo( other.getJoinOrderKey() );
    }
  }

  /**
   * This method contains the legacy, broken compare logic that was used in cases where no join order key was in play.
   * Keeping around in case anyone has unexpected results and needs to revert to the legacy join logic. Note that use of
   * this comparison method can result in non-deterministic results given that it violates the compareTo() contract,
   * allowing (x.compareTo(y) == -1 && y.compareTo(x) == -1).
   */
  private int legacyCompare() {
    logger.debug( "Using legacy SQLJoin compare." );
    if ( getJoinType() == INNER_JOIN ) {
      return -1;
    } else {
      // CASE: no inner join / no inner join : nothing to work with:
      return 0;
    }
  }

  /**
   * @return the leftTableAlias
   */
  public String getLeftTableAlias() {
    return leftTableAlias;
  }

  /**
   * @param leftTableAlias the leftTableAlias to set
   */
  public void setLeftTableAlias( String leftTableAlias ) {
    this.leftTableAlias = leftTableAlias;
  }

  /**
   * @return the rightTableAlias
   */
  public String getRightTableAlias() {
    return rightTableAlias;
  }

  /**
   * @param rightTableAlias the rightTableAlias to set
   */
  public void setRightTableAlias( String rightTableAlias ) {
    this.rightTableAlias = rightTableAlias;
  }
}
