<?php
    include("../connection.php");
    $id = $_POST['id'];

    mysqli_query($conn, "DELETE FROM master_staff WHERE staff_id='$id'");
?>