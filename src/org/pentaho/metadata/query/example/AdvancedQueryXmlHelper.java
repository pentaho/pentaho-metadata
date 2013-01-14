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
package org.pentaho.metadata.query.example;

import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.Order.Type;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.metadata.messages.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is an example of extending the metadata query model.
 * 
 * @author Will Gorman (wgorman@penthao.com)
 *
 */
public class AdvancedQueryXmlHelper extends QueryXmlHelper {

  /**
   * overridden method allowing more advanced selection functionality
   */
  @Override
  protected void addSelectionFromXmlNode(Query query, Element selectionElement) {
    String view = null;
    String column = null;
    String alias = null;
    String formula = null;
    
    NodeList viewnodes = selectionElement.getElementsByTagName("view"); //$NON-NLS-1$
    if (viewnodes.getLength() > 0) {
      view = XMLHandler.getNodeValue(viewnodes.item(0));
      if ((view != null) && (view.trim().length() == 0)) {
        view = null;
      }
    }
    
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
      
    if (view != null && column != null) {
      Category category = query.getLogicalModel().findCategory(view);
      LogicalColumn businessColumn = category.findLogicalColumn(column);
      if (businessColumn != null) {
        query.getSelections().add(new AliasedSelection(category, businessColumn, null, alias));
      } else {
        throw new RuntimeException("Failed to find business column '" + column + "' in model.");
      }
    } else if (formula != null) {
      try {
        query.getSelections().add(new AliasedSelection(formula));
      } catch (PentahoMetadataException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Failed to parse selection, no column or formula provided");      
    }
  }
  
  @Override
  protected void addSelectionToDocument(Document doc, Selection selection, Element selectionElement) {
    AliasedSelection aliasedSelection = (AliasedSelection)selection;
    Category view = selection.getCategory();
    LogicalColumn column = selection.getLogicalColumn();
    Element element = null;

    if (view != null && column != null) {
      element = doc.createElement("view"); //$NON-NLS-1$
      element.appendChild(doc.createTextNode(view.getId()));
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

  @Override
  protected void addOrderByFromXmlNode(Query query, Element orderElement) throws PentahoMetadataException {
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
      Category category = query.getLogicalModel().findCategory(view);

      if (category == null) {
        throw new PentahoMetadataException(Messages.getErrorString(
            "QueryXmlHelper.ERROR_0015_BUSINESS_CATEGORY_NOT_FOUND", view)); //$NON-NLS-1$ 
      }
      LogicalColumn businessColumn = category.findLogicalColumn(column);
      if (businessColumn == null) {
        throw new PentahoMetadataException(Messages.getErrorString(
            "QueryXmlHelper.ERROR_0013_BUSINESS_COL_NOT_FOUND", category.getId(), column)); //$NON-NLS-1$ 
      }
      query.getOrders().add(new Order(new AliasedSelection(category, businessColumn, null, alias), ascending ? Type.ASC : Type.DESC));
    } else if (formula != null) {
      query.getOrders().add(new Order(new AliasedSelection(formula), ascending ? Type.ASC : Type.DESC));
    } else {
      throw new PentahoMetadataException("no column or formula specified"); //$NON-NLS-1$ 
    }
  }
  
  @Override
  protected void addOrderByToDocument(Document doc, Order orderBy, Element orderElement) {
    Element element = doc.createElement("direction"); //$NON-NLS-1$
    element.appendChild(doc.createTextNode(orderBy.getType().toString().toLowerCase())); //$NON-NLS-1$ //$NON-NLS-2$
    orderElement.appendChild(element);

    // Work-around for PMD-93 - Need this to be better into the future...
    AliasedSelection selection = (AliasedSelection)orderBy.getSelection();
    if (!selection.hasFormula()) {
      element = doc.createElement("view"); //$NON-NLS-1$
      element.appendChild(doc.createTextNode(selection.getCategory().getId()));
  
      orderElement.appendChild(element);
      element = doc.createElement("column"); //$NON-NLS-1$
      element.appendChild(doc.createTextNode(selection.getLogicalColumn().getId()));
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
}
