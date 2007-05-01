package org.pentaho.pms.schema.concept.editor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * Instantiates <code>PropertyEditorWidget</code> objects based on property type.
 * @author mlowery
 */
public class PropertyEditorWidgetFactory {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyEditorWidgetFactory.class);

  private static final Map propertyEditorMap;

  private static final Class[] constructorParamTypes = { Composite.class, Integer.TYPE, IConceptModel.class,
      String.class };

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  // ~ Methods =========================================================================================================

  static {
    HashMap propertyEditors = new HashMap();
    propertyEditors.put(ConceptPropertyType.STRING, StringPropertyEditorWidget.class);
    //    propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_DATE, .class);
    //  propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_NUMBER, .class);
    propertyEditors.put(ConceptPropertyType.COLOR, ColorPropertyEditorWidget.class);
    //  propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_FONT, .class);
    //  propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_FIELDTYPE, .class);
    propertyEditors.put(ConceptPropertyType.AGGREGATION, AggregationPropertyEditorWidget.class);
    propertyEditors.put(ConceptPropertyType.BOOLEAN, BooleanPropertyEditorWidget.class);
    //  propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_DATATYPE, .class);
    propertyEditors.put(ConceptPropertyType.LOCALIZED_STRING, LocalizedStringPropertyEditorWidget.class);
    //  propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_TABLETYPE, .class);
    //  propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_URL, .class);
    //  propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_SECURITY, .class);
    //  propertyEditors.put(ConceptPropertyType.PROPERTY_TYPE_ALIGNMENT, .class);
    propertyEditors.put(ConceptPropertyType.COLUMN_WIDTH, ColumnWidthPropertyEditorWidget.class);
    propertyEditorMap = Collections.unmodifiableMap(propertyEditors);
  }

  public static IPropertyEditorWidget getWidget(final SchemaMeta schemaMeta, final ConceptPropertyType propertyType,
      final Composite parent, final int style, final IConceptModel conceptModel, final String propertyId) {

    Class clazz = (Class) propertyEditorMap.get(propertyType);
    if (null == clazz) {
      return null;
    }
    Constructor cons = null;
    try {
      cons = clazz.getConstructor(constructorParamTypes);
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
      widget = (IPropertyEditorWidget) cons.newInstance(makeConstructorArgs(parent, style, conceptModel, propertyId));
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
    if (widget instanceof ISchemaMetaAwarePropertyEditorWidget) {
      ((ISchemaMetaAwarePropertyEditorWidget) widget).setSchemaMeta(schemaMeta);
    }
    return widget;
  }

  private static Object[] makeConstructorArgs(final Composite parent, final int style,
      final IConceptModel conceptModel, final String propertyId) {
    Object[] args = new Object[4];
    args[0] = parent;
    args[1] = new Integer(style);
    args[2] = conceptModel;
    args[3] = propertyId;
    return args;
  }

}
