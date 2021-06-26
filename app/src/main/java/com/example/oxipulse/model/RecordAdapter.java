package com.example.oxipulse.model;

import android.content.Context;
import android.icu.text.AlphabeticIndex;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oxipulse.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class RecordAdapter extends FirebaseRecyclerAdapter<record,RecordAdapter.MyViewHolder> {

    public RecordAdapter(@NonNull FirebaseRecyclerOptions<record> options) {
        super(options);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //se infla el itemlayout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        //se define el tamano, margenes y parametros de la vista del layout
        return new RecordAdapter.MyViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull record model) {
        holder.fecha.setText(model.getDate());
        holder.ritCar.setText(model.getHr());
        holder.satOxi.setText(model.getOxi());
        holder.etiqueta.setText(model.getTag());

        //
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView fecha,ritCar,satOxi,etiqueta;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // get the reference of item view's
            fecha = itemView.findViewById(R.id.row_text1);
            ritCar =  itemView.findViewById(R.id.row_text2);
            satOxi = itemView.findViewById(R.id.row_text3);
            etiqueta = itemView.findViewById(R.id.row_text4);

        }
    }
}
