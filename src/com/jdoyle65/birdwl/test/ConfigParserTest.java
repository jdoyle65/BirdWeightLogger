package com.jdoyle65.birdwl.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jdoyle65.birdwl.ConfigParser;

public class ConfigParserTest {
	private ConfigParser cfg;
	
	@Before
	public void setUp() {
		try {
			cfg = new ConfigParser();
			assertNotNull(cfg);
		} catch (FileNotFoundException e) {
			fail("File not found.");
		} catch (IOException e) {
			fail("IO Exception");
		}
	}
	
	@After
	public void tearDown() {
		cfg = null;
	}
	
	@Test
	public void testConfigParser() {
		try {
			cfg = new ConfigParser();
			assertNotNull(cfg);
		} catch (FileNotFoundException e) {
			fail("File not found.");
		} catch (IOException e) {
			fail("IO Exception");
		}
	}

	@Test
	public void testGetNumRfidReaders() {
		assertEquals(1, cfg.getNumRfidReaders());
	}

	@Test
	public void testGetNumBridges() {
		assertEquals(1, cfg.getNumBridges());
	}

	@Test
	public void testGetRfidSerials() {
		assertArrayEquals(new int[] {
				3322
		}, cfg.getRfidSerials());
	}

	@Test
	public void testGetBridgeSerials() {
		assertArrayEquals(new int[] {
				2211
		}, cfg.getBridgeSerials());
	}

	@Test
	public void testGetRfidSerialPass() {
		assertEquals(3322, cfg.getRfidSerial(0));
	}
	
	@Test
	public void testGetRfidSerialFail() {
		try {
			cfg.getRfidSerial(1);
			cfg.getRfidSerial(-1);
			fail("Rfid array should have been out of bounds");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetBridgeSerial() {
		try {
			cfg.getBridgeSerial(1);
			cfg.getBridgeSerial(-1);
			fail("Bridge array should have been out of bounds");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}

}
