/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model.olap;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.LogicalTable;

public class OlapDimension implements Cloneable {
  private String name;
  private boolean timeDimension;

  private List<OlapHierarchy> hierarchies;

  public OlapDimension() {
    hierarchies = new ArrayList<OlapHierarchy>();
  }

  public Object clone() {
    OlapDimension olapDimension = new OlapDimension();

    olapDimension.name = name;
    olapDimension.timeDimension = timeDimension;
    for (int i = 0; i < hierarchies.size(); i++) {
      OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get(i);
      olapDimension.hierarchies.add((OlapHierarchy) hierarchy.clone());
    }

    return olapDimension;
  }

  public boolean equals(Object obj) {
    return name.equals(((OlapDimension) obj).getName());
  }

  /**
   * @return the hierarchies
   */
  public List<OlapHierarchy> getHierarchies() {
    return hierarchies;
  }

  /**
   * @param hierarchies
   *          the hierarchies to set
   */
  public void setHierarchies(List<OlapHierarchy> hierarchies) {
    this.hierarchies = hierarchies;
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
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the timeDimension
   */
  public boolean isTimeDimension() {
    return timeDimension;
  }

  /**
   * @param timeDimension
   *          the timeDimension to set
   */
  public void setTimeDimension(boolean timeDimension) {
    this.timeDimension = timeDimension;
  }

  public OlapHierarchy findOlapHierarchy(String thisName) {
    for (int i = 0; i < hierarchies.size(); i++) {
      OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get(i);
      if (hierarchy.getName().equalsIgnoreCase(thisName))
        return hierarchy;
    }
    return null;
  }

  /**
   * @return the businessTable
   */
  public LogicalTable findLogicalTable() {
    for (int i = 0; i < hierarchies.size(); i++) {
      OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get(i);
      if (hierarchy.getLogicalTable() != null)
        return hierarchy.getLogicalTable();
    }
    return null;
  }
}
