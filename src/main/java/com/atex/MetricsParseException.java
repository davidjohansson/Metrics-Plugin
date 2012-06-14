package com.atex;

/**
 * Exception used to show Metrics parsing has failed.
 *
 */
public class MetricsParseException extends RuntimeException {
   public MetricsParseException(String msg, Exception e) {
      super(msg, e);
   }

   public MetricsParseException(String msg) {
      super(msg);
   }
}
