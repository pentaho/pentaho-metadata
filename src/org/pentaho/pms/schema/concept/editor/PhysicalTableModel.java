package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;

public class PhysicalTableModel extends AbstractTableModel implements Cloneable {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PhysicalTableModel.class);

  // ~ Instance fields =================================================================================================

  private PhysicalTable table;

  // ~ Constructors ====================================================================================================

  public PhysicalTableModel(final PhysicalTable table) {
    super();
    this.table = table;
  }

  // ~ Methods =========================================================================================================

  public void addColumn(final String id, final String localeCode) throws ObjectAlreadyExistsException {
    PhysicalColumn physicalColumn = new PhysicalColumn(id);
    physicalColumn.setTable(table);

    String name = id;
    if (name.startsWith(getColumnIdPrefix())) {
      name = name.substring(getColumnIdPrefix().length());
    }
    name = Const.fromID(name);
    physicalColumn.setName(localeCode, name);
    table.addPhysicalColumn(physicalColumn);
    fireTableModificationEvent(createAddColumnEvent(id));
  }

  private String getColumnIdPrefix() {
    String columnIdPrefix = Settings.getPhysicalColumnIDPrefix();
    if (Settings.isAnIdUppercase()) {
      columnIdPrefix = columnIdPrefix.toUpperCase();
    }
    return columnIdPrefix;
  }

  public ConceptInterface getConcept() {
    return table.getConcept();
  }

  public String getId() {
    return table.getId();
  }

  public void removeColumn(final String id) {
    PhysicalColumn column = table.findPhysicalColumn(id);
    if (null != column) {
      int index = table.indexOfPhysicalColumn(column);
      table.removePhysicalColumn(index);
      fireTableModificationEvent(createRemoveColumnEvent(id));
    }
  }

  public ConceptUtilityInterface[] getColumns() {
    return (ConceptUtilityInterface[]) table.getPhysicalColumns().toArray(new ConceptUtilityInterface[0]);
  }

  public ConceptUtilityInterface getWrappedTable() {
    return table;
  }

  public boolean isColumn(final ConceptUtilityInterface column) {
    return column instanceof PhysicalColumn;
  }

  public void addAllColumns(final ConceptUtilityInterface[] columns) throws ObjectAlreadyExistsException {
    // TODO mlowery should make this rollback on exception
    for (int i = 0; i < columns.length; i++) {
      table.addPhysicalColumn((PhysicalColumn) columns[i]);
    }
  }

  public void removeAllColumns() {
    table.removeAllPhysicalColumns();
  }

  public Object clone() throws CloneNotSupportedException {
    return new PhysicalTableModel((PhysicalTable) table.clone());
  }

}
