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
		.makanan{
			text-align: left;
			border: 2px solid black;
			padding: 0px;
			border-radius: 10px;
			width: 650px;
			margin-bottom: 10px;
			padding: 10px;
		}
		img{
			width:200px;
			height: 200px;
		}
	</style>
</head>
<body>
	<script>
		function choosealamatmerch(){
			var alamat = document.getElementById('alamat').value;
			document.getElementById('HiddenAddressMerchant').value = alamat;
		}
	</script>
	<div id="container">
		<div id="heading" style="height: 100px">
			<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="width: 100px; height:100px; float: left;">
			<div id="header" style="width: 800px; float:left;">
				<?php echo heading('&nbsp'.$current_merchant, 1, array('margin-top' => 10));?>
				
			</div>
			<div style="text-align: right; width: 250px; float: left;">
				<?php echo br(2);?>
			</div>
		</div>
		<div id='content' style="clear:left; background-color: white; opacity: 0.75; width: 1000px; margin: auto; padding: 50px">
			Pilih Restoran :
			<select id='alamat' onchange='choosealamatmerch()'>
			<?php $first = ""; foreach($alamat as $a){
				if($first == "") {$first = $a->alamat;}?>
				<option value='<?php echo $a->alamat;?>'><?php echo $a->alamat;?></option>
			<?php } ?>
			</select>
			<?php
				echo "<b style='color:red;'>".$error."</b>";
				echo "<h3>Pilih Menu :</h3>";
				foreach($makanan as $m){
					if(strpos($m->nama, 'Deleted') == null){?>
					<div class='makanan'>
					<img src="<?php echo base_url("assets/images/".$m->gambar);?>">
					<?php 
					  echo form_open("mainpage/add_keranjang");
					  echo "<h3>".$m->nama."</h3>";
					  echo "Rp".$m->harga." Qty".form_input('Jumlah');
					  echo form_hidden('HiddenGambar', $m->gambar);
					  echo form_hidden('HiddenNama', $m->nama);
					  echo form_hidden('HiddenHarga', $m->harga);
					  echo form_hidden('HiddenEmailMerchant', $current_email_merchant);
					  echo form_submit('', 'Add to Basket');
					  echo form_close();
					 ?>
				 </div>
					<?php }} 
				echo form_open("mainpage/to_checkout");
				echo form_submit('', 'Bayar Sekarang');
				echo form_hidden('HiddenEmailMerchant', $current_email_merchant);
				$tipe = array(
						'type' => 'hidden',
						'name'  => 'HiddenAddressMerchant',
						'id'    => 'HiddenAddressMerchant',
						'value' => $first
				);
				echo form_input($tipe);
				echo form_close();
			?>
		</div>
	</div>
</body>
</html>