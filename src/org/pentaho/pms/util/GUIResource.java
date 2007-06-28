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
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import be.ibridge.kettle.core.Props;

/**
 * This is a singleton class that contains allocated Fonts, Colors, etc.
 * All colors etc. are allocated once and released once at the end of the program.
 * 
 * @author Matt
 * @since  27/10/2005
 *
 */
public class GUIResource
{
    private static GUIResource guiResource;
    
    private Display display;
    
    // 33 resources
    
    /* * * Colors * * */
    private ManagedColor colorBackground;
    private ManagedColor colorGraph;
    private ManagedColor colorTab;

    private ManagedColor colorRed;
    private ManagedColor colorGreen;
    private ManagedColor colorBlue;
    private ManagedColor colorOrange;
    private ManagedColor colorYellow;
    private ManagedColor colorMagenta;
    private ManagedColor colorBlack;
    private ManagedColor colorGray;
    private ManagedColor colorDarkGray;
    private ManagedColor colorLightGray;
    private ManagedColor colorDemoGray;
    private ManagedColor colorWhite;
    private ManagedColor colorDirectory;

    /* * * Fonts * * */
    private ManagedFont fontGraph;
    private ManagedFont fontNote;
    private ManagedFont fontFixed;
    private ManagedFont fontLarge;

    private Image     imageMetaSplash;
    private Image     imageConnectionsParent;
    private Image     imageBusinessView;
    private Image     imageConnection;
    private Image     imagePhysicalTable;
    private Image     imageCatagory;
    private Image     imageBusinessColumn;
    private Image     imageRelationship;
    private Image     imageRelationshipsParent;
    private Image     imageBusinessTable;
    private Image     imageBusinessTablesParent;
    private Image     imagePhysicalColumn;
    private Image     imageBusinessModel;
    private Image     imageBol;
    private Image     imageIcon;

    private ManagedFont fontMedium;
    
    /**
     * GUIResource also contains the clipboard as it has to be allocated only once!
     * I don't want to put it in a seperate singleton just for this one member.
     */
    private static Clipboard clipboard;

    private GUIResource(Display display)
    {
        this.display = display;
        
        getResources(false);
        
        display.addListener(SWT.Dispose, new Listener()
            {
                public void handleEvent(Event event)
                {
                    dispose(false);
                }
            }
        );
        
        // Force de-allocation of resources on exit, no matter what.
        Runtime.getRuntime().addShutdownHook(
                new Thread() 
                { 
                    public void run()
                    {
                        dispose(false);
                    }
                }
            );
        
        clipboard = null;
    }
    
    public static final GUIResource getInstance()
    {
        if (guiResource!=null) return guiResource;
        guiResource = new GUIResource(Props.getInstance().getDisplay());
        return guiResource;
    }
        
    public synchronized void reload()
    {
        dispose(true);
        getResources(true);
    }
    
    private synchronized void getResources(boolean skipImages)
    {
        Props props = Props.getInstance();
        
        colorBackground = new ManagedColor(display, props.getBackgroundRGB() );
        colorGraph      = new ManagedColor(display, props.getGraphColorRGB() );
        colorTab        = new ManagedColor(display, props.getTabColorRGB()   );
        
        colorRed        = new ManagedColor(display, 255,   0,   0 );
        colorGreen      = new ManagedColor(display,   0, 255,   0 );
        colorBlue       = new ManagedColor(display,   0,   0, 255 );
        colorGray       = new ManagedColor(display, 100, 100, 100 );
        colorYellow     = new ManagedColor(display, 255, 255,   0 );
        colorMagenta    = new ManagedColor(display, 255,   0, 255);
        colorOrange     = new ManagedColor(display, 255, 165,   0 );

        colorWhite      = new ManagedColor(display, 255, 255, 255 );
        colorDemoGray   = new ManagedColor(display, 248, 248, 248 );
        colorLightGray  = new ManagedColor(display, 225, 225, 225 );
        colorDarkGray   = new ManagedColor(display, 100, 100, 100 );
        colorBlack      = new ManagedColor(display,   0,   0,   0 );

        colorDirectory  = new ManagedColor(display,   0,   0, 255 );
        
        fontGraph   = new ManagedFont(display, props.getGraphFont());
        fontNote    = new ManagedFont(display, props.getNoteFont());
        fontFixed   = new ManagedFont(display, props.getFixedFont());

        // Create a large version of the graph font
        FontData largeFontData = props.getGraphFont();
        largeFontData.setHeight(largeFontData.getHeight()*2);
        fontLarge   = new ManagedFont(display, largeFontData);

        // Create a medium size version of the graph font
        FontData mediumFontData = props.getGraphFont();
        mediumFontData.setHeight((int)(mediumFontData.getHeight()*1.5));
        fontMedium = new ManagedFont(display, mediumFontData);

        // Load all images from files...
        if (!skipImages)
        {
            loadImages();
        }
    }
    
    private synchronized void dispose(boolean skipImages)
    {
        // Colors 
        colorBackground.dispose();
        colorGraph     .dispose();
        colorTab       .dispose();
        
        colorRed      .dispose();
        colorGreen    .dispose();
        colorBlue     .dispose();
        colorGray     .dispose();
        colorYellow   .dispose();
        colorMagenta  .dispose();
        colorOrange   .dispose();

        colorWhite    .dispose();
        colorDemoGray .dispose();
        colorLightGray.dispose();
        colorDarkGray .dispose();
        colorBlack    .dispose();
        
        colorDirectory.dispose();
        
        // Fonts
        fontGraph  .dispose();
        fontNote   .dispose();
        fontFixed  .dispose();
        fontLarge  .dispose();
        fontMedium .dispose();
        
        if (!skipImages)
        {
            if (!imageMetaSplash.isDisposed()) imageMetaSplash.dispose();
            if (!imageConnectionsParent.isDisposed()) imageConnectionsParent.dispose();
            if (!imageConnection.isDisposed()) imageConnection.dispose();
            if (!imagePhysicalTable.isDisposed()) imagePhysicalTable.dispose();
            if (!imageBusinessView.isDisposed()) imageBusinessView.dispose();
            if (!imageCatagory.isDisposed()) imageCatagory.dispose();
            if (!imageBusinessColumn.isDisposed()) imageBusinessColumn.dispose();
            if (!imageRelationship.isDisposed()) imageRelationship.dispose();
            if (!imageRelationshipsParent.isDisposed()) imageRelationshipsParent.dispose();
            if (!imageBusinessTable.isDisposed()) imageBusinessTable.dispose();
            if (!imageBusinessTablesParent.isDisposed()) imageBusinessTablesParent.dispose();
            if (!imagePhysicalColumn.isDisposed()) imagePhysicalColumn.dispose();
            if (!imageBusinessModel.isDisposed()) imageBusinessModel.dispose();
            if (!imageBol.isDisposed()) imageBol.dispose();
            if (!imageIcon.isDisposed()) imageIcon.dispose();
        }
    }
    
    private void loadImages()
    {
        imageMetaSplash  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "splash_metadata_editor.png")); //$NON-NLS-1$
        imageConnectionsParent  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "folder_connection.png")); //$NON-NLS-1$
        imageConnection  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "connection.png")); //$NON-NLS-1$
        imagePhysicalTable  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "connection_table.png")); //$NON-NLS-1$
        imageBusinessView  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "business_view.png")); //$NON-NLS-1$
        imageCatagory  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "category.png")); //$NON-NLS-1$
        imageBusinessColumn  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "column.png")); //$NON-NLS-1$
        imageRelationship  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "relationship.png")); //$NON-NLS-1$
        imageRelationshipsParent  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "folder_relationship.png")); //$NON-NLS-1$
        imageBusinessTable  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "business_table.png")); //$NON-NLS-1$
        imageBusinessTablesParent  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "folder_business_table.png")); //$NON-NLS-1$
        imagePhysicalColumn  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "connection_column.png")); //$NON-NLS-1$
        imageBusinessModel  = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "business_model.png")); //$NON-NLS-1$
        imageBol         = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "bol.png")); //$NON-NLS-1$
        imageIcon        = new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "icon.png")); //$NON-NLS-1$
    }

    /**
     * @return Returns the colorBackground.
     */
    public Color getColorBackground()
    {
        return colorBackground.getColor();
    }

    /**
     * @return Returns the colorBlack.
     */
    public Color getColorBlack()
    {
        return colorBlack.getColor();
    }

    /**
     * @return Returns the colorBlue.
     */
    public Color getColorBlue()
    {
        return colorBlue.getColor();
    }

    /**
     * @return Returns the colorDarkGray.
     */
    public Color getColorDarkGray()
    {
        return colorDarkGray.getColor();
    }

    /**
     * @return Returns the colorDemoGray.
     */
    public Color getColorDemoGray()
    {
        return colorDemoGray.getColor();
    }

    /**
     * @return Returns the colorDirectory.
     */
    public Color getColorDirectory()
    {
        return colorDirectory.getColor();
    }

    /**
     * @return Returns the colorGraph.
     */
    public Color getColorGraph()
    {
        return colorGraph.getColor();
    }

    /**
     * @return Returns the colorGray.
     */
    public Color getColorGray()
    {
        return colorGray.getColor();
    }

    /**
     * @return Returns the colorGreen.
     */
    public Color getColorGreen()
    {
        return colorGreen.getColor();
    }

    /**
     * @return Returns the colorLightGray.
     */
    public Color getColorLightGray()
    {
        return colorLightGray.getColor();
    }

    /**
     * @return Returns the colorMagenta.
     */
    public Color getColorMagenta()
    {
        return colorMagenta.getColor();
    }

    /**
     * @return Returns the colorOrange.
     */
    public Color getColorOrange()
    {
        return colorOrange.getColor();
    }

    /**
     * @return Returns the colorRed.
     */
    public Color getColorRed()
    {
        return colorRed.getColor();
    }

    /**
     * @return Returns the colorTab.
     */
    public Color getColorTab()
    {
        return colorTab.getColor();
    }

    /**
     * @return Returns the colorWhite.
     */
    public Color getColorWhite()
    {
        return colorWhite.getColor();
    }

    /**
     * @return Returns the colorYellow.
     */
    public Color getColorYellow()
    {
        return colorYellow.getColor();
    }

    /**
     * @return Returns the display.
     */
    public Display getDisplay()
    {
        return display;
    }

    /**
     * @return Returns the fontFixed.
     */
    public Font getFontFixed()
    {
        return fontFixed.getFont();
    }

    /**
     * @return Returns the fontGraph.
     */
    public Font getFontGraph()
    {
        return fontGraph.getFont();
    }


    /**
     * @return Returns the fontNote.
     */
    public Font getFontNote()
    {
        return fontNote.getFont();
    }

    /**
     * @return the fontLarge
     */
    public Font getFontLarge()
    {
        return fontLarge.getFont();
    }
    
    /**
     * @return Returns the clipboard.
     */
    public Clipboard getNewClipboard()
    {
        if (clipboard!=null)
        {
            clipboard.dispose();
            clipboard=null;
        }
        clipboard=new Clipboard(display);
        
        return clipboard;
    }

    public void toClipboard(String cliptext)
    {
        if (cliptext==null) return;

        getNewClipboard();
        TextTransfer tran = TextTransfer.getInstance();
        clipboard.setContents(new String[] { cliptext }, new Transfer[] { tran });
    }
    
    public String fromClipboard()
    {
        getNewClipboard();
        TextTransfer tran = TextTransfer.getInstance();

        return (String)clipboard.getContents(tran);
    }

    /**
     * @return the splashImage
     */
    public Image getImageMetaSplash()
    {
        return imageMetaSplash;
    }

    /**
     * @param splashImage the splashImage to set
     */
    public void setImageMetaSplash(Image splashImage)
    {
        this.imageMetaSplash = splashImage;
    }

    /**
     * @return the imageConnection
     */
    public Image getImageConnectionsParent()
    {
        return imageConnectionsParent;
    }

    public Image getImageConnection()
    {
        return imageConnection;
    }
    
    public Image getImagePhysicalTable()
    {
        return imagePhysicalTable;
    }
    
    public Image getImageBusinessView()
    {
        return imageBusinessView;
    }
    
    public Image getImageCatagory()
    {
        return imageCatagory;
    }
    
    public Image getImageBusinessColumn()
    {
        return imageBusinessColumn;
    }
    
    public Image getImageRelationship()
    {
        return imageRelationship;
    }
    
    public Image getImageRelationshipsParent()
    {
        return imageRelationshipsParent;
    }
    
    public Image getImageBusinessTable()
    {
        return imageBusinessTable;
    }
    
    public Image getImageBusinessTablesParent()
    {
        return imageBusinessTablesParent;
    }
    
    public Image getImagePhysicalColumn()
    {
        return imagePhysicalColumn;
    }
    
    public Image getImageBusinessModel()
    {
        return imageBusinessModel;
    }
    
    /**
     * @param imageConnection the imageConnection to set
     */
    public void setImageConnectionsParent(Image imageConnectionParent)
    {
        this.imageConnectionsParent = imageConnectionParent;
    }

    public void setImageConnection(Image imageConnection)
    {
        this.imageConnection = imageConnection;
    }
    
    public void setImagePhysicalTable(Image imagePhysicalTable)
    {
        this.imagePhysicalTable = imagePhysicalTable;
    }
    
    public void setImageBusinessView(Image imageBusinessView)
    {
        this.imageBusinessView = imageBusinessView;
    }
    
    public void setImageCatagory(Image imageCatagory)
    {
        this.imageCatagory = imageCatagory;
    }
    
    public void setImageBusinessColumn(Image imageBusinessColumn)
    {
        this.imageBusinessColumn = imageBusinessColumn;
    }
    
    public void setImageRelationship(Image imageRelationship)
    {
        this.imageRelationship = imageRelationship;
    }
    
    public void setImageRelationshipsParent(Image imageRelationshipsParent)
    {
        this.imageRelationshipsParent = imageRelationshipsParent;
    }
    
    public void setImageBusinessTable(Image imageBusinessTable)
    {
        this.imageBusinessTable = imageBusinessTable;
    }
    
    public void setImageBusinessTablesParent(Image imageBusinessTablesParent)
    {
        this.imageBusinessTablesParent = imageBusinessTablesParent;
    }
    
    public void setImagePhysicalColumn(Image imagePhysicalColumn)
    {
        this.imagePhysicalColumn = imagePhysicalColumn;
    }
    
    public void setImageBusinessModel(Image imageBusinessModel)
    {
        this.imageBusinessModel = imageBusinessModel;
    }
    /**
     * @return the imageBol
     */
    public Image getImageBol()
    {
        return imageBol;
    }

    /**
     * @param imageBol the imageBol to set
     */
    public void setImageBol(Image imageBol)
    {
        this.imageBol = imageBol;
    }

    /**
     * @return the imageIcon
     */
    public Image getImageIcon()
    {
        return imageIcon;
    }

    /**
     * @param imageIcon the imageIcon to set
     */
    public void setImageIcon(Image imageIcon)
    {
        this.imageIcon = imageIcon;
    }

    /**
     * @return the fontMedium
     */
    public Font getFontMedium()
    {
        return fontMedium.getFont();
    }
}
