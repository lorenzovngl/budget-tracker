<?php
header('Content-Type: application/json; charset=utf-8');
include '../utils/connect.php';
var_dump($_POST);
var_dump($_FILES);
if (isset($_POST["username"]) && isset($_POST["md5_password"]) && isset($_FILES["filezip"])){
    $username = $_POST["username"];
    $md5_password = $_POST["md5_password"];
    $file = $_FILES["filezip"];
} else {
    echo "-1";
    die();
}
$dir = $username."/";
$file_path = $dir . basename($file["name"]);
if (!move_uploaded_file($file["tmp_name"], $file_path)) {
    echo "-1";
    die();
}
$conn = mySqlConnect();
$sql = "UPDATE users SET last_sync = NOW() WHERE username = '".$username."' AND md5_password = '".$md5_password."'";
$res = $conn->query($sql);
echo $res;
