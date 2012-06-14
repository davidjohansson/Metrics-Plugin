package com.atex;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MetricsReader {


	private ArrayList<MetricsData> list =new ArrayList<MetricsData>(); 

	public MetricsReader(InputStream is, int build) throws MetricsParseException {
		try {
			Document doc = Jsoup.parse(is, "UTF-8", "http://localhost:8080");
			Elements tableRows = doc.getElementsByTag("tr");
			for(int i = 1; i < tableRows.size(); i++){
				Element tableRow = tableRows.get(i);
				Elements tableCells = tableRow.getElementsByTag("td"); 
				Element anchor = tableCells.first();
				String key = anchor.text();
				
				//Get externalid for that key here and save that instead
				
				String totalTime = tableCells.get(2).text();
				MetricsData metric = new MetricsData(key, build);
				metric.setTotalTime(Integer.parseInt(totalTime));
				list.add(metric);
			}
		} catch (IOException e) {
			throw new MetricsParseException("Failed to parse metrics data", e);
		}
	}

	public List<MetricsData> getMetricsList(){
		return list;
		
	}


	@Override
	public String toString() {
		return "MetricsReader [metricsdata=" + list + "]";
	}
	
}
