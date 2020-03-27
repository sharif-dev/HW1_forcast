package com.example.practice1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class    MainActivity extends AppCompatActivity {
    ExecutorService service = Executors.newFixedThreadPool(5);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickSearchCityButton(this);
    }

    public void clickSearchCityButton(final Context context) {
        final Button button = findViewById(R.id.SearchCityButton);
        final EditText text = findViewById(R.id.SearchCityInput);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressFragment fragment = new ProgressFragment();
                fragment.show(getSupportFragmentManager(), "hello");
                service.execute(new SearchCityResult(button, text, context, fragment));
            }
        });
    }
}
