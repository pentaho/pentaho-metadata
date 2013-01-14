/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.util.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.Log4JLayoutInterface;
import org.pentaho.di.core.logging.LogMessage;
import org.pentaho.pms.messages.Messages;

@SuppressWarnings("deprecation")
public class Log4jPMELayout extends Layout implements Log4JLayoutInterface
{
    private boolean timeAdded;
    
    public Log4jPMELayout(boolean addTime)
    {
        this.timeAdded = addTime;
    }

    public String format(LoggingEvent event)
    {
        // OK, perhaps the logging information has multiple lines of data.
        // We need to split this up into different lines and all format these lines...
        String line=""; //$NON-NLS-1$
        
        String dateTimeString = ""; //$NON-NLS-1$
        if (timeAdded)
        {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //$NON-NLS-1$
            dateTimeString = df.format(new Date(event.timeStamp))+" - "; //$NON-NLS-1$
        }

        Object object = event.getMessage();
        if (object instanceof LogMessage)
        {
          LogMessage message = (LogMessage)object;

            String parts[] = message.getMessage().split(Const.CR);
            for (int i=0;i<parts.length;i++)
            {
                // Start every line of the output with a dateTimeString
                line+=dateTimeString;
                
                // Include the subject too on every line...
                if (message.getSubject()!=null)
                {
                    line+=message.getSubject()+" - "; //$NON-NLS-1$
                }
                
                if (message.isError())  
                {
                    line+=Messages.getString("Log4jPMELayout.ERROR_LOG4J_ERROR");                 //$NON-NLS-1$
                 }
                
                line+=parts[i];
                if (i<parts.length-1) line+=Const.CR; // put the CR's back in there!
            }
        }
        else
        {
            line+=dateTimeString+object.toString();
        }
        
        return line;
    }

    public boolean ignoresThrowable()
    {
        return false;
    }

    public void activateOptions()
    {
    }

    public boolean isTimeAdded()
    {
        return timeAdded;
    }

    public void setTimeAdded(boolean addTime)
    {
        this.timeAdded = addTime;
    }

}
