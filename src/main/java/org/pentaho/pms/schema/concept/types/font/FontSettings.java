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
package org.pentaho.pms.schema.concept.types.font;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.pms.util.Const;

/**
 * @deprecated as of metadata 3.0. please see org.pentaho.metadata.model.concept.types.Font
 */
public class FontSettings {
  private String name;
  private int height;
  private boolean bold;
  private boolean italic;

  private static final String SEPARATOR = "-"; //$NON-NLS-1$
  private static final String BOLD = "bold"; //$NON-NLS-1$
  private static final String ITALIC = "italic"; //$NON-NLS-1$

  public FontSettings() {
  }

  /**
   * @param name
   * @param size
   * @param bold
   * @param italic
   */
  public FontSettings( String name, int size, boolean bold, boolean italic ) {
    this.name = name;
    this.height = size;
    this.bold = bold;
    this.italic = italic;
  }

  public String toString() {
    return name + SEPARATOR + height + ( bold ? SEPARATOR + BOLD : "" ) + ( italic ? SEPARATOR + ITALIC : "" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public static FontSettings fromString( String value ) {
    String[] pieces = value.split( SEPARATOR );
    switch ( pieces.length ) {
      case 0:
        return null;
      case 1:
        return new FontSettings( pieces[0], 10, false, false );
      case 2:
        return new FontSettings( pieces[0], Const.toInt( pieces[1], 10 ), false, false );
      case 3:
        return new FontSettings( pieces[0], Const.toInt( pieces[1], 10 ), BOLD.equalsIgnoreCase( pieces[2] ), ITALIC
            .equalsIgnoreCase( pieces[2] ) );
      case 4:
        return new FontSettings( pieces[0], Const.toInt( pieces[1], 10 ), true, true );
      default:
        return null;
    }
  }

  /**
   * @return the bold
   */
  public boolean isBold() {
    return bold;
  }

  /**
   * @param bold
   *          the bold to set
   */
  public void setBold( boolean bold ) {
    this.bold = bold;
  }

  /**
   * @return the italic
   */
  public boolean isItalic() {
    return italic;
  }

  /**
   * @param italic
   *          the italic to set
   */
  public void setItalic( boolean italic ) {
    this.italic = italic;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * @return the size
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param size
   *          the size to set
   */
  public void setHeight( int size ) {
    this.height = size;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof FontSettings == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    FontSettings rhs = (FontSettings) obj;
    return new EqualsBuilder().append( name, rhs.name ).append( height, rhs.height ).append( bold, rhs.bold ).append(
        italic, rhs.italic ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 89, 211 ).append( name ).append( height ).append( bold ).append( italic ).toHashCode();
  }

}
