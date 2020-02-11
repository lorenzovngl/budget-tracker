<?php
require_once '../utils/response_codes.php';
require_once '../utils/authenticate.php';
require_once '../utils/connect.php';
require_once '../dao/DaoDatabases.php';
error_reporting(E_ALL);
header('Content-Type: application/json; charset=utf-8');
const create = 1;
const update = 0;
// Check parameters
if (isset($_POST["username"]) && isset($_POST["md5_user_password"]) &&
    isset($_POST["file_id"]) && isset($_POST["md5_file_password"])){
    $username = $_POST["username"];
    $md5_user_password = $_POST["md5_user_password"];
    $file_id = $_POST["file_id"];
    $md5_file_password = $_POST["md5_file_password"];
} else {
    $response = array("response_id" => RESPONSE_WRONG_PARAMETERS);
    echo json_encode($response);
    die();
}
// Check authentication
if (!authenticate($username, $md5_user_password)) {
    $response = array("response_id" => RESPONSE_WRONG_CREDENTIALS);
}
$conn = mySqlConnect();
// Check password for existing file
if (DaoDatabases::getOne($conn, $file_id, $md5_file_password) < 0){
    $response = array("response_id" => RESPONSE_REMOTE_DB_NOT_FOUND);
    echo json_encode($response);
    die();
}
$url = "https://".$_SERVER['SERVER_NAME']."/denario/databases/".$file_id.".db";
$response = array("response_id" => RESPONSE_DOWNLOAD_OK, "url" => $url);
echo json_encode($response);