package com.example.oxipulse.model;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpinnerPatientAdapter extends ArrayAdapter<patient> {
    // el contexto en el que esta
    private Context context;
    // los valores del spinner
    private patient[] values;


    public SpinnerPatientAdapter(@NonNull Context context, int resource, @NonNull patient[] values) {
        super(context, resource, values);
        this.context=context;
        this.values=values;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Nullable
    @Override
    public patient getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView name = (TextView) super.getView(position, convertView, parent);
        name.setText(values[position].getFirstName());
        return name;
    }

    @Override
    public View getDropDownView(int position, @Nullable @org.jetbrains.annotations.Nullable View convertView, @NonNull @NotNull ViewGroup parent) {
       View v = super.getDropDownView(position, convertView, parent);
        TextView name = ((TextView) v);
       name.setTextColor(Color.BLACK);
       name.setText(values[position].getFirstName());
        return name;
    }
}
