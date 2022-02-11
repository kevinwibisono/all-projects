<?php
	session_start();
	if(isset($_POST['stage'])){
		$stage = $_POST['stage'];
		$_SESSION['stage'] = $stage;
	}
?>