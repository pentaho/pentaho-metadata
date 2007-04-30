package org.pentaho.pms.ui.tree;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ConceptLabelProvider extends LabelProvider implements ITableLabelProvider {

  public Image getColumnImage(Object element, int index) {

    switch (index) {
      case 1: // parent concept
        return ((ConceptTreeNode) element).getConceptImage();
      default: // name
        return ((ConceptTreeNode) element).getImage();
    }
  }

  public String getColumnText(Object element, int index) {
    switch (index) {
      case 1: // parent concept
        return ((ConceptTreeNode) element).getConceptName();
      default: // name 
        return ((ConceptTreeNode) element).getName();
    }
  }

  public String getText(Object element) {
    return getColumnText(element,0);
  }

  public Image getImage(Object element) {
    return getColumnImage(element,0);
  }

}
