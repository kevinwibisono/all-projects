<?php
    include("connection.php");
    $chosen_project;
    //dapatkan project yang dimaksud
    $result = mysqli_query($conn, "SELECT *, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header WHERE project_id = '".$_GET['project_id']."'");
    while($res = mysqli_fetch_array($result)){
        $chosen_project['project_id'] = $res['project_id'];
        $chosen_project['project_name'] = $res['project_name'];
        $chosen_project['domain_admin'] = $res['domain_admin'];
        $chosen_project['domain_name'] = $res['domain_name'];
        $chosen_project['project_total'] = $res['project_total'];
        $chosen_project['start_date'] = $res['start_date'];
        $chosen_project['project_duration'] = $res['project_duration'];
        $chosen_project['target_end'] = $res['target_end'];
    }
    $current_name = "--";

    if(isset($chosen_project)){        
        $operator = ""; $waktuproses = "0000-00-00 00:00:00"; $scrollTop = 0;
        $dividerSize = "500px"; $divSize = "500px";
        $jumSubDetail = 0;

        $query = "SELECT * FROM project_detail_l1 WHERE project_id = '".$chosen_project['project_id']."' ORDER BY step";

        //get Level 1 and Level 2 of project
        $level1 = [];
        $level2 = [];
        $adjusts = [];
        $files = [];
        
        $result = mysqli_query($conn, "SELECT * FROM project_files WHERE project_id = '".$chosen_project['project_id']."' ORDER BY waktuproses DESC");
        while($row = mysqli_fetch_array($result)){
            $data["file_id"] = $row["file_id"];
            $data["project_id"] = $row["project_id"];
            $data["filename"] = $row["filename"];
            $data["operator"] = $row["operator"];
            $data["waktuproses"] = $row["waktuproses"];
            array_push($files, $data);
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

        $res = mysqli_query($conn, "SELECT l2.*, s.staff_name FROM project_detail_l2 l2 LEFT JOIN master_staff s ON (l2.staff_id = s.staff_id) WHERE detail_id IN (SELECT detail_id FROM project_detail_l1 WHERE project_id = '".$chosen_project['project_id']."') ORDER BY step");
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

        $maxL1Duration = $chosen_project['project_duration'];
        $ulSize = (($maxL1Duration+1)*100)."px";

        $result = mysqli_query($conn, "SELECT * FROM master_staff");
        while($row = mysqli_fetch_array($result)){
            $data["staff_id"] = $row["staff_id"];
            $data["staff_name"] = $row["staff_name"];

            array_push($staffList, $data);
        }
        mysqli_free_result($result);
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
    <!--FULL CALENDAR IO-->
    <link href='css/main.css' rel='stylesheet' />
    
    <style>          
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
        
        .legend{
            width: 10px;
            height: 10px;
            margin: 0;
        }
		
		#jump7daysLeft{ padding-right: 10px; 300px; }
		
		#jump7daysRight{ padding-left: 10px; right: 70px; }
        
        .hrefUpload{ color: lightblue; }
        .hrefUpload:hover{ color: deepskyblue; }
        
        #contentDiv .fc-daygrid-day-events a{
            cursor: pointer;
            border: none;
        }
    </style>
</head>

<body class="fix-header">
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
                    <a class="logo">
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
                    <li>
                        <a><img src="./imgicon/AIQ61.png" alt="user-img" width="36" class="img-circle">&nbsp;<b class="hidden-xs"><?=$current_name?></b></a>
                    </li>
                </ul>
            </div>
        </nav>
        <!-- End Top Navigation -->
        <!-- Left Sidebar - style you can find in sidebar.scss  -->
        <?php
            if(isset($chosen_project)){
                ?>
                <div class="navbar-default sidebar" role="navigation">
                    <div class="sidebar-nav slimscrollsidebar" id="sidebarDiv">
                        <div class="sidebar-head">
                            <h3><span class="fa-fw open-close"><i class="ti-close ti-menu"></i></span> <span class="hide-menu">Navigation</span></h3>
                        </div>
                        <ul class="nav" id="side-menu" style="padding-bottom: 150px;">
                            <?php
                                $ctr = -1;
                                foreach($level1 as $l1){
                                    $ctr++;
                                    if($ctr == 0){ $pdTop = 70; }
                                    else{ $pdTop = 0; }
                            ?>
                                <input type="hidden" id="hiddenDetailId<?=$ctr?>" value="<?=$l1['detail_id']?>">
                                <li style="padding-top: <?=$pdTop?>px;">
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

                                    <ul class="nav" style="padding-left:30px;">
                                <?php
                                    $subctr = -1;
                                    foreach($level2 as $l2){
                                        $subctr++;
                                        if($l2["detail_id"] == $l1["detail_id"]){
                                ?>
                                        <input type="hidden" id="hiddenSubId<?=$subctr?>" value="<?=$l2['sub_detail_id']?>">
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
                <?php
            }
        ?>
        
        <!-- End Left Sidebar -->
    <div id="page-wrapper">
        <div class="container-fluid">
            <div class="row bg-title">
                <div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
                    <?php 
                        if(isset($chosen_project)){
                            ?>
                            <a class="page-title" style="font-size: 30px;"><?=$chosen_project['project_name']?></a>&nbsp;<em style="font-size: 14px;"><?=date_format(date_create($chosen_project['start_date']), 'd-M-Y');?> s/d <?=date_format(date_create($chosen_project['target_end']), 'd-M-Y');?></em><br>
                            <button style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 30px; margin-left: 5px;" data-toggle="modal" data-target="#myModal" onclick="displayDetail()">Detail</button>
                            <button style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 30px; margin-left: 5px;" data-toggle="modal" data-target="#myModal" onclick="displayFiles()">Files</button>
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
                                                        Project Domain:&nbsp;<a href="<?=$chosen_project['domain_name']?>" target='_blank' class="hrefUpload"><?=$chosen_project['domain_name']?></a><br><br>
                                                        Project Value:&nbsp;<b style="font-family: arial black;"><?=number_format($chosen_project['project_total'], 0, '', '.')?></b><br><br>
                                                        Start Date:&nbsp;<b style="font-family: arial black;"><?=date_format(date_create($chosen_project['start_date']), 'd-M-Y')?></b><br><br>
                                                        Duration (In Days):&nbsp;<b style="font-family: arial black;"><?=$chosen_project['project_duration']?></b><br><br>
                                                        Target End Date:&nbsp;<b style="font-family: arial black;"><?=date_format(date_create($chosen_project['target_end']), 'd-M-Y')?></b><br><br>
                                                    </div>
                                                    <div id='listFile'>
                                                    Click the name to open file below:<br>
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
                            <?php
                        }
                    ?>
                    
                </div>
            </div>

            <div class="row" style="background-color: white;">
				<div id="guide" style="clear: both; margin-left: 20px; margin-top: 10px;">
					<h3>Guide:</h3>
					<div style="float: left; padding-right: 15px;"><span class="legend" style="background-color: dodgerblue; float: left; margin-top: 5px;"></span>&nbsp;Finished</div>
					<div style="float: left; padding-right: 15px; margin-bottom: 10px;"><span class="legend" style="background-color: lightblue; float: left; margin-top: 5px;"></span>&nbsp;In Progress</div>
                    <div style="float: left; padding-right: 15px; margin-bottom: 10px;"><span class="legend" style="background-color: red; float: left; margin-top: 5px;"></span>&nbsp;Holidays & Sundays</div>
				</div><br><br>
                <div id="contentDiv" style="text-align: center;"></div>
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
    <script src="https://apis.google.com/js/api.js"></script>
    <!--FULL CALENDAR IO-->
    <script src='js/main.js'></script>
	<script src='https://unpkg.com/popper.js/dist/umd/popper.min.js'></script>
	<script src='https://unpkg.com/tooltip.js/dist/umd/tooltip.min.js'></script>
    <?php
        if(isset($chosen_project)){
            ?>
            <script>
                var arrDetail, arrSubDetail, arrAdjust, arrHoliday;
                var months = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];        
                var daysOfMonth = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];

                $.ajax({
                     url: "./json/jsonLevel1.php",
                     data: {project_id : "<?=$chosen_project['project_id']?>"},
                     success: function(data){
                         arrDetail = JSON.parse(data);
                         $.ajax({
                             url: "./json/jsonCalendar.php",
                             data: {project_id : "<?=$chosen_project['project_id']?>"},
                             success: function(data){
                                 arrSubDetail = JSON.parse(data);
                                 var calendarEl = document.getElementById('contentDiv');
                                 var calendar = new FullCalendar.Calendar(calendarEl, {
                                     eventMouseEnter: function(info){
//                                         alert(info.event.title);
                                        var startDate = new Date(info.event.start);
                                        //agar sesuai dengan tanggal aslinya, pada full calendar, tanggal END DATE harus DITAMBAH dgn 1 HARI
                                        //oleh karena itu, tanggal SESSUNGGUHNYA adalah tgl END DATE DIKURANGI 1 hari
                                        var endDate = new Date(Date.parse(info.event.end) - 24*3600*1000);
                                        var formattedStart = ('0'+startDate.getDate()).slice(-2)+" "+months[startDate.getMonth()]+" "+startDate.getFullYear();
                                        var formattedEnd = ('0'+endDate.getDate()).slice(-2)+" "+months[endDate.getMonth()]+" "+endDate.getFullYear();
									    $(info.el).popup({
                                            on: 'click',
                                            position: "top center",
                                            title    : info.event.title+" ["+formattedStart+" - "+formattedEnd+"]",
                                            content  : info.event.extendedProps.description
                                        });
									},
                                    initialView: 'dayGridMonth',
                                    events: arrSubDetail                
                                });
                                 calendar.render();
                                 //ketika calendar GANTI BULAN, maka tandai lagi tanggal-tanggal hari LIBUR
                                 $(".fc-header-toolbar.fc-toolbar button").click(function(event){
                                    markHolidaysnSunday();
                                 });
                             },
                             type: 'GET'
                         });
                     },
                     type: 'GET'
                 });
				 
				$(document).ready(function(){
					$.ajax({
						url : "https://www.googleapis.com/calendar/v3/calendars/en.indonesian%23holiday%40group.v.calendar.google.com/events?key=AIzaSyDoCNqjWSoeUS-dYXG0ahB0BI7N9O17E1o&timeMin=<?=$chosen_project['start_date']?>T10:00:00Z&timeMax=<?=$chosen_project['target_end']?>T10:00:00Z",
						type: 'GET',
						success: function(data){
							arrHoliday = data.items;
							markHolidaysnSunday();
						}
					});
					
					
				});
				 
				function markHolidaysnSunday(){
					var ctr = 0;
					const days = document.querySelectorAll(".fc-scrollgrid-sync-table td");
                    const hrefDays = document.querySelectorAll(".fc-scrollgrid-sync-table td .fc-daygrid-day-number");
					days.forEach(element => {
                        //MINGGU, tandai warna merah
                        var date = new Date(element.getAttribute("data-date").split("-").join("/"));
                        if(daysOfMonth[date.getDay()] == "Sun"){
                            $(hrefDays[ctr]).css("color", "red");
                        }
                        arrHoliday.forEach(h => {
                            if(element.getAttribute("data-date") == h['start']['date']){
                                $(hrefDays[ctr]).css("color", "red");
                                $(hrefDays[ctr]).popup({
                                    postion: "top center",
                                    title    : 'Holiday',
                                    content  : h['summary']
                                });
                            }
                        });
                        ctr++;
					});
//                    $(".fc-event-title-container").popup({
//                        position: "top center",
//                        title    : 'Holiday',
//                        content  : 'Holiday Time'
//                     });
				}
                
                function displayDetail(){
                    $(".box-title").html("Project Details");
                    $("#projectHeader").css("display", "block");
                    $("#listFile").css("display", "none");
                }
                
                function displayFiles(){
                    $(".box-title").html("Uploaded Files");
                    $("#projectHeader").css("display", "none");
                    $("#listFile").css("display", "block");
                }
                
                window.onclick = function(event) {
					$(".ui.custom.popup").removeClass("visible");
					$(".ui.custom.popup").addClass("hidden");
                }
            </script>
        
            <?php
        }
    ?>
    
</body>
</html>