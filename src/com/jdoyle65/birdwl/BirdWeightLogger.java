package com.jdoyle65.birdwl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.phidgets.BridgePhidget;
import com.phidgets.PhidgetException;
import com.phidgets.RFIDPhidget;
import com.phidgets.event.TagGainEvent;
import com.phidgets.event.TagGainListener;

public class BirdWeightLogger {
	private static ConfigParser config;
	private HashMap<Integer, BridgePhidget> bridges;
	private HashMap<Integer, RFIDPhidget> rfids;
	
	public BirdWeightLogger(String[] args) throws FileNotFoundException, IOException {
		config = new ConfigParser("config.cfg");
		initOptions();
	}
	
	
	/**** PRIVATE METHODS ****/
	private void initOptions() {
		bridges = new HashMap<Integer, BridgePhidget>(config.getNumBridges());
		rfids = new HashMap<Integer, RFIDPhidget>(config.getNumRfidReaders());
	}
	
	
	/*** PRIVATE LISTENER CLASSES ***/
	private class RfidTagGainerListener implements TagGainListener {
		@Override
		public void tagGained(TagGainEvent tge) {
			RFIDPhidget rfid = (RFIDPhidget)tge.getSource();
			DateFormat df = new SimpleDateFormat("yyyy-dd-MM_HH:mm:ss");
			Date date = Calendar.getInstance().getTime();
			
			
			try {
				DataLogger dl = new DataLogger(df.format(date), rfid.getDeviceLabel());
				dl.
			} catch (IOException e) {
				e.printStackTrace();
			} catch (PhidgetException e) {
				e.printStackTrace();
			}
		}
		
	}
}
