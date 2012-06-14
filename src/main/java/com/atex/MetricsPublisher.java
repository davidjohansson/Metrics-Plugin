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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * The publisher creates the results we want from the Metrics execution.
 *
 */
public class MetricsPublisher extends Recorder {

   private String name;

   @DataBoundConstructor
   public MetricsPublisher(String name) {
      this.name = name;
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
    	  
    	  InputStream is = new FileInputStream(new File("/Applications/apache-tomcat-7.0.26/webapps/ROOT/metricsdata.html"));
         try {
            build.addAction(new MetricsBuildAction(build, is, logger));
         } catch (MetricsParseException gpe) {
            logger.println("Failed to parse metrics report");
            build.setResult(Result.FAILURE);
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
