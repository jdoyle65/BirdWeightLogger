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

public class BirdWeightLogger {
	private static ConfigParser config;
	private HashMap<Integer, BridgePhidget> bridges;
	private HashMap<Integer, RFIDPhidget> rfids;

	/*** PUBLIC FINALS ***/
	public static final int WAIT_FOR_ATT = 1000*10;
	public final int DATA_RATE;

	public BirdWeightLogger(String[] args) throws FileNotFoundException, IOException {
		config = new ConfigParser("config.cfg");
		DATA_RATE = config.getDataRate();
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
				tempB.addAttachListener(new AttachListener() {
					@Override
					public void attached(AttachEvent ae) {
						System.out.println("Bridge attached");
						BridgePhidget b = (BridgePhidget)ae.getSource();
						try {
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
						RFIDPhidget r = (RFIDPhidget)ae.getSource();
						try {
							System.out.println("RFID attached!");
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

		while(true) {
			// TODO This loop is just for dev purposes
		}
	}


	/*** PRIVATE LISTENER CLASSES ***/
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
					DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
					Date date = Calendar.getInstance().getTime();
					String dateString = df.format(date);
					DataLogger dl = new DataLogger("log/" + df.format(date) + ".csv", myRfid.getDeviceLabel());


					int mySerial = myRfid.getSerialNumber();
					int myBridgeIndex = config.getRfidBridge(mySerial);
					BridgePhidget myBridge = bridges.get(config.getBridgeSerial(myBridgeIndex));
					int myLoadCell = config.getRfidLoadCell(mySerial);
					double myOffset = config.getLoadCellOffset(myBridgeIndex, myLoadCell);
					double myK = config.getLoadCellKValue(myBridgeIndex, myLoadCell);

					double last_data = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset);
					while(myRfid.getTagStatus())
					{
						double data = myK * (myBridge.getBridgeValue(myLoadCell) + myOffset);
						if(data != last_data)
							System.out.print("Data: " + data + "\r");
						last_data = data;
						dateString = df.format(Calendar.getInstance().getTime());
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
