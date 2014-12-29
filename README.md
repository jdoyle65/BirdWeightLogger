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

We decided to use the Raspberry Pi Model B because of its cheap price, low power consumption, and low heat output. The BWL software runs on the Java Virtual Machine though, so any computer with Oracle's Java 6 or later installed may be used. Note: this software has not been tested on any other implementations of Java (e.g. Sun, OpenJDK).

## Installation

1. You will need Java 6 or later installed on the computer you wish to run BWL on. Java can be downloaded from [http://java.com/en/download/](http://java.com/en/download/).
2. You will need to install the Phidget USB Drivers. Links to the drivers and instructions on how to install them can be found here: [http://www.phidgets.com/docs/Operating_System_Support](http://www.phidgets.com/docs/Operating_System_Support).
3. 