<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
	<script src='<?=base_url("assets/jQuery.js");?>'></script>
	<style>
		body {
		 background-image: url("<?php echo base_url("assets/images/foodwall.jpeg")?>");
		 background-size: cover;
		 background-repeat: no-repeat;
		}
	</style>
</head>
<body>
	<script>
		function createnewinput(){
			var jumlah = parseInt(document.getElementById('HideJumlah').value, 10) + 1;
			var isi = document.getElementById('isi');
			var enter = document.createElement('br');
			var notelpinput = document.getElementById('innotelp');
			
			var labelalamat = document.createElement("LABEL");
			labelalamat.innerHTML = "Alamat : ";
			notelpinput.parentNode.insertBefore(labelalamat, notelpinput);
			
			var alamatinput = document.createElement("INPUT");
			alamatinput.setAttribute("name", "alamat["+jumlah+"]");
			notelpinput.parentNode.insertBefore(alamatinput, notelpinput);
			
			var labeltag = document.createElement("LABEL");
			labeltag.innerHTML = " Tag : ";
			notelpinput.parentNode.insertBefore(labeltag, notelpinput);
			var taginput = document.createElement("INPUT");
			taginput.setAttribute("name", "tag["+jumlah+"]");
			notelpinput.parentNode.insertBefore(taginput, notelpinput);
			
			notelpinput.parentNode.insertBefore(enter, notelpinput);
			
			document.getElementById('HideJumlah').value = jumlah;
		}
	</script>
	<div style="">
		<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="float: left; width: 100px; height:100px;">
		<div style="width: 800px; float: left;">
			<?php echo heading('&nbspHalaman Registrasi User', 1, array('margin-top' => 10));?>
			<?php echo heading('&nbsp&nbspSilahkan masukkan data diri anda', 3);?>
		</div>
		<div style="text-align: right; width: 250px; float:left;">
			<?php echo br(2);?>
			Sudah punya akun? Login disini:
			<?php
				echo form_open(site_url("mainpage/to_login"));
				echo form_submit('Login', 'Login');
				echo form_close();
			?>
		</div>
	</div>
	<br>
	<div style="clear:left; background-color: white; opacity: 0.75; text-align: center; width: 1000px; margin: auto" id='isi'>
		<hr>
		<b style="color: red;"><?php echo $error?></b><br>
		<?php
			echo form_open_multipart(site_url("mainpage/handle_register"));
			echo str_repeat('&nbsp', 6)."Nama&nbsp:&nbsp";
			echo form_input('nama').br(2);
		?>
			Alamat&nbsp:&nbsp<input type='text' name='alamat[0]'>
			Tag&nbsp:&nbsp<input type='text' name='tag[0]'>
			<input type='button' name='addadress' value='+' onclick='createnewinput()'><br><br>
		<?php 
			echo str_repeat('&nbsp', 3)."<span id='innotelp'>No Telp&nbsp:&nbsp</span>";
			echo form_input('nomor').br(2);
			echo str_repeat('&nbsp', 6)."Email&nbsp:&nbsp";
			echo form_input('username').br(2);
			echo "Password&nbsp:&nbsp".form_input('password').br(2);
			echo "Confirm Password&nbsp:&nbsp".form_input('cpassword').br(2);
			echo form_checkbox('captcha', 'a')."I'm not a robot".br(2);
			echo form_radio('role', 'customer')."Customer".form_radio('role', 'merchant')."Merchant".form_radio('role', 'driver')."Driver".br(1);
		?>
		<h5 style='display:none' id='textGbrDriver'>Gambar driver: </h5><br><input type='file' name='UploadPP' id='UploadPP' style='display:none; margin:auto'><br><br>
		<?php
			echo form_submit('Register', 'Register');
			$data = array(
					'type' => 'hidden',
					'name'  => 'HiddenJumlah',
					'id'    => 'HideJumlah',
					'value' => '0',
					'class' => 'hiddenjum'
			);
			echo form_input($data);
			echo form_close();
			echo br(5);
		?>
	</div>
	<script>
		$(document).ready(function(){
			$('input:radio[name="role"]').change(function(){
				if ($(this).is(':checked')) {
					if($(this).val() == 'driver'){
						$("#textGbrDriver").css('display', 'block');
						$("#UploadPP").css('display', 'block');
					}
					else{
						$("#textGbrDriver").css('display', 'none');
						$("#UploadPP").css('display', 'none');
					}
				}
			});
		});
	</script>
</body>
</html>