package com.jdoyle65.birdwl;

import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.*;

public class DataLogger {
	private FileWriter file;
	private CSVWriter writer;
	private String deviceId;

	public DataLogger(String fileName, String devId) throws IOException {
		deviceId = devId;
		file = new FileWriter(fileName);
		writer = new CSVWriter(file);
	}

	public DataLogger(String fileName, int devId) throws IOException {
		deviceId = Integer.toString(devId);
		file = new FileWriter(fileName);
		writer = new CSVWriter(file);
	}

	public void logRow(String date, String tagId, String weightData) {
		String[] row = new String[3];
		row[0] = date;
		row[1] = tagId;
		row[2] = weightData;
		writer.writeNext(row);
	}
	
	public void close() throws IOException {
		writer.close();
	}


}
