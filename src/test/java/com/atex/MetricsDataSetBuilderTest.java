package com.atex;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class MetricsDataSetBuilderTest {

	private MetricsDataSetBuilder builder = new MetricsDataSetBuilder();

	@Before
	public void setup() throws Exception{
		MetricsReader reader1 = new MetricsReader(new FileInputStream(new File("src/test/resources/com/atex/test1.html")), 1, "http://localhost:8080", "credentials");
		MetricsReader reader2 = new MetricsReader(new FileInputStream(new File("src/test/resources/com/atex/test2.html")), 2, "http://localhost:8080", "credentials");

		builder.add(reader1.getMetricsList().iterator());
		builder.add(reader2.getMetricsList().iterator());
	}
	
	@Test
	public void testBuildDataSet() throws Exception{
		
		double[] totalTimes = builder.getDataSet("15.288").getTotalTimes();
		Assert.assertTrue(totalTimes.length == 2);
		Assert.assertEquals(71,0D, totalTimes[0]);
		Assert.assertEquals(71,0D, totalTimes[1]);
	}
	
	
	@Test
	public void testDataSetIterator() throws Exception{
		Iterator<MetricsDataSet> dataSetIter = builder.metricsDataSetIterator();
		int count = 0;
		boolean foundKey = false;
		while(dataSetIter.hasNext()){
			count++;
			MetricsDataSet dataSet = dataSetIter.next();
			if(dataSet.getKey().equals("15.288")){
				foundKey = true;
				String[] buildKeys = dataSet.getBuildKeys();
				Assert.assertEquals("1", buildKeys[0]);
				Assert.assertEquals("2", buildKeys[1]);
				
				double[] totalTimes = dataSet.getTotalTimes();
				Assert.assertTrue(totalTimes.length == 2);
				Assert.assertEquals(71,0D, totalTimes[0]);
				Assert.assertEquals(71,0D, totalTimes[1]);
			}
		}
		Assert.assertEquals(19, count);
		Assert.assertTrue(foundKey);

	}
}
