package com.example.walksapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.walksapp.R;
import com.example.walksapp.Route;
import com.example.walksapp.Section;
import com.example.walksapp.adapters.SectionAdapter;
import com.example.walksapp.directionhelpers.FetchURL;
import com.example.walksapp.directionhelpers.TaskLoadedCallback;
import com.example.walksapp.DistanceCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Managers display of walking route;
 */
public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, SectionAdapter.OnNoteListener {

    // Constant variables
    private static final String GET_SECTIONS = "";
    private static final String TAG = "RouteActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    // Instance variables
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private RequestQueue mQueue;
    private RecyclerView sectionRecyclerView;
    private SectionAdapter sectionAdapter;
    private ArrayList<Section> sectionList;
    private Route route;
    private LatLng sectionLatLng;
    private Toolbar mToolbar;
    private Polyline currentPolyline;
    private FloatingActionButton galleryBtn;
    private LatLng startLatLng, endLatLng;

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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Intent intent = getIntent();
        route = intent.getParcelableExtra("selected_route");

        sectionList = new ArrayList<>();
        mQueue = Volley.newRequestQueue(this);

        getSections();
        getLocationPermission();



        sectionRecyclerView = findViewById(R.id.sectionRecyclerView);
        sectionRecyclerView.hasFixedSize();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        double distanceAsDouble = Double.parseDouble(route.getDistance());

        if (distanceAsDouble >= 1000) {
            getSupportActionBar().setTitle(route.getName() + ((String.format(" (%.2fkm)", (distanceAsDouble / 1000)))));
        } else {

            getSupportActionBar().setTitle(route.getName() + ((String.format(" (%.2fm)", distanceAsDouble))));
        }

    if (getIntent().hasExtra("selected_route")) {

        }

        galleryBtn = findViewById(R.id.viewImgBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                intent.putExtra("route_name", route.getName());
                intent.putExtra("section_list", sectionList);
                startActivity(intent);
            }
        });
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

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
                            Log.d(TAG, "onComplete: Current location null");
                            Toast.makeText(RouteActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /**
     * Move map to current location
     */
    private void moveCamera(LatLng latLng, float zoom) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initialiseMap() {
        Log.d(TAG, "initialiseMap: initialising map");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
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
        Log.d(TAG, "onRequestPermissionsResult: dalled");
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

    private void createRecyclerView(List<Section> sectionList) {

        sectionAdapter = new SectionAdapter(route, sectionList, this);
        sectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sectionRecyclerView.setAdapter(sectionAdapter);
    }


    @Override
    public void onNoteClick(int position) {

        Intent intent = new Intent(this, SectionActivity.class);
        intent.putExtra("route", route);
        intent.putParcelableArrayListExtra("section_list", sectionList);
        intent.putExtra("index", position);
        startActivity(intent);
    }

    private void getSections() {

        StringRequest request = new StringRequest(Request.Method.GET, GET_SECTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray sections = new JSONArray(response);

                    for (int i = 0; i < sections.length(); i++) {

                        JSONObject sectionObject = sections.getJSONObject(i);

                        String id = sectionObject.getString("id");
                        String name = sectionObject.getString("name");
                        String blurb = sectionObject.getString("blurb");
                        String filename = sectionObject.getString("filename");
                        String sectionLat = sectionObject.getString("section_lat");
                        String sectionLng = sectionObject.getString("section_lng");
                        String routeId = sectionObject.getString("route_id");

                        Section section = new Section(id, name, blurb, filename, sectionLat, sectionLng, routeId);

                        if (section.getRouteId().equals(route.getId())) {
                            sectionList.add(section);
                        }
                    }

                    createRecyclerView(sectionList);

                    displaySectionLocations(sectionList);

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
    }

    /**
     * Displays markers on map
     * @param sectionList
     */
    private void displaySectionLocations(ArrayList<Section> sectionList) {

        ArrayList<LatLng> markers = new ArrayList<>();

        double startLat = (Double.parseDouble(route.getStartLat()));
        double startLng = (Double.parseDouble(route.getStartLng()));
        startLatLng = new LatLng(startLat, startLng);

        markers.add(startLatLng);

        mMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .title("Start")
        );

        moveCamera(startLatLng, DEFAULT_ZOOM);

        for (Section section : sectionList) {

            double sectionLat = Double.parseDouble(section.getSectionLat());
            double sectionLng = Double.parseDouble(section.getSectionLng());
            sectionLatLng = new LatLng(sectionLat, sectionLng);

            markers.add(sectionLatLng);

            mMap.addMarker(new MarkerOptions()
                    .position(sectionLatLng)
                    .title("Leg " + (sectionList.indexOf(section) + 1) + ": " + section.getName())
                    .snippet(section.getBlurb())
            );
        }

        double endLat = (Double.parseDouble(route.getEndLat()));
        double endLng = (Double.parseDouble(route.getEndLng()));
        endLatLng = new LatLng(endLat, endLng);

        markers.add(endLatLng);

        mMap.addMarker(new MarkerOptions()
                .position(endLatLng)
                .title("End")
        );

        // Iterates through map markers
        for (LatLng marker : markers) {

            // Excludes last index
            if (markers.indexOf(marker) < markers.size() - 1) {

                // Initialises next marker in list
                LatLng nextMarker = markers.get(markers.indexOf(marker) + 1);

                // Draws directional line between current and next marker
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(marker, nextMarker)
                        .width(5)
                        .color(Color.RED));
            }
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