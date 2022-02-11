<?php
	include "conn.php";
	session_start();
	$player = $_SESSION['curplayer'];
	$player = $_POST['nama'];
	new_user($player,"123");
	
	$sql = "SELECT * FROM user WHERE username='$player'";
	$result = mysqli_query($link,$sql);
	
	while($row = mysqli_fetch_assoc($result)){
		$_SESSION["hint"] = $row["clue"];
		$_SESSION["extratime"] = $row["extratime"];
		$_SESSION["gold"]=$row["gold"];
		$_SESSION["change_nn"]=$row["change_nn"];
		$_SESSION['curplayer'] = $row['username'];
	}	
	 echo $_SESSION['curplayer'];
?>