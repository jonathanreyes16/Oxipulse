package com.example.oxipulse.ui.Records;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oxipulse.R;
import com.example.oxipulse.model.CustomAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class RecordsFragment extends Fragment {

    private RecordsViewModel recordsViewModel;
    ArrayList<String> patientdata = new ArrayList<>(Arrays.asList("fecha1", "hr1","rc1","etiqueta1","fecha2", "hr2","rc2","etiqueta2"));
    ArrayList<String> headers = new ArrayList<>(Arrays.asList("Fecha","Ritmo Cardiaco(ppm)","Saturacion de oxigeno(%)","Etiqueta" ));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recordsViewModel =
                new ViewModelProvider(this).get(RecordsViewModel.class);
        View v = inflater.inflate(R.layout.fragment_records, container, false);

        RecyclerView headersView = (RecyclerView) v.findViewById(R.id.listheaders);


        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.datarecycler);


        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
        GridLayoutManager headerLayoutManager = new GridLayoutManager(getContext(),4);
        headersView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(headerLayoutManager);
        //recyclerView.setLayoutManager(linearLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(getContext(),patientdata);
        CustomAdapter headerAdapter = new CustomAdapter(getContext(),headers);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(customAdapter);
        headersView.setAdapter(headerAdapter);






        return v;
    }
}