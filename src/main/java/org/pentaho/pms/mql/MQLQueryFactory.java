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

package org.pentaho.pms.mql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.messages.Messages;

/**
 * @deprecated as of metadata 3.0.
 */
public class MQLQueryFactory {

  private static final Log logger = LogFactory.getLog( MQLQueryFactory.class );

  public static MQLQuery getMQLQuery( String XML, DatabaseMeta meta, String locale, CwmSchemaFactoryInterface factory )
    throws PentahoMetadataException {
    // load MQLQuery class instance from properties somewhere
    String mqlQueryClassName = System.getProperty( "org.pentaho.pms.mql.MQLQueryClassName", //$NON-NLS-1$
        "org.pentaho.pms.mql.MQLQueryImpl" ); //$NON-NLS-1$
    return getMQLQuery( mqlQueryClassName, XML, meta, locale, factory );

  }

  public static MQLQuery getMQLQuery( String mqlQueryClassName, String XML, DatabaseMeta meta, String locale,
      CwmSchemaFactoryInterface factory ) throws PentahoMetadataException {
    // load MQLQuery class instance from properties somewhere
    try {
      Class<?> claz = Class.forName( mqlQueryClassName );

      Class<? extends MQLQuery> clazz = (Class<? extends MQLQuery>) claz.asSubclass( MQLQuery.class );

      if ( MQLQuery.class.isAssignableFrom( clazz ) ) {
        Class[] argClasses = { String.class, DatabaseMeta.class, String.class, CwmSchemaFactoryInterface.class };
        Constructor<? extends MQLQuery> constr = clazz.getConstructor( argClasses );
        Object[] vars = { XML, meta, locale, factory };
        return constr.newInstance( vars );
      } else {
        logger.error( Messages.getErrorString(
            "MQLQueryFactory.ERROR_0001_MQLQUERY_CLASS_NOT_ASSIGNABLE", mqlQueryClassName ) ); //$NON-NLS-1$
      }
    } catch ( ClassNotFoundException e ) {
      logger.error(
          Messages.getErrorString( "MQLQueryFactory.ERROR_0002_MQLQUERY_CLASS_NOT_FOUND", mqlQueryClassName ), e ); //$NON-NLS-1$
    } catch ( NoSuchMethodException e ) {
      logger.error( Messages.getErrorString(
          "MQLQueryFactory.ERROR_0003_MQLQUERY_CLASS_DOES_NOT_CONTAIN_CONSTRUCTOR", mqlQueryClassName ), e ); //$NON-NLS-1$
    } catch ( IllegalAccessException e ) {
      logger.error( Messages.getErrorString(
          "MQLQueryFactory.ERROR_0004_MQLQUERY_CLASS_ILLEGAL_ACCESS", mqlQueryClassName ), e ); //$NON-NLS-1$
    } catch ( InstantiationException e ) {
      logger.error( Messages.getErrorString(
          "MQLQueryFactory.ERROR_0005_MQLQUERY_CLASS_CANNOT_INSTANTIATE", mqlQueryClassName ), e ); //$NON-NLS-1$
    } catch ( InvocationTargetException e ) {
      if ( e.getTargetException() instanceof PentahoMetadataException ) {
        throw (PentahoMetadataException) e.getTargetException();
      } else {
        logger.error( Messages.getErrorString(
            "MQLQueryFactory.ERROR_0006_MQLQUERY_CLASS_CANNOT_INVOKE", mqlQueryClassName ), e ); //$NON-NLS-1$
      }
    }
    return null;
  }

}
