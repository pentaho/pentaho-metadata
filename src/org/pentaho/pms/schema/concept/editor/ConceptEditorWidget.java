package org.pentaho.pms.schema.concept.editor;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.pentaho.pms.schema.security.SecurityReference;

/**
 * Given an <code>IConceptModel</code> instance, this graphical control provides a user interface for modifying the
 * properties of the concept wrapped by the model.
 * @author mlowery
 */
public class ConceptEditorWidget extends Composite {


  public ConceptEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final Map context, SecurityReference securityReference) {
    super(parent, style);
    setLayout(new FillLayout());

    Group group = new Group(this, SWT.SHADOW_OUT);
    group.setText("Properties");
    group.setLayout(new FillLayout());
    SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
    s0.SASH_WIDTH = 10;
    PropertyNavigationWidget propertyNavigationWidget = new PropertyNavigationWidget(s0, SWT.NONE);
    propertyNavigationWidget.setConceptModel(conceptModel);
    PropertyWidgetManager2 propertyWidgetManager = new PropertyWidgetManager2(s0, SWT.NONE, context, securityReference);
    propertyWidgetManager.setConceptModel(conceptModel);
    propertyNavigationWidget.addSelectionChangedListener(propertyWidgetManager);
    s0.setWeights(new int[] { 1, 2 });
  }
}
