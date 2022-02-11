<?php
    include("../connection.php");

    $staffList = []; $query = "SELECT * FROM master_staff";

    if(isset($_GET['project_id'])){
        $query = "SELECT s.* FROM project_detail_l1 l1, project_detail_l2 l2 , master_staff s WHERE l1.detail_id = l2.detail_id and l2.staff_id = s.staff_id and l1.project_id = '".$_GET['project_id']."' GROUP BY s.staff_id";
    }
    
    $result = mysqli_query($conn, $query);
    if(mysqli_num_rows($result) > 0){
        while($row = mysqli_fetch_array($result)){
            $data["staff_id"] = $row["staff_id"];
            $data["staff_name"] = $row["staff_name"];
            $data["Password"] = $row["Password"];
            $data["staff_position"] = $row["staff_position"];
            $data["flgAsAdmin"] = $row["flgAsAdmin"];
            $data["operator"] = $row["operator"];
            $data["waktuproses"] = $row["waktuproses"];
            $data["project_id"] = $row["project_id"];

            array_push($staffList, $data);
        }
        mysqli_free_result($result);
    }

    echo json_encode($staffList);
?>