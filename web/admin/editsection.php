<?php

	session_start();
	
	if(!isset($_SESSION[''])) {
		header('Location: ../index.php');
	}
	
	$myuser = $_SESSION[''];

	include("../../conn.php");
	
	if(isset($_GET['sectionid'])){
		
		$sectionid = $_GET['sectionid'];
		
		$readsection = "SELECT * FROM 1_sections 
		WHERE id = '$sectionid'";
		
		$result = $conn->query($readsection);
	
		if (!$result) {
			$conn->error;
		}
	}
?>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>WalksApp</title>
		<link rel="stylesheet" href="../styles/styles.css">
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
		<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
		<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
	</head>
	 
	<body>
		<nav class="navbar navbar-expand-lg navbar-light navbar-dark" style="background-color: #009999;">
		  <a class="navbar-brand" href="index.php">WalksApp</a>
		  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		  </button>

		  <div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
			  <li class="nav-item dropdown">
				<a class="nav-link dropdown-toggle" href="profile.php" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				  Profile
				</a>
				<div class="dropdown-menu" aria-labelledby="navbarDropdown">
				  <a class="dropdown-item" href="profile.php">My profile</a>
				  <a class="dropdown-item" href="search.php?myroutes">My routes</a>
				</div>
			  </li>
			  <li class="nav-item dropdown">
				<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				  Order by
				</a>
				<div class="dropdown-menu" aria-labelledby="navbarDropdown">
				  <a href="search.php?shortest" class="dropdown-item" href="#">Shortest-longest</a>
				  <a href="search.php?longest" class="dropdown-item" href="#">Longest-shortest</a>
				</div>
			  </li>
			  <li class="nav-item">
				<a class="nav-link" href="../index.php">Log out</a>
			  </li>
			</ul>
			<form class="form-inline my-2 my-lg-0" action='search.php' method='GET'>
			  <input class="form-control mr-sm-2" name='search' type="search" placeholder="Enter a keyword" aria-label="Search">
			  <button class="btn btn-outline-success my-2 my-sm-0 btn-light" type="submit">Search</button>
			</form>
		  </div>
		</nav>
		
		<?php
		
		while($sectionrow = $result->fetch_assoc()) {
							
			$sectionid = $sectionrow['id'];
			$sectionname = $sectionrow['name'];
			$sectionblurb = $sectionrow['blurb'];
			$currentimg = $sectionrow['filename'];
		}
		?>
		
		<div class="container">
			<div class="row">
				<h1>Edit <?php echo "$sectionname"; ?> </h1>
			</div>
		</div>
			
		<form action='process/editsectionprocess.php' method='POST' enctype='multipart/form-data'> 
			<div class="container">
					<?php
						echo "
								<input type='hidden' value='$sectionid' name='newsectionid'>

								<div class='row'>
									<p>Name: </p>
								</div>
								
								<div class='row'>
								<div class='field'>
									<div class='control'>
										<input value='$sectionname' name='newsectionname'";?> required <?php echo ">
									</div>
								</div>
								</div>

							<div class='row'>
								<p>Description: </p>
							</div>
							<div class='row'>
								<div class='field'>
									<div class='control'>
										<input value='$sectionblurb' name='newsectionblurb'";?> required <?php echo ">
									</div>
								</div>
							</div>
							
							<div class='row'>
							  <div class='form-group'>
								<label for='exampleFormControlFile1'>Image: </label>
								<input value='$currentimg' name='newimg' type='file' class='form-control-file' id='exampleFormControlFile1'";?> required <?php echo ">
							  </div>
							  </div>
							";
						
					?>
					<div class='row'>
						<div class='control'>
							<button type="submit" class="btn btn-primary" style="background-color: #009999;">Submit</button>
						</div>		
					</div>
			</div>
		</form>	
	</body>
</html>