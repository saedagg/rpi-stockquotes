-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Dec 14, 2016 at 03:42 PM
-- Server version: 5.5.53-0+deb8u1
-- PHP Version: 5.6.27-0+deb8u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `STOCKQUOTESDB`
--

-- --------------------------------------------------------

--
-- Table structure for table `JOBS`
--

CREATE TABLE IF NOT EXISTS `JOBS` (
`JOB_ID` int(10) unsigned NOT NULL,
  `NAME` varchar(30) NOT NULL,
  `DATE_STARTED` datetime NOT NULL,
  `DATE_FINISHED` datetime NOT NULL,
  `ELAPSED_TIME` int(10) unsigned NOT NULL,
  `ROWS_PROCESSED` int(10) unsigned NOT NULL DEFAULT '0',
  `ROWS_INSERTED` int(10) unsigned NOT NULL DEFAULT '0',
  `ROWS_UPDATED` int(10) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `PORTFOLIO`
--

CREATE TABLE IF NOT EXISTS `PORTFOLIO` (
`PORTFOLIO_ID` int(10) unsigned NOT NULL,
  `STOCK_ID` int(10) unsigned NOT NULL,
  `TYPE` varchar(10) NOT NULL,
  `QUANTITY` int(10) unsigned NOT NULL,
  `DATE_PURCHASED` datetime NOT NULL,
  `PRICE_PURCHASED` decimal(10,4) NOT NULL DEFAULT '0.0000',
  `DATE_SOLD` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `PRICE_SOLD` decimal(10,4) NOT NULL DEFAULT '0.0000'
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `QUOTES`
--

CREATE TABLE IF NOT EXISTS `QUOTES` (
  `STOCK_ID` int(10) unsigned NOT NULL,
  `DATE_TRADE` datetime NOT NULL,
  `DATE_TRADE_CHANGE` int(10) NOT NULL DEFAULT '0',
  `PRICE` decimal(10,4) NOT NULL DEFAULT '0.0000',
  `PRICE_CHANGE` decimal(10,4) NOT NULL DEFAULT '0.0000',
  `PRICE_CHANGE_PERCENT` decimal(5,2) NOT NULL DEFAULT '0.00',
  `DAY_PRICE_CHANGE` decimal(10,4) NOT NULL DEFAULT '0.0000',
  `DAY_PRICE_CHANGE_PERCENT` decimal(5,2) NOT NULL DEFAULT '0.00',
  `VOLUME` bigint(20) unsigned NOT NULL DEFAULT '0',
  `VOLUME_PERCENT` decimal(5,2) NOT NULL DEFAULT '0.00'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `STOCKS`
--

CREATE TABLE IF NOT EXISTS `STOCKS` (
`STOCK_ID` int(10) unsigned NOT NULL,
  `SYMBOL` varchar(10) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `CURRENCY` varchar(10) NOT NULL,
  `SHARES_ISSUED` bigint(20) unsigned NOT NULL DEFAULT '0',
  `DATE_CREATED` datetime NOT NULL,
  `DATE_UPDATED` datetime NOT NULL,
  `ACTIVE` char(1) NOT NULL DEFAULT 'Y',
  `WATCH` char(1) NOT NULL DEFAULT 'N'
) ENGINE=InnoDB AUTO_INCREMENT=6138 DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `JOBS`
--
ALTER TABLE `JOBS`
 ADD PRIMARY KEY (`JOB_ID`);

--
-- Indexes for table `PORTFOLIO`
--
ALTER TABLE `PORTFOLIO`
 ADD PRIMARY KEY (`PORTFOLIO_ID`);

--
-- Indexes for table `QUOTES`
--
ALTER TABLE `QUOTES`
 ADD PRIMARY KEY (`STOCK_ID`,`DATE_TRADE`), ADD KEY `I_DATE_TRADE` (`DATE_TRADE`);

--
-- Indexes for table `STOCKS`
--
ALTER TABLE `STOCKS`
 ADD PRIMARY KEY (`STOCK_ID`), ADD UNIQUE KEY `UI_SYMBOL` (`SYMBOL`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `JOBS`
--
ALTER TABLE `JOBS`
MODIFY `JOB_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `PORTFOLIO`
--
ALTER TABLE `PORTFOLIO`
MODIFY `PORTFOLIO_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=53;
--
-- AUTO_INCREMENT for table `STOCKS`
--
ALTER TABLE `STOCKS`
MODIFY `STOCK_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6138;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
