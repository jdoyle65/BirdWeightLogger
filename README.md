Bird Weight Logger
================

## Introduction


Bird Weight Logger (BWL) is a project that started as an idea between Ben Walters and Justin Doyle at the University of Western Ontario to create a device to use for hands-free, automated weighing of small birds that was much more cost effective than currently available solutions.

This project uses the following hardware:

* Phidget 1024 RFID Reader-Writer. [Phidgets.com](http://www.phidgets.com/products.php?category=14&product_id=1024_0)
* Phidget 1046 Bridge 4-Input. [Phidgets.com](http://www.phidgets.com/products.php?category=34&product_id=1046_0)
* Phidget 3132 Micro Load Cell (0g - 780g). [Phidgets.com](http://www.phidgets.com/products.php?category=34&product_id=3132_0)
* Raspberry Pi - Model B (Running on Raspbian 7.1) [Element14](http://canada.newark.com/raspberry-pi/raspberry-pi-b-starter-kit/silicon-manufacturer-broadcom/dp/84X9502)
* USB Hub with Dedicated Power Supply

We decided to use Phidget's 0g - 780g load cell because its specifications most closely matched the requirements needed for the average weight of the birds we were working with, but any load cell can be used (please check that the load cell you choose is compatible with Phidget's 4-Input Bridge).

We decided to use the Raspberry Pi Model B because of its cheap price, low power consumption, and low heat output. The BWL software runs on the Java Virtual Machine though, so any computer with Oracle's Java 7 or later installed may be used. Note: this software has not been tested on any other implementations of Java (e.g. Sun, OpenJDK).

## Installation

1. You will need Java 7 or later installed on the computer you wish to run BWL on. Java can be downloaded from [http://java.com/en/download/](http://java.com/en/download/).
2. You will need to install the Phidget USB Drivers. Links to the drivers and instructions on how to install them can be found here: [http://www.phidgets.com/docs/Operating_System_Support](http://www.phidgets.com/docs/Operating_System_Support).
3. Download the Bird Weight Logger project as a zip file here: [https://github.com/jdoyle65/BirdWeightLogger/archive/master.zip](https://github.com/jdoyle65/BirdWeightLogger/archive/master.zip).
4. Unzip the file downloaded in step 3. There should be a file titled BWL.jar and another called config.cfg. Open config.cfg with a plain text editor (such as Notepad). There's an example configuration already set up, but you will need to change the configuration for your devices.
  * bridges - Number of bridges you are attaching to this device.
  * rfid_readers - Number of RFID Reader/Writers you are attaching to this device.
  * data_rate - Number of milliseconds between each data sample taken from the load cells. A lower number gives a faster rate but may begin introducing noise into the signalling and affecting your data. We found the a data rate of 400-500 worked best on our system, but your results may vary.
  * bridge_0 - The serial number on the bottom of your Bridge. If using more than one bridge, add another bridge_x line with the corresponding serial number (example bridge_1=123456)
  * rfid_0 - The serial number on the bottom of your RFID Reader. If using more than one reader, add another rfid_x line with the corresponding serial number (example rfid_1=123456)
  * rfid_0_pair - Denotes which bridge and load cell rfid_0 is being paired with. In the example file you are given *rfid_0=0-0* where the first 0 in 0-0 stands for which bridge to pair with, and the second 0 stands for which load cell on the bridge to pair with.
  * offset_0_0 - The offset value to give to load cell 0-0 (Offset values explained below in Calibration section). The first 0 is the bridge index, the second 0 is load cell index on that bridge.
  * k_0_0 - The K value to give to load cell 0-0 (K values explained below in Calibration section). The first 0 is the bridge index, the second 0 is load cell index on that bridge.
5. Using your preferred command line tool, navigate to the directory BWL.jar and config.cfg are located in. Run the command "java -jar BWL.jar". You will most likely have to run the jar as Administrator/root, as the Phidget USB drivers require it to work properly.
6. Once the program is running, you can enter '0' to zero load cell 0, or '1' to zero load cell 1. You can also enter 'q' to quit the program at any time. As of this time we only included support for two load cells per bridge.


## Calibration

Calibrating your load cells can be done with the help of Calibrator.jar that also comes packaged with the zip folder you downloaded in step 4 of the Installation section. To run Calibrator.jar, open up your preferred command line tool, navigate to the folder containing Calibrator.jar, and run "java -jar Calibrator.jar *x* *y*" where x is the serial number of the Phidget Bridge you wish to connect, and y is the index number of the load cell you wish to calibrate.

Calibrator.jar will give you a readout of the raw data streaming in from the load cell in mV/V. I suggest reading Phidget's [Load Cell Primer](http://www.phidgets.com/docs/Load_Cell_Primer) to get you started, but the important part for calibration is:

> You can use this simple formula to convert the measured mv/V output from the load cell to the measured force:
>
> Expected Force or Weight = K * (Measured mV/V - Offset)
>
> Where K is gain value that will change depending on what unit of force or weight you want to measure. Since the offset varies between individual load cells, it’s necessary to measure it for each sensor. Record the output of the load cell at rest on a flat surface with no force on it. The mv/V output measured by the PhidgetBridge is the offset.
>
> Once you’ve found the offset, measure something with a known weight and solve the equation for K. You can also calibrate the load cell at multiple known weights and use these points to model a linear function.

I recommend using several known weights within the weight range of the animals you expect to be weighing in order to get an accurate as possible K value. Every load cell is slightly different, and so each one needs to be calibrated and its calibration values entered in the config.cfg file.

## Running the Program

Since we ran our software on the Raspberry Pi Model B with Raspbian installed, we were able to use a relatively simple setup. Our Raspberry Pis were connected to a network so we could simply ssh into each one. Once logged in we used the Linux tool [screen](http://linux.die.net/man/1/screen) to keep the session running once we were ready to logout and disconnect from the Pi, which allowed us to resume that session the next time we logged into the Pi. If running the software on another operating system, you may have to explore options that work best for you.