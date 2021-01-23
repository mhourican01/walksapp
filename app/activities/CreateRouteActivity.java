package com.example.walksapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.walksapp.User;
import com.example.walksapp.DistanceCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages creation of walking route
 */
public class CreateRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Constant variables
    private static final String INSERT_ROUTE_URL = "";
    private static final String DISPLAY_SECTIONS_URL = "";
    private static final String UPDATE_ROUTE_URL = "";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    // Instance variables
    private ImageButton sectionBtn, endRouteBtn;
    private EditText rNameEditText;
    private String routeId;
    private RequestQueue mQueue;
    private ArrayList<Section> sectionList;
    private Toolbar mToolbar;
    private User user;
    private Route testRoute;
    private Boolean isRouteCreated = false;
    private double routeDistance;
    private DistanceCalculator dC = new DistanceCalculator();
     private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double currentLat, currentLng, endLat, endLng;
    private LocationRequest mLocationRequest;
    private ArrayList<Section> currentSectionList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 5 && resultCode == RESULT_OK) {

            Section lastSection = data.getParcelableExtra("last_section");

            currentSectionList.add(lastSection);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        sectionList = new ArrayList<>();
        currentSectionList = new ArrayList<>();
        mQueue = Volley.newRequestQueue(this);

        // Methods getting location permission and sections
        getLocationPermission();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getSections();

        routeId = UUID.randomUUID().toString();

        sectionBtn = findViewById(R.id.sectionBtn);
        endRouteBtn = findViewById(R.id.uploadBtn);
        rNameEditText = findViewById(R.id.rNameEditText);
        mToolbar = findViewById(R.id.toolbar);

        // sectionRecyclerView.hasFixedSize();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add route");

        // Brings user to activity to create section
        sectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent createRouteIntent = new Intent(getApplicationContext(), UploadActivity.class);
                createRouteIntent.putExtra("routeId", routeId);
                startActivityForResult(createRouteIntent, 5);

            }
        });

        // Posts completed route
        endRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(CreateRouteActivity.this, "A route must have a name.", Toast.LENGTH_SHORT).show();
                } else if (currentSectionList.isEmpty()){
                    Toast.makeText(CreateRouteActivity.this, "You have not added any sections!", Toast.LENGTH_SHORT).show();
                } else {
                    mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
            }
        });
    }

    /**
     * Updates location
     */
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            endLat = locationResult.getLastLocation().getLatitude();
            endLng = locationResult.getLastLocation().getLongitude();

            testRoute.setEndLat(String.valueOf(endLat));
            testRoute.setEndLng(String.valueOf(endLng));

            routeDistance = dC.calculateDistance(testRoute, currentSectionList);
            testRoute.setDistance(String.valueOf(routeDistance));

            updateRoute();
        }
    };

    /**
     * Posts partial route to server
     */
    private void createRoute() {

        testRoute = new Route(routeId, null, String.valueOf(currentLat), String.valueOf(currentLng), null, null, null, user.getEmailAddress());

        StringRequest request = new StringRequest(Request.Method.POST, INSERT_ROUTE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Toast.makeText(CreateRouteActivity.this, "Your route has begun. Happy walking!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("id", testRoute.getId());
                params.put("start_lat", testRoute.getStartLat());
                params.put("start_lng", testRoute.getStartLng());
                params.put("user_id", testRoute.getUserId());

                return params;
            }
        };

        mQueue.add(request);

        isRouteCreated = true;
    }

    /**
     * Updates completed walking route
     */
    private void updateRoute() {

        testRoute.setName(rNameEditText.getText().toString());

        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_ROUTE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Toast.makeText(CreateRouteActivity.this, "Your route has been added. Thanks for sharing!", Toast.LENGTH_SHORT).show();

                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("id", testRoute.getId());
                params.put("name", testRoute.getName());
                params.put("end_lat", testRoute.getEndLat());
                params.put("end_lng", testRoute.getEndLng());
                params.put("distance", testRoute.getDistance());

                return params;
            }
        };

        mQueue.add(request);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Displays device location on map
     * @param map
     */
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

    /**
     * Gets device location
     */
    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            Location currentLocation = (Location) task.getResult();
                            try {
                                
                                currentLat = currentLocation.getLatitude();
                                currentLng = currentLocation.getLongitude();

                                if (!isRouteCreated) {

                                    createRoute();
                                }

                            } catch (Exception e) {
                                
                                e.printStackTrace();

                                Toast.makeText(CreateRouteActivity.this, "Your current location could not be found. Please enable device location settings and restart!", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(getApplicationContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets location permission
     * Github repository: https://github.com/mitchtabian/Google-Maps-Google-Places/tree/6a64cdcaa35f737bf3de899e539e1cf66bdf0cde
     */
    public void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;

            getDeviceLocation();
        } else {

            ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Determines outcome of whether permission is gotten
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        mLocationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;

                getDeviceLocation();
            }
        }
    }

    private void getSections() {

        StringRequest request = new StringRequest(Request.Method.GET, DISPLAY_SECTIONS_URL, new Response.Listener<String>() {
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
                        String sRouteId = sectionObject.getString("route_id");

                        Section section = new Section(id, name, blurb, filename, sectionLat, sectionLng, sRouteId);

                        if (section.getRouteId().equals(routeId)) {
                            sectionList.add(section);
                        }
                    }

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
}