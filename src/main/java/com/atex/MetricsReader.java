package com.atex;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MetricsReader {

	private Map<String, MetricsData> keysToData = new HashMap<String, MetricsData>();

	public MetricsReader(InputStream is, String build) throws MetricsParseException {
		try {
			Document doc = Jsoup.parse(is, "UTF-8", "http://localhost:8080");
			Elements tableRows = doc.getElementsByTag("tr");
			for(int i = 1; i < tableRows.size(); i++){
				Element tableRow = tableRows.get(i);
				Elements tableCells = tableRow.getElementsByTag("td"); 
				Element anchor = tableCells.first();
				String key = anchor.text();
				String totalTime = tableCells.get(2).text();
				MetricsData metric = new MetricsData(key, build);
				metric.setTotalTime(Integer.parseInt(totalTime));
				keysToData.put(key, metric);
			}
		} catch (IOException e) {
			throw new MetricsParseException("Failed to parse metrics data", e);
		}
	}
 
	public Iterator<MetricsData> getMetricsIterator() {
		return new Iterator<MetricsData>(){
			Iterator<String> keyIter = keysToData.keySet().iterator();

			public boolean hasNext() {
				return keyIter.hasNext();
			}

			public MetricsData next() {
				return keysToData.get(keyIter.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public int size() {
		return keysToData.size();
	}

	public MetricsData get(String string) {
		return keysToData.get(string);
	}

	@Override
	public String toString() {
		return "MetricsReader [keysToData=" + keysToData + "]";
	}
	
}
