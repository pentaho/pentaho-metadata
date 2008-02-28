package org.pentaho.pms.example;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.mql.WhereCondition;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
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
	public static class AliasedSelection extends Selection
	{
		String alias;

		public AliasedSelection(BusinessColumn column, String alias)
		{
			super(column);
			this.alias = alias;
		}

		public String getAlias()
		{
			return alias;
		}

		public boolean equals(Object obj)
		{
			AliasedSelection abc = (AliasedSelection) obj;
			return abc.getBusinessColumn().equals(getBusinessColumn()) && equals(alias, abc.getAlias());
		}

		public boolean equals(String a, String b)
		{
			return (a == null && b == null) || ((a != null && b != null) && a.equals(b));
		}
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

	/**
	 * overridden method allowing more advanced selection functionality
	 */
	protected void addSelectionFromXmlNode(Element selectionElement)
	{
		NodeList nodes = selectionElement.getElementsByTagName("column"); //$NON-NLS-1$
		if (nodes.getLength() > 0)
		{
			String columnId = XMLHandler.getNodeValue(nodes.item(0));
			String alias = selectionElement.getAttribute("alias"); //$NON-NLS-1$
			if ((alias != null) && (alias.trim().length() == 0))
			{
				alias = null;
			}
			BusinessColumn businessColumn = getModel().findBusinessColumn(columnId);
			if (businessColumn != null)
			{
				addSelection(new AliasedSelection(businessColumn, alias));
			}
		}
	}


	// override constraint to use own where condition
	public void addConstraint(String operator, String condition) throws PentahoMetadataException
	{
		List<Selection> aliases = super.getSelections();
		AliasAwarePMSFormula formula = new AliasAwarePMSFormula(getModel(), getDatabaseMeta(), condition,
				aliases);
		WhereCondition where = new WhereCondition(formula, operator, condition);
		getConstraints().add(where);
	}
	

	
	public MappedQuery getQuery() throws PentahoMetadataException
	{
		return advancedSQLGenerator.getQuery(getModel(), getSelections(), getConstraints(), getDatabaseMeta(),
				getDisableDistinct(), getLocale());
	}

}
