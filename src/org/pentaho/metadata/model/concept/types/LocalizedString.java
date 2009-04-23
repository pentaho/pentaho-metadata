package org.pentaho.metadata.model.concept.types;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class LocalizedString {
  private Map<String,String> localeStringMap;
  
  public LocalizedString() {
    localeStringMap = new HashMap<String,String>();
  }
  
  public LocalizedString(String locale, String value) {
    this();
    localeStringMap.put(locale, value);
  }

  public LocalizedString(String value) {
    this();
    localeStringMap.put(Locale.getDefault().toString(), value);
  }

  
  public String getString(String locale) {
      return (String) localeStringMap.get(locale);
  }

  public void setLocaleString(String locale, String string) {
      localeStringMap.put(locale, string);
  }

  public Set<String> getLocales() {
      return localeStringMap.keySet();
  }
  
}
