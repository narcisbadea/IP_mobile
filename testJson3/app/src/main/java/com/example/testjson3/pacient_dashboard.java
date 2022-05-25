package com.example.testjson3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class pacient_dashboard extends AppCompatActivity {
    private RequestQueue mQueue;
    private Button buttonLogout;
    private SharedPreferences pref;
    private String jwt = null;

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacient_dashboard);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        buttonLogout = findViewById(R.id.button_logout);
        mQueue = Volley.newRequestQueue(this);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    private void logOut() {
        pref.edit().remove("jwt").commit();
        startActivity(new Intent(pacient_dashboard.this, MainActivity.class));
    }

    private void jsonParse() {
        jwt = pref.getString("jwt", null); // getting String
        Log.e("VOLLEY", jwt);

        String url = "http://api.vhealth.me/puls";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                try {

                     JSONArray jsonArray = new JSONArray(response);
                     for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                         Log.e("VOLLEY", (jo.getInt("valoare")+" --> "+jo.getString("created")+"\n\n"));
                        }
                     } catch (JSONException e) {
                         e.printStackTrace();
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " +jwt);
                return headers;
            }
        };
        mQueue.add(request);
    }
}