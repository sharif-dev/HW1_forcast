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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ForecastListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final Calendar cal = Calendar.getInstance();
    private final String[] daysOfWeek = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    // values array contains icon address, date, min_temp and max_temp separated by whitespace

    public ForecastListAdapter(Context context, String[] values) {
        super(context, R.layout.forecast_row_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.forecast_row_item, null);
        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView mintemptext = (TextView) rowView.findViewById(R.id.mintemp_text);
        TextView maxtemptext = (TextView) rowView.findViewById(R.id.maxtemp_text);
        final ImageView image = (ImageView) rowView.findViewById(R.id.icon);

        String[] strings = values[position].split("\\s+");
        Picasso.get().load("https:" + strings[0]).into(image);
        Picasso.get().setLoggingEnabled(true);
        mintemptext.setText(strings[2]);
        maxtemptext.setText(strings[3]);

        try {
            cal.setTime(new SimpleDateFormat("yyyy-mm-dd").parse(strings[1]));
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            date.setText(daysOfWeek[dayOfWeek] + "\n" + strings[1].substring(5));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rowView;
    }
}
