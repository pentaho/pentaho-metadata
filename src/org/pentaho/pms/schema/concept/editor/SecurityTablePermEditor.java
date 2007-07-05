package org.pentaho.pms.schema.concept.editor;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.schema.security.SecurityACL;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;

public class SecurityTablePermEditor extends ScrolledComposite implements ISelectionChangedListener, SelectionListener {

  SecurityTableViewer securityTableViewer;
  SecurityReference securityReference;
  ArrayList buttons = new ArrayList();
  Composite nestedComposite;
  boolean allowEditing = true;
  
  static final int UNSELECTED_PERM = 0;
  static final int SEMI_SELECTED_PERM = 1;
  static final int SELECTED_PERM = 2;
  private static final String IS_SEMI_SELECTED = "semi_slected";
  private static final String SECURITY_ACL = "security_acl";
  private static Font DEFAULT_BUTTON_FONT;
  private static Font SEMI_SELECTED_FONT;
  
  public SecurityTablePermEditor(Composite arg0, int arg1, SecurityTableViewer securityTableViewer) {
    super(arg0, SWT.H_SCROLL | SWT.V_SCROLL | arg1);
    init(securityReference, securityTableViewer);
  }

  private void init(SecurityReference securityReference, SecurityTableViewer securityTableViewer) {
    setBackground(securityTableViewer.getTable().getBackground());
    this.securityTableViewer = securityTableViewer;
    nestedComposite = new Composite(this, SWT.NONE);
    nestedComposite.setLayout(new GridLayout());
    setContent(nestedComposite);
    
    nestedComposite.setBackground(securityTableViewer.getTable().getBackground());
    setSecurityReference(securityReference);
    securityTableViewer.addSelectionChangedListener(this);
    
    setEnabled(((StructuredSelection)securityTableViewer.getSelection()).size() > 0);
  }
  
  public void setSecurityReference(SecurityReference securityReference) {
    this.securityReference = securityReference;
    for (Iterator iterator = buttons.iterator(); iterator.hasNext();) {
      Button button = (Button)iterator.next();
      button.removeSelectionListener(this);
      button.dispose();
    }
    
    if (securityReference != null) {
      java.util.List acls = securityReference.getAcls();
      for (Iterator iterator = acls.iterator(); iterator.hasNext();) {
        SecurityACL securityACL = (SecurityACL)iterator.next();
        if (securityACL.getMask() != 0) {
          Button button = new Button(nestedComposite, SWT.CHECK);
          button.setText(securityACL.getName() + " ");
          button.setData(IS_SEMI_SELECTED, new Boolean(false));
          button.setData(SECURITY_ACL, securityACL);
          button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
          button.addSelectionListener(this);
          button.setBackground(securityTableViewer.getTable().getBackground());
          button.setEnabled(nestedComposite.isEnabled());
          buttons.add(button);
          if (DEFAULT_BUTTON_FONT == null) {
            DEFAULT_BUTTON_FONT = button.getFont();
            FontData[] fontData = DEFAULT_BUTTON_FONT.getFontData();
            fontData[0].setStyle(SWT.ITALIC);
            SEMI_SELECTED_FONT = new Font(button.getDisplay(),fontData[0]);
          }
        }
      }
    }
    
    nestedComposite.setSize(nestedComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    refresh();
  }
  
  public void selectionChanged(SelectionChangedEvent arg0) {
    if (allowEditing) {
      setEnabled(((StructuredSelection)securityTableViewer.getSelection()).size() > 0);
    }
    refresh();
  }
  
  private void setSelectionState(SecurityACL securityACL, int selectionState) {
    for (Iterator iter = buttons.iterator(); iter.hasNext();) {
      Button button = (Button)iter.next();
      SecurityACL buttonAcl = (SecurityACL)button.getData(SECURITY_ACL);
      if (buttonAcl.getMask() == securityACL.getMask()) {
        button.removeSelectionListener(this);
        switch (selectionState) {
          case UNSELECTED_PERM:
            button.setSelection(false);
            button.setFont(DEFAULT_BUTTON_FONT);
            break;
          case SELECTED_PERM:
            button.setSelection(true);
            button.setData(IS_SEMI_SELECTED, new Boolean(false));
            button.setFont(DEFAULT_BUTTON_FONT);
            break;
          case SEMI_SELECTED_PERM:
            button.setSelection(true);
            button.setData(IS_SEMI_SELECTED, new Boolean(true));
            button.setFont(SEMI_SELECTED_FONT);
            break;
        }
        button.addSelectionListener(this);
        break;
      }
    }
  }
  
  private int getSelectionState(SecurityACL securityACL) {
    int selectionState = UNSELECTED_PERM;
    for (Iterator iter = buttons.iterator(); iter.hasNext();) {
      Button button = (Button)iter.next();
      SecurityACL buttonAcl = (SecurityACL)button.getData(SECURITY_ACL);
      if (buttonAcl.getMask() == securityACL.getMask()) {
        if (button.getSelection()) {
          if (Boolean.TRUE.equals(button.getData(IS_SEMI_SELECTED))) {
            selectionState = SEMI_SELECTED_PERM;
          } else {
            selectionState = SELECTED_PERM;
          }
        }
        break;
      }
    }
    return selectionState;
  }
  
  public void refresh() {  
    if (securityReference != null) {
      SecurityOwner[] selectedOwners = securityTableViewer.getSelectedOwners();
      for (Iterator aclIter = securityReference.getAcls().iterator(); aclIter.hasNext();) {
        SecurityACL securityACL = (SecurityACL)aclIter.next();
        int selectionState = UNSELECTED_PERM;
        if (selectedOwners.length > 0) {
          int rights = securityTableViewer.getSecuritySettings().getOwnerRights(selectedOwners[0]);
          if ((securityACL.getMask() & rights) == securityACL.getMask()) {
            selectionState = SELECTED_PERM;
          }
        }
        for (int i = 1; (i < selectedOwners.length) && (selectionState != SEMI_SELECTED_PERM); i++) {
          int rights = securityTableViewer.getSecuritySettings().getOwnerRights(selectedOwners[i]);
          if (((securityACL.getMask() & rights) == securityACL.getMask())
              && (selectionState == UNSELECTED_PERM)) {
            selectionState = SEMI_SELECTED_PERM;
          } else if (((securityACL.getMask() & rights) != securityACL.getMask())
              && (selectionState == SELECTED_PERM)) {
            selectionState = SEMI_SELECTED_PERM;
          }
        }
        setSelectionState(securityACL, selectionState);
      }
    }
  }

  public void widgetDefaultSelected(SelectionEvent e) {
    // TODO Auto-generated method stub
    
  }

  public void widgetSelected(SelectionEvent e) {
    Button button = (Button)e.getSource();
    if (Boolean.TRUE.equals(button.getData(IS_SEMI_SELECTED))) {
      setSelectionState((SecurityACL)button.getData(SECURITY_ACL), SELECTED_PERM);
    }    
    
    SecurityACL securityAcl = (SecurityACL)button.getData(SECURITY_ACL);
    SecurityOwner[] selectedOwners = securityTableViewer.getSelectedOwners();
    for (int i = 0; i < selectedOwners.length; i++) {
      int rights = securityTableViewer.getSecuritySettings().getOwnerRights(selectedOwners[i]);
      if (button.getSelection()) {
        securityTableViewer.getSecuritySettings().putOwnerRights(selectedOwners[i], rights | securityAcl.getMask());
      } else {
        securityTableViewer.getSecuritySettings().putOwnerRights(selectedOwners[i], rights & ~securityAcl.getMask());
      }
    }
  }
  
  public void setEnabled(boolean enabled) {
    nestedComposite.setEnabled(enabled);
    for (Iterator iterator = buttons.iterator(); iterator.hasNext();) {
      ((Button)iterator.next()).setEnabled(enabled);
    }
  }
  
  public void setAllowEditing(boolean allow) {
    allowEditing = allow;
    if (!allow) {
      setEnabled(false);
    } else {
      setEnabled(((StructuredSelection)securityTableViewer.getSelection()).size() > 0);
    }
  }
}
