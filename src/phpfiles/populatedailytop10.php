<?php
/*
 * populatedailytop10.php
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
	
	// Set the sql statement to select the top10 day price change percent records
	$sqlTop10DayPriceChangePercent = "SELECT S.STOCK_ID, S.SYMBOL, S.NAME, S.CURRENCY, S.SHARES_ISSUED, Q.DATE_TRADE, Q.DATE_TRADE_CHANGE, Q.PRICE, Q.PRICE_CHANGE, Q.PRICE_CHANGE_PERCENT, Q.DAY_PRICE_CHANGE, Q.DAY_PRICE_CHANGE_PERCENT, Q.VOLUME, Q.VOLUME_PERCENT";
	$sqlTop10DayPriceChangePercent .= " FROM STOCKS S, QUOTES Q";
	$sqlTop10DayPriceChangePercent .= " WHERE S.STOCK_ID = Q.STOCK_ID";
	$sqlTop10DayPriceChangePercent .= " AND S.ACTIVE = 'Y'";
	$sqlTop10DayPriceChangePercent .= " AND Q.DATE_TRADE = (SELECT MAX(Q2.DATE_TRADE) FROM QUOTES Q2 WHERE Q2.STOCK_ID = S.STOCK_ID AND Q2.DATE_TRADE BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 day))";
	$sqlTop10DayPriceChangePercent .= " ORDER BY Q.DAY_PRICE_CHANGE_PERCENT DESC";
	$sqlTop10DayPriceChangePercent .= " LIMIT 10";

	if ($result = mysqli_query($link, $sqlTop10DayPriceChangePercent)) {
		if (mysqli_num_rows($result) > 0) {
			while ($row = mysqli_fetch_array($result)) {
				$date_trade = date_create($row["DATE_TRADE"]);
				$date_trade_change = $row["DATE_TRADE_CHANGE"];
				$price_change = $row["PRICE_CHANGE"];
				$price_change_percent = $row["PRICE_CHANGE_PERCENT"];
				$day_price_change = $row["DAY_PRICE_CHANGE"];
				$day_price_change_percent = $row["DAY_PRICE_CHANGE_PERCENT"];
				echo "<tr>";
				echo "<td>" . $row["SYMBOL"] . "</td>";
				echo "<td>" . $row["NAME"] . "</td>";
				echo "<td class='centre'>" . $row["CURRENCY"] . "</td>";
				echo "<td class='right'>" . number_format($row["SHARES_ISSUED"]) . "</td>";
				echo "<td class='centre'>" . date_format($date_trade, "d-m-Y H:i:s") . "</td>";
				echo "<td class='right'>" . minutesToDaysHoursMinutes($date_trade_change) . "</td>";
				echo "<td class='right'>" . $row["PRICE"] . "</td>";
				if ($date_trade_change == 0) {
					echo "<td></td>";
				}
				else if ($price_change > 0) {
					echo "<td class='rightpositive'>+" . $price_change . " (+" . $price_change_percent . "%)</td>";
				}
				else if ($price_change < 0) {
					echo "<td class='rightnegative'>" . $price_change . " (" . $price_change_percent . "%)</td>";
				}
				else {
					echo "<td class='right'>" . $price_change . " (" . $price_change_percent . "%)</td>";
				}
				if ($day_price_change > 0) {
					echo "<td class='rightpositive'>+" . $day_price_change . " (+" . $day_price_change_percent . "%)</td>";
				}
				else if ($day_price_change < 0) {
					echo "<td class='rightnegative'>" . $day_price_change . " (" . $day_price_change_percent . "%)</td>";
				}
				else {
					echo "<td class='right'>" . $day_price_change . " (" . $day_price_change_percent . "%)</td>";
				}
				echo "<td class='right'>" . number_format($row["VOLUME"]) . " (" . $row["VOLUME_PERCENT"] . "%)</td>";
				echo "<td class='centre'><input type='button' id='addwatchstockid' name='" . $row["STOCK_ID"] . "' value='Watch'></td>";
				echo "<td class='centre'><input type='button' id='showquotes' name='" . $row["STOCK_ID"] . "' value='Quotes'></td>";
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

	// Close the MYSQL connection
	mysqli_close($link);
?>
