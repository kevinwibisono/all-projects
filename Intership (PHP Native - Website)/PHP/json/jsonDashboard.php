<?php
    include("../connection.php");
    date_default_timezone_set("Asia/Bangkok");
    $datenow = date("Y-m-d");
    
    $data['done'] = 0; $data['inProgress'] = 0; $data['totalProjects'] = 0;
    
    $flag = $_GET['flag'];
    $staff_id = $_GET['staff_id'];

    //HITUNG JUMLAH TOTAL PROJECT, IN PROGRESS DAN DONE NYA
    if($flag == 0){
        $result = mysqli_query($conn, "SELECT * FROM project_detail_l2 WHERE staff_id = '".$staff_id."'");
        while($r = mysqli_fetch_array($result)){
            //cek flag setiap 
            if($r['flag'] == 1){
                $data['done']++;
            }
            else{
                $data['inProgress']++;
            }
        }

        $result = mysqli_query($conn, "SELECT * FROM project_detail_l2 WHERE staff_id = '".$staff_id."' and sub_detail_start <= '$datenow' and sub_detail_end >= '$datenow'");
        $data['totalProjects'] = mysqli_num_rows($result);
    }
    else{
        $query = "SELECT *, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header";
        $data['totalProjects'] = mysqli_num_rows(mysqli_query($conn, $query));
        
        $query = "SELECT p.*, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header p, project_detail_l1 l1, project_detail_l2 l2 WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id GROUP BY p.project_id HAVING max(l2.sub_detail_end) >= '$datenow'";
        $data['inProgress'] = mysqli_num_rows(mysqli_query($conn, $query));
        
        $query = "SELECT p.*, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header p, project_detail_l1 l1, project_detail_l2 l2 WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id GROUP BY p.project_id HAVING max(l2.sub_detail_end) < '$datenow'";
        $data['done'] = mysqli_num_rows(mysqli_query($conn, $query));
    }

    echo json_encode($data);
?>