package com.atex;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MetricsReader {

	private ArrayList<MetricsData> list =new ArrayList<MetricsData>();
	private static Logger logger = Logger.getLogger(MetricsReader.class.getSimpleName()); 

	public MetricsReader(InputStream is, int build, String wsURI, String authStr) throws MetricsParseException {
		
		try {
			Document doc = Jsoup.parse(is, "UTF-8", "http://localhost:8080");
			Elements tableRows = doc.getElementsByTag("tr");
			for(int i = 1; i < tableRows.size(); i++){
				Element tableRow = tableRows.get(i);
				Elements tableCells = tableRow.getElementsByTag("td"); 
				Element anchor = tableCells.first();
				String key = anchor.text();
				
				try {
					key = getExternalContentId(key, wsURI, authStr);
				}
				catch(Exception e) {
					logger.log(Level.INFO, "Failed to retrieve externalId for " + key, e);
				}

				String totalTime = tableCells.get(2).text();
				MetricsData metric = new MetricsData(key, build);
				metric.setTotalTime(Integer.parseInt(totalTime));
				list.add(metric);
			}
		} catch (IOException e) {
			throw new MetricsParseException("Failed to parse metrics data", e);
		}
	}

	private String getExternalContentId(String key, String wsURI, String authStr) throws MalformedURLException, IOException {		
		logger.log(Level.FINE, "Retrieving externalId from " + wsURI + "/content/" + key);
		URLConnection connection = new URL(wsURI + "/content/" + key).openConnection();
		connection.setRequestProperty("Accept", "application/polopoly-content+xml");
		
		byte[] authEncBytes = Base64.encodeBase64(authStr.getBytes());
		String authStringEnc = new String(authEncBytes);
		connection.setRequestProperty("Authorization", "Basic " + authStringEnc);

		InputStream input = connection.getInputStream();
		Document doc = Jsoup.parse(input, "UTF-8", "");
		return doc.getElementsByTag("metadata")
								 .first()
								 .getElementsByTag("externalid")
								 .first()
								 .text();
	}

	public List<MetricsData> getMetricsList(){
		return list;
	}

	@Override
	public String toString() {
		return "MetricsReader [metricsdata=" + list + "]";
	}
}
