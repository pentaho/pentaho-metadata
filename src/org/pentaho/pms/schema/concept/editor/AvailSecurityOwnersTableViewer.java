package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.GUIResource;

public class AvailSecurityOwnersTableViewer extends TableViewer {

  static final int TYPE_COLUMN_ID = 0;
  static final int NAME_COLUMN_ID = 1;
  
  ArrayList allUnassignedUsersAndRoles = new ArrayList();
  SecurityReference securityReference;
  
  class MyContentProvider implements IStructuredContentProvider {

    public Object[] getElements(Object arg0) {
      return allUnassignedUsersAndRoles.toArray();
    }

    public void dispose() {
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
      allUnassignedUsersAndRoles.clear();
      
      Security security = (Security)arg2;
      if (security != null) {
        List owners = security.getOwners();
        
        ArrayList users = new ArrayList(securityReference.getUsers());
        for (Iterator iterator = owners.iterator(); iterator.hasNext();) {
          SecurityOwner securityOwner = (SecurityOwner)iterator.next();
          if (securityOwner.getOwnerType() == SecurityOwner.OWNER_TYPE_USER) {
            users.remove(securityOwner.getOwnerName());
          }
        }
        for (Iterator iterator = users.iterator(); iterator.hasNext();) {
          String userName = (String)iterator.next();
          allUnassignedUsersAndRoles.add(new SecurityOwner(SecurityOwner.OWNER_TYPE_USER, userName));
        }
        
        ArrayList roles = new ArrayList(securityReference.getRoles());
        for (Iterator iterator = owners.iterator(); iterator.hasNext();) {
          SecurityOwner securityOwner = (SecurityOwner)iterator.next();
          if (securityOwner.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE) {
            roles.remove(securityOwner.getOwnerName());
          }
        }
        for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
          String userName = (String)iterator.next();
          allUnassignedUsersAndRoles.add(new SecurityOwner(SecurityOwner.OWNER_TYPE_ROLE, userName));
        }
      }

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
  
  
  public AvailSecurityOwnersTableViewer(Composite arg0, SecurityReference securityReference) {
    super(arg0);
    this.securityReference = securityReference;
    initTable();
  }

  public AvailSecurityOwnersTableViewer(Table arg0, SecurityReference securityReference) {
    super(arg0);
    this.securityReference = securityReference;
    initTable();
  }

  public AvailSecurityOwnersTableViewer(Composite arg0, int arg1, SecurityReference securityReference) {
    super(arg0, arg1);
    this.securityReference = securityReference;
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
    setSorter(new ViewerSorter() {

      public int category(Object arg0) {
        return -((SecurityOwner)arg0).getOwnerType();
      }
      
    });
  }
  
  public void setSecuritySettings(Security security) {
    setInput(security);
  }
  
  public SecurityOwner[] getSelectedOwners(){
    StructuredSelection selection = (StructuredSelection)getSelection();
    return (SecurityOwner[])selection.toList().toArray(new SecurityOwner[0]);
  }
  
  public void addOwner(SecurityOwner owner) {
    Security security = (Security)getInput();
    security.putOwnerRights(owner, 0);
    refresh();
  }
  
  public void removeOwner(SecurityOwner owner) {
    Security security = (Security)getInput();
    security.removeOwnerRights(owner);
    this.remove(owner);
  }
  
  
}
