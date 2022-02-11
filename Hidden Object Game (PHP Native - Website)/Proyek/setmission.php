<?php
	session_start();
	if(isset($_POST['mission'])){
		$mission = $_POST['mission'];
		
		$_SESSION['mission'] = $mission;
	}
?>