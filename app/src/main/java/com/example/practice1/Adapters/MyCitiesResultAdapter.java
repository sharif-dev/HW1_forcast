package com.example.practice1.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.practice1.R;

import java.util.ArrayList;

public class MyCitiesResultAdapter extends ArrayAdapter<String> {
    private Activity context;
    private ArrayList<String> cities;
    public MyCitiesResultAdapter(Activity context, ArrayList<String> cities) {
        super(context, R.layout.city, cities);
        this.context = context;
        this.cities = cities;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View cityView = inflater.inflate(R.layout.city, null);
        TextView city = cityView.findViewById(R.id.cityName);
        city.setText(cities.get(position));
        return cityView;
    }
}
