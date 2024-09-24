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
package org.pentaho.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.concept.security.RowLevelSecurity;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.security.SecurityOwner.OwnerType;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.RowLevelSecurityHelper;
import org.pentaho.pms.MetadataTestBase;

public class RowLevelSecurityIT {

  @BeforeClass
  public static void initKettle() throws Exception {
    MetadataTestBase.initKettleEnvironment();
  }

  @Test
  public void testRowLevelSecurity() throws Exception {
    LogicalModel model = TestHelper.buildDefaultModel();
    SqlGenerator generator = new SqlGenerator();
    InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository() {
      public String generateRowLevelSecurityConstraint( LogicalModel model ) {
        RowLevelSecurity rls = model.getRowLevelSecurity();
        RowLevelSecurityHelper helper = new RowLevelSecurityHelper();
        List<String> roles = new ArrayList<String>();
        roles.add( "test" );
        return helper.getOpenFormulaSecurityConstraint( rls, "test", roles );
      }
    };

    SecurityOwner so = new SecurityOwner( OwnerType.USER, "test" );
    Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
    map.put( so, "FALSE()" );

    RowLevelSecurity rls = new RowLevelSecurity( map );
    model.setRowLevelSecurity( rls );

    Query query = new Query( null, model );
    query.getSelections().add( new Selection( null, model.findLogicalColumn( "bc1" ), null ) );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

    MappedQuery mquery = generator.generateSql( query, "en_US", repo, databaseMeta );
    // TestHelper.printOutJava(mquery.getQuery());
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n" + "FROM \n"
        + "          pt1 bt1\n" + "WHERE \n" + "        (\n" + "          1=0\n" + "        )\n", mquery.getQuery() ); //$NON-NLS-1$
  }

}
