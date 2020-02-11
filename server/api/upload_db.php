<?php
require_once '../utils/response_codes.php';
require_once '../utils/authenticate.php';
require_once '../utils/connect.php';
require_once '../utils/time_functions.php';
require_once '../dao/DaoUsers.php';
error_reporting(E_ALL);
header('Content-Type: application/json; charset=utf-8');
const create = 1;
const update = 0;
// Check parameters
if (isset($_POST["username"]) && isset($_POST["md5_user_password"]) &&
    isset($_POST["filename"]) && isset($_POST["filetime"]) && isset($_POST["md5_file_password"]) && isset($_FILES["database"])){
    if (isset($_POST["id"])){
        $id = $_POST["id"];
        $action = update;
    } else {
        $action = create;
    }
    $username = $_POST["username"];
    $md5_user_password = $_POST["md5_user_password"];
    $filename = $_POST["filename"];
    // Fix bug due to local time
    $filetime = date("Y-m-d G:i:s", $_POST["filetime"] - 3600);
    $md5_file_password = $_POST["md5_file_password"];
    $file = $_FILES["database"];
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
if ($action == update){
    $sql = "SELECT md5_password FROM t_databases WHERE id = ".$id;
    $res = $conn->query($sql);
    if ($res->num_rows > 0) {
        $found = 0;
        while ($row = $res->fetch_assoc()) {
            if ($md5_file_password == $row["md5_password"]){
                $found = 1;
            }
        }
        if (!$found){
            $response = array("response_id" => RESPONSE_REMOTE_DB_NOT_FOUND);
            echo json_encode($response);
            die();
        }
    }
}
// Update or create database
if ($action == create){
    $sql = "INSERT INTO t_databases (id, name, md5_password, last_sync) VALUES (NULL, '".$filename."', '".$md5_file_password."', '".$filetime."')";
} else {
    $sql = "UPDATE t_databases SET name = '".$filename."', last_sync = '".$filetime."' WHERE id = ".$id." AND md5_password = '".$md5_file_password."'";
}
$res = $conn->query($sql);
if ($action == create){
    $id = $conn->insert_id;
}
// Update or create permissions
if ($action == create) {
    $sql = "INSERT INTO permissions (id_database, id_user) VALUES (" . $id . ", " . DaoUsers::getOne($conn, $username) . ")";
    $res = $conn->query($sql);
}
// Upload file
$dir = "../databases/";
$file_path = $dir . $id.".db";
if (!move_uploaded_file($file["tmp_name"], $file_path)) {
    $response = array("response_id" => RESPONSE_ERROR_UPLOADING_FILE);
    echo json_encode($response);
    die();
}
$response = array("response_id" => RESPONSE_UPLOAD_OK, "record_id" => $id, "time" => $filetime );
echo json_encode($response);