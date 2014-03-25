/*
* Copyright 2014 Justin Doyle
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*     http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
* 
*/

package com.jdoyle65.birdwl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Used to get configuration settings for the Bird Weight Logger.
 * @author Justin Doyle
 *
 */
public class ConfigParser {
	private Properties props;
	private int bridges;
	private int rfid_readers;
	private int[] bridge_serials;
	private int[] rfid_serials;
	private int timeout;
	private int data_rate;
	private BridgePair[] rfid_pairings;
	private HashMap<Integer, Integer> rfid_map;
	private HashMap<Integer, Integer> bridge_map;
	
	/**
	 * Default constructor.
	 * @param fileName The configuration file name.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ConfigParser(String fileName) throws FileNotFoundException, IOException {
		props = new Properties();
		props.load(new FileInputStream(new File(fileName)));
		getProperties();
	}
	
	/**
	 * Get the number of RFIDs supposed to be attached.
	 * @return The number of RFID readers supposed to be attached.
	 */
	public int getNumRfidReaders() { return rfid_readers; }
	
	/**
	 * Get the number of Bridges supposed to be attached.
	 * @return The number of Bridges supposed to be attached.
	 */
	public int getNumBridges() { return bridges; }
	
	/**
	 * Get an array of the serial numbers of attached RFIDs
	 * @return Array of RFID serial numbers.
	 */
	public int[] getRfidSerials() { return rfid_serials; }
	
	/**
	 * Get an array of the Bridge serial numbers attached.
	 * @return Array of Bridge serial numbers.
	 */
	public int[] getBridgeSerials() { return bridge_serials; }
	
	
	/**
	 * Get the serial number for the associated RFID index.
	 * @param i The index of the RFID.
	 * @return The serial number for given RFID index.
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public int getRfidSerial(int i) throws ArrayIndexOutOfBoundsException {
		if(i >= rfid_readers)
			throw new ArrayIndexOutOfBoundsException("RFID Serial index out of bounds\n");
		return rfid_serials[i];
	}
	
	/**
	 * Get the serial number for the associated Bridge index.
	 * @param i The index of the Bridge.
	 * @return The serial number for given Bridge index.
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public int getBridgeSerial(int i) throws ArrayIndexOutOfBoundsException {
		if (i >= bridges)
			throw new ArrayIndexOutOfBoundsException("Bridge Serial index out of bounds\n");
		return bridge_serials[i];
	}
	
	/**
	 * Get the Bridge paired with the given RFID reader.
	 * @param serial The serial number for the RFID reader.
	 * @return The index number for the paired Bridge. Returns -1 if no such pairing.
	 */
	public int getRfidBridge(int serial) {
		Integer i = rfid_map.get(serial);
		if(i == null)
			return -1;
		return rfid_pairings[i].getBridge();
	}
	
	/**
	 * Get the Load Cell paired with the given RFID reader.
	 * @param serial The serial number for the RFID reader.
	 * @return The index number for the paired Load Cell. Returns -1 if no such pairing.
	 */
	public int getRfidLoadCell(int serial) {
		Integer i = rfid_map.get(serial);
		if(i == null)
			return -1;
		return rfid_pairings[i].getLoadCell();
	}
	
	/**
	 * Get the time in seconds at which the program will wait for a
	 * Phidget to be attached.
	 * @return The time in seconds.
	 */
	public int getTimeout() {
		return timeout;
	}
	
	/**
	 * Get the rate in milliseconds at which to capture Bridge data.
	 * @return The rate in milliseconds.
	 */
	public int getDataRate() {
		return data_rate;
	}
	
	/**
	 * Get the offset for a specific Load Cell.
	 * @param bridge The Bridge index the cell is attached to.
	 * @param input The Load Cell index.
	 * @return The cell's offset (positive or negative).
	 */
	public double getLoadCellOffset(int bridge, int input) {
		String offset = props.getProperty("offset_" + bridge + "_" + input, "0");
		return Double.parseDouble(offset);
	}
	
	/**
	 * Get the K value for a specific Load Call.
	 * @param bridge The Bridge index the cell is attached to.
	 * @param input The Load Cell index.
	 * @return The cell's K value.
	 */
	public double getLoadCellKValue(int bridge, int input) {
		String k = props.getProperty("k_" + bridge + "_" + input, "1");
		return Double.parseDouble(k);
	}
	
	private void getProperties() {
		bridges = Integer.parseInt(props.getProperty("bridges", "0"));
		rfid_readers = Integer.parseInt(props.getProperty("rfid_readers", "0"));
		bridge_serials = new int[bridges];
		rfid_serials = new int[rfid_readers];
		rfid_pairings = new BridgePair[rfid_readers];
		rfid_map = new HashMap<Integer, Integer>(rfid_readers);
		bridge_map = new HashMap<Integer, Integer>(bridges);
		timeout = Integer.parseInt(props.getProperty("timeout", "5"));
		data_rate = Integer.parseInt(props.getProperty("data_rate", "500"));
		
		// Get all the bridges and their associated indexes.
		for(int i = 0; i < bridges; i++) {
			bridge_serials[i] = Integer.parseInt(
					props.getProperty("bridge_" + i, "-1"));
			bridge_map.put(bridge_serials[i], i);
		}
		// Get all rfids and their associated indexes.
		for(int i = 0; i < rfid_readers; i++) {
			rfid_serials[i] = Integer.parseInt(
					props.getProperty("rfid_" + i, "-1"));
			rfid_map.put(rfid_serials[i], i);
		}
		// Get all rfids' Bridge and Load Cell pairings
		for(int i = 0; i < rfid_readers; i++) {
			String p = props.getProperty("rfid_" + i + "_pair");
			String[] vals = p.split("-");
			rfid_pairings[i] = new BridgePair(
					Integer.parseInt(vals[0]),
					Integer.parseInt(vals[1]));
		}
	}
}
