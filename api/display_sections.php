<?php

	if($_SERVER["REQUEST_METHOD"]=="GET") {
		
		require 'conn.php';
		displaySections();
	}
	
	function displaySections() {
		
		global $conn;
		
		$query = $conn->prepare("SELECT id, name, blurb, filename, section_lat, section_lng, route_id FROM 1_sections");
		
		$query->execute();
		
		$query->bind_result($id, $name, $blurb, $filename, $section_lat, $section_lng, $route_id);
		
		$section = array();
		
		while ($query->fetch()) {
			
			$temp = array();
			
			$temp['id'] = $id;
			$temp['name'] = $name;
			$temp['blurb'] = $blurb;
			$temp['filename'] = $filename;
			$temp['section_lat'] = $section_lat;
			$temp['section_lng'] = $section_lng;
			$temp['route_id'] = $route_id;
		
			array_push($section, $temp);
		}
		
		echo json_encode($section);
	}
?>