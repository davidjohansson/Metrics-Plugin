package com.atex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MetricsDataSetBuilder {

	private Map<String, MetricsDataSet> data = new HashMap<String, MetricsDataSet>();

	public Iterator<MetricsDataSet> metricsDataSetIterator() {

		return new Iterator<MetricsDataSet>() {

			Iterator<String> keyIterator = data.keySet().iterator();

			public boolean hasNext() {
				return keyIterator.hasNext();
			}

			public MetricsDataSet next() {
				return data.get(keyIterator.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

	}

	public void add(Iterator<MetricsData> iterator) {

		while (iterator.hasNext()) {
			MetricsData metric = iterator.next();
			MetricsDataSet set = data.get(metric.getKey());
			if (set == null) {
				set = new MetricsDataSet(metric.getKey());
				data.put(metric.getKey(), set);
			}
			set.buildKeys.add(Integer.toString(metric.getBuild()));
			
			addTotalTime(set, metric.getTotalTime());
		}
	}

	private void addTotalTime(MetricsDataSet set, int totalTime) {
		set.totalTimes.add((double) totalTime);
	}

	public MetricsDataSet getDataSet(String key) {
		return data.get(key);
	}
}
