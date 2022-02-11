<?php
    include("../connection.php");

    $jsonData = [];
    $done = []; $progress = [];

    //ambil yang sudah selesai
    $query = "SELECT l2.*, l1.detail_name FROM project_detail_l1 l1, project_detail_l2 l2 WHERE l2.detail_id = l1.detail_id and l1.project_id = '".$_GET['project_id']."' and l2.staff_id = '".$_GET['staff_id']."' and (l2.flag = 1 or l2.sub_detail_end < '".date('Y-m-d')."') ORDER BY CAST(substr(l2.detail_id, 3) AS DECIMAL) ASC";

    $result = mysqli_query($conn, $query);
    while($row = mysqli_fetch_array($result)){
        $data["detail_id"] = $row["detail_id"];
        $data["detail_name"] = $row["detail_name"];
        $data["sub_detail_id"] = $row["sub_detail_id"];
        $data["sub_detail_name"] = $row["sub_detail_name"];
        $data["sub_detail_start"] = date_format(date_create($row["sub_detail_start"]), 'd-M-Y');
        $data["sub_detail_end"] = date_format(date_create($row["sub_detail_end"]), 'd-M-Y');
        $data["staff_id"] = $row["staff_id"];
        $data["staff_note"] = $row["staff_note"];
        $data["flag"] = $row["flag"];
        $data["predecessor"] = $row["predecessor"];
        $data["step"] = $row["step"];
        
        array_push($done, $data);
    }
    mysqli_free_result($result);

    //ambil yang belum selesai (SYARAT: sub detail end belum terlwati dan status flag masih 0)
    //jika sub detail end belum lewat tetapi FLAG sudah done, masuk kategori done
    $query = "SELECT l2.*, l1.detail_name FROM project_detail_l1 l1, project_detail_l2 l2 WHERE l2.detail_id = l1.detail_id and l1.project_id = '".$_GET['project_id']."' and l2.staff_id = '".$_GET['staff_id']."' and l2.flag = 0 and l2.sub_detail_end >= '".date('Y-m-d')."' ORDER BY CAST(substr(l2.detail_id, 3) AS DECIMAL) ASC";

    $result = mysqli_query($conn, $query);
    while($row = mysqli_fetch_array($result)){
        $data["detail_id"] = $row["detail_id"];
        $data["detail_name"] = $row["detail_name"];
        $data["sub_detail_id"] = $row["sub_detail_id"];
        $data["sub_detail_name"] = $row["sub_detail_name"];
        $data["sub_detail_start"] = date_format(date_create($row["sub_detail_start"]), 'd-M-Y');
        $data["sub_detail_end"] = date_format(date_create($row["sub_detail_end"]), 'd-M-Y');
        $data["staff_id"] = $row["staff_id"];
        $data["staff_note"] = $row["staff_note"];
        $data["flag"] = $row["flag"];
        $data["predecessor"] = $row["predecessor"];
        $data["step"] = $row["step"];
        
        array_push($progress, $data);
    }
    mysqli_free_result($result);

    $jsonData['done'] = $done;
    $jsonData['progress'] = $progress;

    echo json_encode($jsonData);
?>