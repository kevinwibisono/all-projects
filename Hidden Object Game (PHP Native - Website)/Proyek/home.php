<?php
	
	session_start();
	unset($_SESSION['mission']);
	$_SESSION['removal'] = -1;
	$_SESSION['stage'] = 0;
	$_SESSION['case'] = 0;
	$_SESSION['hint'] = 0;
	//TAMBAHAN CODING
	if(!isset($_SESSION['curplayer'])){
		$_SESSION['curplayer'] = "";
	}
	$_SESSION['gold'] = 0;
	$_SESSION['extratime']=0;
	$_SESSION['change_nn']=0;
	
	if(isset($_POST['login'])){
		$player = $_SESSION['curplayer'];
		$player = $_POST['nama'];
		$_SESSION['curplayer'] = $player;
	}
	
	if(isset($_POST['regis'])){
		$player = $_SESSION['curplayer'];
		$player = $_POST['nama'];
		$_SESSION['curplayer'] = $player;
	}
?>

<html>
<head>
	<style>
		#container{
			width: 800px;
			height: 600px;
			background-image: url("mainmenu.jpg");
		}
		#flogin, #fregis{
			width: 400px;
			height: 300px;
			left: 300px;
			top: 300px;
			position: relative;
			background-color: white;
			background: no-repeat;
		}
		#namaplayer{
			background-color: transparent;
			width: 100px;
			height: 25px;
			left: 620px;
			top: 110px;
			padding-left: 20px;
			position: relative;
			color: black;
			font-family: Magneto;
			font-size: 20px;
		}
	</style>
	<script type="text/javascript" src="jquery.js"></script>
	<script>
		//TAMBAHAN CODING
		var status = "<?php echo $_SESSION['curplayer']?>";		
		var cases = <?php echo $_SESSION['case'] ?>;
	</script>
</head>
<body>
	<div id="container">
		<div id='namaplayer'><?php echo $_SESSION['curplayer'] ?></div>
	</div>
</body>
<script>
	function registernama(name){
		$.post("setuser.php",{nama: name}, function(result){ $("#namaplayer").html(result);});
	}
	function getstage(name){
		$.post("getdata.php",{nama: name}, function(result){ $("#namaplayer").html(result);});
	}
	function setmission(missions){
		$.post("setmission.php",{mission: missions}, function(){});
	}
	$(document).ready(function(){
		$("#container").click(function(e){
			//alert(e.pageX +","+ e.pageY);
			if((e.pageX <= 670 && e.pageX >= 571) && (e.pageY <= 565 && e.pageY >= 488)){
				$("#container").append("<div id='flogin' style=' background-image: url(login.png); position: absolute;'></div>");
				$("#container").append("<input id='nama' type='text' style='background-color: transparent; border: transparent; left: 390px; top: 380px; position: absolute;'>");
				$("#flogin").click(function(e){
					var pWidth = $(this).innerWidth();
					var pOffset = $(this).offset();
					var x = e.pageX - pOffset.left;
					var y = e.pageY - pOffset.top;
					if((x<316 && x>285) && (y<140 && y>114)){
						registernama($("#nama").val());
						$(this).remove();
						$("#nama").remove();
						status = "ready";
					}
				});
			}
			if((e.pageX <= 453 && e.pageX >= 345) && (e.pageY <= 568 && e.pageY >= 489)){
				$("#container").append("<div id='fregis' style=' background-image: url(login.png); position: absolute;'></div>");
				$("#container").append("<input id='nama' type='text' style='background-color: transparent; border: transparent; left: 390px; top: 380px; position: absolute;'>");
				$("#fregis").click(function(e){
					var pWidth = $(this).innerWidth();
					var pOffset = $(this).offset();
					var x = e.pageX - pOffset.left;
					var y = e.pageY - pOffset.top;
					//alert(x+";"+y);
					if((x<316 && x>285) && (y<140 && y>114)){
						if($("#nama").val() != ""){
							registernama($("#nama").val());
							$(this).remove();
							$("#nama").remove();
							alert("User registerred successfully");
							status = "ready";
						}
						else{
							alert("Your name cannot be empty");
						}
					}
				});
			}
			if((e.pageX <= 237 && e.pageX >= 135) && (e.pageY <= 571 && e.pageY >= 489)){
				//TAMBAHAN CODING
				if(status != ""){
					if(cases == 0){
						setmission(2);
					}
					$(location).attr('href', 'map.php');
				}
				else{
					alert("You must login first!");
				}
			}
		});
		
	});
</script>
</html>