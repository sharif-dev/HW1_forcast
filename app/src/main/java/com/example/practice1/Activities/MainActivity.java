package com.example.practice1.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.practice1.Adapters.MyCitiesResultAdapter;
import com.example.practice1.Fragments.ProgressFragment;
import com.example.practice1.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ExecutorService service = Executors.newFixedThreadPool(3);
    private Button searchButton;
    private EditText editText;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchButton = findViewById(R.id.SearchCityButton);
        editText = findViewById(R.id.SearchCityInput);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        clickButton();
                    }
                });
            }
        });
    }

    private void clickButton() {
        final ProgressFragment fragment = new ProgressFragment();
        fragment.show(getSupportFragmentManager(), this.getString(R.string.progressbar_tag));
        RequestQueue queue = Volley.newRequestQueue(this);
        StringBuilder builder = new StringBuilder();
        builder.append(this.getString(R.string.mapbox_url_first_part));
        builder.append(this.editText.getText());
        builder.append(this.getString(R.string.mapbox_url_third_part));
        builder.append(this.getString(R.string.mapbox_access_token));
        System.out.println(builder.toString());
        String url = builder.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fragment.dismiss();
                        setResultAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fragment.dismiss();
                String errText = null;
                if (error instanceof NoConnectionError)
                    errText = getString(R.string.connection_error);
                else if (error instanceof NetworkError)
                    errText = getString(R.string.network_error);
                setToast(errText);
            }
        });
        queue.add(stringRequest);
    }

    private void setToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setResultAdapter(String response) {
        JSONParser parser = new JSONParser();
        ArrayList<String> cities = new ArrayList<>();
        ArrayList<String> positions = new ArrayList<>();
        try {
            JSONObject object = (JSONObject) parser.parse(response);
            ArrayList<JSONObject> features = (ArrayList)object.get("features");
            for(JSONObject feature: features) {
                cities.add(feature.get("place_name").toString());
                positions.add(feature.get("center").toString());
            }
            if (features.size() == 0)
                setToast(getString(R.string.no_result_found));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyCitiesResultAdapter adapter = new MyCitiesResultAdapter(this, cities, positions);
        listView = findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: clickItem
            }
        });
    }

}
