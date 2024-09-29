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

package org.pentaho.pms.util;

import org.pentaho.di.core.Props;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;

public class Settings {
  private static final String BUSINESS_MODEL_ID_PREFIX = "BUSINESS_MODEL_ID_PREFIX"; //$NON-NLS-1$
  private static final String BUSINESS_CATEGORY_ID_PREFIX = "BUSINESS_CATEGORY_ID_PREFIX"; //$NON-NLS-1$
  private static final String BUSINESS_TABLE_ID_PREFIX = "BUSINESS_TABLE_ID_PREFIX"; //$NON-NLS-1$
  private static final String BUSINESS_COLUMN_ID_PREFIX = "BUSINESS_COLUMN_ID_PREFIX"; //$NON-NLS-1$
  private static final String PHYSICAL_TABLE_ID_PREFIX = "PHYSICAL_TABLE_ID_PREFIX"; //$NON-NLS-1$
  private static final String PHYSICAL_COLUMN_ID_PREFIX = "PHYSICAL_COLUMN_ID_PREFIX"; //$NON-NLS-1$
  private static final String IDS_ARE_UPPERCASE = "IDS_ARE_UPPERCASE"; //$NON-NLS-1$
  private static final String SCHEMA_FACTORY_CLASS_NAME = "SCHEMA_FACTORY_CLASS_NAME"; //$NON-NLS-1$
  private static final String CONCEPT_NAME_BASE = null;

  public static final String getBusinessModelIDPrefix() {
    return Props.getInstance().getCustomParameter( BUSINESS_MODEL_ID_PREFIX, "bv_" ); //$NON-NLS-1$
  }

  public static final void setBusinessModelIDPrefix( String prefix ) {
    Props.getInstance().setCustomParameter( BUSINESS_MODEL_ID_PREFIX, prefix );
  }

  public static final String getBusinessTableIDPrefix() {
    return Props.getInstance().getCustomParameter( BUSINESS_TABLE_ID_PREFIX, "bt_" ); //$NON-NLS-1$
  }

  public static final void setBusinessTableIDPrefix( String prefix ) {
    Props.getInstance().setCustomParameter( BUSINESS_TABLE_ID_PREFIX, prefix );
  }

  public static final String getBusinessColumnIDPrefix() {
    return Props.getInstance().getCustomParameter( BUSINESS_COLUMN_ID_PREFIX, "bc_" ); //$NON-NLS-1$
  }

  public static final void setBusinessColumnIDPrefix( String prefix ) {
    Props.getInstance().setCustomParameter( BUSINESS_COLUMN_ID_PREFIX, prefix );
  }

  public static final String getBusinessCategoryIDPrefix() {
    return Props.getInstance().getCustomParameter( BUSINESS_CATEGORY_ID_PREFIX, "bc_" ); //$NON-NLS-1$
  }

  public static final void setBusinessCategoryIDPrefix( String prefix ) {
    Props.getInstance().setCustomParameter( BUSINESS_CATEGORY_ID_PREFIX, prefix );
  }

  public static final String getPhysicalTableIDPrefix() {
    return Props.getInstance().getCustomParameter( PHYSICAL_TABLE_ID_PREFIX, "pt_" ); //$NON-NLS-1$
  }

  public static final void setPhysicalTableIDPrefix( String prefix ) {
    Props.getInstance().setCustomParameter( PHYSICAL_TABLE_ID_PREFIX, prefix );
  }

  public static final String getPhysicalColumnIDPrefix() {
    return Props.getInstance().getCustomParameter( PHYSICAL_COLUMN_ID_PREFIX, "pc_" ); //$NON-NLS-1$
  }

  public static final void setPhysicalColumnIDPrefix( String prefix ) {
    Props.getInstance().setCustomParameter( PHYSICAL_COLUMN_ID_PREFIX, prefix );
  }

  public static final boolean isAnIdUppercase() {
    return "Y".equalsIgnoreCase( Props.getInstance().getCustomParameter( IDS_ARE_UPPERCASE, "Y" ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public static String getConceptNameBase() {
    return Props.getInstance().getCustomParameter( CONCEPT_NAME_BASE, "Base" ); //$NON-NLS-1$
  }

  public static final CwmSchemaFactoryInterface getCwmSchemaFactory() {
    String cwmSchemaClassName =
        Props.getInstance().getCustomParameter( SCHEMA_FACTORY_CLASS_NAME, "org.pentaho.pms.factory.CwmSchemaFactory" ); //$NON-NLS-1$
    try {
      Class cwlSchemaObj = Class.forName( cwmSchemaClassName );
      Object obj = cwlSchemaObj.newInstance();
      return (CwmSchemaFactoryInterface) obj;
    } catch ( Exception ex ) {
      ex.printStackTrace(); // TODO: Proper error handling needs to go here
    }
    return null;
  }

}
