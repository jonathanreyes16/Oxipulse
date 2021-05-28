package com.example.oxipulse.ui.Records;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oxipulse.R;
import com.example.oxipulse.model.CustomAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class RecordsFragment extends Fragment {

    private RecordsViewModel recordsViewModel;
    ArrayList<String> patientdata = new ArrayList<>(Arrays.asList("fecha1", "hr1","rc1","etiqueta1","fecha2", "hr2","rc2","etiqueta2"));


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recordsViewModel =
                new ViewModelProvider(this).get(RecordsViewModel.class);
        View v = inflater.inflate(R.layout.fragment_records, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.datarecycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
        recyclerView.setLayoutManager(gridLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(getContext(),patientdata);
        recyclerView.setAdapter(customAdapter);





        return v;
    }
}