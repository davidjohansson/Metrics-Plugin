package com.atex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.Assert;

import org.junit.Test;

public class MetricsReaderTest {

	@Test
	public void testReadData(){

		MetricsReader reader;
		try {
			reader = new MetricsReader(new FileInputStream(new File("src/test/resources/com/atex/test1.html")), 1, "http://localhost:8080", "credentials");
			
			System.out.println(reader);
			Assert.assertTrue(reader.getMetricsList().size() == 19);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
