<?php
	
	session_start();
	
	if(!isset($_SESSION[''])) {
		header('Location: ../../index.php');
	}

	$myuser = $_SESSION[''];
	
	include("../../../conn.php");

	$newemail = $conn->real_escape_string($_POST['newemail']);
	$newpw = $conn->real_escape_string($_POST['newpw']);

	$update = "UPDATE 1_users
	SET email_address = '$newemail',
	password = MD5('$newpw')
	WHERE email_address = '$myuser'
	;";

	$result = $conn->query($update);
	
	if(!$result) {
		$conn->error;
	}
	
	header('Location: ../../index.php');
?>