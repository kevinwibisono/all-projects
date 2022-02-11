<?php
    include("../connection.php");

    $projectList = [];

    $query = "SELECT *, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header WHERE start_date <= '".$_GET['range_end']."' and start_date >= '".$_GET['range_start']."'";

    $result = mysqli_query($conn, $query);
    while($row = mysqli_fetch_array($result)){
        $data["project_id"] = $row["project_id"];
        $data["project_name"] = $row["project_name"];
        $data["domain_admin"] = $row["domain_admin"];
        $data["domain_name"] = $row["domain_name"];
        $data["project_total"] = $row['project_total'];
        $data["start_date"] = $row['start_date'];
        $data["target_end"] = date_format(date_create($row['target_end']), 'd-M-Y');
        $data["project_duration"] = $row["project_duration"];
        
        array_push($projectList, $data);
    }
    mysqli_free_result($result);

    echo json_encode($projectList);
?>