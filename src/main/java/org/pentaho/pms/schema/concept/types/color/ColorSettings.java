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
package org.pentaho.pms.schema.concept.types.color;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @deprecated as of metadata 3.0. please see org.pentaho.metadata.model.concept.types.Color
 */
public class ColorSettings {
  private static final String SEPARATOR = ","; //$NON-NLS-1$

  public static final ColorSettings BLACK = new ColorSettings( 0, 0, 0 );
  public static final ColorSettings WHITE = new ColorSettings( 255, 255, 255 );
  public static final ColorSettings RED = new ColorSettings( 255, 0, 0 );
  public static final ColorSettings GREEN = new ColorSettings( 0, 255, 0 );
  public static final ColorSettings BLUE = new ColorSettings( 0, 0, 255 );

  int red, green, blue;

  /**
   * @param red
   * @param green
   * @param blue
   */
  public ColorSettings( int red, int green, int blue ) {
    super();
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  /**
   * @return the blue
   */
  public int getBlue() {
    return blue;
  }

  /**
   * @param blue
   *          the blue to set
   */
  public void setBlue( int blue ) {
    this.blue = blue;
  }

  /**
   * @return the green
   */
  public int getGreen() {
    return green;
  }

  /**
   * @param green
   *          the green to set
   */
  public void setGreen( int green ) {
    this.green = green;
  }

  /**
   * @return the red
   */
  public int getRed() {
    return red;
  }

  /**
   * @param red
   *          the red to set
   */
  public void setRed( int red ) {
    this.red = red;
  }

  public String toString() {
    return red + SEPARATOR + green + SEPARATOR + blue;
  }

  public static final ColorSettings fromString( String value ) {
    String[] colors = value.split( SEPARATOR );
    if ( colors.length == 3 ) {
      int red = Integer.parseInt( colors[0] );
      int green = Integer.parseInt( colors[1] );
      int blue = Integer.parseInt( colors[2] );

      return new ColorSettings( red, green, blue );
    }
    return null;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof ColorSettings == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    ColorSettings rhs = (ColorSettings) obj;
    return new EqualsBuilder().append( red, rhs.red ).append( blue, rhs.blue ).append( green, rhs.green ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 89, 173 ).append( red ).append( blue ).append( green ).toHashCode();
  }
}
