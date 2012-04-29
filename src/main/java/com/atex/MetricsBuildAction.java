package com.atex;

import hudson.model.AbstractBuild;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;

/**
 * Action used for Grinder report on build level.
 * 
 */
public class MetricsBuildAction extends AbstractMetricsAction {
	private final AbstractBuild<?, ?> build;
	private Iterator<MetricsData> metricsIterator;

	public MetricsBuildAction(AbstractBuild<?, ?> build, InputStream is,
			PrintStream logger) {
		this.build = build;
		
		MetricsReader reader = new MetricsReader(is, build.getId());
		metricsIterator = reader.getMetricsIterator();

		logger.println("Created Metrics results");
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public Iterator<MetricsData> getMetricsIterator(){
		return metricsIterator;
	}

}
