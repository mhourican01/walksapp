<?php

	if($_SERVER["REQUEST_METHOD"]=="GET") {
		
		require 'conn.php';
		getGroups();
	}
	
	function getGroups() {
		
		global $conn;
		
		$email_address = $_GET["email_address"];
		
		$query = $conn->prepare("SELECT id, name FROM 1_groups
		INNER JOIN 1_membership
		ON 1_groups.id = 1_membership.group_id
		WHERE 1_membership.email_address = '$email_address'");
		
		$query->execute();
		
		$query->bind_result($id, $name);
		
		$group = array();
		
		while ($query->fetch()) {
			
			$temp = array();
			
			$temp['id'] = $id;
			$temp['name'] = $name;
		
			array_push($group, $temp);
		}
		
		echo json_encode($group);
	}
?>