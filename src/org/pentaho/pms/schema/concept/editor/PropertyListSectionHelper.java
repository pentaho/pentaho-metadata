package org.pentaho.pms.schema.concept.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

/**
 * Helper for getting sections, properties belonging to sections, and a section for a given property.
 * @author mlowery
 */
public class PropertyListSectionHelper {

  // ~ Static fields/initializers ======================================================================================

  /**
   * Keys are property IDs. Values are section names.
   */
  private static Map sectionMapping = new HashMap();

  /**
   * Keys are section names. Values are collections of property IDs.
   */
  private static MultiMap propertyMapping = new MultiHashMap();

  public static final String SECTION_GENERAL = "General";

  public static final String SECTION_FORMATTING = "Formatting";

  public static final String SECTION_MODEL_DESCRIPTORS = "Model Descriptors";

  public static final String SECTION_CALCULATION = "Calculation";

  public static final String SECTION_MISC = "Miscellaneous";

  /**
   * Do not directly access sectionMapping or propertyMapping. Instead, use add method.
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
  public static Set getSectionProperties(final String section) {
    if (null != propertyMapping.get(section)) {
      // return set to remove duplicates
      return new HashSet((Collection) propertyMapping.get(section));
    } else {
      return Collections.EMPTY_SET;
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
  public static Set getRelevantPropertiesForSection(final String section, final IConceptModel conceptModel) {
    final Set allProperties = getSectionProperties(section);
    Set relevantProperties = new HashSet();
    for (Iterator iter = allProperties.iterator(); iter.hasNext();) {
      String id = (String) iter.next();
      if (null != conceptModel.getEffectiveProperty(id)) {
        relevantProperties.add(id);
      }
    }
    return relevantProperties;
  }

  public static SortedSet getSections() {
    return new TreeSet(propertyMapping.keySet());
  }

  public static SortedSet getRelevantSections(final IConceptModel conceptModel) {
    SortedSet allSections = getSections();
    SortedSet relevantSections = new TreeSet();
    for (Iterator iter = allSections.iterator(); iter.hasNext();) {
      String sectionName = (String) iter.next();
      if (!getRelevantPropertiesForSection(sectionName, conceptModel).isEmpty()) {
        relevantSections.add(sectionName);
      }
    }
    return relevantSections;
  }

}