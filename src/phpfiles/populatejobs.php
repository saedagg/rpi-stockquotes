<?php
/*
 * populatejobs.php
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

	// Set the sql statement to select the JOBS records
	$sqlJobs = "SELECT JOB_ID, NAME, DATE_STARTED, DATE_FINISHED, ELAPSED_TIME, ROWS_PROCESSED, ROWS_INSERTED, ROWS_UPDATED";
	$sqlJobs .= " FROM JOBS";
	$sqlJobs .= " ORDER BY NAME";

	if ($result = mysqli_query($link, $sqlJobs)) {
		if (mysqli_num_rows($result) > 0) {
			while ($row = mysqli_fetch_array($result)) {
				$date_started = date_create($row["DATE_STARTED"]);
				$date_finished = date_create($row["DATE_FINISHED"]);
				echo "<tr>";
				echo "<td>" . $row["NAME"] . "</td>";
				echo "<td class='centre'>" . date_format($date_started, "d-m-Y H:i:s") . "</td>";
				echo "<td class='centre'>" . date_format($date_finished, "d-m-Y H:i:s") . "</td>";
				echo "<td class='centre'>" . secondsToMinutesSeconds($row["ELAPSED_TIME"]) . "</td>";
				echo "<td class='right'>" . number_format($row["ROWS_PROCESSED"]) . "</td>";
				echo "<td class='right'>" . number_format($row["ROWS_INSERTED"]) . "</td>";
				echo "<td class='right'>" . number_format($row["ROWS_UPDATED"]) . "</td>";
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
