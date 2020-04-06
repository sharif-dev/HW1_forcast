package com.example.practice1.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.practice1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

public class forecast extends AppCompatActivity {
    private ExecutorService service = Executors.newFixedThreadPool(3);
    final Activity context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast2);
        final String center = deserializeCenter(getIntent()
                .getStringExtra(getString(R.string.intent1_key)));
        service.execute(new Runnable() {
            @Override
            public void run() {
                ReadFromServer(center);
            }
        });

    }

    private String deserializeCenter(String center) {
        if (center == null)
            return "";
        String[] positions = center.substring(1, center.length() - 1)
                .split(getString(R.string.comma));
        StringBuilder builder = new StringBuilder();
        builder.append(positions[1]);
        builder.append(getString(R.string.comma));
        builder.append(positions[0]);
        return builder.toString();
    }

    private void ReadFromServer(String center){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.darksky_url_part1));
        builder.append(getString(R.string.darksky_url_part2));
        builder.append(center);
        builder.append("&key=ef4f4e4d37aa4229b37121125200304&days=7");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, builder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = null;
                if (error instanceof NoConnectionError)
                    errorMessage = getString(R.string.connection_error);
                else if (error instanceof NetworkError)
                    errorMessage = getString(R.string.network_error);
                Toast toast = Toast.makeText(getApplicationContext(),
                        errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        queue.add(stringRequest);
    }

    private void getResult(String response){
        final ArrayList<String> dates = new ArrayList<>();
        final ArrayList<String> maxTemp = new ArrayList<>();
        final ArrayList<String> minTemp = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject forecastObj = jsonObject.getJSONObject("forecast");
            final JSONArray objects = forecastObj.getJSONArray("forecastday");
            for(int i =0; i<objects.length(); i++){
                dates.add(objects.getJSONObject(i).getString("date"));
                maxTemp.add(objects.getJSONObject(i)
                        .getJSONObject("day").getString("maxtemp_c"));
                minTemp.add(objects.getJSONObject(i)
                        .getJSONObject("day").getString("mintemp_c"));
            }

        }catch (Exception e){
            Log.i("error", e.toString());
        }

        changeUi(dates, maxTemp, minTemp);
    }

    private void changeUi(final ArrayList dates,final ArrayList minTemp,final ArrayList maxTemp){
        Handler handler = new Handler();
        final ArrayList<String> arrays = new ArrayList<>();
        for(int i=0; i<7; i++){
            String s = dates.get(i) + "\nmintemp: "
                    + minTemp.get(i) + "\nmaxtemp: " + maxTemp.get(i);
            arrays.add(s);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView textView = findViewById(R.id.textView);
                textView.setVisibility(View.INVISIBLE);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                ListView listView = findViewById(R.id.forecast_list);
                ArrayAdapter adapter = new ArrayAdapter<String>(listView.getContext(),
                        R.layout.activity_forecast_listview, arrays);
                listView.setAdapter(adapter);

            }
        });

    }
}
