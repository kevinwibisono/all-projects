<?php
    include("../connection.php");

    $adjustList = [];
    $result = mysqli_query($conn, "SELECT * FROM project_adjust");
    while($r = mysqli_fetch_array($result)){
        $data['adjust_id'] = $r['adjust_id'];
        $data['sub_detail_id'] = $r['sub_detail_id'];
        $data['adjust_comment'] = $r['adjust_comment'];
        $data['adjust_duration'] = $r['adjust_duration'];
        array_push($adjustList, $data);
    }
    mysqli_free_result(($result));

    echo json_encode($adjustList);
?>