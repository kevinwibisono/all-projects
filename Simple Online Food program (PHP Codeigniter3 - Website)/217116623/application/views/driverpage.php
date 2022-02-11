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
		.trans{
			text-align: center;
			border: 2px solid black;
			padding: 0px;
			border-radius: 10px;
			width: 650px;
			margin-bottom: 10px;
			padding: 10px;
			margin: auto;
		}
		table{
			margin: 0 auto;
		}
		input[name='Submit']{
			border: none;
		    background-color: transparent;
		    padding: 0;
			text-size: 16px;
		}
	</style>
</head>
<body>
	<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="float: left; width: 100px; height:100px;">
	<div style="width: 800px; float: left;">
		<?php echo heading("&nbspHello, ".form_open(site_url('mainpage/profile_driver')).form_submit('Submit', $current_user).form_close(), 1, array('margin-top' => 10));?>
		<div style='margin-left: 20px;'><b>Your Balance : </b><?php echo $current_emoney.br(2);
							echo form_open("mainpage/tariksaldo");
							echo form_submit('','Tarik Saldo');
							echo form_close();
					  ?>
		</div>
	</div>	
	<div style="text-align: right; width: 250px; float:left;">
		<?php echo br(2);?>
		Kembali Ke Login:
		<?php
			echo form_open(site_url("mainpage/to_login"));
			echo form_submit('Login', 'Login');
			echo form_close();
		?>
	</div>
	<div id='content' style="clear:left; background-color: white; opacity: 0.75; text-align: center; width: 1000px; margin: auto">
		<h3>Cari Pesanan:</h3>
		<?php foreach($htrans as $h){
				if($h->status == "Menunggu driver"){?>
				<div class='trans'>
				From: <?php echo br(2)."<b>".$h->nama_mechant."</b>".br(2).$h->alamat_dari;?>
				<hr>
				To: <?php echo br(2)."<b>".$h->nama_cust."</b>".br(2).$h->alamat_tujuan.br(4);?>
				<table>
				<?php foreach ($dtrans as $d){
					if($d->id_trans == $h->id_trans){?>
					<tr>
						<td><?php echo $d->makanan;?></td>
						<td><?php echo 'x '.$d->jumlah;?></td>
						<td><?php echo 'Rp '.$d->jumlah*$d->harga;?></td>
					</tr>
				<?php }}?>
				<tr>
					<td>Total</td>
					<td></td>
					<td>Rp&nbsp<?php echo $h->total;?></td>
				</tr>
				</table>
				<?php
					echo form_open('mainpage/acceptdelivery');
					echo form_hidden('HiddenIDTrans', $h->id_trans);
					echo form_submit('', 'Accept Delivery');
					echo form_close();
				?>
				 </div>
				<?php }} ?>
		<?php echo br(5);?>
	</div>
</body>
</html>