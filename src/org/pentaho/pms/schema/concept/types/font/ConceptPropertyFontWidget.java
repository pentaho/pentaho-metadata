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
package org.pentaho.pms.schema.concept.types.font;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.ManagedFont;
import be.ibridge.kettle.core.Props;

public class ConceptPropertyFontWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private String name;
    private Canvas canvas;
    private ManagedFont managedFont;
    private boolean overwrite;
    private ConceptInterface concept;
    
    /**
     * @param name The name of the property
     * @param canvas
     * @param managedFont
     */
    public ConceptPropertyFontWidget(ConceptInterface concept, String name, Canvas canvas, ManagedFont managedFont)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.canvas = canvas;
        this.managedFont = managedFont;
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

    public ConceptPropertyInterface getValue()
    {
        if (!hasChanged()) return null; // Return null if nothing changed! 
        FontSettings fontSettings = getFontSettings(managedFont.getFont().getFontData()[0]);
        return new ConceptPropertyFont(name, fontSettings);
    }

    public void setValue(ConceptPropertyInterface property)
    {
        FontSettings value;
        if ( property.getValue()==null)
        {
            FontData fontData = Props.getInstance().getDefaultFont();
            value = new FontSettings(fontData.getName(), fontData.getHeight(), (fontData.getStyle() & SWT.BOLD)!=0, (fontData.getStyle() & SWT.ITALIC)!=0);
        }
        else
        {
            value = (FontSettings) property.getValue();
        }
        Font font = new Font(canvas.getDisplay(), createFontData(value));
        if (managedFont.getFont()!=null) managedFont.dispose();
        managedFont.setFont(font);
        
        canvas.setFont(font);

        canvas.redraw();
    }

    public void setEnabled(boolean enabled)
    {
        canvas.setEnabled(enabled);
    }

    public static FontData createFontData(FontSettings value)
    {
        int flags = SWT.NORMAL;
        if (value.isBold() || value.isItalic())
        {
            flags = (value.isBold()?SWT.BOLD:SWT.NONE) | (value.isItalic()?SWT.ITALIC:SWT.NONE);
        }
        FontData fontData = new FontData(value.getName(), value.getHeight(),  flags);
        return fontData;
    }
    
    public static FontSettings getFontSettings(FontData fontData)
    {
        FontSettings fontSettings = new FontSettings();
        
        fontSettings.setName(fontData.getName());
        fontSettings.setHeight(fontData.getHeight());
        fontSettings.setBold((fontData.getStyle()&SWT.BOLD)!=0);
        fontSettings.setItalic((fontData.getStyle()&SWT.ITALIC)!=0);
        
        return fontSettings;
    }

    public void setFocus()
    {
        canvas.setFocus();
    }


    
    public static final Control getControl(final Composite composite, ConceptInterface concept, final String name, Control lastControl, Map conceptPropertyInterfaces)
    {
        final Props props = Props.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name); 
        
        final ManagedFont managedFont = new ManagedFont(null, false); // Gets set right after
        composite.addDisposeListener(new DisposeListener() { public void widgetDisposed(DisposeEvent e) { managedFont.dispose(); }});

        final Button button = new Button(composite, SWT.PUSH);
        button.setText("Edit font...");
        FormData fdButton = new FormData();
        fdButton.right = new FormAttachment( 100, 0);
        if (lastControl!=null)
        {
            fdButton.top    = new FormAttachment( lastControl, Const.MARGIN ); 
            fdButton.bottom = new FormAttachment( lastControl, Const.MARGIN + 50); 
        }
        else 
        { 
            fdButton.top    = new FormAttachment( 0, 0 ); 
            fdButton.bottom = new FormAttachment( 0, 50); 
        }
        button.setLayoutData(fdButton);

        final Canvas canvas = new Canvas(composite, SWT.BORDER );
        props.setLook(canvas);
        FormData fdCanvas = new FormData();
        fdCanvas.left  = new FormAttachment( props.getMiddlePct(), Const.MARGIN );
        fdCanvas.right = new FormAttachment( button, -Const.MARGIN);
        fdCanvas.top   = new FormAttachment( button, 0, SWT.TOP ); 
        fdCanvas.bottom= new FormAttachment( button, 0, SWT.BOTTOM );
        canvas.setLayoutData(fdCanvas);
        
        canvas.addPaintListener(
            new PaintListener()
            {
                public void paintControl(PaintEvent event)
                {
                    GC gc = event.gc;
                    
                    Rectangle bounds = canvas.getBounds();
                    FontSettings fontSettings = ConceptPropertyFontWidget.getFontSettings(gc.getFont().getFontData()[0]);
                    String message = fontSettings.toString();
                    Point point = gc.textExtent(message, SWT.DRAW_TRANSPARENT);
                    gc.drawText(message, (bounds.width-point.x)/2, (bounds.height-point.y)/2);
                    canvas.setToolTipText(message);
                }        
            }
        );
        

        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyFontWidget(concept, name, canvas, managedFont);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);    
        
        
        canvas.addMouseListener(new MouseAdapter() { public void mouseDown(MouseEvent event) { askFont(composite.getShell(), widgetInterface, managedFont, canvas); } });
        button.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { askFont(composite.getShell(), widgetInterface, managedFont, canvas); } } );
        
        return canvas;
    }
    
    private static final void askFont(Shell shell, ConceptPropertyWidgetInterface widgetInterface, ManagedFont managedFont, Canvas canvas)
    {
        // Edit the font...
        FontDialog fontDialog = new FontDialog(shell, SWT.NONE);
        fontDialog.setFontList(managedFont.getFont().getFontData());
        FontData selection = fontDialog.open();
        if (selection!=null)
        {
            widgetInterface.setChanged();
            managedFont.dispose(); // Once, twice or more, it's safe :-)
            Font font = new Font(shell.getDisplay(), selection);
            managedFont.setFont(font);
            canvas.setFont(font);
            
            // Redraw it...
            canvas.redraw();
        }

    }
        
}
