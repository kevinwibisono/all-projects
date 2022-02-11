<?php
    include("../connection.php");
    $project_id = $_GET['project_id'];
    $arr = [];
    $result = mysqli_query($conn, "SELECT * FROM master_staff WHERE project_id = '$project_id'");
    while($r = mysqli_fetch_array($result)){
        $data['staff_id'] = $r['staff_id'];
        $data['staff_name'] = $r['staff_name'];
        $data['Password'] = $r['Password'];
        $data['staff_position'] = $r['staff_position'];
        $data['flgAsAdmin'] = $r['flgAsAdmin'];
        
        array_push($arr, $data);
    }

    echo json_encode($arr);
?>