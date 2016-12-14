<?php
/*
 * getsymbols.php
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

	// Include files
	include("mysqlconnect.php");
	
	// Retrieve the search term that autocomplete sends
	$term = $_GET["term"];
	
	// Initialise the JSON row and array
	$dataRow = array();
	$data = array();
	
	// Set the sql statement to select the symbols from the STOCKS table
	$sqlSelectStocksSymbols = "SELECT S.STOCK_ID, S.SYMBOL, S.NAME";
	$sqlSelectStocksSymbols .= " FROM STOCKS S";
	$sqlSelectStocksSymbols .= " WHERE S.SYMBOL LIKE '%" . $term . "%'";
	$sqlSelectStocksSymbols .= " ORDER BY S.SYMBOL ASC";

	if ($result = mysqli_query($link, $sqlSelectStocksSymbols)) {
		if (mysqli_num_rows($result) > 0) {
			while ($row = mysqli_fetch_array($result)) {
				$dataRow["id"] = $row["STOCK_ID"];
				$dataRow["value"] = $row["SYMBOL"];
				$dataRow["label"] = $row["SYMBOL"] . " : " . $row["NAME"];
				array_push($data, $dataRow);
			}
			
			// Close the result set
			mysqli_free_result($result);
		}
		else {
			$dataRow["value"] = "No records were found.";
			$dataRow["label"] = "No records were found.";
			array_push($data, $dataRow);
		}
	}
	else {
		$dataRow["value"] = "ERROR: Could not execute SQL statement. " . mysqli_error($link);
		$dataRow["label"] = "ERROR: Could not execute SQL statement. " . mysqli_error($link);
		array_push($data, $dataRow);
	}

	// Return the JSON data
	echo json_encode($data);

	// Close the MYSQL connection
	mysqli_close($link);
?>
