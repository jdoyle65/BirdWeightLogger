package com.jdoyle65.birdwl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.*;

public class DataLogger {
	private FileWriter file;
	private CSVWriter writer;
	private String deviceId;
	private ArrayList<String> dates;
	private ArrayList<String> tagIds;
	private ArrayList<Double> weightData;

	public DataLogger(String fileName, String devId) throws IOException {
		deviceId = devId;
		file = new FileWriter(fileName);
		writer = new CSVWriter(file);
		dates = new ArrayList<>(100);
		tagIds = new ArrayList<>(100);
		weightData = new ArrayList<>(100);
	}

	public DataLogger(String fileName, int devId) throws IOException {
		deviceId = Integer.toString(devId);
		file = new FileWriter(fileName);
		writer = new CSVWriter(file);
	}

	public void logRow(String date, String tagId, double weight) {
		dates.add(date);
		tagIds.add(tagId);
		weightData.add(weight);
	}
	
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
	
	public void close() throws IOException {
		writer.close();
	}


}
