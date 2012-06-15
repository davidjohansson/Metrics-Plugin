package com.atex;

import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Project;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Action used for Metrics report on project level.
 * 
 */
public class MetricsProjectAction extends AbstractMetricsAction {

	private final Project<?, ?> project;

	private DataSetBuilder<String, NumberOnlyBuildLabel> combinedGraphBuilder;
	private TreeMap<String, DataSetBuilder<String, NumberOnlyBuildLabel>> individualGraphBuilders;

	public List<String> getIndividualGraphBuilders() {
		List<String> list = new ArrayList<String>(
				individualGraphBuilders.keySet());
		Collections.sort(list);
		return list;
	}

	public MetricsProjectAction(Project<?, ?> project) {
		this.project = project;
	}

	public Project<?, ?> getProject() {
		return project;
	}

	public void init() {
		combinedGraphBuilder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		individualGraphBuilders = new TreeMap<String, DataSetBuilder<String, NumberOnlyBuildLabel>>();

		for (Object build : project.getBuilds()) {

			AbstractBuild abstractBuild = (AbstractBuild) build;

			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.SUCCESS)) {
				MetricsBuildAction action = abstractBuild
						.getAction(MetricsBuildAction.class);

				Iterator<MetricsData> i = action.getMetricsList().iterator();
				while (i.hasNext()) {
					MetricsData data = i.next();
					DataSetBuilder<String, NumberOnlyBuildLabel> individualGraphBuilder = individualGraphBuilders
							.get(data.getKey());
					if (individualGraphBuilder == null) {
						individualGraphBuilder = new DataSetBuilder<String, NumberOnlyBuildLabel>();
						individualGraphBuilders.put(data.getKey(),
								individualGraphBuilder);
					}

					addDataToBuilder(combinedGraphBuilder, data, abstractBuild);
					addDataToBuilder(individualGraphBuilder, data,
							abstractBuild);
				}
			}
		}
	}

	public void doMetricsIndividualGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		String key = request.getParameter("key");
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = individualGraphBuilders
				.get(key);
		if (builder != null) {
			ChartUtil.generateGraph(request, response,
					createMetricsGraph(builder), 800, 400);
		}
	}

	public void doMetricsAccumulatedGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {

		if (shouldReloadGraph(request, response)) {
			// Draw the common graph:
			ChartUtil.generateGraph(request, response,
					createMetricsGraph(combinedGraphBuilder), 1200, 600);

		}
	}

	private void addDataToBuilder(
			DataSetBuilder<String, NumberOnlyBuildLabel> builder,
			MetricsData data, AbstractBuild build) {
		builder.add(new Integer(data.getTotalTime()), data.getKey(),
				new NumberOnlyBuildLabel(build));
	}

	private JFreeChart createMetricsGraph(
			DataSetBuilder<String, NumberOnlyBuildLabel> graphBuilder) {
		JFreeChart chart = ChartFactory.createLineChart("", "Build",
				"Rendering time (ms)", graphBuilder.build(),
				PlotOrientation.VERTICAL, true, true, true);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.8f);
		plot.setRangeGridlinesVisible(false);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		CategoryItemRenderer renderer = plot.getRenderer();
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

	private boolean shouldReloadGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		return shouldReloadGraph(request, response,
				project.getLastSuccessfulBuild());
	}

}
