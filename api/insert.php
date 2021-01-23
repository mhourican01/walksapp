<?php

	if($_SERVER["REQUEST_METHOD"]=="POST") {
		
		require 'conn.php';
		insertRoute();
	}
	
	function insertRoute() {
		
		global $conn;
		
		$name = $_POST["name"];
		$blurb = $_POST["blurb"];
		$filename = $_POST["filename"];
		$section_lat = $_POST["section_lat"];
		$section_lng = $_POST["section_lng"];
		$route_id = $_POST["route_id"];
		
		$query = "INSERT INTO 1_sections(name, blurb, filename, section_lat, section_lng, route_id) 
		VALUES ('$name', '$blurb', '$filename', '$section_lat', '$section_lng', '$route_id')";
		
		mysqli_query($conn, $query) or die (mysqli_error($conn));
		mysqli_close($conn);
	}
?>