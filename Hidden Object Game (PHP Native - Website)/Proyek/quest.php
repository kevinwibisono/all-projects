<?php
	session_start();
	
?>

<html>
	<head>
		<link href='https://fonts.googleapis.com/css?family=Orbitron' rel='stylesheet' type='text/css'>
		<style>
			#container{
				width: 800px;
				height: 600px;
				position: absolute;
				background-size: contain;
			}
			#time{
				width: 110px;
				height: 30px;
				position: absolute;
				left: 676px;
				top: 12px;
				background-color: gray;
				color: orange;
				border-radius: 30%;
				text-align: center;
				font-size: 20px;
				font-family: "Orbitron";
				background-image: url("bgtimer.png");
			}
			span{
				text-align: center;
			}
		</style>
		<script type="text/javascript" src="jquery.js"></script>
		<script>
			var miss = <?php echo $_SESSION['mission']?>;
			var sisabrg = 0;
			var click = 0;
			var listbrg = [];
			var arrbrg = [];
			var arrx = [];
			var arry = [];
			var arrurl = [];
			var arrwidth = [];
			var arrheight = [];
			var status = "";
			var stage = <?php echo $_SESSION['stage']?>;
			var cases = <?php echo $_SESSION['case']?>;
			var menit;
			var detik;
			var timer1;
			var timer2;
			var timer3;
			var counter = 30;
			var ctr = 0;
			var index = -1;
			var jumhint = <?php echo $_SESSION['hint']?>;
			var timer2_start = false;
		</script>
	</head>
	<body>
		<div id="container">
			<div id='time'></div>
		</div>
	</body>
	<script>
		function redhint(){
			$.post("reducehint.php",{}, function(){});
		}
		function redmis(){
			$.post("reducemis.php",{}, function(result){miss = result;});
		}
		function lanjut(){
			$.post("nextcase.php",{}, function(){});
		}
		function setremove(num){
			$.post("setremove.php",{number : num}, function(){});
		}
		function resetlist(){
			var arrobjek = $(".list");
			for(var i=0; i<arrobjek.length; i++){
				$(arrobjek[i]).css("top", (i*25+215));
			}
		}
		function hint(){
			var arrbrg = $(".barang");
			var xawal = parseInt($(arrbrg[index]).css("left"));
			var xakhir = parseInt($(arrbrg[index]).css("left")) + parseInt($(arrbrg[index]).css("width"));
			var yawal = parseInt($(arrbrg[index]).css("top"));
			var yakhir = parseInt($(arrbrg[index]).css("top")) + parseInt($(arrbrg[index]).css("height"));
			ctr += 1;
			if(ctr > 10){
				var arrhint = $(".star");
				for(var i=0;i<arrhint.length; i++){
					$(arrhint[i]).remove();
				}
				clearInterval(timer3);
				status = "";
				ctr = 0;
			}
			else{
				var x = Math.floor(Math.random()*(xakhir-xawal)+xawal);
				var y = Math.floor(Math.random()*(yakhir-yawal)+yawal);
				if(ctr % 3 == 0){
					var arrhint = $(".star");
					for(var i=0;i<arrhint.length; i++){
						$(arrhint[i]).remove();
					}
				}
				$("#container").append("<img src ='star.png' class='star' style='width: 15px; height: 15px; left: "+x+"px; top: "+y+"px; position: absolute;'>");
				
			}
			
		}
		
		function timer(){
			if(detik <= 0){
				if(menit <= 0){
					clearInterval(timer1);
					clearInterval(timer2);
					alert("Time's up!");
					$(location).attr('href', 'map.php');
				}
				else{
					menit -= 1;
					detik = 59;
					$("#time").html(menit+":"+detik);
				}
			}
			else{
				detik -=1;
				$("#time").html(menit+":"+detik);
			}
		}
		function penalty(){
			$("#time").css("color", "red");
			if(detik <= 0){
				if(menit <= 0){
					clearInterval(timer1);
					clearInterval(timer2);
					alert("Time's up!");
					$(location).attr('href', 'map.php');
					
				}
				else{
					menit -= 1;
					detik = 59;
					$("#time").html(menit+":"+detik);
					counter -= 1;
					if(counter <= 1){
						clearInterval(timer2);
						timer1 = setInterval(timer, 1000);
						counter = 30;
						$("#time").css("color", "orange");
						timer2_start = false;
					}
				}
			}
			else{
				detik -=1;
				$("#time").html(menit+":"+detik);
				counter -= 1;
				if(counter <= 1){
					clearInterval(timer2);
					timer1 = setInterval(timer, 1000);
					counter = 30;
					$("#time").css("color", "orange");
					timer2_start = false;
				}
			}
		}
		$(document).ready(function(){
			if(stage == 0){
				sisabrg = 6;
				arrxfull = [602, 348, 0, 76, 225, 100, 599, 155, 460, 445];
				arryfull = [510, 251, 520, 51, 104, 183, 27, 291, 157, 440];
				arrwidthfull = [27, 25, 36, 79, 40, 44, 45, 56, 36, 63];
				arrheightfull = [26, 22, 46, 120, 55, 59, 54, 46, 72, 45];
				arrurlfull = ["goldfish.png", "bug.png", "dragonfly.png", "hat.png", "lincoln.png", "poison.png", "notes.png", "rocket.png", "sandhour.png", "snail.png"];
				listbrgfull = ["Goldfish", "Bug", "Dragonfly", "Hat", "Lincoln", "Poison", "Notes", "Rocket", "Hourglass", "Snail"];
				for(var i = 0; i< 6; i++){
					var sama = false;
					var urutan = Math.floor(Math.random()*10);
					arrx[i] = arrxfull[urutan];
					if(arrx.length > 1){
						for(var j = 0; j < arrx.length - 1; j++){
							if(arrx[j] == arrx[i]){
								sama = true;
							}
						}
						while(sama){
							sama = false;
							urutan = Math.floor(Math.random()*10);
							arrx[i] = arrxfull[urutan];
							for(var j = 0; j<arrx.length-1; j++){
								if(arrx[j] == arrx[i]){
									sama = true;
								}
							}
						}
					}
					arry[i] = arryfull[urutan];
					arrwidth[i] = arrwidthfull[urutan];
					arrheight[i] = arrheightfull[urutan];
					arrurl[i] = arrurlfull[urutan];
					listbrg[i] = listbrgfull[urutan];
				}
				
				$("#container").css("background-image", "url(misi1.png)");
				menit = 3;
				detik = 15;
			}
			else if(stage == 1){
				sisabrg = 6;
				arrxfull = [558, 154, 472, 68, 0, 460, 542, 122, 131, 314];
				arryfull = [522, 94, 90, 199, 513, 221, 320, -2, 310, 69];
				arrwidthfull = [44, 65, 44, 76, 29, 51, 27, 47, 33, 41];
				arrheightfull = [33, 60, 61, 110, 36, 113, 41, 36, 39, 56];
				arrurlfull = ["binocular.png", "aquarium.png", "horse.png", "grape.png", "anchor.png", "microphone.png", "ribbon.png", "teapot.png", "glove.png", "bicycle.png"];
				listbrgfull = ["Binocular", "Aquarium", "Horse", "Grape", "Anchor", "Microphone", "Ribbon", "Teapot", "Glove", "Bicycle"];
				for(var i = 0; i< 6; i++){
					var sama = false;
					var urutan = Math.floor(Math.random()*10);
					arrx[i] = arrxfull[urutan];
					if(arrx.length > 1){
						for(var j = 0; j < arrx.length - 1; j++){
							if(arrx[j] == arrx[i]){
								sama = true;
							}
						}
						while(sama){
							sama = false;
							urutan = Math.floor(Math.random()*10);
							arrx[i] = arrxfull[urutan];
							for(var j = 0; j<arrx.length-1; j++){
								if(arrx[j] == arrx[i]){
									sama = true;
								}
							}
						}
					}
					arry[i] = arryfull[urutan];
					arrwidth[i] = arrwidthfull[urutan];
					arrheight[i] = arrheightfull[urutan];
					arrurl[i] = arrurlfull[urutan];
					listbrg[i] = listbrgfull[urutan];
				}
				$("#container").css("background-image", "url(misi2.png)");
				menit = 2;
				detik = 59;
			}
			else if(stage == 2){
				sisabrg = 8;
				arrxfull = [315, 480, 77, 210, 460, 90, 51, 170, 275, -10];
				arryfull = [475, 80, 10, 380, 435, 180, 315, 257, 17, 408];
				arrwidthfull = [80, 12, 54, 32, 36, 82, 49, 35, 47, 38];
				arrheightfull = [33, 88, 38, 37, 36, 42, 70, 79, 45, 186];
				arrurlfull = ["banana.png", "brush.png", "coconut.png", "coins.png", "lemon.png", "magnifier.png", "metronome.png", "pineaple.png", "pistol.png", "sword.png"];
				listbrgfull = ["Banana", "Brush", "Coconut", "Coins", "Lemon", "Magnifier", "Metronome", "Pineapple", "Pistol", "Sword"];
				for(var i = 0; i< 8; i++){
					var sama = false;
					var urutan = Math.floor(Math.random()*10);
					arrx[i] = arrxfull[urutan];
					if(arrx.length > 1){
						for(var j = 0; j < arrx.length - 1; j++){
							if(arrx[j] == arrx[i]){
								sama = true;
							}
						}
						while(sama){
							sama = false;
							urutan = Math.floor(Math.random()*10);
							arrx[i] = arrxfull[urutan];
							for(var j = 0; j<arrx.length-1; j++){
								if(arrx[j] == arrx[i]){
									sama = true;
								}
							}
						}
					}
					arry[i] = arryfull[urutan];
					arrwidth[i] = arrwidthfull[urutan];
					arrheight[i] = arrheightfull[urutan];
					arrurl[i] = arrurlfull[urutan];
					listbrg[i] = listbrgfull[urutan];
				}
				$("#container").css("background-image", "url(misi3.jpg)");
				menit = 1;
				detik = 59;
			}
			else if(stage == 3){
				sisabrg = 8;
				arrxfull = [404, 215, 510, 0, 393, 23, 407, 625, 267, 54];
				arryfull = [59, 357, 514, 13, 558, 395, 474, 458, 226, 287];
				arrwidthfull = [69, 82, 14, 63, 81, 40, 20, 22, 28, 28];
				arrheightfull = [71, 21, 38, 80, 38, 44, 41, 57, 64, 20];
				arrurlfull = ["bull.png", "dagger.png", "duck.png", "fan.png", "hand.png", "icecream.png", "lotion.png", "money.png", "palu.png", "pita.png"];
				listbrgfull = ["Bull Skull", "Dagger", "Duck", "Fan", "Hand", "Ice cream", "Bottle", "Money", "Hammer", "Ribbon"];
				for(var i = 0; i< 8; i++){
					var sama = false;
					var urutan = Math.floor(Math.random()*10);
					arrx[i] = arrxfull[urutan];
					if(arrx.length > 1){
						for(var j = 0; j < arrx.length - 1; j++){
							if(arrx[j] == arrx[i]){
								sama = true;
							}
						}
						while(sama){
							sama = false;
							urutan = Math.floor(Math.random()*10);
							arrx[i] = arrxfull[urutan];
							for(var j = 0; j<arrx.length-1; j++){
								if(arrx[j] == arrx[i]){
									sama = true;
								}
							}
						}
					}
					arry[i] = arryfull[urutan];
					arrwidth[i] = arrwidthfull[urutan];
					arrheight[i] = arrheightfull[urutan];
					arrurl[i] = arrurlfull[urutan];
					listbrg[i] = listbrgfull[urutan];
				}
				$("#container").css("background-image", "url(misi4.jpg)");
				menit = 1;
				detik = 30;
			}
			timer1 = setInterval(timer, 1000);
			for(var i = 0; i<arrx.length;i++){
				$("#container").append("<span id='brg"+i+"' class='list' style='background-color: transparent; width: 100px; height: 10px; left: 680px; top: "+(i*25+215)+"px; position: absolute;'>"+listbrg[i]+"</span>");
				$("#container").append("<img src='"+arrurl[i]+"' id='barang"+ i +"' class='barang' style='display: flex; background: no-repeat; left: "+arrx[i]+"px; top: "+arry[i]+"px; width: "+arrwidth[i]+"px; height: "+arrheight[i]+"px; position: absolute'>");
				var arrbrg = $(".barang");
				//$("#container").append("<div id='barang"+ i +"' class='barang' style='display: inline-block; background-size: 100%; background-image: url("+arrurl[i]+"); left: "+arrx[i]+"px; top: "+arry[i]+"px; width: 200px; height: 200px; position: absolute'></div>");
				$("#barang"+i).click(function(e){
					if(status != "pause"){
							click = 0;
							var index = arrbrg.index(this);
							$(this).animate({width: (arrwidth[index]+25)+"px", height: (arrheight[index]+25)+"px"}, 300, function(){
																			   
							});                                                
							$(this).animate({width: arrwidth[index]+"px", height: arrheight[index]+"px"}, 300, function(){
									
							});                                                
							$(this).animate({width: (arrwidth[index]+25)+"px", height: (arrheight[index]+25)+"px"}, 300, function(){
										  
							});                                                
							$(this).animate({width: arrwidth[index]+"px", height: arrheight[index]+"px"}, 300, function(){
											 
							});                                                
							$(this).animate({width: (arrwidth[index]+25)+"px", height: (arrheight[index]+25)+"px"}, 300, function(){
									
							});                                                
							$(this).animate({width: arrwidth[index]+"px", height: arrheight[index]+"px"}, 300, function(){
									
							});
							$(this).animate({left: "732px", top: "577px", width: (arrwidth[index]/4)+"px", height: (arrheight[index]/4)+"px"}, 500, function(){
								$(this).remove();
								var index = arrbrg.index($(this));
								$("#brg"+index).remove();
								resetlist()
								sisabrg -= 1;
								if(sisabrg <= 0){
									alert("Congratulations! You finished the mission");
									redmis();
									//alert(miss);
									if(miss <= 1){
										lanjut();
									}
									else{
										var angka = stage - (cases*2);
										setremove(angka);
									}
									$(location).attr('href', 'map.php');
								}
							});
							$(this).animate({opacity: 1}, 300, function(){
								
							});
						
					}					
				});
			}
			
			$("#container").click(function(e){
				//alert(e.pageX +","+ e.pageY);
				if(status != "pause"){
					click += 1;
					if(click > 3){
						$("#container").append("<div id='warning' style='background: no-repeat; background-image: url(penalty.png); width: 400px; height: 300px; left: 200px; top: 100px; position: absolute'></div>");
						status = "pause";
						clearInterval(timer1);
					}
				}				
				$("#warning").click(function(e){
					var pWidth = $(this).innerWidth();
					var pOffset = $(this).offset();
					var x = e.pageX - pOffset.left;
					var y = e.pageY - pOffset.top;
					if((x >= 279 && x <= 328) && (y >= 182 && y<= 224)){
						if(!timer2_start){
							timer2_start = true;
							click = 0;
							$(this).remove();
							status = "";
							timer2 = setInterval(penalty, 80);
						}
					}
				})
				if((e.pageX <= 788 && e.pageX >= 688) && (e.pageY <= 124 && e.pageY >= 99)){
					click = 0;
					if(jumhint > 0){
						jumhint -= 1;
						redhint();
						alert("Hint tersisa: "+jumhint);
						var arrbrg = $(".barang");
						index = Math.floor(Math.random()*arrbrg.length);
						status = "pause";
						timer3 = setInterval(hint, 200);
					}
					else{
						alert("Hint sudah habis");
					}
				}
			});
			
		});
	</script>
</html>