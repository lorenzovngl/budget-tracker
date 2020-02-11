<?php

class daoDatabases {

    static function getOne($conn, $id, $md5_password){
        $sql = "SELECT id FROM t_databases WHERE id = '".$id."' AND md5_password = '".$md5_password."'";
        $res = $conn->query($sql);
        if ($res->num_rows > 0) {
            while ($row = $res->fetch_assoc()) {
                return $row["id"];
            }
        }
        return -1;
    }

}