package com.atex;

import java.util.ArrayList;
import java.util.List;


public class MetricsDataSet {

	private String key;
	List<Double> totalTimes = new ArrayList<Double>();
	List<String> buildKeys = new ArrayList<String>();
	
	public MetricsDataSet(String key)
	{
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public double[] getTotalTimes() {
		return convertIntegers(totalTimes);
	}

	private static double[] convertIntegers(List<Double> doubles) {
		double[] ret = new double[doubles.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = doubles.get(i).intValue();
		}
		return ret;
	}

	public String[] getBuildKeys() {
		return buildKeys.toArray(new String[]{});
	}
}