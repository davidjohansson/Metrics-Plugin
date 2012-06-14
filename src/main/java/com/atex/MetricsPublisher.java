package com.atex;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Project;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * The publisher creates the results we want from the Metrics execution.
 *
 */
public class MetricsPublisher extends Recorder {

	private final static Logger LOG = Logger.getLogger(com.atex.MetricsPublisher.class.getName());
   private String name;
	private final String wsURI;
	private final String authStr;

   @DataBoundConstructor
   public MetricsPublisher(String name, String wsURI, String authStr) {
      this.name = name;
	this.wsURI = wsURI;
	this.authStr = authStr;
   }

   public String getName() {
      return name;
   }

   @Override
   public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
      PrintStream logger = listener.getLogger();
      
      //if (build.getWorkspace().child(name).exists()) {
        // InputStream is = build.getWorkspace().child(name).read();
    	  
//    	  InputStream is = new FileInputStream(new File("/Applications/apache-tomcat-7.0.26/webapps/ROOT/metricsdata.html"));
  
      URL metricsUrl = new URL("http://localhost:8080/polopolydevelopment/Metrics?name=_-_-RenderStats__--element__--ownTotal&op=ownTotal&res=hour&fmt=html&asc=false&totalop=ownTotal&col=0");
     
      InputStream is = metricsUrl.openStream();
      try {
            build.addAction(new MetricsBuildAction(build, is, logger, wsURI, authStr));
         } catch (MetricsParseException gpe) {
            LOG.log(Level.WARNING, "Failed to parse metrics data", gpe);
            build.setResult(Result.FAILURE);
     }
         finally{
        	 is.close();
         }
         
    //  } else {
      //   logger.println("Grinder out* log file not found!");
       //  build.setResult(Result.FAILURE);
      //}

      return true;
   }

   @Override
   public Action getProjectAction(AbstractProject<?, ?> project) {
      return project instanceof Project ? new MetricsProjectAction((Project)project) : null;
   }

   public BuildStepMonitor getRequiredMonitorService() {
      return BuildStepMonitor.NONE;
   }

   @Extension
   public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

      public DescriptorImpl() {
         super(MetricsPublisher.class);
      }

      public String getDisplayName() {
         return MetricsPlugin.DISPLAY_NAME;
      }

      @Override
      public boolean isApplicable(Class<? extends AbstractProject> jobType) {
         return Project.class.isAssignableFrom(jobType);
      }
   }
}
