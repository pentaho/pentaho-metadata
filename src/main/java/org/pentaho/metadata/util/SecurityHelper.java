/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.metadata.util;

import java.util.Iterator;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.repository.IMetadataDomainRepository;

public class SecurityHelper {
  public Domain createSecureDomain( IMetadataDomainRepository repo, Domain domain ) {
    Domain clone = (Domain) domain.clone();

    // force security on model, logical tables, logical columns, and categories

    Iterator<LogicalModel> iter = clone.getLogicalModels().iterator();
    while ( iter.hasNext() ) {
      LogicalModel model = iter.next();
      if ( !repo.hasAccess( IMetadataDomainRepository.ACCESS_TYPE_READ, model ) ) {
        iter.remove();
      } else {
        Iterator<LogicalTable> tbliter = model.getLogicalTables().iterator();
        while ( tbliter.hasNext() ) {
          LogicalTable table = tbliter.next();
          if ( !repo.hasAccess( IMetadataDomainRepository.ACCESS_TYPE_READ, table ) ) {
            tbliter.remove();
          } else {
            Iterator<LogicalColumn> coliter = table.getLogicalColumns().iterator();
            while ( coliter.hasNext() ) {
              LogicalColumn col = coliter.next();
              if ( !repo.hasAccess( IMetadataDomainRepository.ACCESS_TYPE_READ, col ) ) {
                coliter.remove();
              }
            }
          }
        }
        Iterator<Category> catiter = model.getCategories().iterator();
        while ( catiter.hasNext() ) {
          Category category = catiter.next();
          if ( !repo.hasAccess( IMetadataDomainRepository.ACCESS_TYPE_READ, category ) ) {
            catiter.remove();
          } else {
            Iterator<LogicalColumn> coliter = category.getLogicalColumns().iterator();
            while ( coliter.hasNext() ) {
              LogicalColumn col = coliter.next();
              if ( !repo.hasAccess( IMetadataDomainRepository.ACCESS_TYPE_READ, col ) ) {
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
