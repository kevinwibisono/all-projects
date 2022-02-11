<?php
    include("../connection.php");

    $subdetailList = [];

    $projectid = $_GET['project_id'];

    $query = "SELECT l2.*, s.staff_name FROM project_detail_l1 l1, project_detail_l2 l2 LEFT JOIN master_staff s ON(s.staff_id = l2.staff_id) WHERE l2.detail_id = l1.detail_id and l1.project_id = '$projectid' ORDER BY step";

    $result = mysqli_query($conn, $query);
    while($row = mysqli_fetch_array($result)){
        $data["detail_id"] = $row["detail_id"];
        $data["sub_detail_id"] = $row["sub_detail_id"];
        $data["sub_detail_name"] = $row["sub_detail_name"];
        $data["sub_detail_start"] = $row["sub_detail_start"];
        $data["sub_detail_end"] = $row["sub_detail_end"];
        $data["staff_id"] = $row["staff_id"];
        $data["staff_note"] = $row["staff_note"];
        $data["flag"] = $row["flag"];
        $data["predecessor"] = $row["predecessor"];
        $data["step"] = $row["step"];
        $data["staff_name"] = $row["staff_name"];
        
        array_push($subdetailList, $data);
    }
    mysqli_free_result($result);

    echo json_encode($subdetailList);
?>