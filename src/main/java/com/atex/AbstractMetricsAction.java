package com.atex;

import hudson.model.Action;
import hudson.model.Run;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * Abstract class with functionality common to all Grinder actions.
 *
 */
public class AbstractMetricsAction implements Action {
   public String getIconFileName() {
      return MetricsPlugin.ICON_FILE_NAME;
   }

   public String getDisplayName() {
      return MetricsPlugin.DISPLAY_NAME;
   }

   public String getUrlName() {
      return MetricsPlugin.URL;
   }
   
   protected boolean shouldReloadGraph(StaplerRequest request, StaplerResponse response, Run build) throws IOException {
	      return !request.checkIfModified(build.getTimestamp(), response);
	   }
}
