<?php
    include("../connection.php");
    date_default_timezone_set("Asia/Bangkok");
    $datenow = date("Y-m-d H:i:s");
    $staffName = $_POST['name'];
    $staffPassword = $_POST['password'];
    $staffPosition = $_POST['position'];
    $staffFlag = $_POST['flag'];

    $id = "";
    $res = mysqli_query($conn, "SELECT ifnull(max(CAST(substr(staff_id, 3) AS DECIMAL)),0)+1 as max FROM master_staff");
    while($r = mysqli_fetch_array($res)){
        $id = "ST0".$r['max'];
    }

    $result = mysqli_query($conn, "INSERT INTO master_staff VALUES('$id', '$staffName', '$staffPassword', '$staffPosition', $staffFlag, '', '".$_POST['staff_name']."', '$datenow')");

    $data['id'] = $id;
    $data['waktuproses'] = $datenow;
    echo json_encode($data);
?>