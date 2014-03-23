package com.jdoyle65.birdwl;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.phidgets.BridgePhidget;
import com.phidgets.RFIDPhidget;

public class BirdWeightLogger {
	private static ConfigParser config;
	private BridgePhidget[] bridges;
	private RFIDPhidget[] rfids;
	
	public BirdWeightLogger(String[] args) throws FileNotFoundException, IOException {
		config = new ConfigParser("config.cfg");
		initOptions();
	}
	
	
	/**** PRIVATE METHODS ****/
	private void initOptions() {
		bridges = new BridgePhidget[config.getNumBridges()];
		rfids = new RFIDPhidget[config.getNumRfidReaders()];
	}
}
