<?php
	include "conn.php";
	session_start();
	
	$player = $_SESSION['curplayer'];
	
?>

<html>
	<head>
		<script type="text/javascript" src="jquery.js"></script>
		<style>
			#container{
				position: absolute;
				width: 800px;
				height: 600px;
				background-color: black;
			}
			
			#back{
				position: absolute;
				background-color: yellow;
				width: 50px;
				height: 50px;
				margin: 10px;
			}
		</style>
	</head>
	<body>
		<div id="container">
			<div id="back">BACK</div>
		</div>
	</body>
</html>