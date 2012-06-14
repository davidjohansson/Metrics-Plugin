package com.atex;

import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Project;
import hudson.util.ChartUtil;

import java.io.IOException;
import java.util.Iterator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Action used for Metrics report on project level.
 * 
 */
public class MetricsProjectAction extends AbstractMetricsAction {

	private final Project<?, ?> project;

	private String imageUrl;

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public MetricsProjectAction(Project<?, ?> project) {
		this.project = project;
		this.imageUrl = "https://docs.google.com/spreadsheet/oimg?key=0Au2IXSomxct0dHZockc0QTNZTnFMaWMzVklRZHdXbmc&oid=1&zx=32aai2qjgfhw";
	}

	public Project<?, ?> getProject() {
		return project;
	}

	public void doMetricsAccumulatedGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {

		if (shouldReloadGraph(request, response)) {
			ChartUtil.generateGraph(request, response,
					createMeanRespLengthGraph(), 800, 150);
		}
	}


	private JFreeChart createMeanRespLengthGraph() {
		return createNumberBuildGraph("",
				"Length (bytes)");
	}

	
	private double[] getBuildKeysDouble(MetricsDataSet set){
		
		String [] keys = set.getBuildKeys();
		double[] doubles = new double[keys.length];

		for(int i = 0; i < keys.length; i++){
			doubles[i] = Integer.parseInt(keys[i]);
		}
		
		return doubles;
	}
	
	private JFreeChart createNumberBuildGraph(String valueName, String unitName) {

		MetricsDataSetBuilder builder = new MetricsDataSetBuilder();
		
		for (Object build : project.getBuilds()) {

			AbstractBuild abstractBuild = (AbstractBuild) build;

			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.SUCCESS)) {
				MetricsBuildAction action = abstractBuild
						.getAction(MetricsBuildAction.class);

				builder.add(action.getMetricsList().iterator());
			}
		}


		Iterator<MetricsDataSet> dataSetIter = builder.metricsDataSetIterator();
		
		DefaultXYDataset dataSet = new DefaultXYDataset();

		while(dataSetIter.hasNext()){
			MetricsDataSet metricsDataSet = dataSetIter.next();
			metricsDataSet.getBuildKeys();
			dataSet.addSeries(metricsDataSet.getKey(), new double[][]{metricsDataSet.getTotalTimes(), getBuildKeysDouble(metricsDataSet)} );
		}

		JFreeChart chart = ChartFactory.createXYLineChart("Accumulated metrics data", "Rendering time (ms)", "Build id", dataSet, PlotOrientation.HORIZONTAL, true, true, true);

		
		/*		
		JFreeChart chart = ChartFactory.createStackedAreaChart(valueName
				+ " Trend", "Build", unitName, builder.build(),
				PlotOrientation.VERTICAL, false, false, false);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.8f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(2, ColorPalette.RED);
		renderer.setSeriesPaint(1, ColorPalette.YELLOW);
		renderer.setSeriesPaint(0, ColorPalette.BLUE);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));
		*/

		return chart;
	}

	private boolean shouldReloadGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		return shouldReloadGraph(request, response,
				project.getLastSuccessfulBuild());
	}

}
