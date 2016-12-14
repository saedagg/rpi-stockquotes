<?php
/*
 * processaction.php
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

	// Get the posted action
	$action = "";
	if (isset($_POST["action"])) {
		$action = $_POST["action"];
	}

	// Get the posted stock id
	$stockId = "";
	if (isset($_POST["stockid"])) {
		$stockId = $_POST["stockid"];
	}

	// Get the posted portfolio id
	$portfolioId = "";
	if (isset($_POST["portfolioid"])) {
		$portfolioId = $_POST["portfolioid"];
	}

	// Check the action and perform the associated logic
    if ($action == "addwatchstockid" || $action == "addwatchsymbol") {
		if ($stockId != "") {
			// Set the sql statement to select the STOCKS record
			$sqlSelectStocksWhereStockId = "SELECT SYMBOL, NAME FROM STOCKS WHERE STOCK_ID = " . $stockId;

			if ($result = mysqli_query($link, $sqlSelectStocksWhereStockId)) {
				if (mysqli_num_rows($result) == 1) {
					// Get the symbol and name from the fetched row
					$row = mysqli_fetch_array($result);
					$symbol = $row["SYMBOL"];
					$name = $row["NAME"];
					
					// Set the sql statement to update the STOCKS record
					$sqlUpdateStocksSetWatch = "UPDATE STOCKS SET WATCH = 'Y' WHERE STOCK_ID = " . $stockId;
					if ($result = mysqli_query($link, $sqlUpdateStocksSetWatch)) {
						echo "Watching stock: " . $symbol . " : " . $name;
					}
					else {
						echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
					}
				}
				else {
					echo "No matching stock found for the stock id '" . $stockId . "'";
				}
			}
			else {
				echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
			}			
		}
	}
	else if ($action == "sellstock") {
		if ($portfolioId != "") {
			// Set the sql statement to select the PORTFOLIO record
			$sqlSelectPortfolio = "SELECT S.STOCK_ID, S.SYMBOL, S.NAME, P.PORTFOLIO_ID FROM PORTFOLIO P, STOCKS S WHERE P.STOCK_ID = S.STOCK_ID AND P.PORTFOLIO_ID = " . $portfolioId;

			if ($result = mysqli_query($link, $sqlSelectPortfolio)) {
				if (mysqli_num_rows($result) == 1) {
					// Get the symbol and name from the fetched row
					$row = mysqli_fetch_array($result);
					$stockId = $row["STOCK_ID"];
					$symbol = $row["SYMBOL"];
					$name = $row["NAME"];
				
					// Call the function to get the latest price from the QUOTES table
					list($error, $price) = getLatestQuote($link, $stockId);
					
					if (empty($error)) {
						if ($price > 0) {
							// Set the sql statement to update the PORTFOLIO record
							$sqlUpdatePortfolio = "UPDATE PORTFOLIO SET DATE_SOLD = now(), PRICE_SOLD = " . $price . " WHERE PORTFOLIO_ID = " . $portfolioId;
					
							if ($result = mysqli_query($link, $sqlUpdatePortfolio)) {
								echo "Sold stock: " . $symbol . " : " . $name;
							}
							else {
								echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
							}
						}
					}
					else {
						echo $error;
					}
				}
				else {
					echo "No matching portfolio found for the portfolio id '" . $portfolioId . "'";
				}
			}
			else {
				echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
			}	
		}		
	}
	else if ($action == "deletestock") {
		if ($portfolioId != "") {
			// Set the sql statement to select the PORTFOLIO record
			$sqlSelectPortfolio = "SELECT S.STOCK_ID, S.SYMBOL, S.NAME, P.PORTFOLIO_ID FROM PORTFOLIO P, STOCKS S WHERE P.STOCK_ID = S.STOCK_ID AND P.PORTFOLIO_ID = " . $portfolioId;

			if ($result = mysqli_query($link, $sqlSelectPortfolio)) {
				if (mysqli_num_rows($result) == 1) {
					// Get the symbol and name from the fetched row
					$row = mysqli_fetch_array($result);
					$symbol = $row["SYMBOL"];
					$name = $row["NAME"];

					// Set the sql statement to delete the PORTFOLIO record
					$sqlDeletePortfolio = "DELETE FROM PORTFOLIO WHERE PORTFOLIO_ID = " . $portfolioId;
					
					if ($result = mysqli_query($link, $sqlDeletePortfolio)) {
						echo "Deleted stock: " . $symbol . " : " . $name;
					}
					else {
						echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
					}
				}
				else {
					echo "No matching portfolio found for the portfolio id '" . $portfolioId . "'";
				}
			}
			else {
				echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
			}	
		}		
	}
	else if ($action == "buystock") {
		if ($stockId != "") {
			// Set the sql statement to select the STOCKS record
			$sqlSelectStocksWhereStockId = "SELECT SYMBOL, NAME, CURRENCY FROM STOCKS WHERE STOCK_ID = " . $stockId;

			if ($result = mysqli_query($link, $sqlSelectStocksWhereStockId)) {
				if (mysqli_num_rows($result) == 1) {
					// Get the symbol and name from the fetched row
					$row = mysqli_fetch_array($result);
					$symbol = $row["SYMBOL"];
					$name = $row["NAME"];
					$currency = $row["CURRENCY"];

					// Call the function to get the latest price from the QUOTES table
					list($error, $price) = getLatestQuote($link, $stockId);
					
					if (empty($error)) {
						// Calculate the quantity
						$quantity = 0;
						if ($currency == "GBp") {
							$quantity = intval((PORTFOLIO_BUY_AMOUNT * 100) / $price);
						}
						else if ($currency == "GBP") {
							$quantity = intval(PORTFOLIO_BUY_AMOUNT / $price);
						}
						
						if ($quantity > 0) {
							// Set the sql statement to insert the PORTFOLIO record
							$sqlInsertPortfolio = "INSERT PORTFOLIO (STOCK_ID, TYPE, QUANTITY, DATE_PURCHASED, PRICE_PURCHASED) VALUES (" . $stockId . ", 'MANUAL', " . $quantity . ", now(), " . $price . ")";
					
							if ($result = mysqli_query($link, $sqlInsertPortfolio)) {
								echo "Purchased stock: " . $symbol . " : " . $name;
							}
							else {
								echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
							}
						}
					}
					else {
						echo $error;
					}
				}
				else {
					echo "No matching stock found for the stock id '" . $stockId . "'";
				}
			}
			else {
				echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
			}	
		}		
	}
	else if ($action == "showquotes") {
		if ($stockId == "") {
			// Unset the session variable
			session_start();
			session_unset();
			
			echo "Cleared quotes.";
		}
		else {
			// Set the sql statement to select the STOCKS record
			$sqlSelectStocksWhereStockId = "SELECT SYMBOL, NAME FROM STOCKS WHERE STOCK_ID = " . $stockId;

			if ($result = mysqli_query($link, $sqlSelectStocksWhereStockId)) {
				if (mysqli_num_rows($result) == 1) {
					// Get the symbol and name from the fetched row
					$row = mysqli_fetch_array($result);
					$symbol = $row["SYMBOL"];
					$name = $row["NAME"];

					// Set the session variable
					session_start();
					$_SESSION["showquotesstockid"] = $stockId;
			
					echo "Showing quotes for stock: " . $symbol . " : " . $name;
				}
				else {
					echo "No matching stock found for the stock id '" . $stockId . "'";
				}
			}
			else {
				echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
			}			
		}
	}
	else if ($action == "unwatch") {
		if ($stockId != "") {
			// Set the sql statement to select the STOCKS record
			$sqlSelectStocksWhereStockId = "SELECT SYMBOL, NAME FROM STOCKS WHERE STOCK_ID = " . $stockId;

			if ($result = mysqli_query($link, $sqlSelectStocksWhereStockId)) {
				if (mysqli_num_rows($result) == 1) {
					// Get the symbol and name from the fetched row
					$row = mysqli_fetch_array($result);
					$symbol = $row["SYMBOL"];
					$name = $row["NAME"];
					
					// Set the sql statement to update the STOCKS record
					$sqlUpdateStocksSetWatch = "UPDATE STOCKS SET WATCH = 'N' WHERE STOCK_ID = " . $stockId;
					if ($result = mysqli_query($link, $sqlUpdateStocksSetWatch)) {
						echo "Unwatching stock: " . $symbol . " : " . $name;
					}
					else {
						echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
					}
				}
				else {
					echo "No matching stock found for the stock id '" . $stockId . "'";
				}
			}
			else {
				echo "ERROR: Could not execute SQL statement. " . mysqli_error($link);
			}			
		}
	}

	// Close the MYSQL connection
	mysqli_close($link);
?>
