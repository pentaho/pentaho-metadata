package org.pentaho.pms.schema.concept.editor;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/**
 * This widget is used to allow the users to enter dates.
 * If you want to create properties like "Creation date", etc.
 * 
 * @author mlowery
 */
public class DatePropertyEditorWidget extends AbstractPropertyEditorWidget {

  public DatePropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
  }

  protected void createContents(final Composite parent) {

    DateTime calendar = new DateTime(parent, SWT.CALENDAR);

    DateTime time = new DateTime(parent, SWT.TIME);

  }

  public Object getValue() {
    // TODO Auto-generated method stub
    return null;
  }

  protected boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }

  protected void setValue(final Object value) {
  }
  
  public void refresh() {
    refreshOverrideButton();
    setValue(getProperty().getValue());
  }

  public void cleanup() {
  }
}
