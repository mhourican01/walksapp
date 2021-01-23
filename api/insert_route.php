<?php

	require 'conn.php';
	
	if($_SERVER["REQUEST_METHOD"]=="POST") {
		
		global $conn;
		
		$id = $_POST["id"];
		$name = $_POST["name"];
		$start_lat = $_POST["start_lat"];
		$start_lng = $_POST["start_lng"];
		$end_lat = $_POST["end_lat"];
		$end_lng = $_POST["end_lng"];
		$distance = $_POST["distance"];
		$user_id = $_POST["user_id"];
		
		$query = "INSERT INTO 1_routes(id, name, start_lat, start_lng, end_lat, end_lng, distance, user_id) 
		VALUES ('$id', '$name', '$start_lat', '$start_lng', '$end_lat', '$end_lng', '$distance', '$user_id')";
			
		mysqli_query($conn, $query) or die (mysqli_error($conn));
		mysqli_close($conn);
	}
?>