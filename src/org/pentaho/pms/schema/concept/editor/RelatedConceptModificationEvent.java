package org.pentaho.pms.schema.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.concept.ConceptInterface;

public class RelatedConceptModificationEvent extends ConceptModificationEvent {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(RelatedConceptModificationEvent.class);

  public static final int ADD_RELATED_CONCEPT = 0;

  public static final int CHANGE_RELATED_CONCEPT = 1;

  public static final int REMOVE_RELATED_CONCEPT = 2;

  // ~ Instance fields =================================================================================================

  private int relatedConcept;

  private int type;

  private ConceptInterface oldValue;

  private ConceptInterface newValue;

  // ~ Constructors ====================================================================================================

  public RelatedConceptModificationEvent(final Object source, final int type, final int relatedConcept,
      final ConceptInterface oldValue, final ConceptInterface newValue) {
    super(source);
    this.type = type;
    this.relatedConcept = relatedConcept;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public ConceptInterface getNewValue() {
    return newValue;
  }

  public ConceptInterface getOldValue() {
    return oldValue;
  }

  /**
   * Returns one of the <code>static final</code> members of <code>IConceptModel</code>. (e.g. <code>REL_*</code>)
   * @return
   */
  public int getRelatedConcept() {
    return relatedConcept;
  }

  public int getType() {
    return type;
  }

  // ~ Methods =========================================================================================================

}
