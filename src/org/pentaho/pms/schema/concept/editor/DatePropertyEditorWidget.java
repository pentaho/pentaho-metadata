package org.pentaho.pms.schema.concept.editor;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/**
 * TODO mlowery can't find where this widget is actually used
 * @author mlowery
 * @deprecated can't find where this widget is actually used
 */
public class DatePropertyEditorWidget extends AbstractPropertyEditorWidget {

  public DatePropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
  }

  protected void addModificationListeners() {
    // TODO Auto-generated method stub

  }

  protected void createContents(final Composite parent) {

    DateTime calendar = new DateTime(parent, SWT.CALENDAR);
    calendar.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        System.out.println("calendar date changed");
      }
    });

    DateTime time = new DateTime(parent, SWT.TIME);
    time.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        System.out.println("time changed");
      }
    });

  }

  protected void removeModificationListeners() {
    // TODO Auto-generated method stub

  }

  public Object getValue() {
    // TODO Auto-generated method stub
    return null;
  }

  protected boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }

}
