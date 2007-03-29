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
package org.pentaho.pms.schema.concept.types.color;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.ManagedColor;
import be.ibridge.kettle.core.Props;

public class ConceptPropertyColorWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private ConceptInterface concept;
    private String name;
    private Canvas canvas;
    private ManagedColor managedColor;
    private boolean overwrite;
    
    /**
     * @param name The name of the property
     * @param canvas
     * @param managedColor
     */
    public ConceptPropertyColorWidget(ConceptInterface concept, String name, Canvas canvas, ManagedColor managedColor)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.canvas = canvas;
        this.managedColor = managedColor;
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
        Color color = managedColor.getColor();
        return new ConceptPropertyColor(name, new ColorSettings(color.getRed(), color.getGreen(), color.getBlue()));
    }

    public void setValue(ConceptPropertyInterface property)
    {
        ColorSettings value = (ColorSettings) property.getValue();
        if (value!=null)
        {
            Color bg = new Color(canvas.getDisplay(), value.getRed(), value.getGreen(), value.getBlue());
            managedColor.dispose();
            managedColor.setColor(bg);
            canvas.redraw();
        }
    }

    public void setEnabled(boolean enabled)
    {
        canvas.setEnabled(enabled);
    }
    
    public void setFocus()
    {
        canvas.setFocus();
    }

    public static final Control getControl(final Composite composite, ConceptInterface concept, final String name, Control lastControl, Map conceptPropertyInterfaces)
    {
        Props props = Props.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);
        
        final ColorSettings value = (ColorSettings) property.getValue();
        Color bg;
        if (value!=null)
        {
            bg= new Color(composite.getDisplay(), value.getRed(), value.getGreen(), value.getBlue());
        }
        else
        {
            bg = new Color(composite.getDisplay(), props.getBackgroundRGB());
        }
        final ManagedColor managedColor = new ManagedColor(bg, false);
        composite.addDisposeListener(new DisposeListener() { public void widgetDisposed(DisposeEvent e) { managedColor.dispose(); } });
        
        final Button button = new Button(composite, SWT.PUSH);
        button.setText(Messages.getString("ConceptPropertyColorWidget.USER_EDIT_COLOR")); //$NON-NLS-1$
        FormData fdButton = new FormData();
        fdButton.right = new FormAttachment( 100, 0);
        if (lastControl!=null)
        {
            fdButton.top   = new FormAttachment( lastControl, Const.MARGIN ); 
        }
        else 
        { 
            fdButton.top   = new FormAttachment( 0, 0 ); 
        }
        button.setLayoutData(fdButton);
        
        final Canvas canvas = new Canvas(composite, SWT.BORDER );
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
                    Color bgColor = managedColor.getColor();
                    canvas.setBackground(bgColor);
                    gc.setForeground(bgColor);
                    Rectangle bounds = canvas.getBounds();
                    String message = Messages.getString("ConceptPropertyColorWidget.USER_CLICK_TO_EDIT"); //$NON-NLS-1$
                    Point point = gc.textExtent(message, SWT.DRAW_TRANSPARENT);
                    
                    Color color = new Color(composite.getDisplay(), 255-bgColor.getRed(), 255-bgColor.getGreen(), 255-bgColor.getBlue());
                    gc.setForeground(color);
                    gc.drawText(message, (bounds.width-point.x)/2, (bounds.height-point.y)/2, SWT.DRAW_TRANSPARENT);
                    color.dispose();
                }
            }
        );

        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyColorWidget(concept, name, canvas, managedColor);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);    


        final MouseListener mouseListener = new MouseAdapter()
            {
                public void mouseDown(MouseEvent e)
                {
                    askColor(composite.getShell(), widgetInterface, managedColor, canvas);
                }
            };

        canvas.addMouseListener(mouseListener);
        button.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    askColor(composite.getShell(), widgetInterface, managedColor, canvas);
                }
            }
        );
        return canvas;
    }

    private static void askColor(Shell shell, ConceptPropertyWidgetInterface widgetInterface, ManagedColor managedColor, Canvas canvas)
    {
        ColorDialog colorDialog = new ColorDialog(shell, SWT.NONE);
        Color color = managedColor.getColor();
        colorDialog.setRGB(new RGB(color.getRed(), color.getGreen(), color.getBlue()));
        RGB selection = colorDialog.open();
        if (selection!=null)
        {
            widgetInterface.setChanged();
            managedColor.dispose();
            Color c = new Color(shell.getDisplay(), selection.red, selection.green, selection.blue);
            managedColor.setColor(c);
            canvas.redraw();
        }
    }

}
