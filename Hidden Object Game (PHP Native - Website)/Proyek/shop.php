<?php
	session_start();
	include "conn.php";
	
	
?>

<html>
<head>
	<script src="jquery.js" type="text/javascript"></script>
	<script>
		var hint = 0;
		var nickname = 0;
		var extra = 0;
		var cash = <?php echo $_SESSION['gold']?>;
	</script>
	<style>
		#container{
			background-color: yellow;
			width: 800px;
			height: 600px;	
			position: absolute;
		}
		#back{
			width: 50px;
			height: 50px;
			background-color: blue;
			margin: 10px;
			position: absolute;
		}
		#title{
			width: 500px;
			height: 50px;
			background-color: blue;
			margin: 10px;
			position: absolute;
			left: 60px;
		}
		#cash{
			width: 120px;
			height: 40px;
			background-color: blue;
			margin: 10px;
			position: absolute;
			left: 570px;
			text-align: center;
			font-size: 2em;
			padding: 5px;
			font-family: Magneto;
		}
		#addcash{
			width: 50px;
			height: 50px;
			background-color: blue;
			margin: 10px;
			position: absolute;
			left: 710px;
		}
		#hint{
			width: 150px;
			height: 150px;
			background-color: green;
			float:left;
			margin: 10px;
			position: absolute;
			left: 160px;
		}
		#nickname{
			width: 150px;
			height: 150px;
			background-color: red;
			float:left;
			margin: 10px;
			position: absolute;
			left: 320px;
		}
		#extra{
			width: 150px;
			height: 150px;
			background-color: pink;
			float:left;
			margin: 10px;
			position: absolute;
		}
		#cart{
			width: 660px;
			height: 200px;
			background-color: lightblue;
			margin: 10px;
			position: absolute;
			top: 280px;
			padding: 10px;
			font-family: Magneto;
			padding-left: 100px;
		}
		#remove{
			width: 50px;
			height: 50px;
			background-color: darkred;
			margin: 10px;
			float: left;
			position: absolute;
		}
		#buy{
			width: 50px;
			left: 60px;
			height: 50px;
			background-color: darkred;
			margin: 10px;
			float: left;
			position: absolute;
		}
		#header{
			background-color: orange;
			width: 780px;
			height: 70px;
			margin: 10px;
			position: absolute;
		}
		#footer{
			background-color: orange;
			width: 780px;
			height: 70px;
			margin: 10px;
			position: absolute;
			top: 510px;
		}
		#shop{
			background-color: orange;
			width: 780px;
			height: 180px;
			margin: 10px;
			top: 90px;
			position: absolute;
		}
	</style>
</head>
<body>
	<div id="container">
		<div id="header">
			<div id="back">BACK</div>
			<div id="title">Welcome to the CRIME COMPUTER SHOP!</div>
			<div id="cash">100G</div>
			<div id="addcash">+</div>
		</div>
		<div id="shop">
			<div id="hint">HINT</div>
			<div id="nickname">CHANGE NICKNAME</div>
			<div id="extra">EXTRA</div>
		</div>
		<div id="cart">
			<h1>CART</h1>
			<h3>Hint x0</h3><h3>Change Nickname x0</h3><h3>Extra Time x0</h3>
		</div>
		<div id="footer">
			<div id="remove">CLEAR ALL</div>
			<div id="buy">BUY</div>
		</div>
		
	</div>
		
</body>
<script>

	function update_cart(){
		var isi = "<h1>CART</h1><h3>Hint x"+hint+"</h3><h3>Change Nickname x"+nickname+"</h3><h3>Extra Time x"+extra+"</h3>";
		$("#cart").html(isi);
		$("#cash").html(cash+"G");
	}
	
	function buy(jumhint, jumextra, jumchange, jumgold){
		$.post("buy.php",{hint: jumhint, extra: jumextra, change: jumchange, gold: jumgold}, function(){});
	}
	$(document).ready(function(){
		update_cart();
		$("#back").click(function(e){
			$(location).attr('href','map.php');
		});
		$("#nickname").click(function(e){
			nickname++;
			update_cart();
		});
		$("#hint").click(function(e){
			hint++;
			update_cart();
		});
		$("#extra").click(function(e){
			extra++;
			update_cart();
		});
		$("#remove").click(function(e){
			hint = 0;
			extra = 0;
			nickname = 0;
			update_cart();
		});
		$("#buy").click(function(e){
			//totalan
			var total = 0;
			if(cash >= total){
				alert("Sukses melakukan transaksi!");
				cash -= total;
				buy(hint,extra,nickname,cash);
				hint = 0;
				extra = 0;
				nickname = 0;
				update_cart();
			}else{
				alert("Maaf uang tidak mencukupi!");
			}
		});
		
		$("#addcash").click(function(e){
			$(location).attr('href','addcash.php');
		});
	});
</script>
</html>