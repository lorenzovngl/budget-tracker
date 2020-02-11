<?php
require_once '../utils/response_codes.php';
require_once '../utils/authenticate.php';
require_once '../utils/connect.php';
require_once '../utils/time_functions.php';
error_reporting(E_ALL);
header('Content-Type: application/json; charset=utf-8');
// Check parameters
if (isset($_POST["username"]) && isset($_POST["md5_user_password"]) &&
    isset($_POST["file_id"]) && isset($_POST["file_time"]) && isset($_POST["md5_file_password"])){
    $username = $_POST["username"];
    $md5_user_password = $_POST["md5_user_password"];
    $id = $_POST["file_id"];
    $filetime = $_POST["file_time"];
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
// Retrieve remote file and compare last modified time
$sql = "SELECT t_databases.md5_password, t_databases.last_sync
        FROM t_databases INNER JOIN permissions ON t_databases.id = id_database 
        INNER JOIN users ON users.id = id_user WHERE t_databases.id = " . $id . " AND username = '" . $username . "'";
$res = $conn->query($sql);
if ($res->num_rows > 0) {
    while ($row = $res->fetch_assoc()) {
        if ($md5_file_password == $row["md5_password"]) {
            if (strtotimeIT($row["last_sync"]) == $filetime){
                $resp_id = RESPONSE_DB_SYNCHRONIZED;
            } else if (strtotimeIT($row["last_sync"]) < $filetime){
                $resp_id = RESPONSE_REMOTE_DB_OUT_OF_DATE;
            } else {
                $resp_id = RESPONSE_LOCAL_DB_OUT_OF_DATE;
            }
            $response = array("response_id" => $resp_id);
            echo json_encode($response);
            die();
        }
    }
}
// File not found
$response = array("response_id" => RESPONSE_REMOTE_DB_NOT_FOUND);
echo json_encode($response);
die();