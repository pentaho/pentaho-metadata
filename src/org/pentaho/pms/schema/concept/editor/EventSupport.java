package org.pentaho.pms.schema.concept.editor;

import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

/**
 * A more generic version of <code>PropertyChangeSupport</code>. Encourages thread-safe listener management and event
 * firing. Use <code>getListeners</code> in your "event fire" methods.
 *
 * TODO move this class into pentaho commons
 *
 * @author mlowery
 */
public class EventSupport {
  private HashSet listeners = new HashSet();

  public synchronized void addListener(final EventListener listener) {
    listeners.add(listener);
  }

  public synchronized void removeListener(final EventListener listener) {
    listeners.remove(listener);
  }

  public Set getListeners() {
    Set targets;
    synchronized (this) {
      targets = (Set) listeners.clone();
    }
    return targets;
  }

}
