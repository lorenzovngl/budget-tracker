<?php

function mySqlConnect(){
    $servername = "localhost";
    $username = "my1705";
    $password = "mooraega";
    $database = "my1705";
    // Create connection
    $conn = new mysqli($servername, $username, $password, $database);
    // Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    return $conn;
}