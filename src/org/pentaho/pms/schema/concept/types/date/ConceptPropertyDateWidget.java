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
package org.pentaho.pms.schema.concept.types.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyWidgetInterface;
import org.pentaho.pms.util.Const;

public class ConceptPropertyDateWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private String name;
    private Text year, month, day, hours, minutes, seconds;
    private Label message;
    private boolean overwrite;
    private ConceptInterface concept;
    
    /**
     * @param name The name of the property
     * @param year 
     * @param month
     * @param day
     * @param hours
     * @param minutes
     * @param seconds
     * @param message Label on which we display error & parsing messages.
     */
    public ConceptPropertyDateWidget(ConceptInterface concept, String name, Text year, Text month, Text day, Text hours, Text minutes, Text seconds, Label message)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.message = message;
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
        return new ConceptPropertyDate(name, getDate(year, month, day, hours, minutes, seconds, message));
    }

    public void setValue(ConceptPropertyInterface property)
    {
        Date value = (Date)property.getValue();
        if (value!=null) year.setText(new SimpleDateFormat("yyyy").format(value));  //$NON-NLS-1$
        if (value!=null) month.setText(new SimpleDateFormat("MM").format(value));  //$NON-NLS-1$
        if (value!=null) day.setText(new SimpleDateFormat("dd").format(value));  //$NON-NLS-1$
        if (value!=null) hours.setText(new SimpleDateFormat("HH").format(value));  //$NON-NLS-1$
        if (value!=null) minutes.setText(new SimpleDateFormat("mm").format(value));  //$NON-NLS-1$
        if (value!=null) seconds.setText(new SimpleDateFormat("ss").format(value));  //$NON-NLS-1$
    }

    public void setEnabled(boolean enabled)
    {
        year.setEnabled(enabled);
        month.setEnabled(enabled);
        day.setEnabled(enabled);
        hours.setEnabled(enabled);
        minutes.setEnabled(enabled);
        seconds.setEnabled(enabled);
    }

    public static Date getDate(Text year, Text month, Text day, Text hours, Text minutes, Text seconds, Label message) throws ParseException
    {
        String dateString = year.getText()+"/"+month.getText()+"/"+day.getText()+" "+hours.getText()+":"+minutes.getText()+":"+seconds.getText(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //$NON-NLS-1$
        format.setLenient(true);

        Date date = format.parse(dateString);
        return date;
    }
    
    public void setFocus()
    {
        year.setFocus();
    }

    public static final Control getControl(Composite composite, ConceptInterface concept, final String name, Control lastControl, Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces)
    {
        PropsUI props = PropsUI.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);

        Composite dateComposite = new Composite(composite, SWT.NONE);
        FormLayout dateLayout = new FormLayout();
        dateComposite.setLayout(dateLayout);
        props.setLook(dateComposite);
        
        // A series of combos with the year, month, day, hours, minutes, seconds
        //
        // Year
        Label yearLabel = new Label(dateComposite, SWT.LEFT);
        props.setLook(yearLabel);
        yearLabel.setText(Messages.getString("ConceptPropertyDateWidget.USER_YEAR")); //$NON-NLS-1$
        FormData fdYearLabel = new FormData();
        fdYearLabel.left   = new FormAttachment(0, 0);
        fdYearLabel.top    = new FormAttachment(0, 0);
        yearLabel.setLayoutData(fdYearLabel);

        final Text year = new Text(dateComposite, SWT.BORDER);
        props.setLook(year);
        year.setToolTipText(Messages.getString("ConceptPropertyDateWidget.USER_YEAR_DESC")); //$NON-NLS-1$
        FormData fdYear = new FormData();
        fdYear.left  = new FormAttachment(yearLabel, Const.MARGIN);
        fdYear.top   = new FormAttachment(0, 0);
        year.setLayoutData(fdYear);
        
        // Month
        Label monthLabel = new Label(dateComposite, SWT.LEFT);
        props.setLook(monthLabel);
        monthLabel.setText(Messages.getString("ConceptPropertyDateWidget.USER_MONTH")); //$NON-NLS-1$
        FormData fdMonthLabel = new FormData();
        fdMonthLabel.left   = new FormAttachment(year, Const.MARGIN);
        fdMonthLabel.top    = new FormAttachment(0, 0);
        monthLabel.setLayoutData(fdMonthLabel);

        final Text month = new Text(dateComposite, SWT.BORDER);
        props.setLook(month);
        month.setToolTipText(Messages.getString("ConceptPropertyDateWidget.USER_MONTH_DESC")); //$NON-NLS-1$
        FormData fdMonth = new FormData();
        fdMonth.left  = new FormAttachment(monthLabel, Const.MARGIN);
        fdMonth.top   = new FormAttachment(0, 0);
        month.setLayoutData(fdMonth);
          
        // Day
        Label dayLabel = new Label(dateComposite, SWT.LEFT);
        props.setLook(dayLabel);
        dayLabel.setText(Messages.getString("ConceptPropertyDateWidget.USER_DAY")); //$NON-NLS-1$
        FormData fdDayLabel = new FormData();
        fdDayLabel.left   = new FormAttachment(month, Const.MARGIN);
        fdDayLabel.top    = new FormAttachment(0, 0);
        dayLabel.setLayoutData(fdDayLabel);

        final Text day = new Text(dateComposite, SWT.BORDER);
        props.setLook(day);
        day.setToolTipText(Messages.getString("ConceptPropertyDateWidget.USER_DAY_DESC")); //$NON-NLS-1$
        FormData fdDay = new FormData();
        fdDay.left  = new FormAttachment(dayLabel, Const.MARGIN);
        fdDay.top   = new FormAttachment(0, 0);
        day.setLayoutData(fdDay);

        // Hours
        Label hoursLabel = new Label(dateComposite, SWT.LEFT);
        props.setLook(hoursLabel);
        hoursLabel.setText(Messages.getString("ConceptPropertyDateWidget.USER_HOURS")); //$NON-NLS-1$
        FormData fdHoursLabel = new FormData();
        fdHoursLabel.left   = new FormAttachment(0, 0);
        fdHoursLabel.top    = new FormAttachment(year, Const.MARGIN);
        hoursLabel.setLayoutData(fdHoursLabel);

        final Text hours = new Text(dateComposite, SWT.BORDER);
        props.setLook(hours);
        hours.setToolTipText(Messages.getString("ConceptPropertyDateWidget.USER_HOURS_DESC")); //$NON-NLS-1$
        FormData fdHours = new FormData();
        fdHours.left  = new FormAttachment(hoursLabel, Const.MARGIN);
        fdHours.top   = new FormAttachment(year, Const.MARGIN);
        hours.setLayoutData(fdHours);

        // Minutes
        Label minutesLabel = new Label(dateComposite, SWT.LEFT);
        props.setLook(minutesLabel);
        minutesLabel.setText(Messages.getString("ConceptPropertyDateWidget.USER_MINUTES")); //$NON-NLS-1$
        FormData fdMinutesLabel = new FormData();
        fdMinutesLabel.left   = new FormAttachment(hours, Const.MARGIN);
        fdMinutesLabel.top    = new FormAttachment(year, Const.MARGIN);
        minutesLabel.setLayoutData(fdMinutesLabel);

        final Text minutes = new Text(dateComposite, SWT.BORDER);
        props.setLook(minutes);
        minutes.setToolTipText(Messages.getString("ConceptPropertyDateWidget.USER_MINUTES_DESC")); //$NON-NLS-1$
        FormData fdMinutes = new FormData();
        fdMinutes.left  = new FormAttachment(minutesLabel, Const.MARGIN);
        fdMinutes.top   = new FormAttachment(year, Const.MARGIN);
        minutes.setLayoutData(fdMinutes);
        
        // Seconds
        Label secondsLabel = new Label(dateComposite, SWT.LEFT);
        props.setLook(secondsLabel);
        secondsLabel.setText(Messages.getString("ConceptPropertyDateWidget.USER_SECONDS")); //$NON-NLS-1$
        FormData fdSecondsLabel = new FormData();
        fdSecondsLabel.left   = new FormAttachment(minutes, Const.MARGIN);
        fdSecondsLabel.top    = new FormAttachment(year, Const.MARGIN);
        secondsLabel.setLayoutData(fdSecondsLabel);

        final Text seconds = new Text(dateComposite, SWT.BORDER);
        props.setLook(seconds);
        seconds.setToolTipText(Messages.getString("ConceptPropertyDateWidget.USER_SECONDS_DESC")); //$NON-NLS-1$
        FormData fdSeconds = new FormData();
        fdSeconds.left  = new FormAttachment(secondsLabel, Const.MARGIN);
        fdSeconds.top   = new FormAttachment(year, Const.MARGIN);
        seconds.setLayoutData(fdSeconds);

        // Message
        final Label message = new Label(dateComposite, SWT.LEFT);
        props.setLook(message);
        final String VALID_DATE = Messages.getString("ConceptPropertyDateWidget.USER_VALID_DATE"); //$NON-NLS-1$
        message.setText(VALID_DATE);
        FormData fdMessage = new FormData();
        fdMessage.left   = new FormAttachment(seconds, 3*Const.MARGIN);
        fdMessage.right  = new FormAttachment(100, 0);
        fdMessage.top    = new FormAttachment(0, 0);
        message.setLayoutData(fdMessage);
        
        ModifyListener lsMod = new ModifyListener()
            {
                public void modifyText(ModifyEvent event)
                {
                    // Try to parse the date...
                    try
                    {
                        Date date = ConceptPropertyDateWidget.getDate(year, month, day, hours, minutes, seconds, message);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //$NON-NLS-1$
                        message.setText(VALID_DATE+" : "+format.format(date)); //$NON-NLS-1$
                    }
                    catch(Exception e)
                    {
                       message.setText(e.toString());
                    }
                }
            };
        
        year.addModifyListener(lsMod);
        month.addModifyListener(lsMod);
        day.addModifyListener(lsMod);
        hours.addModifyListener(lsMod);
        minutes.addModifyListener(lsMod);
        seconds.addModifyListener(lsMod);

        FormData fdDate = new FormData();
        fdDate.left  = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        fdDate.right = new FormAttachment(100, 0);
        if (lastControl!=null) fdDate.top   = new FormAttachment(lastControl, Const.MARGIN); else fdDate.top   = new FormAttachment(0, 0);
        dateComposite.setLayoutData(fdDate);
        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyDateWidget(concept, name, year, month, day, hours, minutes, seconds, message);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);    
        
        year.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        month.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        day.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        hours.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        minutes.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        seconds.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });
        
        return dateComposite;
    }
    
}
