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

package org.pentaho.metadata.automodel.importing.strategy;

import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.metadata.automodel.PhysicalTableImporter;

public class DefaultImportStrategy implements PhysicalTableImporter.ImportStrategy {

  @Override
  public boolean shouldInclude( final ValueMetaInterface valueMeta ) {
    return true;
  }

  @Override
  public String displayName( final ValueMetaInterface valueMeta ) {
    return valueMeta.getName();
  }
}
