package org.pentaho.pms;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.editor.ConceptModel;
import org.pentaho.pms.schema.concept.editor.PropertyListWidget;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;

public class ConceptEditorTestApp extends ApplicationWindow {

  public ConceptEditorTestApp() {
    super(null);
  }

  public void run() {
    setBlockOnOpen(true);
    open();
    Display.getCurrent().dispose();
  }

  protected Control createContents(final Composite parent) {
    ConceptModel c = new ConceptModel(new Concept());
    PropertyListWidget widget = new PropertyListWidget(parent, SWT.NONE, c);
    c.setProperty(new ConceptPropertyString(DefaultPropertyID.NAME.getId(), "mofongo"));
    c.setProperty(new ConceptPropertyString(DefaultPropertyID.DESCRIPTION.getId(), "pollo"));
    return widget;
  }

  public static void main(final String[] args) {
    new ConceptEditorTestApp().run();
  }
}
