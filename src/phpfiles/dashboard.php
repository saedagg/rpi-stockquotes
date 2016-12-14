<?php
/*
 * dashboard.php
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
	include("constants.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
	<title>Stock Quotes Dashboard</title>
	<meta http-equiv="content-type" content="text/html;charset=utf-8" />
	<meta name="generator" content="Geany 1.24.1" />
	<link rel="stylesheet" type="text/css" href="styles.css">
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
	<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
	<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
	<script language="javascript" type="text/javascript">
		// Return the formatted current date and time
		function getCurrentDateTime(){
			var currentDate = new Date();
			var day = currentDate.getDate();
			var month = currentDate.getMonth() + 1;
			var year = currentDate.getFullYear();
			var hours = currentDate.getHours();
			var minutes = currentDate.getMinutes();
			var seconds = currentDate.getSeconds();
			day = day < 10 ? "0" + day : day;
			month = month < 10 ? "0" + month : month;
			hours = hours < 10 ? "0" + hours : hours;
			minutes = minutes < 10 ? "0" + minutes : minutes;
			seconds = seconds < 10 ? "0" + seconds : seconds;
			var currentDateTime = day + "/" + month + "/" + year + " " + hours + ":" + minutes + ":" + seconds;
			return currentDateTime;
		}
		// Populate the Jobs table
		function populateJobs(){
			$.ajax({
				type: "POST",
				url: "populatejobs.php",
				success: function(responseHTML){
					$("#jobsData").html(responseHTML);
					document.getElementById("jobsDateUpdated").innerHTML = getCurrentDateTime();
				}
			});
		}
		// Populate the Daily Top 10 table
		function populateDailyTop10(){
			$.ajax({
				type: "POST",
				url: "populatedailytop10.php",
				success: function(responseHTML){
					$("#dailyTop10Data").html(responseHTML);
					document.getElementById("dailyTop10DateUpdated").innerHTML = getCurrentDateTime();
				}
			});
		}
		// Populate the Portfolio table
		function populatePortfolio(pagenumber){
			$.ajax({
				type: "POST",
				url: "populateportfolio.php",
				data: {pagenumber:pagenumber},
				success: function(responseHTML){
					$("#portfolioData").html(responseHTML);
					document.getElementById("portfolioDateUpdated").innerHTML = getCurrentDateTime();
				}
			});
		}
		// Populate the Watch table
		function populateWatch(pagenumber){
			$.ajax({
				type: "POST",
				url: "populatewatch.php",
				data: {pagenumber:pagenumber},
				success: function(responseHTML){
					$("#watchData").html(responseHTML);
					document.getElementById("watchDateUpdated").innerHTML = getCurrentDateTime();
				}
			});
		}
		// Populate the Quotes table
		function populateQuotes(pagenumber){
			$.ajax({
				type: "POST",
				url: "populatequotes.php",
				data: {pagenumber:pagenumber},
				success: function(responseHTML){
					$("#quotesData").html(responseHTML);
					document.getElementById("quotesDateUpdated").innerHTML = getCurrentDateTime();
				}
			});
		}
		// Call all functions to refresh all data
		function refreshData(){
			populateJobs();
			populateDailyTop10();
			populatePortfolio(1);
			populateWatch(1);
			populateQuotes(1);
		}
		// Buy stock and add to portoflio
		function buyStock(){
			var stockid = $("#buystockid").val();
			$("#dialogbuy").dialog("close");
			$.ajax({
				type: "POST",
				url: "processaction.php",
				data: {action:"buystock", stockid:stockid},
				success: function(responseText){
					document.getElementById("messageResponse").innerHTML = responseText;
					populatePortfolio(1);
				},
				error: function(jqXHR, textStatus, errorThrown){
					document.getElementById("messageResponse").innerHTML = "Ajax Request Error:" + textStatus + jqXHR.responseText + errorThrown;
				}
			});
		}
		// Sell or delete stock in portfolio table
		function sellDeleteStock(action){
			var portfolioid;
			if(action == "sellstock"){
				portfolioid = $("#sellportfolioid").val();
				$("#dialogsell").dialog("close");
			}
			else if(action == "deletestock"){
				portfolioid = $("#deleteportfolioid").val();
				$("#dialogdelete").dialog("close");
			}
			$.ajax({
				type: "POST",
				url: "processaction.php",
				data: {action:action, portfolioid:portfolioid},
				success: function(responseText){
					document.getElementById("messageResponse").innerHTML = responseText;
					populatePortfolio(1);
				},
				error: function(jqXHR, textStatus, errorThrown){
					document.getElementById("messageResponse").innerHTML = "Ajax Request Error:" + textStatus + jqXHR.responseText + errorThrown;
				}
			});
		}
		$(document).ready(function(){
			// Refresh the tables with data
			refreshData();

			// Set the autocomplete for the Watch symbols
			$("#watchsymbol").autocomplete({
				source: "getsymbols.php",
				minLength: 2,
				select: function(event, ui){
					$("#watchstockid").val(ui.item.id);
					$("#watchsymbol").val(ui.item.value);
					return false;
				}
			});

			// Create the buy dialog
			var buydialog = $("#dialogbuy").dialog({
				autoOpen: false,
				height: 250,
				width: 300,
				modal: true,
				buttons: {
					Confirm:function(){
						buyStock();
					},
					Cancel:function(){
						buydialog.dialog("close");
					}
				}
			});

			// Create the sell dialog
			var selldialog = $("#dialogsell").dialog({
				autoOpen: false,
				height: 200,
				width: 300,
				modal: true,
				buttons: {
					Confirm:function(){
						sellDeleteStock("sellstock");
					},
					Cancel:function(){
						selldialog.dialog("close");
					}
				}
			});
			
			// Create the delete dialog
			var deletedialog = $("#dialogdelete").dialog({
				autoOpen: false,
				height: 200,
				width: 300,
				modal: true,
				buttons: {
					Confirm:function(){
						sellDeleteStock("deletestock");
					},
					Cancel:function(){
						deletedialog.dialog("close");
					}
				}
			});

			// Attach the button event handlers
			$(document).on("click", ":button", function(){
				if(this.id == "refresh"){
					refreshData();
				}
				else if(this.id == "togglejobs"){
					$("#divjobs").toggle();
				}
				else if(this.id == "toggledailytop10"){
					$("#divdailytop10").toggle();
				}
				else if(this.id == "toggleportfolio"){
					$("#divportfolio").toggle();
				}
				else if(this.id == "togglewatch"){
					$("#divwatch").toggle();
				}
				else if(this.id == "togglequotes"){
					$("#divquotes").toggle();
				}
				else if(this.id == "addwatchstockid"){
					var stockid = this.name;
					if(stockid > 0){
						$.ajax({
							type: "POST",
							url: "processaction.php",
							data: {action:this.id, stockid:stockid},
							success: function(responseText){
								document.getElementById("messageResponse").innerHTML = responseText;
								populateWatch(1);
							},
							error: function(jqXHR, textStatus, errorThrown){
								document.getElementById("messageResponse").innerHTML = "Ajax Request Error:" + textStatus + jqXHR.responseText + errorThrown;
							}
						});
					}
				}
				else if(this.id == "addwatchsymbol"){
					var stockid = $("#watchstockid").val();
					if(stockid > 0){
						$.ajax({
							type: "POST",
							url: "processaction.php",
							data: {action:this.id, stockid:stockid},
							success: function(responseText){
								document.getElementById("messageResponse").innerHTML = responseText;
								populateWatch(1);
								$("#watchsymbol").val("");
								$("#watchstockid").val("");
							},
							error: function(jqXHR, textStatus, errorThrown){
								document.getElementById("messageResponse").innerHTML = "Ajax Request Error:" + textStatus + jqXHR.responseText + errorThrown;
							}
						});
					}
				}
				else if(this.id == "buystock"){
					var stockid = this.name;
					var buystockamount = "<?php echo "GBP " . number_format(PORTFOLIO_BUY_AMOUNT, 2); ?>";
					$("#buystockid").val(stockid);
					$("#buystockamount").val(buystockamount);
					buydialog.dialog("open");
				}
				else if(this.id == "sellstock"){
					var portfolioid = this.name;
					$("#sellportfolioid").val(portfolioid);
					selldialog.dialog("open");
				}
				else if(this.id == "deletestock"){
					var portfolioid = this.name;
					$("#deleteportfolioid").val(portfolioid);
					deletedialog.dialog("open");
				}
				else if(this.id == "showquotes"){
					var stockid = this.name;
					if(stockid > 0){
						$.ajax({
							type: "POST",
							url: "processaction.php",
							data: {action:this.id, stockid:stockid},
							success: function(responseText){
								document.getElementById("messageResponse").innerHTML = responseText;
								populateQuotes(1);
							},
							error: function(jqXHR, textStatus, errorThrown){
								document.getElementById("messageResponse").innerHTML = "Ajax Request Error:" + textStatus + jqXHR.responseText + errorThrown;
							}
						});
					}
				}
				else if(this.id == "clearquotes"){
					var stockid = "";
					$.ajax({
						type: "POST",
						url: "processaction.php",
						data: {action:"showquotes", stockid:stockid},
						success: function(responseText){
							document.getElementById("messageResponse").innerHTML = responseText;
							populateQuotes(1);
						},
						error: function(jqXHR, textStatus, errorThrown){
							document.getElementById("messageResponse").innerHTML = "Ajax Request Error:" + textStatus + jqXHR.responseText + errorThrown;
						}
					});
				}
				else if(this.id == "unwatch"){
					var stockid = this.name;
					if(stockid > 0){
						$.ajax({
							type: "POST",
							url: "processaction.php",
							data: {action:this.id, stockid:stockid},
							success: function(responseText){
								document.getElementById("messageResponse").innerHTML = responseText;
								populateWatch(1);
							},
							error: function(jqXHR, textStatus, errorThrown){
								document.getElementById("messageResponse").innerHTML = "Ajax Request Error:" + textStatus + jqXHR.responseText + errorThrown;
							}
						});
					}
				}
				else if(this.id == "portfoliofirstpage" || this.id == "portfoliopreviouspage" || this.id == "portfolionextpage" || this.id == "portfoliolastpage"){
					var pagenumber = this.name;
					populatePortfolio(pagenumber);
				}
				else if(this.id == "watchfirstpage" || this.id == "watchpreviouspage" || this.id == "watchnextpage" || this.id == "watchlastpage"){
					var pagenumber = this.name;
					populateWatch(pagenumber);
				}
				else if(this.id == "quotesfirstpage" || this.id == "quotespreviouspage" || this.id == "quotesnextpage" || this.id == "quoteslastpage"){
					var pagenumber = this.name;
					populateQuotes(pagenumber);
				}
			});
		});
	</script>
</head>

<body>
	<div id="dialogbuy" title="Buy Stock">
		<input type="hidden" name="buystockid" id="buystockid" value="">
		<label for="buystockamount">Amount:</label><br>
		<input type="text" name="buystockamount" id="buystockamount" value="" disabled>
		<p>Press 'Confirm' to buy stock.</p>
	</div>
	<div id="dialogsell" title="Sell Stock">
		<input type="hidden" name="sellportfolioid" id="sellportfolioid" value="">
		<p>Press 'Confirm' to sell stock.</p>
	</div>
	<div id="dialogdelete" title="Delete Stock">
		<input type="hidden" name="deleteportfolioid" id="deleteportfolioid" value="">
		<p>Press 'Confirm' to delete stock.</p>
	</div>
	<table class="noborder">
		<tr>
			<td class="noborder" width="25%"><h1>Stock Quotes Dashboard</h1></td>
			<td class="noborder" width="10%"><input type="button" id="refresh" value="Refresh"></td>
			<td class="noborder" width="35%" align="center"><h2 id="messageResponse"></h2></td>
			<td class="noborder" width="30%" align="right">
				<input type="button" id="togglejobs" value="Jobs">
				<input type="button" id="toggledailytop10" value="Daily Top 10">
				<input type="button" id="toggleportfolio" value="Portfolio">
				<input type="button" id="togglewatch" value="Watch">
				<input type="button" id="togglequotes" value="Quotes">
			</td>
		</tr>
	</table>
	<div id="divjobs">
		<table>
			<thead>
				<tr class="title">
					<td colspan="7"><h2 style="float:left;">Jobs</h2><h3 id="jobsDateUpdated" style="float:right;"></h3></td>
				</tr>
				<tr>
					<th>Name</th>
					<th>Date Started</th>
					<th>Date Finished</th>
					<th>Elapsed Time<br>(min:sec)</th>
					<th>Rows Processed</th>
					<th>Rows Inserted</th>
					<th>Rows Updated</th>
				</tr>
			</thead>
			<tbody id="jobsData"></tbody>
		</table>
	</div>
	<div id="divdailytop10">
		<table>
			<thead>
				<tr class="title">
					<td colspan="12"><h2 style="float:left;">Daily Top 10</h2><h3 id="dailyTop10DateUpdated" style="float:right;"></h3></td>
				</tr>
				<tr>
					<th>Symbol</th>
					<th>Name</th>
					<th>Currency</th>
					<th>Shares Issued</th>
					<th>Trade Date</th>
					<th>Trade Date Change<br>(day:hrs:min)</th>
					<th>Price</th>
					<th>Price Change</th>
					<th>Day Price Change</th>
					<th>Volume</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<tbody id="dailyTop10Data"></tbody>
		</table>
	</div>
	<div id="divportfolio">
		<table>
			<thead>
				<tr class="title">
					<td colspan="13"><h2 style="float:left;">Portfolio</h2><h3 id="portfolioDateUpdated" style="float:right;"></h3></td>
				</tr>
				<tr>
					<th>Symbol</th>
					<th>Name</th>
					<th>Currency</th>
					<th>Type</th>
					<th>Quantity</th>
					<th>Purchase Date</th>
					<th>Purchase Price</th>
					<th>Sold Date</th>
					<th>Sold Price</th>
					<th>Profit (GBP)</th>
					<th colspan="3"></th>
				</tr>
			</thead>
			<tbody id="portfolioData"></tbody>
		</table>
	</div>
	<div id="divwatch">
		<table>
			<thead>
				<tr class="title">
					<td colspan="14"><h2 style="float:left;">Watch</h2><h3 id="watchDateUpdated" style="float:right;"></h3></td>
				</tr>
				<tr class="titlewatch">
					<td colspan="14">
						<input type="hidden" id="watchstockid" name="watchstockid">
						<input type="text" id="watchsymbol" name="watchsymbol">
						<input type="button" id="addwatchsymbol" value="Watch">
					</td>
				</tr>
				<tr>
					<th>Symbol</th>
					<th>Name</th>
					<th>Currency</th>
					<th>Active</th>
					<th>Shares Issued</th>
					<th>Trade Date</th>
					<th>Trade Date Change<br>(day:hrs:min)</th>
					<th>Price</th>
					<th>Price Change</th>
					<th>Day Price Change</th>
					<th>Volume</th>
					<th colspan="3"></th>
				</tr>
			</thead>
			<tbody id="watchData"></tbody>
		</table>
	</div>
	<div id="divquotes">
		<table>
			<thead>
				<tr class="title">
					<td colspan="10"><h2 style="float:left;">Quotes</h2><input type="button" id="clearquotes" value="Clear" style="float:left;"><h3 id="quotesDateUpdated" style="float:right;"></h3></td>
				</tr>
				<tr>
					<th>Symbol</th>
					<th>Name</th>
					<th>Currency</th>
					<th>Shares Issued</th>
					<th>Trade Date</th>
					<th>Trade Date Change<br>(day:hrs:min)</th>
					<th>Price</th>
					<th>Price Change</th>
					<th>Day Price Change</th>
					<th>Volume</th>
				</tr>
			</thead>
			<tbody id="quotesData"></tbody>
		</table>
	</div>
</body>

</html>
