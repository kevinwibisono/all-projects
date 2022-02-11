<?php 
	session_start();
	$mis = $_SESSION['mission'];
	$mis -= 1;
	$_SESSION['mission'] = $mis;
	echo $mis;
?>