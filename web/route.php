<?php

	include("../conn.php");
	
	if(isset($_GET['routeid'])){
		
		$routeid = $_GET['routeid'];
		
		$readroute = "SELECT * FROM 1_routes 
		WHERE id = '$routeid'";
		
		$routeresult = $conn->query($readroute);
	
		if (!$routeresult) {
			$conn->error;
		}
		
		$readsections = "SELECT * FROM 1_sections 
		WHERE route_id = '$routeid'";
		
		$sectionsresult = $conn->query($readsections);
	
		if (!$sectionsresult) {
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
		
		<style>
		  /* Always set the map height explicitly to define the size of the div
		   * element that contains the map. */
		  #map {
			width:50%;
			height:500px;
			text-align:center;
		  }
		</style>
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
					<?php
					
						$lats = array();
						$lngs = array();
						$names = array();
						
						while($routerow = $routeresult->fetch_assoc()) {
							
							$routeid = $routerow['id'];
							$routename = $routerow['name'];
							$startlat = $routerow['start_lat'];
							$startlng = $routerow['start_lng'];
							$endlat = $routerow['end_lat'];
							$endlng = $routerow['end_lng'];
							$distance = $routerow['distance'];
							$uploader = $routerow['user_id'];
							
							echo "
								<h1 id='lititle'>$routename (" .$distance, "m)<h1>
								";	
						}
						
						$lats[0] = $startlat;
						$lngs[0] = $startlng;
						$names[0] = "Start";
					?>
				</div>	
		</div>
		
		<div class="container">
			<div id="map"></div>
		</div>
	
		<div class="container">
				<ul class="list-group">
					<?php
					
						$count = 1;
						
						while($sectionrow = $sectionsresult->fetch_assoc()) {
							
							$sectionid = $sectionrow['id'];
							$sectionname = $sectionrow['name'];
							$sectionimg = $sectionrow['filename'];
							$sectionblurb = $sectionrow['blurb'];
							$sectionlat = $sectionrow['section_lat'];
							$sectionlng = $sectionrow['section_lng'];
							
							
							$lats[$count] = $sectionlat;
							$lngs[$count] = $sectionlng;
							$names[$count] = $sectionname;
							
							$count++;
							
							echo "
							<li class='list-group-item'>
								<div class='row'>
								<h3 id='lititle'>$sectionname</h3>
								</div>
								<div class='row'>
									<img id='sectionimg' src='../images/$sectionimg'/>
								
									<div class='col-sm' id='blurb'>
										$sectionblurb
									</div>
								</div>
							</li>
							";
						}
						
						$lats[count($lats)] = $endlat;
						$lngs[count($lngs)] = $endlng;
						$names[count($names)] = "End";
					?>
				</ul>
		</div>

		<script>
			<?php
			
				// Converting array of latitudes from PHP to JavaScript
				$js_lats = json_encode($lats);
				echo "var js_lats = ". $js_lats . ";\n";
				
				// Converting array of longitudes from PHP to JavaScript
				$js_lngs = json_encode($lngs);
				echo "var js_lngs = ". $js_lngs . ";\n";
				
				
				$js_names = json_encode($names);
				echo "var js_names = ". $js_names . ";\n";
			?>	
		
		  var map;
		  
		  var start;
		  var end;
		  
		  var directionsService;
		  var directionsDisplay;
		  
		  var markers = [];
		  var waypoints = [];
		  
		  function initMap() {
			map = new google.maps.Map(document.getElementById('map'), {
			  center: {lat: <?php echo $startlat; ?>, lng: <?php echo $startlng; ?>},
			  zoom: 15
			});
			
			directionsService = new google.maps.DirectionsService();
			directionsDisplay = new google.maps.DirectionsRenderer();
			directionsDisplay.setMap(map);
			
			// Iterating through JavaScript array of latitudes
			for (let loop = 0; loop < js_lats.length; loop++) {
				
				// Creating LatLng objects from values in arrays
				var myLatLng = {
					lat: parseFloat(js_lats[loop]), 
					lng: parseFloat(js_lngs[loop])
				};
				
				// Adding LatLng objects to array
				markers.push(myLatLng);
				
				// Adding markers to map
				var marker = new google.maps.Marker({
					position: markers[loop],
					map: map,
					title: js_names[loop]
				});
			}	
			
			// Drawing line between markers
			var flightPath = new google.maps.Polyline({
				path: markers,
				geodesic: true,
				strokeColor: '#FF0000',
				strokeOpacity: 1.0,
				strokeWeight: 2
			});
			
			// Adding line to app
			flightPath.setMap(map);	
		  }
		</script>
	<script src=""
		async defer></script>	
	</body>
</html>