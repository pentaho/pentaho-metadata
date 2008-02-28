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
package org.pentaho.pms.schema.concept.types.security;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.schema.security.SelectSecurityOwnerRightsDialog;
import org.pentaho.pms.util.Const;

public class ConceptPropertySecurityWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private ConceptInterface concept;
    
    private String    name;
    private TableView tableView;
    private Security  security;
    private boolean   overwrite;
    
    /**
     * @param name The name of the property
     * @param canvas
     * @param managedColor
     */
    public ConceptPropertySecurityWidget(ConceptInterface concept, String name, TableView tableView, Security security)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.tableView = tableView;
        this.security = security;
    }
    
    public ConceptInterface getConcept()
    {
        return concept;
    }
    
    public void setConcept(ConceptInterface concept)
    {
        this.concept = concept;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    public ConceptPropertyInterface getValue()
    {
        if (!hasChanged()) return null; // Return null if nothing changed! 
        return new ConceptPropertySecurity(name, security);
    }

    public void setValue(ConceptPropertyInterface property)
    {
        security = (Security) property.getValue();
    }

    public void setEnabled(boolean enabled)
    {
        tableView.table.setEnabled(enabled);
    }
    
    public void setFocus()
    {
        tableView.setFocusOnFirstEditableField();
    }

    public static final Control getControl(final Composite composite, ConceptInterface concept, final String name, Control lastControl, Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces, final SecurityReference securityReference)
    {
        PropsUI props = PropsUI.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);
        final Security security = (Security)property.getValue();
        // Set buttons to the right of the screen...
        Button delete = new Button(composite, SWT.PUSH);
        delete.setText(Messages.getString("ConceptPropertySecurityWidget.USER_DELETE")); //$NON-NLS-1$
        props.setLook(delete);
        FormData fdDelete = new FormData();
        fdDelete.right= new FormAttachment(100, 0);
        if (lastControl!=null)
        {
            fdDelete.top  = new FormAttachment(lastControl, Const.MARGIN); 
        }
        else 
        { 
            fdDelete.top  = new FormAttachment(0, 0);
        }
        delete.setLayoutData(fdDelete);
        
        Button edit = new Button(composite, SWT.PUSH);
        edit.setText(Messages.getString("ConceptPropertySecurityWidget.USER_EDIT")); //$NON-NLS-1$
        props.setLook(edit);
        FormData fdEdit = new FormData();
        fdEdit.right= new FormAttachment(delete, -Const.MARGIN);
        if (lastControl!=null)
        {
            fdEdit.top  = new FormAttachment(lastControl, Const.MARGIN); 
        }
        else 
        { 
            fdEdit.top  = new FormAttachment(0, 0);
        }
        edit.setLayoutData(fdEdit);
        
        Button add = new Button(composite, SWT.PUSH);
        add.setText(Messages.getString("ConceptPropertySecurityWidget.USER_ADD")); //$NON-NLS-1$
        props.setLook(add);
        FormData fdAdd = new FormData();
        fdAdd.right= new FormAttachment(edit, -Const.MARGIN);
        if (lastControl!=null)
        {
            fdAdd.top  = new FormAttachment(lastControl, Const.MARGIN); 
        }
        else 
        { 
            fdAdd.top  = new FormAttachment(0, 0);
        }
        add.setLayoutData(fdAdd);
        

        ColumnInfo[] colinf=new ColumnInfo[]
           {
              new ColumnInfo(Messages.getString("ConceptPropertySecurityWidget.USER_OWNER_TYPE"),  ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
              new ColumnInfo(Messages.getString("ConceptPropertySecurityWidget.USER_OWNER_NAME"),  ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
              new ColumnInfo(Messages.getString("ConceptPropertySecurityWidget.USER_RIGHTS"),      ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
           };
                                    
        final TableView wFields=new TableView( new Variables(),composite, 
                                          SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
                                          colinf, 
                                          0,  
                                          true, // true = read-only
                                          null,
                                          props
                                          );
        
        
        FormData fdFields = new FormData();
        fdFields.left   = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        fdFields.right  = new FormAttachment(add, -Const.MARGIN);
        if (lastControl!=null)
        {
            fdFields.top   = new FormAttachment(lastControl, Const.MARGIN); 
            fdFields.bottom = new FormAttachment(lastControl, Const.MARGIN+150); 
        }
        else 
        { 
            fdFields.top   = new FormAttachment(0, 0); 
            fdFields.bottom = new FormAttachment(0, 0+150); 
        }

        wFields.setLayoutData(fdFields);
        wFields.pack();
        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertySecurityWidget(concept, name, wFields, security);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);

        SelectionAdapter lsAdd = new SelectionAdapter() 
        { 
            public void widgetSelected(SelectionEvent e) 
            {  
                // Add button clicked 
                //
                SelectSecurityOwnerRightsDialog dialog = new SelectSecurityOwnerRightsDialog(composite.getShell(), securityReference, null, 0);
                if (dialog.open())
                {
                    SecurityOwner owner = new SecurityOwner(dialog.getOwnerType(), dialog.getOwnerName());
                    security.putOwnerRights(owner, dialog.getRights());
                    widgetInterface.setChanged();
                    refreshSecurityView(wFields, security, securityReference);
                }
            } 
        };

        final SelectionAdapter lsEdit = new SelectionAdapter() 
        { 
            public void widgetSelected(SelectionEvent e) 
            {  
                // Edit button clicked 
                //
                TableItem[] items = wFields.table.getSelection();
                if (items.length==1)
                {
                    if (!Const.isEmpty(items[0].getText(1)) && !Const.isEmpty(items[0].getText(2)))
                    {
                        int ownerType = SecurityOwner.getOwnerType(items[0].getText(1));
                        SecurityOwner owner = new SecurityOwner(ownerType, items[0].getText(2));
                        int rights = security.getOwnerRights(owner);
                        
                        SelectSecurityOwnerRightsDialog dialog = new SelectSecurityOwnerRightsDialog(composite.getShell(), securityReference, owner, rights);
                        if (dialog.open())
                        {
                            security.putOwnerRights(owner, dialog.getRights()); // overwrite the owner (can't change)
                            widgetInterface.setChanged();
                            refreshSecurityView(wFields, security, securityReference);
                        }
                    }
                }
            } 
        };

        SelectionAdapter lsDelete = new SelectionAdapter() 
        { 
            public void widgetSelected(SelectionEvent e) 
            {  
                // delete button clicked: remove selected owner+rights from security 
                //
                TableItem[] items = wFields.table.getSelection();
                for (int i=0;i<items.length;i++)
                {
                    if (!Const.isEmpty(items[i].getText(1)) && !Const.isEmpty(items[i].getText(2)))
                    {
                        int ownerType = SecurityOwner.getOwnerType(items[i].getText(1));
                        SecurityOwner owner = new SecurityOwner(ownerType, items[i].getText(2));
                        security.removeOwnerRights(owner);
                    }
                }
                refreshSecurityView(wFields, security, securityReference);
            } 
        };

        add.addSelectionListener( lsAdd );
        edit.addSelectionListener( lsEdit );
        delete.addSelectionListener( lsDelete );

        wFields.table.addSelectionListener(new SelectionAdapter()
            {
                public void widgetDefaultSelected(SelectionEvent e)
                {
                    lsEdit.widgetSelected(e);
                }
            }
        );
        
        refreshSecurityView(wFields, security, securityReference);
        
        return wFields;
    }

    private static void refreshSecurityView(TableView fields, Security security, SecurityReference securityReference)
    {
        fields.clearAll(false);
        List owners = security.getOwners();
        for (int i=0;i<owners.size();i++)
        {
            SecurityOwner owner = (SecurityOwner) owners.get(i);
            String ownerType = owner.getOwnerTypeDescription();
            String ownerName = owner.getOwnerName();
            int rights = security.getOwnerRights(owner);
            String rightsDescription = securityReference.getRightsDescription(rights);
            
            TableItem item = new TableItem(fields.table, SWT.NONE);
            item.setText(1, ownerType);
            item.setText(2, ownerName);
            item.setText(3, rightsDescription);
        }
        
        fields.removeEmptyRows();
        fields.setRowNums();
        fields.optWidth(true);
    }
}
