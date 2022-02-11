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
	</style>
</head>
<body>
	<div id="container">
		<div id="heading" style="height: 100px">
			<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="width: 100px; height:100px; float: left;">
			<div id="header" style="width: 900px; float:left;">
				<?php echo heading('&nbspWelcome to OnlineFood!', 1, array('margin-top' => 10));?>
				<?php echo heading('&nbsp&nbspSilahkan masukkan email dan password anda', 3);?>
			</div>
			<div style="text-align: right; width: 250px; float: left;">
				<?php echo br(2);?>
				Belum punya akun? Registrasi disini:
				<?php
					echo form_open(site_url("mainpage/to_regis"));
					echo form_submit('Register', 'Register');
					echo form_close();
				?>
			</div>
		</div>
		<div id='content' style="clear:left; background-color: white; opacity: 0.75; text-align: center; width: 1000px; margin: auto">
			<b style="color: red;"><?php echo $error?></b><?php echo br(1);?>
			<?php
				echo form_open(site_url("mainpage/handle_login"));
				echo str_repeat('&nbsp', 6)."Email&nbsp:&nbsp";
				echo form_input('username').br(2);
				echo "Password&nbsp:&nbsp".form_input('password').br(2);
				echo form_checkbox('checkremember', 'rememberme')."Remember Me<br><br>";
				echo form_submit('Login', 'Login');
				echo form_close();
			?>
			<?php echo br(5);?>
		</div>
	</div>
</body>
</html>