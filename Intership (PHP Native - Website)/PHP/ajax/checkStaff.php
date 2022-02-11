<?php
    include("../connection.php");
    $staff_id = $_GET['id'];

    $result = mysqli_query($conn, "SELECT p.* FROM project_header p, project_detail_l1 l1, project_detail_l2 l2 WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id and l2.staff_id = '$staff_id' GROUP BY p.project_id");
    $rowCount = mysqli_num_rows($result);
    mysqli_free_result($result);

    echo $rowCount;
?>