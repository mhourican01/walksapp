<?php

	include("../conn.php");
	
	if(isset($_GET['search'])){
		$search = $_GET['search'];
	
		$readroutes = "SELECT * FROM 1_routes
		WHERE (name LIKE '%$search%') 
		ORDER BY name ASC";
		
		$routesresult = $conn->query($readroutes);
		
		if (!$routesresult) {
			$conn->error;
		}
	}
	
	if(isset($_GET['shortest'])){
	
		$readroutes = "SELECT * FROM 1_routes
		ORDER BY distance ASC";
		
		$routesresult = $conn->query($readroutes);
		
		if (!$routesresult) {
			$conn->error;
		}
	}
	
	if(isset($_GET['longest'])){
	
		$readroutes = "SELECT * FROM 1_routes
		ORDER BY distance DESC";
		
		$routesresult = $conn->query($readroutes);
		
		if (!$routesresult) {
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
		<link rel="stylesheet" href="styles/styles.css">
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
		<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
		<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
	</head>
	 
	<body>
		<nav class="navbar navbar-expand-lg navbar-dark" style="background-color: #009999;">
		  <a class="navbar-brand" href="index.php">WalksApp</a>
		  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		  </button>

		  <div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
			  <li class="nav-item">
				<a class="nav-link" href="register.php">Register</a>
			  </li>
			  <li class="nav-item">
				<a class="nav-link" href="login.php">Login</a>
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
			</ul>
		
			<form class="form-inline my-2 my-lg-0" action='search.php' method='GET'>
			  <input class="form-control mr-sm-2" name='search' type="search" placeholder="Enter a keyword" aria-label="Search">
			  <button class="btn btn-outline-success my-2 my-sm-0 btn-light" type="submit">Search</button>
			</form>
		  </div>
		</nav>
	
		<div class="container">
			<div class="row">
				<h1 id="lititle">Routes<h1>
			</div>
		</div>
			
		<div class="container">
				<ul class="list-group">
					<?php
					
						while($routerow = $routesresult->fetch_assoc()) {
							
							$routeid = $routerow['id'];
							$routename = $routerow['name'];
							$distance = $routerow['distance'];
							
							$readsections = "SELECT * FROM 1_sections 
							WHERE route_id = '$routeid'";
							
							$sectionsresult = $conn->query($readsections);
							
							$num = $sectionsresult->num_rows;
							
							echo "
							<li class='list-group-item'>
							<a p href='route.php?routeid=$routeid'>
								$routename
								</a p>
								<p>" .$distance, "m</p>
								<p>$num sections</p>
							</li>
							";
						}
					?>
				</ul>
		</div>
	</body>
</html>