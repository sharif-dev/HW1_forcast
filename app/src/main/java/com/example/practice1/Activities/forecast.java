package com.example.practice1.Activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.practice1.Adapters.ForecastListAdapter;
import com.example.practice1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class forecast extends AppCompatActivity {
    private ExecutorService service = Executors.newFixedThreadPool(3);
    final Activity context = this;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast2);
        handler = new Handler();
        service.execute(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager cm = (ConnectivityManager)getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    ReadFromServer(deserializeCenter(getIntent().getStringExtra(getString(R.string.intent1_key))));
                } else {
                    getResult(readFromFile(context));
                }
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
                    public void onResponse(final String response) {
                        service.execute(new Runnable() {
                            @Override
                            public void run() {
                                writeToFile(response, context);
                            }
                        });
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
        final ArrayList<String> iconUrl = new ArrayList<>();
        final String[] values = new String[7];
        String location = "", temp = "", lastUpdatedTime = "";
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject forecastObj = jsonObject.getJSONObject("forecast");
            final JSONArray objects = forecastObj.getJSONArray("forecastday");
            for(int i =0; i < objects.length(); i++){
                dates.add(objects.getJSONObject(i).getString("date"));
                maxTemp.add(objects.getJSONObject(i)
                        .getJSONObject("day").getString("maxtemp_c"));
                minTemp.add(objects.getJSONObject(i)
                        .getJSONObject("day").getString("mintemp_c"));
                iconUrl.add(objects.getJSONObject(i).getJSONObject("day")
                        .getJSONObject("condition").getString("icon"));
            }
            JSONObject currentLocation = jsonObject.getJSONObject("location");
            location = currentLocation.getString("name") + ", " +
                    currentLocation.getString("region")+ ", " +
                        currentLocation.getString("country");
            temp = jsonObject.getJSONObject("current").getString("temp_c");
            lastUpdatedTime = jsonObject.getJSONObject("current").getString("last_updated").split("\\s+")[1];
            for (int i = 0; i < 7; ++i) {
                values[i] = iconUrl.get(i) + " " + dates.get(i) + " " +
                        minTemp.get(i) + "\u2103 " + maxTemp.get(i) + "\u2103";
            }
        }catch (Exception e){
            Log.i("error", e.toString());
        }

        changeUi(values, location, temp, lastUpdatedTime);
    }

    private void changeUi(final String[] values, final String location, final String temp, final String lastUpdatedTime) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView textView = findViewById(R.id.textView);
                textView.setVisibility(View.INVISIBLE);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                TextView locationTextView = findViewById(R.id.location_textview);
                locationTextView.setText(location);

                TextView tempTextView = findViewById(R.id.temp_textview);
                tempTextView.setText("last updated " + lastUpdatedTime + "\t\t\t\t" + temp + "\u2103");

                ImageButton refreshBtn = findViewById(R.id.refresh_button);
                refreshBtn.setVisibility(View.VISIBLE);
                refreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(getIntent());
                    }
                });

                ListView listView = findViewById(R.id.forecast_list);
                ForecastListAdapter adapter = new ForecastListAdapter(context, values);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            TextView mintemp_textview = parent.getChildAt(i).findViewById(R.id.mintemp_text);
                            TextView maxtemp_textview = parent.getChildAt(i).findViewById(R.id.maxtemp_text);
                            mintemp_textview.setText(convertTemp((String) mintemp_textview.getText()));
                            maxtemp_textview.setText(convertTemp((String) maxtemp_textview.getText()));
                        }
                    }
                });
            }
        });
    }

    private String convertTemp(String text) {
        double temp = Double.parseDouble(text.substring(0, text.length() - 1));
        if (text.charAt(text.length() - 1) == '\u2103') {
            // temp is in celsius, converting to fahrenheit
            temp = temp * 1.8 + 32;
            temp = Math.round(temp * 10.0) / 10.0;
            return temp + "\u2109";
        }
        if (text.charAt(text.length() - 1) == '\u2109') {
            // temp is in fahrenheit, converting to celsius
            temp = (temp - 32) / 1.8;
            temp = Math.round(temp * 10.0) / 10.0;
            return temp + "\u2103";
        }
        return text;
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter osr = new OutputStreamWriter(context.openFileOutput(getString(R.string.lastCheckedCityFilePath), Context.MODE_PRIVATE));
            osr.write(data);
            osr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(Context context) {
        String res = "";
        try {
            InputStream inputStream = context.openFileInput(getString(R.string.lastCheckedCityFilePath));
            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString).append("\n");
                }
                inputStream.close();
                res = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
