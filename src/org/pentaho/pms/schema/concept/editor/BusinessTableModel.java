package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class BusinessTableModel extends AbstractTableModel {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(BusinessTableModel.class);

  // ~ Instance fields =================================================================================================

  private BusinessTable table;

  private PhysicalTable physicalTable;

  // ~ Constructors ====================================================================================================

  public BusinessTableModel(final BusinessTable table) {
    this(table, null);
  }

  public BusinessTableModel(final BusinessTable table, final PhysicalTable physicalTable) {
    super();
    this.table = table;
    setParent(physicalTable);
  }

  // ~ Methods =========================================================================================================

  public void addAllColumns(final ConceptUtilityInterface[] columns) throws ObjectAlreadyExistsException {
    // TODO mlowery should make this rollback on exception
    for (int i = 0; i < columns.length; i++) {
      table.addBusinessColumn((BusinessColumn) columns[i]);
      fireTableModificationEvent(createAddColumnEvent(columns[i].getId()));
    }
  }

  /**
   * Here the id is the name of the physical column on which to base the new business column.
   */
  public void addColumn(final String id, final String localeCode) throws ObjectAlreadyExistsException {
    if (id != null) {
      PhysicalColumn physicalColumn = physicalTable.findPhysicalColumn(localeCode, id);
      String newBusinessColumnId = BusinessColumn.proposeId(localeCode, table, physicalColumn);
      BusinessColumn businessColumn = new BusinessColumn(newBusinessColumnId, physicalColumn, table);
      table.addBusinessColumn(businessColumn);
      fireTableModificationEvent(createAddColumnEvent(newBusinessColumnId));
    }
  }

  public ConceptUtilityInterface[] getColumns() {
    return (ConceptUtilityInterface[]) table.getBusinessColumns().toArray(new ConceptUtilityInterface[0]);
  }

  public ConceptInterface getConcept() {
    return table.getConcept();
  }

  public String getId() {
    return table.getId();
  }

  public ConceptUtilityInterface getWrappedTable() {
    return table;
  }

  public boolean isColumn(final ConceptUtilityInterface column) {
    return column instanceof BusinessColumn;
  }

  public void removeAllColumns() {
    String[] ids = table.getColumnIDs();
    for (int i = 0; i < ids.length; i++) {
      removeColumn(ids[i]);
    }
  }

  public void removeColumn(final String id) {
    BusinessColumn column = table.findBusinessColumn(id);
    if (null != column) {
      int index = table.indexOfBusinessColumn(column);
      table.removeBusinessColumn(index);
      fireTableModificationEvent(createRemoveColumnEvent(id));
    }
  }

  public void setParent(final ConceptUtilityInterface parent) {
    if (null == parent) {
      return;
    }
    if (parent instanceof PhysicalTable) {
      physicalTable = (PhysicalTable) parent;
      table.setPhysicalTable(physicalTable);
      if (logger.isDebugEnabled()) {
        logger.debug("set parent table to " + physicalTable);
      }
    } else {
      throw new IllegalArgumentException("argument can only be instance of PhysicalTable");
    }
  }

  public Object clone() throws CloneNotSupportedException {
    return new BusinessTableModel((BusinessTable) table.clone(), (PhysicalTable) physicalTable.clone());
  }

  public ITableModel getParentAsTableModel() {
    if (null != physicalTable) {
      return new PhysicalTableModel(physicalTable);
    } else {
      return null;
    }
  }

  public ConceptUtilityInterface getParent() {
    return physicalTable;
  }
}
