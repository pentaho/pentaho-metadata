package org.pentaho.pms.schema.concept.editor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.security.SecurityReference;

/**
 * Instantiates <code>PropertyEditorWidget</code> objects based on property type.
 * @author mlowery
 */
public class PropertyEditorWidgetFactory {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyEditorWidgetFactory.class);

  private static final Map propertyEditorMap;

  private static final Class[] constructorParamTypes = { Composite.class, Integer.TYPE, IConceptModel.class,
      String.class, Map.class };

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  // ~ Methods =========================================================================================================

  static {
    HashMap propertyEditors = new HashMap();
    propertyEditors.put(ConceptPropertyType.STRING, StringPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.DATE, DatePropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.NUMBER, NumberPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.COLOR, ColorPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.FONT, FontPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.FIELDTYPE, FieldTypePropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.AGGREGATION, AggregationPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.BOOLEAN, BooleanPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.DATATYPE, DataTypePropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.LOCALIZED_STRING, LocalizedStringPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.TABLETYPE, TableTypePropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.URL, UrlPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.SECURITY, SecurityPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.ALIGNMENT, AlignmentPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.COLUMN_WIDTH, ColumnWidthPropertyEditorWidget.class);
    propertyEditorMap = Collections.unmodifiableMap(propertyEditors);
  }

  public static IPropertyEditorWidget getWidget(final ConceptPropertyType propertyType, final Composite parent,
      final int style, final IConceptModel conceptModel, final String propertyId, final Map context,
      SecurityReference securityReference) {

    Class clazz = (Class) propertyEditorMap.get(propertyType);
    if (null == clazz) {
      return null;
    }
    Constructor cons = null;
    try {
      ArrayList constParams = new ArrayList(Arrays.asList(constructorParamTypes));
      if (clazz == SecurityPropertyEditorWidget.class) {
        constParams.add(SecurityReference.class);
      }
      cons = clazz.getConstructor((Class[]) constParams.toArray(new Class[0]));
    } catch (SecurityException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      return null;
    } catch (NoSuchMethodException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      return null;
    }

    IPropertyEditorWidget widget = null;
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("parent = " + parent);
        logger.debug("style = " + style);
        logger.debug("conceptModel = " + conceptModel);
        logger.debug("propertyId = " + propertyId);
      }
      ArrayList constructorArgs = new ArrayList();
      constructorArgs.add(parent);
      constructorArgs.add(new Integer(style));
      constructorArgs.add(conceptModel);
      constructorArgs.add(propertyId);
      constructorArgs.add(context);
      if (clazz == SecurityPropertyEditorWidget.class) {
        constructorArgs.add(securityReference);
      }

      widget = (IPropertyEditorWidget) cons.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
    } catch (IllegalArgumentException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      return null;
    } catch (InstantiationException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      return null;
    } catch (IllegalAccessException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      return null;
    } catch (InvocationTargetException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
      return null;
    }
    return widget;
  }

}
