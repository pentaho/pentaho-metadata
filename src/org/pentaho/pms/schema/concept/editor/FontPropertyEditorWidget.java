package org.pentaho.pms.schema.concept.editor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.concept.types.font.FontSettings;

public class FontPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(FontPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Button fontButton;

  private static FontSettings fontSettings;

  private Canvas canvas;

  private FontRegistry fontRegistry = new FontRegistry();

  private Label fontString;

  private Text preview;

  // ~ Constructors ====================================================================================================

  public FontPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    setValue(getProperty().getValue());
    if (logger.isDebugEnabled()) {
      logger.debug("created FontPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        FontPropertyEditorWidget.this.widgetDisposed(e);
      }
    });

    Label fontStringLabel = new Label(parent, SWT.NONE);
    fontStringLabel.setText("Font:");
    fontString = new Label(parent, SWT.NONE);
    fontString.setText(getFontAsUserString());

    FormData fdFontLabel = new FormData();
    fdFontLabel.left = new FormAttachment(0, 0);
    fdFontLabel.top = new FormAttachment(0, 0);
    fontStringLabel.setLayoutData(fdFontLabel);

    FormData fdFont = new FormData();
    fdFont.left = new FormAttachment(fontStringLabel, 10);
    fdFont.top = new FormAttachment(0, 0);
    fontString.setLayoutData(fdFont);

    Label previewLabel = new Label(parent, SWT.NONE);
    previewLabel.setText("Preview:");
    preview = new Text(parent, SWT.BORDER);

    FormData fdPreviewLabel = new FormData();
    fdPreviewLabel.left = new FormAttachment(0, 0);
    fdPreviewLabel.top = new FormAttachment(preview, 0, SWT.CENTER);
    previewLabel.setLayoutData(fdPreviewLabel);

    FormData fdPreview = new FormData();
    fdPreview.left = new FormAttachment(previewLabel, 10);
    fdPreview.top = new FormAttachment(fontString, 10);
    fdPreview.right = new FormAttachment(100, 0);
    preview.setLayoutData(fdPreview);

    fontButton = new Button(parent, SWT.PUSH);
    FormData fdButton = new FormData();
    fdButton.left = new FormAttachment(0, 0);
    fdButton.top = new FormAttachment(preview, 10);
    fontButton.setLayoutData(fdButton);
    fontButton.setText("Change...");
    fontButton.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        FontDialog dialog = new FontDialog(parent.getShell());
        if (null != getValueAsFont()) {
          dialog.setFontList(getValueAsFont().getFontData());
        }
        dialog.setText("Choose a Font");
        dialog.open();
        if (null != dialog.getFontList()) {
          FontData fd = dialog.getFontList()[0];
          boolean bold = (SWT.BOLD | fd.style) == SWT.BOLD;
          boolean italic = (SWT.ITALIC | fd.style) == SWT.ITALIC;
          setValue(new FontSettings(fd.getName(), fd.getHeight(), bold, italic));
          putPropertyValue();

          // TODO mlowery this is a hack
          parent.getParent().getParent().layout();
        }
      }
    });
  }

  protected void widgetDisposed(final DisposeEvent e) {

  }

  public Object getValue() {
    return fontSettings;
  }

  protected void setValue(final Object value) {
    if (value instanceof FontSettings) {
      fontSettings = (FontSettings) value;
      preview.setEnabled(true);
      preview.setFont(getValueAsFont());
        preview.setText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    } else {
  preview.setEnabled(false);
    }
    fontString.setText(getFontAsUserString());
  }

  protected void addModificationListeners() {
    // nothing to do
  }

  protected void removeModificationListeners() {
    // nothing to do
  }

  /**
   * Converts <code>FontSettings</code> into <code>Font</code>.
   */
  protected Font getValueAsFont() {
    Font font = null;
    if (null != fontSettings) {
      String key = getFontAsUserString();
      if (!fontRegistry.hasValueFor(key)) {
        int style = SWT.NORMAL;
        style |= (fontSettings.isBold() ? SWT.BOLD : SWT.NORMAL);
        style |= (fontSettings.isItalic() ? SWT.ITALIC : SWT.NORMAL);
        FontData fd = new FontData(fontSettings.getName(), fontSettings.getHeight(), style);
        if (logger.isDebugEnabled()) {
          logger.debug("adding font with key \"" + key + "\" to font registry");
        }
        fontRegistry.put(key, new FontData[] { fd });
      }
      if (logger.isDebugEnabled()) {
        logger.debug("found font with key \"" + key + "\" in font registry");
      }
      font = fontRegistry.get(key);
    }
    return font;
  }

  /**
   * Returns string representation of the font appropriate for viewing by the user.
   */
  protected String getFontAsUserString() {
    if (null != fontSettings) {
      return fontSettings.getName() + "-" + fontSettings.getHeight() + (fontSettings.isBold() ? "-bold" : "")
          + (fontSettings.isItalic() ? "-italic" : "");
    } else {
      return "Not specified";
    }
  }

  protected boolean isValid() {
    return true;
  }
}
