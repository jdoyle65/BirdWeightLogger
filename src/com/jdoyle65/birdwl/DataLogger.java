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
* Many thanks to OpenCSV for providing the libraries used in this class for
* writing out data into CSV format. (OpenCSV is also licensed under Apache
* License, Version 2.0).
* 
* You can find OpenCSV's webpage here: http://opencsv.sourceforge.net/
*/

package com.jdoyle65.birdwl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.*;

/**
 * DataLogger is a class that takes care of logging all data input to CSV files.
 */
public class DataLogger {
	private FileWriter file;
	private CSVWriter writer;
	private String deviceId;
	private ArrayList<String> dates;
	private ArrayList<String> tagIds;
	private ArrayList<Double> weightData;

	/**
	 * @param fileName Name of csv file to create.
	 * @param devId ID number of device logging data.
	 * @throws IOException
	 */
	public DataLogger(String fileName, String devId) throws IOException {
		deviceId = devId;
		file = new FileWriter(fileName);
		writer = new CSVWriter(file);
		dates = new ArrayList<>(100);
		tagIds = new ArrayList<>(100);
		weightData = new ArrayList<>(100);
	}

	/**
	 * @param fileName Name of csv file to create.
	 * @param devId ID number of device logging data.
	 * @throws IOException
	 */
	public DataLogger(String fileName, int devId) throws IOException {
		deviceId = Integer.toString(devId);
		file = new FileWriter(fileName);
		writer = new CSVWriter(file);
		dates = new ArrayList<>(100);
		tagIds = new ArrayList<>(100);
		weightData = new ArrayList<>(100);
	}

	/**
	 * Log a row in the data entry table. 
	 * @param date Current date.
	 * @param tagId Alpha-numeric tag number.
	 * @param weight Weight data captured.
	 */
	public void logRow(String date, String tagId, double weight) {
		dates.add(date);
		tagIds.add(tagId);
		weightData.add(weight);
	}

	/**
	 * Write out rows to file. 
	 */
	public void writeFile() {
		for(int i = 0; i < dates.size(); i++)
		{
			String[] row = new String[3];
			row[0] = dates.get(i);
			row[1] = tagIds.get(i);
			row[2] = Double.toString(weightData.get(i));
			writer.writeNext(row);
		}
	}

	/**
	 * Close file.
	 * @throws IOException
	 */
	public void close() throws IOException {
		writer.close();
	}


}
