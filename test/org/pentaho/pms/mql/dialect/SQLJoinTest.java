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

import org.junit.Assert;
import org.junit.Test;

public class SQLJoinTest {

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
