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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.pms.messages.Messages;

import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.job.entry.JobEntryCopy;
import be.ibridge.kettle.job.entry.JobEntryInterface;

/**
 * This class is used to define a number of default values for various settings throughout Pentaho Metadata.
 * It also contains a number of static final methods to make your life easier.
 *
 * @author Matt 
 * @since 07-05-2003
 *
 */
public class Const
{
	/**
	 *  Version number
	 */
	public static final String VERSION = "0.8.0"; //$NON-NLS-1$

	/**
	 * What's the file systems file separator on this operating system?
	 */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$

	/**
	 * CR: operating systems specific Cariage Return
	 */
	public static final String CR = System.getProperty("line.separator"); //$NON-NLS-1$

	/**
	 * The Java runtime version
	 */
	public static final String JAVA_VERSION = System.getProperty("java.vm.version"); //$NON-NLS-1$

	/**
	 * The margin between the different dialog components & widgets
	 */
	public static final int MARGIN = 3;

	/**
	 * The default percentage of the width of screen where we consider the middle of a dialog.
	 */
	public static final int MIDDLE_PCT = 30;

	/**
	 * The default width of an arrow in the Graphical Views
	 */
	public static final int ARROW_WIDTH = 1;

	/**
	 * The horizontal and vertical margin of a dialog box.
	 */
	public static final int FORM_MARGIN = 3;	

	/**
	 * The default shadow size on the graphical view.
	 */
	public static final int SHADOW_SIZE = 4;

	/**
	 *  The size of relationship symbols
	 */
	public static final int SYMBOLSIZE = 10;

	/**
	 * Max nr. of files to remember
	 */
	public static final int MAX_FILE_HIST = 9;

	/**
	 * The default locale (system defined)
	 */
	public static final Locale DEFAULT_LOCALE = Locale.getDefault();

	/**
	 * The default decimal separator . or ,
	 */
	public static final char DEFAULT_DECIMAL_SEPARATOR = (new DecimalFormatSymbols(DEFAULT_LOCALE)).getDecimalSeparator();

	/**
	 * The default grouping separator , or .
	 */
	public static final char DEFAULT_GROUPING_SEPARATOR = (new DecimalFormatSymbols(DEFAULT_LOCALE)).getGroupingSeparator();

	/**
	 * The default currency symbol
	 */
	public static final String DEFAULT_CURRENCY_SYMBOL = (new DecimalFormatSymbols(DEFAULT_LOCALE)).getCurrencySymbol();

	/**
	 * The default number format
	 */
	public static final String DEFAULT_NUMBER_FORMAT = ((DecimalFormat) (NumberFormat.getInstance())).toPattern();

	/**
	 * Default font name for the fixed width font
	 */
	public static final String FONT_FIXED_NAME = "Courier"; //$NON-NLS-1$

	/**
	 * Default font size for the fixed width font
	 */
	public static final int FONT_FIXED_SIZE = 9;

	/**
	 * Default font type for the fixed width font
	 */
	public static final int FONT_FIXED_TYPE = SWT.NORMAL;

	/**
	 * Default icon size
	 */
	public static final int ICON_SIZE = 48;

	/**
	 * Default line width for arrows & around icons
	 */
	public static final int LINE_WIDTH = 2;

	/**
	 * Default grid size to which the graphical views snap.
	 */
	public static final int GRID_SIZE = 20;

	/**
	 * The minimal size of a note on a graphical view (width & height)
	 */
	public static final int NOTE_MIN_SIZE = 20;

	/**
	 * The margin between the text of a note and its border.
	 */
	public static final int NOTE_MARGIN = 5;

	/**
	 * The default red-component of the background color
	 */
	public static final int COLOR_BACKGROUND_RED = 255;

	/**
	 * The default green-component of the background color
	 */
	public static final int COLOR_BACKGROUND_GREEN = 255;

	/**
	 * The default blue-component of the background color
	 */
	public static final int COLOR_BACKGROUND_BLUE = 255;

	/**
	 * The default red-component of the graph background color
	 */
	public static final int COLOR_GRAPH_RED = 255;

	/**
	 * The default green-component of the graph background color
	 */
	public static final int COLOR_GRAPH_GREEN = 255;

	/**
	 * The default blue-component of the graph background color
	 */
	public static final int COLOR_GRAPH_BLUE = 255;

	/**
	 * The default red-component of the tab selected color
	 */
	public static final int COLOR_TAB_RED = 200;

	/**
	 * The default green-component of the tab selected color
	 */
	public static final int COLOR_TAB_GREEN = 200;

	/**
	 * The default blue-component of the tab selected color
	 */
	public static final int COLOR_TAB_BLUE = 255;

	/**
	 * The default undo level for Kettle
	 */
	public static final int MAX_UNDO = 100;

	/**
	 * Path to the users home directory
	 */
	public static final String USER_HOME_DIRECTORY = Const.isEmpty(System.getProperty("PENTAHO_META_HOME"))?System.getProperty("user.home"):System.getProperty("PENTAHO_META"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * The base name of the Pentaho metadata editor logfile
	 */
	public static final String META_EDITOR_LOG_FILE = "pentaho-meta"; //$NON-NLS-1$

	/**
	 * Default we store our information in Unicode UTF-8 character set.
	 */
	public static final String XML_ENCODING = "UTF-8"; //$NON-NLS-1$

    /**
     * The directory / path to load images from
     */
    public static final String IMAGE_DIRECTORY = "/org/pentaho/pms/images/"; //$NON-NLS-1$

    
    /**
     * Determines the Pentaho metadata base directory in the user's home directory.
     * @return The Pentaho metadata base directory.
     */
    public static final String getBaseDirectory()
    {
        return USER_HOME_DIRECTORY + FILE_SEPARATOR + ".pentaho-meta"; //$NON-NLS-1$
    }
    

    /**
     * @return The properties filename for the Pentaho Metadata Editor
     */
    public static final String getPropertiesFile()
    {
        return Const.getBaseDirectory()+Const.FILE_SEPARATOR+".pme-rc";     //$NON-NLS-1$
    }

    /**
     * @return the saved query file name
     */
    public static String getQueryFile()
    {
        return Const.getBaseDirectory()+Const.FILE_SEPARATOR+".query"; //$NON-NLS-1$
    }
    


    /**
     * See if the pentaho metadata base directory exists, otherwise create it.
     */
    public static final void checkPentahoMetadataDirectory()
    {
        File dir = new File(getBaseDirectory());
        if (!dir.exists())
        {
            try
            {
                dir.mkdirs();
            }
            catch(Exception e)
            {
                // serious problem, serious measures...
                throw new RuntimeException(Messages.getString("Const.ERROR_0001_CANT_CREATE_DIRECTORY")+dir.toString()+"]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }


	/** 
     *  rounds double f to any number of places after decimal point
	 *  Does arithmetic using BigDecimal class to avoid integer overflow while rounding
	 *  
	 * @param f The value to round
	 * @param places The number of decimal places
	 * @return The rounded floating point value
	 */

	public static final double round(double f, int places)
	{
		java.math.BigDecimal bdtemp = new java.math.BigDecimal(f);
		bdtemp = bdtemp.setScale(places, java.math.BigDecimal.ROUND_HALF_EVEN);
		return bdtemp.doubleValue();
	}

	/**
	 * Convert a String into an integer.  If the conversion fails, assign a default value.
	 * @param str The String to convert to an integer 
	 * @param def The default value
	 * @return The converted value or the default.
	 */
	public static final int toInt(String str, int def)
	{
		int retval;
		try
		{
			retval = Integer.parseInt(str);
		} catch (Exception e)
		{
			retval = def;
		}		
		return retval;
	}

	/**
	 * Convert a String into a long integer.  If the conversion fails, assign a default value.
	 * @param str The String to convert to a long integer
	 * @param def The default value
	 * @return The converted value or the default.
	 */
	public static final long toLong(String str, long def)
	{
		long retval;
		try
		{
			retval = Long.parseLong(str);
		} catch (Exception e)
		{
			retval = def;
		}
		return retval;
	}

	/**
	 * Convert a String into a double.  If the conversion fails, assign a default value.
	 * @param str The String to convert to a double
	 * @param def The default value
	 * @return The converted value or the default.
	 */
	public static final double toDouble(String str, double def)
	{
		double retval;
		try
		{
			retval = Double.parseDouble(str);
		} catch (Exception e)
		{
			retval = def;
		}
		return retval;
	}

	/**
	 * Convert a String into a date.  
	 * The date format is <code>yyyy/MM/dd HH:mm:ss.SSS</code>.  
	 * If the conversion fails, assign a default value.
	 * @param str The String to convert into a Date
	 * @param def The default value
	 * @return The converted value or the default.
	 */
	public static final Date toDate(String str, Date def)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.US); //$NON-NLS-1$
		try
		{
			return df.parse(str);
		} catch (ParseException e)
		{
			return def;
		}
	}

	/**
	 * Right trim: remove spaces to the right of a string
	 * @param str The string to right trim
	 * @return The trimmed string.
	 */
	public static final String rtrim(String str)
	{
		int max = str.length();
		while (max > 0 && isSpace(str.charAt(max - 1)))
			max--;

		return str.substring(0, max);
	}

	/**
	 * Determines whether or not a character is considered a space.
	 * A character is considered a space in Kettle if it is a space, a tab, a newline or a cariage return.
	 * @param c The character to verify if it is a space.
	 * @return true if the character is a space. false otherwise. 
	 */
	public static final boolean isSpace(char c)
	{
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
	}

	/**
	 * Left trim: remove spaces to the left of a String.
	 * @param str The String to left trim
	 * @return The left trimmed String
	 */
	public static final String ltrim(String str)
	{
		int from = 0;
		while (from < str.length() && isSpace(str.charAt(from)))
			from++;

		return str.substring(from);
	}

	/**
	 * Trims a string: removes the leading and trailing spaces of a String.
	 * @param str The string to trim
	 * @return The trimmed string.
	 */
	public static final String trim(String str)
	{
		int max = str.length() - 1;
		int min = 0;

		while (min <= max && isSpace(str.charAt(min)))
			min++;
		while (max >= 0 && isSpace(str.charAt(max)))
			max--;

		if (max < min)
			return ""; //$NON-NLS-1$

		return str.substring(min, max + 1);
	}

	/**
	 * Right pad a string: adds spaces to a string until a certain length.
	 * If the length is smaller then the limit specified, the String is truncated.
	 * @param ret The string to pad
	 * @param limit The desired length of the padded string.
	 * @return The padded String.
	 */
	public static final String rightPad(String ret, int limit)
	{
		if (ret == null)
			return rightPad(new StringBuffer(), limit);
		else
			return rightPad(new StringBuffer(ret), limit);
	}

	/**
	 * Right pad a StringBuffer: adds spaces to a string until a certain length.
	 * If the length is smaller then the limit specified, the String is truncated.
	 * @param ret The StringBuffer to pad
	 * @param limit The desired length of the padded string.
	 * @return The padded String.
	 */
	public static final String rightPad(StringBuffer ret, int limit)
	{
		int len = ret.length();
		int l;

		if (len > limit)
		{
			ret.setLength(limit);
		} else
		{
			for (l = len; l < limit; l++)
				ret.append(' ');
		}
		return ret.toString();
	}

	/**
	 * Replace values in a String with another.
	 * @param string The original String.
	 * @param repl The text to replace
	 * @param with The new text bit
	 * @return The resulting string with the text pieces replaced.
	 */
	public static final String replace(String string, String repl, String with)
	{
		StringBuffer str = new StringBuffer(string);
		for (int i = str.length() - 1; i >= 0; i--)
		{
			if (str.substring(i).startsWith(repl))
			{
				str.delete(i, i + repl.length());
				str.insert(i, with);
			}
		}
		return str.toString();
	}

	/**
	 * Alternate faster version of string replace using a stringbuffer as input.
	 * 
	 * @param str The string where we want to replace in
	 * @param code The code to search for
	 * @param repl The replacement string for code
	 */
	public static void repl(StringBuffer str, String code, String repl)
	{
		int clength = code.length();

		int i = str.length() - clength;

		while (i >= 0)
		{
			String look = str.substring(i, i + clength);
			if (look.equalsIgnoreCase(code)) // Look for a match!
			{
				str.replace(i, i + clength, repl);
			}
			i--;
		}
	}

	/**
	 * Count the number of spaces to the left of a text. (leading)
	 * @param field The text to examine
	 * @return The number of leading spaces found.
	 */
	public static final int nrSpacesBefore(String field)
	{
		int nr = 0;
		int len = field.length();
		while (nr < len && field.charAt(nr) == ' ')
		{
			nr++;
		}
		return nr;
	}

	/**
	 * Count the number of spaces to the right of a text. (trailing)
	 * @param field The text to examine
	 * @return The number of trailing spaces found.
	 */
	public static final int nrSpacesAfter(String field)
	{
		int nr = 0;
		int len = field.length();
		while (nr < len && field.charAt(field.length() - 1 - nr) == ' ')
		{
			nr++;
		}
		return nr;
	}

	/**
	 * Checks whether or not a String consists only of spaces.
	 * @param str The string to check
	 * @return true if the string has nothing but spaces.
	 */
	public static final boolean onlySpaces(String str)
	{
		for (int i = 0; i < str.length(); i++)
			if (!isSpace(str.charAt(i)))
				return false;
		return true;
	}

	/**
	 * determine the OS name
	 * @return The name of the OS
	 */
	public static final String getOS()
	{
		return System.getProperty("os.name"); //$NON-NLS-1$
	}

	/** 
	 * @return True if the OS is a Windows diravate. 
	 */
	public static final boolean isWindows()
	{
		return getOS().startsWith("Windows"); //$NON-NLS-1$
	}
    
    /**
     * @return true if the OS is Apple OSX
     * @see <a href="http://developer.apple.com/technotes/tn2002/tn2110.html">Apple developer documentation</a>
     */
    public static final boolean isOSX()
    {
        return getOS().toLowerCase().startsWith("mac os x"); //$NON-NLS-1$
    }

    /**
     * Determine the hostname of the machine Kettle is running on
     * @return The hostname
     */
    public static final String getHostname()
    {
        String lastHostname = "localhost"; //$NON-NLS-1$
        try
        {
            Enumeration en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements())
            {
                NetworkInterface nwi = (NetworkInterface) en.nextElement();
                //System.out.println("nwi : "+nwi.getName()+" ("+nwi.toString()+")");
                Enumeration ip = nwi.getInetAddresses();

                while (ip.hasMoreElements())
                {
                    InetAddress in = (InetAddress) ip.nextElement();
                    lastHostname=in.getHostName();
                    //System.out.println("  ip address bound : "+in.getHostAddress());
                    //System.out.println("  hostname         : "+in.getHostName());
                    //System.out.println("  Cann.hostname    : "+in.getCanonicalHostName());
                    //System.out.println("  ip string        : "+in.toString());
                    if (!lastHostname.equalsIgnoreCase("localhost") && !(lastHostname.indexOf(":")>=0) ) //$NON-NLS-1$ //$NON-NLS-2$
                    {
                        return lastHostname;
                    }
                }
            }
        } catch (SocketException e)
        {

        }

        return lastHostname;
    }

	/**
	 * Determins the IP address of the machine Kettle is running on.
	 * @return The IP address
	 */
	public static final String getIPAddress()
	{
		try
		{
			Enumeration enumInterfaces = NetworkInterface.getNetworkInterfaces();
			while (enumInterfaces.hasMoreElements())
			{
				NetworkInterface nwi = (NetworkInterface) enumInterfaces.nextElement();
				Enumeration ip = nwi.getInetAddresses();
				while (ip.hasMoreElements())
				{
					InetAddress in = (InetAddress) ip.nextElement();
					if (!in.isLoopbackAddress() && in.toString().indexOf(":") < 0) //$NON-NLS-1$
					{
						return in.getHostAddress();
					}
				}
			}
		} catch (SocketException e)
		{

		}

		return "127.0.0.1"; //$NON-NLS-1$
	}

	/**
	 * Tries to determine the MAC address of the machine Kettle is running on.
	 * @return The MAC address.
	 */
	public static final String getMACAddress()
	{
		String ip = getIPAddress();
		String mac = "none"; //$NON-NLS-1$
		String os = getOS();
		String s = ""; //$NON-NLS-1$

		//System.out.println("os = "+os+", ip="+ip);

		if (os.equalsIgnoreCase("Windows NT") || os.equalsIgnoreCase("Windows 2000") || os.equalsIgnoreCase("Windows XP") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				|| os.equalsIgnoreCase("Windows 95") || os.equalsIgnoreCase("Windows 98") || os.equalsIgnoreCase("Windows Me") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				|| os.startsWith("Windows")) //$NON-NLS-1$
		{
			try
			{
				// System.out.println("EXEC> nbtstat -a "+ip);

				Process p = Runtime.getRuntime().exec("nbtstat -a " + ip); //$NON-NLS-1$

				// read the standard output of the command
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while (!procDone(p))
				{
					while ((s = stdInput.readLine()) != null)
					{
						// System.out.println("NBTSTAT> "+s);
						if (s.indexOf("MAC") >= 0) //$NON-NLS-1$
						{
							int idx = s.indexOf("="); //$NON-NLS-1$
							mac = s.substring(idx + 2);
						}
					}
				}
				stdInput.close();
			} catch (Exception e)
			{

			}
		} else if (os.equalsIgnoreCase("Linux")) //$NON-NLS-1$
		{
			try
			{
				Process p = Runtime.getRuntime().exec("/sbin/ifconfig -a"); //$NON-NLS-1$

				// read the standard output of the command
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while (!procDone(p))
				{
					while ((s = stdInput.readLine()) != null)
					{
						int idx = s.indexOf("HWaddr"); //$NON-NLS-1$
						if (idx >= 0)
						{
							mac = s.substring(idx + 7);
						}
					}
				}
				stdInput.close();
			} catch (Exception e)
			{

			}
		} else if (os.equalsIgnoreCase("Solaris")) //$NON-NLS-1$
		{
			try
			{
				Process p = Runtime.getRuntime().exec("/usr/sbin/ifconfig -a"); //$NON-NLS-1$

				// read the standard output of the command
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while (!procDone(p))
				{
					while ((s = stdInput.readLine()) != null)
					{
						int idx = s.indexOf("ether"); //$NON-NLS-1$
						if (idx >= 0)
						{
							mac = s.substring(idx + 6);
						}
					}
				}
				stdInput.close();
			} catch (Exception e)
			{

			}
		} else if (os.equalsIgnoreCase("HP-UX")) //$NON-NLS-1$
		{
			try
			{
				Process p = Runtime.getRuntime().exec("/usr/sbin/lanscan -a"); //$NON-NLS-1$

				// read the standard output of the command
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while (!procDone(p))
				{
					while ((s = stdInput.readLine()) != null)
					{
						if (s.indexOf("MAC") >= 0) //$NON-NLS-1$
						{
							int idx = s.indexOf("0x"); //$NON-NLS-1$
							mac = s.substring(idx + 2);
						}
					}
				}
				stdInput.close();
			} catch (Exception e)
			{

			}
		}

		return Const.trim(mac);
	}

	private static final boolean procDone(Process p)
	{
		try
		{
			p.exitValue();
			return true;
		} catch (IllegalThreadStateException e)
		{
			return false;
		}
	}

    /**
     * Determine the level of where the TreeItem is position in a tree.
     * @param ti The TreeItem
     * @param nrLevelsToSkip 
     * @return The level of the item in the tree
     */
    public static final int getTreeLevel(TreeItem ti)
    {
        return getTreeLevel(ti, 0);
    }
    
	/**
	 * Determine the level of where the TreeItem is position in a tree.
	 * @param ti The TreeItem
	 * @param nrLevelsToSkip the number of levels to skip 
	 * @return The level of the item in the tree
	 */
	public static final int getTreeLevel(TreeItem ti, int nrLevelsToSkip)
	{
		int level = 1;
		TreeItem parent = ti.getParentItem();
		while (parent != null)
		{
			level++;
			parent = parent.getParentItem();
		}
        
        level-=nrLevelsToSkip;
        if (level<0) level=0;
		return level;
	}


    /**
     * Get an array of strings containing the path from the given TreeItem to the parent.
     * @param ti The TreeItem to look at
     * @return An array of string describing the path to the TreeItem.
     */
    public static final String[] getTreeStrings(TreeItem ti)
    {
        return getTreeStrings(ti, 0);
    }
    
	/**
	 * Get an array of strings containing the path from the given TreeItem to the parent.
	 * @param ti The TreeItem to look at
     * @param nrLevelsToSkip the number of levels to skip at the root of the tree.
	 * @return An array of string describing the path to the TreeItem.
	 */
	public static final String[] getTreeStrings(TreeItem ti, int nrLevelsToSkip)
	{
		int nrLevels = getTreeLevel(ti, nrLevelsToSkip);
		String retval[] = new String[nrLevels];
        if (nrLevels==0) return retval;
        
		int current = nrLevels - 1;
        
		retval[current] = ti.getText();
		TreeItem parent = ti.getParentItem();
        current--;
        
		while (current>=0)
		{
			retval[current] = parent.getText();
			parent          = parent.getParentItem();
            current--;
		}

		return retval;
	}
    

	/**
	 * Return the tree path seperated by Const.FILE_SEPARATOR, starting from a certain depth in the tree.
	 *
	 * @param ti The TreeItem to get the path for 
	 * @param from The depth to start at, use 0 to get the complete tree.
	 * @return The tree path.
	 */
	public static final String getTreePath(TreeItem ti, int from)
	{
		String path[] = getTreeStrings(ti);

		if (path == null)
			return null;

		String retval = ""; //$NON-NLS-1$

		for (int i = from; i < path.length; i++)
		{
			if (!path[i].equalsIgnoreCase(Const.FILE_SEPARATOR))
			{
				retval += Const.FILE_SEPARATOR + path[i];
			}
		}

		return retval;
	}

	/**
	 * Flips the TreeItem from expanded to not expanded or vice-versa.
	 * @param ti The TreeItem to flip.
	 */
	public static final void flipExpanded(TreeItem ti)
	{
		ti.setExpanded(!ti.getExpanded());
	}

	/**
	 * Finds a TreeItem with a certain label (name) in a (part of a) tree.
	 * @param parent The TreeItem where we start looking.
	 * @param name The name or item label to look for.
	 * @return The TreeItem if the label was found, null if nothing was found.
	 */
	public static final TreeItem findTreeItem(TreeItem parent, String name)
	{
		if (parent.getText().equalsIgnoreCase(name))
			return parent;

		TreeItem ti[] = parent.getItems();
		for (int i = 0; i < ti.length; i++)
		{
			TreeItem child = findTreeItem(ti[i], name);
			if (child != null)
				return child;
		}
		return null;
	}

	/**
	 * Find a database with a certain name in an arraylist of databases.
	 * @param databases The ArrayList of databases
	 * @param dbname The name of the database connection
	 * @return The database object if one was found, null otherwise.
	 */
	public static final DatabaseMeta findDatabase(List databases, String dbname)
	{
		if (databases == null)
			return null;

		for (int i = 0; i < databases.size(); i++)
		{
			DatabaseMeta ci = (DatabaseMeta) databases.get(i);
			if (ci.getName().equalsIgnoreCase(dbname))
				return ci;
		}
		return null;
	}
    
    /**
     * Find a database with a certain name in an arraylist of databases.
     * @param databases The ArrayList of databases
     * @param dbname The name of the database connection
     * @return The database object if one was found, null otherwise.
     */
    public static final DatabaseMeta findDatabase(List databases, String dbname, String exclude)
    {
        if (databases == null)
            return null;

        for (int i = 0; i < databases.size(); i++)
        {
            DatabaseMeta ci = (DatabaseMeta) databases.get(i);
            if (ci.getName().equalsIgnoreCase(dbname))
                return ci;
        }
        return null;
    }

	/**
	 * Find a database with a certain ID in an arraylist of databases.
	 * @param databases The ArrayList of databases
	 * @param id The id of the database connection
	 * @return The database object if one was found, null otherwise.
	 */
	public static final DatabaseMeta findDatabase(List databases, long id)
	{
		if (databases == null)
			return null;

		for (int i = 0; i < databases.size(); i++)
		{
			DatabaseMeta ci = (DatabaseMeta) databases.get(i);
			if (ci.getID() == id)
				return ci;
		}
		return null;
	}

	/**
	 * Select the SAP R/3 databases in the List of databases.
	 * @param databases All the databases
	 * @return SAP R/3 databases in a List of databases.
	 */
	public static final List selectSAPR3Databases(List databases)
	{
		List sap = new ArrayList();

		Iterator it = databases.iterator();
		while (it.hasNext())
		{
			DatabaseMeta db = (DatabaseMeta) it.next();
			if (db.getDatabaseType() == DatabaseMeta.TYPE_DATABASE_SAPR3)
			{
				sap.add(db);
			}
		}

		return sap;
	}

	/**
	 * Select the SAP R/3 databases in the List of databases.
	 * @param databases All the databases
	 * @return SAP R/3 databases in a List of databases.
	 * @deprecated
	 */
	public static final ArrayList selectSAPR3Databases(ArrayList databases)
	{
		return (ArrayList)selectSAPR3Databases((List)databases);
	}

	/**
	 * Find a jobentry with a certain ID in a list of job entries.
	 * @param jobentries The List of jobentries
	 * @param id_jobentry The id of the jobentry
	 * @return The JobEntry object if one was found, null otherwise.
	 */
	public static final JobEntryInterface findJobEntry(List jobentries, long id_jobentry)
	{
		if (jobentries == null)
			return null;

		for (int i = 0; i < jobentries.size(); i++)
		{
			JobEntryInterface je = (JobEntryInterface) jobentries.get(i);
			if (je.getID() == id_jobentry)
				return je;
		}
		return null;
	}

	/**
	 * Find a jobentrycopy with a certain ID in a list of job entry copies.
	 * @param jobcopies The List of jobentry copies
	 * @param id_jobentry_copy The id of the jobentry copy
	 * @return The JobEntryCopy object if one was found, null otherwise.
	 */
	public static final JobEntryCopy findJobEntryCopy(List jobcopies, long id_jobentry_copy)
	{
		if (jobcopies == null)
			return null;

		for (int i = 0; i < jobcopies.size(); i++)
		{
			JobEntryCopy jec = (JobEntryCopy) jobcopies.get(i);
			if (jec.getID() == id_jobentry_copy)
				return jec;
		}
		return null;
	}

	/**
	 * Gets the value of a commandline option 
	 * @param args The command line arguments
	 * @param option The option to look for
	 * @return The value of the commandline option specified.
	 * @deprecated
	 */
	public static final String getCommandlineOption(List args, String option)
	{
		String optionStart[] = new String[] { "-", "/" }; //$NON-NLS-1$ //$NON-NLS-2$
		String optionDelim[] = new String[] { "=", ":" }; //$NON-NLS-1$ //$NON-NLS-2$

		for (int s = 0; s < optionStart.length; s++)
		{
			for (int d = 0; d < optionDelim.length; d++)
			{
				String optstr = optionStart[s] + option + optionDelim[d];
				String retval = searchCommandLineOption(args, optstr);
				if (retval != null)
					return retval;
			}
		}
		return null;
	}

	/**
	 * Searches a command line option with a certain prefix in a list of command line options
	 * @param args The list to search
	 * @param prefix The prefix to look for
	 * @return The content.
	 */
	private static final String searchCommandLineOption(List args, String prefix)
	{
		String retval = null;

		for (int i = args.size() - 1; i >= 0; i--)
		{
			String arg = (String) args.get(i);
			if (arg != null && arg.toUpperCase().startsWith(prefix.toUpperCase()))
			{
				retval = arg.substring(prefix.length());

				// remove this options from the arguments list...
				// This is why we go from back to front...
				args.remove(i);

				// System.out.println("Option ["+prefix+"] found: ["+retval+"]");
			}
		}
		return retval;
	}

	/**
	 * Retrieves the content of an environment variable
	 * 
	 * @param variable The name of the environment variable
	 * @param deflt The default value in case no value was found
	 * @return The value of the environment variable or the value of deflt in case no variable was defined.
	 */
	public static String getEnvironmentVariable(String variable, String deflt)
	{
		return System.getProperty(variable, deflt);
	}

	/**
	 * Replaces environment variables in a string.
	 * For example if you set KETTLE_HOME as an environment variable, you can 
	 * use %%KETTLE_HOME%% in dialogs etc. to refer to this value.
	 * This procedures looks for %%...%% pairs and replaces them including the 
	 * name of the environment variable with the actual value.
	 * In case the variable was not set, nothing is replaced!
	 * 
	 * @param string The source string where text is going to be replaced.
	 *  
	 * @return The expanded string.
	 * @deprecated use StringUtil.environmentSubstitute(): handles both Windows and unix conventions
	 */
	public static final String replEnv(String string)
	{
		if (string == null)
			return null;
		StringBuffer str = new StringBuffer(string);

		int idx = str.indexOf("%%"); //$NON-NLS-1$
		while (idx >= 0)
		{
			//OK, so we found a marker, look for the next one...
			int to = str.indexOf("%%", idx + 2); //$NON-NLS-1$
			if (to >= 0)
			{
				// OK, we found the other marker also...
				String marker = str.substring(idx, to + 2);
				String var = str.substring(idx + 2, to);

				if (var != null && var.length() > 0)
				{
					// Get the environment variable
					String newval = getEnvironmentVariable(var, null);

					if (newval != null)
					{
						// Replace the whole bunch
						str.replace(idx, to + 2, newval);
						//System.out.println("Replaced ["+marker+"] with ["+newval+"]");

						// The last position has changed...
						to += newval.length() - marker.length();
					}
				}

			} else
			// We found the start, but NOT the ending %% without closing %%
			{
				to = idx;
			}

			// Look for the next variable to replace...
			idx = str.indexOf("%%", to + 1); //$NON-NLS-1$
		}

		return str.toString();
	}

	/**
	 * Replaces environment variables in an array of strings.<p>
	 * See also: replEnv(String string)
	 * @param string The array of strings that wants its variables to be replaced.
	 * @return the array with the environment variables replaced.
	 * @deprecated please use StringUtil.environmentSubstitute now.
	 */
	public static final String[] replEnv(String string[])
	{
		String retval[] = new String[string.length];
		for (int i = 0; i < string.length; i++)
		{
			retval[i] = Const.replEnv(string[i]);
		}
		return retval;
	}

	/**
	 * Implements Oracle style NVL function
	 * @param source The source argument
	 * @param def The default value in case source is null or the length of the string is 0
	 * @return source if source is not null, otherwise return def
	 */
	public static final String NVL(String source, String def)
	{
		if (source == null || source.length() == 0)
			return def;
		return source;
	}

	/**
	 * Search for a string in an array of strings and return the index.
	 * @param lookup The string to search for
	 * @param array The array of strings to look in
	 * @return The index of a search string in an array of strings. -1 if not found.
	 */
	public static final int indexOfString(String lookup, String array[])
	{
		if (array == null)
			return -1;
		if (lookup == null)
			return -1;

		for (int i = 0; i < array.length; i++)
		{
			if (lookup.equalsIgnoreCase(array[i]))
				return i;
		}
		return -1;
	}

	/**
	 * Search for a string in a list of strings and return the index.
	 * @param lookup The string to search for
	 * @param list The ArrayList of strings to look in
	 * @return The index of a search string in an array of strings. -1 if not found.
	 */
	public static final int indexOfString(String lookup, List list)
	{
		if (list == null)
			return -1;

		for (int i = 0; i < list.size(); i++)
		{
			String compare = (String) list.get(i);
			if (lookup.equalsIgnoreCase(compare))
				return i;
		}
		return -1;
	}

	/**
	 * Sort the strings of an array in alphabetical order.
	 * @param input The array of strings to sort.
	 * @return The sorted array of strings.
	 */
	public static final String[] sortStrings(String input[])
	{
		Arrays.sort(input);
		return input;
	}
	
	/**
	 * Convert strings separated by a string into an array of strings.<p>
	 * <code>
	 Example: a;b;c;d    ==  new String[] { a, b, c, d }
	 * </code>
	 *  
	 * @param string The string to split
	 * @param separator The separator used.
	 * @return the string split into an array of strings
	 * 
	 * @deprecated
	 */
	public static final String[] splitString(String string, String separator)
	{
		/*
		 *           0123456
		 *   Example a;b;c;d    -->    new String[] { a, b, c, d }
		 */
		// System.out.println("splitString ["+path+"] using ["+separator+"]");
		List list = new ArrayList();

		if (string == null || string.length() == 0)
		{
			return new String[] {};
		}

		int sepLen = separator.length();
		int from = 0;
	    int end = string.length() - sepLen + 1;

		for (int i = from; i < end; i += sepLen)
		{
			if (string.substring(i, i + sepLen).equalsIgnoreCase(separator))
			{
				// OK, we found a separator, the string to add to the list
				// is [from, i[
				list.add(NVL(string.substring(from, i), "")); //$NON-NLS-1$
				from = i + sepLen;
			}
		}

		// Wait, if the string didn't end with a separator, we still have information at the end of the string...
		// In our example that would be "d"...
		if (from + sepLen <= string.length())
		{
			list.add(NVL(string.substring(from, string.length()), "")); //$NON-NLS-1$
		}

		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Convert strings separated by a character into an array of strings.<p>
	 * <code>
	 Example: a;b;c;d    ==  new String[] { a, b, c, d }
	 * </code>
	 *  
	 * @param string The string to split
	 * @param separator The separator used.
	 * @return the string split into an array of strings
	 */
	public static final String[] splitString(String string, char separator)
	{
		/*
		 *           0123456
		 *   Example a;b;c;d    -->    new String[] { a, b, c, d }
		 */
		// System.out.println("splitString ["+path+"] using ["+separator+"]");
		List list = new ArrayList();

		if (string == null || string.length() == 0)
		{
			return new String[] {};
		}

		int from = 0;
	    int end = string.length();

		for (int i = from; i < end; i += 1)
		{
			if (string.charAt(i) == separator)
			{
				// OK, we found a separator, the string to add to the list
				// is [from, i[
				list.add(NVL(string.substring(from, i), "")); //$NON-NLS-1$
				from = i + 1;
			}
		}

		// Wait, if the string didn't end with a separator, we still have information at the end of the string...
		// In our example that would be "d"...
		if (from + 1 <= string.length())
		{
			list.add(NVL(string.substring(from, string.length()), "")); //$NON-NLS-1$
		}

		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/**
	 * Convert strings separated by a string into an array of strings.<p>
	 * <code>
	 *   Example /a/b/c --> new String[] { a, b, c }
	 * </code>
	 *  
	 * @param path The string to split
	 * @param separator The separator used.
	 * @return the string split into an array of strings
	 */
	public static final String[] splitPath(String path, String separator)
	{
		/*
		 *           012345
		 *   Example /a/b/c    -->    new String[] { a, b, c }
		 */
		// System.out.println("splitString ["+path+"] using ["+separator+"]");
		if (path == null || path.length() == 0 || path.equals(separator))
		{
			return new String[] {};
		}
		int sepLen = separator.length();
		int nr_separators = 0;
		int from = path.startsWith(separator) ? sepLen : 0;
		if (from != 0)
			nr_separators++;

		for (int i = from; i < path.length(); i += sepLen)
		{
			if (path.substring(i, i + sepLen).equalsIgnoreCase(separator))
			{
				nr_separators++;
			}
		}

		String spath[] = new String[nr_separators];
		int nr = 0;
		for (int i = from; i < path.length(); i += sepLen)
		{
			if (path.substring(i, i + sepLen).equalsIgnoreCase(separator))
			{
				spath[nr] = path.substring(from, i);
				// System.out.println(nr+" --> ["+spath[nr]+"], (from,to)=("+from+", "+i+")");
				nr++;

				from = i + sepLen;
			}
		}
		if (nr < spath.length)
		{
			spath[nr] = path.substring(from);
			// System.out.println(nr+" --> ["+spath[nr]+"], (from,to)=("+from+", "+path.length()+")");
		}

		// 
		// a --> { a }
		//
		if (spath.length == 0 && path.length() > 0)
		{
			spath = new String[] { path };
		}

		return spath;
	}


	/**
	 * Sorts the array of Strings, determines the uniquely occuring strings.  
	 * @param strings the array that you want to do a distinct on
	 * @return a sorted array of uniquely occuring strings
	 */
	public static final String[] getDistinctStrings(String[] strings)
	{
		if (strings == null)
			return null;
		if (strings.length == 0)
			return new String[] {};

		String[] sorted = sortStrings(strings);
		List result = new ArrayList();
		String previous = ""; //$NON-NLS-1$
		for (int i = 0; i < sorted.length; i++)
		{
			if (!sorted[i].equalsIgnoreCase(previous))
			{
				result.add(sorted[i]);
			}
			previous = sorted[i];
		}

		return (String[]) result.toArray(new String[result.size()]);
	}
    
    /**
     * Returns a string of the stack trace of the specified exception
     */
    public static final String getStackTracker(Throwable e)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String string = stringWriter.getBuffer().toString();
        try { stringWriter.close(); } catch(IOException ioe) {} // is this really required?
        return string;
    }
    
    /**
     * Check if the string supplied is empty.  A String is empty when it is null or when the length is 0
     * @param string The string to check
     * @return true if the string supplied is empty
     */
    public static final boolean isEmpty(String string)
    {
    	return string==null || string.length()==0;
    }
    
    /**
     * Check if the stringBuffer supplied is empty.  A StringBuffer is empty when it is null or when the length is 0
     * @param string The stringBuffer to check
     * @return true if the stringBuffer supplied is empty
     */
    public static final boolean isEmpty(StringBuffer string)
    {
    	return string==null || string.length()==0;
    }
    

    /**
     * @return a new ClassLoader
     */
    public static final ClassLoader createNewClassLoader() throws KettleException
    {
        try
        {
            // Nothing really in URL, everything is in scope.
            URL urls[] = new URL[] { };
            URLClassLoader ucl = new URLClassLoader(urls);

            return ucl;
        }
        catch (Exception e)
        {
            throw new KettleException(Messages.getString("Const.ERROR_0002_UNEXPECTED_CLASSLOADER_CREATION"), e); //$NON-NLS-1$
        }
    }
    
    /**
     * Utility class for use in JavaScript to create a new byte array.
     * This is surprisingly difficult to do in JavaScript.
     * 
     * @return a new java byte array
     */
    public static final byte[] createByteArray(int size)
    {
        return new byte[size];
    }

    /**
     * Sets the first character of each word in upper-case.
     * @param string The strings to convert to initcap
     * @return the input string but with the first character of each word converted to upper-case.
     */
    public static final String initCap(String string)
    {
        StringBuffer change=new StringBuffer(string);
        boolean new_word;
        int i;
        char lower, upper, ch;
            
        new_word=true;
        for (i=0 ; i<string.length() ; i++)
        {
            lower=change.substring(i,i+1).toLowerCase().charAt(0); // Lowercase is default.
            upper=change.substring(i,i+1).toUpperCase().charAt(0); // Uppercase for new words.
            ch=upper;
    
            if (new_word)
            { 
              change.setCharAt(i, upper);
            }
            else
            {          
              change.setCharAt(i, lower);  
            }

            new_word = false;
    
            if ( !(ch>='A' && ch<='Z') && 
                 !(ch>='0' && ch<='9') &&
                 ch!='_'
               ) new_word = true;
        }
    
        return change.toString();
    }


    /**
     * Convert a normal name with spaces into an ID: with underscores replacing the spaces, etc.
     * @param name the name to convert to an ID 
     * @return The ID-ified name
     */
    public static final String toID(String name)
    {
        name = Const.replace(name, " ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, ".", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, ",", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, ":", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "(", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, ")", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "{", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "}", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "[", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "*", "_TIMES_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "/", "_DIVIDED_BY_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "+", "_PLUS_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "-", "_MINUS_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "____", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "___", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        name = Const.replace(name, "__", "_"); //$NON-NLS-1$ //$NON-NLS-2$

        return name;
    }


    /**
     * Convert a ID with underscored into a name: with spaces and capital etc...
     * @param id the id to convert to a name 
     * @return The name converted from an ID
     */
    public static String fromID(String id)
    {
       id = Const.replace(id, "_", " "); //$NON-NLS-1$ //$NON-NLS-2$
       id = Const.initCap(id);
       
       return id;
    }
}
