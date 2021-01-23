<?php
	session_start();
	
	if(!isset($_SESSION[''])) {
		header('Location: ../../index.php');
	}
	
	$myuser = $_SESSION[''];
	
	include("../../../conn.php");
	
	if(isset($_GET["routeid"])) {
		$routeid = $_GET["routeid"];

		$delete = "DELETE FROM 1_routes 
		WHERE id = '$routeid'";
		
		$result = $conn->query($delete);
		
		if(!$result){
			echo $conn->error;
		}
	}	
	
	header('Location: ../index.php');
?>