package com.example.datacollector;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataCollector {
	private static final int REQUEST_INTERVAL = 20000;

	private static Map<String, BusData> buses = new HashMap<>();


	public static void main(String[] args) {
		String response = JunarHandler.makeRequest();
		List<BusData> realTimeBuses = stringToBus(response);

		for (BusData bus : realTimeBuses)   // Load first values
			buses.put(bus.idNumber, bus);

		for (int i = 0; i < 3; i++) {   // For testing purposes
			try {
				Thread.sleep(REQUEST_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			response = JunarHandler.makeRequest();
			realTimeBuses = stringToBus(response);

			for (BusData bus : realTimeBuses) {
				BusData storedBus = buses.get(bus.idNumber);

				if (storedBus == null)
					buses.put(bus.idNumber, bus);
				else if (storedBus.lastUpdate != bus.lastUpdate) {
					String dataPoint = storedBus.toString() + ", "                              // Bus data
							+ distance(storedBus.lat, storedBus.lng, bus.lat, bus.lng) + ", "   // Distance traveled
							+ (bus.time.getTime() - storedBus.time.getTime()) / 1000 + "\n";    // Seconds since last update

					// TODO: store dataPoint in file

					storedBus.update(bus);
				}
			}


		}

	}

	// TODO: distance
	public static double distance(double lat1, double lng1, double lat2, double lng2) {
		return 0;
	}


	// Converts the text to objects
	// EL VILLARINO, 101, 319, 2018-04-17 12:48:02, -38.718005, -62.266682, 132, 18
	public static List<BusData> stringToBus(String data) {
		List<BusData> toReturn = new LinkedList<>();
		String[] rows = data.split("\n");

		for (String row : rows) {
			String[] values = row.split(",");
			double lat = Double.parseDouble(values[4]);
			double lng = Double.parseDouble(values[5]);
			int angle = Integer.parseInt(values[6]);
			int vel = Integer.parseInt(values[7]);
			toReturn.add(new BusData(values[1], values[2], lat, lng, angle, vel, values[3].hashCode()));
		}

		return toReturn;
	}


	private static class BusData {
		private String idNumber;
		private String line;
		private double lat;
		private double lng;
		private Date time;
		private int vel;
		private int angle;
		private int lastUpdate;

		public BusData(String idNumber, String line, double lat, double lng, int angle, int vel, int lastUpdate) {
			this.idNumber = idNumber;
			this.line = line;
			this.lat = lat;
			this.lng = lng;
			this.vel = vel;
			this.angle = angle;
			this.lastUpdate = lastUpdate;
			time = new Date();
		}

		//TODO: updateBus
		public void update(BusData updatedBus) {

		}

	}

}
