/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
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
package org.pentaho.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.pms.messages.util.LocaleHelper;
import org.pentaho.pms.mql.MQLQueryImpl;

public class ThinQueryTest {
  
  
  @Test
  public void testQueryXmlSerialization() {
    Domain domain = TestHelper.getBasicDomain();
    LogicalModel model = domain.findLogicalModel("MODEL");
    Query query = new Query(domain, model);
    
    Category category = model.findCategory("CATEGORY");
    LogicalColumn column = category.findLogicalColumn("LC_CUSTOMERNAME");
    
    query.getParameters().add(new Parameter("test", DataType.STRING, "val"));
    
    query.getSelections().add(new Selection(category, column, null));
    
    query.getConstraints().add(new Constraint(CombinationType.AND, "[CATEGORY.LC_CUSTOMERNAME] = \"bob\""));

    query.getOrders().add(new Order(new Selection(category, column, null), Order.Type.ASC));
    
    QueryXmlHelper helper = new QueryXmlHelper();
    String xml = helper.toXML(query);
    
    InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
    try {
      repo.storeDomain(domain, true);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    Query newQuery = null;
    try {
      newQuery = helper.fromXML(repo, xml);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    // verify that when we serialize and deserialize, the xml stays the same. 
    Assert.assertEquals(xml, helper.toXML(newQuery));
  }
  
  @Test
  public void testQueryConversion() throws Exception {
    Domain domain = TestHelper.getBasicDomain();
    LogicalModel model = domain.findLogicalModel("MODEL");
    Query query = new Query(domain, model);
    
    Category category = model.findCategory("CATEGORY");
    LogicalColumn column = category.findLogicalColumn("LC_CUSTOMERNAME");
    query.getSelections().add(new Selection(category, column, null));
    
    query.getConstraints().add(new Constraint(CombinationType.AND, "[CATEGORY.LC_CUSTOMERNAME] = \"bob\""));

    query.getOrders().add(new Order(new Selection(category, column, null), Order.Type.ASC));
    MQLQueryImpl impl = null;
    try {
      impl = ThinModelConverter.convertToLegacy(query, null);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    Assert.assertNotNull(impl);
    Assert.assertEquals(
        "SELECT DISTINCT \n" + 
        "          LT.customername AS COL0\n" + 
        "FROM \n" + 
        "          (select * from customers) LT\n" + 
        "WHERE \n" + 
        "        (\n" + 
        "          (\n" + 
        "              LT.customername  = 'bob'\n" + 
        "          )\n" + 
        "        )\n" + 
        "ORDER BY \n" + 
        "          COL0\n",
        impl.getQuery().getQuery()
    );

  }
  
  
  public static void printOutJava(String sql) {
    String lines[] = sql.split("\n");
    for (int i = 0; i < lines.length; i++) {
      System.out.print("        \"" +lines[i]);
      if (i == lines.length - 1) {
        System.out.println("\\n\"");
      } else {
        System.out.println("\\n\" + ");
      }
    }
  }
}
