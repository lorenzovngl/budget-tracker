<?php

function strtotimeIT($datestring){
    // GMT +1:00
    return strtotime($datestring) + 3600;
}