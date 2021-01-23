<?php
	
	header('Content-type: bitmap; charset=utf-8');
	
	if(isset($_POST["encoded_string"])) {
		
		$encoded_string = $_POST["encoded_string"];
		$image_name = $_POST["filename"];
		
		$decoded_string = base64_decode($encoded_string);
		
		$path = 'images/'.$image_name;
		
		$file = fopen($path, 'wb');
		
		$is_written = fwrite($file, $decoded_string);
		fclose($file);
		
		if($is_written > 0) {
			
			$conn = mysqli_connect('', 
			'', 
			'', 
			'');
			
			$query = "INSERT INTO 1_images(name, path) 
			VALUES ('$image_name', '$path')";
			
			$result = mysqli_query($conn, $query);
			
			if ($result) {
				echo "Success";
			} else {
				echo "Failed";
			}
			
			mysqli_close($conn);
		}
	}
?>