package org.pentaho.pms.schema.concept.editor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.util.ObjectAlreadyExistsException;


public class ConceptEditorDialog extends Dialog {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptEditorDialog.class);

  // ~ Instance fields =================================================================================================

  protected String activeLocale;

  protected Composite detailsComposite;

  protected StackLayout stackLayout;

  protected Composite cardComposite;

  protected Map propertyEditorContext = new HashMap();

  protected ConceptModelRegistry conceptModelRegistry = new ConceptModelRegistry();

  protected Map<ConceptInterface,Composite> cards = new HashMap<ConceptInterface,Composite>();

  protected Control defaultCard;

  private Text conceptNameField;

  private IConceptTreeModel conceptTreeModel;

  private ISelectionChangedListener conceptTreeSelectionChangedListener;

  private ConceptTreeWidget conceptTree;
  
  // ~ Constructors ====================================================================================================

  public ConceptEditorDialog(final Shell parent, final IConceptTreeModel conceptTreeModel) {
    super(parent);
    this.conceptTreeModel = conceptTreeModel;
  }

  // ~ Methods =========================================================================================================

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Concept Editor");
  }

  protected Point getInitialSize() {
    return new Point(1000, 800);
  }

  protected final Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new FormLayout());
    GridData gdContainer = new GridData(GridData.FILL_BOTH);
    container.setLayoutData(gdContainer);

    SashForm s0 = new SashForm(container, SWT.HORIZONTAL);
    FormData fdSash = new FormData();
    fdSash.left = new FormAttachment(0, 0);
    fdSash.top = new FormAttachment(0, 0);
    fdSash.right = new FormAttachment(100, 0);
    fdSash.bottom = new FormAttachment(100, 0);
    s0.setLayoutData(fdSash);

    Composite c12 = new Composite(s0, SWT.NONE);
    c12.setLayout(new FormLayout());

        Composite placeholderComposite = new Composite(c12, SWT.NONE);
        FormData fdDetailsComposite = new FormData();
        fdDetailsComposite.top = new FormAttachment(0, 0);
        fdDetailsComposite.left = new FormAttachment(0, 0);
        fdDetailsComposite.right = new FormAttachment(100, -5);
        fdDetailsComposite.bottom = new FormAttachment(100, 0);
        placeholderComposite.setLayoutData(fdDetailsComposite);

        placeholderComposite.setLayout(new FormLayout());


    conceptTree = new ConceptTreeWidget(placeholderComposite, SWT.NONE, conceptTreeModel, true);

    FormData fdlList = new FormData();
    fdlList.left = new FormAttachment(0, 0);
    fdlList.top = new FormAttachment(0, 0);
    fdlList.right = new FormAttachment(100, 0);
    fdlList.bottom = new FormAttachment(100, 0);
    conceptTree.setLayoutData(fdlList);

    conceptTreeSelectionChangedListener = new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent e) {
        if (logger.isDebugEnabled()) {
          logger.debug("heard conceptTree selection changed event: " + e);
          logger.debug("attempting to swap cards");
        }
        if (!e.getSelection().isEmpty()) {
          TreeSelection treeSel = (TreeSelection) e.getSelection();
          if (treeSel.getFirstElement() instanceof ConceptInterface) {
            ConceptInterface cu = (ConceptInterface) treeSel.getFirstElement();
            //                if (tableModel.isColumn(cu)) {
            //                  delButton.setEnabled(true);
            //                } else {
            //                  delButton.setEnabled(false);
            //                }
            swapCard(cu);
          } else {
            swapCard(null);
          }
        }
      }
    };

    conceptTree.addSelectionChangedListener(conceptTreeSelectionChangedListener);

    Composite spacer = new Composite(s0, SWT.NONE);
    spacer.setLayout(new FormLayout());

    cardComposite = new Composite(spacer, SWT.NONE);

    FormData fdCardComposite = new FormData();
    fdCardComposite.top = new FormAttachment(0, 0);
    fdCardComposite.left = new FormAttachment(0, 5);
    fdCardComposite.right = new FormAttachment(100, 0);
    fdCardComposite.bottom = new FormAttachment(100, 0);
    cardComposite.setLayoutData(fdCardComposite);

    stackLayout = new StackLayout();
    cardComposite.setLayout(stackLayout);

    defaultCard = new DefaultCard(cardComposite, SWT.NONE);

    swapCard(null);

    s0.setWeights(new int[] { 1, 3 });

    return c0;
  }

  //  protected Control createTop(final Composite parent) {
  //    Composite c0 = new Composite(parent, SWT.NONE);
  //    c0.setLayout(new FormLayout());
  //
  //    Label wlId = new Label(c0, SWT.RIGHT);
  //    wlId.setText(Messages.getString("PhysicalTableDialog.USER_NAME_ID")); //$NON-NLS-1$
  //    conceptNameField = new Text(c0, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
  //
  //    FormData fdlId = new FormData();
  //    fdlId.left = new FormAttachment(0, 0);
  //
  //    fdlId.top = new FormAttachment(conceptNameField, 0, SWT.CENTER);
  //    wlId.setLayoutData(fdlId);
  //
  //    FormData fdId = new FormData();
  //    fdId.left = new FormAttachment(wlId, 10);
  //    fdId.top = new FormAttachment(0, 0);
  //    fdId.right = new FormAttachment(100, 0);
  //    conceptNameField.setLayoutData(fdId);
  //
  //    if (conceptUtil.getId() != null) {
  //      conceptNameField.setText(conceptUtil.getId());
  //      conceptNameField.selectAll();
  //    }
  //    return c0;
  //  }

  protected void okPressed() {
    try {
      conceptTreeModel.save();
    } catch (ObjectAlreadyExistsException e) {
      if (logger.isErrorEnabled()) {
      	logger.error("an exception occurred", e);
      }
      MessageDialog.openError(getShell(), "Error", "There was an error during save.");

    }
    cleanup();
    super.okPressed();
  }

  protected void cleanup() {
    conceptTree.removeSelectionChangedListener(conceptTreeSelectionChangedListener);
  }

  private void swapCard(final ConceptInterface concept) {
    if (null == concept) {
      stackLayout.topControl = defaultCard;
    } else {
      if (null == cards.get(concept)) {
        IConceptModel conceptModel = conceptModelRegistry.getConceptModel(concept);
        
        Composite conceptEditor = new Composite(cardComposite, SWT.NONE);
        conceptEditor.setLayout(new FillLayout());

        Group group = new Group(conceptEditor, SWT.SHADOW_OUT);
        group.setText("Properties");
        group.setLayout(new FillLayout());
        SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
        s0.SASH_WIDTH = 10;
        PropertyNavigationWidget propertyNavigationWidget = new PropertyNavigationWidget(s0, SWT.NONE);
        propertyNavigationWidget.setConceptModel(conceptModel);
        PropertyWidgetManager2 propertyWidgetManager = new PropertyWidgetManager2(s0, SWT.NONE, propertyEditorContext, conceptTreeModel.getSchemaMeta().getSecurityReference());
        propertyWidgetManager.setConceptModel(conceptModel);
        propertyNavigationWidget.addSelectionChangedListener(propertyWidgetManager);
        s0.setWeights(new int[] { 1, 2 });
        cards.put(concept, conceptEditor);
      }
      stackLayout.topControl = (Control) cards.get(concept);
    }
    cardComposite.layout();
  }

  protected void cancelPressed() {
    cleanup();
    super.cancelPressed();
  }

  /**
   * The card that shows when there is no selection in the concept selection tree.
   */
  private class DefaultCard extends Composite {

    public DefaultCard(final Composite parent, final int style) {
      super(parent, style);
      createContents();
    }

    private void createContents() {
      setLayout(new GridLayout());
      Label lab0 = new Label(this, SWT.CENTER);
      lab0.setText("Select a concept to begin editing properties.");
      GridData gd = new GridData();
      gd.verticalAlignment = GridData.CENTER;
      gd.horizontalAlignment = GridData.CENTER;
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      lab0.setLayoutData(gd);
    }

  }

}
