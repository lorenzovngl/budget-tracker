<?php

class daoUsers {

    static function getOne($conn, $username){
        $sql = "SELECT id FROM users WHERE username = '".$username."'";
        $res = $conn->query($sql);
        if ($res->num_rows > 0) {
            while ($row = $res->fetch_assoc()) {
                return $row["id"];
            }
        }
        return -1;
    }

}