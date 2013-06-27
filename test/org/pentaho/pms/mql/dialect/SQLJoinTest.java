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
 * Copyright (c) 2013 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.mql.dialect;

import org.apache.log4j.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.metadata.model.*;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.concept.types.RelationshipType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.MetadataTestBase;
import org.pentaho.pms.core.exception.PentahoMetadataException;

import java.io.ByteArrayOutputStream;

public class SQLJoinTest {

    @BeforeClass
    public static void initKettle() throws KettleException {
        MetadataTestBase.initKettleEnvironment();
    }

    @Test
    public void testCompareTo() {

        // array of testing scenarios to validate symmetric comparisons.
        //  Parameters for TestScenario are
        // ( this.JoinType, this.joinOrderKey, other.joinType, other.joinOrderKey, expectedComparison)
        TestScenario[] scenarios = {
                new TestScenario(JoinType.INNER_JOIN, null, JoinType.INNER_JOIN, null, 0),
                new TestScenario(JoinType.LEFT_OUTER_JOIN, null, JoinType.LEFT_OUTER_JOIN, null, 0),

                new TestScenario(JoinType.INNER_JOIN, null, JoinType.LEFT_OUTER_JOIN, null, -1),
                new TestScenario(JoinType.LEFT_OUTER_JOIN, null, JoinType.INNER_JOIN, null, 1),

                new TestScenario(JoinType.INNER_JOIN, "A", JoinType.INNER_JOIN, "B", 1),
                new TestScenario(JoinType.INNER_JOIN, "B", JoinType.INNER_JOIN, "A", -1),

                new TestScenario(JoinType.INNER_JOIN, "A", JoinType.INNER_JOIN, "A", 0),

                new TestScenario(JoinType.INNER_JOIN, "A", JoinType.LEFT_OUTER_JOIN, null, 0),
                new TestScenario(JoinType.LEFT_OUTER_JOIN, null, JoinType.INNER_JOIN, "A", 0),

                new TestScenario(JoinType.LEFT_OUTER_JOIN, "A", JoinType.INNER_JOIN, null, 1),
                new TestScenario(JoinType.INNER_JOIN, null, JoinType.LEFT_OUTER_JOIN, "A", -1),
        };

        for (TestScenario scenario : scenarios) {
            SQLJoin thisObject = makeSQLJoin(scenario.joinTypeThis, scenario.joinOrderThis);
            SQLJoin otherObject = makeSQLJoin(scenario.joinTypeOther, scenario.joinOrderOther);

            Assert.assertEquals("\nTesting SQLJoin.compareTo() with scenario: \n" + scenario.toString(),
                    scenario.expected, thisObject.compareTo(otherObject));
        }
    }

    /**
     * Create a SQLJoin, setting the fields we care about for testing,
     * with dummy values for the others.
     */
    private SQLJoin makeSQLJoin(JoinType joinType, String joinOrderKey) {
        return new SQLJoin
                ("Left", "LeftAlias", "Right", "RightAlias",
                new SQLQueryModel.SQLWhereFormula("1=1", null, false),
                        joinType, joinOrderKey);
    }

    /**
     * Since the legacy join compare logic is non-deterministic, it is not
     * safe to build any expected test results on the generated SQL.
     * The fallback is to validate that the legacy code path is traversed
     * when the "legacy_join_order" boolean set to true in the model.
     * To do this, the test verifies that logging output is as
     * expected.
     * @throws PentahoMetadataException
     */
    @Test
    public void testLegacyJoinOrderLogic() throws PentahoMetadataException {
        Logger logger = Logger.getLogger(SQLJoin.class.getName());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Appender appender = new WriterAppender(new SimpleLayout(), out);
        logger.addAppender(appender);

        try {
            RelationshipType[] typesToTest = new RelationshipType[]{RelationshipType._0_N, RelationshipType._1_1};
            for (RelationshipType firstRel : typesToTest) {
                for (RelationshipType secondRel : typesToTest) {
                    final LogicalModel model = new LogicalModel();
                    model.setId("model_01");
                    Category mainCat = new Category();
                    mainCat.setId("cat_01");
                    model.getCategories().add(mainCat);

                    LogicalTable[] tables = getTablesWithRelationships(
                            firstRel,
                            secondRel,
                            mainCat,
                            model);
                    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", "");
                    Query myTest = new Query(null, model);
                    myTest.getSelections().add(new Selection(null, tables[0].getLogicalColumns().get(0), null));
                    myTest.getSelections().add(new Selection(null, tables[1].getLogicalColumns().get(0), null));
                    myTest.getSelections().add(new Selection(null, tables[2].getLogicalColumns().get(0), null));

                    SqlGenerator generator = new SqlGenerator();

                    // first verify the legacy logic is not used if the property is not set
                    generator.generateSql(myTest, "en_US", null, databaseMeta);
                    Assert.assertTrue("Did not expect to use the legacy SQLJoin.compareTo() logic.",
                            !out.toString().contains("Using legacy SQLJoin compare."));

                    // set the property and make sure the legacy logic is used
                    model.setProperty("legacy_join_order", true);
                    generator.generateSql(myTest, "en_US", null, databaseMeta);
                    Assert.assertTrue("Should have used legacy SQLJoin.compareTo() logic.",
                            out.toString().contains("Using legacy SQLJoin compare."));
                    out.reset();  // clear out accumulated logs for next run
                }
            }
        } finally {
            logger.removeAppender(appender);
        }

    }

    private LogicalTable[] getTablesWithRelationships(RelationshipType relationship1,
                                                      RelationshipType relationship2,
                                                      Category category,
                                                      LogicalModel model) {
        LogicalTable table = getDummySingleColumnTable("1");
        category.addLogicalColumn(table.getLogicalColumns().get(0));

        LogicalTable table2 = getDummySingleColumnTable("2");
        category.addLogicalColumn(table2.getLogicalColumns().get(0));

        LogicalTable table3 = getDummySingleColumnTable("3");
        category.addLogicalColumn(table3.getLogicalColumns().get(0));

        final LogicalRelationship rl1 = new LogicalRelationship();
        rl1.setRelationshipType(relationship1);
        rl1.setFromTable(table);
        rl1.setFromColumn(table.getLogicalColumns().get(0));
        rl1.setToTable(table2);
        rl1.setToColumn(table2.getLogicalColumns().get(0));

        final LogicalRelationship rl2 = new LogicalRelationship();
        rl2.setRelationshipType(relationship2);
        rl2.setFromTable(table2);
        rl2.setFromColumn(table2.getLogicalColumns().get(0));
        rl2.setToTable(table3);
        rl2.setToColumn(table3.getLogicalColumns().get(0));

        model.getLogicalRelationships().add(rl1);
        model.getLogicalRelationships().add(rl2);

        return new LogicalTable[] { table, table2, table3 };
    }

    private LogicalTable getDummySingleColumnTable(String identifier) {
        final LogicalTable bt1 = new LogicalTable();
        bt1.setId("bt" + identifier); 
        bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt" + identifier);
        final LogicalColumn bc1 = new LogicalColumn();
        bc1.setId("bc" + identifier); 
        bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc" + identifier);
        bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
        bc1.setLogicalTable(bt1);
        bt1.addLogicalColumn(bc1);
        bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
        return bt1;
    }


    class TestScenario {
        JoinType joinTypeThis;
        String joinOrderThis;
        JoinType joinTypeOther;
        String joinOrderOther;
        int expected;

        TestScenario(JoinType thisType, String thisOrder, JoinType otherType, String otherOrder, int expected) {
            this.joinTypeThis = thisType;
            this.joinOrderThis = thisOrder;
            this.joinTypeOther = otherType;
            this.joinOrderOther = otherOrder;
            this.expected = expected;
        }

        public String toString() {
            return "this.joinType=" + joinTypeThis + ", this.joinOrderKey=" + joinOrderThis + "\n" +
                    "other.joinType=" + joinTypeOther + ", other.joinOrderKey=" + joinOrderOther + "\n" +
                    "Expected result of this.compareTo(other)=" + expected + "\n";
        }
    }
}
