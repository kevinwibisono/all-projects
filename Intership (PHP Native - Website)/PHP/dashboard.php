<?php
    include("connection.php");
    date_default_timezone_set("Asia/Bangkok");
    session_start();
    $visibility = "visible"; $datenow = date("Y-m-d"); $chosen_date = date("Y-m-d"); $displayInputDate = "block"; $displayhref = "none";
    $header = ["Total Projects", "In Progress", "Projects Done"];
    $menuDua = "Master Project";
    if(isset($_SESSION['chosen_date'])){
        $chosen_date = $_SESSION['chosen_date'];
        unset($_SESSION['chosen_date']);
    }
    $current_staff = [];
    $current_name = "--";
    if(isset($_SESSION['staff_login'])){
        $current_staff = $_SESSION['staff_login'];
        $current_name = $current_staff['staff_name'];
        if($current_staff['flgAsAdmin'] == 0){
            $visibility = "hidden";
            $header = ["Today Jobs", "Jobs Not Done", "Jobs Done"];
            $menuDua = "Projects";
        }
    }
    else{
        header("location:index.php");
    }

    if(isset($_POST['toProject'])){
        $id = $_POST['id'];
        
        $result = mysqli_query($conn, "SELECT project_id, project_name, project_total, start_date, project_duration, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end
 FROM project_header WHERE project_id = '$id'");
        while($row = mysqli_fetch_array($result)){
            $data["project_id"] = $row["project_id"];
            $data["project_name"] = $row["project_name"];
            $data["project_total"] = $row["project_total"];
            $data["start_date"] = $row["start_date"];
            $data["project_duration"] = $row["project_duration"];
            $data["target_end"] = $row["target_end"];
        }
        mysqli_free_result($result);
        
        $_SESSION["chosen_project"] = $data;
        header("location:project.php");
    }

    if(isset($_POST['addNote'])){
        $fullDate = date("Y-m-d H:i:s");
        $id = $_POST['id'];
        $note = $_POST['note'];
        
        mysqli_query($conn, "UPDATE project_detail_l2 SET staff_note = '$note', operator = '$current_name', waktuproses = '$fullDate' WHERE sub_detail_id = '$id'");
    }
    
    $list = [];

    function defineQuery($paramquery, $staff){
        if($staff['flgAsAdmin'] == 1){ 
            $paramquery = $paramquery." ORDER BY project_id";
        }
        else{
            $paramquery = $paramquery." and s.staff_id = '".$staff['staff_id']."' ORDER BY p.project_id, l2.sub_detail_end";
        }
        return $paramquery;
    }

    //query awal, sblm pilih kategori total, in progress dan done.
    $query = "SELECT p.project_id, p.project_name, l1.detail_name, l2.sub_detail_id, l2.sub_detail_name, s.staff_name, l2.sub_detail_end, l2.staff_note, l2.flag FROM project_header p, project_detail_l1 l1, project_detail_l2 l2 LEFT JOIN master_staff s ON (s.staff_id = l2.staff_id) WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id and l2.sub_detail_start <= '$chosen_date' and l2.sub_detail_end >= '$chosen_date'";
    if($current_staff['flgAsAdmin'] == 1){
        $displayInputDate = "none";
        $query = "SELECT *, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header";
    }
    
    if(isset($_SESSION['project_id'])){
        $pid = $_SESSION['project_id'];
        $query = $query." and p.project_id = '$pid'";
        unset($_SESSION['project_id']);
    }
    $query = defineQuery($query, $current_staff);
    

    if(isset($_POST['today'])){
        $query = "SELECT *, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header";
        if($current_staff['flgAsAdmin'] == 0){
            $displayInputDate = "none"; $displayhref = "block";
            $query = "SELECT p.project_id, p.project_name, l1.detail_name, l2.sub_detail_id, l2.sub_detail_name, s.staff_name, l2.sub_detail_end, l2.staff_note, l2.flag FROM project_header p, project_detail_l1 l1, project_detail_l2 l2, master_staff s WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id and l2.staff_id = s.staff_id and l2.sub_detail_start <= '$datenow' and l2.sub_detail_end >= '$datenow'";
        }
        $query = defineQuery($query, $current_staff);
    }

    if(isset($_POST['notdone'])){
        //project dimana maximum l2 nya masih ada yang end date lbh besar dari hari ini, maka project tsb blm selesai
        $query = "SELECT p.*, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header p, project_detail_l1 l1, project_detail_l2 l2 WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id GROUP BY p.project_id HAVING max(l2.sub_detail_end) >= '$datenow'";
        if($current_staff['flgAsAdmin'] == 0){
            $displayInputDate = "none"; $displayhref = "block";
            $query = "SELECT p.project_id, p.project_name, l1.detail_name, l2.sub_detail_id, l2.sub_detail_name, s.staff_name, l2.sub_detail_end, l2.staff_note, l2.flag FROM project_header p, project_detail_l1 l1, project_detail_l2 l2 LEFT JOIN master_staff s ON (s.staff_id = l2.staff_id) WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id and l2.flag = 0";
        }
        $query = defineQuery($query, $current_staff);
    }

    if(isset($_POST['done'])){
        //project dimana maximum l2 nya lbh kecil dari hari ini, maka diasumsikan sdh selesai
        $query = "SELECT p.*, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header p, project_detail_l1 l1, project_detail_l2 l2 WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id GROUP BY p.project_id HAVING max(l2.sub_detail_end) < '$datenow'";
        if($current_staff['flgAsAdmin'] == 0){
            $displayInputDate = "none"; $displayhref = "block";
            $query = "SELECT p.project_id, p.project_name, l1.detail_name, l2.sub_detail_id, l2.sub_detail_name, s.staff_name, l2.sub_detail_end, l2.staff_note, l2.flag FROM project_header p, project_detail_l1 l1, project_detail_l2 l2 LEFT JOIN master_staff s ON (s.staff_id = l2.staff_id) WHERE p.project_id = l1.project_id and l1.detail_id = l2.detail_id and l2.flag = 1";
        }
        $query = defineQuery($query, $current_staff);
    }

    $result = mysqli_query($conn, $query);
    while($r = mysqli_fetch_array($result)){
        $data['project_id'] = $r['project_id'];
        $data['project_name'] = $r['project_name'];
        if($current_staff['flgAsAdmin'] == 0){
            $data['detail_name'] = $r['detail_name'];
            $data['sub_detail_id'] = $r['sub_detail_id'];
            $data['sub_detail_name'] = $r['sub_detail_name'];
            $data['staff_name'] = $r['staff_name'];
            $data['sub_detail_end'] = $r['sub_detail_end'];
            $data['staff_note'] = $r['staff_note'];
            $data['flag'] = $r['flag'];
            $checkAdjust = mysqli_query($conn, "SELECT * FROM project_adjust WHERE sub_detail_id = '".$r['sub_detail_id']."'");
            $data['adjust'] = mysqli_num_rows($checkAdjust);
        }
        else{
            $data['project_total'] = $r['project_total'];
            $data['start_date'] = $r['start_date'];
            $data['project_duration'] = $r['project_duration'];
            $data['target_end'] = $r['target_end'];
        }
        array_push($list, $data);
    }

    
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
    <!-- CSS for datatable-->
    <link href="https://cdn.datatables.net/1.10.21/css/jquery.dataTables.min.css" rel="stylesheet">
    <style>
        .input{
            border: none;
        }
        .input:hover{
            border: 1px solid black;
        }
        .hrefProject:hover{
            color: dodgerblue;
            text-decoration: underline;
        }
        
        #bydate{
            display: <?=$displayhref?>;
        }
        
        #changeDate{
            display: <?=$displayInputDate?>;
        }
        
        .changeDate:hover{
            color: dodgerblue;
            text-decoration: underline;
        }
        #topBtn {
          display: none;
          position: fixed;
          bottom: 20px;
          right: 30px;
          z-index: 99;
          font-size: 18px;
          border: none;
          outline: none;
          background-color: red;
          color: white;
          cursor: pointer;
          padding: 15px;
          border-radius: 4px;
        }

        #topBtn:hover {
          background-color: #555;
        }
    </style>
</head>

<body class="fix-header">
    <!-- ============================================================== -->
    <!-- Preloader -->
    <!-- ============================================================== -->
    <div class="preloader">
        <svg class="circular" viewBox="25 25 50 50">
            <circle class="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10" />
        </svg>
    </div>
    <!-- ============================================================== -->
    <!-- Wrapper -->
    <!-- ============================================================== -->
    <div id="wrapper">
        <!-- ============================================================== -->
        <!-- Topbar header - style you can find in pages.scss -->
        <!-- ============================================================== -->
        <nav class="navbar navbar-default navbar-static-top m-b-0">
            <div class="navbar-header">
                <div class="top-left-part">
                    <!-- Logo -->
                    <a class="logo" href="dashboard.php">
                        <!-- Logo icon image, you can use font-icon also -->
                        <b>
                            <img src="./imgicon/logo2.ico" style="width: 25px; height: 30px" alt="home" class="light-logo" />
                        </b>
                        <!-- Logo text image you can use text also -->
                        <span class="hidden-xs">
                            <h2 style="float: left; font-family: arial; padding-top: 5px; padding-left: 10px">SoftQ</h2>
                        </span> 
                    </a>
                </div>
                <!-- /Logo -->
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
            <!-- /.navbar-header -->
            <!-- /.navbar-top-links -->
            <!-- /.navbar-static-side -->
        </nav>
        <!-- End Top Navigation -->
        <!-- ============================================================== -->
        <!-- Left Sidebar - style you can find in sidebar.scss  -->
        <!-- ============================================================== -->
        <div class="navbar-default sidebar" role="navigation">
            <div class="sidebar-nav slimscrollsidebar">
                <div class="sidebar-head">
                    <h3><span class="fa-fw open-close"><i class="ti-close ti-menu"></i></span> <span class="hide-menu">Navigation</span></h3>
                </div>
                <ul class="nav" id="side-menu">
                    <li style="padding: 70px 0 0;">
                        <a href="dashboard.php" class="waves-effect"><i class="fa fa-clock-o fa-fw" aria-hidden="true"></i>Dashboard</a>
                    </li>
                    <li>
                        <a href="listproject.php" class="waves-effect"><i class="fa fa-table fa-fw" aria-hidden="true"></i><?=$menuDua?></a>
                    </li>
                    <li style="visibility: <?=$visibility?>">
                        <a href="teams.php" class="waves-effect"><i class="fa fa-user fa-fw" aria-hidden="true"></i>Master Staff</a>
                    </li>
                </ul>
            </div>
            
        </div>
        <!-- ============================================================== -->
        <!-- End Left Sidebar -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Page Content -->
        <!-- ============================================================== -->
        <div id="page-wrapper">
            <div class="container-fluid">
                <div class="row bg-title">
                    <div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
                        <h4 class="page-title"><a href="dashboard.php">Dashboard</a></h4> </div>
                    <!-- /.col-lg-12 -->
                </div>
                <!-- /.row -->
                <!-- ============================================================== -->
                <!-- Different data widgets -->
                <!-- ============================================================== -->
                <!-- .row -->
                <button id="topBtn" title="Go to top">Top</button>
                <div class="row">
                    <div class="col-lg-4 col-sm-6 col-xs-12">
                        <div class="white-box analytics-info">
                            <h3 class="box-title"><?=$header[0]?></h3>
                            <ul class="list-inline two-part">
                                <li>
                                    <div id="sparklinedash"></div>
                                </li>
                                <li class="text-right"><i class="ti-arrow-up text-success"></i> <span class="counter text-success">
                                    <form method="post">
                                        <input type="submit" name="today" id="totalProjects" style="background-color: white; border: none">
                                    </form>
                                    </span></li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-xs-12">
                        <div class="white-box analytics-info">
                            <h3 class="box-title"><?=$header[1]?></h3>
                            <ul class="list-inline two-part">
                                <li>
                                    <div id="sparklinedash2"></div>
                                </li>
                                <li class="text-right"><i class="ti-arrow-up text-purple"></i> <span class="counter text-purple">
                                    <form method="post">
                                        <input type="submit" name="notdone" id="inProgress" style="background-color: white; border: none">
                                    </form>
                                    </span></li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-xs-12">
                        <div class="white-box analytics-info">
                            <h3 class="box-title"><?=$header[2]?></h3>
                            <ul class="list-inline two-part">
                                <li>
                                    <div id="sparklinedash3"></div>
                                </li>
                                <li class="text-right"><i class="ti-arrow-up text-info"></i> <span class="counter text-info">
                                    <form method="post">
                                        <input type="submit" name="done" id="doneProjects" style="background-color: white; border: none">
                                    </form>
                                    </span></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <!-- ============================================================== -->
                <!-- table -->
                <!-- ============================================================== -->
                <div class="row">
                    <div class="col-md-12 col-lg-12 col-sm-12">
                        <div class="white-box">
                            <?php
                                if($current_staff['flgAsAdmin'] == 0){
                                    echo "<b class='box-title' style='font: 24px;'>To Do List</b>";
                                }
                                else{
                                    echo "<b class='box-title' style='font: 24px;'>Projects List</b>";
                                }
                            ?><br>
                            <input type="date" value="<?=$chosen_date?>" style="border: none; text-decoration: underline;" id="changeDate" title="Press Enter to Submit Change">
                            <input type="button" class="changeDate" id="bydate" style="border: none; background-color: white; text-decoration: italic; font-size: 12px;" name="allDate" onclick="displayDate()" value="[Manually Select By Date]">
                            <br>
                            <div class="table-responsive">
                                <table class="table" id="toDoList">
                                    <thead>
                                        <tr>
                                            <?php
                                                if($current_staff['flgAsAdmin'] == 0){
                                                    ?>
                                                    <th>#</th>
                                                    <th>ADJUST</th>
                                                    <th>JOB NAME</th>
                                                    <th>JOB DESCRIPTION</th>
                                                    <th>END DATE</th>
                                                    <th>STAFF NOTE</th>
                                                    <th>DONE</th>
                                                    <?php
                                                }
                                                else{
                                                    ?>
                                                    <th>#</th>
                                                    <th>PROJECT ID</th>
                                                    <th>PROJECT NAME</th>
                                                    <th>PROJECT TOTAL</th>
                                                    <th>START DATE</th>
                                                    <th>DURATION</th>
                                                    <th>TARGET END</th>
                                                    <?php
                                                }
                                            ?>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <?php
                                            if($current_staff['flgAsAdmin'] == 0){
                                                $lastProjectId = "";
                                                $ctr = 0;
                                                foreach($list as $l){
                                                    $ctr++;
                                                    if($l['project_id'] != $lastProjectId){
                                                        //jika sebelumnya sudah ditampilkan jgn tampilkan lagi, tampilkan sekali saja
                                                        ?>
                                                        <tr style="background-color: gainsboro;">
                                                            <td></td>
                                                            <td>
                                                                <form method="post">
                                                                    <input type="hidden" value="<?=$l['project_id']?>" name="id">
                                                                    <input type="submit" class="hrefProject" style="border: none; background-color: gainsboro;" value="<?=$l['project_id'].' - '.$l['project_name']?>" name="toProject">
                                                                </form>
                                                            </td>
                                                            <td></td><td></td><td></td><td></td><td></td><td></td>
                                                        </tr>
                                                        <?php
                                                    }
                                                ?>
                                                <tr>
                                                    <td><?=$ctr?></td>
                                                    <?php
                                                        if($l['adjust'] > 0){
                                                            //ada adjust, beri tanda
                                                            echo "<td style='color: red;'>Yes</td>";
                                                        }    
                                                        else{
                                                            echo "<td style='color: green;'>No</td>";
                                                        }
                                                    ?>
                                                    <td><?=$l['detail_name']?></td>
                                                    <td><?=$l['sub_detail_name']?></td>
                                                    <td><?=date_format(date_create($l['sub_detail_end']), "d M Y")?></td>
                                                    <td>
                                                        <form method="post" autocomplete="off">
                                                            <input type="hidden" value="<?=$l['sub_detail_id']?>" id="hiddenSubId<?=$ctr?>" name="id">
                                                            <input type="text" class="input" name="note" value="<?=$l['staff_note']?>" title="Press Enter to Submit Change">
                                                            <input type="submit" style="position: absolute; left: -9999px; width: 1px; height: 1px;" tabindex="-1" name="addNote"/>
                                                        </form>
                                                    </td>
                                                    <td>
                                                        <?php
                                                            if($l['flag'] == 0){
                                                                echo "<input type='checkbox' id='checkBox$ctr' onclick='check($ctr)'";
                                                            }
                                                            else{
                                                                echo "<input type='checkbox' id='checkBox$ctr' onclick='check($ctr)' checked>";
                                                            }
                                                        ?>
                                                    </td>
                                                </tr>
                                                    <?php
                                                //simpan project id, agar digunakan pengecekan project id berikutnya
                                                $lastProjectId = $l['project_id'];
                                                }
                                            }
                                            else{
                                                $ctr = 0;
                                                foreach($list as $l){
                                                    $ctr++;
                                                    ?>
                                                    <tr>
                                                        <td><?=$ctr?></td>
                                                        <td>
                                                            <form method="post">
                                                                <input type="hidden" value="<?=$l['project_id']?>" name="id">
                                                                <input type="submit" class="hrefProject" style="border: none; background-color: white;" value="<?=$l['project_id']?>" name="toProject">
                                                            </form>
                                                        </td>
                                                        <td><?=$l['project_name']?></td>
                                                        <td><?=number_format($l['project_total'], 0, '', '.')?></td>
                                                        <td><?=date_format(date_create($l['start_date']), 'd M Y')?></td>
                                                        <td><?=$l['project_duration']." Days"?></td>
                                                        <td><?=date_format(date_create($l['target_end']), 'd M Y')?></td>
                                                    </tr>
                                                    <?php
                                                }
                                            }
                                        ?>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.container-fluid -->
        </div>
        <!-- ============================================================== -->
        <!-- End Page Content -->
        <!-- ============================================================== -->
    </div>
    <!-- ============================================================== -->
    <!-- End Wrapper -->
    <!-- ============================================================== -->
    <!-- ============================================================== -->
    <!-- All Jquery -->
    <!-- ============================================================== -->
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
    <!-- JS for Data Table -->
    <script type="text/javascript" src="https://cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"></script>
    <script>
        $(document).ready(function(){
            $.ajax({
                url: "./json/jsonDashboard.php",
                data: {
                    flag: "<?=$current_staff['flgAsAdmin']?>",
                    staff_id: "<?=$current_staff['staff_id']?>"
                },
                success: function(data){
                    var categories = JSON.parse(data);
                    $("#totalProjects").val(categories.totalProjects);
                    $("#inProgress").val(categories.inProgress);
                    $("#doneProjects").val(categories.done);
                },
                method: 'GET'
            });
            
            $("#topBtn").click(function(){
                document.body.scrollTop = 0;
                document.documentElement.scrollTop = 0;
            });
            
            $("#changeDate").keydown(function(e){
                if(e.keyCode == 13){
                    $.ajax({
                        url : "assignSessionDate.php",
                        data : {"chosen_date":$(this).val()},
                        success : function(){
                            location.replace("dashboard.php");
                        },
                        method : "post"
                     });
                    
                    //3 baris code dibawah utk mencegah munculnya datepicker ketika di-enter
                    e.stopPropagation();
                    e.preventDefault();
                    return false;
                }
            });
        });
        
        window.onscroll = function() {scrollFunction()};

        function scrollFunction() {
          if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
            $("#topBtn").css("display", "block");
          } else {
            $("#topBtn").css("display", "none");
          }
        }
        
        function check(num){
            var subId = $("#hiddenSubId"+num).val();
            var flag = -1;
            if($("#checkBox"+num).prop("checked")){
                flag = 1;
            }
            else{
                flag = 0;
            }
            $.ajax({
                url: "./ajax/updateLevel2.php",
                data: {
                    "name":"<?=$current_name?>",
                    "id":"<?=$current_staff['staff_id']?>",
                    "admin":"<?=$current_staff['flgAsAdmin']?>",
                    "sub_id":subId,
                    "flag":flag
                },
                success: function(data){
                    var arrCategory = JSON.parse(data);
                    $("#totalProjects").val(arrCategory['totalProjects']);
                    $("#inProgress").val(arrCategory['inProgress']);
                    $("#doneProjects").val(arrCategory['done']);
                },
                method:"post"
            });
        }
        
        function displayDate(){
            $("#bydate").css("display", "none");
            $("#changeDate").css("display", "block");
        }
        
        window.onload = function(){
            setInterval(function(){
                $.ajax({
                    url: "./json/jsonDashboard.php",
                    data: {
                        flag: "<?=$current_staff['flgAsAdmin']?>",
                        staff_id: "<?=$current_staff['staff_id']?>"
                    },
                    success: function(data){
                        var categories = JSON.parse(data);
                        $("#totalProjects").val(categories.totalProjects);
                        $("#inProgress").val(categories.inProgress);
                        $("#doneProjects").val(categories.done);
                    },
                    method: 'GET'
                });
            }, 5000);
        }
    </script>
</body>

</html>
