package com.atex;

/**
 * Represents a Metric table row for a specific build
 * 
 */
public class MetricsData {

	private String key;
	private int build;
	private int totalTime;

	public String getKey() {
		return key;
	}

	public int getBuild() {
		return build;
	}

	public MetricsData(String key, int build) {
		this.key = key;
		this.build = build;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	@Override
	public String toString() {
		return "Metric [totalTime=" + totalTime + "]";
	}
}
