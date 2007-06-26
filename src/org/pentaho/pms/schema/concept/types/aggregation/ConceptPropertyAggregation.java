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
package org.pentaho.pms.schema.concept.types.aggregation;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class ConceptPropertyAggregation extends ConceptPropertyBase implements Cloneable
{
    public static final ConceptPropertyAggregation NONE     = new ConceptPropertyAggregation("aggregation", AggregationSettings.NONE); //$NON-NLS-1$
    public static final ConceptPropertyAggregation SUM      = new ConceptPropertyAggregation("aggregation", AggregationSettings.SUM); //$NON-NLS-1$
    public static final ConceptPropertyAggregation AVERAGE  = new ConceptPropertyAggregation("aggregation", AggregationSettings.AVERAGE); //$NON-NLS-1$
    public static final ConceptPropertyAggregation COUNT    = new ConceptPropertyAggregation("aggregation", AggregationSettings.COUNT); //$NON-NLS-1$
    public static final ConceptPropertyAggregation MINIMUM  = new ConceptPropertyAggregation("aggregation", AggregationSettings.MINIMUM); //$NON-NLS-1$
    public static final ConceptPropertyAggregation MAXIMUM  = new ConceptPropertyAggregation("aggregation", AggregationSettings.MAXIMUM); //$NON-NLS-1$

    private AggregationSettings value;

    public ConceptPropertyAggregation(String name, AggregationSettings value)
    {
        this(name, value, false);
    }

    public ConceptPropertyAggregation(String name, AggregationSettings value, boolean required)
    {
        super(name, required);
        this.value = value;
    }

    public String toString()
    {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
      append(getId()).append(isRequired()).append(value).
      toString();
    }

    public Object clone() throws CloneNotSupportedException
    {
      ConceptPropertyAggregation rtn = (ConceptPropertyAggregation)super.clone();
      if (value != null) {
        rtn.value = new AggregationSettings(value.getType());
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.AGGREGATION;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (AggregationSettings) value;
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
