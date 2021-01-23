<?php
	
	session_start();
	
	if(!isset($_SESSION[''])) {
		header('Location: ../../index.php');
	}

	$myuser = $_SESSION[''];
	
	include("../../../conn.php");

	$newsectionid = $conn->real_escape_string($_POST['newsectionid']);
	$newsectionname = $conn->real_escape_string($_POST['newsectionname']);
	$newsectionblurb = $conn->real_escape_string($_POST['newsectionblurb']);
	
	$rand = rand(0, 1000000);
	
	$filename = $_FILES['newimg']['name'];
	$filetmp = $_FILES['newimg']['tmp_name'];
	$filename = $rand.$filename;
	
	move_uploaded_file($filetmp, "../../../images/".$filename);

	$update = "UPDATE 1_sections
	SET name = '$newsectionname',
	blurb = '$newsectionblurb',
	filename = '$filename' 
	WHERE id = '$newsectionid'
	;";

	$result = $conn->query($update);
	
	if(!$result) {
		$conn->error;
	}
	
	$getrouteidquery = "SELECT route_id FROM 1_sections
	WHERE id = '$newsectionid'";
	
	$getrouteidresult = $conn->query($getrouteidquery);
	
	if(!$getrouteidresult) {
		$conn->error;
	}
	
	while($getrouteidrow = $getrouteidresult->fetch_assoc()) {
		$getrouteid = $getrouteidrow['route_id'];
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