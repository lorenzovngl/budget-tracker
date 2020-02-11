<?php
header('Content-Type: application/json; charset=utf-8');
include '../utils/response_codes.php';
include '../utils/authenticate.php';
if (isset($_POST["username"]) && isset($_POST["md5_password"])){
    $username = $_POST["username"];
    $md5_password = $_POST["md5_password"];
} else {
    $response = array("response_id" => RESPONSE_WRONG_PARAMETERS);
    echo json_encode($response);
    die();
}

if (authenticate($username, $md5_password)) {
    $conn = mySqlConnect();
    $sql = "SELECT * FROM users WHERE username = '".$username."'";
    $res = $conn->query($sql);
    if ($res->num_rows > 0) {
        while ($row = $res->fetch_assoc()) {
            if ($username == $row["username"]){
                $response = array("response_id" => RESPONSE_LOGIN_OK, "name" => $row["name"]);
            }
        }
    }
} else {
    $response = array("response_id" => RESPONSE_WRONG_CREDENTIALS);
}
echo json_encode($response);
