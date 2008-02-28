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
package org.pentaho.pms.schema.concept.types.url;

import java.net.URL;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.EnterStringDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

public class ConceptPropertyURLWidget  extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private String name;
    private Link link;
    private StringBuffer url;
    
    private boolean overwrite;
    private ConceptInterface concept;
    
    /**
     * @param name The name of the property
     * @param link
     * @param url
     */
    public ConceptPropertyURLWidget(ConceptInterface concept, String name, Link link, StringBuffer url)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.link = link;
        this.url = url;
    }
    
    /**
     * @return the concept
     */
    public ConceptInterface getConcept()
    {
        return concept;
    }

    /**
     * @param concept the concept to set
     */
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

    public ConceptPropertyInterface getValue() throws Exception
    {
        if (!hasChanged()) return null; // Return null if nothing changed! 
        if (url.length()==0) return null;
        URL someURL = new URL(url.toString());
        return new ConceptPropertyURL(name, someURL);
    }

    public void setValue(ConceptPropertyInterface property)
    {
        final URL value = (URL) property.getValue();
        final StringBuffer thisUrl = new StringBuffer();
        if (value!=null) thisUrl.append(value.toString());
        if (value!=null)
        {
            link.setText("<A>"+value.toString()+"</A>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            link.setText("<A>"+Messages.getString("ConceptPropertyURLWidget.USER_ENTER_URL")+"</A>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    public void setEnabled(boolean enabled)
    {
        link.setEnabled(enabled);
    }
    
    public static boolean getURL(Composite composite, ConceptPropertyInterface property, Link link, StringBuffer url)
    {
        URL value = (URL) property.getValue();
        EnterStringDialog dialog = new EnterStringDialog(composite.getShell(), value!=null?value.toString():"", Messages.getString("ConceptPropertyURLWidget.USER_ENTER_URL"), Messages.getString("ConceptPropertyURLWidget.USER_ENTER_URL")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String newValue = dialog.open();
        if (newValue!=null)
        {
            try
            {
                property.setValue(new URL(newValue));
                url.setLength(0);
                url.append(newValue);
                link.setText("<A>"+newValue+"</A>"); //$NON-NLS-1$ //$NON-NLS-2$
                return true;
            }
            catch(Exception e)
            {
                new ErrorDialog(composite.getShell(), Messages.getString("ConceptPropertyURLWidget.USER_TITLE_ERROR_IN_URL"), Messages.getString("ConceptPropertyURLWidget.USER_ERROR_IN_URL"), e); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        return false;
    }
    
    public void setFocus()
    {
        link.setFocus();
    }


    public static final Control getControl(final Composite composite, ConceptInterface concept, final String name, Control lastControl, Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces)
    {
        final PropsUI props = PropsUI.getInstance();
        final ConceptPropertyInterface property = concept.getProperty(name);

        final StringBuffer url = new StringBuffer();

        final Link link = new Link(composite, SWT.NONE);
        props.setLook(link);
        FormData fdLink = new FormData();
        fdLink.left   = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        fdLink.right  = new FormAttachment(100, 0);
        if (lastControl!=null)
        {
            fdLink.top   = new FormAttachment(lastControl, Const.MARGIN); 
        }
        else 
        { 
            fdLink.top   = new FormAttachment(0, 0); 
        }
        link.setLayoutData(fdLink);

        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyURLWidget(concept, name, link, url);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);    

        link.addSelectionListener(
                new SelectionAdapter()
                {
                    public void widgetSelected(SelectionEvent event)
                    {
                        if (ConceptPropertyURLWidget.getURL(composite, property, link, url)) widgetInterface.setChanged();
                    }
                }
            );
            
        link.addMouseListener(
            new MouseAdapter()
            {
                public void mouseDown(MouseEvent arg0)
                {
                    if (ConceptPropertyURLWidget.getURL(composite, property, link, url)) widgetInterface.setChanged();
                }
            }
        );
            

        return link;
    }
    

}
