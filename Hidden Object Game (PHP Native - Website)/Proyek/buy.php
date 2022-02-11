<?php
	session_start();
	include "conn.php";
	$_SESSION['hint'] += $_POST['hint'];
	$_SESSION["extratime"] += $_POST['extra'];
	$_SESSION["gold"] = 150;
	$_SESSION["change_nn"] += $_POST['change'];
	$thint = $_SESSION['hint'];
	$textra = $_SESSION['extratime'];
	$tgold = $_SESSION['gold'];
	$tchange = $_SESSION['change_nn'];
	$player = $_SESSION['curplayer'];
	
	$sql = "UPDATE user SET clue=$thint, extratime=$textra, gold=$tgold, change_nn=$tchange WHERE username='$player'";
	mysqli_query($link,$sql);
?>