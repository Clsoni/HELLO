<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
error_reporting(0);

$file = 'config.json';
if (file_exists($file)) {
    echo file_get_contents($file);
} else {
    echo json_encode(["products" => [], "marquee" => "", "popup" => "", "banks" => []]);
}
?>
