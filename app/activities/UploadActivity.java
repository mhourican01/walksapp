package com.example.walksapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
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
import com.example.walksapp.Section;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages capturing of section, including image capture
 */
public class UploadActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Constant variables
    private static final String INSERT_SECTION_URL = "";
    private static final String insertImgURL = "";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // Instance variables
    private RequestQueue mQueue;
    private EditText nameEditText, descEditText;
    private String routeId;
    private String name, blurb, filename;
    private File file;
    private Uri fileUri;
    private Bitmap bitmap;
    private String encodedString;
    private Toolbar mToolbar;
    private Button uploadBtn;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double currentLat, currentLng, sectionLat, sectionLng;
    private Section section;
    private LocationRequest mLocationRequest;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK) {

            new ImageEncoder().execute();
        }
    }

    /**
     * Encodes image as Base64 for processing by PHP
     * Github repository: https://github.com/miskoajkula/PHP-MYSQL-IMAGE-UPLOAD
     */
    private class ImageEncoder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            // Decodes file to bitmap
            bitmap = BitmapFactory.decodeFile(fileUri.getPath());

            // Initialises output stream for byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Compresses bitmap to output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            // Initialises byte array
            byte[] array = stream.toByteArray();

            // Encodes data as Base64
            encodedString = Base64.encodeToString(array, 0);

            // Returns null
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            sendRequest();
        }

    }

    /**
     * Sends HTTP request to server, with image data
     */
    public void sendRequest() {

        StringRequest request = new StringRequest(Request.Method.POST, insertImgURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> map = new HashMap<>();
                map.put("encoded_string", encodedString);
                map.put("filename", filename);

                return map;
            }
        };

        mQueue.add(request);
    }

    /**
     * Generates random UUID with which to name image
     */
    private void getFileUri() {

        // Creates filename by concatenating UUID and .jpg suffix
        filename = UUID.randomUUID().toString() + ".jpg";

        // Creates file in device with name above
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "WalksApp" + File.separator + filename);

        // Creates URI from file above
        fileUri = Uri.fromFile(file);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getLocationPermission();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Intent routeIdIntent = getIntent();
        routeId = routeIdIntent.getStringExtra("routeId");

        mQueue = Volley.newRequestQueue(this);

        nameEditText = findViewById(R.id.nameEditText);
        descEditText = findViewById(R.id.descEditText);

        // Sets toolbar with title
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add new section");

        ImageButton cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 // Initialises Activity to open camera in-app
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Invokes getFileUri() method to generate filename
                getFileUri();

                // Passes URI to Activity to assign to captured image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                // Starts Activity to open camera in-app
                startActivityForResult(intent, 2);
            }
        });

        uploadBtn = findViewById(R.id.uploadBtn);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = nameEditText.getText().toString();
                blurb = descEditText.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(UploadActivity.this, "You have not named your section!", Toast.LENGTH_SHORT).show();
                } else if (blurb.isEmpty()) {
                    Toast.makeText(UploadActivity.this, "You have not described your section!", Toast.LENGTH_SHORT).show();
                } else if (encodedString == null) {
                    Toast.makeText(UploadActivity.this, "Either you have not taken a photo for your section, or it is being processed. Please wait.", Toast.LENGTH_SHORT).show();
                } else {
                    mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
            }
        });
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            sectionLat = locationResult.getLastLocation().getLatitude();
            sectionLng = locationResult.getLastLocation().getLongitude();

                section = new Section(null, name, blurb, filename, String.valueOf(sectionLat), String.valueOf(sectionLng), routeId);

                createSection();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("last_section", section);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void createSection() {

        StringRequest request = new StringRequest(Request.Method.POST, INSERT_SECTION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(UploadActivity.this, "Your section has been added!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> parameters = new HashMap<>();

                parameters.put("name", section.getName());
                parameters.put("blurb", section.getBlurb());
                parameters.put("filename", section.getFilename());
                parameters.put("section_lat", section.getSectionLat());
                parameters.put("section_lng", section.getSectionLng());
                parameters.put("route_id", section.getRouteId());

                return parameters;
            }
        };

        mQueue.add(request);
    }

    /**
     * Map methods
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
     * Gets current location
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

                                // currentLatLng = new LatLng(currentLat, currentLng);
                            } catch (Exception e) {

                                e.printStackTrace();

                                Toast.makeText(UploadActivity.this, "Unable to retrieve location. Check that device permission is enabled.", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(UploadActivity.this, "Unable to retrieve location. Check that device permission is enabled.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;

            getDeviceLocation();
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

                getDeviceLocation();
            }
        }
    }
}