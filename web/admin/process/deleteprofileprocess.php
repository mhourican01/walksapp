<?php
	session_start();
	
	if(!isset($_SESSION[''])) {
		header('Location: ../../index.php');
	}
	
	$myuser = $_SESSION[''];
	
	include("../../../conn.php");
	
	if(isset($_GET["delete"])) {

		$delete = "DELETE FROM 1_users 
		WHERE email_address = '$myuser'";
		
		$result = $conn->query($delete);
		
		if(!$result){
			echo $conn->error;
		}
	}	
	
	header('Location: ../../index.php');
?>