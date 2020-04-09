package com.example.practice1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.practice1.R;
import com.squareup.picasso.Picasso;

public class ForecastListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    // values array contains icon address, date, min_temp and max_temp separated by whitespace

    public ForecastListAdapter(Context context, String[] values) {
        super(context, R.layout.activity_forecast_listview, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_forecast_listview, null);
        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView mintemptext = (TextView) rowView.findViewById(R.id.mintemp_text);
        TextView maxtemptext = (TextView) rowView.findViewById(R.id.maxtemp_text);
        ImageView image = (ImageView) rowView.findViewById(R.id.icon);

        String[] strings = values[position].split("\\s+");
        Picasso.get().load(strings[0]).into(image);
        date.setText(strings[1]);
        mintemptext.setText(strings[2]);
        maxtemptext.setText(strings[3]);
        return rowView;
    }
}
