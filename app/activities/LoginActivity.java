package com.example.walksapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.walksapp.R;
import com.example.walksapp.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages user account validation
 */
public class LoginActivity extends AppCompatActivity {

    // Constant variables
    private static final String LOGIN_URL = "";

    // Instance variables
    private Toolbar mToolbar;
    private EditText emailEditText, passwordEditText;
    private Button logInBtn;
    private String emailAddress, password;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialising HTTP request
        mQueue = Volley.newRequestQueue(this);

        mToolbar = findViewById(R.id.toolbar);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        logInBtn = findViewById(R.id.openRegBtn);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Log in to your account");

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailAddress = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                postUserDetails();
            }
        });
    }

    /**
     * Posts login credentials to server
     */
    private void postUserDetails() {

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {

                        Toast.makeText(LoginActivity.this, jsonObject.getString("success"), Toast.LENGTH_SHORT).show();

                        User user = new User(emailAddress);

                        Intent intent = new Intent(getApplicationContext(), ListRouteActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    } else if (jsonObject.names().get(0).equals("error")) {

                        Toast.makeText(LoginActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email_address", emailAddress);
                params.put("password", password);

                return params;
            }
        };
        mQueue.add(request);
    }
}