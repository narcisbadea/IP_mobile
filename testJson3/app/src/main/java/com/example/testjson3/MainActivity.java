package com.example.testjson3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewResult;
    private EditText username;
    private EditText password;
    private RequestQueue mQueue;
    private SharedPreferences pref;
    private String jwt;

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String jwt = pref.getString("jwt", null); // getting String
        if (jwt != null) {
            startActivity(new Intent(MainActivity.this, pacient_dashboard.class));
        } else {
            mTextViewResult = findViewById(R.id.text_view_result);
            Button buttonParse = findViewById(R.id.button_parse);
            username = findViewById(R.id.username);
            password = findViewById(R.id.password);
            mQueue = Volley.newRequestQueue(this);
            buttonParse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jsonParse();
                }
            });
        }
    }

    private void jsonParse() {

        // url to post our data
        String URL = "http://api.vhealth.me/Auth/login";
        //String URL = "http://arduino.vhealth.me/Powers/live";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username.getText());
            jsonBody.put("password", password.getText());
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jo = null;
                    try {
                        jo = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.i("VOLLEY", jo.getString("token"));
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("jwt", jo.getString("token")); // Storing string
                        editor.commit();
                        jwt = jo.getString("token");
                        if (jwt != null) {
                            startActivity(new Intent(MainActivity.this, pacient_dashboard.class));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = new String(response.data, StandardCharsets.UTF_8);
                        //Log.i("VOLLEY", response.);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            mQueue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}