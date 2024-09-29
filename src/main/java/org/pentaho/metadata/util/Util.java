/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.metadata.util;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.util.validation.ValidationStatus;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.Settings;

import java.util.List;

public class Util {

  /** List of unacceptable characters which should corresponds to the regexp's inside the Util#toId method. */
  public static final String MQL_RESERVED_CHARS = " .,:(){}[]\"`'*/+-";

  public static final String CR = System.getProperty( "line.separator" ); //$NON-NLS-1$

  public static String getCategoryIdPrefix() {
    return "c_"; //$NON-NLS-1$
  }

  public static String getLogicalColumnIdPrefix() {
    return "lc_"; //$NON-NLS-1$
  }

  public static String getLogicalModelIdPrefix() {
    return "lm_"; //$NON-NLS-1$
  }

  public static String getPhysicalTableIdPrefix() {
    return "pt_"; //$NON-NLS-1$
  }

  public static String getPhysicalColumnIdPrefix() {
    return "pc_"; //$NON-NLS-1$
  }

  /**
   * Convert a normal name with spaces into an Id: with underscores replacing the spaces, etc.
   * 
   * @param name
   *          the name to convert to an Id
   * @return The Id-ified name
   */
  public static final String toId( String name ) {
    if ( name == null ) {
      return name;
    }
    name = name.replaceAll( "[ .,:(){}\\[\\]]", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = name.replaceAll( "[\"`']", "" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = name.replaceAll( "[*]", "_TIMES_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = name.replaceAll( "[/]", "_DIVIDED_BY_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = name.replaceAll( "[+]", "_PLUS_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = name.replaceAll( "[-]", "_HYPHEN_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = name.replaceAll( "_+", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    return name;
  }

  /**
   * Returns <tt>true</tt> if <tt>code</tt> contains only latin characters, digits and '<tt>_</tt>' or '<tt>$</tt>'.
   * <tt>null</tt> or empty string is considered to be an invalid value.
   *
   * @param id proposed id for column or table
   * @return <tt>true</tt> if the proposed id is acceptable and <tt>false</tt> otherwise
   * @deprecated Use {@link org.pentaho.metadata.util.Util#validateEntityId(CharSequence)} instead of
   */
  @Deprecated
  public static boolean validateId( CharSequence id ) {
    if ( id == null || id.length() == 0 ) {
      return false;
    }

    for ( int i = 0, len = id.length(); i < len; i++ ) {
      char ch = id.charAt( i );
      if ( isUnacceptableCharacter( ch ) ) {
        return false;
      }
    }

    return true;
  }

  /**
   * @param id proposed id for column or table
   * @return IdValidationStatus
   */
  public static ValidationStatus validateEntityId( CharSequence id ) {
    if ( id == null || id.length() == 0 ) {
      return ValidationStatus.invalid( Messages.getString( "Util.ERROR_0001_ID_EMPTY_OR_NULL" ) );
    }

    for ( int i = 0, len = id.length(); i < len; i++ ) {
      char ch = id.charAt( i );
      if ( isUnacceptableCharacter( ch ) ) {
        return ValidationStatus.invalid( Messages.getString(
          "Util.ERROR_0002_ID_CONTAINS_RESERVED_SYMBOLS", id.toString(), "" ) );
      }
    }

    return ValidationStatus.valid();
  }

  /**
   * Check if character is unacceptable for MQL and need to be converted.
   *
   * @param ch character to check
   * @return true if character is unacceptable for MQL, false otherwise
   */
  private static boolean isUnacceptableCharacter( char ch ) {
    return MQL_RESERVED_CHARS.indexOf( ch ) != -1;
  }

  /**
   *
   * @param id
   * @return
   */
  public static IllegalArgumentException idValidationFailed( String id ) {
    return new IllegalArgumentException(
      "Cannot set id '" + id + "'. Please use Util.toId() to create a well-formed identifier" );
  }

  /**
   * Implements Oracle style NVL function
   * 
   * @param source
   *          The source argument
   * @param def
   *          The default value in case source is null or the length of the string is 0
   * @return source if source is not null, otherwise return def
   */
  public static final String NVL( String source, String def ) {
    if ( source == null || source.length() == 0 ) {
      return def;
    }
    return source;
  }

  // public static final boolean conceptIdExists(final String id, final List<? extends IConcept> concepts) {
  // for (IConcept concept : concepts) {
  // if (concept.getId().equalsIgnoreCase(newId)) {
  // return true;
  // }
  // }
  // return false;
  // }

  public static final String uniquify( final String id, final List<? extends IConcept> concepts ) {
    boolean gotNew = false;
    boolean found = false;
    int conceptNr = 1;
    String newId = id;
    while ( !gotNew ) {
      for ( IConcept concept : concepts ) {
        if ( concept.getId().equalsIgnoreCase( newId ) ) {
          found = true;
          break;
        }
      }
      if ( found ) {
        conceptNr++;
        newId = id + "_" + conceptNr; //$NON-NLS-1$
        found = false;
      } else {
        gotNew = true;
      }
    }
    return newId;
  }

  public static final String proposeSqlBasedLogicalTableId( String locale, LogicalTable businessTable,
      SqlPhysicalTable physicalTable ) {
    String baseID = Util.toId( businessTable.getName( locale ) );
    String namePart = Util.toId( Util.NVL( physicalTable.getName( locale ), physicalTable.getTargetTable() ) );
    String id = Settings.getBusinessTableIDPrefix() + baseID + "_" + namePart; //$NON-NLS-1$
    if ( Settings.isAnIdUppercase() ) {
      id = id.toUpperCase();
    }
    return id;
  }

  public static final String proposeSqlBasedLogicalTableId( String locale, LogicalTable businessTable,
      SqlPhysicalTable physicalTable, List<LogicalTable> tables ) {
    String id = proposeSqlBasedLogicalTableId( locale, businessTable, physicalTable );
    return proposeUnique( id, tables );
  }

  public static final String proposeSqlBasedLogicalColumnId( String locale, LogicalTable businessTable,
      SqlPhysicalColumn physicalColumn ) {
    String baseID = Util.toId( businessTable.getName( locale ) );
    String namePart = Util.toId( Util.NVL( physicalColumn.getName( locale ), physicalColumn.getTargetColumn() ) );
    String id = Util.getLogicalColumnIdPrefix() + baseID + "_" + namePart; //$NON-NLS-1$
    return id.toUpperCase();
  }

  public static final String proposeSqlBasedLogicalColumnId( String locale, LogicalTable table,
      SqlPhysicalColumn physicalColumn, List<LogicalColumn> columns ) {
    String id = proposeSqlBasedLogicalColumnId( locale, table, physicalColumn );
    return proposeUnique( id, columns );
  }

  private static final String proposeUnique( String id, List<? extends Concept> list ) {
    boolean gotNew = false;
    boolean found = false;
    int num = 1;
    String newId = id;

    while ( !gotNew ) {
      for ( Concept col : list ) {
        if ( col.getId().equalsIgnoreCase( newId ) ) {
          found = true;
          break;
        }
      }
      if ( found ) {
        num++;
        newId = id + "_" + num; //$NON-NLS-1$
        found = false;
      } else {
        gotNew = true;
      }
    }
    return newId;

  }

  public static final String proposeSqlBasedCategoryId( String locale, LogicalTable table, Category category ) {
    String baseID = ( table != null ) ? Util.toId( (String) table.getProperty( SqlPhysicalTable.TARGET_TABLE ) ) : ""; //$NON-NLS-1$
    String namePart =
        ( ( category != null ) && ( category.getName( locale ) != null ) )
            ? "_" + Util.toId( category.getName( locale ) ) : ""; //$NON-NLS-1$ //$NON-NLS-2$
    String id = Util.getCategoryIdPrefix() + baseID + namePart;
    return id.toUpperCase();
  }

  public static final String proposeSqlBasedCategoryId( String locale, LogicalTable table, Category category,
      List<Category> categories ) {
    String id = proposeSqlBasedCategoryId( locale, table, category );
    return proposeUnique( id, categories );
  }
}
