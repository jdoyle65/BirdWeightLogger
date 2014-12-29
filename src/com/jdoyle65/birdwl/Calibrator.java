package com.jdoyle65.birdwl;

import com.phidgets.BridgePhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;

public class Calibrator {
	private static int sn;
	private static int index;
	private static BridgePhidget bridge;
	
	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.print("Incorrect arguments. Example usage: 'java -jar Calibrator.jar x y'\n"
					+ "where x is Phidget Bridge serial number and y is the index number of the load cell");
			System.exit(-1);
		}
		sn = Integer.parseInt(args[0]);
		index = Integer.parseInt(args[1]);
		
		System.out.println("Searching for Bridge " + sn);
		try {
			bridge = new BridgePhidget();
			bridge.open(sn);
			
			bridge.addAttachListener(new AttachListener() {
				@Override
				public void attached(AttachEvent ae) {
					try {
						System.out.println("Bridge attached: " 
								+ ae.getSource().getDeviceLabel() + ", S/N: "
								+ ae.getSource().getSerialNumber());
						BridgePhidget b = (BridgePhidget)ae.getSource();
						b.setDataRate(500);
						b.setGain(index, BridgePhidget.PHIDGET_BRIDGE_GAIN_32);
						b.setEnabled(index, true);
						Thread.sleep(1000);
						
					}
					catch (PhidgetException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			
			
			
			while (true) {
				if(bridge.isAttached() && bridge.getEnabled(index)) {
					double value = bridge.getBridgeValue(index);
					System.out.println(value);
					Thread.sleep(500);
				}
			}
			
			
		}
		catch (Exception e) {
			
		}
	}

}
