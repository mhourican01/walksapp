<?php

	if($_SERVER["REQUEST_METHOD"]=="GET") {
		
		require 'conn.php';
		getRoutes();
	}
	
	/**
	* Gets routes from database
	*/
	function getRoutes() {
		
		// Connects to server
		global $conn;
		
		// Selects all routes in 'routes' table
		$query = $conn->prepare("SELECT id, name, start_lat, start_lng, 
		end_lat, end_lng, distance, user_id FROM 1_routes");
		
		// Executes query
		$query->execute();
		
		// Binds data resulting from query
		$query->bind_result($id, $name, $start_lat, $start_lng, 
		$end_lat, $end_lng, $distance, $user_id);
		
		// Initialises array of routes
		$route = array();
		
		// Iterates through data resulting from query
		while ($query->fetch()) {
			
			// Initialises temporary array for iteration
			$temp = array();
			
			// Adds data to array as value in key-pair value, 
			// where table column name is key
			$temp['id'] = $id;
			$temp['name'] = $name;
			$temp['start_lat'] = $start_lat;
			$temp['start_lng'] = $start_lng;
			$temp['end_lat'] = $end_lat;
			$temp['end_lng'] = $end_lng;
			$temp['distance'] = $distance;
			$temp['user_id'] = $user_id;
		
			// Pushes value of temp array to route array
			array_push($route, $temp);
		}
		
		// Encodes route array as JSON format
		echo json_encode($route);
	}
?>