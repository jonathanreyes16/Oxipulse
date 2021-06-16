package com.example.oxipulse.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.Period;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oxipulse.R;
import com.example.oxipulse.ui.Records.RecordsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PatientAdapter extends FirebaseRecyclerAdapter<patient, PatientAdapter.MyViewHolder> {

    public PatientAdapter(@NonNull FirebaseRecyclerOptions<patient> options) {
        super(options);
    }
    //int y,m,d;
     private final String UID = "UID";

    public int getAge(int year, int month, int dayOfMonth) {
        return Period.between(
                LocalDate.of(year, month, dayOfMonth),
                LocalDate.now()
        ).getYears();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //se infla el itemlayout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        //se define el tamano, margenes y parametros de la vista del layout
       // v.setOnClickListener(new View.OnClickListener() {
       //     @Override
       //     public void onClick(View v) {
       //         Toast.makeText(parent.getContext(), "t"+viewType, Toast.LENGTH_SHORT).show();
       //     }
       // });

        return new PatientAdapter.MyViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull patient model) {
        String fullname = model.getFirstName()+" "+model.getLastName();
        holder.nombre.setText(fullname);
        holder.edad.setText(model.getBirthdate());
        holder.peso.setText(model.getWeight());
        holder.uid=model.getId();
        holder.estatura.setText(model.getHeight());

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Toast.makeText(v.getContext(), "t "+holder.uid, Toast.LENGTH_SHORT).show();

                 Bundle bundle = new Bundle();
                 bundle.putString("UID",holder.uid);
                 RecordsFragment fragment =new RecordsFragment();
                 fragment.setArguments(bundle);
                 AppCompatActivity activity = ((AppCompatActivity)v.getContext());
                 fragment.show(activity.getSupportFragmentManager(),holder.uid);
                 //fragment.show();

                 //AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                 //LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 //View view1 = inflater.inflate(R.layout.fragment_records,null);
                 //builder.setView(view1);
                 //builder.setTitle("Historial");
                 //Dialog d = builder.create();
                 //d.show();

                 //RecordsFragment fragment =RecordsFragment.newInstance(holder.uid);
                 //AppCompatActivity activity = ((AppCompatActivity)v.getContext());
                 //fragment.show(activity.getSupportFragmentManager(),"UID");

                 //activity.getSupportFragmentManager().beginTransaction().replace(fragment);
                 //fragment.show(fragmentManager, holder.uid);
                 //AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                //LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //View view1 = inflater.inflate(R.layout.fragment_records,null);
                //builder.setView(view1);
                //builder.setTitle("Historial");
                //Dialog d = builder.create();
                //d.show();
             }
         });


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView nombre,edad,peso,estatura;
        String uid;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // get the reference of item view's
            nombre = itemView.findViewById(R.id.row_text1);
            edad =  itemView.findViewById(R.id.row_text2);
            peso = itemView.findViewById(R.id.row_text3);
            estatura = itemView.findViewById(R.id.row_text4);

          // itemView.setOnClickListener(new View.OnClickListener() {
          //     @Override
          //     public void onClick(View v) {
          //         mClickListener.onItemClick(v,getAdapterPosition());
          //     }
          // });


        }
       // private MyViewHolder.ClickListener mClickListener;
       // //Interface to send callbacks...
//
       // public interface ClickListener{
       //     public void onItemClick(View view, int position);
       //     //public void onItemLongClick(View view, int position);
       // }
//
       // public void setOnClickListener(MyViewHolder.ClickListener clickListener){
       //     mClickListener = clickListener;
       // }
    }


}
