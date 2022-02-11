<?php
    include("../connection.php");
    $project_id = $_POST['id'];
    $project_name = $_POST['name'];
    $admin_domain = 'https://'.str_replace(['https://', ' '], '', strtolower($_POST['admindomain']));
    $project_domain = 'https://'.str_replace(['https://', ' '], '',strtolower($_POST['domain']));
    $project_value = $_POST['value'];
    $start_date = $_POST['start_date'];
    $duration = $_POST['duration']; $returnData = [];

    mysqli_query($conn, "INSERT INTO project_header VALUES('$project_id', '$project_name', $project_value, '$start_date', $duration, '$project_domain', '$admin_domain')");

    $result = mysqli_query($conn, "SELECT DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end
 FROM project_header WHERE project_id = '$project_id'");
    while($row = mysqli_fetch_array($result)){
        $returnData['target_end'] = date_format(date_create($row["target_end"]), 'd-M-Y');
		$returnData['admin_domain'] = $admin_domain;
		$returnData['project_domain'] = $project_domain;
    }
    mysqli_free_result($result);

    echo json_encode($returnData);
?>