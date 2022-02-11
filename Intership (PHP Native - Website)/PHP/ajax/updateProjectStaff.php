<?php
    include("../connection.php"); 
    date_default_timezone_set("Asia/Bangkok");
    $id = $_POST['staff_id'];
    $name = $_POST['staff_name'];
    $password = $_POST['Password'];
    $position = $_POST['staff_position'];
    $operator = $_POST['current_staff'];
    $datenow = date("Y-m-d H:i:s");

    mysqli_query($conn, "UPDATE master_staff SET staff_name = '$name', Password = '$password', staff_position = '$position', operator = '$operator', waktuproses = '$datenow' WHERE staff_id = '$id'");
?>