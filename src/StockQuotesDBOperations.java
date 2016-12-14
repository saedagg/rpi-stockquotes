/*
 * StockQuotesDBOperations.java
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.ParseException;

public class StockQuotesDBOperations {
	
	// Set the mysql database JDBC connection parameters
	private static String dbConnectionUrl = "jdbc:mysql://localhost:3306/STOCKQUOTESDB?zeroDateTimeBehavior=convertToNull";
	private static String dbUser = "root";
	private static String dbPassword = "password";

	// ##### Return the job object from the JOBS table given the name
	public static Job GetJob (String name) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		ResultSet dbResultSet = null;

		// Initialise the query for retrieving the job record
		String dbSqlJobsSelect = "SELECT JOB_ID, NAME, DATE_STARTED, DATE_FINISHED, ELAPSED_TIME, ROWS_PROCESSED, ROWS_INSERTED, ROWS_UPDATED FROM JOBS WHERE NAME = '" + name + "'";

		// Instantiate the job object
		Job job = new Job();
		
		// Create a mysql connection and set the stock object properties
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbStatement = dbConnection.createStatement();
			dbResultSet = dbStatement.executeQuery(dbSqlJobsSelect);
			
			if (dbResultSet.next()) {
				job.id = dbResultSet.getInt(1);
				job.name = dbResultSet.getString(2);
				job.dateStarted = dbResultSet.getTimestamp(3);
				job.dateFinished = dbResultSet.getTimestamp(4);
				job.elapsedTime = dbResultSet.getInt(5);
				job.rowsProcessed = dbResultSet.getInt(6);
				job.rowsInserted = dbResultSet.getInt(7);
				job.rowsUpdated = dbResultSet.getInt(8);
			}
			else {
				job.id = -1;
				job.name = "";
			}
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (GetJob)....." + job.id + "....." + ex);
		}
		finally {
			try {
				if (dbResultSet != null) {
					dbResultSet.close();
				}
				if (dbStatement != null) {
					dbStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (GetJob)....." + job.id + "....." + ex);
			}
		}
		
		// Return the job object
		return job;
	}

	// ##### Insert a record into the JOBS table
	public static boolean InsertJob (Job job) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		PreparedStatement dbPreparedStatement = null;

		// Initialise the query for inserting the job record
		String dbSqlJobsInsert = "INSERT INTO JOBS (NAME, DATE_STARTED, DATE_FINISHED, ELAPSED_TIME, ROWS_PROCESSED, ROWS_INSERTED, ROWS_UPDATED) VALUES (?, ?, ?, ?, ?, ?)";

		// Convert the started date and finished date into SQL timestamp format
		java.sql.Timestamp startedDateSql = new java.sql.Timestamp(job.dateStarted.getTime());
		java.sql.Timestamp finishedDateSql = new java.sql.Timestamp(job.dateFinished.getTime());

		// Create a mysql connection and run the insert statement
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbPreparedStatement = dbConnection.prepareStatement(dbSqlJobsInsert);
			dbPreparedStatement.setString(1, job.name);
			dbPreparedStatement.setTimestamp(2, startedDateSql);
			dbPreparedStatement.setTimestamp(3, finishedDateSql);
			dbPreparedStatement.setInt(4, job.elapsedTime);
			dbPreparedStatement.setInt(5, job.rowsProcessed);
			dbPreparedStatement.setInt(6, job.rowsInserted);
			dbPreparedStatement.setInt(7, job.rowsUpdated);
			dbPreparedStatement.execute();
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (InsertJob)....." + job.name + "....." + ex);
		}
		finally {
			try {
				if (dbPreparedStatement != null) {
					dbPreparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (InsertJob)....." + job.name + "....." + ex);
			}
		}
	
		// Return true
		return true;
	}

	// ##### Update the record in the JOBS table for the input job object
	public static boolean UpdateJob (Job job) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		PreparedStatement dbPreparedStatement = null;

		// Initialise the query for updating the JOBS record
		String dbSqlJobsUpdate = "UPDATE JOBS SET NAME = ?, DATE_STARTED = ?, DATE_FINISHED = ?, ELAPSED_TIME = ?, ROWS_PROCESSED = ?, ROWS_INSERTED = ?, ROWS_UPDATED = ? WHERE JOB_ID = ?";

		// Convert the started date and finished date into SQL timestamp format
		java.sql.Timestamp startedDateSql = new java.sql.Timestamp(job.dateStarted.getTime());
		java.sql.Timestamp finishedDateSql = new java.sql.Timestamp(job.dateFinished.getTime());

		// Create a mysql connection and run the update statement
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbPreparedStatement = dbConnection.prepareStatement(dbSqlJobsUpdate);
			dbPreparedStatement.setString(1, job.name);
			dbPreparedStatement.setTimestamp(2, startedDateSql);
			dbPreparedStatement.setTimestamp(3, finishedDateSql);
			dbPreparedStatement.setInt(4, job.elapsedTime);
			dbPreparedStatement.setInt(5, job.rowsProcessed);
			dbPreparedStatement.setInt(6, job.rowsInserted);
			dbPreparedStatement.setInt(7, job.rowsUpdated);
			dbPreparedStatement.setInt(8, job.id);
			dbPreparedStatement.execute();
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (UpdateJob)....." + job.id + "....." + ex);
		}
		finally {
			try {
				if (dbPreparedStatement != null) {
					dbPreparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (UpdateJob)....." + job.id + "....." + ex);
			}
		}
	
		// Return true
		return true;
	}

	// ##### Get the PORTFOLIO STOCKS
	// ##### Modes: "ALL", "NOT_SOLD"
	// ##### Types: "MANUAL", "AUTOMATIC"
	public static List<Portfolio> GetPortfolios (String mode, String type) {

		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		ResultSet dbResultSet = null;
		String dbSqlPortfoliosSelect = "";

		if (mode.equals("ALL")) {
			// Initialise the query for retrieving all the portfolio records
			dbSqlPortfoliosSelect = "SELECT PORTFOLIO_ID FROM PORTFOLIO WHERE TYPE = '" + type + "'";
		}
		else if (mode.equals("NOT_SOLD")) {
			// Initialise the query for retrieving the portfolio records that have not been sold
			dbSqlPortfoliosSelect = "SELECT PORTFOLIO_ID FROM PORTFOLIO WHERE TYPE = '" + type + "' AND DATE_SOLD ='0000-00-00 00:00:00'";
		}
		else {
			System.out.println("ERROR occurred (GetPortfolios).....Invalid mode : " + mode);
		}

		// Initialise the portfolios lists
		List<Portfolio> portfoliosList = new ArrayList<Portfolio>();

		// Create a mysql connection, run the query and populate the portfolios list
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbStatement = dbConnection.createStatement();
			dbResultSet = dbStatement.executeQuery(dbSqlPortfoliosSelect);
			
			while (dbResultSet.next()) {
				// Get the portfolio id
				int portfolioId = dbResultSet.getInt(1);

				// Call the method to get the portfolio object
				Portfolio portfolio = GetPortfolio(portfolioId, -1, type);

				// Add the stock object to the portfolios list
				portfoliosList.add(portfolio);
			}
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (GetPortfolios)....." + ex);
		}
		finally {
			try {
				if (dbResultSet != null) {
					dbResultSet.close();
				}
				if (dbStatement != null) {
					dbStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (GetPortfolios)....." + ex);
			}
		}
		
		// Return the portfolios list
		return portfoliosList;
	}

	// ##### Query the PORTFOLIO table to retrieve the portfolio object
	// ##### Types: "MANUAL", "AUTOMATIC"
	public static Portfolio GetPortfolio (int portfolioId, int stockId, String type) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		ResultSet dbResultSet = null;
		String dbSqlPortfolioSelect = "";
		
		if (portfolioId != -1) {
			// Initialise the query for retrieving the portfolio for a given PORTFOLIO_ID
			dbSqlPortfolioSelect = "SELECT PORTFOLIO_ID, STOCK_ID, TYPE, QUANTITY, DATE_PURCHASED, PRICE_PURCHASED, DATE_SOLD, PRICE_SOLD FROM PORTFOLIO WHERE PORTFOLIO_ID = " + portfolioId;
		}
		else if (stockId != -1) {
			// Initialise the query for retrieving the portfolio for a given STOCK_ID
			dbSqlPortfolioSelect = "SELECT PORTFOLIO_ID, STOCK_ID, TYPE, QUANTITY, DATE_PURCHASED, PRICE_PURCHASED, DATE_SOLD, PRICE_SOLD FROM PORTFOLIO WHERE STOCK_ID = " + stockId + " AND TYPE = '" + type + "'";
		}
		else {
			System.out.println("ERROR occurred (GetPortfolio).....Invalid stockId and portfolioId : " + stockId + "," + portfolioId);
		}

		// Instantiate the portfolio object
		Portfolio portfolio = new Portfolio();
		
		// Create a mysql connection, run the select statement and set the properties of the portfolio object
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbStatement = dbConnection.createStatement();
			dbResultSet = dbStatement.executeQuery(dbSqlPortfolioSelect);
			
			if (dbResultSet.next()) {
				portfolio.id = dbResultSet.getInt(1);
				portfolio.stockId = dbResultSet.getInt(2);
				portfolio.type = dbResultSet.getString(3);
				portfolio.quantity = dbResultSet.getInt(4);
				portfolio.datePurchased = dbResultSet.getTimestamp(5);
				portfolio.pricePurchased = dbResultSet.getDouble(6);
				portfolio.dateSold = dbResultSet.getTimestamp(7);
				portfolio.priceSold = dbResultSet.getDouble(8);
			}
			else {
				portfolio.id = -1;
			}
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (GetPortfolio)....." + portfolioId + "....." + ex);
		}
		finally {
			try {
				if (dbResultSet != null) {
					dbResultSet.close();
				}
				if (dbStatement != null) {
					dbStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (GetPortfolio)....." + portfolioId + "....." + ex);
			}
		}
		
		// Return the portfolio object
		return portfolio;
	}

	// ##### Insert a record into the PORTFOLIO table
	public static boolean InsertPortfolio (Portfolio portfolio) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		PreparedStatement dbPreparedStatement = null;

		// Initialise the query for inserting the portfolio record
		String dbSqlPortfolioInsert = "INSERT INTO PORTFOLIO (STOCK_ID, TYPE, QUANTITY, DATE_PURCHASED, PRICE_PURCHASED) VALUES (?, ?, ?, ?, ?)";

		// Convert the purchased date into SQL timestamp format
		java.sql.Timestamp purchasedDateSql = new java.sql.Timestamp(portfolio.datePurchased.getTime());

		// Create a mysql connection and run the insert statement
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbPreparedStatement = dbConnection.prepareStatement(dbSqlPortfolioInsert);
			dbPreparedStatement.setInt(1, portfolio.stockId);
			dbPreparedStatement.setString(2, portfolio.type);
			dbPreparedStatement.setInt(3, portfolio.quantity);
			dbPreparedStatement.setTimestamp(4, purchasedDateSql);
			dbPreparedStatement.setDouble(5, portfolio.pricePurchased);
			dbPreparedStatement.execute();
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (InsertPortfolio)....." + portfolio.stockId + "....." + ex);
		}
		finally {
			try {
				if (dbPreparedStatement != null) {
					dbPreparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (InsertPortfolio)....." + portfolio.stockId + "....." + ex);
			}
		}
	
		// Return true
		return true;
	}

	// ##### Update the record in the PORTFOLIO table for the input portfolio object
	public static boolean UpdatePortfolio (Portfolio portfolio) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		PreparedStatement dbPreparedStatement = null;

		// Initialise the query for updating the PORTFOLIO record
		String dbSqlPortfolioUpdate = "UPDATE PORTFOLIO SET STOCK_ID = ?, TYPE = ?, QUANTITY = ?, DATE_PURCHASED = ?, PRICE_PURCHASED = ?, DATE_SOLD = ?, PRICE_SOLD = ? WHERE PORTFOLIO_ID = ?";

		// Convert the purchased date and sold date into SQL timestamp format
		java.sql.Timestamp purchasedDateSql = new java.sql.Timestamp(portfolio.datePurchased.getTime());
		java.sql.Timestamp soldDateSql = new java.sql.Timestamp(portfolio.dateSold.getTime());

		// Create a mysql connection and run the update statement
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbPreparedStatement = dbConnection.prepareStatement(dbSqlPortfolioUpdate);
			dbPreparedStatement.setInt(1, portfolio.stockId);
			dbPreparedStatement.setString(2, portfolio.type);
			dbPreparedStatement.setInt(3, portfolio.quantity);
			dbPreparedStatement.setTimestamp(4, purchasedDateSql);
			dbPreparedStatement.setDouble(5, portfolio.pricePurchased);
			dbPreparedStatement.setTimestamp(6, soldDateSql);
			dbPreparedStatement.setDouble(7, portfolio.priceSold);
			dbPreparedStatement.setInt(8, portfolio.id);
			dbPreparedStatement.execute();
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (UpdatePortfolio)....." + portfolio.id + "....." + ex);
		}
		finally {
			try {
				if (dbPreparedStatement != null) {
					dbPreparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (UpdatePortfolio)....." + portfolio.id + "....." + ex);
			}
		}
	
		// Return true
		return true;
	}

	// ##### Get the symbols from the STOCKS table
	public static List<List<String>> GetSymbolsGroups () {
		
		// Initialise the symbol lists
		String symbol = "";
		List<String> symbolsList = new ArrayList<String>();
		List<List<String>> symbolsGroupsList = new ArrayList<List<String>>();
		
		// Call the method to get the active stocks
		List<Stock> activeStocksList = StockQuotesDBOperations.GetStocks("ACTIVE");

		// Add the stock symbol to the sybols list and populate the symbols groups list
		int i = 0;
		for (Stock stock : activeStocksList) {
			symbol = stock.symbol;
			symbolsList.add(symbol);

			i += 1;
			if (i == Constants.SYMBOLS_GROUP_LIMIT) {
				symbolsGroupsList.add(symbolsList);
				symbolsList = new ArrayList<String>();
				i = 0;
			}
		}
		
		// Return the symbols groups list
		return symbolsGroupsList;
	}

	// ##### Get the STOCKS
	// ##### Modes: "ALL", "ACTIVE", "WATCHED", "PRICE_CHANGE"
	public static List<Stock> GetStocks (String mode) {

		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		ResultSet dbResultSet = null;
		String dbSqlStocksSelect = "";

		if (mode.equals("ALL")) {
			// Initialise the query for retrieving all the stocks
			dbSqlStocksSelect = "SELECT STOCK_ID FROM STOCKS";
		}
		else if (mode.equals("ACTIVE")) {
			// Initialise the query for retrieving the active stocks
			dbSqlStocksSelect = "SELECT STOCK_ID FROM STOCKS WHERE ACTIVE = 'Y'";
		}
		else if (mode.equals("WATCHED")) {
			// Initialise the query for retrieving the active watched stocks
			dbSqlStocksSelect = "SELECT STOCK_ID FROM STOCKS WHERE ACTIVE = 'Y' AND WATCH = 'Y'";
		}
		else if (mode.equals("PRICE_CHANGE")) {
			// Initialise the query for retrieving the price change stocks
			dbSqlStocksSelect = "SELECT S.STOCK_ID FROM STOCKS S, QUOTES Q WHERE S.STOCK_ID = Q.STOCK_ID AND S.ACTIVE = 'Y'";
			dbSqlStocksSelect += " AND Q.DATE_TRADE = (SELECT MAX(Q2.DATE_TRADE) FROM QUOTES Q2 WHERE Q2.STOCK_ID = S.STOCK_ID)";
			dbSqlStocksSelect += " AND Q.PRICE_CHANGE_PERCENT >= " + Constants.PRICE_CHANGE_PERCENT_THRESHOLD;
			dbSqlStocksSelect += " ORDER BY Q.PRICE_CHANGE_PERCENT DESC";
		}
		else {
			System.out.println("ERROR occurred (GetStocks).....Invalid mode : " + mode);
		}

		// Initialise the stocks lists
		String symbol = "";
		List<Stock> stocksList = new ArrayList<Stock>();

		// Create a mysql connection, run the query and populate the stocks list
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbStatement = dbConnection.createStatement();
			dbResultSet = dbStatement.executeQuery(dbSqlStocksSelect);
			
			while (dbResultSet.next()) {
				// Get the stock id
				int stockId = dbResultSet.getInt(1);

				// Call the method to get the stock object
				Stock stock = GetStock(stockId, null);

				// Add the stock object to the stocks list
				stocksList.add(stock);
			}
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (GetStocks)....." + ex);
		}
		finally {
			try {
				if (dbResultSet != null) {
					dbResultSet.close();
				}
				if (dbStatement != null) {
					dbStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (GetStocks)....." + ex);
			}
		}
		
		// Return the stocks list
		return stocksList;
	}
	
	// ##### Return the stock object from the STOCKS table using the stock id OR the symbol
	public static Stock GetStock (int stockId, String symbol) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		ResultSet dbResultSet = null;
		String dbSqlStocksSelect = "";

		if (stockId != -1) {
			// Initialise the query for retrieving the stock for the stock id
			dbSqlStocksSelect = "SELECT STOCK_ID, SYMBOL, NAME, CURRENCY, SHARES_ISSUED, DATE_CREATED, DATE_UPDATED, ACTIVE, WATCH FROM STOCKS WHERE STOCK_ID = " + stockId;
		}
		else if (symbol != null) {
			// Initialise the query for retrieving the stock for the symbol
			dbSqlStocksSelect = "SELECT STOCK_ID, SYMBOL, NAME, CURRENCY, SHARES_ISSUED, DATE_CREATED, DATE_UPDATED, ACTIVE, WATCH FROM STOCKS WHERE SYMBOL = '" + symbol + "'";
		}
		else {
			System.out.println("ERROR occurred (GetStock).....Invalid stockId and symbol : " + stockId + "," + symbol);
		}

		// Instantiate the stock object
		Stock stock = new Stock();
		
		// Create a mysql connection and set the stock object properties
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbStatement = dbConnection.createStatement();
			dbResultSet = dbStatement.executeQuery(dbSqlStocksSelect);
			
			if (dbResultSet.next()) {
				stock.id = dbResultSet.getInt(1);
				stock.symbol = dbResultSet.getString(2);
				stock.name = dbResultSet.getString(3);
				stock.currency = dbResultSet.getString(4);
				stock.sharesIssued = dbResultSet.getLong(5);
				stock.dateCreated = dbResultSet.getTimestamp(6);
				stock.dateUpdated = dbResultSet.getTimestamp(7);
				stock.active = dbResultSet.getString(8).charAt(0);
				stock.watch = dbResultSet.getString(9).charAt(0);
			}
			else {
				stock.id = -1;
				stock.symbol = symbol;
				stock.name = "";
				stock.currency = "";
			}
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (GetStock)....." + symbol + "....." + ex);
		}
		finally {
			try {
				if (dbResultSet != null) {
					dbResultSet.close();
				}
				if (dbStatement != null) {
					dbStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (GetStock)....." + symbol + "....." + ex);
			}
		}
		
		// Return the stock object
		return stock;
	}

	// ##### Insert a record into the STOCKS table
	public static boolean InsertStock (Stock stock) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		PreparedStatement dbPreparedStatement = null;

		// Initialise the query for inserting the stock record
		String dbSqlStocksInsert = "INSERT INTO STOCKS (SYMBOL, NAME, CURRENCY, SHARES_ISSUED, DATE_CREATED, DATE_UPDATED) VALUES (?, ?, ?, ?, ?, ?)";

		// Set the current date as a SQL timestamp
		java.sql.Timestamp currentDateSql = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

		// Create a mysql connection and run the insert statement
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbPreparedStatement = dbConnection.prepareStatement(dbSqlStocksInsert);
			dbPreparedStatement.setString(1, stock.symbol);
			dbPreparedStatement.setString(2, stock.name);
			dbPreparedStatement.setString(3, stock.currency);
			dbPreparedStatement.setLong(4, stock.sharesIssued);
			dbPreparedStatement.setTimestamp(5, currentDateSql);
			dbPreparedStatement.setTimestamp(6, currentDateSql);
			dbPreparedStatement.execute();
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (InsertStock)....." + stock.symbol + "....." + ex);
		}
		finally {
			try {
				if (dbPreparedStatement != null) {
					dbPreparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (InsertStock)....." + stock.symbol + "....." + ex);
			}
		}
	
		// Return true
		return true;
	}

	// ##### Update the record in the STOCKS table for the input stock object
	public static boolean UpdateStock (Stock stock) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		PreparedStatement dbPreparedStatement = null;

		// Initialise the query for updating the STOCKS record
		String dbSqlStocksUpdate = "UPDATE STOCKS SET NAME = ?, CURRENCY = ?, SHARES_ISSUED = ?, DATE_UPDATED = ?, ACTIVE = ?, WATCH = ? WHERE STOCK_ID = ?";

		// Set the current date as a SQL timestamp
		java.sql.Timestamp currentDateSql = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

		// Create a mysql connection and run the update statement
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbPreparedStatement = dbConnection.prepareStatement(dbSqlStocksUpdate);
			dbPreparedStatement.setString(1, stock.name);
			dbPreparedStatement.setString(2, stock.currency);
			dbPreparedStatement.setLong(3, stock.sharesIssued);
			dbPreparedStatement.setTimestamp(4, currentDateSql);
			dbPreparedStatement.setString(5, String.valueOf(stock.active));
			dbPreparedStatement.setString(6, String.valueOf(stock.watch));
			dbPreparedStatement.setInt(7, stock.id);
			dbPreparedStatement.execute();
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (UpdateStock)....." + stock.id + "....." + ex);
		}
		finally {
			try {
				if (dbPreparedStatement != null) {
					dbPreparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (UpdateStock)....." + stock.id + "....." + ex);
			}
		}
	
		// Return true
		return true;
	}

	// ##### Query the QUOTES table to retrieve the quote object for the input STOCK_ID
	// ##### Modes: "TRADE_DATE", "LATEST"
	public static Quote GetQuote (int stockId, String mode, Date tradeDate) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		ResultSet dbResultSet = null;
		String dbSqlQuotesSelect = "";

		if (mode.equals("TRADE_DATE")) {
			// Convert the trade date into a string in the mysql format
			String tradeDateSql = Constants.MYSQL_SIMPLE_DATE_FORMAT.format(tradeDate);

			// Initialise the query for retrieving the quote for a given trade date
			dbSqlQuotesSelect = "SELECT STOCK_ID, DATE_TRADE, DATE_TRADE_CHANGE, PRICE, PRICE_CHANGE, PRICE_CHANGE_PERCENT, DAY_PRICE_CHANGE, DAY_PRICE_CHANGE_PERCENT, VOLUME, VOLUME_PERCENT FROM QUOTES WHERE STOCK_ID = " + stockId + " AND DATE_TRADE = '" + tradeDateSql + "'";
		}
		else if (mode.equals("LATEST")) {
			// Initialise the query for retrieving the quotes for the maximum trade date
			dbSqlQuotesSelect = "SELECT STOCK_ID, DATE_TRADE, DATE_TRADE_CHANGE, PRICE, PRICE_CHANGE, PRICE_CHANGE_PERCENT, DAY_PRICE_CHANGE, DAY_PRICE_CHANGE_PERCENT, VOLUME, VOLUME_PERCENT FROM QUOTES WHERE STOCK_ID = " + stockId + " AND DATE_TRADE = (SELECT MAX(DATE_TRADE) FROM QUOTES WHERE STOCK_ID = " + stockId + ")";
		}
		else {
			System.out.println("ERROR occurred (GetQuote).....Invalid mode : " + mode);
		}

		// Instantiate the quote object
		Quote quote = new Quote();
		
		// Create a mysql connection, run the select statement and set the properties of the quote object
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbStatement = dbConnection.createStatement();
			dbResultSet = dbStatement.executeQuery(dbSqlQuotesSelect);
			
			if (dbResultSet.next()) {
				quote.stockId = dbResultSet.getInt(1);
				quote.dateTrade = dbResultSet.getTimestamp(2);
				quote.dateTradeChange = dbResultSet.getInt(3);
				quote.price = dbResultSet.getDouble(4);
				quote.priceChange = dbResultSet.getDouble(5);
				quote.priceChangePercent = dbResultSet.getDouble(6);
				quote.dayPriceChange = dbResultSet.getDouble(7);
				quote.dayPriceChangePercent = dbResultSet.getDouble(8);
				quote.volume = dbResultSet.getInt(9);
				quote.volumePercent = dbResultSet.getDouble(10);
			}
			else {
				quote.stockId = -1;
			}
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (GetQuote)....." + stockId + "....." + ex);
		}
		finally {
			try {
				if (dbResultSet != null) {
					dbResultSet.close();
				}
				if (dbStatement != null) {
					dbStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (GetQuote)....." + stockId + "....." + ex);
			}
		}
		
		// Return the quote object
		return quote;
	}

	// ##### Insert a record into the QUOTES table given the input quote object
	public static boolean InsertQuote (Quote quote) {
		
		// Initialise the mysql database connection objects
		Connection dbConnection = null;
		Statement dbStatement = null;
		PreparedStatement dbPreparedStatement = null;

		// Initialise the insert query for inserting itno the QUOTES table
		String dbSqlQuotesInsert = "INSERT INTO QUOTES (STOCK_ID, DATE_TRADE, DATE_TRADE_CHANGE, PRICE, PRICE_CHANGE, PRICE_CHANGE_PERCENT, DAY_PRICE_CHANGE, DAY_PRICE_CHANGE_PERCENT, VOLUME, VOLUME_PERCENT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		// Convert the trade date into SQL timestamp format
		java.sql.Timestamp tradeDateSql = new java.sql.Timestamp(quote.dateTrade.getTime());

		// Create the mysql connection and run the insert statement
		try {
			dbConnection = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
			dbPreparedStatement = dbConnection.prepareStatement(dbSqlQuotesInsert);
			dbPreparedStatement.setInt(1, quote.stockId);
			dbPreparedStatement.setTimestamp(2, tradeDateSql);
			dbPreparedStatement.setInt(3, quote.dateTradeChange);
			dbPreparedStatement.setDouble(4, quote.price);
			dbPreparedStatement.setDouble(5, quote.priceChange);
			dbPreparedStatement.setDouble(6, quote.priceChangePercent);
			dbPreparedStatement.setDouble(7, quote.dayPriceChange);
			dbPreparedStatement.setDouble(8, quote.dayPriceChangePercent);
			dbPreparedStatement.setLong(9, quote.volume);
			dbPreparedStatement.setDouble(10, quote.volumePercent);
			dbPreparedStatement.execute();
		}
		catch (SQLException ex) {
			System.out.println("ERROR occurred (InsertQuote)....." + quote.stockId + "....." + ex);
		}
		finally {
			try {
				if (dbPreparedStatement != null) {
					dbPreparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			}
			catch (SQLException ex) {
				System.out.println("ERROR occurred (InsertQuote)....." + quote.stockId + "....." + ex);
			}
		}
	
		// Return true
		return true;
	}
}

