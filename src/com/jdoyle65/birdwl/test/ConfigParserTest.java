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
	public void setUp() throws FileNotFoundException, IOException {
		cfg = new ConfigParser("test.cfg");
		assertNotNull(cfg);
	}

	@After
	public void tearDown() {
		cfg = null;
	}

	@Test
	public void testConfigParser() throws FileNotFoundException, IOException {
		cfg = new ConfigParser("test.cfg");
		assertNotNull(cfg);
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

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testGetRfidSerialFail() {
		cfg.getRfidSerial(1);
		cfg.getRfidSerial(-1);
	}

	@Test
	public void testGetBridgeSerialPass() {
		assertEquals(2211, cfg.getBridgeSerial(0));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testGetBridgeSerialFail() {
		cfg.getBridgeSerial(1);
		cfg.getBridgeSerial(-1);

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
	public void testGetTimout() {
		assertEquals(5, cfg.getTimeout());
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
