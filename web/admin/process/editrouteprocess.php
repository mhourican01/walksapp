<?php
	
	session_start();
	
	if(!isset($_SESSION[''])) {
		header('Location: ../../index.php');
	}

	$myuser = $_SESSION[''];
	
	include("../../../conn.php");

	$newrouteid = $conn->real_escape_string($_POST['newrouteid']);
	$newroutename = $conn->real_escape_string($_POST['newroutename']);

	$update = "UPDATE 1_routes
	SET name = '$newroutename'
	WHERE id = '$newrouteid'
	;";

	$result = $conn->query($update);
	
	if(!$result) {
		$conn->error;
	}
	
	header('Location: ../route.php?routeid=' .$newrouteid);
?>