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
		.merchants{
			text-align: left;
			border: 2px solid black;
			padding: 0px;
			border-radius: 10px;
			width: 650px;
			margin: auto;
			margin-bottom: 10px;
			padding: 10px;
		}
		a{
			color: black;
			text-decoration: none;
		}
		input[name='Submit']{
			border: none;
		    background-color: transparent;
		    padding: 0;
			text-size: 16px;
		}
		#FormProfile{
			width: 200px;
		}
		video{
			margin: auto;
			display: block;
			height: 200px;
			object-fit: cover;
		}
		video::-webkit-media-controls-fullscreen-button, video::-webkit-media-controls-play-button, video::-webkit-media-controls-pausebutton {
			display: none;
		}
	</style>
</head>
<body>
	<div id="container">
		<video width="700" controls loop autoplay>
		  <source src="<?php echo base_url('assets/videos/'.$ads);?>" type="video/mp4">
		</video>		
		<div id="heading" style="height: 100px">
			<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="width: 100px; height:100px; float: left;">
			<div id="header" style="width: 800px; float:left;">
			<?php $attributes = array('class' => 'form', 'id' => 'FormProfile'); ?>
				<?php echo heading("&nbspHello, ".form_open(site_url('mainpage/to_profile'), $attributes).form_submit('Submit', $current_user).form_close(), 1, array('margin-top' => 10));?>
				<?php echo heading('&nbsp&nbspYour Balance : '.$current_emoney, 3);?>
				Logout:
				<?php
					echo form_open(site_url("mainpage/to_login"));
					echo form_submit('', 'Logout');
					echo form_close();
				?>
			</div>
			<div style="text-align: right; width: 250px; float: left;">
				<?php echo br(2);?>
				Top Up:
				<?php
					echo form_open(site_url("mainpage/to_topup"));
					echo form_submit('', 'Top Up Now');
					echo form_close();
				?>
			</div>
		</div>
		<div id='content' style="clear:left; background-color: white; opacity: 0.75; width: 1000px; margin: auto; padding: 50px">
			<?php foreach($merchant as $m){?>
				<div class='merchants'>
				<?php 
				  echo form_open("mainpage/to_detail");
				  echo "<h3>".$m->nama."</h3>";
				  echo "<hr>";
				  echo "<ul>";
				  foreach($alamat as $a){
					if($a->email_cust == $m->email){
						echo "<li>".$a->alamat."</li>";
					}
				  }
				  echo "</ul>";
				  echo form_submit('', 'Telusuri');
				  echo form_hidden('HiddenEmailMerchant', $m->email);
				  echo form_close();
				 ?>
				 </div>
			<?php } ?>
		</div>
	</div>
	<script>
		 $(document).ready(function() {
			 $('video').prop('muted',true)[0].play()
		 });
	</script>
</body>
</html>