package com.jdoyle65.birdwl.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.jdoyle65.birdwl.DataLogger;

public class DataLoggerTest {
	DataLogger log;
	CSVReader reader;

	@Before
	public void setUp() throws Exception {
		log = new DataLogger("log/test.csv", 1);
	}

	@After
	public void tearDown() throws Exception {
		log = null;
	}

	@Test
	public void testDataLoggerStringString() {
		try {
			log = new DataLogger("log/test.csv", "1");
		} catch (IOException e) {
			fail("DataLoggerStringString not expected to have IOException");
		}
	}

	@Test
	public void testDataLoggerStringInt() {
		try {
			log = new DataLogger("log/test.csv", 1);
		} catch (IOException e) {
			fail("DataLoggerStringInt not expected to have IOException");
		}
	}

	@Test
	public void testLogRow() {
		try {
			log.logRow("Now", "11", 22.0);
			log.writeFile();
			log.close();
			CSVReader read = new CSVReader(new FileReader("log/test.csv"));
			String[] in = read.readNext();
			double doub = Double.parseDouble(in[2]);
			assertEquals("Now", in[0]);
			assertEquals("11", in[1]);
			assertEquals("Double value okay.", 22.0, doub, 0.01);
			read.close();
		} catch (IOException e) {
			fail("logRow had IOException");
		}
	}

}
