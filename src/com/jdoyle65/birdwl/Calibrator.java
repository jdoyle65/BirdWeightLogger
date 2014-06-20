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
						//b.setGain(0, BridgePhidget.PHIDGET_BRIDGE_GAIN_8);
						//b.setEnabled(0, true);
						b.setGain(index, BridgePhidget.PHIDGET_BRIDGE_GAIN_32);
						b.setEnabled(index, true);
						Thread.sleep(1000);
						
					}
					catch (PhidgetException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			
			
			while (true) {
				if(bridge.isAttached() && bridge.getEnabled(index)) {
					//System.out.print("                                       \r");
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
