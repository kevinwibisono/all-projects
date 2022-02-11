<?php
	session_start();
	if(isset($_POST['num'])){
		$num = $_POST['num'];
		$mission = $_SESSION['mission'];
		$mission[$num] = true;
		$_SESSION['mission'] = $mission;
	}
?>