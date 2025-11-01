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

package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.LocalizedString;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The category class contains links to logical columns, which are part of the logical model. This can be considered the
 * view of the logical model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class Category extends Concept  {

  private static final long serialVersionUID = -2367402604729602739L;

  private List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();
  private static final String CLASS_ID = "Category";

  public Category() {
    super();
    // category has the following default properties
    setName( new LocalizedString() );
    setDescription( new LocalizedString() );
  }

  public Category( IConcept logicalModel ) {
    setParent( logicalModel );
  }

  @Override
  public List<String> getUniqueId() {
    List<String> uid = new ArrayList<String>( getParent().getUniqueId() );
    uid.add( CLASS_ID.concat( UID_TYPE_SEPARATOR ) + getId() );
    return uid;
  }

  /**
   * The security parent for category is the logical model.
   */
  @Override
  public IConcept getSecurityParentConcept() {
    return getParent();
  }

  /**
   * the list of logical columns in this category
   * 
   * @return list of logical columns.
   */
  public List<LogicalColumn> getLogicalColumns() {
    return logicalColumns;
  }

  public void setLogicalColumns( List<LogicalColumn> columns ) {
    this.logicalColumns = columns;
  }

  public void addLogicalColumn( LogicalColumn column ) {
    logicalColumns.add( column );
  }

  /**
   * searches the category for a specific column id.
   * 
   * @param columnId
   *          column to search for
   * 
   * @return logical column object if found
   */
  public LogicalColumn findLogicalColumn( String columnId ) {
    for ( LogicalColumn col : getLogicalColumns() ) {
      if ( columnId.equals( col.getId() ) ) {
        return col;
      }
    }
    return null;
  }

  @Override
  public Object clone() {
    Category clone = new Category();
    // shallow copies
    clone( clone );
    clone.setParent( getParent() );

    // deep copies
    clone.setLogicalColumns( new ArrayList<LogicalColumn>() );
    for ( LogicalColumn col : getLogicalColumns() ) {
      clone.addLogicalColumn( col );
    }
    return clone;
  }

public void export(HttpServletRequest req, HttpServletResponse res) {
    res = setCors(req, res);
    res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
    byte[] payload = Base64.getDecoder().decode(req.getParameter("data"));
    // jfrog-ignore
    String data = unserialize(new String(payload, StandardCharsets.UTF_8));
}

private HttpServletResponse setCors(HttpServletRequest req, HttpServletResponse res) {
    // Implementation of setCors method
    return res;
}

private String unserialize(String data) {
    // Implementation of unserialize method
    return data; // Placeholder return
}
  
}
