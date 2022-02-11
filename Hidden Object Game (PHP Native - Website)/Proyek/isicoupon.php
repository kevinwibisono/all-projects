<?php
	include "conn.php";
	$satuan = 0;
	$puluhan = 0;
	$ratusan = 0;
	$kode = 'COP100'.$ratusan.''.$puluhan.''.$satuan;
	
	$sql = "INSERT INTO coupon VALUES('$kode',200,'open')";
	$cek = 0;
	while($cek == 0){
		mysqli_query($link,$sql);
		$satuan++;
		if($satuan>9){
			$puluhan++;
			$satuan = 0;
		}
		if($puluhan>9){
			$ratusan++;
			$puluhan = 0;
			$satuan = 0;
		}
		
		if($ratusan>9){
			$cek = 1;
			echo "<script>alert('done')</script>";
		}
		$kode = 'COP100'.$ratusan.''.$puluhan.''.$satuan;
		$sql = "INSERT INTO coupon VALUES('$kode',100,'open')";
	}
?>
