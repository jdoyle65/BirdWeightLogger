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
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.TagGainEvent;
import com.phidgets.event.TagGainListener;
import com.phidgets.event.TagLossEvent;
import com.phidgets.event.TagLossListener;

/**
 * Program used to operate multiple paired RFID readers and Load Cells for
 * automated, hands free weighing of birds.
 * @author Justin Doyle
 *
 */
public class BirdWeightLogger {
	/*** PRIVATE VARIABLES ***/
	private static ConfigParser config;
	private HashMap<Integer, BridgePhidget> bridges;
	private HashMap<Integer, RFIDPhidget> rfids;

	/*** PUBLIC FINALS ***/
	public static final int WAIT_FOR_ATT = 1000*10;
	public final int DATA_RATE;

	/**
	 * Default constructor.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public BirdWeightLogger() throws FileNotFoundException, IOException {
		config = new ConfigParser("config.cfg");
		DATA_RATE = config.getDataRate();
		initOptions();
	}


	/************************************
	 *  
	 * PRIVATE METHODS
	 * 
	 ************************************/
	private void initOptions() {
		bridges = new HashMap<Integer, BridgePhidget>(config.getNumBridges());
		rfids = new HashMap<Integer, RFIDPhidget>(config.getNumRfidReaders());

		// Init all bridges in configuration file and open them.
		for(int i = 0; i < config.getNumBridges(); i++) {
			try {
				BridgePhidget tempB = new BridgePhidget();
				tempB.open(config.getBridgeSerial(i));
				tempB.addAttachListener(new AttachListener() {
					@Override
					public void attached(AttachEvent ae) {
						try {
							System.out.println("Bridge attached: " 
									+ ae.getSource().getDeviceLabel() + ", S/N: "
									+ ae.getSource().getDeviceID());
							BridgePhidget b = (BridgePhidget)ae.getSource();
							b.setDataRate(DATA_RATE);
							b.setGain(0, BridgePhidget.PHIDGET_BRIDGE_GAIN_128);
							b.setEnabled(0, true);
						}
						catch (PhidgetException e) {
							e.printStackTrace();
						}
					}
				});
				tempB.waitForAttachment(WAIT_FOR_ATT);
				bridges.put(tempB.getSerialNumber(), tempB);
			} catch (PhidgetException e) {
				System.out.println("Bridge " + i + " connection timed out.");
				e.printStackTrace();
			}
		}
		// Init all RFID reader in configuration file and open them.
		for(int i = 0; i < config.getNumRfidReaders(); i++) {
			try {
				RFIDPhidget tempR = new RFIDPhidget();
				Boolean tempFlag = new Boolean(false);
				tempR.open(config.getRfidSerial(i));
				tempR.addTagGainListener(new RfidTagGainerListener(tempR, tempFlag));
				tempR.addAttachListener(new AttachListener() {
					@Override
					public void attached(AttachEvent ae) {
						try {
							RFIDPhidget r = (RFIDPhidget)ae.getSource();
							System.out.println("RFID attached: " 
									+ ae.getSource().getDeviceLabel() + ", S/N: "
									+ ae.getSource().getDeviceID());
							r.setAntennaOn(true);
							r.setLEDOn(true);
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
					}
				});
				tempR.waitForAttachment(WAIT_FOR_ATT);
				rfids.put(tempR.getSerialNumber(), tempR);
			} catch (PhidgetException e) {
				System.out.println("RFID " + i + " connection timed out.");
				e.printStackTrace();
			}
		}

		// Main thread loop
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	/************************************
	 *  
	 * PRIVATE CLASSES
	 * 
	 ************************************/
	
	/**
	 * Primary class for Tag Gain events.
	 * @author Justin Doyle
	 *
	 */
	private class RfidTagGainerListener implements TagGainListener {
		private RFIDPhidget myRfid;
		private Boolean myFlag;
		
		public RfidTagGainerListener(RFIDPhidget rfid, Boolean flag) {
			myRfid = rfid;
			myFlag = flag;
		}

		@Override
		public void tagGained(TagGainEvent tge) {
			if(myRfid == tge.getSource())
			{
				myFlag = true;
				System.out.println("Tag gained: " + tge.getValue());
				new Thread(new GetData(myRfid, tge.getValue())).start();
			}
		}
	}

	/**
	 * Designed for getting load cell data on its own thread when RFID tag gain
	 * event is triggered. Also saves data captured into a CSV file.
	 * 
	 * @author Justin Doyle
	 *
	 */
	private class GetData implements Runnable {

		private RFIDPhidget myRfid;
		private String myTag;

		public GetData(RFIDPhidget r, String tag) {
			myRfid = r;
			myTag = tag;
		}

		@Override
		public void run() {
			try {
				// Set up info needed for logging data in CSV file
				DateFormat df = new SimpleDateFormat("yyyy-dd-MM_HH:mm:ss");
				Date date = Calendar.getInstance().getTime();
				String dateString = df.format(date);
				DataLogger dl = new DataLogger("log/" 
						+ myRfid.getDeviceLabel().toUpperCase() + "_"
						+ df.format(date) 
						+ ".csv", myRfid.getDeviceLabel());

				// Get all info pertaining to bridges and load cells
				int mySerial = myRfid.getSerialNumber();
				int myBridgeIndex = config.getRfidBridge(mySerial);
				int myLoadCell = config.getRfidLoadCell(mySerial);
				double myOffset = config.getLoadCellOffset(myBridgeIndex, myLoadCell);
				double myK = config.getLoadCellKValue(myBridgeIndex, myLoadCell);
				BridgePhidget myBridge = bridges.get(config.getBridgeSerial(myBridgeIndex));

				// Start grabbing data from the load cell
				double last_data = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset);
				while(myRfid.getTagStatus())
				{
					double data = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset);
					if(data != last_data)
						System.out.print("Data: " + data + "\r");
					last_data = data;
					dl.logRow(dateString, myTag, data);
					Thread.sleep(DATA_RATE);
				}

				System.out.println("\nWriting...");
				dl.writeFile();
				System.out.println("Success!");
				dl.close();

			} catch (PhidgetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
