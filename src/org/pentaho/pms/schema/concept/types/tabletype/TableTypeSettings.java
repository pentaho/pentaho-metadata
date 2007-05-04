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
package org.pentaho.pms.schema.concept.types.tabletype;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.messages.Messages;

public class TableTypeSettings
{
    public static final int TYPE_OTHER     = 0;
    public static final int TYPE_DIMENSION = 1;
    public static final int TYPE_FACT      = 2;

    public static final String typeCodes[] = new String[] { "Other", "Dimension", "Fact" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    public static final String typeDescriptions[] = new String[] { Messages.getString("TableTypeSettings.USER_OTHER_DESC"), Messages.getString("TableTypeSettings.USER_DIMENSION_DESC"), Messages.getString("TableTypeSettings.USER_FACT_DESC") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public static final TableTypeSettings OTHER     = new TableTypeSettings(TYPE_OTHER);
    public static final TableTypeSettings DIMENSION = new TableTypeSettings(TYPE_DIMENSION);
    public static final TableTypeSettings FACT      = new TableTypeSettings(TYPE_FACT);

    public static final TableTypeSettings[] types = new TableTypeSettings[] { OTHER, DIMENSION, FACT };

    private int type;

    /**
     * @param type
     */
    public TableTypeSettings(int type)
    {
        super();
        this.type = type;
    }

    /**
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type)
    {
        this.type = type;
    }

    public boolean isFact()
    {
        return type == TYPE_FACT;
    }

    public boolean isDimension()
    {
        return type == TYPE_DIMENSION;
    }

    public String getDescription()
    {
        return typeDescriptions[type];
    }

    public String getCode()
    {
        return typeCodes[type];
    }

    public boolean equals(Object obj) {
      if (obj instanceof TableTypeSettings == false) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      TableTypeSettings rhs = (TableTypeSettings) obj;
      return new EqualsBuilder().append(type, rhs.type).isEquals();
    }

    public int hashCode() {
      return new HashCodeBuilder(233, 281).append(type).toHashCode();
    }

    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
        append(type).
        toString();
  }

    public static TableTypeSettings getType(String description)
    {
        for (int i=0;i<typeDescriptions.length;i++)
        {
            if (typeDescriptions[i].equalsIgnoreCase(description))
            {
                return types[i];
            }
        }
        for (int i=0;i<typeCodes.length;i++)
        {
            if (typeCodes[i].equalsIgnoreCase(description))
            {
                return types[i];
            }
        }
        return OTHER;
    }
}
