package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

/**
 * Helper for getting groups, properties belonging to groups, and a group for a given property. Maintains
 * arbitrary ordering of groups and properties.
 * @author mlowery
 */
public class PropertyGroupHelper {

  // ~ Static fields/initializers ======================================================================================

  /**
   * Keys are property IDs. Values are group names.
   */
  private static OrderedMap groupMapping = new ListOrderedMap();

  /**
   * Not to be accessed except via <code>getOrderedGroups()</code>.
   */
  private static ListOrderedMap wrappedPropertyMap = new ListOrderedMap();

  /**
   * Keys are group names. Values are collections of property IDs.
   */
  private static MultiValueMap propertyMapping = MultiValueMap.decorate(wrappedPropertyMap);

  public static final String GROUP_GENERAL = "General";

  public static final String GROUP_FORMATTING = "Formatting";

  public static final String GROUP_MODEL_DESCRIPTORS = "Model Descriptors";

  public static final String GROUP_CALCULATION = "Calculation";

  public static final String GROUP_MISC = "Miscellaneous";

  public static final String GROUP_CUSTOM = "Custom";

  /**
   * Do not directly access groupMapping or propertyMapping. Instead, use add method. The order that the groups
   * and properties are added matter!
   */
  static {
    // general group
    add(DefaultPropertyID.NAME.getId(), GROUP_GENERAL);
    add(DefaultPropertyID.DESCRIPTION.getId(), GROUP_GENERAL);
    add(DefaultPropertyID.COMMENTS.getId(), GROUP_GENERAL);
    add(DefaultPropertyID.SECURITY.getId(), GROUP_GENERAL);

    // formatting group
    add(DefaultPropertyID.FONT.getId(), GROUP_FORMATTING);
    add(DefaultPropertyID.COLOR_FG.getId(), GROUP_FORMATTING);
    add(DefaultPropertyID.ALIGNMENT.getId(), GROUP_FORMATTING);
    add(DefaultPropertyID.COLOR_BG.getId(), GROUP_FORMATTING);
    add(DefaultPropertyID.RELATIVE_SIZE.getId(), GROUP_FORMATTING);

    // model descriptors group
    add(DefaultPropertyID.AGGREGATION.getId(), GROUP_MODEL_DESCRIPTORS);
    add(DefaultPropertyID.DATA_TYPE.getId(), GROUP_MODEL_DESCRIPTORS);
    add(DefaultPropertyID.FIELD_TYPE.getId(), GROUP_MODEL_DESCRIPTORS);
    add(DefaultPropertyID.TABLE_TYPE.getId(), GROUP_MODEL_DESCRIPTORS);

    // calculation group
    add(DefaultPropertyID.FORMULA.getId(), GROUP_CALCULATION);
    add(DefaultPropertyID.EXACT.getId(), GROUP_CALCULATION);

    // miscellaneous group
    add(DefaultPropertyID.COLUMN_WIDTH.getId(), GROUP_MISC);
    add(DefaultPropertyID.HIDDEN.getId(), GROUP_MISC);
    add(DefaultPropertyID.MASK.getId(), GROUP_MISC);
    add(DefaultPropertyID.TARGET_SCHEMA.getId(), GROUP_MISC);
    add(DefaultPropertyID.TARGET_TABLE.getId(), GROUP_MISC);
  }

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  // ~ Methods =========================================================================================================

  /**
   * Adds group and property to maps maintained by this helper class.
   * @param id the id of the property
   * @param group the group name to which this property belongs
   */
  private static void add(final String id, final String group) {
    groupMapping.put(id, group);
    propertyMapping.put(group, id);
  }

  /**
   * Gets all properties belonging to given group.
   * @param group the group to search
   * @return a set of property IDs
   */
  public static List getPropertiesForGroup(final String group) {
    if (null != propertyMapping.getCollection(group)) {
      return new ArrayList(propertyMapping.getCollection(group));
    } else {
      return Collections.EMPTY_LIST;
    }
  }

  /**
   * Gets the group to which the property with the given id belongs.
   * @param id the id of the property
   * @return group name
   */
  public static String getGroupForProperty(final String id) {
    if (null != groupMapping.get(id)) {
      return (String) groupMapping.get(id);
    } else {
      return GROUP_CUSTOM;
    }
  }

  /**
   * Gets a subset of all properties belonging to given group, eliminating properties not currently present in the
   * given concept.
   * @param group the group to search
   * @param conceptModel the concept to search
   * @return a set of property IDs
   */
  public static List getUsedPropertiesForGroup(final String group, final IConceptModel conceptModel) {
    if (GROUP_CUSTOM.equals(group)) {
      Map effectivePropertyMap = conceptModel.getEffectivePropertyMap();
      Iterator keyIter = effectivePropertyMap.keySet().iterator();
      List customProperties = new ArrayList();
      while (keyIter.hasNext()) {
        String id = (String) keyIter.next();
        if (GROUP_CUSTOM.equals(getGroupForProperty(id))) {
          customProperties.add(id);
        }
      }
      return customProperties;
    }
    final List allProperties = getPropertiesForGroup(group);
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
   * Gets a subset of all properties belonging to given group, eliminating properties currently present in the given
   * concept.
   * @param group the group to search
   * @param conceptModel the concept to search
   * @return a set of property IDs
   */
  public static List getUnusedPropertiesForGroup(final String group, final IConceptModel conceptModel) {
    if (GROUP_CUSTOM.equals(group)) {
      return Collections.EMPTY_LIST;
    }
    final List allProperties = getPropertiesForGroup(group);
    final List usedProperties = getUsedPropertiesForGroup(group, conceptModel);
    return new ArrayList(CollectionUtils.subtract(allProperties, usedProperties));
  }

  private static List getOrderedGroups() {
    List groups = new ArrayList();
    groups.addAll(wrappedPropertyMap.keyList());
    groups.add(GROUP_CUSTOM);
    return groups;
  }

  public static List getGroups() {
    return getOrderedGroups();
  }

  public static List getUsedGroups(final IConceptModel conceptModel) {
    List allGroups = getGroups();
    List usedGroups = new ArrayList();
    for (Iterator iter = allGroups.iterator(); iter.hasNext();) {
      String groupName = (String) iter.next();
      if (!getUsedPropertiesForGroup(groupName, conceptModel).isEmpty()) {
        usedGroups.add(groupName);
      }
    }
    return usedGroups;
  }

  public static List getUnusedGroups(final IConceptModel conceptModel) {
    List allGroups = getGroups();
    List unusedGroups = new ArrayList();
    for (Iterator iter = allGroups.iterator(); iter.hasNext();) {
      String groupName = (String) iter.next();
      if (!getUnusedPropertiesForGroup(groupName, conceptModel).isEmpty()) {
        unusedGroups.add(groupName);
      }
    }
    return unusedGroups;
  }

}