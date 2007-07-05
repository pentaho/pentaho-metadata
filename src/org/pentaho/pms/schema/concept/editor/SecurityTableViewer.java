package org.pentaho.pms.schema.concept.editor;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.util.GUIResource;

public class SecurityTableViewer extends TableViewer {

  static final int TYPE_COLUMN_ID = 0;
  static final int NAME_COLUMN_ID = 1;
  
  class MyContentProvider implements IStructuredContentProvider {

    public Object[] getElements(Object arg0) {
      return getSecuritySettings() != null ? getSecuritySettings().getOwners().toArray() : new Object[0];
    }

    public void dispose() {
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
    }    
  }
  
  class MyLabelProvider extends LabelProvider implements ITableLabelProvider {
    public Image getColumnImage(Object secOwner, int column) {
      Image image = null;
      if ((secOwner instanceof SecurityOwner) && (column == TYPE_COLUMN_ID)) {
        SecurityOwner securityOwner = (SecurityOwner)secOwner;
        if (securityOwner.getOwnerType() == SecurityOwner.OWNER_TYPE_USER) {
          image = GUIResource.getInstance().getImageUser();
        } else if (securityOwner.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE) {
          image = GUIResource.getInstance().getImageRole();
        }
      }
      return image;
    }

    public String getColumnText(Object secOwner, int column) {
      String text = null;
      if ((secOwner instanceof SecurityOwner) && (column == NAME_COLUMN_ID)) {
        SecurityOwner securityOwner = (SecurityOwner)secOwner;
        text = securityOwner.getOwnerName();
      }
      return text;
    }   
  }
  
  
  public SecurityTableViewer(Composite arg0) {
    super(arg0);
    initTable();
  }

  public SecurityTableViewer(Table arg0) {
    super(arg0);
    initTable();
  }

  public SecurityTableViewer(Composite arg0, int arg1) {
    super(arg0, arg1);
    initTable();
  }

  private void initTable() {
    Table table = getTable();
    
    TableColumn column = new TableColumn (table, SWT.LEFT);
    column.setText ("Type");
    column.setWidth (30);

    column = new TableColumn (table, SWT.LEFT);
    column.setText ("Name");
    column.setWidth (180);
    
    setContentProvider (new MyContentProvider());
    setLabelProvider (new MyLabelProvider());
    setInput(new Security());
    setSorter(new ViewerSorter() {

      public int category(Object arg0) {
        return -((SecurityOwner)arg0).getOwnerType();
      }
      
    });
  }
  
  public void setSecuritySettings(Security security) {
    setInput(security);
  }
  
  public Security getSecuritySettings() {
    Object securitySettings = getInput();
    return securitySettings instanceof Security ? (Security)securitySettings : null;
  }


  public SecurityOwner[] getSelectedOwners(){
    StructuredSelection selection = (StructuredSelection)getSelection();
    return (SecurityOwner[])selection.toList().toArray(new SecurityOwner[0]);
  }
  
  public SecurityOwner[] getOwners() {
    return getSecuritySettings() != null ? (SecurityOwner[])getSecuritySettings().getOwners().toArray(new SecurityOwner[0]) : new SecurityOwner[0];
  }

  public void addOwner(SecurityOwner owner, int rights) {
    Security security = (Security)getInput();
    security.putOwnerRights(owner, rights);
    refresh();
  }
  
  public void addOwner(SecurityOwner owner) {
    addOwner(owner, 0);
  }
  
  public void removeOwner(SecurityOwner owner) {
    Security security = (Security)getInput();
    security.removeOwnerRights(owner);
    remove(owner);
  }
  
  public void removeSelectedOwners() {
    StructuredSelection structuredSelection = (StructuredSelection)getSelection();
    for (Iterator iterator = structuredSelection.toList().iterator(); iterator.hasNext();) {
        removeOwner((SecurityOwner)iterator.next());
    }
    setSelection(new StructuredSelection());
  }
}
