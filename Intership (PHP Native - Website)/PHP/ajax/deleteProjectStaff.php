<?php
    include("../connection.php");
    $staff_id = $_GET['staff_id'];
    mysqli_query($conn, "DELETE FROM master_staff WHERE staff_id = '$staff_id'");
?>