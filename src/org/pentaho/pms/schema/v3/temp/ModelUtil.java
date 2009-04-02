/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 *
 * Copyright 2009 Pentaho Corporation.  All rights reserved. 
 *
 * @created Mar, 2009
 * @author James Dixon
 * 
*/
package org.pentaho.pms.schema.v3.temp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.v3.model.Attribute;
import org.pentaho.pms.schema.v3.model.Category;
import org.pentaho.pms.schema.v3.model.Column;
import org.pentaho.pms.schema.v3.model.Model;
import org.pentaho.pms.schema.v3.model.ModelEnvelope;

/**
 * This class is a utility class that converts between V2 metadata models and v3 metadata models
 * @author jamesdixon
 *
 */
public class ModelUtil {

  /**
   * Returns a model envelope given a BusinessModel, domain and locale. This is used when lists of
   * models are needed
   * @param model
   * @param domain
   * @param locale
   * @return
   */
  public static ModelEnvelope getModelEvelope( BusinessModel model, String domain, String locale ) {
    ModelEnvelope envelope = new ModelEnvelope();
    envelope.setId( model.getId() );
    envelope.setName( model.getName( locale ) );
    envelope.setDescription( model.getDescription(locale) );
    envelope.setDomain(domain);
    return envelope;
  }
  
  /**
   * Returns a thin model given a BusinessModel, domain and locale. If the 'deep' flag is set the
   * attributes of the columns are included, otherwise they are not.
   * @param bmodel
   * @param domain
   * @param locale
   * @param deep
   * @return
   */
  public static Model getModel( BusinessModel bmodel, String domain, String locale, boolean deep ) {
    Model model = new Model();
    model.setId( bmodel.getId() );
    model.setName( bmodel.getName( locale ) );
    model.setDescription( bmodel.getDescription(locale) );
    model.setDomain(domain);
    
    Category rootCategory = ModelUtil.getCategory( bmodel.getRootCategory(), locale, deep );
    model.setRootCategory(rootCategory);
    
    return model;
  }
  
  /**
   * Returns a thin Category give a BusinessCategory, domain and locale. If the 'deep' flag is set the
   * attributes of the columns are included, otherwise they are not
   * @param bCategory
   * @param locale
   * @param deep
   * @return
   */
  public static Category getCategory( BusinessCategory bCategory, String locale, boolean deep ) {
    Category category = new Category();

    // first handle the envelope information
    category.setId( bCategory.getId() );
    category.setName( bCategory.getName( locale ) );
    category.setDescription( bCategory.getDescription(locale) );
    
    // now process any business columns
    List<Column> columns = new ArrayList<Column>();
    for( Object column : bCategory.getBusinessColumns() ) {
      if( deep || !((BusinessColumn)column).isHidden() ) {
        columns.add( getColumn( (BusinessColumn) column, locale, deep  ) );
      }
    }
    category.setColumns(columns.toArray( new Column[columns.size()]));
    
    // now process any sub-categories
    List<Category> subCategories = new ArrayList<Category>();
    for( BusinessCategory subCategory : bCategory.getBusinessCategories() ) {
      subCategories.add( getCategory( subCategory, locale, deep ) );
    }
    category.setSubCategories(subCategories.toArray( new Category[subCategories.size()]));
    
    return category;
  }
  
  /**
   * Returns a thin Column give a BusinessColumn, domain and locale. If the 'deep' flag is set the
   * attributes of the columns are included, otherwise they are not
   * @param bColumn
   * @param locale
   * @param deep
   * @return
   */
  public static Column getColumn( BusinessColumn bColumn, String locale, boolean deep ) {

    Column column = new Column();
    
    // first handle the envelope information
    column.setId( bColumn.getId() );
    column.setName( bColumn.getName( locale ) );
    column.setDescription( bColumn.getDescription(locale) );
    
    // now do standard column settings
    column.setDataType( bColumn.getDataType().getCode() );
    column.setFieldType( bColumn.getFieldType().getCode() );
    
    if( !deep ) {
      return column;
    }
    
    List<Attribute> attributes = new ArrayList<Attribute>();
    
    attributes.add( createAttribute( "exact", "boolean", Boolean.toString( bColumn.isExact() ) ) );
    attributes.add( createAttribute( "formula", "string", bColumn.getFormula() ) );
    attributes.add( createAttribute( "hidden", "boolean", Boolean.toString( bColumn.isHidden() ) ) );

    // now do concepts
    ConceptInterface concept = bColumn.getConcept();
    String ids[] = concept.getChildPropertyIDs();
    for( String id : ids ) {
      ConceptPropertyInterface cpi = concept.getChildProperty( id );
      attributes.add( createAttribute( cpi.getId(), cpi.getType().getCode(), cpi.getValue().toString() ) );
    }
    column.setAttributes(attributes.toArray( new Attribute[attributes.size()]));
    return column;
  }
  
  public static Set<String> getBackingTableNames(Collection<Column> columns) {
    Set<String> tableNames = new HashSet<String>();
    for(Column col : columns) {
      tableNames.add(col.getPhysicalTableName());
    }
    return tableNames;
  }
  
  /**
   * Creates a Attribute object from an id, a type, and a value
   * @param id
   * @param type
   * @param value
   * @return
   */
  public static Attribute createAttribute( String id, String type, String value ) {
    Attribute attribute = new Attribute();
    attribute.setId( id );
    attribute.setType( type );
    attribute.setValue( value );
    return attribute;
  }
  
}
