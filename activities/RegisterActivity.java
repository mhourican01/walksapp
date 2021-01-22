package com.example.walksapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages user account creation
 */
public class RegisterActivity extends AppCompatActivity {

    // Constant variables
    private static final String INSERT_USER_URL = "";
    private static final String TAG = "RegisterActivity";

    // Instance variables
    private Toolbar mToolbar;
    private EditText emailEditText, passwordEditText;
    private Button registerBtn;
    private RequestQueue mQueue;
    private String emailAddress, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mQueue = Volley.newRequestQueue(this);

        mToolbar = findViewById(R.id.toolbar);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerBtn = findViewById(R.id.openRegBtn);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create your account");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailAddress = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (emailAddress.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "You have not provided an email address!", Toast.LENGTH_SHORT).show();
                } else if (!emailAddress.contains("@")){
                    Toast.makeText(RegisterActivity.this, "Please ensure that your email address includes an '@'!", Toast.LENGTH_SHORT).show();
                } if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "You have not provided a password!", Toast.LENGTH_SHORT).show();
                } else {
                    createAccount();
                }
            }
        });
    }

    /**
     * Posts user account details to server
     */
    private void createAccount() {

        StringRequest request = new StringRequest(Request.Method.POST, INSERT_USER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {

                        Toast.makeText(RegisterActivity.this, jsonObject.getString("success"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);

                    } else if (jsonObject.names().get(0).equals("error")) {

                        Log.d(TAG, "onResponse: response " + response);

                        Toast.makeText(RegisterActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
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