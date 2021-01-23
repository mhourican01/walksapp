<?php

	require 'conn.php';
	
	$email_address = $conn->real_escape_string($_POST["email_address"]);
	$password = $conn->real_escape_string($_POST["password"]);
	
	$query = "SELECT * FROM 1_users 
	WHERE email_address='$email_address' 
	AND password = MD5('$password')";
	
	$result = $conn->query($query);
	
	$num = $result->num_rows;
	
	// Checks that the query returned at least one result,
	// that is that there is a match
	if($num > 0) {
		$response['success'] = 
		"Welcome, $email_address!";
	} else {
		$response['error'] = 
		"Those login credentials are invalid. 
		Please try again.";
	}
	
	//Encodes response as JSON
	echo json_encode($response);
	
	mysqli_query($conn, $query) or die (mysqli_error($conn));
	mysqli_close($conn);
?>