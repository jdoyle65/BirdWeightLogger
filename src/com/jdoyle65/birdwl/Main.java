package com.jdoyle65.birdwl;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		try {
			BirdWeightLogger bwl = new BirdWeightLogger(args);
		} catch (FileNotFoundException e) {
			System.out.println("Configuation file not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
