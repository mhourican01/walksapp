package com.example.walksapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.walksapp.R;
import com.example.walksapp.Route;
import com.example.walksapp.Section;
import com.example.walksapp.directionhelpers.FetchURL;
import com.example.walksapp.directionhelpers.TaskLoadedCallback;
import com.example.walksapp.DistanceCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;

/**
 * Manages display of section of route
 */
public class SectionActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    // Constant variables
    private static final String IMAGE_PATH = "";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    // Instance variables
    private TextView sUserTextView, sBlurbTextView, distTextView;
    private ImageView sImageView;
    private FloatingActionButton viewImgBtn;
    private Toolbar mToolbar;
    private DistanceCalculator dC = new DistanceCalculator();
    private Route route;
    private ArrayList<Section> sectionList;
    private Section sectionParcel;
    private double sectionLat, sectionLng;
    private LatLng sectionLatLng;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Polyline currentPolyline;
    private String directionsUrl;
    private int index;
    private Boolean isClicked = false, isClose, hasArrived;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        isClose = false;
        hasArrived = false;

        Intent intent = getIntent();
        route = intent.getParcelableExtra("route");
        sectionList = intent.getParcelableArrayListExtra("section_list");
        index = intent.getIntExtra("index", 0);

        sectionParcel = sectionList.get(index);

        getLocationPermission();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(sectionParcel.getName());

        sUserTextView = findViewById(R.id.sUserTextView);
        sUserTextView.setText(route.getUserId() + " said:");

        sBlurbTextView = findViewById(R.id.sBlurbTextView);
        sBlurbTextView.setText("'" + sectionParcel.getBlurb() + "'");

        sImageView = findViewById(R.id.sImageView);

        Glide.with(getApplicationContext())
                .load(IMAGE_PATH + File.separator + sectionParcel.getFilename())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(45)))
                .into(sImageView);

        sImageView.setRotation(90);

        sImageView.setVisibility(View.GONE);

        viewImgBtn = findViewById(R.id.viewImgBtn);
        viewImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isClicked) {
                    sImageView.setVisibility(View.GONE);
                    isClicked = false;
                }
                else {
                    sImageView.setVisibility(View.VISIBLE);
                    isClicked = true;
                }
            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            Location sectionLocation = new Location("");
            sectionLocation.setLatitude(Double.parseDouble(sectionParcel.getSectionLat()));
            sectionLocation.setLongitude(Double.parseDouble(sectionParcel.getSectionLng()));

            Location currentLocation = new Location("");
            currentLocation.setLatitude(locationResult.getLastLocation().getLatitude());
            currentLocation.setLongitude(locationResult.getLastLocation().getLongitude());

            distTextView = findViewById(R.id.distTextView);

            double distToSection = currentLocation.distanceTo(sectionLocation);

            if (distToSection >= 1000) {
                double distInKm = dC.convertToKm(distToSection);
                distTextView.setText(String.format("You are %.2fkm from the next waypoint!", distInKm));
            } else {
                distTextView.setText(String.format("You are %.2fm from the next waypoint!", distToSection));
            }

            if ((distToSection <= 50)
                    && (!hasArrived)) {

                Toast.makeText(SectionActivity.this, "You have arrived!", Toast.LENGTH_SHORT).show();
                hasArrived = true;
            } else if ((distToSection <= 250)
            && (!isClose)
            && (!hasArrived)) {
                Toast.makeText(SectionActivity.this, "You are getting close!", Toast.LENGTH_SHORT).show();
                isClose = true;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            displaySectionLocations(sectionParcel, sectionList);
        }
    }

    private void enableDeviceLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hm, your location settings are disabled. Do you want to enable them?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getDeviceLocation() {

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            try {

                            } catch (Exception e) {

                                enableDeviceLocation();
                            }
                        } else {

                            Toast.makeText(SectionActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

            e.printStackTrace();
        }
    }

    private void initialiseMap() {

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            initialiseMap();
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        mLocationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initialiseMap();
            }
        }
    }

    /**
     * Displays markers on map
     * @param sectionList
     */
    private void displaySectionLocations(Section section, ArrayList<Section> sectionList) {

        sectionLat = (Double.parseDouble(section.getSectionLat()));
        sectionLng = (Double.parseDouble(section.getSectionLng()));
        sectionLatLng = new LatLng(sectionLat, sectionLng);

        if (section.equals(sectionList.get(0))) {

            double startLat = (Double.parseDouble(route.getStartLat()));
            double startLng = (Double.parseDouble(route.getStartLng()));
            LatLng startLatLng = new LatLng(startLat, startLng);

            mMap.addMarker(new MarkerOptions()
                    .position(startLatLng)
                    .title("Start")
            );

            mMap.addMarker(new MarkerOptions()
                    .position(sectionLatLng)
                    .title("Leg " + (sectionList.indexOf(section) + 1) + ": " + section.getName())
                    .snippet(section.getBlurb())
            );

            directionsUrl = getUrl(startLatLng, sectionLatLng, "walking");
            new FetchURL(SectionActivity.this).execute(directionsUrl, "walking");

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, DEFAULT_ZOOM));
        } else {

            Section previousSection = sectionList.get(sectionList.indexOf(section) - 1);

            double previousLat = Double.parseDouble(previousSection.getSectionLat());
            double previousLng = Double.parseDouble(previousSection.getSectionLng());
            LatLng previousLatLng = new LatLng(previousLat, previousLng);

            mMap.addMarker(new MarkerOptions()
                    .position(previousLatLng)
                    .title("Leg " + (sectionList.indexOf(previousSection) + 1) + ": " + previousSection.getName())
                    .snippet(previousSection.getBlurb())
            );

            mMap.addMarker(new MarkerOptions()
                    .position(sectionLatLng)
                    .title("Leg " + (sectionList.indexOf(section) + 1) + ": " + section.getName())
                    .snippet(section.getBlurb())
            );

            directionsUrl = getUrl(previousLatLng, sectionLatLng, "walking");
            new FetchURL(SectionActivity.this).execute(directionsUrl, "walking");

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(previousLatLng, DEFAULT_ZOOM));
        }
    }

    /**
     * Constructs URL to display route between two points
     * @param origin
     * @param dest
     * @param directionMode
     * @return
     */
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_API_key);

        return url;
    }

    /**
     * Governs Polyline when displaying route between two points
     * @param values
     */
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        } else {
            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        }
    }
}