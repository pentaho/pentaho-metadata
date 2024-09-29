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

package org.pentaho.pms.locale;

import org.pentaho.di.core.changed.ChangedFlagInterface;

public interface LocaleInterface extends ChangedFlagInterface, Comparable<LocaleInterface> {
  public void setCode( String code );

  public String getCode();

  public void setDescription( String description );

  public String getDescription();

  public void setOrder( int order );

  public int getOrder();

  public void setActive( boolean active );

  public boolean isActive();

  public void clearChanged();
}
