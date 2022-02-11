<?php
    if (! isset($_SERVER['HTTPS']) or $_SERVER['HTTPS'] == 'off' ) {
        $redirect_url = "https://" . $_SERVER['HTTP_HOST'] . $_SERVER['REQUEST_URI'];
        header("Location: $redirect_url");
        exit();
    }
	error_reporting(E_ALL ^ E_DEPRECATED);
    $conn = mysqli_connect("localhost", "u652342532_softq", "Calendar1", "u652342532_softq");
?>