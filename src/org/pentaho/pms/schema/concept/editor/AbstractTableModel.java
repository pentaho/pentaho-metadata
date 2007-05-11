package org.pentaho.pms.schema.concept.editor;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractTableModel implements ITableModel {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(AbstractTableModel.class);

  // ~ Instance fields =================================================================================================

  private EventSupport eventSupport = new EventSupport();

  // ~ Constructors ====================================================================================================

  public AbstractTableModel() {
    super();
  }

  // ~ Methods =========================================================================================================

  protected void fireTableModificationEvent(final TableModificationEvent e) {
    for (Iterator iter = eventSupport.getListeners().iterator(); iter.hasNext();) {
      ITableModificationListener target = (ITableModificationListener) iter.next();
      target.tableModified(e);
    }
  }

  protected TableModificationEvent createAddColumnEvent(final String id) {
    return new TableModificationEvent(this, id, TableModificationEvent.ADD_COLUMN);
  }

  protected TableModificationEvent createRemoveColumnEvent(final String id) {
    return new TableModificationEvent(this, id, TableModificationEvent.REMOVE_COLUMN);
  }

  public void addTableModificationListener(ITableModificationListener tableModelListener) {
    eventSupport.addListener(tableModelListener);
  }

  public void removeTableModificationListener(ITableModificationListener tableModelListener) {
    eventSupport.removeListener(tableModelListener);
  }
}
