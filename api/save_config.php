<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');
error_reporting(0);

$data = file_get_contents('php://input');
$json = json_decode($data, true);

if ($json !== null) {
    file_put_contents('config.json', json_encode($json, JSON_PRETTY_PRINT));
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error", "message" => "Invalid JSON"]);
}
?>
