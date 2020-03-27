package com.example.practice1;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.practice1.R;
import com.google.gson.Gson;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchCityResult implements Runnable {
    private Button button;
    private EditText editText;
    private Context context;
    private ProgressFragment fragment;
    private HashMap<Object, Object> cities = new HashMap<>();

    public SearchCityResult(Button button, EditText editText, Context context, ProgressFragment fragment) {
        this.button = button;
        this.editText = editText;
        this.context = context;
        this.fragment = fragment;
    }

    private void handlerPost() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                fragment.dismiss();
            }
        });
    }

    private void parseCities(String response) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject object = (JSONObject) parser.parse(response);
            ArrayList<JSONObject> features = (ArrayList)object.get("features");
            for(JSONObject feature: features)
                this.cities.put(feature.get("place_name"), feature.get("center"));
        } catch (Exception e) {
        }
    }

    public HashMap getCities() {
        return this.cities;
    }

    @Override
    public void run() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/" +
                this.editText.getText() + ".json?access_token=" +
                this.context.getString(R.string.mapbox_access_token);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseCities(response);
                        handlerPost();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handlerPost();
            }
        });
        queue.add(stringRequest);
    }

}
