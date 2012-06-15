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
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.atex.LoadGeneratorBuilder.DescriptorImpl;

/**
 * The publisher creates the results we want from the Metrics execution.
 * 
 */
public class MetricsPublisher extends Recorder {

	private final static Logger LOG = Logger
			.getLogger(com.atex.MetricsPublisher.class.getName());
	private String metricsServletURI;
	private final String wsURI;
	private final String authStr;

	@DataBoundConstructor
	public MetricsPublisher(String metricsServletURI, String wsURI, String authStr) {
		this.metricsServletURI = metricsServletURI;
		this.wsURI = wsURI;
		this.authStr = authStr;
	}

	public String getWsURI() {
		return wsURI;
	}

	public String getAuthStr() {
		return authStr;
	}

	public String getMetricsServletURI() {
		return metricsServletURI;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		PrintStream logger = listener.getLogger();
		InputStream is = null;
		try {
			if (!metricsServletURI.endsWith("/")) {
				metricsServletURI = metricsServletURI + "/";
			}
			URL metricsUrl = new URL(
					metricsServletURI
							+ "?name=_-_-RenderStats__--element__--ownTotal&op=ownTotal&res=hour&fmt=html&asc=false&totalop=ownTotal&col=0");
			is = metricsUrl.openStream();
			build.addAction(new MetricsBuildAction(build, is, logger, wsURI,
					authStr));
		} catch (MalformedURLException mue) {
			build.setResult(Result.FAILURE);
			throw new IOException(
					"Failed to connect to Metrics servlet using provided address '"
							+ metricsServletURI + "'", mue);
		} catch (MetricsParseException gpe) {
			LOG.log(Level.WARNING, "Failed to parse metrics data", gpe);
			build.setResult(Result.FAILURE);
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return true;

	}

	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return project instanceof Project ? new MetricsProjectAction(
				(Project) project) : null;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	// Overridden for better type safety.
	// If your plugin doesn't really define any property on Descriptor,
	// you don't have to do this.
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {

		public FormValidation doCheckMetricsServletURI(
				@QueryParameter String value) throws IOException,
				ServletException {
			return ValidationUtil.checkUrl(value);
		}

		public FormValidation doCheckWsURI(@QueryParameter String value)
				throws IOException, ServletException {
			return ValidationUtil.checkUrl(value);
		}

		public FormValidation doCheckAuthStr(@QueryParameter String value)
				throws IOException, ServletException {
			return ValidationUtil.checkAuthString(value);
		}

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
