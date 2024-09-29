/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.pms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

  public static String readAsXml( String filename ) throws IOException {
    File file = new File( filename );
    FileInputStream fileInputStream = new FileInputStream( file );
    byte[] bytes = new byte[(int) file.length()];
    fileInputStream.read( bytes );
    fileInputStream.close();
    return new String( bytes, Const.XML_ENCODING );
  }

  public static void saveAsXml( String queryFile, String xml ) throws IOException {
    File file = new File( queryFile );
    FileOutputStream fileOutputStream = new FileOutputStream( file );
    fileOutputStream.write( xml.getBytes( Const.XML_ENCODING ) );
    fileOutputStream.close();
  }

}
