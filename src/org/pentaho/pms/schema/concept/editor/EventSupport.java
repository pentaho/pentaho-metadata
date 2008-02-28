package org.pentaho.pms.schema.concept.editor;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A more generic version of <code>PropertyChangeSupport</code>. Encourages thread-safe listener management and event
 * firing. Use <code>getListeners</code> in your "notify listeners" methods.
 *
 * TODO move this class into pentaho commons
 *
 * @author mlowery
 */
public class EventSupport {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(EventSupport.class);

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public EventSupport() {
    super();
  }

  // ~ Methods =========================================================================================================

  private HashSet<Object> listeners = new HashSet<Object>();

  public synchronized void addListener(final Object listener) {
    listeners.add(listener);
  }

  public synchronized void removeListener(final Object listener) {
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
