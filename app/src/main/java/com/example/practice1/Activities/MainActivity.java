package com.example.practice1.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.practice1.Fragments.ProgressFragment;
import com.example.practice1.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ExecutorService service = Executors.newFixedThreadPool(3);
    private Button searchButton;
    private EditText editText;

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
        fragment.show(getSupportFragmentManager(), "progressbar");
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
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fragment.dismiss();
            }
        });
        queue.add(stringRequest);
    }
}
