<?php

	require 'conn.php';
	
	$email_address = $conn->real_escape_string($_POST["email_address"]);
	$password = $conn->real_escape_string($_POST["password"]);
	
	// Searches for submitted email address
	$query = "SELECT * FROM 1_users
	WHERE email_address = '$email_address'";
	
	// Assigns result to variable
	$result = $conn->query($query);
	
	// Gets number of rows of result
	$num = $result->num_rows;
	
	// If there is more than one row,
	// then an account with that email address already exists
	if ($num > 0) {
		
		// Response to user
		$response['error'] = "An account with this email address 
		already exists.";
		
		// Encodes response as JSON
		echo json_encode($response);
		mysqli_close($conn);
	} else {
		
		
		$regquery = "INSERT into 1_users (email_address, password)
					VALUES ('$email_address', MD5('$password'))";
			
				$response['success'] = "Your account has been created, $email_address!";
			
				echo json_encode($response);
				mysqli_query($conn, $regquery) or die (mysqli_error($conn));
				mysqli_close($conn);
	}
		
	mysqli_query($conn, $query) or die (mysqli_error($conn));
?>