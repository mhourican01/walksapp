<?php

	session_start();

	include('../../conn.php');
	
	$email_address = $conn->real_escape_string($_POST["email_address"]);
	$password = $conn->real_escape_string($_POST["password"]);
	
	$readuser = "SELECT * FROM 1_users 
	WHERE email_address='$email_address' 
	AND password=MD5('$password')";
	
	$result = $conn->query($readuser);
	
	$num = $result->num_rows;
	
	if($num > 0) {
		
		while($row = $result->fetch_assoc()) {
			
			$myid = $row['id'];
			
			$_SESSION[''] = $myid;
			
			$myuser = $row['email_address'];
			$_SESSION[''] = $myuser;
		}
		
		header('location: ../admin/index.php');
	} else {
		header('location: ../login.php');
	}
?>
