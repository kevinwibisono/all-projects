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
	</style>
</head>
<body>
	<script>
		function choosetaguser(){
			var tag = document.getElementById('tujuan').value;
			document.getElementById('HiddenTag').value = tag;
		}
		function cekBerhasil(){
			var saldo = document.getElementById('HiddenEMoney').value;
			var total = document.getElementById('total').value;
			if(parseInt(saldo) >= parseInt(total)){
				alert('Berhasil');
			}
			else{
				alert('Gagal! Saldo tidak cukup');
				alert('Transaksi dibatalkan');
			}
		}
	</script>			
	<div id="container">
		<div id="heading" style="height: 100px">
			<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="width: 100px; height:100px; float: left;">
			<div id="header" style="width: 800px; float:left;">
				<?php echo heading('&nbspPembayaran', 1, array('margin-top' => 10));?>
			</div>
			<div style="text-align: right; width: 250px; float: left;">
			</div>
		</div>
		<div id='content' style="clear:left; background-color: white; opacity: 0.75; width: 1000px; margin: auto; padding: 50px">
			Kirim Ke :
			<select id='tujuan' onchange="choosetaguser()">
			<?php $first = ""; foreach($alamat as $a){
				if($first == ""){$first = $a->tag;}
				if($a->email_cust == $current_email){?>
				<option value='<?php echo $a->tag;?>'><?php echo $a->tag;?></option>
			<?php } }?>
			</select>
			<h3>Berikut adalah daftar keranjang anda</h3>
			<table>
			<?php $total = 0; foreach($keranjang as $k){?>
					<tr>
						<td><?php echo $k->makanan;?></td>
						<td><?php echo 'x'.$k->jumlah;?></td>
						<td><?php echo 'Rp'.$k->harga*$k->jumlah;?></td>
						<td><?php echo form_open("mainpage/dec_makanan");
								   echo form_submit('', '-');
								   echo form_hidden('HiddenMakanan', $k->makanan);
								   echo form_hidden('HiddenJumlah', $k->jumlah);
								   echo form_hidden('HiddenEmailMerchant', $current_email_merchant);
								   echo form_hidden('HiddenAddressMerchant', $chosen_address_merchant);
								   echo form_close();
								   echo form_open("mainpage/inc_makanan");
								   echo form_hidden('HiddenMakanan', $k->makanan);
								   echo form_hidden('HiddenJumlah', $k->jumlah);
								   echo form_hidden('HiddenEmailMerchant', $current_email_merchant);
								   echo form_hidden('HiddenAddressMerchant', $chosen_address_merchant);
								   echo form_submit('', '+');
								   echo form_close();
						?></td>
					</tr>
					
			<?php $total +=($k->harga*$k->jumlah); }?>
			<tr>
				<td>Total</td>
				<td></td>
				<td>Rp&nbsp<?php echo $total;?></td>
			</tr>
			</table>
			<?php
				echo form_open("mainpage/do_transaction");
				echo form_hidden('HiddenEmailMerchant', $current_email_merchant);
				echo form_hidden('HiddenAddressMerchant', $chosen_address_merchant);
				$tipe = array(
						'type' => 'hidden',
						'name'  => 'HiddenTag',
						'id'    => 'HiddenTag',
						'value' => $first
				);
				echo form_input($tipe);
				$tipe2 = array(
						'type' => 'hidden',
						'name'  => 'HiddenEMoney',
						'id'    => 'HiddenEMoney',
						'value' => $current_emoney
				);
				echo form_input($tipe2);
				$tipe3 = array(
						'type' => 'hidden',
						'name'  => 'total',
						'id'    => 'total',
						'value' => $total
				);
				echo form_input($tipe3);
				?>
				<input type='submit' value='Pay Now' onclick="cekBerhasil()">
				<?php
				echo form_close();
			?>
		</div>
	</div>
</body>
</html>