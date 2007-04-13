package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

/**
 * Helper for getting sections, properties belonging to sections, and a section for a given property. Maintains
 * arbitrary ordering of sections and properties.
 * @author mlowery
 */
public class PropertySectionHelper {

  // ~ Static fields/initializers ======================================================================================

  /**
   * Keys are property IDs. Values are section names.
   */
  private static OrderedMap sectionMapping = new ListOrderedMap();

  /**
   * Not to be accessed except via <code>getOrderedSections()</code>.
   */
  private static ListOrderedMap wrappedPropertyMap = new ListOrderedMap();

  /**
   * Keys are section names. Values are collections of property IDs.
   */
  private static MultiValueMap propertyMapping = MultiValueMap.decorate(wrappedPropertyMap);

  public static final String SECTION_GENERAL = "General";

  public static final String SECTION_FORMATTING = "Formatting";

  public static final String SECTION_MODEL_DESCRIPTORS = "Model Descriptors";

  public static final String SECTION_CALCULATION = "Calculation";

  public static final String SECTION_MISC = "Miscellaneous";

  /**
   * Do not directly access sectionMapping or propertyMapping. Instead, use add method. The order that the sections
   * and properties are added matter!
   */
  static {
    // general section
    add(DefaultPropertyID.NAME.getId(), SECTION_GENERAL);
    add(DefaultPropertyID.DESCRIPTION.getId(), SECTION_GENERAL);
    add(DefaultPropertyID.COMMENTS.getId(), SECTION_GENERAL);
    add(DefaultPropertyID.SECURITY.getId(), SECTION_GENERAL);

    // formatting section
    add(DefaultPropertyID.FONT.getId(), SECTION_FORMATTING);
    add(DefaultPropertyID.COLOR_FG.getId(), SECTION_FORMATTING);
    add(DefaultPropertyID.ALIGNMENT.getId(), SECTION_FORMATTING);
    add(DefaultPropertyID.COLOR_BG.getId(), SECTION_FORMATTING);
    add(DefaultPropertyID.RELATIVE_SIZE.getId(), SECTION_FORMATTING);

    // model descriptors section
    add(DefaultPropertyID.AGGREGATION.getId(), SECTION_MODEL_DESCRIPTORS);
    add(DefaultPropertyID.DATA_TYPE.getId(), SECTION_MODEL_DESCRIPTORS);
    add(DefaultPropertyID.FIELD_TYPE.getId(), SECTION_MODEL_DESCRIPTORS);
    add(DefaultPropertyID.TABLE_TYPE.getId(), SECTION_MODEL_DESCRIPTORS);

    // calculation section
    add(DefaultPropertyID.FORMULA.getId(), SECTION_CALCULATION);
    add(DefaultPropertyID.EXACT.getId(), SECTION_CALCULATION);

    // miscellaneous section
    add(DefaultPropertyID.COLUMN_WIDTH.getId(), SECTION_MISC);
    add(DefaultPropertyID.HIDDEN.getId(), SECTION_MISC);
    add(DefaultPropertyID.MASK.getId(), SECTION_MISC);
    add(DefaultPropertyID.TARGET_SCHEMA.getId(), SECTION_MISC);
    add(DefaultPropertyID.TARGET_TABLE.getId(), SECTION_MISC);
  }

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  // ~ Methods =========================================================================================================

  /**
   * Adds section and property to maps maintained by this helper class.
   * @param id the id of the property
   * @param section the section name to which this property belongs
   */
  private static void add(final String id, final String section) {
    sectionMapping.put(id, section);
    propertyMapping.put(section, id);
  }

  /**
   * Gets all properties belonging to given section.
   * @param section the section to search
   * @return a set of property IDs
   */
  public static List getPropertiesForSection(final String section) {
    if (null != propertyMapping.getCollection(section)) {
      return new ArrayList(propertyMapping.getCollection(section));
    } else {
      return Collections.EMPTY_LIST;
    }
  }

  /**
   * Gets the section to which the property with the given id belongs.
   * @param id the id of the property
   * @return section name
   */
  public static String getSectionForProperty(final String id) {
    return (String) sectionMapping.get(id);
  }

  /**
   * Gets a subset of all properties belonging to given section, eliminating properties not currently present in the
   * given concept.
   * @param section the section to search
   * @param conceptModel the concept to search
   * @return a set of property IDs
   */
  public static List getUsedPropertiesForSection(final String section, final IConceptModel conceptModel) {
    final List allProperties = getPropertiesForSection(section);
    List usedProperties = new ArrayList();
    for (Iterator iter = allProperties.iterator(); iter.hasNext();) {
      String id = (String) iter.next();
      if (null != conceptModel.getEffectiveProperty(id)) {
        usedProperties.add(id);
      }
    }
    return usedProperties;
  }

  /**
   * Gets a subset of all properties belonging to given section, eliminating properties currently present in the given
   * concept.
   * @param section the section to search
   * @param conceptModel the concept to search
   * @return a set of property IDs
   */
  public static List getUnusedPropertiesForSection(final String section, final IConceptModel conceptModel) {
    final List allProperties = getPropertiesForSection(section);
    final List usedProperties = getUsedPropertiesForSection(section, conceptModel);
    return new ArrayList(CollectionUtils.subtract(allProperties, usedProperties));
  }

  private static List getOrderedSections() {
    return wrappedPropertyMap.keyList();
  }

  public static List getSections() {
    return getOrderedSections();
  }

  public static List getUsedSections(final IConceptModel conceptModel) {
    List allSections = getSections();
    List usedSections = new ArrayList();
    for (Iterator iter = allSections.iterator(); iter.hasNext();) {
      String sectionName = (String) iter.next();
      if (!getUsedPropertiesForSection(sectionName, conceptModel).isEmpty()) {
        usedSections.add(sectionName);
      }
    }
    return usedSections;
  }

  public static List getUnusedSections(final IConceptModel conceptModel) {
    List allSections = getSections();
    List unusedSections = new ArrayList();
    for (Iterator iter = allSections.iterator(); iter.hasNext();) {
      String sectionName = (String) iter.next();
      if (!getUnusedPropertiesForSection(sectionName, conceptModel).isEmpty()) {
        unusedSections.add(sectionName);
      }
    }
    return unusedSections;
  }

}