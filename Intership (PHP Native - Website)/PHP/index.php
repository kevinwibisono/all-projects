<?php
    session_start();
    include("connection.php");
    $staffList = [];
    $error = "";
    $color = "red";

    $result = mysqli_query($conn, "select * from master_staff");
    while($row = mysqli_fetch_array($result)){
        $data["staff_id"] = $row["staff_id"];
        $data["staff_name"] = $row["staff_name"];
        $data["Password"] = $row["Password"];
        $data["staff_position"] = $row["staff_position"];
        $data["flgAsAdmin"] = $row["flgAsAdmin"];
        $data["operator"] = $row["operator"];
        $data["waktuproses"] = $row["waktuproses"];
        array_push($staffList, $data);
    }
    mysqli_free_result($result);

    if(isset($_POST["login"])){
        $stafflogin = null;
      foreach ($staffList as $s){
          if($s['staff_name'] == $_POST['name'] && $s['Password'] == $_POST['password']){
              $staffLogin = $s;
          }
      }
      if($staffLogin != null){
          $_SESSION['staff_login'] = $staffLogin;
          ob_start();
          while (ob_get_status()) 
            {
                ob_end_clean();
            }
          header("location:dashboard.php");
      }
      else{
          $color = "red";
          $error = "Failed to login! Wrong name or password";
      }
  }
?>
<html>
    <head>
        <link rel="icon" type="image/png" sizes="16x16" href="./imgicon/logo2.ico">
        <title>Project Scheduling</title>
        <style>
            @import url(https://fonts.googleapis.com/css?family=Roboto:300);

            .login-page {
              width: 360px;
              padding: 8% 0 0;
              margin: auto;
            }
            .form {
              position: relative;
              z-index: 1;
              background: #FFFFFF;
              max-width: 360px;
              margin: 0 auto 100px;
              padding: 45px;
              text-align: center;
              box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);
            }
            .form input {
              font-family: "Roboto", sans-serif;
              outline: 0;
              background: #f2f2f2;
              width: 100%;
              border: 0;
              margin: 0 0 15px;
              padding: 15px;
              box-sizing: border-box;
              font-size: 14px;
            }
            .form .submit {
              font-family: "Roboto", sans-serif;
              text-transform: uppercase;
              outline: 0;
              background: black;
              width: 100%;
              border: 0;
              padding: 15px;
              color: #FFFFFF;
              font-size: 14px;
              -webkit-transition: all 0.3 ease;
              transition: all 0.3 ease;
              cursor: pointer;
            }
            .form button:hover,.form button:active,.form button:focus {
              background: #43A047;
            }
            .form .message {
              margin: 15px 0 0;
              color: #b3b3b3;
              font-size: 12px;
            }
            .form .message a {
              color: #4CAF50;
              text-decoration: none;
            }
            .container {
              position: relative;
              z-index: 1;
              max-width: 300px;
              margin: 0 auto;
            }
            .container:before, .container:after {
              content: "";
              display: block;
              clear: both;
            }
            .container .info {
              margin: 50px auto;
              text-align: center;
            }
            .container .info h1 {
              margin: 0 0 15px;
              padding: 0;
              font-size: 36px;
              font-weight: 300;
              color: #1a1a1a;
            }
            .container .info span {
              color: #4d4d4d;
              font-size: 12px;
            }
            .container .info span a {
              color: #000000;
              text-decoration: none;
            }
            .container .info span .fa {
              color: #EF3B3A;
            }
            body {
              background: black; /* fallback for old browsers */
              font-family: "Roboto", sans-serif;
              -webkit-font-smoothing: antialiased;
              -moz-osx-font-smoothing: grayscale;      
            }
        </style>
        
    </head>
    <body bgcolor="black">
        <div class="login-page">
          <div class="form">                
                <form class="login-form" method="post" autocomplete="off">
                  <input type="text" placeholder="staff name" name="name" id="loginName"/>
                  <input type="password" placeholder="password" name="password" id="loginPass"/>
                  <br>
                  <input type="submit" class="submit" value="login" name="login"/>
                  <h3 class="errorMsg" style="color: red;"><?=$error?></h3>
                </form>
          </div>
        </div>
    </body>
    <script src="./plugins/bower_components/jquery/dist/jquery.min.js"></script>
    <script>
            $(document).ready(function(){
                $(".login-form").submit(function(event){
                    var username = $("#loginName").val();
                    var password = $("#loginPass").val();
                    if(username == "" || password == ""){
                        $(".errorMsg").css("display", "block");
                        $(".errorMsg").html("Field cannot be null");
                        event.preventDefault();
                    }
                });
            });
        </script>
</html>