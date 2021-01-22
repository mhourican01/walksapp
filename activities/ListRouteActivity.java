package com.example.walksapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

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
import com.example.walksapp.adapters.RouteAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages display of list of walking routes
 */
public class ListRouteActivity extends AppCompatActivity implements RouteAdapter.OnNoteListener {

    // Constant variables
    private static final String GET_ROUTES_URL = "";
    private static final String GET_SECTIONS_URL = "";

    // Instance variables
    private RecyclerView routeRecyclerView;
    private RouteAdapter routeAdapter;
    private ArrayList<Route> routeList = new ArrayList<>();
    private ArrayList<Section> sectionList = new ArrayList<>();
    private RequestQueue mQueue;
    private Toolbar mToolbar;
    private BottomNavigationView bottomNav;
    private Intent switchIntent;
    private User user;
    private FloatingActionButton searchBtn;
    private String searchResult;
    private double searchDistResult;

    /**
     * Restricts route list according to search
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {

                if (data.hasExtra("result")) {

                    searchResult = data.getStringExtra("result");

                    ArrayList<Route> searchList = new ArrayList<>();

                    for (Route r : routeList) {
                        if (containsIgnoreCase(r.getName(), searchResult)) {
                            searchList.add(r);
                        }
                    }

                    createRecyclerView(searchList);
                }

                if (data.hasExtra("dist_result")) {

                    searchDistResult = data.getIntExtra("dist_result", 0);

                    ArrayList<Route> searchList = new ArrayList<>();

                    for (Route r : routeList) {
                        if (Double.parseDouble(r.getDistance()) <= (searchDistResult * 1000)) {
                            searchList.add(r);
                        }
                    }

                    createRecyclerView(searchList);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    /**
     * Converts search result and string being compared to lower case
     * @param str
     * @param subString
     * @return
     */
    public static boolean containsIgnoreCase(String str, String subString) {

        return str.toLowerCase().contains(subString.toLowerCase());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_route);

        mQueue = Volley.newRequestQueue(this);
        bottomNav = findViewById(R.id.bottom_navigation);
        routeRecyclerView = findViewById(R.id.routeRecyclerView);
        routeRecyclerView.hasFixedSize();

        getRoutes();

        // Sets toolbar with title
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Explore available routes");

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        searchBtn = findViewById(R.id.viewImgBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ListRouteActivity.this, SearchActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * Controls bottom navigation view
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {

                case R.id.nav_add:

                    if (user == null) {
                        switchIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(switchIntent);
                    } else {
                        switchIntent = new Intent(getApplicationContext(), CreateRouteActivity.class);
                        switchIntent.putExtra("user", user);
                        startActivity(switchIntent);
                    }

                    break;

                case R.id.nav_list:

                    switchIntent = new Intent(getApplicationContext(), ListRouteActivity.class);
                    switchIntent.putExtra("user", user);
                    startActivity(switchIntent);
                    break;

                case R.id.nav_profile:

                    if (user == null) {
                        switchIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(switchIntent);
                    } else {
                        switchIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        switchIntent.putExtra("user", user);
                        startActivity(switchIntent);
                    }
                    break;
            }

            return true;
        }
    };

    /**
     * Gets data from database and constructs Route objects
     */
    private void getRoutes() {

        // Sends HTTP request to server using Volley library
        StringRequest request = new StringRequest(Request.Method.GET, GET_ROUTES_URL,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    // Array of Routes in JSON format
                    JSONArray routes = new JSONArray(response);

                    // Iterates through Routes in JSON format
                    for (int i = 0; i < routes.length(); i++) {

                        // Creates Route object in JSON format
                        JSONObject routeObject = routes.getJSONObject(i);

                        // Assigns to variables the value paired with specified key
                        String id = routeObject.getString("id");
                        String name = routeObject.getString("name");
                        String startLat = routeObject.getString("start_lat");
                        String startLng = routeObject.getString("start_lng");
                        String endLat = routeObject.getString("end_lat");
                        String endLng = routeObject.getString("end_lng");
                        String distance = routeObject.getString("distance");
                        String userId = routeObject.getString("user_id");

                        // Constructs Route object from above variables
                        Route route = new Route(id, name, startLat, startLng, endLat,
                                endLng, distance, userId);

                        // Adds Route object to ArrayList of Route objects
                        if (!route.getName().isEmpty()) {
                            routeList.add(route);
                        }

                    }

                    // Invokes method to display Route objects in RecyclerView
                    getSections();

                // Handles JSON exception
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
     * Gets sections from server
     */
    private void getSections() {

        StringRequest request = new StringRequest(Request.Method.GET, GET_SECTIONS_URL, new Response.Listener<String>() {
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

                        sectionList.add(section);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }

                createRecyclerView(routeList);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
    }

    /**
     * Creates RecyclerView of walking routes
     * @param routeList
     */
    private void createRecyclerView(List<Route> routeList) {

        routeAdapter = new RouteAdapter(routeList, sectionList, this);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routeRecyclerView.setAdapter(routeAdapter);
    }

    /**
     * Brings user to screen of route clicked from RecyclerView
     * @param position
     */
    @Override
    public void onNoteClick(int position) {

        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("selected_route", routeList.get(position));
        startActivity(intent);
    }
}