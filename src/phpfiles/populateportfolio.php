<?php
/*
 * populateportfolio.php
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
	include("functions.php");
	include("constants.php");

	// Get the posted page number
	$pageNumber = "1";
	if (isset($_POST["pagenumber"])) {
		$pageNumber = $_POST["pagenumber"];
	}
	
	// Calculate the page offset
	$pageOffset = ($pageNumber - 1) * PAGE_RECORD_LIMIT;
	
	// Calculate the previous and next page numbers
	$previousPage = $pageNumber - 1;
	$nextPage = $pageNumber + 1;

	// Calculate the page first and last record numbers
	$pageFirstRecord = $pageOffset + 1;
	$pageLastRecord = $pageFirstRecord + PAGE_RECORD_LIMIT - 1;

	// Get the total number of records
	$sqlPortfolioTotalRecords = "SELECT COUNT(S.STOCK_ID)";
	$sqlPortfolioTotalRecords .= " FROM STOCKS S, PORTFOLIO P";
	$sqlPortfolioTotalRecords .= " WHERE S.STOCK_ID = P.STOCK_ID";
	if ($result = mysqli_query($link, $sqlPortfolioTotalRecords)) {
		$row = mysqli_fetch_array($result);
		$totalRecords = $row[0];
		$totalPages = ceil($totalRecords / PAGE_RECORD_LIMIT);
		if ($pageLastRecord > $totalRecords) {$pageLastRecord = $totalRecords;}
	}
	else {
		$totalRecords = 0;
		$totalPages = 0;
	}

	// Set the sql statement to select the portfolio records that have been sold
	$sqlPortfolioProfit = "SELECT S.CURRENCY, P.QUANTITY, P.PRICE_PURCHASED, P.PRICE_SOLD";
	$sqlPortfolioProfit .= " FROM STOCKS S, PORTFOLIO P";
	$sqlPortfolioProfit .= " WHERE S.STOCK_ID = P.STOCK_ID";
	$sqlPortfolioProfit .= " AND P.DATE_SOLD != '0000-00-00 00:00:00'";

	// Initialise the grand total variables
	$total_purchased_amount = 0.0;
	$total_profit = 0.0;

	// Calculate the total purchased amount and profit
	if ($result = mysqli_query($link, $sqlPortfolioProfit)) {
		if (mysqli_num_rows($result) > 0) {
			while ($row = mysqli_fetch_array($result)) {
				$currency = $row["CURRENCY"];
				$quantity = $row["QUANTITY"];
				$price_purchased = $row["PRICE_PURCHASED"];
				$price_sold = $row["PRICE_SOLD"];

				// Calculate the profit using the price sold
				$profit = ($price_sold - $price_purchased) * $quantity;

				// Convert the profit and total purchased amount to 'GBP'
				if ($currency == "GBp") {
					$profit = $profit / 100;
					$total_purchased_amount += ($price_purchased * $quantity) / 100;
				}
				else {
					$total_purchased_amount += $price_purchased * $quantity;
				}
				
				// Add to the total profit
				$total_profit += $profit;
			}
		}
	}

	// Set the sql statement to select the portfolio records
	$sqlPortfolio = "SELECT S.STOCK_ID, S.SYMBOL, S.NAME, S.CURRENCY, P.PORTFOLIO_ID, P.TYPE, P.QUANTITY, P.DATE_PURCHASED, P.PRICE_PURCHASED, P.DATE_SOLD, P.PRICE_SOLD";
	$sqlPortfolio .= " FROM STOCKS S, PORTFOLIO P";
	$sqlPortfolio .= " WHERE S.STOCK_ID = P.STOCK_ID";
	$sqlPortfolio .= " ORDER BY P.DATE_PURCHASED DESC";
	$sqlPortfolio .= " LIMIT $pageOffset, " . PAGE_RECORD_LIMIT;

	if ($result = mysqli_query($link, $sqlPortfolio)) {
		if (mysqli_num_rows($result) > 0) {
			while ($row = mysqli_fetch_array($result)) {
				$currency = $row["CURRENCY"];
				$quantity = $row["QUANTITY"];
				$date_purchased = date_create($row["DATE_PURCHASED"]);
				$price_purchased = $row["PRICE_PURCHASED"];
				$date_sold = date_create($row["DATE_SOLD"]);
				$price_sold = $row["PRICE_SOLD"];
				
				if ($price_sold == 0) {
					// Call the function to get the latest price from the QUOTES table
					list($error, $price) = getLatestQuote($link, $row["STOCK_ID"]);

					// Calculate the profit using the current price
					if (empty($error)) {
						$profit = ($price - $price_purchased) * $quantity;
						$profit_percent = (($price - $price_purchased) / $price_purchased) * 100;
					}
				}
				else {
					// Calculate the profit using the price sold
					$profit = ($price_sold - $price_purchased) * $quantity;
					$profit_percent = (($price_sold - $price_purchased) / $price_purchased) * 100;
				}

				// Convert the profit and total purchased amount to 'GBP'
				if ($currency == "GBp") {
					$profit = $profit / 100;
				}
				
				echo "<tr>";
				echo "<td>" . $row["SYMBOL"] . "</td>";
				echo "<td>" . $row["NAME"] . "</td>";
				echo "<td class='centre'>" . $row["CURRENCY"] . "</td>";
				echo "<td class='centre'>" . $row["TYPE"] . "</td>";
				echo "<td class='right'>" . number_format($quantity) . "</td>";
				echo "<td class='centre'>" . date_format($date_purchased, "d-m-Y H:i:s") . "</td>";
				echo "<td class='right'>" . $price_purchased . "</td>";
				if ($price_sold == 0) {
					echo "<td></td>";
					echo "<td></td>";
				}
				else {
					echo "<td class='centre'>" . date_format($date_sold, "d-m-Y H:i:s") . "</td>";
					echo "<td class='right'>" . $price_sold . "</td>";
				}
				if (!empty($error)) {
					echo "<td>" . $error . "</td>";
				}
				else if ($profit > 0) {
					echo "<td class='rightpositive'>+" . number_format($profit, 2) . " (+" . number_format($profit_percent, 2) . "%)</td>";
				}
				else if ($profit < 0) {
					echo "<td class='rightnegative'>" . number_format($profit, 2) . " (" . number_format($profit_percent, 2) . "%)</td>";
				}
				else {
					echo "<td class='right'>" . number_format($profit, 2) . " (" . number_format($profit_percent, 2) . "%)</td>";
				}
				if ($price_sold != 0) {
					echo "<td class='centre'><input type='button' id='sellstock' name='" . $row["PORTFOLIO_ID"] . "' value='Sell' disabled></td>";
				}
				else {
					echo "<td class='centre'><input type='button' id='sellstock' name='" . $row["PORTFOLIO_ID"] . "' value='Sell'></td>";
				}
				echo "<td class='centre'><input type='button' id='showquotes' name='" . $row["STOCK_ID"] . "' value='Quotes'></td>";
				echo "<td class='centre'><input type='button' id='deletestock' name='" . $row["PORTFOLIO_ID"] . "' value='Delete'></td>";
				echo "</tr>";
			}
			
			// Close the result set
			mysqli_free_result($result);
		}
		else {
			echo "<tr><td>No records were found.</td></tr>";
		}
	}
	else {
		echo "<tr><td>ERROR: Could not execute SQL statement. " . mysqli_error($link) . "</td></tr>";
	}

	// Print the pagination
	echo "<tr class='footer'>";
	echo "<td colspan='14'>Page " . $pageNumber . " of " . $totalPages . " (" . $pageFirstRecord . "-" . $pageLastRecord . " of " . $totalRecords . ")";
	if ($pageNumber > 1) {
		echo "<input type='button' id='portfoliofirstpage' name='1' value='First'>";
		echo "<input type='button' id='portfoliopreviouspage' name='" . $previousPage . "' value='Previous'>";
	}
	if ($pageNumber < $totalPages) {
		echo "<input type='button' id='portfolionextpage' name='" . $nextPage . "' value='Next'>";
		echo "<input type='button' id='portfoliolastpage' name='" . $totalPages . "' value='Last'>";
	}
	echo "</td></tr>";

	// Calculate the total profit percent
	$total_profit_percent = ($total_profit / $total_purchased_amount) * 100;

	echo "<tr class='footer'><td class='right' colspan='7'>Total Purchase Amount (GBP): " . number_format($total_purchased_amount, 2) . "</td>";
	if ($total_profit > 0) {
		echo "<td class='rightpositive' colspan='3'>Total Profit (GBP): +" . number_format($total_profit, 2) . " (+" . number_format($total_profit_percent, 2) . "%)</td>";
	}
	else if ($total_profit < 0) {
		echo "<td class='rightnegative' colspan='3'>Total Profit (GBP): " . number_format($total_profit, 2) . " (" . number_format($total_profit_percent, 2) . "%)</td>";
	}
	else {
		echo "<td class='right' colspan='3'>Total Profit (GBP): +" . number_format($total_profit, 2) . " (+" . number_format($total_profit_percent, 2) . "%)</td>";
	}
	echo "<td colspan='3'></td></tr>";

	// Close the MYSQL connection
	mysqli_close($link);
?>
