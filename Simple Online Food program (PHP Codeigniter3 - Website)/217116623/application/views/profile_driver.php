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
	</style>
</head>
<body>
	<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="float: left; width: 100px; height:100px;">
	<div style="width: 800px; float: left;">
		<?php echo heading('&nbspProfil Driver', 1, array('margin-top' => 10));?>
		<?php echo heading('&nbsp&nbspIni Profil lengkapmu, '.$current_user, 3);?>
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
		<h1>Profile User</h1>
		<b>Nama : </b><?php echo $current_user.br(2);?>
	  <b>Alamat&nbsp-&nbspTag : </b><br><?php 
					$idx = 1;
					foreach($current_alamat as $c){
						echo $idx.". ".$c->alamat." - ".$c->tag."<br>";
						$idx += 1;
					}
					echo br(1);
				?>
	 <b>No Telp : </b><?php echo $current_no_telp.br(2);?>
	   <b>Email : </b><?php echo $current_email.br(2);?>
<b>Saldo E-Money : </b><?php echo $current_emoney.br(2);?>
		<h3>Pesanan yang pernah kamu antarkan:</h3>
		<?php foreach($htrans as $h){
			if($h->email_driver == $current_email && $h->status == 'Sampai'){?>
				<div class='trans'>
				<h3><?php echo $h->nama_mechant;?></h3><br>
				Pengiriman dari: <?php echo $h->alamat_dari.br(2);?>
				Atas pesanan: <?php echo "<h3>".$h->nama_cust."</h3>"?>
				Menuju: <?php echo $h->alamat_tujuan.br(3);?>
				Status pesanan: <?php echo $h->status.br(3);?>
				<?php
					echo "Tanggal Pengiriman: ".$h->tglpengiriman.br(3);
					echo "Tanggal Sampai: ".$h->tglsampai.br(3);
				?>
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
				 </div>
			<?php }} ?>
		<?php echo br(5);?>
	</div>
</body>
</html>