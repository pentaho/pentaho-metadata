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
package org.pentaho.metadata.query.impl.sql;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.commons.connection.IPentahoMetaData;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryModelMetaData;

/**
 * A mapped query holds a query string that has the "as" identifiers mapped to truncated
 * values in order to avoid the limitation of finite identifier lengths in some databases 
 * (known issue in Oracle, DB2). 
 * 
 * The MappedQuery holds the query string and a map of truncated names mapped to the real 
 * identifiers, so that the truncation is as transparent as possible to the user. 
 * 
 * @author gmoran
 *
 */
public class MappedQuery implements SqlQuery {
  protected String query;

  protected Map columnsMap;

  protected List<? extends Selection> selections;
  protected List<String> paramNameList;

  public MappedQuery(String sql, Map columnsMap, List<? extends Selection> selections, List<String> paramNameList) {
    query = sql;
    this.selections = selections;
    this.columnsMap = columnsMap;
    this.paramNameList = paramNameList;
  }

  /**
   * The "display" query returns the query with the full identifiers in the "as" portion 
   * of the statement. NOTE that this is NOT the query that should be executed. Use getQuery()
   * for the proper executable query string. 
   * 
   * @return displayable query string
   */
  public String getDisplayQuery() {

    String execQuery = new String(query);
    for (Iterator iter = columnsMap.keySet().iterator(); iter.hasNext();) {
      String element = (String) iter.next();
      String identifier = (String) columnsMap.get(element);
      execQuery = wholeWordReplaceAll(execQuery, element, identifier);
    }
    return execQuery;
  }

  /**
   * Does a "whole word" find and replace-all on the source string.
   * <br/>
   * We need to replace the <code>searchString</code> with the <code>replacement</code> string
   * while being careful that the <code>searchString</code> is not part of a larger word...
   * <br/>
   * In BISERVER-2881, the <code>String.replaceAll(...)</code> was being used but caused a bug
   * in the following situation:
   * <code>
   *   String s = "SELECT A AS COL1, B AS COL10, C AS COL11";
   *   s = s.replaceAll("COL1", "TEST");
   *   // At this point, s = "SELECT A AS TEST, B AS TEST0, C AS TEST1"
   * </code> 
   * 
   * So this method will use a regular expression to surround the search string with "non-Word characters"
   * and will retain them in the match so that the replacement will contain those same "non-Word characters".
   * In java, the \W character is treated as equivalent to [^a-zA-Z0-9]. 
   * 
   * @param source the string upon which the search-and-replace will occur
   * @param search the search string
   * @param repl the string used in the replacement
   * @return the string after the whole-word search-and-replace has been completed
   */
  private String wholeWordReplaceAll(final String source, final String search, final String repl) {
    final Pattern p = Pattern.compile("(\\W)" + search + "(\\W)"); //$NON-NLS-1$ //$NON-NLS-2$
    final Matcher m = p.matcher(source);
    final StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, m.group(1) + repl + m.group(2));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * The map holds the mapping from short ids to long ids. 
   * @return the map of short ids to long ids
   */
  public Map getMap() {
    return columnsMap;
  }
  
  public List<String> getParamList() {
    return paramNameList;
  }

  /**
   * returns a generated sql query string
   * @return sql query string
   */
  public String getQuery() {
    return query;
  }

  @SuppressWarnings("deprecation")
  public IPentahoMetaData generateMetadata(IPentahoMetaData nativeMetadata) {

    // columnsMap holds a reference to the id of the columns that we retrieved - the column
    // headers in the resultSet currently (if columnsMap is not null) are truncated names that 
    // we query with to get past length limitations of some databases. Here, we reinstate the true column ids for 
    // display and further metadata mapping purposes. 

    return new QueryModelMetaData(columnsMap, nativeMetadata.getColumnHeaders(), nativeMetadata.getRowHeaders(),
        selections);
  }
}
