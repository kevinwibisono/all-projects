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
	<script>
		function alertmessage(){
			alert("Transaksi Berhasil!");
			alert("Terima kasih telah mempercayai kami");
		}
	</script>
	<div style="">
		<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="float: left; width: 100px; height:100px;">
		<div style="width: 900px; float: left;">
			<?php echo heading('&nbspSatu langkah lagi, '.$current_user, 1, array('margin-top' => 10));?>
			<?php echo heading('&nbsp&nbspTunggu apa lagi? Makanan sudah menunggumu', 3);?>
		</div>
		<div style="text-align: right; width: 250px; float:left;">
			<?php echo br(2);?>
			Kelupaan satu makanan? Kembali ke pemesanan: 
			<form method='post' action='<?=site_url("mainpage/to_pemesanan")?>'>
				<input type='submit' name='Order' value='Order'>
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
		</div>
	</div>
	<div style="clear:left; background-color: white; opacity: 0.75; text-align: center; width: 1000px; margin: auto">
	<hr><br>
	<b>Ringkasan Order :</b><?php echo br(1);?>
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
	?></b><?php echo br(1);?>
	<?php echo str_repeat("&nbsp", 10); ?>Saldo E-money:<b><?php echo " ".$current_emoney?></b><?php echo br(2);?>
	<form method='post' action='<?=site_url("mainpage/handle_pembayaran")?>'>
		<input type='submit' name='Pay' value='Pay' onclick="alertmessage()">
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