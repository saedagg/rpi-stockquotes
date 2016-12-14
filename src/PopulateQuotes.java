/*
 * PopulateQuotes.java
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

import java.util.Date;
import java.util.List;
import java.util.Calendar;

import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;

public class PopulateQuotes {
	
	// ##### Main program
	public static void main (String args[]) throws IOException, JSONException {

		// Get the current date and set the date started
		Date dateStarted = Calendar.getInstance().getTime();

		// Initialise the processed rows, inserted rows and updated rows
		int rowsProcessed = 0;
		int rowsInserted = 0;
		int rowsUpdated = 0;

		// Call the method to get the list of grouped symbols
		List<List<String>> symbolsGroupsList = StockQuotesDBOperations.GetSymbolsGroups();
		
		// Loop through each group of symbols
		for (List<String> symbolsGroup : symbolsGroupsList) {

			// Initialise the JSON object or array quote
			JSONObject jsonObjectQuote = null;
			JSONArray jsonArrayQuote = null;
			int numberOfQuotes = 0;
			
			// Call the method to retrieve the JSON response from the Yahoo API
			JSONObject jsonObjectResponse = GetStockQuotes.GetJSONObject(symbolsGroup);

			if (jsonObjectResponse != null) {
				// Get the JSON object query
				JSONObject jsonObjectQuery = jsonObjectResponse.optJSONObject("query");
				
				if (jsonObjectQuery != null) {
					// Get the JSON object results
					JSONObject jsonObjectResults = jsonObjectQuery.optJSONObject("results");
					
					if (jsonObjectResults != null) {
						// Get the JSON object quote as Object
						Object objectQuote = jsonObjectResults.get("quote");

						// Check if there is a single quote or an array of quotes
						if (objectQuote instanceof JSONArray) {
							jsonArrayQuote = (JSONArray) objectQuote;
							numberOfQuotes = jsonArrayQuote.length();
						}
						else {
							numberOfQuotes = 1;
							jsonObjectQuote = (JSONObject) objectQuote;
						}
					}
				}
				
				// Loop through the contents of the JSON response
				for (int i = 0; i < numberOfQuotes; i++) {
					// Get the JSON object from the JSON quote array
					if (numberOfQuotes > 1) {
						jsonObjectQuote = jsonArrayQuote.getJSONObject(i);
					}

					// Increment the processed rows counter
					rowsProcessed++;

					// Initialise the objects for the quote properties
					String symbol = "";
					String currency = "";
					String volumeString = "";
					String tradeDateString = "";
					String priceString = "";
					String previousCloseString = "";
					String marketCapitalizationString = "";
					String marketCapitalizationFactor = "";
					double marketCapitalization = 0.0;
					long sharesIssued = 0;
					Date tradeDate = null;
					double previousClose = 0.0;
					int tradeDateChange = 0;
					double price = 0.0;
					double priceChange = 0.0;
					double priceChangePercent = 0.0;
					double dayPriceChange = 0.0;
					double dayPriceChangePercent = 0.0;
					long volume = 0;
					double volumePercent = 0.0;

					// Check if the JSON object result values are not empty
					if (!jsonObjectQuote.isNull("Symbol") && !jsonObjectQuote.isNull("LastTradePriceOnly") && !jsonObjectQuote.isNull("LastTradeDate") && !jsonObjectQuote.isNull("LastTradeTime") && !jsonObjectQuote.isNull("PreviousClose") && !jsonObjectQuote.isNull("Volume")) {

						// Retrieve the result values from the JSON object
						symbol = jsonObjectQuote.getString("Symbol");
						priceString = jsonObjectQuote.getString("LastTradePriceOnly");
						tradeDateString = jsonObjectQuote.getString("LastTradeDate") + " " + jsonObjectQuote.getString("LastTradeTime");
						previousCloseString = jsonObjectQuote.getString("PreviousClose");
						volumeString = jsonObjectQuote.getString("Volume");

						// Retrieve the result value for Currency from the JSON object
						if (!jsonObjectQuote.isNull("Currency")) {
							currency = jsonObjectQuote.getString("Currency");
						}

						// Retrieve the result value for Market Capitalization from the JSON object
						if (!jsonObjectQuote.isNull("MarketCapitalization")) {
							marketCapitalizationString = jsonObjectQuote.getString("MarketCapitalization");

							// Retrieve and remove the last character ('M' = Million, or 'B' = Billion)
							marketCapitalizationFactor = marketCapitalizationString.substring(marketCapitalizationString.length() - 1);
							marketCapitalizationString = marketCapitalizationString.substring(0, marketCapitalizationString.length() - 1);
						
							// Convert the market capitalization string to a Double
							try {
								marketCapitalization = Double.parseDouble(marketCapitalizationString);
							
								// Multiply the market capitalization by the factor
								if (marketCapitalizationFactor.equals("M")) {
									marketCapitalization = marketCapitalization * 1000000;
								}
								else if (marketCapitalizationFactor.equals("B")) {
									marketCapitalization = marketCapitalization * 1000000000;
								}
							}
							catch (NumberFormatException ex) {
								System.out.println("ERROR occurred for stock....." + symbol + "....." + ex);
							}
						}

						// Convert the trade date string to a Date
						try {
							tradeDate = Constants.YAHOO_SIMPLE_DATE_FORMAT.parse(tradeDateString);
						}
						catch (ParseException ex) {
							System.out.println("ERROR occurred for stock....." + symbol + "....." + ex);
						}

						// Convert the price string to a Double
						try {
							price = Double.parseDouble(priceString);
						}
						catch (NumberFormatException ex) {
							System.out.println("ERROR occurred for stock....." + symbol + "....." + ex);
						}
			
						// Convert the previous close string to a Double
						try {
							previousClose = Double.parseDouble(previousCloseString);
						}
						catch (NumberFormatException ex) {
							System.out.println("ERROR occurred for stock....." + symbol + "....." + ex);
						}

						// Convert the volume string to an Int
						try {
							volume = Integer.parseInt(volumeString);
						}
						catch (NumberFormatException ex) {
							System.out.println("ERROR occurred for stock....." + symbol + "....." + ex);
						}

						// Call the method to retrieve the stock object
						Stock stock = StockQuotesDBOperations.GetStock(-1, symbol);

						// Get the time difference between the current date and the trade date
						long timeDifferenceInMilliseconds = dateStarted.getTime() - tradeDate.getTime();
					
						// Check the time difference in not too large
						if (timeDifferenceInMilliseconds < Constants.DATE_TRADE_LIMIT_IN_MILLISECONDS) {
						
							// Check that the stock object exists
							if (stock.id != -1) {
							
								// Check if the currency has changed and if so then update the STOCK record
								if (!stock.currency.equals(currency)) {
									
									// Set the currency property for the stock object
									stock.currency = currency;
									
									// Call the method to update the STOCK record
									StockQuotesDBOperations.UpdateStock(stock);
						
									// Output the result
									System.out.println("Updated currency....." + symbol + "," + currency);
								}
								
								// Check that the currency is in GBP or GBp, otherwise deactivate stock
								if (!stock.currency.equals("GBP") && !stock.currency.equals("GBp")) {
									
									// Set the active property for the stock object
									stock.active = 'N';

									// Call the method to update the STOCK record
									StockQuotesDBOperations.UpdateStock(stock);

									// Increment the updated rows counter
									rowsUpdated++;
						
									// Output the result
									System.out.println("Deactivated stock....." + symbol + "," + stock.currency);
								}

								// Convert the market capitalization to pence if currency is 'GBp'
								if (currency.equals("GBp")) {
									marketCapitalization = marketCapitalization * 100;
								}
							
								// Calculate the shares issued
								sharesIssued = (long) (marketCapitalization / price);

								// Check if the shares issued has changed and if so then update the STOCK record
								if (stock.sharesIssued != sharesIssued) {
									
									// Set the shares issued property for the stock object
									stock.sharesIssued = sharesIssued;
								
									// Call the method to update the STOCK record
									StockQuotesDBOperations.UpdateStock(stock);

									// Increment the updated rows counter
									rowsUpdated++;
						
									// Output the result
									System.out.println("Updated shares issued....." + symbol + "," + sharesIssued);
								}
							
								// Call the method to retrieve the quote object for the trade date
								Quote quote = StockQuotesDBOperations.GetQuote(stock.id, "TRADE_DATE", tradeDate);

								// Check that the quote record DOES NOT already exist for the trade date
								if (quote.stockId == -1) {
								
									// Call the method to retrieve the latest quote object for the stock
									Quote latestQuote = StockQuotesDBOperations.GetQuote(stock.id, "LATEST", null);
								
									// Set the trade date difference and price change properties for the quote object
									if (latestQuote.stockId != -1) {
										long timeDifferenceInMinutes = (tradeDate.getTime() - latestQuote.dateTrade.getTime()) / 60000;
										tradeDateChange = (int) timeDifferenceInMinutes;

										priceChange = price - latestQuote.price;
										priceChangePercent = (priceChange / price) * 100;
									}
								
									// Set the day price change properties for the quote object
									dayPriceChange = price - previousClose;
									dayPriceChangePercent = (dayPriceChange / previousClose) * 100;

									// Calculate the volume percent property for the quote object
									if (sharesIssued > 0) {
										volumePercent = ((double) volume / sharesIssued) * 100;
									}
									
									// Check to ensure the volume percent is not greater than 100
									if (volumePercent > 100) {
										volumePercent = 0.0;
									}
								
									// Instantiate the new quote object and set the properties
									Quote newQuote = new Quote();
									newQuote.stockId = stock.id;
									newQuote.dateTrade = tradeDate;
									newQuote.dateTradeChange = tradeDateChange;
									newQuote.price = price;
									newQuote.priceChange = priceChange;
									newQuote.priceChangePercent = priceChangePercent;
									newQuote.dayPriceChange = dayPriceChange;
									newQuote.dayPriceChangePercent = dayPriceChangePercent;
									newQuote.volume = volume;
									newQuote.volumePercent = volumePercent;
				
									// Call the method to insert the quote record
									StockQuotesDBOperations.InsertQuote(newQuote);
								
									// Increment the inserted rows counter
									rowsInserted++;

									// Output the result
									System.out.println("Inserted quote....." + symbol + "," + tradeDate + "," + price);
								}
							}
						}
					}
				}
			}
		}
		
		// Call the function to update the job record
		Functions.FinishJob("Populate Quotes", dateStarted, rowsProcessed, rowsInserted, rowsUpdated);
	}
}
