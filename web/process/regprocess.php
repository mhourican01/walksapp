<?php

	include('../../conn.php');
	
	$email_address = $conn->real_escape_string($_POST["email_address"]);
	$password = $conn->real_escape_string($_POST["password"]);
	
	$query = "SELECT * FROM 1_users
	WHERE email_address = '$email_address'";
	
	$result = $conn->query($query);
	
	$num = $result->num_rows;
	
	if ($num > 0) {
		
		header('location: ../register.php');
	} else {
		
		$regquery = "INSERT INTO 1_users (email_address, password) 
		VALUES ('$email_address', MD5('$password'))";
		
		$regresult = $conn->query($regquery);
		
		header('location: ../login.php');
	}
?>
