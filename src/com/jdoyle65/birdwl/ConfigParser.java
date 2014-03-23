package com.jdoyle65.birdwl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class ConfigParser {
	private Properties props;
	private int bridges;
	private int rfid_readers;
	private int[] bridge_serials;
	private int[] rfid_serials;
	private BridgePair[] rfid_pairings;
	private HashMap<Integer, Integer> rfid_map;
	private HashMap<Integer, Integer> bridge_map;
	
	public ConfigParser(String fileName) throws FileNotFoundException, IOException {
		props = new Properties();
		props.load(new FileInputStream(new File(fileName)));
		getProperties();
	}
	
	public int getNumRfidReaders() { return rfid_readers; }
	public int getNumBridges() { return bridges; }
	public int[] getRfidSerials() { return rfid_serials; }
	public int[] getBridgeSerials() { return bridge_serials; }
	public int getRfidSerial(int i) throws ArrayIndexOutOfBoundsException {
		if(i >= rfid_readers)
			throw new ArrayIndexOutOfBoundsException("RFID Serial index out of bounds\n");
		return rfid_serials[i];
	}
	
	public int getBridgeSerial(int i) throws ArrayIndexOutOfBoundsException {
		if (i >= bridges)
			throw new ArrayIndexOutOfBoundsException("Bridge Serial index out of bounds\n");
		return bridge_serials[i];
	}
	
	public int getRfidBridge(int serial) {
		int i = rfid_map.get(serial);
		return rfid_pairings[i].getBridge();
	}
	
	public int getRfidLoadCell(int serial) {
		int i = rfid_map.get(serial);
		return rfid_pairings[i].getLoadCell();
	}
	
	private void getProperties() {
		bridges = Integer.parseInt(props.getProperty("bridges", "0"));
		rfid_readers = Integer.parseInt(props.getProperty("rfid_readers", "0"));
		bridge_serials = new int[bridges];
		rfid_serials = new int[rfid_readers];
		rfid_pairings = new BridgePair[rfid_readers];
		rfid_map = new HashMap<Integer, Integer>(rfid_readers);
		bridge_map = new HashMap<Integer, Integer>(bridges);
		
		for(int i = 0; i < bridges; i++) {
			bridge_serials[i] = Integer.parseInt(
					props.getProperty("bridge_" + i, "-1"));
			bridge_map.put(bridge_serials[i], i);
		}
		for(int i = 0; i < rfid_readers; i++) {
			rfid_serials[i] = Integer.parseInt(
					props.getProperty("rfid_" + i, "-1"));
			rfid_map.put(rfid_serials[i], i);
		}
		for(int i = 0; i < rfid_readers; i++) {
			String p = props.getProperty("rfid_" + i + "_pair");
			String[] vals = p.split("-");
			rfid_pairings[i] = new BridgePair(
					Integer.parseInt(vals[0]),
					Integer.parseInt(vals[1]));
		}
	}
}
