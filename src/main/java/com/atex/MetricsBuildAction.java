package com.atex;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.QueryParameter;

/**
 * Action used for Grinder report on build level.
 * 
 */
public class MetricsBuildAction extends AbstractMetricsAction {
	private final AbstractBuild<?, ?> build;
	private List<MetricsData> metricsList;

	public MetricsBuildAction(AbstractBuild<?, ?> build, InputStream is,
			PrintStream logger, String wsURI, String authStr) {
		this.build = build;
		MetricsReader reader = new MetricsReader(is, build.getNumber(), wsURI, authStr);
		metricsList = reader.getMetricsList();

		logger.println("Created Metrics results");
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public List<MetricsData> getMetricsList(){
		return metricsList;
	}
	


}
