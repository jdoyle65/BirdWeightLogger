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
import java.util.Scanner;

import com.phidgets.BridgePhidget;
import com.phidgets.PhidgetException;
import com.phidgets.RFIDPhidget;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.BridgeDataEvent;
import com.phidgets.event.BridgeDataListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
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
	public final int WAIT_FOR_ATT;
	public final int DATA_RATE;

	/*** PUBLIC STATICS ***/
	// TODO Tares are only temporary. Need to set up individual load cell taring.
	public static double TARE_0 = 0.0;
	public static double TARE_1 = 0.0;
	public static double POSSIBLE_TARE_0 = 0.0;
	public static double POSSIBLE_TARE_1 = 0.0;


	/**
	 * Default constructor.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public BirdWeightLogger() throws FileNotFoundException, IOException {
		config = new ConfigParser("config.cfg");
		WAIT_FOR_ATT = config.getTimeout() * 1000;
		DATA_RATE = config.getDataRate();
		initOptions();
	}


	/************************************
	 *  
	 * PRIVATE METHODS
	 * 
	 ************************************/
	private void initOptions() {
		System.out.println("Starting Bird Weight Logger...");
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
									+ ae.getSource().getSerialNumber());
							BridgePhidget b = (BridgePhidget)ae.getSource();
							b.setDataRate(DATA_RATE);
							b.setGain(0, BridgePhidget.PHIDGET_BRIDGE_GAIN_32);
							b.setEnabled(0, true);
							b.setGain(1, BridgePhidget.PHIDGET_BRIDGE_GAIN_32);
							b.setEnabled(1, true);

						}
						catch (PhidgetException e) {
							e.printStackTrace();
						}
					}
				});
				tempB.addBridgeDataListener(new BridgeDataListener() {
					
					@Override
					public void bridgeData(BridgeDataEvent arg0) {
						BridgePhidget b = (BridgePhidget)arg0.getSource();
						try {

							double data_0;
							double data_1;
							double k_0 = config.getLoadCellKValue(0, 0);
							double k_1 = config.getLoadCellKValue(0, 1);
							double offset_0 = config.getLoadCellOffset(0, 0);
							double offset_1 = config.getLoadCellOffset(0, 1);

							POSSIBLE_TARE_0 = k_0 * (b.getBridgeValue(0) + offset_0);
							data_0 = k_0 * (b.getBridgeValue(0) + offset_0) - TARE_0;

							POSSIBLE_TARE_1 = k_1 * (b.getBridgeValue(1) + offset_1);
							data_1 = k_1 * (b.getBridgeValue(1) + offset_1) - TARE_1;

							int i_data_0 = (int)(data_0 * 10);
							int i_data_1 = (int)(data_1 * 10);
							data_0 = i_data_0 / 10.0;
							data_1 = i_data_1 / 10.0;
							
							String rfid0 = rfids.get(config.getRfidSerials()[1]).getDeviceLabel();
							String rfid1 = rfids.get(config.getRfidSerials()[0]).getDeviceLabel();

							System.out.print("                                       \r");
							System.out.print(rfid0 + ": " + data_0 + "\t" + rfid1 + ":" + data_1 + "\r");
							//Thread.sleep(500);
						} catch (PhidgetException e) {
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
				tempR.addDetachListener(new DetachListener() {

					@Override
					public void detached(DetachEvent arg0) {
						RFIDPhidget r = (RFIDPhidget)arg0.getSource();
						try {
							System.out.println("Detached: " + r.getDeviceLabel());
						} catch (PhidgetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				tempR.addAttachListener(new AttachListener() {
					@Override
					public void attached(AttachEvent ae) {
						try {
							RFIDPhidget r = (RFIDPhidget)ae.getSource();
							System.out.println("RFID attached: " 
									+ ae.getSource().getDeviceLabel() + ", S/N: "
									+ ae.getSource().getSerialNumber());
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
		Scanner s = new Scanner(System.in);
		while(true) {
			char option = s.next().charAt(0);
			switch(option)
			{
			case 'q':
				System.exit(0);
				break;
			case '0':
				TARE_0 = POSSIBLE_TARE_0;
				break;
			case '1':
				TARE_1 = POSSIBLE_TARE_1;
				break;
			default:
				System.out.println("Invalid command.");
				break;
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
				double last_data = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset) - (myLoadCell == 0 ? TARE_0 : TARE_1);
				int i_last_data = (int)(last_data * 10);
				last_data = i_last_data / 10.0;
				while(myRfid.getTagStatus())
				{
					double data;
					if(myLoadCell == 0)
					{
						POSSIBLE_TARE_0 = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset);
						data = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset) - TARE_0;
					}
					else
					{
						POSSIBLE_TARE_1 = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset);
						data = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset) - TARE_1;
					}
					int i_data = (int)(data * 10);
					data = i_data / 10.0;
					//if(data != last_data)
					//{
					//System.out.print("                                       \r");
					//System.out.print(myRfid.getDeviceLabel() + ": " + data + "\r");
					//}
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
