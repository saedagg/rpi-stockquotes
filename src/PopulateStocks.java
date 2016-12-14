/*
 * PopulateStocks.java
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

public class PopulateStocks {
	
	// ##### Main program
	public static void main (String args[]) {
		
		// Get the current date and set the date started
		Date dateStarted = Calendar.getInstance().getTime();

		// Initialise the processed rows, inserted rows and updated rows
		int rowsProcessed = 0;
		int rowsInserted = 0;
		int rowsUpdated = 0;
		
		// Initialise the file path to the stock symbols file
		String symbolsFilePath = "/home/pi/projects/stockquotes/resources/LSE.txt";
		
		// Create a BufferedReader to read the contents of the file
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(symbolsFilePath))) {
			
			// Read in each line of the file contents and add insert into STOCKS table
			String inputLine = "";
			
			while ((inputLine = bufferedReader.readLine()) != null) {
				// Split the TAB separated columns and convert to list
				String[] inputLineArray = inputLine.split("\t");
				List<String> inputLineList = Arrays.asList(inputLineArray);
				
				// Increment the processed rows counter
				rowsProcessed++;

				if (inputLineList.size() == 2) {
					String symbol = inputLineList.get(0);
					String name = inputLineList.get(1);
					
					// Call the method to retrieve the stock object
					Stock stock = StockQuotesDBOperations.GetStock(-1, symbol);
					
					// Check if the stock object exists
					if (stock.id == -1) {
						
						// Set the name and active property of the stock object
						stock.name = name;
						stock.active = 'Y';

						// Call the method to insert the STOCK record
						StockQuotesDBOperations.InsertStock(stock);
						
						// Increment the inserted rows counter
						rowsInserted++;
						
						// Output the result
						System.out.println("Inserted stock....." + stock.symbol);

					}
					else {
						if (stock.active == 'Y') {
							
							// Check if the stock name has changed
							if (!stock.name.equals(name)) {
								
								// Call the method to update the STOCK record
								StockQuotesDBOperations.UpdateStock(stock);

								// Increment the updated rows counter
								rowsUpdated++;
						
								// Output the result
								System.out.println("Updated stock......" + stock.symbol);
							}
							
							// Call the method to retrieve the latest quote object for the stock
							Quote latestQuote = StockQuotesDBOperations.GetQuote(stock.id, "LATEST", null);
								
							// Set the trade date difference and price change properties for the quote object
							if (latestQuote.stockId == -1) {

								// Get the time difference between the current date and the stock date updated
								long timeDifferenceInMilliseconds = dateStarted.getTime() - stock.dateUpdated.getTime();
					
								// Check the time difference and deactivate the stock record
								if (timeDifferenceInMilliseconds > Constants.DATE_UPDATED_LIMIT_IN_MILLISECONDS) {
									
									// Set the active property for the stock object
									stock.active = 'N';
								
									// Call the method to update the STOCK record
									StockQuotesDBOperations.UpdateStock(stock);

									// Increment the updated rows counter
									rowsUpdated++;
						
									// Output the result
									System.out.println("Deactivated stock......" + stock.symbol);
								}
							}
						}
					}
				}
			}
		}
		catch (IOException ex) {
			System.out.println("ERROR occurred....." + ex);
		}
		
		// Call the function to update the job record
		Functions.FinishJob("Populate Stocks", dateStarted, rowsProcessed, rowsInserted, rowsUpdated);
	}
}
