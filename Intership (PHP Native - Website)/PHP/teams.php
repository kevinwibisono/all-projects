<?php
    include("connection.php");
    date_default_timezone_set("Asia/Bangkok");
    session_start();
    $visibility = "visible"; $menuDua = "Master Project";
    $current_staff = []; $current_name = "--"; $current_id = "";
    if(isset($_SESSION['staff_login'])){
        $current_staff = $_SESSION['staff_login'];
        $current_name = $current_staff['staff_name'];
        $current_id = $current_staff['staff_id'];
        if($current_staff['flgAsAdmin'] == 0){
            $visibility = "hidden";
        }
    }
    else{
        header("location:index.php");
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
    <link href="https://cdn.datatables.net/1.10.21/css/jquery.dataTables.min.css" rel="stylesheet">
    <!--<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">-->
    <style>
        #tableStaffs i:hover{
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
          border-radius: 4px;
        }

        #topBtn:hover {
          background-color: #555;
        }
        
        .sorting::after{display: none;}
        
        .sorting_desc::after{display: none;}
        
        .sorting_asc::after{display: none;}
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
                        <a href="listproject.php" class="waves-effect"><i class="fa fa-table fa-fw" aria-hidden="true"></i>Master Project</a>
                    </li>
                    <li>
                        <a href="teams.php" class="waves-effect"><i class="fa fa-user fa-fw" aria-hidden="true"></i>Master Staff</a>
                    </li>
                </ul>
            </div>
            
        </div>
        <!-- ============================================================== -->
        <!-- End Left Sidebar -->
        <!-- ============================================================== -->
        <div id="page-wrapper">
            <div class="container-fluid">
                <div class="row bg-title">
                    <div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
                        <h4 class="page-title"><a href="teams.php">List Of Staffs</a></h4></div>
                    <!-- /.col-lg-12 -->
                </div>
                <div class="row">
                    <div class="col-md-12 col-lg-12 col-sm-12">
                        <div class="white-box">
                            <button id="topBtn" title="Go to top">Top</button>
                            
                            <button style="background-color: black; color: white; border: none; border-radius: 5px; width: 50px; height: 25px; margin-bottom: 10px; visibility: <?=$visibility?>" data-toggle="modal" data-target="#myModal" onclick = "displayAdd()">Add</button>
                            <div class="modal fade" id="myModal" role="dialog">
                                <div class="modal-dialog">

                                  <!-- Modal content-->
                                  <div class="modal-content">
                                    <div class="modal-header">
                                      <h3 class="box-title"></h3>
                                    </div>
                                    <div class="modal-body">
                                          <div class="row">
                                            <div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
                                                <div class="white-box">
                                                    <form action="" method="post" id="formAdd">
                                                        Staff Name:<br><input type="text" name="name" id="addStaffName" placeholder="name"><br><br>
                                                        Staff Password:<br><input type="text" name="password" id="addStaffPassword" placeholder="name"><br><br>
                                                        Staff Position:<br><input type="text" name="position" id="addStaffPosition" placeholder="ex: Project Manager"><br><br>
                                                        <input type="checkbox" name="flag" id="addStaffFlag">&nbsp;flag this staff as admin<br><br>
                                                        <input type="submit" value="Add" style="background-color: black; color: white; border: none; border-radius: 5px; width: 50px; height: 25px;" name="add">
                                                        <h3 style="color: red;" id="errorAdd"></h3>
                                                    </form>

                                                    <form action="" method="post" id="formUpdate">
                                                        Staff Id:<br><input type="text" style="border: none; font-size: 20px; text-decoration: bold;" readonly="true" name="id" id="updateStaffId"><br><br>
                                                        Staff Name:<br><input type="text" name="name" id="updateStaffName" placeholder="new name"><br><br>
                                                        Staff Password:<br><input type="text" name="password" id="updateStaffPassword" placeholder="new pass"><br><br>
                                                        Staff Position:<br><input type="text" name="position" id="updateStaffPosition" placeholder="new position"><br><br>
                                                        <input type="checkbox" name="flag" id="updateStaffFlag">&nbsp;flag this staff as admin<br><br>
                                                        <input type="submit" value="Update" style="background-color: black; color: white; border: none; border-radius: 5px; width: 75px; height: 40px;" name="update" id="btnUpdate">
                                                        <h3 style="color: red;" id="errorUpdate"></h3>
                                                    </form>
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
                            
                            <div class="table-responsive">
                                <table id="tableStaffs" class="display" style="text-align: right;">
                                    <thead>
                                        <tr>
                                            <th style="text-align: right;">#</th>
                                            <th style="text-align: right;">ACTION</th>
                                            <th style="text-align: left;">NAME</th>
                                            <th style="text-align: left;">PASSWORD</th>
                                            <th style="text-align: left;">POSITION</th>
                                            <th style="text-align: right;">FLAG ADMIN</th>
                                            <th style="text-align: left;">OPERATOR</th>
                                            <th style="text-align: center;">PROCESS TIME</th>
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
    <!-- MDBootstrap Datatables  -->
    <script type="text/javascript" src="https://cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
    <script>
        var arrStaff = [], current_staff_id = "", lastCtr = 0;
        $(document).ready(function () {
            loadStaffs();

            $('#tableStaffs').DataTable({
                "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
            });
			
			$('#tableStaffs').on("length.dt", function(){
                loadStaffs();
            });
            
            $("#formAdd").submit(function(event){
                event.preventDefault();
                var s = [];
                s['staff_name'] = $("#addStaffName").val();
                s['Password'] = $("#addStaffPassword").val();
                s['staff_position'] = $("#addStaffPosition").val();
                if($("#addStaffFlag").prop("checked")){
                    s['flgAsAdmin'] = 1;
                }
                else{
                    s['flgAsAdmin'] = 0;
                }
                if(s['staff_name'] == "" || s['Password'] == "" || s['staff_position'] == ""){
                    $("#errorAdd").html("Fields must not be null");
                }
                else{
                    var staffName = s['staff_name'].toLowerCase();
                    var staffPassword = s['Password'].toLowerCase();
                    if(staffName.indexOf(staffPassword) > -1){
                        $("#errorAdd").html("Pick stronger password");
                    }
                    else if(staffPassword.indexOf(staffName) > -1){
                        $("#errorAdd").html("Pick stronger password");
                    }
                    else{
                        $("#errorAdd").html("");
                        
                        $.ajax({
                            url: "./ajax/addStaff.php",
                            data: {
                                name: s['staff_name'],
                                password: s['Password'],
                                position: s['staff_position'],
                                flag: s['flgAsAdmin'],
                                staff_name: "<?=$current_staff['staff_name']?>"
                            },
                            success: function(data){
                                //setelah add, dapatkan autogen id nya
                                var staff = JSON.parse(data);
                                s['staff_id'] = staff.id;
                                s['operator'] = "<?=$current_staff['staff_name']?>";
                                s['waktuproses'] = staff.waktuproses;
                                s['project_id'] = "";
                                arrStaff.push(s);
                                addStaff(s, lastCtr);
                                lastCtr++;
                                
                                $("#addStaffName").val("");
                                $("#addStaffPassword").val("");
                                $("#addStaffPosition").val("");
                                $("#addStaffFlag").prop("checked", false);
                                $("#addStaffName").focus();
                            },
                            method: 'post'
                        });
                    }
                }
            });
            
            $(".deleteStaff").submit(function(){
                 var id = jQuery(this).attr("id");
                 id = id.substr(11);
                 var staffName = $("#staffName"+id).html();
                 var cfr = confirm("Are you sure you want delete "+staffName+"?");
                 if(!cfr){
                     event.preventDefault();
                 }
            });
            
            $("#btnUpdate").click(function(event){
                event.preventDefault();
                var s = [];
                s['staff_id']= current_staff_id;
                s['staff_name'] = $("#updateStaffName").val();
                s['Password'] = $("#updateStaffPassword").val();
                s['staff_position'] = $("#updateStaffPosition").val();
                if($("#updateStaffFlag").prop("checked")){
                    s['flgAsAdmin'] = 1;
                }
                else{
                    s['flgAsAdmin'] = 0;
                }
                if(s['staff_name'] == "" || s['Password'] == "" || s['staff_position'] == ""){
                    $("#errorUpdate").html("Fields must not be null");
                }
                else{
                    var staffName = s['staff_name'].toLowerCase();
                    var staffPassword = s['Password'].toLowerCase();
                    if(staffName.indexOf(staffPassword) > -1){
                        $("#errorUpdate").html("Pick stronger password");
                    }
                    else if(staffPassword.indexOf(staffName) > -1){
                        $("#errorUpdate").html("Pick stronger password");
                    }
                    else{
                        $("#errorUpdate").html("");
                        updateStaff(s);
                    }
                }               
            });
            
            $("#topBtn").click(function(){
                document.body.scrollTop = 0;
                document.documentElement.scrollTop = 0;
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
		
		function loadStaffs(){
			Swal.fire({
                title: 'Loading All Staff...',
                text: 'Please Wait Patiently',
                imageUrl: './imgicon/loading2.gif',
                imageWidth: 400,
                imageHeight: 200,
                imageAlt: 'Custom image',
                showConfirmButton: false,
                allowOutsideClick: false
            });
            $.ajax({
               url: "./json/jsonStaff.php",
               success: function(data){
                   $("#tableStaffs tbody").html("");
                   arrStaff = JSON.parse(data);
                   var ctr = 0;
                   arrStaff.forEach(s => {
                        ctr++;
                        if(addStaff(s, ctr) == 0){
                            ctr--;
                        }
                    });
                    lastCtr = ctr + 1;
                   Swal.close();
               },
               type: 'GET'
            });
		}
        
        function deleteStaff(id, name){
			$.ajax({
				url: "./ajax/checkStaff.php",
				data: {id: id},
				success: function(data){
					if(parseInt(data) > 0){
						//staff masih BERHUBUNGAN dengan suatu PROJECT, tidak bisa di-delete
						Swal.fire(
						  'Cannot Delete '+name,
						  name+' is still connected to projects',
						  'error'
						);
					}
					else{
						var cfr = confirm("Are you sure you want to delete "+name+"?");
						if(cfr){
							$.ajax({
								url: "./ajax/deleteStaff.php",
								data: {id: id},
								success: function(){
									var number = parseInt($("#"+id+" .rowCounter").html());
									$("#"+id).remove();
									var rowStaffs = document.querySelectorAll("#tableStaffs .rowCounter");
									lastCtr = -999;
									rowStaffs.forEach(r =>{
										var current_number = parseInt(r.textContent);
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
									arrStaff.forEach(s => {
										if(s['staff_id'] == id){
											idx = ctr;
										}
										ctr++;
									});
									arrStaff.splice(idx, 1);
								},
								method: 'post'
							});
						}
					}
				},
				type: 'GET'
			});
            
        }
        
        function updateStaff(s){
            $("#btnUpdate").attr("data-dismiss", "modal");
            $.ajax({
                url: "./ajax/updateStaff.php",
                data: {
                    id: s['staff_id'],
                    name: s['staff_name'],
                    password: s['Password'],
                    position: s['staff_position'],
                    flag: s['flgAsAdmin'],
                    staff_name: "<?=$current_staff['staff_name']?>"
                },
                success: function(data){
                    s['operator'] = "<?=$current_staff['staff_name']?>";
                    s['waktuproses'] = data;
                    //setelah add, dapatkan target end date nya
                    arrStaff.forEach(staff =>{
                        if(staff['staff_id'] == s['staff_id']){
                            staff['staff_name'] = s['staff_name'];
                            staff['Password'] = s['Password'];
                            staff['staff_position'] = s['staff_position'];
                            staff['flgAsAdmin'] = s['flgAsAdmin'];
                            staff['operator'] = "<?=$current_staff['staff_name']?>";
                            staff['waktuproses'] = data;
                        } 
                    });

                    //hapus semua td kecuali untuk number
                    $("#"+s['staff_id']+" td").not(":first").remove();
                    $("#"+s['staff_id']).append("<td id='actions"+s['staff_id']+"'></td>");
                    $("#actions"+s['staff_id']).append(`<button data-toggle='modal' data-target='#myModal' style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="displayUpdate('`+s['staff_id']+`')"><i class='fa fa-pencil-square' style='color: blue;' aria-hidden='true'></i></button>`);
                    $("#actions"+s['staff_id']).append(`<button style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="deleteStaff('`+s['staff_id']+`', '`+s['staff_name']+`')"><i class='fa fa-trash' style='color: red;' aria-hidden='true'></i></button>`);
                    $("#"+s['staff_id']).append(`<td style="text-align: left;">`+s['staff_name']+`</td>`);
                    $("#"+s['staff_id']).append(`<td style="text-align: left;">`+s['Password']+`</td>`);
                    $("#"+s['staff_id']).append(`<td style="text-align: left;">`+s['staff_position']+`</td>`);
                    $("#"+s['staff_id']).append(`<td style="text-align: right;">`+s['flgAsAdmin']+`</td>`);
                    $("#"+s['staff_id']).append(`<td style="text-align: left;">`+s['operator']+`</td>`);
                    $("#"+s['staff_id']).append(`<td style="text-align: center;">`+s['waktuproses']+`</td>`);
                },
                method: 'post'
            });
        }
        
        function displayUpdate(id){
            $("#formUpdate").css("display", "block");
            $("#formAdd").css("display", "none");
            var staffId = id;
            var staff = [];
            arrStaff.forEach(s => {
                if(s['staff_id'] == staffId){
                    staff = s;
                }
            });
            if(staff['flgAsAdmin'] == 0){
                $("#updateStaffFlag").prop("checked", false);
            }
            else if(staff['flgAsAdmin'] == 1){
                $("#updateStaffFlag").prop("checked", true);
            }
            $("#updateStaffId").val(staff['staff_id']);
            $("#updateStaffName").val(staff['staff_name']);
            $("#updateStaffPassword").val(staff['Password']);
            $("#updateStaffPosition").val(staff['staff_position']);
            current_staff_id = id;
        }

        function displayAdd(){
            $("#formUpdate").css("display", "none");
            $("#formAdd").css("display", "block");
        }
        
        function addStaff(s, ctr){
            if(s['staff_id'] != "<?=$current_staff['staff_id']?>" && s['project_id'] == ""){
                $("#tableStaffs tbody").append("<tr id='"+s['staff_id']+"'></tr>");
                $("#"+s['staff_id']).append("<td style='text-align: right;' class='rowCounter'>"+ctr+"</td>");
                $("#"+s['staff_id']).append("<td id='actions"+s['staff_id']+"'></td>");
                $("#actions"+s['staff_id']).append(`<button data-toggle='modal' data-target='#myModal' style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="displayUpdate('`+s['staff_id']+`')"><i class='fa fa-pencil-square' style='color: blue;' aria-hidden='true'></i></button>`);
                $("#actions"+s['staff_id']).append(`<button style='border: none; background-color: transparent; float:right; visibility: <?=$visibility?>' onclick="deleteStaff('`+s['staff_id']+`', '`+s['staff_name']+`')"><i class='fa fa-trash' style='color: red;' aria-hidden='true'></i></button>`);
                $("#"+s['staff_id']).append(`<td style="text-align: left;">`+s['staff_name']+`</td>`);
                $("#"+s['staff_id']).append(`<td style="text-align: left;">`+s['Password']+`</td>`);
                $("#"+s['staff_id']).append(`<td style="text-align: left;">`+s['staff_position']+`</td>`);
                $("#"+s['staff_id']).append(`<td style="text-align: right;">`+s['flgAsAdmin']+`</td>`);
                $("#"+s['staff_id']).append(`<td style="text-align: left;">`+s['operator']+`</td>`);
                $("#"+s['staff_id']).append(`<td style="text-align: center;">`+s['waktuproses']+`</td>`);
                return 1;
            }
            else{
                //DATA tidak dapat dimasukkan ke arrStaff, no urut jangan ditambah
                return 0;
            }
        }
    </script>
</body>
</html>