<?php
    
    $link = mysqli_connect("localhost","root","","proyek");
    
    function new_user($nama,$password){
        global $link;
        $sql = "INSERT INTO user VALUES ('$nama','$password',0,3,0,0,0)";
        mysqli_query($link,$sql);
    }
?>