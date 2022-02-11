<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
	<style>
		body {
		 background-image: url("<?php echo base_url("assets/images/foodwall.jpeg")?>");
		 background-size: cover;
		 background-repeat: no-repeat;
		}
		table{
			margin: 0 auto;
		}
	</style>
</head>
<body>
	<div style="">
		<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="float: left; width: 100px; height:100px;">
		<div style="width: 800px; float: left;">
			<?php echo heading('&nbspSelamat Siang, '.$current_user, 1, array('margin-top' => 10));?>
			<?php echo heading('&nbsp&nbspMau makan apa hari ini?', 3);?>
		</div>
		<div style="text-align: right; width: 250px; float:left;">
			<?php echo br(2);?>
			Kembali ke login:
			<form method='post' action='<?=site_url("mainpage/to_login")?>'>
				<input type='submit' name='Login' value='Login'>
				<input type='hidden' name='HiddenUser' value = '<?php echo $arruser?>'>
				<input type='hidden' name='HiddenPass' value = '<?php echo $arrpass?>'>
				<input type='hidden' name='HiddenEMoney' value = '<?php echo $arremoney?>'>
				<input type='hidden' name='HiddenNama' value = '<?php echo $arrnama?>'>
				<input type='hidden' name='HiddenNoTelp' value = '<?php echo $arrnotelp?>'>
				<input type='hidden' name='HiddenAlamat' value = '<?php echo $arralamat?>'>
				<input type='hidden' name='HiddenTag' value = '<?php echo $arrtag?>'>
			</form>
		</div>
	</div>
	<div id="content" style="clear:left; background-color: white; opacity: 0.75; text-align: center; width: 1000px; margin-left: auto; margin-right:auto;">
	<hr>
	<form method='post' action='<?=site_url("mainpage/change_merchant")?>'>
		<b>Step 1:Pilih merchant</b><?php echo br(1);?>
		Merchant: <select name="ChooseMerchant" onchange="ChangeItem" id="ChooseMerchant">
		  <option value="Bakso Pak Gembul">Bakso Pak Gembul</option>
		  <option value="Warkop Pak Kumis">Warkop Pak Kumis</option>
		  <option value="Pecel Mbak Suminem">Pecel Mbak Suminem</option>
		  <option value="Nasi Goreng Ala Kadarnya">Nasi Goreng Ala Kadarnya</option>
		</select>
		<input type='submit' name='Merchant' value='Go'>
		<input type='hidden' name='HiddenUser' value = '<?php echo $arruser?>'>
		<input type='hidden' name='HiddenPass' value = '<?php echo $arrpass?>'>
		<input type='hidden' name='HiddenEMoney' value = '<?php echo $arremoney?>'>
		<input type='hidden' name='HiddenNama' value = '<?php echo $arrnama?>'>
		<input type='hidden' name='HiddenNoTelp' value = '<?php echo $arrnotelp?>'>
		<input type='hidden' name='HiddenAlamat' value = '<?php echo $arralamat?>'>
		<input type='hidden' name='HiddenTag' value = '<?php echo $arrtag?>'>
		<input type='hidden' name='CurrentEMoney' value = '<?php echo $current_emoney?>'>
		<input type='hidden' name='CurrentUser' value = '<?php echo $current_user?>'>
	</form>
	<form method='post' action='<?=site_url("mainpage/handle_pemesanan")?>'>
	<?php echo br(1);?>
	<b>Step 2:Pilih menu</b><?php echo br(1);?>
		Menu: 
		<?php 
			if($current_merchant == 'Bakso Pak Gembul'){
				$array = array(
					'Bakso Besar' => 'Bakso Besar',
					'Bakso Kecil' => 'Bakso Kecil',
					'Siomay Ayam' => 'Siomay Ayam',
					'Tahu' => 'Tahu'
				);
			}
			else if($current_merchant == 'Warkop Pak Kumis'){
				$array = array(
					'Kopi Luwak' => 'Kopi Luwak',
					'Mie Goreng' => 'Mie Goreng',
					'Nasi Goreng' => 'Nasi Goreng',
					'Kopi Hitam' => 'Kopi Hitam'
				);
			}
			else if($current_merchant == 'Pecel Mbak Suminem'){
				$array = array(
					'Pecel Madiun' => 'Pecel Madiun',
					'Tempe Penyet' => 'Tempe Penyet',
					'Terong Penyet' => 'Terong Penyet',
					'Semanggi' => 'Semanggi'
				);
			}
			else if($current_merchant == 'Nasi Goreng Ala Kadarnya'){
				$array = array(
					'Nasi Goreng Mawut' => 'Nasi Goreng Mawut',
					'Nasi Goreng' => 'Nasi Goreng',
					'Mie Goreng' => 'Mie Goreng',
					'Ayam Goreng' => 'Ayam Goreng'
				);
			}
			echo form_dropdown('ChooseFood',$array);
		?>
		Jumlah:<input type="number" name="Jumlah" value=0>
		<input type='submit' name='Add' value='Add'>
		<input type='hidden' name='CurrentMerchant' value = '<?php echo $current_merchant?>'>
		<input type='hidden' name='HiddenUser' value = '<?php echo $arruser?>'>
		<input type='hidden' name='HiddenPass' value = '<?php echo $arrpass?>'>
		<input type='hidden' name='HiddenEMoney' value = '<?php echo $arremoney?>'>
		<input type='hidden' name='HiddenNama' value = '<?php echo $arrnama?>'>
		<input type='hidden' name='HiddenNoTelp' value = '<?php echo $arrnotelp?>'>
		<input type='hidden' name='HiddenAlamat' value = '<?php echo $arralamat?>'>		
		<input type='hidden' name='HiddenTag' value = '<?php echo $arrtag?>'>
		<input type='hidden' name='CurrentEMoney' value = '<?php echo $current_emoney?>'>
		<input type='hidden' name='CurrentUser' value = '<?php echo $current_user?>'>
	</form>
	<?php echo br(2);?>
	<?php
		$tmpl = array("table_open" => "<table border = 1>");
        $this->table->set_template($tmpl);
        $this->table->set_heading("Makanan","Jumlah", "Harga Satuan", "Subtotal");
		foreach($this->cart->contents() as $items):
			$subtotal = $items['qty'] * $items['price'];
			$this->table->add_row($items['name'], $items['qty'], $items['price'], $subtotal);
		endforeach;
		$this->table->add_row("", "", "Total :", $this->cart->total());
		echo $this->table->generate();
		?><b style="color: red;"><?php echo $error;
	?></b><?php echo br(2);?>
	Sudah selesai memilih menu? Satu langkah lagi dan makanan akan segera menghampirimu: 
	<form method='post' action='<?=site_url("mainpage/to_pembayaran")?>'>
		<input type='submit' name='Payment' value='Payment'>
		<input type='hidden' name='HiddenUser' value = '<?php echo $arruser?>'>
		<input type='hidden' name='HiddenPass' value = '<?php echo $arrpass?>'>
		<input type='hidden' name='HiddenEMoney' value = '<?php echo $arremoney?>'>
		<input type='hidden' name='HiddenNama' value = '<?php echo $arrnama?>'>
		<input type='hidden' name='HiddenNoTelp' value = '<?php echo $arrnotelp?>'>
		<input type='hidden' name='HiddenAlamat' value = '<?php echo $arralamat?>'>		
		<input type='hidden' name='HiddenTag' value = '<?php echo $arrtag?>'>
		<input type='hidden' name='CurrentEMoney' value = '<?php echo $current_emoney?>'>
		<input type='hidden' name='CurrentUser' value = '<?php echo $current_user?>'>
	</form><?php echo br(5);?>
	</div>
</body>
</html>