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
package org.pentaho.pms.mql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;

import org.pentaho.pms.messages.Messages;

import be.ibridge.kettle.core.database.DatabaseMeta;

public class MQLQueryFactory {
    
  private static final Log logger = LogFactory.getLog(MQLQueryFactory.class);
  
  public static MQLQuery getMQLQuery(String XML, DatabaseMeta meta, String locale, CwmSchemaFactoryInterface factory) throws PentahoMetadataException {
    // load MQLQuery class instance from properties somewhere
    String mqlQueryClassName = 
      System.getProperty(
          "org.pentaho.pms.mql.MQLQueryClassName", //$NON-NLS-1$
          "org.pentaho.pms.mql.MQLQueryImpl"); //$NON-NLS-1$
    return getMQLQuery(mqlQueryClassName, XML, meta, locale, factory);

  }
  
  public static MQLQuery getMQLQuery(String mqlQueryClassName, String XML, DatabaseMeta meta, String locale, CwmSchemaFactoryInterface factory) throws PentahoMetadataException {
    // load MQLQuery class instance from properties somewhere
    try {
      Class clazz = Class.forName(mqlQueryClassName);
      if (MQLQuery.class.isAssignableFrom(clazz)) {
        Class argClasses[] = {String.class, DatabaseMeta.class, String.class, CwmSchemaFactoryInterface.class};
        Constructor constr = clazz.getConstructor(argClasses);
        Object vars[] = {XML, meta, locale, factory};
        return (MQLQuery)constr.newInstance(vars);
      } else {
        logger.error(Messages.getErrorString("MQLQueryFactory.ERROR_0001_MQLQUERY_CLASS_NOT_ASSIGNABLE", mqlQueryClassName)); //$NON-NLS-1$
      }
    } catch (ClassNotFoundException e) {
      logger.error(Messages.getErrorString("MQLQueryFactory.ERROR_0002_MQLQUERY_CLASS_NOT_FOUND", mqlQueryClassName), e); //$NON-NLS-1$
    } catch (NoSuchMethodException e) {
      logger.error(Messages.getErrorString("MQLQueryFactory.ERROR_0003_MQLQUERY_CLASS_DOES_NOT_CONTAIN_CONSTRUCTOR", mqlQueryClassName), e); //$NON-NLS-1$
    } catch (IllegalAccessException e) {
      logger.error(Messages.getErrorString("MQLQueryFactory.ERROR_0004_MQLQUERY_CLASS_ILLEGAL_ACCESS", mqlQueryClassName), e); //$NON-NLS-1$
    } catch (InstantiationException e) {
      logger.error(Messages.getErrorString("MQLQueryFactory.ERROR_0005_MQLQUERY_CLASS_CANNOT_INSTANTIATE", mqlQueryClassName), e); //$NON-NLS-1$
    } catch (InvocationTargetException e) {
      if (e.getTargetException() instanceof  PentahoMetadataException) {
        throw (PentahoMetadataException)e.getTargetException();
      } else {
        logger.error(Messages.getErrorString("MQLQueryFactory.ERROR_0006_MQLQUERY_CLASS_CANNOT_INVOKE", mqlQueryClassName), e); //$NON-NLS-1$
      }
    }
    return null;
  }

}
