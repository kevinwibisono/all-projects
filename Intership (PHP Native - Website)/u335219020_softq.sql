-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 29, 2020 at 11:57 AM
-- Server version: 10.1.28-MariaDB
-- PHP Version: 7.1.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `u335219020_softq`
--
CREATE DATABASE IF NOT EXISTS `u335219020_softq` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `u335219020_softq`;

-- --------------------------------------------------------

--
-- Table structure for table `master_staff`
--

CREATE TABLE `master_staff` (
  `staff_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `staff_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Password` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `staff_position` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `flgAsAdmin` int(11) NOT NULL DEFAULT '0' COMMENT 'sisakan 1 record Admin utk bisa masuk ke sistem',
  `project_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `waktuproses` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `master_staff`
--

INSERT INTO `master_staff` (`staff_id`, `staff_name`, `Password`, `staff_position`, `flgAsAdmin`, `project_id`, `operator`, `waktuproses`) VALUES
('ST01', 'Super Admin', 'administrator', 'Administrator', 1, '', '', '0000-00-00 00:00:00'),
('ST010', 'Hendra Lukito', 'sales01', 'Sales Manager', 0, '', 'Super Admin', '2020-07-20 10:03:51'),
('ST02', 'Regular Staff', 'regular', 'Staff', 0, '', '', '0000-00-00 00:00:00'),
('ST04', 'Member', 'asdf', 'Project Member', 0, '', 'Super Admin', '2020-07-01 06:24:27'),
('ST05', 'Reyhan Wibisono', 'netprogrammer01', 'Programmer', 0, '', 'Super Admin', '2020-07-20 10:01:54'),
('ST06', 'Stefan William', 'netprogrammer03', 'Programmer', 0, '', 'Super Admin', '2020-07-20 10:02:22'),
('ST07', 'Kevin', 'alex', 'Magang', 0, '', 'Super Admin', '2020-07-20 09:54:10'),
('ST08', 'Puri Ayu', 'accounting', 'Accounting Admin', 0, '', 'Super Admin', '2020-07-20 10:02:51'),
('ST09', 'Budianto Tjendra', 'general', 'Direktur', 1, '', 'Super Admin', '2020-07-20 10:03:23');

-- --------------------------------------------------------

--
-- Table structure for table `project_adjust`
--

CREATE TABLE `project_adjust` (
  `adjust_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sub_detail_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `adjust_duration` int(11) NOT NULL,
  `adjust_comment` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `waktuproses` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `project_adjust`
--

INSERT INTO `project_adjust` (`adjust_id`, `sub_detail_id`, `adjust_duration`, `adjust_comment`, `operator`, `waktuproses`) VALUES
('AD02', 'SD07', 2, 'testing', 'Super Admin', '2020-07-15 11:43:53');

-- --------------------------------------------------------

--
-- Table structure for table `project_detail_l1`
--

CREATE TABLE `project_detail_l1` (
  `project_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `detail_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `detail_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `detail_start` date NOT NULL,
  `detail_end` date NOT NULL,
  `predecessor` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `step` int(11) NOT NULL,
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `waktuproses` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `project_detail_l1`
--

INSERT INTO `project_detail_l1` (`project_id`, `detail_id`, `detail_name`, `detail_start`, `detail_end`, `predecessor`, `step`, `operator`, `waktuproses`) VALUES
('FYF_SS', 'DT010', 'PEMBAYARAN', '2020-07-01', '2020-07-31', 'DT09', 3, 'Super Admin', '2020-07-20 10:50:17'),
('FYF_SS', 'DT011', 'LAPORAN', '2020-07-13', '2020-07-24', 'DT010', 4, 'Budianto Tjendra', '2020-07-22 08:23:40'),
('FYF_SS', 'DT012', 'KEUANGAN', '2020-07-23', '2020-07-31', 'DT013', 6, 'Super Admin', '2020-07-20 13:42:54'),
('FYF_SS', 'DT013', 'STOK', '2020-07-20', '2020-07-31', 'DT011', 5, 'Budianto Tjendra', '2020-07-22 08:24:55'),
('FYF_SS', 'DT014', 'DASHBOARD', '2020-07-20', '2020-07-31', 'DT012', 7, 'Super Admin', '2020-07-20 11:37:00'),
('FYF_SS', 'DT08', 'MASTER', '2020-07-01', '2020-07-31', '', 1, 'Super Admin', '2020-07-20 11:06:22'),
('FYF_SS', 'DT09', 'TRANSAKSI', '2020-07-13', '2020-07-24', 'DT08', 2, 'Super Admin', '2020-07-20 11:19:43'),
('NEW', 'DT015', 'Master', '2020-07-01', '2020-07-01', '', 1, 'Super Admin', '2020-07-28 14:12:59');

-- --------------------------------------------------------

--
-- Table structure for table `project_detail_l2`
--

CREATE TABLE `project_detail_l2` (
  `detail_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sub_detail_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sub_detail_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sub_detail_start` date NOT NULL,
  `sub_detail_end` date NOT NULL,
  `staff_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `staff_note` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `flag` int(11) NOT NULL DEFAULT '0',
  `predecessor` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `step` int(11) NOT NULL,
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `waktuproses` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `project_detail_l2`
--

INSERT INTO `project_detail_l2` (`detail_id`, `sub_detail_id`, `sub_detail_name`, `sub_detail_start`, `sub_detail_end`, `staff_id`, `staff_note`, `flag`, `predecessor`, `step`, `operator`, `waktuproses`) VALUES
('DT010', 'SD028', 'PEMBAYARAN HUTANG', '2020-07-13', '2020-07-15', 'ST05', '', 0, 'SD019', 3, 'Super Admin', '2020-07-20 10:50:47'),
('DT010', 'SD029', 'PEMBAYARAN PIUTANG', '2020-07-16', '2020-07-20', 'ST05', '', 0, 'SD028', 4, 'Super Admin', '2020-07-20 10:51:31'),
('DT011', 'SD030', 'REKAP  PEMBELIAN', '2020-07-16', '2020-07-21', 'ST05', '', 0, 'SD034', 18, 'Budianto Tjendra', '2020-07-22 08:23:48'),
('DT011', 'SD031', 'REKAP PENJUALAN', '2020-07-16', '2020-07-23', 'ST05', '', 0, 'SD030', 19, 'Budianto Tjendra', '2020-07-22 08:24:07'),
('DT011', 'SD032', 'LAPORAN PENGIRIMAN', '2020-07-22', '2020-07-24', 'ST05', '', 0, 'SD031', 20, 'Budianto Tjendra', '2020-07-22 08:24:23'),
('DT012', 'SD035', 'PEMBIAYAAN', '2020-07-23', '2020-07-23', 'ST05', '', 0, 'SD047', 22, 'Budianto Tjendra', '2020-07-22 08:25:54'),
('DT012', 'SD036', 'LABA RUGI', '2020-07-28', '2020-07-31', 'ST05', '', 0, 'SD035', 23, 'Budianto Tjendra', '2020-07-22 08:26:15'),
('DT013', 'SD037', 'KARTU STOK', '2020-07-27', '2020-07-28', 'ST05', '', 0, 'SD022', 26, 'Budianto Tjendra', '2020-07-22 08:25:08'),
('DT013', 'SD038', 'PERSEDIAAN AWAL', '2020-07-27', '2020-07-28', 'ST05', '', 0, 'SD037', 27, 'Budianto Tjendra', '2020-07-22 08:25:16'),
('DT014', 'SD047', 'Analisa Stok', '2020-07-20', '2020-07-20', '', '', 0, 'SD032', 21, 'Super Admin', '2020-07-20 11:30:17'),
('DT015', 'SD048', 'Master Kategori', '2020-07-01', '2020-07-01', '', '', 0, '', 1, 'Super Admin', '2020-07-28 14:13:07'),
('DT08', 'SD013', 'SUPPLIER', '2020-07-01', '2020-07-02', 'ST05', '', 0, '', 1, 'Super Admin', '2020-07-21 11:24:49'),
('DT08', 'SD014', 'CUSTOMER', '2020-07-01', '2020-07-02', 'ST05', '', 0, 'SD029', 5, 'Super Admin', '2020-07-20 10:52:18'),
('DT08', 'SD015', 'BARANG', '2020-07-06', '2020-07-08', 'ST05', '', 0, 'SD014', 6, 'Super Admin', '2020-07-20 10:52:49'),
('DT08', 'SD016', 'PRICE LIST', '2020-07-08', '2020-07-10', 'ST05', '', 0, 'SD017', 8, 'Super Admin', '2020-07-20 10:54:34'),
('DT08', 'SD017', 'KATEGORI', '2020-07-06', '2020-07-06', 'ST05', '', 0, 'SD015', 7, 'Super Admin', '2020-07-20 10:55:02'),
('DT08', 'SD018', 'PROMO', '2020-07-10', '2020-07-13', 'ST05', '', 0, 'SD016', 9, 'Super Admin', '2020-07-20 11:19:06'),
('DT08', 'SD019', 'PERKIRAAN', '2020-07-01', '2020-07-01', '', '', 0, 'SD013', 2, 'Super Admin', '2020-07-20 04:53:31'),
('DT08', 'SD020', 'SALES', '2020-07-08', '2020-07-08', 'ST05', '', 0, 'SD018', 10, 'Super Admin', '2020-07-20 11:08:38'),
('DT08', 'SD021', 'HAK AKSES', '2020-07-27', '2020-07-27', 'ST05', '', 0, 'SD036', 24, 'Super Admin', '2020-07-20 11:07:10'),
('DT08', 'SD022', 'USER', '2020-07-27', '2020-07-27', 'ST05', '', 0, 'SD021', 25, 'Super Admin', '2020-07-20 11:06:42'),
('DT09', 'SD023', 'PENJUALAN', '2020-07-14', '2020-07-15', 'ST05', '', 0, 'SD025', 13, 'Super Admin', '2020-07-20 11:22:00'),
('DT09', 'SD024', 'ORDER PEMBELIAN', '2020-07-14', '2020-07-15', 'ST05', '', 0, 'SD020', 11, 'Super Admin', '2020-07-20 11:20:12'),
('DT09', 'SD025', 'PEMBELIAN', '2020-07-14', '2020-07-15', 'ST05', '', 0, 'SD024', 12, 'Super Admin', '2020-07-20 11:21:22'),
('DT09', 'SD026', 'RETUR BELI', '2020-07-16', '2020-07-16', 'ST05', '', 0, 'SD027', 15, 'Super Admin', '2020-07-20 11:24:03'),
('DT09', 'SD027', 'RETUR JUAL', '2020-07-15', '2020-07-15', 'ST05', '', 0, 'SD023', 14, 'Super Admin', '2020-07-20 11:22:25'),
('DT09', 'SD033', 'SURAT JALAN', '2020-07-13', '2020-07-13', '', '', 0, 'SD026', 16, 'Super Admin', '2020-07-20 11:19:37'),
('DT09', 'SD034', 'RUTE KIRIM', '2020-07-13', '2020-07-13', '', '', 0, 'SD033', 17, 'Super Admin', '2020-07-20 11:19:37');

-- --------------------------------------------------------

--
-- Table structure for table `project_files`
--

CREATE TABLE `project_files` (
  `file_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `project_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `filename` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `waktuproses` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `project_header`
--

CREATE TABLE `project_header` (
  `project_id` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `project_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `project_total` decimal(10,0) NOT NULL,
  `start_date` date NOT NULL,
  `project_duration` int(11) NOT NULL COMMENT '(In Days)',
  `domain_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `domain_admin` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `project_header`
--

INSERT INTO `project_header` (`project_id`, `project_name`, `project_total`, `start_date`, `project_duration`, `domain_name`, `domain_admin`) VALUES
('FYF_SS', 'PT. FYF', '30000000', '2020-07-01', 60, '', ''),
('NEW', 'New Project', '1000', '2020-07-01', 10, '', ''),
('PR2020026', 'Trial Project', '12000000', '2020-07-29', 60, 'https://calendar.databasket.online/project.php', 'https://calendar.databasket.online/project.php');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `master_staff`
--
ALTER TABLE `master_staff`
  ADD PRIMARY KEY (`staff_id`);

--
-- Indexes for table `project_adjust`
--
ALTER TABLE `project_adjust`
  ADD PRIMARY KEY (`adjust_id`);

--
-- Indexes for table `project_detail_l1`
--
ALTER TABLE `project_detail_l1`
  ADD PRIMARY KEY (`project_id`,`detail_id`);

--
-- Indexes for table `project_detail_l2`
--
ALTER TABLE `project_detail_l2`
  ADD PRIMARY KEY (`detail_id`,`sub_detail_id`);

--
-- Indexes for table `project_files`
--
ALTER TABLE `project_files`
  ADD PRIMARY KEY (`file_id`);

--
-- Indexes for table `project_header`
--
ALTER TABLE `project_header`
  ADD PRIMARY KEY (`project_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
