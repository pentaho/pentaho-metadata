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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.metadata.util;

import java.util.Iterator;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.repository.IMetadataDomainRepository;

public class SecurityHelper {
  public Domain createSecureDomain(IMetadataDomainRepository repo, Domain domain) {
    Domain clone = (Domain)domain.clone();
    
    // force security on model, logical tables, logical columns, and categories
    
    Iterator<LogicalModel> iter = clone.getLogicalModels().iterator();
    while (iter.hasNext()) {
      LogicalModel model = iter.next();
      if (!repo.hasAccess(IMetadataDomainRepository.ACCESS_TYPE_READ, model)) {
        iter.remove();
      } else {
        Iterator<LogicalTable> tbliter = model.getLogicalTables().iterator();
        while (tbliter.hasNext()) {
          LogicalTable table = tbliter.next();
          if (!repo.hasAccess(IMetadataDomainRepository.ACCESS_TYPE_READ, table)) {
            tbliter.remove();
          } else {
            Iterator<LogicalColumn> coliter = table.getLogicalColumns().iterator();
            while (coliter.hasNext()) {
              LogicalColumn col = coliter.next();
              if (!repo.hasAccess(IMetadataDomainRepository.ACCESS_TYPE_READ, col)) {
                coliter.remove();
              }            
            }
          }
        }
        Iterator<Category> catiter = model.getCategories().iterator();
        while (catiter.hasNext()) {
          Category category = catiter.next();
          if (!repo.hasAccess(IMetadataDomainRepository.ACCESS_TYPE_READ, category)) {
            catiter.remove();
          } else {
            Iterator<LogicalColumn> coliter = category.getLogicalColumns().iterator();
            while (coliter.hasNext()) {
              LogicalColumn col = coliter.next();
              if (!repo.hasAccess(IMetadataDomainRepository.ACCESS_TYPE_READ, col)) {
                coliter.remove();
              }            
            }
          }
        }
      }
    }
    return clone;
  }
}
