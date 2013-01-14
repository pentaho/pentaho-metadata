package org.pentaho.metadata.model.concept.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link LocalizedString}.
 * 
 * @author mlowery
 */
@SuppressWarnings("nls")
public class LocalizedStringTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetLocalizedStringFallbackOnEmptyValue() {
    LocalizedString s = new LocalizedString();
    s.setString("en_US_WIN", "en_US_WIN value");
    s.setString("en_US", "en_US value");
    s.setString("en", "");
    assertEquals("en_US value", s.getLocalizedString("en"));  // en_US is default
  }
  
  @Test
  public void testGetLocalizedStringExactMatchLanguage() {
    LocalizedString s = new LocalizedString();
    s.setString("en_US_WIN", "en_US_WIN value");
    s.setString("en_US", "en_US value");
    s.setString("en", "en value");
    assertEquals("en value", s.getLocalizedString("en"));
  }

  @Test
  public void testGetLocalizedStringExactMatchLanguageCountry() {
    LocalizedString s = new LocalizedString();
    s.setString("en_US_WIN", "en_US_WIN value");
    s.setString("en_US", "en_US value");
    s.setString("en", "en value");
    assertEquals("en_US value", s.getLocalizedString("en_US"));
  }

  @Test
  public void testGetLocalizedStringExactMatchLanguageCountryVariant() {
    LocalizedString s = new LocalizedString();
    s.setString("en_US_WIN", "en_US_WIN value");
    s.setString("en_US", "en_US value");
    s.setString("en", "en value");
    assertEquals("en_US_WIN value", s.getLocalizedString("en_US_WIN"));
  }

  @Test
  public void testGetLocalizedStringFallbackToLanguageCountry() {
    LocalizedString s = new LocalizedString();
    s.setString("en_US", "en_US value");
    s.setString("en", "en value");
    assertEquals("en_US value", s.getLocalizedString("en_US_WIN"));
  }

  @Test
  public void testGetLocalizedStringFallbackToLanguage() {
    LocalizedString s = new LocalizedString();
    s.setString("en", "en value");
    assertEquals("en value", s.getLocalizedString("en_US_WIN"));
  }

  /**
   * Assert fallback to default locale (en_US) and not null.
   */
  @Test
  public void testGetLocalizedStringFallbackToDefaultLocale() {
    LocalizedString s = new LocalizedString();
    s.setString(LocalizedString.DEFAULT_LOCALE, "default value");
    assertEquals("default value", s.getLocalizedString("en"));
  }

  @Test
  public void testGetLocalizedStringNull() {
    LocalizedString s = new LocalizedString();
    assertNull(s.getLocalizedString("en"));
    assertNull(s.getLocalizedString("en_US"));
    assertNull(s.getLocalizedString("en_US_WIN"));
  }

  @Test
  public void testGetLocalizedStringFallbackOnEmptyValueFrench() {
    LocalizedString s = new LocalizedString();
    s.setString("fr_FR_WIN", "");
    s.setString("fr_FR", "fr_FR value");
    s.setString("fr", "fr value");
    assertEquals("fr_FR value", s.getLocalizedString("fr_FR_WIN"));
  }
  
  @Test
  public void testGetLocalizedStringFallbackOnEmptyValueFrench2() {
    LocalizedString s = new LocalizedString();
    s.setString("fr_FR_WIN", " ");
    s.setString("fr_FR", "fr_FR value");
    s.setString("fr", "fr value");
    assertEquals(" ", s.getLocalizedString("fr_FR_WIN"));  // en_US is default
  }
  
  @Test
  public void testGetLocalizedStringExactMatchLanguageFrench() {
    LocalizedString s = new LocalizedString();
    s.setString("fr_FR_WIN", "fr_FR_WIN value");
    s.setString("fr_FR", "fr_FR value");
    s.setString("fr", "fr value");
    s.setString(LocalizedString.DEFAULT_LOCALE, "default value");
    assertEquals("fr value", s.getLocalizedString("fr"));
  }

  @Test
  public void testGetLocalizedStringExactMatchLanguageCountryFrench() {
    LocalizedString s = new LocalizedString();
    s.setString("fr_FR_WIN", "fr_FR_WIN value");
    s.setString("fr_FR", "fr_FR value");
    s.setString("fr", "fr value");
    s.setString(LocalizedString.DEFAULT_LOCALE, "default value");
    assertEquals("fr_FR value", s.getLocalizedString("fr_FR"));
  }

  @Test
  public void testGetLocalizedStringExactMatchLanguageCountryVariantFrench() {
    LocalizedString s = new LocalizedString();
    s.setString("fr_FR_WIN", "fr_FR_WIN value");
    s.setString("fr_FR", "fr_FR value");
    s.setString("fr", "fr value");
    s.setString(LocalizedString.DEFAULT_LOCALE, "default value");
    assertEquals("fr_FR_WIN value", s.getLocalizedString("fr_FR_WIN"));
  }

  @Test
  public void testGetLocalizedStringFallbackToLanguageCountryFrench() {
    LocalizedString s = new LocalizedString();
    s.setString("fr_FR", "fr_FR value");
    s.setString("fr", "fr value");
    s.setString(LocalizedString.DEFAULT_LOCALE, "default value");
    assertEquals("fr_FR value", s.getLocalizedString("fr_FR_WIN"));
  }

  @Test
  public void testGetLocalizedStringFallbackToLanguageFrench() {
    LocalizedString s = new LocalizedString();
    s.setString("fr", "fr value");
    s.setString(LocalizedString.DEFAULT_LOCALE, "default value");
    assertEquals("fr value", s.getLocalizedString("fr_FR_WIN"));
  }

  @Test
  public void testGetLocalizedStringFallbackToDefaultLocaleFrench() {
    LocalizedString s = new LocalizedString();
    s.setString(LocalizedString.DEFAULT_LOCALE, "default value");
    assertEquals("default value", s.getLocalizedString("fr"));
  }

  @Test
  public void testGetLocalizedStringNullFrench() {
    LocalizedString s = new LocalizedString();
    assertNull(s.getLocalizedString("fr"));
    assertNull(s.getLocalizedString("fr_FR"));
    assertNull(s.getLocalizedString("fr_FR_WIN"));
  }
  
  @Test
  public void testGetLocalizedStringNullLocale() {
    LocalizedString s = new LocalizedString();
    assertNull(s.getLocalizedString(null));
  }
}
