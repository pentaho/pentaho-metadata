package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public class PropertyManagementWidget extends Composite {

	private static final Log logger = LogFactory
			.getLog(PropertyTreeWidget.class);

	private PropertyTreeWidget propertyTree;

	private ToolItem delButton;

	private IConceptModel conceptModel;

	public PropertyManagementWidget(final Composite parent, final int style,
			final IConceptModel conceptModel) {
		super(parent, style);
		this.conceptModel = conceptModel;
		createContents();
	}

	protected void createContents() {
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				PropertyManagementWidget.this.widgetDisposed(e);
			}
		});

		setLayout(new FormLayout());

		Label lab1 = new Label(this, SWT.NONE);
		lab1.setText("Properties");

		ToolBar tb3 = new ToolBar(this, SWT.FLAT);

		ToolItem ti4 = new ToolItem(tb3, SWT.PUSH);
		ti4.setText("ADD");
		ti4.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(final SelectionEvent e) {
				PropertyManagementWidget.this.addButtonPressed(e);
			}

			public void widgetSelected(final SelectionEvent e) {
				PropertyManagementWidget.this.addButtonPressed(e);
			}

		});
		delButton = new ToolItem(tb3, SWT.PUSH);
		delButton.setText("DEL");
		delButton.setEnabled(false);
		delButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
				PropertyManagementWidget.this.deleteButtonPressed(e);
			}

			public void widgetSelected(final SelectionEvent e) {
				PropertyManagementWidget.this.deleteButtonPressed(e);
			}
		});

		propertyTree = new PropertyTreeWidget(this, SWT.NONE, conceptModel,
				PropertyTreeWidget.SHOW_USED);

		propertyTree
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(final SelectionChangedEvent e) {
						if (e.getSelection() instanceof PropertyTreeSelection) {
							PropertyTreeSelection sel = (PropertyTreeSelection) e
									.getSelection();
							if (sel.isSection()) {
								delButton.setEnabled(false);
							} else {
								delButton.setEnabled(true);
							}
						} else {
							delButton.setEnabled(false);
						}
					}
				});

		FormData fd1 = new FormData();
		fd1.bottom = new FormAttachment(propertyTree, -10);
		fd1.left = new FormAttachment(0, 0);
		lab1.setLayoutData(fd1);

		FormData fd6 = new FormData();
		fd6.top = new FormAttachment(tb3, 10);
		fd6.left = new FormAttachment(0, 0);
		fd6.right = new FormAttachment(100, 0);
		fd6.bottom = new FormAttachment(100, 0);
		propertyTree.setLayoutData(fd6);

		FormData fd3 = new FormData();
		fd3.top = new FormAttachment(0, 0);
		// fd3.bottom = new FormAttachment(pt6, 0);
		fd3.right = new FormAttachment(100, 0);
		tb3.setLayoutData(fd3);

	}

	protected void deleteButtonPressed(final SelectionEvent e) {
		String propertyId = null;
		ISelection sel = propertyTree.getSelection();
		if (sel instanceof PropertyTreeSelection) {
			PropertyTreeSelection treeSel = (PropertyTreeSelection) sel;
			propertyId = treeSel.getName();

			// event should now fire from the model
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("unknown node selected in property tree");
			}
			return;
		}

		boolean delete = MessageDialog.openConfirm(getShell(), "Confirm",
				"Are you sure you want to remove the property ["
						+ DefaultPropertyID.findDefaultPropertyID(propertyId)
								.getDescription() + "]?");

		if (delete) {
			conceptModel.removeProperty(propertyId);
		}
	}

	protected void widgetDisposed(final DisposeEvent e) {
		// TODO Auto-generated method stub

	}

	protected void addButtonPressed(final SelectionEvent e) {
		new AddPropertyDialog(getShell(), conceptModel).open();
	}
}
