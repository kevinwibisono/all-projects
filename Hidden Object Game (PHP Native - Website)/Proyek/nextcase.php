<?php
	session_start();
	$case = $_SESSION['case'];
	$case += 1;
	$_SESSION['case'] = $case;
	$_SESSION['mission'] = 2;
	$_SESSION['removal'] = -1;
?>