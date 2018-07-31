package com.example.datacollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class JunarHandler {

	private static final String JUNAR_URL = "http://api.datos.bahiablanca.gob.ar/api/v2/datastreams/";
	private static final String GPS_URL = "GPS-TRANS/data.csv/?auth_key=";
	private static final String API_KEY = "a7c0719da9126768e4833822be1b9abb586b0345";   //Distinata API KEY para recolectar data de GPS fuera de la app
	private static final String LIMIT_URL = "&limit=500";
	private static String FILTER_URL = "&filter0=column2[!=]";
	private static String urlText = JUNAR_URL + GPS_URL + API_KEY + FILTER_URL + LIMIT_URL;

	public static String makeRequest() {
		URL url;
		HttpURLConnection connection;
		StringBuffer response = new StringBuffer();
		BufferedReader in;
		String inputLine;

		try {
			url = new URL(urlText);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			in.readLine();

			while ((inputLine = in.readLine()) != null)
				response.append(inputLine).append('\n');

			connection.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return response.toString();
	}
}