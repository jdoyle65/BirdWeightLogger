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

import java.util.Scanner;

import com.phidgets.BridgePhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.RFIDPhidget;

public class DeviceNamer {
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		while(true)
		{
			System.out.println("Phidget type:\n\t1. Bridge\n\t2. RFID");
			int type = s.nextInt();
			Phidget phidget;

			try {
				if(type == 1)
					phidget = new BridgePhidget();
				else
					phidget = new RFIDPhidget();

				System.out.print("Enter serial: ");
				int serial = s.nextInt();
				phidget.open(serial);
				System.out.println("Waiting...");
				phidget.waitForAttachment(1000*5);
				System.out.println("Phidget attached. Current name is " + phidget.getDeviceLabel());
				String name;
				while(true) {
					System.out.print("Enter name: ");
					name = s.next();
					if(name.length() <= 10)
						break;
					System.out.println("Name too long! Must be 10 or less chars.");
				}
				System.out.println("Setting name...");
				phidget.setDeviceLabel(name);
				System.out.println("Success if same: " + name + "... " + phidget.getDeviceLabel());
				phidget.close();

			} catch (PhidgetException e) {
				e.printStackTrace();
			}
		}
	}
}
