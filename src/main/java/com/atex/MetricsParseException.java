package com.atex;

/**
 * Exception used to show Grinder parsing has failed.
 *
 * @author Eivind B Waaler
 */
public class MetricsParseException extends RuntimeException {
   public MetricsParseException(String msg, Exception e) {
      super(msg, e);
   }

   public MetricsParseException(String msg) {
      super(msg);
   }
}
