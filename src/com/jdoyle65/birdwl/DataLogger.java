package com.jdoyle65.birdwl;

import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.*;

public class DataLogger {
	private FileWriter file;
	private CSVWriter writer;
	
	DataLogger(String fileName) throws IOException {
		file = new FileWriter(fileName);
		writer = new CSVWriter(file);
	}
}
