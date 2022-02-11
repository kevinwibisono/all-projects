<?php
	session_start();
	$hint = $_SESSION['hint'];
	$hint -= 1;
	$_SESSION['hint'] = $hint;
?>