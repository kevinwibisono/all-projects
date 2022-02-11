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
		img{
			width: 100px;
			height: 100px;
		}
	</style>
</head>
<body>
	<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="float: left; width: 100px; height:100px;">
	<div style="width: 800px; float: left;">
		<?php echo heading('&nbspWelcome Admin!', 1, array('margin-top' => 10));?>
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
		<h3 style='color:red;'><?php echo $error;?></h3>
		Ini seluruh iklan yang sudah ditambahkan:<br><br>
		<table border='1'>
			<tr>
				<th>Ads</th>
				<th>Action</th>
			</tr>
		<?php foreach($video as $v){?>
			<tr>
				<td><video width="400" controls loop autoplay>
					  <source src="<?php echo base_url('assets/videos/'.$v->nama);?>" type="video/mp4">
					</video>		
				</td>
				<td>
				<?php 
					echo form_open('mainpage/deletevideo');
					echo form_hidden('HiddenAdsName', $v->nama);
					echo form_submit('', 'Delete');
					echo form_close();
				?></td>
			</tr>
		<?php }?>
		</table>
		<br><br><br>
		Tambahkan iklan baru:
		<?php
			echo form_open_multipart(site_url('mainpage/tambahvideo'));
		?>
		<input type='file' name='UploadVideo'><br><br>
		<?php
			echo form_submit('','Submit');
			echo form_close();
		?>
	</div>
</body>
</html>