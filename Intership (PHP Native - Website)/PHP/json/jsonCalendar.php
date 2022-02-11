<?php
    //FILE INI AKAN MENGEMBALIKAN JSON DGN FORMAT YANG DIBUTUHKAN OLEH FULLCALENDAR
    include("../connection.php");

    $subdetailList = [];

    $projectid = $_GET['project_id'];

    $result = mysqli_query($conn, "SELECT l2.*, s.staff_name FROM project_detail_l1 l1, project_detail_l2 l2 LEFT JOIN master_staff s ON(s.staff_id = l2.staff_id) WHERE l2.detail_id = l1.detail_id and l1.project_id = '$projectid' ORDER BY step");
    while($row = mysqli_fetch_array($result)){
        $data['title'] = $row['sub_detail_name'];
        $data['start'] = $row['sub_detail_start'];
        $data['end'] = date_format(date_add(date_create($row['sub_detail_end']),date_interval_create_from_date_string("1 days")), 'Y-m-d');
        $data['description'] = "Not Assigned";
        if($row['staff_name'] != null){
            $data['description'] = "Assigned to ".$row['staff_name'];
        }
        if(date_format(date_create($row['sub_detail_end']), 'Y/m/d') >= date("Y/m/d")){
            $data['backgroundColor'] = 'lightblue';
        }
        else{
            $data['backgroundColor'] = 'dodgerblue';
        }
        array_push($subdetailList, $data);
    }
    mysqli_free_result($result);

    echo json_encode($subdetailList);
?>