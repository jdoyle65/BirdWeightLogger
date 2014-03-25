package com.jdoyle65.birdwl.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.jdoyle65.birdwl.BridgePair;

public class BridgePairTest {
	private BridgePair bp;

	@Before
	public void setUp() throws Exception {
		bp = new BridgePair(1, 2);
	}

	@After
	public void tearDown() throws Exception {
		bp = null;
	}

	@Test
	public void testGetBridge() {
		assertEquals(1, bp.getBridge());
	}

	@Test
	public void testGetLoadCell() {
		assertEquals(2, bp.getLoadCell());
	}

}
