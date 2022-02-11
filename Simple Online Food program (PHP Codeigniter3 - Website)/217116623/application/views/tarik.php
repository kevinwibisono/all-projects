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
		<?php echo heading('&nbspTarik Saldo', 1, array('margin-top' => 10));?>
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
		<?php
			echo br(5);
			echo "<b style='color:red;'>".$error."</b>";
			echo form_open('mainpage/do_penarikan');
			$input = array(
					'type' => 'input',
					'name'  => 'Jumlah',
					'id'    => 'Jumlah'
			);
			echo form_input($input);
			$tipe = array(
					'type' => 'hidden',
					'name'  => 'HiddenEMoney',
					'id'    => 'HiddenEMoney',
					'value' => $current_saldo
			);
			echo form_input($tipe);
		?><input type='submit' value='Tarik' onclick='ceksaldo()'>
		<?php echo form_close();
			echo br(5);
		?>
	</div>
</body>
</html>