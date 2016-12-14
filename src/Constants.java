/*
 * Constants.java
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Constants {
	
	// ##### Get Yahoo Stock Quotes
	public final static int SYMBOLS_GROUP_LIMIT = 100;
	public final static int HTTP_CONNECTION_RETRY_ATTEMPTS = 10;

	// ##### Decimal Formats
	public final static DecimalFormat PERCENT_DECIMAL_FORMAT = new DecimalFormat("##0.00");
	
	// ##### Date formats
	public final static SimpleDateFormat MYSQL_SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat YAHOO_SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mma");
	
	// ##### Date limits
	public final static long DATE_UPDATED_LIMIT_IN_MILLISECONDS = 5 * 24 * 60 * 60 * 1000;	// 5 days
	public final static long DATE_TRADE_LIMIT_IN_MILLISECONDS = 6 * 60 * 60 * 1000;			// 6 hours
	public final static long DATE_SOLD_LIMIT_IN_MILLISECONDS = 24 * 60 * 60 * 1000;			// 24 hours

	// ##### Stock Picker thresholds and limits
	public final static double PRICE_CHANGE_PERCENT_THRESHOLD = 10.0;
	public final static double PRICE_CHANGE_PERCENT_MINIMUM = 5.0;
	public final static double VOLUME_PERCENT_THRESHOLD = 1.0;
	public final static double PRICE_CHANGE_PERCENT_SELL_UPPER_THRESHOLD = 10.0;
	public final static double PRICE_CHANGE_PERCENT_SELL_LOWER_THRESHOLD = -5.0;
	public final static double PORTFOLIO_BUY_AMOUNT = 1000.0;
	
	// ##### WhatsApp phone numbers
	public final static String[] WHATSAPP_PHONE_NUMBERS = {"4401234567890"};
}

