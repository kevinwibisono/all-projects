<?php  
    include("../connection.php");
    $file_id = $_POST['fileId'];
    $filename = $_POST['filename'];
    $notUsed = true;
    //cek apakah file tersebut masih digunakan di project lainnya, jika tidak, maka hapus file
    $result = mysqli_query($conn, "SELECT * FROM project_files WHERE file_id <> '$file_id'");
    while($r = mysqli_fetch_array($result)){
        if($r['filename'] == $filename){
            $notUsed = false;
        }
    }
    if($notUsed){
        unlink("../uploads/".$filename);
    }
    mysqli_query($conn, "DElETE FROM project_files WHERE file_id = '$file_id'");
?>