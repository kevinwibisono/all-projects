<?php
    include("../connection.php");
    date_default_timezone_set("Asia/Bangkok");
    $target_dir = "../uploads/";
    $target_file = $target_dir . basename($_FILES["file"]["name"]);
    $imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));
    move_uploaded_file($_FILES["file"]["tmp_name"], $target_file);

    $file_id = "";
    $res = mysqli_query($conn, "SELECT ifnull(max(CAST(substr(file_id, 3) AS DECIMAL)),0)+1 as max FROM project_files");
    while($r = mysqli_fetch_array($res)){
        $file_id = "FL0".$r['max'];
    }

    $datenow = date("Y-m-d H:i:s");
    $staff_name = $_POST['staff_name'];
    $id = $_POST['id'];
    $filename = basename($_FILES["file"]["name"]);

    mysqli_query($conn, "INSERT INTO project_files VALUES('$file_id', '$id', '$filename', '$staff_name', '$datenow')");
?>