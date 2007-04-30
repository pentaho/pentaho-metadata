package org.pentaho.pms.schema.concept.editor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;

public class ConceptEditorWidget extends Composite {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private PropertyNavigationWidget propertyManagementWidget;

  private PropertyWidgetManager2 propertyWidgetManager;

  private SchemaMeta schemaMeta;

  // ~ Constructors ====================================================================================================

  public ConceptEditorWidget(final Composite parent, final int style, final SchemaMeta schemaMeta, final IConceptModel conceptModel) {
    super(parent, style);
    this.schemaMeta = schemaMeta;
    this.conceptModel = conceptModel;
    createContents();
  }

  // ~ Methods =========================================================================================================

  protected void createContents() {
    setLayout(new FormLayout());

    // sash form
    SashForm s0 = new SashForm(this, SWT.HORIZONTAL);
    FormData fd0 = new FormData();
    fd0.top = new FormAttachment(0, 0);
    fd0.bottom = new FormAttachment(100, 0);
    fd0.left = new FormAttachment(0, 0);
    fd0.right = new FormAttachment(100, 0);
    s0.setLayoutData(fd0);

    // left side of sash
    Composite c1 = new Composite(s0, SWT.NONE);
    c1.setLayout(new FormLayout());

    // right side of sash
    Composite c3 = new Composite(s0, SWT.NONE);
    c3.setLayout(new FormLayout());

    // widget for left side of sash
    PropertyNavigationWidget w5 = new PropertyNavigationWidget(c1, SWT.NONE, conceptModel);
    FormData fd5 = new FormData();
    fd5.top = new FormAttachment(0, 10);
    fd5.bottom = new FormAttachment(100, -10);
    fd5.left = new FormAttachment(0, 10);
    fd5.right = new FormAttachment(100, -10);
    w5.setLayoutData(fd5);
    this.propertyManagementWidget = w5;


    // widget for right side of sash
    PropertyWidgetManager2 m7 = new PropertyWidgetManager2(c3, SWT.NONE, schemaMeta, conceptModel);
    FormData fd7 = new FormData();
    fd7.top = new FormAttachment(0, 10);
    fd7.bottom = new FormAttachment(100, -10);
    fd7.left = new FormAttachment(0, 10);
    fd7.right = new FormAttachment(100, -10);
    m7.setLayoutData(fd7);
    this.propertyWidgetManager = m7;


    propertyManagementWidget.addSelectionChangedListener(propertyWidgetManager);



    s0.setWeights(new int[] { 1, 2 });
  }
}
