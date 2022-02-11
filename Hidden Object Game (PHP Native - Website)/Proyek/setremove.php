<?php
	session_start();
	if(isset($_POST['number'])){
		$number = $_POST['number'];
		$_SESSION['removal'] = $number;
	}
?>