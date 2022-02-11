<?php
    include("../connection.php");

    $detailList = [];

    $projectid = $_GET['project_id'];

    $result = mysqli_query($conn, "SELECT * FROM project_detail_l1 WHERE project_id = '$projectid' ORDER BY step");
    while($row = mysqli_fetch_array($result)){
        $data["detail_id"] = $row["detail_id"];
        $data["detail_name"] = $row["detail_name"];
        $data["detail_start"] = $row["detail_start"];
        $data["detail_end"] = $row["detail_end"];
        $data["predecessor"] = $row["predecessor"];
        $data["step"] = $row["step"];
        
        array_push($detailList, $data);
    }
    mysqli_free_result($result);

    echo json_encode($detailList);
?>