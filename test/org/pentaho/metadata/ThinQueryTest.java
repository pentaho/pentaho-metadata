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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.pms.mql.MQLQueryImpl;

@SuppressWarnings("deprecation")
public class ThinQueryTest {
  
  @BeforeClass
  public static void initKettle() throws Exception {
    KettleEnvironment.init(false);
  }
  
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
    TestHelper.assertEqualsIgnoreWhitespaces(
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
    
    query.setLimit(10);
    impl = ThinModelConverter.convertToLegacy(query, null);
    Assert.assertEquals(10, impl.getLimit());
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
