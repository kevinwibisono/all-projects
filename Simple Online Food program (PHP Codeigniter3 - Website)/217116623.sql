-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 03, 2019 at 03:44 PM
-- Server version: 10.4.6-MariaDB
-- PHP Version: 7.1.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `217116623`
--
CREATE DATABASE IF NOT EXISTS `217116623` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `217116623`;

-- --------------------------------------------------------

--
-- Table structure for table `alamat`
--

CREATE TABLE `alamat` (
  `alamat` varchar(100) NOT NULL,
  `tag` varchar(30) NOT NULL,
  `email_cust` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `dtrans`
--

CREATE TABLE `dtrans` (
  `makanan` varchar(100) NOT NULL,
  `harga` int(10) NOT NULL,
  `jumlah` int(10) NOT NULL,
  `id_trans` int(10) NOT NULL,
  `gambar` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `htrans`
--

CREATE TABLE `htrans` (
  `alamat_dari` varchar(100) NOT NULL,
  `alamat_tujuan` varchar(100) NOT NULL,
  `email_merchant` varchar(100) NOT NULL,
  `nama_mechant` varchar(30) NOT NULL,
  `total` int(30) NOT NULL,
  `email_cust` varchar(100) NOT NULL,
  `nama_cust` varchar(100) NOT NULL,
  `id_trans` int(10) NOT NULL,
  `status` varchar(30) NOT NULL,
  `tglpengiriman` varchar(30) NOT NULL,
  `tglsampai` varchar(30) NOT NULL,
  `email_driver` varchar(100) NOT NULL,
  `nama_driver` varchar(100) NOT NULL,
  `gambar_driver` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `keranjang`
--

CREATE TABLE `keranjang` (
  `makanan` varchar(100) NOT NULL,
  `harga` int(30) NOT NULL,
  `jumlah` int(16) NOT NULL,
  `subtotal` int(30) NOT NULL,
  `email_cust` varchar(100) NOT NULL,
  `gambar` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `makanan`
--

CREATE TABLE `makanan` (
  `nama` varchar(30) NOT NULL,
  `harga` int(11) NOT NULL,
  `deskripsi` varchar(500) NOT NULL,
  `email_merch` varchar(100) NOT NULL,
  `status` varchar(10) NOT NULL,
  `gambar` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `email` varchar(100) NOT NULL,
  `nama` varchar(30) NOT NULL,
  `no_telp` varchar(16) NOT NULL,
  `password` varchar(30) NOT NULL,
  `saldo` int(11) NOT NULL,
  `role` varchar(10) NOT NULL,
  `gambar` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`email`, `nama`, `no_telp`, `password`, `saldo`, `role`, `gambar`) VALUES
('admin', 'admin', 'admin', 'admin', 0, 'admin', '');

-- --------------------------------------------------------

--
-- Table structure for table `video`
--

CREATE TABLE `video` (
  `nama` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
