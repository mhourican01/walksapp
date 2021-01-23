<?php

	if($_SERVER["REQUEST_METHOD"]=="GET") {
		
		require 'conn.php';
		displayRoute();
	}
	
	function displayRoute() {
		
		global $conn;
		
		$query = $conn->prepare("SELECT name, blurb, filename, location FROM 1_sections");
		
		$query->execute();
		
		$query->bind_result($name, $blurb, $filename, $location);
		
		$route = array();
		
		while ($query->fetch()) {
			
			$temp = array();
			
			$temp['name'] = $name;
			$temp['blurb'] = $blurb;
			$temp['filename'] = $filename;
			$temp['location'] = $location;
			
			array_push($route, $temp);
		}
		
		echo json_encode($route);
	}
?>