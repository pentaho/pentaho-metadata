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
    fontRegistry.put("card-title", new FontData[] { new FontData("Tahoma", 10, SWT.BOLD) }); //$NON-NLS-1$ //$NON-NLS-2$
    fontRegistry.put("group-name", new FontData[] { new FontData("Tahoma", 12, SWT.BOLD) }); //$NON-NLS-1$ //$NON-NLS-2$
    fontRegistry.put("prop-mgmt-title", new FontData[] { new FontData("Tahoma", 10, SWT.BOLD) }); //$NON-NLS-1$ //$NON-NLS-2$
  }

  private static void initImageRegistry(final Display display) {
    imageRegistry = new ImageRegistry(display);
    imageRegistry.put("add-button", createImage(display, "child-property-add.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("del-button", createImage(display, "child-property-delete.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("override-button", createImage(display, "override.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("stop-override-button", createImage(display, "stop-override.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("concept-editor-app", createImage(display, "concept-editor.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("property-group", createImage(display, "folder.png")); //$NON-NLS-1$ //$NON-NLS-2$

    imageRegistry.put("parent-property", createImage(display, "parent-property.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("inherited-property", createImage(display, "inherited-property.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("security-property", createImage(display, "security-property.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("child-property", createImage(display, "child-property.png")); //$NON-NLS-1$ //$NON-NLS-2$

    imageRegistry.put("column-add-button", createImage(display, "column-add.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("column-del-button", createImage(display, "column-delete.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("show-id-button", createImage(display, "show-id.png")); //$NON-NLS-1$ //$NON-NLS-2$

    imageRegistry.put("concept-add-button", createImage(display, "concept-add.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("concept-del-button", createImage(display, "concept-delete.png")); //$NON-NLS-1$ //$NON-NLS-2$


    imageRegistry.put("column", createImage(display, "column.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("table", createImage(display, "table.png")); //$NON-NLS-1$ //$NON-NLS-2$
    imageRegistry.put("concept", createImage(display, "concept.png")); //$NON-NLS-1$ //$NON-NLS-2$

    imageRegistry.put("add-arrow", createImage(display, "add.gif"));
    imageRegistry.put("add-all-arrow", createImage(display, "add_all.gif"));
    imageRegistry.put("remove-arrow", createImage(display, "remove.gif"));
    imageRegistry.put("remove-all-arrow", createImage(display, "remove_all.gif"));
  }

  private static Image createImage(final Display display, final String filename) {
    return new Image(display, Constants.class.getResourceAsStream(Const.IMAGE_DIRECTORY + filename));
  }
}
