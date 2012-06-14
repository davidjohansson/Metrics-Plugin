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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * The publisher creates the results we want from the Metrics execution.
 * 
 */
public class MetricsPublisher extends Recorder {

	private final static Logger LOG = Logger
			.getLogger(com.atex.MetricsPublisher.class.getName());
	private String name;

	@DataBoundConstructor
	public MetricsPublisher(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		PrintStream logger = listener.getLogger();
		InputStream is = null;
		try {
			//TODO: can name be made required in the gui?
			if(! name.endsWith("/")){
				name = name + "/";
			}
			URL metricsUrl = new URL(name + 
					"polopolydevelopment/Metrics?name=_-_-RenderStats__--element__--ownTotal&op=ownTotal&res=hour&fmt=html&asc=false&totalop=ownTotal&col=0");
			is = metricsUrl.openStream();
			build.addAction(new MetricsBuildAction(build, is, logger));
		} catch (MalformedURLException mue) {
			build.setResult(Result.FAILURE);
			throw new IOException("Failed to connect to Metrics servlet using provided host address '" + name + "'", mue);
		} catch (MetricsParseException gpe) {
			LOG.log(Level.WARNING, "Failed to parse metrics data", gpe);
			build.setResult(Result.FAILURE);
		} finally {
			if(is != null){
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

	@Extension
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {

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
