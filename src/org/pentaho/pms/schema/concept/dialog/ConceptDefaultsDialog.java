/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
 */

package org.pentaho.pms.schema.concept.dialog;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.DefaultProperty;
import org.pentaho.pms.schema.RequiredProperties;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregationWidget;
import org.pentaho.pms.schema.concept.types.alignment.ConceptPropertyAlignmentWidget;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBooleanWidget;
import org.pentaho.pms.schema.concept.types.color.ConceptPropertyColorWidget;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidthWidget;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataTypeWidget;
import org.pentaho.pms.schema.concept.types.date.ConceptPropertyDateWidget;
import org.pentaho.pms.schema.concept.types.fieldtype.ConceptPropertyFieldTypeWidget;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFontWidget;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedStringWidget;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumberWidget;
import org.pentaho.pms.schema.concept.types.security.ConceptPropertySecurityWidget;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyStringWidget;
import org.pentaho.pms.schema.concept.types.tabletype.ConceptPropertyTableTypeWidget;
import org.pentaho.pms.schema.concept.types.url.ConceptPropertyURLWidget;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;

/*******************************************************************************
 * Represents a business category
 * 
 * @since 30-aug-2006
 * 
 */
public class ConceptDefaultsDialog extends Dialog
{
	public static final int MIDDLE = PropsUI.getInstance().getMiddlePct();

	public static final int MARGIN = Const.MARGIN;

	private static Button wAddProperty;

	private static Button wDelProperty;

	private LogWriter log;

	private Button wOK, wCancel;

	private Listener lsOK, lsCancel;

	private Shell shell;

	private PropsUI props;

	private ConceptInterface concept;

	private RequiredProperties requiredProperties;

	private String title;

	private Class subject;

	// private ConceptUtilityInterface conceptUtility;

	private Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces;

	private Locales locales;

	private SecurityReference securityReference;

	public ConceptDefaultsDialog(Shell parent, String title, ConceptUtilityInterface conceptUtility,
			RequiredProperties requiredProperties, Locales locales, SecurityReference securityReference)
	{
		super(parent, SWT.NONE);

		this.title = title;
		this.concept = conceptUtility.getConcept();
		this.requiredProperties = requiredProperties;
		this.locales = locales;
		this.securityReference = securityReference;

		log = LogWriter.getInstance();
		props = PropsUI.getInstance();

		this.subject = conceptUtility.getClass();

		this.conceptPropertyInterfaces = new Hashtable<String,ConceptPropertyWidgetInterface>();
	}

	public RequiredProperties open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);

		log.logDebug(this.getClass().getName(), Messages.getString("General.DEBUG_OPENING_DIALOG")); //$NON-NLS-1$

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(title);

		int margin = Const.MARGIN;

		// Buttons at the bottom of the page...
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FormLayout());
		composite.setBackground(GUIResource.getInstance().getColorRed());

		getControls(
				composite,
				Messages.getString("ConceptDefaultsDialog.USER_SAMPLE_MESSAGE"), subject, requiredProperties, concept, conceptPropertyInterfaces, locales, securityReference); //$NON-NLS-1$

		FormData fdComposite = new FormData();
		fdComposite.left = new FormAttachment(0, 0);
		fdComposite.right = new FormAttachment(100, 0);
		fdComposite.top = new FormAttachment(0, 0);
		fdComposite.bottom = new FormAttachment(wOK, -margin);
		composite.setLayoutData(fdComposite);

		// Add listeners
		lsCancel = new Listener()
		{
			public void handleEvent(Event e)
			{
				cancel();
			}
		};
		lsOK = new Listener()
		{
			public void handleEvent(Event e)
			{
				ok();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter()
		{
			public void shellClosed(ShellEvent e)
			{
				cancel();
			}
		});

		shell.layout();

		WindowProperty winprop = props.getScreen(shell.getText());
		if (winprop != null) {
			winprop.setShell(shell);
		} else {
			shell.pack();
		}

		getData();

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return requiredProperties;
	}

	public static final ScrolledComposite getControls(Composite parentComposite, String message,
			ConceptInterface concept, Map<String, ConceptPropertyWidgetInterface> conceptPropertyInterfaces, Locales locales,
			SecurityReference securityReference)
	{
		return getControls(parentComposite, message, null, null, concept, conceptPropertyInterfaces, locales,
				securityReference);
	}

	public static final ScrolledComposite getControls(final Composite parentComposite, String message,
			Class subject, RequiredProperties requiredProperties, final ConceptInterface concept,
			Map<String, ConceptPropertyWidgetInterface> conceptPropertyInterfaces, Locales locales,
			SecurityReference securityReference)
	{
		return getControls(parentComposite, null, message, subject, requiredProperties, concept,
				conceptPropertyInterfaces, locales, securityReference);
	}

	public static final ScrolledComposite getControls(final Composite parentComposite,
			final ConceptUtilityInterface utilityInterface, final String message,
			final ConceptInterface concept,
			final Map<String, ConceptPropertyWidgetInterface> conceptPropertyInterfaces,
			final Locales locales, final SecurityReference securityReference)
	{
		return getControls(parentComposite, utilityInterface, message, null, null, concept,
				conceptPropertyInterfaces, locales, securityReference);
	}

	public static final ScrolledComposite getControls(final Composite parentComposite,
			final ConceptUtilityInterface utilityInterface, final String message, final Class subject,
			final RequiredProperties requiredProperties, final ConceptInterface concept,
			final Map<String, ConceptPropertyWidgetInterface> conceptPropertyInterfaces,
			final Locales locales, final SecurityReference securityReference)
	{
		PropsUI props = PropsUI.getInstance();

		parentComposite.setVisible(false);

		// Start with a clean slate
		Control[] children = parentComposite.getChildren();
		for (int i = 0; i < children.length; i++)
		{
			if (children[i] instanceof ScrolledComposite) children[i].dispose();
		}

		// And go at it again!
		ScrolledComposite scrolledComposite = new ScrolledComposite(parentComposite, SWT.V_SCROLL
				| SWT.H_SCROLL);
		scrolledComposite.setLayout(new FormLayout());

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		props.setLook(composite);
		FormLayout compLayout = new FormLayout();
		compLayout.marginWidth = Const.FORM_MARGIN;
		compLayout.marginHeight = Const.FORM_MARGIN;
		composite.setLayout(compLayout);

		// Add everything back on the composite...
		//
		Label wMessage = new Label(composite, SWT.RIGHT);
		props.setLook(wMessage);
		wMessage.setFont(GUIResource.getInstance().getFontMedium());
		wMessage.setText(message);
		FormData fdMessage = new FormData();
		fdMessage.left = new FormAttachment(0, 0);
		fdMessage.top = new FormAttachment(0, 0);
		wMessage.setLayoutData(fdMessage);
		Control lastControl = wMessage;

		// Add everything back on the composite...
		//
		String inherits = null;
		if (concept.findFirstParentConcept() != null)
		{
			inherits = Messages
					.getString(
							"ConceptDefaultsDialog.USER_PARENT_CONCEPT_IS", concept.findFirstParentConcept().getName()); //$NON-NLS-1$ 
		}

		if (concept.getInheritedInterface() != null)
		{
			ConceptInterface inherited = concept.getInheritedInterface();
			String name = inherited.getName(locales.getActiveLocale());
			if (name != null)
			{
				if (inherits != null) {
					inherits += ".   "; //$NON-NLS-1$
				} else {
					inherits = ""; //$NON-NLS-1$
				}

				inherits += Messages.getString("ConceptDefaultsDialog.USER_INHERITS_FROM", name); //$NON-NLS-1$ 
			}
		}

		if (inherits != null)
		{
			Label wInherits = new Label(composite, SWT.RIGHT);
			props.setLook(wInherits);
			wInherits.setText(inherits);
			FormData fdInherits = new FormData();
			fdInherits.left = new FormAttachment(0, 0);
			fdInherits.top = new FormAttachment(lastControl, Const.MARGIN);
			wInherits.setLayoutData(fdInherits);
			lastControl = wInherits;
		}

		Label line = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		line.setVisible(false);
		props.setLook(line);
		FormData fdLine = new FormData();
		fdLine.left = new FormAttachment(5, 0);
		fdLine.right = new FormAttachment(95, 0);
		fdLine.top = new FormAttachment(lastControl, 3 * MARGIN);
		line.setLayoutData(fdLine);
		lastControl = line;

		if (utilityInterface != null)
		{
			// Add a couple of buttons at the bottom.
			// Those will allow you to enter or delete properties
			// Delete a property
			wDelProperty = new Button(composite, SWT.PUSH);
			props.setLook(wDelProperty);
			wDelProperty.setText(Messages.getString("ConceptDefaultsDialog.USER_DELETE_PROPERTY")); //$NON-NLS-1$
			wDelProperty.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent arg0)
				{
					delProperty(parentComposite, utilityInterface, message, subject, requiredProperties,
							concept, conceptPropertyInterfaces, locales, securityReference);
				}
			});
			FormData fdDelProperty = new FormData();
			fdDelProperty.right = new FormAttachment(props.getMiddlePct(), 0);
			fdDelProperty.top = new FormAttachment(lastControl, Const.MARGIN);
			wDelProperty.setLayoutData(fdDelProperty);

			// Add a property
			wAddProperty = new Button(composite, SWT.PUSH);
			props.setLook(wAddProperty);
			wAddProperty.setText(Messages.getString("ConceptDefaultsDialog.USER_ADD_PROPERTY")); //$NON-NLS-1$
			wAddProperty.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent arg0)
				{
					addProperty(parentComposite, utilityInterface, message, subject, requiredProperties,
							concept, conceptPropertyInterfaces, locales, securityReference);
				}
			});
			FormData fdAddProperty = new FormData();
			fdAddProperty.right = new FormAttachment(wDelProperty, -Const.MARGIN);
			fdAddProperty.top = new FormAttachment(lastControl, Const.MARGIN);
			wAddProperty.setLayoutData(fdAddProperty);

			lastControl = wAddProperty;
		}

		// Slap the type composites on there...
		// What are the default properties for the concept?
		// 
		if (requiredProperties != null && subject != null)
		{
			java.util.List list = requiredProperties.getDefaultProperties(subject);
			for (int i = 0; i < list.size(); i++)
			{
				DefaultProperty defaultProperty = (DefaultProperty) list.get(i);

				// If no value is set, set it to the default value?
				// 
				boolean changed = false;
				ConceptPropertyInterface property = concept.getProperty(defaultProperty.getName());
				if (property == null)
				{
					// This way we're sure that we always have a property set.
					// Although the value of that property might still be empty.
					//
					concept.addProperty(DefaultPropertyID.getDefaultEmptyProperty(defaultProperty
							.getConceptPropertyType(), defaultProperty.getName()));
					property = concept.getProperty(defaultProperty.getName()); // it's
																				// empty
				}

				String description = defaultProperty.getDescription();

				/*
				 * Draw a horizontal line between the properties
				 * 
				 * Label line = new Label(composite, SWT.SEPARATOR |
				 * SWT.HORIZONTAL); props.setLook(line); FormData fdLine = new
				 * FormData(); fdLine.left = new FormAttachment(5, 0);
				 * fdLine.right = new FormAttachment(100, -MARGIN); fdLine.top =
				 * new FormAttachment(lastControl, MARGIN);
				 * line.setLayoutData(fdLine); lastControl = line;
				 */

				lastControl = addControl(composite, concept, description, property, lastControl,
						conceptPropertyInterfaces, locales, securityReference);

				// Set to changed if we did add a default concept...
				ConceptPropertyWidgetInterface widgetInterface = (ConceptPropertyWidgetInterface) conceptPropertyInterfaces
						.get(defaultProperty.getName());
				if (widgetInterface != null)
				{
					widgetInterface.setChanged(changed);
				}
			}
		} else
		{
			String ids[] = concept.getPropertyIDs();
			for (int i = 0; i < ids.length; i++)
			{
				// If no value is set, set it to the default value?
				// 
				boolean changed = false;
				ConceptPropertyInterface property = concept.getProperty(ids[i]);

				/*
				 * Draw a horizontal line between the properties
				 * 
				 * Label line = new Label(composite, SWT.SEPARATOR |
				 * SWT.HORIZONTAL); props.setLook(line); FormData fdLine = new
				 * FormData(); fdLine.left = new FormAttachment(5, 0);
				 * fdLine.right = new FormAttachment(100, -MARGIN); fdLine.top =
				 * new FormAttachment(lastControl, MARGIN);
				 * line.setLayoutData(fdLine); lastControl = line;
				 */

				String description = ids[i];
				// See if we can make a better description...
				// In case it's a default property we can look up a real
				// description
				//
				DefaultPropertyID defaultPropertyID = DefaultPropertyID.findDefaultPropertyID(ids[i]);
				if (defaultPropertyID != null)
				{
					description = defaultPropertyID.getDescription();
				} else
				{
					// clarify by showing the type
					description += " (" + property.getType().getDescription() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				}

				lastControl = addControl(composite, concept, description, property, lastControl,
						conceptPropertyInterfaces, locales, securityReference);

				// Set to changed if we did add a default concept...
				ConceptPropertyWidgetInterface widgetInterface = (ConceptPropertyWidgetInterface) conceptPropertyInterfaces
						.get(ids[i]);
				if (widgetInterface != null)
				{
					widgetInterface.setChanged(changed);
				}
			}
		}

		composite.pack();
		composite.layout(true, true);

		scrolledComposite.setContent(composite);
		scrolledComposite.layout();

		Rectangle bounds = composite.getBounds();

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth(bounds.width);
		scrolledComposite.setMinHeight(bounds.height);

		FormData fdComposite = new FormData();
		fdComposite.left = new FormAttachment(0, 0);
		fdComposite.right = new FormAttachment(100, 0);
		fdComposite.top = new FormAttachment(0, 0);
		fdComposite.bottom = new FormAttachment(100, 0);
		composite.setLayoutData(fdComposite);

		FormData fdScrolled = new FormData();
		fdScrolled.left = new FormAttachment(0, 0);
		fdScrolled.right = new FormAttachment(100, 0);
		fdScrolled.top = new FormAttachment(0, 0);
		fdScrolled.bottom = new FormAttachment(100, 0);
		scrolledComposite.setLayoutData(fdScrolled);

		// Not just the parent composite, re-consider the layout of the whole
		// shell
		parentComposite.layout(true, true);
		parentComposite.getParent().layout(true, true);
		parentComposite.getShell().layout(true, true);

		parentComposite.setVisible(true);

		return scrolledComposite;
	}

	private static final void addProperty(final Composite parentComposite,
			final ConceptUtilityInterface utilityInterface, final String message, final Class subject,
			final RequiredProperties requiredProperties, final ConceptInterface concept,
			final Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces, final Locales locales,
			final SecurityReference securityReference)
	{
		ConceptPropertyInterface property = NewPropertyDialog.addNewProperty(parentComposite.getShell(),
				concept);
		if (property != null)
		{
			getControls(parentComposite, utilityInterface, message, subject, requiredProperties, concept,
					conceptPropertyInterfaces, locales, securityReference);

			// Set the focus on this new property
			ConceptPropertyWidgetInterface widgetInterface = (ConceptPropertyWidgetInterface) conceptPropertyInterfaces
					.get(property.getId());
			if (widgetInterface != null)
			{
				widgetInterface.setFocus();
			}
		}
	}

	private static final void delProperty(final Composite parentComposite,
			final ConceptUtilityInterface utilityInterface, final String message, final Class subject,
			final RequiredProperties requiredProperties, final ConceptInterface concept,
			final Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces, final Locales locales,
			final SecurityReference securityReference)
	{
		if (ConceptDialog.delChildProperty(parentComposite.getShell(), concept))
		{
			getControls(parentComposite, utilityInterface, message, subject, requiredProperties, concept,
					conceptPropertyInterfaces, locales, securityReference);
		}
	}

	public static final Control addControl(Composite composite, ConceptInterface concept, String description,
			ConceptPropertyInterface property, Control previousControl,
			Map<String, ConceptPropertyWidgetInterface> conceptPropertyInterfaces, Locales locales,
			SecurityReference securityReference)
	{
		PropsUI props = PropsUI.getInstance();
		String id = property.getId();
		ConceptPropertyType type = property.getType();
		Control lastControl = null;

		final ConceptPropertyInterface childProperty = concept.getChildProperty(id);
		final ConceptPropertyInterface parentProperty = concept.getParentProperty(id);
		final ConceptPropertyInterface inheritedProperty = concept.getInheritedProperty(id);
		final ConceptPropertyInterface securityProperty = concept.getSecurityProperty(id);

		switch (type.getType())
		{
		case ConceptPropertyType.PROPERTY_TYPE_STRING:
			lastControl = ConceptPropertyStringWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_DATE:
			lastControl = ConceptPropertyDateWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_NUMBER:
			lastControl = ConceptPropertyNumberWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_COLOR:
			lastControl = ConceptPropertyColorWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_FONT:
			lastControl = ConceptPropertyFontWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_FIELDTYPE:
			lastControl = ConceptPropertyFieldTypeWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_AGGREGATION:
			lastControl = ConceptPropertyAggregationWidget.getControl(composite, concept, id,
					previousControl, conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_BOOLEAN:
			lastControl = ConceptPropertyBooleanWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_DATATYPE:
			lastControl = ConceptPropertyDataTypeWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_LOCALIZED_STRING:
			lastControl = ConceptPropertyLocalizedStringWidget.getControl(composite, concept, id,
					previousControl, conceptPropertyInterfaces, locales);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_TABLETYPE:
			lastControl = ConceptPropertyTableTypeWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_URL:
			lastControl = ConceptPropertyURLWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_SECURITY:
			lastControl = ConceptPropertySecurityWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces, securityReference);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_ALIGNMENT:
			lastControl = ConceptPropertyAlignmentWidget.getControl(composite, concept, id, previousControl,
					conceptPropertyInterfaces);
			break;
		case ConceptPropertyType.PROPERTY_TYPE_COLUMN_WIDTH:
			lastControl = ConceptPropertyColumnWidthWidget.getControl(composite, concept, id,
					previousControl, conceptPropertyInterfaces);
			break;
		default:
			break;
		}

		final Button overwrite;

		ConceptInterface parent = concept.getParentInterface();
		ConceptInterface inherit = concept.getInheritedInterface();
		ConceptInterface security = concept.getSecurityParentInterface();

		if (parent != null || inherit != null || security != null)
		{
			overwrite = new Button(composite, SWT.CHECK);
			props.setLook(overwrite);
			overwrite.setText(Messages.getString("ConceptDefaultsDialog.USER_TITLE_OVERRIDE")); //$NON-NLS-1$
			if (parent != null)
			{
				overwrite.setToolTipText(Messages.getString(
						"ConceptDefaultsDialog.USER_OVERRIDE_PARENT_CONCEPT", parent.getName())); //$NON-NLS-1$ 
			}
			if (inherit != null)
			{
				overwrite.setToolTipText(Messages
						.getString("ConceptDefaultsDialog.USER_OVERRIDE_FROM_INHERITED_CONCEPT")); //$NON-NLS-1$
			}
			if (security != null)
			{
				overwrite.setToolTipText(Messages
						.getString("ConceptDefaultsDialog.USER_OVERRIDE_FROM_INHERITED_SECURITY")); //$NON-NLS-1$
			}
			FormData fdCheckBox = new FormData();
			fdCheckBox.right = new FormAttachment(MIDDLE, 0);
			fdCheckBox.top = new FormAttachment(lastControl, 0, SWT.CENTER);
			overwrite.setLayoutData(fdCheckBox);

			final ConceptPropertyWidgetInterface dialogInterface = (ConceptPropertyWidgetInterface) conceptPropertyInterfaces
					.get(id);
			if (dialogInterface != null)
			{
				final SelectionAdapter selectionAdapter = new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent event)
					{
						dialogInterface.setEnabled(overwrite.getSelection());
						dialogInterface.setOverwrite(overwrite.getSelection());
					}
				};
				overwrite.addSelectionListener(selectionAdapter);

				// if there is no parent property for this, the overwrite is
				// always selected and the checkbox is disabled.
				if (parentProperty == null && inheritedProperty == null && securityProperty == null)
				{
					overwrite.setSelection(true);
					overwrite.setEnabled(false);
					dialogInterface.setEnabled(true);
					dialogInterface.setOverwrite(true);
				} else
				{
					// If the parent property exists and the child property too:
					// enable overwrite
					if (childProperty != null)
					{
						overwrite.setSelection(true);
						dialogInterface.setOverwrite(true);
						dialogInterface.setEnabled(true);
					} else
					{
						overwrite.setSelection(false);
						dialogInterface.setOverwrite(false);
						dialogInterface.setEnabled(false);
					}
				}
			}

		} else
		{
			overwrite = null;
		}

		Label label = new Label(composite, SWT.RIGHT);
		props.setLook(label);
		label.setText(description);
		FormData fdLabel = new FormData();
		fdLabel.left = new FormAttachment(0, 0);
		if (overwrite != null)
		{
			fdLabel.top = new FormAttachment(overwrite, 0, SWT.CENTER);
			fdLabel.right = new FormAttachment(overwrite, -3 * Const.MARGIN);
		} else
		{
			fdLabel.top = new FormAttachment(lastControl, 0, SWT.CENTER);
			fdLabel.right = new FormAttachment(MIDDLE, -3 * Const.MARGIN);
		}
		label.setLayoutData(fdLabel);

		lastControl.pack();

		return lastControl;
	}

	public void dispose()
	{
		props.setScreen(new WindowProperty(shell));
		shell.dispose();
	}

	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */
	public void getData()
	{
	}

	private void cancel()
	{
		requiredProperties = null;
		dispose();
	}

	private void ok()
	{
		boolean allOK = setPropertyValues(shell, conceptPropertyInterfaces);

		if (allOK) dispose();
	}

	public static final boolean setPropertyValues(Shell shell, Map conceptWidgetInterfaces)
	{
		// Loop over all settings and call the getValue()
		//
		boolean allOK = true;
		Set keySet = conceptWidgetInterfaces.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();)
		{
			// Which property do we need to get here?
			String name = (String) iter.next();

			ConceptPropertyWidgetInterface widgetInterface = (ConceptPropertyWidgetInterface) conceptWidgetInterfaces
					.get(name);
			if (widgetInterface != null)
			{
				ConceptInterface concept = widgetInterface.getConcept();
				try
				{
					ConceptPropertyInterface property = widgetInterface.getValue();
					if (property != null) // otherwise the value is not
											// set/changed
					{
						concept.addProperty(property);
					} else
					{
						// If we had a parent or inherited concept & the
						// overwrite goes away: clear the value...
						if ((concept.hasParentConcept() || concept.hasInheritedConcept() || concept
								.hasSecurityParentConcept())
								&& !widgetInterface.isOverwrite())
						{

							// clear the child property if there is one and if
							// there is also one defined above this concept
							// level.
							//
							ConceptPropertyInterface parentProperty = concept.getParentProperty(name);
							ConceptPropertyInterface inheritedProperty = concept.getInheritedProperty(name);
							ConceptPropertyInterface securityProperty = concept.getSecurityProperty(name);

							property = concept.getChildProperty(name);
							if (property != null
									&& (parentProperty != null || inheritedProperty != null || securityProperty != null))
							{
								concept.removeChildProperty(property);
							}
						}
					}
				} catch (Exception e)
				{
					allOK = false;
					new ErrorDialog(
							shell,
							Messages.getString("ConceptDefaultsDialog.TITLE_ERROR_PARSING_VALUE"), Messages.getString("ConceptDefaultsDialog.ERROR_0001_ERROR_PARSING_VALUE"), e); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		return allOK;
	}

}
