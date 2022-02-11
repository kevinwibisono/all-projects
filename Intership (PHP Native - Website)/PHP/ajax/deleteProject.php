<?php  
    include("../connection.php");
    $project_id = $_GET['project_id'];
        
    //URUTAN delete project:
    $result = mysqli_query($conn, "SELECT detail_id FROM project_detail_l1 WHERE project_id = '$project_id'");
    while($row = mysqli_fetch_array($result)){
        $detail_id = $row['detail_id'];

        //1. delete adjust
        $res = mysqli_query($conn, "SELECT sub_detail_id FROM project_detail_l2 WHERE detail_id = '$detail_id'");
        while($r = mysqli_fetch_array($res)){
            $sub_id = $r['sub_detail_id'];
            mysqli_query($conn, "DELETE FROM project_adjust WHERE sub_detail_id = '$sub_id'");
        }

        //2. delete level 2
        mysqli_query($conn, "DELETE FROM project_detail_l2 WHERE detail_id = '$detail_id'");
    }
    //3. delete level 1
    mysqli_query($conn, "DELETE FROM project_detail_l1 WHERE project_id = '$project_id'");
    //4. delete staff khusus
    mysqli_query($conn, "DELETE FROM master_staff WHERE project_id = '$project_id'");
    //5. delete project
    mysqli_query($conn, "DELETE FROM project_header WHERE project_id = '$project_id'");
?>