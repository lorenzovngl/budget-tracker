<?php
header('Content-Type: application/json; charset=utf-8');
include '../utils/response_codes.php';
include '../utils/authenticate.php';
include "../dao/DaoUsers.php";
if (isset($_POST["username"]) && isset($_POST["fullname"]) && isset($_POST["md5_password"])){
    $username = $_POST["username"];
    $fullname = $_POST["fullname"];
    $md5_password = $_POST["md5_password"];
} else {
    $response = array("response_id" => RESPONSE_WRONG_PARAMETERS);
    echo json_encode($response);
    die();
}
$conn = mySqlConnect();
if (DaoUsers::getOne($conn, $username)){
    $response = array("response_id" => RESPONSE_USERNAME_ALREADY_EXISTS);
    echo json_encode($response);
    die();
}
$sql = "INSERT INTO users (id, username, md5_password, name, last_sync) 
          VALUES (NULL, '".$username."', '".$md5_password."', '".$fullname."', NULL);";
mkdir($username);
$res = $conn->query($sql);
if($res) {
    $response = array("response_id" => RESPONSE_REGISTRATION_OK);
} else {
    $response = array("response_id" => RESPONSE_QUERY_ERROR);
}
echo json_encode($response);
