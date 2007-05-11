package org.pentaho.pms.schema.concept.editor;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.concept.ConceptInterface;

/**
 * Given a <code>ConceptInterface</code>, returns the <code>IConceptModel</code> wrapping the
 * <code>ConceptInterface</code>. (If an <code>IConceptModel</code> doesn't already exist for
 * a <code>ConceptInterface</code>, one is created and saved in this registry for later requests.) This is to make sure
 * that given a <code>ConceptInterface</code>, all listeners subscribe and operate on the same instance. Only higher
 * level models, such as ITableModel should use this class. Client objects should be unaware of this class.
 *
 * For now, the planned use of this class is among all controls within a single dialog. Consequently, its lifetime is
 * expected to be short.
 * @author mlowery
 */
public class ConceptModelRegistry {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptModelRegistry.class);

  // ~ Instance fields =================================================================================================

  /**
   * Keys are <code>ConceptInterface</code> instances. Values are <code>IConceptModel</code> instances.
   */
  private Map map = new IdentityHashMap();

  // ~ Constructors ====================================================================================================

  public ConceptModelRegistry() {
    super();
  }

  // ~ Methods =========================================================================================================

  public synchronized IConceptModel getConceptModel(final ConceptInterface concept) {
    if (map.containsKey(concept)) {
      if (logger.isDebugEnabled()) {
        logger.debug("found concept model in registry");
      }
      return (IConceptModel) map.get(concept);
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("did not find concept model in registry; creating new concept model");
      }
      IConceptModel conceptModel = new ConceptModel(concept);
      map.put(concept, conceptModel);
      return conceptModel;
    }
  }
}
