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
		table{
			margin: 0 auto;
		}
		.popup{
		  display: none;
		  position: fixed;
		  z-index: 1;
		  padding-top: 30px;
		  left: 0;
		  top: 0;
		  width : 100%;
		  height: 100%;
		  text-align: center;
		}

		.isipopup {
		  background-color: white;
		  margin: auto;
		  padding: 20px;
		  border: 1px solid black;
		  width: 80%;
		}
		img{
			width: 200px;
			height: 200px;
		}
	</style>
</head>
<body>
	<script>
		function displaymodal(i){
			var path = document.getElementById('HiddenUpGambar['.concat(i, ']')).value;
			var nama = document.getElementById('HiddenMakanan['.concat(i, ']')).value;
			var modal = document.getElementById("PopupMenu");
			modal.style.display = 'block';
			document.getElementById('HiddenUpMakanan').value = nama;
			document.getElementById('HiddenUpMakananForDelete').value = nama;
			document.getElementById('gbrUpdate').src = "http://localhost/217116623/assets/images/".concat(path);
		}
		function hidemodal(){
			var modal = document.getElementById("PopupMenu");
			modal.style.display = 'none';
		}
		
	</script>
	<div id="container">
		<div id="heading" style="height: 100px">
			<img src="<?php echo base_url("assets/images/onlinefood.png")?>" style="width: 100px; height:100px; float: left;">
			<div id="header" style="width: 800px; float:left;">
				<?php echo heading('&nbspWelcome to Food Management', 1, array('margin-top' => 10));?>
				<?php echo heading('&nbsp&nbspAnda dapat menambahkan menu yang dapat ditampilkan untuk toko anda', 3);?>
			</div>
			<div style="text-align: right; width: 250px; float: left;">
				<?php echo br(2);?>
				Kembali ke Login:
				<?php
					echo form_open(site_url("mainpage/to_login"));
					echo form_submit('Login', 'Login');
					echo form_close();
				?>
			</div>
		</div>
		<div id='content' style="clear:left; background-color: white; opacity: 0.75; text-align: center; width: 1000px; margin: auto">
			<b style="color: red;"><?php echo $error?></b><?php echo br(1);?>
			<?php
				echo form_open_multipart(site_url("mainpage/handle_insert_food"));
				echo "Nama Makanan : ".form_input('makanan').br(2);
				echo "Harga : ".form_input('harga').br(2);
				echo "Deskripsi : ".br(1).form_textarea('deskripsi').br(2);
				echo "Gambar : ";
			?>
			<input type='file' name='UploadGambar' onchange='readUrl(this)'><br><br>
			<img id='gbr' src="<?php echo base_url("assets/images/defaultfood.png")?>"><br><br>
			<?php
				echo form_submit('Buat', 'Buat Menu Baru');
				echo form_close().br(2);
			?>
			<table border='1'>
				<thead>
					<th>No</th>
					<th>Gambar</th>
					<th>Nama Menu</th>
					<th>Harga</th>
					<th>Action</th>
				</thead>
				<?php $idx = 1; 
					foreach($makanan as $m){
						if($m->email_merch == $current_user && strpos($m->nama, 'Deleted') == null){
							
				?>
				<tr>
					<td><?php echo $idx;?></td>
					<td><img src="<?php echo base_url("assets/images/".$m->gambar);?>" alt="<?php echo base_url("assets/images/defaultfood.png")?>"></td>
					<td><?php echo $m->nama;?></td>
					<td><?php echo $m->harga;?></td>
					<td><?php echo form_open();
							  echo form_hidden('HiddenUser', $current_user);?>
							  <input type='button' value='Edit' onclick="displaymodal(<?php echo $idx-1;?>)"> 
					    <?php $tipe = array(
									'type' => 'hidden',
									'name'  => 'HiddenMakanan',
									'id'    => 'HiddenMakanan['.($idx-1).']',
									'value' => $m->nama
							  );
							  echo form_input($tipe);
							  $tipe2 = array(
									'type' => 'hidden',
									'name'  => 'HiddenUpGambar',
									'id'    => 'HiddenUpGambar['.($idx-1).']',
									'value' => $m->gambar
							  );
							  echo form_input($tipe2);
							  echo form_close();
							  echo form_open(site_url("mainpage/disablefood"));
							  if($m->status == 'Enable'){
								echo form_submit('', 'Disable');
							  }
							  else{
								echo form_submit('', 'Enable');
							  }
							  echo form_hidden('HiddenMakanan', $m->nama);
							  echo form_close();
							  echo form_open(site_url("mainpage/deletefood"));?>
							  <input type='submit' value='Delete' onclick="return confirm('Are you sure you want to delete that food?')"> 
					    <?php 
							  echo form_hidden('HiddenMakanan', $m->nama);
							  echo form_close();
						?></td>
				</tr>
				<?php $idx = $idx + 1;} } ?>
			</table>
			<?php echo br(5);?>
		</div>
	</div>
	<div id="PopupMenu" class="popup">
		<div class="isipopup">
			<span class="close" onclick="hidemodal()">&times;</span>
			<?php
				echo form_open(site_url('mainpage/deletegambar'));
				echo form_input(array('type' => 'hidden', 'name' => 'HiddenUpMakananForDelete', 'value' => '', 'id' => 'HiddenUpMakananForDelete'));
				echo form_submit('','Hapus Gambar');
				echo form_close();
				echo form_open_multipart(site_url("mainpage/updatefood"));
				echo "Nama Makanan : ".form_input('NewNama')."<br>";
				echo "Harga : ".form_input('NewHarga')."<br>";
				echo "Deskripsi : <br>".form_textarea('NewDeskripsi')."<br>";
				echo "Gambar : ";
			?>
				<input type='file' name='NewUploadGambar' onchange='readUrl2(this)'><br><br>
				<img id='gbrUpdate'><br><br>
			<?php
				echo form_submit('', 'Update');
				echo form_input(array('type' => 'hidden', 'name' => 'HiddenUpMakanan', 'value' => '', 'id' => 'HiddenUpMakanan'));				
				echo form_close();
			?>
		</div>
	</div>
	<script>
		function readUrl(input){
			if(input.files && input.files[0]){
				var reader = new FileReader();
				reader.onload = function(e){
					$('#gbr').attr('src', e.target.result);
				};
				reader.readAsDataURL(input.files[0]);
			}
			else{
				$('#gbr').attr('src', 'http://localhost/217116623/assets/images/defaultfood.png');
			}
		}
		function readUrl2(input){
			if(input.files && input.files[0]){
				var reader = new FileReader();
				reader.onload = function(e){
					$('#gbrUpdate').attr('src', e.target.result);
				};
				reader.readAsDataURL(input.files[0]);
			}
			else{
				$('#gbrUpdate').attr('src', 'http://localhost/217116623/assets/images/defaultfood.png');
			}
		}
	</script>
</body>
</html>