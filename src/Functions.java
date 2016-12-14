/*
 * Functions.java
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
import java.util.Calendar;

public class Functions {
	
	public static Date GetStartOfToday () {
		// Get the start of today
		Calendar calendarStartOfToday = Calendar.getInstance();
		calendarStartOfToday.set(Calendar.HOUR_OF_DAY, 0);
		calendarStartOfToday.set(Calendar.MINUTE, 0);
		calendarStartOfToday.set(Calendar.SECOND, 0);
		calendarStartOfToday.set(Calendar.MILLISECOND, 0);
		Date dateToday = calendarStartOfToday.getTime();
		
		return dateToday;
	}
	
	public static void FinishJob (String jobName, Date dateStarted, int rowsProcessed, int rowsInserted, int rowsUpdated) {
		// Get the current date and set the date finished
		Date dateFinished = Calendar.getInstance().getTime();

		// Calculate the elapsed time
		long timeDifferenceInSeconds = (dateFinished.getTime() - dateStarted.getTime()) / 1000;
		int elapsedTime = (int) timeDifferenceInSeconds;

		// Get the existing record from the JOBS table
		Job job = StockQuotesDBOperations.GetJob(jobName);

		// Instantiate the new job object and set the properties
		Job newJob = new Job();
		newJob.name = jobName;
		newJob.dateStarted = dateStarted;
		newJob.dateFinished = dateFinished;
		newJob.elapsedTime = elapsedTime;
		newJob.rowsProcessed = rowsProcessed;
		newJob.rowsInserted = rowsInserted;
		newJob.rowsUpdated = rowsUpdated;
		
		if (job.id == -1) {
			// Insert record into JOB table
			StockQuotesDBOperations.InsertJob(newJob);
		}
		else {
			// Update record into JOB table
			newJob.id = job.id;
			StockQuotesDBOperations.UpdateJob(newJob);
		}
	}
	
	// ##### Update the STOCK table to set the watch flag to 'Y'
	public static void WatchStock (Stock stock) {
		if (stock.watch != 'Y') {
			// Add to the watched stocks
			stock.watch = 'Y';
					
			// Call the method to update the STOCK record
			StockQuotesDBOperations.UpdateStock(stock);

			// Output the result
			System.out.println("Watching stock....." + stock.symbol);
		}
	}

	// ##### Update the STOCK table to set the watch flag to 'N'
	public static void UnwatchStock (Stock stock) {
		if (stock.watch != 'N') {
			// Unwatch the stock
			stock.watch = 'N';
					
			// Call the method to update the STOCK record
			StockQuotesDBOperations.UpdateStock(stock);

			// Output the result
			System.out.println("Unwatching stock....." + stock.symbol);
		}
	}
}

