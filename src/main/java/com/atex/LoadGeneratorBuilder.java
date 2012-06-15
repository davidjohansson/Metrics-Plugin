package com.atex;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.http.benchmark.Config;
import org.apache.http.benchmark.HttpBenchmark;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * Starts grinder to generate load on the site.
 * </p>
 * 
 */
public class LoadGeneratorBuilder extends Builder {

    private URL url;
    private int threads;
    private int requests;
    private boolean clearMetrics;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public LoadGeneratorBuilder(String url, String threads, String requests, Boolean clearMetrics) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Failed to set URL", e);
		}
        try {
			this.threads = Integer.parseInt(threads);
		} catch (NumberFormatException e) {
			this.threads = 1;
		}
        try {
			this.requests = Integer.parseInt(requests);
		} catch (NumberFormatException e) {
			this.requests = 100;
		}
        this.clearMetrics = clearMetrics;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getUrl() {
        return url.toString();
    }
    public String getThreads() {
        return Integer.toString(threads);
    }
    public String getRequests() {
    	return Integer.toString(requests);
    }
    public boolean getClearMetrics() {
    	return clearMetrics;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.

        List<Cause> buildStepCause = new ArrayList();
        buildStepCause.add(new Cause() {
          public String getShortDescription() {
            return "Build Step started by the load generator";
          }
        });
        listener.started(buildStepCause);

        PrintStream logger = listener.getLogger();
        
        if(clearMetrics) {
        	clearMetricsData(logger);
        }
        
        generateLoad(logger);

        listener.finished(Result.SUCCESS);
        
        return true;
    }

    private void clearMetricsData(PrintStream logger) {
    	
    }

    private void generateLoad(PrintStream logger) {
		logger.println("Generating load on " + url.toString());
        
        Config config = new Config();
        config.setKeepAlive(true);
        config.setRequests(requests);
        config.setThreads(threads);
        config.setUrl(url);
        
        HttpBenchmark ab = new HttpBenchmark(config);
        try {
			logger.println(ab.execute());
		} catch (Exception e) {
			logger.println("Failed during load generation," + e.getMessage());
		}
	}
    

	// Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link LoadGeneratorBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * Performs on-the-fly validation of the form field 'url'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckUrl(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0) {
            	return FormValidation.error("Please set a URL");
            }
            try {
        		new URL(value);
        		return FormValidation.ok();
        	}
        	catch(MalformedURLException e) {
        		return FormValidation.error("Malformed URL, " + e.getMessage());
        	}
        }
        
        public FormValidation doCheckThreads(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() > 0) {
            	try {
            		Integer.parseInt(value);
            	}
            	catch(NumberFormatException e) {
            		return FormValidation.error("Please enter a valid integer value, " + e.getMessage());
            	}
            }
            return FormValidation.ok();
        }
        
        public FormValidation doCheckRequests(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() > 0) {
            	try {
            		Integer.parseInt(value);
            	}
            	catch(NumberFormatException e) {
            		return FormValidation.error("Please enter a valid integer value, " + e.getMessage());
            	}
            }
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Load generator";
        }
    }
}

