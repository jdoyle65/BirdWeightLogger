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
import com.phidgets.event.TagLossEvent;
import com.phidgets.event.TagLossListener;

public class BirdWeightLogger {
	private static ConfigParser config;
	private HashMap<Integer, BridgePhidget> bridges;
	private HashMap<Integer, RFIDPhidget> rfids;

	/*** PUBLIC FINALS ***/
	public static final int WAIT_FOR_ATT = 1000*10;

	public BirdWeightLogger(String[] args) throws FileNotFoundException, IOException {
		config = new ConfigParser("config.cfg");
		initOptions();
	}


	/**** PRIVATE METHODS ****/
	private void initOptions() {
		bridges = new HashMap<Integer, BridgePhidget>(config.getNumBridges());
		rfids = new HashMap<Integer, RFIDPhidget>(config.getNumRfidReaders());

		// Init all bridges in configuration file and open them.
		for(int i = 0; i < config.getNumBridges(); i++) {
			try {
				BridgePhidget tempB = new BridgePhidget();
				tempB.open(config.getBridgeSerial(i));
				tempB.waitForAttachment(WAIT_FOR_ATT);
				bridges.put(tempB.getSerialNumber(), tempB);
			} catch (PhidgetException e) {
				e.printStackTrace();
			}
		}
		// Init all RFID reader in configuration file and open them.
		for(int i = 0; i < config.getNumRfidReaders(); i++) {
			try {
				RFIDPhidget tempR = new RFIDPhidget();
				tempR.open(config.getRfidSerial(i));
				tempR.waitForAttachment(WAIT_FOR_ATT);
				tempR.setAntennaOn(true);
				tempR.setLEDOn(true);
				tempR.addTagGainListener(new RfidTagGainerListener());
				rfids.put(tempR.getSerialNumber(), tempR);
			} catch (PhidgetException e) {
				e.printStackTrace();
			}
		}
	}


	/*** PRIVATE LISTENER CLASSES ***/
	private class RfidTagGainerListener implements TagGainListener {
		@Override
		public void tagGained(TagGainEvent tge) {
			RFIDPhidget rfid = (RFIDPhidget)tge.getSource();


			try {
				flashLed(rfid);
				DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
				Date date = Calendar.getInstance().getTime();
				String dateString = df.format(date);
				final DataLogger dl = new DataLogger(df.format(date), rfid.getDeviceLabel());

				TagLossListener tgl = new TagLossListener() {
					@Override
					public void tagLost(TagLossEvent arg0) {
						dl.writeFile();
						try {
							dl.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				rfid.addTagLossListener(tgl);

				int mySerial = rfid.getSerialNumber();
				int myBridgeIndex = config.getRfidBridge(mySerial);
				BridgePhidget myBridge = bridges.get(config.getBridgeSerial(myBridgeIndex));
				int myLoadCell = config.getRfidLoadCell(mySerial);
				String myTag = tge.getValue();

				for(int i = 0; i < 100; i++) // Using a for loop only for testing purposes
				{
					double data = myBridge.getBridgeValue(myLoadCell);
					dl.logRow(dateString, myTag, data);
					wait(8*8);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (PhidgetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void flashLed(RFIDPhidget p) throws PhidgetException {
			try {
				p.setLEDOn(false);
				wait(10);
				p.setLEDOn(true);
				wait(10);
				p.setLEDOn(false);
				wait(10);
				p.setLEDOn(true);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
