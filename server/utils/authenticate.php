<?php

include 'connect.php';

function authenticate($username, $md5_password){
    $conn = mySqlConnect();
    $sql = "SELECT * FROM users WHERE username = '".$username."'";
    $res = $conn->query($sql);
    if ($res->num_rows > 0) {
        while ($row = $res->fetch_assoc()) {
            if ($username == $row["username"] && $md5_password == $row["md5_password"]){
                return true;
            }
        }
    }
    return false;
}