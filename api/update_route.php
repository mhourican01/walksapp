<?php

	require 'conn.php';
	
	if($_SERVER["REQUEST_METHOD"]=="POST") {
		
		global $conn;
		
		$id = $_POST["id"];
		$name = $_POST["name"];
		$end_lat = $_POST["end_lat"];
		$end_lng = $_POST["end_lng"];
		$distance = $_POST["distance"];
		
		$query = "UPDATE `1_routes`
		SET name = '$name', 
		end_lat = '$end_lat', 
		end_lng = '$end_lng',
		distance = '$distance'
		WHERE id = '$id'";
		
		mysqli_query($conn, $query) or die (mysqli_error($conn));
		mysqli_close($conn);
	}
?>