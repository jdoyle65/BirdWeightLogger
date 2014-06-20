package com.jdoyle65.birdwl.tools;

import com.phidgets.PhidgetException;
import com.phidgets.RFIDPhidget;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.TagGainEvent;
import com.phidgets.event.TagGainListener;

public class RfidTagReader {
	public static void main(String[] args) {
		try {
			RFIDPhidget rfid = new RFIDPhidget();
			
			rfid.addAttachListener(new AttachListener() {
				@Override
				public void attached(AttachEvent ae) {
					System.out.println("RFID reader attached.");
					RFIDPhidget r = (RFIDPhidget)ae.getSource();
					try {
						r.setAntennaOn(true);
						r.setLEDOn(true);
					} catch (PhidgetException e) {
						e.printStackTrace();
					}
				}
			});
			
			rfid.addTagGainListener(new TagGainListener() {
				@Override
				public void tagGained(TagGainEvent tge) {
					System.out.println("Tag: " + tge.getValue());
				}
			});
			
			rfid.open(335827);
			rfid.waitForAttachment(1000*3);
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
		
	}

}
