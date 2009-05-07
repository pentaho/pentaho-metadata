package org.pentaho.metadata.util;

public class SQLModelGeneratorException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -6089798664483298023L;

  /**
   * 
   */
  public SQLModelGeneratorException() {
    super();
  }

  /**
   * @param message
   */
  public SQLModelGeneratorException(String message) {
    super(message);
  }

  /**
   * @param message
   * @param reas
   */
  public SQLModelGeneratorException(String message, Throwable reas) {
    super(message, reas);
  }

  /**
   * @param reas
   */
  public SQLModelGeneratorException(Throwable reas) {
    super(reas);
  }

}
