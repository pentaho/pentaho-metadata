/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
/**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/

/*
 * Created on 17-mei-2003
 *
 */

package org.pentaho.pms.editor;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.DescriptionInterface;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.dnd.DragAndDropContainer;
import org.pentaho.di.core.dnd.XMLTransfer;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.gui.Redrawable;
import org.pentaho.di.core.gui.SnapAllignDistribute;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.editor.Constants;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.GUIResource;

public class MetaEditorGraph extends Canvas implements Redrawable {
  private static final int HOP_SEL_MARGIN = 9;

  private static final int RIGHT_HAND_SLOP = 20;

  private Shell shell;

  private MetaEditorGraph metaEditorGraph;

  private LogWriter log;

  private int iconsize;

  private int linewidth;

  private Point lastclick;

  private BusinessTable selected_items[];

  private BusinessTable selected_icon;

  private Point prev_locations[];

  private NotePadMeta selected_note;

  private RelationshipMeta candidate;

  private Point drop_candidate;

  private MetaEditor metaEditor;

  private Point offset, iconoffset, noteoffset;

  private ScrollBar hori;

  private ScrollBar vert;

  public boolean shift, control;

  private int last_button;

  private Rectangle selrect;

  private PropsUI props;

  private Menu mPop;

  public MetaEditorGraph(Composite par, int style, MetaEditor pm) {
    super(par, style);
    shell = par.getShell();
    log = LogWriter.getInstance();
    this.metaEditor = pm;
    metaEditorGraph = this;

    props = PropsUI.getInstance();

    iconsize = props.getIconSize();

    selrect = null;
    candidate = null;
    last_button = 0;
    selected_items = null;
    selected_note = null;

    hori = getHorizontalBar();
    vert = getVerticalBar();

    hori.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        redraw();
      }
    });
    vert.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        redraw();
      }
    });
    hori.setThumb(100);
    vert.setThumb(100);

    hori.setVisible(true);
    vert.setVisible(true);

    setVisible(true);
    newProps();

    metaEditorGraph.setBackground(GUIResource.getInstance().getColorBackground());

    addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        MetaEditorGraph.this.paintControl(e);
      }
    });

    selected_items = null;
    lastclick = null;

    addKeyListener(metaEditor.modKeys);

    /*
     * Handle the mouse...
     */

    addMouseListener(new MouseAdapter() {
      public void mouseDoubleClick(MouseEvent e) {
        BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
        if (activeModel == null)
          return;

        selected_items = null;
        selected_icon = null;
        selected_note = null;
        candidate = null;
        iconoffset = null;
        selrect = null;

        Point real = screen2real(e.x, e.y);

        BusinessTable businessTable = activeModel.getTable(real.x, real.y, iconsize);
        if (businessTable != null) {
          if (e.button == 1)
            editBusinessTable(activeModel, businessTable);
          else
            editDescription(businessTable.getConcept());
        } else {
          // Check if point lies on one of the many hop-lines...
          RelationshipMeta online = findRelationship(real.x, real.y);
          if (online != null) {
            editRelationship(online);
          } else {
            NotePadMeta ni = activeModel.getNote(real.x, real.y);
            if (ni != null) {
              selected_note = null;
              editNote(ni);
            }
          }
        }
      }

      public void mouseDown(MouseEvent e) {
        BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
        if (activeModel == null)
          return;

        last_button = e.button;

        selected_items = null;
        selected_icon = null;
        selected_note = null;
        candidate = null;
        iconoffset = null;
        selrect = null;

        Point real = screen2real(e.x, e.y);

        // Clear the tooltip!
        setToolTipText(null);

        if (last_button == 3) {
          createPopup(real.x, real.y);
        }

        // Did we click on a step?
        BusinessTable ti = activeModel.getTable(real.x, real.y, iconsize);
        if (ti != null) {
          selected_items = activeModel.getSelectedTables();
          selected_icon = ti;
          // make sure this is correct!!!
          // When an icon is moved that is not selected, it gets selected too late.
          // It is not captured here, but in the mouseMoveListener...
          prev_locations = activeModel.getSelectedLocations();

          Point p = ti.getLocation();
          iconoffset = new Point(real.x - p.x, real.y - p.y);
        } else {
          // Dit we hit a note?
          NotePadMeta ni = activeModel.getNote(real.x, real.y);
          if (ni != null && last_button == 1) {
            selected_note = ni;
            Point loc = ni.getLocation();
            noteoffset = new Point(real.x - loc.x, real.y - loc.y);
          } else {
            if (!control)
              selrect = new Rectangle(real.x, real.y, 0, 0);
          }
        }
        lastclick = new Point(real.x, real.y);
        redraw();
      }

      public void mouseUp(MouseEvent e) {
        BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
        if (activeModel == null)
          return;

        if (iconoffset == null)
          iconoffset = new Point(0, 0);
        Point real = screen2real(e.x, e.y);

        // Quick new hop option? (drag from one step to another)
        //
        if (candidate != null) {
          if (activeModel.findRelationship(candidate.getTableFrom().getId(), candidate.getTableTo().getId()) == null) {
            activeModel.addRelationship(candidate);
            metaEditor.synchronize(candidate);
            //metaEditor.refreshTree();
          }
          candidate = null;
          selected_items = null;
          last_button = 0;
        }
        // Did we select a region on the screen? Mark steps in region as selected
        //
        else if (selrect != null) {
          selrect.width = real.x - selrect.x;
          selrect.height = real.y - selrect.y;

          activeModel.unselectAll();
          activeModel.selectInRect(selrect);
          selrect = null;
        }
        // Clicked on an icon?
        //
        else if (selected_icon != null) {
          if (e.button == 1) {
            if (lastclick.x == e.x && lastclick.y == e.y) {
              // Flip selection when control is pressed!
              if (control) {
                selected_icon.flipSelected();
              } else {
                // Otherwise, select only the icon clicked on!
                activeModel.unselectAll();
                selected_icon.setSelected(true);
              }
            } else // We moved around some items: store undo info...
            if (selected_items != null && prev_locations != null) {
              // int indexes[] = activeView.getTableIndexes(selected_items);
            }
          }
          selected_items = null;
        }

        // Notes?
        else if (selected_note != null) {
          // Point note = new Point(real.x - noteoffset.x, real.y - noteoffset.y);
          if (last_button == 1) {
            if (lastclick.x != e.x || lastclick.y != e.y) {
              // int indexes[] = new int[] { activeView.indexOfNote(selected_note) };
            }
          }
          selected_note = null;
        }
        redraw();
      }
    });

    addMouseMoveListener(new MouseMoveListener() {
      public void mouseMove(MouseEvent e) {
        BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
        if (activeModel == null)
          return;

        if (iconoffset == null)
          iconoffset = new Point(0, 0);
        Point real = screen2real(e.x, e.y);
        Point icon = new Point(real.x - iconoffset.x, real.y - iconoffset.y);

        setToolTip(real.x, real.y);

        // First see if the icon we clicked on was selected.
        // If the icon was not selected, we should unselect all other icons,
        // selected and move only the one icon
        if (selected_icon != null && !selected_icon.isSelected()) {
          activeModel.unselectAll();
          selected_icon.setSelected(true);
          selected_items = new BusinessTable[] { selected_icon };
          prev_locations = new Point[] { selected_icon.getLocation() };
        }

        // Did we select a region...?
        if (selrect != null) {
          selrect.width = real.x - selrect.x;
          selrect.height = real.y - selrect.y;
          metaEditor.refreshGraph();
        }

        // Or just one entry on the screen?
        else if (selected_items != null) {
          if (last_button == 1) {
            /*
             * One or more icons are selected and moved around...
             *
             * new : new position of the ICON (not the mouse pointer) dx : difference with previous position
             */
            int dx = icon.x - selected_icon.getLocation().x;
            int dy = icon.y - selected_icon.getLocation().y;

            // Adjust position (location) of selected steps...
            for (int i = 0; i < selected_items.length; i++) {
              BusinessTable businessTable = selected_items[i];
              businessTable.setLocation(businessTable.getLocation().x + dx, businessTable.getLocation().y + dy);
            }

            metaEditor.refreshGraph();
          }
          // The middle button perhaps?
          else if (last_button == 2) {
            BusinessTable businessTable = activeModel.getTable(real.x, real.y, iconsize);
            if (businessTable != null && !selected_icon.equals(businessTable)) {
              if (candidate == null) {
                candidate = new RelationshipMeta(selected_icon, businessTable, null, null);
                redraw();
              }
            } else {
              if (candidate != null) {
                candidate = null;
                metaEditor.refreshGraph();
              }
            }
          }
        } else if (selected_note != null) {
          // Move around a note...
          if (last_button == 1) {
            Point note = new Point(real.x - noteoffset.x, real.y - noteoffset.y);
            selected_note.setLocation(note.x, note.y);
            metaEditor.refreshGraph();
          }
        }
      }
    });

    // Drag & Drop for tables etc.
    Transfer[] ttypes = new Transfer[] { XMLTransfer.getInstance() };
    DropTarget ddTarget = new DropTarget(this, DND.DROP_MOVE);
    ddTarget.setTransfer(ttypes);
    ddTarget.addDropListener(new DropTargetListener() {
      public void dragEnter(DropTargetEvent event) {
        selected_items = null;
        selected_icon = null;
        selrect = null;
        drop_candidate = getRealPosition(metaEditorGraph, event.x, event.y);
        redraw();
      }

      public void dragLeave(DropTargetEvent event) {
        drop_candidate = null;
        redraw();
      }

      public void dragOperationChanged(DropTargetEvent event) {
      }

      public void dragOver(DropTargetEvent event) {
        drop_candidate = getRealPosition(metaEditorGraph, event.x, event.y);
        redraw();
      }

      public void drop(DropTargetEvent event) {
        // no data to copy, indicate failure in event.detail
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }

        // What's the real drop position?
        Point p = getRealPosition(metaEditorGraph, event.x, event.y);

        //
        // We expect a Drag and Drop container... (encased in XML)
        try {
          DragAndDropContainer container = (DragAndDropContainer) event.data;
          switch (container.getType()) {
            //
            // Drag physical table onto metaEditorGraph:
            //  0) Look up the referenced Physical Table name, if it exists continue
            //  1) If there is an active business model use that one, if not ask name, create one, edit it
            //  2) Create the business table based on the physical table, edit
            //  3) Place the business table on the selected coordinates.
            //
            case DragAndDropContainer.TYPE_PHYSICAL_TABLE: {
              PhysicalTable physicalTable = metaEditor.getSchemaMeta().findPhysicalTable(container.getData()); // 0)
              if (physicalTable != null) {
                BusinessModel businessModel = metaEditor.getSchemaMeta().getActiveModel();
                if (businessModel == null)
                  businessModel = metaEditor.newBusinessModel(); // 1)

                if (businessModel != null) {
                  BusinessTable businessTable = metaEditor.newBusinessTable(physicalTable); // 2)
                  if (businessTable != null) {
                    businessTable.setLocation(p.x, p.y);
                    businessTable.setDrawn(true);
                    businessTable.setSelected(true);
                    metaEditor.refreshAll();
                  }
                }
              }
            }
              break;

            //
            // Nothing we can use: give an error!
            //
            default: {
              MessageBox mb = new MessageBox(shell, SWT.OK);
              mb.setMessage(Messages.getString("MetaEditorGraph.USER_CANT_PLACE_ON_GRAPH")); //$NON-NLS-1$
              mb.setText(Messages.getString("General.USER_TITLE_ERROR")); //$NON-NLS-1$
              mb.open();
              return;
            }
          }
        } catch (Exception e) {
          new ErrorDialog(
              shell,
              Messages.getString("MetaEditorGraph.USER_TITLE_DND_ERROR"), Messages.getString("MetaEditorGraph.USER_DND_ERROR"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

      public void dropAccept(DropTargetEvent event) {
      }
    });

    // Keyboard shortcuts...
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
        if (activeModel == null)
          return;

        if (e.character == 1) // CTRL-A
        {
          activeModel.selectAll();
          redraw();
        }
        if (e.keyCode == SWT.ESC) {
          activeModel.unselectAll();
          redraw();
        }

        // CTRL-UP : allignTop();
        if (e.keyCode == SWT.ARROW_UP && (e.stateMask & SWT.MOD1) != 0) {
          alligntop();
        }
        // CTRL-DOWN : allignBottom();
        if (e.keyCode == SWT.ARROW_DOWN && (e.stateMask & SWT.MOD1) != 0) {
          allignbottom();
        }
        // CTRL-LEFT : allignleft();
        if (e.keyCode == SWT.ARROW_LEFT && (e.stateMask & SWT.MOD1) != 0) {
          allignleft();
        }
        // CTRL-RIGHT : allignRight();
        if (e.keyCode == SWT.ARROW_RIGHT && (e.stateMask & SWT.MOD1) != 0) {
          allignright();
        }
        // ALT-RIGHT : distributeHorizontal();
        if (e.keyCode == SWT.ARROW_RIGHT && (e.stateMask & SWT.ALT) != 0) {
          distributehorizontal();
        }
        // ALT-UP : distributeVertical();
        if (e.keyCode == SWT.ARROW_UP && (e.stateMask & SWT.ALT) != 0) {
          distributevertical();
        }
        // ALT-HOME : snap to grid
        if (e.keyCode == SWT.HOME && (e.stateMask & SWT.ALT) != 0) {
          snaptogrid(Const.GRID_SIZE);
        }
      }
    });

    addKeyListener(metaEditor.defKeys);

    setBackground(GUIResource.getInstance().getColorBackground());
  }

  public Point screen2real(int x, int y) {
    offset = getOffset();
    Point real = new Point(x - offset.x, y - offset.y);

    return real;
  }

  public Point real2screen(int x, int y) {
    getOffset();
    Point screen = new Point(x + offset.x, y + offset.y);

    return screen;
  }

  public Point getRealPosition(Composite canvas, int x, int y) {
    Point p = new Point(0, 0);
    Composite follow = canvas;
    while (follow != null) {
      org.eclipse.swt.graphics.Point loc = follow.getLocation();
      Point xy = new Point(loc.x, loc.y);
      p.x += xy.x;
      p.y += xy.y;
      follow = follow.getParent();
    }

    p.x = x - p.x - 8;
    p.y = y - p.y - 48;

    return screen2real(p.x, p.y);
  }

  // See if location (x,y) is on a line between two entities: the relationship!
  // return the Relationship if so, otherwise: null
  private RelationshipMeta findRelationship(int x, int y) {
    BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
    if (activeModel == null)
      return null; // no active business model

    for (int i = 0; i < activeModel.nrRelationships(); i++) {
      RelationshipMeta ri = activeModel.getRelationship(i);
      BusinessTable fs = ri.getTableFrom();
      BusinessTable ts = ri.getTableTo();

      if (fs != null && ts != null) {
        int line[] = new int[4];

        getLine(fs, ts, line);

        if (pointOnLine(x, y, line))
          return ri;
      }
    }
    return null;
  }

  private double getLine(BusinessTable fs, BusinessTable ts, int line[]) {
    Point from = fs.getLocation();
    Point to = ts.getLocation();
    offset = getOffset();

    Point A = new Point(from.x, from.y);
    Point B = new Point(to.x, to.y);

    Point X = new Point(0, 0);
    Point Y = new Point(0, 0);

    double angle = calcRelationshipLine(A, B, X, Y);

    line[0] = X.x;
    line[1] = X.y;
    line[2] = Y.x;
    line[3] = Y.y;

    return angle;
  }

  private void createPopup(int x, int y) {
    if (mPop != null) {
      mPop.dispose();
    }
    setMenu(null);
    mPop = new Menu(this);

    final BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
    if (activeModel == null)
      return;
    final String activeLocale = metaEditor.getSchemaMeta().getActiveLocale();

    // final String activeLocale = metaEditor.getSchemaMeta().getActiveLocale();

    final BusinessTable bTable = activeModel.getTable(x, y, iconsize);
    if (bTable != null) // We clicked on a Step!
    {
      int sels = activeModel.nrSelected();
      if (sels == 1) {
        MenuItem miNewBTable = new MenuItem(mPop, SWT.CASCADE);
        miNewBTable.setText(Messages.getString("MetaEditorGraph.USER_NEW_BUSINESS_TABLE")); //$NON-NLS-1$
        miNewBTable.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            selected_items = null;
            newBusinessTable();
          }
        });
        MenuItem miEditBTable = new MenuItem(mPop, SWT.CASCADE);
        miEditBTable.setText(Messages.getString("MetaEditorGraph.USER_EDIT_BUSINESS_TABLE")); //$NON-NLS-1$
        miEditBTable.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            selected_items = null;
            editBusinessTable(activeModel, bTable);
          }
        });
        MenuItem miDupeBTable = new MenuItem(mPop, SWT.CASCADE);
        miDupeBTable.setText(Messages.getString("MetaEditorGraph.USER_DUPE_BUSINESS_TABLE")); //$NON-NLS-1$
        miDupeBTable.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            dupeBusinessTable(activeModel, bTable);
          }
        });
        MenuItem miDelStep = new MenuItem(mPop, SWT.CASCADE);
        miDelStep.setText(Messages.getString("MetaEditorGraph.USER_DELETE_TABLE")); //$NON-NLS-1$
        miDelStep.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            int nrsels = activeModel.nrSelected();
            if (nrsels == 0) {
              metaEditor.delPhysicalTable(bTable.getId());
            } else {
              if (!bTable.isSelected())
                nrsels++;

              MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_WARNING);
              mb.setText(Messages.getString("MetaEditorGraph.USER_WARNING")); //$NON-NLS-1$
              String message = Messages.getString(
                  "MetaEditorGraph.USER_CONFRIM_DELETE_TABLES", Integer.toString(nrsels)); //$NON-NLS-1$
              for (int i = activeModel.nrBusinessTables() - 1; i >= 0; i--) {
                BusinessTable tableinfo = activeModel.getBusinessTable(i);
                if (tableinfo.isSelected() || bTable.equals(tableinfo)) {
                  message += "   " + tableinfo.getId() + Const.CR; //$NON-NLS-1$
                }
              }

              mb.setMessage(message);
              int result = mb.open();
              if (result == SWT.YES) {
                for (int i = activeModel.nrBusinessTables() - 1; i >= 0; i--) {
                  BusinessTable tableinfo = activeModel.getBusinessTable(i);
                  if (tableinfo.isSelected() || bTable.equals(tableinfo)) {
                    metaEditor.delBusinessTable(tableinfo);
                  }
                }
              }
            }
          }
        });

        new MenuItem(mPop, SWT.SEPARATOR);
        MenuItem miSetParentConcept = new MenuItem(mPop, SWT.CASCADE);
        miSetParentConcept.setText(Messages.getString("MetaEditorGraph.USER_SET_PARENT_CONCEPT")); //$NON-NLS-1$
        miSetParentConcept.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            ConceptUtilityInterface[] conceptUtilityInterfaces = metaEditor
                .getSelectedConceptUtilityInterfacesInMainTree();
            metaEditor.setParentConcept(conceptUtilityInterfaces);
          }
        });
        MenuItem miClearParentConcept = new MenuItem(mPop, SWT.CASCADE);
        miClearParentConcept.setText(Messages.getString("MetaEditorGraph.USER_CLEAR_PARENT_CONCEPT")); //$NON-NLS-1$
        miClearParentConcept.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            ConceptUtilityInterface[] conceptUtilityInterfaces = metaEditor
                .getSelectedConceptUtilityInterfacesInMainTree();
            metaEditor.clearParentConcept(conceptUtilityInterfaces);
          }
        });
        MenuItem miRemoveChildProps = new MenuItem(mPop, SWT.CASCADE);
        miRemoveChildProps.setText(Messages.getString("MetaEditorGraph.USER_REMOVE_CHILD_PROPS")); //$NON-NLS-1$
        miRemoveChildProps.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            ConceptUtilityInterface[] conceptUtilityInterfaces = metaEditor
                .getSelectedConceptUtilityInterfacesInMainTree();
            metaEditor.removeChildProperties(conceptUtilityInterfaces);
          }
        });

        new MenuItem(mPop, SWT.SEPARATOR);
        MenuItem miEditPTable = new MenuItem(mPop, SWT.CASCADE);
        miEditPTable.setText(Messages.getString(
            "MetaEditorGraph.USER_EDIT_PHYSICAL_TABLE", bTable.getPhysicalTable().getDisplayName(activeLocale))); //$NON-NLS-1$
        if (bTable.getPhysicalTable() != null) {
          miEditPTable.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              metaEditor.editPhysicalTable(bTable.getPhysicalTable());
            }
          });
        }
        MenuItem miDupeStep = new MenuItem(mPop, SWT.CASCADE);
        miDupeStep.setText(Messages.getString("MetaEditorGraph.USER_DUPLICATE_TABLE")); //$NON-NLS-1$
        miDupeStep.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            if (activeModel.nrSelected() <= 1) {
              metaEditor.dupePhysicalTable(bTable.getPhysicalTable());
            } else {
              for (int i = 0; i < activeModel.nrBusinessTables(); i++) {
                BusinessTable businessTable = activeModel.getBusinessTable(i);
                if (businessTable.isSelected()) {
                  metaEditor.dupePhysicalTable(businessTable.getPhysicalTable());
                }
              }
            }
          }
        });
      } else if (sels == 2) {
        MenuItem miNewHop = new MenuItem(mPop, SWT.CASCADE);
        miNewHop.setText(Messages.getString("MetaEditorGraph.USER_ADD_RELATIONSHIP")); //$NON-NLS-1$
        miNewHop.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            selected_items = null;
            newRelationship();
          }
        });
      }

      // Allign & Distribute options...
      if (sels > 1) {
        new MenuItem(mPop, SWT.SEPARATOR);
        MenuItem miPopAD = new MenuItem(mPop, SWT.CASCADE);
        miPopAD.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_DISTRIBUTE")); //$NON-NLS-1$

        Menu mPopAD = new Menu(miPopAD);
        MenuItem miPopALeft = new MenuItem(mPopAD, SWT.CASCADE);
        miPopALeft.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_LEFT")); //$NON-NLS-1$
        MenuItem miPopARight = new MenuItem(mPopAD, SWT.CASCADE);
        miPopARight.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_RIGHT")); //$NON-NLS-1$
        MenuItem miPopATop = new MenuItem(mPopAD, SWT.CASCADE);
        miPopATop.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_TOP")); //$NON-NLS-1$
        MenuItem miPopABottom = new MenuItem(mPopAD, SWT.CASCADE);
        miPopABottom.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_BOTTOM")); //$NON-NLS-1$
        new MenuItem(mPopAD, SWT.SEPARATOR);
        MenuItem miPopDHoriz = new MenuItem(mPopAD, SWT.CASCADE);
        miPopDHoriz.setText(Messages.getString("MetaEditorGraph.USER_DISTRIBUTE_HORIZ")); //$NON-NLS-1$
        MenuItem miPopDVertic = new MenuItem(mPopAD, SWT.CASCADE);
        miPopDVertic.setText(Messages.getString("MetaEditorGraph.USER_DISTRIBUTE_VERT")); //$NON-NLS-1$
        new MenuItem(mPopAD, SWT.SEPARATOR);
        MenuItem miPopSSnap = new MenuItem(mPopAD, SWT.CASCADE);
        miPopSSnap.setText(Messages.getString("MetaEditorGraph.USER_SNAP_TO_GRID", Integer.toString(Const.GRID_SIZE))); //$NON-NLS-1$
        miPopAD.setMenu(mPopAD);

        miPopALeft.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            allignleft();
          }
        });
        miPopARight.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            allignright();
          }
        });
        miPopATop.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            alligntop();
          }
        });
        miPopABottom.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            allignbottom();
          }
        });
        miPopDHoriz.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            distributehorizontal();
          }
        });
        miPopDVertic.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            distributevertical();
          }
        });
        miPopSSnap.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            snaptogrid(Const.GRID_SIZE);
          }
        });
      }
    } else {
      final RelationshipMeta relationshipMeta = findRelationship(x, y);
      if (relationshipMeta != null) // We clicked on a relationship!
      {
        final BusinessModel model = metaEditor.getSchemaMeta().getActiveModel(); // not null because we found a relationship

        MenuItem miEditHop = new MenuItem(mPop, SWT.CASCADE);
        miEditHop.setText(Messages.getString("MetaEditorGraph.USER_EDIT_RELATIONSHIP")); //$NON-NLS-1$
        miEditHop.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            selrect = null;
            editRelationship(relationshipMeta);
          }
        });

        MenuItem miDelHop = new MenuItem(mPop, SWT.CASCADE);
        miDelHop.setText(Messages.getString("MetaEditorGraph.USER_DELETE_RELATIONSHIP")); //$NON-NLS-1$
        miDelHop.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            selrect = null;
            int idx = model.indexOfRelationship(relationshipMeta);
            model.removeRelationship(idx);
            metaEditor.synchronize(relationshipMeta);
            metaEditor.refreshGraph();
          }
        });
      } else {
        // Clicked on the background: maybe we hit a note?
        final NotePadMeta ni = activeModel.getNote(x, y);
        if (ni != null) {
          // Delete note
          // Edit note

          MenuItem miNoteEdit = new MenuItem(mPop, SWT.CASCADE);
          miNoteEdit.setText(Messages.getString("MetaEditorGraph.USER_EDIT_NOTE")); //$NON-NLS-1$
          MenuItem miNoteDel = new MenuItem(mPop, SWT.CASCADE);
          miNoteDel.setText(Messages.getString("MetaEditorGraph.USER_DELETE_NOTE")); //$NON-NLS-1$

          miNoteEdit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              selrect = null;
              editNote(ni);
            }
          });
          miNoteDel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              selrect = null;
              int idx = activeModel.indexOfNote(ni);
              if (idx >= 0) {
                activeModel.removeNote(idx);
                redraw();
              }
            }
          });

        } else {
          // New note
          MenuItem miNoteNew = new MenuItem(mPop, SWT.CASCADE);
          miNoteNew.setText(Messages.getString("MetaEditorGraph.USER_NEW_NOTE")); //$NON-NLS-1$
          miNoteNew.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              selrect = null;
              String title = Messages.getString("MetaEditorGraph.USER_TITLE_NOTES"); //$NON-NLS-1$
              String message = Messages.getString("MetaEditorGraph.USER_NOTE_TEXT"); //$NON-NLS-1$
              EnterTextDialog dd = new EnterTextDialog(shell, title, message, ""); //$NON-NLS-1$
              String n = dd.open();
              if (n != null) {
                NotePadMeta npi = new NotePadMeta(n, lastclick.x, lastclick.y, Const.NOTE_MIN_SIZE, Const.NOTE_MIN_SIZE);
                activeModel.addNote(npi);
                redraw();
              }
            }
          });
          MenuItem miNewBTable = new MenuItem(mPop, SWT.CASCADE);
          miNewBTable.setText(Messages.getString("MetaEditorGraph.USER_NEW_BUSINESS_TABLE")); //$NON-NLS-1$
          miNewBTable.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              selrect = null;
              newBusinessTable();
            }
          });
          MenuItem miNewRelationship = new MenuItem(mPop, SWT.CASCADE);
          miNewRelationship.setText(Messages.getString("MetaEditorGraph.USER_NEW_RELATIONSHIP")); //$NON-NLS-1$
          miNewRelationship.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              selrect = null;
              newRelationship();
            }
          });
          MenuItem miSetLocale = new MenuItem(mPop, SWT.CASCADE);
          miSetLocale.setText(Messages.getString("MetaEditorGraph.USER_SET_LOCALE")); //$NON-NLS-1$
          miSetLocale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              selrect = null;
              // TODO implement setLocale
              log.logError("Not Implemented", "Set Locale functionality not yet implemented"); //$NON-NLS-1$ //$NON-NLS-2$
            }
          });
        }
      }
    }
    setMenu(mPop);
    mPop.setVisible(true);
  }

  private void setToolTip(int x, int y) {
    BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
    if (activeModel == null)
      return;
    String activeLocale = metaEditor.getSchemaMeta().getActiveLocale();

    final BusinessTable businessTable = activeModel.getTable(x, y, iconsize);
    if (businessTable != null) // We clicked on a Step!
    {
      ConceptInterface concept = businessTable.getConcept();

      // Also: set the tooltip!
      if (concept.getDescription(activeLocale) != null) {
        String desc = concept.getDescription(activeLocale);
        int le = desc.length() >= 200 ? 200 : desc.length();
        String tip = desc.substring(0, le);
        if (!tip.equalsIgnoreCase(getToolTipText())) {
          setToolTipText(tip);
        }
      } else {
        setToolTipText(null);
      }
    } else {
      final RelationshipMeta hi = findRelationship(x, y);
      if (hi != null) // We clicked on a HOP!
      {
        // Set the tooltip for the hop:
        setToolTipText(hi.toString());
      } else {
        setToolTipText(null);
      }
    }
  }

  public void editDescription(final ConceptInterface conceptInterface) {
    final String activeLocale = metaEditor.getSchemaMeta().getActiveLocale();

    DescriptionInterface descriptionInterface = new DescriptionInterface() {
      public void setDescription(String desc) {
        conceptInterface.setDescription(activeLocale, desc);
      }

      public String getDescription() {
        return conceptInterface.getDescription(activeLocale);
      }
    };

    EnterTextDialog
        .editDescription(
            shell,
            descriptionInterface,
            Messages.getString("MetaEditorGraph.USER_TABLE_DESCRIPTION_DIALOG"), Messages.getString("MetaEditorGraph.USER_TABLE_DESCRIPTION")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void paintControl(PaintEvent e) {
    Point area = getArea();
    if (area.x == 0 || area.y == 0)
      return; // nothing to do!

    Display disp = shell.getDisplay();
    Image img = new Image(disp, area.x, area.y);
    GC gc = new GC(img);

    // First clear the image in the background color
    gc.setBackground(GUIResource.getInstance().getColorBackground());
    gc.fillRectangle(0, 0, area.x, area.y);

    // Then draw stuff on it!
    drawSchema(gc);
    e.gc.drawImage(img, 0, 0);
    gc.dispose();
    img.dispose();

    metaEditor.setShellText();
  }

  public void drawSchema(GC gc) {
    BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();
    String activeLocale = metaEditor.getSchemaMeta().getActiveLocale();

    if (props.isAntiAliasingEnabled())
      gc.setAntialias(SWT.ON);

    gc.setBackground(GUIResource.getInstance().getColorBackground());

    // Draw the active business model.  If there is none, don't draw anything except the drop rectangles ;-)
    if (activeModel != null) {
      // display the name/description of the business model
      //
      String title = activeModel.getDisplayName(activeLocale);
      gc.setFont(GUIResource.getInstance().getFontLarge());
      org.eclipse.swt.graphics.Point point = gc.textExtent(title);
      gc.drawText(title, 10, 10);
      gc.setFont(GUIResource.getInstance().getFontMedium());
      String description = activeModel.getConcept().getDescription(activeLocale);
      if (description != null) {
        description = paginateString(gc, description);
        gc.drawText(description, 10, 10 + point.y, SWT.DRAW_DELIMITER);
      }

      // Display the active locale in the right hand corner.
      org.eclipse.swt.graphics.Rectangle rect = gc.getClipping();
      gc.setFont(GUIResource.getInstance().getFontMedium());
      String localeMessage = Messages.getString("MetaEditorGraph.USER_LOCALE", activeLocale); //$NON-NLS-1$
      org.eclipse.swt.graphics.Point messageSize = gc.textExtent(localeMessage);
      gc.drawText(localeMessage, rect.width - messageSize.x - 20, 10);

      // Back to our regular show...

      Point area = getArea();
      Point max = activeModel.getMaximum();
      Point thumb = getThumb(area, max);
      offset = getOffset(thumb, area);

      hori.setThumb(thumb.x);
      vert.setThumb(thumb.y);

      // First the notes
      for (int i = 0; i < activeModel.nrNotes(); i++) {
        NotePadMeta ni = activeModel.getNote(i);
        drawNote(gc, ni);
      }

      for (int i = 0; i < activeModel.nrRelationships(); i++) {
        RelationshipMeta hi = activeModel.getRelationship(i);
        drawRelationship(gc, hi);
      }

      if (candidate != null) {
        drawRelationship(gc, candidate, true);
      }

      for (int i = 0; i < activeModel.nrBusinessTables(); i++) {
        BusinessTable si = activeModel.getBusinessTable(i);
        drawBusinessTable(gc, si);
      }

      drawRect(gc, selrect);
    } else {
      // Set the name of the business model
      //
      int h = (int) GUIResource.getInstance().getFontLarge().getFontData()[0].height;

      gc.setFont(GUIResource.getInstance().getFontLarge());
      int nrModels = metaEditor.getSchemaMeta().nrBusinessModels();
      String message1, message2;
      switch (nrModels) {
        case 0:
          message1 = Messages.getString("MetaEditorGraph.USER_NO_BUSINESS_MODELS_DEFINED");message2 = Messages.getString("MetaEditorGraph.USER_CREATE_ONE_FIRST");break; //$NON-NLS-1$ //$NON-NLS-2$
        case 1:
          message1 = Messages.getString("MetaEditorGraph.USER_ONE_BUSINESS_MODEL_DEFINED");message2 = Messages.getString("MetaEditorGraph.USER_SELECT_IN_TREE");break; //$NON-NLS-1$ //$NON-NLS-2$
        default:
          message1 = Messages.getString("MetaEditorGraph.USER_N_BUSINESS_MODELS_DEFINED", Integer.toString(nrModels));message2 = Messages.getString("MetaEditorGraph.USER_SELECT_ONE_IN_TREE");break; //$NON-NLS-1$ //$NON-NLS-2$
      }
      gc.drawText(message1, 10, 10, true);
      gc.drawText(message2, 10, 20 + h, true);
    }

    if (drop_candidate != null) {
      gc.setLineStyle(SWT.LINE_SOLID);
      gc.setForeground(GUIResource.getInstance().getColorBlack());
      Point screen = real2screen(drop_candidate.x, drop_candidate.y);
      gc.drawRectangle(screen.x, screen.y, iconsize, iconsize);
    }
  }

  /**
   * @param gc - The Graphics content
   * @param src - The string to break up
   * @return String that contains \n characters where the distance between each
   * \n character is less than the width of the frame
   */
  private String paginateString(GC gc, String strSrc) {
    int stringWidth = gc.textExtent(strSrc).x;
    int frameWidth = getSize().x - RIGHT_HAND_SLOP;
    if (stringWidth <= frameWidth) {
      return strSrc;
    }
    String[] tokens = strSrc.split("\\s"); //$NON-NLS-1$
    String result = "";
    ArrayList<String> textRuns = new ArrayList<String>();
    for (int i=0; i<tokens.length; i++) {
      if (gc.textExtent(result + tokens[i]).x < frameWidth) {
        result = result + " " + tokens[i]; //$NON-NLS-1$
      } else {
        textRuns.add(result);
        result = tokens[i];
      }
    }
    textRuns.add(result);

    result = "";
    Iterator iter = textRuns.iterator();
    while (iter.hasNext()) {
      result += iter.next().toString() + "\n";
    }
    return result;
  }

  private void drawRelationship(GC gc, RelationshipMeta hi) {
    drawRelationship(gc, hi, false);
  }

  private void drawNote(GC gc, NotePadMeta ni) {
    int flags = SWT.DRAW_DELIMITER | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT;

    org.eclipse.swt.graphics.Point ext = gc.textExtent(ni.getNote(), flags);
    Point p = new Point(ext.x, ext.y);
    Point loc = ni.getLocation();
    Point note = real2screen(loc.x, loc.y);
    int margin = Const.NOTE_MARGIN;
    p.x += 2 * margin;
    p.y += 2 * margin;
    int width = ni.width;
    int height = ni.height;
    if (p.x > width)
      width = p.x;
    if (p.y > height)
      height = p.y;

    gc.setForeground(GUIResource.getInstance().getColorGray());
    gc.setBackground(GUIResource.getInstance().getColorYellow());

    int noteshape[] = new int[] { note.x, note.y, // Top left
        note.x + width + 2 * margin, note.y, // Top right
        note.x + width + 2 * margin, note.y + height, // bottom right 1
        note.x + width, note.y + height + 2 * margin, // bottom right 2
        note.x + width, note.y + height, // bottom right 3
        note.x + width + 2 * margin, note.y + height, // bottom right 1
        note.x + width, note.y + height + 2 * margin, // bottom right 2
        note.x, note.y + height + 2 * margin // bottom left
    };
    gc.fillPolygon(noteshape);
    gc.drawPolygon(noteshape);

    gc.setForeground(GUIResource.getInstance().getColorBlack());
    gc.drawText(ni.getNote(), note.x + margin, note.y + margin, flags);

    ni.width = width; // Save for the "mouse" later on...
    ni.height = height;
  }

  private void drawRelationship(GC gc, RelationshipMeta hi, boolean is_candidate) {
    BusinessTable fs = hi.getTableFrom();
    BusinessTable ts = hi.getTableTo();

    if (fs != null && ts != null) {
      drawRelationshipLine(gc, fs, ts, hi, is_candidate);
    }
  }

  private void drawBusinessTable(GC gc, BusinessTable businessTable) {
    Point pt = businessTable.getLocation();

    int x, y;
    if (pt != null) {
      x = pt.x;
      y = pt.y;

    } else {
      x = 50;
      y = 50;
    }

    Point screen = real2screen(x, y);

    int sizeX = iconsize;
    int sizeY = iconsize;

    String name = businessTable.getDisplayName(metaEditor.getSchemaMeta().getActiveLocale());

    Image im = Constants.getImageRegistry(Display.getCurrent()).get("bus-table-graph-icon");
    org.eclipse.swt.graphics.Rectangle bounds = im.getBounds();
    gc.drawImage(im, 0, 0, bounds.width, bounds.height, screen.x, screen.y, iconsize, iconsize);

    // PMD-204: Previously, the boxes were colored according to type; leaving this code commented out for reference
//    if (businessTable.isFactTable()) {
//      gc.setBackground(GUIResource.getInstance().getColorOrange());
//    } else if (businessTable.isDimensionTable()) {
//      gc.setBackground(GUIResource.getInstance().getColorYellow());
//    } else {
//      gc.setBackground(GUIResource.getInstance().getColorLightGray());
//    }

    if (businessTable.isSelected()) {
      gc.setLineWidth(linewidth + 2);
      gc.setBackground(GUIResource.getInstance().getColorBackground());
      gc.drawRectangle(screen.x - 1, screen.y - 1, sizeX + 1, sizeY + 1);
    }

    org.eclipse.swt.graphics.Point ext = gc.textExtent(name);
    Point textsize = new Point(ext.x, ext.y);
    gc.setBackground(GUIResource.getInstance().getColorDarkGray());
    gc.setForeground(GUIResource.getInstance().getColorBlack());
    gc.setLineWidth(linewidth);
    int xpos = screen.x + (sizeX / 2) - (textsize.x / 2);
    int ypos = screen.y + sizeY + Const.SYMBOLSIZE + 4;

    gc.drawText(name, xpos, ypos, SWT.DRAW_TRANSPARENT);
  }

  private void drawRelationshipLine(GC gc, BusinessTable fs, BusinessTable ts, RelationshipMeta hi, boolean is_candidate) {
    int line[] = new int[4];
    double angle = getLine(fs, ts, line);

    gc.setLineWidth(linewidth);
    Color col;

    if (is_candidate) {
      col = GUIResource.getInstance().getColorBlue();
    } else {
      col = GUIResource.getInstance().getColorBlack();
    }
    gc.setForeground(col);

    drawArrow(gc, line, hi, angle);

    gc.setForeground(GUIResource.getInstance().getColorBlack());
    gc.setBackground(GUIResource.getInstance().getColorBackground());
  }

  private Point getArea() {
    org.eclipse.swt.graphics.Rectangle rect = getClientArea();
    Point area = new Point(rect.width, rect.height);

    return area;
  }

  private Point getThumb(Point area, Point max) {
    Point thumb = new Point(0, 0);
    if (max.x <= area.x)
      thumb.x = 100;
    else
      thumb.x = 100 * area.x / max.x;
    if (max.y <= area.y)
      thumb.y = 100;
    else
      thumb.y = 100 * area.y / max.y;

    return thumb;
  }

  private Point getOffset() {
    Point area = getArea();
    Point max = new Point(0, 0);
    if (metaEditor.getSchemaMeta().getActiveModel() != null) {
      max = metaEditor.getSchemaMeta().getActiveModel().getMaximum();
    }
    Point thumb = getThumb(area, max);
    Point off = getOffset(thumb, area);

    return off;

  }

  private Point getOffset(Point thumb, Point area) {
    Point p = new Point(0, 0);
    Point sel = new Point(hori.getSelection(), vert.getSelection());

    if (thumb.x == 0 || thumb.y == 0)
      return p;

    p.x = -sel.x * area.x / thumb.x;
    p.y = -sel.y * area.y / thumb.y;

    return p;
  }

  public int sign(int n) {
    return n < 0 ? -1 : (n > 0 ? 1 : 1);
  }

  private void newBusinessTable() {
    metaEditor.newBusinessTable(null);
  }

  private void editBusinessTable(BusinessModel activeModel, BusinessTable businessTable) {
    metaEditor.editBusinessTable(businessTable);
  }

  private void dupeBusinessTable(BusinessModel activeModel, BusinessTable businessTable) {
    metaEditor.dupeBusinessTable(businessTable);
  }

  private void editNote(NotePadMeta ni) {
    String title = Messages.getString("MetaEditorGraph.USER_TITLE_NOTES"); //$NON-NLS-1$
    String message = Messages.getString("MetaEditorGraph.USER_NOTE_TEXT"); //$NON-NLS-1$
    EnterTextDialog dd = new EnterTextDialog(shell, title, message, ni.getNote());
    String n = dd.open();
    if (n != null) {
      ni.setChanged();
      ni.setNote(n);
      ni.width = Const.NOTE_MIN_SIZE;
      ni.height = Const.NOTE_MIN_SIZE;

      metaEditor.refreshGraph();
    }
  }

  private void editRelationship(RelationshipMeta hopinfo) {
    String name = hopinfo.toString();
    log.logDebug(toString(), Messages.getString("MetaEditorGraph.DEBUG_EDITING_RELATIONSHIP", name)); //$NON-NLS-1$
    metaEditor.editRelationship(hopinfo);
  }

  private void newRelationship() {

    if (metaEditor.getSchemaMeta().getActiveModel() == null)
      return;

    BusinessTable from = null;
    BusinessTable to = null;
    BusinessTable[] selectedTables = metaEditor.getSchemaMeta().getActiveModel().getSelectedTables();

    if (selectedTables != null){

      from = selectedTables[0];
      if (selectedTables.length > 1){
        to = metaEditor.getSchemaMeta().getActiveModel().getSelectedTables()[1];
      }
    }
    metaEditor.newRelationship(from, to);

  }

  private double calcRelationshipLine(Point A, Point B, Point X, Point Y) {
    double angle = calcAngle(A, B);

    if (angle > -45 && angle <= 45) {
      X.x = A.x + iconsize + Const.SYMBOLSIZE;
      Y.x = B.x - Const.SYMBOLSIZE;
      X.y = A.y + iconsize / 2;
      Y.y = B.y + iconsize / 2;
    } else if (angle > 45 && angle <= 135) {
      X.x = A.x + iconsize / 2;
      Y.x = B.x + iconsize / 2;
      X.y = A.y + iconsize + Const.SYMBOLSIZE;
      Y.y = B.y - Const.SYMBOLSIZE;
    } else if (angle > 135 || angle <= -135) {
      X.x = A.x - Const.SYMBOLSIZE;
      Y.x = B.x + iconsize + Const.SYMBOLSIZE;
      X.y = A.y + iconsize / 2;
      Y.y = B.y + iconsize / 2;
    } else if (angle <= -45) {
      X.x = A.x + iconsize / 2;
      Y.x = B.x + iconsize / 2;
      X.y = A.y - Const.SYMBOLSIZE;
      Y.y = B.y + iconsize + Const.SYMBOLSIZE;
    }

    return angle;
  }

  private double calcAngle(Point A, Point B) {
    return Math.atan2(B.y - A.y, B.x - A.x) * 360 / (2 * Math.PI);
  }

  private void drawArrow(GC gc, int line[], RelationshipMeta ri, double angle) {
    Point X = real2screen(line[0], line[1]);
    Point Y = real2screen(line[2], line[3]);

    // Main line connecting the 2 entities (tables)
    gc.drawLine(X.x, X.y, Y.x, Y.y);

    Point a, b, c;
    Point a2, b2, c2;

    // Start of the relationship N:, 1:, 0:
    // 1:
    if (ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_1_0
        || ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_1_1
        || ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_1_N || ri.isComplex()) {
      if (angle > -45 && angle <= 45) {
        b = new Point(X.x - Const.SYMBOLSIZE, X.y);
      } else if (angle > 45 && angle <= 135) {
        b = new Point(X.x, X.y - Const.SYMBOLSIZE);
      } else if (angle > 135 || angle <= -135) {
        b = new Point(X.x + Const.SYMBOLSIZE, X.y);
      } else // (angle<=-45)
      {
        b = new Point(X.x, X.y + Const.SYMBOLSIZE);
      }

      gc.drawLine(X.x, X.y, b.x, b.y);
    } else
    // N:
    if (ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_N_0
        || ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_N_1
        || ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_N_N) {
      if (angle > -45 && angle <= 45) {
        a = new Point(X.x - Const.SYMBOLSIZE, X.y - Const.SYMBOLSIZE);
        b = new Point(X.x - Const.SYMBOLSIZE, X.y);
        c = new Point(X.x - Const.SYMBOLSIZE, X.y + Const.SYMBOLSIZE);
      } else if (angle > 45 && angle <= 135) {
        a = new Point(X.x - Const.SYMBOLSIZE, X.y - Const.SYMBOLSIZE);
        b = new Point(X.x, X.y - Const.SYMBOLSIZE);
        c = new Point(X.x + Const.SYMBOLSIZE, X.y - Const.SYMBOLSIZE);
      } else if (angle > 135 || angle <= -135) {
        a = new Point(X.x + Const.SYMBOLSIZE, X.y - Const.SYMBOLSIZE);
        b = new Point(X.x + Const.SYMBOLSIZE, X.y);
        c = new Point(X.x + Const.SYMBOLSIZE, X.y + Const.SYMBOLSIZE);
      } else // (angle<=-45)
      {
        a = new Point(X.x - Const.SYMBOLSIZE, X.y + Const.SYMBOLSIZE);
        b = new Point(X.x, X.y + Const.SYMBOLSIZE);
        c = new Point(X.x + Const.SYMBOLSIZE, X.y + Const.SYMBOLSIZE);
      }

      gc.drawLine(X.x, X.y, a.x, a.y);
      gc.drawLine(X.x, X.y, b.x, b.y);
      gc.drawLine(X.x, X.y, c.x, c.y);
    } else // 0:
    {
      if (angle > -45 && angle <= 45) {
        a = new Point(X.x - Const.SYMBOLSIZE, X.y - Const.SYMBOLSIZE / 2);
      } else if (angle > 45 && angle <= 135) {
        a = new Point(X.x - Const.SYMBOLSIZE / 2, X.y - Const.SYMBOLSIZE);
      } else if (angle > 135 || angle <= -135) {
        a = new Point(X.x, X.y - Const.SYMBOLSIZE / 2);
      } else // (angle<=-45)
      {
        a = new Point(X.x - Const.SYMBOLSIZE / 2, X.y);
      }

      gc.drawOval(a.x, a.y, Const.SYMBOLSIZE, Const.SYMBOLSIZE);
    }

    // Start of the relationship :N, :1, :0
    // :1
    if (ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_0_1
        || ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_1_1
        || ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_N_1 || ri.isComplex()) {
      if (angle > -45 && angle <= 45) // -->
      {
        b2 = new Point(Y.x + Const.SYMBOLSIZE, Y.y);
      } else if (angle > 45 && angle <= 135) //    |
      { //   \|/
        //    '
        b2 = new Point(Y.x, Y.y + Const.SYMBOLSIZE);
      } else if (angle > 135 || angle <= -135) //  <--
      {
        b2 = new Point(Y.x - Const.SYMBOLSIZE, Y.y);
      } else // (angle<=-45)          //   .
      { //  /|\
        //   |
        b2 = new Point(Y.x, Y.y - Const.SYMBOLSIZE);
      }

      gc.drawLine(Y.x, Y.y, b2.x, b2.y);
    } else // :N
    if (ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_0_N
        || ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_1_N
        || ri.getType() == RelationshipMeta.TYPE_RELATIONSHIP_N_N) {
      if (angle > -45 && angle <= 45) // -->
      {
        a2 = new Point(Y.x + Const.SYMBOLSIZE, Y.y - Const.SYMBOLSIZE);
        b2 = new Point(Y.x + Const.SYMBOLSIZE, Y.y);
        c2 = new Point(Y.x + Const.SYMBOLSIZE, Y.y + Const.SYMBOLSIZE);
      } else if (angle > 45 && angle <= 135) //    |
      { //   \|/
        //    '
        a2 = new Point(Y.x + Const.SYMBOLSIZE, Y.y + Const.SYMBOLSIZE);
        b2 = new Point(Y.x, Y.y + Const.SYMBOLSIZE);
        c2 = new Point(Y.x - Const.SYMBOLSIZE, Y.y + Const.SYMBOLSIZE);
      } else if (angle > 135 || angle <= -135) //  <--
      {
        a2 = new Point(Y.x - Const.SYMBOLSIZE, Y.y - Const.SYMBOLSIZE);
        b2 = new Point(Y.x - Const.SYMBOLSIZE, Y.y);
        c2 = new Point(Y.x - Const.SYMBOLSIZE, Y.y + Const.SYMBOLSIZE);
      } else // (angle<=-45)          //   .
      { //  /|\
        //   |
        a2 = new Point(Y.x + Const.SYMBOLSIZE, Y.y - Const.SYMBOLSIZE);
        b2 = new Point(Y.x, Y.y - Const.SYMBOLSIZE);
        c2 = new Point(Y.x - Const.SYMBOLSIZE, Y.y - Const.SYMBOLSIZE);
      }

      gc.drawLine(Y.x, Y.y, a2.x, a2.y);
      gc.drawLine(Y.x, Y.y, b2.x, b2.y);
      gc.drawLine(Y.x, Y.y, c2.x, c2.y);
    } else // :0
    {
      if (angle > -45 && angle <= 45) {
        a2 = new Point(Y.x, Y.y - Const.SYMBOLSIZE / 2);
      } else if (angle > 45 && angle <= 135) {
        a2 = new Point(Y.x - Const.SYMBOLSIZE / 2, Y.y);
      } else if (angle > 135 || angle <= -135) {
        a2 = new Point(Y.x - Const.SYMBOLSIZE, Y.y - Const.SYMBOLSIZE / 2);
      } else // (angle<=-45)
      {
        a2 = new Point(Y.x - Const.SYMBOLSIZE / 2, Y.y - Const.SYMBOLSIZE);
      }

      gc.drawOval(a2.x, a2.y, Const.SYMBOLSIZE, Const.SYMBOLSIZE);
    }

  }

  private boolean pointOnLine(int x, int y, int line[]) {
    int dx, dy;
    int pm = HOP_SEL_MARGIN / 2;
    boolean retval = false;

    for (dx = -pm; dx <= pm && !retval; dx++) {
      for (dy = -pm; dy <= pm && !retval; dy++) {
        retval = pointOnThinLine(x + dx, y + dy, line);
      }
    }

    return retval;
  }

  private boolean pointOnThinLine(int x, int y, int line[]) {
    int x1 = line[0];
    int y1 = line[1];
    int x2 = line[2];
    int y2 = line[3];

    // Not in the square formed by these 2 points: ignore!
    if (!(((x >= x1 && x <= x2) || (x >= x2 && x <= x1)) && ((y >= y1 && y <= y2) || (y >= y2 && y <= y1))))
      return false;

    double angle_line = Math.atan2(y2 - y1, x2 - x1) + Math.PI;
    double angle_point = Math.atan2(y - y1, x - x1) + Math.PI;

    // Same angle, or close enough?
    if (angle_point >= angle_line - 0.01 && angle_point <= angle_line + 0.01)
      return true;

    return false;
  }

  /**
   * Note: we know that we have an active business model here so it's safe to use this BusinessModel.$
   *
   * @return a new SnapAllignDistribute object
   */
  private SnapAllignDistribute createSnapAllignDistribute() {
    BusinessModel activeModel = metaEditor.getSchemaMeta().getActiveModel();

    List<BusinessTable> elements;
    int[] indices;

    if (activeModel != null) {
      elements = activeModel.getSelectedDrawnBusinessTableList();
      indices = activeModel.getBusinessTableIndexes((BusinessTable[]) elements.toArray(new BusinessTable[elements
          .size()]));
    } else {
      elements = new ArrayList<BusinessTable>();
      indices = new int[] {};
    }
		// null in position 1 and 4 are related. The old code had null in the
		// 4th position which meant that the 1st argument was originally ignored.
    return new SnapAllignDistribute(null, elements, indices, null, this);
  }

  void snaptogrid(int size) {
    createSnapAllignDistribute().snaptogrid(size);
  }

  void allignleft() {
    createSnapAllignDistribute().allignleft();
  }

  void allignright() {
    createSnapAllignDistribute().allignright();
  }

  void alligntop() {
    createSnapAllignDistribute().alligntop();
  }

  void allignbottom() {
    createSnapAllignDistribute().allignbottom();
  }

  void distributehorizontal() {
    createSnapAllignDistribute().distributehorizontal();
  }

  public void distributevertical() {
    createSnapAllignDistribute().distributevertical();
    redraw();
  }

  private void drawRect(GC gc, Rectangle rect) {
    if (rect == null)
      return;

    gc.setLineStyle(SWT.LINE_DASHDOT);
    gc.setLineWidth(linewidth);
    gc.setForeground(GUIResource.getInstance().getColorGray());
    gc.drawRectangle(rect.x + offset.x, rect.y + offset.y, rect.width, rect.height);
    gc.setLineStyle(SWT.LINE_SOLID);
  }

  public void newProps() {
    GUIResource.getInstance().reload();
    iconsize = props.getIconSize();
    linewidth = props.getLineWidth();
  }

  public String toString() {
    return this.getClass().getName();
  }
}
