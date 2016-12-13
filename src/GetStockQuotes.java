/*
 * GetStockQuotes.java
 * 
 * Copyright 2016  <pi@raspberrypi>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.json.JSONException;

public class GetStockQuotes {
	
	// ##### Queries the Yahoo YQL API and returns a JSON object response
	public static JSONObject GetJSONObject (List<String> symbols) throws IOException, JSONException {

		// Initialise the HTTP connection
		HttpsURLConnection urlConnection = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		boolean responseOK = false;
		int httpConnectionRetryAttempt = 1;

		// Initialise the JSON response string and object
		String jsonResponse = "";
		JSONObject jsonObjectResponse = null;

		// Initialise the symbols list string
		String symbolsList = "";

		// Convert the symbols list into the symbols string to add to the WHERE clause
		int i = 0;
		for (String symbol : symbols) {
			i += 1;
			if (i > 1) {
				symbolsList += ",";
			}
			symbolsList += "'" + symbol + "'";
		}

		// Initialise the columns string
		String columns = "Symbol,Currency,LastTradeDate,LastTradeTime,LastTradePriceOnly,PreviousClose,Volume,MarketCapitalization";
		
		// Set and encode the URL string
		String yqlBaseUrl = "https://query.yahooapis.com/v1/public/yql?q=";
		String yqlQuery = "SELECT " + columns + " FROM yahoo.finance.quotes WHERE symbol IN (" + symbolsList + ")";
		String yqlSuffix = "&format=json&env=store://datatables.org/alltableswithkeys";
		String yqlFullUrlStr = yqlBaseUrl + URLEncoder.encode(yqlQuery, "UTF-8") + yqlSuffix;

		// Create the URL
		URL yahooUrl = new URL(yqlFullUrlStr);

		while (!responseOK && httpConnectionRetryAttempt <= Constants.HTTP_CONNECTION_RETRY_ATTEMPTS) {
			try {
				// Create the URL and open a connection
				urlConnection = (HttpsURLConnection) yahooUrl.openConnection();

				// Set the HTTP request type method to GET (Default: GET)
				urlConnection.setRequestMethod("GET");
				urlConnection.setRequestProperty("Accept", "application/text");

				if (urlConnection.getResponseCode() == 200) {
					responseOK = true;
				}
				else {
					httpConnectionRetryAttempt += 1;
					System.out.println("ERROR occurred with HTTP connection....." + urlConnection.getResponseCode() + ":" + urlConnection.getResponseMessage());
					System.out.println("Retrying connection.....Attempt " + httpConnectionRetryAttempt + " of " + Constants.HTTP_CONNECTION_RETRY_ATTEMPTS);
				}
			}
			catch (Exception ex) {
				System.out.println("ERROR occurred with HTTP connection....." + urlConnection.getResponseCode() + ":" + urlConnection.getResponseMessage());
			}
		}
		
		try {
			// Create a BufferedReader to read the contents of the request
			inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);

			// Read in each line of the request contents and add to the JSON response string
			String inputLine = "";
			while ((inputLine = bufferedReader.readLine()) != null) {
				jsonResponse += inputLine + "\n";
			}
			
			// Convert the JSON string to a JSON object
			jsonObjectResponse = new JSONObject(jsonResponse);
		}
		catch (Exception ex) {
			System.out.println("ERROR occurred....." + ex);
		}
		finally {
			// Close the connections
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			
			// Return the JSON object
			return jsonObjectResponse;
		}
	}
}
