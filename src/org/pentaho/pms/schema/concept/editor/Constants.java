package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.pentaho.pms.util.Const;

public class Constants {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(Constants.class);

  private static FontRegistry fontRegistry;

  private static ImageRegistry imageRegistry;

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  // ~ Methods =========================================================================================================

  public static FontRegistry getFontRegistry(final Display display) {
    if (null == fontRegistry) {
      initFontRegistry(display);
    }
    return fontRegistry;
  }

  public static ImageRegistry getImageRegistry(final Display display) {
    if (null == imageRegistry) {
      initImageRegistry(display);
    }
    return imageRegistry;
  }

  private static void initFontRegistry(final Display display) {
    fontRegistry = new FontRegistry(display);
    fontRegistry.put("card-title", new FontData[] { new FontData("Tahoma", 10, SWT.BOLD) });
    fontRegistry.put("group-name", new FontData[] { new FontData("Tahoma", 12, SWT.BOLD) });
    fontRegistry.put("prop-mgmt-title", new FontData[] { new FontData("Tahoma", 10, SWT.BOLD) });
  }

  private static void initImageRegistry(final Display display) {
    imageRegistry = new ImageRegistry(display);
    imageRegistry.put("add-button", createImage(display, "child-property-add.png"));
    imageRegistry.put("del-button", createImage(display, "child-property-delete.png"));
    imageRegistry.put("override-button", createImage(display, "override.png"));
    imageRegistry.put("stop-override-button", createImage(display, "stop-override.png"));
    imageRegistry.put("concept-editor-app", createImage(display, "concept-editor.png"));
    imageRegistry.put("property-group", createImage(display, "folder.png"));

    imageRegistry.put("parent-property", createImage(display, "parent-property.png"));
    imageRegistry.put("inherited-property", createImage(display, "inherited-property.png"));
    imageRegistry.put("security-property", createImage(display, "security-property.png"));
    imageRegistry.put("child-property", createImage(display, "child-property.png"));
  }

  private static Image createImage(final Display display, final String filename) {
    return new Image(display, Constants.class.getResourceAsStream(Const.IMAGE_DIRECTORY + filename));
  }
}
