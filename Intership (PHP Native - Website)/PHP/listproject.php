<?php
    include("connection.php");
    date_default_timezone_set("Asia/Bangkok");
    session_start();
    $visibility = "visible"; $menuDua = "Master Project";
    $current_staff = []; $current_name = "--";
    if(isset($_SESSION['staff_login'])){
        $current_staff = $_SESSION['staff_login'];
        $current_name = $current_staff['staff_name'];
        if($current_staff['flgAsAdmin'] == 0){
            $visibility = "hidden";
            $menuDua = "Projects";
        }
    }
    else{
        header("location:index.php");
    }
    $error = "";
    $query = "SELECT *, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header";
    if($current_staff['flgAsAdmin'] == 0){
        $id = $current_staff['staff_id'];
        $query = "SELECT p.*, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end FROM project_header p, project_detail_l1 l, project_detail_l2 l2 WHERE l.project_id = p.project_id and l.detail_id = l2.detail_id and l2.staff_id = '$id' GROUP BY p.project_id";
    }

    if(isset($_POST['detail'])){
        $id = $_POST['id'];
        $result = mysqli_query($conn, "SELECT *, DATE_ADD(start_date, INTERVAL project_duration DAY) as target_end
 FROM project_header WHERE project_id = '$id'");
        while($row = mysqli_fetch_array($result)){
            $data["project_id"] = $row["project_id"];
            $data["project_name"] = $row["project_name"];
            $data["domain_admin"] = $row["domain_admin"];
            $data["domain_name"] = $row["domain_name"];
            $data["project_total"] = $row["project_total"];
            $data["start_date"] = $row["start_date"];
            $data["project_duration"] = $row["project_duration"];
            $data["target_end"] = $row["target_end"];
        }
        mysqli_free_result($result);
        
        $_SESSION["chosen_project"] = $data;
        header("location:project.php");
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
    <!--<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">-->
    <style>
        #tableProjects i:hover{
            transform: scale(2);
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
          border-radius: 5px;
        }
        
        .sorting::after{display: none;}
        
        .sorting_desc::after{display: none;}
        
        .sorting_asc::after{display: none;}
        
        .singleFile{
            border: 1px solid black;
            margin: 10px;
            padding: 5px;
            max-height: 32px;
            max-width: 300px;
            float: left;
        }
        
        .singleFile i{
            display: none;
        }
        
        em{
            float: left;
            margin-top: 15px;
        }
        
        #addProjectStaffDiv{
            max-width: 300px;
            display: none;
            border: 1px solid black;
        }
        
        .headerRow{font-style: italic; background-color: gainsboro;}
    </style>
    <script>
        var oldName = "";
    </script>
</head>

<body class="fix-header">
    <!-- Preloader -->
    <div class="preloader">
        <svg class="circular" viewBox="25 25 50 50">
            <circle class="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10" />
        </svg>
    </div>
    <!-- Wrapper -->
    <div id="wrapper">
        <!-- Topbar header - style you can find in pages.scss -->
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
        </nav>
        <!-- End Top Navigation -->
        <!-- ============================================================== -->
        <!-- Left Sidebar - style you can find in sidebar.scss  -->
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
        <!-- End Left Sidebar -->
        <div id="page-wrapper">
            <div class="container-fluid">
                <div class="row bg-title">
                    <div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
                        <h4 class="page-title"><a href="listproject.php">List Of Projects</a></h4></div>
                    <!-- /.col-lg-12 -->
                </div>
                <div class="row">
                    <div class="col-md-12 col-lg-12 col-sm-12">
                        <div class="white-box">
                            <button id="topBtn" title="Go to top">Top</button>
                            <!-- ============================================================== -->
                            <!-- Modals -->
                            <!-- ============================================================== -->
                            <div class="modal fade" id="modal" role="dialog">
                                <div class="modal-dialog">

                                  <!-- Modal content-->
                                  <div class="modal-content">
                                    <div class="modal-header">
                                      <h3 class="modal-title">Add New Project</h3>
                                    </div>
                                    <div class="modal-body">
                                          <div class="row">
                                            <div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
                                                <div class="white-box">
                                                    <form action="" method="post" id="formAdd">
                                                        Project ID:<br><input type="text" name="id" id="addProjectId"><br><br>
                                                        Project Name:<br><input type="text" name="name" id="addProjectName" placeholder="ex: PHP Project"><br><br>
                                                        Admin Domain:<br><input type="text" name="admindomain" id="addAdminDomain" placeholder="ex: https:/www.example.com"><br><br>
                                                        Project Domain:<br><input type="text" name="domain" id="addProjectDomain" placeholder="ex: https:/www.example.com"><br><br>
                                                        Project Value:<br><input type="number" min="1" name="value" id="addProjectTotal" placeholder="million rupiahs"><br><br>
                                                        Start Date:<br><input type="date" name="start_date" id="addProjectDate"><br><br>
                                                        Project Duration (In Days):<br><input type="number" name="duration" min="1" max="730" id="addProjectDuration" placeholder="ex: 60 days"><br><br>
                                                        <input type="submit" value="Add" style="background-color: black; color: white; border: none; border-radius: 5px; width: 50px; height: 25px;" name="add">
                                                        <h3 style="color: red;" id="errorMsg"></h3>
                                                    </form>

                                                    <form method="post" id="formFilter">
                                                        Start Date Range:<br>
                                                        From:&nbsp;<input type="date" name="r_date_start" id="r_date_start">&nbsp;-&nbsp;To:&nbsp;<input type="date" name="r_date_end" id="r_date_end"><br><br>
                                                        <input type="submit" value="Apply Filter" style="background-color: black; color: white; border: none; border-radius: 5px; width: 100px; height: 40px;" name="filter" id="filterButton">
                                                    </form>

                                                    <form action="" method="post" id="formUpdate">
                                                        Project ID:<br><input type="text" style="border: none; font-size: 20px; text-decoration: bold;" readonly="true" name="id" id="updateProjectId"><br><br>
                                                        Project Name:<br><input type="text" name="name" id="updateProjectName"><br><br>
                                                        Admin Domain:<br><input type="text" name="admindomain" id="updateAdminDomain"><br><br>
                                                        Project Domain:<br><input type="text" name="domain" id="updateProjectDomain"><br><br>
                                                        Project Value:<br><input type="number" name="value" min="1" id="updateProjectTotal"><br><br>
                                                        Start Date:<br><input type="date" name="start_date" id="updateProjectDate"><br><br>
                                                        Project Duration (In Days):<br><input type="number" min="1" max="730" name="duration" id="updateProjectDuration"><br><br>
                                                        <input type="submit" value="Update" style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 40px;" name="update" id="btnUpdate">
                                                        <h3 style="color: red;" id="errorMsgUpdate"></h3><hr style="border: none; height: 2px; background-color: gray;">
                                                        <div id="addProjectStaffDiv" style="text-align: center;">
                                                            <h3 id="titleProjectStaff">Add New Staff: </h3>
                                                            Staff Name:<br><input type="text" id="addProjectStaffName" placeholder="name"><br><br>
                                                            Staff Password:<br><input type="text" id="addProjectStaffPassword" placeholder="name"><br><br>
                                                            Staff Position:<br><input type="text" id="addProjectStaffPosition" placeholder="ex: Project Manager"><br><br>
                                                            <input type="submit" id="addProjectStaff" style="background-color: black; color: white; border: none; border-radius: 5px; width: 60px; height: 25px;" value="Add">
                                                            <h3 style="color: red;" id="errorProjectStaff"></h3>
                                                        </div><br>
                                                        <button id="toggle">Add New Staff</button><br><br>
                                                        List Of Project Staff:
                                                        <div class="table-responsive">
                                                            <table class="table">
                                                                <thead>
                                                                    <tr>
                                                                        <th>#</th>
                                                                        <th style="text-align: right;">ACTION</th>
                                                                        <th>ID</th>
                                                                        <th>NAME</th>
                                                                        <th>PASSWORD</th>
                                                                        <th>POSITION</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody id="tableBody"></tbody>
                                                            </table>
                                                        </div>
                                                    </form>
                                                    
                                                    <form action="" method="post" id="formUpload" enctype="multipart/form-data">
                                                        <input type='hidden' name='id' id='uploadProjectId'>
                                                        Upload New File:<br><input type="file" id="uploadFile"><br>
                                                        <input type="button" value="Upload" style="background-color: black; color: white; border: none; border-radius: 5px; width: 55px; height: 25px;" id="uploadBtn">
                                                        <h3 style="color: grey" id="errorMsgUpload"></h3><br>
                                                        Uploaded Files:<br>
                                                        <div id='listFile'></div>
                                                    </form>
                                                    
                                                    <div id="formReport">
                                                        Select Staff:&nbsp;&nbsp;
                                                        <select id="reportStaff"></select><br><br>
                                                        Preview:<br><hr style="border: none; height: 2px; background-color: gray;">
                                                        <div id="reportPreview">
                                                            <div style="clear: both;">
                                                                <h3 style="float:left;">CV. SOFTQ DATA KOMPUTINDO</h3>
                                                                <h3 style="float:right;">WORK ORDER</h3>
                                                            </div>
                                                            <div style="clear: both;">
                                                                <label style="float: left; font-weight: 200;">Project Schedule</label>
                                                                <label style="float: right; font-weight: 200;">Print Date: <b><?=date('Y-m-d H:i:s')?></b></label>
                                                            </div>
                                                            <div style="clear: both;">
                                                                <label style="float: right; font-weight: 200;">Programmer/Staff Name: <b id="chosenStaffName"></b></label>
                                                            </div><br><br>
                                                            <div id="tableHeader" style="text-align: center; clear: both;">
                                                                <b id="reportPID" style="font-size: 20px;"></b>&nbsp;&nbsp;:&nbsp;&nbsp;<b id="reportPName" style="font-size: 20px;"></b> 
                                                            </div><br>
                                                            <div class="table-responsive">
                                                                <table class="table">
                                                                    <thead>
                                                                        <tr>
                                                                            <th>JOB DESCRIPTION</th>
                                                                            <th>START DATE</th>
                                                                            <th>END DATE</th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody id="tableContent"></tbody>
                                                                </table>
                                                            </div><br>
                                                            <div style="clear: both;">
                                                                <div style="float:left; width: 33.33%; text-align: center;"><b>Assigned To</b></div>
                                                                <div style="float:left; width: 33.33%; text-align: center;"><b>Approved By</b></div>
                                                                <div style="float:left; width: 33.33%; text-align: center;"><b>Approved by</b></div>
                                                            </div>
                                                            <div style="clear: both; margin-top: 80px;">
                                                                <div style="float:left; width: 33.33%; text-align: center;"><b id="signStaffName"></b></div>
                                                                <div style="float:left; width: 33.33%; text-align: center;"><b><?="(  CV. SOFTQ DATA KOMPUTINDO  )"?></b></div>
                                                                <div style="float:left; width: 33.33%; text-align: center;"><b id="signProjectName"></b></div>
                                                            </div>
                                                            <div style="clear: both;">
                                                                <ul style="padding-top: 30px;">
                                                                    <li>Invoice may follow after work order already done</li>
                                                                    <li>No complains after 7 days from this work order</li>
                                                                    <li>Work order can only be modified by SoftQ</li>
                                                                </ul>
                                                            </div>
                                                        </div><br><br><hr style="border: none; height: 2px; background-color: gray;">
                                                        <button style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 40px;" id="btnPrint">Print</button>
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
                            
                            <button style="background-color: black; color: white; border: none; border-radius: 5px; width: 50px; height: 25px; margin-bottom: 10px; visibility: <?=$visibility?>" data-toggle="modal" data-target="#modal" onclick="displayAdd()">Add</button>
                            
                            <button style="background-color: black; color: white; border: none; border-radius: 5px; width: 50px; height: 25px; visibility: <?=$visibility?>" data-toggle="modal" data-target="#modal" onclick="displayFilter()">Filter</button>
                            
                            <div class="table-responsive" id="divProjects">
                                <table id="tableProjects" class="display">
                                    <thead>
                                        <tr>
                                            <th style="text-align: right;">#</th>
                                            <th style="text-align: right;">ACTION</th>
                                            <th style="text-align: left;">NAME</th>
                                            <th style="text-align: left;">ADMIN DOMAIN</th>
                                            <th style="text-align: left;">PROJECT DOMAIN</th>
                                            <th style="text-align: right;">VALUE (Rp)</th>
                                            <th style="text-align: center;">START</th>
                                            <th style="text-align: center;">DURATION (DAYS)</th>
                                            <th style="text-align: center;">TARGET END DATE</th>
                                        </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
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
    <script type="text/javascript" src="https://cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
    <script>
        var arrProject, arrFile, current_project_id, current_staff_id, lastCtr = 0;
        var months = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
        $(document).ready(function () {
            loadProjects();
            
            $('#tableProjects').DataTable({
                "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
            });
			
			$('#tableProjects').on("length.dt", function(){
				loadProjects();
			});

            $("#formAdd").submit(function(event){
                event.preventDefault();
                var p = [];
                p['project_name'] = $("#addProjectName").val();
                p['project_id'] = $("#addProjectId").val().split(" ").join("");
                p['domain_admin'] = $("#addAdminDomain").val();
                p['domain_name'] = $("#addProjectDomain").val();
                p['project_total'] = $("#addProjectTotal").val();
                p['start_date'] = $("#addProjectDate").val();
                p['project_duration'] = $("#addProjectDuration").val();
                if(p['project_name'] == "" || p['start_date'] == "" || p['project_id'] == "" || p['project_total'] == "" || p['project_duration'] == "" || p['domain_name'] == "" || p['domain_admin'] == ""){
                    $("#errorMsg").html("Fields must not be null");
                }
				else if(parseInt(p['project_duration']) > 730){
					$("#errorMsgUpdate").html("Duration must be less or equal to 730 days");
				}
                else {
                    var unique = true;
                    for(var i=0;i<arrProject.length;i++){
                        if(arrProject[i].project_name == p['project_name'] || arrProject[i].project_id == p['project_id']){
                            unique = false;
                        }
                    }
                    if(!unique){
                        $("#errorMsg").html("Project name and ID must be unique");
                    }
                    else{
                        $("#errorMsg").html("");
                        
                        $.ajax({
                            url: "./ajax/addProject.php",
                            data: {
                                id: p['project_id'],
                                name: p['project_name'],
                                admindomain: p['domain_admin'],
                                domain: p['domain_name'],
                                value: p['project_total'],
                                start_date: p['start_date'],
                                duration: p['project_duration']
                            },
                            success: function(data){
                                //setelah add, dapatkan target end date nya
								var returnData = JSON.parse(data);
                                p['target_end'] = returnData['target_end'];
								p['domain_admin'] = returnData['admin_domain'];
								p['domain_name'] = returnData['project_domain'];
                                arrProject.push(p);
                                addProject(p, lastCtr);
                                lastCtr++;
                                
                                $("#addProjectName").val("");
                                $("#addProjectId").val("");
                                $("#addAdminDomain").val("");
                                $("#addProjectDomain").val("");
                                $("#addProjectTotal").val("");
                                $("#addProjectDate").val("");
                                $("#addProjectDuration").val("");
                                $("#addProjectId").focus();
                            },
                            method: 'post'
                        });
                    }
                }                
            });

            $("#btnUpdate").click(function(event){
                event.preventDefault();
                var p = [];
                p['project_id'] = current_project_id;
                p['project_name'] = $("#updateProjectName").val();
                p['domain_admin'] = $("#updateAdminDomain").val();
                p['domain_name'] = $("#updateProjectDomain").val();
                p['project_total'] = $("#updateProjectTotal").val();
                p['start_date'] = $("#updateProjectDate").val();
                p['project_duration'] = $("#updateProjectDuration").val();
                if(p['project_name'] == "" || p['start_date'] == "" || p['project_total'] == "" || p['project_duration'] == "" || p['domain_admin'] == "" || p['domain_name'] == ""){
                    $("#errorMsgUpdate").html("Fields must not be null");
                }
				else if(parseInt(p['project_duration']) > 730){
					$("#errorMsgUpdate").html("Duration must be less or equal to 730 days");
				}
                else {
                    if(p['project_name'] != oldName){
                        var unique = true;
                        for(var i=0;i<arrProject.length;i++){
                            if(arrProject[i].project_name == p['project_name']){
                                unique = false;
                            }
                        }
                        if(!unique){
                            $("#errorMsgUpdate").html("Project name must be unique");
                        }
                        else{
                            $("#errorMsgUpdate").html("");
                            updateProject(p);
                        }
                    }
                    else{
                        $("#errorMsgUpdate").html("");
                        updateProject(p);
                    }
                }                
            });

            $("#topBtn").click(function(){
                document.body.scrollTop = 0;
                document.documentElement.scrollTop = 0;
            });
            
            $("#uploadBtn").click(function(){
                var file_data = $('#uploadFile').prop('files')[0]; 
				if(file_data != undefined){
					$("#uploadFile").attr("disabled", "disabled");
					$("#uploadBtn").css("background-color", "lightgrey");
					$("#uploadBtn").attr("disabled", "disabled");
					$("#errorMsgUpload").css("color", "grey");
					$("#errorMsgUpload").html("Uploading...");
					var form_data = new FormData();
					form_data.append('file', file_data);
					form_data.append("id", current_project_id);
					form_data.append("staff_name", "<?=$current_staff['staff_name']?>");
					$.ajax({
						url: "./ajax/upload.php",
						data: form_data,
						success: function(){
							$("#uploadFile").removeAttr("disabled");
							$("#uploadBtn").css("background-color", "black");
							$("#uploadBtn").removeAttr("disabled");
							$("#errorMsgUpload").html("");
							refreshListFile();
						},
						processData: false,
						contentType: false,
						method: 'post'
					});
				}
                else{
					$("#errorMsgUpload").css("color", "red");
					$("#errorMsgUpload").html("File must be selected");
				}
            });
            
            $("#toggle").click(function(){
                event.preventDefault();
                var displayDiv = $("#addProjectStaffDiv").css("display");
                if(displayDiv == "block"){
                    $("#toggle").html("Add New Staff");
                    $("#titleProjectStaff").html("Add New Staff");
                    $("#addProjectStaffDiv").css("display", "none");
                    $("#addProjectStaffName").val("");
                    $("#addProjectStaffPassword").val("");
                    $("#addProjectStaffPosition").val("");
                    $("#errorProjectStaff").html("");
                    $("#addProjectStaff").val("Add");
                }
                else{
                    $("#toggle").html("Hide Form");
                    $("#addProjectStaffDiv").css("display", "block");
                }
            });
            
            $("#addProjectStaff").click(function(){
                event.preventDefault();
                var name = $("#addProjectStaffName").val();
                var password = $("#addProjectStaffPassword").val();
                var position = $("#addProjectStaffPosition").val();
                if(name == "" || password == "" || position == ""){
                    $("#errorProjectStaff").html("Field must not be null");
                }
                else{
                    var type = $("#addProjectStaff").val();
                    if(type == "Add"){
                        $.ajax({
                            url: "./ajax/addprojectstaff.php",
                            data: {
                                current_staff: "<?=$current_staff['staff_name']?>",
                                staff_name: name,
                                password: password,
                                position: position,
                                project_id: $("#updateProjectId").val(),
                            },
                            success: function(){
                                refreshListProjectStaff();
                                $("#errorProjectStaff").html("");
                                $("#addProjectStaffName").val("");
                                $("#addProjectStaffPassword").val("");
                                $("#addProjectStaffPosition").val("");
                                $("#addProjectStaffName").focus();
                            },
                            method: 'post'
                        });
                    }
                    else{
                        $.ajax({
                            url: "./ajax/updateProjectStaff.php",
                            data: {
                                current_staff: "<?=$current_staff['staff_name']?>",
                                staff_name: name,
                                Password: password,
                                staff_position: position,
                                staff_id: current_staff_id
                            },
                            success: function(){
                                refreshListProjectStaff();
                                $("#errorProjectStaff").html("");
                                $("#toggle").html("Add New Staff");
                                $("#titleProjectStaff").html("Add New Staff");
                                $("#addProjectStaffDiv").css("display", "none");
                                $("#addProjectStaffName").val("");
                                $("#addProjectStaffPassword").val("");
                                $("#addProjectStaffPosition").val("");
                                $("#errorProjectStaff").val("");
                                $("#addProjectStaff").val("Add");
                            },
                            method: 'post'
                        });
                    }
                    $("#errorProjectStaff").html("");
                    
                }
            });
            
            $("#reportStaff").change(function(){
                $("#tableContent").html("");
                $("#signStaffName").html("(  "+$("#reportStaff option:selected").text()+"  )");
                $("#chosenStaffName").html($("#reportStaff option:selected").text());
                //isi tabel sesuai staff yang dipilih
                $.ajax({
                    url: "./json/jsonLevel2Report.php",
                    data: {
                        project_id: $("#reportPID").html(),
                        staff_id: $("#reportStaff").val()
                    },
                    success: function(data){
                        var lastL1Done = "", lastL1Progress = "";
                        var arrL2 = JSON.parse(data);
                        var arrDone = arrL2.done;
                        var arrProgress = arrL2.progress;
                        arrDone.forEach(d => {
                            if(lastL1Done != d['detail_id']){
                                $("#tableContent").append("<tr><td class='headerRow' colspan='2'>"+d['detail_name']+"</td><td class='headerRow green' style='color: green;'>DONE</td></tr>");
                            }
                            $("#tableContent").append("<tr><td>"+d['sub_detail_name']+"</td><td>"+d['sub_detail_start']+"</td><td>"+d['sub_detail_end']+"</td></tr>");
                            lastL1Done = d['detail_id'];
                        });
                        arrProgress.forEach(d => {
                            if(lastL1Progress != d['detail_id']){
                                $("#tableContent").append("<tr><td class='headerRow' colspan='2'>"+d['detail_name']+"</td><td class='headerRow red' style='color: red;'>IN PROGRESS</td></tr>");
                            }
                            $("#tableContent").append("<tr><td>"+d['sub_detail_name']+"</td><td>"+d['sub_detail_start']+"</td><td>"+d['sub_detail_end']+"</td></tr>");
                            lastL1Progress = d['detail_id'];
                        });
                    }, 
                    type: 'GET'
                });
            });
            
            $("#btnPrint").click(function(){
                var newWindow = window.open();
                //SERTAKAN FILE utk CSS nya
                newWindow.document.write("<head><link href='bootstrap/dist/css/bootstrap.css' rel='stylesheet'><link href='https://cdn.datatables.net/1.10.21/css/jquery.dataTables.min.css' rel='stylesheet'><style>.headerRow{font-style: italic; background-color: gainsboro;} @media print { .table .headerRow {background-color: gainsboro !important; font-style: italic} .headerRow.green{color: green !important;} .headerRow.red{color: red !important;}}</style></head>");
                
                //CONTENT
                newWindow.document.write($('#reportPreview').html());
                
                //SERTAKAN FILE utk JS nya
                var script = newWindow.document.createElement("script");
                script.src = "./plugins/bower_components/jquery/dist/jquery.min.js";
                newWindow.document.write(script.outerHTML);
                var script = newWindow.document.createElement("script");
                script.src = "./js/printClose.js";
                newWindow.document.write(script.outerHTML);
            });
            
            $("#filterButton").click(function(){
                event.preventDefault();
                $("#filterButton").attr("data-dismiss", "modal");
                var rangeStart = $("#r_date_start").val();
                $("#r_date_start").val("");
                var rangeEnd = $("#r_date_end").val();
                $("#r_date_end").val("");
                if(rangeStart == ""){
                    rangeStart = "1970-01-01";
                }
                if(rangeEnd == ""){
                    rangeEnd = "9999-12-31";
                }
                Swal.fire({
                    title: 'Loading All Projects...',
                    text: 'Please Wait Patiently',
                    imageUrl: './imgicon/loading2.gif',
                    imageWidth: 400,
                    imageHeight: 200,
                    imageAlt: 'Custom image',
                    showConfirmButton: false,
                    allowOutsideClick: false
                });
                $.ajax({
                    url: "./json/jsonProjectFiltered.php",
                    data: {
                        range_end: rangeEnd,
                        range_start: rangeStart
                    },
                    success: function(data){
                        arrProject = JSON.parse(data);
                        
                        //tampilkan PROJECTS pada table
                        $("#tableProjects tbody").html("");
                        var ctr = 0;
                        arrProject.forEach(p => {
                            ctr++;
                            addProject(p, ctr);
                        });
                        lastCtr = ctr + 1;

                        Swal.close();
                    }, 
                    type: 'GET'
                });
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
		
		function loadProjects(){
			Swal.fire({
                title: 'Loading All Projects...',
                text: 'Please Wait Patiently',
                imageUrl: './imgicon/loading2.gif',
                imageWidth: 400,
                imageHeight: 200,
                imageAlt: 'Custom image',
                showConfirmButton: false,
                allowOutsideClick: false
            });
            if("<?=$current_staff['flgAsAdmin']?>" == "0"){
                $.ajax({
                    url: "./json/jsonProject.php",
                    data: {
                        staff_id: "<?=$current_staff['staff_id']?>"
                    },
                    success: function(data){
                        arrProject = JSON.parse(data);
                        
                        //tampilkan PROJECTS pada table
                        $("#tableProjects tbody").html("");
                        var ctr = 0;
                        arrProject.forEach(p => {
                            ctr++;
                            addProject(p, ctr);
                        });
                        lastCtr = ctr + 1;

                        Swal.close();
                    }, 
                    type: 'GET'
                });
            }
            else{
                $.ajax({
                    url: "./json/jsonProject.php",
                    success: function(data){
                        arrProject = JSON.parse(data);
                        
                        //tampilkan PROJECTS pada table
                        $("#tableProjects tbody").html("");
                        var ctr = 0;
                        arrProject.forEach(p => {
                            ctr++;
                            addProject(p, ctr);
                        });
                        lastCtr = ctr + 1;

                        Swal.close();
                    }, 
                    type: 'GET'
                });
            }
		}
        
        function addProject(p, ctr){
            $("#tableProjects tbody").append("<tr id='"+p['project_id']+"'></tr>");
            $("#"+p['project_id']).append("<td style='text-align: right;' class='rowCounter'>"+ctr+"</td>");
            $("#"+p['project_id']).append("<td id='actions"+p['project_id']+"'></td>");
            $("#actions"+p['project_id']).append("<form method='post' style='float:right;'><input type='hidden' name='id' value='"+p['project_id']+"' id='projectId"+p['project_id']+"'><button type='submit' name='detail' style='border: none; background-color: transparent;'><i class='fa fa-info-circle' aria-hidden='true'></i></button></form>");
            if("<?=$current_staff['flgAsAdmin']?>" == "1"){
                $("#actions"+p['project_id']).append(`<button data-toggle='modal' data-target='#modal' style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="displayReport('`+p['project_id']+`')"><i class='fa fa-print' style='color: black;' aria-hidden='true'></i></button>`);
                $("#actions"+p['project_id']).append(`<button data-toggle='modal' data-target='#modal' style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="displayUpload('`+p['project_id']+`')"><i class='fa fa-folder-open' style='color: goldenrod;' aria-hidden='true'></i></button>`);
                $("#actions"+p['project_id']).append(`<button data-toggle='modal' data-target='#modal' style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="displayProject('`+p['project_id']+`')"><i class='fa fa-pencil-square' style='color: blue;' aria-hidden='true'></i></button>`);
                $("#actions"+p['project_id']).append(`<button style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="deleteProject('`+p['project_id']+`', '`+p['project_name']+`')"><i class='fa fa-trash' style='color: red;' aria-hidden='true'></i></button>`);
            }
            $("#"+p['project_id']).append("<td style='text-align: left;' id='projectName"+p['project_id']+"'>"+p['project_name']+"</td>");
            $("#"+p['project_id']).append("<td style='text-align: left;'><a href='"+p['domain_admin']+"' target='_blank'>"+p['domain_admin']+"</a></td>");
            $("#"+p['project_id']).append("<td style='text-align: left;'><a href='"+p['domain_name']+"' target='_blank'>"+p['domain_name']+"</a></td>");
            $("#"+p['project_id']).append("<td style='text-align: right;'>"+parseFloat(p['project_total']).toLocaleString('en')+"</td>");
            $("#"+p['project_id']).append("<td style='text-align: right;'>"+formatDate(p['start_date'])+"</td>");
            $("#"+p['project_id']).append("<td style='text-align: right;'>"+p['project_duration']+"</td>");
            $("#"+p['project_id']).append("<td style='text-align: right;'>"+p['target_end']+"</td>");
        }    
        
        function updateProject(p){
            $("#btnUpdate").attr("data-dismiss", "modal");
            $.ajax({
                url: "./ajax/updateProject.php",
                data: {
                    id: p['project_id'],
                    name: p['project_name'],
                    admindomain: p['domain_admin'],
                    domain: p['domain_name'],
                    value: p['project_total'],
                    start_date: p['start_date'],
                    duration: p['project_duration'],
                    staff_name: "<?=$current_staff['staff_name']?>"
                },
                success: function(data){
                    //setelah add, dapatkan target end date nya
                    var returnData = JSON.parse(data);
					p['target_end'] = returnData['target_end'];
					p['domain_admin'] = returnData['admin_domain'];
					p['domain_name'] = returnData['project_domain'];
                    arrProject.forEach(pro =>{
                        if(pro['project_id'] == p['project_id']){
                            pro['project_name'] = p['project_name'];
                            pro['domain_admin'] = p['domain_admin'];
                            pro['domain_name'] = p['domain_name'];
                            pro['project_total'] = p['project_total'];
                            pro['start_date'] = p['start_date'];
                            pro['target_end'] = p['target_end'];
                        } 
                    });

                    //hapus semua td kecuali untuk number
                    $("#"+p['project_id']+" td").not(":first").remove();
                    $("#"+p['project_id']).append("<td id='actions"+p['project_id']+"'></td>");
                    $("#actions"+p['project_id']).append("<form method='post' style='float:right;'><input type='hidden' name='id' value='"+p['project_id']+"' id='projectId"+p['project_id']+"'><button type='submit' name='detail' style='border: none; background-color: transparent;'><i class='fa fa-info-circle' aria-hidden='true'></i></button></form>");
                    if("<?=$current_staff['flgAsAdmin']?>" == "1"){
                        $("#actions"+p['project_id']).append(`<button data-toggle='modal' data-target='#modal' style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="displayReport('`+p['project_id']+`')"><i class='fa fa-print' style='color: black;' aria-hidden='true'></i></button>`);
                        $("#actions"+p['project_id']).append(`<button data-toggle='modal' data-target='#modal' style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="displayUpload('`+p['project_id']+`')"><i class='fa fa-folder-open' style='color: goldenrod;' aria-hidden='true'></i></button>`);
                        $("#actions"+p['project_id']).append(`<button data-toggle='modal' data-target='#modal' style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="displayProject('`+p['project_id']+`')"><i class='fa fa-pencil-square' style='color: blue;' aria-hidden='true'></i></button>`);
                        $("#actions"+p['project_id']).append(`<button style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="deleteProject('`+p['project_id']+`', '`+p['project_name']+`')"><i class='fa fa-trash' style='color: red;' aria-hidden='true'></i></button>`);
                    }
                    $("#"+p['project_id']).append("<td style='text-align: left;' id='projectName"+p['project_id']+"'>"+p['project_name']+"</td>");
                    $("#"+p['project_id']).append("<td style='text-align: left;'><a href='"+p['domain_admin']+"' target='_blank'>"+p['domain_admin']+"</a></td>");
                    $("#"+p['project_id']).append("<td style='text-align: left;'><a href='"+p['domain_name']+"' target='_blank'>"+p['domain_name']+"</a></td>");
                    $("#"+p['project_id']).append("<td style='text-align: right;'>"+parseFloat(p['project_total']).toLocaleString('en')+"</td>");
                    $("#"+p['project_id']).append("<td style='text-align: right;'>"+formatDate(p['start_date'])+"</td>");
                    $("#"+p['project_id']).append("<td style='text-align: right;'>"+p['project_duration']+"</td>");
                    $("#"+p['project_id']).append("<td style='text-align: right;'>"+p['target_end']+"</td>");
                },
                method: 'post'
            });
        }
        
        function deleteProject(project_id, namaProject){
            var cfr = confirm("Are you sure you want to delete "+namaProject+"?");
            if(cfr){
                var cfr2 = confirm("WARNING! Deleting project will also delete its details and staffs. Do you still want to continue?");
                if(cfr2){
                    $.ajax({
                        url: "./ajax/deleteProject.php",
                        data: {project_id: project_id},
                        success: function(){
                            var number = parseInt($("#"+project_id+" .rowCounter").html());
                            $("#"+project_id).remove();
                            var rowProjects = document.querySelectorAll("#tableProjects .rowCounter");
							lastCtr = -999;
                            rowProjects.forEach(r =>{
                                var current_number = parseInt(r.textContent);
								//ketika delete suatu project, jangan lupa untuk menaikkan urutan project lainnya dan update lastCtr dengan angka terbesar skrg
                                if(current_number > number){
									current_number--;
                                    r.textContent = current_number;
                                }
								if(current_number > lastCtr){
									lastCtr = current_number;
								}
                            });
							lastCtr++;
							var idx = -1, ctr = 0;
							arrProject.forEach(p => {
								if(p['project_id'] == project_id){
									idx = ctr;
								}
								ctr++;
							});
							arrProject.splice(idx, 1);
                        },
                        method: 'get'
                    });
                }
            }
        }
        
        function deleteFile(num){
            event.preventDefault();
            $.ajax({
                url: "./ajax/deleteUpload.php",
                data: {fileId: $("#fileId"+num).val(), filename: $("#filename"+num).val()},
                success: function(){
                    refreshListFile();
                },
                method: 'post'
            })
        }
        
        function displayProject(id){
            changeModal("block", "none", "none", "none", "none");
            current_project_id = id;
            var project = [];
            arrProject.forEach(p => {
                if(p['project_id'] == id){
                    project = p;
                }
            });
            
            oldName = project['project_name'];
            
            $(".modal-title").html(project['project_name']);

            $("#updateProjectId").val(project['project_id']);
            $("#updateProjectName").val(project['project_name']);
            $("#updateAdminDomain").val(project['domain_admin']);
            $("#updateProjectDomain").val(project['domain_name']);
            $("#updateProjectTotal").val(project['project_total']);
            $("#updateProjectDate").val(project['start_date']);
            $("#updateProjectDuration").val(project['project_duration']);
            refreshListProjectStaff();
        }

        function displayAdd(){
            $(".modal-title").html("Add New Project");
            changeModal("none", "block", "none", "none", "none");
        }
        
        function displayUpload(id){
            current_project_id = id, nama = "";
            arrProject.forEach(a =>{
                if(a['project_id'] == current_project_id){
                    nama = a['project_name'];
                }
            });
            $("#uploadProjectId").val(current_project_id);
            $(".modal-title").html(nama);
            changeModal("none", "none", "none", "block", "none");
            $("#listFile").html("");
            refreshListFile();
        }

        function displayFilter(){
            $(".modal-title").html("Filter Project");
            changeModal("none", "none", "block", "none", "none");
        }
        
        function displayReport(id){
            $("#tableContent").html("");
            $(".modal-title").html("Print Report");
            $("#reportStaff").html("");
            $("#chosenStaffName").html("");
            $("#signStaffName").html("(    )");
            //termukan semua staff yang berhubungan dengan project ini, kemudian tampilkan sebagai pilihan di combo box
            $.ajax({
                url: "./json/jsonStaff.php",
                data: {project_id: id},
                success: function(data){
                    var arrStaff = JSON.parse(data);
                    arrStaff.forEach(s => {
                        $("#reportStaff").append("<option value='"+s['staff_id']+"'>"+s['staff_name']+"</option>");
                    });
                    $("#reportStaff").val("");
                    $("#reportPID").html(id);
                    $("#reportPName").html($("#projectName"+id).html());
                    $("#signProjectName").html("(  "+$("#projectName"+id).html()+"  )");
                },
                method: 'get'
            });
            changeModal("none", "none", "none", "none", "block");
        }
        
        function changeModal(update, add, filter, upload, report){
            $("#formUpdate").css("display", update);
            $("#formAdd").css("display", add);
            $("#formFilter").css("display", filter);
            $("#formUpload").css("display", upload);
            $("#formReport").css("display", report);
        }
        
        function refreshListFile(){
            $("#listFile").html("");
            $.ajax({
                url: "./json/jsonFile.php",
                data: {"project_id" : current_project_id},
                success: function(data){
                    var arrFile = JSON.parse(data);
                    var counter = 0;
                    arrFile.forEach(f => {
                        counter++;
                        var file = "";
                        if(String(f['filename']).length > 38){
                            file = String(f['filename']).substr(0, 37);
                        }
                        else{
                            file = f['filename'];
                        }
                        $("#listFile").append("<div style='clear: both;'><div class='singleFile' id='singleFile"+counter+"'><a href='./uploads/"+f['filename']+"' target='_blank'>"+file+"</a></div><em>"+f['waktuproses']+"</em></div>");
                        $("#singleFile"+counter).append("<form method='post' style='float: right;'><input type='hidden' id='filename"+counter+"' value="+f['filename']+"><input type='hidden' id='fileId"+counter+"' value='"+f['file_id']+"'><button style='border:none; background-color: white;' id='deleteFile"+counter+"' onclick='deleteFile("+counter+")'><i class='fa fa-times' aria-hidden='true'></i></button></form>");
                    });
                    $(".singleFile").mouseenter(function(){
                        var id = jQuery(this).attr("id");
                        $("#"+id+" i").css("display", "inline");
                    });
                    $(".singleFile").mouseleave(function(){
                        var id = jQuery(this).attr("id");
                        $("#"+id+" i").css("display", "none");
                    });
                    $("#uploadFile").val(null);
                },
                method: 'get'
            });
        }
        
        function refreshListProjectStaff(){
            $("#tableBody").html("");
            $.ajax({
                url: "./json/listprojectstaff.php",
                data: {project_id: $("#updateProjectId").val()},
                success: function(data){
                    var arrProjectStaff = JSON.parse(data);
                    var counter = 0;
                    arrProjectStaff.forEach(a => {
                        counter++;
                        $("#tableBody").append("<tr id='row"+counter+"'></tr>");
                        
                        $("#row"+counter).append("<td>"+counter+"</td>");
                        $("#row"+counter).append(`<td><button style="float: right; border: none; background-color: white;" onclick="updateProjectStaff('`+a['staff_id']+`', '`+a['staff_name']+`', '`+a['Password']+`', '`+a['staff_position']+`')"><i class="fa fa-pencil-square" style="color: blue;" aria-hidden="true"></i></button><button style="float: right; border: none; background-color: white;" onclick="deleteProjectStaff('`+a['staff_name']+`', '`+a['staff_id']+`')"><i class="fa fa-trash" style="color: red;" aria-hidden="true"></i></button></td>`);
                        $("#row"+counter).append("<td>"+a['staff_id']+"</td><td>"+a['staff_name']+"</td><td>"+a['Password']+"</td><td>"+a['staff_position']+"</td>");
                    });
                }
            });
        }
        
        function deleteProjectStaff(name, id){
            event.preventDefault();
            var cfr = confirm("Are you sure you want to delete "+name+"?");
            if(cfr){
                $.ajax({
                    url: "./ajax/deleteProjectStaff.php",
                    data: {staff_id: id},
                    success: function(){
                        refreshListProjectStaff();
                    },
                    method: 'get'
                });
            }
        }
        
        function formatDate(d){
            var displayDate = ('0'+new Date(d).getDate()).slice(-2)+"-"+months[new Date(d).getMonth()]+"-"+new Date(d).getFullYear();
            return displayDate;
        }
        
        function updateProjectStaff(staff_id, staff_name, Password, staff_position){
            event.preventDefault();
            $("#errorProjectStaff").html("");
            $("#toggle").html("Hide Form");
            $("#titleProjectStaff").html("Update Staff");
            $("#addProjectStaffDiv").css("display", "block");
            $("#addProjectStaff").val("Update");
            current_staff_id = staff_id;
            $("#addProjectStaffName").val(staff_name);
            $("#addProjectStaffPassword").val(Password);
            $("#addProjectStaffPosition").val(staff_position);
        }
        
        window.onload = function(){
            
        }
    </script>
</body>

</html>