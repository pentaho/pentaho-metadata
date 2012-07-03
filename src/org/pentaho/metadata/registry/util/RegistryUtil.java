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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.metadata.registry.util;

import java.util.regex.Pattern;

/**
 * Collection of utility methods for interacting with the Metadata Registry.
 */
public class RegistryUtil {
  /**
   * Separator for parts of a composite id
   */
  public static final String COMPOUND_ID_SEPARATOR = "~";

  /**
   * Placeholder for null values in a composite id
   */
  public static final String NULL = "$NULL$";
  /**
   * Placeholder for empty strings (String's of length 0) in a composite id
   */
  public static final String EMPTY = "$EMPTY$";

  private static final Pattern COMPOUND_ID_SEPARATOR_PATTERN = Pattern.compile(COMPOUND_ID_SEPARATOR);

  /**
   * Generate an composite id by concatenating the provided parts together with the {@link #COMPOUND_ID_SEPARATOR}.
   * <p>
   * See {@link #splitCompositeId} to break a composite id apart.
   * </p>
   *
   * @param parts Parts of an id to be joined together to form a single id
   * @return Id created from the provided parts.
   */
  public String generateCompositeId(String... parts) {
    if (parts == null || parts.length == 0) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      if (i != 0) {
        sb.append(COMPOUND_ID_SEPARATOR);
      }
      // Replace nulls and empty strings with a placeholder so we can parse them back out properly
      String s = parts[i];
      if (s == null) {
        s = NULL;
      } else if (s.length() == 0) {
        s = EMPTY;
      }
      sb.append(s);
    }
    return sb.toString();
  }

  /**
   * Split apart a composite id into it's individual parts. Converse to {@link #generateCompositeId(String...)}.
   *
   * @param id
   * @return
   */
  public String[] splitCompositeId(String id) {
    if (id == null) {
      return null;
    }
    String[] parts = COMPOUND_ID_SEPARATOR_PATTERN.split(id);
    // Replace placeholders with actual values so we have a lossless conversion
    for (int i = 0; i < parts.length; i++) {
      if (NULL.equals(parts[i])) {
        parts[i] = null;
      } else if (EMPTY.equals(parts[i])) {
        parts[i] = "";
      }
    }
    return parts;
  }
}
