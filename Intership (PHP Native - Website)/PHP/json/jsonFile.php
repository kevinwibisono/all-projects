<?php
    include("../connection.php");
    $project_id = $_GET['project_id'];
    $arr = [];
    $result = mysqli_query($conn, "SELECT * FROM project_files WHERE project_id = '$project_id' ORDER BY waktuproses DESC");
    while($r = mysqli_fetch_array($result)){
        $data['file_id'] = $r['file_id'];
        $data['project_id'] = $r['project_id'];
        $data['filename'] = $r['filename'];
        $data['operator'] = $r['operator'];
        $data['waktuproses'] = $r['waktuproses'];
        array_push($arr, $data);
    }
    echo json_encode($arr);
?>