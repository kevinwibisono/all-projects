<?php
    include("connection.php");
    date_default_timezone_set("Asia/Bangkok");
    session_start();
    $operator = ""; $waktuproses = "0000-00-00 00:00:00"; $scrollTop = 0; $focusId = -1;
    $dividerSize = "500px"; $divSize = "500px";
    $jumSubDetail = 0; $visibility = "visible"; $disabled = "enabled";
    $current_name = "--"; $current_staff = [];
    if(isset($_SESSION['staff_login'])){
        $current_staff = $_SESSION['staff_login'];
        $current_name = $current_staff['staff_name'];
        if($current_staff['flgAsAdmin'] == 0){
            $visibility = "hidden";
            $disabled = "disabled";
        }
    }
    else{
        header("location:index.php");
    }
    $error = "";

    $chosen_project = [];
    if(isset($_SESSION['chosen_project'])){
        $chosen_project = $_SESSION['chosen_project'];
    }
    else{
        header("location:listproject.php");
    }
    $projectid = $chosen_project['project_id'];
    $projectStartDate = $chosen_project['start_date'];
    
    $query = "SELECT * FROM project_detail_l1 WHERE project_id = '$projectid' ORDER BY step";

    //get Level 1 and Level 2 of project
    $level1 = [];
    $level2 = [];
    $adjusts = [];
    $files = [];
    
    if(isset($_POST['addSubDetail'])){
        if(isset($_SESSION['scroll-sidebar'])){
            $scrollTop = $_SESSION['scroll-sidebar'];
            $focusId = $_SESSION['focus'];
        }
        $datenow = date("Y-m-d H:i:s");
        $date = date("Y-m-d");
        $detail_id = $_POST['detailId'];
        $name = $_POST['newSubDetail'];
        
        if($name != ""){
            $pre = "";

            $nextstep = 0;

            $step = mysqli_query($conn, "SELECT ifnull(max(l2.step),0)+1 as max FROM project_detail_l2 l2, project_detail_l1 l1 WHERE l2.detail_id = l1.detail_id and l1.project_id = '$projectid'");
            while($s = mysqli_fetch_array($step)){
                $nextstep = $s['max'];
            }

            if($nextstep > 1){
                //sub detail kedua dst, perlu ada predecessor
                //ambil id dgn step/urutan terbesar sebagai predecessor
                $findPre = mysqli_query($conn, "SELECT sub_detail_id FROM project_detail_l2 WHERE step IN (SELECT max(l2.step) as step FROM project_detail_l2 l2, project_detail_l1 l1 WHERE l2.detail_id = l1.detail_id and l1.project_id = '$projectid')");
                while($s = mysqli_fetch_array($findPre)){
                    $pre = $s['sub_detail_id'];
                }
            }

            $id = "";
            $res = mysqli_query($conn, "SELECT ifnull(max(CAST(substr(sub_detail_id, 3) AS DECIMAL)),0)+1 as max FROM project_detail_l2");
            while($r = mysqli_fetch_array($res)){
                $id = "SD0".$r['max'];
            }

            $res = mysqli_query($conn, "SELECT detail_start FROM project_detail_l1 WHERE detail_id = '$detail_id'");
            while($r = mysqli_fetch_array($res)){
                $date = $r['detail_start'];
            }

            mysqli_query($conn, "INSERT INTO project_detail_l2 VALUES('$detail_id', '$id', '$name', '$date', '$date', '', '', 0, '$pre', $nextstep, '$current_name', '$datenow')"); 
        }
    }

    if(isset($_POST['addDetail'])){
        $datenow = date("Y-m-d H:i:s");
        $date = date("Y-m-d");
        $name = $_POST['newDetail'];
        
        if($name != ""){
            $nextstep = 0;
            $pre = "";

            $step = mysqli_query($conn, "SELECT ifnull(max(step),0)+1 as max FROM project_detail_l1 WHERE project_id = '$projectid'");
            while($s = mysqli_fetch_array($step)){
                $nextstep = $s['max'];
            }

            if($nextstep > 1){
                //sub detail kedua dst, perlu ada predecessor
                //ambil id dgn step/urutan terbesar sebagai predecessor
                $findPre = mysqli_query($conn, "SELECT detail_id FROM project_detail_l1 WHERE step IN (SELECT max(step) as step FROM project_detail_l1 WHERE project_id = '$projectid')");
                while($s = mysqli_fetch_array($findPre)){
                    $pre = $s['detail_id'];
                }
            }

            $id = "";
            $res = mysqli_query($conn, "SELECT ifnull(max(CAST(substr(detail_id, 3) AS DECIMAL)),0)+1 as max FROM project_detail_l1");
            while($r = mysqli_fetch_array($res)){
                $id = "DT0".$r['max'];
            }

            mysqli_query($conn, "INSERT INTO project_detail_l1 VALUES('$projectid', '$id', '$name', '".$chosen_project['start_date']."', '".$chosen_project['start_date']."', '$pre', $nextstep, '$current_name', '$datenow')");
        }
    }

    if(isset($_POST['deleteDetailSubmit'])){
        //urutan delete detail level 1:
        //1. Delete semua adjust yg berkaitan dgn level 2 nya
        //2. Delete semua detail level 2 yang berkaitan (termasuk pengaturan predecessor)
        //3. Atur predecessor kemudian delete detail level 1 nya
        
        $id = $_POST['id'];
        $step = -1;
        $oldPre = "";
        $res = mysqli_query($conn, "SELECT step, predecessor FROM project_detail_l1 WHERE detail_id = '$id'");
        while($r = mysqli_fetch_array($res)){
            $step = $r['step'];
            $oldPre = $r['predecessor'];
        }
        
        //jika dlm detail yg akan dihapus trdpt > 1 sub, maka ambil step yg terakhir & pre yg pertama, serta hitung jumlah sub nya utk pengurangan step
        $last_sub_step; $first_old_pre; $count = 0;
        $result = mysqli_query($conn, "SELECT * FROM project_detail_l2 WHERE detail_id = '$id'");
        while($r = mysqli_fetch_array($result)){
            $count++;
            if($count == 1){
                $first_old_pre = $r['predecessor'];
            }
            $last_sub_step = $r['step'];
            $sub_id = $r['sub_detail_id'];
            mysqli_query($conn, "DELETE FROM project_adjust WHERE sub_detail_id = '$sub_id'");
        }
        
        //geser step & predecessor ketika suatu sub detail dihapus
        mysqli_query($conn, "UPDATE project_detail_l2 SET predecessor = '$first_old_pre' WHERE step = $last_sub_step+1 and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");
        mysqli_query($conn, "UPDATE project_detail_l2 SET step = step - $count WHERE step > $last_sub_step and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");
        
        mysqli_query($conn, "DELETE FROM project_detail_l2 WHERE detail_id = '$id'");
        
        //geser step & predecessor ketika suatu detail dihapus
        mysqli_query($conn, "UPDATE project_detail_l1 SET predecessor = '$oldPre' WHERE project_id = '$projectid' and step = $step+1");
        mysqli_query($conn, "UPDATE project_detail_l1 SET step = step -1 WHERE project_id = '$projectid' and step > $step");
        mysqli_query($conn, "DELETE FROM project_detail_l1 WHERE detail_id = '$id'");
    }

    if(isset($_POST['deleteSubSubmit'])){
        $id = $_POST['id'];
        $step = -1;
        $oldPre = "";
        $res = mysqli_query($conn, "SELECT step, predecessor FROM project_detail_l2 WHERE sub_detail_id = '$id'");
        while($r = mysqli_fetch_array($res)){
            $step = $r['step'];
            $oldPre = $r['predecessor'];
        }
        
        //geser step & predecessor ketika suatu sub detail dihapus
        mysqli_query($conn, "UPDATE project_detail_l2 SET predecessor = '$oldPre' WHERE step = $step+1 and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");
        mysqli_query($conn, "UPDATE project_detail_l2 SET step = step -1 WHERE step > $step and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");
        
        mysqli_query($conn, "DELETE FROM project_adjust WHERE sub_detail_id = '$id'");
        mysqli_query($conn, "DELETE FROM project_detail_l2 WHERE sub_detail_id = '$id'");
    }

    if(isset($_POST['updatel1Detail'])){
        
        $datenow = date("Y-m-d H:i:s");
        
        //Dua hal yang perlu diperhatikan ketika update tanggal
        //1. Jika start_date maju, maka majukan semua start_date di l2 yang melanggar (lebih awal)
        //2. Jika end_date mundur, maka mundurkan semua end_date l2 yang melanggar (lebih akhir)
        $id = $_POST['id'];
        $name = $_POST['name'];
        $start_date = $_POST['start_date'];
        $end_date = $_POST['end_date'];
        $pre = $_POST['pre'];
        $oldStep = 0; $newStep = 0;
        $oldPre = "";
        $res = mysqli_query($conn, "SELECT step, predecessor FROM project_detail_l1 WHERE detail_id = '$id'");
        while($r = mysqli_fetch_array($res)){
            $oldPre = $r['predecessor'];
            $oldStep = $r['step'];
        }
        
        if($pre == "first"){
            //dinaikkan paling atas
            //step seluruh detail dibawahnya + 1
            //update predecessor dr detail plg awal 
            $pre = "";
            $newStep = 1;
            if($oldStep > 1){
                mysqli_query($conn, "UPDATE project_detail_l1 SET step = step + 1 WHERE project_id = '$projectid' and detail_id <> '$id'");            
                mysqli_query($conn, "UPDATE project_detail_l1 SET predecessor = '$id' WHERE project_id = '$projectid' and predecessor = ''");
            }
        }
        else{
            
            //setelah detail yg lain, tukar step, tukar predecessor
            //predecessor diisi detail tsb
            $res = mysqli_query($conn, "SELECT step FROM project_detail_l1 WHERE detail_id = '$pre'");
            while($r = mysqli_fetch_array($res)){
                $newStep = $r['step'];
            }
            if($oldStep < $newStep){
                //ubah ke bawah
                //1. Ubah predecessor detail yg tepat di bawahnya (sblm update) menjadi predecessor miliknya
                //2. Kurangi step setiap detail yang dilewati
                //3. Ubah predecessor detail yg tepat di bawahnya (stlh update) menjadi detail skrg
                mysqli_query($conn, "UPDATE project_detail_l1 SET predecessor = '$oldPre' WHERE project_id = '$projectid' and step = $oldStep + 1");                
                mysqli_query($conn, "UPDATE project_detail_l1 SET predecessor = '$id' WHERE project_id = '$projectid' and step = $newStep + 1");
                mysqli_query($conn, "UPDATE project_detail_l1 SET step = step - 1 WHERE project_id = '$projectid' and step <= $newStep and step > $oldStep");
            }
            else if($oldStep > $newStep){
                //ubah ke atas
                //1. Ubah predecessor detail yg tepat di bawahnya (stlh update) menjadi detail skrg
                //2. Tambahkan step setiap detail yang dilewati
                mysqli_query($conn, "UPDATE project_detail_l1 SET predecessor = '$oldPre' WHERE project_id = '$projectid' and step = $newStep + 1");
                mysqli_query($conn, "UPDATE project_detail_l1 SET step = step + 1 WHERE project_id = '$projectid' and step > $newStep and step < $oldStep");
                $newStep++;
            }
        }
        
        $result = mysqli_query($conn, "SELECT * FROM project_detail_l2 WHERE detail_id = '$id'");
        while($row = mysqli_fetch_array($result)){
            $sub_detail_id = $row['sub_detail_id'];

            if($row['sub_detail_start'] < $start_date){
                if($row['sub_detail_end'] < $start_date){
                    //ex: 17-06-2020 to 20-06-2020
                    //changed start_date 29-06-2020, both start and end date is invalid
                    mysqli_query($conn, "UPDATE project_detail_l2 SET sub_detail_start = '$start_date', sub_detail_end = '$start_date', operator = '$current_name', waktuproses = '$datenow' WHERE sub_detail_id = '$sub_detail_id'");
                }
                else{
                    //ex: 27-06-2020 to 30-06-2020
                    //changed start_date 29-06-2020, only start date is invalid
                    mysqli_query($conn, "UPDATE project_detail_l2 SET sub_detail_start = '$start_date', operator = '$current_name', waktuproses = '$datenow' WHERE sub_detail_id = '$sub_detail_id'");
                }
            }

            if($row['sub_detail_end'] > $end_date){
                if($row['sub_detail_start'] > $end_date){
                    //ex: 29-06-2020 to 02-07-2020
                    //changed end_date 27-06-2020, both start and end date is invalid
                    mysqli_query($conn, "UPDATE project_detail_l2 SET sub_detail_start = '$end_date', sub_detail_end = '$end_date', operator = '$current_name', waktuproses = '$datenow' WHERE sub_detail_id = '$sub_detail_id'");
                }
                else{
                    //ex: 29-06-2020 to 02-07-2020
                    //changed end_date 30-06-2020, only end date is invalid
                    mysqli_query($conn, "UPDATE project_detail_l2 SET sub_detail_end = '$end_date', operator = '$current_name', waktuproses = '$datenow' WHERE sub_detail_id = '$sub_detail_id'");
                }
                mysqli_query($conn, "DELETE FROM project_adjust WHERE sub_detail_id = '$sub_detail_id'");
            }
        }
        
        mysqli_query($conn, "UPDATE project_detail_l1 SET detail_name = '$name', detail_start = '$start_date', detail_end = '$end_date', predecessor = '$pre', step = $newStep, operator = '$current_name', waktuproses = '$datenow' WHERE detail_id = '$id'");
    }

    if(isset($_POST['updatel2Detail'])){
        $datenow = date("Y-m-d H:i:s");
        $id = $_POST['id'];
        $name = $_POST['name'];
        $start_date = $_POST['start_date'];
        $end_date = $_POST['end_date'];
        $staff_id = $_POST['staff'];
        $pre = $_POST['pre'];
        $oldStep = 0; $newStep = 0;
        $oldPre = "";
        $res = mysqli_query($conn, "SELECT step, predecessor FROM project_detail_l2 WHERE sub_detail_id = '$id'");
        while($r = mysqli_fetch_array($res)){
            $oldPre = $r['predecessor'];
            $oldStep = $r['step'];
        }
        if($pre == "first"){
            //dinaikkan paling atas
            //step seluruh detail dibawahnya + 1
            //update predecessor dr detail plg awal 
            $pre = "";
            $newStep = 1;
            if($oldStep > 1){
                mysqli_query($conn, "UPDATE project_detail_l2 SET step = step + 1 WHERE detail_id <> '$id'");            
                mysqli_query($conn, "UPDATE project_detail_l2 SET predecessor = '$id' WHERE predecessor = ''");
            }
        }
        else{
            //setelah detail yg lain, tukar step, tukar predecessor
            //predecessor diisi detail tsb
            
            $res = mysqli_query($conn, "SELECT step FROM project_detail_l2 WHERE sub_detail_id = '$pre'");
            while($r = mysqli_fetch_array($res)){
                $newStep = $r['step'];
            }
            
            if($oldStep < $newStep){
                //ubah ke bawah
                //1. Ubah predecessor sub detail yg tepat di bawahnya (sblm update) menjadi predecessor miliknya
                //2. Kurangi step setiap sub detail yang dilewati
                //3. Ubah predecessor sub detail yg tepat di bawahnya (stlh update) menjadi sub detail skrg
                mysqli_query($conn, "UPDATE project_detail_l2 SET predecessor = '$oldPre' WHERE step = $oldStep + 1 and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");                
                mysqli_query($conn, "UPDATE project_detail_l2 SET predecessor = '$id' WHERE step = $newStep + 1 and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");
                mysqli_query($conn, "UPDATE project_detail_l2 SET step = step - 1 WHERE step <= $newStep and step > $oldStep and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");
            }
            else if($oldStep > $newStep){
                //ubah ke atas
                //1. Ubah predecessor sub detail yg tepat di bawahnya (stlh update) menjadi sub detail skrg
                //2. Tambahkan step setiap sub detail yang dilewati
                mysqli_query($conn, "UPDATE project_detail_l2 SET predecessor = '$id' WHERE step = $newStep + 1 and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");
                mysqli_query($conn, "UPDATE project_detail_l2 SET step = step + 1 WHERE step > $newStep and step < $oldStep and detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid')");
                $newStep++;
            }
        }
        
        mysqli_query($conn, "UPDATE project_detail_l2 SET sub_detail_name = '$name', sub_detail_start = '$start_date', sub_detail_end = '$end_date', staff_id = '$staff_id', predecessor = '$pre', step = $newStep, operator = '$current_name', waktuproses = '$datenow' WHERE sub_detail_id = '$id'");
    }

    if(isset($_POST['l2AdjustSubmit'])){
        $datenow = date("Y-m-d H:i:s");
        $sub_id = $_POST['sub_id'];
        $id = $_POST['id'];
        $comment = $_POST['comment'];
        $duration = $_POST['duration'];
        if($id != ""){
            mysqli_query($conn, "UPDATE project_adjust SET adjust_comment = '$comment', adjust_duration = $duration WHERE adjust_id = '$id'");
        }
        else{
            $res = mysqli_query($conn, "SELECT ifnull(max(CAST(substr(adjust_id, 3) AS DECIMAL)),0)+1 as max FROM project_adjust");
            while($r = mysqli_fetch_array($res)){
                $id = "AD0".$r['max'];
            }
            mysqli_query($conn, "INSERT INTO project_adjust VALUES('$id', '$sub_id', $duration, '$comment', '$current_name', '$datenow')");
        }
    }

    if(isset($_POST['l2AdjustDelete'])){
        $adjust_id = $_POST['id'];
        mysqli_query($conn, "DELETE FROM project_adjust WHERE adjust_id = '$adjust_id'");
    }
    
    if(isset($_POST['btnSaveChange'])){
        $full_date = date('Y-m-d H:i:s');
        $id = $_POST['id'];
        $start = $_POST['start_date'];
        $end = $_POST['end_date'];
        $arrId = explode(";", $id);
        $arrStart = explode(";", $start);
        $arrEnd = explode(";", $end);
        for ($i=0; $i < count($arrId); $i++) { 
            if($i < count($arrId) -1){
                //data terakhir pasti kosong, tdk usah diupdate
                $start = date_format(date_create($arrStart[$i]), "Y-m-d");
                $end = date_format(date_create($arrEnd[$i]), "Y-m-d");
                mysqli_query($conn, "UPDATE project_detail_l2 SET sub_detail_start = '$start', sub_detail_end = '$end', operator = '$current_name', waktuproses = '$full_date' WHERE sub_detail_id = '$arrId[$i]'");
            }
        }
    }

    $result = mysqli_query($conn, $query);
    while($row = mysqli_fetch_array($result)){
        $id = $row["detail_id"];
        $data["detail_id"] = $row["detail_id"];
        $data["detail_name"] = $row["detail_name"];
        $data["detail_start"] = $row["detail_start"];
        $data["detail_end"] = $row["detail_end"];
        $data["step"] = $row["step"];
        array_push($level1, $data);
        
    }

    $result = mysqli_query($conn, "SELECT * FROM project_files WHERE project_id = '".$chosen_project['project_id']."' ORDER BY waktuproses DESC");
    while($row = mysqli_fetch_array($result)){
        $data["file_id"] = $row["file_id"];
        $data["project_id"] = $row["project_id"];
        $data["filename"] = $row["filename"];
        $data["operator"] = $row["operator"];
        $data["waktuproses"] = $row["waktuproses"];
        array_push($files, $data);
    }

    $res = mysqli_query($conn, "SELECT l2.*, s.staff_name FROM project_detail_l2 l2 LEFT JOIN master_staff s ON (l2.staff_id = s.staff_id) WHERE detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '$projectid') ORDER BY step");
    while($r = mysqli_fetch_array($res)){
        $jumSubDetail++;
        $sub["detail_id"] = $r["detail_id"];
        $sub_id = $r["sub_detail_id"];
        $sub["sub_detail_id"] = $r["sub_detail_id"];
        $sub["sub_detail_name"] = $r["sub_detail_name"];
        $sub["sub_detail_start"] = $r["sub_detail_start"];
        $sub["sub_detail_end"] = $r["sub_detail_end"];
        $sub["staff_note"] = $r["staff_note"];
        $sub["flag"] = $r["flag"];
        $sub["staff_id"] = $r["staff_id"];
        $sub["step"] = $r["step"];
        $sub["staff_name"] = $r["staff_name"];
        $operator = $r['operator'];
        $waktuproses = $r['waktuproses'];
        
        array_push($level2, $sub);

        $adjustquery = mysqli_query($conn, "SELECT * FROM project_adjust WHERE sub_detail_id = '$sub_id'");
        while($a = mysqli_fetch_array($adjustquery)){
            $ad["adjust_id"] = $a["adjust_id"];
            $ad["sub_detail_id"] = $a["sub_detail_id"];
            $ad["adjust_duration"] = $a["adjust_duration"];
            $ad["adjust_comment"] = $a["adjust_comment"];
            array_push($adjusts, $ad);
        }
    }

    $dividerSize = ($jumSubDetail*60+120);
    $divSize = ($jumSubDetail*60+120);
    

    $staffList = [];

    $detail_end; $maxL1Duration;

    $max_date = mysqli_query($conn, "SELECT max(detail_end) as max FROM project_detail_l1 WHERE project_id = '$projectid'");
    while($m = mysqli_fetch_array($max_date)){
        $detail_end = $m['max'];
    }

    if($detail_end > $chosen_project['target_end']){
        //jika detail end lbh besar dari estimate end, maka berarti ada yg molor, gunakan maxL1Duration
        $max_date = mysqli_query($conn, "SELECT max(DATEDIFF(detail_end, '$projectStartDate')) as max FROM project_detail_l1 WHERE project_id = '$projectid'");
        while($m = mysqli_fetch_array($max_date)){
            $maxL1Duration = $m['max'];
            $ulSize = (($maxL1Duration+1)*100)."px";
        }
        mysqli_free_result($max_date);
    }
    else{
        $maxL1Duration = $chosen_project['project_duration'];
        $ulSize = (($maxL1Duration+1)*100)."px";
    }

    $result = mysqli_query($conn, "SELECT * FROM master_staff WHERE project_id = '' or project_id = '".$chosen_project['project_id']."'");
    while($row = mysqli_fetch_array($result)){
        $data["staff_id"] = $row["staff_id"];
        $data["staff_name"] = $row["staff_name"];
        
        array_push($staffList, $data);
    }
    mysqli_free_result($result);
?>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" type="image/png" sizes="16x16" href="./imgicon/logo2.ico">
    <title>Project Scheduling</title>
    <!-- Semantic UI CSS (for popup)-->
    <link href="css/semantic.min.css" rel="stylesheet">
    <!-- Bootstrap Core CSS -->
    <link href="bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Menu CSS -->
    <link href="./plugins/bower_components/sidebar-nav/dist/sidebar-nav.min.css" rel="stylesheet">
    <!-- toast CSS -->
    <link href="./plugins/bower_components/toast-master/css/jquery.toast.css" rel="stylesheet">
    <!-- morris CSS -->
    <link href="./plugins/bower_components/morrisjs/morris.css" rel="stylesheet">
    <!-- chartist CSS -->
    <link href="./plugins/bower_components/chartist-js/dist/chartist.min.css" rel="stylesheet">
    <link href="./plugins/bower_components/chartist-plugin-tooltip-master/dist/chartist-plugin-tooltip.css" rel="stylesheet">
    <!-- animation CSS -->
    <link href="css/animate.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="css/style.css" rel="stylesheet">
    <!-- color CSS -->
    <link href="css/colors/default.css" id="theme" rel="stylesheet">
    <link href="css/graph.css" rel="stylesheet">
    
    <style>  
        body{
            overflow-y: hidden;
        }
        
        .singleFile{
            border: 1px solid black;
            margin: 10px;
            padding: 5px;
            max-height: 32px;
            max-width: 300px;
            float: left;
        }
        
        .fileDate{
            float: left;
            margin-top: 15px;
        }
        
        .dropdown-content {
          display: none;
          position: absolute;
          background-color: #f1f1f1;
          min-width: 160px;
          box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
          z-index: 99;
        }
        
        .dropdown-content li {
          color: black;
          padding: 12px 16px;
          text-decoration: none;
          display: block;
        }
        
        :root {
          --divider: lightgrey;
        }

        ul {
          list-style: none;
        }

        a {
          text-decoration: none;
          color: inherit;
        }

        #chart-container {
            height: <?=$divSize?>;
            clear: both;
            padding-top: 20px;
        }
        
        #chart-header{
            overflow: hidden;
        }
        
        <?php
            if($current_staff['flgAsAdmin'] == 0){
                ?>
                #chart-header li{
                    cursor: pointer;
                }
                <?php
            }
        ?>
        
        #task-container{
            overflow: auto;
            height: <?=($divSize-20)."px"?>;
        }

        .chart-values {
            position: relative;
            display: flex;
            padding: 0px;
            font-weight: bold;
            font-size: 1.2rem;
            width: <?=$ulSize?>;
            margin: auto;
            margin-bottom: 20px;
        }

        .chart-values li {
            flex: 1;
            text-align: center;
        }

        .chart-values li:not(:last-child) {
            position: relative;
        }

        .chart-values li:not(:last-child)::before {
            content: '';
            position: absolute;
            right: 0;
            height: <?=$dividerSize."px"?>;
            border-right: 1px solid var(--divider);
        }
        
        .chart-values-hidden {
            position: relative;
            display: flex;
            padding: 0px;
            font-weight: bold;
            font-size: 1.2rem;
            width: <?=$ulSize?>;
            margin: auto;
            margin-bottom: 20px;
        }

        .chart-values-hidden li {
            flex: 1;
            text-align: center;
        }

        .chart-values-hidden li:not(:last-child) {
            position: relative;
        }

        .chart-values-hidden li:not(:last-child)::before {
            content: '';
            position: absolute;
            right: 0;
            height: <?=$dividerSize."px"?>;
            border-right: 1px solid var(--divider);
        }
        
        .chart-bars{
            width: <?=$ulSize?>;
            padding: 0px;
            margin: auto;
        }

        .chart-bars li div{
            position: relative;
            cursor: pointer;
            display: inline-block;
            max-height: 37px;
            margin-bottom: 15px;
            font-size: 12px;
            border-radius: 20px;
            padding: 10px;
            color: white;
            font-weight: bold;
        }

        .chart-individual:hover{
            border: 2px solid black;
            box-sizing: border-box;
        }

        .chart-individual .resizers{
            display: none;
            width: 17px;
            height: 17px;
            top: 10px;
            background-color: white;
            position: absolute;
            border-radius: 50%;
            border: 2px solid black;
            cursor: ew-resize;
        }

        .chart-individual .upanddown{
            display: none;
            width: 30px;
            height: 20px;
            text-align: center;
            background-color: white;
            position: absolute;
            border-radius: 25%;
            border: 2px solid black;
            cursor: pointer;
        }
        
        .legend{
            width: 10px;
            height: 10px;
            margin: 0;
        }
		
		.dragger{
            cursor: move;
            overflow: hidden;
            max-width: 20px;
            max-height: 17px;
        }
		
		.btnJump{
              position: fixed;
              z-index: 99;
              border: none;
              outline: none;
              background-color: lightgrey;
              cursor: pointer;
              border-radius: 50%;
            top: 0px;
            min-width: 30px;
		}
        
        .btnJump:hover{ background-color: gray; }
		
		#jump7daysLeft{ padding-right: 10px; 300px; }
		
		#jump7daysRight{ padding-left: 10px; right: 70px; }
        
        .hrefUpload{ color: lightblue; }
        .hrefUpload:hover{ color: deepskyblue; }
    </style>
</head>

<body class="fix-header" onresize="resizeContainer()">
    <!-- Preloader -->
    <div class="preloader">
        <svg class="circular" viewBox="25 25 50 50">
            <circle class="path" cx="5000" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10" />
        </svg>
    </div>
    <!-- Wrapper -->
    <div id="wrapper">
        <!-- Topbar header - style you can find in pages.scss -->
        <nav class="navbar navbar-default navbar-static-top m-b-0">
            <div class="navbar-header">
                <div class="top-left-part">
                    <a class="logo" href="dashboard.php">
                        <!-- Logo icon image, you can use font-icon also -->
                        <b>
                            <img src="./imgicon/logo2.ico" style="width: 25px; height: 30px" alt="home" class="light-logo" />
                        </b>
                        <!-- Logo text image you can use text also -->
                        <span class="hidden-xs">
                            <h2 style="float: left; font-family: arial; padding-top: 15px; padding-left: 10px">SoftQ</h2>
                        </span> 
                    </a>
                </div>
                <!-- User login -->
                <ul class="nav navbar-top-links navbar-right pull-right">
                    <li>
                        <a class="nav-toggler open-close waves-effect waves-light hidden-md hidden-lg" href="javascript:void(0)"><i class="fa fa-bars"></i></a>
                    </li>
                    <li class="dropdown">
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#"><img src="./imgicon/AIQ61.png" alt="user-img" width="36" class="img-circle">&nbsp;<b class="hidden-xs"><?=$current_name?></b></a>
                        <ul class="dropdown-menu">
                            <li>
                                <a href="index.php" class="waves-effect"><i class="fa fa-sign-out" aria-hidden="true"></i>&nbsp;<b>Logout</b></a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
        <!-- End Top Navigation -->
        <!-- Left Sidebar - style you can find in sidebar.scss  -->
        <div class="navbar-default sidebar" role="navigation">
            <div class="sidebar-nav slimscrollsidebar" id="sidebarDiv">
                <div class="sidebar-head">
                    <h3><span class="fa-fw open-close"><i class="ti-close ti-menu"></i></span> <span class="hide-menu">Navigation</span></h3>
                </div>
                <ul class="nav" id="side-menu" style="padding-bottom: 150px;">
                    <li style="padding: 70px 0 0;">
                        <a href="listproject.php"><i class="fa fa-arrow-left" aria-hidden="true"></i>&nbsp;<b>Back</b></a>
                    </li>
                    <li>
                        <form method="post" autocomplete="off">
                            <?php
                                if($current_staff['flgAsAdmin'] == 0){
                                    echo "<input class='waves-effect' type='text' style='text-decoration: bold;' placeholder='Add New Job' name='newDetail' disabled>";
                                }
                                else{
                                    echo "<input class='waves-effect' type='text' style='text-decoration: bold;' placeholder='Add New Level 1 Detail' name='newDetail'>";
                                }
                            ?>
                            <input type="submit" style="position: absolute; left: -9999px; width: 1px; height: 1px; visibility: <?=$visibility?>" tabindex="-1" name="addDetail"/>
                        </form>
                    </li>
                    <?php
                        $ctr = -1;
                        foreach($level1 as $l1){
                            $ctr++;
                    ?>
                        <li>
                            <a id="detail<?=$ctr?>" class="details" style="cursor: pointer;">
                                <?php
                                    $datenow = date("Y-m-d");
                                    if($l1['detail_end'] < $datenow){
                                        //masanya sudah selesai
                                        ?>
                                        <i class="fa fa-check-square" aria-hidden="true"></i>
                                        <?php
                                    }
                                    else{
                                        //masih in progress
                                        ?>
                                        <i class="fa fa-square-o" aria-hidden="true"></i>
                                        <?php
                                    }
                                ?>
                                <b><?=$l1["detail_name"]?></b>&nbsp;
                                <i class="fa fa-caret-down"></i>
                            </a>
                            <div class="dropdown-content" style="margin-left: 15px;" id="dropDownDetail<?=$ctr?>">
                                <ul style="padding: 0px;">
                                    <li><button data-toggle="modal" style="border: none; width: 160px; text-align: left;" data-target="#myModal" onclick="displayL1(<?=$ctr?>)">Details</button></li>
                                    <li>
                                        <form class="deleteDetailForm" id="deleteDetailForm<?=$ctr?>" method="post">
                                            <input type="hidden" id="hiddenDetailId<?=$ctr?>" name="id" style="border: none;" value="<?=$l1['detail_id']?>">
                                            <input type="hidden" id="hiddenDetailName<?=$ctr?>" style="border: none;" value="<?=$l1['detail_name']?>">
                                            <input type="submit" style="border: none; width: 160px; text-align: left; visibility: <?=$visibility?>" name="deleteDetailSubmit" value="Delete">
                                        </form>
                                    </li>
                                </ul>
                            </div> 

                            <ul class="nav" style="padding-left:30px;">
                                <li>
                                    <form method="post" autocomplete="off">
                                        <input type="hidden" name="detailId" value="<?=$l1['detail_id']?>">
                                        <?php
                                            if($current_staff['flgAsAdmin'] == 0){
                                                echo "<input class='waves-effect' type='text' style='font-size: 14px;' placeholder='Add New Job Desc' name='newSubDetail' disabled>";
                                            }
                                            else{
                                                $autofocus = "";
                                                if($ctr == $focusId){
                                                    $autofocus = "autofocus";
                                                }
                                                echo "<input class='waves-effect' type='text' style='font-size: 14px;' placeholder='Add New Level 2 Detail' name='newSubDetail' id='newSub$ctr' $autofocus>";
                                            }
                                        ?>
                                        <input type="submit" onclick="saveState(<?=$ctr?>)" style="position: absolute; left: -9999px; width: 1px; height: 1px; visibility: <?=$visibility?>" tabindex="-1" name="addSubDetail"/>
                                    </form>
                                </li>
                        <?php
                            $subctr = -1;
                            foreach($level2 as $l2){
                                $subctr++;
                                if($l2["detail_id"] == $l1["detail_id"]){
                        ?>
                                <li>
                                    <a id="subDetail<?=$subctr?>" style="font-size: 12px; max-width: 210px; cursor: pointer; color: dimgray;" class="sub_details" style="padding-left: 15px;">
                                        <?php
                                            $datenow = date("Y-m-d");
                                            if($l2['sub_detail_end'] < $datenow){
                                                //masanya sudah selesai
                                                ?>
                                                <i class="fa fa-check-square" aria-hidden="true"></i>
                                                <?php
                                            }
                                            else{
                                                //masih in progress
                                                ?>
                                                <i class="fa fa-square-o" aria-hidden="true"></i>
                                                <?php
                                            }
                                        ?>
                                        <?=$l2["sub_detail_name"]?>
                                    </a>
                                    <div class="dropdown-content" style="margin-left: 15px;" id="dropDownSub<?=$subctr?>">
                                        <ul style="padding: 0px;">
                                            <li>
                                                <button data-toggle="modal" style="border: none;" data-target="#myModal" onclick="displayL2(<?=$subctr?>,<?=$ctr?>)" class="btnSubDetails">Details</button>
                                            </li>
                                            <li>
                                                <button data-toggle="modal" style="border: none;" data-target="#myModal" onclick="displayAdjust(<?=$subctr?>)" class="btnSubDetails">Adjust</button>
                                            </li>
                                            <li>
                                                <form class="deleteSubForm" id="deleteSubForm<?=$ctr?>.<?=$subctr?>" method="post">
                                                    <input type="hidden" id="hiddenSubId<?=$subctr?>" name="id" value="<?=$l2['sub_detail_id']?>">
                                                    <input type="hidden" id="hiddenSubName<?=$subctr?>"  value="<?=$l2['sub_detail_name']?>">
                                                    <input type="submit" style="border: none; visibility: <?=$visibility?>" name="deleteSubSubmit" value="Delete">
                                                </form>
                                            </li>
                                        </ul>
                                    </div> 
                                </li>
                        <?php
                                }
                            }
                        ?>
                            </ul>
                        </li>
                    <?php
                        }
                    ?>
                </ul>
            </div>
        </div>
        <!-- End Left Sidebar -->
    <div id="page-wrapper">
        <div class="container-fluid">
            <div class="row bg-title">
                <div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
                    <a href="project.php" class="page-title" style="font-size: 30px; cursor: pointer;"><?=$chosen_project['project_name']?></a>&nbsp;<em style="font-size: 14px;"><?=date_format(date_create($chosen_project['start_date']), 'd-M-Y');?> s/d <?=date_format(date_create($chosen_project['target_end']), 'd-M-Y');?></em><br>
                    <button style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 30px; margin-left: 5px;" data-toggle="modal" data-target="#myModal" onclick="displayDetail()">Detail</button>
                    <button style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 30px; margin-left: 5px;" data-toggle="modal" data-target="#myModal" onclick="displayFile()">Files</button>
                    <!--<button style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 30px;" data-toggle="modal" data-target="#myModal" onclick="displayFilter()">Filter</button>-->
                    <div class="modal fade" id="myModal" role="dialog">
                        <div class="modal-dialog">

                          <!-- Modal content-->
                          <div class="modal-content">
                            <div class="modal-header">
                              <h3 class="box-title">Project Details</h3>
                            </div>
                            <div class="modal-body">
                                  <div class="row">
                                    <div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
                                        <div class="white-box">
                                            <div id="projectHeader">
                                                Project ID:&nbsp;<h3 style="font-family: arial;"><?=$chosen_project['project_id']?></h3><br>
                                                Project Name:&nbsp;<b style="font-family: arial black;"><?=$chosen_project['project_name']?></b><br><br>
                                                Admin Domain:&nbsp;<a href="<?=$chosen_project['domain_admin']?>" target='_blank' class="hrefUpload"><?=$chosen_project['domain_admin']?></a><br><br>
                                                Project Domain:&nbsp;<a href="<?=$chosen_project['domain_name']?>" target='_blank' class="hrefUpload"><?=$chosen_project['domain_name']?></a><br><br>
                                                Project Value:&nbsp;<b style="font-family: arial black;"><?=number_format($chosen_project['project_total'], 0, '', '.')?></b><br><br>
                                                Start Date:&nbsp;<b style="font-family: arial black;"><?=date_format(date_create($chosen_project['start_date']), 'd-M-Y')?></b><br><br>
                                                Duration (In Days):&nbsp;<b style="font-family: arial black;"><?=$chosen_project['project_duration']?></b><br><br>
                                                Target End Date:&nbsp;<b style="font-family: arial black;"><?=date_format(date_create($chosen_project['target_end']), 'd-M-Y')?></b><br><br>
                                            </div>
                                            
                                            <form action="" method="post" id="formL1Detail">
                                                <?php
                                                    if($current_staff['flgAsAdmin'] == 1){ $title='Detail'; }
                                                    else{ $title = ""; }
                                                ?>
                                                <?=$title?> ID:<br><input type="text" style="border: none; font-size: 20px; text-decoration: bold;" readonly="true" name="id" id="l1DetailId" <?=$disabled?>><br><br>
                                                <?=$title?> Name:<br><input type="text" name="name" style="border: 1px solid black;" id="l1DetailName" placeholder="detail title" <?=$disabled?>><br><br>
                                                <?=$title?> Start:<br><input type="date" name="start_date" min="<?=$chosen_project['start_date']?>" max="<?=$chosen_project['target_end']?>" id="l1DetailStart" <?=$disabled?>><br><br>
                                                <?=$title?> End:<br><input type="date" name="end_date" min="<?=$chosen_project['start_date']?>" max="<?=$chosen_project['target_end']?>" id="l1DetailEnd" <?=$disabled?>><br><br>
                                                Predecessor:<br>
                                                <select name="pre" id="l1Pre" <?=$disabled?>>
                                                    <option value='first'>Beginning</option>
                                                </select>
                                                <br><br>
                                                <input type="submit" value="Update" style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 40px; visibility: <?=$visibility?>" name="updatel1Detail">
                                                <h3 style="color: red;" id="errorMsgDetail"></h3>
                                            </form>

                                            <form action="" method="post" id="formL2Detail">
                                                <?php
                                                    if($current_staff['flgAsAdmin'] == 1){ $title='Sub Detail'; }
                                                    else{ $title = ""; }
                                                ?>
                                                <?=$title?> ID:<br><input type="text" style="border: none; font-size: 20px; text-decoration: bold;" readonly="true" name="id" id="l2DetailId" <?=$disabled?>><br><br>
                                                <?=$title?> Name:<br><input type="text" name="name" id="l2DetailName" style="border: 1px solid black;" placeholder="detail title" <?=$disabled?>><br><br>
                                                <?=$title?> Start:<br><input type="date" name="start_date" id="l2DetailStart" <?=$disabled?>><br><br>
                                                <?=$title?> End:<br><input type="date" name="end_date" id="l2DetailEnd" <?=$disabled?>><br><br>
                                                Staff:<br>
                                                <select name="staff" id="l2Staff" <?=$disabled?>>
                                                    <?php
                                                        foreach($staffList as $s){
                                                            ?>
                                                            <option value="<?=$s['staff_id']?>"><?=$s['staff_name']?></option>
                                                            <?php
                                                        }
                                                    ?>
                                                </select><br><br>
                                                Predecessor:<br>
                                                <select name="pre" id="l2Pre" <?=$disabled?>>
                                                </select><br><br>
                                                Staff Note:<br>
                                                <b id="l2StaffNote"></b>
                                                <br><br>
                                                Staff Flag:
                                                <h3 id="l2Flag"></h3>
                                                <br>
                                                <input type="submit" value="Update" style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 40px; visibility: <?=$visibility?>" name="updatel2Detail">
                                                <h3 style="color: red;" id="errorMsgSub"></h3>
                                            </form>

                                            <form action="" method="post" id="formAdjust">
                                                <input type="hidden" name="sub_id" id="l2AdjustSubId">
                                                <input type="hidden" name="id" id="l2AdjustId">
                                                <input type="hidden" id="l2AdjustSubName">
                                                Adjust Comment:<br><textarea name="comment" id="l2AdjustComment" cols="30" rows="10" <?=$disabled?>></textarea><br><br>
                                                Adjust Duration (Days):<br><input type="number" name="duration" min="1" id="l2AdjustDuration" <?=$disabled?>><br><br>
                                                <input type="submit" id="l2AdjustSubmit" name="l2AdjustSubmit" value="Apply" style="background-color: black; color: white; border: none; border-radius: 5px; width: 50px; height: 25px; visibility: <?=$visibility?>">
                                                <input type="submit" name="l2AdjustDelete" id="l2AdjustDelete" value="Delete" style="background-color: red; color: white; border: none; border-radius: 5px; width: 55px; height: 25px; visibility: <?=$visibility?>"><br><br>
                                                <h3 style="color: red;" id="errorMsgAdjust"></h3>
                                            </form>
                                            
                                            <div id='listFile'>
                                            Click the name to open file below::<br>
                                            <?php
                                                foreach($files as $f){
                                                    $file = "";
                                                    if(strlen($f['filename']) > 38){
                                                        $file = substr($f['filename'], 0, 37);
                                                    }
                                                    else{
                                                        $file = $f['filename'];
                                                    }
                                                    echo "<div style='clear: both;'><div class='singleFile'><a class='hrefUpload' href='./uploads/".$f['filename']."' target='_blank'>$file</a></div><em class='fileDate'>".$f['waktuproses']."</em></div>";
                                                }
                                            ?>
                                            </div>
                                        </div>
                                    </div>
                                 </div>
                            </div>
                            <div class="modal-footer">
                              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                            </div>
                          </div>
                        </div>
                    </div>
                    
                </div>
            </div>

            <div class="row" id="contentDiv">
                <div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
                    <div class="white-box analysis-info">
                        <button id="jump7daysLeft" class="btnJump" title="Jump 7 days before" onclick="jump7dayBefore()"><i class="fa fa-caret-left fa-2x" aria-hidden="true" style="color: black;"></i></button>
                        <button id="jump7daysRight" class="btnJump" title="Jump 7 days after" onclick="jump7dayAhead()"><i class="fa fa-caret-right fa-2x" aria-hidden="true" style="color: black;"></i></button>
                        <!--Gantt Chart-->
                        <h1 style="float: left;">Gantt Chart</h1>
                        <div style="float: right;">
                            <h3>Last Updated</h3>
                            <?=$operator." - ".$waktuproses?>
                        </div>
                        <div id="guide" style="clear: both; margin-left: 10px; margin-top: 10px;">
                            <div style='float: left; padding-right: 15px;'><span class='legend' style='background-color: darkblue; float:left; margin-top: 5px;'></span>&nbsp;Assigned</div>
                            <div style="float: left; padding-right: 15px;"><span class="legend" style="background-color: dodgerblue; float: left; margin-top: 5px;"></span>&nbsp;Finished</div>
                            <div style="float: left; padding-right: 15px; margin-bottom: 10px;"><span class="legend" style="background-color: lightblue; float: left; margin-top: 5px;"></span>&nbsp;In Progress</div>
                            <div style="float: left; padding-right: 15px; margin-bottom: 10px;"><span class="legend" style="background-color: red; float: left; margin-top: 5px;"></span>&nbsp;Weekend & Holiday</div>
                            <div style="float: left; padding-right: 15px; margin-bottom: 10px;"><span class="legend" style="background-color: darkorchid; float: left; margin-top: 5px;"></span>&nbsp;Adjust</div>
                            <div id="guideDate" style=" display: none;"><span class="legend" style="background-color: limegreen; float:left; margin-top: 5px; "></span>&nbsp;Range Date</div>
                        </div>
                        <div id="chart-container">
							
                            <div id="chart-header">
                                <ul class="chart-values">
                                  <?php
                                    for($i=0;$i<=$maxL1Duration;$i++){
                                        $curDate = date_create($chosen_project['start_date']);
                                        date_add($curDate,date_interval_create_from_date_string("$i days"));
                                        if(date_format($curDate,"D, d M y") == date("D, d M y")){
                                            //beri tanda jika hari ini
                                            ?>
                                            <li style="min-width: 100px; max-width: 100px; background-color: cornflowerblue; color: white;" id="liToday"><?=date_format($curDate,"D, d M y");?></li>
                                            <?php
                                        }
                                        else if(date_format($curDate,"D") == "Sat" || date_format($curDate,"D") == "Sun"){
                                            //beri tanda jika weekend
                                            ?>
                                            <li style="min-width: 100px; max-width: 100px; color: red;" class="liWeekend"><?=date_format($curDate,"D, d M y");?></li>
                                            <?php
                                        }
                                        else{
                                            ?>
                                            <li style="min-width: 100px; max-width: 100px;"><?=date_format($curDate,"D, d M y");?></li>
                                            <?php
                                        }
                                    }
                                  ?>
                              </ul>
                            </div>
                            <div id="task-container">
								
                                <ul class="chart-values-hidden">
                                      <?php
                                        for($i=0;$i<=$maxL1Duration;$i++){
                                            //hanya untuk DIVIDER saja, li kosong
                                            echo "<li></li>";
                                        }
                                      ?>
                                    </ul>
                                    <ul class="chart-bars">
                                  <?php
                                    $ctr = -1;
                                    foreach($level2 as $l){
                                        $ctr++;
                                        $start = date_create($l['sub_detail_start']);
                                        $end = date_create($l['sub_detail_end']);
                                        $hundredPercent = date_diff($start, $end)->format("%a")+1;
                                        $percentAdjust = 0;
                                        $endAdjusted = date_create('0000-00-00');
                                        if($l['sub_detail_end'] < date("Y-m-d") || $l['flag'] == 1){
                                            //done
                                            $bgcolor = "dodgerblue";
                                            $iconcolor = "blue";
                                        }
                                        else{
                                            //in progress
                                            $bgcolor = "lightblue";                                            
                                            $iconcolor = "blue";
                                        }
                                        if($l['staff_id'] == $current_staff['staff_id']){
                                            $bgcolor = "darkblue";                                            
                                            $iconcolor = "yellow";
                                        }

                                        foreach($adjusts as $a){
                                            if($a['sub_detail_id'] == $l['sub_detail_id']){
                                                $endAdjusted = date_create($l['sub_detail_end']);
                                                $dur = $a['adjust_duration'];
                                                $hundredPercent += $dur;
                                                $percentAdjust = ($dur / $hundredPercent) * 100;
                                                date_add($endAdjusted,date_interval_create_from_date_string("$dur days"));
                                            }
                                        }
                                        if($endAdjusted > $end){
                                            //ada adjust thd sub detail berikut
                                                if($percentAdjust < 50){
                                                    //adjust lbh kecil
                                                    ?>
                                                    <li>
                                                        <div data-duration="<?=date_format($start,"D, d M y")?>/<?=date_format($endAdjusted,"D, d M y")?>/<?=$l['flag']?>" style="background: linear-gradient(to right, <?=$bgcolor?> <?=100-$percentAdjust?>%, darkorchid <?=$percentAdjust?>%);" class="chart-individual" id="chartItem<?=$ctr?>">
                                                            <!--trdpt 2 JENIS span: span popup, up & down-->
                                                            <span style="font-size; 14px;" class="ui custom popup transition hidden" id="popup<?=$ctr?>">
                                                                <h5><?=$l['sub_detail_name']?></h5>
                                                                Staff Name: <b><?=$l['staff_name']?></b><br>
                                                                Staff Note: <b style="color: orange;"><?=$l['staff_note']?></b><br>
                                                                Flag:
                                                                    <?php
                                                                        if($l['flag'] == 1){
                                                                            echo "<b style='color: green'>Done</b>";
                                                                        }
                                                                        else{
                                                                            echo "<b style='color: grey'>Not Done</b>";
                                                                        }
                                                                    ?>
                                                                <br>
                                                            </span>
                                                            <!--<span class="upanddown up" style="top: -17px;"><i class="fa fa-arrow-up" style="color: black;" aria-hidden="true"></i></span>
                                                            <span class="upanddown down" style="top: -17px; margin-left: 35px;"><i class="fa fa-arrow-down" style="color: black;" aria-hidden="true"></i></span>-->
                                                            <label style="cursor: move; margin: 0; float: left;" class="dragger">
																<?=$l['sub_detail_name'];?>
															</label>
                                                        </div>
                                                    </li>
                                                    <?php
                                                }
                                                else{
                                                    ?>
                                                    <li>
                                                        <div data-duration="<?=date_format($start,"D, d M y")?>/<?=date_format($endAdjusted,"D, d M y")?>/<?=$l['flag']?>" style="background: linear-gradient(to left, darkorchid <?=$percentAdjust?>%, <?=$bgcolor?> <?=100-$percentAdjust?>%);" class="chart-individual" id="chartItem<?=$ctr?>">
                                                            <!--trdpt 2 JENIS span: span popup, up & down-->
                                                            <span style="font-size; 14px;" class="ui custom popup transition hidden" id="popup<?=$ctr?>">
                                                                <h5><?=$l['sub_detail_name']?></h5>
                                                                Staff Name: <b><?=$l['staff_name']?></b><br>
                                                                Staff Note: <b style="color: orange;"><?=$l['staff_note']?></b><br>
                                                                Flag:
                                                                    <?php
                                                                        if($l['flag'] == 1){
                                                                            echo "<b style='color: green'>Done</b>";
                                                                        }
                                                                        else{
                                                                            echo "<b style='color: grey'>Not Done</b>";
                                                                        }
                                                                    ?>
                                                                <br>
                                                            </span>
                                                            <!--<span class="upanddown up" style="top: -17px;"><i class="fa fa-arrow-up" style="color: black;" aria-hidden="true"></i></span>
                                                            <span class="upanddown down" style="top: -17px; margin-left: 35px;"><i class="fa fa-arrow-down" style="color: black;" aria-hidden="true"></i></span>-->
                                                            <label style="cursor: move; margin: 0; float: left;" class="dragger">
																<?=$l['sub_detail_name'];?>
															</label>
                                                        </div>
                                                    </li>
                                                    <?php
                                                }
                                            ?>
                                            <?php
                                        }
                                        else{
                                            //tidak ada adjust, tidak perlu membagi warna div
                                            ?>
                                            <li>
                                                <div data-duration="<?=date_format($start,"D, d M y")?>/<?=date_format($end,"D, d M y")?>/<?=$l['flag']?>" data-color="<?=$bgcolor?>" class="chart-individual" id="chartItem<?=$ctr?>">
                                                    <!--trdpt 4 JENIS span: span popup, up & down, dragger, resizers left & right-->
                                                    <span style="font-size; 14px;" class="ui custom popup transition hidden" id="popup<?=$ctr?>">
                                                        <h5><?=$l['sub_detail_name']?></h5>
                                                        Staff Name: <b><?=$l['staff_name']?></b><br>
                                                        Staff Note: <b style="color: orange;"><?=$l['staff_note']?></b><br>
                                                        Flag:
                                                            <?php
                                                                if($l['flag'] == 1){
                                                                    echo "<b style='color: green'>Done</b>";
                                                                }
                                                                else{
                                                                    echo "<b style='color: grey'>Not Done</b>";
                                                                }
                                                            ?>
                                                        <br>
                                                    </span>
                                                    <!--<span class="upanddown up" style="top: -17px;"><i class="fa fa-arrow-up" style="color: black;" aria-hidden="true"></i></span>
                                                    <span class="upanddown down" style="top: -17px; margin-left: 35px;"><i class="fa fa-arrow-down" style="color: black;" aria-hidden="true"></i></span>-->
                                                    
                                                    <?php
                                                        if($l['flag'] == 0 && $current_staff['flgAsAdmin'] == 1){
                                                            echo "<label style='cursor: move; margin: 0; float: left;' class='dragger'>".$l['sub_detail_name']."</label>";
                                                            echo "<span class='resizers left' style='left: -10px;'></span>";
                                                            echo "<span class='resizers right' style='right: -10px;'></span>";
                                                        }
                                                        else{
                                                            echo "<label style='cursor: pointer; margin: 0; float: left;' class='dragger'>".$l['sub_detail_name']."</label>";
                                                        }
                                                    ?>
                                                </div>
                                            </li>
                                            <?php
                                        }
                                    }
                                  ?>
                              </ul>
                            </div>
                        </div>
                        <!--FORM TO SAVE GANTT CHART CHANGES (DRAG & RESIZE)-->
                        <form action="" method="post">
                            <input type="hidden" name="id" id="changedL2Id">
                            <input type="hidden" name="start_date" id="changedL2Start">
                            <input type="hidden" name="end_date" id="changedL2End">
                            <input type="submit" style="background-color: green; color: white; border: none; border-radius: 5px; width: 125px; height: 50px; margin-top: 15px; font-size: 16px; display: none" id="btnSaveChange" name="btnSaveChange" value="Save Changes">
                        </form>
                        <br>
                    </div>
                </div>
            </div>
        </div> 
    </div>
    </div>
    <!-- End Wrapper -->
    <!-- ============================================================== -->
    <!-- All Jquery -->
    <script src="./plugins/bower_components/jquery/dist/jquery.min.js"></script>
    <!-- Bootstrap Core JavaScript -->
    <script src="bootstrap/dist/js/bootstrap.min.js"></script>
    <!-- Menu Plugin JavaScript -->
    <script src="./plugins/bower_components/sidebar-nav/dist/sidebar-nav.min.js"></script>
    <!--slimscroll JavaScript -->
    <script src="js/jquery.slimscroll.js"></script>
    <!--Wave Effects -->
    <script src="js/waves.js"></script>
    <!--Counter js -->
    <script src="./plugins/bower_components/waypoints/lib/jquery.waypoints.js"></script>
    <script src="./plugins/bower_components/counterup/jquery.counterup.min.js"></script>
    <!-- chartist chart -->
    <script src="./plugins/bower_components/chartist-js/dist/chartist.min.js"></script>
    <script src="./plugins/bower_components/chartist-plugin-tooltip-master/dist/chartist-plugin-tooltip.min.js"></script>
    <!-- Sparkline chart JavaScript -->
    <script src="./plugins/bower_components/jquery-sparkline/jquery.sparkline.min.js"></script>
    <!-- Custom Theme JavaScript -->
    <script src="js/custom.min.js"></script>
    <script src="js/semantic.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
    <script>
        var arrDetail, arrSubDetail, arrAdjust, arrHoliday, holidayDone = false, chartDone = false;
        var months = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];        
        var daysOfMonth = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];

        $.ajax({
             url: "./json/jsonLevel1.php",
             data: {project_id : "<?=$projectid?>"},
             success: function(data){
                 arrDetail = JSON.parse(data);
                 $.ajax({
                     url: "./json/jsonLevel2.php",
                     data: {project_id : "<?=$projectid?>"},
                     success: function(data){
                         arrSubDetail = JSON.parse(data);
                         $.ajax({
                            url: "./json/jsonFindAdjust.php",
                            success: function(data){
                                arrAdjust = JSON.parse(data);
                                createChartResizeable();
                                initialize();
                            },
                            type: 'GET'
                        });
                     },
                     type: 'GET'
                 });
             },
             type: 'GET'
         });
        
        
        
        $(document).ready(function(){
            //ambil list holiday            
            $.ajax({
                url : "https://www.googleapis.com/calendar/v3/calendars/en.indonesian%23holiday%40group.v.calendar.google.com/events?key=AIzaSyDoCNqjWSoeUS-dYXG0ahB0BI7N9O17E1o&timeMin=<?=$chosen_project['start_date']?>T10:00:00Z&timeMax=<?=$chosen_project['target_end']?>T10:00:00Z",
                type: 'GET',
                success: function(data){
                    arrHoliday = data.items;
                    markHolidays();
                }
            });
            
             resizeContainer();
             $("#sidebarDiv").scrollTop("<?=$scrollTop?>");
             
            
             $(".details").contextmenu(function(event) {
                $(".dropdown-content").css("display", "none");
                var id = this.id;
                id = id.substr(6);
                $("#dropDownDetail"+id).css("display", "block");
                event.preventDefault();
            });

            $(".sub_details").contextmenu(function(event) {
                $(".dropdown-content").css("display", "none");
                var id = this.id;
                id = id.substr(9);
                $("#dropDownSub"+id).css("display", "block");
                event.preventDefault();
            });

            $("#formL1Detail").submit(function(){
                var detailName = $("#l1DetailName").val();
                var detailStart = $("#l1DetailStart").val();
                var detailEnd = $("#l1DetailEnd").val();
                var step = $("#l1DetailStep").val();
                if(detailName == "" || detailStart == "" || detailEnd == "" || step == ""){
                    $("#errorMsgDetail").html("Fields must not be null");
                    event.preventDefault();
                }
                else{
                    if(detailStart > detailEnd){
                        $("#errorMsgDetail").html("Range of date invalid, End Date must not fall behind Start Date");
                        event.preventDefault();
                    }
                }
            });

            $("#formL2Detail").submit(function(){
                var subId = $("#l2DetailId").val();
                var subName = $("#l2DetailName").val();
                var subStart = $("#l2DetailStart").val();
                var subEnd = $("#l2DetailEnd").val();
                var subStaff = $("#l2Staff").val();

                var adjust = 0;
                arrAdjust.forEach(a => {
                    if(a['sub_detail_id'] == subId){
                        adjust = a['adjust_duration'];
                        adjust = adjust * 24 * 3600 * 1000;
                    }
                });
                if(subName == "" || subStart == "" || subEnd == "" || subStaff == null || subStep == ""){
                    $("#errorMsgSub").html("Fields must not be null");
                    event.preventDefault();
                } 
                else{
                    var maxDate;
                    for(var i=0;i<arrDetail.length;i++){
                        if(arrDetail[i].detail_id == currentDetailId){
                            maxDate = arrDetail[i].detail_end;
                        }
                    }
                    if((Date.parse(subEnd) + adjust) > Date.parse(maxDate)){
                        $("#errorMsgSub").html("End date (+adjust) out of range, please change the level 1 detail first");
                        event.preventDefault();
                    }
                    if(subEnd < subStart){
                        $("#errorMsgSub").html("End Date must not fall behind Start Date");
                        event.preventDefault();
                    }
                }
            });

            $("#l2AdjustSubmit").click(function(){
                var subId = $("#l2AdjustSubId").val();
                var comment = $("#l2AdjustComment").val();
                var duration = $("#l2AdjustDuration").val();
                if(comment == "" || duration == ""){
                    $("#errorMsgAdjust").html("Fields must not be null");
                    event.preventDefault();
                }
                else{
                    var detailId, subEndDate, endDate;
                    arrSubDetail.forEach(s => {
                        if(s['sub_detail_id'] == subId){
                            detailId = s['detail_id'];
                            subEndDate = s['sub_detail_end'];
                        }
                    });
                    arrDetail.forEach(d => {
                        if(d['detail_id'] == detailId){
                            endDate = d['detail_end'];
                        }
                    });
                    duration = duration * 24 * 3600 * 1000;
                    if((Date.parse(subEndDate) + duration) > Date.parse(endDate)){
                        $("#errorMsgAdjust").html("Duration invalid, end date become out of range");
                        event.preventDefault();
                    }
                }
            });

            $("#l2AdjustDelete").click(function(){
                var subName = $("#l2AdjustSubName").val();
                var cfr = confirm("Are you sure you want to delete adjust for "+subName+"?");
                if(!cfr){
                    event.preventDefault();
                }
            });

            $(".deleteDetailForm").submit(function(event){
                var id = jQuery(this).attr("id");
                id = id.substr(16);
                var detailName = $("#hiddenDetailName"+id).val();
                var cfr = confirm("Are you sure you want to delete "+detailName+"? All its information including level 2 details will be deleted!");
                if(!cfr){
                    event.preventDefault();
                }
            });
            
            $(".deleteSubForm").submit(function(event){
                var id = jQuery(this).attr("id");
                var dot = id.indexOf('.');
                var l1 = id.substring(13, dot);
                var l2 = id.substr(dot+1);
                var l1Name = $("#hiddenDetailName"+l1).val();
                var l2Name = $("#hiddenSubName"+l2).val();
                var cfr = confirm("Are you sure you want to delete "+l2Name+" in "+l1Name+"?");
                if(!cfr){
                    event.preventDefault();
                }
            });

            $(".chart-individual").mouseenter(function(event){
				var id = jQuery(this).attr("id");
				const element = document.querySelector("#"+id);
				var num = id.substr(9);
				$("#"+id+" .resizers").css("display", "block");
				$("#"+id+" .upanddown").css("display", "block");
				lastClicked = num;
				
				$("#"+id).click(function(){
					if(parseInt(element.offsetLeft)+45 < $("#task-container").scrollLeft()){
						$("#task-container").scrollLeft(element.offsetLeft-45);
					}
				});
				
				$("#"+id).popup({
					on: 'click',
					target   : "#"+id,
					popup: $("#popup"+num)
				});
			});

            $(".chart-individual").mouseleave(function(event){
                $(".resizers").css("display", "none");
                $(".upanddown").css("display", "none");
            });
            
            $(".upanddown").click(function(){
                var div = $(this).parent(); 
                var oldMTop = div.css("top");
                //oldMTop = parseInt(oldMTop.substr(0, oldMTop.indexOf("px")));
                alert(oldMTop);
            });

            $("#btnSaveChange").click(function(){
                //ketika hendak save changes, masukkan semua data l2 yang diubah-ubah
                
                //pertama, cari tau end date dan start date masing" l2 setelah diubah, cocokkan dengan daftar tgl diatasnya
                const days = document.querySelectorAll(".chart-values li");
                const daysArray = [...days];
                const tasks = document.querySelectorAll(".chart-bars div");
                tasks.forEach(element => {
                    //cari start date nya
                    var filteredArray = daysArray.filter(days => parseInt(days.offsetLeft) == parseInt(element.style.left));
                    $("#changedL2Start").val($("#changedL2Start").val() + filteredArray[0].textContent+";");

                    //cari end date nya
                    filteredArray = daysArray.filter(days => parseInt(days.offsetLeft)+100 == parseInt(element.style.left)+parseInt(element.style.width));
                    $("#changedL2End").val($("#changedL2End").val() + filteredArray[0].textContent+";");
                    
                    //cari sub detail id nya
                    var sub_id = element.id.substr(9);
                    sub_id = $("#hiddenSubId"+sub_id).val();
                    $("#changedL2Id").val($("#changedL2Id").val() + sub_id + ";");
                });
            });
            
            $("#task-container").on("scroll", function(event){
				$(".ui.custom.popup").removeClass("visible");
				$(".ui.custom.popup").addClass("hidden");
                var scroll = $("#task-container").scrollLeft(); 
                var actualSize = "<?=$ulSize?>"; //size dari ul task
                actualSize = actualSize.substr(0, actualSize.indexOf('px'));
                var displayedSize = $("#task-container").css("width"); //size dari div container nya
                displayedSize = displayedSize.substr(0, displayedSize.indexOf('px'));
                //actualSize - displayed Size = scroll size
                
                //scroll jgn sampai menabrak vertical scroll bar, agar tidka pecah line antara header dan task
                if(scroll > (actualSize - displayedSize)){
                    scroll = actualSize - displayedSize;
                    $("#task-container").scrollLeft(scroll);
                }
                $("#chart-header").scrollLeft(scroll);
            });
            
            $("#chart-header li").click(function(event){
                if("<?=$current_staff['flgAsAdmin']?>" == 0){
                    $.ajax({
                        url : "assignSessionDate.php",
                        data : {"chosen_date":jQuery(this).html().substr(5), "project_id":"<?=$projectid?>"},
                        success : function(){
                            location.replace("dashboard.php");
                        },
                        method : "post"
                     });
                }
            });
        });
        
        function displayL1(num){
            changeModal("none", "block", "none", "none", "none", "none");
            $(".dropdown-content").css("display", "none");
            var detail_id = $("#hiddenDetailId"+num).val();
            var detail = [];
            var nextPre = "first";
            $("#l1Pre").html("");
            $("#l1Pre").append("<option value='first'>Beginning</option>");
            for(var i=0;i<arrDetail.length;i++){
                if(arrDetail[i].detail_id == detail_id){
                    detail = arrDetail[i];
                }
                else{
                    $("#l1Pre").append("<option value='"+arrDetail[i].detail_id+"'>After "+arrDetail[i].detail_name+"</option>");
                }
            }
            for(var i=0;i<arrDetail.length;i++){
                if(arrDetail[i].step < detail['step']){
                    //ambil step yg plg mendekati
                    //cari yang merupakan After untuk combobox nya
                    nextPre = arrDetail[i].detail_id;
                }
            }
            $("#l1Pre").val(nextPre);
            
            $("#l1DetailId").val(detail['detail_id']);
            $("#l1DetailName").val(detail['detail_name']);
            $("#l1DetailStart").val(detail['detail_start']);
            $("#l1DetailEnd").val(detail['detail_end']);
            //$(".box-title").html("<?=$chosen_project['project_name']?>");
            $(".box-title").html(detail['detail_name']);
            $("#l1DetailStep").val(detail['step']);
            $("#errorMsgDetail").html("");
        }
        
        function displayL2(num, num1){
            changeModal("none", "none", "block", "none", "none", "none");
            $(".dropdown-content").css("display", "none");
            currentDetailId = $("#hiddenDetailId"+num1).val();
            var minDate, title, maxDate;
            for(var i=0;i<arrDetail.length;i++){
                if(arrDetail[i].detail_id == currentDetailId){
                    minDate = arrDetail[i].detail_start;
                    maxDate = arrDetail[i].detail_end;
                    title = arrDetail[i].detail_name;
                }
            }
            var sub = [];
            var sub_detail_id = $("#hiddenSubId"+num).val();
            var nextPre = "first";
            $("#l2Pre").html("");
            $("#l2Pre").append("<option value='first'>Beginning</option>");
            for(var i=0;i<arrSubDetail.length;i++){
                if(arrSubDetail[i].sub_detail_id == sub_detail_id){
                    sub = arrSubDetail[i];
                }
                else{
                    $("#l2Pre").append("<option value='"+arrSubDetail[i].sub_detail_id+"'>After "+arrSubDetail[i].sub_detail_name+"</option>");
                }
            }
            for(var i=0;i<arrSubDetail.length;i++){
                if(arrSubDetail[i].step < sub['step']){
                    //ambil step yang plaing mendekati
                    //cari yang merupakan After untuk combobox nya
                    nextPre = arrSubDetail[i].sub_detail_id;
                }
            }
            $("#l2Pre").val(nextPre);
            
            //cari apakah sub ini mempunyai ADJUST, jika iya, disable start & end date nya
            var adjust = "";
            arrAdjust.forEach(ad => {
                if(ad['sub_detail_id'] == sub_detail_id){
                    adjust = ad['adjust_id'];
                }
            });
            if(adjust != ""){
                $("#l2DetailStart").prop("readonly", true);
                $("#l2DetailStart").css("border", "none");
                $("#l2DetailEnd").prop("readonly", true);
                $("#l2DetailEnd").css("border", "none");
            }
            else{
                $("#l2DetailStart").attr("min", minDate);
                $("#l2DetailStart").attr("max", maxDate);
                $("#l2DetailEnd").attr("min", minDate);
                $("#l2DetailEnd").attr("max", maxDate);
            }
            
            $("#l2DetailId").val(sub['sub_detail_id']);
            $("#l2DetailName").val(sub['sub_detail_name']);
            $("#l2DetailStart").val(sub['sub_detail_start']);
            $("#l2DetailEnd").val(sub['sub_detail_end']);
            //$(".box-title").html(title);
            $(".box-title").html(sub['sub_detail_name']);
            if(sub['staff_id'] != ""){
                $("#l2Staff").val(sub['staff_id']);
            }
            $("#l2StaffNote").html(sub['staff_note']);
            if(sub['flag'] == 1){
                $("#l2Flag").css("color", "green");
                $("#l2Flag").html("Done");
            }
            else{
                $("#l2Flag").css("color", "grey");
                $("#l2Flag").html("Not Done");
            }
            $("#errorMsgSub").html("");
        }

        function displayAdjust(num){
            changeModal("none", "none", "none", "block", "none", "none");
            $(".dropdown-content").css("display", "none");
            var sub_detail_id = $("#hiddenSubId"+num).val();
            var sub_detail_name = $("#hiddenSubName"+num).val();
            $(".box-title").html(sub_detail_name+" Adjustment");
            $("#l2AdjustSubId").val(sub_detail_id);
            $("#errorMsgAdjust").html("");
            $("#l2AdjustDelete").css("display", "none");
            $("#l2AdjustId").val("");
            $("#l2AdjustSubName").val("");
            $("#l2AdjustComment").val("");
            $("#l2AdjustDuration").val("");
            arrAdjust.forEach(a => {
                if(a['sub_detail_id'] == sub_detail_id){
                    $("#l2AdjustDelete").css("display", "inline");
                    $("#l2AdjustId").val(a['adjust_id']);
                    $("#l2AdjustSubName").val(sub_detail_name);
                    $("#l2AdjustComment").val(a['adjust_comment']);
                    $("#l2AdjustDuration").val(a['adjust_duration']);
                }
            });
        }

        function displayDetail(){
            changeModal("block", "none", "none", "none", "none", "none");
        }

        function displayFilter(){
            changeModal("none", "none", "none", "none", "block", "none");
        }
        
        function displayFile(){
            $(".box-title").html("Uploaded Files");
            changeModal("none", "none", "none", "none", "none", "block");
        }

        function makeChartDraggable(div){
            const days = document.querySelectorAll(".chart-values li");
            const daysArray = [...days];

            //batas kanan layar, JANGAN melebihi
            var maxDate, minDate;
            
            const element = document.querySelector(div);
            var number = element.id;
            number = number.substr(9);

            var subId = $("#hiddenSubId"+number).val();
            arrSubDetail.forEach(s => {
                if(s['sub_detail_id'] == subId){
                    //temukan sub detail dgn id, kemudian cocokkan dgn detail utk mendapatkan end date & start date
                    arrDetail.forEach(d => {
                        if(d['detail_id'] == s['detail_id']){
                            maxDate = new Date(d['detail_end']);
                            year = maxDate.getFullYear()+"";
                            maxDate = daysOfMonth[maxDate.getDay()]+", "+('0'+maxDate.getDate()).slice(-2)+" "+months[maxDate.getMonth()]+" "+year.substr(2);
                            minDate = new Date(d['detail_start']);
                            year = minDate.getFullYear()+"";
                            minDate = daysOfMonth[minDate.getDay()]+", "+('0'+minDate.getDate()).slice(-2)+" "+months[minDate.getMonth()]+" "+year.substr(2);
                        }
                    });
                }
            });

            //berdasarkan maxDate dan minDate, cari bound nya
            var filteredArray = daysArray.filter(day => day.textContent == maxDate);
            var maxWidth = filteredArray[0].offsetLeft + filteredArray[0].offsetWidth;

            var filteredArray2 = daysArray.filter(day => day.textContent == minDate);
            var minLeft = filteredArray2[0].offsetLeft;
            
            //temukan semua li di range date minDate - maxDate
            var filteredArray3 = daysArray.filter(day => Date.parse(day.textContent) > Date.parse(minDate) && Date.parse(day.textContent) < Date.parse(maxDate));
            
            const dragger = document.querySelectorAll(div+' .dragger');
            for (let index = 0; index < dragger.length; index++) {
                const currentDragger = dragger[index];
                currentDragger.addEventListener('mousedown', function(e){
                    displayRange(filteredArray[0], filteredArray2[0], filteredArray3);
                    
                    width = element.style.width;
                    width = parseInt(width.substr(0, width.indexOf("px")));
                    oldX = e.pageX;
                    oldLeft = element.style.left;
                    oldLeft = parseInt(oldLeft.substr(0, oldLeft.indexOf("px")));
                    window.addEventListener('mousemove', drag);
                    window.addEventListener('mouseup', stopDrag);
                });
            }

            function drag(e) {
				$(".ui.custom.popup").removeClass("visible");
				$(".ui.custom.popup").addClass("hidden");
                //hanya untuk animasi drag saja, perhitungan di stopDrag
                if(oldLeft + (e.pageX - oldX) > minLeft && (oldLeft + (e.pageX - oldX) + width) < maxWidth){
                    element.style.left = oldLeft + (e.pageX - oldX) + "px";
                }
            }
            
            function stopDrag() {
                undisplayRange(filteredArray[0], filteredArray2[0], filteredArray3);
                
                newLeft = element.style.left;
                newLeft = newLeft.substr(0, newLeft.indexOf("px"));
                diffLeft = Math.round((newLeft - oldLeft)/100)*100;
                element.style.left = (oldLeft+parseInt(diffLeft)) + 'px';
                window.removeEventListener('mousemove', drag);
                $("#btnSaveChange").css("display","block");
            }
        }

        function makeChartResizeable(div){
            const days = document.querySelectorAll(".chart-values li");
            const daysArray = [...days];

            //batas kanan layar, JANGAN melebihi
            var maxDate, minDate;
            
            const element = document.querySelector(div);
            var number = element.id;
            number = number.substr(9);

            var subId = $("#hiddenSubId"+number).val();
            arrSubDetail.forEach(s => {
                if(s['sub_detail_id'] == subId){
                    //temukan sub detail dgn id, kemudian cocokkan dgn detail utk mendapatkan end date & start date
                    arrDetail.forEach(d => {
                        if(d['detail_id'] == s['detail_id']){
                            maxDate = new Date(d['detail_end']);
                            year = maxDate.getFullYear()+"";
                            maxDate = daysOfMonth[maxDate.getDay()]+", "+('0'+maxDate.getDate()).slice(-2)+" "+months[maxDate.getMonth()]+" "+year.substr(2);
                            minDate = new Date(d['detail_start']);
                            year = minDate.getFullYear()+"";
                            minDate = daysOfMonth[minDate.getDay()]+", "+('0'+minDate.getDate()).slice(-2)+" "+months[minDate.getMonth()]+" "+year.substr(2);
                        }
                    });
                }
            });

            //berdasarkan maxDate dan minDate, cari bound nya
            var filteredArray = daysArray.filter(day => day.textContent == maxDate);
            var maxWidth = filteredArray[0].offsetLeft + filteredArray[0].offsetWidth;

            var filteredArray2 = daysArray.filter(day => day.textContent == minDate);
            var minLeft = filteredArray2[0].offsetLeft;
            
            //temukan semua li di range date minDate - maxDate
            var filteredArray3 = daysArray.filter(day => Date.parse(day.textContent) > Date.parse(minDate) && Date.parse(day.textContent) < Date.parse(maxDate));

            const resizers = document.querySelectorAll(div+' .resizers');
            for (let i = 0;i < resizers.length; i++) {
                const currentResizer = resizers[i];
                currentResizer.addEventListener('mousedown', function(e) {
                    //tunjukkan bound dengan warnai batas tanggal detail nya
                    displayRange(filteredArray[0], filteredArray2[0], filteredArray3);
                    
                    //ambil width yang lama sblm di resize, untuk menentukan bahwa pergeseran width hrs kelipatan 100
                    oldWidth = element.style.width;
                    oldWidth = parseInt(oldWidth.substr(0, oldWidth.indexOf("px")));
                    oldX = e.pageX;
                    oldLeft = element.style.left;
                    oldLeft = parseInt(oldLeft.substr(0, oldLeft.indexOf("px")));
                    //alert(oldLeft+";"+oldX);
                    e.preventDefault();
                    window.addEventListener('mousemove', resize);
                    window.addEventListener('mouseup', stopResize);
                });
                
                function resize(e) {
					$(".ui.custom.popup").removeClass("visible");
					$(".ui.custom.popup").addClass("hidden");
                    //hanya untuk animasi resize saja, perhitungan di stopResize
                    if (currentResizer.classList.contains('right')) {
                        if((e.pageX - element.getBoundingClientRect().left) > 20 && (e.pageX - element.getBoundingClientRect().left + oldLeft) < maxWidth){
                            element.style.width = e.pageX - element.getBoundingClientRect().left + 'px';
                        }
                    }
                    else if (currentResizer.classList.contains('left')) {
                        if(oldWidth - (e.pageX - oldX) > 20 && oldLeft + (e.pageX - oldX) > minLeft){
                            element.style.width = oldWidth - (e.pageX - oldX) + 'px';
                            element.style.left = oldLeft + (e.pageX - oldX) + "px";
                        }
                    }
                }
                
                function stopResize() {
                    undisplayRange(filteredArray[0], filteredArray2[0], filteredArray3);
                    if (currentResizer.classList.contains('right')) {
                        newWidth = element.style.width;
                        newWidth = newWidth.substr(0, newWidth.indexOf("px"));
                        //hitung perbedaan width baru dan lama, jadikan kelipatan 100
                        difference = Math.round((newWidth - oldWidth)/100)*100;
                        //ubah luas element
                        if((oldWidth+parseInt(difference)) > 0){
                            element.style.width = (oldWidth+parseInt(difference)) + 'px';
                        }
                        else{
                            element.style.width = '100px';
                        }
                        $("#btnSaveChange").css("display","block");
                    }
                    else if (currentResizer.classList.contains('left')) {
                        
                        newLeft = element.style.left;
                        newLeft = newLeft.substr(0, newLeft.indexOf("px"));

                        newWidth = element.style.width;
                        newWidth = newWidth.substr(0, newWidth.indexOf("px"));
                        //hitung perbedaan left baru dan lama serta width baru dan lama, jadikan kelipatan 100
                        diffLeft = Math.round((newLeft - oldLeft)/100)*100;
                        diffWidth = Math.round((newWidth - oldWidth)/100)*100;
                        //ubah posisi dan luas element
                        element.style.left = (oldLeft+parseInt(diffLeft)) + 'px';
                        if((oldWidth+parseInt(diffWidth)) > 0){
                            element.style.width = (oldWidth+parseInt(diffWidth)) + 'px';
                        }
                        else{
                            element.style.width = '100px';
                        }
                        $("#btnSaveChange").css("display","block");
                    }
                    
                    window.removeEventListener('mousemove', resize);
                }
            }
        }
        
        function markHolidays(){
            const days = document.querySelectorAll(".chart-values li");
            const daysArray = [...days];
            
            var counter = 0;
            arrHoliday.forEach(h =>{
                counter++;
                //alert(Date.parse(h['date']));
                var filteredArray = daysArray.filter(day => Date.parse(day.textContent.substr(5)) == Date.parse(h['start']['date'].split("-").join("/")));
                if(filteredArray.length > 0){
                    filteredArray[0].className = "liHoliday";
                    filteredArray[0].id = "liHoliday"+counter;
                    filteredArray[0].style.color = "red";
                    $("#liHoliday"+counter).popup({
                        target   : "#liHoliday"+counter,
                        title    : 'Holiday',
                        content  : h['summary']
                    });
                }
            });
            holidayDone = true;
            if(holidayDone && chartDone){
                Swal.close();
            }
        }
        
        function createChartResizeable(e) {
          const flgAsAdmin = "<?=$current_staff['flgAsAdmin']?>";
          const days = document.querySelectorAll(".chart-values li");
          const tasks = document.querySelectorAll(".chart-bars div");
		  const labelTask = document.querySelectorAll(".chart-bars div label");
          const rightResizers = document.querySelectorAll(".chart-bars .resizers.right");
          const daysArray = [...days];
        
          for (let index = 0; index < tasks.length; index++) {
                const el = tasks[index];
				const label = labelTask[index];
                const right = rightResizers[index];
                const duration = el.dataset.duration.split("/");
                const startDay = duration[0];
                const endDay = duration[1];
                const done = duration[2];
                let left = 0;
                let width = 0;
              
                var filteredArray = daysArray.filter(day => day.textContent == String(startDay));
                left = filteredArray[0].offsetLeft;

                filteredArray = daysArray.filter(day => day.textContent == endDay);
                width = filteredArray[0].offsetLeft + filteredArray[0].offsetWidth - left;
                
                if(flgAsAdmin == 1 && done == 0){
                    makeChartDraggable("#"+el.id);
                    makeChartResizeable("#"+el.id);
                }

                // apply css
                
                el.style.left = left+"px";
                el.style.width = width+"px";
				label.style.maxWidth = (parseInt(width)-20)+"px";
                
                el.style.backgroundColor = el.dataset.color;
                el.style.opacity = 1;
          }
          chartDone = true;
          if(holidayDone && chartDone){
              Swal.close();
          }
        }
        
        function displayRange(el1, el2, inBetween){
            $("#guideDate").css("display", "block");
            $("#liToday").css("background-color", "white");
            $("#liToday").css("color", "black");            
            $(".liWeekend").css("color", "black");

            //el1 adalah element utk start date
            //el2 adalah element utk end date
            //warnai semua diantaranya
            el1.style.color = "limegreen";
            el1.style.fontSize = "14px";
            el2.style.color = "limegreen";
            el2.style.fontSize = "14px";
            
            for(let index =0 ; index < inBetween.length; index++){
                const element = inBetween[index];
                element.style.color = "limegreen";
                element.style.fontSize = "14px";
            }
        }
        
        function undisplayRange(el1, el2, inBetween){
            //el1 adalah element utk start date
            //el2 adalah element utk end date
            //warnai semua diantaranya
            el1.style.color = "black";
            el1.style.fontSize = "12px";
            el2.style.color = "black";
            el2.style.fontSize = "12px";
            
            for(let index =0 ; index < inBetween.length; index++){
                const element = inBetween[index];
                element.style.color = "black";
                element.style.fontSize = "12px";
            }
            
            $("#liToday").css("background-color", "cornflowerblue");
            $("#liToday").css("color", "white");
            $(".liWeekend").css("color", "red");
            $(".liHoliday").css("color", "red");
            $("#guideDate").css("display", "none");
        }

        function changeModal(display1, display2, display3, display4, display5, display6){
            $("#projectHeader").css("display", display1);
            $("#formL1Detail").css("display", display2);
            $("#formL2Detail").css("display", display3);
            $("#formAdjust").css("display", display4);
            $("#formFilter").css("display", display5);
            $("#listFile").css("display", display6);
        }
        
        function resizeContainer(){
            var selisihAtas = 96+60+25;
            var initialHeight = "<?=$divSize?>", heightDivBesar, selisih;
            if(initialHeight > window.innerHeight-350){
                //jika terlalu besar, kurangi height nya
                $("#chart-container").css("height", window.innerHeight-350+"px");
                $("#task-container").css("height", window.innerHeight-420+"px");
                heightDivBesar = $("#contentDiv").css("height");
                heightDivBesar = parseInt(heightDivBesar.substr(0, heightDivBesar.indexOf("px")));
            }
            else{
                //jika tidak terlalu besar, ikut height awal yg sudah diinisialiasi dr php 
                $("#chart-container").css("height", (parseInt(initialHeight)+70)+"px");
                $("#task-container").css("height", initialHeight+"px");
                heightDivBesar = $("#contentDiv").css("height");
                heightDivBesar = parseInt(heightDivBesar.substr(0, heightDivBesar.indexOf("px")));
            }
            
            //posisikan button jump 7 day di tengah layar
            $(".btnJump").css("top", (heightDivBesar/2+selisihAtas)+"px");
        }
        
        function initialize(){
            var top = -1, counter = 0;
            arrSubDetail.forEach(s =>{
                counter++;
                if((Date.parse(s['sub_detail_end']) > Date.parse("<?=date('Y/m/d')?>")) && top == -1){
                    if(counter == 1){
                        top = 0;
                    }
                    else{
                        top = (counter-1)*52+37;
                    }
                }
            });
            $("#task-container").scrollTop(top);
            
            //scroll otomatis hingga tanggal HARI INI di paling kiri
            var today = "<?=date('Y/m/d')?>";
            const days = document.querySelectorAll(".chart-values li");
            const daysArray = [...days];
            
            var filteredArray = daysArray.filter(day => Date.parse(day.textContent.substr(5)) == Date.parse(today));
            if(filteredArray.length >= 1){
                $("#task-container").scrollLeft(filteredArray[0].offsetLeft);
                $("#chart-header").scrollLeft(filteredArray[0].offsetLeft);
            }
        }
        
        function saveState(num){
            $.ajax({
               url:"assignSessionScroll.php",
               data:{scroll:$("#sidebarDiv").scrollTop(), focus:num}
            });
        }
        
        function jump7dayAhead(){
            //1. temukan tanggal yang paling kiri
            //2. tambahkan tanggal tsb dengan 7 hari
            //3. pada saat menambahkan jangan lupakan maximum datenya
            const days = document.querySelectorAll(".chart-values li");
            const daysArray = [...days];
            
            var filteredArray = daysArray.filter(day => day.offsetLeft >= $("#task-container").scrollLeft());
            var leftDate = Date.parse(filteredArray[0].textContent.substr(5));
            var maxDate = Date.parse("<?=date_format(date_create($chosen_project['target_end']), 'Y/m/d')?>");
            if((leftDate + 7*24*3600*1000) > maxDate){
                //out of bound
                var filteredArray = daysArray.filter(day => Date.parse(day.textContent.substr(5)) == maxDate);
                $("#task-container").scrollLeft(filteredArray[0].offsetLeft);
            }
            else{
                var filteredArray = daysArray.filter(day => Date.parse(day.textContent.substr(5)) == (leftDate + 7*24*3600*1000));
                $("#task-container").scrollLeft(filteredArray[0].offsetLeft);
            }
        }
        
        function jump7dayBefore(){
            //1. temukan tanggal yang paling kiri
            //2. kurangi tanggal tsb dengan 7 hari
            //3. pada saat menambahkan jangan lupakan minimum datenya
            const days = document.querySelectorAll(".chart-values li");
            const daysArray = [...days];
            
            var filteredArray = daysArray.filter(day => day.offsetLeft >= $("#task-container").scrollLeft());
            var leftDate = Date.parse(filteredArray[0].textContent.substr(5));
            var minDate = Date.parse("<?=date_format(date_create($chosen_project['start_date']), 'Y/m/d')?>");
            if((leftDate - 7*24*3600*1000) < minDate){
                //out of bound
                var filteredArray = daysArray.filter(day => Date.parse(day.textContent.substr(5)) == minDate);
                $("#task-container").scrollLeft(filteredArray[0].offsetLeft);
            }
            else{
                var filteredArray = daysArray.filter(day => Date.parse(day.textContent.substr(5)) == (leftDate - 7*24*3600*1000));
                $("#task-container").scrollLeft(filteredArray[0].offsetLeft);
            }
        }
        
        window.onclick = function(event) {
			$(".ui.custom.popup").removeClass("visible");
			$(".ui.custom.popup").addClass("hidden");
            $(".dropdown-content").css("display", "none");
        }
        
        window.onresize = function(event){
            resizeContainer();
        }
        
        window.onload = function(){
            Swal.fire({
                title: 'Loading Project...',
                text: 'Please Wait Patiently',
                imageUrl: './imgicon/loading.gif',
                imageWidth: 200,
                imageHeight: 200,
                imageAlt: 'Custom image',
                showConfirmButton: false,
                allowOutsideClick: false
            });
        }
    </script>
</body>
</html>