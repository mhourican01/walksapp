package com.example.walksapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
 * Manages user profile
 */
public class ProfileActivity extends AppCompatActivity implements RouteAdapter.OnNoteListener {

    //Constant variables

    private static final String GET_ROUTES_URL = "";
    private static final String GET_SECTIONS_URL = "";

    // Instance variables
    private Toolbar mToolbar;
    private User user;
    private TextView emailTextView;
    private RequestQueue mQueue;
    private RecyclerView myRoutesRecyclerView;
    private ArrayList<Route> myRoutes;
    private ArrayList<Section> sectionList;
    private RouteAdapter routeAdapter;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        mQueue = Volley.newRequestQueue(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My profile");

        emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText(user.getEmailAddress());

        myRoutesRecyclerView = findViewById(R.id.myRoutesReyclerView);
        myRoutesRecyclerView.hasFixedSize();

        myRoutes = new ArrayList<>();
        sectionList = new ArrayList<>();
        getRoutes();

        // Redirects user to home screen, unsetting their User object
        logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Gets routes from server
     */
    private void getRoutes() {

        StringRequest request = new StringRequest(Request.Method.GET, GET_ROUTES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray routes = new JSONArray(response);

                    for (int i = 0; i < routes.length(); i++) {

                        JSONObject routeObject = routes.getJSONObject(i);

                        String id = routeObject.getString("id");
                        String name = routeObject.getString("name");
                        String startLat = routeObject.getString("start_lat");
                        String startLng = routeObject.getString("start_lng");
                        String endLat = routeObject.getString("end_lat");
                        String endLng = routeObject.getString("end_lng");
                        String distance = routeObject.getString("distance");
                        String userId = routeObject.getString("user_id");

                        Route route = new Route(id, name, startLat, startLng, endLat, endLng, distance, userId);

                        if ((!route.getName().isEmpty())
                            && (route.getUserId().equals(user.getEmailAddress()))) {
                            myRoutes.add(route);
                        }
                    }

                    createRecyclerView(myRoutes);

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

        getSections();
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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
    }

    private void createRecyclerView(List<Route> myRoutes) {

        routeAdapter = new RouteAdapter(myRoutes, sectionList, this);
        myRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRoutesRecyclerView.setAdapter(routeAdapter);
    }

    @Override
    public void onNoteClick(int position) {

        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("selected_route", myRoutes.get(position));
        startActivity(intent);
    }
}
