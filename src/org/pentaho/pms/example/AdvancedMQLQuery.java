package org.pentaho.pms.example;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.mql.OrderBy;
import org.pentaho.pms.mql.PMSFormula;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.mql.WhereCondition;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class shows an example of extending MQLQuery's functionality. While the
 * implementation isn't 100% complete, it demonstrates extending various parts
 * of the MQL XML Serialization / Deserialization, along with algorithm
 * extensions in the AdvancedSQLGenerator class.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public class AdvancedMQLQuery extends MQLQueryImpl
{

	private static final Log logger = LogFactory.getLog(AdvancedMQLQuery.class);

	private AdvancedSQLGenerator advancedSQLGenerator = new AdvancedSQLGenerator();
  
	/**
	 * an example that extends Selection with alias capabilities
	 */
	public static class AliasedSelection extends Selection {
		String alias;
    String formula;
    PMSFormula pmsformula;
    Boolean hasAgg;

    public String toString() {
      return "[bc=" + getBusinessColumn() + "; alias="+ alias + "; formula="+formula+ "]";
    }
    
		public AliasedSelection(BusinessColumn column, String alias) {
			super(column);
			this.alias = alias;
		}
    
    public AliasedSelection(String formula) throws PentahoMetadataException {
      super(null);
      this.formula = formula;
    }

		public String getAlias() {
			return alias;
		}
    
    public String getFormula() {
      return formula;
    }

    public void initPMSFormula(BusinessModel model, DatabaseMeta databaseMeta, List<Selection> selections) throws PentahoMetadataException{
      pmsformula = new AliasAwarePMSFormula(model, databaseMeta, formula, selections, AdvancedSQLGenerator.DEFAULT_ALIAS); // formula;
      pmsformula.setAllowAggregateFunctions(true);
      pmsformula.parseAndValidate();
    }
    
    /**
     * traverse the field list and see if any of the fields are aggregate
     * fields. we cache hasAgg for future calls
     * 
     * @return true if aggregate
     */
    public boolean hasAggregate() {
      if (hasAgg == null) {
        hasAgg = false;
        if (getBusinessColumn() != null) {
          hasAgg = getBusinessColumn().hasAggregate();
          return hasAgg;
        } else {
        
          Iterator iter = pmsformula.getBusinessColumns().iterator();
          while (iter.hasNext()) {
            BusinessColumn col = (BusinessColumn) iter.next();
            if (col.hasAggregate()) {
              hasAgg = true;
              return hasAgg;
            }
          }
          
          // the formula may also define an aggregate function... 
          // we need to see if an agg function is defined
          if (pmsformula.hasAggregateFunction()) {
            hasAgg = true;
            return hasAgg;
          }
        }
      }
      return hasAgg;
    }
    
    public PMSFormula getPMSFormula() {
      return pmsformula;
    }
    
    public boolean hasFormula() {
      return formula != null;
    }
    
		public boolean equals(Object obj) {
			AliasedSelection abc = (AliasedSelection) obj;
			return equals(abc.getBusinessColumn(), getBusinessColumn()) && 
             equals(alias, abc.getAlias()) &&
             equals(formula, abc.getFormula());
		}

		public boolean equals(Object a, Object b) {
			return (a == null && b == null) || ((a != null && b != null) && a.equals(b));
		}
	}

  public AdvancedMQLQuery(String XML, DatabaseMeta databaseMeta, String locale, CwmSchemaFactoryInterface factory)
  throws PentahoMetadataException
  {
    super(XML, databaseMeta, locale, factory);
  }
  
	public AdvancedMQLQuery(String XML, String locale, CwmSchemaFactoryInterface factory)
			throws PentahoMetadataException
	{
		super(XML, null, locale, factory);
	}

	public AdvancedMQLQuery(SchemaMeta schemaMeta, BusinessModel model, DatabaseMeta databaseMeta,
			String locale)
	{
		super(schemaMeta, model, databaseMeta, locale);
	}

  public void addSelection(AliasedSelection selection) {
    if (!selection.hasFormula() && selection.getBusinessColumn() == null) {
      throw new RuntimeException("Error adding selection, no formula or business column specified");
    }
    super.addSelection(selection);
  }
  
	/**
	 * overridden method allowing more advanced selection functionality
	 */
	protected void addSelectionFromXmlNode(Element selectionElement) {
    String column = null;
    String alias = null;
    String formula = null;
    
		NodeList nodes = selectionElement.getElementsByTagName("column"); //$NON-NLS-1$
		if (nodes.getLength() > 0) {
			column = XMLHandler.getNodeValue(nodes.item(0));
      if ((column != null) && (column.trim().length() == 0)) {
        column = null;
      }
    }
    
    nodes = selectionElement.getElementsByTagName("alias"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      alias = XMLHandler.getNodeValue(nodes.item(0));
      if ((alias != null) && (alias.trim().length() == 0)) {
        alias = null;
      }
    }
    
    nodes = selectionElement.getElementsByTagName("formula"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      formula = XMLHandler.getNodeValue(nodes.item(0));
      if ((formula != null) && (formula.trim().length() == 0)) {
        formula = null;
      }
    }
    	
    if (column != null) {
  		BusinessColumn businessColumn = getModel().findBusinessColumn(column);
			if (businessColumn != null) {
				addSelection(new AliasedSelection(businessColumn, alias));
			} else {
        throw new RuntimeException("Failed to find business column '" + column + "' in model.");
      }
		} else if (formula != null) {
      try {
        addSelection(new AliasedSelection(formula));
      } catch (PentahoMetadataException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Failed to parse selection, no column or formula provided");      
    }
	}

  protected void addSelectionToDocument(Document doc, Selection selection, Element selectionElement) {
    AliasedSelection aliasedSelection = (AliasedSelection)selection;
    BusinessColumn column = selection.getBusinessColumn();
    Element element = doc.createElement("view"); //$NON-NLS-1$

    // element.appendChild( doc.createTextNode( column.getBusinessTable().getId() ) );
    //
    // Work-around for PMD-93 - not using BusinessView in the MQL.
    if (column != null) {
      BusinessCategory rootCat = getModel().getRootCategory();
      BusinessCategory businessCategory = rootCat.findBusinessCategoryForBusinessColumn(column);
      element.appendChild(doc.createTextNode(businessCategory.getId()));
  
      selectionElement.appendChild(element);
  
      element = doc.createElement("column"); //$NON-NLS-1$
      element.appendChild(doc.createTextNode(column.getId()));
      selectionElement.appendChild(element);

      if (aliasedSelection.getAlias() != null) {
        element = doc.createElement("alias"); //$NON-NLS-1$
        element.appendChild(doc.createTextNode(aliasedSelection.getAlias()));
        selectionElement.appendChild(element);
      }

    } else if (aliasedSelection.getFormula() != null) {
      element = doc.createElement("formula"); //$NON-NLS-1$
      element.appendChild(doc.createTextNode(aliasedSelection.getFormula()));
      selectionElement.appendChild(element);
    }
  }

  protected void addOrderByFromXmlNode(Element orderElement) throws PentahoMetadataException {
    boolean ascending = true;
    String view = null;
    String column = null;
    String formula = null;
    String alias = null;

    NodeList nodes = orderElement.getElementsByTagName("direction"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      ascending = XMLHandler.getNodeValue(nodes.item(0)).equals("asc"); //$NON-NLS-1$
    }
    nodes = orderElement.getElementsByTagName("view"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      view = XMLHandler.getNodeValue(nodes.item(0));
    }
    nodes = orderElement.getElementsByTagName("column"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      column = XMLHandler.getNodeValue(nodes.item(0));
    }
    nodes = orderElement.getElementsByTagName("alias"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      alias = XMLHandler.getNodeValue(nodes.item(0));
    }
    nodes = orderElement.getElementsByTagName("formula"); //$NON-NLS-1$
    if (nodes.getLength() > 0) {
      formula = XMLHandler.getNodeValue(nodes.item(0));
    }
    if (view != null && column != null) {
      BusinessCategory rootCat = getModel().getRootCategory();
      BusinessCategory businessCategory = rootCat.findBusinessCategory(view);
      if (businessCategory == null) {
        throw new PentahoMetadataException(Messages.getErrorString(
            "MQLQuery.ERROR_0014_BUSINESS_CATEGORY_NOT_FOUND", view)); //$NON-NLS-1$ 
      }
      BusinessColumn businessColumn = businessCategory.findBusinessColumn(column);
      if (businessColumn == null) {
        throw new PentahoMetadataException(Messages.getErrorString(
            "MQLQuery.ERROR_0016_BUSINESS_COLUMN_NOT_FOUND", businessCategory.getId(), column)); //$NON-NLS-1$ 
      }
      getOrder().add(new OrderBy(new AliasedSelection(businessColumn, alias), ascending));
    } else if (formula != null) {
      getOrder().add(new OrderBy(new AliasedSelection(formula), ascending));
    } else {
      throw new PentahoMetadataException("no column or formula specified"); //$NON-NLS-1$ 
    }
  }
  
  protected void addOrderByToDocument(Document doc, OrderBy orderBy, Element orderElement) {
    Element element = doc.createElement("direction"); //$NON-NLS-1$
    element.appendChild(doc.createTextNode(orderBy.isAscending() ? "asc" : "desc")); //$NON-NLS-1$ //$NON-NLS-2$
    orderElement.appendChild(element);

    // Work-around for PMD-93 - Need this to be better into the future...
    AliasedSelection selection = (AliasedSelection)orderBy.getSelection();
    if (!selection.hasFormula()) {
      element = doc.createElement("view"); //$NON-NLS-1$
      BusinessCategory rootCat = getModel().getRootCategory();
      BusinessCategory businessView = rootCat.findBusinessCategoryForBusinessColumn(selection.getBusinessColumn());
      element.appendChild(doc.createTextNode(businessView.getId()));
  
      orderElement.appendChild(element);
      element = doc.createElement("column"); //$NON-NLS-1$
      element.appendChild(doc.createTextNode(selection.getBusinessColumn().getId()));
      orderElement.appendChild(element);
      
      if (selection.getAlias() != null) {
        orderElement.appendChild(element);
        element = doc.createElement("alias"); //$NON-NLS-1$
        element.appendChild(doc.createTextNode(selection.getAlias()));
        orderElement.appendChild(element);
      }
    } else {
      orderElement.appendChild(element);
      element = doc.createElement("formula"); //$NON-NLS-1$
      element.appendChild(doc.createTextNode(selection.getFormula()));
      orderElement.appendChild(element);
    }
  }
  
  
	/**
   * override addConstraint to use own where condition
   * 
   * @param operator operator to separate constraints with
   * @param condition MQL Function which defines the constraint
	 */ 
	public void addConstraint(String operator, String condition) throws PentahoMetadataException
	{
		List<Selection> aliases = super.getSelections();
		AliasAwarePMSFormula formula = new AliasAwarePMSFormula(getModel(), getDatabaseMeta(), condition,
				aliases, AdvancedSQLGenerator.DEFAULT_ALIAS);
		formula.setAllowAggregateFunctions(true);
		WhereCondition where = new WhereCondition(formula, operator, condition);
		getConstraints().add(where);
	}
	
  public void addOrderBy(AliasedSelection selection,  boolean ascending) {
    getOrder().add(new OrderBy(selection, ascending));
  }
	
	public MappedQuery getQuery() throws PentahoMetadataException
	{
		return advancedSQLGenerator.getQuery(getModel(), getSelections(), getConstraints(), getOrder(), getDatabaseMeta(),
				getDisableDistinct(), getLocale());
	}

}
