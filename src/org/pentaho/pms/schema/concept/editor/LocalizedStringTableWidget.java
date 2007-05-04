package org.pentaho.pms.schema.concept.editor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;

/**
 * A specialized table for holding localized string values. Automatically persists changes to the model as they occur.
 * @author mlowery
 */
public class LocalizedStringTableWidget extends Composite {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(LocalizedStringTableWidget.class);

  // ~ Instance fields =================================================================================================

  private Table table;

  private TableViewer tableViewer;

  private String[] columnNames = new String[] { "Locale", "String" };

  private Locales locales;

  private IConceptModel conceptModel;

  private String propertyId;

  // ~ Constructors ====================================================================================================

  public LocalizedStringTableWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Locales locales) {
    super(parent, style);
    this.conceptModel = conceptModel;
    this.propertyId = propertyId;
    this.locales = locales;
    createContents();
  }

  // ~ Methods =========================================================================================================

  private LocalizedStringSettings getLocalizedStringSettings() {
    return (LocalizedStringSettings) conceptModel.getEffectiveProperty(propertyId).getValue();
  }

  private List getColumnNames() {
    return Arrays.asList(columnNames);
  }

  private void createContents() {
    setLayout(new FormLayout());

    // Create the table
    createTable(this);

    // Create and setup the TableViewer
    createTableViewer();
    tableViewer.setContentProvider(new LocalizedStringTableContentProvider());
    tableViewer.setLabelProvider(new LocalizedStringTableLabelProvider());
    // The input for the table viewer is the instance of ExampleTaskList

    tableViewer.setInput(getLocalizedStringSettings());
  }

  /**
   * Create the Table
   */
  private void createTable(Composite parent) {
    int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

    table = new Table(parent, style);

    FormData fdTable = new FormData();
    fdTable.top = new FormAttachment(0, 0);
    fdTable.left = new FormAttachment(0, 0);
    fdTable.right = new FormAttachment(100, 0);
    fdTable.bottom = new FormAttachment(100, 0);
    table.setLayoutData(fdTable);

    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    TableColumn column = new TableColumn(table, SWT.LEFT, 0);
    column.setText(columnNames[0]);
    column.setWidth(100);
    // Add listener to column so tasks are sorted by description when clicked
    column.addSelectionListener(new SelectionAdapter() {

      public void widgetSelected(SelectionEvent e) {
        //        tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.LOCALE_NAME));
      }
    });

    // 3rd column with task Owner
    column = new TableColumn(table, SWT.LEFT, 1);
    column.setText(columnNames[1]);
    column.setWidth(400);
    // Add listener to column so tasks are sorted by owner when clicked
    column.addSelectionListener(new SelectionAdapter() {

      public void widgetSelected(SelectionEvent e) {
        //        tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.VALUE_FOR_PROPERTY_FOR_LOCALE));
      }
    });

  }

  /**
   * Create the TableViewer
   */
  private void createTableViewer() {

    tableViewer = new TableViewer(table);
    tableViewer.setUseHashlookup(true);

    tableViewer.setColumnProperties(columnNames);

    CellEditor[] editors = new CellEditor[columnNames.length];

    // Column 0 : Description (Free text)
    TextCellEditor textEditor0 = new TextCellEditor(table);
    //        ((Text) textEditor.getControl()).setTextLimit(60);
    editors[0] = textEditor0;

    TextCellEditor textEditor1 = new TextCellEditor(table);
    //    ((Text) textEditor.getControl()).setTextLimit(60);
    editors[1] = textEditor1;

    // Assign the cell editors to the viewer
    tableViewer.setCellEditors(editors);
    // Set the cell modifier for the viewer
    tableViewer.setCellModifier(new LocalizedStringTableCellModifier());
    // Set the default sorter for the viewer
    //    tableViewer.setSorter(new ExampleTaskSorter(LocaleSorter.LOCALE_NAME));
  }

  class LocalizedStringTableContentProvider implements IStructuredContentProvider {
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public Object[] getElements(final Object parent) {
      LocalizedStringEntry[] entries = new LocalizedStringEntry[locales.getLocaleList().size()];
      List localeList = locales.getLocaleList();
      int i = 0;
      for (Iterator iter = localeList.iterator(); iter.hasNext();) {
        LocaleInterface l = (LocaleInterface) iter.next();
        entries[i++] = new LocalizedStringEntry(l.getCode(), getLocalizedStringSettings().getString(l.getCode()));
      }
      return entries;
    }
  }

  private class LocalizedStringEntry {
    private String name;

    private String value;

    public LocalizedStringEntry(final String name, final String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }

    public boolean equals(Object obj) {
      if (obj instanceof LocalizedStringEntry == false) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      LocalizedStringEntry rhs = (LocalizedStringEntry) obj;
      return new EqualsBuilder().append(name, rhs.name).append(value, rhs.value).isEquals();
    }

    public int hashCode() {
      return new HashCodeBuilder(43, 149).append(name).append(value).toHashCode();
    }

    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(name).append(value).toString();
    }

  }

  public class LocalizedStringTableCellModifier implements ICellModifier {

    public boolean canModify(final Object element, final String property) {
      return getColumnNames().indexOf(property) == 1;
    }

    public Object getValue(final Object element, final String property) {

      // Find the index of the column
      int columnIndex = getColumnNames().indexOf(property);

      Object result = null;
      LocalizedStringEntry entry = (LocalizedStringEntry) element;

      switch (columnIndex) {
        case 0: // locale name
          result = entry.getName();
          break;
        case 1: // value for property for locale
          result = LocalizedStringTableWidget.this.getValue(entry);
          break;
        default:
          result = "";
      }
      if (logger.isDebugEnabled()) {
        logger.debug("returning \"" + result + "\"");
      }
      return result;
    }

    public void modify(final Object element, final String property, final Object value) {

      if (logger.isDebugEnabled()) {
        logger.debug("value = \"" + value + "\"");
      }

      // Find the index of the column
      int columnIndex = getColumnNames().indexOf(property);

      TableItem item = (TableItem) element;
      LocalizedStringEntry entry = (LocalizedStringEntry) item.getData();

      switch (columnIndex) {
        case 0:
          break;
        case 1:
          // TODO mlowery the result concept event shows an old value with the same value as the new value;
          // this is because the code directly accesses the mutable value of the property in the line below;
          // properties should return immutable or copies of their values
          getLocalizedStringSettings().setLocaleString(entry.getName(), (String) value);
          conceptModel.setPropertyValue(propertyId, getLocalizedStringSettings());
          tableViewer.refresh();
          break;
        default:
      }
    }
  }

  private class LocalizedStringTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    public String getColumnText(final Object element, final int columnIndex) {
      String result = "";
      LocalizedStringEntry entry = (LocalizedStringEntry) element;
      switch (columnIndex) {
        case 0:
          result = entry.getName();
          break;
        case 1:
          result = getValue(entry);
          break;
        default:
          break;
      }
      return result;
    }

    public Image getColumnImage(final Object element, final int columnIndex) {
      return null;
    }

  }

  protected String getValue(final LocalizedStringEntry entry) {
    String result = getLocalizedStringSettings().getString(entry.getName());
    return null != result ? result : "";
  }

}