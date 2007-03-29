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

package org.pentaho.pms.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.messages.Messages;

/**
 * Displays the Kettle splash screen
 * 
 * @author Matt
 * @since  14-mrt-2005
 */
public class Splash
{
	private Shell splash;
	
	public Splash(Display display)
	{
		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
		
		final Image splashImage = GUIResource.getInstance().getImageMetaSplash(); // new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "MetaSplash.png"));
        final Image splashIcon  = GUIResource.getInstance().getImageIcon(); // new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "icon.png"));
        
        splash = new Shell(display, SWT.NONE /*SWT.ON_TOP*/);
        splash.setImage(splashIcon);
        splash.setText(Messages.getString("Splash.USER_APP_TITLE")); //$NON-NLS-1$
        
		FormLayout splashLayout = new FormLayout();
		splash.setLayout(splashLayout);
        
		Canvas canvas = new Canvas(splash, SWT.NO_BACKGROUND);
		
		FormData fdCanvas = new FormData();
		fdCanvas.left   = new FormAttachment(0,0);
		fdCanvas.top    = new FormAttachment(0,0);
		fdCanvas.right  = new FormAttachment(100,0);
		fdCanvas.bottom = new FormAttachment(100,0);
		canvas.setLayoutData(fdCanvas);

		canvas.addPaintListener(new PaintListener()
			{
				public void paintControl(PaintEvent e)
				{
					e.gc.drawImage(splashImage, 0, 0);
				}
			}
		);
		
		splash.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent arg0)
				{
					splashImage.dispose();
				}
			}
		);
		Rectangle bounds = splashImage.getBounds();
		int x = (displayBounds.width - bounds.width)/2;
		int y = (displayBounds.height - bounds.height)/2;
		
		splash.setSize(bounds.width, bounds.height);
		splash.setLocation(x,y);
		
		splash.open();
	}
	
	public void dispose()
	{
		if (!splash.isDisposed()) splash.dispose();
	}
	
	public void hide()
	{
		splash.setVisible(false);
	}
	
	public void show()
	{
		splash.setVisible(true);
	}
}
