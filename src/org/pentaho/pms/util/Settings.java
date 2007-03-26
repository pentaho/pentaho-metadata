/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.util;

import org.pentaho.pms.factory.CwmSchemaFactoryInterface;

import be.ibridge.kettle.core.Props;

public class Settings
{
    private static final String BUSINESS_MODEL_ID_PREFIX      = "BUSINESS_MODEL_ID_PREFIX";
    private static final String BUSINESS_CATEGORY_ID_PREFIX  = "BUSINESS_CATEGORY_ID_PREFIX";
    private static final String BUSINESS_TABLE_ID_PREFIX     = "BUSINESS_TABLE_ID_PREFIX";
    private static final String BUSINESS_COLUMN_ID_PREFIX    = "BUSINESS_COLUMN_ID_PREFIX";
    private static final String PHYSICAL_TABLE_ID_PREFIX     = "PHYSICAL_TABLE_ID_PREFIX";
    private static final String PHYSICAL_COLUMN_ID_PREFIX    = "PHYSICAL_COLUMN_ID_PREFIX";
    private static final String IDS_ARE_UPPERCASE            = "IDS_ARE_UPPERCASE";
    private static final String SCHEMA_FACTORY_CLASS_NAME         = "SCHEMA_FACTORY_CLASS_NAME";
    private static final String CONCEPT_NAME_BASE = null;
    private static final String CONCEPT_NAME_NUMBER = null;
    private static final String CONCEPT_NAME_ID = null;
    private static final String CONCEPT_NAME_SK = null;
    
    public static final String getBusinessModelIDPrefix()
    {
        return Props.getInstance().getCustomParameter(BUSINESS_MODEL_ID_PREFIX, "bv_");
    }
    
    public static final void setBusinessModelIDPrefix(String prefix)
    {
        Props.getInstance().setCustomParameter(BUSINESS_MODEL_ID_PREFIX, prefix);
    }
    
    public static final String getBusinessTableIDPrefix()
    {
        return Props.getInstance().getCustomParameter(BUSINESS_TABLE_ID_PREFIX, "bt_");
    }

    public static final void setBusinessTableIDPrefix(String prefix)
    {
        Props.getInstance().setCustomParameter(BUSINESS_TABLE_ID_PREFIX, prefix);
    }

    public static final String getBusinessColumnIDPrefix()
    {
        return Props.getInstance().getCustomParameter(BUSINESS_COLUMN_ID_PREFIX, "bc_");
    }

    public static final void setBusinessColumnIDPrefix(String prefix)
    {
        Props.getInstance().setCustomParameter(BUSINESS_COLUMN_ID_PREFIX, prefix);
    }

    public static final String getBusinessCategoryIDPrefix()
    {
        return Props.getInstance().getCustomParameter(BUSINESS_CATEGORY_ID_PREFIX, "bc_");
    }

    public static final void setBusinessCategoryIDPrefix(String prefix)
    {
        Props.getInstance().setCustomParameter(BUSINESS_CATEGORY_ID_PREFIX, prefix);
    }

    public static final String getPhysicalTableIDPrefix()
    {
        return Props.getInstance().getCustomParameter(PHYSICAL_TABLE_ID_PREFIX, "pt_");
    }

    public static final void setPhysicalTableIDPrefix(String prefix)
    {
        Props.getInstance().setCustomParameter(PHYSICAL_TABLE_ID_PREFIX, prefix);
    }

    public static final String getPhysicalColumnIDPrefix()
    {
        return Props.getInstance().getCustomParameter(PHYSICAL_COLUMN_ID_PREFIX, "pc_");
    }

    public static final void setPhysicalColumnIDPrefix(String prefix)
    {
        Props.getInstance().setCustomParameter(PHYSICAL_COLUMN_ID_PREFIX, prefix);
    }

    public static final boolean isAnIdUppercase()
    {
        return "Y".equalsIgnoreCase( Props.getInstance().getCustomParameter(IDS_ARE_UPPERCASE, "Y") );
    }

    public static String getConceptNameBase()
    {
        return Props.getInstance().getCustomParameter(CONCEPT_NAME_BASE, "Base");
    }

    public static String getConceptNameNumber()
    {
        return Props.getInstance().getCustomParameter(CONCEPT_NAME_NUMBER, "Number");
    }

    public static String getConceptNameID()
    {
        return Props.getInstance().getCustomParameter(CONCEPT_NAME_ID, "ID");
    }

    public static String getConceptNameSK()
    {
        return Props.getInstance().getCustomParameter(CONCEPT_NAME_SK, "SurrogateKey");
    }

    
    public static final CwmSchemaFactoryInterface getCwmSchemaFactory() {
      String cwmSchemaClassName = Props.getInstance().getCustomParameter(SCHEMA_FACTORY_CLASS_NAME, "org.pentaho.pms.factory.CwmSchemaFactory");
      try {
        Class cwlSchemaObj = Class.forName(cwmSchemaClassName);
        Object obj = cwlSchemaObj.newInstance();
        return (CwmSchemaFactoryInterface)obj;
      } catch (Exception ex) {
        ex.printStackTrace(); // TODO: Proper error handling needs to go here
      }
      return null;
    }



}
