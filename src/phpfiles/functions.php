<?php
/*
 * functions.php
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

	// Convert the number of minutes to "days:hours:minutes" format
	function minutesToDaysHoursMinutes($inputMinutes) {
		// Initialise the output variable
		$output = "";
		
		if (!empty($inputMinutes )) {
			// Set the constants
			$hoursInADay = 24;
			$minutesInAnHour = 60;
			$minutesInADay = $hoursInADay * $minutesInAnHour;
		
			// Calculate the number of days
			$days = floor($inputMinutes / $minutesInADay);
		
			// Calculate the number of hours
			$hourMinutes = $inputMinutes % $minutesInADay;
			$hours = floor($hourMinutes / $minutesInAnHour);
		
			// Calculate the number of minutes
			$minutes = $hourMinutes % $minutesInAnHour;
			
			// Set the output string
			$output = str_pad($days, 2, "0", STR_PAD_LEFT) . ":" . str_pad($hours, 2, "0", STR_PAD_LEFT) . ":" . str_pad($minutes, 2, "0", STR_PAD_LEFT);
		}
		
		return $output;
	}

	// Convert the number of seconds to "minutes:seconds" format
	function secondsToMinutesSeconds($inputSeconds) {
		// Initialise the output variable
		$output = "";
		
		if (!empty($inputSeconds )) {
			// Set the constants
			$secondsInAMinute = 60;
		
			// Calculate the number of minutes
			$minutes = floor($inputSeconds / $secondsInAMinute);
		
			// Calculate the number of seconds
			$minuteSeconds = $inputSeconds % $secondsInAMinute;
			$seconds = $minuteSeconds % $secondsInAMinute;
			
			// Set the output string
			$output = str_pad($minutes, 2, "0", STR_PAD_LEFT) . ":" . str_pad($seconds, 2, "0", STR_PAD_LEFT);
		}
		
		return $output;
	}
	
	// Get the latest price from the QUOTES table
	function getLatestQuote($link, $stockId) {
		// Initialise the return outputs
		$error = "";
		$price = 0;
		
		// Set the sql statement to select the QUOTES record
		$sqlSelectQuotes = "SELECT PRICE FROM QUOTES WHERE STOCK_ID = " . $stockId . " AND DATE_TRADE = (SELECT MAX(DATE_TRADE) FROM QUOTES WHERE STOCK_ID = " . $stockId . ")";

		if ($result = mysqli_query($link, $sqlSelectQuotes)) {
			if (mysqli_num_rows($result) == 1) {
				// Get the price from the fetched row
				$row = mysqli_fetch_array($result);
				$price = $row["PRICE"];
			}
			else {
				$error = "No quote found for the stock id '" . $stockId . "'";
			}
		}
		else {
			$error = "ERROR: Could not execute SQL statement. " . mysqli_error($link);
		}
		
		return array($error, $price);
	}
?>
