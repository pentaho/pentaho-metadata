/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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
