<?php
    include("../connection.php");
    date_default_timezone_set("Asia/Bangkok");
    $datenow = date("Y-m-d H:i:s");
    $project_id = $_POST['id'];
    $project_name = $_POST['name'];
    $domain_admin = 'https://'.str_replace(['https://', ' '], '', strtolower($_POST['admindomain']));
    $domain_name = 'https://'.str_replace(['https://', ' '], '', strtolower($_POST['domain']));
    $project_value = $_POST['value'];
    $start_date = $_POST['start_date']; $returnData = [];

    $duration = $_POST['duration'];

    $result = mysqli_query($conn, "SELECT * FROM project_detail_l1 WHERE project_id = '$project_id'");
    while($row = mysqli_fetch_array($result)){
        $detail_id = $row['detail_id'];

        $res = mysqli_query($conn, "SELECT * FROM project_detail_l2 WHERE detail_id = '$detail_id'");
        while($r = mysqli_fetch_array($res)){
            $sub_id = $r['sub_detail_id'];
            if($r['sub_detail_start'] < $start_date){
                if($r['sub_detail_end'] < $start_date){
                    //ex: 27-06-2020 s/d 30-06-2020
                    //changed start_date to 01-07-2020
                    mysqli_query($conn, "UPDATE project_detail_l2 SET sub_detail_start = '$start_date', sub_detail_end = '$start_date', operator = '".$_POST['staff_name']."', waktuproses = '$datenow' WHERE sub_detail_id = '$sub_id'");
                }
                else{//ex: 27-06-2020 s/d 30-06-2020
                    //changed start_date to 28-06-2020
                    mysqli_query($conn, "UPDATE project_detail_l2 SET sub_detail_start = '$start_date', operator = '".$_POST['staff_name']."', waktuproses = '$datenow' WHERE sub_detail_id = '$sub_id'");
                }
            }
        }

        if($row['detail_start'] < $start_date){
            if($row['detail_end'] < $start_date){
                mysqli_query($conn, "UPDATE project_detail_l1 SET detail_start = '$start_date', detail_end = '$start_date', operator = '".$_POST['staff_name']."', waktuproses = '$datenow' WHERE detail_id = '$detail_id'");
            }
            else{
                mysqli_query($conn, "UPDATE project_detail_l1 SET detail_start = '$start_date', operator = '".$_POST['staff_name']."', waktuproses = '$datenow' WHERE detail_id = '$detail_id'");
            }
        }
    }

    mysqli_query($conn, "UPDATE project_header SET project_name = '$project_name', domain_admin = '$domain_admin', domain_name = '$domain_name', project_total = '$project_value', start_date = '$start_date', project_duration = '$duration' WHERE project_id = '$project_id'");

    $result = mysqli_query($conn, "SELECT DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end
 FROM project_header WHERE project_id = '$project_id'");
    while($row = mysqli_fetch_array($result)){
        $returnData['target_end'] = date_format(date_create($row["target_end"]), 'd-M-Y');
		$returnData['admin_domain'] = $domain_admin;
		$returnData['project_domain'] = $domain_name;
    }
    mysqli_free_result($result);

    echo json_encode($returnData);
?>