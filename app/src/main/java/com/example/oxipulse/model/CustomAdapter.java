package com.example.oxipulse.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oxipulse.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    ArrayList<String> evaluaciones;
    Context context;

    public CustomAdapter(Context context,ArrayList<String> evaluaciones) {
        this.evaluaciones = evaluaciones;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //se infla el itemlayout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout,parent,false);
        //se define el tamano, margenes y parametros de la vista del layout
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.data.setText(evaluaciones.get(position));
       /*
        holder.fecha.setText(evaluaciones.get(position));
        holder.ritCar.setText(evaluaciones.get(position));
        holder.satOxi.setText(evaluaciones.get(position));
        holder.etiqueta.setText(evaluaciones.get(position));

        */
        // implement setOnClickListener event on item view.

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //evento del click de un objeto en la lista
                Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return evaluaciones.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView data ; //fecha,ritCar,satOxi,etiqueta;

        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            data = (TextView) itemView.findViewById(R.id.row_txt1);
            /*
            fecha = (TextView) itemView.findViewById(R.id.row_text1);
            ritCar = (TextView) itemView.findViewById(R.id.row_text2);
            satOxi = (TextView) itemView.findViewById(R.id.row_text3);
            etiqueta = (TextView) itemView.findViewById(R.id.row_text4);
             */
        }
    }
}
