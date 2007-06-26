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
package org.pentaho.pms.schema.concept.types.partitioning;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

import be.ibridge.kettle.trans.step.StepPartitioningMeta;

public class ConceptPropertyDatabasePartitioning extends ConceptPropertyBase implements Cloneable
{
    private StepPartitioningMeta value;

    public ConceptPropertyDatabasePartitioning(String name, StepPartitioningMeta value)
    {
      this(name, value, false);
    }

    public ConceptPropertyDatabasePartitioning(String name, StepPartitioningMeta value, boolean required)
    {
        super(name, required);
        this.value = value;
    }


    public String toString()
    {
        if (value==null) return null;
        return value.toString();
    }

    public Object clone() throws CloneNotSupportedException
    {
      ConceptPropertyDatabasePartitioning rtn = (ConceptPropertyDatabasePartitioning) super.clone();
      if (value != null) {
        rtn.value = (StepPartitioningMeta) value.clone();
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.DATATYPE;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (StepPartitioningMeta) value;
    }

    public boolean equals(Object obj)
    {
        return value.equals(obj);
    }

    public int hashCode()
    {
        return value.hashCode();
    }
}
