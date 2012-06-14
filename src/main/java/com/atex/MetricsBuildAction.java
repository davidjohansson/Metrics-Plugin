package com.atex;

import hudson.model.AbstractBuild;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

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
