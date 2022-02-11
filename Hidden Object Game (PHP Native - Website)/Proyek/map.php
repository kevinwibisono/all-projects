<?php
	session_start();
?>

<html>
	<head>
		<style>
			#container{
				width: 800px;
				height: 600px;
				position: absolute;
				background-image: url("mainmap.jpg");
				background-repeat: no-repeat;
			}
			#case{
				width: 200px;
				height: 75px;
				color: gold;
				background-color: transparent;
				font-size: 20px;
				left: 30px;
				top: 25px;
				position: absolute;
			}
			#namaplayer{
				background-color: transparent;
				width: 100px;
				height: 25px;
				left: 685px;
				top: 118px;
				padding-left: 20px;
				position: relative;
				color: black;
				font-family: Magneto;
				font-size: 16px;
			}
		</style>
		<script type="text/javascript" src="jquery.js"></script>
		<script>
			 var remove = <?php echo $_SESSION['removal'] ?>;
			 var places = [];
			 var xplace = [];
			 var yplace = [];
			 var mission = [false, false];
			 var Case = <?php echo $_SESSION['case'] ?>;
		</script>
	</head>
	<body>
		<div id='container'>
			<span id='case'></span>
			<!--TAMBAHAN CODING-->
			<span id='namaplayer'><?php echo $_SESSION['curplayer']?></span>
		</div>
	</body>
	<script>
		function setstage(st){
			$.post("setstage.php",{stage:st}, function(){});
		}
		$(document).ready(function(){
			if(Case == 0){
				places = ["barber_shop.png", "pawnshop.png"];
				xplace = [305, 340];
				yplace = [121, 275];
			}
			if(Case == 1){
				places = ["clock_shopee.png", "cemetery.png"];
				xplace = [217, 470];
				yplace = [50, 330];
			}
			if(Case == 2){
				places = ["captain_inn.png", "bookstore.png"];
				xplace = [140, 238];
				yplace = [130, -38];
			}
			if(Case == 3){
				places = ["science_museum.png", "greg_toyland.png"];
				xplace = [35, -5];
				yplace = [-55, 234];
			}
			$("#case").html("Case "+(Case+1));
			for(var i=0; i< places.length; i++){
				if(i != remove){
					$("#container").append("<img src='"+places[i]+"' id='tempat"+ i +"' class='tempat' style='background: no-repeat; left: "+xplace[i]+"px; top: "+yplace[i]+"px; width: 200px; height: 200px; position: absolute'>");
					var arrplace = $(".tempat");
					$("#tempat"+i).click(function(){
						var index = arrplace.index(this);
						if(places[index] == "barber_shop.png"){
							setstage(0);
						}
						else if(places[index] == "pawnshop.png"){
							setstage(1);
						}
						else if(places[index] == "clock_shopee.png"){
							setstage(2);
						}
						else if(places[index] == "cemetery.png"){
							setstage(3);
						}
						else if(places[index] == "captain_inn.png"){
							setstage(4);
						}
						else if(places[index] == "bookstore.png"){
							setstage(5);
						}
						else if(places[index] == "science_museum.png"){
							setstage(6);
						}
						else if(places[index] == "greg_toyland.png"){
							setstage(7);
						}
						$(location).attr('href', 'quest.php');
					});
				}
				else{
					$("#container").append("<span class='tempat'></span>");
				}
			}
			$("#container").append("<img src='shop.png' id='shop' class='tempat' style='background: no-repeat; left: 262px; top: 397px; width: 200px; height: 200px; position: absolute'>");
			$("#container").click(function(e){
				//alert(e.pageX+";"+e.pageY);
				//TAMBAHAN CODING
				if((e.pageX <= 787 && e.pageX >= 690) && (e.pageY <= 482 && e.pageY >= 463)){
					$(location).attr('href','home.php');
				}
			});
			$("#shop").click(function(e){
				$(location).attr('href','shop.php');				
			});
		});
	</script>
</html>