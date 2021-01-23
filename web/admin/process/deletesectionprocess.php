<?php
	session_start();
	
	if(!isset($_SESSION[''])) {
		header('Location: ../../index.php');
	}
	
	$myuser = $_SESSION[''];
	
	include("../../../conn.php");
	
	if(isset($_GET["sectionid"])) {
		$sectionid = $_GET["sectionid"];

		$getrouteidquery = "SELECT route_id FROM 1_sections
		WHERE id = '$sectionid'";
		
		$getrouteidresult = $conn->query($getrouteidquery);
		
		if(!$getrouteidresult) {
			$conn->error;
		}
		
		while($getrouteidrow = $getrouteidresult->fetch_assoc()) {
			$getrouteid = $getrouteidrow['route_id'];
		}
		
		$delete = "DELETE FROM 1_sections 
		WHERE id = '$sectionid'";
		
		$result = $conn->query($delete);
		
		if(!$result){
			echo $conn->error;
		}
	}	
	
	$getroute = "SELECT * FROM 1_routes
	WHERE id = '$getrouteid'";
	
	$routeresult = $conn->query($getroute);
	
	if(!$routeresult) {
		$conn->error;
	}
	
	while($routerow = $routeresult->fetch_assoc()) {
		$routeid = $routerow['id'];
	}
	
	header('Location: ../route.php?routeid=' .$routeid);
?>