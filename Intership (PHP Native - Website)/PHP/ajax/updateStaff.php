<?php
    include("../connection.php");
    date_default_timezone_set("Asia/Bangkok");
    $datenow = date("Y-m-d H:i:s");
    $staffId = $_POST['id'];
    $staffName = $_POST['name'];
    $staffPassword = $_POST['password'];
    $staffPosition = $_POST['position'];
    $staffFlag = $_POST['flag'];

    $result = mysqli_query($conn, "UPDATE master_staff SET staff_name = '$staffName', Password = '$staffPassword', staff_position = '$staffPosition', flgAsAdmin = $staffFlag, operator = '".$_POST['staff_name']."', waktuproses = '$datenow' WHERE staff_id = '$staffId'");

    echo $datenow;
?>