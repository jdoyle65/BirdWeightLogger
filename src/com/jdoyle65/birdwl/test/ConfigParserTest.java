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
			cfg = new ConfigParser("test.cfg");
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
			cfg = new ConfigParser("test.cfg");
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
			fail("Rfid array should have been out of bounds");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
		try {
			cfg.getRfidSerial(-1);
			fail("Rfid array should have been out of bounds");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetBridgeSerialPass() {
		try {
			assertEquals(2211, cfg.getBridgeSerial(0));
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Bridge serial should not have been out of bounds.");
		}
	}

	@Test
	public void testGetBridgeSerialFail() {
		try {
			cfg.getBridgeSerial(1);
			fail("Bridge array should have been out of bounds");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
		try {
			cfg.getBridgeSerial(-1);
			fail("Bridge array should have been out of bounds");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetRfidBridge() {
		int b = cfg.getRfidBridge(3322);
		assertEquals(0, b);
		b = cfg.getRfidBridge(00);
		assertEquals(-1, b);
	}
	
	@Test
	public void testGetRfidLoadCell() {
		int b = cfg.getRfidLoadCell(3322);
		assertEquals(0, b);
		b = cfg.getRfidLoadCell(00);
		assertEquals(-1, b);
	}
	
	@Test 
	public void testGetDataRate() {
		assertEquals(100, cfg.getDataRate());
	}
	
	@Test
	public void testGetLoadCellOffset() {
		double offset = cfg.getLoadCellOffset(0, 0);
		assertEquals("Load Cell offset okay.", 0, offset, 0.01);
	}
	
	@Test
	public void testGetLoadCellKValue() {
		double k = cfg.getLoadCellKValue(0, 0);
		assertEquals("Load Cell K value okay.", 1, k, 0.01);
	}

}
