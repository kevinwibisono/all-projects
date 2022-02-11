<?php
    include("../connection.php");
    date_default_timezone_set("Asia/Bangkok");
    $current_staff = $_POST['current_staff'];
    $staff_name = $_POST['staff_name'];
    $password = $_POST['password'];
    $position = $_POST['position'];
    $project_id = $_POST['project_id'];
    $id = "";
    $res = mysqli_query($conn, "SELECT ifnull(max(CAST(substr(staff_id, 3) AS DECIMAL)),0)+1 as max FROM master_staff");
    while($r = mysqli_fetch_array($res)){
        $id = "ST0".$r['max'];
    }
    
    $datenow = date("Y-m-d H:i:s");
    mysqli_query($conn, "INSERT INTO master_staff VALUES('$id', '$staff_name', '$password', '$position', 0, '$project_id', '$current_staff', '$datenow')");
?>