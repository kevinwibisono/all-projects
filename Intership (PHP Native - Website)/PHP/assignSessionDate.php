<?php
    session_start();

    $_SESSION['chosen_date'] = date_format(date_create($_POST['chosen_date']), 'Y-m-d');

    if(isset($_POST['project_id'])){
        $_SESSION['project_id'] = $_POST['project_id'];
    }
?>